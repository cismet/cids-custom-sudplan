/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.MenuElement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A CustomChartPanel extends the org.jfree.ChartPanel with the functionality of a scrollbar that makes it possible to
 * scroll the data on time axis. Additional features like multiple time series with multiple axes in one chart,
 * selection / deselcetion and removing time series are also implemented. Time series can be removed by right clicking
 * on a time series shape.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class CustomChartPanel extends ChartPanel implements AxisChangeListener, ChangeListener {

    //~ Instance fields --------------------------------------------------------

    private InnerChartPanel innerChartPanel = new InnerChartPanel();
    private JScrollBar scrollbar;
//    private JSlider slider;
    private final long timeSeriesMinVal;
    private final long timeSeriesMaxVal;
    private final long timeSeriesInterval;
    private final int scrollbar_max_val;
    private boolean ignoreNextStateChangeEvent = false;
    private boolean ignoreNextAxisChangeEvent = false;
    private DateAxis timeAxis;
    private JFreeChart chart;
    private ArrayList<TimeSeriesRemovedListener> listeners = new ArrayList<TimeSeriesRemovedListener>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomChartPanel object.
     *
     * @param  chart  DOCUMENT ME!
     */
    public CustomChartPanel(final JFreeChart chart) {
        super(chart);
        this.chart = chart;
        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        this.innerChartPanel = new InnerChartPanel(chart);
        if (chart.getPlot() instanceof XYPlot) {
            final XYPlot plot = (XYPlot)chart.getPlot();
            if (plot.getDomainAxis() instanceof DateAxis) {
                timeAxis = (DateAxis)plot.getDomainAxis();
                timeAxis.addChangeListener(this);
                ignoreNextStateChangeEvent = true;
                timeAxis.setAutoRange(true);
            }
            plot.getRangeAxis().setAutoRange(true);
            plot.getRangeAxis().addChangeListener(this);
        } else {
            // Log that zooming/scrolling functionality could not configured correctly
        }

        centerPanel.add(innerChartPanel, BorderLayout.CENTER);

        timeSeriesMaxVal = timeAxis.getMaximumDate().getTime();
        timeSeriesMinVal = timeAxis.getMinimumDate().getTime();
        timeSeriesInterval = timeSeriesMaxVal - timeSeriesMinVal;
        scrollbar_max_val = (int)(timeSeriesInterval / Integer.MAX_VALUE);

        scrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollbar.setValues(0, scrollbar_max_val, 0, scrollbar_max_val);
        scrollbar.getModel().addChangeListener(this);
        centerPanel.add(scrollbar, BorderLayout.SOUTH);

//        slider = new JSlider(JSlider.HORIZONTAL);
        // TODO
//        slider.setMinimum(0);
//        slider.setMaximum(scrollbar_max_val);
//        slider.setValue(0);
//        slider.setExtent(scrollbar_max_val);
//        slider.getModel().addChangeListener(this);
//        centerPanel.add(slider, BorderLayout.SOUTH);
//        final TimeSeriesChartToolBar toolbar = new TimeSeriesChartToolBar(this);
        this.setLayout(new BorderLayout());
//        this.add(toolbar, BorderLayout.NORTH);
        this.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  index  DOCUMENT ME!
     */
    public void fireTimeSeriesRemoved(final int index) {
        for (final TimeSeriesRemovedListener l : listeners) {
            l.timeSeriesRemoved(index);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addTimeSeriesRemovedListener(final TimeSeriesRemovedListener l) {
        listeners.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeTimeSeriesRemovedListener(final TimeSeriesRemovedListener l) {
        listeners.remove(l);
    }

    @Override
    public void axisChanged(final AxisChangeEvent event) {
        if (ignoreNextAxisChangeEvent) {
            ignoreNextAxisChangeEvent = false;
            return;
        }
        if (event.getAxis() instanceof DateAxis) {
            final DateAxis axis = (DateAxis)event.getAxis();
            final long minDate = axis.getMinimumDate().getTime();
            final long maxDate = axis.getMaximumDate().getTime();

            /*
             * zoom is limited in fact that BoundedRangemodel is based on int values, so we have to take care that the
             * whole dataset is reachable with scrolling.
             */
            if ((maxDate - minDate) < (timeSeriesInterval / scrollbar_max_val)) {
                // TODO LOG System.out.println("Zu nah ran gezoomt");
                ignoreNextAxisChangeEvent = true;
                axis.setMaximumDate(new Date(minDate + (timeSeriesInterval / scrollbar_max_val)));
                ignoreNextStateChangeEvent = true;
//                slider.setValue(getScaledScrollbarValue(minDate));
//                slider.setExtent(0);
                scrollbar.setValues(getScaledScrollbarValue(minDate),
                    0,
                    scrollbar.getMinimum(),
                    scrollbar.getMaximum());
                return;
            }
            final int axisMin = getScaledScrollbarValue(minDate);
            final int axisMax = getScaledScrollbarValue(maxDate);
            final int extend = axisMax - axisMin;
            ignoreNextStateChangeEvent = true;
//            slider.setValue(axisMin);
//            slider.setMaximum(axisMax);
            scrollbar.setValues(axisMin, extend, scrollbar.getMinimum(), scrollbar.getMaximum());
            return;
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        if (ignoreNextStateChangeEvent) {
            this.invalidate();
            this.validate();
            ignoreNextStateChangeEvent = false;
            return;
        }
        if (e.getSource() instanceof DefaultBoundedRangeModel) {
            final DefaultBoundedRangeModel model = (DefaultBoundedRangeModel)e.getSource();
            // adapt the axis value according the slider position
            final Date minDate = new Date(getOrignalforScaledValue(model.getValue()));
            final long axisInterval = (timeAxis.getMaximumDate().getTime() - timeAxis.getMinimumDate().getTime());
            final Date maxDate = new Date(minDate.getTime() + axisInterval);
            ignoreNextAxisChangeEvent = true;
            timeAxis.setMinimumDate(minDate);
            ignoreNextAxisChangeEvent = true;
            timeAxis.setMaximumDate(maxDate);
            // adjust the range of all value axes

//            final XYPlot plot = (XYPlot)timeAxis.getPlot();
//            for (int i = 0; i < plot.getRangeAxisCount(); i++) {
//                final ValueAxis valueAxis = plot.getRangeAxis(i);
//                if (valueAxis != null) {
//                    ignoreNextAxisChangeEvent = true;
//                    valueAxis.configure();
//                }
//            }
//            innerChartPanel.restoreAutoRangeBounds();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   l  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int getScaledScrollbarValue(final long l) {
        return (int)(((l - timeSeriesMinVal) * scrollbar_max_val) / timeSeriesInterval);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   i  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private long getOrignalforScaledValue(final int i) {
        return ((i * timeSeriesInterval / scrollbar_max_val) + timeSeriesMinVal);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        innerChartPanel.actionPerformed(event);
    }

    @Override
    public void addChartMouseListener(final ChartMouseListener listener) {
        innerChartPanel.addChartMouseListener(listener);
    }

    @Override
    public void addOverlay(final Overlay overlay) {
        innerChartPanel.addOverlay(overlay);
    }

    @Override
    public void chartChanged(final ChartChangeEvent event) {
        innerChartPanel.chartChanged(event);
    }

    @Override
    public void chartProgress(final ChartProgressEvent event) {
        innerChartPanel.chartProgress(event);
    }

    @Override
    public void createChartPrintJob() {
        innerChartPanel.createChartPrintJob();
    }

    @Override
    protected JPopupMenu createPopupMenu(final boolean properties,
            final boolean save,
            final boolean print,
            final boolean zoom) {
        return innerChartPanel.createPopupMenu(properties, save, print, zoom);
    }

    @Override
    protected JPopupMenu createPopupMenu(final boolean properties,
            final boolean copy,
            final boolean save,
            final boolean print,
            final boolean zoom) {
        if (innerChartPanel != null) {
            return innerChartPanel.createPopupMenu(properties, copy, save, print, zoom);
        }
        return null;
    }

    @Override
    protected void displayPopupMenu(final int x, final int y) {
        innerChartPanel.displayPopupMenu(x, y);
    }

    @Override
    public void doCopy() {
        innerChartPanel.doCopy();
    }

    @Override
    public void doEditChartProperties() {
        innerChartPanel.doEditChartProperties();
    }

    @Override
    public void doSaveAs() throws IOException {
        innerChartPanel.doSaveAs();
    }

    @Override
    public Point2D getAnchor() {
        return innerChartPanel.getAnchor();
    }

    @Override
    public JFreeChart getChart() {
        return innerChartPanel.getChart();
    }

    @Override
    public ChartRenderingInfo getChartRenderingInfo() {
        return innerChartPanel.getChartRenderingInfo();
    }

    @Override
    public File getDefaultDirectoryForSaveAs() {
        return innerChartPanel.getDefaultDirectoryForSaveAs();
    }

    @Override
    public int getDismissDelay() {
        return innerChartPanel.getDismissDelay();
    }

    @Override
    public ChartEntity getEntityForPoint(final int viewX, final int viewY) {
        return innerChartPanel.getEntityForPoint(viewX, viewY);
    }

    @Override
    public boolean getFillZoomRectangle() {
        return innerChartPanel.getFillZoomRectangle();
    }

    @Override
    public boolean getHorizontalAxisTrace() {
        return innerChartPanel.getHorizontalAxisTrace();
    }

    @Override
    protected Line2D getHorizontalTraceLine() {
        return innerChartPanel.getHorizontalTraceLine();
    }

    @Override
    public int getInitialDelay() {
        return innerChartPanel.getInitialDelay();
    }

    @Override
    public EventListener[] getListeners(final Class listenerType) {
        return innerChartPanel.getListeners(listenerType);
    }

    @Override
    public int getMaximumDrawHeight() {
        return innerChartPanel.getMaximumDrawHeight();
    }

    @Override
    public int getMaximumDrawWidth() {
        return innerChartPanel.getMaximumDrawWidth();
    }

    @Override
    public int getMinimumDrawHeight() {
        return innerChartPanel.getMinimumDrawHeight();
    }

    @Override
    public int getMinimumDrawWidth() {
        return innerChartPanel.getMinimumDrawWidth();
    }

    @Override
    public JPopupMenu getPopupMenu() {
        return innerChartPanel.getPopupMenu();
    }

    @Override
    public boolean getRefreshBuffer() {
        return innerChartPanel.getRefreshBuffer();
    }

    @Override
    public int getReshowDelay() {
        return innerChartPanel.getReshowDelay();
    }

    @Override
    public double getScaleX() {
        return innerChartPanel.getScaleX();
    }

    @Override
    public double getScaleY() {
        return innerChartPanel.getScaleY();
    }

    @Override
    public Rectangle2D getScreenDataArea() {
        return innerChartPanel.getScreenDataArea();
    }

    @Override
    public Rectangle2D getScreenDataArea(final int x, final int y) {
        return innerChartPanel.getScreenDataArea(x, y);
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        return innerChartPanel.getToolTipText(e);
    }

    @Override
    public boolean getVerticalAxisTrace() {
        return innerChartPanel.getVerticalAxisTrace();
    }

    @Override
    protected Line2D getVerticalTraceLine() {
        return innerChartPanel.getVerticalTraceLine();
    }

    @Override
    public boolean getZoomAroundAnchor() {
        return innerChartPanel.getZoomAroundAnchor();
    }

    @Override
    public Paint getZoomFillPaint() {
        return innerChartPanel.getZoomFillPaint();
    }

    @Override
    public double getZoomInFactor() {
        return innerChartPanel.getZoomInFactor();
    }

    @Override
    public double getZoomOutFactor() {
        return innerChartPanel.getZoomOutFactor();
    }

    @Override
    public Paint getZoomOutlinePaint() {
        return innerChartPanel.getZoomOutlinePaint();
    }

    @Override
    public int getZoomTriggerDistance() {
        return innerChartPanel.getZoomTriggerDistance();
    }

    @Override
    public boolean isDomainZoomable() {
        return innerChartPanel.isDomainZoomable();
    }

    @Override
    public boolean isEnforceFileExtensions() {
        return innerChartPanel.isEnforceFileExtensions();
    }

    @Override
    public boolean isMouseWheelEnabled() {
        return innerChartPanel.isMouseWheelEnabled();
    }

    @Override
    public boolean isRangeZoomable() {
        return innerChartPanel.isRangeZoomable();
    }

    @Override
    public void mouseClicked(final MouseEvent event) {
        innerChartPanel.mouseClicked(event);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        innerChartPanel.mouseDragged(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        innerChartPanel.mouseEntered(e);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        innerChartPanel.mouseExited(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        innerChartPanel.mouseMoved(e);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        innerChartPanel.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        innerChartPanel.mouseReleased(e);
    }

    @Override
    public void overlayChanged(final OverlayChangeEvent event) {
        innerChartPanel.overlayChanged(event);
    }

    @Override
    public void paintComponent(final Graphics g) {
        innerChartPanel.paintComponent(g);
    }

    @Override
    public int print(final Graphics g, final PageFormat pf, final int pageIndex) {
        return innerChartPanel.print(g, pf, pageIndex);
    }

    @Override
    public void removeChartMouseListener(final ChartMouseListener listener) {
        innerChartPanel.removeChartMouseListener(listener);
    }

    @Override
    public void removeOverlay(final Overlay overlay) {
        innerChartPanel.removeOverlay(overlay);
    }

    @Override
    public void restoreAutoBounds() {
        innerChartPanel.restoreAutoBounds();
    }

    @Override
    public void restoreAutoDomainBounds() {
        innerChartPanel.restoreAutoDomainBounds();
    }

    @Override
    public void restoreAutoRangeBounds() {
        innerChartPanel.restoreAutoRangeBounds();
    }

    @Override
    public Rectangle2D scale(final Rectangle2D rect) {
        return innerChartPanel.scale(rect);
    }

    @Override
    protected void setAnchor(final Point2D anchor) {
        innerChartPanel.setAnchor(anchor);
    }

    @Override
    public void setChart(final JFreeChart chart) {
        if (innerChartPanel != null) {
            innerChartPanel.setChart(chart);
        }
    }

    @Override
    public void setDefaultDirectoryForSaveAs(final File directory) {
        innerChartPanel.setDefaultDirectoryForSaveAs(directory);
    }

    @Override
    public void setDismissDelay(final int delay) {
        innerChartPanel.setDismissDelay(delay);
    }

    @Override
    public void setDisplayToolTips(final boolean flag) {
        if (innerChartPanel != null) {
            innerChartPanel.setDisplayToolTips(flag);
        }
    }

    @Override
    public void setDomainZoomable(final boolean flag) {
        innerChartPanel.setDomainZoomable(flag);
    }

    @Override
    public void setEnforceFileExtensions(final boolean enforce) {
        innerChartPanel.setEnforceFileExtensions(enforce);
    }

    @Override
    public void setFillZoomRectangle(final boolean flag) {
        innerChartPanel.setFillZoomRectangle(flag);
    }

    @Override
    public void setHorizontalAxisTrace(final boolean flag) {
        innerChartPanel.setHorizontalAxisTrace(flag);
    }

    @Override
    protected void setHorizontalTraceLine(final Line2D line) {
        innerChartPanel.setHorizontalTraceLine(line);
    }

    @Override
    public void setInitialDelay(final int delay) {
        innerChartPanel.setInitialDelay(delay);
    }

    @Override
    public void setMaximumDrawHeight(final int height) {
        innerChartPanel.setMaximumDrawHeight(height);
    }

    @Override
    public void setMaximumDrawWidth(final int width) {
        innerChartPanel.setMaximumDrawWidth(width);
    }

    @Override
    public void setMinimumDrawHeight(final int height) {
        innerChartPanel.setMinimumDrawHeight(height);
    }

    @Override
    public void setMinimumDrawWidth(final int width) {
        innerChartPanel.setMinimumDrawWidth(width);
    }

    @Override
    public void setMouseWheelEnabled(final boolean flag) {
        innerChartPanel.setMouseWheelEnabled(flag);
    }

    @Override
    public void setMouseZoomable(final boolean flag) {
        innerChartPanel.setMouseZoomable(flag);
    }

    @Override
    public void setMouseZoomable(final boolean flag, final boolean fillRectangle) {
        innerChartPanel.setMouseZoomable(flag, fillRectangle);
    }

    @Override
    public void setPopupMenu(final JPopupMenu popup) {
        innerChartPanel.setPopupMenu(popup);
    }

    @Override
    public void setRangeZoomable(final boolean flag) {
        innerChartPanel.setRangeZoomable(flag);
    }

    @Override
    public void setRefreshBuffer(final boolean flag) {
        innerChartPanel.setRefreshBuffer(flag);
    }

    @Override
    public void setReshowDelay(final int delay) {
        innerChartPanel.setReshowDelay(delay);
    }

    @Override
    public void setVerticalAxisTrace(final boolean flag) {
        innerChartPanel.setVerticalAxisTrace(flag);
    }

    @Override
    protected void setVerticalTraceLine(final Line2D line) {
        innerChartPanel.setVerticalTraceLine(line);
    }

    @Override
    public void setZoomAroundAnchor(final boolean zoomAroundAnchor) {
        innerChartPanel.setZoomAroundAnchor(zoomAroundAnchor);
    }

    @Override
    public void setZoomFillPaint(final Paint paint) {
        innerChartPanel.setZoomFillPaint(paint);
    }

    @Override
    public void setZoomInFactor(final double factor) {
        innerChartPanel.setZoomInFactor(factor);
    }

    @Override
    public void setZoomOutFactor(final double factor) {
        innerChartPanel.setZoomOutFactor(factor);
    }

    @Override
    public void setZoomOutlinePaint(final Paint paint) {
        innerChartPanel.setZoomOutlinePaint(paint);
    }

    @Override
    public void setZoomTriggerDistance(final int distance) {
        innerChartPanel.setZoomTriggerDistance(distance);
    }

    @Override
    public Point translateJava2DToScreen(final Point2D java2DPoint) {
        return innerChartPanel.translateJava2DToScreen(java2DPoint);
    }

    @Override
    public Point2D translateScreenToJava2D(final Point screenPoint) {
        return innerChartPanel.translateScreenToJava2D(screenPoint);
    }

    @Override
    public void updateUI() {
        if (innerChartPanel != null) {
            innerChartPanel.updateUI();
        }
    }

    @Override
    public void zoom(final Rectangle2D selection) {
        innerChartPanel.zoom(selection);
    }

    @Override
    public void zoomInBoth(final double x, final double y) {
        innerChartPanel.zoomInBoth(x, y);
    }

    @Override
    public void zoomInDomain(final double x, final double y) {
        innerChartPanel.zoomInDomain(x, y);
    }

    @Override
    public void zoomInRange(final double x, final double y) {
        innerChartPanel.zoomInRange(x, y);
    }

    @Override
    public void zoomOutBoth(final double x, final double y) {
        innerChartPanel.zoomOutBoth(x, y);
    }

    @Override
    public void zoomOutDomain(final double x, final double y) {
        innerChartPanel.zoomOutDomain(x, y);
    }

    @Override
    public void zoomOutRange(final double x, final double y) {
        innerChartPanel.zoomOutRange(x, y);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class InnerChartPanel extends ChartPanel {

        //~ Instance fields ----------------------------------------------------

        private JPopupMenu removeTimeSeriesMenue;
        private JPopupMenu origMenu;
        private final JMenuItem removeItem;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new InnerChartPanel object.
         *
         * @param  chart  DOCUMENT ME!
         */
        public InnerChartPanel(final JFreeChart chart) {
            super(chart);
            removeTimeSeriesMenue = new JPopupMenu();
            origMenu = getPopupMenu();

            // TODO make the customized popup menue look like the default (seperators)
            final MenuElement[] elements = origMenu.getSubElements();
            final MenuElement[] clonedElements = elements.clone();
            for (int i = 0; i < elements.length; i++) {
                removeTimeSeriesMenue.add((JMenuItem)clonedElements[i]);
            }

            removeItem = new JMenuItem();
            removeItem.setText("Remove");
            removeTimeSeriesMenue.addSeparator();
            removeTimeSeriesMenue.add(removeItem);

            origMenu = createPopupMenu(true, true, true, true);
        }

        /**
         * Creates a new InnerChartPanel object.
         */
        private InnerChartPanel() {
            this(new JFreeChart(new XYPlot()));
        }

        //~ Methods ------------------------------------------------------------

        // Popup menu must be declared in mousePressed and Mouse release in fact of different PopupTrigger for different
        // L&F's
        @Override
        public void mousePressed(final MouseEvent e) {
            super.mousePressed(e);
            final ChartEntity entity = getEntityforMouseClick(e);
            // If the mouseclick was on an time series create a customized popup menu
            if (e.isPopupTrigger()) {
                createCustomizedPopup(entity, e);
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            super.mouseReleased(e);
            final ChartEntity entity = getEntityforMouseClick(e);

            // If the mouseclick was on an time series create a customized popup menu
            if (e.isPopupTrigger()) {
                createCustomizedPopup(entity, e);
            }
        }

        @Override
        public JPopupMenu createPopupMenu(final boolean properties,
                final boolean save,
                final boolean print,
                final boolean zoom) {
            return super.createPopupMenu(properties, save, print, zoom);
        }

        @Override
        public JPopupMenu createPopupMenu(final boolean properties,
                final boolean copy,
                final boolean save,
                final boolean print,
                final boolean zoom) {
            return super.createPopupMenu(properties, copy, save, print, zoom);
        }

        @Override
        public void displayPopupMenu(final int x, final int y) {
            super.displayPopupMenu(x, y);
        }

        @Override
        public Line2D getHorizontalTraceLine() {
            return super.getHorizontalTraceLine();
        }

        @Override
        public Line2D getVerticalTraceLine() {
            return super.getVerticalTraceLine();
        }

        @Override
        public void setAnchor(final Point2D anchor) {
            super.setAnchor(anchor);
        }

        @Override
        public void setHorizontalTraceLine(final Line2D line) {
            super.setHorizontalTraceLine(line);
        }

        @Override
        public void setVerticalTraceLine(final Line2D line) {
            super.setVerticalTraceLine(line);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  entity  DOCUMENT ME!
         * @param  e       DOCUMENT ME!
         */
        protected void createCustomizedPopup(final ChartEntity entity, final MouseEvent e) {
            final XYPlot plot = (XYPlot)this.getChart().getPlot();

            // quick&dirty fix the problem, thast datasetCount also count null references.
            int nonNullDatasetCount = 0;
            final HashMap<Integer, TimeSeriesCollection> selectedTimeSeries =
                new HashMap<Integer, TimeSeriesCollection>();
            for (int i = 0; i < plot.getDatasetCount(); i++) {
                if (plot.getDataset(i) != null) {
                    nonNullDatasetCount++;
                    if (plot.getRenderer(i) instanceof SelectionXYLineRenderer) {
                        final SelectionXYLineRenderer renderer = (SelectionXYLineRenderer)plot.getRenderer(i);
                        if (renderer.isSelected()) {
                            selectedTimeSeries.put(new Integer(i), (TimeSeriesCollection)plot.getDataset(i));
                        }
                    }
                }
            }

            // popup on time series with remove action if is not the last one
            if ((nonNullDatasetCount > 1) && (entity != null) && (entity instanceof XYItemEntity)) {
                final XYItemEntity xyEntity = (XYItemEntity)entity;
                final TimeSeriesCollection tsc = (TimeSeriesCollection)xyEntity.getDataset();

                final RemoveTimeSeriesAction removeAction = new RemoveTimeSeriesAction(
                        tsc,
                        (XYPlot)this.getChart().getPlot(),
                        CustomChartPanel.this);
                removeItem.setAction(removeAction);
                removeTimeSeriesMenue.show(e.getComponent(), e.getX(), e.getY());
            } else {
                origMenu.show(e.getComponent(), e.getX(), e.getY());
                return;
            }
        }

        @Override
        public void zoom(final Rectangle2D selection) {
            // just zoom up to the limit
            super.zoom(selection);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   e  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        protected ChartEntity getEntityforMouseClick(final MouseEvent e) {
            if (getChartRenderingInfo() != null) {
                final EntityCollection entities = getChartRenderingInfo().getEntityCollection();
                if (entities != null) {
                    return entities.getEntity(e.getX(), e.getY());
                }
            }
            return null;
        }
    }
}
