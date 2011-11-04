/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.sudplan.sosclient.SOSClientHandler;
import at.ac.ait.enviro.sudplan.spsclient.SPSClientHandler;
import at.ac.ait.enviro.tsapi.handler.DataHandler;

import org.apache.log4j.Logger;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
// TODO: timed cleanup to release memory
public final class DataHandlerCache {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(DataHandlerCache.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Map<String, DataHandler> sosCache;
    private final transient Map<String, DataHandler> spsCache;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DataHandlerCache object.
     */
    private DataHandlerCache() {
        this.sosCache = new HashMap<String, DataHandler>();
        this.spsCache = new HashMap<String, DataHandler>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   lookup  DOCUMENT ME!
     * @param   url     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  DataHandlerCacheException  DOCUMENT ME!
     */
    public DataHandler getSOSDataHandler(final String lookup, final URL url) throws DataHandlerCacheException {
        final String key = lookup + "-" + url.toString(); // NOI18N

        if (!sosCache.containsKey(key)) {
            final SOSClientHandler handler = new SOSClientHandler();

            handler.setId(lookup);

            try {
                handler.getConnector().connect(url.toExternalForm());
                handler.open();
            } catch (final Exception e) {
                final String message = "cannot initialise handler"; // NOI18N
                LOG.error(message, e);
                throw new DataHandlerCacheException(message, e);
            }

            sosCache.put(key, handler);
        }

        return sosCache.get(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   lookup  DOCUMENT ME!
     * @param   url     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  DataHandlerCacheException  DOCUMENT ME!
     */
    public DataHandler getSPSDataHandler(final String lookup, final URL url) throws DataHandlerCacheException {
        final String key = lookup + "-" + url.toString(); // NOI18N

        if (!spsCache.containsKey(key)) {
            final SPSClientHandler handler = new SPSClientHandler();

            handler.setId(lookup);

            try {
                handler.getConnector().connect(url.toExternalForm());
                handler.open();
            } catch (final Exception e) {
                final String message = "cannot initialise handler"; // NOI18N
                LOG.error(message, e);
                throw new DataHandlerCacheException(message, e);
            }

            spsCache.put(key, handler);
        }

        return spsCache.get(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static DataHandlerCache getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final DataHandlerCache INSTANCE = new DataHandlerCache();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
