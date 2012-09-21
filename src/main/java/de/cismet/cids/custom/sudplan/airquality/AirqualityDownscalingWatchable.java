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
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.URL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.cids.custom.sudplan.ProgressEvent;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class AirqualityDownscalingWatchable extends AbstractModelRunWatchable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingWatchable.class);

    public static final String SPS_TASK_STATE_FINISHED = "finished";               // NOI18N
    public static final String SPS_TASK_STATE_RUNNING = "in operation";            // NOI18N
    public static final String SPS_TASK_STATE_UNKNOWN = "unknown";                 // NOI18N
    public static final String SPS_TASK_STATE_NOT_STARTED_YET = "not yet started"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final String runId;
    private final Datapoint datapoint;
    private Collection<String> offerings;

    private transient URL dailyResolution;
    private transient URL originalResolution;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingWatchable object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  runId     DOCUMENT ME!
     * @param  dp        DOCUMENT ME!
     */
    public AirqualityDownscalingWatchable(final CidsBean cidsBean, final String runId, final Datapoint dp) {
        super(cidsBean);
        this.runId = runId;
        this.datapoint = dp;
    }

    /**
     * Creates a new AirqualityDownscalingWatchable object.
     *
     * @param   cidsBean     DOCUMENT ME!
     * @param   dataHandler  DOCUMENT ME!
     * @param   runId        DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public AirqualityDownscalingWatchable(final CidsBean cidsBean, final DataHandler dataHandler, final String runId)
            throws IOException {
        super(cidsBean);

        final Properties filter = new Properties();
        filter.put(PropertyNames.TASK_ID, runId);

        final Set<Datapoint> datapoints = dataHandler.getDatapoints(filter, DataHandler.Access.READ);

        if (datapoints.isEmpty()) {
            throw new IOException("No task available with run id '" + runId + "'. dataHandler: '" + dataHandler + "'."); // NOI18N
        } else if (datapoints.size() > 1) {
            throw new IOException("Too many tasks available with runid '" + runId + "'. dataHandler: '" + dataHandler
                        + "'.");                                                                                         // NOI18N
        } else {
            datapoint = datapoints.iterator().next();
        }

        this.runId = runId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ProgressEvent requestStatus() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Requesting status for airquality downscaling run '" + runId + "'."); // NOI18N
        }

        final TimeSeries statusTs = datapoint.getTimeSeries(new TimeInterval(
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
                final StringBuilder errorMessage = new StringBuilder();

                for (final String error : errors) {
                    if ((error != null) && !error.isEmpty()) {
                        errorMessage.append(error);
                        errorMessage.append(' ');
                    }
                }

                if (errorMessage.length() > 0) {
                    LOG.error("Errors found for airquality downscaling run '" + runId + "': "
                                + errorMessage.toString()); // NOI18N

                    return new ProgressEvent(
                            this,
                            ProgressEvent.State.BROKEN,
                            "Errors found for airquality downscaling run '"
                                    + runId // NOI18N
                                    + "': "
                                    + errorMessage.toString()); // NOI18N
                }
            }
        }

        // find the current status
        for (final TimeStamp stamp : statusTs.getTimeStamps().descendingSet()) {
            final Object value = statusTs.getValue(stamp, PropertyNames.TaskStatus);
            if ((value != null) && (value instanceof String)) {
                final String state = (String)value;

                if (LOG.isDebugEnabled()) {
                    LOG.debug("State of airquality downscaling task '" + runId + "': " + state + "."); // NOI18N
                }

                // FIXME: mapping not started yet to running
                if (SPS_TASK_STATE_NOT_STARTED_YET.equalsIgnoreCase(state)
                            || SPS_TASK_STATE_RUNNING.equalsIgnoreCase(state)) {
                    return new ProgressEvent(
                            this,
                            ProgressEvent.State.PROGRESSING,
                            -1,
                            -1,
                            "State of airquality downscaling task '"
                                    + runId // NOI18N
                                    + "': "
                                    + state // NOI18N
                                    + "."); // NOI18N
                } else if (SPS_TASK_STATE_FINISHED.equalsIgnoreCase(state)) {
                    return new ProgressEvent(this, ProgressEvent.State.FINISHED);
                } else if (SPS_TASK_STATE_UNKNOWN.equalsIgnoreCase(state)) {
                    return new ProgressEvent(this, ProgressEvent.State.UNKNOWN);
                } else {
                    throw new IOException("Undefined status for airquality downscaling run '" + runId + "': " + state
                                + "."); // NOI18N
                }
            }
        }

        throw new IOException("Can't extract status from timeseries."); // NOI18N
    }

    @Override
    public void startDownload() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Download results for airquality downscaling run '" + runId + "'."); // NOI18N
        }

        setStatus(State.RUNNING);

        final TimeSeries statusTimeseries = datapoint.getTimeSeries(new TimeInterval(
                    TimeInterval.Openness.OPEN,
                    TimeStamp.NEGATIVE_INFINITY,
                    TimeStamp.POSITIVE_INFINITY,
                    TimeInterval.Openness.OPEN));

        // FIXME: bull**** impl

        // find the run results status
        for (final TimeStamp stamp : statusTimeseries.getTimeStamps().descendingSet()) {
            final Object value = statusTimeseries.getValue(stamp, PropertyNames.TaskResults);
            if ((value != null) && (value instanceof String[])) {
                final String[] results = (String[])value;

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Results of task '" + runId + "': " + Arrays.toString(results) + "."); // NOI18N
                }

                offerings = Arrays.asList(results);

                setStatus(State.COMPLETED);

                return;
            }
        }

        throw new IllegalStateException("No results found for task with id '" + runId + "'."); // NOI18N
    }

    @Override
    public String getTitle() {
        return "Results of airquality downscaling run '" + runId + "'."; // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public URL getDailyResolutionResult() {
        return dailyResolution;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public URL getOrigResolutionResult() {
        return originalResolution;
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
    public Collection<String> getOfferings() {
        return offerings;
    }
}
