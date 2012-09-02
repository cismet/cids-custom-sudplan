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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

import de.cismet.cids.custom.sudplan.geocpmrest.io.Rainevent;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class RaineventPanel extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient Rainevent rainevent;
    private JButton btnExport;

    private Action exportRainevent = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent ae) {
                // TODO Initialize the export wizard here
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblInterval;
    private javax.swing.JLabel lblShowInterval;
    private javax.swing.JLabel lblShowUnit;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JPanel pnlChart;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JToolBar tbToolbar;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RaineventPanel.
     *
     * @param  rainevent  DOCUMENT ME!
     */
    public RaineventPanel(final Rainevent rainevent) {
        this.rainevent = rainevent;
        initComponents();
        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        lblShowInterval.setText(String.valueOf(rainevent.getInterval()));
        lblShowUnit.setText(Unit.L_S_HA.getLocalisedName());
        tbToolbar.setRollover(true);
        tbToolbar.add(createExportRaineventButton());

        pnlTable.add(new RaineventTablePanel(rainevent), BorderLayout.CENTER);
        pnlChart.add(new RaineventChartPanel(rainevent), BorderLayout.CENTER);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createExportRaineventButton() {
        btnExport = new JButton(exportRainevent);
        btnExport.setFocusPainted(false);
        btnExport.setToolTipText(org.openide.util.NbBundle.getMessage(
                RaineventPanel.class,
                "RaineventPanel.createExportRaineventButton().tooltiptext"));
        btnExport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/data/io/ts_export.png")));
        return btnExport;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tbToolbar = new javax.swing.JToolBar();
        pnlTable = new javax.swing.JPanel();
        pnlChart = new javax.swing.JPanel();
        lblInterval = new javax.swing.JLabel();
        lblUnit = new javax.swing.JLabel();
        lblShowInterval = new javax.swing.JLabel();
        lblShowUnit = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        tbToolbar.setRollover(true);
        tbToolbar.setBorderPainted(false);
        tbToolbar.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        add(tbToolbar, gridBagConstraints);

        pnlTable.setMaximumSize(new java.awt.Dimension(300, 300));
        pnlTable.setOpaque(false);
        pnlTable.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        add(pnlTable, gridBagConstraints);

        pnlChart.setOpaque(false);
        pnlChart.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlChart, gridBagConstraints);

        lblInterval.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lblInterval.setText(org.openide.util.NbBundle.getMessage(
                RaineventPanel.class,
                "RaineventPanel.lblInterval.text"));                    // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblInterval, gridBagConstraints);

        lblUnit.setFont(new java.awt.Font("Lucida Grande", 1, 13));                                                 // NOI18N
        lblUnit.setText(org.openide.util.NbBundle.getMessage(RaineventPanel.class, "RaineventPanel.lblUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUnit, gridBagConstraints);

        lblShowInterval.setText(org.openide.util.NbBundle.getMessage(
                RaineventPanel.class,
                "RaineventPanel.lblShowInterval.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblShowInterval, gridBagConstraints);

        lblShowUnit.setText(org.openide.util.NbBundle.getMessage(
                RaineventPanel.class,
                "RaineventPanel.lblShowUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblShowUnit, gridBagConstraints);
    }                                                // </editor-fold>//GEN-END:initComponents
}
