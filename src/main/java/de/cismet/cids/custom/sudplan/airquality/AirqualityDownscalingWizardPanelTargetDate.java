/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.util.Date;

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelTargetDate implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient AirqualityDownscalingVisualPanelTargetDate component;
    private transient Date startDate;
    private transient Date endDate;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingWizardPanelScenarios object.
     */
    public AirqualityDownscalingWizardPanelTargetDate() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelTargetDate(this);
        }

        return component;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getStartDate() {
        return startDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getEndDate() {
        return endDate;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        startDate = (Date)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_START_DATE);
        endDate = (Date)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_END_DATE);
        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_START_DATE, component.getSelectedStartDate());
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_END_DATE, component.getSelectedEndDate());
    }

    @Override
    public boolean isValid() {
        boolean valid;

        final Date currentStartDate = component.getSelectedStartDate();
        final Date currentEndDate = component.getSelectedEndDate();

        if ((currentStartDate == null) || (currentEndDate == null)) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelTargetDate.class,
                    "AirqualityDownscalingWizardPanelTargetDate.isValid().noStartOrEndDate")); // NOI18N
            valid = false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

            if (currentStartDate.after(currentEndDate)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    NbBundle.getMessage(
                        AirqualityDownscalingWizardPanelTargetDate.class,
                        "AirqualityDownscalingWizardPanelTargetDate.isValid().startAfterEnd")); // NOI18N
                valid = false;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
                valid = true;
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
     */
    protected void fireChangeEvent() {
        changeSupport.fireChange();
    }
}
