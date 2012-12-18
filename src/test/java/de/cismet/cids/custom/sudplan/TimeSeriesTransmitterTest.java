package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
    public void testPut() throws Exception
    {
       this.testPut(TimeSeriesTestUtil.createTestTimeSeries());
    }
    
    
    public void testPut(final TimeSeries ts) throws Exception
    {
        final URL url = new URL(TimeSeriesRemoteHelper.DAV_HOST + TARGET_FILE);
        assertTrue(this.transmitter.put(url, ts, TimeSeriesRemoteHelper.DAV_CREDS).get());
        
        final HttpClient client = TimeSeriesTestUtil.createHttpClient();
        GetMethod get = new GetMethod(TimeSeriesRemoteHelper.DAV_HOST + TARGET_FILE);
        
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
