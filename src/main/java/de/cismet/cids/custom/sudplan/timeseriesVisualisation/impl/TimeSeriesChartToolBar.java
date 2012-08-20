/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import Sirius.navigator.plugin.PluginRegistry;
import Sirius.navigator.ui.ComponentRegistry;

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
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.custom.objectrenderer.sudplan.TimeseriesRenderer;
import de.cismet.cids.custom.sudplan.dataExport.TimeSeriesExportWizardAction;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.Controllable;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSelectionNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.*;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

/**
 * Offers interaction functionality for <code>SimpleTimeSeriesVisualisation.</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesChartToolBar extends JToolBar implements TimeSeriesOperationListChangedListener,
    TimeSeriesSelectionListener,
    TimeSeriesListChangedListener {

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
                    ((SimpleTSVisualisation)tsVis).fireTimeSeriesSelectionChanged(new TimeSeriesSelectionEvent(
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
                    ((SimpleTSVisualisation)tsVis).fireTimeSeriesSelectionChanged(new TimeSeriesSelectionEvent(
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
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            removeAction.actionPerformed(e);
                        }
                    });
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

    private TimeSeriesExportWizardAction exportAction = new TimeSeriesExportWizardAction();
    private Action showOrigTSAction = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (originTS != null) {
                    final int answer = JOptionPane.showConfirmDialog(
                            ComponentRegistry.getRegistry().getMainWindow(),
                            java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle")
                                        .getString(
                                            "TimeSeriesRenderer.btnOriginalTSActionPerformed(ActionEvent).message"),
                            java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle")
                                        .getString(
                                            "TimeSeriesRenderer.btnOriginalTSActionPerformed(ActionEvent).title"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (answer == JOptionPane.YES_OPTION) {
                        fireShowOrigTSEvent(originTS);
                    }
                }
            }
        };

    private JButton btnSelectAll;
    private JButton btnDeselectAll;
    private JButton btnRemove;
    private JButton btnResetZoom;
    private JButton btnSaveAs;
    private JButton btnMapRemoveAll;
    private JButton btnExportTimeSeries;
    private JButton btnShowOriginTS;
    private final JMenuBar operationsMenubar = new JMenuBar();
    private TimeSeries originTS;
    private ArrayList<ShowOrigTimeseriesListener> showOrigListeners;

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
                "TimeSeriesChartToolBar.name")); // NOI18N
        chartPanel = p;
        this.tsVis = tsVis;
        showOrigListeners = new ArrayList<ShowOrigTimeseriesListener>();
        tsVis.addTimeSeriesListChangeListener(this);
        setRollover(true);
        setPreferredSize(new java.awt.Dimension(400, 30));
        this.setSize(400, 30);
        this.addButtons();
        if (tsVis.getTimeSeriesCollection().size() == 1) {
            exportAction.setTimeSeries(tsVis.getTimeSeriesCollection().iterator().next());
            btnExportTimeSeries.setEnabled(true);
            originTS = tsVis.getTimeSeriesCollection().iterator().next();
        }
        operationsMenubar.setBorderPainted(false);
//        operationsMenu = new JMenu(NbBundle.getMessage(
//                    TimeSeriesChartToolBar.class,
//                    "TimeSeriesChartToolBar.operationsMenu.text")); // NOI18N
        operationsMenu = new JMenu();
        operationsMenu.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.operationsMenu.text")); // NOI18N
        operationsMenu.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/timeseries_op_16.png")));
        operationsMenu.setSize(operationsMenu.getWidth(), 30);
        operationsMenu.setMinimumSize(new Dimension(0, 30));
        operationsMenubar.add(operationsMenu);
        operationsMenubar.setSize(operationsMenubar.getWidth(), 30);
        this.add(operationsMenubar);
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * adds the configured buttons to the tool bar.
     */
    private void addButtons() {
        this.add(createResetZoomButton());
        this.add(createSaveAsActionButton());
        final Controllable tsVisController = tsVis.getLookup(Controllable.class);
        if (!tsVisController.isSelectionEnabled()) {
            if (tsVis.getTimeSeriesCollection().size() == 1) {
                this.add(createExportTimeSeriesButton());
                this.add(createShowOriginalTSBUtton());
            }
        } else {
            this.add(createExportTimeSeriesButton());
            this.add(createShowOriginalTSBUtton());
            this.add(createRemoveActionButton());
            this.add(createSelectAllButton());
            this.add(createDeselectAllButton());
            this.add(createRemoveAllFromMapButton());
        }
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
    private JButton createExportTimeSeriesButton() {
        btnExportTimeSeries = new JButton(exportAction);
        btnExportTimeSeries.setFocusPainted(false);
        return btnExportTimeSeries;
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
//        btnSelectAll.setText(NbBundle.getMessage(
//                TimeSeriesChartToolBar.class,
//                "TimeSeriesChartToolBar.btnSelectAll.text"));        // NOI18N
        btnSelectAll.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/select_all_timeseries_16.png"))); // NOI18N
        return btnSelectAll;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createShowOriginalTSBUtton() {
        btnShowOriginTS = new JButton(showOrigTSAction);
        btnShowOriginTS.setFocusCycleRoot(false);
        btnShowOriginTS.setText(org.openide.util.NbBundle.getMessage(
                TimeseriesRenderer.class,
                "TimeseriesRenderer.btnOriginalTS.text"));
        btnShowOriginTS.setToolTipText(org.openide.util.NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnShowOriginTS.toolTipText"));
        return btnShowOriginTS;
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
//        btnDeselectAll.setText(NbBundle.getMessage(
//                TimeSeriesChartToolBar.class,
//                "TimeSeriesChartToolBar.btnDeselectAll.text"));        // NOI18N
        btnDeselectAll.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/deselect_all_timeseries_16.png")));
        return btnDeselectAll;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createRemoveActionButton() {
        btnRemove = new JButton(removeAllSelectedTimeseries);
        btnRemove.setEnabled(false);
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
//        btnMapRemoveAll.setText(NbBundle.getMessage(
//                TimeSeriesChartToolBar.class,
//                "TimeSeriesChartToolBar.btnMapRemoveAll.text"));        // NOI18N
        btnMapRemoveAll.setToolTipText(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "TimeSeriesChartToolBar.btnMapRemoveAll.toolTipText")); // NOI18N
        btnMapRemoveAll.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/remove_timeseries_from_map_16.png")));
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
    
    @Override
    public void selectionChanged(final TimeSeriesSelectionEvent evt) {
        if (evt.getSelectedTs().size() >= 1) {
            if (evt.getSelectedTs().size() == 1) {
                btnExportTimeSeries.setEnabled(true);
                btnShowOriginTS.setEnabled(true);
                final TimeSeries selectetTS = evt.getSelectedTs().iterator().next();
                exportAction.setTimeSeries(selectetTS);
                originTS = selectetTS;
            } else {
                btnExportTimeSeries.setEnabled(false);
                btnShowOriginTS.setEnabled(false);
                originTS = null;
            }
            final TimeSeriesVisualisation srcTsVis = (TimeSeriesVisualisation)evt.getSource();
            if (evt.getSelectedTs().size() == srcTsVis.getTimeSeriesCollection().size()) {
                btnRemove.setEnabled(false);
                return;
            } else if (srcTsVis.getTimeSeriesCollection().size() > 1) {
                btnRemove.setEnabled(true);
                return;
            }
        }
        if (tsVis.getTimeSeriesCollection().size() == 1) {
            btnExportTimeSeries.setEnabled(true);
            btnShowOriginTS.setEnabled(true);
            final TimeSeries selectetTS = evt.getSelectedTs().iterator().next();
            exportAction.setTimeSeries(selectetTS);
            originTS = selectetTS;
        } else {
            btnExportTimeSeries.setEnabled(false);
            btnShowOriginTS.setEnabled(false);
            originTS = null;
        }
        btnRemove.setEnabled(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    public void enableOperationsMenue(final boolean aFlag) {
        if (aFlag) {
            this.add(operationsMenubar);
        } else {
            this.remove(operationsMenubar);
        }
        updateUI();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    public void enableMapButton(final boolean aFlag) {
        if (aFlag) {
            this.add(btnMapRemoveAll);
        } else {
            this.remove(btnMapRemoveAll);
        }
        updateUI();
    }
    
    @Override
    public void timeSeriesListChanged(final TimeSeriesListChangedEvent evt) {
        if (tsVis.getTimeSeriesCollection().size() > 1) {
            final Controllable tsVisController = tsVis.getLookup(Controllable.class);
            if ((tsVisController != null) && !tsVisController.isSelectionEnabled()) {
                btnExportTimeSeries.setEnabled(false);
                btnShowOriginTS.setEnabled(false);
                return;
            } else {
                // is only one ts selected??
                final XYPlot plot = chartPanel.getChart().getXYPlot();
                TimeSeries ts = null;
                for (int i = 0; i < plot.getDatasetCount(); i++) {
                    if ((plot.getDataset(i) != null) && (plot.getDataset(i) instanceof TimeSeriesDatasetAdapter)
                            && (plot.getRenderer(i) instanceof SelectionXYLineRenderer)) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer) plot.getRenderer(i);
                        if (renderer.isSelected()) {
                            if (ts != null) {
                                ts = null;
                                break;
                            } else {
                                final TimeSeriesDatasetAdapter tsc = (TimeSeriesDatasetAdapter) plot.getDataset(i);
                                ts = tsc.getOriginTimeSeries();
                            }
                        }
                    }
                }
                if (ts == null) {
                    btnExportTimeSeries.setEnabled(false);
                    btnShowOriginTS.setEnabled(false);
                } else {
                    originTS = ts;
                    exportAction.setTimeSeries(ts);
                }
                return;
            }
        }
        btnExportTimeSeries.setEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aThis  DOCUMENT ME!
     */
    public void setTimeseriesChartPanel(final TimeseriesChartPanel aThis) {
        this.chartpnl = aThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addShowOrigTSListener(final ShowOrigTimeseriesListener listener) {
        showOrigListeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removeShowOrigTSListener(final ShowOrigTimeseriesListener listener) {
        showOrigListeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ts  DOCUMENT ME!
     */
    private void fireShowOrigTSEvent(final TimeSeries ts) {
        for (final ShowOrigTimeseriesListener l : showOrigListeners) {
            l.showOrigTS(ts);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setShowOrigButtonEnabled(final boolean b) {
        if (b) {
            this.add(btnShowOriginTS);
        } else {
            this.remove(btnShowOriginTS);
        }
    }
}
