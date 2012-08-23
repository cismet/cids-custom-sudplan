package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import java.net.URL;
import java.util.concurrent.Future;
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
       
       // if test crashes or is canceled, the test file put to the DAV might not be deleted by tearDownClass()
       TimeSeriesTestUtil.removeRemoteFile(TARGET_FILE);
        
       testTs = TimeSeriesTestUtil.createTestTimeSeries(); 
        
       final URL url = new URL(TimeSeriesRemoteHelper.DAV_HOST + TARGET_FILE);
       final TimeseriesTransmitter transmitter = TimeseriesTransmitter.getInstance();
       transmitter.put(url, testTs, TimeSeriesRemoteHelper.DAV_CREDS);
       
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
