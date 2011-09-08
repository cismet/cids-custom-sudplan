/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import org.openide.util.NbBundle;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.Icon;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.AbstractTimeSeriesOperation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

/**
 * Operation that calculates the difference of two <code>TimeSeries</code> objects.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class DifferenceOperation extends AbstractTimeSeriesOperation { 

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DifferenceOperation object.
     *
     * @param  tsv  the <code>TimeSeriesVisualisation</code> this operation relies to
     */
    public DifferenceOperation(final TimeSeriesVisualisation tsv) {
        this(NbBundle.getMessage(
                DifferenceOperation.class,
                "DifferenceOperation.opName"), null, tsv); // NOI18N
        this.putValue(
            Action.LONG_DESCRIPTION,
            NbBundle.getMessage(
                DifferenceOperation.class,
                "DifferenceOperation.longDesc"));          // NOI18N
        this.putValue(
            TimeSeriesOperation.OP_EXPRESSION,
            NbBundle.getMessage(
                DifferenceOperation.class,
                "DifferenceOperation.opExpr"));            // NOI18N
    }

    /**
     * Creates a new DifferenceOperation object.
     *
     * @param  name  the op
     * @param  icon  the operations icon
     * @param  tsv   the <code>TimeSeriesVisualisation</code> this operation relies to
     */
    private DifferenceOperation(final String name, final Icon icon, final TimeSeriesVisualisation tsv) {
        super(2,name, icon, tsv);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<TimeSeries> calculate(final TimeSeries[] params) {
        if (params.length != 2) {
            throw new IllegalStateException("To many parameters for operation Difference"); // NOI18N
        }
        final ArrayList<TimeSeries> resultList = new ArrayList<TimeSeries>();

        final TimeSeries result = new TimeSeriesImpl();
        // copy the properties to the new created result time series
        for (final String key : params[0].getTSKeys()) {
            result.setTSProperty(key, params[0].getTSProperty(key));
        }
        final TimeSeries paramA = params[0];
        final TimeSeries paramB = params[1];

        // copy timestamps to ensure that param values arent changed
        final TimeStamp[] clonedStamps = paramA.getTimeStampsArray().clone();

        // check if both params have the same valueKey
        final Object paramAKeyObj = params[0].getTSProperty(TimeSeries.VALUE_KEYS);
        String paramAKey = ""; // NOI18N
        if (paramAKeyObj instanceof String) {
            paramAKey = (String)paramAKeyObj;
        } else {
            paramAKey = ((String[])paramAKeyObj)[0];
        }

        final Object paramBKeyObj = params[1].getTSProperty(TimeSeries.VALUE_KEYS);
        String paramBKey = ""; // NOI18N
        if (paramBKeyObj instanceof String) {
            paramBKey = (String)paramBKeyObj;
        } else {
            paramBKey = ((String[])paramBKeyObj)[0];
        }

        if ((paramAKey == null) || (paramBKey == null) || !paramAKey.equals(paramBKey)) {
            throw new IllegalStateException(
                "value key for a parameter time series is null or the value keys are not equal"); // NOI18N
        }

        // do the calculation
        for (final TimeStamp ts : clonedStamps) {
            final Float a = (Float)paramA.getValue(ts, paramAKey);
            final Float b = (Float)paramB.getValue(ts, paramAKey);

            if ((a == null) || (b == null)) {
                throw new IllegalStateException("parameter time series does not have the same time stamps"); // NOI18N
            }
            final Float opVal = a - b;
            result.setValue(ts, paramAKey, opVal);
        }
        resultList.add(result);
        return resultList;
    }
}
