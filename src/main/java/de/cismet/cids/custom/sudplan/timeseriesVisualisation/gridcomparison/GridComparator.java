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

import java.io.File;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.imageio.ImageIO;

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

        /*ADDITION, */ SUBTRACTION, AVERAGE;

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
        return compare(operation, 128D, 1D, 0D);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   operation    DOCUMENT ME!
     * @param   scaleCenter  DOCUMENT ME!
     * @param   contrast     DOCUMENT ME!
     * @param   brightness   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Image> compare(final Operation operation,
            final double scaleCenter,
            final double contrast,
            final double brightness) {
//        List<Image> result = results.get(operation);

//        if (result != null) {
//            return result;
//        }

//        result = new ArrayList<Image>();
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
                    process(operation, firstOperands.get(i), secondOperands.get(i), scaleCenter, contrast, brightness));
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
     * @param   scaleCenter    DOCUMENT ME!
     * @param   contrast       DOCUMENT ME!
     * @param   brightness     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception                 DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    protected Image process(final Operation operation,
            final RenderedImage firstOperand,
            final RenderedImage secondOperand,
            final double scaleCenter,
            final double contrast,
            final double brightness) throws Exception {
        RenderedOp resultImage = null;
        ParameterBlock pb;

//        try {
//            pb = new ParameterBlock();
//            pb.add(firstOperandAWT);
//            firstOperand = JAI.create("awtimage", pb);
//
//            pb = new ParameterBlock();
//            pb.add(secondOperandAWT);
//            secondOperand = JAI.create("awtimage", pb);
//        } catch (final Exception ex) {
//            throw new Exception("Couldn't convert AWT images to JAI images.", ex);
//        }

        switch (operation) {
//            case ADDITION: {
//                pb = new ParameterBlock();
//                pb.addSource(firstOperand);
//                pb.addSource(secondOperand);
//                resultImage = JAI.create("add", pb);
//
//                pb = new ParameterBlock();
//                pb.addSource(resultImage);
//                // Invert the red band of the addition and use it as green band
//                pb.add(
//                    new double[][] {
//                        { 1D, 0D, 0D, 0D },
//                        { -1D, 0D, 0D, 255D },
//                        { 0D, 0D, 0D, 0D } // ,
////                        { 0D, 0D, 0D, 255D },
//                    });
//
//                // Invert the green band of the addition and use it as red band
//// pb.add(
//// new double[][]{
//// {1D, 0D, 0D, 0D, 0D},
//// {-1D, 0D, 0D, 0D, 255D},
//// {0D, 0D, 0D, 0D, 0D},
//// {0D, 0D, 0D, 0D, 255D},});
//
//                resultImage = JAI.create("bandcombine", pb);
//
//                break;
//            }
            case SUBTRACTION: {
                pb = new ParameterBlock();
                pb.addSource(firstOperand);
                pb.addSource(secondOperand);
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

                final boolean[] minBands = getInfluencingBands(colorMap.get(0).getColor());
                final boolean[] maxBands = getInfluencingBands(colorMap.get(1).getColor());

                if ((minBands == null) || (maxBands == null) || (minBands.length != maxBands.length)) {
                    throw new Exception("Couldn't interpret colors of layer style.");
                }

                final double[] factor = new double[] { 0D, 0D, 0D, 1D };
                final double[] constant = new double[] { 0D, 0D, 0D, 0D };
                final double[][] bandCombinationMatrix = new double[][] {
                        { 0D, 0D, 0D, 0D },
                        { 0D, 0D, 0D, 0D },
                        { 0D, 0D, 0D, 0D } // ,
//                        { 0D, 0D, 0D, 255D }
                    };

                for (int i = 0; i < maxBands.length; i++) {
                    if (maxBands[i]) {
                        factor[i] = .5D;
                        bandCombinationMatrix[i][i] = 1D;
                    }
                }

                for (int i = 0; i < minBands.length; i++) {
                    if (minBands[i]) {
                        bandCombinationMatrix[i][3] = 255D;

                        for (int j = 0; j < maxBands.length; j++) {
                            if (maxBands[j]) {
                                bandCombinationMatrix[i][j] = -1D;
                            }
                        }
                    }
                }

                // Scale down operands
                pb = new ParameterBlock();
                pb.addSource(firstOperand);
                pb.add(factor);
                pb.add(constant);
                final RenderedOp firstOperandScaled = JAI.create("rescale", pb);

//                try {
//                    final File outputFileBuffered = new File("D:\\Caches\\xy\\BI_" + operation.name() + "_0_"
//                                    + System.currentTimeMillis()
//                                    + ".png");
//                    ImageIO.write(firstOperandScaled, "png", outputFileBuffered);
//                } catch (final Exception ex) {
//                    // NoOp
//                }

                pb = new ParameterBlock();
                pb.addSource(secondOperand);
                pb.add(factor);
                pb.add(constant);
                final RenderedOp secondOperandScaled = JAI.create("rescale", pb);

//                try {
//                    final File outputFileBuffered = new File("D:\\Caches\\xy\\BI_" + operation.name() + "_1_"
//                                    + System.currentTimeMillis()
//                                    + ".png");
//                    ImageIO.write(secondOperandScaled, "png", outputFileBuffered);
//                } catch (final Exception ex) {
//                    // NoOp
//                }

                // Add scaled operands
                pb = new ParameterBlock();
                pb.addSource(firstOperandScaled);
                pb.addSource(secondOperandScaled);
                final RenderedOp addition = JAI.create("add", pb);

//                try {
//                    final File outputFileBuffered = new File("D:\\Caches\\xy\\BI_" + operation.name() + "_2_"
//                                    + System.currentTimeMillis()
//                                    + ".png");
//                    ImageIO.write(addition, "png", outputFileBuffered);
//                } catch (final Exception ex) {
//                    // NoOp
//                }

                pb = new ParameterBlock();
                pb.addSource(addition);
                pb.add(bandCombinationMatrix);
                resultImage = JAI.create("bandcombine", pb);

//                try {
//                    final File outputFileBuffered = new File("D:\\Caches\\xy\\BI_" + operation.name() + "_3_"
//                                    + System.currentTimeMillis()
//                                    + ".png");
//                    ImageIO.write(resultImage, "png", outputFileBuffered);
//                } catch (final Exception ex) {
//                    // NoOp
//                }

                break;
            }
        }

        if (resultImage == null) {
            throw new Exception("Result of operation '" + operation.toString() + "' is null.");
        }

        resultImage = rescale(contrast, brightness, scaleCenter, resultImage);

        // final BufferedImage result = resultImage.getAsBufferedImage();
// try {
        // final File outputFile = new File("D:\\Caches\\xy\\" + operation.name() + "_" + System.currentTimeMillis()
        // + ".png");
        // final FileOutputStream stream = new FileOutputStream(outputFile);
        // JAI.create("encode", resultImage, stream, "PNG", null);
        // stream.flush();
        // stream.close();
        //
        // // Store the image in the BMP format.
        //// JAI.create(
        //// "filestore",
        //// resultImage,
        //// "D:\\Caches\\xy\\1_"
        //// // + operation.name()
        //// // + "_"
        //// + System.currentTimeMillis()
        //// + ".tif",
        //// "TIFF",
        //// null);
        //
//         final File outputFileBuffered = new File("D:\\Caches\\xy\\BI_" + operation.name() + "_"
//         + System.currentTimeMillis()
//         + ".png");
//         ImageIO.write(resultImage, "png", outputFileBuffered);
//         } catch (final Exception ex) {
//         // NoOp
//         }
        //
        // return result;

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
        final RenderedOp alphaImage = null;
        for (final Iterator iter = firstOperand.getPNode().getChildrenIterator(); iter.hasNext();) {
            final Object imageObj = iter.next();

            if (!(imageObj instanceof XPImage)) {
                firstOperands.clear();
                return;
            }

//            pb = new ParameterBlock();
//            pb.add(((XPImage)imageObj).getImage());
//            final RenderedOp renderedImage = JAI.create("awtimage", pb);
//
//            final Image image = ((XPImage)imageObj).getImage();
//            final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
//                    image.getHeight(null),
//                    BufferedImage.TYPE_INT_ARGB);
//            final Graphics2D documentGraphics = bufferedImage.createGraphics();
//            documentGraphics.drawImage(image, 0, 0, null);
//            documentGraphics.dispose();
//
//            pb = new ParameterBlock();
//            pb.add(bufferedImage);
//            final RenderedOp renderedImage = JAI.create("awtimage", pb);
            final RenderedOp renderedImage = JAI.create("awtimage", ((XPImage)imageObj).getImage());

//            if (alphaImage == null) {
//                pb = new ParameterBlock();
//                pb.add(new Float(renderedImage.getWidth()));
//                pb.add(new Float(renderedImage.getHeight()));
//                pb.add(new Integer[] { new Integer(255) });
//                alphaImage = JAI.create("constant", pb);
//            }
//
//            if (alphaImage != null) {
//                pb = new ParameterBlock();
//                pb.addSource(alphaImage);
//                pb.addSource(renderedImage);
//                final ImageLayout layout = new ImageLayout();
//                layout.setColorModel(new ComponentColorModel(
//                        ColorSpace.getInstance(ColorSpace.CS_sRGB),
//                        new int[] { 8, 8, 8, 8 },
//                        true,
//                        false,
//                        Transparency.OPAQUE,
//                        DataBuffer.TYPE_BYTE));
//                pb.add(new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout));
//                renderedImage = JAI.create("bandmerge", pb);
//            }

            pb = new ParameterBlock();
            pb.addSource(renderedImage);
//            pb.addSource(bufferedImage);
            pb.add(cropX);
            pb.add(cropY);
            pb.add(cropWidth);
            pb.add(cropHeight);

            firstOperands.add(JAI.create("crop", pb));

//            try {
//                final File outputFile = new File("D:\\Caches\\xy\\EI_" + System.currentTimeMillis() + ".png");
//                final FileOutputStream stream = new FileOutputStream(outputFile);
//                JAI.create("encode", firstOperands.get(i++), stream, "PNG", null);
//                stream.flush();
//                stream.close();
//            } catch (final Exception ex) {
//                // NoOp
//            }
        }

        for (final Iterator iter = secondOperand.getPNode().getChildrenIterator(); iter.hasNext();) {
            final Object imageObj = iter.next();

            if (!(imageObj instanceof XPImage)) {
                firstOperands.clear();
                secondOperands.clear();
                return;
            }

//            pb = new ParameterBlock();
//            pb.add(((XPImage)imageObj).getImage());
//            final RenderedOp renderedImage = JAI.create("awtimage", pb);

//            final Image image = ((XPImage)imageObj).getImage();
//            final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
//                    image.getHeight(null),
//                    BufferedImage.TYPE_INT_ARGB);
//            final Graphics2D documentGraphics = bufferedImage.createGraphics();
//            documentGraphics.drawImage(image, 0, 0, null);
//            documentGraphics.dispose();
//
//            pb = new ParameterBlock();
//            pb.add(bufferedImage);
//            final RenderedOp renderedImage = JAI.create("awtimage", pb);
            final RenderedOp renderedImage = JAI.create("awtimage", ((XPImage)imageObj).getImage());

//            if (alphaImage != null) {
//                pb = new ParameterBlock();
//                pb.addSource(alphaImage);
//                pb.addSource(renderedImage);
//                final ImageLayout layout = new ImageLayout();
//                layout.setColorModel(new ComponentColorModel(
//                        ColorSpace.getInstance(ColorSpace.CS_sRGB),
//                        new int[] { 8, 8, 8, 8 },
//                        true,
//                        false,
//                        Transparency.OPAQUE,
//                        DataBuffer.TYPE_BYTE));
//                pb.add(new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout));
//                renderedImage = JAI.create("bandmerge", pb);
//            }

            pb = new ParameterBlock();
            pb.addSource(renderedImage);
//            pb.addSource(bufferedImage);
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
     * @param   brightness   DOCUMENT ME!
     * @param   scaleCenter  DOCUMENT ME!
     * @param   resultImage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RenderedOp rescale(final double contrast,
            final double brightness,
            final double scaleCenter,
            final RenderedOp resultImage) {
        // Should be OK not to use an epsilon here, since a good developer uses real double constants (1D) as parameter.
        if ((1D != contrast) || (0D != brightness)) {
//            final double constant = ((contrast * scaleCenter) * -1D) + scaleCenter + brightness;
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
