/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.treeicons.sudplan;

import Sirius.navigator.types.treenode.ClassTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.CrsTransformer;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CatchmentAreaIconFactory implements CidsTreeObjectIconFactory {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Icon getClosedPureNodeIcon(final PureTreeNode ptn) {
        // default icon
        return null;
    }

    @Override
    public Icon getOpenPureNodeIcon(final PureTreeNode ptn) {
        // default icon
        return null;
    }

    @Override
    public Icon getLeafPureNodeIcon(final PureTreeNode ptn) {
        // default icon
        return null;
    }

    @Override
    public Icon getOpenObjectNodeIcon(final ObjectTreeNode otn) {
        return getLeafObjectNodeIcon(otn);
    }

    @Override
    public Icon getClosedObjectNodeIcon(final ObjectTreeNode otn) {
        return getLeafObjectNodeIcon(otn);
    }

    @Override
    public Icon getLeafObjectNodeIcon(final ObjectTreeNode otn) {
        // TBD
        final CidsBean bean = otn.getMetaObject().getBean();
        final Geometry geom = CrsTransformer.transformToGivenCrs((Geometry)bean.getProperty("area.geo_field"),
                SMSUtils.EPSG_WUPP);

        final BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = (Graphics2D)bi.getGraphics();

        final Paint paint = Color.BLUE;

        final Envelope env = geom.getEnvelopeInternal();
        final double scale = Math.min(16 / env.getWidth(), 16 / env.getHeight());
        final AffineTransform at = AffineTransform.getScaleInstance(scale, scale);

        final Coordinate[] coords = geom.getCoordinates();

        final Polygon p = new Polygon();

        g2d.fill(null);

        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Icon getClassNodeIcon(final ClassTreeNode dmtn) {
        // default icon
        return null;
    }
}
