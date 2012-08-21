/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * TimeSeriesRemoteHelper provides frequently needed WebDAV connection parameters as well as methods for creating
 * corresponding HttpClient instances.
 *
 * @author   benjamin.friedrich@cismet.de
 * @version  1.0, 04.01.2012
 */
public final class TimeSeriesRemoteHelper {

    //~ Static fields/initializers ---------------------------------------------

    public static final String DAV_HOST = "http://sudplan.cismet.de/tsDav";
    public static final Credentials DAV_CREDS = new UsernamePasswordCredentials("tsDav", "RHfio2l4wrsklfghj");

    public static final String NETCDF_HOST = "http://fswwwww1.tu-graz.ac.at:8080/datasets/";
    public static final Credentials NETCDF_CREDS = new UsernamePasswordCredentials("sudplan", "sudplan@tugraz");

    private static final Logger LOG = Logger.getLogger(TimeSeriesRemoteHelper.class);

    private static final Map<String, HttpClient> CLIENT_CACHE = new HashMap<String, HttpClient>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Avoids instanciation of TimeSeriesRemoteHelper.
     */
    private TimeSeriesRemoteHelper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   host   DOCUMENT ME!
     * @param   creds  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static synchronized HttpClient createHttpClient(final String host, final Credentials creds) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering createHttpClient(String, Credentials) with host=" + host + "; creds=" + creds);
        }

        HttpClient client = CLIENT_CACHE.get(host);
        if (client == null) {
            final HostConfiguration hostConfig = new HostConfiguration();
            hostConfig.setHost(host);
            final HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setMaxConnectionsPerHost(hostConfig, 20);
            connectionManager.setParams(params);

            client = new HttpClient(connectionManager);
            client.setHostConfiguration(hostConfig);
            client.getState().setCredentials(AuthScope.ANY, creds);

            CLIENT_CACHE.put(host, client);
            if (LOG.isDebugEnabled()) {
                LOG.debug("New HttpClient instance created");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving createHttpClient(String, Credentials) with client=" + client);
        }
        return client;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HttpClient createHttpClient() {
        return createHttpClient(DAV_HOST, DAV_CREDS);
    }
}
