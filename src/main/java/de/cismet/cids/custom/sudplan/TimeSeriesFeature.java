/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jfree.util.Log;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import javax.imageio.ImageIO;

import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesFeature extends DefaultStyledFeature {

    //~ Instance fields --------------------------------------------------------

    private final transient Logger LOG = Logger.getLogger(TimeSeriesFeature.class);
    private FeatureAnnotationSymbol featureAnnotationSymbol;
    private int overlayWidth = 0;
    private int overlayHeight = 0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesFeature object.
     *
     * @param  g    the geometry of the time series
     * @param  bi2  s the time series shape (from legend)
     */
    public TimeSeriesFeature(final Geometry g, final BufferedImage bi2) {
        super();
        setGeometry(g);
        BufferedImage featureIcon = null;
        try {
            final InputStream is = getClass().getResourceAsStream(
                    "/de/cismet/cismap/commons/gui/res/featureInfo.png"); // NOI18N
            featureIcon = ImageIO.read(is);
        } catch (final IOException ex) {
            LOG.warn("cannot load timeseries feature icon", ex);          // NOI18N
        }
        // set the overlay on the lower left edge of the icon as default..
        int xPos = featureIcon.getWidth() - overlayWidth;
        int yPos = featureIcon.getHeight() - overlayHeight;
        int bgR = 255;
        int bgG = 255;
        int bgB = 255;
        Color standardBG = new Color(bgR, bgG, bgB);

        // try to get metainformation for overlay position, width, color from properties file and override the
        // default values if succesfull
        final Properties iconProps = new Properties();
        try {
            final InputStream in = getClass().getResourceAsStream(
                    "/de/cismet/cismap/commons/gui/res/featureInfoIcon.properties");                             // NOI18N
            if (in != null) {
                iconProps.load(in);
                in.close();
            } else {
                LOG.warn(
                    "Could not laod featureInfoIcon.properties file. Default values for overlay area are used"); // NOI18N
            }
        } catch (IOException ex) {
            LOG.error(
                "Could not read featureInfoIcon.properties file. Default values for overlay area are used",
                ex);                                                                                             // NOI18N
        }

        if (iconProps.isEmpty()
                    || !(iconProps.containsKey("overlayPositionX")                                                             // NOI18N
                        && iconProps.containsKey("overlayPositionY")
                        && iconProps.containsKey("overlayBackgroundColorR")
                        && iconProps.containsKey("overlayBackgroundColorG")
                        && iconProps.containsKey("overlayBackgroundColorB")
                        && iconProps.containsKey("overlayWidth")
                        && iconProps.containsKey("overlayHeigth"))) {                                                          // NOI18N
            LOG.warn(
                "featureInfoIcon.properties file does not contain all needed keys. Default values for overlay area are used"); // NOI18N
        } else {
            try {
                xPos = Integer.parseInt((String)iconProps.get("overlayPositionX"));                                            // NOI18N
                yPos = Integer.parseInt((String)iconProps.get("overlayPositionY"));                                            // NOI18N
                bgR = Integer.parseInt((String)iconProps.get("overlayBackgroundColorR"));
                bgG = Integer.parseInt((String)iconProps.get("overlayBackgroundColorG"));
                bgB = Integer.parseInt((String)iconProps.get("overlayBackgroundColorB"));
                standardBG = new Color(bgR, bgG, bgB);
                overlayWidth = Integer.parseInt((String)iconProps.get("overlayWidth"));                                        // NOI18N
                overlayHeight = Integer.parseInt((String)iconProps.get("overlayHeigth"));                                      // NOI18N
            } catch (NumberFormatException ex) {
                Log.error(
                    "Error while retrieving properties for overlay area. Default values for overlay area are used",            // NOI18N
                    ex);
            }
        }
//        final BufferedImage bi = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = (Graphics2D)featureIcon.getSubimage(xPos, yPos, overlayWidth, overlayHeight)
                    .getGraphics();

        // paint the time series symbol
        g2.drawImage(bi2, 0, 0, standardBG, null);

        final FeatureAnnotationSymbol symb = new FeatureAnnotationSymbol(featureIcon);
        symb.setSweetSpotX(0.5);
        symb.setSweetSpotY(0.9);
        featureAnnotationSymbol = symb;
    }

    //~ Methods ----------------------------------------------------------------

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
