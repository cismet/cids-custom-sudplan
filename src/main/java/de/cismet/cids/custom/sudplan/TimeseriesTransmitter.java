/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.commons.CismetExecutors;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimeseriesTransmitter {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesTransmitter.class);

    private static final TimeseriesTransmitter INSTANCE = new TimeseriesTransmitter();

    //~ Instance fields --------------------------------------------------------

    private final transient ExecutorService executor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeseriesRetriever object.
     */
    private TimeseriesTransmitter() {
        executor = CismetExecutors.newCachedThreadPool(SudplanConcurrency.createThreadFactory(
                    "timeseries-transmitter")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   targetLocation  config DOCUMENT ME!
     * @param   ts              converter DOCUMENT ME!
     * @param   creds           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Future<Boolean> put(final URL targetLocation, final TimeSeries ts, final Credentials creds) {
        final TransmitterFuture future = new TransmitterFuture(targetLocation, ts);

        return executor.submit(future);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   host  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private HttpClient getClient(final URL host) {
        final HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(host.toExternalForm());
        final HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setMaxConnectionsPerHost(hostConfig, 20);
        connectionManager.setParams(params);
        final HttpClient client = new HttpClient(connectionManager);
        client.setHostConfiguration(hostConfig);

        // TODO: remove PW from source code
        final Credentials creds = new UsernamePasswordCredentials("tsDav", "RHfio2l4wrsklfghj"); // NOI18N
        client.getState().setCredentials(AuthScope.ANY, creds);

        return client;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static TimeseriesTransmitter getInstance() {
        return INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class TransmitterFuture implements Callable<Boolean> {

        //~ Instance fields ----------------------------------------------------

        private final transient URL targetLocation;
        private final transient TimeSeries ts;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RetrieverFuture object.
         *
         * @param  targetLocation  config DOCUMENT ME!
         * @param  ts              converter DOCUMENT ME!
         */
        TransmitterFuture(final URL targetLocation, final TimeSeries ts) {
            this.targetLocation = targetLocation;
            this.ts = ts;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Boolean call() throws Exception {
            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            return this.sendToDav();
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  TimeseriesRetrieverException  DOCUMENT ME!
         */
        private Boolean sendToDav() throws TimeseriesRetrieverException {
            // we don't use the cismet dav client as its "care-less" implementation leads to unpleasant behaviour in
            // case of exception/about etc.
            final HttpClient client = TimeseriesTransmitter.getInstance().getClient(this.targetLocation);

            final PutMethod put = new PutMethod(this.targetLocation.toExternalForm());
            final ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(TimeSeriesSerializer
                            .serializeTimeSeries(this.ts));
            put.setRequestEntity(requestEntity);

            try {
                client.executeMethod(put);
                final int statusCode = put.getStatusCode();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("PUT operation has been finished with status code: " + statusCode);
                }
                return (statusCode == 201) || (statusCode == 202);
            } catch (final Exception ex) {
                LOG.error(put.getStatusText(), ex);
                put.abort();
                throw new TimeseriesRetrieverException(put.getStatusText(), ex);
            } finally {
                put.releaseConnection();
            }
        }
    }
}
