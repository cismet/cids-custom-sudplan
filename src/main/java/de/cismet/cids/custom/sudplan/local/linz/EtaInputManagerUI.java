/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * EtaInputManagerUI.java
 *
 * Created on 07.12.2011, 15:18:55
 */
package de.cismet.cids.custom.sudplan.local.linz;

import org.apache.log4j.Logger;

import java.io.IOException;

import javax.swing.table.DefaultTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class EtaInputManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaInputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient EtaInputManager inputManager;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable etaTable;
    private javax.swing.JScrollPane etaTableScrollPane;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EtaInputManagerUI.
     *
     * @param  inputManager  DOCUMENT ME!
     */
    public EtaInputManagerUI(final EtaInputManager inputManager) {
        // TODO: Better Visualisation (Charts?), Links to CSOs

        this.inputManager = inputManager;
        initComponents();
        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void init() {
        try {
            final EtaInput etaInput = inputManager.getUR();
            final DefaultTableModel ctoTableModel = (DefaultTableModel)this.etaTable.getModel();
            if (LOG.isDebugEnabled()) {
                LOG.debug("loading configuration of #" + etaInput.getEtaConfigurations().size() + "CSO");
            }
            for (final EtaConfiguration etaConfiguration : etaInput.getEtaConfigurations()) {
                ctoTableModel.addRow(
                    new Object[] {
                        etaConfiguration.getName(),
                        etaConfiguration.isEnabled(),
                        etaConfiguration.getSedimentationEfficency()
                    });
            }
            ctoTableModel.fireTableDataChanged();
        } catch (IOException ex) {
            LOG.error("cannot initialise eta input manager ui", ex); // NOI18N
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

        etaTableScrollPane = new javax.swing.JScrollPane();
        etaTable = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        etaTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] { "CSO", "aktiv", "Wirkungsgrad" }) {

                Class[] types = new Class[] { java.lang.String.class, java.lang.Boolean.class, java.lang.Float.class };
                boolean[] canEdit = new boolean[] { false, true, true };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        etaTableScrollPane.setViewportView(etaTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(etaTableScrollPane, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents
}
