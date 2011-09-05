/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;

/**
 * A SelectionXYLineRenderer is ever related to exact one <code>TimeSeriesDatasetAdapter</code>. It controlls the visual
 * appearance of the dataset in the chart.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class SelectionXYLineRenderer extends XYLineAndShapeRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final BasicStroke SELECTION_STROKE = new BasicStroke(2f);
    private static final BasicStroke SELECTION_OUTLINE_STROKE = new BasicStroke(5f);
    private static final Paint SELECTION_OUTLINE_PAINT = Color.BLUE;

    //~ Instance fields --------------------------------------------------------

    private ArrayList<SelectionChartMouseListener> listener = new ArrayList<SelectionChartMouseListener>();
    private boolean selected;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SelectionXYLineRenderer object.
     */
    public SelectionXYLineRenderer() {
        this(true, false, false);
    }

    /**
     * Creates a new SelectionXYLineRenderer object.
     *
     * @param  lines     a flag controlling if the line of the series is drawn
     * @param  shapes    a flag controlling if the shapes of the series are drawn
     * @param  selected  a flag controlling the selection state
     */
    public SelectionXYLineRenderer(final boolean lines, final boolean shapes, final boolean selected) {
        super(lines, shapes);
        this.selected = selected;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * gets the selection state of this renderer.
     *
     * @return  the true if this renderer is selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * set the selcetion state of the renderer.
     *
     * @param  selected  a flag
     */
    public void setSelected(final boolean selected) {
        this.selected = selected;
        setPaintSelected(0);
    }

    /**
     * sets the paramets how the related dataset shall be drawn.
     *
     * @param  index  the index of the series - ever 0
     */
    private void setPaintSelected(final int index) {
        this.setDrawOutlines(isSelected());
        this.setUseOutlinePaint(isSelected());
        if (isSelected()) {
            this.setSeriesStroke(index, SELECTION_STROKE, false);
        } else {
            this.setSeriesStroke(index, DEFAULT_STROKE, false);
        }
    }

    /**
     * adds a SelectionChartMouseListener that is notified whenever the closest dataitem in fact of a mouseclick was
     * found.
     *
     * @param  l  a <code>SelectionChartMouseListener</code>
     */
    public void addSelectionChartMouseListener(final SelectionChartMouseListener l) {
        listener.add(l);
    }

    /**
     * During the rendering of the dataset, JFreeChart calculates the closest dataitem for the crosshair point If this
     * dataitem was found we notify all registers <code>SeelctionChartMouseListeners</code> to check if the last
     * mouseClick lies on the line beetween the new crosshairpoint and the one before or after that.
     *
     * @param  g2              DOCUMENT ME!
     * @param  plot            DOCUMENT ME!
     * @param  dataset         DOCUMENT ME!
     * @param  pass            DOCUMENT ME!
     * @param  series          DOCUMENT ME!
     * @param  item            DOCUMENT ME!
     * @param  domainAxis      DOCUMENT ME!
     * @param  dataArea        DOCUMENT ME!
     * @param  rangeAxis       DOCUMENT ME!
     * @param  crosshairState  DOCUMENT ME!
     * @param  entities        DOCUMENT ME!
     */
    @Override
    protected void drawSecondaryPass(final Graphics2D g2,
            final XYPlot plot,
            final XYDataset dataset,
            final int pass,
            final int series,
            final int item,
            final ValueAxis domainAxis,
            final Rectangle2D dataArea,
            final ValueAxis rangeAxis,
            final CrosshairState crosshairState,
            final EntityCollection entities) {
        super.drawSecondaryPass(
            g2,
            plot,
            dataset,
            pass,
            series,
            item,
            domainAxis,
            dataArea,
            rangeAxis,
            crosshairState,
            entities);
        final double itemX = dataset.getXValue(series, item);
        final double itemY = dataset.getYValue(series, item);
        final double newCrosshairX = crosshairState.getCrosshairX();
        final double newCrosshhairY = crosshairState.getCrosshairY();
        /*
         * the crosshair coordinates are actualised to the item coordiantes as long as a closer data item to the last
         * mousecklick was found. If the closest datatitem was found the coordinates of the next dataitem and the
         * crosshair distinguish and we have found the closest item. and can check if the click was on line. This check
         * is eventually done for multiple (if the closes data item is not the last one) but the
         * SelectionChartMouseListener takes care of it
         */
        if ((itemX != newCrosshairX) && (itemY != newCrosshhairY)) {
            for (final SelectionChartMouseListener l : listener) {
                l.checkIfCklickWasOnDataLine(
                    dataset,
                    // the item before is the closest one
                    (item == 0) ? 0 : (item - 1));
            }
        }
    }

    /**
     * draws the dataset after the selection state selected or not.
     *
     * @param  g2      DOCUMENT ME!
     * @param  pass    DOCUMENT ME!
     * @param  series  DOCUMENT ME!
     * @param  item    DOCUMENT ME!
     * @param  shape   DOCUMENT ME!
     */
    @Override
    protected void drawFirstPassShape(final Graphics2D g2,
            final int pass,
            final int series,
            final int item,
            final Shape shape) {
        super.drawFirstPassShape(g2, pass, series, item, shape);
        if (isSelected()) {
            // background line
            final AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            final Composite originComposite = g2.getComposite();
            g2.setComposite(ac);
            g2.setStroke(SELECTION_OUTLINE_STROKE);
            g2.setPaint(SELECTION_OUTLINE_PAINT);
            g2.draw(shape);
            g2.setComposite(originComposite);
        }
    }
}
