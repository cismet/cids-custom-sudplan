/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunHelper.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunHelper object.
     */
    private RunHelper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   runBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String createIONameSnippet(final CidsBean runBean) {
        final StringBuilder sb = new StringBuilder();

        sb.append((String)((CidsBean)runBean.getProperty("model")).getProperty("name")); // NOI18N
        sb.append("(Run: ");
        sb.append((String)runBean.getProperty("name"));                                  // NOI18N
        sb.append(')');

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   grid  data DOCUMENT ME!
     * @param   var   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image gridToImage(final Grid grid, final int var) {
        final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

        final Double[][] data = grid.getData();
        final int height = data.length;
        int width = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < data.length; ++i) {
            if (data[i].length > width) {
                width = data[i].length;
            }

            for (int j = 0; j < data[i].length; ++j) {
                if (data[i][j] > max) {
                    max = data[i][j];
                } else if (data[i][j] < min) {
                    min = data[i][j];
                }
            }
        }
        final BufferedImage image = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                final Double value = data[i][j];
                final double h = 0.0d + ((value - min) * (0.75d - 0.0d) / (max - min));
                final double variant = 0.0d + ((var - min) * (0.75d - 0.0d) / (max - min));
//                final double ten = Math.round(h * 10) / 10.0d;
                final double shift = (h + 0.25d + variant) * -1;
                image.setRGB(j, i, Color.getHSBColor((float)shift, 0.6f, 0.6f).getRGB());
            }
        }

        return image;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   image  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BufferedImage toBufferedImage(final Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // Determine if the image has transparent pixels
        final boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        try {
            // Determine the type of transparency of the new buffered image
            final int transparency;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            } else {
                transparency = Transparency.OPAQUE;
            }

            // Create the buffered image
            final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice gs = env.getDefaultScreenDevice();
            final GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (final HeadlessException e) {
            // The system does not have a screen
            // Create a buffered image using the default color model
            final int type;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            } else {
                type = BufferedImage.TYPE_INT_RGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        final Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }
    /**
     * This method returns true if the specified image has transparent pixels.
     *
     * @param   image  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean hasAlpha(final Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            final BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        final PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            // skip
        }

        // Get the image's color model
        final ColorModel cm = pg.getColorModel();

        return cm.hasAlpha();
    }
}
