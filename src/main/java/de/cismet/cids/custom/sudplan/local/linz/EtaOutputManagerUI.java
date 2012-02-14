/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * EtaOutputManagerUI.java
 *
 * Created on 07.12.2011, 19:05:30
 */
package de.cismet.cids.custom.sudplan.local.linz;

import eu.hansolo.steelseries.tools.LedColor;
import eu.hansolo.steelseries.tools.Section;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import java.awt.EventQueue;
import java.awt.GridLayout;

import java.io.IOException;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.Titled;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaOutputManagerUI extends javax.swing.JPanel implements Titled {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaInputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    final Section[] SECTION_ETA_SED_75 = {
            new Section(0, 65, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(65, 75, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(75, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    final Section[] R720 = {
            new Section(0, 30, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(30, 50, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(50, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final Section[] SECTION_ETA_HYD_50 = {
            new Section(0, 40, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(40, 50, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(60, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final Section[] SECTION_ETA_HYD_60 = {
            new Section(0, 50, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(50, 60, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(60, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final Section[] SECTION_ETA_SED_65 = {
            new Section(0, 55, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(55, 65, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(65, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final transient EtaOutputManager outputManager;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private eu.hansolo.steelseries.gauges.Radial etaHydGauge;
    private eu.hansolo.steelseries.gauges.Radial etaSedGauge;
    private javax.swing.JPanel fancyResultPanel;
    private javax.swing.JTextField fld_720;
    private javax.swing.JTextField fld_etaHydActual;
    private javax.swing.JTextField fld_etaHydRequired;
    private javax.swing.JTextField fld_etaSedActual;
    private javax.swing.JTextField fld_etaSedRequired;
    private javax.swing.JTextField fld_totalOverflowVolume;
    private javax.swing.JLabel lblEtaHyd;
    private javax.swing.JLabel lblEtaSed;
    private javax.swing.JLabel lblR720;
    private javax.swing.JLabel lblTotalOverflow;
    private javax.swing.JLabel lbl_etaHydActual;
    private javax.swing.JLabel lbl_etaHydRequired;
    private javax.swing.JLabel lbl_etaSedActual;
    private javax.swing.JLabel lbl_etaSedRequired;
    private javax.swing.JLabel lbl_r720;
    private javax.swing.JLabel lbl_totalOverflowVolume;
    private eu.hansolo.steelseries.gauges.Radial2Top r720Gauge;
    private javax.swing.JPanel singleResultPanel;
    private eu.hansolo.steelseries.gauges.Radial2Top totalOverflowGauge;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EtaOutputManagerUI.
     *
     * @param  outputManager  DOCUMENT ME!
     */
    public EtaOutputManagerUI(final EtaOutputManager outputManager) {
        this.outputManager = outputManager;
        initComponents();
        this.r720Gauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        this.totalOverflowGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        init();
    }

    /**
     * Creates a new EtaOutputManagerUI object.
     */
    private EtaOutputManagerUI() {
        this.outputManager = null;
        initComponents();
        this.r720Gauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        this.totalOverflowGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        this.add(this.fancyResultPanel);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  etaOutput  DOCUMENT ME!
     */
    private void updateGauges(final EtaOutput etaOutput) {
        this.etaHydGauge.setThreshold(etaOutput.getEtaHydRequired());
        this.etaHydGauge.setToolTipText(new DecimalFormat("#.##").format(etaOutput.getEtaHydActual()));
        switch ((int)etaOutput.getEtaHydRequired()) {
            case 50: {
                this.etaHydGauge.setSections(SECTION_ETA_HYD_50);
                this.etaHydGauge.setAreas(SECTION_ETA_HYD_50);
            }

            case 60: {
                this.etaHydGauge.setSections(SECTION_ETA_HYD_60);
                this.etaHydGauge.setAreas(SECTION_ETA_HYD_60);
            }

            default: {
                LOG.warn("ETA HYD not in expected range (50-60):" + etaOutput.getEtaHydRequired());
                this.etaHydGauge.setSections(SECTION_ETA_HYD_60);
                this.etaHydGauge.setAreas(SECTION_ETA_HYD_60);
            }
        }

        if (etaOutput.getEtaHydActual() >= etaOutput.getEtaHydRequired()) {
            this.etaHydGauge.setUserLedBlinking(false);
            this.etaHydGauge.setUserLedColor(LedColor.GREEN_LED);
        } else {
            this.etaHydGauge.setUserLedBlinking(true);
            this.etaHydGauge.setUserLedColor(LedColor.RED_LED);
        }

        this.etaSedGauge.setThreshold(etaOutput.getEtaSedRequired());
        this.etaSedGauge.setToolTipText(new DecimalFormat("#.##").format(etaOutput.getEtaSedActual()));
        switch ((int)etaOutput.getEtaSedRequired()) {
            case 50: {
                this.etaSedGauge.setSections(SECTION_ETA_SED_65);
                this.etaSedGauge.setAreas(SECTION_ETA_SED_65);
            }

            case 60: {
                this.etaSedGauge.setSections(SECTION_ETA_SED_75);
                this.etaSedGauge.setAreas(SECTION_ETA_SED_75);
            }

            default: {
                LOG.warn("ETA SED not in expected range (65-75):" + etaOutput.getEtaSedRequired());
                this.etaSedGauge.setSections(SECTION_ETA_SED_75);
                this.etaSedGauge.setAreas(SECTION_ETA_SED_75);
            }
        }

        if (etaOutput.getEtaSedActual() >= etaOutput.getEtaSedRequired()) {
            this.etaSedGauge.setUserLedBlinking(false);
            this.etaSedGauge.setUserLedColor(LedColor.GREEN_LED);
        } else {
            this.etaSedGauge.setUserLedBlinking(true);
            this.etaSedGauge.setUserLedColor(LedColor.RED_LED);
        }

        this.r720Gauge.setToolTipText(new DecimalFormat("#.##").format(etaOutput.getR720()));
        this.totalOverflowGauge.setToolTipText(new DecimalFormat("#.##").format(etaOutput.getTotalOverflowVolume()));
        if (etaOutput.getTotalOverflowVolume() > 0) {
            this.totalOverflowGauge.setMaxValue(etaOutput.getTotalOverflowVolume() * 1.5);
        } else {
            LOG.warn("total overflow volume not computed: " + etaOutput.getTotalOverflowVolume());
            this.totalOverflowGauge.setMaxValue(100);
        }

        this.etaHydGauge.setValueAnimated(etaOutput.getEtaHydActual());
        this.r720Gauge.setValueAnimated(etaOutput.getR720());
        this.totalOverflowGauge.setValueAnimated(etaOutput.getTotalOverflowVolume());
        this.etaSedGauge.setValueAnimated(etaOutput.getEtaSedActual());
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initilaising EtaOutputManagerUI V2");
        }

        if ((this.outputManager.getCidsBeans() != null) && !this.outputManager.getCidsBeans().isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("showing and comparing " + this.outputManager.getCidsBeans().size() + " eta results");
            }

            final EfficiencyRatesComparisionPanel etaOutputComparisionPanel = new EfficiencyRatesComparisionPanel();
            final TotalOverflowComparisionPanel totalOverflowComparisionPanel = new TotalOverflowComparisionPanel();

            final List<String> etaRunNames = new ArrayList<String>(this.outputManager.getCidsBeans().size());
            final List<EtaOutput> etaOutputs = new ArrayList<EtaOutput>(this.outputManager.getCidsBeans().size());

            for (final CidsBean modelOutputBean : this.outputManager.getCidsBeans()) {
                try {
                    etaOutputs.add(this.getEtaOutput(modelOutputBean));
                    etaRunNames.add((String)modelOutputBean.getProperty("name"));
                } catch (Throwable t) {
                    LOG.error("could not process model output '" + modelOutputBean + "': " + t.getMessage(), t);
                }
            }

            if (EventQueue.isDispatchThread()) {
                etaOutputComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);
                totalOverflowComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);

                final JPanel contentPanel = new JPanel(new GridLayout(2, 0, 10, 0));
                contentPanel.setOpaque(false);
                contentPanel.add(etaOutputComparisionPanel);
                contentPanel.add(totalOverflowComparisionPanel);
                this.removeAll();
                this.add(contentPanel);
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            etaOutputComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);
                            totalOverflowComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);

                            final JPanel contentPanel = new JPanel(new GridLayout(2, 0, 10, 0));
                            contentPanel.setOpaque(false);
                            contentPanel.add(etaOutputComparisionPanel);
                            contentPanel.add(totalOverflowComparisionPanel);
                            removeAll();
                            add(contentPanel);
                        }
                    });
            }
        } else if (this.outputManager.getCidsBean() != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("showing only one eta result");
            }
            final EtaOutput etaOutput = this.getEtaOutput();

            if (EventQueue.isDispatchThread()) {
                this.updateGauges(etaOutput);
                this.removeAll();
                this.add(this.fancyResultPanel);
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            updateGauges(etaOutput);
                            removeAll();
                            add(fancyResultPanel);
                        }
                    });
            }

//            this.fld_etaHydActual.setText(String.valueOf(etaOutput.getEtaHydActual()));
//            this.fld_etaHydRequired.setText(String.valueOf(etaOutput.getEtaHydRequired()));
//            this.fld_etaSedActual.setText(String.valueOf(etaOutput.getEtaSedActual()));
//            this.fld_etaSedRequired.setText(String.valueOf(etaOutput.getEtaSedRequired()));
//            this.fld_720.setText(String.valueOf(etaOutput.getR720()));
//            this.fld_totalOverflowVolume.setText(String.valueOf(etaOutput.getTotalOverflowVolume()));
//            this.add(this.singleResultPanel);

        } else {
            LOG.error("error initilaising UI: no cidsbean(s) set");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final EtaOutputManagerUI etaOutputManagerUI = new EtaOutputManagerUI();

        final EtaOutput etaOutput = new EtaOutput();
        etaOutput.setEtaHydActual(73.17948f);
        etaOutput.setEtaHydRequired(60);
        etaOutput.setEtaSedActual(78.41792f);
        etaOutput.setEtaSedRequired(75);
        etaOutput.setR720(14.227189f);
        etaOutput.setTotalOverflowVolume(610.362f);

        etaOutputManagerUI.updateGauges(etaOutput);

        final JFrame frame = new JFrame("EtaOutputManagerUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(etaOutputManagerUI);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public EtaOutput getEtaOutput() {
        try {
            return this.outputManager.getUR();
        } catch (Exception ex) {
            LOG.error("could not load eta output", ex);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelOutputBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public EtaOutput getEtaOutput(final CidsBean modelOutputBean) throws IOException {
        final String json = (String)modelOutputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, EtaOutput.class);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        singleResultPanel = new javax.swing.JPanel();
        lbl_r720 = new javax.swing.JLabel();
        lbl_etaHydRequired = new javax.swing.JLabel();
        lbl_etaSedRequired = new javax.swing.JLabel();
        lbl_etaHydActual = new javax.swing.JLabel();
        lbl_etaSedActual = new javax.swing.JLabel();
        lbl_totalOverflowVolume = new javax.swing.JLabel();
        fld_720 = new javax.swing.JTextField();
        fld_etaHydRequired = new javax.swing.JTextField();
        fld_etaSedRequired = new javax.swing.JTextField();
        fld_etaHydActual = new javax.swing.JTextField();
        fld_etaSedActual = new javax.swing.JTextField();
        fld_totalOverflowVolume = new javax.swing.JTextField();
        fancyResultPanel = new javax.swing.JPanel();
        lblEtaHyd = new javax.swing.JLabel();
        lblEtaSed = new javax.swing.JLabel();
        etaHydGauge = new eu.hansolo.steelseries.gauges.Radial();
        etaSedGauge = new eu.hansolo.steelseries.gauges.Radial();
        lblR720 = new javax.swing.JLabel();
        lblTotalOverflow = new javax.swing.JLabel();
        r720Gauge = new eu.hansolo.steelseries.gauges.Radial2Top();
        totalOverflowGauge = new eu.hansolo.steelseries.gauges.Radial2Top();

        singleResultPanel.setOpaque(false);
        singleResultPanel.setLayout(new java.awt.GridBagLayout());

        lbl_r720.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_r720.text"));        // NOI18N
        lbl_r720.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_r720.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_r720, gridBagConstraints);

        lbl_etaHydRequired.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydRequired.text"));        // NOI18N
        lbl_etaHydRequired.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydRequired.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaHydRequired, gridBagConstraints);

        lbl_etaSedRequired.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedRequired.text"));        // NOI18N
        lbl_etaSedRequired.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedRequired.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaSedRequired, gridBagConstraints);

        lbl_etaHydActual.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydActual.text"));        // NOI18N
        lbl_etaHydActual.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydActual.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaHydActual, gridBagConstraints);

        lbl_etaSedActual.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedActual.text"));        // NOI18N
        lbl_etaSedActual.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedActual.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaSedActual, gridBagConstraints);

        lbl_totalOverflowVolume.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_totalOverflowVolume.text"));        // NOI18N
        lbl_totalOverflowVolume.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_totalOverflowVolume.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_totalOverflowVolume, gridBagConstraints);

        fld_720.setColumns(4);
        fld_720.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_720, gridBagConstraints);

        fld_etaHydRequired.setColumns(4);
        fld_etaHydRequired.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaHydRequired, gridBagConstraints);

        fld_etaSedRequired.setColumns(4);
        fld_etaSedRequired.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaSedRequired, gridBagConstraints);

        fld_etaHydActual.setColumns(4);
        fld_etaHydActual.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaHydActual, gridBagConstraints);

        fld_etaSedActual.setColumns(4);
        fld_etaSedActual.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaSedActual, gridBagConstraints);

        fld_totalOverflowVolume.setColumns(4);
        fld_totalOverflowVolume.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_totalOverflowVolume, gridBagConstraints);

        fancyResultPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 50, 0, 50));
        fancyResultPanel.setOpaque(false);
        fancyResultPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        fancyResultPanel.setLayout(new java.awt.GridBagLayout());

        lblEtaHyd.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblEtaHyd.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaHyd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaHyd.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblEtaHyd.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 40);
        fancyResultPanel.add(lblEtaHyd, gridBagConstraints);

        lblEtaSed.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblEtaSed.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaSed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaSed.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblEtaSed.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 10, 10);
        fancyResultPanel.add(lblEtaSed, gridBagConstraints);

        etaHydGauge.setAreasVisible(true);
        etaHydGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        etaHydGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        etaHydGauge.setLcdVisible(false);
        etaHydGauge.setLedVisible(false);
        etaHydGauge.setMaximumSize(new java.awt.Dimension(400, 400));
        etaHydGauge.setSectionsVisible(true);
        etaHydGauge.setThreshold(0.0);
        etaHydGauge.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        etaHydGauge.setThresholdVisible(true);
        etaHydGauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydGauge.title")); // NOI18N
        etaHydGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydGauge.unit"));  // NOI18N
        etaHydGauge.setUserLedVisible(true);

        final javax.swing.GroupLayout etaHydGaugeLayout = new javax.swing.GroupLayout(etaHydGauge);
        etaHydGauge.setLayout(etaHydGaugeLayout);
        etaHydGaugeLayout.setHorizontalGroup(
            etaHydGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));
        etaHydGaugeLayout.setVerticalGroup(
            etaHydGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 40);
        fancyResultPanel.add(etaHydGauge, gridBagConstraints);

        etaSedGauge.setAreasVisible(true);
        etaSedGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        etaSedGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        etaSedGauge.setLcdVisible(false);
        etaSedGauge.setLedVisible(false);
        etaSedGauge.setMaximumSize(new java.awt.Dimension(400, 400));
        etaSedGauge.setName(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedGauge.name"));       // NOI18N
        etaSedGauge.setSectionsVisible(true);
        etaSedGauge.setThreshold(0.0);
        etaSedGauge.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        etaSedGauge.setThresholdVisible(true);
        etaSedGauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedGauge.title"));      // NOI18N
        etaSedGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedGauge.unitString")); // NOI18N
        etaSedGauge.setUserLedVisible(true);

        final javax.swing.GroupLayout etaSedGaugeLayout = new javax.swing.GroupLayout(etaSedGauge);
        etaSedGauge.setLayout(etaSedGaugeLayout);
        etaSedGaugeLayout.setHorizontalGroup(
            etaSedGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));
        etaSedGaugeLayout.setVerticalGroup(
            etaSedGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 10, 10);
        fancyResultPanel.add(etaSedGauge, gridBagConstraints);

        lblR720.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblR720.setForeground(new java.awt.Color(51, 51, 51));
        lblR720.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblR720.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblR720.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(40, 10, 10, 40);
        fancyResultPanel.add(lblR720, gridBagConstraints);

        lblTotalOverflow.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTotalOverflow.setForeground(new java.awt.Color(51, 51, 51));
        lblTotalOverflow.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalOverflow.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblTotalOverflow.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(40, 40, 10, 10);
        fancyResultPanel.add(lblTotalOverflow, gridBagConstraints);

        r720Gauge.setAreasVisible(true);
        r720Gauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        r720Gauge.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE1);
        r720Gauge.setLedVisible(false);
        r720Gauge.setMaxValue(60.0);
        r720Gauge.setMaximumSize(new java.awt.Dimension(300, 300));
        r720Gauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Gauge.title")); // NOI18N
        r720Gauge.setTrackSection(30.0);
        r720Gauge.setTrackVisible(true);
        r720Gauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Gauge.unit"));  // NOI18N

        final javax.swing.GroupLayout r720GaugeLayout = new javax.swing.GroupLayout(r720Gauge);
        r720Gauge.setLayout(r720GaugeLayout);
        r720GaugeLayout.setHorizontalGroup(
            r720GaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));
        r720GaugeLayout.setVerticalGroup(
            r720GaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 40);
        fancyResultPanel.add(r720Gauge, gridBagConstraints);

        totalOverflowGauge.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowGauge.toolTipText")); // NOI18N
        totalOverflowGauge.setAreasVisible(true);
        totalOverflowGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        totalOverflowGauge.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE1);
        totalOverflowGauge.setLedVisible(false);
        totalOverflowGauge.setMaxValue(60.0);
        totalOverflowGauge.setMaximumSize(new java.awt.Dimension(300, 300));
        totalOverflowGauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowGauge.title"));       // NOI18N
        totalOverflowGauge.setTrackSection(30.0);
        totalOverflowGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowGauge.unitString"));  // NOI18N

        final javax.swing.GroupLayout totalOverflowGaugeLayout = new javax.swing.GroupLayout(totalOverflowGauge);
        totalOverflowGauge.setLayout(totalOverflowGaugeLayout);
        totalOverflowGaugeLayout.setHorizontalGroup(
            totalOverflowGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));
        totalOverflowGaugeLayout.setVerticalGroup(
            totalOverflowGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 0, 10);
        fancyResultPanel.add(totalOverflowGauge, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.GridLayout(1, 0, 50, 50));
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public String getTitle() {
        if ((this.outputManager.getCidsBeans() != null) && !this.outputManager.getCidsBeans().isEmpty()) {
            return org.openide.util.NbBundle.getMessage(
                        EtaOutputManagerUI.class,
                        "EtaOutputManagerUI.title.aggregated")
                        .replaceAll("%n", String.valueOf(this.outputManager.getCidsBeans().size()));
        } else if (this.outputManager.getCidsBean() != null) {
            return this.outputManager.getCidsBean().getProperty("name").toString();
        }

        return "";
    }

    @Override
    public void setTitle(final String title) {
        LOG.warn("set title '" + title + "' not supported by this UI");
    }
}
