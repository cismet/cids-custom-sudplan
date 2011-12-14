/*
 * Copyright (C) 2011 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.sudplan.converter;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;
import java.util.Calendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bfriedrich
 */
public class TimeSeriesAggregatorTest {
    
    public TimeSeriesAggregatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test(expected=NullPointerException.class)
    public void testAggregateByDayWithNullTimeSeries()
    {
        TimeSeriesAggregator.aggregateByDay(null);
    }

    @Test
    public void testAggregateByDayWithEmtyTimeSeries()
    {
        final TimeSeries ts    = new TimeSeriesImpl(); 
        final TimeSeries newTs = TimeSeriesAggregator.aggregateByDay(ts);
        
        assertEquals(ts.getTSProperty(TimeSeries.VALUE_KEYS),             newTs.getTSProperty(TimeSeries.VALUE_KEYS));
        assertEquals(ts.getTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES), newTs.getTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES));
        assertEquals(ts.getTSProperty(TimeSeries.VALUE_TYPES),            newTs.getTSProperty(TimeSeries.VALUE_TYPES));
    }
    
    
    @Test
    public void testAggregateByDay()
    {
        final Calendar cal = Calendar.getInstance();
        
        final TimeSeries ts = new TimeSeriesImpl(); 
        ts.setTSProperty(TimeSeries.VALUE_KEYS,             new String[] { PropertyNames.VALUE });
        ts.setTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES, new String[] { Float.class.getName() });
        ts.setTSProperty(TimeSeries.VALUE_TYPES,            new String[] { TimeSeries.VALUE_TYPE_NUMBER });

        //--- 
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,  1);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,  2);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,  3);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,  4);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        //---
        
        cal.set(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.HOUR_OF_DAY,  1);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        cal.set(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.HOUR_OF_DAY,  2);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        //---

        cal.set(Calendar.DAY_OF_MONTH, 3);
        cal.set(Calendar.HOUR_OF_DAY,  1);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        //---
        
        
        final TimeSeries newTs = TimeSeriesAggregator.aggregateByDay(ts);
        
        assertEquals(ts.getTSProperty(TimeSeries.VALUE_KEYS),             newTs.getTSProperty(TimeSeries.VALUE_KEYS));
        assertEquals(ts.getTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES), newTs.getTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES));
        assertEquals(ts.getTSProperty(TimeSeries.VALUE_TYPES),            newTs.getTSProperty(TimeSeries.VALUE_TYPES));
    
        
        final TimeStamp[] timestamps = newTs.getTimeStampsArray();
        assertEquals(3, timestamps.length);
        
        TimeStamp stamp = timestamps[0];
        cal.setTime(stamp.asDate());
        assertEquals(1,   cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0,   cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0,   cal.get(Calendar.MINUTE));
        assertEquals(0,   cal.get(Calendar.MILLISECOND));
        assertEquals(40f, newTs.getValue(stamp,PropertyNames.VALUE));
        
        stamp = timestamps[1];
        cal.setTime(stamp.asDate());
        assertEquals(2,   cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0,   cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0,   cal.get(Calendar.MINUTE));
        assertEquals(0,   cal.get(Calendar.MILLISECOND));
        assertEquals(20f, newTs.getValue(stamp,PropertyNames.VALUE));
        
        stamp = timestamps[2];
        cal.setTime(stamp.asDate());
        assertEquals(3,   cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0,   cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0,   cal.get(Calendar.MINUTE));
        assertEquals(0,   cal.get(Calendar.MILLISECOND));
        assertEquals(10f, newTs.getValue(stamp,PropertyNames.VALUE));
    }
}
