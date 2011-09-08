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

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.AbstractTimeSeriesOperation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

/**
 * Operation that calculates the normalised difference of two <code>TimeSeries</code> objects.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
//TODO reference zu TimeSeriesVisualisation wird gebraucht um Frame zu ermitteln... geht das auch anders?
public class NormalizedDifferenceOperation extends AbstractTimeSeriesOperation { 

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NormalizedDifferenceOperation object.
     *
     * @param  tsv  the <code>TimeSeriesVisualisation</code> that this operation relies to.
     */
    public NormalizedDifferenceOperation(final TimeSeriesVisualisation tsv) {
        super(2,NbBundle.getMessage(
                NormalizedDifferenceOperation.class,
                "NormalizedDifferenceOperation.opName"), // NOI18N
            null,
            tsv);
        this.putValue(
            Action.LONG_DESCRIPTION,
            NbBundle.getMessage(
                NormalizedDifferenceOperation.class,
                "NormalizedDifferenceOperation.longDesc")); // NOI18N
        this.putValue(
            TimeSeriesOperation.OP_EXPRESSION,
            NbBundle.getMessage(
                NormalizedDifferenceOperation.class,
                "NormalizedDifferenceOperation.opExpr")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<TimeSeries> calculate(final TimeSeries[] params) {
        if (params.length != 2) {
            throw new IllegalStateException("To many parameters for operation Difference"); // NOI18N
        }
        final ArrayList<TimeSeries> resultList = new ArrayList<TimeSeries>();
        final TimeSeries paramA = params[0];
        final TimeSeries paramB = params[1];

        final TimeSeries result = new TimeSeriesImpl();
        // set the properties for the result TimeSeries
        for (final String key : paramA.getTSKeys()) {
            if (key.equals(TimeSeries.UNIT)) {
                if (!paramA.getTSProperty(TimeSeries.UNIT).equals(paramB.getTSProperty(TimeSeries.UNIT))) {
                    result.setTSProperty(
                        TimeSeries.UNIT,
                        paramA.getTSProperty(TimeSeries.UNIT + "/" + paramB.getTSProperty(TimeSeries.UNIT))); // NOI18N
                } else {
                    result.setTSProperty(key, paramA.getTSProperty(key));
                }
            } else {
                result.setTSProperty(key, paramA.getTSProperty(key));
            }
        }

        // copy timestamps to ensure that the values of the param time series are not changes
        final TimeStamp[] clonedStamps = paramA.getTimeStampsArray().clone();

        // check if both params have the same valueKey
        final Object paramAKeyObj = params[0].getTSProperty(TimeSeries.VALUE_KEYS);
        String paramAKey = "";
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

        // do the subtraction
        for (final TimeStamp ts : clonedStamps) {
            final Float a = (Float)paramA.getValue(ts, paramAKey);
            final Float b = (Float)paramB.getValue(ts, paramAKey);

            if ((a == null) || (b == null)) {
                throw new IllegalStateException("parameter time series does not have the same time stamps"); // NOI18N
            }
            final Float opVal = (a - b) / a;
            result.setValue(ts, paramAKey, opVal);
        }
        resultList.add(result);
        return resultList;
    }
}
