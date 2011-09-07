/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.awt.AWTEvent;

import java.util.Collection;

/**
 * Event that contains all needed information about the selection. Used as parameter in listener method <code>
 * selectionChanged()</code> in <code>TimeSeriesSelectionListener</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesSelectionEvent extends AWTEvent {

    //~ Static fields/initializers ---------------------------------------------

    /** Event ID for events as result of a selection. */
    public static final int TS_SELECTED = AWTEvent.RESERVED_ID_MAX + 1;
    /** Event ID for eventes as result of a deselection. */
    public static final int TS_DESELECTED = AWTEvent.RESERVED_ID_MAX + 2;

    //~ Instance fields --------------------------------------------------------

    private Collection<TimeSeries> selectedTs;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesSelectionEvent object.
     *
     * @param  source      the <code>TimeSeriesVisualisatio</code> that generates the event
     * @param  id          the selection type.
     * @param  selectedTs  a Collection of all current selected <code>TimeSeries</code>. Empty if no <code>
     *                     TimeSeries</code> is selected.
     */
    public TimeSeriesSelectionEvent(final Object source, final int id, final Collection<TimeSeries> selectedTs) {
        super(source, id);
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
}
