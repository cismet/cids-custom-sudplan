/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Stroke;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import javax.vecmath.Vector2d;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.XStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.gui.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class MovingCameraFeature extends DefaultStyledFeature implements XStyledFeature, CameraChangedListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(MovingCameraFeature.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ImageIcon defaultPointIcon;
    private final transient ImageIcon featureIcon;
    private final transient Vector2d iconDirection;
    private final transient Canvas3D canvas3d;

    private transient ImageIcon currentPointIcon;

    private transient Geometry currentGeometry;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MovingCameraFeature object.
     */
    public MovingCameraFeature() {
        featureIcon = SMSUtils.loadImageIcon(getClass(), "video_camera_24.png");      // NOI18N
        defaultPointIcon = SMSUtils.loadImageIcon(getClass(), "video_camera_48.png"); // NOI18N

        assert featureIcon != null : "feature icon must be loaded";            // NOI18N
        assert defaultPointIcon != null : "default point icon must be loaded"; // NOI18N

        // this cannot be computed somehow and depends on the icon chosen above
        iconDirection = new Vector2d(1, 0);
        currentPointIcon = defaultPointIcon;

        canvas3d = Lookup.getDefault().lookup(Canvas3D.class);
        if (canvas3d == null) {
            LOG.warn("there is no 3d canvas, the moving camera feature cannot be used"); // NOI18N
        } else {
            currentGeometry = canvas3d.getCameraPosition();
            final Vector2d camDir = new Vector2d(canvas3d.getCameraDirection().x, canvas3d.getCameraDirection().y);
            currentPointIcon = Static2DTools.rotate(currentPointIcon, iconDirection.angle(camDir), true);

            canvas3d.addCameraChangedListener(WeakListeners.create(CameraChangedListener.class, this, canvas3d));
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ImageIcon getIconImage() {
        return featureIcon;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(MovingCameraFeature.class, "MovingCameraFeature.getName().returnValue"); // NOI18N
    }

    @Override
    public String getType() {
        return getName();
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
    public void cameraChanged(final CameraChangedEvent cce) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("camera changed: " + cce); // NOI18N
        }

        final Vector2d newDirection = new Vector2d(cce.getNewDirection().x, cce.getNewDirection().y);
        newDirection.normalize();

        // rotate clock-wise if looking to south or counter clock-wise if looking to north
        final double angle = -1 * Math.signum(newDirection.y) * iconDirection.angle(newDirection);

        if (LOG.isDebugEnabled()) {
            LOG.debug("new angle: " + angle); // NOI18N
        }

        final ImageIcon newDirIcon = Static2DTools.rotate(defaultPointIcon, angle, true);

        synchronized (this) {
            currentPointIcon = newDirIcon;
            currentGeometry = cce.getNewPosition();
        }

        final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
        if (mc.getFeatureCollection().contains(this)) {
            if (mc.getCurrentBoundingBox() instanceof XBoundingBox) {
                final XBoundingBox bbox = (XBoundingBox)mc.getCurrentBoundingBox();
                final Geometry bboxGeom = bbox.getGeometry();

                if (!bboxGeom.contains(cce.getNewPosition())) {
                    mc.gotoBoundingBoxWithHistory(new XBoundingBox(canvas3d.getBoundingBox()));
                }
            } else {
                throw new IllegalStateException("MappingComponent is supposed to use XBoundingBoxes"); // NOI18N
            }

            mc.reconsiderFeature(this);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("moving camera feature not in featurecollection, not reflecting changes to map"); // NOI18N
            }
        }
    }

    @Override
    public FeatureAnnotationSymbol getPointAnnotationSymbol() {
        return FeatureAnnotationSymbol.newCenteredFeatureAnnotationSymbol(
                currentPointIcon.getImage(),
                currentPointIcon.getImage());
    }

    @Override
    public Geometry getGeometry() {
        return currentGeometry;
    }
}
