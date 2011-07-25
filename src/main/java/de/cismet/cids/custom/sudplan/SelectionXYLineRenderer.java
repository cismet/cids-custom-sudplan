/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class SelectionXYLineRenderer extends XYLineAndShapeRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final BasicStroke SELECTION_STROKE = new BasicStroke(2.5f);
    private static final Paint SELECTION_PAINT = Color.YELLOW;
    private static final Font SELECTION_FONT = new Font("SansSerif", Font.BOLD, 10);

    //~ Instance fields --------------------------------------------------------

    private boolean selected;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SelectionXYLineRenderer object.
     */
    public SelectionXYLineRenderer() {
        super(true, true);
        this.selected = false;
    }

    /**
     * Creates a new SelectionXYLineRenderer object.
     *
     * @param  lines     DOCUMENT ME!
     * @param  shapes    DOCUMENT ME!
     * @param  selected  DOCUMENT ME!
     */
    public SelectionXYLineRenderer(final boolean lines, final boolean shapes, final boolean selected) {
        super(lines, shapes);
        this.selected = selected;
        
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selected  DOCUMENT ME!
     */
    public void setSelected(final boolean selected) {
        this.selected = selected;
        setPaintSelected(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  index  DOCUMENT ME!
     */
    private void setPaintSelected(final int index) {
        this.setDrawOutlines(isSelected());
        this.setUseOutlinePaint(isSelected());
//        selectionRenderer.setSeriesPaint(index, Color.BLACK);
//        selectionRenderer.setSeriesOutlinePaint(index, Color.green);
//        selectionRenderer.setSeriesOutlineStroke(index, new BasicStroke(4.0f), true);
//        XYTextAnnotation annotation = new XYTextAnnotation("selecetd", tsc.getXValue(xyEntity.getSeriesIndex(), xyEntity.getItem()), tsc.getYValue(xyEntity.getSeriesIndex(), xyEntity.getItem()));
//        annotation.setFont(new Font("SansSerif", Font.PLAIN, 9));
//        selectionRenderer.addAnnotation(annotation);
        if (isSelected()) {
            this.setSeriesOutlinePaint(index, SELECTION_PAINT, false);
            this.setSeriesStroke(index, SELECTION_STROKE, false);
            this.setSeriesVisibleInLegend(index, selected, false);
//            this.setLegendTextPaint(0, Color.BLUE);
            this.setLegendTextFont(index, SELECTION_FONT);
        } else {
            this.setSeriesOutlinePaint(index, DEFAULT_OUTLINE_PAINT, false);
            this.setSeriesStroke(index, DEFAULT_STROKE, false);
            this.setLegendTextFont(index, DEFAULT_VALUE_LABEL_FONT);
        }
    }

    @Override
    protected void drawPrimaryLine(final XYItemRendererState state,
            final Graphics2D g2,
            final XYPlot plot,
            final XYDataset dataset,
            final int pass,
            final int series,
            final int item,
            final ValueAxis domainAxis,
            final ValueAxis rangeAxis,
            final Rectangle2D dataArea) {
        super.drawPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
    }
}
