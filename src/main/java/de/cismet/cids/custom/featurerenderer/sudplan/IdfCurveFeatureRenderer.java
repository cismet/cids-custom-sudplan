/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.sudplan;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.sudplan.IDFCurve;
import de.cismet.cids.custom.sudplan.IDFTablePanel;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class IdfCurveFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(IdfCurveFeatureRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Image rainPointSymbolUnselected;

    //~ Constructors -----------------------------------------------------------

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form TimeseriesFeatureRenderer.
     */
    public IdfCurveFeatureRenderer() {
        initComponents();
        BufferedImage image = null;
        try {
            final InputStream is = getClass().getResourceAsStream("rain.png"); // NOI18N
            image = ImageIO.read(is);
        } catch (final IOException ex) {
            LOG.warn("cannot load idf curve feature icon", ex);                // NOI18N
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("assign"); // NOI18N
        }

        final String json = (String)cidsBean.getProperty("uri");
        final ObjectMapper mapper = new ObjectMapper();
        final IDFCurve curve;

        try {
            curve = mapper.readValue(new StringReader(json), IDFCurve.class);
            final IDFTablePanel panel = new IDFTablePanel(curve);
            add(panel, BorderLayout.CENTER);
        } catch (final Exception ex) {
            final String message = "cannot create idf feature renderer component"; // NOI18N
            LOG.error(message, ex);
        }
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
