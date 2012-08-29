/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.sudplan;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class IDFCurvePanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(IDFCurvePanel.class);

    //~ Instance fields --------------------------------------------------------

    public Action exportData = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO Start Export-Wizard
            }
        };

    private final transient IDFCurve idfCurve;

    private JButton btnExportData;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblShowUnit;
    private javax.swing.JLabel lblShowYear;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JLabel lblYear;
    private javax.swing.JPanel pnlChart;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JToolBar tlbToolBar;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form IDFCurvePanel.
     *
     * @param  idfCurve  raineventBean DOCUMENT ME!
     */
    public IDFCurvePanel(final IDFCurve idfCurve) {
        this.idfCurve = idfCurve;

        assert this.idfCurve != null : "Rainevent cannot be null";

        initComponents();

        lblShowYear.setText(String.valueOf(this.idfCurve.getCenterYear()));
        lblShowUnit.setText(Unit.MM_H.getLocalisedName());

        tlbToolBar.add(createExportDataButton());

        try {
            pnlTable.removeAll();
            pnlTable.add(new IDFTablePanel(this.idfCurve), BorderLayout.CENTER);
            pnlChart.removeAll();
            pnlChart.add(new IDFChartPanel(this.idfCurve), BorderLayout.CENTER);
        } catch (final Exception ex) {
            LOG.error("cannot initialise rainevent visualization", ex);
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

        pnlTable = new javax.swing.JPanel();
        pnlChart = new javax.swing.JPanel();
        tlbToolBar = new javax.swing.JToolBar();
        lblYear = new javax.swing.JLabel();
        lblUnit = new javax.swing.JLabel();
        lblShowYear = new javax.swing.JLabel();
        lblShowUnit = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlTable.setOpaque(false);
        pnlTable.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        add(pnlTable, gridBagConstraints);

        pnlChart.setOpaque(false);
        pnlChart.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlChart, gridBagConstraints);

        tlbToolBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tlbToolBar.setFloatable(false);
        tlbToolBar.setRollover(true);
        tlbToolBar.setMinimumSize(new java.awt.Dimension(12, 30));
        tlbToolBar.setOpaque(false);
        tlbToolBar.setPreferredSize(new java.awt.Dimension(12, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(tlbToolBar, gridBagConstraints);

        lblYear.setText(org.openide.util.NbBundle.getMessage(IDFCurvePanel.class, "IDFCurvePanel.lblYear.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblYear, gridBagConstraints);

        lblUnit.setText(org.openide.util.NbBundle.getMessage(IDFCurvePanel.class, "IDFCurvePanel.lblUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUnit, gridBagConstraints);

        lblShowYear.setText(org.openide.util.NbBundle.getMessage(
                IDFCurvePanel.class,
                "IDFCurvePanel.lblShowYear.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblShowYear, gridBagConstraints);

        lblShowUnit.setText(org.openide.util.NbBundle.getMessage(
                IDFCurvePanel.class,
                "IDFCurvePanel.lblShowUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblShowUnit, gridBagConstraints);
    }                                               // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createExportDataButton() {
        btnExportData = new JButton(exportData);
        btnExportData.setToolTipText(org.openide.util.NbBundle.getMessage(
                IDFCurvePanel.class,
                "IDFCurvePanel.createExportButton().btnExportData.tooltip"));
        btnExportData.setFocusPainted(false);
        btnExportData.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/dataExport/idf_export.png")));
        return btnExportData;
    }
}
