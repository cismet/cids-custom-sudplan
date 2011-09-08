/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import java.awt.AWTEvent;

import java.util.EventObject;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;

/**
 * this Event class is mentioned to represent all further until now not specified events that someone could interest.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesEvent extends EventObject {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID if Zoom was enabled. */
    public static ID ZOOM_ENABLED = ID.ENABLE;
    /** Event ID if Zoom was disabled. */
    public static ID ZOOM_DISABLED = ID.DISABLE;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected enum ID {

        //~ Enum constants -----------------------------------------------------

        ENABLE, DISABLE
    }

    //~ Instance fields --------------------------------------------------------

    private ID eventID;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesEvent object.
     *
     * @param  source  the <code>TimeSeriesVisualisation</code> that fired the event
     * @param  id      the event type
     */
    public TimeSeriesEvent(final TimeSeriesVisualisation source, final ID id) {
        super(source);
        eventID = id;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ID getID() {
        return eventID;
    }
}
