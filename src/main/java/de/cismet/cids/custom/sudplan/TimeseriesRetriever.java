/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.sudplan.sosclient.SOSClientHandler;
import at.ac.ait.enviro.sudplan.util.EnvelopeQueryParameter;
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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    private final transient Map<String, DataHandler> dhCache;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeseriesRetriever object.
     */
    private TimeseriesRetriever() {
        executor = Executors.newCachedThreadPool();
        clientCache = new HashMap<String, HttpClient>();
        dhCache = new HashMap<String, DataHandler>();
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
            throw new TimeseriesRetrieverException("config must not be null");       // NOI18N
        } else if (config.getSosLocation() == null) {
            throw new TimeseriesRetrieverException("sos location must not be null"); // NOI18N
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class RetrieverFuture implements Callable<TimeSeries> {

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
            if (LOG.isDebugEnabled()) {
                LOG.debug("starting timeseries retrieval for config: " + config);
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            // FIXME: for demo
            final String key = config.getHandlerLookup() + "-" + config.getSosLocation().toString(); // NOI18N
            if (!dhCache.containsKey(key)) {
                final SOSClientHandler handler = new SOSClientHandler();

                handler.setId(config.getHandlerLookup());
                try {
                    handler.getConnector().connect(config.getSosLocation().toExternalForm());
                    handler.open();
                } catch (final Exception e) {
                    final String message = "cannot initialise handler"; // NOI18N
                    LOG.error(message, e);
                    throw new TimeseriesRetrieverException(message, e);
                }

                dhCache.put(key, handler);
            }

            final DataHandler handler = dhCache.get(key);

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

            final EnvelopeQueryParameter point = new EnvelopeQueryParameter();
            point.setEnvelope(config.getGeometry().getEnvelopeInternal());

            return datapoint.getTimeSeries(interval, point);
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
            final HttpClient client = TimeseriesRetriever.getInstance().getClient(config.getSosLocation().getHost());
            final GetMethod get = new GetMethod(config.getSosLocation().toExternalForm());
            BufferedInputStream bis = null;
            try {
                client.executeMethod(get);

                bis = new BufferedInputStream(get.getResponseBodyAsStream());

                if (Thread.currentThread().isInterrupted()) {
                    throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
                }
                final TimeSeries ts = converter.convertForward(bis);

                if (Thread.currentThread().isInterrupted()) {
                    throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
                }
                return ts;
            } catch (final Exception ex) {
                final String message = "cannot fetch timeseries from dav: " + config;    // NOI18N
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
