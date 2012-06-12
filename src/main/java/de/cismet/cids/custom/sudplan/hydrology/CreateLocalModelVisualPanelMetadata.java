/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.openide.util.NbBundle;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class CreateLocalModelVisualPanelMetadata extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient CreateLocalModelWizardPanelMetadata model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPoi;
    private javax.swing.JLabel lblPoiValue;
    private javax.swing.JTextArea txaDescription;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CreateLocalModelVisualPanelMetadata.
     *
     * @param  model  DOCUMENT ME!
     */
    public CreateLocalModelVisualPanelMetadata(final CreateLocalModelWizardPanelMetadata model) {
        this.model = model;
        this.setName("Enter Metadata");

        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (model.getName() == null) {
            txtName.setText("Local Model for catchment area " + model.getBasinId());
        }

        if (model.getDescription() == null) {
            txaDescription.setText("Local Model for catchment area " + model.getBasinId());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CreateLocalModelWizardPanelMetadata getModel() {
        return model;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblPoi = new javax.swing.JLabel();
        lblPoiValue = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaDescription = new javax.swing.JTextArea();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblName.setText(NbBundle.getMessage(
                CreateLocalModelVisualPanelMetadata.class,
                "CreateLocalModelVisualPanelMetadata.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<null>");
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtName, gridBagConstraints);

        lblPoi.setText(NbBundle.getMessage(
                CreateLocalModelVisualPanelMetadata.class,
                "CreateLocalModelVisualPanelMetadata.lblPoi.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblPoi, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.basinId}"),
                lblPoiValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<null>");
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblPoiValue, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                CreateLocalModelVisualPanelMetadata.class,
                "CreateLocalModelVisualPanelMetadata.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDescription, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.description}"),
                txaDescription,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
}
