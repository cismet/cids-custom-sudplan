/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelUpload implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelUpload.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizard;
    private transient UploadWizardPanelUploadUI component;
    private String inpFile = null;

    private boolean uploadComplete = false;

    private boolean uploadCanceled = false;

    private boolean uploadErroneous = false;

    private boolean uploadInProgress = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public UploadWizardPanelUpload() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new UploadWizardPanelUploadUI(this);
        }

        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        this.setInpFile(wizard.getProperty(UploadWizardAction.PROP_SWMM_INP_FILE).toString());

        this.uploadCanceled = false;
        // this.uploadComplete = false;
        this.uploadErroneous = false;

        this.fireChangeEvent();

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        // nothinmg to store
    }

    @Override
    public boolean isValid() {
        if (this.isUploadErroneous()) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelUpload.isValid().error",
                    NbBundle.getMessage(
                        UploadWizardPanelProject.class,
                        "UploadWizardPanelUpload.isValid().connectionError")));
            return false;
        } else if (this.isUploadCanceled()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelUpload.isValid().canceled"));
            return false;
        } else if (this.isUploadComplete()) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelUpload.isValid().complete"));
            return false;
        } else if (this.isUploadInProgress()) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelUpload.isValid().progressing"));
            return false;
        } else {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelUpload.isValid().upload"));
            return true;
        }
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    /**
     * DOCUMENT ME!
     */
    protected void fireChangeEvent() {
        changeSupport.fireChange();
    }

    /**
     * Get the value of inpFile.
     *
     * @return  the value of inpFile
     */
    public String getInpFile() {
        return inpFile;
    }

    /**
     * Set the value of inpFile.
     *
     * @param  inpFile  new value of inpFile
     */
    public void setInpFile(final String inpFile) {
        this.inpFile = inpFile;
    }

    /**
     * Get the value of uploadComplete.
     *
     * @return  the value of uploadComplete
     */
    public boolean isUploadComplete() {
        return uploadComplete;
    }

    /**
     * Set the value of uploadComplete.
     *
     * @param  uploadComplete  new value of uploadComplete
     */
    public void setUploadComplete(final boolean uploadComplete) {
        if (uploadComplete) {
            this.uploadInProgress = false;
            this.uploadCanceled = false;
            this.uploadErroneous = false;
        }

        this.uploadComplete = uploadComplete;
        this.fireChangeEvent();
    }

    /**
     * Get the value of uploadCanceled.
     *
     * @return  the value of uploadCanceled
     */
    public boolean isUploadCanceled() {
        return uploadCanceled;
    }

    /**
     * Set the value of uploadCanceled.
     *
     * @param  uploadCanceled  new value of uploadCanceled
     */
    public void setUploadCanceled(final boolean uploadCanceled) {
        if (uploadCanceled) {
            this.uploadInProgress = false;
            this.uploadComplete = false;
            this.uploadErroneous = false;
        }

        this.uploadCanceled = uploadCanceled;
        this.fireChangeEvent();
    }

    /**
     * Get the value of uploadErroneous.
     *
     * @return  the value of uploadErroneous
     */
    public boolean isUploadErroneous() {
        return uploadErroneous;
    }

    /**
     * Set the value of uploadErroneous.
     *
     * @param  uploadErroneous  new value of uploadErroneous
     */
    public void setUploadErroneous(final boolean uploadErroneous) {
        if (uploadErroneous) {
            this.uploadInProgress = false;
            this.uploadComplete = false;
            this.uploadCanceled = false;
        }

        this.uploadErroneous = uploadErroneous;
        this.fireChangeEvent();
    }

    /**
     * Get the value of uploadInProgress.
     *
     * @return  the value of uploadInProgress
     */
    public boolean isUploadInProgress() {
        return uploadInProgress;
    }

    /**
     * Set the value of uploadInProgress.
     *
     * @param  uploadInProgress  new value of uploadInProgress
     */
    public void setUploadInProgress(final boolean uploadInProgress) {
        this.uploadInProgress = uploadInProgress;
    }
}
