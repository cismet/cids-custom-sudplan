/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.sudplan;

import java.beans.PropertyChangeListener;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin Scholl
 */
public class SudplanOptionsTest {
    
    public SudplanOptionsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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

    /**
     * Test of getProperty method, of class SudplanOptions.
     */
    @Test
    public void testGetProperty_waitForInit_noTimeout() {
        System.out.println("getProperty concurrently");
        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(3000);
                    SudplanOptions.getInstance().setAqSosUrl("abc");
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        });
        
        boolean waitForInit = true;
        long timeout = 0L;
        SudplanOptions instance = SudplanOptions.getInstance();
        t.start();
        assertNull(SudplanOptions.getInstance().getAqSosUrl());
        
        final Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                SudplanOptions.getInstance().setAqSpsUrl("aqsps");
                SudplanOptions.getInstance().setHdHypeIp("hype");
            }
        });
        final Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                SudplanOptions.getInstance().setRfSosUrl("rfsos");
                SudplanOptions.getInstance().setRfSpsUrl("rfsps");
            }
        });
        final Thread t3 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("aqsos=" + SudplanOptions.getInstance().getAqSosUrl());
                System.out.println("aqsps=" + SudplanOptions.getInstance().getAqSpsUrl());
                System.out.println("hype=" + SudplanOptions.getInstance().getHdHypeIp());
                System.out.println("rfsos=" + SudplanOptions.getInstance().getRfSosUrl());
                System.out.println("rfsps=" + SudplanOptions.getInstance().getRfSpsUrl());
            }
        });
        final Thread t4 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    t1.start();
                    t2.start();
                    t3.start();
                } catch (final InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        });
        Object expResult = "abc";
        t4.start();
        System.out.println("wait for init");
        final long start = System.currentTimeMillis();
        Object result = instance.getProperty(SudplanOptions.PROP_AQ_SOS_URL, waitForInit, timeout);
        System.out.println("waited for " + (System.currentTimeMillis() - start));
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetProperty_waitForInit_timeout() {
        System.out.println("getProperty concurrently");
        SudplanOptions.getInstance().clearProperties();
        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(3000);
                    SudplanOptions.getInstance().setAqSosUrl("abc");
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        });
        
        boolean waitForInit = true;
        long timeout = 1000L;
        SudplanOptions instance = SudplanOptions.getInstance();
        t.start();
        assertNull(SudplanOptions.getInstance().getAqSosUrl());
        
        final Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                SudplanOptions.getInstance().setAqSpsUrl("aqsps");
                SudplanOptions.getInstance().setHdHypeIp("hype");
            }
        });
        final Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                SudplanOptions.getInstance().setRfSosUrl("rfsos");
                SudplanOptions.getInstance().setRfSpsUrl("rfsps");
            }
        });
        final Thread t3 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("aqsos=" + SudplanOptions.getInstance().getAqSosUrl());
                System.out.println("aqsps=" + SudplanOptions.getInstance().getAqSpsUrl());
                System.out.println("hype=" + SudplanOptions.getInstance().getHdHypeIp());
                System.out.println("rfsos=" + SudplanOptions.getInstance().getRfSosUrl());
                System.out.println("rfsps=" + SudplanOptions.getInstance().getRfSpsUrl());
            }
        });
        final Thread t4 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    t1.start();
                    t2.start();
                    t3.start();
                } catch (final InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        });
        t4.start();
        System.out.println("wait for init");
        final long start = System.currentTimeMillis();
        Object result = instance.getProperty(SudplanOptions.PROP_AQ_SOS_URL, waitForInit, timeout);
        System.out.println("waited for " + (System.currentTimeMillis() - start));
        assertNull(result);
    }
}
