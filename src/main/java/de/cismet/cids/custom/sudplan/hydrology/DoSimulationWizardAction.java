/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.Feature;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

import se.smhi.sudplan.client.Scenario;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.text.DateFormat;
import java.text.MessageFormat;

import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SMSUtils.Model;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DoSimulationWizardAction extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SELECTED_WORKSPACE = "__prop_select_workspace__"; // NOI18N
    public static final String PROP_BASIN_ID = "__prop_basin_id__";                   // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(DoSimulationWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    private transient Integer basinId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AssignTimeseriesWizardAction object.
     *
     * @param   feature  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public DoSimulationWizardAction(final Feature feature) {
        super("Do Simulation"); // NOI18N

        final Object value = WFSUtils.getFeaturePropertyValue(
                feature,
                new QualifiedName(
                    ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                    "subid", // NOI18N
                    ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI));

        // the id is an integer, but the property value is a string
        if (value instanceof String) {
            basinId = Integer.valueOf((String)value);
        } else {
            throw new IllegalStateException("property value not instanceof string: " + value); // NOI18N
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        assert EventQueue.isDispatchThread() : "can only be called from EDT"; // NOI18N

        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new DoSimulationWizardPanelSelectWorkspace(),
                    new DoSimulationWizardPanelSelectScenario(),
                    new DoSimulationWizardPanelSelectTimerange(),
                    new DoSimulationWizardPanelSelectMetadata()
                };
            final String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                final Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) {
                    // assume Swing components
                    final JComponent jc = (JComponent)c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(i));
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the
                    // background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }

        return panels;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   e  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}")); // NOI18N
        wizard.setTitle("Do Simulation");

        final CidsBean currWorkspace = HydrologyCache.getInstance().getCurrentWorkspace();
        if (currWorkspace != null) {
            wizard.putProperty(PROP_SELECTED_WORKSPACE, currWorkspace);
        }

        wizard.putProperty(PROP_BASIN_ID, basinId);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final CidsBean hwBean = (CidsBean)wizard.getProperty(PROP_SELECTED_WORKSPACE);
            final Scenario scenario = (Scenario)wizard.getProperty(
                    DoSimulationWizardPanelSelectScenario.PROP_SELECTED_SCENARIO);
            final Date startDate = (Date)wizard.getProperty(
                    DoSimulationWizardPanelSelectTimerange.PROP_SELECTED_START_DATE);
            final Date endDate = (Date)wizard.getProperty(
                    DoSimulationWizardPanelSelectTimerange.PROP_SELECTED_END_DATE);
            final String name = (String)wizard.getProperty(DoSimulationWizardPanelSelectMetadata.PROP_NAME);
            final String desc = (String)wizard.getProperty(DoSimulationWizardPanelSelectMetadata.PROP_DESC);

            if (LOG.isDebugEnabled()) {
                LOG.debug("wizard results: [hwid=" + hwBean.getMetaObject().getID() // NOI18N
                            + "|scenario=" + scenario.getScenarioId()        // NOI18N
                            + "|startdate=" + DateFormat.getDateInstance().format(startDate) // NOI18N
                            + "|enddate=" + DateFormat.getDateInstance().format(endDate) // NOI18N
                            + "|name=" + name                                // NOI18N
                            + "|desc=" + desc + "]");                        // NOI18N
            }

            try {
                final User user = SessionManager.getSession().getUser();

                final SimulationInput input = new SimulationInput();
                input.setHydrologyWorkspaceId(hwBean.getMetaObject().getID());
                input.setScenario(scenario);
                input.setStartDate(startDate);
                input.setEndDate(endDate);
                input.setCreatedBy(user.toString());
                input.setCreated(new Date(System.currentTimeMillis()));

                final CidsBean inputBean = SMSUtils.createModelInput("Input of " + name, input, Model.HY_SIM);
                final CidsBean runBean = SMSUtils.createModelRun(name, desc, inputBean);
                runBean.persist();

                final List<CidsBean> simulations = hwBean.getBeanCollectionProperty("simulations"); // NOI18N
                simulations.add(runBean);
                hwBean.persist();

                SMSUtils.executeAndShowRun(runBean);
            } catch (final Exception ex) {
                final String message = "cannot do simulation"; // NOI18N
                LOG.error(message, ex);
                // TODO: throw proper exception
                throw new RuntimeException(message, ex);
            }
        }
    }
}
