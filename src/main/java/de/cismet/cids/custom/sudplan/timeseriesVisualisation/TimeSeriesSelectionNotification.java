/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionListener;

/**
 * Offers the capability to notify a set of <code>TimeSeriesSelectionListeners</code> about selection events within a
 * <code>TimeSeriesVisualisation</code>. Use the <code>getLookup()</code> method to find out if the <code>
 * TimeSeriesVisualisation</code> implements this Interface.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesSelectionNotification {

    //~ Methods ----------------------------------------------------------------

    /**
     * adds a <code>TimeSeriesSelectionListener</code> to the set of managed Listeners.
     *
     * @param  l  the TimeSeriesSelectionListener to add
     */
    void addTimeSeriesSelectionListener(TimeSeriesSelectionListener l);
    /**
     * removes a <code>TimeSeriesSelectionListener</code> from the set of managed Listeners.
     *
     * @param  l  the TimeSeriesSelectionListener to remove
     */
    void removeTimeSeriesSelectionListener(TimeSeriesSelectionListener l);
}
