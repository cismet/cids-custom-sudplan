/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.server.middleware.types.MetaObject;

import org.openide.WizardDescriptor;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class SurfaceManipulationWizardPanelConfigSelection extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient SurfaceManipulationVisualPanelConfigSelection component;
    private transient CidsBean initialConfig;
    private transient CidsBean configModel;
    private transient Boolean isConfigModelNew;
    private transient Boolean isSelectionChanged;
    private transient CidsBean deltaSurfaceToAdd;
    private transient MetaObject[] overlappingSurfaces;

    //~ Methods ----------------------------------------------------------------

    /**
     * /** * public Boolean isIsConfigModelNew() { return isConfigModelNew; }. * * @param isConfigModelNew DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getOverlappingSurfaces() {
        return overlappingSurfaces;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getConfigModel() {
        return configModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getDeltaSurfaceToAdd() {
        return deltaSurfaceToAdd;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  configModel       model DOCUMENT ME!
     * @param  selectionChanged  DOCUMENT ME!
     */
    public void setConfigModel(final CidsBean configModel, final boolean selectionChanged) {
        this.configModel = configModel;
        isSelectionChanged = selectionChanged;
        if ((this.configModel != null)
                    && this.configModel.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                        SMSUtils.TABLENAME_GEOCPM_CONFIGURATION)) {
            this.isConfigModelNew = true;
        } else {
            this.isConfigModelNew = false;
        }
        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getInitialConfig() {
        return initialConfig;
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new SurfaceManipulationVisualPanelConfigSelection(this);
        }
        return component;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        final CidsBean config = (CidsBean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_INITIAL_CONFIG);

        if (config == null) {
            throw new IllegalStateException("models cannot be null");
        }
        initialConfig = config;
        configModel = (CidsBean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG);
        isConfigModelNew = (Boolean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG_IS_NEW);
        isSelectionChanged = (Boolean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_CONFIG_SELECTION_CHANGED);
        deltaSurfaceToAdd = (CidsBean)wizard.getProperty(SurfaceManipulationWizardAction.PROP_ADD_DELTA_SURFACE);
        overlappingSurfaces = (MetaObject[])wizard.getProperty(
                SurfaceManipulationWizardAction.PROP_OVERLAPPING_SURFACES);
        component.init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG, configModel);
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_DELTA_CONFIG_IS_NEW, isConfigModelNew);
        wizard.putProperty(SurfaceManipulationWizardAction.PROP_CONFIG_SELECTION_CHANGED, isSelectionChanged);
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        if (configModel == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                org.openide.util.NbBundle.getMessage(
                    SurfaceManipulationWizardPanelConfigSelection.class,
                    "SurfaceManipulationWizardPanelConfigSelection.isValid().noModel"));
            return false;
        } else if ((configModel != null) && (isConfigModelNew != null) && isConfigModelNew.booleanValue()) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                org.openide.util.NbBundle.getMessage(
                    SurfaceManipulationWizardPanelConfigSelection.class,
                    "SurfaceManipulationWizardPanelConfigSelection.isValid().createNewModel"));
        }
        return true;
    }
}
