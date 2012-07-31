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
public class GridComparisonAction extends AbstractMapPopupAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridComparisonAction.class);

    private static final String CUSTOMSTYLE = " <StyledLayerDescriptor version=\"1.0.0\""
                + "     xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\""
                + "     xmlns=\"http://www.opengis.net/sld\""
                + "     xmlns:ogc=\"http://www.opengis.net/ogc\""
                + "     xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                + "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "   <NamedLayer>"
                + "     <Name>" + WMSServiceLayer.TEMPLATETOKEN_CUSTOMSTYLE_LAYERNAME + "</Name>"
                + "     <UserStyle>"
                + "       <Title>Airquality</Title>"
                + "       <FeatureTypeStyle>"
                + "         <Rule>"
                + "           <PolygonSymbolizer>"
                + "             <Geometry>"
                + "               <ogc:PropertyName>geometry</ogc:PropertyName>"
                + "             </Geometry>"
                + " "
                + "             <Fill>"
                + "               <CssParameter name=\"fill\">"
                + "                 <ogc:Function name=\"Interpolate\">"
                + "                   <!-- Property to transform -->"
                + "                   <ogc:PropertyName>value</ogc:PropertyName>"
                + "                   "
                + "                   <ogc:PropertyName>minvalue</ogc:PropertyName>"
                + "                   <ogc:Literal>#0000ff</ogc:Literal>"
                + "                   "
                + "                   <ogc:PropertyName>maxvalue</ogc:PropertyName>"
                + "                   <ogc:Literal>#ffff00</ogc:Literal>"
                + " "
                + "                   <!-- Interpolation method -->"
                + "                   <ogc:Literal>color</ogc:Literal>"
                + " "
                + "                   <!-- Interpolation mode - defaults to linear -->"
                + "                   <!--<ogc:Literal>color</ogc:Literal>"
                + "                   <ogc:Literal>cubic</ogc:Literal>"
                + "                   <ogc:Literal>cosine</ogc:Literal>-->"
                + "                 </ogc:Function>"
                + "               </CssParameter>"
                + "             </Fill>"
                + "           </PolygonSymbolizer>"
                + "         </Rule>"
                + "       </FeatureTypeStyle>"
                + "     </UserStyle>"
                + "   </NamedLayer>"
                + " </StyledLayerDescriptor>";

    //~ Instance fields --------------------------------------------------------

    private SlidableWMSServiceLayerGroup sourceForComparison;
    private final JMenu submenu;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridComparisonAction object.
     */
    public GridComparisonAction() {
        submenu = new JMenu(NbBundle.getMessage(GridComparisonAction.class, "GridComparisonAction.name.noSelection"));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Action performed on layer '"
                        + ((sourceForComparison != null) ? sourceForComparison.getName() : "null") + "'.");
        }
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public boolean isActive(final boolean featuresSubjacent) {
        sourceForComparison = null;

        final Point positionOfMouse = getPoint();

        if (positionOfMouse == null) {
            LOG.warn("Didn't get the click coordinates.");
            return false;
        }

        final List<SlidableWMSServiceLayerGroup> layers = GridComparisonLayerProvider.instance().getLayers(false);

        if (layers.size() < 2) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Comparing less than two layers wouldn't work, wouldn't it?");
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
                    if (sourceForComparison == null) {
                        sourceForComparison = layer;
                    } else {
                        return true;
                    }
                }
            } else {
                LOG.info("Layer '" + layer.getName() + "' hasn't a bounding box.");
            }
        }

        return false;
    }

    @Override
    public JMenu getSubmenu() {
        submenu.removeAll();

        if (sourceForComparison == null) {
            submenu.setText(NbBundle.getMessage(GridComparisonAction.class, "GridComparisonAction.name.noSelection"));
            submenu.setEnabled(false);

            return submenu;
        }

        submenu.setText(NbBundle.getMessage(
                GridComparisonAction.class,
                "GridComparisonAction.name",
                GridComparisonLayerProvider.generateMenuRepresentation(sourceForComparison)));
        submenu.setEnabled(true);

//        submenu.add(new GridComparisonTargetAction(sourceForComparison));
//        submenu.addSeparator();

        final List<SlidableWMSServiceLayerGroup> layers = GridComparisonLayerProvider.instance().getLayers(true);
        for (final SlidableWMSServiceLayerGroup layer : layers) {
            if (!layer.equals(sourceForComparison) && layer.isVisible()) {
                submenu.add(new GridComparisonTargetAction(layer));
            }
        }

        return submenu;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class GridComparisonTargetAction extends AbstractAction {

        //~ Instance fields ----------------------------------------------------

        private final SlidableWMSServiceLayerGroup targetForComparison;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GridComparisonTargetAction object.
         *
         * @param  targetForComparison  DOCUMENT ME!
         */
        public GridComparisonTargetAction(final SlidableWMSServiceLayerGroup targetForComparison) {
            this.targetForComparison = targetForComparison;

            if (targetForComparison.equals(sourceForComparison)) {
                putValue(
                    Action.NAME,
                    NbBundle.getMessage(
                        GridComparisonAction.class,
                        "GridComparisonAction.GridComparisonTargetAction.name.itself"));
            } else {
                putValue(Action.NAME, GridComparisonLayerProvider.generateMenuRepresentation(targetForComparison));
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action performed on layers '"
                            + ((sourceForComparison != null) ? sourceForComparison.getName() : "null") + "' & "
                            + ((targetForComparison != null) ? targetForComparison.getName() : "null") + "'.");
            }

            GridComparisonWidgetProvider.getWidget().setOperands(sourceForComparison, targetForComparison);
        }
    }
}
