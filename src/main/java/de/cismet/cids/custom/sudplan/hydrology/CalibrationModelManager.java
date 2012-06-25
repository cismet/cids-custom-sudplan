/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.joda.time.LocalDate;

import se.smhi.sudplan.client.Sample;
import se.smhi.sudplan.client.Scenario;
import se.smhi.sudplan.client.SudPlanHypeAPI;
import se.smhi.sudplan.client.exception.UnknownWorkareaException;
import se.smhi.sudplan.client.exception.UnrecoverableException;

import java.io.IOException;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.cismet.cids.custom.sudplan.AbstractAsyncModelManager;
import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SMSUtils.Model;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CalibrationModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CalibrationModelManager.class);

    private static final int MAX_STEPS = 5;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   runBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException            DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private CalibrationInput inputFromRun(final CidsBean runBean) throws IOException {
        final Object resource = SMSUtils.inputFromRun(runBean);

        if (!(resource instanceof CalibrationInput)) {
            throw new IllegalStateException("illegal calibration input resource: " + resource); // NOI18N
        }

        return (CalibrationInput)resource;
    }

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing hydrology calibration"); // NOI18N
        }

        fireProgressed(0, MAX_STEPS, "Preparing model execution");

        final CalibrationInput input = inputFromRun(cidsBean);

        final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();
        final Scenario calScenario = HydrologyCache.getInstance().getCalibrationScenario();

        fireProgressed(1, MAX_STEPS, "Set calibration simulation time range");

        final CidsBean hwBean = SMSUtils.fetchCidsBean(input.getHydrologyWorkspaceId(),
                SMSUtils.TABLENAME_HYDROLOGY_WORKSPACE);
        final String localModelId = (String)hwBean.getProperty("local_model_id"); // NOI18N

        try {
            hypeClient.setSimulationTime(
                localModelId,
                calScenario.getBdate(),
                calScenario.getCdate(),
                calScenario.getEdate());
        } catch (final UnrecoverableException ex) {
            final String message = "cannot set calibration simulation time: " + localModelId; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        final DateFormat df = HydrologyCache.getInstance().getHydroDateFormat();
        try {
            uploadTimeseries(
                localModelId,
                input.getBasinToTimeseries(),
                df.parse(calScenario.getBdate()),
                df.parse(calScenario.getEdate()));
        } catch (final ParseException ex) {
            final String message = "illegal scenario start or end date from scenario: " + localModelId; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IllegalStateException(message, ex);
        }

        fireProgressed(-1, -1, "Creating submodel");

        final CalibrationRunInfo runinfo = new CalibrationRunInfo();
        try {
            final String exexId = hypeClient.createSubmodel(localModelId);
            runinfo.setSubmodelExecutionId(exexId);
            runinfo.setBasinId((Integer)hwBean.getProperty("basin_id"));           // NOI18N
            runinfo.setLocalModelId((String)hwBean.getProperty("local_model_id")); // NOI18N
        } catch (UnknownWorkareaException ex) {
            final String message = "illegal local model id: " + localModelId;      // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IllegalStateException(message, ex);
        } catch (UnrecoverableException ex) {
            final String message = "cannot create submodel: " + localModelId;      // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, runinfo);

            cidsBean.setProperty("runinfo", writer.toString());             // NOI18N
            cidsBean = cidsBean.persist();
        } catch (final Exception ex) {
            final String message = "cannot store runinfo: " + localModelId; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   localModelId   DOCUMENT ME!
     * @param   basinIdToTSId  DOCUMENT ME!
     * @param   begin          DOCUMENT ME!
     * @param   end            DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void uploadTimeseries(final String localModelId,
            final Map<Integer, Integer> basinIdToTSId,
            final Date begin,
            final Date end) throws IOException {
        fireProgressed(2, MAX_STEPS, "Preparing timeseries");

        final TimeseriesRetriever tsRetriever = TimeseriesRetriever.getInstance();

        final List<List<Sample>> convertedTimeseries = new ArrayList<List<Sample>>(basinIdToTSId.size());
        final String[] qStations = new String[basinIdToTSId.size()];
        int i = 0;
        for (final Entry<Integer, Integer> entry : basinIdToTSId.entrySet()) {
            final int basinId = entry.getKey();
            final int tsId = entry.getValue();

            final CidsBean tsBean = SMSUtils.fetchCidsBean(tsId, "timeseries");                             // NOI18N
            if (tsBean == null) {
                final String message = "cannot fetch time series object with id from cids server: " + tsId; // NOI18N
                LOG.error(message);
                throw new IOException(message);
            }

            TimeseriesRetrieverConfig config = null;
            final TimeSeries ts;
            try {
                config = TimeseriesRetrieverConfig.fromUrl((String)tsBean.getProperty("uri")); // NOI18N

                ts = tsRetriever.retrieve(config).get();
            } catch (final Exception ex) {
                final String message = "cannot fetch time series object with id from repository: " + config; // NOI18N
                LOG.error(message, ex);
                throw new IOException(message, ex);
            }

            try {
                qStations[i++] = String.valueOf(basinId);
                convertedTimeseries.add(convertTimeseries(basinId, ts, begin, end));
            } catch (final Exception ex) {
                final String message = "cannot write temporary ts file: " + config; // NOI18N
                LOG.error(message, ex);
                throw new IOException(message, ex);
            }
        }

        fireProgressed(3, MAX_STEPS, "Merging timeseries");

        // TODO: store already uploaded ts reference so that uploading is not necessary anymore
        final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();
        try {
            hypeClient.mergeObservations(localModelId, qStations, convertedTimeseries);
        } catch (final Exception ex) {
            final String message = "cannot merge observations"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    /**
     * we won't use a special inputstream for the upload of the timeseries because it is more overhead than writing an
     * appropriate file.
     *
     * @param   basinId     DOCUMENT ME!
     * @param   timeseries  DOCUMENT ME!
     * @param   begin       DOCUMENT ME!
     * @param   end         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException            DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private List<Sample> convertTimeseries(final int basinId,
            final TimeSeries timeseries,
            final Date begin,
            final Date end) throws IOException {
        final Calendar cal = new GregorianCalendar();
        final Calendar endCal = new GregorianCalendar();
        final DateFormat df = HydrologyCache.getInstance().getHydroDateFormat();
        cal.setTime(begin);
        endCal.setTime(end);
        final long delta = endCal.getTimeInMillis() - cal.getTimeInMillis();
        final long days = delta / 1000 / 60 / 60 / 24;

        final List<Sample> ts = new ArrayList<Sample>(Long.valueOf(days).intValue());

        while (!cal.after(endCal)) {
            // day by day
            final Object value = timeseries.getValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE);

            final Sample sample;
            if (value == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("no value for timestamp: [basinid=" + basinId + "|timeseries=" + timeseries // NOI18N
                                + "|date="   // NOI18N
                                + df.format(cal.getTime()) + "]"); // NOI18N
                }

                sample = new Sample(new LocalDate(cal.getTimeInMillis()), -9999);
            } else if (!(value instanceof Float)) {
                throw new IllegalStateException("unsupported time series value format: " + value); // NOI18N
            } else {
                sample = new Sample(new LocalDate(cal.getTimeInMillis()), ((Float)value).doubleValue());
            }

            ts.add(sample);

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return ts;
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        return new CalibrationWatchable(cidsBean, getRunInfo());
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (!(getWatchable() instanceof CalibrationWatchable)) {
            throw new IllegalStateException("cannot create output if there is no valid watchable"); // NOI18N
        }

        try {
            final CalibrationInput input = inputFromRun(cidsBean);
            final CidsBean hwBean = SMSUtils.fetchCidsBean(input.getHydrologyWorkspaceId(), "HYDROLOGY_WORKSPACE"); // NOI18N
            final Integer basinId = (Integer)hwBean.getProperty("basin_id");                                        // NOI18N
            final Integer inputTsId = input.getTimeseries(basinId);

            if (inputTsId == null) {
                LOG.warn("no timeseries present for area of interest: " + basinId);
            }

            final String domain = SessionManager.getSession().getUser().getDomain();
            final MetaClass tsClass = ClassCacheMultiple.getMetaClass(domain, "TIMESERIES"); // NOI18N

            final CalibrationWatchable watchable = (CalibrationWatchable)getWatchable();
            CidsBean tsBean = tsClass.getEmptyInstance().getBean();
            tsBean.setProperty("name", watchable.getResultTs().getOffering());
            tsBean.setProperty("uri", watchable.getResultTs().toUrl()); // NOI18N
            tsBean.setProperty("forecast", Boolean.FALSE);              // NOI18N
            tsBean = tsBean.persist();

            final CalibrationOutput output = new CalibrationOutput();
            output.setResultTs(tsBean.getMetaObject().getID());
            output.setInputTs(inputTsId);

            return SMSUtils.createModelOutput("Result of Calibration run " + cidsBean.getProperty("name"),
                    output,
                    Model.HY_CAL);
        } catch (final Exception ex) {
            final String message = "cannot create output"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    @Override
    protected String getReloadId() {
        try {
            final CalibrationInput input = inputFromRun(cidsBean);

            return "hydrology.localmodel." + input.getHydrologyWorkspaceId() + ".calibration";
        } catch (final IOException ex) {
            LOG.warn("cannot fetch input from run, reload id cannot be built", ex); // NOI18N

            return null;
        }
    }

    @Override
    public CalibrationRunInfo getRunInfo() {
        return SMSUtils.getRunInfo(cidsBean, CalibrationRunInfo.class);
    }
}
