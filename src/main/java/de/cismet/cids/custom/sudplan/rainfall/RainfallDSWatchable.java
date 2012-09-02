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
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.ProgressEvent.State;

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
    private transient Float[][] dsStatRes;
    private transient IDFCurve curve;

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

        final TimeSeries statusTs = dp.getTimeSeries(new TimeInterval(
                    TimeInterval.Openness.OPEN,
                    TimeStamp.NEGATIVE_INFINITY,
                    TimeStamp.POSITIVE_INFINITY,
                    TimeInterval.Openness.OPEN));

        // search for errors first and bail out if present
        for (final TimeStamp stamp : statusTs.getTimeStamps().descendingSet()) {
            final Object value = statusTs.getValue(stamp, PropertyNames.TaskErrors);
            // f***n bull**** error handling
            if ((value != null) && (value instanceof String[])) {
                final String[] errors = (String[])value;
                for (final String error : errors) {
                    if ((error != null) && !error.isEmpty()) {
                        LOG.error("errors found for rf ds run: " + runId + " | errors: " + Arrays.toString(errors)); // NOI18N

                        return new ProgressEvent(
                                this,
                                ProgressEvent.State.BROKEN,
                                "errors found for rf ds run: "
                                        + runId);
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
                    LOG.debug("state of ds task: " + runId + " = " + state); // NOI18N
                }

                // FIXME: mapping not started yet to running
                if (SPS_TASK_STATE_NOT_STARTED_YET.equals(state) || SPS_TASK_STATE_RUNNING.equals(state)) {
                    return new ProgressEvent(
                            this,
                            ProgressEvent.State.PROGRESSING,
                            -1,
                            -1,
                            "state of ds task: "
                                    + runId
                                    + " = "
                                    + state);
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

        final TimeSeries statusTs = dp.getTimeSeries(new TimeInterval(
                    TimeInterval.Openness.OPEN,
                    TimeStamp.NEGATIVE_INFINITY,
                    TimeStamp.POSITIVE_INFINITY,
                    TimeInterval.Openness.OPEN));

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

                // assume idf crap
                if (results.length == 2) {
                    final String dsOrig = results[1].substring(results[1].indexOf("idf:idf"), results[1].length() - 1); // NOI18N

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processing idf ds results"); // NOI18N
                    }

                    try {
                        final DataHandler dh = DataHandlerCache.getInstance()
                                    .getSOSDataHandler(String.valueOf(System.currentTimeMillis()),
                                        SudplanOptions.getInstance().getRfSosUrl());
                        final Properties filter = new Properties();
                        filter.setProperty(TimeSeries.OFFERING, dsOrig);

                        final Set<Datapoint> dps = dh.getDatapoints(filter, Access.READ);
                        if (dps.size() != 1) {
                            throw new IllegalStateException("there should be exactly one datapoint: " + dsOrig); // NOI18N
                        }

                        final Datapoint datapoint = dps.iterator().next();

                        final TimeSeries ts = datapoint.getTimeSeries(TimeInterval.ALL_INTERVAL);
                        final TimeStamp[] stamps = ts.getTimeStampsArray();
                        final Float[] durations = (Float[])ts.getValue(stamps[0], "Duration");       // NOI18N
                        final Float[] frequencies = (Float[])ts.getValue(stamps[0], "ReturnPeriod"); // NOI18N
                        final Float[] intensities = (Float[])ts.getValue(stamps[0], "Intensity");    // NOI18N

                        assert (durations.length == frequencies.length) && (frequencies.length == intensities.length);

                        curve = new IDFCurve();
                        for (int i = 0; i < durations.length; ++i) {
                            curve.add(
                                durations[i].intValue(),
                                frequencies[i].intValue(),
                                Math.round(intensities[i].doubleValue() * 100)
                                        / 100);
                        }

                        setStatus(State.COMPLETED);
                    } catch (final Exception ex) {
                        LOG.error("error while downloading the results", ex); // NOI18N
                        setDownloadException(ex);
                        setStatus(State.COMPLETED_WITH_ERROR);
                    }
                } else {
                    final String dsOrigResult = results[2].substring(results[2].indexOf("tsf"), // NOI18N
                            results[2].length()
                                    - 1);
                    final String dsDailyResult = results[3].substring(results[3].indexOf("tsf"), // NOI18N
                            results[3].length()
                                    - 1);
                    final String dsStatisticalResult = results[4].substring(results[4].indexOf("tbl:"),
                            results[4].length()
                                    - 1);
                    // table results don't work! it's annoying
                    try {
                        // ds results with original resolution
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("processing ds result with original resolution"); // NOI18N
                        }

                        final DataHandler dh = DataHandlerCache.getInstance()
                                    .getSOSDataHandler(String.valueOf(System.currentTimeMillis()),
                                        SudplanOptions.getInstance().getRfSosUrl());
                        final Properties filter = new Properties();
                        filter.setProperty(TimeSeries.OFFERING, dsOrigResult);

                        Set<Datapoint> dps = dh.getDatapoints(filter, Access.READ);
                        if (dps.size() != 1) {
                            throw new IllegalStateException("there should be exactly one datapoint: " + dsOrigResult); // NOI18N
                        }

                        Datapoint datapoint = dps.iterator().next();
                        TimeSeries ts = datapoint.getTimeSeries(new TimeInterval(
                                    TimeInterval.Openness.OPEN,
                                    TimeStamp.NEGATIVE_INFINITY,
                                    TimeStamp.POSITIVE_INFINITY,
                                    TimeInterval.Openness.OPEN));

                        final CidsBean bean = getCidsBean();
                        assert bean != null : "null cidsbean in rainfall watchable"; // NOI18N

                        final Manager m = SMSUtils.loadManagerFromRun(bean, ManagerType.INPUT);
                        m.setCidsBean((CidsBean)bean.getProperty("modelinput"));                              // NOI18N
                        final RainfallDownscalingInput input = (RainfallDownscalingInput)m.getUR();
                        final CidsBean tsBean = input.fetchRainfallObject();
                        final String tsName = URLEncoder.encode((String)tsBean.getProperty("name"), "UTF-8"); // NOI18N

                        URL url = new URL(TimeSeriesRemoteHelper.DAV_HOST + "/" + tsName + "_" + runId + "_unknown"); // NOI18N

                        TimeseriesTransmitter.getInstance().put(url, ts, TimeSeriesRemoteHelper.DAV_CREDS);

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
                        ts = datapoint.getTimeSeries(new TimeInterval(
                                    TimeInterval.Openness.OPEN,
                                    TimeStamp.NEGATIVE_INFINITY,
                                    TimeStamp.POSITIVE_INFINITY,
                                    TimeInterval.Openness.OPEN));

                        url = new URL(TimeSeriesRemoteHelper.DAV_HOST + "/" + tsName + "_" + runId + "_86400s"); // NOI18N

                        TimeseriesTransmitter.getInstance().put(url, ts, TimeSeriesRemoteHelper.DAV_CREDS);

                        dsDailyRes = url;

                        // ds statistical results
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("processing ds result with daily resolution"); // NOI18N
                        }

                        filter.setProperty(TimeSeries.OFFERING, dsStatisticalResult);
                        dps = dh.getDatapoints(filter, Access.READ);
                        if (dps.size() != 1) {
                            throw new IllegalStateException("there should be exactly one datapoint: "
                                        + dsStatisticalResult); // NOI18N
                        }

                        datapoint = dps.iterator().next();
                        ts = datapoint.getTimeSeries(new TimeInterval(
                                    TimeInterval.Openness.OPEN,
                                    TimeStamp.NEGATIVE_INFINITY,
                                    TimeStamp.POSITIVE_INFINITY,
                                    TimeInterval.Openness.OPEN));

                        final TimeStamp avDataMinStamp = new TimeStamp((Date)ts.getTSProperty(
                                    TimeSeries.AVAILABLE_DATA_MIN));
                        final Float[] minValues = (Float[])ts.getValue(avDataMinStamp, "Minimum");
                        final Float[] maxValues = (Float[])ts.getValue(avDataMinStamp, "Maximum");
                        final Float[] freqValues = (Float[])ts.getValue(avDataMinStamp, "Frequency");

                        final Float[][] tableData = new Float[3][];
                        tableData[0] = minValues;
                        tableData[1] = maxValues;
                        tableData[2] = freqValues;

                        dsStatRes = tableData;

                        setStatus(State.COMPLETED);
                    } catch (final Exception ex) {
                        LOG.error("error while downloading the results", ex); // NOI18N
                        setDownloadException(ex);
                        setStatus(State.COMPLETED_WITH_ERROR);
                    }
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
     * First min, then max, then freq.
     *
     * @return  DOCUMENT ME!
     */
    public Float[][] getStatisticalResult() {
        return dsStatRes;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRunId() {
        return runId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public IDFCurve getResultCurve() {
        return curve;
    }
}
