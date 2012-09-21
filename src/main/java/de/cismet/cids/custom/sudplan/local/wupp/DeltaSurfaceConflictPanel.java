/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class DeltaSurfaceConflictPanel extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient boolean isSurfaceConflict;
    private final transient boolean isBreakingedgeConflict;
    private final transient ChangeListener cL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbEdges;
    private javax.swing.JCheckBox cbSurfaces;
    private javax.swing.JLabel lblHead;
    private javax.swing.JLabel lblInfoMessage;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DeltaSurfaceConflictPanel.
     *
     * @param  isSurfaceConflict       DOCUMENT ME!
     * @param  isBreakingedgeConflict  DOCUMENT ME!
     * @param  surfaceCount            DOCUMENT ME!
     * @param  breakingedgesCount      DOCUMENT ME!
     */
    public DeltaSurfaceConflictPanel(final boolean isSurfaceConflict,
            final boolean isBreakingedgeConflict,
            final int surfaceCount,
            final int breakingedgesCount) {
        this.isSurfaceConflict = isSurfaceConflict;
        this.isBreakingedgeConflict = isBreakingedgeConflict;
        cL = new CheckBoxChangeListener();

        initComponents();

        cbSurfaces.addChangeListener(WeakListeners.change(cL, cbSurfaces));
        cbEdges.addChangeListener(WeakListeners.change(cL, cbEdges));

        cbSurfaces.setSelected(this.isSurfaceConflict);
        cbSurfaces.setEnabled(this.isSurfaceConflict);
        final StringBuilder strS = new StringBuilder(cbSurfaces.getText());
        strS.append(NbBundle.getMessage(
                DeltaSurfaceConflictPanel.class,
                "DeltaSurfaceConflictPanel.<init>.conflictCount", // NOI18N
                surfaceCount));
        cbSurfaces.setText(strS.toString());

        cbEdges.setSelected(this.isBreakingedgeConflict);
        cbEdges.setEnabled(this.isBreakingedgeConflict);
        final StringBuilder strB = new StringBuilder(cbEdges.getText());
        strB.append(NbBundle.getMessage(
                DeltaSurfaceConflictPanel.class,
                "DeltaSurfaceConflictPanel.<init>.conflictCount", // NOI18N
                breakingedgesCount));
        cbEdges.setText(strB.toString());

        lblInfoMessage.setIcon(ImageUtilities.loadImageIcon("org/netbeans/modules/dialogs/warning.gif", false)); // NOI18N
        if (!isBreakingedgeConflict) {
            lblInfoMessage.setText(
                NbBundle.getMessage(
                    DeltaSurfaceConflictPanel.class,
                    "DeltaSurfaceConflictPanel.<init>.lblInfoMessage.text.surfaceConflict"));                    // NOI18N
        } else {
            lblInfoMessage.setText(
                NbBundle.getMessage(
                    DeltaSurfaceConflictPanel.class,
                    "DeltaSurfaceConflictPanel.<init>.lblInfoMessage.text.breakingEdgeConflict"));               // NOI18N
        }
    }

    //~ Methods ----------------------------------------------------------------

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
        lblInfoMessage = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lblHead.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictPanel.class,
                "DeltaSurfaceConflictPanel.lblHead.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblHead, gridBagConstraints);

        cbEdges.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictPanel.class,
                "DeltaSurfaceConflictPanel.cbEdges.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cbEdges, gridBagConstraints);

        cbSurfaces.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictPanel.class,
                "DeltaSurfaceConflictPanel.cbSurfaces.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cbSurfaces, gridBagConstraints);

        lblInfoMessage.setText(org.openide.util.NbBundle.getMessage(
                DeltaSurfaceConflictPanel.class,
                "DeltaSurfaceConflictPanel.lblInfoMessage.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblInfoMessage, gridBagConstraints);
    }                                                              // </editor-fold>//GEN-END:initComponents

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
            cbEdges.setSelected(isBreakingedgeConflict);
            cbSurfaces.setSelected(isSurfaceConflict);
        }
    }
}
