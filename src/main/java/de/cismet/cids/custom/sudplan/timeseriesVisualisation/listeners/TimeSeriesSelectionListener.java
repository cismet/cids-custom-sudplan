/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import java.util.EventListener;

/**
 * Represents a Listener that is notified whenever the selection within a <code>TimeSeriesVisualisation</code> has
 * changed. The <code>TimeSeriesVisualisation</code> implements the Interface <code>
 * TimeSeriesSelectionNotification</code> if it supports selection. Use their <code>getLookup()</code> to find out if
 * its supported or not.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesSelectionListener extends EventListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * called whenever the selection of the visualised time series has changed.
     *
     * @param  evt  the TimeSeriesSelectionEvent contains a Collection of all selected <code>TimeSeries</code>.
     *              Collection is empty if no <code>TimeSeries</code> is selected.
     */
    void selectionChanged(TimeSeriesSelectionEvent evt);
}
