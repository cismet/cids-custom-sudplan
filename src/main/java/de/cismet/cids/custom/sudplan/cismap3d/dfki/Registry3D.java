/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d.dfki;

import com.dfki.av.sudplan.vis.VisualizationPanel;

import java.awt.Dimension;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
final class Registry3D {

    //~ Instance fields --------------------------------------------------------

    private final transient VisualizationPanel visPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Registry3D object.
     */
    private Registry3D() {
        visPanel = new VisualizationPanel(new Dimension(400, 400));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VisualizationPanel getVisPanel() {
        return visPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Registry3D getInstance() {
        return LazyInitializer.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitializer {

        //~ Static fields/initializers -----------------------------------------

        private static final Registry3D INSTANCE = new Registry3D();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitializer object.
         */
        private LazyInitializer() {
        }
    }
}
