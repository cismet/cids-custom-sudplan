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
import java.util.Random;
import java.util.Set;
import org.junit.Ignore;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
@Ignore
public class SOSTest {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SOSTest.class);
    private static Set<Datapoint> dps;
    private static BufferedWriter w;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SOSTest object.
     */
    public SOSTest() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        final File resultsFile = new File(System.getProperty("user.home") + System.getProperty("file.separator")
                        + "SOSTestResults.txt");
        w = new BufferedWriter(new FileWriter(resultsFile));
        final SOSClientHandler handler = new SOSClientHandler();

        try {
            handler.getConnector().connect("http://enviro3.ait.ac.at:8080");
            handler.open();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        dps = handler.getDatapoints(null, Access.DONT_CARE);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
        w.close();
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
        final Iterator<Datapoint> it = dps.iterator();

        while (it.hasNext()) {
            // bitte nach auflösung sortieren, von grob nach fein
            // alle verfügbaren dps ausgeben und mit den verfügbaren layern vergleichen: gibt es zu jedem layer alle auflösungen
            final Datapoint dp = it.next();
            LOG.info("starting timeseries retrieval for Offering: " + dp.toString());
            w.write("starting timeseries retrieval for Offering: " + dp.toString() + "\n");
            LOG.info("Filter: " + dp.getFilter().toString());
            w.write("Filter: " + dp.getFilter().toString() + "\n");
            final Map<String, Object> props = dp.getProperties();
            final Envelope env = (Envelope)props.get("ts:geometry");

            final double maxX = env.getMaxX();
            final double maxY = env.getMaxY();
            final double minX = env.getMinX();
            final double minY = env.getMinY();
            for (int i = 1; i < 21; i++) {
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
                final Date minDate = (Date)props.get("ts:available_data_min");
                final Date maxDate = (Date)props.get("ts:available_data_max");
                final long a = (long)((r.nextDouble() * (maxDate.getTime() - minDate.getTime())) + minDate.getTime());
                final long b = (long)((r.nextDouble() * (maxDate.getTime() - minDate.getTime())) + minDate.getTime());
                final long start = (a > b) ? b : a;
                final long end = (a > b) ? a : b;

                Assert.assertTrue(minDate.getTime() <= start);
                Assert.assertTrue(start <= maxDate.getTime());
                Assert.assertTrue(minDate.getTime() <= end);
                Assert.assertTrue(end <= maxDate.getTime());
                final GregorianCalendar startDate = new GregorianCalendar();
                startDate.setTimeInMillis(start);
                final GregorianCalendar endDate = new GregorianCalendar();
                endDate.setTimeInMillis(end);

                final TimeInterval interval = new TimeInterval(
                        TimeInterval.Openness.OPEN,
                        new TimeStamp(startDate.getTime()),
                        new TimeStamp(endDate.getTime()),
                        TimeInterval.Openness.OPEN);

                final EnvelopeQueryParameter point = new EnvelopeQueryParameter();
                point.setEnvelope(new Envelope(new Coordinate(x, y)));
                long startTime;
                long endTime;
                TimeSeries ts = null;
                try {
                    /*
                     * TimeSeriesRetrieval for specific point
                     */
                    LOG.info("TimeSeriesRetrieval " + i + ", for Point: " + x + " / " + y);
                    w.write("\t TimeSeriesRetrieval " + i + ", for Point: " + x + " / " + y);
                    startTime = System.currentTimeMillis();
                    ts = dp.getTimeSeries(TimeInterval.ALL_INTERVAL, point);
                    endTime = System.currentTimeMillis();

//                    Assert.assertNotNull(ts);
                    if (ts != null) {
                        LOG.info(" - retrieval succesfull ");
                        w.write(" - retrieval succesfull \n");
                        LOG.info("Time: " + ((endTime - startTime) / 1000) + " secs");
                        w.write("\t Time: " + ((endTime - startTime) / 1000) + " secs \n");
                    } else {
                        LOG.error(" - retrieval failed");
                        w.write(" - retrieval failed \n");
                    }
                    ts = null;
                    /*
                     * TimeSeriesRetrieval for specific interval and specific point
                     */

                    LOG.info("TimeSeriesRetrieval" + i + ", for Interval: " + startDate.getTime() + " / "
                                + endDate.getTime());
                    w.write(" \t TimeSeriesRetrieval" + i + ", for Interval: " + startDate.getTime() + " / "
                                + endDate.getTime());
                    startTime = System.currentTimeMillis();
                    ts = dp.getTimeSeries(interval, point);
                    endTime = System.currentTimeMillis();

//                    Assert.assertNotNull(ts);
                    if (ts != null) {
                        LOG.info(" - retrieval succesfull ");
                        w.write(" - retrieval succesfull \n");
                        LOG.info("Time: " + ((endTime - startTime) / 1000) + " secs");
                        w.write("\t Time: " + ((endTime - startTime) / 1000) + " secs \n");
                    } else {
                        LOG.error(" - retrieval failed");
                        w.write(" - retrieval failed \n");
                    }
                } catch (Exception e) {
                    LOG.error(" - retrieval failed", e);
                    w.write(" - retrieval failed \n");
                    w.write("Error: " + e.getMessage() + "\n");
                    continue;
                } catch (Throwable t) {
                    LOG.error(" - retrieval failed", t);
                    w.write(" - retrieval failed \n");
                    w.write("Error: " + t.getMessage() + "\n");
                    
                    return;
                } finally {
                    w.flush();
                }
            }
        }
    }
}
