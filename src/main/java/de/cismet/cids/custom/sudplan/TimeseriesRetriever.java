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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.commons.CismetExecutors;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

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

    private final transient Map<String, HttpClient> clientCache;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeseriesRetriever object.
     */
    private TimeseriesRetriever() {
        executor = CismetExecutors.newCachedThreadPool(SudplanConcurrency.createThreadFactory("timeseries-retriever")); // NOI18N
        clientCache = new HashMap<String, HttpClient>();
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
        return retrieve(config, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   config     DOCUMENT ME!
     * @param   converter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  TimeseriesRetrieverException  DOCUMENT ME!
     */
    public Future<TimeSeries> retrieve(final TimeseriesRetrieverConfig config, final TimeseriesConverter converter)
            throws TimeseriesRetrieverException {
        validateConfig(config);

        final RetrieverFuture future = new RetrieverFuture(config, converter);

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
            throw new TimeseriesRetrieverException("config must not be null");   // NOI18N
        } else if (config.getLocation() == null) {
            throw new TimeseriesRetrieverException("location must not be null"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   host  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private HttpClient getClient(final String host) {
        if (!clientCache.containsKey(host)) {
            final HostConfiguration hostConfig = new HostConfiguration();
            hostConfig.setHost(host);
            final HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setMaxConnectionsPerHost(hostConfig, 20);
            connectionManager.setParams(params);
            final HttpClient client = new HttpClient(connectionManager);
            client.setHostConfiguration(hostConfig);

            // TODO: remove PW from source code
            final Credentials creds = new UsernamePasswordCredentials("tsDav", "RHfio2l4wrsklfghj"); // NOI18N
            client.getState().setCredentials(AuthScope.ANY, creds);

            clientCache.put(host, client);
        }

        return clientCache.get(host);
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
        private final transient TimeseriesConverter converter;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RetrieverFuture object.
         *
         * @param  config     DOCUMENT ME!
         * @param  converter  DOCUMENT ME!
         */
        RetrieverFuture(final TimeseriesRetrieverConfig config, final TimeseriesConverter converter) {
            this.config = config;
            this.converter = converter;
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

            if (TimeseriesRetrieverConfig.PROTOCOL_TSTB.equals(config.getProtocol())) {
                return fromTSTB();
            } else if (TimeseriesRetrieverConfig.PROTOCOL_DAV.equals(config.getProtocol())) {
                return fromDav();
            } else {
                throw new TimeseriesRetrieverException("unknown config: " + config); // NOI18N
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  TimeseriesRetrieverException  DOCUMENT ME!
         */
        private TimeSeries fromTSTB() throws TimeseriesRetrieverException {
            final DataHandler handler;
            try {
                handler = DataHandlerCache.getInstance()
                            .getSOSDataHandler(config.getHandlerLookup(), config.getLocation());
            } catch (DataHandlerCacheException ex) {
                final String message = "cannot create data handler"; // NOI18N
                LOG.error(message, ex);
                throw new TimeseriesRetrieverException(message, ex);
            }

            if (handler == null) {
                throw new TimeseriesRetrieverException("cannot lookup handler: " + config.getHandlerLookup()); // NOI18N
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

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  TimeseriesRetrieverException  DOCUMENT ME!
         */
        private TimeSeries fromDav() throws TimeseriesRetrieverException {
            if (converter == null) {
                throw new TimeseriesRetrieverException("cannot fetch timeseries from dav without converter"); // NOI18N
            }

            // we don't use the cismet dav client as its "care-less" implementation leads to unpleasant behaviour in
            // case of exception/about etc.
            final HttpClient client = TimeseriesRetriever.getInstance().getClient(config.getLocation().getHost());
            final GetMethod get = new GetMethod(config.getLocation().toExternalForm());
            BufferedInputStream bis = null;
            try {
                client.executeMethod(get);

                bis = new BufferedInputStream(get.getResponseBodyAsStream());

                if (Thread.currentThread().isInterrupted()) {
                    throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
                }

                return converter.convertForward(bis);
            } catch (final Exception ex) {
                final String message = "cannot fetch timeseries from dav: " + config; // NOI18N
                LOG.error(message, ex);

                get.abort();

                throw new TimeseriesRetrieverException(message, ex);
            } finally {
                get.releaseConnection();
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (final IOException ex) {
                        LOG.warn("cannot close inputstream", ex); // NOI18N
                    }
                }
            }
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

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
