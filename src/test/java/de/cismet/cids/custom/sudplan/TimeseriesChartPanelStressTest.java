/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.sudplan.sosclient.SOSClientHandler;
import at.ac.ait.enviro.sudplan.util.EnvelopeQueryParameter;
import at.ac.ait.enviro.tsapi.handler.DataHandler.Access;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import junit.framework.Assert;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.openide.util.Exceptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.MalformedURLException;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import javax.swing.JFrame;
import org.junit.Ignore;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
@Ignore
public class TimeseriesChartPanelStressTest {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesChartPanelStressTest.class);
    private static Set<Datapoint> dps;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SOSTest object.
     */
    public TimeseriesChartPanelStressTest() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        TimeSeriesTestUtil.initLogger();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     */
    @Before
    public void setUp() {
    }

    /**
     * DOCUMENT ME!
     */
    @After
    public void tearDown() {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Test
    public void retriveTSForSpecificPoint() throws IOException {
        
        final SOSClientHandler handler = new SOSClientHandler();

        try {
            handler.getConnector().connect("http://enviro3.ait.ac.at:8080");
            handler.open();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
         
        
        /*
         * DP: SOSDataPoint {
         * ts:procedure           = urn:ogc:object:echam5a1b3:prec:3600s, 
         * ts:feature_of_interest = urn:SMHI:feature:Europe, 
         * ts:observed_property   = urn:ogc:def:property:OGC:prec, 
         * ts:offering            = climate_echam5a1b3_prec_1h}
         */
        
        
        //dav:http://sudplan.cismet.de/tsDav/buchenhofen-pluvio_n_k_o_60-10_short.csv?ts:observed_property=urn:ogc:def:property:OGC:prec
        
        final Properties filter = new Properties();
//        filter.put("ts:procedure" ,          "urn:ogc:object:echam5a1b3:prec:86400s"); // 1 day precision
//        filter.put("ts:feature_of_interest", "urn:SMHI:feature:Europe");
//        filter.put("ts:observed_property",   "urn:ogc:def:property:OGC:prec");
//        filter.put("ts:offering",            "climate_echam5a1b3_prec_1h");
        
//        LOG.info("CAPABILITIES: " + handler.getCapabilities());
        
        
        dps = handler.getDatapoints(filter, Access.DONT_CARE);
        
        final Iterator<Datapoint> it = dps.iterator();

        while (it.hasNext()) {
   
            final Datapoint dp = it.next();
            LOG.info("starting timeseries retrieval for Offering: " + dp.toString());
            LOG.info("Filter: " + dp.getFilter().toString());
            final Map<String, Object> props = dp.getProperties();
            final Envelope env = (Envelope)props.get("ts:geometry");

            LOG.info("DP: " + dp);
            LOG.info("DP PROPS: " + props);
            LOG.info("ENVELOPE: " + env);
            
            
            final double maxX = env.getMaxX();
            final double maxY = env.getMaxY();
            final double minX = env.getMinX();
            final double minY = env.getMinY();
            
            
            
            
            for (int i = 1; i < 21; i++) 
            {
                /*
                 * calculate a random point
                 */
                final Random r = new Random();
                final double x = (r.nextDouble() * (maxX - minX)) + minX;
                final double y = (r.nextDouble() * (maxY - minY)) + minY;

                Assert.assertTrue(minX <= x);
                Assert.assertTrue(x <= maxX);
                Assert.assertTrue(minY <= y);
                Assert.assertTrue(y <= maxY);
                /*
                 * calculate a random time interval
                 */
                LOG.info("min date: " + (Date)props.get("ts:available_data_min"));
                LOG.info("max date: " + (Date)props.get("ts:available_data_max"));
                
                final EnvelopeQueryParameter point = new EnvelopeQueryParameter();
                point.setEnvelope(new Envelope(new Coordinate(x, y)));
   
                TimeSeries ts = null;
                try {
                    /*
                     * TimeSeriesRetrieval for specific interval and specific point
                     */
//                     final TimeInterval interval = new TimeInterval( TimeInterval.Openness.OPEN,
//                                                                     new TimeStamp(minDate.getTime()),
//                                                                     new TimeStamp(maxDate.getTime()),
//                                                                     TimeInterval.Openness.OPEN);
                    
                    long memUsage = Runtime.getRuntime().freeMemory();
                    ts = dp.getTimeSeries(new TimeInterval(TimeInterval.Openness.OPEN, TimeStamp.NEGATIVE_INFINITY, TimeStamp.POSITIVE_INFINITY, TimeInterval.Openness.OPEN), point);
                    memUsage = memUsage - Runtime.getRuntime().freeMemory();
                    LOG.info("Main Memory Usage (in bytes): " + memUsage);
                    
                    
                    
                                          final String url = "http://enviro3.ait.ac.at:8080" + 
                              "?ts:procedure="           + dp.getFilter().get("ts:procedure") + 
                              "&ts:observed_property="   + dp.getFilter().get("ts:observed_property") + 
                              "&ts:feature_of_interest=" + dp.getFilter().get("ts:feature_of_interest=") + 
                              "&ts:offering="            + dp.getFilter().get("ts:offering") +
                              "&ts:geometry="            + dp.getProperties().get("ts:geometry") + 
                              "&ts:available_data_max=Fri Dec 31 23:00:00 CET 2100" +
                              "&ts:available_data_min=Fri Jan 01 00:00:00 CET 1960";
                       
                                          
                                          
                              LOG.info("URL: " + url);
                    
                              final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(url);
                              final TimeseriesChartPanel panel = new TimeseriesChartPanel(config);
                              
                              final JFrame frame = new JFrame();
                              frame.add(panel);
//                              frame.pack();
                              frame.setVisible(true);
                              
                    
                    if(ts == null)
                    {
                        LOG.warn("UPS...no data has been retrieved");
                    }
                    else
                    {
                       final TimeStamp[] timestamps = ts.getTimeStampsArray();
                       // it seems that number of timestamps corresponds to number of records
                       LOG.info("Number of timestamps: " + timestamps.length);
                       
                       
//                      final String url = "http://enviro3.ait.ac.at:8080" + 
//                              "?ts:procedure="           + dp.getProperties().get("ts:procedure") + 
//                              "&ts:observed_property="   + dp.getProperties().get("ts:observed_property") + 
//                              "&ts:feature_of_interest=" + dp.getProperties().get("ts:feature_of_interest=") + 
//                              "&ts:offering="            + dp.getProperties().get("ts:offering") +
//                              "&ts:geometry="            + dp.getProperties().get("ts:geometry") + 
//                              "&ts:available_data_max=Fri Dec 31 23:00:00 CET 2100" +
//                              "&ts:available_data_min=Fri Jan 01 00:00:00 CET 1960";
//                       
//                              LOG.fatal("URL: " + url);
                              
                       
                       
                       
                       
                       

                       
//                        final String url = "tstb:SOS-SUDPLAN-Dummy@http://dummy.org?"
//                + "ts:procedure=urn:ogc:object:LINZ:rain:1&"
//                + "ts:feature_of_interest=urn:MyOrg:feature:linz&"
//                + "ts:observed_property=urn:ogc:def:property:OGC:rain&"
//                + "ts:offering=Station_3202_10min";
//                       
//                                TimeseriesRetrieverConfig.fromUrl(url)
//                        TimeseriesRetrieverConfig result = TimeseriesRetrieverConfig.fromTSTBUrl(url);
//                       
//                       final TimeseriesRetrieverConfig config = new TimeseriesRetrieverConfig(null, null, null, null, null, null, null, null, new TimeInterval(TimeInterval.Openness.OPEN, TimeStamp.NEGATIVE_INFINITY, TimeStamp.POSITIVE_INFINITY, TimeInterval.Openness.OPEN))
//                       
//                       
//                       TimeseriesChartPanel panel = new TimeseriesChartPanel
//                       panel.add
//                               
//                       int numData = 0;
//                       long dataSze = 0;
//                  
//                            final Object valueKeyObject = ts.getTSProperty(TimeSeries.VALUE_KEYS);
//                            final String valueKey;
//                            if (valueKeyObject instanceof String) {
//                                valueKey = (String)valueKeyObject;
//                                numData++;
//                            } 
//                            else if (valueKeyObject instanceof String[]) 
//                            {
//                                final String[] valueKeys = (String[])valueKeyObject;
//                                valueKey                 = valueKeys[0];
//                                numData += valueKeys.length;
//                            }
//                            
//                            
//                            for(int j = 0; j < timestamps.length; j++)
//                            {
//                                 final Float value = (Float)ts.getValue(timestamps[j], valueKey);
//                                 value.
//                            }
//                       
//                       LOG.info("Number of records: " + numData);
                       
                       
                    }
                } 
                catch (Exception e) 
                {
                    LOG.error(" - retrieval failed", e);
                    continue;
                } 
            }
        }
    }
    
    
//      private TimeSeriesDatasetAdapter createJFreeDataset(final TimeSeries ts) {
//        final TimeStamp[] timeStamps = ts.getTimeStampsArray();
//        String name = (String)ts.getTSProperty(TimeSeries.OBSERVEDPROPERTY);
//        if (name == null) {
//            LOG.error("Could not relate the time series with an name");     // NOI18N
//            name = "notFound";                                              // NOI18N
//        }
//        final Object valueKeyObject = ts.getTSProperty(TimeSeries.VALUE_KEYS);
//        final String valueKey;
//        if (valueKeyObject instanceof String) {
//            valueKey = (String)valueKeyObject;
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("found valuekey: " + valueKey);                   // NOI18N
//            }
//        } else if (valueKeyObject instanceof String[]) {
//            final String[] valueKeys = (String[])valueKeyObject;
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("found multiple valuekeys: " + valueKeys.length); // NOI18N
//            }
//
//            if (valueKeys.length == 1) {
//                valueKey = valueKeys[0];
//            } else {
//                throw new IllegalStateException("found too many valuekeys");              // NOI18N
//            }
//        } else {
//            throw new IllegalStateException("unknown value key type: " + valueKeyObject); // NOI18N
//        }
//        // TODO problem was für eine zeitliche Auflösung soll für die Jfreechart zeitreihe genommen werden...
//        // TODO Problem was für ein datentyp steckt hinter der zeitreihe...
//        final org.jfree.data.time.TimeSeries data = new org.jfree.data.time.TimeSeries(name); // NOI18N
//        data.setRangeDescription(SMSUtils.unitFromTimeseries(ts).getLocalisedName());
//        for (final TimeStamp stamp : timeStamps) {
//            final Float value = (Float)ts.getValue(stamp, valueKey);
//            data.add(new Millisecond(stamp.asDate()), value);
//        }
//
//        final TimeSeriesDatasetAdapter dataset = new TimeSeriesDatasetAdapter(data);
//        Geometry g = null;
//        if (ts.getTSProperty(TimeSeries.GEOMETRY) instanceof Envelope) {
//            final Envelope e = (Envelope)ts.getTSProperty(TimeSeries.GEOMETRY);
//            final GeometryFactory gf = new GeometryFactory();
//            g = gf.createPoint(new Coordinate(e.getMinX(), e.getMinY()));
//        } else {
//            g = (Geometry)ts.getTSProperty(TimeSeries.GEOMETRY);
//        }
//
//        dataset.setGeometry(g);
//        dataset.setOriginTimeSeries(ts);
//
//        return dataset;
//    }

    
    
}
