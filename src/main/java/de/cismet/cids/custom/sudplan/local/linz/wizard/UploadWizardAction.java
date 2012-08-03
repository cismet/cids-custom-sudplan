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

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UploadWizardAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    // public static final String SWMM_WEBDAV_HOST = "https://sudplan.ait.ac.at/model/config/"
    public static final String SWMM_WEBDAV_HOST = "http://sudplan.cismet.de/tsDav/";
    public static final String SWMM_WEBDAV_USER = "tsDav";
    // public static final String SWMM_WEBDAV_USER = "SMS";
    public static final String SWMM_WEBDAV_PASSWORD = "RHfio2l4wrsklfghj";
    // public static final String SWMM_WEBDAV_PASSWORD = "cismet42";

    public static final String TABLENAME_SWMM_PROJECT = SwmmInput.TABLENAME_SWMM_PROJECT;
    public static final String PROP_NEW_SWMM_PROJECT_BEAN = "__prop_new_swmm_project_bean__";       // NOI18N
    public static final String PROP_SELECTED_SWMM_PROJECT_ID = "__prop_selected_swmm_project_id__"; // NOI18N
    public static final String PROP_SWMM_INP_FILE = "__prop_swmm_inp_file__";                       // NOI18N
    public static final String PROP_UPLOAD_COMPLETE = "__prop_upload_complete__";                   // NOI18N
    public static final String PROP_UPLOAD_ERRORNEOUS = "__prop_upload_erroneous__";                // NOI18N
    public static final String PROP_UPLOAD_IN_PROGRESS = "__prop_upload_in_progress__";             // NOI18N
    public static final String PROP_COPY_CSOS_COMPLETE = "__prop_copy_csos_complete__";             // NOI18N
    public static final String PROP_COPY_CSOS_ERRORNEOUS = "__prop_copy_csos_erroneous__";          // NOI18N
    public static final String PROP_COPY_CSOS_IN_PROGRESS = "__prop_copy_csos_in_progress__";       // NOI18N

    private static final transient Logger LOG = Logger.getLogger(UploadWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wizardDescriptor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UploadWizardAction object.
     */
    public UploadWizardAction() {
        super("Perform SWMM Project Upload");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Perform SWMM Project Upload Action instanciated");
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
                    new UploadWizardPanelProject(),
                    new UploadWizardPanelUpload(),
                    new UploadWizardPanelCSOs()
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

        final MetaClass swmmModelClass = ClassCacheMultiple.getMetaClass(
                SessionManager.getSession().getUser().getDomain(),
                TABLENAME_SWMM_PROJECT);

        final CidsBean newSwmmBean = swmmModelClass.getEmptyInstance().getBean();

        wizardDescriptor = new WizardDescriptor(this.getPanels());
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));                // NOI18N
        wizardDescriptor.setTitle(NbBundle.getMessage(
                UploadWizardAction.class,
                "UploadWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N

        if (this.getCidsBean() != null) {
            wizardDescriptor.putProperty(PROP_SELECTED_SWMM_PROJECT_ID,
                this.getCidsBean().getProperty("id"));
        } else {
            wizardDescriptor.putProperty(PROP_SELECTED_SWMM_PROJECT_ID, "-1");
        }

        wizardDescriptor.putProperty(PROP_NEW_SWMM_PROJECT_BEAN, newSwmmBean);
        wizardDescriptor.putProperty(PROP_SWMM_INP_FILE, "");
        wizardDescriptor.putProperty(PROP_UPLOAD_COMPLETE, false);
        wizardDescriptor.putProperty(PROP_UPLOAD_ERRORNEOUS, false);
        wizardDescriptor.putProperty(PROP_UPLOAD_IN_PROGRESS, false);
        wizardDescriptor.putProperty(PROP_COPY_CSOS_COMPLETE, false);
        wizardDescriptor.putProperty(PROP_COPY_CSOS_ERRORNEOUS, false);
        wizardDescriptor.putProperty(PROP_COPY_CSOS_IN_PROGRESS, false);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);

        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;

        if (!cancelled) {
            if (LOG.isDebugEnabled()) {
                LOG.info("wizard closed (not cancelled), saving new SWMM Model");
            }

//            try {
//                newSwmmBean.persist();
//            } catch (final Exception ex) {
//                final String message = "Cannot save new SWMM Model '"
//                            + newSwmmBean + "'";
//                LOG.error(message, ex);
//                JOptionPane.showMessageDialog(ComponentRegistry.getRegistry().getMainWindow(),
//                    message,
//                    NbBundle.getMessage(
//                        UploadWizardAction.class,
//                        "UploadWizardAction.actionPerformed(ActionEvent).wizard.error"),
//                    JOptionPane.ERROR_MESSAGE);
//            }
        } else {
            LOG.warn("Wizard cancelld! Uploaded SWMM INF File and copied CSOs will not be removed!");
        }
    }
}
