/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d.dfki;

import com.dfki.av.sudplan.ui.vis.VisualisationComponent;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.lookup.ServiceProvider;

import javax.swing.JComponent;

import javax.vecmath.Vector3d;

import de.cismet.cids.custom.sudplan.cismap3d.CameraChangedListener;
import de.cismet.cids.custom.sudplan.cismap3d.Canvas3D;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = Canvas3D.class)
public final class Canvas3DDFKIImpl implements Canvas3D {

    //~ Instance fields --------------------------------------------------------

    private final transient VisualisationComponent visComponent;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Canvas3DDFKIImpl object.
     */
    public Canvas3DDFKIImpl() {
        visComponent = Cismap3DDFKI.getInstance().getVisComponent();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void home() {
        visComponent.gotoToHome();
    }

    @Override
    public void resetCamera() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCameraPosition(final Coordinate coord3d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Coordinate getCameraPosition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCameraDirection(final Vector3d direction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Vector3d getCameraDirection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBoundingBox(final Geometry geom) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getBoundingBox() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addCameraChangedListener(final CameraChangedListener ccl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeCameraChangedListener(final CameraChangedListener ccl) {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
