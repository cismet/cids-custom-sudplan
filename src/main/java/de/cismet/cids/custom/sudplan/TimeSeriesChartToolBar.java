/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.plugin.PluginRegistry;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;

import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.IOException;

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

/**
 * The time series chart toobar offers a toolbar with functions to remove Time Series from chart, make annotations,
 * reset Zoom Area and so on.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesChartToolBar extends JToolBar implements ItemListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesChartToolBar.class);

    //~ Instance fields --------------------------------------------------------

    private ChartPanel chartPanel;
    private Action resetZoom = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                chartPanel.restoreAutoBounds();
            }
        };

    private Action removeAllSelectedTimeseries = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYPlot plot = chartPanel.getChart().getXYPlot();
                final HashMap<Integer, TimeSeriesCollection> selectedTS = new HashMap<Integer, TimeSeriesCollection>();

                for (int i = 0; i < plot.getDatasetCount(); i++) {
                    if ((plot.getDataset(i) != null) && (plot.getDataset(i) instanceof TimeSeriesCollection)
                                && (plot.getRenderer(i) instanceof SelectionXYLineRenderer)) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i);
                        if (renderer.isSelected()) {
                            final TimeSeriesCollection tsc = (TimeSeriesCollection)plot.getDataset(i);
                            selectedTS.put(i, tsc);
                        }
                    }
                }
                final RemoveTimeSeriesAction removeAction = new RemoveTimeSeriesAction(
                        selectedTS,
                        plot,
                        (CustomChartPanel)chartPanel);
                removeAction.actionPerformed(e);
            }
        };

    private Action autoAdjustRange = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                chartPanel.restoreAutoRangeBounds();
            }
        };

    private Action autoAdjustDomain = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                chartPanel.restoreAutoDomainBounds();
            }
        };

    private Action saveAsimage = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    chartPanel.doSaveAs();
                } catch (IOException ex) {
                    LOG.warn("Can not save as image", ex);
                }
            }
        };

    private Action selectAll = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();
                for (int i = 0; i < plot.getRendererCount(); i++) {
                    if (plot.getRenderer(i) instanceof SelectionXYLineRenderer) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i);
                        renderer.setSelected(true);
                    }
                }
            }
        };

    private Action deselectAll = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();
                for (int i = 0; i < plot.getRendererCount(); i++) {
                    if (plot.getRenderer(i) instanceof SelectionXYLineRenderer) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i);
                        renderer.setSelected(false);
                    }
                }
            }
        };

    private Action removeAllFromMap = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final CismapPlugin cismapPl = (CismapPlugin)PluginRegistry.getRegistry().getPlugin("cismap"); // NOI18N
                final MappingComponent mc = cismapPl.getMappingComponent();
                mc.getTmpFeatureLayer().removeAllChildren();
            }
        };

    private JButton btnSelectAll;
    private JButton btnDeselectAll;
    private JButton btnRemove;
    private JButton btnResetZoom;
    private JButton btnSaveAs;
    private JButton btnMapRemoveAll;

    //~ Constructors -----------------------------------------------------------

    /**
     * private Action showAllTimeSeriesOnMap = new AbstractAction() { @Override public void actionPerformed(ActionEvent
     * e) { final XYPlot plot = chartPanel.getChart().getXYPlot(); for (int i = 0; i < plot.getDatasetCount(); i++) { if
     * ((plot.getDataset() != null) && (plot.getDataset(i) instanceof TimeSeriesCollection)) { final
     * SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i); final TimeSeriesCollection tsc =
     * (TimeSeriesCollection)plot.getDataset(i); } } } }.
     */
    public TimeSeriesChartToolBar() {
        this(null);
    }

    /**
     * Creates a new TimeSeriesChartToolBar object.
     *
     * @param  p  the ChartPanel that this Toolbar correspond to
     */
    public TimeSeriesChartToolBar(final ChartPanel p) {
        super("Time Series Chart Tools"); // NOI18N
        chartPanel = p;
        setRollover(true);
        setPreferredSize(new java.awt.Dimension(500, 30));
        this.setSize(500, 30);
        this.addButtons();
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void addButtons() {
        this.add(createResetZoomButton());
//        this.add(createAutoAdjustRangeButton());
//        this.add(createAutoAdjustDomainButton());

        this.add(createRemoveActionButton());
        this.add(createSaveAsActionButton());
        this.add(createSelectAllButton());
        this.add(createDeselectAllButton());
        this.add(createRemoveAllFromMapButton());
        final JComboBox cb = new JComboBox();
//        cb.addItem("Time Series Chart");//NOI18N
//        cb.addItem("Bar Chart");
//        cb.addItem("Point Chart");//NOI18N
//        cb.addItemListener(this);
//        cb.addItem("Difference Chart");
//        this.add(cb);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  chartPanel  DOCUMENT ME!
     */
    public void setChartPanel(final ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createSelectAllButton() {
        btnSelectAll = new JButton(selectAll);
        btnSelectAll.setFocusPainted(false);
        btnSelectAll.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnSelectAll.toolTipText")); // NOI18N
        btnSelectAll.setText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnSelectAll.text"));        // NOI18N
        return btnSelectAll;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createDeselectAllButton() {
        btnDeselectAll = new JButton(deselectAll);
        btnDeselectAll.setFocusPainted(false);
        btnDeselectAll.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnDeselectAll.toolTipText")); // NOI18N
        btnDeselectAll.setText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnDeselectAll.text"));        // NOI18N
        return btnDeselectAll;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createRemoveActionButton() {
        btnRemove = new JButton(removeAllSelectedTimeseries);
        btnRemove.setFocusPainted(false);
        btnRemove.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnRemove.toolTipText"));                                 // NOI18N
        btnRemove.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/chart_line_delete.png"))); // NO18N
//        b.setText("Remove all selected time series");
        btnRemove.setSize(16, 16);
        return btnRemove;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createResetZoomButton() {
        btnResetZoom = new JButton(resetZoom);
        btnResetZoom.setFocusPainted(false);
        btnResetZoom.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnResetZoom.toolTipText"));                      // NOI18N
        btnResetZoom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/arrow_out.png"))); // NOI18N
//        b.setText("ResetZoom");
        btnResetZoom.setSize(16, 16);
        return btnResetZoom;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
// private JButton createAutoAdjustRangeButton() {
// final JButton b = new JButton(autoAdjustRange);
// b.setFocusPainted(false);
// b.setToolTipText("Autoadjust Value Axis");
////        b.setIcon(null);
//        b.setText("Autoadjust value axis");
//        return b;
//    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
//    private JButton createAutoAdjustDomainButton() {
//        final JButton b = new JButton(resetZoom);
//        b.setFocusPainted(false);
//        b.setToolTipText("Autoadjust Time Axis");
////        b.setIcon(null);
//        b.setText("Autoadjust Time Axis");
//        return b;
//    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createSaveAsActionButton() {
        btnSaveAs = new JButton(saveAsimage);
        btnSaveAs.setFocusPainted(false);
        btnSaveAs.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnSaveAs.toolTipText"));                            // NOI18N
        btnSaveAs.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/picture_save.png"))); // NOI18N
        btnSaveAs.setSize(16, 16);
//        b.setText("Save As");
        return btnSaveAs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createRemoveAllFromMapButton() {
        btnMapRemoveAll = new JButton(removeAllFromMap);
        btnMapRemoveAll.setFocusPainted(false);
//        b.setIcon();
        btnMapRemoveAll.setText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnMapRemoveAll.text"));        // NOI18N
        btnMapRemoveAll.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnMapRemoveAll.toolTipText")); // NOI18N
        btnMapRemoveAll.setSize(16, 16);
//        b.setText("Save As");
        return btnMapRemoveAll;
    }
    @Override
    public void itemStateChanged(final ItemEvent e) {
        final JComboBox cb = (JComboBox)e.getSource();
        final String item = (String)cb.getSelectedItem();
        final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();

        if (item.equals("Point Chart")) { // NOI18N
            for (int i = 0; i < plot.getRendererCount(); i++) {
                final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer(i);
                renderer.setSeriesLinesVisible(0, false);
            }
        }
//        else if (item.equals("Bar Chart")) {
//            for (int i = 0; i < plot.getRendererCount(); i++) {
//                final ClusteredXYBarRenderer barRenderer = new ClusteredXYBarRenderer();
//
//                final XYBarDataset dataset = new XYBarDataset(plot.getDataset(i), 5000000d);
//                for (int j = 0; j < dataset.getSeriesCount(); j++) {
//                    if (LOG.isDebugEnabled()) {
//                        LOG.debug("Point: " + j + " start x / end x:" + dataset.getStartXValue(0, j) + "/"
//                                    + dataset.getEndXValue(0, j));
//
//                        final double foo = (dataset.getEndXValue(0, j) - dataset.getStartXValue(0, j));
//                        LOG.debug("bar width: " + foo);
//                    }
//                }
//                barRenderer.setShadowVisible(false);
//                plot.setRenderer(i, barRenderer);
//                plot.setDataset(i, dataset);
//            }
//        }
        else if (item.equals("Time Series Chart")) { // NOI18N
            for (int i = 0; i < plot.getRendererCount(); i++) {
                final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer(i);
                renderer.setSeriesLinesVisible(0, true);
            }
        }
    }
}
