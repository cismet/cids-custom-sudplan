/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.model;

import org.apache.log4j.Logger;

import java.io.*;

//import org.openide.util.Exceptions;

import java.io.InputStream;

import java.net.*;

import java.util.*;
import java.util.Date;

import javax.net.ssl.*;

import de.cismet.cids.custom.sudplan.ProgressListener;
import de.cismet.cids.custom.sudplan.ProgressSupport;
import de.cismet.cids.custom.sudplan.local.linz.CsoOverflow;
import de.cismet.cids.custom.sudplan.local.linz.SwmmOutput;

/**
 * Parser for SWMM Report (RPT) Files.
 *
 * @author   Pascal Dihé
 * @author   Jimmy Lauter
 * @version  $Revision$, $Date$
 */
public class SwmmReportParser {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmReportParser.class);
    public static final int MAX_PROGRESS_STEPS = 5;
    private static final String WET_WEATHER_INFLOW = "Wet Weather Inflow";
    private static final String OUTFALL_LOADING_SUMMARY = "Outfall Loading Summary";

    //~ Instance fields --------------------------------------------------------

    private final transient ProgressSupport progressSupport;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmReportParser object.
     */
    public SwmmReportParser() {
        progressSupport = new ProgressSupport();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Parses the result of a SWMM Clalucation (RPT File), extracts all parameters required for the ETA Calculation
     * (efficency values) and returns the SWMM output bean with all properties set.
     *
     * @param   swmmOutput      pre-initialized swmm output bean
     * @param   swmmReportFile  report file to be parsed
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public SwmmOutput parseRPT(final SwmmOutput swmmOutput, final InputStream swmmReportFile) throws Exception {
        // TODO: parse RPT,. set Properties
        assert swmmReportFile != null : "Report-File is null";
        float VQ_r = 0.0f;
        final float VQ_o = 0.0f;
        final Map<String, Float> cso_totalVolume = new HashMap<String, Float>();
        // final InputStream stream = swmmReportFile;

        // Hier wird der Byte-Stream Zeile für Zeile durchsucht
        final InputStreamReader isr = new InputStreamReader(swmmReportFile);
        final BufferedReader br = new BufferedReader(isr);
        String strLine;
        while ((strLine = br.readLine()) != null) {
            // Suche nach der richtigen Zeile
            final int index1 = strLine.indexOf(WET_WEATHER_INFLOW);

            final int index2 = strLine.indexOf(OUTFALL_LOADING_SUMMARY);

            // Erster Suchindex gefunden
            if (index1 != -1) {
                final String[] split = strLine.split(" +");
                try {
                    VQ_r = Float.parseFloat(split[split.length - 1]);
                } catch (NumberFormatException nfe) {
                    // TODO Fehlerbehandlung
                    nfe.printStackTrace();
                }
            }

            if (index2 != -1) {
                // Springe zur Zeile mit den Daten
                for (int j = 0; j <= 6; j++) {
                    strLine = br.readLine();
                }

                // Hole alle Daten und speichern
                while ((strLine = br.readLine()).matches(" +-+") == false) {
                    final String[] split = strLine.split(" +");

                    if (split.length >= 6) {
                        try {
                            cso_totalVolume.put(split[1], Float.parseFloat(split[5]));
                        } catch (NumberFormatException nfe) {
                            // TODO Fehlerbehandlung
                            nfe.printStackTrace();
                        }
                    }
                }
                br.close();
                break;
            }
        }

        for (final String key : cso_totalVolume.keySet()) {
            final CsoOverflow csoOverflow = new CsoOverflow();
            csoOverflow.setName(key);
            csoOverflow.setSwmmProject(swmmOutput.getSwmmProject());
            csoOverflow.setOverflowVolume(cso_totalVolume.get(key));
            // csoOverflow.setOverflowDuration((float)Math.random() * 100f);
            // csoOverflow.setOverflowFrequency((float)Math.random() * 10f);
            // csoOverflow.setOverflowVolume((float)Math.random() * 1000f);
            swmmOutput.getCsoOverflows().put(key, csoOverflow);
        }

        swmmOutput.setTotalRunoffVolume(VQ_r);

        return swmmOutput;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    public void addProgressListener(final ProgressListener progressL) {
        progressSupport.addProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    public void removeProgressListener(final ProgressListener progressL) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeProgressListener: " + progressL);
        }
        progressSupport.removeProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final long startZeit = System.currentTimeMillis();
        final SwmmReportParser swmmReportParser = new SwmmReportParser();

        // Wir basteln uns einen Certificat-Manager der alles erlaubt
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(final java.security.cert.X509Certificate[] certs,
                            final String authType) {
                        // No need to implement.
                    }

                    @Override
                    public void checkServerTrusted(final java.security.cert.X509Certificate[] certs,
                            final String authType) {
                        // No need to implement.
                    }
                }
            };

        // Sag Java: wir vertrauen der Verbindung
        try {
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        InputStream swmmReportFile = null;
        try {
            final URL url = new URL("https://sudplan.ait.ac.at/model/run/run_1337759124496/linz_v1_2011-08-29.rpt");
            final HttpsURLConnection http = (HttpsURLConnection)url.openConnection();
            Authenticator.setDefault(new Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("SMS", "cismet42".toCharArray());
                    }
                });
            http.setAllowUserInteraction(true);
            http.setRequestMethod("GET");
            http.connect();

            swmmReportFile = http.getInputStream(); // (InputStream)uc.getInputStream();//.getInputStream();//new
                                                    // FileInputStream("c:\\linz_v1_2011-08-29_neu.rpt"); // TODO
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwmmOutput swmmOutput = new SwmmOutput();
        swmmOutput.setSwmmProject(2);
        swmmOutput.setUser("Pascal Dihé");
        swmmOutput.setCreated(new Date());
        try {
            swmmOutput = swmmReportParser.parseRPT(swmmOutput, swmmReportFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final long endZeit = System.currentTimeMillis();
        System.out.println("Gesamte Durchlaufzeit: " + (endZeit - startZeit));
    }
}
