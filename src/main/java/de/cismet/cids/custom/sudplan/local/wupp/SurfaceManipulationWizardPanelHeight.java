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
public final class SurfaceManipulationWizardPanelHeight extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    // private final transient ChangeSupport changeSupport;

    // private transient WizardDescriptor wizard;

    private transient Double height;

    private transient Boolean seaType;

    private transient SurfaceManipulationVisualPanelHeight component;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getHeight() {
        return height;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  height  DOCUMENT ME!
     */
    public void setHeight(final Double height) {
        this.height = height;
        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isSeaType() {
        return seaType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  seaType  type DOCUMENT ME!
     */
    public void setSeaType(final Boolean seaType) {
        this.seaType = seaType;
        changeSupport.fireChange();
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new SurfaceManipulationVisualPanelHeight(this);
        }
        return component;
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        if (height.doubleValue() == 0.0d) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                org.openide.util.NbBundle.getMessage(
                    SurfaceManipulationWizardPanelHeight.class,
                    "SurfaceManipulationWizardPanelHeight.isValid().noHeight"));
            return false;
        }

        return true;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        try {
            height = (Double)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_HEIGHT);
        } catch (NullPointerException npe) {
            height = 0.0d;
        }
        try {
            seaType = (Boolean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_TYPE);
        } catch (NullPointerException npe) {
            seaType = false;
        }
        component.init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_HEIGHT, height);
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_SURFACE_TYPE, seaType);
    }
}
