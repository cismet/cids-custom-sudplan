/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.openide.WizardDescriptor;

import se.smhi.sudplan.client.Scenario;

import java.awt.Component;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DoSimulationWizardPanelSelectScenario extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SELECTED_SCENARIO = "__prop_selected_scenario__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient Scenario selectedScenario;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Scenario getSelectedScenario() {
        return selectedScenario;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedScenario  DOCUMENT ME!
     */
    public void setSelectedScenario(final Scenario selectedScenario) {
        this.selectedScenario = selectedScenario;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Scenario> getAvailableScenarios() {
        return new ArrayList<Scenario>(HydrologyCache.getInstance().getSimulationScenarios());
    }

    @Override
    protected Component createComponent() {
        return new DoSimulationVisualPanelSelectScenario(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        setSelectedScenario((Scenario)wizard.getProperty(PROP_SELECTED_SCENARIO));

        ((DoSimulationVisualPanelSelectScenario)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_SELECTED_SCENARIO, selectedScenario);
    }
}
