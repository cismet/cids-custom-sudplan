/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;

import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class DeltaSurfaceConflictDialog extends javax.swing.JDialog {

    //~ Instance fields --------------------------------------------------------

    private final transient boolean isSurfaceConflict;
    private final transient boolean isBreakingedgeConflict;
    private final transient ChangeListener cL;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnConflicts;
    private javax.swing.JButton btnWizard;
    private javax.swing.JCheckBox cbEdges;
    private javax.swing.JCheckBox cbSurfaces;
    private javax.swing.JLabel lblFeed;
    private javax.swing.JLabel lblHead;
    private javax.swing.JLabel lblMessage;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DeltaSurfaceConflictDialog.
     *
     * @param  parent                  DOCUMENT ME!
     * @param  modal                   DOCUMENT ME!
     * @param  isSurfaceConflict       DOCUMENT ME!
     * @param  isBreakingedgeConflict  DOCUMENT ME!
     */
    public DeltaSurfaceConflictDialog(final java.awt.Frame parent,
            final boolean modal,
            final boolean isSurfaceConflict,
            final boolean isBreakingedgeConflict) {
        super(parent, modal);
        cL = new CheckBoxChangeListener();
        this.isSurfaceConflict = isSurfaceConflict;
        this.isBreakingedgeConflict = isBreakingedgeConflict;
        initComponents();

        cbSurfaces.addChangeListener(WeakListeners.change(cL, cbSurfaces));
        cbEdges.addChangeListener(WeakListeners.change(cL, cbEdges));

        cbSurfaces.setVisible(this.isSurfaceConflict);
        cbSurfaces.setSelected(this.isSurfaceConflict);
        cbEdges.setVisible(this.isBreakingedgeConflict);
        cbEdges.setSelected(this.isBreakingedgeConflict);
        btnWizard.setEnabled(!this.isBreakingedgeConflict);
        if (btnWizard.isEnabled()) {
            lblMessage.setIcon(ImageUtilities.loadImageIcon("org/netbeans/modules/dialogs/warning.gif", false));
            lblMessage.setText("Das ist eine Warnung");
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSurfaceConflictSelected() {
        return (cbSurfaces.isSelected() && cbSurfaces.isVisible());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isBreakingedgeConflictSelected() {
        return (cbEdges.isSelected() && cbEdges.isValid());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void addShowConflictsButtonActionListener(final ActionListener al) {
        btnConflicts.addActionListener(al);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void removeShowConflictsButtonActionListener(final ActionListener al) {
        btnConflicts.removeActionListener(al);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void addWizardButtonActionListener(final ActionListener al) {
        btnWizard.addActionListener(al);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void removeWizardButtonActionListener(final ActionListener al) {
        btnWizard.removeActionListener(al);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void addCancelButtonActionListener(final ActionListener al) {
        btnCancel.addActionListener(al);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void removeCancelButtonActionListener(final ActionListener al) {
        btnCancel.removeActionListener(al);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblHead = new javax.swing.JLabel();
        cbEdges = new javax.swing.JCheckBox();
        cbSurfaces = new javax.swing.JCheckBox();
        lblFeed = new javax.swing.JLabel();
        btnConflicts = new javax.swing.JButton();
        btnWizard = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblHead.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.lblHead.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblHead, gridBagConstraints);

        cbEdges.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.cbEdges.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cbEdges, gridBagConstraints);

        cbSurfaces.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.cbSurfaces.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cbSurfaces, gridBagConstraints);

        lblFeed.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.lblFeed.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblFeed, gridBagConstraints);

        btnConflicts.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.btnConflicts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btnConflicts, gridBagConstraints);

        btnWizard.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.btnWizard.text")); // NOI18N
        btnWizard.setMaximumSize(new java.awt.Dimension(100, 29));
        btnWizard.setMinimumSize(new java.awt.Dimension(100, 29));
        btnWizard.setPreferredSize(new java.awt.Dimension(100, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btnWizard, gridBagConstraints);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.btnCancel.text")); // NOI18N
        btnCancel.setMaximumSize(new java.awt.Dimension(80, 29));
        btnCancel.setMinimumSize(new java.awt.Dimension(80, 29));
        btnCancel.setPreferredSize(new java.awt.Dimension(80, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(btnCancel, gridBagConstraints);

        lblMessage.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictDialog.class,
                "DeltaSurfaceConflictDialog.lblMessage.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblMessage, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DeltaSurfaceConflictDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DeltaSurfaceConflictDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DeltaSurfaceConflictDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DeltaSurfaceConflictDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final DeltaSurfaceConflictDialog dialog = new DeltaSurfaceConflictDialog(
                            new javax.swing.JFrame(),
                            false,
                            true,
                            false);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                            @Override
                            public void windowClosing(final java.awt.event.WindowEvent e) {
                                System.exit(0);
                            }
                        });
                    dialog.setVisible(true);
                }
            });
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CheckBoxChangeListener implements ChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void stateChanged(final ChangeEvent ce) {
            if ((cbEdges.isVisible() && cbEdges.isSelected()) || (cbSurfaces.isVisible() && cbSurfaces.isSelected())) {
                btnConflicts.setEnabled(true);
            } else {
                btnConflicts.setEnabled(false);
            }
        }
    }
}
