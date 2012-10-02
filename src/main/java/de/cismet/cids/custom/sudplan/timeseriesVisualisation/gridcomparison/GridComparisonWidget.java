/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ItemEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.MessageFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyle.Entry;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;
import de.cismet.cismap.commons.raster.wms.WMSServiceLayer;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;
import de.cismet.cismap.commons.wms.capabilities.Layer;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class GridComparisonWidget extends javax.swing.JPanel implements FeatureCollectionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridComparisonWidget.class);

    //~ Instance fields --------------------------------------------------------

    private final LayerStylesChangeListener layerStylesChangeListener;
    private final EnableGridComparisonRetrievalListener enableGridComparisonListener;
    private GridComparator gridComparator = null;
    private SlidingImagesFeature feature = null;
    private Distribution firstDistribution;
    private Distribution secondDistribution;
    private SlidableWMSServiceLayerGroup firstOperand;
    private SlidableWMSServiceLayerGroup secondOperand;
    private LayerStyle layerStyle;
    private LayerStyle modifiedLayerStyle;
    private GridComparator.Operation operation;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbComparisonMethod;
    private javax.swing.JComboBox cmbFirstOperand;
    private javax.swing.JComboBox cmbLayerStyle;
    private javax.swing.JComboBox cmbSecondOperand;
    private de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.DistributionComponent disDistributions;
    private javax.swing.Box.Filler gluLayerStyle;
    private javax.swing.Box.Filler gluMain;
    private javax.swing.Box.Filler gluOperationHints;
    private javax.swing.JLabel lblFirstOperand;
    private javax.swing.JLabel lblOperationHintMax;
    private javax.swing.JLabel lblOperationHintMin;
    private javax.swing.JLabel lblSecondOperand;
    private de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyleComponent lscLayerStyle;
    private javax.swing.JPanel pnlLayerStyle;
    private javax.swing.JPanel pnlLayerStyleAdditionalInformation;
    private javax.swing.JPanel pnlOperationHints;
    private javax.swing.JSlider sldContrastResult;
    private javax.swing.JSlider sldTimestamp;
    private javax.swing.Box.Filler strCmbLayerStyle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GridComparisonWidget.
     */
    public GridComparisonWidget() {
        enableGridComparisonListener = new EnableGridComparisonRetrievalListener();
        layerStylesChangeListener = new LayerStylesChangeListener();

        initComponents();

        cmbFirstOperand.setRenderer(new SlidableWMSServiceLayerGroupCellRenderer());
        cmbSecondOperand.setRenderer(new SlidableWMSServiceLayerGroupCellRenderer());

        cmbLayerStyle.insertItemAt(NbBundle.getMessage(
                GridComparisonWidget.class,
                "GridComparisonWidget.cmbLayerStyle.selectAStyle"),
            0);
        cmbLayerStyle.insertItemAt(NbBundle.getMessage(
                GridComparisonWidget.class,
                "GridComparisonWidget.cmbLayerStyle.automaticStyle"),
            1);
        LayerStyles.instance().addPropertyChangeListener(layerStylesChangeListener);

        lscLayerStyle.setVisible(false);

        String cmbComparisonMethodNoSelection = "Please select an operation";
        String nullValueForLayerCmbs = "Please select an operation";
        try {
            cmbComparisonMethodNoSelection = NbBundle.getMessage(
                    GridComparisonWidget.class,
                    "GridComparisonWidget.cmbComparisonMethod.noSelection");
            nullValueForLayerCmbs = NbBundle.getMessage(
                    SlidableWMSServiceLayerGroupCellRenderer.class,
                    "GridComparisonWidget.SlidableWMSServiceLayerGroupCellRenderer.nullValue");
        } catch (final MissingResourceException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Didn't find resource for no selection value of combo box for operations.", ex);
            }
        }
        cmbComparisonMethod.insertItemAt(cmbComparisonMethodNoSelection, 0);

        cmbComparisonMethod.setPrototypeDisplayValue(cmbComparisonMethodNoSelection);
        cmbFirstOperand.setPrototypeDisplayValue(nullValueForLayerCmbs);
        cmbSecondOperand.setPrototypeDisplayValue(nullValueForLayerCmbs);

        StaticSwingTools.enableSliderToolTips(sldContrastResult, new MessageFormat("Contrast: {0,number,#0.0}"), .1D);
        sldContrastResult.setEnabled(false);
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

        disDistributions =
            new de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.DistributionComponent();
        pnlOperationHints = new javax.swing.JPanel();
        lblOperationHintMin = new javax.swing.JLabel();
        lblOperationHintMax = new javax.swing.JLabel();
        gluOperationHints = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        cmbFirstOperand = new javax.swing.JComboBox();
        lblFirstOperand = new javax.swing.JLabel();
        lblSecondOperand = new javax.swing.JLabel();
        cmbSecondOperand = new javax.swing.JComboBox();
        sldTimestamp = new javax.swing.JSlider();
        cmbComparisonMethod = new javax.swing.JComboBox();
        gluMain = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        sldContrastResult = new javax.swing.JSlider();
        cmbLayerStyle = new javax.swing.JComboBox();
        strCmbLayerStyle = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20),
                new java.awt.Dimension(0, 20),
                new java.awt.Dimension(32767, 20));
        pnlLayerStyle = new javax.swing.JPanel();
        lscLayerStyle = new de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyleComponent();
        gluLayerStyle = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlLayerStyleAdditionalInformation = new javax.swing.JPanel();

        pnlOperationHints.setLayout(new java.awt.GridBagLayout());

        lblOperationHintMin.setText(org.openide.util.NbBundle.getMessage(
                GridComparisonWidget.class,
                "GridComparisonWidget.lblOperationHintMin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlOperationHints.add(lblOperationHintMin, gridBagConstraints);

        lblOperationHintMax.setText(org.openide.util.NbBundle.getMessage(
                GridComparisonWidget.class,
                "GridComparisonWidget.lblOperationHintMax.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        pnlOperationHints.add(lblOperationHintMax, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlOperationHints.add(gluOperationHints, gridBagConstraints);

        setName(org.openide.util.NbBundle.getMessage(GridComparisonWidget.class, "GridComparisonWidget.name")); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        cmbFirstOperand.setEnabled(false);
        cmbFirstOperand.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(final java.awt.event.ItemEvent evt) {
                    cmbFirstOperandItemStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cmbFirstOperand, gridBagConstraints);

        lblFirstOperand.setText(org.openide.util.NbBundle.getMessage(
                GridComparisonWidget.class,
                "GridComparisonWidget.lblFirstOperand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblFirstOperand, gridBagConstraints);

        lblSecondOperand.setText(org.openide.util.NbBundle.getMessage(
                GridComparisonWidget.class,
                "GridComparisonWidget.lblSecondOperand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblSecondOperand, gridBagConstraints);

        cmbSecondOperand.setEnabled(false);
        cmbSecondOperand.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(final java.awt.event.ItemEvent evt) {
                    cmbSecondOperandItemStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cmbSecondOperand, gridBagConstraints);

        sldTimestamp.setEnabled(false);
        sldTimestamp.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sldTimestampStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(sldTimestamp, gridBagConstraints);

        cmbComparisonMethod.setModel(new DefaultComboBoxModel(GridComparator.Operation.values()));
        cmbComparisonMethod.setEnabled(false);
        cmbComparisonMethod.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmbComparisonMethodActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cmbComparisonMethod, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        add(gluMain, gridBagConstraints);

        sldContrastResult.setMaximum(160);
        sldContrastResult.setMinimum(1);
        sldContrastResult.setValue(10);
        sldContrastResult.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sldContrastResultStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(sldContrastResult, gridBagConstraints);

        cmbLayerStyle.setEnabled(false);
        cmbLayerStyle.setRenderer(new LayerStyleCellRenderer());
        cmbLayerStyle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmbLayerStyleActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        add(cmbLayerStyle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(strCmbLayerStyle, gridBagConstraints);

        pnlLayerStyle.setLayout(new java.awt.GridBagLayout());

        lscLayerStyle.setMinimumSize(new java.awt.Dimension(0, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlLayerStyle.add(lscLayerStyle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlLayerStyle.add(gluLayerStyle, gridBagConstraints);

        pnlLayerStyleAdditionalInformation.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlLayerStyle.add(pnlLayerStyleAdditionalInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlLayerStyle, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmbComparisonMethodActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmbComparisonMethodActionPerformed
        pnlLayerStyleAdditionalInformation.removeAll();

        final Object operationObj = cmbComparisonMethod.getSelectedItem();
        if (!(operationObj instanceof GridComparator.Operation)) {
            operation = null;

            // The user reset the operation, but if there is a valid layer style, we still display the distributions.
            if ((firstOperand != null) && (secondOperand != null) && (layerStyle != null)) {
                pnlLayerStyleAdditionalInformation.add(disDistributions, BorderLayout.CENTER);
            }

            sldContrastResult.setValue(10);
            sldContrastResult.setEnabled(false);

            lscLayerStyle.setLayerStyle(layerStyle);
        } else {
            operation = (GridComparator.Operation)operationObj;
        }

        refreshFeature();

        if ((firstDistribution == null) || (secondDistribution == null) || (operation == null)) {
            return;
        }

        switch (operation) {
            case SUBTRACTION: {
                final List<Entry> colorMap = layerStyle.getColorMap();
                final List<Entry> modifiedColorMap = new LinkedList<Entry>();
                modifiedColorMap.add(new Entry(
                        colorMap.get(0).getValue()
                                - colorMap.get(1).getValue(),
                        colorMap.get(1).getColor()));
                modifiedColorMap.add(new Entry(0D, Color.black));
                modifiedColorMap.add(new Entry(
                        colorMap.get(1).getValue()
                                - colorMap.get(0).getValue(),
                        colorMap.get(0).getColor()));

                modifiedLayerStyle = new LayerStyle(firstOperand.getName() + " - " + secondOperand.getName(),
                        modifiedColorMap);

                lscLayerStyle.setLayerStyle(modifiedLayerStyle);
                sldContrastResult.setEnabled(true);

                lblOperationHintMin.setText(NbBundle.getMessage(
                        GridComparisonWidget.class,
                        "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).lblOperationHintMin.operation.subtraction",
                        NbBundle.getMessage(
                            GridComparisonWidget.class,
                            "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).operation.subtraction.firstOperand"),
                        NbBundle.getMessage(
                            GridComparisonWidget.class,
                            "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).operation.subtraction.secondOperand")));
                lblOperationHintMax.setText(NbBundle.getMessage(
                        GridComparisonWidget.class,
                        "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).lblOperationHintMax.operation.subtraction",
                        NbBundle.getMessage(
                            GridComparisonWidget.class,
                            "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).operation.subtraction.firstOperand"),
                        NbBundle.getMessage(
                            GridComparisonWidget.class,
                            "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).operation.subtraction.secondOperand")));
                lblOperationHintMin.setToolTipText(NbBundle.getMessage(
                        GridComparisonWidget.class,
                        "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).lblOperationHintMin.operation.subtraction",
                        GridComparisonLayerProvider.generateMenuRepresentation(firstOperand),
                        GridComparisonLayerProvider.generateMenuRepresentation(secondOperand)));
                lblOperationHintMax.setToolTipText(NbBundle.getMessage(
                        GridComparisonWidget.class,
                        "GridComparisonWidget.cmbComparisonMethodActionPerformed(ActionEvent).lblOperationHintMax.operation.subtraction",
                        GridComparisonLayerProvider.generateMenuRepresentation(firstOperand),
                        GridComparisonLayerProvider.generateMenuRepresentation(secondOperand)));

                pnlOperationHints.setVisible(true);

                pnlLayerStyleAdditionalInformation.add(pnlOperationHints, BorderLayout.CENTER);

                break;
            }
            case AVERAGE: {
                modifiedLayerStyle = layerStyle;
                lscLayerStyle.setLayerStyle(modifiedLayerStyle);

                sldContrastResult.setEnabled(false);
                sldContrastResult.setValue(10);
            }
        }
    } //GEN-LAST:event_cmbComparisonMethodActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void sldTimestampStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_sldTimestampStateChanged
        if (feature != null) {
            feature.setSliderPosition(sldTimestamp.getValue());
            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().reconsiderFeature(feature);
        }
    }                                                                                //GEN-LAST:event_sldTimestampStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmbFirstOperandItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_cmbFirstOperandItemStateChanged
        // If the combo box is disabled, we are in the progress of reloading layers. Within this progress, the combo box
        // will be enabled again. Then this method will be invoked by changing the selection.
        if ((evt.getStateChange() != ItemEvent.SELECTED) || !cmbFirstOperand.isEnabled()) {
            return;
        }

        if (evt.getItem() instanceof SlidableWMSServiceLayerGroup) {
            setFirstOperand((SlidableWMSServiceLayerGroup)evt.getItem());
        } else {
            setFirstOperand(null);
        }
    } //GEN-LAST:event_cmbFirstOperandItemStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmbSecondOperandItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_cmbSecondOperandItemStateChanged
        // If the combo box is disabled, we are in the progress of reloading layers. Within this progress, the combo box
        // will be enabled again. Then this method will be invoked by changing the selection.
        if ((evt.getStateChange() != ItemEvent.SELECTED) || !cmbSecondOperand.isEnabled()) {
            return;
        }

        if (evt.getItem() instanceof SlidableWMSServiceLayerGroup) {
            setSecondOperand((SlidableWMSServiceLayerGroup)evt.getItem());
        } else {
            setSecondOperand(null);
        }
    } //GEN-LAST:event_cmbSecondOperandItemStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmbLayerStyleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmbLayerStyleActionPerformed
        final Object layerStyleObj = cmbLayerStyle.getSelectedItem();
        final List<Entry> colorMap;

        if (layerStyleObj instanceof LayerStyle) {
            layerStyle = (LayerStyle)layerStyleObj;
            colorMap = layerStyle.getColorMap();
        } else {
            layerStyle = null;
            colorMap = null;
        }

        lscLayerStyle.setLayerStyle(layerStyle);

        if ((colorMap == null) || colorMap.isEmpty()) {
            lscLayerStyle.setVisible(false);
            disDistributions.setVisible(false);
        } else {
            lscLayerStyle.setVisible(true);
            disDistributions.setVisible(true);

            disDistributions.setMin(colorMap.get(0).getValue());
            disDistributions.setMax(colorMap.get(colorMap.size() - 1).getValue());
        }

        checkControls();
        cmbComparisonMethod.setSelectedIndex(0);

        if ((firstOperand != null) && (secondOperand != null)) {
            if (layerStyle != null) {
                firstOperand.setCustomSLD(layerStyle.getSLDForLayer());
                secondOperand.setCustomSLD(layerStyle.getSLDForLayer());
            } else {
                firstOperand.setCustomSLD(null);
                secondOperand.setCustomSLD(null);
            }

            firstOperand.retrieve(true);
            secondOperand.retrieve(true);
        }

        initGridComparator();
    } //GEN-LAST:event_cmbLayerStyleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void sldContrastResultStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_sldContrastResultStateChanged
        if (sldContrastResult.getValueIsAdjusting()) {
            if (operation == null) {
                return;
            }

            switch (operation) {
                case SUBTRACTION: {
                    final List<Entry> colorMap = layerStyle.getColorMap();
                    final List<Entry> modifiedColorMap = new LinkedList<Entry>();

                    final double factor = sldContrastResult.getValue() * .1D;
                    final double range = Math.abs((colorMap.get(1).getValue() - colorMap.get(0).getValue()) / factor);

                    modifiedColorMap.add(new Entry(0D - range, colorMap.get(0).getColor()));
                    modifiedColorMap.add(new Entry(0D, Color.black));
                    modifiedColorMap.add(new Entry(0D + range, colorMap.get(1).getColor()));

                    modifiedLayerStyle = new LayerStyle(firstOperand.getName() + " - " + secondOperand.getName(),
                            modifiedColorMap);

                    break;
                }
                case AVERAGE: {
                    break;
                }
            }

            lscLayerStyle.setLayerStyle(modifiedLayerStyle);
        } else {
            refreshFeature();
        }
    } //GEN-LAST:event_sldContrastResultStateChanged

    /**
     * DOCUMENT ME!
     */
    protected void reloadLayers() {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final List<SlidableWMSServiceLayerGroup> layers = GridComparisonLayerProvider.instance()
                                .getLayers(true);

                    cmbFirstOperand.removeAllItems();
                    cmbSecondOperand.removeAllItems();

                    if ((layers == null) || (layers.size() < 2)) {
                        firstOperand = null;
                        secondOperand = null;
                        layerStyle = null;

                        cmbLayerStyle.setSelectedIndex(0);

                        checkControls();

                        return;
                    }

                    for (final SlidableWMSServiceLayerGroup layer : layers) {
                        cmbFirstOperand.addItem(layer);
                        cmbSecondOperand.addItem(layer);
                    }

                    // Don't let the selection be changed by just adding some items
                    cmbFirstOperand.setSelectedItem(null);
                    cmbSecondOperand.setSelectedItem(null);

                    // Enable the layer selectors in order to allow processing of selection changes.
                    cmbFirstOperand.setEnabled(true);
                    cmbSecondOperand.setEnabled(true);

                    if (!(layers.contains(firstOperand) && !(layers.contains(secondOperand)))) {
                        setOperands(layers.get(0), layers.get(1));

                        // Set the automatic layer style after the new operands have been set. setOperands() appends
                        // a Runnable at the end of the EventQueue, thus forcing us to generate and set the automatic
                        // layer style after that.
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    if (layerStyle == null) {
                                        generateAutomaticLayerStyle();
                                        cmbLayerStyle.setSelectedIndex(1);
                                    }
                                }
                            });
                    } else if (!layers.contains(secondOperand)) {
                        SlidableWMSServiceLayerGroup secondLayer = null;
                        for (final SlidableWMSServiceLayerGroup layerCandidate : layers) {
                            if (layerCandidate.equals(firstOperand)) {
                                secondLayer = layerCandidate;
                                break;
                            }
                        }

                        setOperands(null, secondLayer);
                    } else if (!layers.contains(firstOperand)) {
                        SlidableWMSServiceLayerGroup firstLayer = null;
                        for (final SlidableWMSServiceLayerGroup layerCandidate : layers) {
                            if (layerCandidate.equals(secondOperand)) {
                                firstLayer = layerCandidate;
                                break;
                            }
                        }

                        setOperands(firstLayer, null);
                    }

                    checkControls();
                    cmbComparisonMethod.setSelectedIndex(0);
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshFeature() {
        if (feature != null) {
            if ((firstOperand != null) && (firstOperand.getPNode() != null)) {
                firstOperand.getPNode().setVisible(true);
            }
            if ((secondOperand != null) && (secondOperand.getPNode() != null)) {
                secondOperand.getPNode().setVisible(true);
            }

            final Feature oldFeature = feature;
            feature = null;
            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(oldFeature);
        }

        if ((firstOperand == null) || (secondOperand == null) || (layerStyle == null) || (operation == null)) {
            sldTimestamp.setEnabled(false);
            return;
        }

        final double contrast = (sldContrastResult.getValue() == 10) ? 1D : (sldContrastResult.getValue() * .1D);

        final List<Image> result;
        if (Double.isNaN(contrast)) {
            result = gridComparator.compare(operation);
        } else {
            result = gridComparator.compare(operation, contrast);
        }

        final String nameOfFeature;
        switch (operation) {
            case SUBTRACTION: {
                nameOfFeature = NbBundle.getMessage(
                        GridComparisonWidget.class,
                        "GridComparisonWidget.refreshFeature().operation.subtraction",
                        GridComparisonLayerProvider.generateMenuRepresentation(firstOperand),
                        GridComparisonLayerProvider.generateMenuRepresentation(secondOperand));

                break;
            }
            case AVERAGE: {
                nameOfFeature = NbBundle.getMessage(
                        GridComparisonWidget.class,
                        "GridComparisonWidget.refreshFeature().operation.average",
                        GridComparisonLayerProvider.generateMenuRepresentation(firstOperand),
                        GridComparisonLayerProvider.generateMenuRepresentation(secondOperand));
                break;
            }
            default: {
                nameOfFeature = NbBundle.getMessage(
                        GridComparisonWidget.class,
                        "GridComparisonWidget.refreshFeature().operation.default",
                        GridComparisonLayerProvider.generateMenuRepresentation(firstOperand),
                        GridComparisonLayerProvider.generateMenuRepresentation(secondOperand));
            }
        }

        if ((result != null) && !result.isEmpty()) {
            final Geometry geometry = ((SlidableWMSServiceLayerGroup)firstOperand).getBoundingBox().getGeometry();

            feature = new SlidingImagesFeature(result, geometry);
            feature.setSliderPosition(sldTimestamp.getValue());
            feature.setName(nameOfFeature);

            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(feature);

            sldTimestamp.setEnabled(true);

            if ((firstOperand != null) && (firstOperand.getPNode() != null)) {
                firstOperand.getPNode().setVisible(false);
            }
            if ((secondOperand != null) && (secondOperand.getPNode() != null)) {
                secondOperand.getPNode().setVisible(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maximum  DOCUMENT ME!
     */
    private void reconfigureTimestampSlider(final int maximum) {
        sldTimestamp.setMinimum(0);
        sldTimestamp.setMaximum(maximum);
        sldTimestamp.setValue(0);
        sldTimestamp.setMinorTickSpacing(100);

        sldTimestamp.setPaintTicks(true);
        sldTimestamp.setPaintLabels(false);

        sldTimestamp.setSnapToTicks(true);
        sldTimestamp.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  firstOperand  DOCUMENT ME!
     */
    public void setFirstOperand(final SlidableWMSServiceLayerGroup firstOperand) {
        this.firstOperand = firstOperand;
        enableGridComparisonListener.setFirstOperand(this.firstOperand);

        disDistributions.removeDistribution(firstDistribution);

        if (this.firstOperand != null) {
            if (this.firstOperand.equals(this.secondOperand)) {
                setSecondOperand(null);
            }

            firstDistribution = extractDistribution(this.firstOperand);
            disDistributions.addDistribution(firstDistribution, Color.green);

            if (layerStyle != null) {
                this.firstOperand.setCustomSLD(layerStyle.getSLDForLayer());
                this.firstOperand.retrieve(true);
            } else {
                if (this.firstOperand.getCustomSLD() != null) {
                    this.firstOperand.setCustomSLD(null);
                    this.firstOperand.retrieve(true);
                }
            }
        } else {
            this.firstOperand = null;
            firstDistribution = null;

            if (cmbFirstOperand.getSelectedItem() != null) {
                cmbFirstOperand.setSelectedItem(null);
            }

            cmbLayerStyle.setSelectedIndex(0);
        }

        checkControls();
        initGridComparator();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  secondOperand  DOCUMENT ME!
     */
    public void setSecondOperand(final SlidableWMSServiceLayerGroup secondOperand) {
        this.secondOperand = secondOperand;
        enableGridComparisonListener.setSecondOperand(this.secondOperand);

        disDistributions.removeDistribution(secondDistribution);

        if (this.secondOperand != null) {
            if (this.secondOperand.equals(this.firstOperand)) {
                setFirstOperand(null);
            }

            secondDistribution = extractDistribution(this.secondOperand);
            disDistributions.addDistribution(secondDistribution, Color.blue);

            if (layerStyle != null) {
                this.secondOperand.setCustomSLD(layerStyle.getSLDForLayer());
                this.secondOperand.retrieve(true);
            } else {
                if (this.secondOperand.getCustomSLD() != null) {
                    this.secondOperand.setCustomSLD(null);
                    this.secondOperand.retrieve(true);
                }
            }
        } else {
            this.secondOperand = null;
            secondDistribution = null;

            if (cmbSecondOperand.getSelectedItem() != null) {
                cmbSecondOperand.setSelectedItem(null);
            }

            cmbLayerStyle.setSelectedIndex(0);
        }

        checkControls();
        initGridComparator();
    }

    /**
     * DOCUMENT ME!
     */
    private void initGridComparator() {
        cmbComparisonMethod.setSelectedIndex(0);
        sldTimestamp.setEnabled(false);

        if ((firstOperand != null) && (secondOperand != null) && (layerStyle != null)) {
            double cropX;
            double cropY;
            double cropWidth;
            double cropHeight;

            try {
                final BoundingBox mapBoundingBox = CismapBroker.getInstance()
                            .getMappingComponent()
                            .getCurrentBoundingBox();
                final int mapWidth = CismapBroker.getInstance().getMappingComponent().getWidth();
                final int mapHeight = CismapBroker.getInstance().getMappingComponent().getHeight();
                final double bbWidth = mapBoundingBox.getWidth();
                final double bbHeight = mapBoundingBox.getHeight();
                final double scaleWidth = mapWidth / bbWidth;
                final double scaleHeight = mapHeight / bbHeight;

                final XBoundingBox layerBoundingBox = GridComparisonWidget.this.firstOperand.getBoundingBox();

                cropX = (layerBoundingBox.getX1() - mapBoundingBox.getX1()) * scaleWidth;
                cropY = (mapBoundingBox.getY2() - layerBoundingBox.getY2()) * scaleHeight;
                cropWidth = layerBoundingBox.getWidth() * scaleWidth;
                cropHeight = layerBoundingBox.getHeight() * scaleHeight;
            } catch (final Exception e) {
                LOG.fatal("Error while calculating crop area.", e);
                cropX = Double.NaN;
                cropY = Double.NaN;
                cropWidth = Double.NaN;
                cropHeight = Double.NaN;
            }

            gridComparator = new GridComparator(
                    GridComparisonWidget.this.firstOperand,
                    GridComparisonWidget.this.secondOperand,
                    layerStyle,
                    (float)cropX,
                    (float)cropY,
                    (float)cropWidth,
                    (float)cropHeight);
            reconfigureTimestampSlider((GridComparisonWidget.this.firstOperand.getLayers().size() - 1) * 100);
        } else {
            gridComparator = null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  firstOperand   DOCUMENT ME!
     * @param  secondOperand  DOCUMENT ME!
     */
    public void setOperands(final SlidableWMSServiceLayerGroup firstOperand,
            final SlidableWMSServiceLayerGroup secondOperand) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (firstOperand != null) {
                        if (!firstOperand.equals(cmbFirstOperand.getSelectedItem())) {
                            cmbFirstOperand.setSelectedItem(firstOperand);
                        }
                    }

                    if (secondOperand != null) {
                        if (!secondOperand.equals(cmbSecondOperand.getSelectedItem())) {
                            cmbSecondOperand.setSelectedItem(secondOperand);
                        }
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param   layer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Distribution extractDistribution(final SlidableWMSServiceLayerGroup layer) {
        if (layer == null) {
            return new Distribution(Double.NaN, Double.NaN, Double.NaN);
        }

        for (final WMSServiceLayer wMSServiceLayer : layer.getLayers()) {
            if (wMSServiceLayer == null) {
                continue;
            }

            final Layer layerInformation = wMSServiceLayer.getLayerInformation();
            if (layerInformation == null) {
                continue;
            }

            final String[] keywords = layerInformation.getKeywords();
            double min = Double.NaN;
            double max = Double.NaN;
            double mean = Double.NaN;
            for (final String keyword : keywords) {
                try {
                    if (keyword.startsWith("min:")) {
                        min = Double.parseDouble(keyword.substring(keyword.lastIndexOf(':') + 1));
                    } else if (keyword.startsWith("max:")) {
                        max = Double.parseDouble(keyword.substring(keyword.lastIndexOf(':') + 1));
                    } else if (keyword.startsWith("mean:")) {
                        mean = Double.parseDouble(keyword.substring(keyword.lastIndexOf(':') + 1));
                    }
                } catch (final Exception ex) {
                }
            }

            if (!Double.isNaN(min) && !Double.isNaN(max) && !Double.isNaN(mean)) {
                return new Distribution(min, max, mean);
            }
        }

        return new Distribution(Double.NaN, Double.NaN, Double.NaN);
    }

    /**
     * DOCUMENT ME!
     */
    private void checkControls() {
        cmbFirstOperand.setEnabled(cmbFirstOperand.getItemCount() > 0);
        cmbSecondOperand.setEnabled(cmbSecondOperand.getItemCount() > 0);
        cmbLayerStyle.setEnabled((firstOperand != null) && (secondOperand != null));
        cmbComparisonMethod.setEnabled(enableGridComparisonListener.isFirstOperandReady()
                    && enableGridComparisonListener.isSecondOperandReady()
                    && (layerStyle != null));
    }

    /**
     * DOCUMENT ME!
     */
    private void generateAutomaticLayerStyle() {
        if ((firstOperand != null) && (secondOperand != null)) {
            final Distribution firstDistribution = extractDistribution(firstOperand);
            final Distribution secondDistribution = extractDistribution(secondOperand);

            if ((firstDistribution != null) && (firstDistribution.getMax() != Double.NaN)
                        && (firstDistribution.getMin() != Double.NaN)
                        && (secondDistribution != null)
                        && (secondDistribution.getMax() != Double.NaN)
                        && (secondDistribution.getMin() != Double.NaN)) {
                final List<Entry> colorMap = new LinkedList<Entry>();
                colorMap.add(new Entry(Math.min(firstDistribution.getMin(), secondDistribution.getMin()), Color.green));
                colorMap.add(new Entry(Math.max(firstDistribution.getMax(), secondDistribution.getMax()), Color.red));

                cmbLayerStyle.removeItemAt(1);
                cmbLayerStyle.insertItemAt(new LayerStyle(
                        NbBundle.getMessage(
                            LayerStyleCellRenderer.class,
                            "GridComparisonWidget.cmbLayerStyle.automaticStyle"),
                        colorMap),
                    1);
            }
        }
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
        if (feature != null) {
            cmbComparisonMethod.setSelectedIndex(0);
        }
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        if ((feature == null) || (fce == null) || (fce.getEventFeatures() == null)) {
            return;
        }

        for (final Feature featureFromEvent : fce.getEventFeatures()) {
            if (feature.equals(featureFromEvent)) {
                cmbComparisonMethod.setSelectedIndex(0);
                break;
            }
        }
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featureCollectionChanged() {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class SlidableWMSServiceLayerGroupCellRenderer extends JLabel implements ListCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SlidableWMSServiceLayerGroupCellRenderer object.
         */
        public SlidableWMSServiceLayerGroupCellRenderer() {
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

            if (value instanceof SlidableWMSServiceLayerGroup) {
                final SlidableWMSServiceLayerGroup layer = (SlidableWMSServiceLayerGroup)value;
                setText(GridComparisonLayerProvider.generateMenuRepresentation(layer));

                if (!layer.isEnabled()) {
                    setBackground(UIManager.getDefaults().getColor("ComboBox.disabledBackground")); // NOI18N
                    setForeground(UIManager.getDefaults().getColor("ComboBox.disabledForeground")); // NOI18N
                }
            } else {
                if (value == null) {
                    setText(NbBundle.getMessage(
                            SlidableWMSServiceLayerGroupCellRenderer.class,
                            "GridComparisonWidget.SlidableWMSServiceLayerGroupCellRenderer.nullValue"));
                } else {
                    LOG.info(
                        "Should render an object which is not of type SlidableWMSServiceLayerGroup. There must be something wrong with the combo box this renderer is attached to.");
                }
            }

            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class LayerStyleCellRenderer extends JLabel implements ListCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SlidableWMSServiceLayerGroupCellRenderer object.
         */
        public LayerStyleCellRenderer() {
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

            if (value instanceof LayerStyle) {
                final LayerStyle layerStyle = (LayerStyle)value;
                setText(layerStyle.getName());
            } else {
                if (value instanceof String) {
                    setText((String)value);
                } else {
                    LOG.info("Should render an object which is not of type LayerStyle.");
                }
            }

            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class LayerStylesChangeListener implements PropertyChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (!LayerStyles.PROPERTY_LAYERSTYLES.equalsIgnoreCase(evt.getPropertyName())
                        || !(evt.getNewValue() instanceof List)) {
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("The layer styles were changed. Updating the combo box.");
            }

            final List newValue = (List)evt.getNewValue();
            LayerStyle automaticStyle = null;
            if (cmbLayerStyle.getItemCount() > 1) {
                final Object automaticStyleCandidate = cmbLayerStyle.getItemAt(1);

                if (!(automaticStyleCandidate instanceof LayerStyle)
                            || !(((LayerStyle)automaticStyleCandidate).getName().equals(
                                    NbBundle.getMessage(
                                        LayerStyleCellRenderer.class,
                                        "GridComparisonWidget.cmbLayerStyle.automaticStyle")))) {
                    automaticStyle = null;
                }
            }

            cmbLayerStyle.removeAllItems();
            cmbLayerStyle.addItem(NbBundle.getMessage(
                    LayerStyleCellRenderer.class,
                    "GridComparisonWidget.cmbLayerStyle.selectAStyle"));
            if (automaticStyle != null) {
                cmbLayerStyle.addItem(automaticStyle);
            }

            for (final Object layerStyleObj : newValue) {
                if (!(layerStyleObj instanceof LayerStyle)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("An invalid object was announced: '" + layerStyleObj + "'.");
                    }
                    continue;
                }

                final LayerStyle layerStyle = (LayerStyle)layerStyleObj;
                final List<Entry> colorMap = layerStyle.getColorMap();

                if ((colorMap == null) || (colorMap.size() != 2)
                            || !GridComparator.areIndependant(colorMap.get(0).getColor(), colorMap.get(1).getColor())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Layer style '" + layerStyle.getName()
                                    + "' has an invalid count of colors or they aren't independant.");
                    }
                    continue;
                }

                cmbLayerStyle.addItem((LayerStyle)layerStyleObj);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class EnableGridComparisonRetrievalListener implements RetrievalListener {

        //~ Instance fields ----------------------------------------------------

        private SlidableWMSServiceLayerGroup firstOperand;
        private SlidableWMSServiceLayerGroup secondOperand;
        private boolean firstOperandReady;
        private boolean secondOperandReady;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  firstOperand  DOCUMENT ME!
         */
        public void setFirstOperand(final SlidableWMSServiceLayerGroup firstOperand) {
            if ((this.firstOperand != null) && !this.firstOperand.equals(secondOperand)) {
                this.firstOperand.removeRetrievalListener(this);
            }

            this.firstOperand = firstOperand;

            if (this.firstOperand != null) {
                firstOperandReady = this.firstOperand.getProgress() == 100;
                this.firstOperand.addRetrievalListener(this);
            } else {
                firstOperandReady = false;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  secondOperand  DOCUMENT ME!
         */
        public void setSecondOperand(final SlidableWMSServiceLayerGroup secondOperand) {
            if ((this.secondOperand != null) && !this.secondOperand.equals(firstOperand)) {
                this.secondOperand.removeRetrievalListener(this);
            }

            this.secondOperand = secondOperand;

            if (this.secondOperand != null) {
                secondOperandReady = this.secondOperand.getProgress() == 100;
                this.secondOperand.addRetrievalListener(this);
            } else {
                secondOperandReady = false;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isFirstOperandReady() {
            return firstOperandReady;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isSecondOperandReady() {
            return secondOperandReady;
        }

        @Override
        public void retrievalStarted(final RetrievalEvent e) {
            if ((e == null) || (e.getRetrievalService() == null)) {
                return;
            }

            if (e.getRetrievalService().equals(firstOperand)) {
                firstOperandReady = false;
            } else if (e.getRetrievalService().equals(secondOperand)) {
                secondOperandReady = false;
            }

            checkControls();
        }

        @Override
        public void retrievalProgress(final RetrievalEvent e) {
            if ((e == null) || (e.getRetrievalService() == null)) {
                return;
            }

            if (e.getRetrievalService().equals(firstOperand)) {
                firstOperandReady = e.getPercentageDone() == 100;
            } else if (e.getRetrievalService().equals(secondOperand)) {
                secondOperandReady = e.getPercentageDone() == 100;
            }

            checkControls();
        }

        @Override
        public void retrievalComplete(final RetrievalEvent e) {
            if ((e == null) || (e.getRetrievalService() == null)) {
                return;
            }

            if (e.getRetrievalService().equals(firstOperand)) {
                firstOperandReady = true;
            } else if (e.getRetrievalService().equals(secondOperand)) {
                secondOperandReady = true;
            }

            checkControls();
        }

        @Override
        public void retrievalAborted(final RetrievalEvent e) {
            if ((e == null) || (e.getRetrievalService() == null)) {
                return;
            }

            if (e.getRetrievalService().equals(firstOperand)) {
                firstOperandReady = false;
            } else if (e.getRetrievalService().equals(secondOperand)) {
                secondOperandReady = false;
            }

            checkControls();
        }

        @Override
        public void retrievalError(final RetrievalEvent e) {
            if ((e == null) || (e.getRetrievalService() == null)) {
                return;
            }

            if (e.getRetrievalService().equals(firstOperand)) {
                firstOperandReady = false;
            } else if (e.getRetrievalService().equals(secondOperand)) {
                secondOperandReady = false;
            }

            checkControls();
        }
    }
}
