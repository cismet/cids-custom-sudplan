/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class DistributionComponent extends JComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(DistributionComponent.class);

    private static final Color[] COLORS = new Color[] {
            Color.green,
            Color.blue,
            Color.black,
            Color.yellow,
            Color.cyan,
            Color.gray
        };
    private static final BasicStroke STROKE = new BasicStroke(1F, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
    private static final RenderingHints RENDERING_HINTS = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    static {
        RENDERING_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    //~ Instance fields --------------------------------------------------------

    private double min;
    private double max;
    private final Map<Distribution, Color> distributions = new HashMap<Distribution, Color>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DistributionComponent object.
     */
    public DistributionComponent() {
        // JUST A HACK FOR MATISSE
    }

    /**
     * Creates a new DistributionComponent object.
     *
     * @param  min  DOCUMENT ME!
     * @param  max  DOCUMENT ME!
     */
    public DistributionComponent(final double min, final double max) {
        this.min = min;
        this.max = max;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMax() {
        return max;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  max  DOCUMENT ME!
     */
    public void setMax(final double max) {
        this.max = max;

        revalidate();
        repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMin() {
        return min;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  min  DOCUMENT ME!
     */
    public void setMin(final double min) {
        this.min = min;

        revalidate();
        repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  distribution  DOCUMENT ME!
     */
    public void addDistribution(final Distribution distribution) {
        addDistribution(distribution, COLORS[distributions.entrySet().size() % COLORS.length]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  distribution  DOCUMENT ME!
     * @param  color         DOCUMENT ME!
     */
    public void addDistribution(final Distribution distribution, final Color color) {
        if ((distribution == null) || Double.isNaN(distribution.getMin()) || Double.isNaN(distribution.getMax())
                    || Double.isNaN(distribution.getMean())) {
            return;
        }

        distributions.put(distribution, color);

        revalidate();
        repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  distribution  DOCUMENT ME!
     */
    public void removeDistribution(final Distribution distribution) {
        distributions.remove(distribution);

        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth() - getInsets().left - getInsets().right, 10 * distributions.size());
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(getWidth() - getInsets().left - getInsets().right, 5 * distributions.size());
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (distributions.isEmpty()) {
            return;
        }

        final int componentX = getInsets().left;
        final int componentY = getInsets().top;
        final int componentWidth = getWidth() - getInsets().left - getInsets().right;
        final int componentHeight = getHeight() - getInsets().top - getInsets().bottom;
        final int bandHeight = componentHeight / distributions.size();

        final Graphics2D g2 = (Graphics2D)g.create();

        g2.setStroke(STROKE);
        g2.setRenderingHints(RENDERING_HINTS);

//        final Composite originalComposite = g2.getComposite();
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));

        int bandPosition = 0;
        for (final Map.Entry<Distribution, Color> entry : distributions.entrySet()) {
            g2.setPaint(entry.getValue());
            g2.fillPolygon(createPolygon(
                    entry.getKey(),
                    componentX,
                    componentWidth,
                    bandPosition,
                    bandHeight));
            bandPosition += bandHeight;
        }

//        g2.setComposite(originalComposite);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   distribution    DOCUMENT ME!
     * @param   componentX      DOCUMENT ME!
     * @param   componentWidth  DOCUMENT ME!
     * @param   bandPosition    DOCUMENT ME!
     * @param   bandHeight      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Polygon createPolygon(final Distribution distribution,
            final int componentX,
            final int componentWidth,
            final int bandPosition,
            final int bandHeight) {
        final Polygon result = new Polygon();

        final int bandCenter = bandPosition + (bandHeight / 2);

        result.addPoint(determinePosition(distribution.getMin(), componentX, componentWidth), bandCenter);
        result.addPoint(determinePosition(distribution.getMean(), componentX, componentWidth), bandPosition);
        result.addPoint(determinePosition(distribution.getMax(), componentX, componentWidth), bandCenter);
        result.addPoint(determinePosition(distribution.getMean(), componentX, componentWidth),
            bandPosition
                    + bandHeight);
        result.addPoint(determinePosition(distribution.getMin(), componentX, componentWidth), bandCenter);

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   value           DOCUMENT ME!
     * @param   componentX      DOCUMENT ME!
     * @param   componentWidth  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int determinePosition(final double value, final int componentX, final int componentWidth) {
        if (value <= min) {
            return componentX;
        } else if (value >= max) {
            return componentWidth;
        }

        final double range = Math.abs(max - min);
        final double slice = Math.abs(value) / range;
        final double result = componentWidth * slice;
        final int resultingPosition = (int)Math.round(result);

        return Math.max(Math.min(resultingPosition, componentWidth), componentX);
    }
}
