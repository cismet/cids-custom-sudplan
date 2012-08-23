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

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import de.cismet.tools.PasswordEncrypter;

/**
 * TimeSeriesRemoteHelper provides frequently needed WebDAV connection parameters as well as methods for creating
 * corresponding HttpClient instances.
 *
 * @author   benjamin.friedrich@cismet.de
 * @version  1.0, 04.01.2012
 */
public final class TimeSeriesRemoteHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesRemoteHelper.class);

    public static final String DAV_HOST;
    public static final Credentials DAV_CREDS;

    public static final String NETCDF_HOST;
    public static final Credentials NETCDF_CREDS;

    private static final Map<String, HttpClient> CLIENT_CACHE = new HashMap<String, HttpClient>();

    private static final String PROP_TS_STORE_DAV_HOST = "timeseries.store.dav.host";     // NOI18N
    private static final String PROP_TS_STORE_DAV_USER = "timeseries.store.dav.user";     // NOI18N
    private static final String PROP_TS_STORE_DAV_PASS = "timeseries.store.dav.password"; // NOI18N

    private static final String PROP_TS_STORE_NETCDF_TUG_HOST = "timeseries.store.netcdf.tugraz.host";     // NOI18N
    private static final String PROP_TS_STORE_NETCDF_TUG_USER = "timeseries.store.netcdf.tugraz.user";     // NOI18N
    private static final String PROP_TS_STORE_NETCDF_TUG_PASS = "timeseries.store.netcdf.tugraz.password"; // NOI18N

    static {
        final Properties p = new Properties();
        final InputStream is = TimeSeriesRemoteHelper.class.getResourceAsStream("timeseriesRemoteHelper.properties"); // NOI18N

        try {
            p.load(is);
        } catch (final Exception e) {
            final String message = "cannot load properties, timeseries remote helper not operational"; // NOI18N
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        }

        DAV_HOST = p.getProperty(PROP_TS_STORE_DAV_HOST, "");                // NOI18N
        if (DAV_HOST.isEmpty()) {
            throw new MissingResourceException(
                "dav host url not present",                                  // NOI18N
                "timeseriesRemoteHelper.properties",                         // NOI18N
                PROP_TS_STORE_DAV_HOST);
        }
        final String davUser = p.getProperty(PROP_TS_STORE_DAV_USER, "");    // NOI18N
        if (davUser.isEmpty()) {
            throw new MissingResourceException(
                "dav username not present",                                  // NOI18N
                "timeseriesRemoteHelper.properties",                         // NOI18N
                PROP_TS_STORE_DAV_USER);
        }
        final String davPassword = String.valueOf(PasswordEncrypter.decrypt(
                    p.getProperty(PROP_TS_STORE_DAV_PASS, "").toCharArray(), // NOI18N
                    false));
        if (davPassword.isEmpty()) {
            throw new MissingResourceException(
                "dav password not present",                                  // NOI18N
                "timeseriesRemoteHelper.properties",                         // NOI18N
                PROP_TS_STORE_DAV_PASS);
        }
        DAV_CREDS = new UsernamePasswordCredentials(davUser, davPassword);

        NETCDF_HOST = p.getProperty(PROP_TS_STORE_NETCDF_TUG_HOST, "");             // NOI18N
        if (NETCDF_HOST.isEmpty()) {
            throw new MissingResourceException(
                "netcdf host url not present",                                      // NOI18N
                "timeseriesRemoteHelper.properties",                                // NOI18N
                PROP_TS_STORE_NETCDF_TUG_HOST);
        }
        final String netcdfUser = p.getProperty(PROP_TS_STORE_NETCDF_TUG_USER, ""); // NOI18N
        if (netcdfUser.isEmpty()) {
            throw new MissingResourceException(
                "netcdf username not present",                                      // NOI18N
                "timeseriesRemoteHelper.properties",                                // NOI18N
                PROP_TS_STORE_NETCDF_TUG_USER);
        }
        final String netcdfPassword = String.valueOf(PasswordEncrypter.decrypt(
                    p.getProperty(PROP_TS_STORE_NETCDF_TUG_PASS, "").toCharArray(), // NOI18N
                    false));
        if (netcdfPassword.isEmpty()) {
            throw new MissingResourceException(
                "netcdf password not present",                                      // NOI18N
                "timeseriesRemoteHelper.properties",                                // NOI18N
                PROP_TS_STORE_NETCDF_TUG_PASS);
        }
        NETCDF_CREDS = new UsernamePasswordCredentials(netcdfUser, netcdfPassword);
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Avoids instantiation of TimeSeriesRemoteHelper.
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
