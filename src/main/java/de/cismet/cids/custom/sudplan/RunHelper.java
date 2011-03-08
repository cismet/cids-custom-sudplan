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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

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
     * @param   min   DOCUMENT ME!
     * @param   max   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BufferedImage gridToImage(final Grid grid, final int var, final double min, final double max) {
        final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

        final Double[][] data = grid.getData();
        final int height = data.length;
        int width = 0;
        for (int i = 0; i < data.length; ++i) {
            if (data[i].length > width) {
                width = data[i].length;
            }
        }

        final BufferedImage image = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                final Double value = data[i][j];
//                final double h = 0.0d + ((value - min) * (0.75d - 0.0d) / (max - min));
//                final double variant = 0.0d + ((var - min) * (0.75d - 0.0d) / (max - min));
////                final double ten = Math.round(h * 10) / 10.0d;
//                final double shift = (h + 0.25d + variant) * -1;
                final double normalised = (value - min) / (max - min);
                final double inverted = (normalised * -1) + 1;
                final double greenToRed = inverted / 3d;
                image.setRGB(j, i, Color.getHSBColor((float)greenToRed, 1f, 1f).getRGB());
            }
        }

        return image;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   grids  DOCUMENT ME!
     *
     * @return  a <code>double[]</code> of length 2, first value is min, second is max, never null
     */
    public static double[] getMinMaxValue(final Grid[] grids) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (final Grid grid : grids) {
            final Double[][] data = grid.getData();
            for (int i = 0; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    if (data[i][j] > max) {
                        max = data[i][j];
                    } else if (data[i][j] < min) {
                        min = data[i][j];
                    }
                }
            }
        }

        return new double[] { min, max };
    }
}
