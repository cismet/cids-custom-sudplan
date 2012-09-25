/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class WizardPanelMetadataUI extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient WizardPanelMetadata model;
    private final transient DocumentListener docL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton descriptionButton = new javax.swing.JButton();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JTextArea txaDescription = new javax.swing.JTextArea();
    private final transient javax.swing.JTextField txtName = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RainfallDownscalingVisualPanelTargetDate.
     *
     * @param  model  DOCUMENT ME!
     */
    public WizardPanelMetadataUI(final WizardPanelMetadata model) {
        this.model = model;
        this.docL = new DocumentListenerImpl();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                WizardPanelMetadataUI.class,
                "WizardPanelMetadataUI.this.name")); // NOI18N

        initComponents();

        txtName.getDocument().addDocumentListener(WeakListeners.document(docL, txtName.getDocument()));
        txaDescription.getDocument().addDocumentListener(WeakListeners.document(docL, txaDescription.getDocument()));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        txtName.setSelectionStart(0);
        txtName.setSelectionEnd(txtName.getText().length());

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    txtName.requestFocus();
                }
            });

        model.fireChangeEvent();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getSelectedName() {
        return txtName.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getSelectedDescription() {
        return txaDescription.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.GridBagLayout());

        lblName.setText(NbBundle.getMessage(WizardPanelMetadataUI.class, "WizardPanelMetadataUI.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(lblName, gridBagConstraints);

        txtName.setText(NbBundle.getMessage(WizardPanelMetadataUI.class, "WizardPanelMetadataUI.txtName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(txtName, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                WizardPanelMetadataUI.class,
                "WizardPanelMetadataUI.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        add(lblDescription, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setLineWrap(true);
        txaDescription.setRows(5);
        txaDescription.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(jScrollPane1, gridBagConstraints);

        descriptionButton.setText(org.openide.util.NbBundle.getMessage(
                WizardPanelMetadataUI.class,
                "WizardPanelMetadataUI.descriptionButton.text")); // NOI18N
        descriptionButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    descriptionButtonActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        add(descriptionButton, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void descriptionButtonActionPerformed(final java.awt.event.ActionEvent evt) //GEN-FIRST:event_descriptionButtonActionPerformed
    {                                                                                   //GEN-HEADEREND:event_descriptionButtonActionPerformed
        this.txaDescription.setText(model.getDefaultDescription());
    }                                                                                   //GEN-LAST:event_descriptionButtonActionPerformed

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DocumentListenerImpl implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            model.fireChangeEvent();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            model.fireChangeEvent();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            model.fireChangeEvent();
        }
    }
}
