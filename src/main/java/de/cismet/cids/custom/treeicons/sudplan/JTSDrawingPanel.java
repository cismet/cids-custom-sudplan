/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.treeicons.sudplan;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import org.geotools.geometry.jts.LiteShape;

import java.awt.*;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class JTSDrawingPanel extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final int MARGIN = 5;

    //~ Instance fields --------------------------------------------------------

    private List<Geometry> geometries = new ArrayList<Geometry>();
    private AffineTransform geomToScreen;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    public void addGeometry(final Geometry geom) {
        geometries.add(geom);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (!geometries.isEmpty()) {
            setTransform();

            final Graphics2D g2 = (Graphics2D)g;
            final Paint polyPaint = new GradientPaint(0, 0, Color.CYAN, 100,
                    100, Color.MAGENTA, true);
            final Paint defaultPaint = Color.getHSBColor(223f / 360f, 0.45f, 0.76f);

            for (final Geometry geom : geometries) {
                final LiteShape shape = new LiteShape(geom, geomToScreen, false);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, this.getWidth(), this.getHeight());

//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                final Composite origComp = g2.getComposite();
                final AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
                g2.setPaint(new Color(153, 153, 254));
//                g2.setComposite(comp);
                g2.fill(shape);
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.setPaint(Color.BLACK);
//                g2.setComposite(origComp);
//                g2.fillRect(0, 0,biShape.getWidth(), biShape.getHeight());
                g2.draw(shape);

//                g2.drawRect(MARGIN, MARGIN, 16, 16);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setTransform() {
        final Envelope env = getGeometryBounds();
        final Rectangle drawingRect = new Rectangle(0, 0, 16, 16);

        final double scale = Math.min(drawingRect.getWidth()
                        / env.getWidth(),
                drawingRect.getHeight()
                        / env.getHeight());
        final double xoff = MARGIN - (scale * env.getMinX());
        final double yoff = MARGIN + (env.getMaxY() * scale);
        geomToScreen = new AffineTransform(scale, 0, 0, -scale, xoff, yoff);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Envelope getGeometryBounds() {
        final Envelope env = new Envelope();
        for (final Geometry geom : geometries) {
            final Envelope geomEnv = geom.getEnvelopeInternal();
            env.expandToInclude(geomEnv);
        }

        return env;
    }
}
