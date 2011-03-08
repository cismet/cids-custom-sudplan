/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import Sirius.navigator.ui.ComponentRegistry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class AirqualityDownscalingInputManagerUI extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient AirqualityDownscalingInput model;

    private final transient ActionListener showBBoxL;

    private transient PureNewFeature bboxFeature;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnShowBBox = new javax.swing.JButton();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JLabel lblCreated = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCreatedBy = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCreatedByValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCreatedValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDatabases = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblEndDate = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblEndDateValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblGridSize = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblGridSizeValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblLLCoord = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblLLCoordValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblNameValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblScenario = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblScenarioValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblStartDate = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblStartDateValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblURCoord = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblURCoordValue = new javax.swing.JLabel();
    private final transient javax.swing.JList lstDatabases = new javax.swing.JList();
    private final transient javax.swing.JPanel pnlFiller = new javax.swing.JPanel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingInputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingInputManagerUI(final AirqualityDownscalingInput model) {
        this.model = model;
        this.showBBoxL = new ShowBBoxActionListener();

        initComponents();

        init();

        btnShowBBox.addActionListener(WeakListeners.create(ActionListener.class, showBBoxL, btnShowBBox));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        lblNameValue.setText(model.getName());
        lblCreatedValue.setText(model.getCreated().toString());
        lblCreatedByValue.setText(model.getCreatedBy());
        lblScenarioValue.setText(model.getScenario());
        lblStartDateValue.setText(model.getStartDate().toString());
        lblEndDateValue.setText(model.getEndDate().toString());
        lblLLCoordValue.setText(model.getLlCoord().toString());
        lblURCoordValue.setText(model.getUrCoord().toString());
        lblGridSizeValue.setText(String.valueOf(model.getGridSize()) + " meters");

        final DefaultListModel dlm = (DefaultListModel)lstDatabases.getModel();
        for (final String db : model.getDatabases().keySet()) {
            for (final Integer year : model.getDatabases().get(db)) {
                dlm.addElement(db + " -> " + year); // NOI18N
            }
        }

        final GeometryFactory factory = new GeometryFactory();

        final Coordinate ll = model.getLlCoord();
        final Coordinate ur = model.getUrCoord();
        final Coordinate[] bbox = new Coordinate[5];
        bbox[0] = ll;
        bbox[1] = new Coordinate(ll.x, ur.y);
        bbox[2] = ur;
        bbox[3] = new Coordinate(ur.x, ll.y);
        bbox[4] = ll;
        final LinearRing ring = new LinearRing(new CoordinateArraySequence(bbox), factory);
        final Geometry geometry = factory.createPolygon(ring, new LinearRing[0]);
        bboxFeature = new PureNewFeature(geometry);
        bboxFeature.setName(model.getName()); // NOI18N
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

        lblName.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblName, gridBagConstraints);

        lblCreated.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreated.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblCreated, gridBagConstraints);

        lblCreatedValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreatedValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblCreatedValue, gridBagConstraints);

        lblNameValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblNameValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblNameValue, gridBagConstraints);

        lblCreatedBy.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreatedBy.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblCreatedBy, gridBagConstraints);

        lblCreatedByValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreatedByValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblCreatedByValue, gridBagConstraints);

        lblScenario.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblScenario.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblScenario, gridBagConstraints);

        lblScenarioValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblScenarioValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblScenarioValue, gridBagConstraints);

        lblStartDate.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblStartDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblStartDate, gridBagConstraints);

        lblStartDateValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblStartDateValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblStartDateValue, gridBagConstraints);

        lblEndDate.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblEndDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblEndDate, gridBagConstraints);

        lblEndDateValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblEndDateValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblEndDateValue, gridBagConstraints);

        lblLLCoord.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblLLCoord.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblLLCoord, gridBagConstraints);

        lblURCoord.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblURCoord.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblURCoord, gridBagConstraints);

        lblLLCoordValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblLLCoordValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblLLCoordValue, gridBagConstraints);

        lblURCoordValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblURCoordValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblURCoordValue, gridBagConstraints);

        lblGridSize.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblGridSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblGridSize, gridBagConstraints);

        lblGridSizeValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblGridSizeValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblGridSizeValue, gridBagConstraints);

        lblDatabases.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblDatabases.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(lblDatabases, gridBagConstraints);

        lstDatabases.setModel(new DefaultListModel());
        lstDatabases.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstDatabases.setFocusable(false);
        lstDatabases.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(lstDatabases);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jScrollPane1, gridBagConstraints);

        pnlFiller.setOpaque(false);

        final org.jdesktop.layout.GroupLayout pnlFillerLayout = new org.jdesktop.layout.GroupLayout(pnlFiller);
        pnlFiller.setLayout(pnlFillerLayout);
        pnlFillerLayout.setHorizontalGroup(
            pnlFillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 18, Short.MAX_VALUE));
        pnlFillerLayout.setVerticalGroup(
            pnlFillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 292, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(pnlFiller, gridBagConstraints);

        btnShowBBox.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.btnShowBBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(btnShowBBox, gridBagConstraints);
    }                                                                     // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ShowBBoxActionListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            // collection will not add the feature if it is already present
            mc.getFeatureCollection().addFeature(bboxFeature);
            SMSUtils.showMappingComponent();
        }
    }
}
