/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import java.awt.AWTEvent;

/**
 * Event that contains all needed information about the changed set of <code>TimeSeries</code>. Used as parameter in
 * listener method <code>timeSeriesListChanged()</code> in <code>TimeSeriesListChangedListener</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesListChangedEvent extends AWTEvent {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID if an <code>TimeSeries</code> was added. */
    public static final int TIME_SERIES_REMOVED = AWTEvent.RESERVED_ID_MAX + 1;
    /** Event ID if an <code>TimeSeries</code> was removed. */
    public static final int TIME_SERIES_ADDED = AWTEvent.RESERVED_ID_MAX + 2;
    /** Event ID if the set of <code>TimeSeriesOperation</code> were cleared. */
    public static final int TIME_SERIES_CLEARED = AWTEvent.RESERVED_ID_MAX + 3;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesListChangedEvent object.
     *
     * @param  ts  If the event id is <code>TIME_SERIES_CLEARED</code> this represents the <code>
     *             TimeSeriesVisualisation</code> that fired the event.<br/>
     *             Else the affected <code>TimeSeries</code>
     * @param  id  the event type
     */
    public TimeSeriesListChangedEvent(final Object ts, final int id) {
        super(ts, id);
    }
}
