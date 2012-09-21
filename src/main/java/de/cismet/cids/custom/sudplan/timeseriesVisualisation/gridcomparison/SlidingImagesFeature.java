/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.features.RasterDocumentFeature;
import de.cismet.cismap.commons.features.XStyledFeature;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class SlidingImagesFeature implements RasterDocumentFeature, XStyledFeature {

    //~ Instance fields --------------------------------------------------------

    private List<Image> images;
    private Geometry geometry;
    private int width = -1;
    private int height = -1;
    private int sliderPosition = 0;
    private BufferedImage document;
    private Graphics2D documentGraphics;

    private boolean hidden = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SlidingImagesFeature object.
     *
     * @param  images    DOCUMENT ME!
     * @param  geometry  DOCUMENT ME!
     */
    public SlidingImagesFeature(final List<Image> images, final Geometry geometry) {
        this.images = new ArrayList<Image>(images.size());
        this.geometry = geometry;

        for (int i = 0; i < images.size(); i++) {
            // Load the image
            final Image image = new ImageIcon(images.get(i)).getImage();
            this.images.add(i, image);

            if (width == -1) {
                width = image.getWidth(null);
            } else if (width != image.getWidth(null)) {
                width = -1;
                this.images.clear();
                break;
            }
            if (height == -1) {
                height = image.getHeight(null);
            } else if (height != image.getHeight(null)) {
                height = -1;
                this.images.clear();
                break;
            }
        }

        document = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        documentGraphics = document.createGraphics();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSliderPosition() {
        return sliderPosition;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sliderPosition  DOCUMENT ME!
     */
    public void setSliderPosition(final int sliderPosition) {
        this.sliderPosition = sliderPosition;
    }

    @Override
    public BufferedImage getRasterDocument() {
        if ((width <= 0) || (height <= 0) || (images == null) || (images.size() <= 0)) {
            return null;
        }

        final int i = sliderPosition / 100;
        final int rest = sliderPosition % 100;

        documentGraphics.drawImage(images.get(i), 0, 0, null);

        if ((i + 1) < images.size()) {
            documentGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((float)rest) / 100F));
            documentGraphics.drawImage(images.get(i + 1), 0, 0, null);
        }

        return document;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public void setGeometry(final Geometry geom) {
//        this.geometry = geom;
    }

    @Override
    public boolean canBeSelected() {
        return false;
    }

    @Override
    public void setCanBeSelected(final boolean canBeSelected) {
        // NoOp
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(final boolean editable) {
        // NoOp
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void hide(final boolean hiding) {
        this.hidden = hiding;
    }

    @Override
    public ImageIcon getIconImage() {
        return null;
    }

    @Override
    public String getName() {
        return "TADA";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public Stroke getLineStyle() {
        return null;
    }

    @Override
    public Paint getLinePaint() {
        return Color.BLACK;
    }

    @Override
    public void setLinePaint(final Paint linePaint) {
        // NoOp
    }

    @Override
    public int getLineWidth() {
        return 1;
    }

    @Override
    public void setLineWidth(final int width) {
        // NoOp
    }

    @Override
    public Paint getFillingPaint() {
        return null;
    }

    @Override
    public void setFillingPaint(final Paint fillingStyle) {
        // NoOp
    }

    @Override
    public float getTransparency() {
        return 1F;
    }

    @Override
    public void setTransparency(final float transparrency) {
        // NoOp
    }

    @Override
    public FeatureAnnotationSymbol getPointAnnotationSymbol() {
        return null;
    }

    @Override
    public void setPointAnnotationSymbol(final FeatureAnnotationSymbol featureAnnotationSymbol) {
        // NoOp
    }

    @Override
    public boolean isHighlightingEnabled() {
        return false;
    }

    @Override
    public void setHighlightingEnabled(final boolean enabled) {
        // NoOp
    }
}
