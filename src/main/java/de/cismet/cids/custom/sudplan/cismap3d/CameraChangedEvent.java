/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d;

import com.vividsolutions.jts.geom.Geometry;

import java.util.EventObject;

import javax.vecmath.Vector3d;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CameraChangedEvent extends EventObject {

    //~ Instance fields --------------------------------------------------------

    private final transient Geometry oldPosition;
    private final transient Geometry newPosition;
    private final transient Vector3d oldDirection;
    private final transient Vector3d newDirection;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CameraChangedEvent object.
     *
     * @param  eventSource   DOCUMENT ME!
     * @param  oldPosition   DOCUMENT ME!
     * @param  newPosition   DOCUMENT ME!
     * @param  oldDirection  DOCUMENT ME!
     * @param  newDirection  DOCUMENT ME!
     */
    public CameraChangedEvent(final Object eventSource,
            final Geometry oldPosition,
            final Geometry newPosition,
            final Vector3d oldDirection,
            final Vector3d newDirection) {
        super(eventSource);

        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.oldDirection = oldDirection;
        this.newDirection = newDirection;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3d getNewDirection() {
        return newDirection;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getNewPosition() {
        return newPosition;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3d getOldDirection() {
        return oldDirection;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getOldPosition() {
        return oldPosition;
    }

    @Override
    public String toString() {
        return super.toString() + " ["
                    + "old Pos: " + oldPosition + " | "
                    + "new Pos: " + newPosition + " | "
                    + "old Dir: " + oldDirection + " | "
                    + "new Dir: " + newDirection + " ] ";
    }
}
