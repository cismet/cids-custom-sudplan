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
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

import java.awt.Component;

import java.text.ParseException;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;
import de.cismet.cids.custom.sudplan.local.wupp.*;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelProject implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelProject.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizard;
    /** local swmm project variable. */
    private transient CidsBean swmmProject;
    /** local swmm input variable. */
    private transient SwmmInput swmmInput;
    private transient volatile SwmmWizardPanelProjectUI component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public SwmmWizardPanelProject() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new SwmmWizardPanelProjectUI(this);
                    } catch (final WizardInitialisationException ex) {
                        LOG.error("cannot create wizard panel component", ex); // NOI18N
                    }
                }
            }
        }

        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("read settings");
        }
        wizard = (WizardDescriptor)settings;
        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm project bean is null";
        this.swmmProject = (CidsBean)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN);

        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm input bean is null";
        this.swmmInput = (SwmmInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("store settings");
        }
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN, this.getSwmmProject());
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT, this.swmmInput);
    }

    @Override
    public boolean isValid() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isValid called");
        }
        boolean valid = true;
        try {
            if (this.swmmInput.getSwmmProject() == -1) {
                // FIXME: i18n
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE,
                    "Bitte wählen Sie ein SWMM Project aus");
                valid = false;
            } else if ((this.swmmInput.getInpFile() == null) || this.swmmInput.getInpFile().isEmpty()) {
                Object inpFile = this.getSwmmProject().getProperty("inp_file_name");
                if (inpFile != null) {
                    swmmInput.setInpFile(inpFile.toString());
                    LOG.warn("SWMM INP file not set, setting to " + swmmInput.getInpFile());
                } else {
                    inpFile = this.getSwmmProject().getProperty("title");
                    LOG.warn("INP File not set in swmm model configuration, setting automatically to '"
                                + inpFile + "'");
                }

                // dieser beansbinding und property change mist funktioniert einfach nicht
                // warum sonst wird jetzt das textfield im UI nicht aktualisiert???!!!!
                this.swmmInput.setInpFile(inpFile.toString());

                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    "<html>Da keine INP Datei angegeben wurde, "
                            + "wurde der Name automatich auf '"
                            + inpFile
                            + "' festgelegt.</html>");
            } else if ((this.swmmInput.getStartDate() == null) || this.swmmInput.getStartDate().isEmpty()) {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE,
                    "Bitte geben Sie ein Startdatum an");
                valid = false;
            } else if ((this.swmmInput.getEndDate() == null) || this.swmmInput.getEndDate().isEmpty()) {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE,
                    "Bitte geben Sie ein Enddatum an");
                valid = false;
            } else if (this.swmmInput.getStartDateDate().getTime() >= this.swmmInput.getEndDateDate().getTime()) {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    "Das Startdatum muss vor dem Enddatum liegen");
                valid = false;
            } else if ((this.swmmInput.getEndDateDate().getTime()
                            - this.swmmInput.getStartDateDate().getTime()) < 200) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    "Bitte wählen sie einen Zitrum von mindestens 6.5 Monaten aus");
                valid = false;
            } else {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    null);
            }
        } catch (ParseException ex) {
            LOG.warn("invalid date format", ex);
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                "Das eingegebenen Datumsformat wird nicht unterstützt");
            valid = false;
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
     *
     * @return  DOCUMENT ME!
     */
    public SwmmInput getSwmmInput() {
        // nicht schön aber notwendig, damit die Validierung funktioniert
        this.changeSupport.fireChange();
        return swmmInput;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSwmmProject() {
        return swmmProject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmProject  DOCUMENT ME!
     */
    public void setSwmmProject(final CidsBean swmmProject) {
        this.swmmProject = swmmProject;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public WizardDescriptor getWizard() {
        return wizard;
    }
}
