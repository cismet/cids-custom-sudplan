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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
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
    private static Set<Datapoint> dps;
    private static BufferedWriter w;
    private static final DatapointComparator comparator = new DatapointComparator();
    private static ArrayList<Datapoint> sortedDpsList;
    private static String succsBuffer = "";
    private static String errReportBuffer = "";
    private static final String newLine = System.getProperty("line.separator");

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
//        handler.setId("foo");

        try {
            handler.getConnector().connect("http://enviro3.ait.ac.at:8080");
            handler.open();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        dps = handler.getDatapoints(null, Access.DONT_CARE);
        sortedDpsList = new ArrayList<Datapoint>(dps);

        errReportBuffer += newLine + newLine;
        errReportBuffer += "################################################################################################################################"
                + newLine;
        errReportBuffer += "#############################################################" + newLine;

        errReportBuffer += "Summary of occured errors" + newLine;
        errReportBuffer +=
                "---------------------------------------------------------------------------------------------------------------------------------------"
                + newLine
                + newLine;

        Collections.sort(sortedDpsList, comparator);
        w.write("SOS Testrun - Report" + newLine);
        final Date d = new Date(System.currentTimeMillis());
        w.write("Date: " + d + newLine + newLine);

        w.write("Available Layers" + newLine);
        w.write(
                "---------------------------------------------------------------------------------------------------------------------------------------"
                + newLine);
        final String dpsList = sortedDpsList.toString().replaceAll("},", newLine + newLine).replaceAll(",", newLine + "\t");
        w.write(dpsList);
        w.flush();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws IOException {
        w.flush();
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
        final Iterator<Datapoint> it = sortedDpsList.iterator();
        w.write(newLine + newLine + "starting time series retrieval..." + newLine + newLine);
        while (it.hasNext()) {
            succsBuffer = "";
            String layerInfo = "";
            final Datapoint dp = it.next();

            //keep out 1h and 30m time series
            final String[] offering = dp.getFilter().getProperty("ts:offering").split("_");
            final String prec = offering[offering.length - 1];
            
            //TODO: dont filter high precision timeseries
            if (prec.equals("1h") || prec.equals("30min")) {
                continue;
            }

            layerInfo += newLine
                    + newLine
                    + dp.getFilter().getProperty("ts:offering")
                    + newLine;
            layerInfo +=
                    "---------------------------------------------------------------------------------------------------------------------------------------"
                    + newLine;
            layerInfo += "Layer:"
                    + dp.toString().replaceAll(", ", newLine + "\t")
                    + newLine;

            layerInfo += newLine
                    + "Properties: "
                    + dp.getProperties().toString().replaceFirst(",", " /").replaceAll(", ", newLine + "\t")
                    + newLine;

            errReportBuffer += layerInfo;
            final Map<String, Object> props = dp.getProperties();
            final Envelope env = (Envelope) props.get("ts:geometry");

            final double maxX = env.getMaxX();
            final double maxY = env.getMaxY();
            final double minX = env.getMinX();
            final double minY = env.getMinY();

            w.write(layerInfo + newLine);
            w.write("\t Retrievals with errors:" + newLine + newLine);

//            succsBuffer += layerInfo;
//            errBuffer += layerInfo;

            for (int i = 0; i < 21; i++) {
                /*
                 * calculate a random point
                 */
                final Random r = new Random();
                final double x = (r.nextDouble() * (maxX - minX))
                        + minX;
                final double y = (r.nextDouble() * (maxY - minY))
                        + minY;

                Assert.assertTrue(minX <= x);
                Assert.assertTrue(x <= maxX);
                Assert.assertTrue(minY <= y);
                Assert.assertTrue(y <= maxY);
                /*
                 * calculate a random time interval
                 */
                final Date minDate = (Date) props.get("ts:available_data_min");
                final Date maxDate = (Date) props.get("ts:available_data_max");
                final long a = (long) ((r.nextDouble() * (maxDate.getTime() - minDate.getTime())) + minDate.getTime());
                final long b = (long) ((r.nextDouble() * (maxDate.getTime() - minDate.getTime())) + minDate.getTime());
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

                String pointInterval = "";

                try {
                    /*
                     * TimeSeriesRetrieval for specific point
                     */
                    pointInterval = newLine
                            + "\t\t TimeSeriesRetrieval "
                            + i
                            + ", for Point: "
                            + x
                            + " / "
                            + y;
                    startTime = System.currentTimeMillis();
                    ts = dp.getTimeSeries(new TimeInterval(TimeInterval.Openness.OPEN, TimeStamp.NEGATIVE_INFINITY, TimeStamp.POSITIVE_INFINITY, TimeInterval.Openness.OPEN), point);
                    endTime = System.currentTimeMillis();

//                    Assert.assertNotNull(ts);
                    if (ts != null) {
                        String result = " - retrieval succesfull"
                                + newLine;
                        result += "\t\t Time: "
                                + ((endTime - startTime) / 1000)
                                + " secs"
                                + newLine;

                        succsBuffer += pointInterval
                                + result;
                    } else {
                        errReportBuffer += newLine
                                + pointInterval
                                + " - retrieval failed"
                                + newLine;
                        w.write(pointInterval + " - retrieval failed" + newLine);
                    }
                    ts = null;

                    /*
                     * TimeSeriesRetrieval for specific interval and specific point
                     */
                    pointInterval = newLine
                            + "\t\t TimeSeriesRetrieval"
                            + i
                            + ", for Interval: "
                            + startDate.getTime()
                            + " / "
                            + endDate.getTime();

                    startTime = System.currentTimeMillis();
                    ts = dp.getTimeSeries(interval, point);
                    endTime = System.currentTimeMillis();

//                    Assert.assertNotNull(ts);
                    if (ts != null) {
                        String result = " - retrieval succesfull"
                                + newLine;
                        result += "\t\t Time: "
                                + ((endTime - startTime) / 1000)
                                + " secs"
                                + newLine;
                        succsBuffer += newLine
                                + pointInterval
                                + result;
                    } else {
                        w.write(pointInterval + " - retrieval failed" + newLine);
                        errReportBuffer += pointInterval
                                + " - retrieval failed"
                                + newLine;
                    }
                } catch (Exception e) {
                    w.write(pointInterval + " - retrieval failed" + newLine);
                    w.write("\t\t Error: " + e.getMessage() + newLine);
                    errReportBuffer += pointInterval
                            + " - retrieval failed"
                            + newLine;
                    errReportBuffer += "\t\t Error: "
                            + e.getMessage()
                            + newLine;
                    continue;
                } catch (Throwable t) {
                    w.write("\t\t Error: " + t.getMessage() + "\n");
                    errReportBuffer += "\t\t Error: "
                            + t.getMessage()
                            + "\n";
                    w.write(errReportBuffer);
                    return;
                } finally {
                    w.flush();
                }
            }
            w.write(newLine + "\t Succesfull retrievals" + newLine + newLine);
            w.write(succsBuffer);
            w.write(newLine + newLine);
            w.flush();
        }
        return;
    }

    //~ Inner Classes ----------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected static final class DatapointComparator implements Comparator<Datapoint> {

        //~ Instance fields ----------------------------------------------------
        final HashMap<String, Integer> lookupTable;

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new DatapointComparator object.
         */
        public DatapointComparator() {
            lookupTable = new HashMap<String, Integer>();
            lookupTable.put("10Y", 100);
            lookupTable.put("1Y", 200);
            lookupTable.put("1M", 300);
            lookupTable.put("1d", 400);
            lookupTable.put("1h", 500);
            lookupTable.put("30m", 600);
        }

        //~ Methods ------------------------------------------------------------

        /*
         * take the temp value of the sos:Offering String  wich encodes the precision it is one of the following values
         * (increasing precision) 10Y, 1Y, 1M, 1d = 86400s, 3600s, 1800s
         */
        @Override
        public int compare(final Datapoint o1, final Datapoint o2) {
            final Properties f1 = o1.getFilter();
            final String[] offering1 = f1.getProperty("ts:offering").split("_");
            final String prec1 = offering1[offering1.length
                    - 1];
            final Integer valueOfPrec1 = lookupTable.get(prec1);

            final Properties f2 = o2.getFilter();
            final String[] offering2 = f2.getProperty("ts:offering").split("_");
            final String prec2 = offering2[offering2.length
                    - 1];
            final Integer valueOfPrec2 = lookupTable.get(prec2);

            if ((valueOfPrec1 == null) || (valueOfPrec2 == null)) {
                throw new IllegalStateException("Could not compare the two datapoints " + o1 + o2
                        + " because the precision is unkown. ");
            }

            if (valueOfPrec1.intValue() < valueOfPrec2.intValue()) {
                return -1;
            } else if (valueOfPrec1.intValue() == valueOfPrec2.intValue()) {
                return 0;
            }

            return 1;
        }
    }
}
