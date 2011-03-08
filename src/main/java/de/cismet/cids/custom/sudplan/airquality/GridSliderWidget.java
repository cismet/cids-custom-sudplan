/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2011 mscholl
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * GridSliderWidget.java
 *
 * Created on Feb 21, 2011, 5:58:07 PM
 */
package de.cismet.cids.custom.sudplan.airquality;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Grid;
import de.cismet.cids.custom.sudplan.RunHelper;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.RasterDocumentFeature;
import de.cismet.cismap.commons.features.XStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class GridSliderWidget extends javax.swing.JInternalFrame implements RasterDocumentFeature,
    XStyledFeature,
    FeatureCollectionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridSliderWidget.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Map<Integer, Grid> grids;
    private final transient Geometry geom;
    private final transient Integer[] years;

    private transient boolean canBeSelected;

    private transient boolean editable;

    private transient boolean hiding;

    private final double min;
    private final double max;

    private final transient String name;

    private final transient Map<Grid, BufferedImage> imageCache;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    private final transient javax.swing.JSlider jslTime = new javax.swing.JSlider();
    private final transient javax.swing.JLabel lblAbsoluteMax = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblAbsoluteMaxValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblAbsoluteMin = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblAbsoluteMinValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblUnit = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblUnitValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblVariableValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lvlVariable = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GridSliderWidget.
     *
     * @param   name   DOCUMENT ME!
     * @param   grids  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public GridSliderWidget(final String name, final Map<Integer, Grid> grids) {
        if (grids == null) {
            throw new IllegalArgumentException("grids must not be null");         // NOI18N
        } else if (grids.isEmpty()) {
            throw new IllegalArgumentException("grids must not be empty");        // NOI18N
        } else if ((name == null) || name.isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty"); // NOI18N
        }

        this.grids = grids;
        this.name = name;
        this.imageCache = new HashMap<Grid, BufferedImage>((int)(grids.size() / 0.75) + 1);

        final Grid[] gridArray = grids.values().toArray(new Grid[grids.values().size()]);
        final double[] minMax = RunHelper.getMinMaxValue(gridArray);

        // we assume that every grid has the same geometry
        this.geom = gridArray[0].getGeometry();
        this.min = minMax[0];
        this.max = minMax[1];

        years = grids.keySet().toArray(new Integer[grids.keySet().size()]);
        Arrays.sort(years);

        initComponents();

        init();

        final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
        mc.getFeatureCollection().addFeatureCollectionListener(this);
        mc.getFeatureCollection().reconsiderFeature(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        jslTime.setMinimum(years[0]);
        jslTime.setMaximum(years[years.length - 1]);
        jslTime.setMajorTickSpacing(20);
        jslTime.setMinorTickSpacing(10);

        final Grid grid = grids.values().iterator().next();
        lblVariableValue.setText(grid.getDataType());
        lblUnitValue.setText(grid.getUnit());
        lblAbsoluteMinValue.setText("<html><font color=\"green\">" + (Math.round(min * 100) / 100d) + "</font></html>");
        lblAbsoluteMaxValue.setText("<html><font color=\"red\">" + (Math.round(max * 100) / 100d) + "</font></html>");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setMinimumSize(new java.awt.Dimension(300, 90));
        setPreferredSize(new java.awt.Dimension(300, 90));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jslTime.setPaintLabels(true);
        jslTime.setPaintTicks(true);
        jslTime.setSnapToTicks(true);
        jslTime.setMinimumSize(new java.awt.Dimension(290, 58));
        jslTime.setPreferredSize(new java.awt.Dimension(290, 58));
        jslTime.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jslTimeStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 4, 4);
        jPanel1.add(jslTime, gridBagConstraints);

        lvlVariable.setText(NbBundle.getMessage(GridSliderWidget.class, "GridSliderWidget.lvlVariable.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 1);
        jPanel1.add(lvlVariable, gridBagConstraints);

        lblVariableValue.setText(NbBundle.getMessage(GridSliderWidget.class, "GridSliderWidget.lblVariableValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 1, 4, 4);
        jPanel1.add(lblVariableValue, gridBagConstraints);

        lblUnit.setText(NbBundle.getMessage(GridSliderWidget.class, "GridSliderWidget.lblUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 1);
        jPanel1.add(lblUnit, gridBagConstraints);

        lblUnitValue.setText(NbBundle.getMessage(GridSliderWidget.class, "GridSliderWidget.lblUnitValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 1, 4, 4);
        jPanel1.add(lblUnitValue, gridBagConstraints);

        lblAbsoluteMin.setText(NbBundle.getMessage(GridSliderWidget.class, "GridSliderWidget.lblAbsoluteMin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 1);
        jPanel1.add(lblAbsoluteMin, gridBagConstraints);

        lblAbsoluteMinValue.setText(NbBundle.getMessage(
                GridSliderWidget.class,
                "GridSliderWidget.lblAbsoluteMinValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 1, 4, 4);
        jPanel1.add(lblAbsoluteMinValue, gridBagConstraints);

        lblAbsoluteMax.setText(NbBundle.getMessage(GridSliderWidget.class, "GridSliderWidget.lblAbsoluteMax.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 1);
        jPanel1.add(lblAbsoluteMax, gridBagConstraints);

        lblAbsoluteMaxValue.setText(NbBundle.getMessage(
                GridSliderWidget.class,
                "GridSliderWidget.lblAbsoluteMaxValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 1, 4, 4);
        jPanel1.add(lblAbsoluteMaxValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jslTimeStateChanged(final javax.swing.event.ChangeEvent evt) //GEN-FIRST:event_jslTimeStateChanged
    {                                                                         //GEN-HEADEREND:event_jslTimeStateChanged
        CismapBroker.getInstance().getMappingComponent().getFeatureCollection().reconsiderFeature(this);
    }                                                                         //GEN-LAST:event_jslTimeStateChanged

    @Override
    public BufferedImage getRasterDocument() {
        final Integer year = jslTime.getValue();

        Grid grid = grids.get(year);
        if (grid == null) {
            for (final Integer i : years) {
                if (year < i) {
                    grid = grids.get(i);
                    break;
                }
            }
        }

        assert grid != null : "grid cannot be null"; // NOI18N

        if (!imageCache.containsKey(grid)) {
            imageCache.put(grid, RunHelper.gridToImage(grid, 0, min, max));
        }

        return imageCache.get(grid);
    }

    @Override
    public Geometry getGeometry() {
        return geom.getEnvelope();
    }

    @Override
    public void setGeometry(final Geometry geom) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setGeometry called, but geometry is immutable: " + geom); // NOI18N
        }
    }

    @Override
    public boolean canBeSelected() {
        return canBeSelected;
    }

    @Override
    public void setCanBeSelected(final boolean canBeSelected) {
        this.canBeSelected = canBeSelected;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isHidden() {
        return hiding;
    }

    @Override
    public void hide(final boolean hiding) {
        this.hiding = hiding;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        // ignore
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
        CismapBroker.getInstance().getMappingComponent().removeInternalWidget(getName());
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        if (fce.getEventFeatures().contains(this)) {
            CismapBroker.getInstance().getMappingComponent().removeInternalWidget(getName());
        }
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        // ignore
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        CismapBroker.getInstance()
                .getMappingComponent()
                .showInternalWidget(getName(), fce.getFeatureCollection().getSelectedFeatures().contains(this), 3);
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
        // ignore
    }

    @Override
    public void featureCollectionChanged() {
        // ignore
    }

    @Override
    public ImageIcon getIconImage() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public Stroke getLineStyle() {
        return null;
    }

    @Override
    public Paint getLinePaint() {
        return Color.BLACK;
    }

    @Override
    public void setLinePaint(final Paint linePaint) {
        // ignore
    }

    @Override
    public int getLineWidth() {
        return 1;
    }

    @Override
    public void setLineWidth(final int width) {
        // ignore
    }

    @Override
    public Paint getFillingPaint() {
        return null;
    }

    @Override
    public void setFillingPaint(final Paint fillingStyle) {
        // ignore
    }

    @Override
    public float getTransparency() {
        return 0.8f;
    }

    @Override
    public void setTransparency(final float transparrency) {
        // ignore
    }

    @Override
    public FeatureAnnotationSymbol getPointAnnotationSymbol() {
        return null;
    }

    @Override
    public void setPointAnnotationSymbol(final FeatureAnnotationSymbol featureAnnotationSymbol) {
        // ignore
    }

    @Override
    public boolean isHighlightingEnabled() {
        return false;
    }

    @Override
    public void setHighlightingEnabled(final boolean enabled) {
        // ignore
    }
}
