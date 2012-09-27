/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMOptions {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(GeoCPMOptions.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Properties props;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMOptions object.
     */
    private GeoCPMOptions() {
        props = new Properties();
        final InputStream is = getClass().getResourceAsStream("wupp.properties");                               // NOI18N
        if (is == null) {
            LOG.warn("cannot find wupp.properties, wuppertal specific services won't work as expected");        // NOI18N
        } else {
            try {
                props.load(is);
            } catch (final IOException ex) {
                LOG.warn("cannot load wupp.properties, wuppertal specific service won't work as expected", ex); // NOI18N
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GeoCPMOptions getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProperty(final String key) {
        return props.getProperty(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key           DOCUMENT ME!
     * @param   defaultValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProperty(final String key, final String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Properties getProperties() {
        return new Properties(props);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final GeoCPMOptions INSTANCE = new GeoCPMOptions();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
