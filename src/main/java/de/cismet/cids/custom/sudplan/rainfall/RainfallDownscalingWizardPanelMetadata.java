/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

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
public final class RainfallDownscalingWizardPanelMetadata implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient RainfallDownscalingVisualPanelMetadata component;

    private transient String name;
    private transient String description;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public RainfallDownscalingWizardPanelMetadata() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new RainfallDownscalingVisualPanelMetadata(this);
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
     * @return  DOCUMENT ME!
     */
    String getDescription() {
        return description;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        name = (String)wizard.getProperty(RainfallDownscalingWizardAction.PROP_NAME);
        description = (String)wizard.getProperty(RainfallDownscalingWizardAction.PROP_DESCRIPTION);
        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(RainfallDownscalingWizardAction.PROP_NAME, component.getSelectedName());
        wizard.putProperty(RainfallDownscalingWizardAction.PROP_DESCRIPTION, component.getSelectedDescription());
    }

    @Override
    public boolean isValid() {
        final String currentName = component.getSelectedName();
        boolean valid;

        if ((currentName == null) || currentName.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    RainfallDownscalingWizardPanelMetadata.class,
                    "RainfallDownscalingWizardPanelMetadata.isValid().emptyName")); // NOI18N
            valid = false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

            final String currentDesc = component.getSelectedDescription();
            if ((currentDesc == null) || currentDesc.isEmpty()) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        RainfallDownscalingWizardPanelMetadata.class,
                        "RainfallDownscalingWizardPanelMetadata.isValid().emptyDescription")); // NOI18N
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

    /**
     * DOCUMENT ME!
     */
    protected void fireChangeEvent() {
        changeSupport.fireChange();
    }
}
