/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import java.util.EventObject;

/**
 * Event that contains all needed information about the changed set of <code>TimeSeries</code>. Used as parameter in
 * listener method <code>timeSeriesListChanged()</code> in <code>TimeSeriesListChangedListener</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesListChangedEvent extends EventObject {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID if an <code>TimeSeries</code> was added. */
    public static final ID TIME_SERIES_REMOVED = ID.REMOVE;
    /** Event ID if an <code>TimeSeries</code> was removed. */
    public static final ID TIME_SERIES_ADDED = ID.ADD;
    /** Event ID if the set of <code>TimeSeriesOperation</code> were cleared. */
    public static final ID TIME_SERIES_CLEARED = ID.CLEAR;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected enum ID {

        //~ Enum constants -----------------------------------------------------

        REMOVE, ADD, CLEAR
    }

    //~ Instance fields --------------------------------------------------------

    private ID eventID;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesListChangedEvent object.
     *
     * @param  ts  If the event id is <code>TIME_SERIES_CLEARED</code> this represents the <code>
     *             TimeSeriesVisualisation</code> that fired the event.<br/>
     *             Else the affected <code>TimeSeries</code>
     * @param  id  the event type
     */
    public TimeSeriesListChangedEvent(final Object ts, final ID id) {
        super(ts);
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
