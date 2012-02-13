/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.Border;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.ImageUtil;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class LinzSensorRenderer extends AbstractCidsBeanRenderer implements BorderProvider,
    CidsBeanRenderer,
    FooterComponentProvider,
    TitleComponentProvider,
    ActionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LinzSensorRenderer.class);
    private static final String CARD_1 = "CARD1";
    private static final String CARD_2 = "CARD2";

    //~ Instance fields --------------------------------------------------------

    private CardLayout cardLayout = null;
    // private String title;
    private final Timer timer;
    private LinzSensorRenderer.ImageResizeWorker currentResizeWorker;
    private boolean firstPageShowing = true;
    private transient BufferedImage wwtpSensorsImage;
    // private transient BufferedImage secondPageImage;
    private final transient LinzSensorTitleComponent linzSensorTitleComponent = new LinzSensorTitleComponent();

    private final javax.swing.Timer TIMER = new javax.swing.Timer(3000, this);
    private final javax.swing.Timer TIMER1 = new javax.swing.Timer(3500, this);
    // private final javax.swing.Timer TIMER2 = new javax.swing.Timer(2500, this);

    private boolean resizeListenerEnabled = true;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private eu.hansolo.steelseries.gauges.Radial codDeqGauge;
    private eu.hansolo.steelseries.gauges.Radial2Top inflowGauge;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblEtaHyd;
    private javax.swing.JLabel lblEtaSed;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblR720;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalOverflow;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panControl;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private javax.swing.JPanel panPage1;
    private javax.swing.JPanel panPage2;
    private javax.swing.JPanel panTitle;
    private eu.hansolo.steelseries.gauges.Radial tssSeqGauge;
    private eu.hansolo.steelseries.gauges.Radial2Top waterLevelGauge;
    private javax.swing.JLabel wwtpSensorsLabel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LinzWwtpRenderer.
     */
    public LinzSensorRenderer() {
        initComponents();
        final LayoutManager layoutManager = getLayout();
        if (layoutManager instanceof CardLayout) {
            cardLayout = (CardLayout)layoutManager;
            cardLayout.show(this, CARD_1);
        }

        try {
            wwtpSensorsImage = ImageIO.read(getClass().getResource(
                        "/de/cismet/cids/custom/sudplan/local/linz/wwtpsensors.png"));
        } catch (Throwable t) {
            wwtpSensorsImage = null;
            LOG.error(t);
        }

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new LinzSensorRenderer.ImageResizeWorker();
                            CismetThreadPool.execute(currentResizeWorker);
                        }
                    }
                });
        timer.setRepeats(false);

        this.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    timer.restart();
                }
            });
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

        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panPage1 = new javax.swing.JPanel();
        lblEtaHyd = new javax.swing.JLabel();
        lblEtaSed = new javax.swing.JLabel();
        codDeqGauge = new eu.hansolo.steelseries.gauges.Radial();
        tssSeqGauge = new eu.hansolo.steelseries.gauges.Radial();
        lblR720 = new javax.swing.JLabel();
        lblTotalOverflow = new javax.swing.JLabel();
        waterLevelGauge = new eu.hansolo.steelseries.gauges.Radial2Top();
        inflowGauge = new eu.hansolo.steelseries.gauges.Radial2Top();
        panPage2 = new javax.swing.JPanel();
        panControl = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        wwtpSensorsLabel = new javax.swing.JLabel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.BorderLayout());

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridBagLayout());

        panFooterLeft.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        lblBack.setText("Current");
        lblBack.setEnabled(false);
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblBackMouseClicked(evt);
                }
            });
        panFooterLeft.add(lblBack);

        btnBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/arrow-left.png"))); // NOI18N
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panFooterLeft.add(btnBack);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panButtons.add(panFooterLeft, gridBagConstraints);

        panFooterRight.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterRight.setOpaque(false);
        panFooterRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/arrow-right.png"))); // NOI18N
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new java.awt.Dimension(30, 30));
        btnForward.setMinimumSize(new java.awt.Dimension(30, 30));
        btnForward.setPreferredSize(new java.awt.Dimension(30, 30));
        btnForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnForwardActionPerformed(evt);
                }
            });
        panFooterRight.add(btnForward);

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblForw.setForeground(new java.awt.Color(255, 255, 255));
        lblForw.setText("Historic");
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblForwMouseClicked(evt);
                }
            });
        panFooterRight.add(lblForw);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panButtons.add(panFooterRight, gridBagConstraints);

        panFooter.add(panButtons, java.awt.BorderLayout.CENTER);

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.lblTitle.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        setBackground(new java.awt.Color(204, 204, 204));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panPage1.setBackground(new java.awt.Color(204, 204, 204));
        panPage1.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 0, 15));
        panPage1.setOpaque(false);
        panPage1.setPreferredSize(new java.awt.Dimension(800, 500));
        panPage1.setLayout(new java.awt.GridBagLayout());

        lblEtaHyd.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEtaHyd.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaHyd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaHyd.setText("<html><center>Chemical Oxygen Demand</center></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 40);
        panPage1.add(lblEtaHyd, gridBagConstraints);

        lblEtaSed.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEtaSed.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaSed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaSed.setText("<html><center>Total Suspended Solid</center></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 10, 10);
        panPage1.add(lblEtaSed, gridBagConstraints);

        codDeqGauge.setAreasVisible(true);
        codDeqGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        codDeqGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        codDeqGauge.setLcdVisible(false);
        codDeqGauge.setLedVisible(false);
        codDeqGauge.setMaxValue(2000.0);
        codDeqGauge.setMaximumSize(new java.awt.Dimension(400, 400));
        codDeqGauge.setPreferredSize(new java.awt.Dimension(220, 220));
        codDeqGauge.setSectionsVisible(true);
        codDeqGauge.setThreshold(0.0);
        codDeqGauge.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        codDeqGauge.setTitle("CODeq");
        codDeqGauge.setUnitString("mg/L");

        final org.jdesktop.layout.GroupLayout codDeqGaugeLayout = new org.jdesktop.layout.GroupLayout(codDeqGauge);
        codDeqGauge.setLayout(codDeqGaugeLayout);
        codDeqGaugeLayout.setHorizontalGroup(
            codDeqGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));
        codDeqGaugeLayout.setVerticalGroup(
            codDeqGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 40);
        panPage1.add(codDeqGauge, gridBagConstraints);

        tssSeqGauge.setAreasVisible(true);
        tssSeqGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        tssSeqGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        tssSeqGauge.setLcdVisible(false);
        tssSeqGauge.setLedVisible(false);
        tssSeqGauge.setMaxValue(1800.0);
        tssSeqGauge.setMaximumSize(new java.awt.Dimension(400, 400));
        tssSeqGauge.setName(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.tssSeqGauge.name")); // NOI18N
        tssSeqGauge.setPreferredSize(new java.awt.Dimension(220, 220));
        tssSeqGauge.setSectionsVisible(true);
        tssSeqGauge.setThreshold(0.0);
        tssSeqGauge.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        tssSeqGauge.setTitle("TSSeq");
        tssSeqGauge.setUnitString("mg/L");

        final org.jdesktop.layout.GroupLayout tssSeqGaugeLayout = new org.jdesktop.layout.GroupLayout(tssSeqGauge);
        tssSeqGauge.setLayout(tssSeqGaugeLayout);
        tssSeqGaugeLayout.setHorizontalGroup(
            tssSeqGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));
        tssSeqGaugeLayout.setVerticalGroup(
            tssSeqGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 10, 10);
        panPage1.add(tssSeqGauge, gridBagConstraints);

        lblR720.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblR720.setForeground(new java.awt.Color(51, 51, 51));
        lblR720.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblR720.setText("<html><center>Water Level</center></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 40);
        panPage1.add(lblR720, gridBagConstraints);

        lblTotalOverflow.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotalOverflow.setForeground(new java.awt.Color(51, 51, 51));
        lblTotalOverflow.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalOverflow.setText("<html><center>Inflow</center></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 10, 10);
        panPage1.add(lblTotalOverflow, gridBagConstraints);

        waterLevelGauge.setAreasVisible(true);
        waterLevelGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        waterLevelGauge.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE1);
        waterLevelGauge.setLedVisible(false);
        waterLevelGauge.setMaxValue(3.0);
        waterLevelGauge.setMaximumSize(new java.awt.Dimension(300, 300));
        waterLevelGauge.setTitle("Water level");
        waterLevelGauge.setTransparentAreasEnabled(true);
        waterLevelGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.waterLevelGauge.unitString")); // NOI18N

        final org.jdesktop.layout.GroupLayout waterLevelGaugeLayout = new org.jdesktop.layout.GroupLayout(
                waterLevelGauge);
        waterLevelGauge.setLayout(waterLevelGaugeLayout);
        waterLevelGaugeLayout.setHorizontalGroup(
            waterLevelGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));
        waterLevelGaugeLayout.setVerticalGroup(
            waterLevelGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 40);
        panPage1.add(waterLevelGauge, gridBagConstraints);

        inflowGauge.setToolTipText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.inflowGauge.toolTipText")); // NOI18N
        inflowGauge.setAreasVisible(true);
        inflowGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        inflowGauge.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE1);
        inflowGauge.setLedVisible(false);
        inflowGauge.setMaxValue(1200.0);
        inflowGauge.setMaximumSize(new java.awt.Dimension(300, 300));
        inflowGauge.setTitle("Qinflow");
        inflowGauge.setTrackSection(600.0);
        inflowGauge.setTrackStop(2000.0);
        inflowGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.inflowGauge.unitString"));  // NOI18N

        final org.jdesktop.layout.GroupLayout inflowGaugeLayout = new org.jdesktop.layout.GroupLayout(inflowGauge);
        inflowGauge.setLayout(inflowGaugeLayout);
        inflowGaugeLayout.setHorizontalGroup(
            inflowGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));
        inflowGaugeLayout.setVerticalGroup(
            inflowGaugeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                200,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 0, 10);
        panPage1.add(inflowGauge, gridBagConstraints);

        add(panPage1, "CARD1");

        panPage2.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panPage2.setOpaque(false);
        panPage2.setPreferredSize(new java.awt.Dimension(400, 400));
        panPage2.setLayout(new java.awt.BorderLayout(15, 15));

        panControl.setOpaque(false);
        panControl.setPreferredSize(new java.awt.Dimension(800, 40));
        panControl.setRequestFocusEnabled(false);
        panControl.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControl.add(jLabel1, gridBagConstraints);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.jTextField1.text")); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jTextField1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControl.add(jTextField1, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panControl.add(jLabel3, gridBagConstraints);

        jTextField2.setText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.jTextField2.text")); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jTextField2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControl.add(jTextField2, gridBagConstraints);

        jButton1.setText("OK");
        jButton1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panControl.add(jButton1, gridBagConstraints);

        panPage2.add(panControl, java.awt.BorderLayout.PAGE_START);

        wwtpSensorsLabel.setBackground(new java.awt.Color(255, 255, 255));
        wwtpSensorsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wwtpSensorsLabel.setText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.wwtpSensorsLabel.text")); // NOI18N
        panPage2.add(wwtpSensorsLabel, java.awt.BorderLayout.CENTER);

        add(panPage2, "CARD2");
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblBackMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblBackMouseClicked
        btnBackActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblBackMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(this, CARD_1);
        firstPageShowing = true;
        timer.restart();
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        lblBack.setEnabled(false);
        lblForw.setEnabled(true);
    }                                                                           //GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnForwardActionPerformed
        cardLayout.show(this, CARD_2);
        firstPageShowing = false;
        timer.restart();
        btnBack.setEnabled(true);
        btnForward.setEnabled(false);
        lblBack.setEnabled(true);
        lblForw.setEnabled(false);
    }                                                                              //GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblForwMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblForwMouseClicked
        btnForwardActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblForwMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextField1ActionPerformed(final java.awt.event.ActionEvent evt) //GEN-FIRST:event_jTextField1ActionPerformed
    {                                                                             //GEN-HEADEREND:event_jTextField1ActionPerformed
                                                                                  // TODO add your handling code here:
    }                                                                             //GEN-LAST:event_jTextField1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextField2ActionPerformed(final java.awt.event.ActionEvent evt) //GEN-FIRST:event_jTextField2ActionPerformed
    {                                                                             //GEN-HEADEREND:event_jTextField2ActionPerformed
                                                                                  // TODO add your handling code here:
    }                                                                             //GEN-LAST:event_jTextField2ActionPerformed

    @Override
    public Border getCenterrBorder() {
        return null;
    }

    @Override
    public Border getFooterBorder() {
        return null;
    }

    @Override
    public Border getTitleBorder() {
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (currentResizeWorker != null) {
            currentResizeWorker.cancel(true);
        }
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);
        this.linzSensorTitleComponent.setTitle(title);
        // this.title = title;
        // lblTitle.setText(this.title);
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    @Override
    public JComponent getTitleComponent() {
        // return panTitle;
        return linzSensorTitleComponent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        // DevelopmentTools.showTestFrame(new LinzWwtpRenderer(), 1024, 768);
        final LinzSensorRenderer linzWwtpRenderer = new LinzSensorRenderer();
        linzWwtpRenderer.setPreferredSize(new java.awt.Dimension(1024, 768));
        final JFrame frame = new JFrame("LinzWwtpRenderer");
        frame.getContentPane().add(linzWwtpRenderer, BorderLayout.CENTER);
        frame.getContentPane().add(linzWwtpRenderer.getTitleComponent(), BorderLayout.NORTH);
        frame.getContentPane().add(linzWwtpRenderer.getFooterComponent(), BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource().equals(TIMER)) {
            final double codDeq = Math.random() * 2000;
            if ((codDeq < 1600) && (codDeq > 200)) {
                this.codDeqGauge.setValueAnimated(codDeq);
            }

            final double waterLevel = Math.random() * 3;
            if ((waterLevel > 0.2) && (waterLevel < 2.2)) {
                this.waterLevelGauge.setValueAnimated(waterLevel);
            }
        }

        if (e.getSource().equals(TIMER1)) {
            final double tssSeq = Math.random() * 2000;
            if ((tssSeq < 1300) && (tssSeq > 175)) {
                this.tssSeqGauge.setValueAnimated(tssSeq);
            }

            final double inflow = Math.random() * 1000;
            this.inflowGauge.setValueAnimated(inflow);
        }
    }

    @Override
    protected void init() {
        TIMER.start();
        TIMER1.start();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageResizeWorker extends SwingWorker<ImageIcon, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageResizeWorker object.
         */
        public ImageResizeWorker() {
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            ImageIcon result = null;
            result = new ImageIcon(ImageUtil.adjustScale(wwtpSensorsImage, wwtpSensorsLabel, 0, 0));

            return result;
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    resizeListenerEnabled = false;
                    final ImageIcon result = get();
                    wwtpSensorsLabel.setIcon(result);
                } catch (InterruptedException ex) {
                    LOG.warn(ex, ex);
                } catch (ExecutionException ex) {
                    LOG.error(ex, ex);
                } finally {
                    if (currentResizeWorker == this) {
                        currentResizeWorker = null;
                    }
                    resizeListenerEnabled = true;
                }
            }
        }
    }
}
