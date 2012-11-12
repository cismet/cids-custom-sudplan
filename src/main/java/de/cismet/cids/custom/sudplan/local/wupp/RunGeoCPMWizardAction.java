/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunGeoCPMWizardAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_INPUT_BEAN = "__prop_input_bean__";         // NOI18N
    public static final String PROP_RAINEVENT_BEAN = "__prop_rainevent_bean__"; // NOI18N
    public static final String PROP_NAME = "__prop_name__";                     // NOI18N
    public static final String PROP_DESCRIPTION = "__prop_description__";       // NOI18N

    private static final transient Logger LOG = Logger.getLogger(RunGeoCPMWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardAction object.
     */
    public RunGeoCPMWizardAction() {
        // TODO: add icon
        super(NbBundle.getMessage(RunGeoCPMWizardAction.class, "RunGeoCPMWizardAction.constructor().actionName")); // NOI18N
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
                    new RunGeoCPMWizardPanelInput(),
                    new RunGeoCPMWizardPanelRainevent(),
                    new RunGeoCPMWizardPanelMetadata()
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
        final CidsBean cidsBean = getCidsBean();
        assert cidsBean != null : "cidsbean not set";                            // NOI18N
        assert cidsBean.getMetaObject() != null : "cidsbean without metaobject"; // NOI18N

        final MetaObject mo = cidsBean.getMetaObject();
        final MetaClass mc = mo.getMetaClass();

        assert mc != null : "metaobject without metaclass"; // NOI18N

        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}"));                             // NOI18N
        wizard.setTitle(NbBundle.getMessage(
                RunGeoCPMWizardAction.class,
                "RunGeoCPMWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N

        if (SMSUtils.TABLENAME_GEOCPM_CONFIGURATION.equalsIgnoreCase(mc.getTableName())
                    || SMSUtils.TABLENAME_DELTA_CONFIGURATION.equalsIgnoreCase(mc.getTableName())) {
            wizard.putProperty(PROP_INPUT_BEAN, cidsBean);
        } else if (SMSUtils.TABLENAME_RAINEVENT.equalsIgnoreCase(mc.getTableName())) {
            wizard.putProperty(PROP_RAINEVENT_BEAN, cidsBean);
        }

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;

        if (!cancelled) {
            try {
                final CidsBean modelInput = createModelInput(wizard);

                CidsBean modelRun = createModelRun(wizard, modelInput);

                modelRun = modelRun.persist();

                attachScenario(modelRun, wizard);

                SMSUtils.executeAndShowRun(modelRun);

                lockInputConfig(wizard);
            } catch (final Exception ex) {
                // TODO: proper error panel
                final String message = "Cannot perform geocpm run";
                LOG.error(message, ex);
                JOptionPane.showMessageDialog(ComponentRegistry.getRegistry().getMainWindow(),
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelRun  DOCUMENT ME!
     * @param   wizard    DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void attachScenario(final CidsBean modelRun, final WizardDescriptor wizard) throws IOException {
        final CidsBean input = (CidsBean)wizard.getProperty(PROP_INPUT_BEAN);
        final String iaProperty;

        if (SMSUtils.TABLENAME_DELTA_CONFIGURATION.equals(input.getMetaObject().getMetaClass().getTableName())) {
            iaProperty = "original_object.investigation_area";           // NOI18N
        } else {
            iaProperty = "investigation_area";
        }
        final CidsBean iaBean = (CidsBean)input.getProperty(iaProperty); // NOI18N

        final List<CidsBean> scenarios = (List)iaBean.getProperty("scenarios"); // NOI18N
        scenarios.add(modelRun);

        try {
            iaBean.persist();
        } catch (final Exception ex) {
            final String message = "cannot attach modelrun to investigation area"; // NOI18N
            throw new IOException(message, ex);
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
    private CidsBean createModelInput(final WizardDescriptor wizard) throws IOException {
        final CidsBean input = (CidsBean)wizard.getProperty(PROP_INPUT_BEAN);
        final CidsBean rainevent = (CidsBean)wizard.getProperty(PROP_RAINEVENT_BEAN);

        assert input != null : "input was not set";         // NOI18N
        assert rainevent != null : "rainevent was not set"; // NOI18N

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new geocpm modelinput: " // NOI18N
                        + "input=" + input         // NOI18N
                        + " || rainevent=" + rainevent); // NOI18N
        }

        final String wizName = (String)wizard.getProperty(PROP_NAME);
        final String name = "GeoCPM run input (" + wizName + ")";

        final RunoffInput runoffIO = new RunoffInput();

        if (SMSUtils.TABLENAME_DELTA_CONFIGURATION.equals(input.getMetaObject().getMetaClass().getTableName())) {
            runoffIO.setDeltaInputId(input.getMetaObject().getID());
            runoffIO.setGeocpmInputId(((CidsBean)input.getProperty("original_object")).getMetaObject().getID());
        } else {
            runoffIO.setGeocpmInputId(input.getMetaObject().getID());
        }

        runoffIO.setRaineventId(rainevent.getMetaObject().getID());

        return SMSUtils.createModelInput(name, runoffIO, SMSUtils.Model.GEOCPM);
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
    private CidsBean createModelRun(final WizardDescriptor wizard, final CidsBean inputBean) throws IOException {
        final String name = (String)wizard.getProperty(PROP_NAME);
        final String description = (String)wizard.getProperty(PROP_DESCRIPTION);

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new geocpm modelrun: " // NOI18N
                        + "name=" + name         // NOI18N
                        + " || description=" + description // NOI18N
                        + " || cidsbean=" + inputBean); // NOI18N
        }

        return SMSUtils.createModelRun(name, description, inputBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void lockInputConfig(final WizardDescriptor wizard) throws Exception {
        final CidsBean input = (CidsBean)wizard.getProperty(PROP_INPUT_BEAN);

        assert input != null : "input was not set";

        if (
            !SMSUtils.TABLENAME_DELTA_CONFIGURATION.equalsIgnoreCase(
                        input.getMetaObject().getMetaClass().getTableName())) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("lock input configuration: "
                        + "input=" + input);
        }

        input.setProperty("locked", true);
        input.persist();
        // Refresh object katalogue and widged
        final Integer investID = (Integer)input.getProperty("original_object.investigation_area.id");
        ComponentRegistry.getRegistry()
                .getCatalogueTree()
                .requestRefreshNode("wupp.investigation_area." + investID + ".config");
        DeltaConfigurationListWidged.getInstance().fireConfigsChanged();
    }

    @Override
    public boolean isEnabled() {
        boolean isEnabled = false;

        final User user = SessionManager.getSession().getUser();
        final Callable<Boolean> enable = new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    try {
                        return SessionManager.getProxy().hasConfigAttr(user, "sudplan.local.wupp.geocpm.run"); // NOI18N
                    } catch (final ConnectionException ex) {
                        LOG.warn("cannot check for config attr", ex);                                          // NOI18N
                        return false;
                    }
                }
            };

        final Future<Boolean> future = SudplanConcurrency.getSudplanGeneralPurposePool().submit(enable);
        try {
            isEnabled = future.get(300, TimeUnit.MILLISECONDS);
        } catch (final Exception ex) {
            LOG.warn("cannot get result of enable future", ex); // NOI18N
        }

        setEnabled(isEnabled);

        return isEnabled;
    }
}
