/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.sudplan;

import org.apache.log4j.Logger;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class TimeseriesFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesFeatureRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Image rainPointSymbolUnselected;

    //~ Constructors -----------------------------------------------------------

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form TimeseriesFeatureRenderer.
     */
    public TimeseriesFeatureRenderer() {
        initComponents();
        BufferedImage image = null;
        try {
            final InputStream is = getClass().getResourceAsStream("rain.png"); // NOI18N
            image = ImageIO.read(is);
        } catch (final IOException ex) {
            LOG.warn("cannot load timeseries feature icon", ex);               // NOI18N
        }

        rainPointSymbolUnselected = image;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setMaximumSize(new java.awt.Dimension(300, 200));
        setMinimumSize(new java.awt.Dimension(300, 200));
        setPreferredSize(new java.awt.Dimension(300, 200));
        setLayout(new java.awt.BorderLayout());
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    @Override
    public void assign() {
        // possible memory leak and missing preview resolution adjustment if (LOG.isDebugEnabled()) {
        // LOG.debug("assign");                                                                              // NOI18N }
        // try { final TimeseriesChartPanel panel = new TimeseriesChartPanel((String)cidsBean.getProperty("uri")); //
        // NOI18N add(panel, BorderLayout.CENTER); } catch (final MalformedURLException ex) { final String message =
        // "cidsbean contains invalid uri";                                           // NOI18N LOG.error(message, ex);
        // throw new IllegalStateException(message, ex); }
    }

    // TODO: not necessarily only rain symbol, symbol selection must be cidsbean dependent
    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        if (rainPointSymbolUnselected == null) {
            return super.getPointSymbol();
        } else {
            return FeatureAnnotationSymbol.newCustomSweetSpotFeatureAnnotationSymbol(
                    rainPointSymbolUnselected,
                    null,
                    0.5,
                    0.9);
        }
    }

    @Override
    public float getTransparency() {
        return 0.9f;
    }
}
