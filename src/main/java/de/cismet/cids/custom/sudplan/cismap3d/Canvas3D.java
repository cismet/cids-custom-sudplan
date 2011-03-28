/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d;

import com.vividsolutions.jts.geom.Geometry;

import javax.vecmath.Vector3d;

import de.cismet.cids.custom.sudplan.UIProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface Canvas3D extends UIProvider {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    enum InteractionMode {

        //~ Enum constants -----------------------------------------------------

        ZOOM, PAN, ROTATE
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void home();

    /**
     * DOCUMENT ME!
     *
     * @param  geom  coord3d DOCUMENT ME!
     */
    void setCameraPosition(Geometry geom);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Geometry getCameraPosition();

    /**
     * DOCUMENT ME!
     *
     * @param  direction  DOCUMENT ME!
     */
    void setCameraDirection(Vector3d direction);

    /**
     * DOCUMENT ME!
     */
    void resetCamera();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Vector3d getCameraDirection();

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    void setBoundingBox(Geometry geom);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Geometry getBoundingBox();

    /**
     * DOCUMENT ME!
     *
     * @param  mode  DOCUMENT ME!
     */
    void setInteractionMode(InteractionMode mode);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    InteractionMode getInteractionMode();

    /**
     * DOCUMENT ME!
     *
     * @param  ccl  DOCUMENT ME!
     */
    void addCameraChangedListener(CameraChangedListener ccl);

    /**
     * DOCUMENT ME!
     *
     * @param  ccl  DOCUMENT ME!
     */
    void removeCameraChangedListener(CameraChangedListener ccl);
}
