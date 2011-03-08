/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d.dfki;

import com.dfki.av.sudplan.conf.InitialisationException;
import com.dfki.av.sudplan.control.ComponentController;
import com.dfki.av.sudplan.ui.SimpleLayerPanel;
import com.dfki.av.sudplan.ui.vis.VisualisationComponentPanel;

import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Cismap3DDFKI {

    //~ Static fields/initializers ---------------------------------------------

    private static Cismap3DDFKI instance;

    private static final transient Logger LOG = Logger.getLogger(Cismap3DDFKI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient VisualisationComponentPanel visPanel;
    private final transient ComponentController controller;
    private final transient SimpleLayerPanel layerPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Cismap3DDFKI object.
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private Cismap3DDFKI() {
        try {
            visPanel = new VisualisationComponentPanel();
            controller = new ComponentController(visPanel);
            layerPanel = new SimpleLayerPanel(controller.getLayerManager());
        } catch (final InitialisationException ex) {
            final String message = "error initialising Cismap 3D components"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * not using static initialiser class to have optimised exception handling because constructor throws uncaught ex.
     *
     * @return  DOCUMENT ME!
     */
    public static synchronized Cismap3DDFKI getInstance() {
        if (instance == null) {
            instance = new Cismap3DDFKI();
        }

        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VisualisationComponentPanel getVisComponent() {
        return visPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ComponentController getController() {
        return controller;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleLayerPanel getLayerPanel() {
        return layerPanel;
    }
}
