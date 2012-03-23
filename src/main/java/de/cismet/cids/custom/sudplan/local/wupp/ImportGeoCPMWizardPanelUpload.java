/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

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

import de.cismet.cids.dynamics.CidsBean;

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

    private static final String BIN_GEOCMPFD = "GEOCPMF.D"; // NOI18N
    private static final String BIN_GEOCMPSD = "GEOCPMS.D"; // NOI18N
    private static final String BIN_GEOCMPID = "GEOCPMI.D"; // NOI18N

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
        component = new StatusPanel(NbBundle.getMessage(
                    ImportGeoCPMWizardPanelUpload.class,
                    "ImportGeoCPMWizardPanelUpload.constructor().panelName")); // NOI18N
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
            final CidsBean cfgBean = (CidsBean)wizard.getProperty(ImportGeoCPMWizardPanelMetadata.PROP_GEOCPM_BEAN);

            assert geocpmFile != null : "empty geocpm file";  // NOI18N
            assert dynaFile != null : "empty dyna file";      // NOI18N
            assert cfgBean != null : "empty geocpm cfg bean"; // NOI18N

            setStatusEDT(
                true,
                NbBundle.getMessage(
                    ImportGeoCPMWizardPanelUpload.class,
                    "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.beginWork")); // NOI18N

            uploadTask = SudplanConcurrency.getSudplanGeneralPurposePool().submit(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(
                                        false,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelUpload.class,
                                            "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.uploadCancelled")); // NOI18N
                                    return;
                                }

                                setStatusEDT(
                                    true,
                                    NbBundle.getMessage(
                                        ImportGeoCPMWizardPanelUpload.class,
                                        "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.readingCfg")); // NOI18N

                                // FIXME: for the validation
                                Thread.currentThread().sleep(1333);
                                // FIXME: read geocpm file final
                                final String geocpmGzip = GeoCPMUtils.readContentGzip(geocpmFile);

                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(
                                        false,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelUpload.class,
                                            "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.uploadCancelled")); // NOI18N
                                    return;
                                }

                                setStatusEDT(
                                    true,
                                    NbBundle.getMessage(
                                        ImportGeoCPMWizardPanelUpload.class,
                                        "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.readingDyna")); // NOI18N
                                // FIXME: read dyna files
                                final String dynaGzip = GeoCPMUtils.readContentGzip(dynaFile);

                                // FIXME: for the validation
                                Thread.currentThread().sleep(1333);

                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(
                                        false,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelUpload.class,
                                            "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.uploadCancelled")); // NOI18N
                                    return;
                                }

                                setStatusEDT(
                                    true,
                                    NbBundle.getMessage(
                                        ImportGeoCPMWizardPanelUpload.class,
                                        "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.uploading")); // NOI18N

                                // FIXME: for the validation
                                Thread.currentThread().sleep(1333);

                                // FIXME: hardcoded geocpm service url

                                final GeoCPMService client = new GeoCPMRestClient(RunoffModelManager.CLIENT_URL);
                                final ImportConfig cfg = new ImportConfig();

                                cfg.setGeocpmData(geocpmGzip);
                                cfg.setDynaData(dynaGzip);

                                cfg.setGeocpmFolder(geocpmFile.getParentFile().getName());
                                cfg.setDynaFolder(dynaFile.getParentFile().getName());

                                File geocpmFDFile = null;
                                File geocpmSDFile = null;
                                File geocpmIDFile = null;

                                final File[] dynaFiles = dynaFile.getParentFile().listFiles();
                                String fileName;
                                for (int i = 0; i < dynaFiles.length; i++) {
                                    fileName = dynaFiles[i].getName();

                                    if (BIN_GEOCMPFD.equalsIgnoreCase(fileName)) {
                                        geocpmFDFile = dynaFiles[i];
                                    } else if (BIN_GEOCMPSD.equalsIgnoreCase(fileName)) {
                                        geocpmSDFile = dynaFiles[i];
                                    } else if (BIN_GEOCMPID.equalsIgnoreCase(fileName)) {
                                        geocpmIDFile = dynaFiles[i];
                                    }
                                }

                                if (geocpmFDFile == null) {
                                    LOG.error("There is no GEOCPMF.D file in the dyna folder");                        // NOI18N
                                    setStatusEDT(
                                        false,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelUpload.class,
                                            "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.no_geocpmfd")); // NOI18N
                                    return;
                                }

                                if (geocpmSDFile == null) {
                                    LOG.error("There is no GEOCPMS.D file in the dyna folder");                        // NOI18N
                                    setStatusEDT(
                                        false,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelUpload.class,
                                            "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.no_geocpmsd")); // NOI18N));
                                    return;
                                }

                                if (geocpmIDFile == null) {
                                    LOG.error("There is no GEOCPMI.D file in the dyna folder");                        // NOI18N
                                    setStatusEDT(
                                        false,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelUpload.class,
                                            "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.no_geocpmid")); // NOI18N
                                    return;
                                }

                                cfg.setGeocpmFData(GeoCPMUtils.readBytes(geocpmFDFile));
                                cfg.setGeocpmSData(GeoCPMUtils.readBytes(geocpmSDFile));
                                cfg.setGeocpmIData(GeoCPMUtils.readBytes(geocpmIDFile));

                                if (Thread.currentThread().isInterrupted()) {
                                    setStatusEDT(
                                        false,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelUpload.class,
                                            "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.uploadCancelled")); // NOI18N
                                    return;
                                }

                                // FIXME: validation adaption
                                final ImportStatus status = client.importConfiguration(cfg);

//                                final ImportStatus status = new ImportStatus(-1);

                                geocpmId = status.getGeocpmId();

                                setStatusEDT(
                                    true,
                                    NbBundle.getMessage(
                                        ImportGeoCPMWizardPanelUpload.class,
                                        "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.saveCidsBean")); // NOI18N

                                // FIXME: for the validation
                                Thread.currentThread().sleep(1333);

                                // FIXME: hardcoded domain final MetaClass mc = ClassCacheMultiple.getMetaClass(
                                // "SUDPLAN-WUPP", // NOI18N "geocpm_configuration"); // NOI18N final MetaObject mo =
                                // SessionManager.getProxy() .getMetaObject(geocpmId, mc.getID(), "SUDPLAN-WUPP"); //
                                // NOI18N final CidsBean importBean = mo.getBean(); importBean.setProperty("name",
                                // cfgBean.getProperty("name")); // NOI18N importBean.setProperty("description",
                                // cfgBean.getProperty("description")); // NOI18N
                                // importBean.setProperty("investigation_area",
                                // cfgBean.getProperty("investigation_area")); // NOI18N
                                //
                                // importBean.persist();

                                setStatusEDT(
                                    false,
                                    NbBundle.getMessage(
                                        ImportGeoCPMWizardPanelUpload.class,
                                        "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.importSuccessful")); // NOI18N

                                synchronized (lock) {
                                    ImportGeoCPMWizardPanelUpload.this.uploadTask = null;
                                }
                            } catch (final Throwable e) {
                                LOG.error("Import encountered an error", e);                                     // NOI18N
                                setStatusEDT(
                                    false,
                                    NbBundle.getMessage(
                                        ImportGeoCPMWizardPanelUpload.class,
                                        "ImportGeoCPMWizardPanelUpload.readSettings(Object).status.importError", // NOI18N
                                        e));

                                // FIXME: use thread pool that doesn't swallow errors
                                if (e instanceof Error) {
                                    throw (Error)e;
                                }
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
