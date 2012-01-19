/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.DataHandler.Access;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.URL;
import java.net.URLEncoder;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.cids.custom.sudplan.DataHandlerCache;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.ProgressEvent;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeSeriesRemoteHelper;
import de.cismet.cids.custom.sudplan.TimeseriesTransmitter;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallDSWatchable extends AbstractModelRunWatchable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RainfallDSWatchable.class);

    public static final String SPS_TASK_STATE_FINISHED = "finished";               // NOI18N
    public static final String SPS_TASK_STATE_RUNNING = "in operation";            // NOI18N
    public static final String SPS_TASK_STATE_NOT_STARTED_YET = "not yet started"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final String runId;
    private final Datapoint dp;

    private transient URL dsDailyRes;
    private transient URL dsOrigRes;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDSWatchable object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  runId     DOCUMENT ME!
     * @param  dp        DOCUMENT ME!
     */
    public RainfallDSWatchable(final CidsBean cidsBean, final String runId, final Datapoint dp) {
        super(cidsBean);
        this.runId = runId;
        this.dp = dp;
    }

    /**
     * Creates a new RainfallDSWatchable object.
     *
     * @param   cidsBean    DOCUMENT ME!
     * @param   spsHandler  DOCUMENT ME!
     * @param   runId       DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public RainfallDSWatchable(final CidsBean cidsBean, final DataHandler spsHandler, final String runId)
            throws IOException {
        super(cidsBean);

        final Properties filter = new Properties();
        filter.put(PropertyNames.TASK_ID, runId);
        final Set<Datapoint> dps = spsHandler.getDatapoints(filter, DataHandler.Access.READ);
        if (dps.isEmpty()) {
            throw new IOException("no task available with runid: " + runId + " | spsHandler=" + spsHandler); // NOI18N
        } else if (dps.size() > 1) {
            throw new IOException("too many task available with runid: " + runId                             // NOI18N
                        + " | spsHandler=" + spsHandler);                                                    // NOI18N
        } else {
            dp = dps.iterator().next();
        }

        this.runId = runId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ProgressEvent requestStatus() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("requesting status for rf ds run: " + runId); // NOI18N
        }

        final TimeSeries statusTs = dp.getTimeSeries(TimeInterval.ALL_INTERVAL);

        // search for errors first and bail out if present
        for (final TimeStamp stamp : statusTs.getTimeStamps().descendingSet()) {
            final Object value = statusTs.getValue(stamp, PropertyNames.TaskErrors);
            // f***n bull**** error handling
            if ((value != null) && (value instanceof String[])) {
                final String[] errors = (String[])value;
                for (final String error : errors) {
                    if ((error != null) && !error.isEmpty()) {
                        LOG.error("errors found for rf ds run: " + runId + " | errors: " + Arrays.toString(errors)); // NOI18N

                        return new ProgressEvent(this, ProgressEvent.State.BROKEN);
                    }
                }
            }
        }

        // find the current status
        for (final TimeStamp stamp : statusTs.getTimeStamps().descendingSet()) {
            final Object value = statusTs.getValue(stamp, PropertyNames.TaskStatus);
            if ((value != null) && (value instanceof String)) {
                final String state = (String)value;

                if (LOG.isDebugEnabled()) {
                    LOG.debug("state of task: " + runId + " = " + state); // NOI18N
                }

                // FIXME: mapping not started yet to running
                if (SPS_TASK_STATE_NOT_STARTED_YET.equals(state) || SPS_TASK_STATE_RUNNING.equals(state)) {
                    return new ProgressEvent(this, ProgressEvent.State.PROGRESSING);
                } else if (SPS_TASK_STATE_FINISHED.equals(state)) {
                    return new ProgressEvent(this, ProgressEvent.State.FINISHED);
                } else {
                    throw new IOException("unknown status for run: " + runId + " = " + state); // NOI18N
                }
            }
        }

        throw new IOException("cannot extract status from timeseries"); // NOI18N
    }

    @Override
    public void startDownload() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("download results for rf ds run: " + runId); // NOI18N
        }

        setStatus(State.RUNNING);

        final TimeSeries statusTs = dp.getTimeSeries(TimeInterval.ALL_INTERVAL);

        // FIXME: bull**** impl

        // find the run results status
        for (final TimeStamp stamp : statusTs.getTimeStamps().descendingSet()) {
            final Object value = statusTs.getValue(stamp, PropertyNames.TaskResults);
            if ((value != null) && (value instanceof String[])) {
                final String[] results = (String[])value;

                if (LOG.isDebugEnabled()) {
                    LOG.debug("results of task: " + runId + " = " + Arrays.toString(results)); // NOI18N
                }

                // bull**** result, has to be parsed etc pp, crap code for crap api
                final String histDailyResult = results[1].substring(results[1].indexOf("tsf"), results[1].length() - 1); // NOI18N
                final String dsOrigResult = results[2].substring(results[2].indexOf("tsf"), results[2].length() - 1);    // NOI18N
                final String dsDailyResult = results[3].substring(results[3].indexOf("tsf"), results[3].length() - 1);   // NOI18N
                // table results don't work! it's annoying
                try {
                    // ds results with original resolution
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processing ds result with original resolution"); // NOI18N
                    }

                    final DataHandler dh = DataHandlerCache.getInstance()
                                .getSOSDataHandler(String.valueOf(System.currentTimeMillis()),
                                    RainfallDownscalingModelManager.RF_SOS_URL);
                    final Properties filter = new Properties();
                    filter.setProperty(TimeSeries.OFFERING, dsOrigResult);

                    Set<Datapoint> dps = dh.getDatapoints(filter, Access.READ);
                    if (dps.size() != 1) {
                        throw new IllegalStateException("there should be exactly one datapoint: " + dsOrigResult); // NOI18N
                    }

                    Datapoint datapoint = dps.iterator().next();
                    TimeSeries ts = datapoint.getTimeSeries(TimeInterval.ALL_INTERVAL);

                    final CidsBean bean = getCidsBean();
                    assert bean != null : "null cidsbean in rainfall watchable"; // NOI18N

                    final Manager m = SMSUtils.loadManagerFromRun(bean, ManagerType.INPUT);
                    m.setCidsBean((CidsBean)bean.getProperty("modelinput"));                              // NOI18N
                    final RainfallDownscalingInput input = (RainfallDownscalingInput)m.getUR();
                    final CidsBean tsBean = input.fetchTimeseries();
                    final String tsName = URLEncoder.encode((String)tsBean.getProperty("name"), "UTF-8"); // NOI18N

                    URL url = new URL(TimeSeriesRemoteHelper.DAV_HOST + "/" + tsName + "_" + runId + "_unknown"); // NOI18N

                    TimeseriesTransmitter.getInstance().put(url, ts, TimeSeriesRemoteHelper.CREDS);

                    dsOrigRes = url;

                    // ds results with daily resolution
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processing ds result with daily resolution"); // NOI18N
                    }

                    filter.setProperty(TimeSeries.OFFERING, dsDailyResult);
                    dps = dh.getDatapoints(filter, Access.READ);
                    if (dps.size() != 1) {
                        throw new IllegalStateException("there should be exactly one datapoint: " + dsOrigResult); // NOI18N
                    }

                    datapoint = dps.iterator().next();
                    ts = datapoint.getTimeSeries(TimeInterval.ALL_INTERVAL);

                    url = new URL(TimeSeriesRemoteHelper.DAV_HOST + "/" + tsName + "_" + runId + "_86400s"); // NOI18N

                    TimeseriesTransmitter.getInstance().put(url, ts, TimeSeriesRemoteHelper.CREDS);

                    dsDailyRes = url;

                    setStatus(State.COMPLETED);
                } catch (final Exception ex) {
                    LOG.error("error while downloading the results", ex); // NOI18N
                    setDownloadException(ex);
                    setStatus(State.COMPLETED_WITH_ERROR);
                }
            }
        }

        throw new IllegalStateException("no results found for task with id: " + runId); // NOI18N
    }

    @Override
    public String getTitle() {
        return "Results of Rainfall Downscaling run: " + runId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public URL getDailyResolutionResult() {
        return dsDailyRes;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public URL getOrigResolutionResult() {
        return dsOrigRes;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRunId() {
        return runId;
    }
}
