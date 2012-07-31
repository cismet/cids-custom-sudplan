/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.text.DecimalFormat;

import java.util.List;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyle.Entry;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class LayerStyleComponent extends JComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LayerStyleComponent.class);
    private static final transient double EPS = 1.0e-8;

    //~ Instance fields --------------------------------------------------------

    private LayerStyle layerStyle;
    private final DecimalFormat format = new DecimalFormat("#0.00");

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LayerStyleComponent object.
     *
     * <p>JUST A HACK FOR MATISSE TO DISPLAY COMPONENT IN PANELS!</p>
     */
    public LayerStyleComponent() {
        // JUST A HACK FOR MATISSE TO DISPLAY COMPONENT IN PANELS
    }

    /**
     * Creates a new LayerStyleComponent object.
     *
     * @param  layerStyle  DOCUMENT ME!
     */
    public LayerStyleComponent(final LayerStyle layerStyle) {
        this.layerStyle = layerStyle;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public LayerStyle getLayerStyle() {
        return layerStyle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  layerStyle  DOCUMENT ME!
     */
    public void setLayerStyle(final LayerStyle layerStyle) {
        this.layerStyle = layerStyle;

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (layerStyle == null) {
            return;
        }

        final List<Entry> colorMap = layerStyle.getColorMap();

        if ((colorMap == null) || (colorMap.size() > getWidth())) {
            return;
        }

        final int componentX = getInsets().left;
        final int componentWidth = getWidth() - getInsets().left - getInsets().right;
        final int componentHeight = getHeight() - getInsets().top - getInsets().bottom;
        final int remainingPixels = componentWidth % (colorMap.size() - 1);

        final FontMetrics fontMetrics = g.getFontMetrics(getFont());
        final int textHeight = (int)Math.ceil(fontMetrics.getStringBounds("0", g).getHeight());

        int currentSegment = -1;
        int positionOfSegmentChange = 0;
        Entry currentEntry = null;
        Entry nextEntry = colorMap.get(0);
        double currentValueDifference = 0D;
        // 0 % whatever is 0 ...
        int segmentWidth = 1;
        for (int x = componentX; x < componentWidth; x++) {
            if (x == positionOfSegmentChange) {
                currentSegment++;
                currentEntry = nextEntry;

                segmentWidth = componentWidth / (colorMap.size() - 1);
                if (currentSegment < remainingPixels) {
                    segmentWidth++;
                }
                positionOfSegmentChange += segmentWidth;

                g.setColor(currentEntry.getColor());
                g.drawLine(x, 0, x, componentHeight - textHeight);

                g.setColor(getForeground());
                g.drawLine(x, componentHeight - textHeight - 3, x, componentHeight - textHeight);

                final String currentValueString = format.format(currentEntry.getValue());
                final int textWidth = (int)Math.ceil(fontMetrics.getStringBounds(currentValueString, g).getWidth());

                if (currentSegment == 0) {
                    g.drawString(currentValueString, x, componentHeight);
                } else {
                    g.drawString(currentValueString, x - (textWidth / 2), componentHeight);
                }

                if ((currentSegment + 1) < colorMap.size()) {
                    nextEntry = colorMap.get(currentSegment + 1);
                    currentValueDifference = nextEntry.getValue() - currentEntry.getValue();
                } else {
                    nextEntry = null;
                    currentValueDifference = 0D;
                }
            } else {
                if (nextEntry == null) {
                    LOG.warn("Should never happen. Next entry is null.");
                    g.setColor(getBackground());
                } else {
                    final double translatedX = currentEntry.getValue()
                                + ((x
                                        - (positionOfSegmentChange - segmentWidth))
                                    * (currentValueDifference / segmentWidth));

                    g.setColor(linearInterpolate(translatedX, currentEntry, nextEntry));
                }

                g.drawLine(x, 0, x, componentHeight - textHeight);
            }
        }

        g.setColor(nextEntry.getColor());
        g.drawLine(componentWidth - 1, 0, componentWidth - 1, componentHeight - textHeight);

        g.setColor(getForeground());
        g.drawLine(componentWidth - 1,
            componentHeight
                    - textHeight
                    - 3,
            componentWidth
                    - 1,
            componentHeight
                    - textHeight);
        final String nextValueString = format.format(nextEntry.getValue());
        final int textWidth = (int)Math.ceil(fontMetrics.getStringBounds(
                    nextValueString,
                    g).getWidth());
        g.drawString(nextValueString, componentWidth - textWidth, componentHeight);
    }

    @Override
    public Dimension getMinimumSize() {
        if (layerStyle == null) {
            return super.getMinimumSize();
        }

        final FontMetrics fontMetrics = getFontMetrics(getFont());
        final int charWidth = fontMetrics.charWidth('0');
        final int charHeight = fontMetrics.getHeight();

        final List<Entry> colorMap = layerStyle.getColorMap();

        if ((colorMap != null) || !colorMap.isEmpty()) {
            int charCount = 0;

            for (final Entry entry : colorMap) {
                charCount += Double.toString(entry.getValue()).length();
            }

            return new Dimension(charCount * charWidth, charHeight);
        }

        return super.getMinimumSize();
    }

    @Override
    public Dimension getPreferredSize() {
        if (layerStyle == null) {
            return super.getPreferredSize();
        }

        final FontMetrics fontMetrics = getFontMetrics(getFont());
        final int charWidth = fontMetrics.charWidth('0');
        final int charHeight = fontMetrics.getHeight();

        final List<Entry> colorMap = layerStyle.getColorMap();

        if ((colorMap != null) || !colorMap.isEmpty()) {
            int charCount = 0;

            for (final Entry entry : colorMap) {
                charCount += Double.toString(entry.getValue()).length();
            }

            return new Dimension((charCount * charWidth) + (colorMap.size() * 2), charHeight + 2);
        }

        return super.getPreferredSize();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   value         DOCUMENT ME!
     * @param   currentEntry  DOCUMENT ME!
     * @param   nextEntry     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Color linearInterpolate(final Double value, final Entry currentEntry, final Entry nextEntry) {
        final Double currentValue = currentEntry.getValue();
        final Double nextValue = nextEntry.getValue();
        final Color currentColor = currentEntry.getColor();
        final Color nextColor = nextEntry.getColor();

        final int r = (int)clamp(Math.round(
                    doLinear(value, currentValue, nextValue, currentColor.getRed(), nextColor.getRed())),
                0,
                255);
        final int g = (int)clamp(Math.round(
                    doLinear(value, currentValue, nextValue, currentColor.getGreen(), nextColor.getGreen())),
                0,
                255);
        final int b = (int)clamp(Math.round(
                    doLinear(value, currentValue, nextValue, currentColor.getBlue(), nextColor.getBlue())),
                0,
                255);

        return new Color(r, g, b);
    }

    /**
     * Performs linear interpolation.
     *
     * @param   x   value for which a y ordinate is being interpolated
     * @param   x0  lower interpolation point x ordinate
     * @param   x1  upper interpolation point x ordinate
     * @param   y0  lower interpolation point y ordinate
     * @param   y1  upper interpolation point y ordinate
     *
     * @return  interpolated y value
     */
    private double doLinear(final double x, final double x0, final double x1, final double y0, final double y1) {
        final double xspan = getSpan(x0, x1);
        final double t = (x - x0) / xspan;

        return y0 + (t * (y1 - y0));
    }

    /**
     * Helper method for the linear, cosine and cubic interpolation methods. Checks that the span of the interval from
     * x0 to x1 is > 0.
     *
     * @param   x0  lower interval point
     * @param   x1  upper interval point
     *
     * @return  interval span
     *
     * @throws  IllegalArgumentException  if the span is less than a small tolerance value
     */
    private double getSpan(final double x0, final double x1) {
        final double result = x1 - x0;
        // the span should be > 0
        if (result < EPS) {
            throw new IllegalArgumentException(
                "Interpolation points must be in ascending order of data (lookup) values with no ties");
        }

        return result;
    }

    /**
     * Clamp a value to lie between the given min and max values (inclusive).
     *
     * @param   x    input value
     * @param   min  minimum
     * @param   max  maximum
     *
     * @return  the clamped value
     */
    private double clamp(final double x, final double min, final double max) {
        return Math.max(min, Math.min(max, x));
    }
}
