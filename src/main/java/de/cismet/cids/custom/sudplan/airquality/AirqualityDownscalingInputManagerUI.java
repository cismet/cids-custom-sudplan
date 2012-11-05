/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cismap.commons.features.Feature;
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

    private final transient ActionListener showBoundingBox;

    private transient Feature grid;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnShowBoundingBox = new javax.swing.JButton();
    private final transient javax.swing.JLabel lblCreated = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCreatedBy = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCreatedByValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCreatedValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDatabase = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDatabaseValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDescriptionValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblEndDate = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblEndDateValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblGridcellSize = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblGridcellSizeValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblLowerleft = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblLowerleftValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblNameValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblScenario = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblScenarioValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSrs = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSrsValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblStartDate = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblStartDateValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblUpperright = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblUpperrightValue = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingInputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingInputManagerUI(final AirqualityDownscalingInput model) {
        this.model = model;
        this.showBoundingBox = new ShowBoundingBox();

        initComponents();

        init();

        btnShowBoundingBox.addActionListener(WeakListeners.create(
                ActionListener.class,
                showBoundingBox,
                btnShowBoundingBox));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        lblNameValue.setText(model.getName());
        lblCreatedValue.setText(model.getCreated().toString());
        lblCreatedByValue.setText(model.getCreatedBy());
        lblLowerleftValue.setText(model.getLowerleft().toString());
        lblUpperrightValue.setText(model.getUpperright().toString());
        lblGridcellSizeValue.setText(String.valueOf(model.getGridcellSize())
                    + NbBundle.getMessage(
                        AirqualityDownscalingInputManagerUI.class,
                        "AirqualityDownscalingInputManagerUI.lblGridSizeValue.text.appendix")); // NOI18N
        lblScenarioValue.setText(model.getScenario());
        lblStartDateValue.setText(model.getStartDate().toString());
        lblEndDateValue.setText(model.getEndDate().toString());
        lblDatabaseValue.setText(model.getDatabase());
        lblDescriptionValue.setText(model.getDescription());
        lblSrsValue.setText(model.getSrs());

        grid = new GridFeature(model.getGridcellCountX(),
                model.getGridcellCountY(),
                model.getGridcellSize(),
                model.getLowerleft(),
                model.getUpperright(),
                Integer.parseInt(model.getSrs().substring(5)));
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

        lblName.setLabelFor(lblNameValue);
        lblName.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblName, gridBagConstraints);

        lblCreated.setLabelFor(lblCreatedValue);
        lblCreated.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreated.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblCreated, gridBagConstraints);

        lblCreatedValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreatedValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblCreatedValue, gridBagConstraints);

        lblNameValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblNameValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblNameValue, gridBagConstraints);

        lblCreatedBy.setLabelFor(lblCreatedByValue);
        lblCreatedBy.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreatedBy.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblCreatedBy, gridBagConstraints);

        lblCreatedByValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblCreatedByValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblCreatedByValue, gridBagConstraints);

        lblScenario.setLabelFor(lblScenarioValue);
        lblScenario.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblScenario.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblScenario, gridBagConstraints);

        lblScenarioValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblScenarioValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblScenarioValue, gridBagConstraints);

        lblStartDate.setLabelFor(lblStartDateValue);
        lblStartDate.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblStartDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblStartDate, gridBagConstraints);

        lblStartDateValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblStartDateValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblStartDateValue, gridBagConstraints);

        lblEndDate.setLabelFor(lblEndDateValue);
        lblEndDate.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblEndDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblEndDate, gridBagConstraints);

        lblEndDateValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblEndDateValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblEndDateValue, gridBagConstraints);

        lblLowerleft.setLabelFor(lblLowerleftValue);
        lblLowerleft.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblLowerleft.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblLowerleft, gridBagConstraints);

        lblUpperright.setLabelFor(lblUpperrightValue);
        lblUpperright.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblUpperright.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUpperright, gridBagConstraints);

        lblLowerleftValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblLowerleftValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblLowerleftValue, gridBagConstraints);

        lblUpperrightValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblUpperrightValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUpperrightValue, gridBagConstraints);

        lblGridcellSize.setLabelFor(lblGridcellSizeValue);
        lblGridcellSize.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblGridcellSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblGridcellSize, gridBagConstraints);

        lblGridcellSizeValue.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblGridcellSizeValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblGridcellSizeValue, gridBagConstraints);

        lblDatabase.setLabelFor(lblDatabaseValue);
        lblDatabase.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblDatabase.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDatabase, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDatabaseValue, gridBagConstraints);

        lblDescription.setLabelFor(lblDescriptionValue);
        lblDescription.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblDescription.text")); // NOI18N
        lblDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDescription, gridBagConstraints);

        lblDescriptionValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDescriptionValue, gridBagConstraints);

        btnShowBoundingBox.setText(NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.btnShowBoundingBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btnShowBoundingBox, gridBagConstraints);

        lblSrs.setLabelFor(lblSrsValue);
        lblSrs.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingInputManagerUI.class,
                "AirqualityDownscalingInputManagerUI.lblSrs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblSrs, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblSrsValue, gridBagConstraints);
    }                                                                // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ShowBoundingBox implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();

            // TODO: collection will not add the feature if it is already present
            mc.getFeatureCollection().addFeature(grid);
            SMSUtils.showMappingComponent();
        }
    }
}
