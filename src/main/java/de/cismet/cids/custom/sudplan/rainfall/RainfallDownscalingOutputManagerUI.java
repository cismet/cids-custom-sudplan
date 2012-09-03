/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import de.cismet.cids.custom.sudplan.IDFCurve;
import de.cismet.cids.custom.sudplan.IDFCurvePanel;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesChartPanel;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class RainfallDownscalingOutputManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RainfallDownscalingOutputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient RainfallDownscalingOutput model;

    private final transient ActionListener runL;
    private final transient ActionListener inputL;
    private final transient ImageIcon tsIcon;
    private final transient ImageIcon idfIcon;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient org.jdesktop.swingx.JXHyperlink hypInput = new org.jdesktop.swingx.JXHyperlink();
    private final transient org.jdesktop.swingx.JXHyperlink hypRun = new org.jdesktop.swingx.JXHyperlink();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JTabbedPane jtpResults = new javax.swing.JTabbedPane();
    private final transient javax.swing.JLabel lblInput = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblRun = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblStatisticalCaption = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlStatisticalResults = new javax.swing.JPanel();
    private final transient javax.swing.JTable tblStatisticalResults = new javax.swing.JTable();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RainfallDownscalingOutputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public RainfallDownscalingOutputManagerUI(final RainfallDownscalingOutput model) {
        this.model = model;
        this.runL = new OpenRunActionListener();
        this.inputL = new OpenInputActionListener();
        this.tsIcon = ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/rainfall/timeseries.png", false); // NOI18N
        this.idfIcon = ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/rainfall/idf.png", false);       // NOI18N

        initComponents();

        init();

        hypRun.addActionListener(WeakListeners.create(ActionListener.class, runL, hypRun));
        hypInput.addActionListener(WeakListeners.create(ActionListener.class, inputL, hypInput));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void init() {
        final CidsBean inputTs = model.fetchInputRFObj();
        final CidsBean resultTs = model.fetchResultRFObj();

        final JPanel resultPanel;
        final JPanel inputPanel;
        final ImageIcon icon;

        if (SMSUtils.TABLENAME_TIMESERIES.equals(model.getRfObjTableName())) {
            final TimeseriesRetrieverConfig resultCfg;
            final TimeseriesRetrieverConfig inputCfg;

            try {
                resultCfg = TimeseriesRetrieverConfig.fromUrl((String)resultTs.getProperty("uri")); // NOI18N
                inputCfg = TimeseriesRetrieverConfig.fromUrl((String)inputTs.getProperty("uri"));   // NOI18N

                resultPanel = new JPanel();
                resultPanel.setOpaque(false);
                resultPanel.setLayout(new BorderLayout());
                resultPanel.add(new TimeseriesChartPanel(resultCfg, true), BorderLayout.CENTER);
                inputPanel = new JPanel();
                inputPanel.setOpaque(false);
                inputPanel.setLayout(new BorderLayout());
                inputPanel.add(new TimeseriesChartPanel(inputCfg, true), BorderLayout.CENTER);

                final Float[][] statData = model.getRfStatisticalData();
                if (statData == null) {
                    LOG.warn("incomplete rf ts ds data, statistical results missing"); // NOI18N
                } else {
                    final DefaultTableModel dtm = (DefaultTableModel)tblStatisticalResults.getModel();
                    for (int i = 0; i < statData.length; ++i) {
                        for (int j = 0; j < statData[i].length; ++j) {
                            dtm.setValueAt(statData[i][j] + "%", j, i + 1);
                        }
                    }
                }
            } catch (final MalformedURLException ex) {
                final String message = "illegal ts uri";                               // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }

            icon = tsIcon;
            tblStatisticalResults.setDefaultRenderer(String.class, new StatisticalResultsCellRenderer());
            tblStatisticalResults.setPreferredScrollableViewportSize(tblStatisticalResults.getPreferredSize());
        } else {
            final ObjectMapper mapper = new ObjectMapper();
            final String uriInput = (String)inputTs.getProperty("uri");   // NOI18N
            final String uriResult = (String)resultTs.getProperty("uri"); // NOI18N

            final IDFCurve idfInput;
            final IDFCurve idfResult;
            try {
                idfInput = mapper.readValue(uriInput, IDFCurve.class);
                idfResult = mapper.readValue(uriResult, IDFCurve.class);
            } catch (final Exception ex) {
                final String message = "cannot read idf data from uri"; // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }

            inputPanel = new IDFCurvePanel(idfInput);
            resultPanel = new IDFCurvePanel(idfResult);

            icon = idfIcon;
            this.remove(pnlStatisticalResults);
        }

        jtpResults.insertTab(model.getRfObjInputName(), icon, inputPanel, null, 0);
        jtpResults.insertTab(model.getRfObjResultName(), icon, resultPanel, null, 0);
        jtpResults.setSelectedIndex(0);

        final CidsBean runBean = SMSUtils.fetchCidsBean(model.getModelRunId(), SMSUtils.TABLENAME_MODELRUN);
        final CidsBean inputBean = SMSUtils.fetchCidsBean(model.getModelInputId(), SMSUtils.TABLENAME_MODELINPUT);

        hypRun.setText((String)runBean.getProperty("name"));     // NOI18N
        hypInput.setText((String)inputBean.getProperty("name")); // NOI18N
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jtpResults.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jtpResults.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(jtpResults, gridBagConstraints);

        lblRun.setFont(new java.awt.Font("Lucida Grande", 1, 13));  // NOI18N
        lblRun.setText(NbBundle.getMessage(
                RainfallDownscalingOutputManagerUI.class,
                "RainfallDownscalingOutputManagerUI.lblRun.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(lblRun, gridBagConstraints);

        hypRun.setText(NbBundle.getMessage(
                RainfallDownscalingOutputManagerUI.class,
                "RainfallDownscalingOutputManagerUI.hypRun.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(hypRun, gridBagConstraints);

        lblInput.setFont(new java.awt.Font("Lucida Grande", 1, 13));  // NOI18N
        lblInput.setText(NbBundle.getMessage(
                RainfallDownscalingOutputManagerUI.class,
                "RainfallDownscalingOutputManagerUI.lblInput.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(lblInput, gridBagConstraints);

        hypInput.setText(NbBundle.getMessage(
                RainfallDownscalingOutputManagerUI.class,
                "RainfallDownscalingOutputManagerUI.hypInput.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(hypInput, gridBagConstraints);

        pnlStatisticalResults.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                NbBundle.getMessage(
                    RainfallDownscalingOutputManagerUI.class,
                    "RainfallDownscalingOutputManagerUI.pnlStatisticalResults.border.title"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pnlStatisticalResults.setMinimumSize(new java.awt.Dimension(296, 150));
        pnlStatisticalResults.setOpaque(false);
        pnlStatisticalResults.setLayout(new java.awt.GridBagLayout());

        lblStatisticalCaption.setText(NbBundle.getMessage(
                RainfallDownscalingOutputManagerUI.class,
                "RainfallDownscalingOutputManagerUI.lblStatisticalCaption.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlStatisticalResults.add(lblStatisticalCaption, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 120));

        tblStatisticalResults.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { "Winter (Dec-Feb)", "+ 27 %", "+ 27 %", "+ 9 %" },
                    { "Spring (Mar-May)", "+ 15 %", "+ 16 %", "+ 8 %" },
                    { "Summer (Jun-Aug)", "- 14 %", "+ 20 %", "- 16 %" },
                    { "Autumn (Sep-Nov)", "+ 20 %", "+ 35 %", "- 3 %" }
                },
                new String[] {
                    "",
                    "Total season accumulation",
                    "Maximum 30-min intensity",
                    "Frequency of occurrence"
                }) {

                Class[] types = new Class[] {
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class
                    };
                boolean[] canEdit = new boolean[] { false, false, false, false };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        tblStatisticalResults.setMinimumSize(new java.awt.Dimension(250, 60));
        tblStatisticalResults.setPreferredSize(new java.awt.Dimension(500, 60));
        jScrollPane1.setViewportView(tblStatisticalResults);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlStatisticalResults.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(pnlStatisticalResults, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class StatisticalResultsCellRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel) {
                final JLabel label = (JLabel)c;
                if (column == 0) {
                    final TableCellRenderer tcr = tblStatisticalResults.getTableHeader().getDefaultRenderer();
                    c = tcr.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
                } else {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }
            }

            return c;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class OpenRunActionListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final MetaClass metaclass = ClassCacheMultiple.getMetaClass(
                    SessionManager.getSession().getUser().getDomain(),
                    SMSUtils.TABLENAME_MODELRUN);

            assert metaclass != null : "Run metaclass not present"; // NOI18N

            ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(metaclass, model.getModelRunId(), ""); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class OpenInputActionListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final MetaClass metaclass = ClassCacheMultiple.getMetaClass(
                    SessionManager.getSession().getUser().getDomain(),
                    SMSUtils.TABLENAME_MODELINPUT);

            assert metaclass != null : "ModelInput metaclass not present"; // NOI18N

            ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(metaclass, model.getModelInputId(), ""); // NOI18N
        }
    }
}
