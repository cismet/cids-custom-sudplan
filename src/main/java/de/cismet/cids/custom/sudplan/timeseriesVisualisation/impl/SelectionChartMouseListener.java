/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSelectionNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionEvent;

/**
 * A customised ChartMouseListener that takes care of the selection of <code>TimeSeriesDatasetAdapter</code>. Each
 * <code>TimeSeriesDatasetAdapter</code> is rendered by an instance of <code>SelectionXYLineRenderer</code> which
 * controls the flag of the selection and the visualisation of the selection. The selection is detected by two different
 * ways. JFreeChart only detects clicks on the data items of a <code>TimeSeriesDatasetAdapter</code>. If such a data
 * item was clicked the corresponding renderer is set selected. In fact that a selection should also be possible if a
 * click on the line between two data items was performed, The <code>SelectionXYLineRenderer</code> of each dataset
 * calculated the distance between the click and their data points If this distance is minimal it calls the method
 * <code>checkIfClickWasOnDataline</code> which checks if the click lies on the line between two data items and sets the
 * corresponding renderer to paint the dataset selected<br>
 * If a selection or deselection was performed it notifies all listeners of the corresponding <code>
 * TimeSeriesVisualisation</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class SelectionChartMouseListener implements ChartMouseListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SelectionChartMouseListener.class);

    //~ Instance fields --------------------------------------------------------

    TimeSeriesSelectionNotification selectionNotifier;
    ArrayList<TimeSeries> selectedTS = new ArrayList<TimeSeries>();
    private XYPlot plot;
    private TimeSeriesVisualisation tsVis;
    private boolean isSelectionNotifier = false;
    private ChartPanel chartPanel;
    private ChartMouseEvent lastevent;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesSelectionListener object.
     *
     * @param  p      the <code>XYPlot</code> that contains the datasets
     * @param  tsVis  the <code>TimeSeriesVisualisation</code>that contains the chart this Listener relies to. If a
     *                selection or deselection was performed the registers <code>TimeSeriesSelectionListeners</code> of
     *                it are notified
     * @param  pnl    the <code>ChartPanel</code> that contains the chart this Listener relies to
     */
    public SelectionChartMouseListener(final XYPlot p, final TimeSeriesVisualisation tsVis, final ChartPanel pnl) {
        plot = p;
        chartPanel = pnl;
        this.tsVis = tsVis;
        selectionNotifier = tsVis.getLookup(TimeSeriesSelectionNotification.class);
        if (selectionNotifier != null) {
            isSelectionNotifier = true;
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void chartMouseClicked(final ChartMouseEvent event) {
        final ChartEntity entity = event.getEntity();

        if ((entity != null) && (entity instanceof XYItemEntity)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Clicked on an data item. Select/Deselect dataset");                                                // NOI18N
            }
            final XYItemEntity xyEntity = (XYItemEntity)entity;
            final TimeSeriesDatasetAdapter tsc = (TimeSeriesDatasetAdapter)xyEntity.getDataset();
            final boolean multiSelection = event.getTrigger().isControlDown();
            doSelection(multiSelection, tsc);
        } else if ((entity != null) && (entity instanceof PlotEntity)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "Clicked on the Plot, store the MouseEvent to detect if the click was on a line between two data items"); // NOI18N
            }
            lastevent = event;
        }
    }

    @Override
    public void chartMouseMoved(final ChartMouseEvent event) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * deletes the selection of datasets.
     */
    public void clearSelection() {
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            if (plot.getDataset(i) != null) {
                final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRendererForDataset(
                        plot.getDataset(i));
                renderer.setSelected(false);
            }
        }
        selectedTS.clear();
        if (isSelectionNotifier) {
            final TimeSeriesSelectionEvent evt = new TimeSeriesSelectionEvent(
                    tsVis,
                    TimeSeriesSelectionEvent.TS_DESELECTED,
                    selectedTS);
            ((SimpleTSVisualisation)tsVis).fireTimeSeriesSelectionChanged(evt);
        }
    }

    /**
     * checks if the last click was on a line between two data items. if so the dataset gets selected. this method is a
     * listener method that is called from the <code>SelectionXYLineRenderer</code> during the rendering of the dataset.
     * It ensures that a selection is only done once.
     *
     * @param  dataset    the dataset that gets selected eventually!
     * @param  itemIndex  the closest item index to the last mouse click!
     */
    public void checkIfCklickWasOnDataLine(final XYDataset dataset, final int itemIndex) {
        // this ensures that the selection is done only onces, in fact that this method is called mulitple times
        if (lastevent != null) {
            final TimeSeriesCollection tsc = (TimeSeriesCollection)dataset;
            int secondItemIndex = 0;
            final int clickedX = lastevent.getTrigger().getX();
            final int clickedY = lastevent.getTrigger().getY();
            final Rectangle2D dataArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();

            final ValueAxis rangeAxis = plot.getRangeAxisForDataset(plot.indexOf(tsc));
            final ValueAxis domainAxis = plot.getDomainAxis();
            final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            final RectangleEdge yAxisLocation = plot.getRangeAxisEdge(plot.getRangeAxisIndex(rangeAxis));

            final double transX0 = domainAxis.valueToJava2D(tsc.getXValue(0, itemIndex), dataArea, xAxisLocation);
            final double transY0 = rangeAxis.valueToJava2D(tsc.getYValue(0, itemIndex), dataArea, yAxisLocation);

            if ((transX0 > clickedX) && !(itemIndex <= 0)) {
                secondItemIndex = itemIndex - 1;
            } else {
                secondItemIndex = itemIndex + 1;
            }

            final double transX1 = domainAxis.valueToJava2D(tsc.getXValue(0, secondItemIndex), dataArea, xAxisLocation);
            final double transY1 = rangeAxis.valueToJava2D(tsc.getYValue(0, secondItemIndex), dataArea, yAxisLocation);

            final Line2D line = new Line2D.Double();
            line.setLine(transX0, transY0, transX1, transY1);

            final double distance = line.ptLineDist(clickedX, clickedY);
            /* check if the last mouseclick lies on the line beetween the closest dataitem and the one before or the
             * one after
             */
            if (itemIndex < secondItemIndex) {
                if ((clickedX >= transX0) && (clickedX <= transX1)
                            && (distance < 3)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Clicked on the line between 2 dataitems. perform selection"); // NOI18N
                    }
                    doSelection(lastevent.getTrigger().isControlDown(), (TimeSeriesDatasetAdapter)tsc);
                    // sets the event to null so we do the selection only onces!
                    lastevent = null;
                }
            } else {
                if ((clickedX <= transX0) && (clickedX >= transX1)
                            && (distance < 3)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Clicked on the line between 2 dataitems. perform selection"); // NOI18N
                    }
                    doSelection(lastevent.getTrigger().isControlDown(), (TimeSeriesDatasetAdapter)tsc);
                    // sets the event to null so we do the selection only onces!
                    lastevent = null;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selected  DOCUMENT ME!
     */
    private void fireSelectionChanged(final boolean selected) {
        if (isSelectionNotifier) {
            final TimeSeriesSelectionEvent evt = new TimeSeriesSelectionEvent(
                    tsVis,
                    selected ? TimeSeriesSelectionEvent.TS_SELECTED : TimeSeriesSelectionEvent.TS_DESELECTED,
                    selectedTS);
            ((SimpleTSVisualisation)tsVis).fireTimeSeriesSelectionChanged(evt);
        }
    }

    /**
     * performs a selection operation on the <code>TimeSeriesDatasetAdapter</code>. Distinguish between Multi/single and
     * selection / deselection. Notifies all <code>TimeSeriesSelectionListener</code> of the corresponding <code>
     * TimeSeriesVisualisation</code>
     *
     * @param  ctrDown  the flag for single or multi selection
     * @param  tsc      the <code>TimeSeriesDatasetAdapter</code> for that the selection should be performed
     */
    private void doSelection(final boolean ctrDown, final TimeSeriesDatasetAdapter tsc) {
        final XYItemRenderer renderer = plot.getRendererForDataset((TimeSeriesCollection)tsc);
        final int index = plot.getIndexOf(renderer);
        final SelectionXYLineRenderer selectionRenderer = (SelectionXYLineRenderer)renderer;

        if (ctrDown) {
            // For Multi Selection
            selectionRenderer.setSelected(!selectionRenderer.isSelected());
            plot.setRenderer(index, selectionRenderer);
            if (selectionRenderer.isSelected()) {
                selectedTS.add(tsc.getOriginTimeSeries());
                fireSelectionChanged(true);
            } else {
                if (selectedTS.contains(tsc.getOriginTimeSeries())) {
                    selectedTS.remove(tsc.getOriginTimeSeries());
                } else {
                    if (LOG.isDebugEnabled()) {
                        // this could only happen if a selectAll Action in TimeSeriesChartToolBar was performed
                        LOG.debug("time series " + tsc.getOriginTimeSeries() // NOI18N
                                    + " could not be removed from selection collection"); // NOI18N
                    }
                    selectedTS.clear();
                    final int datasetCount = plot.getDatasetCount();
                    for (int i = 0; i < datasetCount; i++) {
                        final SelectionXYLineRenderer r = (SelectionXYLineRenderer)plot.getRenderer(i);
                        if (r.isSelected()) {
                            final TimeSeriesDatasetAdapter dataset = (TimeSeriesDatasetAdapter)plot.getDataset(i);
                            selectedTS.add(dataset.getOriginTimeSeries());
                        }
                    }
                }
                fireSelectionChanged(false);
            }
        } else {
            // single selection
            final boolean wasSelected = selectionRenderer.isSelected();
            int multiSelection = 0;
            // first remove the selction of all timeseries
            selectedTS = new ArrayList<TimeSeries>();
            for (int i = 0; i < plot.getDatasetCount(); i++) {
                final TimeSeriesCollection tsCollection = (TimeSeriesCollection)plot.getDataset(i);
                if (tsCollection != null) {
                    final SelectionXYLineRenderer nonSelectionrenderer = (SelectionXYLineRenderer)
                        plot.getRendererForDataset(tsCollection);
                    if (nonSelectionrenderer.isSelected()) {
                        multiSelection++;
                        nonSelectionrenderer.setSelected(false);
                        plot.setRenderer(i, nonSelectionrenderer, true);
                    }
                }
            }

            // paint the time series that was clicked on selected
            if (!wasSelected || (multiSelection > 1)) {
                selectionRenderer.setSelected(true);
                plot.setRenderer(index, selectionRenderer);
                selectedTS.add(tsc.getOriginTimeSeries());
                fireSelectionChanged(true);
            } else {
                fireSelectionChanged(false);
            }
        }
    }
}
