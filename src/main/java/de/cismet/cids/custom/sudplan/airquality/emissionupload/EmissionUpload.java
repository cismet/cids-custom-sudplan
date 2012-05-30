/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.EventQueue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.URL;

import java.nio.charset.Charset;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.cismet.cismap.commons.Crs;

import de.cismet.security.WebAccessManager;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class EmissionUpload implements Runnable {

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

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum State {

        //~ Enum constants -----------------------------------------------------

        WAITING, RUNNING, DONE, ERRONEOUS, ABORTED;
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Action {

        //~ Enum constants -----------------------------------------------------

        WAITING, CREATING_EMISSIONSCENARIO, UPLOADING_EMISSIONSCENARIO;

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return NbBundle.getMessage(Action.class, "EmissionUpload.Action." + name()); // NOI18N
        }
    }

    //~ Instance fields --------------------------------------------------------

    private EmissionUploadPanelUpload model;
    private Grid[] grids;
    private String emissionScenarioName;
    private Crs srs;
    private String description;
    private URL url;

    private Collection<Exception> exceptions;
    private volatile State state = State.WAITING;
    private volatile Action action = Action.WAITING;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmissionUpload object.
     *
     * @param  model                 DOCUMENT ME!
     * @param  grids                 DOCUMENT ME!
     * @param  emissionScenarioName  DOCUMENT ME!
     * @param  srs                   DOCUMENT ME!
     * @param  description           DOCUMENT ME!
     * @param  url                   DOCUMENT ME!
     */
    public EmissionUpload(final EmissionUploadPanelUpload model,
            final Collection<Grid> grids,
            final String emissionScenarioName,
            final Crs srs,
            final String description,
            final URL url) {
        this.model = model;

        if (grids != null) {
            this.grids = grids.toArray(new Grid[grids.size()]);
        } else {
            this.grids = new Grid[0];
        }

        this.emissionScenarioName = emissionScenarioName;
        this.srs = srs;
        this.description = description;
        this.url = url;

        exceptions = new LinkedList<Exception>();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (state != State.WAITING) {
            return;
        }

        setState(State.RUNNING, Action.CREATING_EMISSIONSCENARIO);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking conditions for emission upload."); // NOI18N
        }

        if ((grids == null) || (grids.length == 0)) {
            exceptions.add(new IllegalArgumentException("No grids specified."));                   // NOI18N
        }
        if ((emissionScenarioName == null) || (emissionScenarioName.trim().length() <= 0)) {
            exceptions.add(new IllegalArgumentException("Name of emission scenario is invalid.")); // NOI18N
        }
        if (srs == null) {
            exceptions.add(new IllegalArgumentException("No SRS specified"));                      // NOI18N //NOI18N
        }
        if (url == null) {
            exceptions.add(new IllegalArgumentException("No URL specified."));
        }

        if (Thread.currentThread().isInterrupted()) {
            setState(State.ABORTED, Action.CREATING_EMISSIONSCENARIO);
            return;
        }

        if (!exceptions.isEmpty()) {
            setErroneousState();
            return;
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ZipOutputStream zipStream = new ZipOutputStream(output);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating file '" + FILENAME_CONTENT + "'."); // NOI18N
        }

        try {
            zipStream.putNextEntry(new ZipEntry(FILENAME_CONTENT));
            zipStream.write(createContentFile());
            zipStream.closeEntry();
        } catch (IOException ex) {
            setErroneousState(zipStream, new Exception("Could not create file '" + FILENAME_CONTENT + "'.", ex)); // NOI18N
            return;
        }

        if (Thread.currentThread().isInterrupted()) {
            setAbortedState(zipStream, Action.CREATING_EMISSIONSCENARIO);
            return;
        }

        if ((description != null) && (description.trim().length() > 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating file '" + FILENAME_DESCRIPTION + "'."); // NOI18N
            }

            try {
                zipStream.putNextEntry(new ZipEntry(FILENAME_DESCRIPTION));
                zipStream.write(description.getBytes(CHARSET));
                zipStream.closeEntry();
            } catch (IOException ex) {
                exceptions.add(new Exception("Could not create file '" + FILENAME_DESCRIPTION + "'.", ex)); // NOI18N
                // The description file is not essential. So we can proceed.
            }
        }

        if (Thread.currentThread().isInterrupted()) {
            setAbortedState(zipStream, Action.CREATING_EMISSIONSCENARIO);
            return;
        }

        for (int gridCount = 0; gridCount < grids.length; gridCount++) {
            final Grid grid = grids[gridCount];

            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding grid '" + grid.getGridName() + "' at position '" + (gridCount + 1) + "'."); // NOI18N
            }

            try {
                insertZipEntry(
                    zipStream,
                    grid.getEmissionGrid(),
                    FILENAME_LAYER_PREFIX
                            + (gridCount + 1)
                            + FILENAME_LAYER_SUFFIX_GRID,
                    CHARSET);
            } catch (final FileNotFoundException ex) {
                setErroneousState(
                    zipStream,
                    new Exception("Could not find the emission grid file for '" + grid.getGridName() + "'.", ex)); // NOI18N
                return;
            } catch (final IOException ex) {
                setErroneousState(
                    zipStream,
                    new Exception("Could not insert emission grid file for '" + grid.getGridName() + "'.", ex));   // NOI18N
                return;
            }

            if (grid.getTimeVariation().equals(TimeVariation.CUSTOM)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Adding custom time variation '" + grid.getCustomTimeVariation().getAbsolutePath()
                                + "' for grid '" + grid.getGridName() + "'."); // NOI18N //NOI18N
                }

                try {
                    insertZipEntry(
                        zipStream,
                        grid.getCustomTimeVariation(),
                        FILENAME_LAYER_PREFIX
                                + (gridCount + 1)
                                + FILENAME_LAYER_SUFFIX_TIMEVARIATION,
                        CHARSET);
                } catch (final FileNotFoundException ex) {
                    setErroneousState(
                        zipStream,
                        new Exception(
                            "Could not find the custom time variation file for '"
                                    + grid.getGridName() // NOI18N
                                    + "'.",              // NOI18N
                            ex));
                    return;
                } catch (final IOException ex) {
                    setErroneousState(
                        zipStream,
                        new Exception(
                            "Could not insert custom time variation file for '"
                                    + grid.getGridName() // NOI18N
                                    + "'.",              // NOI18N
                            ex));
                    return;
                }
            }

            if (Thread.currentThread().isInterrupted()) {
                setAbortedState(zipStream, Action.CREATING_EMISSIONSCENARIO);
                return;
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing zip file."); // NOI18N
        }

        try {
            zipStream.close();
        } catch (IOException ex) {
            setErroneousState(new Exception("Could not close the generated zip file.", ex)); // NOI18N
            return;
        }

        setState(State.RUNNING, Action.UPLOADING_EMISSIONSCENARIO);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Start upload of emission scenario."); // NOI18N
        }

        InputStream response = null;
        try {
            response = WebAccessManager.getInstance()
                        .doRequest(
                                url,
                                new ByteArrayInputStream(output.toByteArray()),
                                new HashMap<String, String>());
        } catch (Exception ex) {
            setErroneousState(new Exception("Could not upload the generated zip file.", ex)); // NOI18N
            return;
        }

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
        } finally {
            try {
                if (responseReader != null) {
                    responseReader.close();
                }
            } catch (IOException ex) {
                LOG.warn("Couldn't close response.", ex);                                               // NOI18N
            }
        }

        if ("ok".equalsIgnoreCase(responseMessage.toString())) { // NOI18N
            setState(State.DONE, Action.WAITING);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Emission scenario uploaded."); // NOI18N
            }
        } else {
            setErroneousState(new Exception(responseMessage.toString()));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public State getState() {
        return state;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Action getAction() {
        return action;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Exception> getExceptions() {
        return exceptions;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private byte[] createContentFile() {
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
        result.append(srs.getName());
        result.append(NEWLINE);

        for (int gridCount = 0; gridCount < grids.length; gridCount++) {
            final Grid grid = grids[gridCount];

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_NAME);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(grid.getGridName());
            result.append(NEWLINE);

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_HEIGHT);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(grid.getGridHeight().getRepresentationFile());
            result.append(NEWLINE);

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_SUBSTANCE);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(grid.getSubstance().getRepresentationFile());
            result.append(NEWLINE);

            result.append(CONTENTFILE_CATEGORY_LAYER);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(gridCount + 1);
            result.append(CONTENTFILE_SEPARATOR_PROPERTY);
            result.append(CONTENTFILE_PROPERTY_LAYER_TIMEVARIATION);
            result.append(CONTENTFILE_SEPARATOR_KEYVALUE);
            result.append(grid.getTimeVariation().getRepresentationFile());
            result.append(NEWLINE);
        }

        return result.toString().getBytes(CHARSET);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   zipStream  DOCUMENT ME!
     * @param   file       DOCUMENT ME!
     * @param   filename   DOCUMENT ME!
     * @param   charset    buffer DOCUMENT ME!
     *
     * @throws  FileNotFoundException  DOCUMENT ME!
     * @throws  IOException            DOCUMENT ME!
     */
    private void insertZipEntry(final ZipOutputStream zipStream,
            final File file,
            final String filename,
            final Charset charset) throws FileNotFoundException, IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(file));

        zipStream.putNextEntry(new ZipEntry(filename));

        String line = null;
        while ((line = reader.readLine()) != null) {
            zipStream.write(charset.encode(line + "\n").array()); // NOI18N
        }

        zipStream.closeEntry();
        reader.close();
    }

    /**
     * DOCUMENT ME!
     */
    private void setErroneousState() {
        for (final Exception exception : exceptions) {
            LOG.error("Error occurred while creating or uploading emission scenario.", exception); // NOI18N
        }

        setState(State.ERRONEOUS, Action.WAITING);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  exception  DOCUMENT ME!
     */
    private void setErroneousState(final Exception exception) {
        LOG.error("Error occurred while creating or uploading emission scenario.", exception); // NOI18N
        exceptions.add(exception);

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    model.setExceptions(exceptions);
                }
            });

        setState(State.ERRONEOUS, Action.WAITING);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  outputStream  DOCUMENT ME!
     * @param  exception     DOCUMENT ME!
     */
    private void setErroneousState(final OutputStream outputStream, final Exception exception) {
        setErroneousState(exception);

        try {
            outputStream.close();
        } catch (IOException ex1) {
            // Nothing to do here.
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  outputStream  DOCUMENT ME!
     * @param  action        DOCUMENT ME!
     */
    private void setAbortedState(final OutputStream outputStream, final Action action) {
        setState(State.ABORTED, action);

        try {
            outputStream.close();
        } catch (IOException ex1) {
            // Nothing to do here.
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  state   DOCUMENT ME!
     * @param  action  DOCUMENT ME!
     */
    private void setState(final State state, final Action action) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting state to '" + state.name() + "'. Setting action to '" + action.name() + "'."); // NOI18N
        }

        this.state = state;
        this.action = action;

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    model.setAction(action);
                    model.setState(state);
                }
            });
    }
}
