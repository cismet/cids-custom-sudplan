/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import Sirius.navigator.plugin.PluginRegistry;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.log4j.Logger;

import org.jfree.chart.plot.XYPlot;

import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSelectionNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

/**
 * Offers interaction functionality for <code>SimpleTimeSeriesVisualisation.</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesChartToolBar extends JToolBar implements TimeSeriesOperationListChangedListener { 

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesChartToolBar.class);

    //~ Instance fields --------------------------------------------------------

    private final HashMap<Action, JMenuItem> operationMenuItemSet = new HashMap<Action, JMenuItem>();
    private JMenu operationsMenu;
    private CustomChartPanel chartPanel;
    public Action selectAll = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final TimeSeriesSelectionNotification notifyier = tsVis.getLookup(
                        TimeSeriesSelectionNotification.class);
                final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();
                for (int i = 0; i < plot.getRendererCount(); i++) {
                    if (plot.getRenderer(i) instanceof SelectionXYLineRenderer) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i);
                        renderer.setSelected(true);
                    }
                }
                if (notifyier != null) {
                    notifyier.fireTimeSeriesSelectionChanged(new TimeSeriesSelectionEvent(
                            tsVis,
                            TimeSeriesSelectionEvent.TS_SELECTED,
                            tsVis.getTimeSeriesCollection()));
                }
            }
        };

    public Action deselectAll = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final TimeSeriesSelectionNotification notifyier = tsVis.getLookup(
                        TimeSeriesSelectionNotification.class);
                final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();
                for (int i = 0; i < plot.getRendererCount(); i++) {
                    if (plot.getRenderer(i) instanceof SelectionXYLineRenderer) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i);
                        renderer.setSelected(false);
                    }
                }
                if (notifyier != null) {
                    notifyier.fireTimeSeriesSelectionChanged(new TimeSeriesSelectionEvent(
                            tsVis,
                            TimeSeriesSelectionEvent.TS_DESELECTED,
                            new ArrayList<TimeSeries>()));
                }
            }
        };

    private TimeSeriesVisualisation tsVis;
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
                final HashMap<Integer, TimeSeriesDatasetAdapter> selectedTS =
                    new HashMap<Integer, TimeSeriesDatasetAdapter>();

                for (int i = 0; i < plot.getDatasetCount(); i++) {
                    if ((plot.getDataset(i) != null) && (plot.getDataset(i) instanceof TimeSeriesDatasetAdapter)
                                && (plot.getRenderer(i) instanceof SelectionXYLineRenderer)) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i);
                        if (renderer.isSelected()) {
                            final TimeSeriesDatasetAdapter tsc = (TimeSeriesDatasetAdapter)plot.getDataset(i);
                            selectedTS.put(i, tsc);
                        }
                    }
                }
                final RemoveTimeSeriesAction removeAction = new RemoveTimeSeriesAction(
                        selectedTS,
                        plot,
                        tsVis);
                removeAction.actionPerformed(e);
            }
        };

    private Action saveAsimage = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    chartPanel.doSaveAs();
                } catch (IOException ex) {
                    LOG.warn("Can not save as image", ex); // NOI18N
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
     * Creates a new TimeSeriesChartToolBar object.
     */
    public TimeSeriesChartToolBar() {
        this(null, null);
    }

    /**
     * Creates a new TimeSeriesChartToolBar object.
     *
     * @param  p      the ChartPanel that this tool bar correspond to
     * @param  tsVis  the <code>TimeSeriesVisualisation</code> that this tool bar corresponds to
     */
    public TimeSeriesChartToolBar(final CustomChartPanel p, final TimeSeriesVisualisation tsVis) {
        super(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.name"));                    // NOI18N
        chartPanel = p;
        this.tsVis = tsVis;
        setRollover(true);
        setPreferredSize(new java.awt.Dimension(400, 30));
        this.setSize(400, 30);
        this.addButtons();
        final JMenuBar menubar = new JMenuBar();
        menubar.setBorderPainted(false);
        operationsMenu = new JMenu(NbBundle.getMessage(
                    TimeSeriesChartToolBar.class,
                    "TimeSeriesChartToolBar.operationsMenu.text")); // NOI18N
        operationsMenu.setSize(operationsMenu.getWidth(), 30);
        operationsMenu.setMinimumSize(new Dimension(0, 30));
        menubar.add(operationsMenu);
        menubar.setSize(menubar.getWidth(), 30);
        this.add(menubar);
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * adds the configured buttons to the tool bar.
     */
    private void addButtons() {
        this.add(createResetZoomButton());
        this.add(createRemoveActionButton());
        this.add(createSaveAsActionButton());
        this.add(createSelectAllButton());
        this.add(createDeselectAllButton());
        this.add(createRemoveAllFromMapButton());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  chartPanel  DOCUMENT ME!
     */
    public void setChartPanel(final CustomChartPanel chartPanel) {
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
    private JButton createSaveAsActionButton() {
        btnSaveAs = new JButton(saveAsimage);
        btnSaveAs.setFocusPainted(false);
        btnSaveAs.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnSaveAs.toolTipText"));                            // NOI18N
        btnSaveAs.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/picture_save.png"))); // NOI18N
        btnSaveAs.setSize(16, 16);
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
        btnMapRemoveAll.setText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnMapRemoveAll.text"));        // NOI18N
        btnMapRemoveAll.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnMapRemoveAll.toolTipText")); // NOI18N
        btnMapRemoveAll.setSize(16, 16);
        return btnMapRemoveAll;
    }

    @Override
    public void timeSeriesOperationChanged(final TimeSeriesOperationChangedEvent evt) {
        final TimeSeriesOperation tsOp = (TimeSeriesOperation)evt.getSource();
        if (evt.getID() == TimeSeriesOperationChangedEvent.OPERATION_ADD) {
            final JMenuItem newOp = new JMenuItem(tsOp);
            operationMenuItemSet.put(tsOp, newOp);
            operationsMenu.add(newOp);
        } else if (evt.getID() == TimeSeriesOperationChangedEvent.OPERATION_REMOVE) {
            final JMenuItem toRemove = operationMenuItemSet.get(tsOp);
            operationsMenu.remove(toRemove);
            operationMenuItemSet.remove(tsOp);
        } else {
            for (final Action a : operationMenuItemSet.keySet()) {
                final JMenuItem toRemove = operationMenuItemSet.get(a);
                operationsMenu.remove(toRemove);
            }
            operationMenuItemSet.clear();
        }
        this.updateUI();
        this.repaint();
        this.invalidate();
        this.validate();
    }
}
