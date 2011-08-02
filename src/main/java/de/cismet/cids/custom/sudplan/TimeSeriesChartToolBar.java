/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.event.ActionEvent;

import java.io.IOException;

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * The time series chart toobar offers a toolbar with functions to remove Time Series from chart, make annotations,
 * reset Zoom Area and so on.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesChartToolBar extends JToolBar {

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
        super("Time Series Chart Tools");
        chartPanel = p;
        setRollover(true);
        setPreferredSize(new java.awt.Dimension(100, 25));
        this.setSize(100, 25);
        this.addButtons();
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
        final JButton b = new JButton(selectAll);
        b.setFocusPainted(false);
        b.setToolTipText("Show all time series on map");
        b.setText("Show all on map");
        return b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createDeselectAllButton() {
        final JButton b = new JButton(deselectAll);
        b.setFocusPainted(false);
        b.setToolTipText("remove all time series on map");
        b.setText("Remove all from map");
        return b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createRemoveActionButton() {
        final JButton b = new JButton(removeAllSelectedTimeseries);
        b.setFocusPainted(false);
        b.setToolTipText("Remove all selected time series from chart");
        b.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/chart_line_delete.png")));
//        b.setText("Remove all selected time series");
        b.setSize(16, 16);
        return b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createResetZoomButton() {
        final JButton b = new JButton(resetZoom);
        b.setFocusPainted(false);
        b.setToolTipText("Reset Zoom");
        b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/sudplan/arrow_out.png")));
//        b.setText("ResetZoom");
        b.setSize(16, 16);
        return b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createAutoAdjustRangeButton() {
        final JButton b = new JButton(autoAdjustRange);
        b.setFocusPainted(false);
        b.setToolTipText("Autoadjust Value Axis");
//        b.setIcon(null);
        b.setText("Autoadjust value axis");
        return b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createAutoAdjustDomainButton() {
        final JButton b = new JButton(resetZoom);
        b.setFocusPainted(false);
        b.setToolTipText("Autoadjust Time Axis");
//        b.setIcon(null);
        b.setText("Autoadjust Time Axis");
        return b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createSaveAsActionButton() {
        final JButton b = new JButton(saveAsimage);
        b.setFocusPainted(false);
        b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/sudplan/picture_save.png")));
        b.setSize(16, 16);
//        b.setText("Save As");
        return b;
    }
}
