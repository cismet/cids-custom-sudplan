/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.cismet.cismap.commons.interaction.ActiveLayerListener;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.interaction.events.ActiveLayerEvent;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;
import de.cismet.cismap.commons.raster.wms.WMSServiceLayer;
import de.cismet.cismap.commons.wms.capabilities.Layer;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class GridComparisonLayerProvider implements ActiveLayerListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridComparisonLayerProvider.class);

    private static GridComparisonLayerProvider INSTANCE;

    //~ Instance fields --------------------------------------------------------

    private final List<SlidableWMSServiceLayerGroup> layers;
    private final SlidableWMSServiceLayerGroupComparator comparator;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridComparisonLayerProvider object.
     */
    public GridComparisonLayerProvider() {
        layers = new LinkedList<SlidableWMSServiceLayerGroup>();
        comparator = new SlidableWMSServiceLayerGroupComparator();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GridComparisonLayerProvider instance() {
        if (INSTANCE == null) {
            INSTANCE = new GridComparisonLayerProvider();

            // TODO: Would be really nice to do the following. But since the mapping model is neither stuffed with
            // layers from configuration nor even instantiated at this point, it would be useless. Additionally it's
            // impossible to load the recently used layers from the config file. There's something wrong with the
            // capabilities SlidableWMSLayerGroup gets in its constructor. Thus a call to
            // SlidableWMSLayerGroup.getLayerInformation() returns null which means it can't be recognized as AQDS layer
            /* try {
             *     INSTANCE.reloadLayers(); } catch (final Exception ex) {    LOG.info("Couldn't inspect layers already
             * displayed in cismap.", ex); }
             */

            CismapBroker.getInstance().addActiveLayerListener(INSTANCE);
        }

        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   layer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isAQDSLayer(final Object layer) {
        if (!(layer instanceof SlidableWMSServiceLayerGroup)) {
            return false;
        }

        final Layer layerInformation = ((SlidableWMSServiceLayerGroup)layer).getLayerInformation();

        if (layerInformation == null) {
            // Maybe it's while starting the Navigator, so let's try another way to inspect the children
            final List<WMSServiceLayer> children = ((SlidableWMSServiceLayerGroup)layer).getLayers();

            if ((children == null) || (children.isEmpty())) {
                return false;
            }

            final String nameOfChildren = children.get(0).getName();

            return (nameOfChildren != null) && nameOfChildren.startsWith("aqds_view_");
        }

        final Layer[] children = layerInformation.getChildren();

        if ((children == null) || (children.length <= 0)) {
            return false;
        }

        final String nameOfChildren = children[0].getName();

        return (nameOfChildren != null) && nameOfChildren.contains(":aqds_view_");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   layer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateMenuRepresentation(final SlidableWMSServiceLayerGroup layer) {
        final StringBuilder result = new StringBuilder();
        final String[] pathOfLayer = layer.getPath().split("/");

        if ((pathOfLayer == null) || (pathOfLayer.length != 3)) {
            LOG.warn("Something is wrong with layer '" + layer + "', since its path is in invalid (path: '"
                        + layer.getPath() + "').");

            for (final String partOfPath : pathOfLayer) {
                result.append(partOfPath);
            }
        } else {
            result.append(pathOfLayer[2].substring(0, pathOfLayer[2].lastIndexOf('[')));
            result.append(" (");
            result.append(pathOfLayer[0]);
            result.append(", ");
            result.append(pathOfLayer[1]);
            result.append(")");
        }

        return result.toString();
    }

    /**
     * DOCUMENT ME!
     */
    private void reloadLayers() {
        if ((CismapBroker.getInstance() == null)
                    || (CismapBroker.getInstance().getMappingComponent() == null)
                    || (CismapBroker.getInstance().getMappingComponent().getMappingModel() == null)
                    || (CismapBroker.getInstance().getMappingComponent().getMappingModel().getRasterServices() == null)) {
            return;
        }

        layers.clear();

        final TreeMap rasterServices = CismapBroker.getInstance()
                    .getMappingComponent()
                    .getMappingModel()
                    .getRasterServices();
        for (final Object rasterService : rasterServices.entrySet()) {
            if (rasterService instanceof Map.Entry) {
                final Map.Entry entry = (Map.Entry)rasterService;

                if (isAQDSLayer(entry.getValue())) {
                    layers.add((SlidableWMSServiceLayerGroup)entry.getValue());
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sorted  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<SlidableWMSServiceLayerGroup> getLayers(final boolean sorted) {
        final List<SlidableWMSServiceLayerGroup> result = new LinkedList<SlidableWMSServiceLayerGroup>(layers);

        if (sorted) {
            Collections.sort(result, comparator);
        }

        return result;
    }

    @Override
    public void layerAdded(final ActiveLayerEvent e) {
        final Object layer = e.getLayer();

        if (isAQDSLayer(layer) && !layers.contains((SlidableWMSServiceLayerGroup)layer)) {
            layers.add((SlidableWMSServiceLayerGroup)layer);

            GridComparisonWidgetProvider.getWidget().reloadLayers();
        }
    }

    @Override
    public void layerRemoved(final ActiveLayerEvent e) {
        final Object layer = e.getLayer();

        if (isAQDSLayer(layer)) {
            layers.remove((SlidableWMSServiceLayerGroup)layer);

            GridComparisonWidgetProvider.getWidget().reloadLayers();
        }
    }

    @Override
    public void layerPositionChanged(final ActiveLayerEvent e) {
        final Object layer = e.getLayer();

        if (isAQDSLayer(layer)) {
            reloadLayers();
        }
    }

    @Override
    public void layerVisibilityChanged(final ActiveLayerEvent e) {
        final Object layer = e.getLayer();

        if (isAQDSLayer(layer)) {
            GridComparisonWidgetProvider.getWidget().reloadLayers();
        }
    }

    @Override
    public void layerAvailabilityChanged(final ActiveLayerEvent e) {
        final Object layer = e.getLayer();

        if (isAQDSLayer(layer)) {
            GridComparisonWidgetProvider.getWidget().reloadLayers();
        }
    }

    @Override
    public void layerInformationStatusChanged(final ActiveLayerEvent e) {
    }

    @Override
    public void layerSelectionChanged(final ActiveLayerEvent e) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class SlidableWMSServiceLayerGroupComparator implements Comparator<SlidableWMSServiceLayerGroup> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final SlidableWMSServiceLayerGroup o1, final SlidableWMSServiceLayerGroup o2) {
            if ((o1 == null) && (o2 == null)) {
                return 0;
            } else if ((o1 == null) && (o2 != null)) {
                return -1;
            } else if ((o1 != null) && (o2 == null)) {
                return 1;
            }

            return generateMenuRepresentation(o1).compareTo(generateMenuRepresentation(o2));
        }
    }
}
