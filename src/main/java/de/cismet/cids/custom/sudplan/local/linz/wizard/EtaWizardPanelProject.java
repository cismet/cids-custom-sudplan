/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.text.ParseException;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.local.linz.EtaInput;
import de.cismet.cids.custom.sudplan.local.linz.EtaOutput;
import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;
import de.cismet.cids.custom.sudplan.local.linz.SwmmOutput;
import de.cismet.cids.custom.sudplan.local.wupp.*;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class EtaWizardPanelProject implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaWizardPanelProject.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizard;
    /** local swmm project variable. */
    private transient CidsBean selectedSwmmProject;
    private transient CidsBean selectedSwmmScenario;
    private transient volatile EtaWizardPanelProjectUI component;
    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public EtaWizardPanelProject() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new EtaWizardPanelProjectUI(this);
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
        assert wizard.getProperty(EtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm project bean is null";
        this.selectedSwmmProject = (CidsBean)wizard.getProperty(EtaWizardAction.PROP_SWMM_PROJECT_BEAN);
        if (wizard.getProperty(EtaWizardAction.PROP_SWMM_SCENARIO_BEAN) != null) {
            this.selectedSwmmScenario = (CidsBean)wizard.getProperty(EtaWizardAction.PROP_SWMM_SCENARIO_BEAN);
        }

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("store settings");
        }
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(EtaWizardAction.PROP_SWMM_PROJECT_BEAN, this.getSelectedSwmmProject());
        wizard.putProperty(EtaWizardAction.PROP_SWMM_SCENARIO_BEAN, this.getSelectedSwmmScenario());

        try {
            final CidsBean swmmOutputBean = (CidsBean)this.getSelectedSwmmScenario().getProperty("modeloutput");
            final String json = (String)swmmOutputBean.getProperty("ur"); // NOI18N
            final ObjectMapper mapper = new ObjectMapper();
            final SwmmOutput swmmOutput = mapper.readValue(json, SwmmOutput.class);
            final EtaInput etaInput = new EtaInput(swmmOutput);

            wizard.putProperty(EtaWizardAction.PROP_ETA_INPUT, etaInput);
        } catch (Throwable t) {
            LOG.error("invalid SWMM Model Output, could not create valid ETA Input: " + t.getMessage(), t);
        }
    }

    @Override
    public boolean isValid() {
        boolean valid = true;

        if (this.getSelectedSwmmProject() == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(EtaWizardPanelProject.class, "SwmmWizardPanelProject.error.noproject"));
            valid = false;
        } else if (this.getSelectedSwmmScenario() == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(EtaWizardPanelProject.class, "SwmmWizardPanelProject.error.noscenario"));
            valid = false;
        } else if (this.getSelectedSwmmScenario().getProperty("modeloutput") == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(EtaWizardPanelProject.class, "SwmmWizardPanelProject.error.noresults"));
            valid = false;
        } else {
            try {
                final CidsBean swmmOutputBean = (CidsBean)this.getSelectedSwmmScenario().getProperty("modeloutput");
                final String json = (String)swmmOutputBean.getProperty("ur"); // NOI18N
                final ObjectMapper mapper = new ObjectMapper();
                mapper.readValue(json, SwmmOutput.class);
            } catch (Throwable t) {
                LOG.error("invalid SWMM Model Output: " + t.getMessage(), t);
                wizard.putProperty(
                    WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(EtaWizardPanelProject.class, "SwmmWizardPanelProject.error.invalidResults"));
                valid = false;
            }
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
    public CidsBean getSelectedSwmmProject() {
        return selectedSwmmProject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmProject  DOCUMENT ME!
     */
    public void setSelectedSwmmProject(final CidsBean swmmProject) {
        this.selectedSwmmProject = swmmProject;
    }

    /**
     * Get the value of selectedSwmmScenario.
     *
     * @return  the value of selectedSwmmScenario
     */
    public CidsBean getSelectedSwmmScenario() {
        return selectedSwmmScenario;
    }

    /**
     * Set the value of selectedSwmmScenario.
     *
     * @param  selectedSwmmScenario  new value of selectedSwmmScenario
     */
    public void setSelectedSwmmScenario(final CidsBean selectedSwmmScenario) {
        this.selectedSwmmScenario = selectedSwmmScenario;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
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
