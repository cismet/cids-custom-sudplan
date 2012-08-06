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
public final class EtaWizardPanelEtaConfigurationUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaWizardPanelEtaConfigurationUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient EtaWizardPanelEtaConfiguration model;
    private transient int lastSwmmProjectId = -1;
    private CsoUpdater csoUpdater;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel etaConfigurationPanel;
    private javax.swing.JScrollPane jScrollPaneEtaConfiguration;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JTable tblEtaConfiguration;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public EtaWizardPanelEtaConfigurationUI(final EtaWizardPanelEtaConfiguration model)
            throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                EtaWizardPanelEtaConfigurationUI.class,
                "EtaWizardPanelEtaConfiguration.this.name")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initialising user interface");
        }
        try {
            if ((this.model.getEtaConfigurations() != null) && !this.model.getEtaConfigurations().isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ETA Configurations already available, updating Table with stored ETA Configuration");
                }

                if ((csoUpdater != null) && csoUpdater.isRunning()) {
                    LOG.warn("A CSO update thread is running, stopping Thread");
                    csoUpdater.stopIt();
                }

                this.tblEtaConfiguration.setModel(new EtaConfigurationTableModel(this.model.getEtaConfigurations()));

                // trigger change event
                // this.model.setEtaConfigurations(this.model.getEtaConfigurations());
                this.model.fireChangeEvent();
            } else if (this.model.getSwmmProjectId() != this.lastSwmmProjectId) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("project id changed (" + this.model.getSwmmProjectId() + "), loading CSO list");
                }

                Mnemonics.setLocalizedText(
                    progressLabel,
                    NbBundle.getMessage(
                        EtaWizardPanelEtaConfigurationUI.class,
                        "EtaWizardPanelEtaConfigurationUI.progressLabel.text")); // NOI18N
                progressBar.setIndeterminate(true);
                ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");

                if ((csoUpdater != null) && csoUpdater.isRunning()) {
                    LOG.warn("another cso update thread is running, stopping thred");
                    csoUpdater.stopIt();
                }

                csoUpdater = new CsoUpdater();
                SudplanConcurrency.getSudplanGeneralPurposePool().execute(csoUpdater);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("project id did not change (" + this.lastSwmmProjectId + "), using cached CSO List");
                }

                if ((csoUpdater != null) && csoUpdater.isRunning()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.warn("cso update thread is still running, waiting for thred to finish");
                    }
                } else {
                    ((CardLayout)cardPanel.getLayout()).show(cardPanel, "csos");
                }
            }
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
            progressBar.setIndeterminate(false);
            org.openide.awt.Mnemonics.setLocalizedText(
                progressLabel,
                org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelEtaConfigurationUI.class,
                    "EtaWizardPanelEtaConfiguration.progressLabel.error")); // NOI18N
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");
        }

        this.lastSwmmProjectId = this.model.getSwmmProjectId();
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
    private EtaConfigurationTableModel initCSOs(final int swmmProjectId) throws WizardInitialisationException {
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
        this.model.setEtaConfigurations(etaConfigurations);
        return new EtaConfigurationTableModel(etaConfigurations);

//        {
//            LOG.warn("eta configuration disabled, creating fixed configurations available at server side");
//            final List<EtaConfiguration> etaConfigurations = this.createFixedEtaConfigurations();
//            this.model.setEtaConfigurations(etaConfigurations);
//            this.etaConfigurationTableModel = new EtaConfigurationTableModel(etaConfigurations);
//        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        etaConfigurationPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        jScrollPaneEtaConfiguration = new javax.swing.JScrollPane();
        tblEtaConfiguration = new javax.swing.JTable();
        progressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        etaConfigurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelEtaConfigurationUI.class,
                    "EtaWizardPanelEtaConfigurationUI.etaConfigurationPanel.border.title"))); // NOI18N
        etaConfigurationPanel.setLayout(new java.awt.GridBagLayout());

        cardPanel.setLayout(new java.awt.CardLayout());

        tblEtaConfiguration.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        tblEtaConfiguration.setEnabled(false);
        jScrollPaneEtaConfiguration.setViewportView(tblEtaConfiguration);

        cardPanel.add(jScrollPaneEtaConfiguration, "csos");

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
                EtaWizardPanelEtaConfigurationUI.class,
                "EtaWizardPanelEtaConfigurationUI.progressLabel.text")); // NOI18N
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
        etaConfigurationPanel.add(cardPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(etaConfigurationPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public EtaWizardPanelEtaConfiguration getModel() {
        return model;
    }

    /**
     * Just to show a list of fixed eta confiurations (available at server side) , since the SPS does not support eta
     * configurations in V2.
     *
     * @return      DOCUMENT ME!
     *
     * @deprecated  DOCUMENT ME!
     */
    private List<EtaConfiguration> createFixedEtaConfigurations() {
        final List<EtaConfiguration> etaConfigurationList = new java.util.ArrayList<EtaConfiguration>();
        EtaConfiguration etaConfiguration;

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("RDSRUE51");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("ULKS1");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(20);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("FUEAusl");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("RKL_Ablauf");
        etaConfiguration.setEnabled(false);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("AB_Plesching");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(20);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HSU12_1S5b");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HSU1_1RUE2");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("ALBSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("ALKSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("ANFSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("EDBSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("ENNSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("ENNSP2nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("RUEB_Traunnolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("EWDSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("FKDSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("GLWSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("GRSSP2nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HEMSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HHSSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HOESP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HOESP2nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HZDSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("KRTSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("KSSSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("LTBSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("LTBSP2nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("LTBSP2nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("RUEB_Lunzerstr2nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("NNKSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("OFTSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("OTHSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("RUEB_Pleschingnolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("PNASP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("PUKSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("RDS20_1S48Anolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("SMMSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("STFSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("STMSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("STYSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("RHHB_Weikerlsee3nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("HSMSEntlnolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("WLDSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("WLDSP2nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        etaConfiguration = new EtaConfiguration();
        etaConfiguration.setName("WLGSP1nolink");
        etaConfiguration.setEnabled(true);
        etaConfiguration.setSedimentationEfficency(0);
        etaConfigurationList.add(etaConfiguration);

        return etaConfigurationList;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class EtaConfigurationTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final List<EtaConfiguration> etaConfigurations;
        // FIXME: i18n
        private final String[] columnNames = { "CSO", "Aktiv", "η Sedimentation" };
        private final Class[] columnClasses = { String.class, Boolean.class, Float.class };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EtaConfigurationTableModel object.
         *
         * @param  etaConfigurations  metaObjects DOCUMENT ME!
         */
        private EtaConfigurationTableModel(final List<EtaConfiguration> etaConfigurations) {
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
    private class CsoUpdater implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private transient boolean run = true;
        private EtaConfigurationTableModel etaConfigurationTableModel;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        public void stopIt() {
            run = false;
            LOG.warn("CsoUpdater stopped");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isRunning() {
            return run;
        }

        @Override
        public void run() {
            if (run) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("CsoUpdater: loading results");
                    }
                    etaConfigurationTableModel = initCSOs(model.getSwmmProjectId());
                } catch (Throwable t) {
                    LOG.error("CsoUpdater: could not retrieve CSOs: " + t.getMessage(), t);
                    run = false;
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setIndeterminate(false);
                                org.openide.awt.Mnemonics.setLocalizedText(
                                    progressLabel,
                                    org.openide.util.NbBundle.getMessage(
                                        EtaWizardPanelEtaConfigurationUI.class,
                                        "EtaWizardPanelEtaConfigurationUI.progressLabel.error")); // NOI18N
                            }
                        });
                }

                if (run) {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("CsoUpdater: updating loaded results");
                                }
                                tblEtaConfiguration.setModel(etaConfigurationTableModel);
                                ((CardLayout)cardPanel.getLayout()).show(cardPanel, "csos");
                                run = false;
                            }
                        });
                } else {
                    LOG.warn("CsoUpdater stopped, ignoring retrieved results");
                }
            }
        }
    }
}
