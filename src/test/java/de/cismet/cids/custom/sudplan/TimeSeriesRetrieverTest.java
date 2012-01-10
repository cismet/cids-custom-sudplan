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
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import java.net.URL;
import java.util.concurrent.Future;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author bfriedrich
 */
public class TimeSeriesRetrieverTest 
{
    private TimeseriesRetrieverConfig config;
    
    private static final String TARGET_FILE = "timeseries_retriever_test_file";
     
    private static final String URL = "dav:" +
                                      TimeSeriesRemoteHelper.DAV_HOST
                                      + '?'
                                      + TimeSeries.OBSERVEDPROPERTY
                                      + "=urn:ogc:def:property:OGC:prec&" // NOI18N
                                      + TimeSeries.PROCEDURE
                                      + "=urn:ogc:object:"
                                      + TARGET_FILE
                                      + ":prec:unknown&"                  // NOI18N
                                      + TimeSeries.OFFERING
                                      + '='
                                      + TARGET_FILE;          // NOI18N
    
    
    private static TimeSeries testTs;
    
    @BeforeClass
    public static void setUpClass() throws Exception
    {
       TimeSeriesTestUtil.initLogger(); 
        
       testTs = TimeSeriesTestUtil.createTestTimeSeries(); 
        
       final URL url = new URL(TimeSeriesTestUtil.DAV_HOST + TARGET_FILE);
       final TimeseriesTransmitter transmitter = TimeseriesTransmitter.getInstance();
       transmitter.put(url, testTs, TimeSeriesTestUtil.CREDS);
       
       // data which has been transferred to the server might not be immediately available
       Thread.sleep(1000l);
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception
    {
        TimeSeriesTestUtil.removeRemoteFile(TARGET_FILE);
    }
    
    @Before
    public void setUp() throws Exception
    {
        this.config = TimeseriesRetrieverConfig.fromDavUrl(URL);
    }
    
    @Test
    public void testWithTimeSeriesSerializerConververter() throws Exception
    {
        final TimeseriesConverter converter = TimeSeriesSerializer.getInstance();
        
        final TimeseriesRetriever retriever = TimeseriesRetriever.getInstance();
        final Future<TimeSeries>  result    = retriever.retrieve(this.config, converter);
        
        TimeSeriesTestUtil.compareTimeSeries(testTs, result.get());
    }
}
