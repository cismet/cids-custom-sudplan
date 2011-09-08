/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import java.util.EventObject;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

/**
 * Event that contains all needed information about the changed set of <code>TimeSeriesOperation</code>. Used as
 * parameter in listener method <code>timeSeriesOperationChanged()</code> in <code>
 * TimeSeriesOperationListChangedListener</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesOperationChangedEvent extends EventObject {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID if an <code>TimeSeriesOperation</code> was added. */
    public static final ID OPERATION_ADD = ID.ADD;
    /** Event ID if an <code>TimeSeriesOperation</code> was removed. */
    public static final ID OPERATION_REMOVE = ID.REMOVE;
    /** Event ID if the set of <code>TimeSeriesOperation</code> were cleared. */
    public static final ID OPERATIONS_CLEARED = ID.CLEAR;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected static enum ID {

        //~ Enum constants -----------------------------------------------------

        ADD, REMOVE, CLEAR
    }

    //~ Instance fields --------------------------------------------------------

    private ID eventID;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesOperationChangedEvent object.
     *
     * @param  ts  the affected <code>TimeSeriesOperation</code>
     * @param  id  ID the event type
     */
    public TimeSeriesOperationChangedEvent(final TimeSeriesOperation ts, final ID id) {
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
