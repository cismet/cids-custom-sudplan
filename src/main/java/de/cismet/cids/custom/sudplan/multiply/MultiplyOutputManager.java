/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.URI;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class MultiplyOutputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(MultiplyOutputManager.class);

    //~ Instance fields --------------------------------------------------------

    private final transient MultiplyOutputManagerUI ui;

    private transient CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MultiplyOutputManager object.
     */
    public MultiplyOutputManager() {
        ui = new MultiplyOutputManagerUI(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getUI() {
        return ui;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Override
    public URI getLocation() throws IOException {
        try {
            return new URI((String)cidsBean.getProperty("uri")); // NOI18N
        } catch (final Exception e) {
            final String message = "cannot create uri";          // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Override
    public void apply() throws IOException {
        // NOOP
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        ui.init();
    }
}
