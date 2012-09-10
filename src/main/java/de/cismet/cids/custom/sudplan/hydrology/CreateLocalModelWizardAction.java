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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.Feature;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

import se.smhi.sudplan.client.SudPlanHypeAPI;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.text.MessageFormat;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SMSUtils.Model;
import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CreateLocalModelWizardAction extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_POI_ID = "__prop_poi_id"; // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CreateLocalModelWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient int basinId;

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreateLocalModelWizardAction object.
     *
     * @param   feature  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public CreateLocalModelWizardAction(final Feature feature) {
        super("Create Local Model");

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
            panels = new WizardDescriptor.Panel[] { new CreateLocalModelWizardPanelMetadata() };
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
     * @return  DOCUMENT ME!
     */
    public int getBasinId() {
        return basinId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  basinId  DOCUMENT ME!
     */
    public void setBasinId(final int basinId) {
        this.basinId = basinId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}")); // NOI18N
        wizard.setTitle("Create Local Model");           // NOI18N

        wizard.putProperty(PROP_POI_ID, basinId);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final String name = (String)wizard.getProperty(CreateLocalModelWizardPanelMetadata.PROP_MODEL_NAME);
            final String desc = (String)wizard.getProperty(CreateLocalModelWizardPanelMetadata.PROP_MODEL_DESC);

            try {
                final User user = SessionManager.getSession().getUser();
                final MetaClass hwClass = ClassCacheMultiple.getMetaClass(user.getDomain(), "hydrology_workspace"); // NOI18N

                CidsBean hwBean = hwClass.getEmptyInstance().getBean();
                hwBean.setProperty("basin_id", basinId); // NOI18N
                hwBean.setProperty("name", name);        // NOI18N
                hwBean.setProperty("description", desc); // NOI18N
                hwBean = hwBean.persist();

                final String calSimId = createLocalModel(basinId);

                final CidsBean miBean = SMSUtils.createModelInput(name + " Calibration input",
                        new CalibrationInput(hwBean.getMetaObject().getID()),
                        Model.HY_CAL);

                final CidsBean runBean = SMSUtils.createModelRun(name + " Calibration run", null, miBean);

                hwBean.setProperty("local_model_id", calSimId); // NOI18N
                hwBean.setProperty("calibration", runBean);     // NOI18N
                hwBean = hwBean.persist();

                HydrologyCache.getInstance().setCurrentWorkspace(hwBean);

                ComponentRegistry.getRegistry().getCatalogueTree().requestRefreshNode("hydrology.localmodel"); // NOI18N
            } catch (final Exception ex) {
                LOG.error("cannot create new hydrology workspace", ex);                                        // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   basinId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private String createLocalModel(final int basinId) {
        final String cancelOption = "Cancel";
        final StatusPanel statusPanel = new StatusPanel("Please wait");
        final JOptionPane pane = new JOptionPane(
                statusPanel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.CANCEL_OPTION,
                null,
                new Object[] { cancelOption });
        statusPanel.setBusy(true);
        statusPanel.setStatusMessage("Creating Local Model...");

        final JDialog dialog = pane.createDialog(ComponentRegistry.getRegistry().getMainWindow(), "Please wait");

        final Future<String> task = SudplanConcurrency.getSudplanGeneralPurposePool().submit(new Callable<String>() {

                    @Override
                    public String call() {
                        try {
                            if (Thread.currentThread().isInterrupted()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("local model creator was interrupted"); // NOI18N
                                }

                                return null;
                            }

                            final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();

                            if (Thread.currentThread().isInterrupted()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("local model creator was interrupted"); // NOI18N
                                }

                                return null;
                            }

                            return hypeClient.createCalibrationSimulation(
                                    HydrologyCache.getInstance().getCalibrationScenario(),
                                    basinId);
                        } catch (final Exception ex) {
                            LOG.error("cannot create local model", ex); // NOI18N
                            try {
                                final ErrorInfo errorInfo = new ErrorInfo(
                                        "Hype Error",
                                        "Error while creating Local Model",
                                        "The Local Model could not be created because of an error",
                                        "ERROR",
                                        ex,
                                        Level.SEVERE,
                                        null);

                                EventQueue.invokeAndWait(new Runnable() {

                                        @Override
                                        public void run() {
                                            JXErrorPane.showDialog(dialog, errorInfo);
                                        }
                                    });
                            } catch (final Exception ex1) {
                                LOG.error("cannot display error dialog", ex1); // NOI18N
                            }

                            return null;
                        } finally {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        dialog.setVisible(false);
                                    }
                                });
                        }
                    }
                });

        StaticSwingTools.showDialog(dialog);

        if (cancelOption.equals(pane.getValue())) {
            // the cancel button has been pressed
            if (!task.isDone()) {
                if (!task.cancel(true)) {
                    LOG.warn("cannot cancel local model creator task"); // NOI18N
                }
            }

            return null;
        } else {
            try {
                return task.get();
            } catch (final Exception ex) {
                final String message = "cannot get calibration id"; // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }
        }
    }
}
