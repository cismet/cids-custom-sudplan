/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.openide.WizardDescriptor;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class SurfaceManipulationWizardPanelMetadataConfig extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient SurfaceManipulationVisualPanelMetadataConfig component;
    private transient String name;
    private transient String description;
    private transient Boolean isConfigNew;
    private transient CidsBean selectedConfig;
//    private transient Boolean isDescChanged = false;
    private transient Boolean isSelectionChanged;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIsConfigNew() {
        return isConfigNew;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSelectedConfig() {
        return selectedConfig;
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
//        isDescChanged = true;
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

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new SurfaceManipulationVisualPanelMetadataConfig(this);
        }
        return component;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        isConfigNew = (Boolean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG_IS_NEW);
        selectedConfig = (CidsBean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG);
        isSelectionChanged = (Boolean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_CONFIG_SELECTION_CHANGED);

        if (isSelectionChanged) {
            if (isConfigNew) {
                name = null;
                description = null;
            } else {
                name = (String)selectedConfig.getProperty("name");
                description = (String)selectedConfig.getProperty("description");
            }
        } else {
            name = (String)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG_NAME);
            description = (String)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG_DESCRIPTION);
        }

        component.init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG_NAME, name);
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG_DESCRIPTION, description);
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        if ((name == null) || name.isEmpty() || name.matches(" +")) {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Please specify a name");
            return false;
        }

        if ((description == null) || description.isEmpty() || description.matches(" +")) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "You are encouraged to enter a description");
        }
        return true;
    }
}
