/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesEventListener;

/**
 * Interface for generic event notification. The event is determined by the event type.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesEventNotification {

    //~ Methods ----------------------------------------------------------------

    /**
     * adds a TimeSeriesEventListener.
     *
     * @param  l  a TimeSeriesEventListener to add
     */
    void addTimeSeriesEventListener(TimeSeriesEventListener l);
    /**
     * removes a TimeSeriesEventListener.
     *
     * @param  l  a TimeSeriesEventListener to remove
     */
    void removeTimeSeriesEventListener(TimeSeriesEventListener l);
}
