/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import java.awt.AWTEvent;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

/**
 * Event that contains all needed information about the changed set of <code>TimeSeriesOperation</code>. Used as
 * parameter in listener method <code>timeSeriesOperationChanged()</code> in <code>
 * TimeSeriesOperationListChangedListener</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesOperationChangedEvent extends AWTEvent {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID if an <code>TimeSeriesOperation</code> was added. */
    public static final int OPERATION_ADD = AWTEvent.RESERVED_ID_MAX + 1;
    /** Event ID if an <code>TimeSeriesOperation</code> was removed. */
    public static final int OPERATION_REMOVE = AWTEvent.RESERVED_ID_MAX + 2;
    /** Event ID if the set of <code>TimeSeriesOperation</code> were cleared. */
    public static final int OPERATIONS_CLEARED = AWTEvent.RESERVED_ID_MAX + 3;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesOperationChangedEvent object.
     *
     * @param  ts  the affected <code>TimeSeriesOperation</code>
     * @param  id  the event type
     */
    public TimeSeriesOperationChangedEvent(final TimeSeriesOperation ts, final int id) {
        super(ts, id);
    }
}
