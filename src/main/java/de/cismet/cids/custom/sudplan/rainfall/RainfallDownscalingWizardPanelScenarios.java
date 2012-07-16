/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import net.opengis.sps.v_1_0_0.InputDescriptor;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.EventQueue;

import java.util.List;
import java.util.Properties;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.DataHandlerCache;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallDownscalingWizardPanelScenarios implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RainfallDownscalingWizardPanelScenarios.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient RainfallDownscalingVisualPanelScenarios component;
    private transient String scenario;
    private transient String[] scenarios;
    private transient Exception spsError;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public RainfallDownscalingWizardPanelScenarios() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new RainfallDownscalingVisualPanelScenarios(this);
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
        spsError = null;
        scenario = (String)wizard.getProperty(RainfallDownscalingWizardAction.PROP_SCENARIO);
        scenarios = new String[] {};

        SudplanConcurrency.getSudplanGeneralPurposePool().execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        final DataHandler dh = DataHandlerCache.getInstance()
                                    .getSPSDataHandler(
                                        RainfallDownscalingModelManager.RF_SPS_LOOKUP,
                                        RainfallDownscalingModelManager.RF_SPS_URL);
                        final Properties filter = new Properties();
                        filter.put(TimeSeries.PROCEDURE, RainfallDownscalingModelManager.RF_TS_DS_PROCEDURE);
                        final Datapoint dp = dh.createDatapoint(filter, null, DataHandler.Access.READ);
                        final InputDescriptor id = (InputDescriptor)dp.getProperties()
                                    .get("jaxb_desc:climate_scenario");  // NOI18N
                        final List<String> scenarioList = id.getDefinition()
                                    .getCommonData()
                                    .getCategory()
                                    .getConstraint()
                                    .getAllowedTokens()
                                    .getValueList()
                                    .get(0)
                                    .getValue();
                        scenarios = scenarioList.toArray(new String[scenarioList.size()]);
                    } catch (final Exception ex) {
                        LOG.error("error during sps communication", ex); // NOI18N
                        spsError = ex;
                    } finally {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    component.init();

                                    fireChangeEvent();
                                }
                            });
                    }
                }
            });

        component.init();
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
     * @return  DOCUMENT ME!
     */
    String[] getScenarios() {
        return scenarios;
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(RainfallDownscalingWizardAction.PROP_SCENARIO, component.getSelectedScenario());
    }

    @Override
    public boolean isValid() {
        if (spsError != null) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                "An error occurred during SPS communication");

            return false;
        }

        final String choosenScenario = component.getSelectedScenario();
        boolean valid = false;

        if (choosenScenario == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    RainfallDownscalingWizardPanelScenarios.class,
                    "RainfallDownscalingWizardPanelScenarios.isValid().selectScenario")); // NOI18N
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
            valid = true;
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
