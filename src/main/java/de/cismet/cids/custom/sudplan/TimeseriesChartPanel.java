/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.RectangleInsets;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.net.MalformedURLException;

import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TimeseriesChartPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesChartPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final transient TimeseriesRetrieverConfig config;

    private transient JFreeChart chart;
    private transient BufferedImage image;

    private transient volatile Boolean cached;
    private final transient Refreshable refreshable;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel pnlLoading;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TimeseriesFeatureRenderer.
     *
     * @param   tstburi  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String tstburi) throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromTSTBUrl(tstburi), false, null);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config) {
        this(config, false);
    }

    /**
     * Creates new form TimeseriesFeatureRenderer.
     *
     * @param   tstburi      DOCUMENT ME!
     * @param   refreshable  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String tstburi, final Refreshable refreshable) throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromTSTBUrl(tstburi), false, refreshable);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config             DOCUMENT ME!
     * @param  cacheImmedialtely  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config, final boolean cacheImmedialtely) {
        this(config, cacheImmedialtely, null);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param   config             DOCUMENT ME!
     * @param   cacheImmedialtely  DOCUMENT ME!
     * @param   refreshable        DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config,
            final boolean cacheImmedialtely,
            final Refreshable refreshable) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null"); // NOI18N
        }

        this.refreshable = refreshable;
        this.config = config;

        validateConfig();

        initComponents();

        cached = cacheImmedialtely;

        new TimeseriesDisplayer().execute();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void validateConfig() {
        if (config.getObservedProperty() == null) {
            throw new IllegalStateException("config must contain an observed property"); // NOI18N
        }
    }

    /**
     * Caches the chart and repaints itself. Caching means that the chart is transformed into an image so after this
     * method has been called once successfully no manipulation can be done with the chart anymore. It has turned into
     * an image. If the chart has already been cached nothing will be done.<br/>
     * <br/>
     * NOTE: This method does a synchronized(this) until the caching is finished
     *
     * @throws  IOException  if an error occurs during caching
     */
    public void cache() throws IOException {
        if (!cached) {
            synchronized (this) {
                if (!cached) {
                    performCaching();
                    cached = true;

                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                repaint();
                            }
                        });
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  g  DOCUMENT ME!
     */
    @Override
    public void paint(final Graphics g) {
        if (cached) {
            final Graphics2D g2 = (Graphics2D)g;

            assert image != null : "image is null"; // NOI18N

            g2.drawImage(image, null, 0, 0);
        } else {
            super.paint(g);
        }
    }

    /**
     * Returns the cached state of this <code>TimeseriesChartPanel</code>.
     *
     * @return  true if the chart is cached, false otherwise
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void performCaching() throws IOException {
        PipedOutputStream pos = null;
        BufferedInputStream bis = null;
        try {
            pos = new PipedOutputStream();
            bis = new BufferedInputStream(new PipedInputStream(pos));

            // TODO: proper image size
            ChartUtilities.writeChartAsPNG(pos, chart, 640, 480, true, 1);

            image = ImageIO.read(bis);

            // image was created, dismiss the chart
            chart = null;
        } finally {
            if (pos != null) {
                pos.close();
            }

            if (bis != null) {
                bis.close();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlLoading = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlLoading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlLoading.setText(NbBundle.getMessage(TimeseriesChartPanel.class, "TimeseriesChartPanel.pnlLoading.text")); // NOI18N
        add(pnlLoading, java.awt.BorderLayout.CENTER);
    }                                                                                                                // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class TimeseriesDisplayer extends SwingWorker<Void, Void> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Void doInBackground() throws Exception {
            if (LOG.isDebugEnabled()) {
                LOG.debug("creating timeseries chart"); // NOI18N
            }

            try {
                final Future<TimeSeries> timeseriesFuture = TimeseriesRetriever.getInstance().retrieve(config);
                final TimeSeries timeseries = timeseriesFuture.get();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("retrieved timeseries");
                }

                final IntervalXYDataset dataset = createDataset(timeseries, config.getObsProp());

                chart = createChart(dataset, SMSUtils.unitFromTimeseries(timeseries));
            } catch (final Exception ex) {
                LOG.error("cannot create chart", ex);
                throw ex;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("timeseries chart is ready"); // NOI18N
            }

            if (cached) {
                performCaching();
            }

            return null;
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                if (!cached) {
                    final JComponent comp = new ChartPanel(chart, true);
                    remove(pnlLoading);
                    add(comp, BorderLayout.CENTER);

                    Container parent = TimeseriesChartPanel.this;
                    Container current = getParent();
                    while (current != null) {
                        parent = current;
                        current = parent.getParent();
                    }
                    parent.invalidate();
                    parent.validate();
                    CismapBroker.getInstance().getMappingComponent().rescaleStickyNodes();
                }
            } catch (final Exception e) {
                final String message = "cannot create chart";  // NOI18N
                LOG.error(message, e);
                add(new JLabel("ERROR"), BorderLayout.CENTER); // NOI18N
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   timeseries  DOCUMENT ME!
         * @param   name        DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  IllegalStateException  DOCUMENT ME!
         */
        private IntervalXYDataset createDataset(final TimeSeries timeseries, final String name) {
            final Object valueKeyObject = timeseries.getTSProperty(TimeSeries.VALUE_KEYS);

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

            final TimeStamp[] timeStamps = timeseries.getTimeStampsArray(); // getTimeStamps();
            final org.jfree.data.time.TimeSeries data = new org.jfree.data.time.TimeSeries(name);

            for (final TimeStamp ts : timeStamps) {
                final Object value = timeseries.getValue(ts, valueKey);

                if (!(value instanceof Number)) {
                    throw new IllegalStateException("illegal value, can only display numbers: " + value); // NOI18N
                }

                data.add(new Minute(ts.asDate()), (Number)value);
            }

            final TimeSeriesCollection collection = new TimeSeriesCollection(data);

            return collection;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   dataset  DOCUMENT ME!
         * @param   unit     DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  IllegalStateException  DOCUMENT ME!
         */
        private JFreeChart createChart(final IntervalXYDataset dataset, final Unit unit) {
            final JFreeChart chart;
            final XYItemRenderer renderer;
            final Variable observed = config.getObservedProperty();
            if (Variable.PRECIPITATION.equals(observed)) {
                chart = ChartFactory.createXYBarChart(
                        "Rainfall data",
                        "Time",
                        true,
                        unit.getLocalisedName(),
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false);
                // TODO: create sampling bar renderer
                renderer = new SamplingXYLineRenderer();
            } else if (Variable.TEMPERATURE.equals(observed)) {
                chart = ChartFactory.createTimeSeriesChart(
                        "Timeseries data",
                        "Time",
                        unit.getLocalisedName(),
                        dataset,
                        true,
                        true,
                        false);
                renderer = new SamplingXYLineRenderer();
            } else {
                throw new IllegalStateException("unsupported variable: " + observed);
            }

            chart.setBackgroundPaint(getBackground());

            final XYPlot plot = (XYPlot)chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.BLUE);
            plot.setRangeGridlinePaint(Color.BLUE);
            plot.setAxisOffset(new RectangleInsets(15d, 15d, 15d, 15d));
            plot.setDomainCrosshairVisible(true);
            plot.setRangeCrosshairVisible(true);
            plot.setRenderer(renderer);

            return chart;
        }
    }
}
