/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.SMSUtils;

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

    public static final String PROP_INPUT_BEAN = "__prop_input_bean__";           // NOI18N
    public static final String PROP_TIMESERIES_BEAN = "__prop_timeseries_bean__"; // NOI18N
    public static final String PROP_NAME = "__prop_name__";                       // NOI18N
    public static final String PROP_DESCRIPTION = "__prop_description__";         // NOI18N

    public static final String TABLENAME_GEOCPM_CONFIG = "GEOCPM_CONFIG"; // NOI18N
    public static final String TABLENAME_TIMESERIES = "TIMESERIES";       // NOI18N

    private static final transient Logger LOG = Logger.getLogger(RunGeoCPMWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardAction object.
     */
    public RunGeoCPMWizardAction() {
        super("Perform GeoCPM Run");
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
                    new RunGeoCPMWizardPanelTimerseries(),
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

        if (TABLENAME_GEOCPM_CONFIG.equalsIgnoreCase(mc.getTableName())) {
            wizard.putProperty(PROP_INPUT_BEAN, cidsBean);
        } else if (TABLENAME_TIMESERIES.equalsIgnoreCase(mc.getTableName())) {
            wizard.putProperty(PROP_TIMESERIES_BEAN, cidsBean);
        }

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            try {
                CidsBean modelInput = createModelInput(wizard);
                CidsBean modelRun = createModelRun(wizard, modelInput);

                modelRun = modelRun.persist();
                modelInput = (CidsBean)modelRun.getProperty("modelinput"); // NOI18N

                SMSUtils.executeAndShowRun(modelRun);
            } catch (final Exception ex) {
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
     * @param   wizard  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createModelInput(final WizardDescriptor wizard) throws IOException {
        final CidsBean input = (CidsBean)wizard.getProperty(PROP_INPUT_BEAN);
        final CidsBean timeseries = (CidsBean)wizard.getProperty(PROP_TIMESERIES_BEAN);

        assert input != null : "input was not set";           // NOI18N
        assert timeseries != null : "timeseries was not set"; // NOI18N

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new geocpm modelinput: " // NOI18N
                        + "input=" + input         // NOI18N
                        + " || timeseries=" + timeseries); // NOI18N
        }

        final Date created = GregorianCalendar.getInstance().getTime();
        final String user = SessionManager.getSession().getUser().getName();

        final String wizName = (String)wizard.getProperty(PROP_NAME);
        final String name = "GeoCPM run input (" + wizName + ")";

        final RunoffIO runoffIO = new RunoffIO();
        runoffIO.setGeocpmInput(input.getMetaObject().getID());
        runoffIO.setTimeseries(timeseries.getMetaObject().getID());

        return SMSUtils.createModelInput(name, input, SMSUtils.Model.GEOCPM);
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

    // FIXME: better action enable
    @Override
    public boolean isEnabled() {
        if (getCidsBean() == null) {
            LOG.warn("source == null, geocpm run action disabled"); // NOI18N

            return false;
        } else {
            return SessionManager.getSession().getUser().getUserGroup().getName().equalsIgnoreCase("Wuppertal");
        }
    }
}