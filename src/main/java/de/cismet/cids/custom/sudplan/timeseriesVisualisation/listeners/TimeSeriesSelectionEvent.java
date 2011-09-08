/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.util.Collection;
import java.util.EventObject;

/**
 * Event that contains all needed information about the selection. Used as parameter in listener method <code>
 * selectionChanged()</code> in <code>TimeSeriesSelectionListener</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesSelectionEvent extends EventObject {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID for events as result of a selection. */
    public static final ID TS_SELECTED = ID.SELECTED;
    /** Event ID for events as result of a deselection. */
    public static final ID TS_DESELECTED = ID.DESELECTED;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected static enum ID {

        //~ Enum constants -----------------------------------------------------

        SELECTED, DESELECTED
    }

    //~ Instance fields --------------------------------------------------------

    private Collection<TimeSeries> selectedTs;
    private final ID eventID;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesSelectionEvent object.
     *
     * @param  source      the <code>TimeSeriesVisualisatio</code> that generates the event
     * @param  id          the selection type.
     * @param  selectedTs  a Collection of all current selected <code>TimeSeries</code>. Empty if no <code>
     *                     TimeSeries</code> is selected.
     */
    public TimeSeriesSelectionEvent(final Object source, final ID id, final Collection<TimeSeries> selectedTs) {
        super(source);
        eventID = id;
        this.selectedTs = selectedTs;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  Collection of all selected <code>TimeSeries</code>. Empty Collection if no <code>TimeSeries</code> is
     *          selected
     */
    public Collection<TimeSeries> getSelectedTs() {
        return selectedTs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ID getID() {
        return eventID;
    }
}
