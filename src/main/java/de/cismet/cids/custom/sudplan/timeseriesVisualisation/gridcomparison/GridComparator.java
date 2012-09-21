/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyle.Entry;

import de.cismet.cismap.commons.gui.piccolo.XPImage;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class GridComparator {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridComparator.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Operation {

        //~ Enum constants -----------------------------------------------------

        SUBTRACTION, AVERAGE;

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            String result = this.name().toLowerCase();

            try {
                result = NbBundle.getMessage(
                        GridComparator.class,
                        "GridComparator.Operation."
                                + this.name().toLowerCase());
            } catch (final MissingResourceException ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No UI string found for Operation '" + this.name() + "' (key: '"
                                + "GridComparator.Operation." + this.name().toLowerCase() + "').",
                        ex);
                }
            }

            return result;
        }
    }

    //~ Instance fields --------------------------------------------------------

    private final SlidableWMSServiceLayerGroup firstOperand;
    private final SlidableWMSServiceLayerGroup secondOperand;
    private final List<RenderedOp> firstOperands;
    private final List<RenderedOp> secondOperands;
    private final Map<Operation, List<Image>> results;
    private final LayerStyle layerStyle;
    private final float cropX;
    private final float cropY;
    private final float cropWidth;
    private final float cropHeight;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridComparator object.
     *
     * @param  firstOperand   DOCUMENT ME!
     * @param  secondOperand  DOCUMENT ME!
     * @param  layerStyle     DOCUMENT ME!
     * @param  cropX          DOCUMENT ME!
     * @param  cropY          DOCUMENT ME!
     * @param  cropWidth      DOCUMENT ME!
     * @param  cropHeight     DOCUMENT ME!
     */
    public GridComparator(final SlidableWMSServiceLayerGroup firstOperand,
            final SlidableWMSServiceLayerGroup secondOperand,
            final LayerStyle layerStyle,
            final float cropX,
            final float cropY,
            final float cropWidth,
            final float cropHeight) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.layerStyle = layerStyle;
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;

        this.firstOperands = new ArrayList<RenderedOp>();
        this.secondOperands = new ArrayList<RenderedOp>();

        results = new EnumMap<Operation, List<Image>>(Operation.class);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   operation  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Image> compare(final Operation operation) {
        return compare(operation, 1D);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   operation  DOCUMENT ME!
     * @param   contrast   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Image> compare(final Operation operation,
            final double contrast) {
        final List<Image> result = new ArrayList<Image>();
        results.put(operation, result);

        if ((firstOperands == null) || (secondOperands == null) || (firstOperands.size() != secondOperands.size())) {
            return result;
        }

        try {
            if (firstOperands.isEmpty() || secondOperands.isEmpty()) {
                extractImages();
            }
        } catch (final Exception ex) {
            LOG.warn("Could not extract images.", ex);
            return result;
        }

        for (int i = 0; i < firstOperands.size(); i++) {
            try {
                result.add(
                    i,
                    process(operation, firstOperands.get(i), secondOperands.get(i), contrast));
            } catch (final Exception ex) {
                LOG.warn("Could not calculate '" + operation + "'.", ex);
                result.clear();
                break;
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   operation      DOCUMENT ME!
     * @param   firstOperand   DOCUMENT ME!
     * @param   secondOperand  DOCUMENT ME!
     * @param   contrast       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception                 DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    protected Image process(final Operation operation,
            final RenderedImage firstOperand,
            final RenderedImage secondOperand,
            final double contrast) throws Exception {
        RenderedOp resultImage = null;
        ParameterBlock pb;

        switch (operation) {
            case SUBTRACTION: {
                pb = new ParameterBlock();

                // In common cases, the time period of the first operand will be before the time period of the second
                // operand. This means, the user provides the two time series in a manner like "ozone 2030, ozone 2040".
                // In order to show the difference between the time series, it's useful to support a reading like
                // "compared with 2030, ozone concentrations in 2040 will be increase/decrease".

                // Let's assume we have low concentrations in the first operand and high concentrations in the second.
                // Further we assume that "green" means low and "red" high concentration.
                // Then the calculation green - red would produce green, representing a low value. But the user shall
                // see that the concentration increases, which is represented by red. Thus, we switch the operands.

                pb.addSource(secondOperand);
                pb.addSource(firstOperand);
                resultImage = JAI.create("subtract", pb);

                break;
            }
            case AVERAGE: {
                if (layerStyle == null) {
                    throw new IllegalArgumentException("Calculating the average of layers requires a layer style.");
                }

                final List<Entry> colorMap = layerStyle.getColorMap();
                if ((colorMap == null) || (colorMap.size() != 2)) {
                    throw new IllegalArgumentException("Calculating the average of layers requires a valid color map.");
                }
                if (!areIndependant(colorMap.get(0).getColor(), colorMap.get(1).getColor())) {
                    throw new Exception("Colors of layer style are not independent.");
                }

                final double[] factor = new double[] { 0.5D, 0.5D, 0.5D, 1D };
                final double[] constant = new double[] { 0D, 0D, 0D, 0D };

                // Scale down operands
                pb = new ParameterBlock();
                pb.addSource(firstOperand);
                pb.add(factor);
                pb.add(constant);
                final RenderedOp firstOperandScaled = JAI.create("rescale", pb);

                pb = new ParameterBlock();
                pb.addSource(secondOperand);
                pb.add(factor);
                pb.add(constant);
                final RenderedOp secondOperandScaled = JAI.create("rescale", pb);

                // Add scaled operands
                pb = new ParameterBlock();
                pb.addSource(firstOperandScaled);
                pb.addSource(secondOperandScaled);
                final RenderedOp addition = JAI.create("add", pb);

                resultImage = addition;

                break;
            }
        }

        if (resultImage == null) {
            throw new Exception("Result of operation '" + operation.toString() + "' is null.");
        }

        resultImage = rescale(contrast, resultImage);

        return resultImage.getAsBufferedImage();
    }

    /**
     * DOCUMENT ME!
     */
    private void extractImages() {
        if ((firstOperand.getPNode() == null) || (secondOperand.getPNode() == null)
                    || (firstOperand.getPNode().getChildrenCount() != secondOperand.getPNode().getChildrenCount())) {
            return;
        }

        firstOperands.clear();
        secondOperands.clear();

        ParameterBlock pb;
        for (final Iterator iter = firstOperand.getPNode().getChildrenIterator(); iter.hasNext();) {
            final Object imageObj = iter.next();

            if (!(imageObj instanceof XPImage)) {
                firstOperands.clear();
                return;
            }

            final RenderedOp renderedImage = JAI.create("awtimage", ((XPImage)imageObj).getImage());

            pb = new ParameterBlock();
            pb.addSource(renderedImage);
            pb.add(cropX);
            pb.add(cropY);
            pb.add(cropWidth);
            pb.add(cropHeight);

            firstOperands.add(JAI.create("crop", pb));
        }

        for (final Iterator iter = secondOperand.getPNode().getChildrenIterator(); iter.hasNext();) {
            final Object imageObj = iter.next();

            if (!(imageObj instanceof XPImage)) {
                firstOperands.clear();
                secondOperands.clear();
                return;
            }

            final RenderedOp renderedImage = JAI.create("awtimage", ((XPImage)imageObj).getImage());

            pb = new ParameterBlock();
            pb.addSource(renderedImage);
            pb.add(cropX);
            pb.add(cropY);
            pb.add(cropWidth);
            pb.add(cropHeight);
            secondOperands.add(JAI.create("crop", pb));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   contrast     DOCUMENT ME!
     * @param   resultImage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RenderedOp rescale(final double contrast,
            final RenderedOp resultImage) {
        // Should be OK not to use an epsilon here, since a good developer uses real double constants (1D) as parameter.
        if (1D != contrast) {
            final ParameterBlock pb = new ParameterBlock();

            pb.addSource(resultImage);
            pb.add(new double[] { contrast, contrast, contrast, 1D });
            pb.add(new double[] { 0D, 0D, 0D, 0D });

            return JAI.create("rescale", pb);
        }

        return resultImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   color  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean[] getInfluencingBands(final Color color) {
        final boolean[] result = new boolean[] { false, false, false };

        if (color == null) {
            return result;
        }

        if (color.getRed() > 0) {
            result[0] = true;
        }
        if (color.getGreen() > 0) {
            result[1] = true;
        }
        if (color.getBlue() > 0) {
            result[2] = true;
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   minColor  DOCUMENT ME!
     * @param   maxColor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean areIndependant(final Color minColor, final Color maxColor) {
        if ((minColor == null) || (maxColor == null)) {
            LOG.warn("Determining the independance of two colors requires two not null colors.");
            return false;
        }

        final boolean[] minBands = getInfluencingBands(minColor);
        final boolean[] maxBands = getInfluencingBands(maxColor);

        if ((minBands == null) || (maxBands == null) || (minBands.length != maxBands.length)) {
            LOG.warn("Could not determine if color '" + minColor + "' and color '" + maxColor
                        + "' are independant. Band count doesn't match.");
            return false;
        }

        for (int i = 0; i < maxBands.length; i++) {
            if (maxBands[i] && minBands[i]) {
                return false;
            }
        }

        return true;
    }
}
