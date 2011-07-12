/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunGeoCPMWizardPanelMetadata implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient volatile RunGeoCPMVisualPanelMetadata component;

    private transient String name;
    private transient String description;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public RunGeoCPMWizardPanelMetadata() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    component = new RunGeoCPMVisualPanelMetadata(this);
                }
            }
        }

        return component;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.name = name;
        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  DOCUMENT ME!
     */
    public void setDescription(final String description) {
        this.description = description;
        changeSupport.fireChange();
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        setName((String)wizard.getProperty(RunGeoCPMWizardAction.PROP_NAME));
        setDescription((String)wizard.getProperty(RunGeoCPMWizardAction.PROP_DESCRIPTION));

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(RunGeoCPMWizardAction.PROP_NAME, name);
        wizard.putProperty(RunGeoCPMWizardAction.PROP_DESCRIPTION, description);
    }

    @Override
    public boolean isValid() {
        boolean valid;

        if ((name == null) || name.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    RunGeoCPMWizardPanelMetadata.class,
                    "RunGeoCPMWizardPanelMetadata.isValid().emptyName")); // NOI18N
            valid = false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

            if ((description == null) || description.isEmpty()) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        RunGeoCPMWizardPanelMetadata.class,
                        "RunGeoCPMWizardPanelMetadata.isValid().emptyDescription")); // NOI18N
            } else {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
            }

            valid = true;
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
}
