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

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelGridSize implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient AirqualityDownscalingVisualPanelGridSize component;
    private transient Integer gridSize;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingWizardPanelGridSize object.
     */
    public AirqualityDownscalingWizardPanelGridSize() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelGridSize(this);
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
        gridSize = (Integer)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_GRID_SIZE);
        component.init();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getGridSize() {
        return gridSize;
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        // we can parse here without caring for errors since we rely on isValid()
        if (isValid()) {
            wizard.putProperty(
                AirqualityDownscalingWizardAction.PROP_GRID_SIZE,
                Integer.parseInt(component.getSelectedGridSize()));
        }
    }

    @Override
    public boolean isValid() {
        final String choosenGridSize = component.getSelectedGridSize();
        boolean valid;

        if ((choosenGridSize == null) || choosenGridSize.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelGridSize.class,
                    "AirqualityDownscalingWizardPanelGridSize.isValid().gridSizeEmpty")); // NOI18N
            valid = false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
            valid = true;
        }

        try {
            final Integer size = Integer.parseInt(choosenGridSize);
            // no exception, it is ok
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

            if ((size < 1000) || (size > 10000)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    "Only sizes between 1000 and 10000 are supported");
                valid = false;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
                valid = true;
            }
        } catch (final NumberFormatException e) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelGridSize.class,
                    "AirqualityDownscalingWizardPanelGridSize.isValid().notAnInteger")); // NOI18N);
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
