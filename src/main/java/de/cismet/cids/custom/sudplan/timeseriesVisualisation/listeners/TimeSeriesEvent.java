/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import java.awt.AWTEvent;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;

/**
 * this Event class is mentioned to represent all further until now not specified events that someone could interest.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesEvent extends AWTEvent {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID if Zoom was enabled. */
    public static int ZOOM_ENABLED = AWTEvent.RESERVED_ID_MAX + 1;
    /** Event ID if Zoom was disabled. */
    public static int ZOOM_DISABLED = AWTEvent.RESERVED_ID_MAX + 2;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesEvent object.
     *
     * @param  source  the <code>TimeSeriesVisualisation</code> that fired the event
     * @param  id      the event type
     */
    public TimeSeriesEvent(final TimeSeriesVisualisation source, final int id) {
        super(source, id);
    }
}
