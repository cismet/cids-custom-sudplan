/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXHyperlink;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.local.linz.SwmmOutputManager;
import de.cismet.cids.custom.sudplan.local.linz.SwmmOutputManagerUI;
import de.cismet.cids.custom.sudplan.server.search.CsoByOverflowSearch;
import de.cismet.cids.custom.sudplan.server.search.EtaResultSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public class SwmmProjectRenderer extends AbstractCidsBeanRenderer implements TitleComponentProvider,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmProjectRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final InputVerifier overflowVerifier = new InputVerifier() {

            @Override
            public boolean verify(final JComponent input) {
                final JTextField textField = ((JTextField)input);
                try {
                    final Float isFloat = Float.valueOf(textField.getText());
                    return isFloat.floatValue() >= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        };

    private final transient SwmmProjectTitleComponent titleComponent = new SwmmProjectTitleComponent();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntEtaSearch;
    private javax.swing.JButton bntSwmmSearch;
    private javax.swing.JComboBox cbSwmmRuns;
    private javax.swing.JCheckBox chbEtaHyd;
    private javax.swing.JCheckBox chbEtaSed;
    private javax.swing.JPanel configPanel;
    private javax.swing.JTextArea configurationArea;
    private javax.swing.JPanel etaAnalysisPanel;
    private javax.swing.JPanel etaRunPanel;
    private javax.swing.JTextField fldEtaHyd;
    private javax.swing.JTextField fldEtaSed;
    private javax.swing.JTextField fldOverflowVolume;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionText;
    private javax.swing.JLabel lblOverflowUnit;
    private javax.swing.JLabel lblSwmmAnalysisProject;
    private javax.swing.JLabel lblSwmmAnalysisVolume;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitleText;
    private javax.swing.JPanel pnlSpacer;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JPanel swmmAnalysisPanel;
    private javax.swing.JPanel swmmRunPanel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RunRenderer.
     */
    public SwmmProjectRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        final List<CidsBean> swmmScenarios = (List)cidsBean.getProperty("swmm_scenarios"); // NOI18N
        final List<CidsBean> etaScenarios = (List)cidsBean.getProperty("eta_scenarios");   // NOI18N
        final HashMap beansMap = new HashMap(swmmScenarios.size() + etaScenarios.size());
        final ScenarioListener scenarioListener = new ScenarioListener(beansMap);
        final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

        for (final CidsBean swmmBean : swmmScenarios) {
            final String key = "SWMM::" + swmmBean.getProperty("id");
            beansMap.put(key, swmmBean);
            final JXHyperlink hyperLink = new JXHyperlink();
            hyperLink.setText((String)swmmBean.getProperty("name")); // NOI18N
            hyperLink.setActionCommand(key);
            hyperLink.addActionListener(WeakListeners.create(
                    ActionListener.class,
                    scenarioListener,
                    hyperLink));
            this.swmmRunPanel.add(hyperLink, gridBagConstraints);
            gridBagConstraints.gridy++;

            comboBoxModel.addElement(swmmBean);
        }

        gridBagConstraints.gridx = 0;
        for (final CidsBean etaBean : etaScenarios) {
            final String key = "ETA::" + etaBean.getProperty("id");
            beansMap.put(key, etaBean);
            final JXHyperlink hyperLink = new JXHyperlink();
            hyperLink.setText((String)etaBean.getProperty("name")); // NOI18N
            hyperLink.setActionCommand(key);
            hyperLink.addActionListener(WeakListeners.create(
                    ActionListener.class,
                    scenarioListener,
                    hyperLink));
            this.etaRunPanel.add(hyperLink, gridBagConstraints);
            gridBagConstraints.gridy++;
        }

        this.lblTitleText.setText(cidsBean.getProperty("title").toString());
        this.lblDescriptionText.setText(cidsBean.getProperty("description").toString());

        this.configurationArea.setText(cidsBean.getProperty("options").toString());
        this.cbSwmmRuns.setModel(comboBoxModel);
        this.titleComponent.setCidsBean(cidsBean);
        this.bntSwmmSearch.setEnabled(!swmmScenarios.isEmpty());
        this.bntEtaSearch.setEnabled(!etaScenarios.isEmpty());
        if (!swmmScenarios.isEmpty()) {
            this.cbSwmmRuns.setSelectedIndex(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fldEtaHyd = new javax.swing.JTextField();
        fldEtaSed = new javax.swing.JTextField();
        lblTitle = new javax.swing.JLabel();
        lblTitleText = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblDescriptionText = new javax.swing.JLabel();
        previewPanel = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();
        configPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        configurationArea = new javax.swing.JTextArea();
        swmmRunPanel = new javax.swing.JPanel();
        swmmAnalysisPanel = new javax.swing.JPanel();
        lblSwmmAnalysisProject = new javax.swing.JLabel();
        lblSwmmAnalysisVolume = new javax.swing.JLabel();
        cbSwmmRuns = new javax.swing.JComboBox();
        fldOverflowVolume = new javax.swing.JTextField();
        lblOverflowUnit = new javax.swing.JLabel();
        bntSwmmSearch = new javax.swing.JButton();
        etaRunPanel = new javax.swing.JPanel();
        etaAnalysisPanel = new javax.swing.JPanel();
        chbEtaHyd = new javax.swing.JCheckBox();
        chbEtaSed = new javax.swing.JCheckBox();
        bntEtaSearch = new javax.swing.JButton();
        pnlSpacer = new javax.swing.JPanel();

        fldEtaHyd.setColumns(4);
        fldEtaHyd.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.fldEtaHyd.text")); // NOI18N

        fldEtaSed.setColumns(4);
        fldEtaSed.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.fldEtaSed.text")); // NOI18N

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblTitle.setText(NbBundle.getMessage(SwmmProjectRenderer.class, "SwmmProjectRenderer.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(lblTitle, gridBagConstraints);

        lblTitleText.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.lblTitleText.text")); // NOI18N
        lblTitleText.setMaximumSize(null);
        lblTitleText.setMinimumSize(null);
        lblTitleText.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblTitleText, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.lblDescription.text")); // NOI18N
        lblDescription.setMaximumSize(null);
        lblDescription.setMinimumSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(lblDescription, gridBagConstraints);

        lblDescriptionText.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.lblDescriptionText.text")); // NOI18N
        lblDescriptionText.setMaximumSize(null);
        lblDescriptionText.setMinimumSize(null);
        lblDescriptionText.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDescriptionText, gridBagConstraints);

        previewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.previewPanel.border.title"))); // NOI18N
        previewPanel.setOpaque(false);
        previewPanel.setLayout(new java.awt.GridLayout(1, 0));

        previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewLabel.setIcon(new javax.swing.JLabel() {

                @Override
                public javax.swing.Icon getIcon() {
                    try {
                        return new javax.swing.ImageIcon(
                                new java.net.URL(
                                    "http://sudplan.cismet.de/geoserver/wms?SERVICE=WMS&&VERSION=1.1.1&REQUEST=GetMap&BBOX=13.966774023257251,48.045408747871186,14.539433634959217,48.535798950568086&WIDTH=355&HEIGHT=304&SRS=EPSG:4326&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=Sudplan-Linz:Linz_TUG_WS_2011-05-09_SUBCATCHMENTS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_CONDUITS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_WEIRS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_PUMPS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_JUNCTIONS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_STORAGE_UNITS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_OUTFALLS&STYLES=Subcatchment,Conduit,line,pump,Junction,Storage_Unit,Outfall"));
                    } catch (java.net.MalformedURLException e) {
                    }
                    return null;
                }
            }.getIcon());
        previewLabel.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.previewLabel.text")); // NOI18N
        previewLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        previewLabel.setMaximumSize(null);
        previewLabel.setMinimumSize(new java.awt.Dimension(300, 300));
        previewLabel.setPreferredSize(null);
        previewPanel.add(previewLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(previewPanel, gridBagConstraints);

        configPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.configPanel.title"))); // NOI18N
        configPanel.setOpaque(false);
        configPanel.setLayout(new java.awt.GridLayout(1, 0));

        configurationArea.setColumns(20);
        configurationArea.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        configurationArea.setLineWrap(true);
        configurationArea.setRows(6);
        configurationArea.setTabSize(16);
        configurationArea.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.configurationArea.text"));            // NOI18N
        jScrollPane2.setViewportView(configurationArea);

        configPanel.add(jScrollPane2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(configPanel, gridBagConstraints);

        swmmRunPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.swmmRunPanel.border.title"))); // NOI18N
        swmmRunPanel.setOpaque(false);
        swmmRunPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(swmmRunPanel, gridBagConstraints);

        swmmAnalysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.swmmAnalysisPanel.border.title"))); // NOI18N
        swmmAnalysisPanel.setOpaque(false);
        swmmAnalysisPanel.setLayout(new java.awt.GridBagLayout());

        lblSwmmAnalysisProject.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.lblSwmmAnalysisProject.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        swmmAnalysisPanel.add(lblSwmmAnalysisProject, gridBagConstraints);

        lblSwmmAnalysisVolume.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.lblSwmmAnalysisVolume.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        swmmAnalysisPanel.add(lblSwmmAnalysisVolume, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        swmmAnalysisPanel.add(cbSwmmRuns, gridBagConstraints);

        fldOverflowVolume.setColumns(6);
        fldOverflowVolume.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.fldOverflowVolume.text")); // NOI18N
        fldOverflowVolume.setInputVerifier(this.overflowVerifier);
        fldOverflowVolume.setMinimumSize(new java.awt.Dimension(54, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        swmmAnalysisPanel.add(fldOverflowVolume, gridBagConstraints);

        lblOverflowUnit.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.lblOverflowUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        swmmAnalysisPanel.add(lblOverflowUnit, gridBagConstraints);

        bntSwmmSearch.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.bntSwmmSearch.text")); // NOI18N
        bntSwmmSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    bntSwmmSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        swmmAnalysisPanel.add(bntSwmmSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(swmmAnalysisPanel, gridBagConstraints);

        etaRunPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.etaRunPanel.border.title"))); // NOI18N
        etaRunPanel.setOpaque(false);
        etaRunPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(etaRunPanel, gridBagConstraints);

        etaAnalysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.etaAnalysisPanel.border.title"))); // NOI18N
        etaAnalysisPanel.setOpaque(false);
        etaAnalysisPanel.setLayout(new java.awt.GridBagLayout());

        chbEtaHyd.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.chbEtaHyd.text")); // NOI18N
        chbEtaHyd.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        etaAnalysisPanel.add(chbEtaHyd, gridBagConstraints);

        chbEtaSed.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.chbEtaSed.text")); // NOI18N
        chbEtaSed.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        etaAnalysisPanel.add(chbEtaSed, gridBagConstraints);

        bntEtaSearch.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.bntEtaSearch.text")); // NOI18N
        bntEtaSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    bntEtaSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        etaAnalysisPanel.add(bntEtaSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(etaAnalysisPanel, gridBagConstraints);

        pnlSpacer.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlSpacer, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void bntSwmmSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_bntSwmmSearchActionPerformed
        if (this.cbSwmmRuns.getSelectedItem() != null) {
            final int swmmRun = (Integer)((CidsBean)this.cbSwmmRuns.getSelectedItem()).getProperty("id");
            float overflowVolume = 0f;

            try {
                overflowVolume = Float.valueOf(this.fldOverflowVolume.getText());
            } catch (Throwable t) {
                LOG.warn(t.getMessage());
            }

            final CsoByOverflowSearch csoByOverflowSearch = new CsoByOverflowSearch(swmmRun, overflowVolume);

            LOG.info("performing search for SWMM RUN #" + swmmRun
                        + " and max. overflow volume " + overflowVolume);

            CidsSearchExecutor.searchAndDisplayResultsWithDialog(csoByOverflowSearch);
        } else {
            LOG.warn("no SWMM runs available to perform search");
        }
    } //GEN-LAST:event_bntSwmmSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void bntEtaSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_bntEtaSearchActionPerformed

        final int swmmProjectId = (Integer)this.getCidsBean().getProperty("id");
        int parameter = EtaResultSearch.NONE;
        if (this.chbEtaSed.isSelected()) {
            parameter += EtaResultSearch.ETA_SED;
        }
        if (this.chbEtaHyd.isSelected()) {
            parameter += EtaResultSearch.ETA_HYD;
        }

        final EtaResultSearch etaResultSearch = new EtaResultSearch(swmmProjectId, parameter);

        LOG.info("performing search for SWMM PROJECT #" + swmmProjectId
                    + " and parameter " + parameter);

        CidsSearchExecutor.searchAndDisplayResultsWithDialog(etaResultSearch);
    } //GEN-LAST:event_bntEtaSearchActionPerformed

    @Override
    public JComponent getTitleComponent() {
        return titleComponent;
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);
        titleComponent.setTitle(title);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            BasicConfigurator.configure();
            final CidsBean swmmProject = DevelopmentTools.createCidsBeanFromRMIConnectionOnLocalhost(
                    "SUDPLAN",
                    "Administratoren",
                    "admin",
                    "cismetz12",
                    "swmm_project",
                    2);

            final SwmmProjectRenderer swmmProjectRenderer = new SwmmProjectRenderer();
            swmmProjectRenderer.setCidsBean(swmmProject);
            swmmProjectRenderer.init();
            swmmProjectRenderer.setPreferredSize(new java.awt.Dimension(600, 400));
            final JFrame frame = new JFrame("SwmmProjectRenderer");
            frame.setContentPane(swmmProjectRenderer);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ScenarioListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        final HashMap<String, CidsBean> beansMap;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ScenarioListener object.
         *
         * @param  beansMap  DOCUMENT ME!
         */
        public ScenarioListener(final HashMap<String, CidsBean> beansMap) {
            this.beansMap = beansMap;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if ((beansMap != null) && beansMap.containsKey(e.getActionCommand())) {
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObject(beansMap.get(e.getActionCommand()).getMetaObject(), null);
            } else {
                LOG.warn("beans map does not contain cids bean '" + e.getActionCommand() + "'");
            }
        }
    }
}
