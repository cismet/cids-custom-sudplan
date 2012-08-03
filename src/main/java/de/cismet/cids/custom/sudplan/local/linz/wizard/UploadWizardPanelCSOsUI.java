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
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.localserver.attribute.ClassAttribute;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.local.linz.EtaConfiguration;
import de.cismet.cids.custom.sudplan.local.wupp.WizardInitialisationException;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelCSOsUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelCSOsUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient UploadWizardPanelCSOs model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel csoConfigurationPanel;
    private javax.swing.JScrollPane jScrollPaneCsoConfiguration;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JTable tblCsoConfiguration;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public UploadWizardPanelCSOsUI(final UploadWizardPanelCSOs model) throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                UploadWizardPanelCSOs.class,
                "UploadWizardPanelCSOs.this.name")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initialising user interface");
        }

        if (this.model.isCopyCSOsComplete()
                    && (this.model.getCopiedCSOs() != null)
                    && !this.model.getCopiedCSOs().isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSOs already available");
            }
        } else if (this.model.isCopyCSOsInProgress()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSO copy thread still in progress");
            }
        } else if (this.model.isCopyCSOsErroneous()) {
            LOG.warn("CSO copy process was erroneous");
        } else if (this.model.getSelectedSwmmProject() != -1) {
            Mnemonics.setLocalizedText(
                progressLabel,
                NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "UploadWizardPanelCSOsUI.progressLabel.text")); // NOI18N
            progressBar.setIndeterminate(true);
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");

            final CsoCopyThread csoCopyThread = new CsoCopyThread();
            SudplanConcurrency.getSudplanGeneralPurposePool().execute(csoCopyThread);
        } else {
            progressBar.setIndeterminate(false);
            org.openide.awt.Mnemonics.setLocalizedText(
                progressLabel,
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "UploadWizardPanelCSOs.progressLabel.error")); // NOI18N
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   swmmProjectId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private CsoConfigurationTableModel initCSOs(final int swmmProjectId) throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_CSOS);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch CSO metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        assert swmmProjectId != -1 : "no suitable swmm project selected: -1";
        sb.append(" WHERE swmm_project = ").append(swmmProjectId);

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());               // NOI18N
        }

        final MetaObject[] metaObjects;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("executing SQL statement: \n" + sb);
            }
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get CSO meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        final List<EtaConfiguration> etaConfigurations = new ArrayList<EtaConfiguration>(metaObjects.length);

        for (final MetaObject metaObject : metaObjects) {
            final EtaConfiguration etaConfiguration = new EtaConfiguration();
            etaConfiguration.setName(metaObject.getName());
            etaConfiguration.setCso(metaObject.getID());

            if (etaConfiguration.getName().equalsIgnoreCase("ULKS1")) {
                etaConfiguration.setSedimentationEfficency(20);
            } else if (etaConfiguration.getName().equalsIgnoreCase("RKL_Ablauf")) {
                etaConfiguration.setEnabled(false);
            } else if (etaConfiguration.getName().equalsIgnoreCase("AB_Plesching")) {
                etaConfiguration.setSedimentationEfficency(20);
            } else if (etaConfiguration.getName().equalsIgnoreCase("RHHB_Weikerlsee3nolink")) {
                etaConfiguration.setSedimentationEfficency(20);
            }

            etaConfigurations.add(etaConfiguration);
        }

        // trigger change event
        // this.model.setEtaConfigurations(etaConfigurations);
        return new CsoConfigurationTableModel(etaConfigurations);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        csoConfigurationPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        jScrollPaneCsoConfiguration = new javax.swing.JScrollPane();
        tblCsoConfiguration = new javax.swing.JTable();
        progressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        csoConfigurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    UploadWizardPanelCSOsUI.class,
                    "UploadWizardPanelCSOsUI.csoConfigurationPanel.border.title"))); // NOI18N
        csoConfigurationPanel.setLayout(new java.awt.GridBagLayout());

        cardPanel.setLayout(new java.awt.CardLayout());

        tblCsoConfiguration.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        tblCsoConfiguration.setEnabled(false);
        jScrollPaneCsoConfiguration.setViewportView(tblCsoConfiguration);

        cardPanel.add(jScrollPaneCsoConfiguration, "csos");

        progressPanel.setLayout(new java.awt.GridBagLayout());

        progressBar.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 25);
        progressPanel.add(progressBar, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            progressLabel,
            org.openide.util.NbBundle.getMessage(
                UploadWizardPanelCSOsUI.class,
                "UploadWizardPanelCSOsUI.progressLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        progressPanel.add(progressLabel, gridBagConstraints);

        cardPanel.add(progressPanel, "progress");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        csoConfigurationPanel.add(cardPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(csoConfigurationPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public UploadWizardPanelCSOs getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CsoConfigurationTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final List<EtaConfiguration> etaConfigurations;
        // FIXME: i18n
        private final String[] columnNames = { "CSO", "Aktiv", "η Sedimentation" };
        private final Class[] columnClasses = { String.class, Boolean.class, Float.class };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CsoConfigurationTableModel object.
         *
         * @param  etaConfigurations  metaObjects DOCUMENT ME!
         */
        private CsoConfigurationTableModel(final List<EtaConfiguration> etaConfigurations) {
            this.etaConfigurations = etaConfigurations;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return etaConfigurations.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return etaConfigurations.get(rowIndex).getName();
                }
                case 1: {
                    return etaConfigurations.get(rowIndex).isEnabled();
                }
                case 2: {
                    return etaConfigurations.get(rowIndex).getSedimentationEfficency();
                }
            }

            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col) {
            if (col == 1) {
                etaConfigurations.get(row).setEnabled((Boolean)value);
            } else if (col == 2) {
                etaConfigurations.get(row).setSedimentationEfficency((Float)value);
            }

            fireTableCellUpdated(row, col);
        }

        @Override
        public String getColumnName(final int col) {
            return columnNames[col];
        }

        @Override
        public Class getColumnClass(final int col) {
            return columnClasses[col];
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            return (col == 1) || (col == 2);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CsoCopyThread implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private CsoConfigurationTableModel csoConfigurationTableModel;

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            model.setCopyCSOsInProgress(true);
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("CsoCopyThread: copying CSOs from SWMM project "
                                + model.getSelectedSwmmProject());
                }

                csoConfigurationTableModel = initCSOs(model.getSelectedSwmmProject());
                model.setCopyCSOsComplete(true);
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("CsoUpdater: updating loaded results");
                            }
                            tblCsoConfiguration.setModel(csoConfigurationTableModel);
                            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "csos");
                        }
                    });
            } catch (Throwable t) {
                LOG.error("CsoUpdater: could not retrieve CSOs: " + t.getMessage(), t);
                model.setCopyCSOsErroneous(true);
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setIndeterminate(false);
                            org.openide.awt.Mnemonics.setLocalizedText(
                                progressLabel,
                                org.openide.util.NbBundle.getMessage(
                                    SwmmWizardPanelTimeseriesUI.class,
                                    "UploadWizardPanelCSOsUI.progressLabel.error")); // NOI18N
                        }
                    });
            }
        }
    }
}
