/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.util.Collection;

/**
 * Listener that is called whenever a <code>TimeSeriesOperation</code> was executed.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesOperationResultListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * this method is called after a <code>TimeSeriesOperation</code> was executed.
     *
     * @param  result  a Collection that contains the calculated time series
     */
    void submitResult(Collection<TimeSeries> result);
}
