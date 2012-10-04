/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.NbBundle;

import java.io.IOException;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.SMSUtils.Model;
import de.cismet.cids.custom.sudplan.airquality.AirqualityDownscalingOutput.Result;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingModelManager.class);

    public static final String PARAM_CLIMATE_SCENARIO = "climate_scenario";   // NOI18N
    public static final String PARAM_START_TIME = "start_time";               // NOI18N
    public static final String PARAM_END_TIME = "end_time";                   // NOI18N
    public static final String PARAM_COORDINATE_SYSTEM = "coordinate_system"; // NOI18N
    public static final String PARAM_X_MIN = "x_min";                         // NOI18N
    public static final String PARAM_Y_MIN = "y_min";                         // NOI18N
    public static final String PARAM_X_MAX = "x_max";                         // NOI18N
    public static final String PARAM_Y_MAX = "y_max";                         // NOI18N
    public static final String PARAM_N_X = "n_x";                             // NOI18N
    public static final String PARAM_N_Y = "n_y";                             // NOI18N
    public static final String PARAM_EMISSION_SCENARIO = "emission_scenario"; // NOI18N

    public static final String AQ_SOS_LOOKUP = "airquality_sos_lookup";                  // NOI18N
    public static final String AQ_SPS_LOOKUP = "airquality_sps_lookup";                  // NOI18N
    public static final String AQ_TS_DS_PROCEDURE = "AirQuality_Timeseries_Downscaling"; // NOI18N

    public static final String AQ_RESULT_KEY_URL = "ts:result_service_url";   // NOI18N
    public static final String AQ_RESULT_KEY_TYPE = "ts:result_service_type"; // NOI18N
    public static final String AQ_RESULT_KEY_DESCRIPTION = "ts:description";  // NOI18N
    public static final String AQ_RESULT_KEY_OFFERING = "ts:offering";        // NOI18N

    public static final int STEPS = 3;
    protected static final DateFormat DATEFORMAT_SOS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    //~ Instance fields --------------------------------------------------------

    private transient Datapoint runningTask;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("Can't create output bean when not finished yet."); // NOI18N
        }

        if (!(getWatchable() instanceof AirqualityDownscalingWatchable)) {
            throw new IllegalStateException("Can't create output if there is no valid watchable."); // NOI18N
        }

        final AirqualityDownscalingWatchable watchable = (AirqualityDownscalingWatchable)getWatchable();
        final AirqualityDownscalingOutput output = new AirqualityDownscalingOutput();

        try {
            output.setModelInputId((Integer)cidsBean.getProperty("modelinput.id")); // NOI18N
            output.setModelRunId((Integer)cidsBean.getProperty("id"));              // NOI18N
            output.setResults(parseResults(watchable.getOfferings()));
        } catch (final Exception e) {
            final String message = "Can't create model output";                     // NOI18N
            LOG.error(message, e);
            this.fireBroken(message);
            throw new IOException(message, e);
        }

        return SMSUtils.createModelOutput(NbBundle.getMessage(
                    AirqualityDownscalingModelManager.class,
                    "AirqualityDownscalingModelManager.createOutputBean().output.name", // NOI18N
                    cidsBean.getProperty("name")), // NOI18N
                output,
                Model.AQ_DS);
    }

    @Override
    protected String getReloadId() {
        return "airquality.*"; // NOI18N
    }

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing airquality downscaling."); // NOI18N
        }

        fireProgressed(
            0,
            STEPS,
            NbBundle.getMessage(
                AirqualityDownscalingModelManager.class,
                "AirqualityDownscalingModelManager.prepareExecution().progress.dispatch")); // NOI18N

        final String runId = dispatchDownscaling();

        fireProgressed(
            1,
            STEPS,
            NbBundle.getMessage(
                AirqualityDownscalingModelManager.class,
                "AirqualityDownscalingModelManager.prepareExecution().progress.save")); // NOI18N

        final AirqualityDownscalingRunInfo runInfo = new AirqualityDownscalingRunInfo(
                runId,
                AQ_SPS_LOOKUP,
                SudplanOptions.getInstance().getAqSpsUrl());

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, runInfo);

            cidsBean.setProperty("runinfo", writer.toString());           // NOI18N
            cidsBean = cidsBean.persist();
        } catch (final Exception ex) {
            final String message = "Can't store runinfo: " + runId + "."; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        // now set to indeterminate
        fireProgressed(
            -1,
            -1,
            NbBundle.getMessage(
                AirqualityDownscalingModelManager.class,
                "AirqualityDownscalingModelManager.prepareExecution().progress.running", // NOI18N
                runInfo.getTaskId()));
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        if (cidsBean == null) {
            throw new IllegalStateException("CidsBean not set."); // NOI18N
        }

        final AirqualityDownscalingRunInfo runInfo = this.getRunInfo();

        if (runInfo == null) {
            throw new IllegalStateException("Run info not set."); // NOI18N
        }

        if (runInfo.isCanceled() || runInfo.isBroken()) {
            final String message = "Run '" + cidsBean + "' is canceled or broken, ignoring run."; // NOI18N
            LOG.warn(message);
            throw new IllegalStateException(message);
        } else {
            try {
                if (runningTask == null) {
                    final DataHandler datahandler = DataHandlerCache.getInstance()
                                .getSPSDataHandler(
                                    runInfo.getHandlerLookup(),
                                    runInfo.getHandlerUrl());
                    return new AirqualityDownscalingWatchable(cidsBean, datahandler, runInfo.getTaskId());
                } else {
                    return new AirqualityDownscalingWatchable(cidsBean, runInfo.getTaskId(), runningTask);
                }
            } catch (final Exception ex) {
                final String message = "Can't read run info from run: " + cidsBean + ".";         // NOI18N
                LOG.error(message, ex);
                this.fireBroken(message);
                throw new IOException(message, ex);
            }
        }
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    @Override
    public AirqualityDownscalingRunInfo getRunInfo() {
        return SMSUtils.<AirqualityDownscalingRunInfo>getRunInfo(cidsBean, AirqualityDownscalingRunInfo.class);
    }

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
    public static AirqualityDownscalingInput inputFromRun(final CidsBean runBean) throws IOException {
        final Manager manager = SMSUtils.loadManagerFromRun(runBean, ManagerType.INPUT);
        manager.setCidsBean((CidsBean)runBean.getProperty("modelinput")); // NOI18N

        final Object resource = manager.getUR();
        if (!(resource instanceof AirqualityDownscalingInput)) {
            throw new IllegalStateException("Manager resource is not suited for airquality downscaling"); // NOI18N
        }

        return (AirqualityDownscalingInput)resource;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private String dispatchDownscaling() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Preparing downscaling run."); // NOI18N
        }

        final DataHandler dataHandler;
        try {
            dataHandler = DataHandlerCache.getInstance()
                        .getSPSDataHandler(AQ_SPS_LOOKUP, SudplanOptions.getInstance().getAqSpsUrl());
        } catch (final DataHandlerCacheException ex) {
            final String message = "Can't fetch datahandler."; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        final Properties filter = new Properties();
        filter.put(TimeSeries.PROCEDURE, AQ_TS_DS_PROCEDURE);

        final Datapoint datapoint = dataHandler.createDatapoint(filter, null, DataHandler.Access.READ_WRITE);

        final AirqualityDownscalingInput input = inputFromRun(cidsBean);

        final TimeSeries timeseries = new TimeSeriesImpl(datapoint.getProperties());
        final TimeStamp now = new TimeStamp();

        timeseries.setValue(now, PARAM_CLIMATE_SCENARIO, input.getScenario());
        timeseries.setValue(now, PARAM_COORDINATE_SYSTEM, input.getSrs()); // NOI18N
        timeseries.setValue(now, PARAM_EMISSION_SCENARIO, input.getDatabase());
        timeseries.setValue(now, PARAM_END_TIME, DATEFORMAT_SOS.format(input.getEndDate()));
        timeseries.setValue(now, PARAM_N_X, input.getGridcellCountX().toString());
        timeseries.setValue(now, PARAM_N_Y, input.getGridcellCountY().toString());
        timeseries.setValue(now, PARAM_START_TIME, DATEFORMAT_SOS.format(input.getStartDate()));
        timeseries.setValue(now, PARAM_X_MAX, Double.toString(input.getUpperright().x));
        timeseries.setValue(now, PARAM_X_MIN, Double.toString(input.getLowerleft().x));
        timeseries.setValue(now, PARAM_Y_MAX, Double.toString(input.getUpperright().y));
        timeseries.setValue(now, PARAM_Y_MIN, Double.toString(input.getLowerleft().y));

        timeseries.setValue(now, PropertyNames.TaskAction, PropertyNames.TaskActionStart);

        datapoint.putTimeSeries(timeseries);

        runningTask = datapoint;

        return (String)datapoint.getProperties().get(PropertyNames.TASK_ID);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   resultsToParse  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<Result> parseResults(final Collection<String> resultsToParse) {
        final Collection<Result> result = new LinkedList<Result>();

        for (final String resultToParse : resultsToParse) {
            String url = null;
            String type = null;
            String description = null;
            String offering = null;

            for (final String resultKeyValue : resultToParse.split(";")) { // NOI18N
                final String[] keyValue = resultKeyValue.split("=");       // NOI18N

                if (keyValue.length != 2) {
                    LOG.warn("Result '" + resultToParse
                                + "' is invalid (only one '=' allowed for a key-value pair). Skipping this result."); // NOI18N
                    continue;
                }

                if (AQ_RESULT_KEY_URL.equalsIgnoreCase(keyValue[0])) {
                    if (url == null) {
                        url = keyValue[1];
                    } else {
                        LOG.warn("Result '" + resultToParse + "' contains more than one url. Skipping this result.");  // NOI18N
                        continue;
                    }
                } else if (AQ_RESULT_KEY_TYPE.equalsIgnoreCase(keyValue[0])) {
                    if (type == null) {
                        type = keyValue[1];
                    } else {
                        LOG.warn("Result '" + resultToParse + "' contains more than one type. Skipping this result."); // NOI18N
                        continue;
                    }
                } else if (AQ_RESULT_KEY_DESCRIPTION.equalsIgnoreCase(keyValue[0])) {
                    if (description == null) {
                        description = keyValue[1];
                    } else {
                        LOG.warn("Result '" + resultToParse
                                    + "' contains more than one description. Skipping this result.");                  // NOI18N
                        continue;
                    }
                } else if (AQ_RESULT_KEY_OFFERING.equalsIgnoreCase(keyValue[0])) {
                    if (offering == null) {
                        offering = keyValue[1];
                    } else {
                        LOG.warn("Result '" + resultToParse
                                    + "' contains more than one offering. Skipping this result.");                     // NOI18N
                        continue;
                    }
                }
            }

            if ((url != null) && (type != null) && (description != null) && (offering != null)) {
                result.add(new Result(url, type, description, offering));
            }
        }

        return result;
    }
}
