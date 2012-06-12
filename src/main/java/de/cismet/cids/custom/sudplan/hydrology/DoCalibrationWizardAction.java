/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DoCalibrationWizardAction extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SELECTED_CALIBRATION_INPUT = "__prop_current_calibration__"; // NOI18N
    public static final String PROP_BASIN_ID = "__prop_basin_id__";                              // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(DoCalibrationWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AssignTimeseriesWizardAction object.
     */
    public DoCalibrationWizardAction() {
        super("Do Calibration"); // NOI18N
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
                    new AssignTimeseriesWizardPanelSelectCalibration(),
                    new DoCalibrationWizardPanelMetadata()
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

    @Override
    public void actionPerformed(final ActionEvent e) {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}")); // NOI18N
        wizard.setTitle("Create Local Model");           // NOI18N

        final CidsBean currWorkspace = HydrologyCache.getInstance().getCurrentWorkspace();
        if (currWorkspace != null) {
            wizard.putProperty(
                AssignTimeseriesWizardAction.PROP_SELECTED_CALIBRATION_INPUT,
                currWorkspace.getProperty("calibration.modelinput")); // NOI18N
        }

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final String name = (String)wizard.getProperty(DoCalibrationWizardPanelMetadata.PROP_NAME);
            final String desc = (String)wizard.getProperty(DoCalibrationWizardPanelMetadata.PROP_DESC);
            final CidsBean selectedCalInput = (CidsBean)wizard.getProperty(
                    AssignTimeseriesWizardAction.PROP_SELECTED_CALIBRATION_INPUT);

            try {
                CidsBean runBean = SMSUtils.loadModelManagerBeanFromIO(selectedCalInput);
                runBean.setProperty("name", name);
                runBean.setProperty("description", desc);

                runBean = runBean.persist();

                // the dialog is modal, so we don't have to care about concurrent modification, unless some background
                // thread does it for no apparent reason, to cut a long story short: mark theworkspace for reload to
                // ensure freshness, if it is still the selected one
                if ((currWorkspace != null)
                            && selectedCalInput.equals(currWorkspace.getProperty("calibration.modelinput"))) {
                    reloadWorkspace(currWorkspace);
                }

                SMSUtils.executeAndShowRun(runBean);
            } catch (final Exception ex) {
                final String message = "cannot do calibration"; // NOI18N
                LOG.error(message, ex);
                // TODO: throw proper exception
                throw new RuntimeException(message, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   workspace  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private void reloadWorkspace(final CidsBean workspace) throws ConnectionException {
        final MetaObject womo = workspace.getMetaObject();
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaObject newWomo = SessionManager.getProxy().getMetaObject(womo.getID(), womo.getClassID(), domain);

        HydrologyCache.getInstance().setCurrentWorkspace(newWomo.getBean());
    }
}
