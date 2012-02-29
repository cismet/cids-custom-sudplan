/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jfree.util.Log;

import org.openide.util.WeakListeners;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collection;
import java.util.Properties;

import javax.imageio.ImageIO;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSelectionNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSignature;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionListener;

import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class SOSModelComparisonFeatureInfoDisplay extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private TimeSeriesVisualisation tsVis;
    private int overlayWidth;
    private int overlayHeight;
    private TimeSeriesSelectionListener tsSelectionL;
    private JTabbedPane tbPane;
    private final transient Logger LOG = Logger.getLogger(SOSModelComparisonFeatureInfoDisplay.class);
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JToolBar closeToolbar;
    private javax.swing.JLabel lblFiller1;
    private javax.swing.JPanel pnlChart;
    private javax.swing.JPanel pnlChartToolbar;
    private javax.swing.JPanel pnlControlElements;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlTop;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SOSModelComparisonFeatureInfoDisplay.
     *
     * @param  tsv  tsVis DOCUMENT ME!
     * @param  tp   DOCUMENT ME!
     */
    public SOSModelComparisonFeatureInfoDisplay(final TimeSeriesVisualisation tsv, final JTabbedPane tp) {
        tbPane = tp;
        tsVis = tsv;
        // try to get properties for size of the overlay
        overlayWidth = 16;
        overlayHeight = 16;
        // try to get metainformation for overlay position, width, color from properties file and override the
        // default values if succesfull
        final Properties iconProps = new Properties();
        try {
            final InputStream in = getClass().getResourceAsStream(
                    "/de/cismet/cismap/commons/gui/res/featureInfoIcon.properties");                             // NOI18N
            if (in != null) {
                iconProps.load(in);
                in.close();
            } else {
                LOG.warn(
                    "Could not laod featureInfoIcon.properties file. Default values for overlay area are used"); // NOI18N
            }
        } catch (final IOException ex) {
            LOG.error(
                "Could not read featureInfoIcon.properties file. Default values for overlay area are used",      // NOI18N
                ex);                                                                                             // NOI18N
        }

        if (iconProps.isEmpty() || !(iconProps.containsKey("overlayWidth") && iconProps.containsKey("overlayHeigth"))) {       // NOI18N
            LOG.warn(
                "featureInfoIcon.properties file does not contain all needed keys. Default values for overlay area are used"); // NOI18N
        } else {
            try {
                overlayWidth = Integer.parseInt((String)iconProps.get("overlayWidth"));                                        // NOI18N
                overlayHeight = Integer.parseInt((String)iconProps.get("overlayHeigth"));                                      // NOI18N
            } catch (NumberFormatException ex) {
                Log.error(
                    "Error while retrieving properties for overlay area. Default values for overlay area are used",            // NOI18N
                    ex);
            }
        }
        final TimeSeriesSelectionNotification tsn = tsVis.getLookup(TimeSeriesSelectionNotification.class);
        if (tsn == null) {
            tsSelectionL = null;
        } else {
            tsSelectionL = new TimeSeriesSelectionListenerImpl();
            tsn.addTimeSeriesSelectionListener(
                WeakListeners.create(TimeSeriesSelectionListener.class, tsSelectionL, tsn));
        }
        initComponents();
        pnlChart.add(new JScrollPane(tsVis.getVisualisationUI()), BorderLayout.CENTER);
        pnlChartToolbar.add(tsVis.getToolbar(), BorderLayout.CENTER);
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

        pnlMain = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        pnlChartToolbar = new javax.swing.JPanel();
        lblFiller1 = new javax.swing.JLabel();
        pnlControlElements = new javax.swing.JPanel();
        closeToolbar = new javax.swing.JToolBar();
        btnClose = new javax.swing.JButton();
        pnlChart = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        pnlMain.setLayout(new java.awt.GridBagLayout());

        pnlTop.setPreferredSize(new java.awt.Dimension(40, 30));
        pnlTop.setLayout(new java.awt.GridBagLayout());

        pnlChartToolbar.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlTop.add(pnlChartToolbar, gridBagConstraints);

        lblFiller1.setText(org.openide.util.NbBundle.getMessage(
                SOSModelComparisonFeatureInfoDisplay.class,
                "SOSModelComparisonFeatureInfoDisplay.lblFiller1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlTop.add(lblFiller1, gridBagConstraints);

        pnlControlElements.setPreferredSize(new java.awt.Dimension(22, 22));
        pnlControlElements.setLayout(new java.awt.BorderLayout());

        closeToolbar.setBorder(null);
        closeToolbar.setFloatable(false);
        closeToolbar.setRollover(true);
        closeToolbar.setPreferredSize(new java.awt.Dimension(22, 22));
        closeToolbar.setRequestFocusEnabled(false);
        closeToolbar.setVerifyInputWhenFocusTarget(false);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/sudplan/cross.png"))); // NOI18N
        btnClose.setToolTipText(org.openide.util.NbBundle.getMessage(
                SOSModelComparisonFeatureInfoDisplay.class,
                "SOSModelComparisonFeatureInfoDisplay.btnClose.toolTipText"));                                           // NOI18N
        btnClose.setFocusPainted(false);
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setLabel(org.openide.util.NbBundle.getMessage(
                SOSModelComparisonFeatureInfoDisplay.class,
                "SOSModelComparisonFeatureInfoDisplay.btnClose.label"));                                                 // NOI18N
        btnClose.setPreferredSize(new java.awt.Dimension(22, 30));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCloseActionPerformed(evt);
                }
            });
        closeToolbar.add(btnClose);

        pnlControlElements.add(closeToolbar, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlTop.add(pnlControlElements, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
        pnlMain.add(pnlTop, gridBagConstraints);

        pnlChart.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 5);
        pnlMain.add(pnlChart, gridBagConstraints);

        add(pnlMain, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCloseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCloseActionPerformed
        final int index = tbPane.indexOfComponent(SOSModelComparisonFeatureInfoDisplay.this);
        if (index != -1) {
            tbPane.remove(index);
        }
    }                                                                            //GEN-LAST:event_btnCloseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   g   DOCUMENT ME!
     * @param   bi  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Feature createFeature(final Geometry g, final BufferedImage bi) {
        final TimeSeriesFeature feature = new TimeSeriesFeature(g, bi);

        return feature;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class TimeSeriesSelectionListenerImpl implements TimeSeriesSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void selectionChanged(final TimeSeriesSelectionEvent evt) {
            final Collection<TimeSeries> selectedTS = evt.getSelectedTs();
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            mc.getRubberBandLayer().removeAllChildren();
            mc.getTmpFeatureLayer().removeAllChildren();
            mc.repaint();
            final TimeSeriesSignature tss = tsVis.getLookup(TimeSeriesSignature.class);
            if (tss != null) {
                for (final TimeSeries ts : selectedTS) {
                    final BufferedImage bi = tss.getTimeSeriesSignature(ts, overlayWidth, overlayHeight);
                    final Geometry g = (Geometry)ts.getTSProperty(TimeSeries.GEOMETRY);
                    final PFeature pf = new PFeature(createFeature(g, bi), mc);
                    mc.addStickyNode(pf);
                    mc.getTmpFeatureLayer().addChild(pf);
                }
                mc.rescaleStickyNodes();
            }
        }
    }
}
