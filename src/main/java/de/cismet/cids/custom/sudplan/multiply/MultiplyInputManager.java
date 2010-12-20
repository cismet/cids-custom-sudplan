/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import java.awt.EventQueue;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class MultiplyInputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    public static final String FILENAME_MULTIPLICAND = "multiplicand.txt"; // NOI18N

    public static final String FILENAME_MULTIPLIERS = "multipliers.txt"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(MultiplyInputManager.class);

    //~ Instance fields --------------------------------------------------------

    protected transient MultiplyInputManagerUI ui;

    private transient CidsBean modelInputBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MultiplyInputManager object.
     */
    public MultiplyInputManager() {
        ui = new MultiplyInputManagerUI(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean getCidsBean() {
        try {
            apply();
        } catch (final Exception e) {
            final String message = "could not apply changes"; // NOI18N
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        }

        return modelInputBean;
    }

    @Override
    public void apply() throws IOException {
        ui.store();
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.modelInputBean = cidsBean;

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ui.init();
                }
            });
    }

    @Override
    public URI getLocation() throws IOException {
        final String uri = (String)modelInputBean.getProperty("uri");     // NOI18N
        if (uri == null) {
            return new File(".").toURI();                                 // NOI18N
        } else {
            try {
                return new URI(uri);
            } catch (final URISyntaxException ex) {
                final String message = "invalid model input uri: " + uri; // NOI18N
                LOG.error(message, ex);
                throw new IOException(message, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   location  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public void setLocation(final URI location) throws IOException {
        try {
            modelInputBean.setProperty("uri", location.toString());  // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot set location" + location; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    Double[] getMultipliers() throws IOException {
        final Double[] multipliers;
        final File multipliersInput = getFactorFile(new File(getLocation()), FILENAME_MULTIPLIERS, false);

        if (multipliersInput == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("no multipliers file found, multipliers empty"); // NOI18N
            }
            multipliers = new Double[0];
        } else {
            multipliers = MultiplyHelper.doublesFromFile(multipliersInput);
        }

        return multipliers;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   multipliers  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    void setMultipliers(final Double[] multipliers) throws IOException {
        MultiplyHelper.numbersToFile(getFactorFile(new File(getLocation()), FILENAME_MULTIPLIERS, true), multipliers);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    int getMulitplicand() throws IOException {
        int multiplicand = 1;
        final File multiplicandInput = getFactorFile(new File(getLocation()), FILENAME_MULTIPLICAND, false);

        if (multiplicandInput == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("no multiplicand file found, multiplicand defaults to 1");                                 // NOI18N
            }
        } else {
            try {
                multiplicand = MultiplyHelper.intFromFile(multiplicandInput);
            } catch (final IOException e) {
                LOG.warn("cannot get multiplicand from file, multiplicand defaults to 1: " + multiplicandInput, e); // NOI18N
            }
        }

        return multiplicand;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   multiplicand  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    void setMultiplicand(final int multiplicand) throws IOException {
        MultiplyHelper.numbersToFile(getFactorFile(new File(getLocation()), FILENAME_MULTIPLICAND, true), multiplicand);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getUI() {
        return ui;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   parentDir   DOCUMENT ME!
     * @param   factorFile  DOCUMENT ME!
     * @param   create      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    protected File getFactorFile(final File parentDir, final String factorFile, final boolean create)
            throws IOException {
        if (!parentDir.exists() || !parentDir.isDirectory() || !parentDir.canRead()) {
            throw new IOException("illegal parent directory: " + parentDir); // NOI18N
        }

        File input = new File(parentDir, factorFile);
        if (input.exists()) {
            if (!input.isFile() || !input.canRead()) {
                throw new IOException("cannot read factor file: " + input);                                           // NOI18N
            }
        } else {
            if (create) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("factor input file does not exist, creating empty file: " + factorFile);                 // NOI18N
                }
                if (!parentDir.canWrite() || !input.createNewFile()) {
                    throw new IOException("cannot create new factor file '" + factorFile + "' in dir: " + parentDir); // NOI18N
                }
            } else {
                input = null;
            }
        }

        return input;
    }

    @Override
    public Feature getFeature() throws IOException {
        return null;
    }
}
