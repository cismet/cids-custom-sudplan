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
import java.util.Properties;
import java.util.concurrent.Future;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bfriedrich
 */
@Ignore
public class TimeIntervalTest {
    
    public TimeIntervalTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception 
    {
        TimeSeriesTestUtil.initLogger();
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
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
        @Test
    public void testTimeInterval() throws Exception
    {
        TimeseriesRetriever retriever = TimeseriesRetriever.getInstance();
        
        final String url = "tstb:http://enviro3.ait.ac.at:8080?" +
                 "ts:feature_of_interest=urn:SMHI:feature:Europe&" + 
                "ts:offering=climate_echam5a1b3_prec_1h&" + 
//         "ts:observed_property   = urn:ogc:def:property:OGC:prec&" + 
                         "ts:ts_interval=[20000101T000000;20011231T235959]";
        
        Future<TimeSeries> result = retriever.retrieve(TimeseriesRetrieverConfig.fromTSTBUrl(url));
        assertNotNull(result);
        final TimeSeries ts = result.get();
//        System.out.println("NUMBER: " +  ts.getTimeStamps());
    }
    
}