/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;

import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class EmissionUploadVisualPanelGridSpecification extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EmissionUploadVisualPanelGridSpecification.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected enum Modelproperty {

        //~ Enum constants -----------------------------------------------------

        EMISSIONGRID, CUSTOMTIMEVARIATION, GRIDNAME;
    }

    //~ Instance fields --------------------------------------------------------

    private transient EmissionUploadPanelGrids model;
    private transient DocumentListener changeModelEmissiongridListener;
    private transient DocumentListener changeModelCustomTimevariationListener;
    private transient DocumentListener changeModelGridnameListener;
    private JFileChooser fileChooser;
    private Grid grid;
    private boolean dirty;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrGridHeight;
    private javax.swing.ButtonGroup bgrTimeVariation;
    private javax.swing.JButton btnEmissionGrid;
    private javax.swing.JButton btnTimeVariationCustom;
    private javax.swing.JComboBox cmbSubstance;
    private javax.swing.Box.Filler gluFill;
    private javax.swing.JLabel lblEmissionGrid;
    private javax.swing.JLabel lblGridHeight;
    private javax.swing.JLabel lblGridName;
    private javax.swing.JLabel lblSubstance;
    private javax.swing.JLabel lblTimeVariation;
    private javax.swing.JPanel pnlGridHeight;
    private javax.swing.JPanel pnlTimeVariation;
    private javax.swing.JRadioButton rdoGridHeight0;
    private javax.swing.JRadioButton rdoGridHeight160;
    private javax.swing.JRadioButton rdoGridHeight40;
    private javax.swing.JRadioButton rdoGridHeight80;
    private javax.swing.JRadioButton rdoTimeVariationConstant;
    private javax.swing.JRadioButton rdoTimeVariationCustom;
    private javax.swing.JRadioButton rdoTimeVariationTraffic;
    private javax.swing.JTextField txtEmissionGrid;
    private javax.swing.JTextField txtGridName;
    private javax.swing.JTextField txtTimeVariationCustom;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmissionUploadVisualPanelGridSpecification object.
     */
    public EmissionUploadVisualPanelGridSpecification() {
        // JUST A HACK TO ALLOW MATISSE TO DISPLAY EmissionUploadPanelGrids
    }

    /**
     * Creates new form EmissionUploadVisualPanelLayerSpecification.
     *
     * @param  model  DOCUMENT ME!
     */
    public EmissionUploadVisualPanelGridSpecification(final EmissionUploadPanelGrids model) {
        this.model = model;
        grid = new Grid();
        changeModelEmissiongridListener = new ChangeModelListener(Modelproperty.EMISSIONGRID);
        changeModelCustomTimevariationListener = new ChangeModelListener(Modelproperty.CUSTOMTIMEVARIATION);
        changeModelGridnameListener = new ChangeModelListener(Modelproperty.GRIDNAME);

        model.setGrid(grid);

        dirty = false;

        initComponents();

        final DefaultComboBoxModel substances = new DefaultComboBoxModel();
        substances.addElement(Substance.NOX);
        substances.addElement(Substance.NH3);
        substances.addElement(Substance.SO2);
        substances.addElement(Substance.CO);
        substances.addElement(Substance.NMVOC);
        substances.addElement(Substance.PM10);
        cmbSubstance.setModel(substances);

        txtEmissionGrid.getDocument()
                .addDocumentListener(WeakListeners.document(
                        changeModelEmissiongridListener,
                        txtEmissionGrid.getDocument()));
        txtTimeVariationCustom.getDocument()
                .addDocumentListener(WeakListeners.document(
                        changeModelCustomTimevariationListener,
                        txtTimeVariationCustom.getDocument()));
        txtGridName.getDocument()
                .addDocumentListener(WeakListeners.document(changeModelGridnameListener, txtGridName.getDocument()));
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

        bgrTimeVariation = new javax.swing.ButtonGroup();
        bgrGridHeight = new javax.swing.ButtonGroup();
        lblSubstance = new javax.swing.JLabel();
        cmbSubstance = new javax.swing.JComboBox();
        lblEmissionGrid = new javax.swing.JLabel();
        txtEmissionGrid = new javax.swing.JTextField();
        btnEmissionGrid = new javax.swing.JButton();
        lblTimeVariation = new javax.swing.JLabel();
        lblGridHeight = new javax.swing.JLabel();
        lblGridName = new javax.swing.JLabel();
        pnlTimeVariation = new javax.swing.JPanel();
        rdoTimeVariationConstant = new javax.swing.JRadioButton();
        rdoTimeVariationCustom = new javax.swing.JRadioButton();
        rdoTimeVariationTraffic = new javax.swing.JRadioButton();
        btnTimeVariationCustom = new javax.swing.JButton();
        txtTimeVariationCustom = new javax.swing.JTextField();
        pnlGridHeight = new javax.swing.JPanel();
        rdoGridHeight0 = new javax.swing.JRadioButton();
        rdoGridHeight40 = new javax.swing.JRadioButton();
        rdoGridHeight80 = new javax.swing.JRadioButton();
        rdoGridHeight160 = new javax.swing.JRadioButton();
        gluFill = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        txtGridName = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        lblSubstance.setLabelFor(cmbSubstance);
        lblSubstance.setText(org.openide.util.NbBundle.getMessage(
                EmissionUploadVisualPanelGridSpecification.class,
                "EmissionUploadVisualPanelGridSpecification.lblSubstance.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblSubstance, gridBagConstraints);

        cmbSubstance.setRenderer(new SubstanceRenderer());
        cmbSubstance.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmbSubstanceActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(cmbSubstance, gridBagConstraints);

        lblEmissionGrid.setLabelFor(txtEmissionGrid);
        lblEmissionGrid.setText(org.openide.util.NbBundle.getMessage(
                EmissionUploadVisualPanelGridSpecification.class,
                "EmissionUploadVisualPanelGridSpecification.lblEmissionGrid.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblEmissionGrid, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(txtEmissionGrid, gridBagConstraints);

        btnEmissionGrid.setText(org.openide.util.NbBundle.getMessage(
                EmissionUploadVisualPanelGridSpecification.class,
                "EmissionUploadVisualPanelGridSpecification.btnEmissionGrid.text")); // NOI18N
        btnEmissionGrid.setFocusPainted(false);
        btnEmissionGrid.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnEmissionGridActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(btnEmissionGrid, gridBagConstraints);

        lblTimeVariation.setLabelFor(rdoTimeVariationConstant);
        lblTimeVariation.setText(org.openide.util.NbBundle.getMessage(
                EmissionUploadVisualPanelGridSpecification.class,
                "EmissionUploadVisualPanelGridSpecification.lblTimeVariation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblTimeVariation, gridBagConstraints);

        lblGridHeight.setLabelFor(rdoGridHeight0);
        lblGridHeight.setText(org.openide.util.NbBundle.getMessage(
                EmissionUploadVisualPanelGridSpecification.class,
                "EmissionUploadVisualPanelGridSpecification.lblGridHeight.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblGridHeight, gridBagConstraints);

        lblGridName.setLabelFor(txtGridName);
        lblGridName.setText(org.openide.util.NbBundle.getMessage(
                EmissionUploadVisualPanelGridSpecification.class,
                "EmissionUploadVisualPanelGridSpecification.lblGridName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblGridName, gridBagConstraints);

        pnlTimeVariation.setLayout(new java.awt.GridBagLayout());

        bgrTimeVariation.add(rdoTimeVariationConstant);
        rdoTimeVariationConstant.setSelected(true);
        rdoTimeVariationConstant.setText(TimeVariation.CONSTANT.getRepresentationUI());
        rdoTimeVariationConstant.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rdoTimeVariationConstantActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlTimeVariation.add(rdoTimeVariationConstant, gridBagConstraints);

        bgrTimeVariation.add(rdoTimeVariationCustom);
        rdoTimeVariationCustom.setText(TimeVariation.CUSTOM.getRepresentationUI());
        rdoTimeVariationCustom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rdoTimeVariationCustomActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlTimeVariation.add(rdoTimeVariationCustom, gridBagConstraints);

        bgrTimeVariation.add(rdoTimeVariationTraffic);
        rdoTimeVariationTraffic.setText(TimeVariation.TRAFFIC.getRepresentationUI());
        rdoTimeVariationTraffic.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rdoTimeVariationTrafficActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlTimeVariation.add(rdoTimeVariationTraffic, gridBagConstraints);

        btnTimeVariationCustom.setText(org.openide.util.NbBundle.getMessage(
                EmissionUploadVisualPanelGridSpecification.class,
                "EmissionUploadVisualPanelGridSpecification.btnTimeVariationCustom.text")); // NOI18N
        btnTimeVariationCustom.setFocusPainted(false);
        btnTimeVariationCustom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnTimeVariationCustomActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlTimeVariation.add(btnTimeVariationCustom, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlTimeVariation.add(txtTimeVariationCustom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(pnlTimeVariation, gridBagConstraints);

        pnlGridHeight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 4, 5));

        bgrGridHeight.add(rdoGridHeight0);
        rdoGridHeight0.setSelected(true);
        rdoGridHeight0.setText(de.cismet.cids.custom.sudplan.airquality.emissionupload.GridHeight.ZERO
                    .getRepresentationUI());
        rdoGridHeight0.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rdoGridHeight0ActionPerformed(evt);
                }
            });
        pnlGridHeight.add(rdoGridHeight0);

        bgrGridHeight.add(rdoGridHeight40);
        rdoGridHeight40.setText(de.cismet.cids.custom.sudplan.airquality.emissionupload.GridHeight.FORTY
                    .getRepresentationUI());
        rdoGridHeight40.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rdoGridHeight40ActionPerformed(evt);
                }
            });
        pnlGridHeight.add(rdoGridHeight40);

        bgrGridHeight.add(rdoGridHeight80);
        rdoGridHeight80.setText(de.cismet.cids.custom.sudplan.airquality.emissionupload.GridHeight.EIGHTY
                    .getRepresentationUI());
        rdoGridHeight80.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rdoGridHeight80ActionPerformed(evt);
                }
            });
        pnlGridHeight.add(rdoGridHeight80);

        bgrGridHeight.add(rdoGridHeight160);
        rdoGridHeight160.setText(de.cismet.cids.custom.sudplan.airquality.emissionupload.GridHeight.HUNDREDSIXTY
                    .getRepresentationUI());
        rdoGridHeight160.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rdoGridHeight160ActionPerformed(evt);
                }
            });
        pnlGridHeight.add(rdoGridHeight160);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(pnlGridHeight, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        add(gluFill, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtGridName, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnTimeVariationCustomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnTimeVariationCustomActionPerformed
        requestPathToLocalFile(txtTimeVariationCustom);
    }                                                                                          //GEN-LAST:event_btnTimeVariationCustomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnEmissionGridActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnEmissionGridActionPerformed
        requestPathToLocalFile(txtEmissionGrid);
    }                                                                                   //GEN-LAST:event_btnEmissionGridActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rdoTimeVariationConstantActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rdoTimeVariationConstantActionPerformed
        if (!TimeVariation.CONSTANT.equals(model.getTimeVariation())) {
            dirty = true;
        }
        model.setTimeVariation(TimeVariation.CONSTANT);
    }                                                                                            //GEN-LAST:event_rdoTimeVariationConstantActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rdoTimeVariationTrafficActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rdoTimeVariationTrafficActionPerformed
        if (!TimeVariation.TRAFFIC.equals(model.getTimeVariation())) {
            dirty = true;
        }
        model.setTimeVariation(TimeVariation.TRAFFIC);
    }                                                                                           //GEN-LAST:event_rdoTimeVariationTrafficActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rdoTimeVariationCustomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rdoTimeVariationCustomActionPerformed
        if (!TimeVariation.CUSTOM.equals(model.getTimeVariation())) {
            dirty = true;
        }
        model.setTimeVariation(TimeVariation.CUSTOM);
    }                                                                                          //GEN-LAST:event_rdoTimeVariationCustomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rdoGridHeight0ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rdoGridHeight0ActionPerformed
        if (!GridHeight.ZERO.equals(model.getGridHeight())) {
            dirty = true;
        }
        model.setGridHeight(GridHeight.ZERO);
    }                                                                                  //GEN-LAST:event_rdoGridHeight0ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rdoGridHeight40ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rdoGridHeight40ActionPerformed
        if (!GridHeight.FORTY.equals(model.getGridHeight())) {
            dirty = true;
        }
        model.setGridHeight(GridHeight.FORTY);
    }                                                                                   //GEN-LAST:event_rdoGridHeight40ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rdoGridHeight80ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rdoGridHeight80ActionPerformed
        if (!GridHeight.EIGHTY.equals(model.getGridHeight())) {
            dirty = true;
        }
        model.setGridHeight(GridHeight.EIGHTY);
    }                                                                                   //GEN-LAST:event_rdoGridHeight80ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rdoGridHeight160ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rdoGridHeight160ActionPerformed
        if (!GridHeight.HUNDREDSIXTY.equals(model.getGridHeight())) {
            dirty = true;
        }
        model.setGridHeight(GridHeight.HUNDREDSIXTY);
    }                                                                                    //GEN-LAST:event_rdoGridHeight160ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmbSubstanceActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmbSubstanceActionPerformed
        if (!((Substance)cmbSubstance.getSelectedItem()).equals(model.getSubstance())) {
            dirty = true;
        }
        model.setSubstance((Substance)cmbSubstance.getSelectedItem());
    }                                                                                //GEN-LAST:event_cmbSubstanceActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  textfield  DOCUMENT ME!
     */
    protected void requestPathToLocalFile(final JTextField textfield) {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
        }

        final boolean emissionGrid = txtEmissionGrid.equals(textfield);
        if (emissionGrid) {
            fileChooser.setDialogTitle(NbBundle.getMessage(
                    EmissionUploadVisualPanelGridSpecification.class,
                    "EmissionUploadVisualPanelGridSpecification.requestPathToLocalFile().txtEmissionGrid.title"));        // NOI18N
        } else {
            fileChooser.setDialogTitle(NbBundle.getMessage(
                    EmissionUploadVisualPanelGridSpecification.class,
                    "EmissionUploadVisualPanelGridSpecification.requestPathToLocalFile().txtTimeVariationCustom.title")); // NOI18N
        }

        final int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            textfield.setText(fileChooser.getSelectedFile().getAbsolutePath());

            if (emissionGrid) {
                model.setEmissionGrid(generateFile(textfield.getText()));
            } else {
                model.setCustomTimeVariation(generateFile(textfield.getText()));
            }

            dirty = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Grid saveGrid() {
        grid.setSubstance((Substance)cmbSubstance.getSelectedItem());
        grid.setEmissionGrid(new File(txtEmissionGrid.getText()));
        grid.setGridName(txtGridName.getText());

        if (rdoGridHeight0.isSelected()) {
            grid.setGridHeight(GridHeight.ZERO);
        } else if (rdoGridHeight40.isSelected()) {
            grid.setGridHeight(GridHeight.FORTY);
        } else if (rdoGridHeight80.isSelected()) {
            grid.setGridHeight(GridHeight.EIGHTY);
        } else {
            grid.setGridHeight(GridHeight.HUNDREDSIXTY);
        }

        if (rdoTimeVariationConstant.isSelected()) {
            grid.setTimeVariation(TimeVariation.CONSTANT);
            grid.setCustomTimeVariation(null);
        } else if (rdoTimeVariationTraffic.isSelected()) {
            grid.setTimeVariation(TimeVariation.TRAFFIC);
            grid.setCustomTimeVariation(null);
        } else {
            grid.setTimeVariation(TimeVariation.CUSTOM);
            grid.setCustomTimeVariation(new File(txtTimeVariationCustom.getText()));
        }

        dirty = false;

        return grid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  grid  DOCUMENT ME!
     */
    public void setGrid(final Grid grid) {
        this.grid = grid;

        String textToInsert = ""; // NOI18N
        if (grid.getEmissionGrid() != null) {
            textToInsert = grid.getEmissionGrid().getPath();
        }
        txtEmissionGrid.setText(textToInsert);

        textToInsert = ""; // NOI18N
        if (grid.getCustomTimeVariation() != null) {
            textToInsert = grid.getCustomTimeVariation().getPath();
        }
        txtTimeVariationCustom.setText(textToInsert);

        txtGridName.setText(grid.getGridName());
        cmbSubstance.setSelectedItem(grid.getSubstance());

        if (grid.getGridHeight().equals(GridHeight.ZERO)) {
            rdoGridHeight0.setSelected(true);
        } else if (grid.getGridHeight().equals(GridHeight.FORTY)) {
            rdoGridHeight40.setSelected(true);
        } else if (grid.getGridHeight().equals(GridHeight.EIGHTY)) {
            rdoGridHeight80.setSelected(true);
        } else {
            rdoGridHeight160.setSelected(true);
        }

        if (grid.getTimeVariation().equals(TimeVariation.CONSTANT)) {
            rdoTimeVariationConstant.setSelected(true);
        } else if (grid.getTimeVariation().equals(TimeVariation.TRAFFIC)) {
            rdoTimeVariationTraffic.setSelected(true);
        } else {
            rdoTimeVariationCustom.setSelected(true);
        }

        dirty = false;

        model.setGrid(grid);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        cmbSubstance.setEnabled(enabled);
        txtEmissionGrid.setEnabled(enabled);
        rdoTimeVariationConstant.setEnabled(enabled);
        rdoTimeVariationTraffic.setEnabled(enabled);
        rdoTimeVariationCustom.setEnabled(enabled);
        txtTimeVariationCustom.setEnabled(enabled);
        rdoGridHeight0.setEnabled(enabled);
        rdoGridHeight40.setEnabled(enabled);
        rdoGridHeight80.setEnabled(enabled);
        rdoGridHeight160.setEnabled(enabled);
        txtGridName.setEnabled(enabled);
        btnEmissionGrid.setEnabled(enabled);
        btnTimeVariationCustom.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   path  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static File generateFile(final String path) {
        if ((path != null) && (path.trim().length() > 0)) {
            return new File(path);
        }

        return null;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class SubstanceRenderer extends JLabel implements ListCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SubstancesRenderer object.
         */
        public SubstanceRenderer() {
            setOpaque(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            if (isSelected) {
                setBackground(UIManager.getDefaults().getColor("ComboBox.selectionBackground")); // NOI18N
                setForeground(UIManager.getDefaults().getColor("ComboBox.selectionForeground")); // NOI18N
            } else {
                setBackground(UIManager.getDefaults().getColor("ComboBox.background"));          // NOI18N
                setForeground(UIManager.getDefaults().getColor("ComboBox.foreground"));          // NOI18N
            }

            if (value instanceof Substance) {
                final Substance substance = (Substance)value;
                setText(substance.getRepresentationUI());
            } else {
                // TODO: I18N
                setText("Unknown substance"); // NOI18N
            }

            return this;
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class ChangeModelListener implements DocumentListener {

        //~ Instance fields ----------------------------------------------------

        private Modelproperty modelproperty;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new NotifyListenersListener object.
         *
         * @param  modelproperty  DOCUMENT ME!
         */
        public ChangeModelListener(final Modelproperty modelproperty) {
            this.modelproperty = modelproperty;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            updateModelproperty();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            updateModelproperty();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            updateModelproperty();
        }

        /**
         * DOCUMENT ME!
         */
        private void updateModelproperty() {
            dirty = true;
            if (Modelproperty.EMISSIONGRID.equals(modelproperty)) {
                model.setEmissionGrid(generateFile(txtEmissionGrid.getText()));
            } else if (Modelproperty.CUSTOMTIMEVARIATION.equals(modelproperty)) {
                model.setCustomTimeVariation(generateFile(txtTimeVariationCustom.getText()));
            } else if (Modelproperty.GRIDNAME.equals(modelproperty)) {
                model.setGridName(txtGridName.getText());
            }
        }
    }
}
