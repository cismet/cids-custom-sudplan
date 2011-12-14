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

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;
import de.cismet.cids.custom.sudplan.converter.WuppertalTimeseriesConverter;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Properties;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bfriedrich
 */
public class TimeSeriesTransmitterTest 
{
    private TimeseriesTransmitter transmitter;
    
    private static final String TARGET_FILE = "timeseries_transmitter_test_file";
    
    @BeforeClass
    public static void setUpClass() throws Exception 
    {
        final Properties p = new Properties();
        p.put("log4j.appender.Remote", "org.apache.log4j.net.SocketAppender");
        p.put("log4j.appender.Remote.remoteHost", "localhost");
        p.put("log4j.appender.Remote.port", "4445");
        p.put("log4j.appender.Remote.locationInfo", "true");
        p.put("log4j.rootLogger", "ALL,Remote");
        org.apache.log4j.PropertyConfigurator.configure(p);
    }
    
    @Before
    public void setUp()
    {
        this.transmitter = TimeseriesTransmitter.getInstance();
    }
        
    
    @Test
    public void testPutWithWuppertalData() throws Exception
    {
        final WuppertalTimeseriesConverter converter = new WuppertalTimeseriesConverter();
        final InputStream in = this.getClass().getResourceAsStream("/de/cismet/cids/custom/sudplan/converter/buchenhofen-pluvio_n_k_o_60-10.csv");
      
        assertNotNull(in);
        
        final TimeSeries ts = converter.convertForward(in);
        this.testPut(ts);
    }
    
    @Test
    public void testPut() throws Exception
    {
       this.testPut(TimeSeriesTestUtil.createTestTimeSeries());
    }
    
    
    public void testPut(final TimeSeries ts) throws Exception
    {
        final URL url = new URL(TimeSeriesTestUtil.DAV_HOST + TARGET_FILE);
        assertTrue(this.transmitter.put(url, ts, TimeSeriesTestUtil.CREDS).get());
        
        final HttpClient client = TimeSeriesTestUtil.createHttpClient();
        GetMethod get = new GetMethod(TimeSeriesTestUtil.DAV_HOST + TARGET_FILE);
        
        try {
            client.executeMethod(get);
            
            byte[]     tsRep      = get.getResponseBody();
            TimeSeries receivedTs = TimeSeriesSerializer.deserializeTimeSeries(tsRep);
            TimeSeriesTestUtil.compareTimeSeries(ts, receivedTs);
            
            receivedTs = TimeSeriesSerializer.deserializeTimeSeries(get.getResponseBodyAsStream());
            TimeSeriesTestUtil.compareTimeSeries(ts, receivedTs);
        } catch (final Exception ex) {
            get.abort();
            throw new TimeseriesRetrieverException(get.getStatusText(), ex);
        } 
        finally 
        {
            get.releaseConnection();
        }
    }
    

    
    
    @After
    public void tearDown() throws Exception
    {
        TimeSeriesTestUtil.removeRemoteFile(TARGET_FILE);
    }
}
