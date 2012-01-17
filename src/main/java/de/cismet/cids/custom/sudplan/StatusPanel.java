/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import java.awt.EventQueue;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class StatusPanel extends javax.swing.JPanel {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    private final transient org.jdesktop.swingx.JXBusyLabel lblProgress = new org.jdesktop.swingx.JXBusyLabel();
    private final transient javax.swing.JLabel lblStatusMsg = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TimeSeriesImportFileChoosePanel.
     *
     * @param  name  DOCUMENT ME!
     */
    public StatusPanel(final String name) {
        initComponents();

        setName(name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  statusMsg  DOCUMENT ME!
     */
    public void setStatusMessage(final String statusMsg) {
        if (EventQueue.isDispatchThread()) {
            this.lblStatusMsg.setText(statusMsg);
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        StatusPanel.this.lblStatusMsg.setText(statusMsg);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isBusy  DOCUMENT ME!
     */
    public void setBusy(final boolean isBusy) {
        if (EventQueue.isDispatchThread()) {
            this.lblProgress.setBusy(isBusy);
            this.lblProgress.setVisible(isBusy);
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        StatusPanel.this.lblProgress.setBusy(isBusy);
                        StatusPanel.this.lblProgress.setVisible(isBusy);
                    }
                });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblProgress.setText(org.openide.util.NbBundle.getMessage(StatusPanel.class, "StatusPanel.lblProgress.text")); // NOI18N
        jPanel1.add(lblProgress, new java.awt.GridBagConstraints());

        lblStatusMsg.setFont(new java.awt.Font("DejaVu Sans", 1, 13));                                                  // NOI18N
        lblStatusMsg.setText(org.openide.util.NbBundle.getMessage(StatusPanel.class, "StatusPanel.lblStatusMsg.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        jPanel1.add(lblStatusMsg, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents
}