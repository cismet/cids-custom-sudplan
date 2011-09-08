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

import javax.swing.Action;

/**
 * Represents a operation on <code>TimeSeries</code> objects. a TimeSeriesOperation always returns an Array of
 * calculated <code>TimeSeries</code> If you want to implement your own TimeSeriesOperation, create a subclass of <code>
 * AbstractTimeSeriesOperation</code> and put your operation code in the method <code>calculate</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesOperation extends Action {

    //~ Instance fields --------------------------------------------------------

    /** Property key for the operation expression. */
    String OP_EXPRESSION = "expr";

    //~ Methods ----------------------------------------------------------------

    /**
     * adds a <code>TimeSeriesOperationResultListener.</code>
     *
     * @param  l  a <code>TimeSeriesOperationsResultListener</code>
     */
    void addTimeSeriesOperationResultListener(TimeSeriesOperationResultListener l);
    /**
     * removes a <code>TimeSeriesOperationResultListener.</code>
     *
     * @param  l  a <code>TimeSeriesOperationResultListener</code>
     */
    void removeTimeSeriesOperationResultListener(TimeSeriesOperationResultListener l);

    /**
     * determines the set of candidate parameters. In case of a <code>SimpleTimeSeriesVisualisation>/code> this is the
     * set of selected TimeSeries. The set is used to determine if the operation can be executed or not.</code>
     *
     * @param  c  a Collection of <code>TimeSeries</code>
     */
    void setavailableTimeSeriesList(Collection<TimeSeries> c);

    /**
     * determines the parameters that are used to determine the result of the operation. is called from <code>
     * DefaultParamOrderUI</code> after the parameters were selected from the available time series
     *
     * @param  paramArray  DOCUMENT ME!
     */
    void setParameters(TimeSeries[] paramArray);
}
