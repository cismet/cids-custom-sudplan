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
 * Represents a Listener that is notified whenever the set of managed <code>TimeSeriesOperations</code> within a <code>
 * TimeSeriesVisualisation</code> has changed.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesOperationListChangedListener extends EventListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * called whenever the set of managed <code>TimeSeriesOperation</code> has changed.
     *
     * @param  evt  the <code>TimeSeriesOperationChangedEvent</code>
     */
    void timeSeriesOperationChanged(TimeSeriesOperationChangedEvent evt);
}
