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

import java.awt.Component;

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
        wizard = (WizardDescriptor)settings;
        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm project bean is null";
        final CidsBean swmmProjectBean = (CidsBean)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN);

        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm input bean is null";
        final SwmmInput swmmInputObject = (SwmmInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT);

        this.setSwmmProject(swmmProjectBean);
        this.setSwmmInput(swmmInputObject);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN, this.getSwmmProject());
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT, this.getSwmmInput());
    }

    @Override
    public boolean isValid() {
        return (this.getSwmmProject() != null) && (this.getSwmmInput() != null);
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
        return swmmInput;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmInput  DOCUMENT ME!
     */
    public void setSwmmInput(final SwmmInput swmmInput) {
        this.swmmInput = swmmInput;
        changeSupport.fireChange();
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
        changeSupport.fireChange();
    }
}
