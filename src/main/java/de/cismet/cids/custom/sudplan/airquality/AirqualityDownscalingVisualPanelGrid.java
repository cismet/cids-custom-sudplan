/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import com.vividsolutions.jts.geom.Coordinate;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;

import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingVisualPanelGrid extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingVisualPanelGrid.class);

    //~ Instance fields --------------------------------------------------------

    private final transient AirqualityDownscalingWizardPanelGrid model;
    private final transient DocumentListener gridcellSizeListener;
    private final transient ChangeListener gridCornerXListener;
    private final transient ChangeListener gridCornerYListener;
    private final transient ChangeListener gridcellCountXListener;
    private final transient ChangeListener gridcellCountYListener;
    private final transient MappingComponent mappingComponent;
    private final transient GridFeature gridFeature;
    private transient UpdateFeatureInMapWorker updateFeatureInMapWorker;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler gluFillerVertical;
    private javax.swing.Box.Filler gluHorizontalFiller;
    private javax.swing.JLabel lblGridcellCount;
    private javax.swing.JLabel lblGridcellSize;
    private javax.swing.JLabel lblLowerleft;
    private javax.swing.JLabel lblMeters;
    private javax.swing.JLabel lblUpperright;
    private javax.swing.JLabel lblUpperrightX;
    private javax.swing.JLabel lblUpperrightY;
    private javax.swing.JLabel lblX;
    private javax.swing.JLabel lblY;
    private javax.swing.JSpinner spnGridcellCountX;
    private javax.swing.JSpinner spnGridcellCountY;
    private javax.swing.JSpinner spnLowerleftX;
    private javax.swing.JSpinner spnLowerleftY;
    private javax.swing.JTextField txtGridcellSize;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingVisualPanelGridSize.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingVisualPanelGrid(final AirqualityDownscalingWizardPanelGrid model) {
        this.model = model;
        gridcellSizeListener = new GridcellSizeListener();
        gridCornerXListener = new GridCornerListener(true);
        gridCornerYListener = new GridCornerListener(false);
        gridcellCountXListener = new GridcellCountListener(true);
        gridcellCountYListener = new GridcellCountListener(false);

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.this.name")); // NOI18N

        initComponents();

        ((SpinnerNumberModel)spnLowerleftX.getModel()).setMaximum(Double.MAX_VALUE);
        ((SpinnerNumberModel)spnLowerleftY.getModel()).setMaximum(Double.MAX_VALUE);

        txtGridcellSize.getDocument()
                .addDocumentListener(WeakListeners.document(gridcellSizeListener, txtGridcellSize.getDocument()));

        spnLowerleftX.addChangeListener(WeakListeners.change(gridCornerXListener, spnLowerleftX));
        spnLowerleftY.addChangeListener(WeakListeners.change(gridCornerYListener, spnLowerleftY));
        spnGridcellCountX.addChangeListener(WeakListeners.change(gridcellCountXListener, spnGridcellCountX));
        spnGridcellCountY.addChangeListener(WeakListeners.change(gridcellCountYListener, spnGridcellCountY));

//        ((AbstractDocument)txtGridcellSize.getDocument()).setDocumentFilter(new GridcellSizeFilter());

        if ((CismapBroker.getInstance() != null) && (CismapBroker.getInstance().getMappingComponent() != null)) {
            mappingComponent = CismapBroker.getInstance().getMappingComponent();
        } else {
            mappingComponent = null;
        }

        gridFeature = new GridFeature(model.getGridcellCountX(),
                model.getGridcellCountY(),
                model.getGridcellSize(),
                model.getLowerleft(),
                model.getUpperright());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        final double lowerleftX = model.getLowerleft().x;
        final double lowerleftY = model.getLowerleft().y;
        final double upperrightX = model.getUpperright().x;
        final double upperrightY = model.getUpperright().y;
        final Integer gridcellSize = model.getGridcellSize();
        final Long gridcellCountX = model.getGridcellCountX();
        final Long gridcellCountY = model.getGridcellCountY();

        spnLowerleftX.setValue(lowerleftX);
        spnLowerleftY.setValue(lowerleftY);
        lblUpperrightX.setText(String.valueOf(upperrightX));
        lblUpperrightY.setText(String.valueOf(upperrightY));
        spnGridcellCountX.setValue(gridcellCountX);
        spnGridcellCountY.setValue(gridcellCountY);

        if (gridcellSize == null) {
            txtGridcellSize.setText("1000"); // NOI18N
            model.setGridcellSize(Integer.valueOf(1000));
        } else {
            txtGridcellSize.setText(gridcellSize.toString());
        }

        updateFeatureInMap();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblGridcellSize = new javax.swing.JLabel();
        lblMeters = new javax.swing.JLabel();
        txtGridcellSize = new javax.swing.JTextField();
        lblX = new javax.swing.JLabel();
        lblY = new javax.swing.JLabel();
        lblLowerleft = new javax.swing.JLabel();
        lblUpperright = new javax.swing.JLabel();
        lblUpperrightX = new javax.swing.JLabel();
        lblUpperrightY = new javax.swing.JLabel();
        lblGridcellCount = new javax.swing.JLabel();
        spnGridcellCountX = new javax.swing.JSpinner();
        spnGridcellCountY = new javax.swing.JSpinner();
        gluFillerVertical = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        spnLowerleftX = new javax.swing.JSpinner();
        spnLowerleftY = new javax.swing.JSpinner();
        gluHorizontalFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        setMinimumSize(new java.awt.Dimension(200, 150));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(450, 300));
        setLayout(new java.awt.GridBagLayout());

        lblGridcellSize.setLabelFor(txtGridcellSize);
        lblGridcellSize.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblGridcellSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblGridcellSize, gridBagConstraints);

        lblMeters.setLabelFor(txtGridcellSize);
        lblMeters.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblMeters.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(lblMeters, gridBagConstraints);

        txtGridcellSize.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGridcellSize.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.txtGridcellSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtGridcellSize, gridBagConstraints);

        lblX.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblX.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblX, gridBagConstraints);

        lblY.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblY.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblY, gridBagConstraints);

        lblLowerleft.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblLowerleft.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblLowerleft, gridBagConstraints);

        lblUpperright.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblUpperright.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUpperright, gridBagConstraints);

        lblUpperrightX.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblUpperrightX.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUpperrightX, gridBagConstraints);

        lblUpperrightY.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblUpperrightY.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUpperrightY, gridBagConstraints);

        lblGridcellCount.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelGrid.class,
                "AirqualityDownscalingVisualPanelGrid.lblGridcellCount.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblGridcellCount, gridBagConstraints);

        spnGridcellCountX.setModel(new javax.swing.SpinnerNumberModel(
                Long.valueOf(1L),
                Long.valueOf(1L),
                Long.valueOf(100L),
                Long.valueOf(1L)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spnGridcellCountX, gridBagConstraints);

        spnGridcellCountY.setModel(new javax.swing.SpinnerNumberModel(
                Long.valueOf(1L),
                Long.valueOf(1L),
                Long.valueOf(100L),
                Long.valueOf(1L)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spnGridcellCountY, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        add(gluFillerVertical, gridBagConstraints);

        spnLowerleftX.setModel(new javax.swing.SpinnerNumberModel(
                Double.valueOf(0.0d),
                null,
                null,
                Double.valueOf(100.0d)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spnLowerleftX, gridBagConstraints);

        spnLowerleftY.setModel(new javax.swing.SpinnerNumberModel(
                Double.valueOf(0.0d),
                null,
                null,
                Double.valueOf(100.0d)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spnLowerleftY, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        add(gluHorizontalFiller, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param   x  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double probeForLowerleft(final boolean x) {
        try {
            if (x) {
                return Double.valueOf(spnLowerleftX.getValue().toString()).doubleValue();
            } else {
                return Double.valueOf(spnLowerleftY.getValue().toString()).doubleValue();
            }
        } catch (NumberFormatException ex) {
            return 0D;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateFeatureInMap() {
        if ((updateFeatureInMapWorker == null) || updateFeatureInMapWorker.isDone()) {
            updateFeatureInMapWorker = new UpdateFeatureInMapWorker();
        } else {
            updateFeatureInMapWorker.cancel(true);
        }

        SudplanConcurrency.getSudplanGeneralPurposePool().submit(updateFeatureInMapWorker);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class GridCornerListener implements ChangeListener {

        //~ Instance fields ----------------------------------------------------

        private boolean x;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GridCornerListener object.
         *
         * @param  x  DOCUMENT ME!
         */
        public GridCornerListener(final boolean x) {
            this.x = x;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void stateChanged(final ChangeEvent event) {
            final double newLowerleft = probeForLowerleft(x);
            final Coordinate oldUpperrightCoordinate = model.getUpperright();
            final Coordinate oldLowerleftCoordinate = model.getLowerleft();
            final double oldLowerleft;
            final double oldUpperright;

            if (x) {
                oldLowerleft = oldLowerleftCoordinate.x;
                oldUpperright = oldUpperrightCoordinate.x;
            } else {
                oldLowerleft = oldLowerleftCoordinate.y;
                oldUpperright = oldUpperrightCoordinate.y;
            }

            final double distance = oldUpperright - oldLowerleft;
            final double newUpperright = newLowerleft + distance;

            if (x) {
                lblUpperrightX.setText(Double.toString(newUpperright));

                model.setLowerleft(new Coordinate(newLowerleft, oldLowerleftCoordinate.y));
                model.setUpperright(new Coordinate(newUpperright, oldUpperrightCoordinate.y));
            } else {
                lblUpperrightY.setText(Double.toString(newUpperright));

                model.setLowerleft(new Coordinate(oldLowerleftCoordinate.x, newLowerleft));
                model.setUpperright(new Coordinate(oldUpperrightCoordinate.x, newUpperright));
            }

            repaint();

            updateFeatureInMap();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class GridcellSizeListener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void changedUpdate(final DocumentEvent e) {
            updateModelAndUI();
        }

        @Override
        public void insertUpdate(final DocumentEvent e) {
            updateModelAndUI();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            updateModelAndUI();
        }

        /**
         * DOCUMENT ME!
         */
        private void updateModelAndUI() {
            final Runnable calculateNewGrid = new Runnable() {

                    @Override
                    public void run() {
                        Integer gridcellSizeTest;
                        try {
                            gridcellSizeTest = Integer.valueOf(txtGridcellSize.getText());
                        } catch (NumberFormatException ex) {
                            gridcellSizeTest = null;
                        }

                        final Integer gridcellSize = gridcellSizeTest;
                        final Coordinate lowerleft;
                        final Coordinate upperright;
                        if (gridcellSize != null) {
                            lowerleft = model.getLowerleft();
                            upperright = new Coordinate(
                                    lowerleft.x
                                            + (model.getGridcellCountX() * gridcellSize),
                                    lowerleft.y
                                            + (model.getGridcellCountY() * gridcellSize));
                        } else {
                            lowerleft = null;
                            upperright = null;
                        }

                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    model.setGridcellSize(gridcellSize);
                                    model.setUpperright(upperright);

                                    if (upperright != null) {
                                        lblUpperrightX.setText(Double.toString(upperright.x));
                                        lblUpperrightY.setText(Double.toString(upperright.y));
                                    } else {
                                        lblUpperrightX.setText("-");
                                        lblUpperrightY.setText("-");
                                    }

                                    updateFeatureInMap();
                                }
                            });
                    }
                };

            if ((txtGridcellSize.getText() != null) && (txtGridcellSize.getText().trim().length() > 0)) {
                SudplanConcurrency.getSudplanGeneralPurposePool().submit(calculateNewGrid);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class GridcellCountListener implements ChangeListener {

        //~ Instance fields ----------------------------------------------------

        private final transient boolean x;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GridcellCountListener object.
         *
         * @param  x  DOCUMENT ME!
         */
        public GridcellCountListener(final boolean x) {
            this.x = x;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void stateChanged(final ChangeEvent e) {
            final long gridcellCount;

            if (x) {
                gridcellCount = ((Long)spnGridcellCountX.getValue());
            } else {
                gridcellCount = ((Long)spnGridcellCountY.getValue());
            }

            final Integer gridcellSize = model.getGridcellSize();
            final Coordinate lowerleft = model.getLowerleft();

            if (x) {
                final Coordinate upperright = new Coordinate(
                        lowerleft.x
                                + (gridcellCount * gridcellSize),
                        lowerleft.y
                                + (model.getGridcellCountY() * gridcellSize));
                model.setUpperright(upperright);
                model.setGridcellCountX(gridcellCount);

                lblUpperrightX.setText(Double.toString(model.getUpperright().x));
            } else {
                final Coordinate upperright = new Coordinate(
                        lowerleft.x
                                + (model.getGridcellCountX() * gridcellSize),
                        lowerleft.y
                                + (gridcellCount * gridcellSize));
                model.setUpperright(upperright);
                model.setGridcellCountY(gridcellCount);

                lblUpperrightY.setText(Double.toString(model.getUpperright().y));
            }

            updateFeatureInMap();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class UpdateFeatureInMapWorker extends SwingWorker<Void, Void> {

        //~ Methods ------------------------------------------------------------

        @Override
        protected Void doInBackground() throws Exception {
            gridFeature.setLowerleft(model.getLowerleft());
            gridFeature.setUpperright(model.getUpperright());
            gridFeature.setGridcellSize(model.getGridcellSize());
            gridFeature.setGridcellCountX(model.getGridcellCountX());
            gridFeature.setGridcellCountY(model.getGridcellCountY());

            gridFeature.calculateGeometry();

            return null;
        }

        @Override
        protected void done() {
            final FeatureCollection featureCollection = mappingComponent.getFeatureCollection();

            if (!isCancelled()) {
                try {
                    get();
                } catch (Exception ex) {
                    LOG.error("Couldn't recalculate grid feature. Trying to remove it from map.", ex); // NOI18N
                    featureCollection.removeFeature(gridFeature);
                    return;
                }
            }

            if (!isCancelled()) {
                if (gridFeature.getGeometry() == null) {
                    featureCollection.removeFeature(gridFeature);
                } else {
                    if (featureCollection.contains(gridFeature)) {
                        featureCollection.reconsiderFeature(gridFeature);
                    } else {
                        featureCollection.addFeature(gridFeature);
                    }
                }
            }
        }
    }
}
