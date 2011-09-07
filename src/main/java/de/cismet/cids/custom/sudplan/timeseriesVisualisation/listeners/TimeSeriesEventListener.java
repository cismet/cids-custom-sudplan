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
 * A generic listener that is responsible all events that need notitification.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesEventListener extends EventListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * called whenever a <code>TimeSeriesEvent</code> occured.
     *
     * @param  evt  The TimeSeriesEvent
     */
    void timeSeriesEventOccured(TimeSeriesEvent evt);
}
