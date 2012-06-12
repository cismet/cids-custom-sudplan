/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval.Openness;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import se.smhi.sudplan.client.ExecutionStatus;
import se.smhi.sudplan.client.Scenario;
import se.smhi.sudplan.client.SudPlanHypeAPI;
import se.smhi.sudplan.client.exception.UnknownWorkareaException;

import java.io.IOException;
import java.io.StringWriter;

import java.net.URL;

import java.text.DateFormat;

import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.cids.custom.sudplan.ProgressEvent;
import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.TimeSeriesRemoteHelper;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.TimeseriesTransmitter;
import de.cismet.cids.custom.sudplan.Variable;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CalibrationWatchable extends AbstractModelRunWatchable {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CalibrationWatchable.class);

    //~ Instance fields --------------------------------------------------------

    private final transient CalibrationRunInfo runinfo;

    private transient TimeseriesRetrieverConfig resultTs;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CalibrationWatchable object.
     *
     * @param  runBean  DOCUMENT ME!
     * @param  runinfo  DOCUMENT ME!
     */
    public CalibrationWatchable(final CidsBean runBean, final CalibrationRunInfo runinfo) {
        super(runBean);

        this.runinfo = runinfo;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ProgressEvent requestStatus() throws IOException {
        // we need special handling here since the calibration is a two step process
        final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();

        if (runinfo.getCalibrationExecutionId() == null) {
            // the submodel creation is still running
            final ExecutionStatus status = hypeClient.getExecutionStatus(runinfo.getSubmodelExecutionId());
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
                            "Unknown execution status for submodel creation process");
                }
                case DONE: {
                    try {
                        // we have to start the next execution, the real calibration
                        // NOTE: in case of a null local model id
                        final String execId = hypeClient.runSimulation(runinfo.getLocalModelId());
                        runinfo.setCalibrationExecutionId(execId);
                    } catch (final UnknownWorkareaException ex) {
                        final String message = "illegal local model id: " + runinfo.getLocalModelId(); // NOI18N
                        LOG.error(message, ex);
                        throw new IllegalStateException(message, ex);
                    } catch (final Exception ex) {
                        final String message = "cannot create submodel: " + runinfo.getLocalModelId(); // NOI18N
                        LOG.error(message, ex);
                        throw new IOException(message, ex);
                    }

                    try {
                        final ObjectMapper mapper = new ObjectMapper();
                        final StringWriter writer = new StringWriter();

                        mapper.writeValue(writer, runinfo);

                        getCidsBean().setProperty("runinfo", writer.toString());                     // NOI18N
                        getCidsBean().persist();
                    } catch (final Exception ex) {
                        final String message = "cannot store runinfo: " + runinfo.getLocalModelId(); // NOI18N
                        LOG.error(message, ex);
                        throw new IOException(message, ex);
                    }

                    return new ProgressEvent(this, ProgressEvent.State.PROGRESSING, "Calibrating submodel");
                }
                default: {
                    throw new IllegalStateException("unknown status: " + status); // NOI18N
                }
            }
        } else {
            // the calibration is running
            final ExecutionStatus status = hypeClient.getExecutionStatus(runinfo.getCalibrationExecutionId());
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
                            "Unknown execution status for calibration process");
                }
                case DONE: {
                    return new ProgressEvent(this, ProgressEvent.State.FINISHED);
                }
                default: {
                    throw new IllegalStateException("unknown status: " + status); // NOI18N
                }
            }
        }
    }

    @Override
    public void startDownload() {
        setStatus(State.RUNNING);

        final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();
        final String tsId = "cal_result_" + System.currentTimeMillis();                                 // NOI18N
        final String tsOfferingUnknown = tsId + "_unknown";                                             // NOI18N
        final String tsProcedureUnknown = "urn:ogc:object:" + tsId + ":cout:unknown";                   // NOI18N
        final String tsOffering = tsId + "_" + Resolution.DAY.getPrecision();
        final String tsProcedure = "urn:ogc:object:" + tsId + ":cout:" + Resolution.DAY.getPrecision(); // NOI18N

        try {
            final Scenario scenario = HydrologyCache.getInstance().getCalibrationScenario();
            final DateFormat df = HydrologyCache.getInstance().getHydroDateFormat();
            hypeClient.storeSimulationResult(runinfo.getLocalModelId(), "cout", scenario.getCdate(), tsOffering); // NOI18N

            final TimeInterval interval = new TimeInterval();
            interval.setLeft(Openness.OPEN);
            interval.setRight(Openness.OPEN);
            interval.setStart(new TimeStamp(df.parse(scenario.getCdate())));
            interval.setEnd(new TimeStamp(df.parse(scenario.getEdate())));

            final TimeseriesRetrieverConfig cfg = new TimeseriesRetrieverConfig(
                    TimeseriesRetrieverConfig.PROTOCOL_HYPE,
                    null,
                    new URL("http://79.125.2.136"), // NOI18N
                    tsProcedure,
                    String.valueOf(runinfo.getBasinId()),
                    Variable.COUT.getPropertyKey(),
                    tsOffering,
                    null,
                    interval);

            final Future<TimeSeries> tsFuture = TimeseriesRetriever.getInstance().retrieve(cfg);
            final TimeSeries ts = tsFuture.get();

            ts.setTSProperty(PropertyNames.DESCRIPTION, tsOffering);

            final URL tsUrl = new URL(TimeSeriesRemoteHelper.DAV_HOST + "/" + tsOffering); // NOI18N
            final Future<Boolean> ttFuture = TimeseriesTransmitter.getInstance()
                        .put(tsUrl, ts, TimeSeriesRemoteHelper.CREDS);

            if (!ttFuture.get()) {
                throw new IOException("could not put time series to dav"); // NOI18N
            }

            resultTs = new TimeseriesRetrieverConfig(
                    TimeseriesRetrieverConfig.PROTOCOL_DAV,
                    null,
                    new URL(TimeSeriesRemoteHelper.DAV_HOST),
                    tsProcedure,
                    String.valueOf(runinfo.getBasinId()),
                    Variable.COUT.getPropertyKey(),
                    tsOffering,
                    null,
                    null);

            setStatus(State.COMPLETED);
        } catch (final Exception ex) {
            final String message = "cannot store calibration results: " + runinfo.getLocalModelId(); // NOI18N
            LOG.error(message, ex);
            setDownloadException(ex);
            setStatus(State.COMPLETED_WITH_ERROR);
        }
    }

    @Override
    public String getTitle() {
        return "Calibration control results: " + runinfo.getLocalModelId();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TimeseriesRetrieverConfig getResultTs() {
        return resultTs;
    }
}
