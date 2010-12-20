/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import java.awt.EventQueue;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.sql.Timestamp;

import java.util.GregorianCalendar;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Executable;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.ProgressEvent;
import de.cismet.cids.custom.sudplan.ProgressListener;
import de.cismet.cids.custom.sudplan.ProgressSupport;
import de.cismet.cids.custom.sudplan.RunHelper;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class MultiplyModelManager implements Manager, Executable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(MultiplyModelManager.class);
    public static final String FILENAME_MULTIPLYRESULTS = "results.txt"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient MultiplyModelManagerUI ui;
    private final transient ProgressSupport progressSupport;

    private transient CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MultiplyModelManager object.
     */
    public MultiplyModelManager() {
        ui = new MultiplyModelManagerUI(this);
        progressSupport = new ProgressSupport();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Override
    public void apply() throws IOException {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Override
    public void execute() throws IOException {
        fireStarted();
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("interrupted", ex);
            }
        }

        final File inputFolder;
        try {
            inputFolder = new File(new URI(getInputFolderURI()));
        } catch (final URISyntaxException ex) {
            final String message = "cannot create file from input folder uri: " + getInputFolderURI(); // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        final File multipliersInput = new File(inputFolder, MultiplyInputManager.FILENAME_MULTIPLIERS);
        final Double[] multipliers = MultiplyHelper.doublesFromFile(multipliersInput);

        final int maxSteps = multipliers.length + 2;
        fireProgressed(1, maxSteps);

        final File multiplicandInput = new File(inputFolder, MultiplyInputManager.FILENAME_MULTIPLICAND);
        final int multiplicand = MultiplyHelper.intFromFile(multiplicandInput);

        final Double[] results = new Double[multipliers.length];
        for (int i = 0; i < multipliers.length; ++i) {
            results[i] = multipliers[i] * multiplicand;
            fireProgressed(i + 1, maxSteps);
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("interrupted", ex); // NOI18N
                }
            }
        }

        final File outputFile = new File(getLocation());
        MultiplyHelper.numbersToFile(outputFile, results);
        fireProgressed(maxSteps, maxSteps);
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("interrupted", ex); // NOI18N
            }
        }

        fireFinised();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    protected void fireStarted() {
        final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
        try {
            cidsBean.setProperty("started", dateConverter.convertReverse(GregorianCalendar.getInstance().getTime())); // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot set started flag in runbean";                                              // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        progressSupport.fireEvent(new ProgressEvent(ProgressEvent.State.STARTED));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  step      percent DOCUMENT ME!
     * @param  maxSteps  DOCUMENT ME!
     */
    protected void fireProgressed(final int step, final int maxSteps) {
        progressSupport.fireEvent(new ProgressEvent(ProgressEvent.State.PROGRESSING, step, maxSteps));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    protected void fireFinised() {
        final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
        try {
            cidsBean.setProperty("finished", dateConverter.convertReverse(GregorianCalendar.getInstance().getTime())); // NOI18N
            final MetaClass modelOutputClasss = ClassCacheMultiple.getMetaClass(cidsBean.getMetaObject().getDomain(),
                    "modeloutput");                                                                                    // NOI18N
            final CidsBean modelOutputBean = modelOutputClasss.getEmptyInstance().getBean();
            modelOutputBean.setProperty("uri", getLocation().toString());                                              // NOI18N
            modelOutputBean.setProperty("comments", "Output of the Multiply Model");
            modelOutputBean.setProperty("name", "Output from " + RunHelper.createIONameSnippet(cidsBean));             // NOI18N
            modelOutputBean.setProperty("model", cidsBean.getProperty("model"));                                       // NOI18N
            cidsBean.setProperty("modeloutput", modelOutputBean);                                                      // NOI18N
            cidsBean.persist();
            RunHelper.reloadCatalogTree();
        } catch (final Exception ex) {
            final String message = "cannot save new values in runbean";                                                // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        progressSupport.fireEvent(new ProgressEvent(ProgressEvent.State.FINISHED));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isStarted() {
        final Timestamp ts = (Timestamp)cidsBean.getProperty("started"); // NOI18N

        return ts != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isFinished() {
        final Timestamp ts = (Timestamp)cidsBean.getProperty("finished"); // NOI18N

        return ts != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getInputFolderURI() {
        final CidsBean modelinput = (CidsBean)cidsBean.getProperty("modelinput"); // NOI18N

        assert modelinput != null : "modelinput cannot be null at this point"; // NOI18N

        final String inputFolderURI = (String)modelinput.getProperty("uri"); // NOI18N

        assert inputFolderURI != null : "inputFolderURI cannot be null at this point"; // NOI18N

        return inputFolderURI;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Override
    public URI getLocation() throws IOException {
        final StringBuilder sb = new StringBuilder(getInputFolderURI());

        if ('/' != sb.charAt(sb.length() - 1)) {
            sb.append('/');
        }

        sb.append(FILENAME_MULTIPLYRESULTS);
        try {
            return new URI(sb.toString());
        } catch (final URISyntaxException ex) {
            final String message = "cannot create output uri from string: " + sb.toString(); // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ui.init();
                }
            });
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
     * @param  progressL  DOCUMENT ME!
     */
    @Override
    public void addProgressListener(final ProgressListener progressL) {
        progressSupport.addProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    @Override
    public void removeProgressListener(final ProgressListener progressL) {
        progressSupport.removeProgressListener(progressL);
    }

    @Override
    public Feature getFeature() throws IOException {
        return null;
    }
}
