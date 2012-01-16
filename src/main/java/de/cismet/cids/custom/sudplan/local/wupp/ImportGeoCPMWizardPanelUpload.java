/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

import java.awt.EventQueue;

import java.io.File;

import java.util.concurrent.Future;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.geocpmrest.GeoCPMRestClient;
import de.cismet.cids.custom.sudplan.geocpmrest.GeoCPMService;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMUtils;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportStatus;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ImportGeoCPMWizardPanelUpload implements WizardDescriptor.Panel, Cancellable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ImportGeoCPMWizardPanelCFGSelect.class);

    public static final String PROP_GEOCPM_ID = "__prop_geocpm_id__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient StatusPanel component;
    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient Integer geocpmId;
    private transient Future uploadTask;

    private final transient Object lock;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportGeoCPMWizardPanelCFGSelect object.
     */
    public ImportGeoCPMWizardPanelUpload() {
        component = new StatusPanel("Upload status");
        changeSupport = new ChangeSupport(this);
        lock = new Object();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public StatusPanel getComponent() {
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        synchronized (lock) {
            wizard = (WizardDescriptor)settings;

            final File geocpmFile = (File)wizard.getProperty(ImportGeoCPMWizardPanelCFGSelect.PROP_GEOCPM_FILE);
            final File dynaFile = (File)wizard.getProperty(ImportGeoCPMWizardPanelCFGSelect.PROP_DYNA_FILE);

            assert geocpmFile != null : "empty geocpm file"; // NOI18N

            setStatusEDT(true, "Begin work...");

            uploadTask = SudplanConcurrency.getSudplanGeneralPurposePool().submit(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(true, "Upload is cancelled");
                                }

                                setStatusEDT(true, "Reading GeoCPM Configuration");
                                final String geocpmGzip = GeoCPMUtils.readContentGzip(geocpmFile);

                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(true, "Upload is cancelled");
                                }

                                final String dynaGzip;
                                if (dynaFile == null) {
                                    dynaGzip = null;
                                } else {
                                    setStatusEDT(true, "Reading DYNA Configuration");
                                    dynaGzip = GeoCPMUtils.readContentGzip(dynaFile);
                                }

                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(true, "Upload is cancelled");
                                }

                                setStatusEDT(true, "Uploading new configuration");

                                // FIXME: hardcoded geocpm service url
                                final GeoCPMService client = new GeoCPMRestClient(RunoffModelManager.CLIENT_URL);
                                final ImportConfig cfg = new ImportConfig(geocpmGzip, dynaGzip);

                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(true, "Upload is cancelled");
                                }

                                final ImportStatus status = client.importConfiguration(cfg);

                                geocpmId = status.getGeocpmId();

                                setStatusEDT(false, "Import successful");

                                synchronized (lock) {
                                    ImportGeoCPMWizardPanelUpload.this.uploadTask = null;
                                }
                            } catch (final Exception e) {
                                setStatusEDT(false, "Import encountered an error: " + e);
                            } finally {
                                changeSupport.fireChange();
                            }
                        }
                    });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  busy     DOCUMENT ME!
     * @param  message  DOCUMENT ME!
     */
    private void setStatusEDT(final boolean busy, final String message) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    component.setBusy(busy);
                    component.setStatusMessage(message);
                }
            });
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(PROP_GEOCPM_ID, geocpmId);
    }

    @Override
    public boolean isValid() {
        return geocpmId != null;
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public boolean cancel() {
        synchronized (lock) {
            if (uploadTask != null) {
                if (!uploadTask.cancel(true)) {
                    if (uploadTask.isDone()) {
                        // FIXME: consider cfg removal
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("upload task was already complete consider config removal"); // NOI18N
                        }
                    } else {
                        LOG.warn("upload task could not be cancelled");                            // NOI18N

                        return false;
                    }
                }
            }

            return true;
        }
    }
}
