/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.exception.ConnectionException;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval.Openness;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import se.smhi.sudplan.client.ExecutionStatus;
import se.smhi.sudplan.client.SudPlanHypeAPI;

import java.io.IOException;

import java.net.URL;

import java.text.DateFormat;
import java.text.MessageFormat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.commons.utils.ProgressEvent;
import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SudplanOptions;
import de.cismet.cids.custom.sudplan.TimeSeriesRemoteHelper;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.TimeseriesTransmitter;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SimulationWatchable extends AbstractModelRunWatchable {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(SimulationWatchable.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SimulationRunInfo runinfo;

    private final transient Set<TimeseriesRetrieverConfig> results;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimulationWatchable object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  runinfo   DOCUMENT ME!
     */
    public SimulationWatchable(final CidsBean cidsBean, final SimulationRunInfo runinfo) {
        super(cidsBean);

        this.runinfo = runinfo;
        this.results = new HashSet<TimeseriesRetrieverConfig>();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ProgressEvent requestStatus() throws IOException {
        final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();
        final ExecutionStatus status = hypeClient.getExecutionStatus(runinfo.getExecutionId());
        switch (status) {
            case NOT_STARTED: {
                return new ProgressEvent(this, ProgressEvent.State.PROGRESSING);
            }
            case RUNNING: {
                return new ProgressEvent(this, ProgressEvent.State.PROGRESSING);
            }
            case UNKNOWN: {
                return new ProgressEvent(
                        this,
                        ProgressEvent.State.BROKEN,
                        "Unknown execution status for simulation process: " // NOI18N
                                + runinfo.getExecutionId());
            }
            case DONE: {
                return new ProgressEvent(this, ProgressEvent.State.FINISHED);
            }
            default: {
                throw new IllegalStateException("unknown status: " + status); // NOI18N
            }
        }
    }

    @Override
    public void startDownload() {
        setStatus(State.RUNNING);

        try {
            final Object inputObject = SMSUtils.inputFromRun(getCidsBean());
            if (!(inputObject instanceof SimulationInput)) {
                throw new IllegalStateException("unsupported input type: " + inputObject); // NOI18N
            }

            final SimulationInput input = (SimulationInput)inputObject;
            final DateFormat df = HydrologyCache.getInstance().getHydroDateFormat();

            final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();

            final String tsIdPattern = "sim_result_{0}_" + System.currentTimeMillis();                                   // NOI18N
            final String tsOfferingPattern = tsIdPattern + "_" + Resolution.DAY.getPrecision();                          // NOI18N
            final String tsProcedurePattern = "urn:ogc:object:" + tsIdPattern + ":{0}:" + Resolution.DAY.getPrecision(); // NOI18N

            for (final String var : HydrologyCache.getInstance().getVars()) {
                final String tsOffering = MessageFormat.format(tsOfferingPattern, var);
                final String tsProcedure = MessageFormat.format(tsProcedurePattern, var);

                hypeClient.storeSimulationResult(runinfo.getSimulationId(),
                    var,
                    df.format(input.getStartDate()),
                    tsOffering);

                final TimeInterval interval = new TimeInterval();
                interval.setLeft(Openness.OPEN);
                interval.setRight(Openness.OPEN);
                interval.setStart(new TimeStamp(input.getStartDate()));
                interval.setEnd(new TimeStamp(input.getEndDate()));

                final TimeseriesRetrieverConfig cfg = new TimeseriesRetrieverConfig(
                        TimeseriesRetrieverConfig.PROTOCOL_HYPE,
                        null,
                        new URL("http://" + SudplanOptions.getInstance().getHdHypeIp()), // NOI18N
                        tsProcedure,
                        String.valueOf(runinfo.getBasinId()),
                        HydrologyCache.getInstance().getVariableForVar(var).getPropertyKey(),
                        tsOffering,
                        null,
                        interval);

                final Future<TimeSeries> tsFuture = TimeseriesRetriever.getInstance().retrieve(cfg);
                final TimeSeries ts = tsFuture.get();

                ts.setTSProperty(PropertyNames.DESCRIPTION, tsOffering);

                final URL tsUrl = new URL(TimeSeriesRemoteHelper.DAV_HOST + "/" + tsOffering); // NOI18N
                final Future<Boolean> ttFuture = TimeseriesTransmitter.getInstance()
                            .put(tsUrl, ts, TimeSeriesRemoteHelper.DAV_CREDS);

                if (!ttFuture.get()) {
                    throw new IOException("could not put time series to dav"); // NOI18N
                }

                final TimeseriesRetrieverConfig resultCfg = new TimeseriesRetrieverConfig(
                        TimeseriesRetrieverConfig.PROTOCOL_DAV,
                        null,
                        new URL(TimeSeriesRemoteHelper.DAV_HOST),
                        tsProcedure,
                        String.valueOf(runinfo.getBasinId()),
                        HydrologyCache.getInstance().getVariableForVar(var).getPropertyKey(),
                        tsOffering,
                        null,
                        null);

                results.add(resultCfg);
            }

            setStatus(State.COMPLETED);
        } catch (final Exception e) {
            final String message = "cannot download simulation results"; // NOI18N
            LOG.error(message, e);
            setDownloadException(e);
            setStatus(State.COMPLETED_WITH_ERROR);
        }
    }

    @Override
    public String getTitle() {
        try {
            return "Simulation results of " + getCidsBean().getProperty("name");
        } catch (final ConnectionException ex) {
            LOG.error("cannot build title", ex); // NOI18N

            return "Simulation results of unknown execution: " + ex;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<TimeseriesRetrieverConfig> getResults() {
        return results;
    }
}
