/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class DefaultModelManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(DefaultModelManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ModelManager model;
    private final transient ActionListener runL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnRun;
    private javax.swing.JProgressBar jpbStatus;
    private javax.swing.JLabel lblExecStatus;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MultiplyModelManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public DefaultModelManagerUI(final ModelManager model) {
        this.model = model;
        this.runL = new RunListener();

        // not using weaklistener because the execution listener deregisters itself when the execution is finised
        // TODO: handle execution over a long period of time
        // TODO: ensure that self-deregistration always works properly
        model.addProgressListener(new ExecutionListener());

        initComponents();

        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        if (EventQueue.isDispatchThread()) {
            performInit();
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        performInit();
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void performInit() {
        if (isStarted()) {
            btnRun.setEnabled(false);
            if (isFinished()) {
                lblStatus.setText(NbBundle.getMessage(
                        DefaultModelManagerUI.class,
                        "DefaultModelManagerUI.lblStatus.text.finished")); // NOI18N
                btnCancel.setEnabled(false);
            } else {
                lblStatus.setText(NbBundle.getMessage(
                        DefaultModelManagerUI.class,
                        "DefaultModelManagerUI.lblStatus.text.running"));  // NOI18N
                btnCancel.setEnabled(true);
            }
        } else {
            btnRun.addActionListener(WeakListeners.create(ActionListener.class, runL, btnRun));

            lblStatus.setText(NbBundle.getMessage(
                    DefaultModelManagerUI.class,
                    "DefaultModelManagerUI.lblStatus.text.runnable")); // NOI18N
            btnRun.setEnabled(true);
            btnCancel.setEnabled(false);
        }
        jpbStatus.setMaximum(1);
        jpbStatus.setValue(1);
        jpbStatus.setStringPainted(isStarted());
        jpbStatus.setIndeterminate(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isStarted() {
        return model.getCidsBean().getProperty("started") != null; // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isFinished() {
        return model.getCidsBean().getProperty("finished") != null; // NOI18N
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jpbStatus = new javax.swing.JProgressBar();
        lblExecStatus = new javax.swing.JLabel();
        btnRun = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jpbStatus.setBorderPainted(false);
        jpbStatus.setDoubleBuffered(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 419;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        add(jpbStatus, gridBagConstraints);

        lblExecStatus.setText(NbBundle.getMessage(
                DefaultModelManagerUI.class,
                "DefaultModelManagerUI.lblExecStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(lblExecStatus, gridBagConstraints);

        btnRun.setText(NbBundle.getMessage(DefaultModelManagerUI.class, "DefaultModelManagerUI.btnRun.text")); // NOI18N
        btnRun.setMaximumSize(new java.awt.Dimension(86, 29));
        btnRun.setMinimumSize(new java.awt.Dimension(86, 29));
        btnRun.setPreferredSize(new java.awt.Dimension(86, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 5, 10);
        add(btnRun, gridBagConstraints);

        btnCancel.setText(NbBundle.getMessage(DefaultModelManagerUI.class, "DefaultModelManagerUI.btnCancel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 5, 8);
        add(btnCancel, gridBagConstraints);

        lblStatus.setText(NbBundle.getMessage(DefaultModelManagerUI.class, "DefaultModelManagerUI.lblStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        add(lblStatus, gridBagConstraints);
    }                                                                                                                // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class RunListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            ExecutableThreadPool.getInstance().execute(model);
            btnRun.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ExecutionListener implements ProgressListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void progress(final ProgressEvent event) {
            if (EventQueue.isDispatchThread()) {
                handleProgress(event);
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            handleProgress(event);
                        }
                    });
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  event  DOCUMENT ME!
         */
        private void handleProgress(final ProgressEvent event) {
            if (ProgressEvent.State.STARTED.equals(event.getState())) {
                lblStatus.setText(NbBundle.getMessage(
                        DefaultModelManagerUI.class,
                        "DefaultModelManagerUI.lblStatus.text.started"));  // NOI18N
                jpbStatus.setIndeterminate(true);
            } else if (ProgressEvent.State.PROGRESSING.equals(event.getState())) {
                lblStatus.setText(NbBundle.getMessage(
                        DefaultModelManagerUI.class,
                        "DefaultModelManagerUI.lblStatus.text.running"));  // NOI18N
                if (event.getMaxSteps() < 1) {
                    jpbStatus.setIndeterminate(true);
                } else {
                    jpbStatus.setIndeterminate(false);
                    jpbStatus.setMaximum(event.getMaxSteps());
                    jpbStatus.setValue(event.getStep());
                    jpbStatus.setStringPainted(true);
                }
            } else if (ProgressEvent.State.FINISHED.equals(event.getState())) {
                lblStatus.setText(NbBundle.getMessage(
                        DefaultModelManagerUI.class,
                        "DefaultModelManagerUI.lblStatus.text.finished")); // NOI18N
                model.removeProgressListener(this);
            }
        }
    }
}
