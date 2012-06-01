/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import net.opengis.sps.v_1_0_0.InputDescriptor;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;

import java.util.List;
import java.util.Properties;

import de.cismet.cids.custom.sudplan.DataHandlerCache;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.dataImport.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelScenario extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient AirqualityDownscalingVisualPanelScenario component;
    private transient String scenario;
    private transient String[] scenariosFromSPS;
    private transient Exception spsError;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final AirqualityDownscalingWizardPanelScenario panel =
                        new AirqualityDownscalingWizardPanelScenario();
                    panel.createComponent();
                    final WizardDescriptor wizard = new WizardDescriptor(new WizardDescriptor.Panel[] { panel });
                    final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    dialog.toFront();
                }
            });
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelScenario(this);
        }

        return component;
    }

    @Override
    public void read(final WizardDescriptor wizard) {
        scenario = (String)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_SCENARIO);
        scenariosFromSPS = (String[])wizard.getProperty(AirqualityDownscalingWizardAction.PROP_SCENARIOS);

        if ((scenariosFromSPS != null) && (scenariosFromSPS.length > 0)) {
            component.init();
            return;
        }

        spsError = null;

        SudplanConcurrency.getSudplanGeneralPurposePool().execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        final Properties filter = new Properties();
                        filter.put(TimeSeries.PROCEDURE, AirqualityDownscalingModelManager.AQ_TS_DS_PROCEDURE);

                        final DataHandler dataHandler = DataHandlerCache.getInstance()
                                    .getSPSDataHandler(
                                        AirqualityDownscalingModelManager.AQ_SPS_LOOKUP,
                                        AirqualityDownscalingModelManager.AQ_SPS_URL);
                        final Datapoint datapoint = dataHandler.createDatapoint(filter, null, DataHandler.Access.READ);
                        final InputDescriptor inputDescriptor = (InputDescriptor)datapoint.getProperties()
                                    .get("jaxb_desc:climate_scenario"); // NOI18N
                        final List<String> scenarios = inputDescriptor.getDefinition()
                                    .getCommonData()
                                    .getCategory()
                                    .getConstraint()
                                    .getAllowedTokens()
                                    .getValueList()
                                    .get(0)
                                    .getValue();
                        AirqualityDownscalingWizardPanelScenario.this.scenariosFromSPS = scenarios.toArray(
                                new String[scenarios.size()]);
                    } catch (final Exception ex) {
                        spsError = ex;
                    } finally {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    component.init();

                                    changeSupport.fireChange();
                                }
                            });
                    }
                }
            });

        component.init();
    }

    @Override
    public void store(final WizardDescriptor wizard) {
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_SCENARIO, scenario);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_SCENARIOS, scenariosFromSPS);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getScenario() {
        return scenario;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  scenario  DOCUMENT ME!
     */
    public void setScenario(final String scenario) {
        this.scenario = scenario;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String[] getScenariosFromSPS() {
        return scenariosFromSPS;
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        if (spsError != null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelScenario.class,
                    "AirqualityDownscalingWizardPanelScenario.isValid().spsError"));
            return false;
        }

        if (scenario == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelScenario.class,
                    "AirqualityDownscalingWizardPanelScenario.isValid().noScenario"));
            return false;
        }

        return true;
    }
}
