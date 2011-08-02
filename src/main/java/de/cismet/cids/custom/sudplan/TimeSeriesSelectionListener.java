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

import edu.umd.cs.piccolo.PLayer;

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

import javax.swing.SwingUtilities;

import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.gui.piccolo.PFeature;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

//TODO nicht removeAll auf FeatureCollection/ Layer der die timeseries features enthaetlt ausführen, sondern nur TimeSeriesFeatures entfernen
/**
 * This class handles the selection of time series and the reflection to the map. It listen to all mouseclicks on
 * ChartPanel but just reacts if the item that was clicked on is a XYItemEntity. The selection is rendered by a
 * SelectionXYLineRenderer. The reflection to map is done by a TimeSeriesFeature.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesSelectionListener implements ChartMouseListener, PlotChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesSelectionListener.class);

    //~ Instance fields --------------------------------------------------------

    private XYPlot plot;
    private HashMap<Integer, PFeature> featureMap;
    private final CismapPlugin cismapPlugin;
    private final MappingComponent mc;
    private final PLayer fc;
//  private final FeatureCollection fc;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesSelectionListener object.
     *
     * @param  p  DOCUMENT ME!
     */
    public TimeSeriesSelectionListener(final XYPlot p) {
        plot = p;
        for (int i = 0; i < plot.getRendererCount(); i++) {
            if (plot.getRenderer(i) instanceof SelectionXYLineRenderer) {
                ((SelectionXYLineRenderer)plot.getRenderer(i)).addTSSelectionListener(this);
            }
        }
        featureMap = new HashMap<Integer, PFeature>();
        cismapPlugin = (CismapPlugin)PluginRegistry.getRegistry().getPlugin("cismap"); // NOI18N
        mc = cismapPlugin.getMappingComponent();
        fc = mc.getTmpFeatureLayer();
//      fc = mc.getFeatureCollection();
//      fc.removeAllFeatures();
        fc.removeAllChildren();
        mc.repaint();
    }

    //~ Methods ----------------------------------------------------------------

    // TODO Show the geom of the selected Time Series on map
    @Override
    public void chartMouseClicked(final ChartMouseEvent event) {
        final ChartEntity entity = event.getEntity();

        if ((entity != null) && (entity instanceof XYItemEntity)) {
            final XYItemEntity xyEntity = (XYItemEntity)entity;

            final TimeSeriesCollection tsc = (TimeSeriesCollection)xyEntity.getDataset();
            final XYItemRenderer renderer = plot.getRendererForDataset(tsc);
            final int index = plot.getIndexOf(renderer);
            final SelectionXYLineRenderer selectionRenderer = (SelectionXYLineRenderer)renderer;

            // remove all feeatures
            mc.getRubberBandLayer().removeAllChildren();
            fc.removeAllChildren();

            if (event.getTrigger().isControlDown()) {
                // For Multi Selection
                selectionRenderer.setSelected(!selectionRenderer.isSelected());
                plot.setRenderer(index, selectionRenderer);
            } else {
                // single selection
                final boolean wasSelected = selectionRenderer.isSelected();
                int multiSelection = 0;
                // first remove the selction of all timeseries and corresponding features from map
                featureMap.clear();
                for (int i = 0; i < plot.getDatasetCount(); i++) {
                    final TimeSeriesCollection tsCollection = (TimeSeriesCollection)plot.getDataset(i);
                    if (tsCollection != null) {
                        final SelectionXYLineRenderer nonSelectionrenderer = (SelectionXYLineRenderer)
                            plot.getRendererForDataset(tsCollection);
                        if (nonSelectionrenderer.isSelected()) {
                            multiSelection++;
                            nonSelectionrenderer.setSelected(false);
                            plot.setRenderer(i, nonSelectionrenderer, true);
                        }
                    }
                }

                // paint the time series that was clicked on selected
                if (!wasSelected || (multiSelection > 1)) {
                    selectionRenderer.setSelected(true);
                    plot.setRenderer(index, selectionRenderer);
                }
            }

            for (final PFeature pf : featureMap.values()) {
                addFeatureToMap(pf);
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
        final XYPlot tmpPlot = (XYPlot)event.getPlot();

        if (type.equals(ChartChangeEventType.DATASET_UPDATED)) {
            for (int i = 0; i < tmpPlot.getDatasetCount(); i++) {
                final XYDataset dataset = tmpPlot.getDataset(i);
                if ((dataset == null) && (tmpPlot.getRenderer(i) != null)) {
                    final PFeature featureToRemove = featureMap.get(new Integer(i));
                    if (featureToRemove != null) {
//                        fc.removeFeature(featureToRemove);
                        fc.removeChild(featureToRemove);
                        featureMap.remove(new Integer(i));
                        return;
                    }
                }
            }
//            for (final PFeature f : feautreMap.values()) {
//                addFeatureToMap(f);
////                fc.addFeature(f);
//            }
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

    /**
     * DOCUMENT ME!
     *
     * @param  pf  DOCUMENT ME!
     */
// private void removeFeatureFromMap() {
// for (int i = 0; i < fc.getChildrenCount(); i++) {
// if (fc.getChild(i) instanceof FeatureAnnotationSymbol) {
// final FeatureAnnotationSymbol symb = (FeatureAnnotationSymbol)fc.getChild(i);
// }
// }
//
////        for(Feature f : fc.getAllFeatures()){
////            if(f instanceof TimeSeriesFeature){
////                fc.removeFeature(f);
////            }
////        }
//    }
    /**
     * DOCUMENT ME!
     *
     * @param  pf  DOCUMENT ME!
     */
    private void addFeatureToMap(final PFeature pf) {
        mc.addStickyNode(pf);
        mc.getTmpFeatureLayer().addChild(pf);
        mc.rescaleStickyNodes();
//        final double s = mc.getCamera().getViewScale();
//        pf.setScale(1 / s);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  renderer  DOCUMENT ME!
     */
    public void selectionChanged(final SelectionXYLineRenderer renderer) {
        final int index = plot.getIndexOf(renderer);

        if (renderer.isSelected()) {
            Geometry geom = null;

            if (plot.getDataset(index) instanceof TimeSeriesDatasetAdapter) {
                final TimeSeriesDatasetAdapter adapterDataset = (TimeSeriesDatasetAdapter)plot.getDataset(index);
                geom = adapterDataset.getGeometry();
            } else {
                LOG.warn("time series chart dataset is no instance of TimeSeriesDatasetAdapter. Selection fails"); // NOI18N
                return;
            }

            final Shape s = renderer.getLegendItem(index, 0).getShape();
            final Paint p = renderer.getLegendItem(index, 0).getFillPaint();
            final Feature f = createFeature(geom, s, p);
            final PFeature pf = new PFeature(f, mc);
            featureMap.put(index, pf);
        } else {
            featureMap.remove(index);
        }
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    mc.getTmpFeatureLayer().removeAllChildren();
                    for (final PFeature pf : featureMap.values()) {
                        addFeatureToMap(pf);
                    }
                }
            });
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

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TimeSeriesFeature object.
         *
         * @param  g  the geometry of the time series
         * @param  s  the time series shape (from legend)
         * @param  p  the time series paint
         */
        public TimeSeriesFeature(final Geometry g, final Shape s, final Paint p) {
            super();
            setGeometry(g);
            BufferedImage bi = null;
            try {
                final InputStream is = getClass().getResourceAsStream("timeseries_feature_icon.png"); // NOI18N
                bi = ImageIO.read(is);
            } catch (final IOException ex) {
                LOG.warn("cannot load timeseries feature icon", ex);                                  // NOI18N
            }
//        final BufferedImage bi = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2 = (Graphics2D)bi.getGraphics();
            // paint background circle

//        g2.fillOval(6, 5, 25, 25);
            g2.setPaint(p);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(12, 18, 27, 18);

            // paint the time series symbol
            final AffineTransform saveXform = g2.getTransform();
            final AffineTransform at = new AffineTransform();
            final AffineTransform scaleTrans = new AffineTransform();
            scaleTrans.scale(1.5, 1.5);
            final Shape scaledShape = scaleTrans.createTransformedShape(s);
            final double shapeXMittelpunkt = (scaledShape.getBounds().getWidth() / 2) - (3 * 1.5);
            final double shapeYMittelpunkt = (scaledShape.getBounds().getHeight() / 2) - (3 * 1.5);
            at.translate(20 - (shapeXMittelpunkt), 18 - (shapeYMittelpunkt));
            g2.transform(at);

            g2.setPaint(p);
            g2.fill(scaledShape);
            g2.transform(saveXform);

            final FeatureAnnotationSymbol symb = new FeatureAnnotationSymbol(bi);
            symb.setSweetSpotX(0.5);
            symb.setSweetSpotY(0.9f);
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
