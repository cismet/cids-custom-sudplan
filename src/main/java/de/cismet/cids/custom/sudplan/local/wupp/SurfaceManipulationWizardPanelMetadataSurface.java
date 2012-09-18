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

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class SurfaceManipulationWizardPanelMetadataSurface extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient SurfaceManipulationVisualPanelMetadataSurface component;
    private transient String name;
    private transient String description;

    //~ Methods ----------------------------------------------------------------

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

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new SurfaceManipulationVisualPanelMetadataSurface(this);
        }
        return component;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        name = (String)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_NAME);
        description = (String)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_DESCRIPTION);
        component.init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_NAME, name);
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_DESCRIPTION, description);
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        if ((name == null) || name.isEmpty() || name.matches(" +")) {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, org.openide.util.NbBundle.getMessage(SurfaceManipulationWizardPanelMetadataSurface.class, "SurfaceManipulationWizardPanelMetadataSurface.isValid().noName"));
            return false;
        }

        if ((description == null) || description.isEmpty() || description.matches(" +")) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, org.openide.util.NbBundle.getMessage(SurfaceManipulationWizardPanelMetadataSurface.class, "SurfaceManipulationWizardPanelMetadataSurface.isValid().noDescription"));
        }
        return true;
    }
}
