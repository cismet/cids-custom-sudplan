/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public final class TimeSeriesAggregator {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesAggregator object.
     */
    private TimeSeriesAggregator() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   ts  TimeSeries instance to be aggregated by day
     *
     * @return  TimeSeries instance holding aggregated values
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public static TimeSeries aggregateByDay(final TimeSeries ts) {
        if (ts == null) {
            throw new NullPointerException("Given TimeSeries instance must not be null");
        }

        final TimeSeriesImpl newTs = new TimeSeriesImpl();

        final Set<String> tsKeys = ts.getTSKeys();
        for (final String tsKey : tsKeys) {
            newTs.setTSProperty(tsKey, ts.getTSProperty(tsKey));
        }

        // Note: According to the javadoc documentation,the sequence of the TimeStamps is guaranteed to
        // be in incremental order.
        final TimeStamp[] timestamps = ts.getTimeStampsArray();

        if (timestamps.length == 0) {
            return newTs;
        }

        final Calendar cal = Calendar.getInstance();

        cal.setTime(timestamps[0].asDate());
        int startDay = cal.get(Calendar.DAY_OF_YEAR);
        int startDayIndex = 0;
        float aggregatedValue = (Float)ts.getValue(timestamps[0], PropertyNames.VALUE);

        Date date;
        int currentDayOfYear;

        final int timeStampsLength = timestamps.length;
        for (int i = 1; i < timeStampsLength; i++) {
            cal.setTime(timestamps[i].asDate());

            currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);

            if (currentDayOfYear == startDay) {
                aggregatedValue += (Float)ts.getValue(timestamps[i], PropertyNames.VALUE);
            } else {
                // set time of the aggregated timestamp to 00:00:00
                date = timestamps[startDayIndex].asDate();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 0);

                // set aggregated value in new TimeSeries instance
                newTs.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, aggregatedValue);

                // begin of new aggregation group
                cal.setTime(timestamps[i].asDate());
                startDay = cal.get(Calendar.DAY_OF_YEAR);
                startDayIndex = i;
                aggregatedValue = (Float)ts.getValue(timestamps[0], PropertyNames.VALUE);
            }
        }

        // handling of the last aggregation group
        date = timestamps[startDayIndex].asDate();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        newTs.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, aggregatedValue);

        return newTs;
    }
}
