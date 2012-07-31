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

import java.io.File;

import javax.swing.event.ChangeListener;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelProject implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelProject.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizard;
    private transient UploadWizardPanelProjectUI component;
    private String title;
    private String description;
    private String inpFile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public UploadWizardPanelProject() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new UploadWizardPanelProjectUI(this);
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
        final CidsBean swmmProject = (CidsBean)wizard.getProperty(
                UploadWizardAction.PROP_SWMM_PROJECT_BEAN);

        this.setTitle((swmmProject.getProperty("title") != null) ? swmmProject.getProperty("title").toString() : "");
        this.setDescription((swmmProject.getProperty("description") != null)
                ? swmmProject.getProperty("description").toString() : "");
//        this.setInpFile(swmmProject.getProperty("inp_file_name") != null
//                ? swmmProject.getProperty("inp_file_name").toString() : "");

        this.setInpFile((wizard.getProperty(UploadWizardAction.PROP_SWMM_INP_FILE) != null)
                ? wizard.getProperty(UploadWizardAction.PROP_SWMM_INP_FILE).toString() : "");

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        final CidsBean swmmProject = (CidsBean)wizard.getProperty(
                UploadWizardAction.PROP_SWMM_PROJECT_BEAN);
        try {
            swmmProject.setProperty("title", this.getTitle());
            swmmProject.setProperty("description", this.getDescription());

            if ((this.inpFile != null) && !this.inpFile.isEmpty()
                        && (this.inpFile.lastIndexOf(File.pathSeparator) != -1)) {
                swmmProject.setProperty(
                    "inp_file_name",
                    this.inpFile.substring(this.inpFile.lastIndexOf(File.pathSeparator)));
            } else {
                LOG.warn("Input file path '" + this.inpFile
                            + "' is not set or does not contain path separator '" + File.pathSeparator + "'");
            }

            wizard.putProperty(UploadWizardAction.PROP_SWMM_INP_FILE,
                this.getInpFile());
        } catch (Throwable t) {
            LOG.error("could not set property of SWMM Input File", t);
        }
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        if ((this.title == null) || this.title.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelProject.isValid().emptyName"));        // NOI18N
            valid = false;
        } else if ((this.description == null) || this.description.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelProject.isValid().emptyDescription")); // NOI18N
        } else if ((this.inpFile == null) || this.inpFile.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelProject.isValid().emptyFile"));        // NOI18N
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        }

        return valid;
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
     * Get the value of title.
     *
     * @return  the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title.
     *
     * @param  title  new value of title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Get the value of description.
     *
     * @return  the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description.
     *
     * @param  description  new value of description
     */
    public void setDescription(final String description) {
        this.description = description;
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
}
