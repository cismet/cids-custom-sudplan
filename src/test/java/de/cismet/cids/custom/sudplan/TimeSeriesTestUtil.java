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
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import static org.junit.Assert.*;
/**
 *
 * @author bfriedrich
 */
public class TimeSeriesTestUtil 
{
    public static final String      DAV_HOST = "http://sudplan.cismet.de/tsDav/";
    public static final Credentials CREDS = new UsernamePasswordCredentials("tsDav", "RHfio2l4wrsklfghj");

    
    private TimeSeriesTestUtil() {}
    
    
    public static void initLogger()
    {
        final Properties p = new Properties();
        p.put("log4j.appender.Remote", "org.apache.log4j.net.SocketAppender");
        p.put("log4j.appender.Remote.remoteHost", "localhost");
        p.put("log4j.appender.Remote.port", "4445");
        p.put("log4j.appender.Remote.locationInfo", "true");
        p.put("log4j.rootLogger", "ALL,Remote");
        org.apache.log4j.PropertyConfigurator.configure(p);
    }
    
    
    public static TimeSeries createTestTimeSeries()
    {
        final Calendar cal = Calendar.getInstance();
        
        final TimeSeries ts = new TimeSeriesImpl(); 
        ts.setTSProperty(TimeSeries.VALUE_KEYS,             new String[] { PropertyNames.VALUE });
        ts.setTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES, new String[] { Float.class.getName() });
        ts.setTSProperty(TimeSeries.VALUE_TYPES,            new String[] { TimeSeries.VALUE_TYPE_NUMBER });

        //--- 
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,  1);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,  2);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,  3);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        //---

        cal.set(Calendar.DAY_OF_MONTH, 3);
        cal.set(Calendar.HOUR_OF_DAY,  1);
        ts.setValue(new TimeStamp(cal.getTimeInMillis()), PropertyNames.VALUE, 10.0f);
        
        return ts;
    }
    
    
    public static void compareTimeSeries(final TimeSeries ts1, final TimeSeries ts2) throws Exception
    {
        assertNotNull(ts1);
        assertNotNull(ts2);
        
        
        assertEquals(ts1.getTSKeys(), ts2.getTSKeys());
        
        for(String tsKey : ts1.getTSKeys())
        {
            final Object val = ts1.getTSProperty(tsKey);
            if(val instanceof String[])
            {
                assertArrayEquals("Comparision with ts property key " + tsKey, (String[]) val, (String[]) ts2.getTSProperty(tsKey));
            }
            else
            {
                assertEquals("Comparision with ts property key " + tsKey, ts1.getTSProperty(tsKey), ts2.getTSProperty(tsKey));
            }
               
            
        }
        
        assertArrayEquals(ts1.getTimeStampsArray(), ts2.getTimeStampsArray());
        
        for(TimeStamp stamp : ts1.getTimeStampsArray())
        {
            assertEquals(ts1.getValue(stamp, PropertyNames.VALUE), ts2.getValue(stamp, PropertyNames.VALUE));
        }
    }
    
    
    public static HttpClient createHttpClient(final String host, final Credentials creds)
    {
         final HostConfiguration hostConfig = new HostConfiguration();
         hostConfig.setHost(host);
         final HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
         final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
         params.setMaxConnectionsPerHost(hostConfig, 20);
         connectionManager.setParams(params);
         final HttpClient client = new HttpClient(connectionManager);
         client.setHostConfiguration(hostConfig);
         client.getState().setCredentials(AuthScope.ANY, creds);
         
         return client;
    }
    
    public static HttpClient createHttpClient()
    {
        return createHttpClient(DAV_HOST, CREDS);
    }
    
    
    public static void removeRemoteFile(final String host, final Credentials creds, final String file) throws Exception
    {
        final HttpClient   client = TimeSeriesTestUtil.createHttpClient();
        final DeleteMethod del = new DeleteMethod(TimeSeriesTestUtil.DAV_HOST + file);


        try {
            client.executeMethod(del);
        } catch (final Exception ex) {
            ex.printStackTrace();
            del.abort();
            throw ex;
        } 
        finally 
        {
            del.releaseConnection();
        }
    }
    
    
    public static void removeRemoteFile(final String file) throws Exception
    {
        removeRemoteFile(DAV_HOST, CREDS, file);
    }
}
