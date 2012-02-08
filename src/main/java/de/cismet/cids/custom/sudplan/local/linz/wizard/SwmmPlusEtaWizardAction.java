/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.local.linz.EtaInput;
import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmPlusEtaWizardAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TABLENAME_SWMM_PROJECT = SwmmInput.TABLENAME_SWMM_PROJECT;
    public static final String TABLENAME_MONITOR_STATION = SwmmInput.TABLENAME_MONITOR_STATION;
    public static final String TABLENAME_TIMESERIES = "timeseries";
    public static final String TABLENAME_CSOS = "linz_cso";
    // public static final String PROP_SCENARIO = "__prop_scenario__";       // NOI18N public static final String
    // PROP_TARGET_YEAR = "__prop_target_year__"; // NOI18N
    /** Name of the model run. */
    public static final String PROP_SWMM_PROJECT_BEAN = "__prop_swmm_project_bean__"; // NOI18N
    public static final String PROP_SWMM_INPUT = "__prop_swmm_input__";               // NOI18N
    public static final String PROP_ETA_INPUT = "__prop_eta_input__";                 // NOI18N
    public static final String PROP_STATION_IDS = "__prop_station_ids__";             // NOI18N
    /** Name of the model run. */
    public static final String PROP_NAME = "__prop_name__"; // NOI18N
    /** Description of the model run. */
    public static final String PROP_DESCRIPTION = "__prop_description__"; // NOI18N
    private static final transient Logger LOG = Logger.getLogger(SwmmPlusEtaWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardAction object.
     */
    public SwmmPlusEtaWizardAction() {
        super("Perform SWMM + ETA calculation");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Perform SWMM + ETA calculation");
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * EDT only !
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        assert EventQueue.isDispatchThread() : "can only be called from EDT"; // NOI18N

        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    // new RainfallDownscalingWizardPanelScenarios(),
                    // new RainfallDownscalingWizardPanelTargetDate(),
                    new SwmmWizardPanelProject(),
                    new SwmmWizardPanelStations(),
                    new SwmmWizardPanelTimeseries(),
                    new EtaWizardPanelEtaConfiguration(),
                    new WizardPanelMetadata()
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
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        LOG.info("Wizard actionPerformed: " + e.getActionCommand());
        final CidsBean cidsBean = getCidsBean();
        assert cidsBean != null : "cidsbean not set";                            // NOI18N
        assert cidsBean.getMetaObject() != null : "cidsbean without metaobject"; // NOI18N

        final MetaObject mo = cidsBean.getMetaObject();
        final MetaClass mc = mo.getMetaClass();

        assert mc != null : "metaobject without metaclass"; // NOI18N

        if (TABLENAME_SWMM_PROJECT.equals(mc.getTableName())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("opening wizard with swmm project:" + mo);
            }
            final WizardDescriptor wizard = new WizardDescriptor(getPanels());
            wizard.setTitleFormat(new MessageFormat("{0}"));                               // NOI18N
            wizard.setTitle(NbBundle.getMessage(
                    SwmmPlusEtaWizardAction.class,
                    "SwmmPlusEtaWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N

            wizard.putProperty(PROP_SWMM_PROJECT_BEAN, cidsBean);
            wizard.putProperty(PROP_SWMM_INPUT, new SwmmInput());
            wizard.putProperty(PROP_STATION_IDS, new ArrayList<Integer>());
            wizard.putProperty(PROP_ETA_INPUT, new EtaInput());

            final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);

            dialog.pack();
            dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
            dialog.setVisible(true);
            dialog.toFront();

            final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("wizard closed (not cancelled), creating new SWMM+ETA Runs");
                }
                try {
                    final CidsBean swmmModelInput = this.createSwmmModelInput(wizard);
                    CidsBean swmmModelRun = this.createSwmmModelRun(wizard, swmmModelInput);

                    final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
                    swmmModelRun.setProperty(
                        "started", // NOI18N
                        dateConverter.convertReverse(GregorianCalendar.getInstance().getTime()));

                    swmmModelRun = swmmModelRun.persist();

                    // .........................................................

                    final CidsBean etaModelInput = this.createEtaModelInput(
                            wizard,
                            Integer.valueOf(swmmModelRun.getProperty("id").toString()));
                    CidsBean etaModelRun = this.createEtaModelRun(wizard, etaModelInput);
                    etaModelRun = etaModelRun.persist();

                    final List<CidsBean> swmmScenarios = (List)cidsBean.getProperty("swmm_scenarios"); // NOI18N
                    swmmScenarios.add(swmmModelRun);
                    final List<CidsBean> etaScenarios = (List)cidsBean.getProperty("eta_scenarios");   // NOI18N
                    etaScenarios.add(etaModelRun);

                    // cidsBean = swmmProject
                    cidsBean.persist();

                    SMSUtils.executeAndShowRun(etaModelRun);
                } catch (final Exception ex) {
                    final String message = "Cannot perform SWMM+ETA calculation";
                    LOG.error(message, ex);
                    JOptionPane.showMessageDialog(ComponentRegistry.getRegistry().getMainWindow(),
                        message,
                        NbBundle.getMessage(
                            SwmmPlusEtaWizardAction.class,
                            "SwmmPlusEtaWizardAction.actionPerformed(ActionEvent).wizard.error"),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            LOG.warn("can only perform this action on objects of metaclass SWMM_PROJECT"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createSwmmModelInput(final WizardDescriptor wizard) throws IOException {
        final SwmmInput swmmInput = (SwmmInput)wizard.getProperty(PROP_SWMM_INPUT);
        final Date created = GregorianCalendar.getInstance().getTime();
        final String user = SessionManager.getSession().getUser().getName();
        final String name = (String)wizard.getProperty(PROP_NAME);
        final String runName = name + " (SWMM 5.0)";
        final String inputName = "Modellkonfiguration " + runName;

        swmmInput.setCreated(created);
        swmmInput.setUser(user);
        return SMSUtils.createModelInput(inputName, swmmInput, SMSUtils.Model.SWMM);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard     DOCUMENT ME!
     * @param   swmmRunId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createEtaModelInput(final WizardDescriptor wizard, final int swmmRunId) throws IOException {
        final EtaInput etaInput = (EtaInput)wizard.getProperty(PROP_ETA_INPUT);
        final Date created = GregorianCalendar.getInstance().getTime();
        final String user = SessionManager.getSession().getUser().getName();
        final String name = (String)wizard.getProperty(PROP_NAME);
        final String runName = name + " (ETA)";
        final String inputName = "Modellkonfiguration " + runName;
        final String swmmRunName = name + " (SWMM 5.0)";

        etaInput.setCreated(created);
        etaInput.setUser(user);
        etaInput.setSwmmRun(swmmRunId);
        etaInput.setSwmmRunName(swmmRunName);

        return SMSUtils.createModelInput(inputName, etaInput, SMSUtils.Model.LINZ_ETA);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard     DOCUMENT ME!
     * @param   inputBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createSwmmModelRun(final WizardDescriptor wizard, final CidsBean inputBean) throws IOException {
        final String name = (String)wizard.getProperty(PROP_NAME);
        final String runName = name + " (SWMM 5.0)";
        final String description = (String)wizard.getProperty(PROP_DESCRIPTION);

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new swmm modelrun: " // NOI18N
                        + "name=" + runName    // NOI18N
                        + " || description=" + description // NOI18N
                        + " || cidsbean=" + inputBean); // NOI18N
        }

        return SMSUtils.createModelRun(runName, description, inputBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard     DOCUMENT ME!
     * @param   inputBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createEtaModelRun(final WizardDescriptor wizard, final CidsBean inputBean) throws IOException {
        final String name = (String)wizard.getProperty(PROP_NAME);
        final String runName = name + " (ETA)";
        final String description = (String)wizard.getProperty(PROP_DESCRIPTION);

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new swmm modelrun: " // NOI18N
                        + "name=" + runName    // NOI18N
                        + " || description=" + description // NOI18N
                        + " || cidsbean=" + inputBean); // NOI18N
        }

        return SMSUtils.createModelRun(runName, description, inputBean);
    }
}
