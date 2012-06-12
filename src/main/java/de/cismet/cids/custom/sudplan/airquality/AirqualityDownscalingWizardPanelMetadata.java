/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelMetadata extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient AirqualityDownscalingVisualPanelMetadata component;

    private transient String name;
    private transient String description;

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component createComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelMetadata(this);
        }

        return component;
    }

    @Override
    public void read(final WizardDescriptor wizard) {
        name = (String)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_NAME);
        description = (String)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_DESCRIPTION);

        component.init();
    }

    @Override
    public void store(final WizardDescriptor wizard) {
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_NAME, name);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_DESCRIPTION, description);
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        boolean valid = true;

        if ((name == null) || name.trim().isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelMetadata.class,
                    "AirqualityDownscalingWizardPanelMetadata.isValid().emptyName"));        // NOI18N
            valid = false;
        } else if ((description == null) || description.trim().isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelMetadata.class,
                    "AirqualityDownscalingWizardPanelMetadata.isValid().emptyDescription")); // NOI18N
            valid = false;
        }

        return valid;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDescription() {
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
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
}
