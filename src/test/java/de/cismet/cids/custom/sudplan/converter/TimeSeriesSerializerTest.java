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

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import de.cismet.cids.custom.sudplan.TimeSeriesTestUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bfriedrich
 */
public class TimeSeriesSerializerTest 
{
    @Test(expected=NullPointerException.class)
    public void testSerializationWithNullParam()
    {
        TimeSeriesSerializer.serializeTimeSeries(null);
    }
    
    @Test
    public void testSerialization()
    {
        final TimeSeries ts    = TimeSeriesTestUtil.createTestTimeSeries();
        final byte[]     tsRep = TimeSeriesSerializer.serializeTimeSeries(ts);
        
        assertNotNull(tsRep);
        assertTrue(tsRep.length > 0);
    }
    
    @Test(expected=NullPointerException.class)
    public void testDeserializationWithByteArrayNullParam()
    {
        TimeSeriesSerializer.deserializeTimeSeries((byte[])null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testDeserializationWithInputStreamNullParam()
    {
        TimeSeriesSerializer.deserializeTimeSeries((InputStream)null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testSerializationToStreamWithNullParam()
    {
        TimeSeriesSerializer.serializeTimeSeriesToInputStream(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSerializationWithEmtpyByteArray() throws Exception
    {
        TimeSeriesSerializer.deserializeTimeSeries(new byte[0]);
    }
    
    //TODO better exception type
    @Test(expected=RuntimeException.class)
    public void testSerializationWithEmtpyInputStream() throws Exception
    {
        TimeSeriesSerializer.deserializeTimeSeries(new ByteArrayInputStream(new byte[0]));
    }
    
    
    
    private void testSerializationDeserialization(final TimeSeries ts) throws Exception
    {
        final byte[] tsRep = TimeSeriesSerializer.serializeTimeSeries(ts);

        final TimeSeries newTs = TimeSeriesSerializer.deserializeTimeSeries(tsRep);
        assertNotNull(newTs);

        
        TimeSeriesTestUtil.compareTimeSeries(ts, newTs);
    }
    
    
    @Test
    public void testSerializationDeserialization() throws Exception
    {
        final TimeSeries ts  = TimeSeriesTestUtil.createTestTimeSeries();
        this.testSerializationDeserialization(ts);
    }
    
    @Test
    public void testSerializationDeserializationFromInputStream() throws Exception
    {
        final TimeSeries  ts  = TimeSeriesTestUtil.createTestTimeSeries();
        final InputStream tsRepStream = TimeSeriesSerializer.serializeTimeSeriesToInputStream(ts);
        final TimeSeries  newTs       = TimeSeriesSerializer.deserializeTimeSeries(tsRepStream);

        assertNotNull(newTs);

        TimeSeriesTestUtil.compareTimeSeries(ts, newTs);
    }

    
    
    @Test
    public void testSerializationDeserializationWithWuppertalData() throws Exception
    {
        final WuppertalTimeseriesConverter converter = new WuppertalTimeseriesConverter();
        final InputStream in = this.getClass().getResourceAsStream("buchenhofen-pluvio_n_k_o_60-10.csv");
      
        assertNotNull(in);
        
        final TimeSeries ts = converter.convertForward(in);
        this.testSerializationDeserialization(ts);
    }
}
