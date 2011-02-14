/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimeseriesRetriever {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesRetriever.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ExecutorService executor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeseriesRetriever object.
     */
    private TimeseriesRetriever() {
        executor = Executors.newCachedThreadPool();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   config  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  TimeseriesRetrieverException  DOCUMENT ME!
     */
    public Future<TimeSeries> retrieve(final TimeseriesRetrieverConfig config) throws TimeseriesRetrieverException {
        validateConfig(config);

        final RetrieverFuture future = new RetrieverFuture(config);

        return executor.submit(future);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   config  DOCUMENT ME!
     *
     * @throws  TimeseriesRetrieverException  DOCUMENT ME!
     */
    private void validateConfig(final TimeseriesRetrieverConfig config) throws TimeseriesRetrieverException {
        if (config == null) {
            throw new TimeseriesRetrieverException("config must not be null");                  // NOI18N
        } else if ((config.getHandlerLookup() == null) || config.getHandlerLookup().isEmpty()) {
            throw new TimeseriesRetrieverException("handler lookup must not be null or empty"); // NOI18N
        } else if (config.getSosLocation() == null) {
            throw new TimeseriesRetrieverException("sos location must not be null");            // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static TimeseriesRetriever getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class RetrieverFuture implements Callable<TimeSeries> {

        //~ Instance fields ----------------------------------------------------

        private final transient TimeseriesRetrieverConfig config;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RetrieverFuture object.
         *
         * @param  config  DOCUMENT ME!
         */
        RetrieverFuture(final TimeseriesRetrieverConfig config) {
            this.config = config;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public TimeSeries call() throws Exception {
            if (LOG.isDebugEnabled()) {
                LOG.debug("starting timeseries retrieval for config: " + config);
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            // for demo
            final DataHandler handler;
            if (config.getOffering().contains("ownscale") && (Demo.getInstance().getDSSOSDH() != null)) {
                handler = Demo.getInstance().getDSSOSDH();
            } else {
                handler = Demo.getInstance().getSOSDH(); // TODO: <- for demo
                                                         // DataHandlerFactory.Lookup.lookup(config.getHandlerLookup());
            }

            if (handler == null) {
                throw new TimeseriesRetrieverException("cannot lookup handler: " + config.getHandlerLookup());
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            handler.setId(config.getHandlerLookup());
            try {
                final BeanInfo info = Introspector.getBeanInfo(handler.getClass(), Introspector.USE_ALL_BEANINFO);
                for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
                    if (pd.getName().equals("endpoint")) { // NOI18N
                        pd.getWriteMethod().invoke(handler, config.getSosLocation());
                    }
                }

                handler.open();
            } catch (final Exception e) {
                final String message = "cannot initialise handler"; // NOI18N
                LOG.error(message, e);
                throw new TimeseriesRetrieverException(message, e);
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            final Set<Datapoint> dps = handler.getDatapoints(config.getFilterProperties(), DataHandler.Access.READ);
            final Datapoint datapoint;
            if (dps.size() < 1) {
                throw new TimeseriesRetrieverException("no datapoint for configuration: " + config);        // NOI18N
            } else if (dps.size() > 1) {
                throw new TimeseriesRetrieverException("too many datapoints for configuration: " + config); // NOI18N
            } else {
                datapoint = dps.iterator().next();
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            final TimeInterval interval;
            if (config.getInterval() == null) {
                interval = new TimeInterval(
                        TimeInterval.Openness.OPEN,
                        TimeStamp.NEGATIVE_INFINITY,
                        TimeStamp.POSITIVE_INFINITY,
                        TimeInterval.Openness.OPEN);
            } else {
                interval = config.getInterval();
            }

            // TODO: spatial query

            return datapoint.getTimeSeries(interval);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final transient TimeseriesRetriever INSTANCE = new TimeseriesRetriever();
    }
}
