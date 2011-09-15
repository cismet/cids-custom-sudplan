/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.Unit;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.AbstractTimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.Controllable;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesEventNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSelectionNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSignature;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesEventListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperationResultListener;

import de.cismet.cismap.commons.features.SignaturedFeature;

/**
 * <code>TimeSeriesVisualisation</code> for simple time series (for each time t exists only a simple value v, no grid).
 * Visualises the <code>TimeSeries</code> objects with a JFreeChart TimeSeriesChart. Implements also the Interface
 * <code>TimeSeriesSelectionNotification</code> to notify registered Listeners about selection events. The selection of
 * time series is done by a <code>SelectionChartMouseListener</code> which also fires the event.<br>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class SimpleTSVisualisation extends AbstractTimeSeriesVisualisation implements TimeSeriesSelectionNotification,
    TimeSeriesEventNotification,
    Controllable,
    TimeSeriesSignature,
    TimeSeriesOperationResultListener {

    //~ Static fields/initializers ---------------------------------------------

    /** limit to control if the shaped for data items are drawn or not. */
    public static final int ITEM_LIMIT = 20;
    private static final transient Logger LOG = Logger.getLogger(SimpleTSVisualisation.class);

    //~ Instance fields --------------------------------------------------------

    private final ArrayList<TimeSeries> tsList = new ArrayList<TimeSeries>();
    private final ArrayList<TimeSeriesOperation> operationList = new ArrayList<TimeSeriesOperation>();
    private final ArrayList<TimeSeriesEventListener> eventListeners = new ArrayList<TimeSeriesEventListener>();
    private final ArrayList<TimeSeriesSelectionListener> selectionListeners =
        new ArrayList<TimeSeriesSelectionListener>();
    private CustomChartPanel chartPanel;
    private TimeSeriesChartToolBar toolbar;
    private boolean contextMenuEnabled;
    private boolean legendVisible;
    private boolean toolTipsEnabled;
    private boolean zoomEnabled;
    private boolean selectionEnabled;
    private SelectionChartMouseListener listener;
    private LegendTitle legend;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimpleTSVisualisation object.
     */
    public SimpleTSVisualisation() {
        chartPanel = null;
        toolbar = null;
        props.put(
            TimeSeriesVisualisation.TITLE_KEY,
            NbBundle.getMessage(
                SimpleTSVisualisation.class,
                "SimpleTSVisualisation.title")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addTimeSeries(final TimeSeries ts) {
        if (!checkTimeSeriesFormat(ts)) {
            throw new IllegalStateException("Time Series that shall be added does not match format constraints"); // NOI18N
        }
        tsList.add(ts);
        // add the new time series to chartpanel
        final TimeSeriesDatasetAdapter tsc = createJFreeDataset(ts);
        // if this is the first time series being added, create the chart
        if (chartPanel == null) {
            final JFreeChart chart = createChart(tsc, SMSUtils.unitFromTimeseries(ts));
            // sets the color list for the chart
            final XYPlot plot = chart.getXYPlot();
            final Paint[] paintSequence = new Paint[] {
                    new Color(0xFF, 0x55, 0x55),
                    new Color(0x55, 0x55, 0xFF),
                    new Color(0x55, 0xFF, 0x55),
                    new Color(0xFF, 0x55, 0xFF),
                    Color.pink,
                    Color.gray,
                    ChartColor.DARK_RED,
                    ChartColor.DARK_BLUE,
                    ChartColor.DARK_GREEN,
                    ChartColor.DARK_MAGENTA,
                    ChartColor.DARK_CYAN,
                    Color.darkGray,
                    ChartColor.LIGHT_RED,
                    ChartColor.LIGHT_BLUE,
                    ChartColor.VERY_DARK_RED,
                    ChartColor.VERY_DARK_BLUE,
                    ChartColor.VERY_DARK_GREEN,
                    ChartColor.VERY_DARK_YELLOW,
                    ChartColor.VERY_DARK_MAGENTA,
                    ChartColor.VERY_DARK_CYAN,
                    ChartColor.VERY_LIGHT_RED,
                    ChartColor.VERY_LIGHT_MAGENTA,
                };
            plot.setDrawingSupplier(new DefaultDrawingSupplier(
                    paintSequence,
                    DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
            chartPanel = new CustomChartPanel(chart, this);
            listener = new SelectionChartMouseListener(chart.getXYPlot(), this, chartPanel);

            if (isSelectionEnabled()) {
                chartPanel.addChartMouseListener(listener);
            }
            // set the default behaviour configuration
            chartPanel.setEnableContextMenu(contextMenuEnabled);
            chartPanel.setDomainZoomable(isZoomEnabled());
            chartPanel.setRangeZoomable(isZoomEnabled());
            legend = new LegendTitle(chartPanel.getChart().getPlot());
            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
            legend.setFrame(new LineBorder());
            legend.setBackgroundPaint(Color.white);
            legend.setPosition(RectangleEdge.BOTTOM);
            if (toolbar == null) {
                toolbar = new TimeSeriesChartToolBar(chartPanel, this);
                this.addTimeSeriesOperationListListener(WeakListeners.create(
                        TimeSeriesOperationListChangedListener.class,
                        toolbar,
                        this));
                this.addTimeSeriesSelectionListener(WeakListeners.create(
                        TimeSeriesSelectionListener.class,
                        toolbar,
                        this));
                for (final TimeSeriesOperation op : operationList) {
                    // to notify the toolbar about the operations...
                    fireTSOperationsChanged(new TimeSeriesOperationChangedEvent(
                            op,
                            TimeSeriesOperationChangedEvent.OPERATION_ADD));
                    // to set initial enable value of the operations
                    op.setavailableTimeSeriesList(new ArrayList<TimeSeries>());
                }
            } else {
                toolbar.setChartPanel(chartPanel);
            }

            // setting selection listener to renderer for custom selecteion detection...
            final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRendererForDataset(tsc);
            renderer.addSelectionChartMouseListener(listener);

            // this is needed to make sure, that just a limited number of shapes are drawn for that series
            if (tsc.getItemCount(0) <= ITEM_LIMIT) {
                renderer.setBaseShapesVisible(true);
                renderer.setBaseShapesFilled(true);
            }
        } else {
            if (chartPanel.getChart().getPlot() instanceof XYPlot) {
                final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();
                final org.jfree.data.time.TimeSeries newTimeseries = tsc.getSeries(0);
                final SelectionXYLineRenderer renderer = new SelectionXYLineRenderer(true, false, false);
                renderer.addSelectionChartMouseListener(listener);
                // this is needed to make sure, that just a limited number of shapes are drawn for that series
                if (tsc.getItemCount(0) <= ITEM_LIMIT) {
                    renderer.setBaseShapesVisible(true);
                    renderer.setBaseShapesFilled(true);
                }
                /*
                 * if a dataset was removed it can happen that a dataset in the plots list is null therefore put the new
                 * timeseries in the first null dataset. if no time series was deleted add to the end of the list
                 */
                final int datasetCount = plot.getDatasetCount();
                boolean tsDeleted = false;
                for (int i = 0; i < datasetCount; i++) {
                    if (plot.getDataset(i) == null) {
                        tsDeleted = true;

                        plot.setDataset(i, tsc);
                        plot.setRenderer(i, renderer);
                        break;
                    }
                }
                if (!tsDeleted) {
                    plot.setDataset(datasetCount, tsc);
                    plot.setRenderer(datasetCount, renderer);
                }

                // if there are different units we have to create a multi axis chart
                boolean newTSVariable = true;
                for (int i = 0; i < (plot.getDatasetCount() - 1); i++) {
                    final TimeSeriesCollection tsCollection = (TimeSeriesCollection)plot.getDataset(i);
                    if (tsCollection != null) {
                        final org.jfree.data.time.TimeSeries timeSeries = tsCollection.getSeries(0);
                        if (newTimeseries.getRangeDescription().equals(timeSeries.getRangeDescription())) {
                            // map new dataset to the corresponding axis
                            final ValueAxis axis = plot.getRangeAxisForDataset(i);
                            final int axisIndex = plot.getRangeAxisIndex(axis);
                            plot.mapDatasetToRangeAxis(tsList.size() - 1, axisIndex);
                            newTSVariable = false;
                            break;
                        }
                    }
                }
                if (newTSVariable) {
                    // time series doesnt fit to any axisting axis, so create a new one
                    final NumberAxis axis = new NumberAxis(newTimeseries.getRangeDescription());
                    axis.setLabel(tsc.getOriginTimeSeries().getTSProperty(TimeSeries.OBSERVEDPROPERTY) + " in "
                                + newTimeseries.getRangeDescription());
                    axis.setAutoRange(true);
                    axis.setAutoRangeIncludesZero(false);
                    axis.setLabelFont(plot.getRangeAxis(0).getLabelFont());
                    final int axisIndex = plot.getRangeAxisCount();
                    plot.setRangeAxis(axisIndex, axis);
                    plot.mapDatasetToRangeAxis(tsList.size() - 1, axisIndex);
                }
            }
        }

        fireTimeSeriesChanged(new TimeSeriesListChangedEvent(ts, TimeSeriesListChangedEvent.TIME_SERIES_ADDED));
    }

    @Override
    public void removeTimeSeries(final TimeSeries ts) {
        if (tsList.contains(ts)) {
            final org.jfree.data.time.TimeSeriesCollection toRemoveCollection = createJFreeDataset(ts);
            final JFreeChart chart = chartPanel.getChart();
            final XYPlot plot = (XYPlot)chart.getPlot();
            boolean tscRemoved = false;
            for (int i = 0; i < plot.getDatasetCount(); i++) {
                if ((plot.getDataset(i) != null) && plot.getDataset(i).equals(toRemoveCollection)) {
                    plot.setDataset(i, null);
                    plot.setRenderer(i, null);
                    tscRemoved = true;
                    break;
                }
            }
            if (!tscRemoved && LOG.isDebugEnabled()) {
                LOG.debug("time series that shall be removed was not found in JFreeChart Dataset");    // NO18N
            }
            tsList.remove(ts);
            fireTimeSeriesChanged(new TimeSeriesListChangedEvent(ts, TimeSeriesListChangedEvent.TIME_SERIES_REMOVED));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("time series visualisation does not contain time series: " + ts.toString()); // NO18N
            }
        }
    }

    @Override
    public void clearTimeSeries() {
        tsList.clear();
        chartPanel = null;
        fireTimeSeriesChanged(new TimeSeriesListChangedEvent(
                this,
                TimeSeriesListChangedEvent.TIME_SERIES_CLEARED));
    }

    @Override
    public Collection<TimeSeries> getTimeSeriesCollection() {
        return tsList;
    }

    @Override
    public void addTimeSeriesOperation(final TimeSeriesOperation op) {
        operationList.add(op);
        op.addTimeSeriesOperationResultListener(this);
        fireTSOperationsChanged(new TimeSeriesOperationChangedEvent(
                op,
                TimeSeriesOperationChangedEvent.OPERATION_REMOVE));
    }

    @Override
    public void removeTimeSeriesOperation(final TimeSeriesOperation op) {
        if (operationList.remove(op)) {
            fireTSOperationsChanged(new TimeSeriesOperationChangedEvent(
                    op,
                    TimeSeriesOperationChangedEvent.OPERATION_REMOVE));
        }
    }

    @Override
    public void clearTimeSeriesOperations() {
        operationList.clear();
        fireTSOperationsChanged(new TimeSeriesOperationChangedEvent(
                null,
                TimeSeriesOperationChangedEvent.OPERATIONS_CLEARED));
    }

    @Override
    public <T> T getLookup(final Class<T> clazz) {
        if (clazz.isAssignableFrom(SimpleTSVisualisation.class)) {
            return clazz.cast(this);
        } else {
            return null;
        }
    }

    @Override
    public JComponent getVisualisationUI() {
        return chartPanel;
    }

    @Override
    public JToolBar getToolbar() {
        return toolbar;
    }

    /**
     * converts a <code>TimeSeries</code> object into JFreeChart format.
     *
     * @param   ts  the <code>TimeSeries</code> object to convert
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private TimeSeriesDatasetAdapter createJFreeDataset(final TimeSeries ts) {
        final TimeStamp[] timeStamps = ts.getTimeStampsArray();
        String name = (String)ts.getTSProperty(TimeSeries.OBSERVEDPROPERTY);
        if (name == null) {
            LOG.error("Could not relate the time series with an name");     // NOI18N
            name = "notFound";                                              // NOI18N
        }
        final Object valueKeyObject = ts.getTSProperty(TimeSeries.VALUE_KEYS);
        final String valueKey;
        if (valueKeyObject instanceof String) {
            valueKey = (String)valueKeyObject;
            if (LOG.isDebugEnabled()) {
                LOG.debug("found valuekey: " + valueKey);                   // NOI18N
            }
        } else if (valueKeyObject instanceof String[]) {
            final String[] valueKeys = (String[])valueKeyObject;
            if (LOG.isDebugEnabled()) {
                LOG.debug("found multiple valuekeys: " + valueKeys.length); // NOI18N
            }

            if (valueKeys.length == 1) {
                valueKey = valueKeys[0];
            } else {
                throw new IllegalStateException("found too many valuekeys");              // NOI18N
            }
        } else {
            throw new IllegalStateException("unknown value key type: " + valueKeyObject); // NOI18N
        }
        // TODO problem was für eine zeitliche Auflösung soll für die Jfreechart zeitreihe genommen werden...
        // TODO Problem was für ein datentyp steckt hinter der zeitreihe...
        final org.jfree.data.time.TimeSeries data = new org.jfree.data.time.TimeSeries(name); // NOI18N
        data.setRangeDescription(SMSUtils.unitFromTimeseries(ts).getLocalisedName());
        for (final TimeStamp stamp : timeStamps) {
            final Float value = (Float)ts.getValue(stamp, valueKey);
            data.add(new Millisecond(stamp.asDate()), value);
        }

        final TimeSeriesDatasetAdapter dataset = new TimeSeriesDatasetAdapter(data);
        Geometry g = null;
        if (ts.getTSProperty(TimeSeries.GEOMETRY) instanceof Envelope) {
            final Envelope e = (Envelope)ts.getTSProperty(TimeSeries.GEOMETRY);
            final GeometryFactory gf = new GeometryFactory();
            g = gf.createPoint(new Coordinate(e.getMinX(), e.getMinY()));
        } else {
            g = (Geometry)ts.getTSProperty(TimeSeries.GEOMETRY);
        }

        dataset.setGeometry(g);
        dataset.setOriginTimeSeries(ts);

        return dataset;
    }

    /**
     * creates a chart with the first added <code>TimeSeries <code>as dataset.</code></code>
     *
     * @param   tsc   the dataset
     * @param   unit  the unit of the underlying <code>TimeSeries</code> for the y-axis title
     *
     * @return  DOCUMENT ME!
     */
    private JFreeChart createChart(final TimeSeriesDatasetAdapter tsc, final Unit unit) {
        final JFreeChart chart;
        final String yAxisLabel = tsc.getOriginTimeSeries().getTSProperty(TimeSeries.OBSERVEDPROPERTY) + " in "
                    + tsc.getSeries(0).getRangeDescription();
        chart = ChartFactory.createTimeSeriesChart(
                this.getProperty(TimeSeriesVisualisation.TITLE_KEY), // title
                "Time",              // time axis label
                yAxisLabel,          // value axis label
                tsc,                 // dataset
                islegendVisible(),   // legend
                isToolTipsEnabled(), // tooltips
                false);              // urls

        final XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.BLUE);
        plot.setRangeGridlinePaint(Color.BLUE);
        plot.setAxisOffset(new RectangleInsets(15d, 15d, 15d, 15d));
        // setting cusotmized Stroke for crosshair and background data grid
        final float[] dash = { 5f };
        final BasicStroke crosshairStroke = new BasicStroke(
                0.7f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                2f,
                dash,
                0f);
        plot.setRangeCrosshairStroke(crosshairStroke);
        plot.setRangeCrosshairPaint(Color.darkGray);
        plot.setDomainCrosshairStroke(crosshairStroke);
        plot.setDomainCrosshairPaint(Color.darkGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeCrosshairVisible(true);
        plot.setDomainCrosshairVisible(true);

        final SelectionXYLineRenderer renderer = new SelectionXYLineRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseShapesFilled(false);
        final NumberAxis axis = (NumberAxis)plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        plot.setRenderer(renderer);
        return chart;
    }

    @Override
    public void addTimeSeriesSelectionListener(final TimeSeriesSelectionListener l) {
        selectionListeners.add(l);
    }

    @Override
    public void removeTimeSeriesSelectionListener(final TimeSeriesSelectionListener l) {
        selectionListeners.remove(l);
    }

    /**
     * notifies all managed <code>TimeSeriesSelectionListener</code> about the <code>TimeSeriesSelectionEvent.</code>
     *
     * @param  evt  DOCUMENT ME!
     */
    public void fireTimeSeriesSelectionChanged(final TimeSeriesSelectionEvent evt) {
        for (final TimeSeriesOperation op : operationList) {
            op.setavailableTimeSeriesList(evt.getSelectedTs());
        }

        final Iterator<TimeSeriesSelectionListener> it;

        synchronized (selectionListeners) {
            it = new ArrayList<TimeSeriesSelectionListener>(selectionListeners).iterator();
        }

        while (it.hasNext()) {
            final TimeSeriesSelectionListener selectionListener = it.next();
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        selectionListener.selectionChanged(evt);
                    }
                });
        }
    }

    /**
     * Checks the format of a <code>TimeSeries</code> object if.
     *
     * @param   timeseries  the <code>TimeSeries</code> to check
     *
     * @return  true if the
     *
     *          <p><code>timeseries</code> fulfils the format else false</p>
     */
    private boolean checkTimeSeriesFormat(final TimeSeries timeseries) {
        final Object valueKeyObject = timeseries.getTSProperty(TimeSeries.VALUE_KEYS);
        String valueKey = null;
        // check if just one valueKey
        if (valueKeyObject instanceof String[]) {
            final String[] valueKeyArr = (String[])valueKeyObject;
            if (valueKeyArr.length > 1) {
                return false;
            }
            if (valueKeyArr[0] == null) {
                return false;
            } else {
                valueKey = valueKeyArr[0];
            }
        } else {
            valueKey = (String)valueKeyObject;
            if (valueKey == null) {
                return false;
            }
        }
        // check if simple or gridded TimeSeries
        for (final TimeStamp ts : timeseries.getTimeStampsArray()) {
            final Object value = timeseries.getValue(ts, valueKey);
            if (value != null) {
                try {
                    final Float val = (Float)value;
                } catch (ClassCastException e) {
                    return false;
                }
            }
        }

        // check properties
// if (timeseries.getTSProperty(TimeSeries.GEOMETRY) == null) {
// return false;
// }
// else if (timeseries.getTSProperty(TimeSeries.UNIT) == null) {
// return false;
// }

        return true;
    }

    @Override
    public void addTimeSeriesEventListener(final TimeSeriesEventListener l) {
        eventListeners.add(l);
    }

    @Override
    public void removeTimeSeriesEventListener(final TimeSeriesEventListener l) {
        eventListeners.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void fireTimeSeriesEventOccured(final TimeSeriesEvent evt) {
        final Iterator<TimeSeriesEventListener> it;

        synchronized (eventListeners) {
            it = new ArrayList<TimeSeriesEventListener>(eventListeners).iterator();
        }

        while (it.hasNext()) {
            final TimeSeriesEventListener timeSeriesEventListener = it.next();
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        timeSeriesEventListener.timeSeriesEventOccured(evt);
                    }
                });
        }
    }

    @Override
    public void enableContextMenu(final boolean aFlag) {
        contextMenuEnabled = aFlag;
        if (chartPanel != null) {
            if (isContextMenuEnabled()) {
                chartPanel.setEnableContextMenu(true);
            } else {
                chartPanel.setEnableContextMenu(false);
            }
        }
    }

    @Override
    public boolean isContextMenuEnabled() {
        return contextMenuEnabled;
    }

    @Override
    public void showLegend(final boolean aFlag) {
        legendVisible = aFlag;
        if (chartPanel != null) {
            if (legendVisible) {
                chartPanel.getChart().addSubtitle(legend);
                legend.addChangeListener(chartPanel.getChart());
            } else {
                chartPanel.getChart().removeSubtitle(legend);
                legend.removeChangeListener(chartPanel.getChart());
            }
        }
    }

    @Override
    public boolean islegendVisible() {
        return legendVisible;
    }

    @Override
    public void enableToolTips(final boolean aFlag) {
        toolTipsEnabled = aFlag;
        if (chartPanel != null) {
            if (isToolTipsEnabled()) {
                final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();
                final XYToolTipGenerator toolTipGenerator = new DateValueToolTipGenerator();
                for (int i = 0; i < plot.getDatasetCount(); i++) {
                    plot.getRenderer(i).setBaseToolTipGenerator(toolTipGenerator);
                }
                chartPanel.setDisplayToolTips(true);
            } else {
                final XYPlot plot = (XYPlot)chartPanel.getChart().getPlot();
                for (int i = 0; i < plot.getDatasetCount(); i++) {
                    plot.getRenderer(i).setBaseToolTipGenerator(null);
                }
                chartPanel.setDisplayToolTips(false);
            }
        }
    }

    @Override
    public boolean isToolTipsEnabled() {
        return toolTipsEnabled;
    }

    @Override
    public void enableZoom(final boolean aFlag) {
        zoomEnabled = aFlag;
        if (chartPanel != null) {
            if (isZoomEnabled()) {
                chartPanel.setDomainZoomable(true);
                chartPanel.setRangeZoomable(true);
                fireTimeSeriesEventOccured(new TimeSeriesEvent(this, TimeSeriesEvent.ZOOM_ENABLED));
                // TODO scrollbar in CustomChartPanel rein, Button In Toolbar rein
            } else {
                chartPanel.setDomainZoomable(false);
                chartPanel.setRangeZoomable(false);
                fireTimeSeriesEventOccured(new TimeSeriesEvent(this, TimeSeriesEvent.ZOOM_DISABLED));
                // TODO scrollbar aus CustomChartPanel weg, Buton In Toolbar weg
            }
        }
    }

    @Override
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    @Override
    public void enableSelection(final boolean aFlag) {
        selectionEnabled = aFlag;
        if (chartPanel != null) {
            if (isSelectionEnabled()) {
                chartPanel.removeChartMouseListener(listener);
                chartPanel.addChartMouseListener(listener);
                toolbar.selectAll.setEnabled(true);
                toolbar.deselectAll.setEnabled(true);
                // TODO toolbar ändern
            } else {
                chartPanel.removeChartMouseListener(listener);
                // TODO Toolbar Ändern, vorhandende selektion aufheben
                listener.clearSelection();
                toolbar.selectAll.setEnabled(false);
                toolbar.deselectAll.setEnabled(false);
            }
        }
    }

    @Override
    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    @Override
    public BufferedImage getTimeSeriesSignature(final TimeSeries timeseries, final int heigth, final int width) {
        final TimeSeriesDatasetAdapter requestTSC = (TimeSeriesDatasetAdapter)createJFreeDataset(timeseries);
        final XYPlot plot = chartPanel.getChart().getXYPlot();
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            if (plot.getDataset(i) != null) {
                final TimeSeriesDatasetAdapter tsc = (TimeSeriesDatasetAdapter)plot.getDataset(i);
                if ((tsc != null) && tsc.equals(requestTSC) && tsc.getOriginTimeSeries().equals(timeseries)) {
                    final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRendererForDataset(tsc);
                    final Shape s = renderer.getLegendItem(i, 0).getShape();
                    final Paint paint = renderer.getLegendItem(i, 0).getFillPaint();
                    final SignaturedFeature tsFeature = createFeatureSignature(tsc.getGeometry(),
                            s,
                            paint,
                            width,
                            heigth);
                    return tsFeature.getOverlayIcon();
                }
            }
        }
        throw new IllegalStateException(
            "Could not create TimeSeriesSignature. Timeseries was not found in JFreeDataSet"); // NOI18N
    }

    /**
     * creates a SignaturedFeature Object that contains the shape and paint of the <code>TimeSeriesDatasetAdapter</code>
     * as an Image.
     *
     * @param   g       DOCUMENT ME!
     * @param   s       DOCUMENT ME!
     * @param   p       DOCUMENT ME!
     * @param   width   DOCUMENT ME!
     * @param   height  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SignaturedFeature createFeatureSignature(final Geometry g,
            final Shape s,
            final Paint p,
            final int width,
            final int height) {
        final SignaturedFeature feature = new SignaturedFeature(g);
        // create an image containing the time series shape as overlay icon
        if ((s == null) || (p == null)) {
            return feature;
        }
        final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setPaint(p);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, height / 2, width, height / 2);
        feature.setOverlayIcon(bi);
        // paint the time series symbol
        final AffineTransform saveXform = g2.getTransform();
        final AffineTransform at = new AffineTransform();
        final AffineTransform scaleTrans = new AffineTransform();

        scaleTrans.scale(1.5, 1.5);
        final Shape scaledShape = scaleTrans.createTransformedShape(s);
        final double imageXMittelpunkt = bi.getWidth() / 2;
        final double imageYMittelpunkt = bi.getHeight() / 2;
        final double shapeXMittelpunkt = (scaledShape.getBounds().getWidth() / 2) - 4.5;
        final double shapeYMittelpunkt = (scaledShape.getBounds().getHeight() / 2) - 4.5;
        at.translate(imageXMittelpunkt - (shapeXMittelpunkt), imageYMittelpunkt - (shapeYMittelpunkt));
        g2.transform(at);

        g2.setPaint(p);
        g2.fill(scaledShape);
        g2.transform(saveXform);

        return feature;
    }

    @Override
    public void submitResult(final Collection<TimeSeries> result) {
        for (final TimeSeries ts : result) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        SimpleTSVisualisation.this.addTimeSeries(ts);
                    }
                });
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Customised ToolTipGenerator which shows the x axis value as Date and the y axis value as Number if tool tips are
     * enabled.
     *
     * @version  $Revision$, $Date$
     */
    protected final class DateValueToolTipGenerator implements XYToolTipGenerator {

        //~ Methods ------------------------------------------------------------

        @Override
        public String generateToolTip(final XYDataset dataset, final int series, final int item) {
            String result = "";
            if (dataset instanceof TimeSeriesDatasetAdapter) {
                final TimeSeriesDatasetAdapter tsc = (TimeSeriesDatasetAdapter)dataset;
                final TimeSeries timeseries = tsc.getOriginTimeSeries();
                final String obsProp = (String)timeseries.getTSProperty(TimeSeries.OBSERVEDPROPERTY);
                if (obsProp != null) {
                    result += obsProp + ", ";                                                                    // NOI18N
                }
                result += NbBundle.getMessage(SimpleTSVisualisation.class, "SimpleTSVisualisation.toolTipText"); // NOI18N
                final Double xVal = dataset.getXValue(series, item);
                final Calendar c = new GregorianCalendar();
                c.setTimeInMillis(xVal.longValue());
                result += c.getTime().toString() + " / ";                                                        // NOI18N
                final Double yVal = dataset.getYValue(series, item);
                final DecimalFormat f = new DecimalFormat("##.00");                                              // NOI18N
                result += f.format(yVal.doubleValue());
//                result += yVal;
                final Unit unit = SMSUtils.unitFromTimeseries(timeseries);
                if (unit != null) {
                    result += " " + unit.getLocalisedName();                                                     // NOI18N
                }
            }
            return result;
        }
    }
}
