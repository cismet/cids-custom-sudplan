/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.cismet.cids.custom.sudplan.SudplanOptions;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.Converter;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class EmissionUpload /*implements Runnable*/ {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EmissionUpload.class);

    private static final transient char NEWLINE = '\n';
    private static final transient Charset CHARSET = Charset.forName("ISO-8859-1"); // NOI18N

    private static final transient String FILENAME_CONTENT = "content.txt";                     // NOI18N
    private static final transient String FILENAME_DESCRIPTION = "emissionscenariodesc.txt";    // NOI18N
    private static final transient String FILENAME_LAYER_PREFIX = "emissionlayer_";             // NOI18N
    private static final transient String FILENAME_LAYER_SUFFIX_GRID = "_grid.txt";             // NOI18N
    private static final transient String FILENAME_LAYER_SUFFIX_TIMEVARIATION = "_timevar.txt"; // NOI18N

    private static final transient String CONTENTFILE_SEPARATOR_PROPERTY = ".";                 // NOI18N
    private static final transient String CONTENTFILE_SEPARATOR_KEYVALUE = ": ";                // NOI18N
    private static final transient String CONTENTFILE_CATEGORY_SCENARIO = "emissionscenario";   // NOI18N
    private static final transient String CONTENTFILE_CATEGORY_LAYER = "emissionlayer";         // NOI18N
    private static final transient String CONTENTFILE_PROPERTY_SCENARIO_NAME = "name";          // NOI18N
    private static final transient String CONTENTFILE_PROPERTY_SCENARIO_SRS = "coordsys";       // NOI18N
    private static final transient String CONTENTFILE_PROPERTY_LAYER_NAME = "name";             // NOI18N
    private static final transient String CONTENTFILE_PROPERTY_LAYER_HEIGHT = "height";         // NOI18N
    private static final transient String CONTENTFILE_PROPERTY_LAYER_SUBSTANCE = "substance";   // NOI18N
    private static final transient String CONTENTFILE_PROPERTY_LAYER_TIMEVARIATION = "timevar"; // NOI18N

    public static final transient URL UPLOAD_URL;

    static {
        URL intermediateURL = null;
        // careful, if this class is loaded during the application bootstrap the property may not have been loaded yet.
        // in such case use the waitForInit option
        final String edbUrl = SudplanOptions.getInstance().getAqEdbUrl();
        try {
            intermediateURL = new URL(edbUrl);
        } catch (final MalformedURLException ex) {
            LOG.warn(
                "Couldn't create an URL object for '"                // NOI18N
                        + edbUrl
                        + "'. Emission database upload won't work.", // NOI18N
                ex);
        }

        UPLOAD_URL = intermediateURL;
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmissionUpload object.
     */
    private EmissionUpload() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static byte[] zip(final CidsBean cidsBean) throws Exception {
        final String name = (String)cidsBean.getProperty("name");
        final String srs = (String)cidsBean.getProperty("srs");
        final String description = (String)cidsBean.getProperty("description");
        final List<CidsBean> grids = cidsBean.getBeanCollectionProperty("grids");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Zipping emission database."); // NOI18N
        }

        if ((grids == null) || (grids.isEmpty())) {
            throw new Exception("No grids specified.");                  // NOI18N
        }
        if ((name == null) || (name.trim().length() <= 0)) {
            throw new Exception("No emission database name specified."); // NOI18N
        }
        if (srs == null) {
            throw new Exception("No srs specified.");                    // NOI18N
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ZipOutputStream zipStream = new ZipOutputStream(output);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating file '" + FILENAME_CONTENT + "'."); // NOI18N
        }

        try {
            zipStream.putNextEntry(new ZipEntry(FILENAME_CONTENT));
            zipStream.write(createContentFile(name, srs, grids));
            zipStream.closeEntry();
        } catch (final IOException ex) {
            throw new Exception("Could not create file '" + FILENAME_CONTENT + "'.", ex); // NOI18N
        }

        if ((description != null) && (description.trim().length() > 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating file '" + FILENAME_DESCRIPTION + "'."); // NOI18N
            }

            try {
                zipStream.putNextEntry(new ZipEntry(FILENAME_DESCRIPTION));
                zipStream.write(description.getBytes(CHARSET));
                zipStream.closeEntry();
            } catch (final IOException ex) {
                LOG.info("Could not create file '" + FILENAME_DESCRIPTION + "'.", ex); // NOI18N
                // The description file is not essential. So we can proceed.
            }
        }

        for (int gridCount = 0; gridCount < grids.size(); gridCount++) {
            final CidsBean grid = grids.get(gridCount);
            final TimeVariation timeVariation = TimeVariation.timeVariationFor((String)grid.getProperty(
                        "timevariation"));

            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding grid '" + grid.toString() + "' at position '" + (gridCount + 1) + "'."); // NOI18N
            }

            try {
                zipStream.putNextEntry(new ZipEntry(
                        FILENAME_LAYER_PREFIX
                                + (gridCount + 1)
                                + FILENAME_LAYER_SUFFIX_GRID));
                zipStream.write(Converter.fromString((String)grid.getProperty("grid")));
                zipStream.closeEntry();
            } catch (final IOException ex) {
                throw new Exception("Could not insert emission grid file for '" + grid.toString() + "'.", ex); // NOI18N
            }

            if (TimeVariation.CUSTOM.equals(timeVariation)) {
                try {
                    zipStream.putNextEntry(new ZipEntry(
                            FILENAME_LAYER_PREFIX
                                    + (gridCount + 1)
                                    + FILENAME_LAYER_SUFFIX_TIMEVARIATION));
                    zipStream.write(Converter.fromString(((String)grid.getProperty("customtimevariation"))));
                    zipStream.closeEntry();
                } catch (final IOException ex) {
                    throw new Exception(
                        "Could not insert custom time variation file for '"
                                + grid.toString() // NOI18N
                                + "'.",           // NOI18N
                        ex);
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing zip file."); // NOI18N
        }

        try {
            zipStream.close();
        } catch (IOException ex) {
            throw new Exception("Could not close the generated zip file.", ex); // NOI18N
        }

        return output.toByteArray();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   emissionScenarioName  DOCUMENT ME!
     * @param   srs                   DOCUMENT ME!
     * @param   grids                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static byte[] createContentFile(final String emissionScenarioName,
            final String srs,
            final List<CidsBean> grids) {
        final StringBuilder result = new StringBuilder();

        result.append(CONTENTFILE_CATEGORY_SCENARIO);
        result.append(CONTENTFILE_SEPARATOR_PROPERTY);
        result.append(CONTENTFILE_PROPERTY_SCENARIO_NAME);
        result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
        result.append(emissionScenarioName);
        result.append(NEWLINE);

        result.append(CONTENTFILE_CATEGORY_SCENARIO);
        result.append(CONTENTFILE_SEPARATOR_PROPERTY);
        result.append(CONTENTFILE_PROPERTY_SCENARIO_SRS);
        result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
        result.append(srs);
        result.append(NEWLINE);

        for (int gridCount = 0; gridCount < grids.size(); gridCount++) {
            final CidsBean grid = grids.get(gridCount);

            final String gridName = (String)grid.getProperty("name");
            final Substance substance = Substance.substanceFor((String)grid.getProperty("substance"));
            final GridHeight gridHeight = GridHeight.gridHeightFor((String)grid.getProperty("height"));
            final TimeVariation timeVariation = TimeVariation.timeVariationFor((String)grid.getProperty(
                        "timevariation"));

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_NAME);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(gridName);
            result.append(NEWLINE);

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_HEIGHT);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(gridHeight.getRepresentationFile());
            result.append(NEWLINE);

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_SUBSTANCE);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(substance.getRepresentationFile());
            result.append(NEWLINE);

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_TIMEVARIATION);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(timeVariation.getRepresentationFile());
            result.append(NEWLINE);
        }

        return result.toString().getBytes(CHARSET);
    }

    /**
     * /** * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception                 DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static String upload(final CidsBean cidsBean) throws Exception {
        if ((cidsBean == null) || !(cidsBean.getProperty("file") instanceof String)) {
            throw new IllegalArgumentException("Given emission database to upload is invalid.");
        }

        final InputStream response = WebAccessManager.getInstance()
                    .doRequest(
                        UPLOAD_URL,
                        new ByteArrayInputStream(Converter.fromString((String)cidsBean.getProperty("file"))),
                        new HashMap<String, String>());

        BufferedReader responseReader = null;
        final StringBuilder responseMessage = new StringBuilder();

        try {
            responseReader = new BufferedReader(new InputStreamReader(response));
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseMessage.append(line);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Response of emission upload script: '" + responseMessage.toString() + "'."); // NOI18N
            }
        } catch (final Exception e) {
            return "Emission database was uploaded, but the server didn't send a response.";
        } finally {
            try {
                if (responseReader != null) {
                    responseReader.close();
                }
            } catch (final IOException ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Couldn't close response.", ex);                                          // NOI18N
                }
            }
        }

        return responseMessage.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static byte[] read(final File file) {
        if ((file == null) || !file.canRead() || (file.length() == 0)) {
            return new byte[0];
        }

        BufferedInputStream reader = null;
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[8192];

        try {
            reader = new BufferedInputStream(new FileInputStream(file));
            int length = -1;

            while ((length = reader.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
        } catch (final Exception ex) {
            LOG.warn("Couldn't read file '" + file + "'.", ex);
            return new byte[0];
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ex) {
                }
            }
        }

        return result.toByteArray();
    }
}
