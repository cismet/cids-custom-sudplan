/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.event.ActionEvent;

import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.AbstractMapPopupAction;
import de.cismet.cismap.commons.gui.MapPopupAction;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;
import de.cismet.cismap.commons.raster.wms.WMSServiceLayer;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = MapPopupAction.class)
public class ChangeLayerStyleAction extends AbstractMapPopupAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ChangeLayerStyleAction.class);

    //~ Instance fields --------------------------------------------------------

    private final List<SlidableWMSServiceLayerGroup> layers;
    private final JMenu submenu;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ChangeLayerStyleAction object.
     */
    public ChangeLayerStyleAction() {
        layers = new LinkedList<SlidableWMSServiceLayerGroup>();

        submenu = new JMenu(NbBundle.getMessage(
                    ChangeLayerStyleAction.class,
                    "ChangeLayerStyleAction.name.noSelection"));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Action performed on layers '" + layers + "'.");
        }
    }

    @Override
    public int getPosition() {
        return 1;
    }

    @Override
    public boolean isActive(final boolean featuresSubjacent) {
        layers.clear();

        final Point positionOfMouse = getPoint();

        if (positionOfMouse == null) {
            LOG.warn("Didn't get the click coordinates.");
            return false;
        }

        final List<SlidableWMSServiceLayerGroup> layers = GridComparisonLayerProvider.instance().getLayers(false);

        if (layers.size() < 1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("We need a layer to change the style for.");
            }

            return false;
        }

        for (final SlidableWMSServiceLayerGroup layer : layers) {
            if (layer.isVisible() && (layer.getBoundingBox() != null)) {
                final XBoundingBox boundingBox = layer.getBoundingBox();
                final String currentSrs = CrsTransformer.createCrsFromSrid(positionOfMouse.getSRID());
                final Geometry boundingBoxGeometry;

                if (!boundingBox.getSrs().equals(currentSrs)) {
                    boundingBoxGeometry = CrsTransformer.transformToGivenCrs(boundingBox.getGeometry(), currentSrs);
                } else {
                    boundingBoxGeometry = boundingBox.getGeometry();
                }

                if (boundingBoxGeometry.contains(positionOfMouse)) {
                    this.layers.add(layer);
                }
            } else {
                LOG.info("Layer '" + layer.getName() + "' hasn't a bounding box.");
            }
        }

        return !this.layers.isEmpty();
    }

    @Override
    public JMenu getSubmenu() {
        submenu.removeAll();

        if (layers == null) {
            submenu.setEnabled(false);

            return submenu;
        }

        submenu.setEnabled(true);

        for (final SlidableWMSServiceLayerGroup layer : layers) {
            final JMenu subsubmenu = new JMenu(layer.getName());

            subsubmenu.add(new ReallyChangeLayerStyleAction(layer, null));
            subsubmenu.addSeparator();

            final List<LayerStyle> layerStyles = LayerStyles.instance().getLayerStyles();
            for (final LayerStyle layerStyle : layerStyles) {
                subsubmenu.add(new ReallyChangeLayerStyleAction(layer, layerStyle));
            }

            submenu.add(subsubmenu);
        }

        return submenu;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ReallyChangeLayerStyleAction extends AbstractAction {

        //~ Instance fields ----------------------------------------------------

        private final SlidableWMSServiceLayerGroup layer;
        private final LayerStyle layerStyle;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GridComparisonTargetAction object.
         *
         * @param  layer       targetForComparison DOCUMENT ME!
         * @param  layerStyle  DOCUMENT ME!
         */
        public ReallyChangeLayerStyleAction(final SlidableWMSServiceLayerGroup layer, final LayerStyle layerStyle) {
            this.layer = layer;
            this.layerStyle = layerStyle;

            if (layerStyle == null) {
                putValue(
                    Action.NAME,
                    NbBundle.getMessage(
                        ChangeLayerStyleAction.class,
                        "ChangeLayerStyleAction.ReallyChangeLayerStyleAction.name.reset"));
            } else {
                putValue(Action.NAME, this.layerStyle.getName());
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action performed on layer '"
                            + ((layer != null) ? layer.getName() : "null") + "' and layer style "
                            + ((layerStyle != null) ? layerStyle.getName() : "null") + "'.");
            }

            layer.setCustomSLD(layerStyle.getSLDForLayer());
            layer.retrieve(true);
        }
    }
}
