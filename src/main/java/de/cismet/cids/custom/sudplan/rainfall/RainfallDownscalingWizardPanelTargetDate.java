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
public final class RainfallDownscalingWizardPanelTargetDate implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient RainfallDownscalingVisualPanelTargetDate component;

    private transient Integer targetYear;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public RainfallDownscalingWizardPanelTargetDate() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new RainfallDownscalingVisualPanelTargetDate(this);
        }

        return component;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getTargetYear() {
        return targetYear;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        targetYear = (Integer)wizard.getProperty(RainfallDownscalingWizardAction.PROP_TARGET_YEAR);
        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        // we are sure that isValid() has checked the year
        wizard.putProperty(RainfallDownscalingWizardAction.PROP_TARGET_YEAR, Integer.parseInt(component.getYear()));
    }

    @Override
    public boolean isValid() {
        final String choosenYear = component.getYear();
        boolean valid;

        try {
            final Integer year = Integer.parseInt(choosenYear);
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

            if ((year < 2025) || (year > 2085)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    NbBundle.getMessage(
                        RainfallDownscalingWizardPanelTargetDate.class,
                        "RainfallDownscalingWizardPanelTargetDate.isValid().yearNotInRange")); // NOI18N
                valid = false;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
                valid = true;
            }
        } catch (final NumberFormatException numberFormatException) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    RainfallDownscalingWizardPanelTargetDate.class,
                    "RainfallDownscalingWizardPanelTargetDate.isValid().onlyNumbers"));        // NOI18N
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
     */
    protected void fireChangeEvent() {
        changeSupport.fireChange();
    }
}
