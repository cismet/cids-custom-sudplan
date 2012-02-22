/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.sudplan;

import org.openide.util.NbBundle;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.SqlTimestampToStringConverter;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class GeocpmConfigurationEditor extends AbstractCidsBeanRenderer implements EditorSaveListener {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient de.cismet.cids.editors.DefaultBindableReferenceCombo cboInvestigationArea =
        new de.cismet.cids.editors.DefaultBindableReferenceCombo();
    private final transient javax.swing.JCheckBox chkLastValues = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkMergeTriangles = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkSaveFlowCurves = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkSaveMarked = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkSaveVelocityCurves = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkTimeStepRestriction = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkWriteEdge = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkWriteNode = new javax.swing.JCheckBox();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JLabel lblCalcBegin = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCalcBeginValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCalcEnd = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCalcEndValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblInvestigationArea = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblLastValues = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblMergeTriangles = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblMinCalcTriangleSize = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblMinCalcTriangleSizeValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblNumberOfThreads = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblNumberOfThreadsValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQIn = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQInValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQOut = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQOutValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResultSaveLimit = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResultSaveLimitValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSaveFlowCurves = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSaveMarked = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSaveVelocityCurves = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblTimeStepRestriction = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblWriteEdge = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblWriteNode = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlFiller = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlInfo = new javax.swing.JPanel();
    private final transient javax.swing.JTextArea txaDescription = new javax.swing.JTextArea();
    private final transient javax.swing.JTextField txtName = new javax.swing.JTextField();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GeocpmConfigurationEditor.
     */
    public GeocpmConfigurationEditor() {
        this(true);
    }

    /**
     * Creates a new GeocpmConfigurationEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public GeocpmConfigurationEditor(final boolean editable) {
        initComponents();

        txtName.setEditable(editable);
        txaDescription.setEditable(editable);
        cboInvestigationArea.setEnabled(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
            bindingGroup,
            cidsBean);
        bindingGroup.unbind();
        bindingGroup.bind();
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
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

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblName.setText(NbBundle.getMessage(GeocpmConfigurationEditor.class, "GeocpmConfigurationEditor.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 5);
        add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 3);
        add(txtName, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 5);
        add(lblDescription, gridBagConstraints);

        pnlInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(
                NbBundle.getMessage(
                    GeocpmConfigurationEditor.class,
                    "GeocpmConfigurationEditor.pnlInfo.border.title"))); // NOI18N
        pnlInfo.setOpaque(false);
        pnlInfo.setLayout(new java.awt.GridBagLayout());

        lblCalcBegin.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblCalcBegin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblCalcBegin, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.calc_begin}"),
                lblCalcBeginValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        binding.setConverter(new SqlTimestampToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblCalcBeginValue, gridBagConstraints);

        lblCalcEnd.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblCalcEnd.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblCalcEnd, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.calc_end}"),
                lblCalcEndValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        binding.setConverter(new SqlTimestampToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblCalcEndValue, gridBagConstraints);

        lblWriteNode.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblWriteNode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblWriteNode, gridBagConstraints);

        lblWriteEdge.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblWriteEdge.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblWriteEdge, gridBagConstraints);

        chkWriteNode.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkWriteNode.text")); // NOI18N
        chkWriteNode.setContentAreaFilled(false);
        chkWriteNode.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.write_node}"),
                chkWriteNode,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkWriteNode, gridBagConstraints);

        chkWriteEdge.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkWriteEdge.text")); // NOI18N
        chkWriteEdge.setContentAreaFilled(false);
        chkWriteEdge.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.write_edge}"),
                chkWriteEdge,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkWriteEdge, gridBagConstraints);

        lblLastValues.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblLastValues.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblLastValues, gridBagConstraints);

        lblSaveMarked.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblSaveMarked.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblSaveMarked, gridBagConstraints);

        chkLastValues.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkLastValues.text")); // NOI18N
        chkLastValues.setContentAreaFilled(false);
        chkLastValues.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.last_values}"),
                chkLastValues,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkLastValues, gridBagConstraints);

        chkSaveMarked.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkSaveMarked.text")); // NOI18N
        chkSaveMarked.setContentAreaFilled(false);
        chkSaveMarked.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.save_marked}"),
                chkSaveMarked,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkSaveMarked, gridBagConstraints);

        lblMergeTriangles.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblMergeTriangles.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblMergeTriangles, gridBagConstraints);

        chkMergeTriangles.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkMergeTriangles.text")); // NOI18N
        chkMergeTriangles.setContentAreaFilled(false);
        chkMergeTriangles.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.merge_triangles}"),
                chkMergeTriangles,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkMergeTriangles, gridBagConstraints);

        lblMinCalcTriangleSize.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblMinCalcTriangleSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblMinCalcTriangleSize, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.min_calc_triangle_size}"),
                lblMinCalcTriangleSizeValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblMinCalcTriangleSizeValue, gridBagConstraints);

        lblTimeStepRestriction.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblTimeStepRestriction.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblTimeStepRestriction, gridBagConstraints);

        lblSaveVelocityCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblSaveVelocityCurves.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblSaveVelocityCurves, gridBagConstraints);

        lblSaveFlowCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblSaveFlowCurves.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblSaveFlowCurves, gridBagConstraints);

        chkTimeStepRestriction.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkTimeStepRestriction.text")); // NOI18N
        chkTimeStepRestriction.setContentAreaFilled(false);
        chkTimeStepRestriction.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.time_step_restriction}"),
                chkTimeStepRestriction,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkTimeStepRestriction, gridBagConstraints);

        chkSaveVelocityCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkSaveVelocityCurves.text")); // NOI18N
        chkSaveVelocityCurves.setContentAreaFilled(false);
        chkSaveVelocityCurves.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.save_velocity_curves}"),
                chkSaveVelocityCurves,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkSaveVelocityCurves, gridBagConstraints);

        chkSaveFlowCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkSaveFlowCurves.text")); // NOI18N
        chkSaveFlowCurves.setContentAreaFilled(false);
        chkSaveFlowCurves.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.save_flow_curves}"),
                chkSaveFlowCurves,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(chkSaveFlowCurves, gridBagConstraints);

        lblResultSaveLimit.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblResultSaveLimit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblResultSaveLimit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.result_save_limit}"),
                lblResultSaveLimitValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblResultSaveLimitValue, gridBagConstraints);

        lblNumberOfThreads.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblNumberOfThreads.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblNumberOfThreads, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.number_of_threads}"),
                lblNumberOfThreadsValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblNumberOfThreadsValue, gridBagConstraints);

        lblQIn.setText(NbBundle.getMessage(GeocpmConfigurationEditor.class, "GeocpmConfigurationEditor.lblQIn.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblQIn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.q_in}"),
                lblQInValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblQInValue, gridBagConstraints);

        lblQOut.setText(NbBundle.getMessage(GeocpmConfigurationEditor.class, "GeocpmConfigurationEditor.lblQOut.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblQOut, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.q_out}"),
                lblQOutValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfo.add(lblQOutValue, gridBagConstraints);

        pnlFiller.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlInfo.add(pnlFiller, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(pnlInfo, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.investigation_area}"),
                cboInvestigationArea,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 3);
        add(cboInvestigationArea, gridBagConstraints);

        lblInvestigationArea.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblInvestigationArea.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 5);
        add(lblInvestigationArea, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setRows(5);
        jScrollPane1.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        // noop
    }

    @Override
    public boolean prepareForSave() {
        if (cidsBean.getProperty("investigation_area") == null) {                       // NOI18N
            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(
                    GeocpmConfigurationEditor.class,
                    "GeocpmConfigEditor.prepareForSave().noInvestigationArea.message"), // NOI18N
                NbBundle.getMessage(
                    GeocpmConfigurationEditor.class,
                    "GeocpmConfigEditor.prepareForSave().noInvestigationArea.title"),   // NOI18N
                JOptionPane.INFORMATION_MESSAGE);

            return false;
        }

        return true;
    }
}
