/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.openide.WizardDescriptor;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DoSimulationWizardPanelSelectWorkspace extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean selectedWorkspace;
    private transient Integer basinId;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSelectedWorkspace() {
        return selectedWorkspace;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedWorkspace  DOCUMENT ME!
     */
    public void setSelectedWorkspace(final CidsBean selectedWorkspace) {
        this.selectedWorkspace = selectedWorkspace;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getBasinId() {
        return basinId;
    }

    @Override
    protected Component createComponent() {
        return new DoSimulationVisualPanelSelectWorkspace(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        setSelectedWorkspace((CidsBean)wizard.getProperty(DoSimulationWizardAction.PROP_SELECTED_WORKSPACE));
        basinId = (Integer)wizard.getProperty(DoSimulationWizardAction.PROP_BASIN_ID);

        ((DoSimulationVisualPanelSelectWorkspace)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(DoSimulationWizardAction.PROP_SELECTED_WORKSPACE, selectedWorkspace);
    }

    @Override
    public boolean isValid() {
        if (selectedWorkspace == null) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please select a local model");

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            return true;
        }
    }
}
