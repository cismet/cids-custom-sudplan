/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d.dfki;

import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.CameraEvent;
import com.dfki.av.sudplan.camera.CameraListener;
import com.dfki.av.sudplan.ui.vis.VisualisationComponent;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import javax.swing.JComponent;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.cismap3d.CameraChangedEvent;
import de.cismet.cids.custom.sudplan.cismap3d.CameraChangedListener;
import de.cismet.cids.custom.sudplan.cismap3d.CameraChangedSupport;
import de.cismet.cids.custom.sudplan.cismap3d.Canvas3D;

import de.cismet.cismap.commons.CrsTransformer;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = Canvas3D.class)
public final class Canvas3DDFKIImpl implements Canvas3D {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(Canvas3DDFKIImpl.class);

    //~ Instance fields --------------------------------------------------------

    private final transient VisualisationComponent visComponent;
    private final transient CameraListener cameraL;

    private final transient CameraChangedSupport cameraChangedSupport;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Canvas3DDFKIImpl object.
     */
    public Canvas3DDFKIImpl() {
        visComponent = Cismap3DDFKI.getInstance().getVisComponent();
        cameraChangedSupport = new CameraChangedSupport();
        cameraL = new CameraListenerImpl();

        // TODO: we need to use weak listeners
        visComponent.getGeographicCamera().addCameraListner(cameraL);
    }

    //~ Methods ----------------------------------------------------------------

    // TODO: remove as soon as we use weak listener impl for the CameraListener
    @Override
    protected void finalize() throws Throwable {
        final Camera cam = visComponent.getGeographicCamera();
        // just to be sure that nothing happened to the camera in the meantime, we don't want an exception in finalize
        if (cam != null) {
            cam.removeCameraListner(cameraL);
        }

        super.finalize();
    }

    @Override
    public void home() {
        visComponent.gotoToHome();
    }

    @Override
    public void resetCamera() {
        visComponent.getGeographicCamera().setCameraToInitalViewingDirection();
    }

    @Override
    public void setCameraPosition(final Geometry geom) {
        // currently only epsg 4326 can be used with the 3d component
        final Geometry tg = CrsTransformer.transformToGivenCrs(geom, "EPSG:4326"); // NOI18N
        final Point3d pos3d = new Point3d(tg.getCoordinate().x, tg.getCoordinate().y, 0.3d);

        if (LOG.isDebugEnabled()) {
            LOG.debug("set camera position to: " + pos3d); // NOI18N
        }

        visComponent.getGeographicCamera().setCameraPosition(pos3d);
    }

    @Override
    public Geometry getCameraPosition() {
        final Point3d pos3d = visComponent.getGeographicCamera().getCameraPosition();
        final Coordinate coord = new Coordinate(pos3d.x, pos3d.y, pos3d.z);

        // currently the 3D component makes use of the 4326 srs only
        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        final Geometry pos = new Point(new CoordinateArraySequence(new Coordinate[] { coord }), factory);

        return pos;
    }

    @Override
    public void setCameraDirection(final Vector3d direction) {
        visComponent.getGeographicCamera().setCameraDirection(direction);
    }

    @Override
    public Vector3d getCameraDirection() {
        return visComponent.getGeographicCamera().getCameraDirection();
    }

    @Override
    public void setBoundingBox(final Geometry geom) {
        final Coordinate[] llUr = SMSUtils.getLlAndUr(geom);
        final Point3d ll3d = new Point3d(llUr[0].x, llUr[0].y, 0);
        final Point3d ur3d = new Point3d(llUr[1].x, llUr[1].y, 0);

        if (LOG.isDebugEnabled()) {
            LOG.debug("setBBox to: ll: " + ll3d + " | ur: " + ur3d); // NOI18N
        }

        final AdvancedBoundingBox abb = new AdvancedBoundingBox(ll3d, ur3d);

        visComponent.getGeographicCamera().gotoBoundingBox(abb);
    }

    @Override
    public Geometry getBoundingBox() {
        final AdvancedBoundingBox abb = visComponent.getGeographicCamera().getReducedBoundingBox();
        final Point3d ll3d = abb.getLower();
        final Point3d ur3d = abb.getUpper();

        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        final Coordinate[] bbox = new Coordinate[5];
        bbox[0] = new Coordinate(ll3d.x, ll3d.y);
        bbox[1] = new Coordinate(ll3d.x, ur3d.y);
        bbox[2] = new Coordinate(ur3d.x, ur3d.y);
        bbox[3] = new Coordinate(ur3d.x, ll3d.y);
        bbox[4] = new Coordinate(ll3d.x, ll3d.y);
        final LinearRing ring = new LinearRing(new CoordinateArraySequence(bbox), factory);
        final Geometry geometry = factory.createPolygon(ring, new LinearRing[0]);

        return geometry;
    }

    @Override
    public void addCameraChangedListener(final CameraChangedListener ccl) {
        cameraChangedSupport.addCameraChangedListener(ccl);
    }

    @Override
    public void removeCameraChangedListener(final CameraChangedListener ccl) {
        cameraChangedSupport.removeCameraChangedListener(ccl);
    }

    @Override
    public JComponent getUI() {
        return Cismap3DDFKI.getInstance().getVisComponent();
    }

    @Override
    public void setInteractionMode(final InteractionMode mode) {
        switch (mode) {
            case ZOOM: {
                visComponent.setModeZoom();
                break;
            }
            case PAN: {
                visComponent.setModePan();
                break;
            }
            case ROTATE: {
                visComponent.setModeRotate();
                break;
            }
        }
    }

    @Override
    public InteractionMode getInteractionMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CameraListenerImpl implements CameraListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void cameraMoved(final CameraEvent cameraEvent) {
            // moves don't change the direction
            final Point3d oldCamPos3d = cameraEvent.getOldCameraPosition();
            final Point3d newCamPos3d = cameraEvent.getNewCameraPosition();

            final Vector3d camDirection = visComponent.getGeographicCamera().getCameraDirection();

            final Coordinate oldCamPos = new Coordinate(oldCamPos3d.x, oldCamPos3d.y, oldCamPos3d.z);
            final Coordinate newCamPos = new Coordinate(newCamPos3d.x, newCamPos3d.y, newCamPos3d.z);

            // TODO: remove if fixed
            if (LOG.isDebugEnabled()) {
                LOG.debug("new cam pos: " + newCamPos3d);
            }

            // currently the 3D component makes use of the 4326 srs only
            final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
            final Geometry oldPos = new Point(new CoordinateArraySequence(new Coordinate[] { oldCamPos }), factory);
            final Geometry newPos = new Point(new CoordinateArraySequence(new Coordinate[] { newCamPos }), factory);

            final CameraChangedEvent cce = new CameraChangedEvent(
                    Canvas3DDFKIImpl.this,
                    oldPos,
                    newPos,
                    camDirection,
                    camDirection);

            cameraChangedSupport.fireCameraChanged(cce);
        }

        @Override
        public void cameraViewChanged(final CameraEvent cameraEvent) {
            // view changes don't change the position
            final Point3d camPos3d = visComponent.getGeographicCamera().getCameraPosition();
            final Coordinate camPos = new Coordinate(camPos3d.x, camPos3d.y, camPos3d.z);

            // currently the 3D component makes use of the 4326 srs only
            final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
            final Geometry curPos = new Point(new CoordinateArraySequence(new Coordinate[] { camPos }), factory);

            final Vector3d oldCamDir3d = cameraEvent.getOldCameraViewDirection();
            final Vector3d newCamDir3d = cameraEvent.getNewCameraViewDirection();

            final CameraChangedEvent cce = new CameraChangedEvent(
                    Canvas3DDFKIImpl.this,
                    curPos,
                    curPos,
                    oldCamDir3d,
                    newCamDir3d);

            // TODO: remove when fixed
            if (LOG.isDebugEnabled()) {
                LOG.debug("new cam dir: " + newCamDir3d);
            }

            cameraChangedSupport.fireCameraChanged(cce);
        }

        @Override
        public void cameraRegistered(final CameraEvent cameraEvent) {
            // we're probably not interested in this event
        }

        @Override
        public void cameraUnregistered(final CameraEvent cameraEvent) {
            // we're probably not interested in this event
        }
    }
}
