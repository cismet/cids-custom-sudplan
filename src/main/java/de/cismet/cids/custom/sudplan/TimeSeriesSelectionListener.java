/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.plugin.PluginRegistry;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;

import javax.imageio.ImageIO;

import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

//TODO nicht removeAll auf FeatureCollection/ Layer der die timeseries features enthaetlt ausführen, sondern nur TimeSeriesFeatures entfernen
/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesSelectionListener implements ChartMouseListener, PlotChangeListener {

    //~ Static fields/initializers ---------------------------------------------
    private static final transient Logger LOG = Logger.getLogger(TimeSeriesSelectionListener.class);
    //~ Instance fields --------------------------------------------------------
    private XYPlot plot;
    private HashMap<Integer, Feature> feautreMap;

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new TimeSeriesSelectionListener object.
     *
     * @param  p  DOCUMENT ME!
     */
    public TimeSeriesSelectionListener(final XYPlot p) {
        plot = p;
        feautreMap = new HashMap<Integer, Feature>();
        final CismapPlugin cismapPlugin = (CismapPlugin) PluginRegistry.getRegistry().getPlugin("cismap");// NOI18N
        final MappingComponent mc = cismapPlugin.getMappingComponent();
//            final PLayer fc = mc.getTmpFeatureLayer();
        final FeatureCollection fc = mc.getFeatureCollection();
        fc.removeAllFeatures();
        
    }

    //~ Methods ----------------------------------------------------------------
    // TODO Show the geom of the selected Time Series on map
    @Override
    public void chartMouseClicked(final ChartMouseEvent event) {
        final ChartEntity entity = event.getEntity();
//            System.out.println("ChartMouse Clicked: " + event.toString());

        if ((entity != null) && (entity instanceof XYItemEntity)) {
            final XYItemEntity xyEntity = (XYItemEntity) entity;
            Geometry geom = null;

            if (xyEntity.getDataset() instanceof TimeSeriesDatasetAdapter) {
                final TimeSeriesDatasetAdapter adapterDataset = (TimeSeriesDatasetAdapter) xyEntity.getDataset();
                geom = adapterDataset.getGeometry();
            } else {
                LOG.warn("time series chart dataset is no instance of TimeSeriesDatasetAdapter. Selection fails");// NOI18N
                return;
            }
            final TimeSeriesCollection tsc = (TimeSeriesCollection) xyEntity.getDataset();
            final XYItemRenderer renderer = plot.getRendererForDataset(tsc);
            final int index = plot.getIndexOf(renderer);
            final SelectionXYLineRenderer selectionRenderer = (SelectionXYLineRenderer) renderer;
            final CismapPlugin cismapPlugin = (CismapPlugin) PluginRegistry.getRegistry().getPlugin("cismap");// NOI18N
            final MappingComponent mc = cismapPlugin.getMappingComponent();
//            final PLayer fc = mc.getTmpFeatureLayer();
            final FeatureCollection fc = mc.getFeatureCollection();

            // remove the feeature that was created by clicking on map
            mc.getRubberBandLayer().removeAllChildren();

            if (event.getTrigger().isControlDown()) {
                // For Multi Selection
                // remove all, then add for each in hashmap...

//                fc.removeAllChildren();
                fc.removeAllFeatures();
                selectionRenderer.setSelected(!selectionRenderer.isSelected());
                plot.setRenderer(index, selectionRenderer);
                if (selectionRenderer.isSelected()) {
                    // the time series is now selected so shwo it on map
                    final Shape s = selectionRenderer.getLegendItem(index, 0).getShape();
                    final Paint p = selectionRenderer.getLegendItem(index, 0).getFillPaint();
                    final Feature f = createFeature(geom, s, p);
                    feautreMap.put(index, f);
                } else {
                    // remove the corresponding image from map
                    final Feature featureToRemove = feautreMap.get(new Integer(index));
                    if (featureToRemove != null) {
                        feautreMap.remove(index);
                    }
                }
                for (final Feature f : feautreMap.values()) {
//                    fc.addChild(((DefaultStyledFeature)f).getPointAnnotationSymbol());
                    fc.addFeature(f);
                }
                return;
            } else {
                // single selection
                final boolean wasSelected = selectionRenderer.isSelected();
                int multiSelection = 0;
                // first remove the selction of all timeseries and corresponding features from map
// fc.removeAllChildren();
                fc.removeAllFeatures();
                feautreMap.clear();
                for (int i = 0; i < plot.getDatasetCount(); i++) {
                    final TimeSeriesCollection tsCollection = (TimeSeriesCollection) plot.getDataset(i);
                    if (tsCollection != null) {
                        final SelectionXYLineRenderer nonSelectionrenderer = (SelectionXYLineRenderer) plot.getRendererForDataset(tsCollection);
                        if (nonSelectionrenderer.isSelected()) {
                            multiSelection++;
                        }
                        nonSelectionrenderer.setSelected(false);
                        plot.setRenderer(i, nonSelectionrenderer, true);
                    }
                }

                // paint the time series that was clicked on selected
                if (!wasSelected || (multiSelection > 1)) {
                    selectionRenderer.setSelected(true);
                    plot.setRenderer(index, selectionRenderer);
                    final Shape s = selectionRenderer.getLegendItem(index, 0).getShape();
                    final Paint p = selectionRenderer.getLegendItem(index, 0).getFillPaint();
                    final Feature f = createFeature(geom, s, p);
//                    fc.addChild(((DefaultStyledFeature)f).getPointAnnotationSymbol());
                    fc.addFeature(f);
                    feautreMap.put(index, f);
                }
            }
        }
    }

    @Override
    public void chartMouseMoved(final ChartMouseEvent event) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void plotChanged(final PlotChangeEvent event) {
        final ChartChangeEventType type = event.getType();
        final XYPlot tmpPlot = (XYPlot) event.getPlot();
        final CismapPlugin cismapPlugin = (CismapPlugin) PluginRegistry.getRegistry().getPlugin("cismap");// NOI18N
        final MappingComponent mc = cismapPlugin.getMappingComponent();
        final FeatureCollection fc = mc.getFeatureCollection();

        if (type.equals(ChartChangeEventType.DATASET_UPDATED)) {
            for (int i = 0; i < tmpPlot.getDatasetCount(); i++) {
                final XYDataset dataset = tmpPlot.getDataset(i);
//            tmpPlot.getRenderer(i);
                if ((dataset == null) && (tmpPlot.getRenderer(i) != null)) {
//                mc.getRubberBandLayer().removeAllChildren();
                    final Feature featureToRemove = feautreMap.get(new Integer(i));
                    if (featureToRemove != null) {
                        fc.removeFeature(featureToRemove);
                        feautreMap.remove(new Integer(i));
                        return;
                    }
                }
            }
            for (final Feature f : feautreMap.values()) {
                fc.addFeature(f);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g      DOCUMENT ME!
     * @param   shape  DOCUMENT ME!
     * @param   p      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Feature createFeature(final Geometry g, final Shape shape, final Paint p) {
//        final PureNewFeature feature = new PureNewFeature(g);
        final TimeSeriesFeature feature = new TimeSeriesFeature(g, shape, p);
//        feature.setName("timeSeries Object");
        return feature;
    }

    //~ Inner Classes ----------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @author   dmeiers
     * @version  $Revision$, $Date$
     */
    protected final class TimeSeriesFeature extends DefaultStyledFeature {

        //~ Instance fields ----------------------------------------------------
        private final transient Logger LOG = Logger.getLogger(TimeSeriesFeature.class);
        private FeatureAnnotationSymbol featureAnnotationSymbol;
        private Shape shape;

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new TimeSeriesFeature object.
         *
         * @param  g  DOCUMENT ME!
         * @param  s  DOCUMENT ME!
         * @param  p  DOCUMENT ME!
         */
        public TimeSeriesFeature(final Geometry g, final Shape s, final Paint p) {
            super();
            shape = s;
            setGeometry(g);
            BufferedImage bi = null;
            try {
                final InputStream is = getClass().getResourceAsStream("timeseries_feature_icon.png"); // NOI18N
                bi = ImageIO.read(is);
            } catch (final IOException ex) {
                LOG.warn("cannot load timeseries feature icon", ex);                                  // NOI18N
            }
//        final BufferedImage bi = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2 = (Graphics2D) bi.getGraphics();
            // paint background circle

//        g2.fillOval(6, 5, 25, 25);
            g2.setPaint(p);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(11, 17, 27, 17);

            // paint the time series symbol
            final AffineTransform saveXform = g2.getTransform();
            final AffineTransform at = new AffineTransform();

            final double shapeXMittelpunkt = (s.getBounds().getWidth() / 2) - 3;
            final double shapeYMittelpunkt = (s.getBounds().getHeight() / 2) - 3;
            at.translate(19 - shapeXMittelpunkt, 17 - shapeYMittelpunkt);
            g2.transform(at);
            g2.scale(2, 2);

            g2.setPaint(p);
            g2.fill(s);
            g2.transform(saveXform);

            final FeatureAnnotationSymbol symb = new FeatureAnnotationSymbol(bi);
            symb.setSweetSpotX(0.5);
            symb.setSweetSpotY(0.9);
            featureAnnotationSymbol = symb;
        }

        //~ Methods ------------------------------------------------------------
        /**
         * Creates a new TimeSeriesFeature object.
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public FeatureAnnotationSymbol getPointAnnotationSymbol() {
            return featureAnnotationSymbol;
        }
    }
}
