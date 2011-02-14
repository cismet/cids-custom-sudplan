package de.cismet.cids.custom.sudplan;



import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class TimeseriesRetrieverConfigTest
{

    public TimeseriesRetrieverConfigTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }


    private String getCurrentMethodName()
    {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    @Test
    public void testFromTSTBUrl() throws Exception
    {
        final String url = "tstb:SOS-SUDPLAN-Dummy@http://dummy.org?"
                + "ts:procedure=urn:ogc:object:LINZ:rain:1&"
                + "ts:feature_of_interest=urn:MyOrg:feature:linz&"
                + "ts:observed_property=urn:ogc:def:property:OGC:rain&"
                + "ts:offering=Station_3202_10min";

        TimeseriesRetrieverConfig result = TimeseriesRetrieverConfig.fromTSTBUrl(url);

        assertNotNull("result is null", result);
        assertEquals("illegal handler lookup", "SOS-SUDPLAN-Dummy", result.getHandlerLookup());
        assertEquals("illegal location", new URL("http://dummy.org"), result.getSosLocation());
        assertEquals("illegal procedure", "urn:ogc:object:LINZ:rain:1", result.getProcedure());
        assertEquals("illegal foi", "urn:MyOrg:feature:linz", result.getFoi());
        assertEquals("illegal obsprop", "urn:ogc:def:property:OGC:rain", result.getObsProp());
        assertEquals("illegal offering", "Station_3202_10min", result.getOffering());
        assertNull(result.getGeometry());
        assertNull(result.getInterval());
    }

    @Test
    public void testToTSTBUrl() throws Exception
    {
        final String url = "tstb:SOS-SUDPLAN-Dummy@http://dummy.org?"
                + "ts:procedure=urn:ogc:object:LINZ:rain:1&"
                + "ts:feature_of_interest=urn:MyOrg:feature:linz&"
                + "ts:observed_property=urn:ogc:def:property:OGC:rain&"
                + "ts:offering=Station_3202_10min";

        TimeseriesRetrieverConfig result = TimeseriesRetrieverConfig.fromTSTBUrl(url);

        assertEquals(url, result.toTSTBUrl());
    }
}