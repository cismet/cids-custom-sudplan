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
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import org.apache.log4j.Logger;

import org.jfree.chart.JFreeChart;
import org.jfree.util.Log;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.net.MalformedURLException;

import java.util.HashMap;
import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.Controllable;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.TimeSeriesVisualisationFactory;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.VisualisationType;

import de.cismet.cids.dynamics.Disposable;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TimeseriesChartPanel extends javax.swing.JPanel implements Disposable {

    //~ Static fields/initializers ---------------------------------------------
    private static final transient Logger LOG = Logger.getLogger(TimeseriesChartPanel.class);
    //~ Instance fields --------------------------------------------------------
    private final transient HashMap<TimeseriesRetrieverConfig, TimeseriesConverter> configs;
    private transient JFreeChart chart;
    private transient BufferedImage image;
    private transient volatile Boolean cached;
    private final transient Refreshable refreshable;
    // End of variables declaration
    private final transient TimeseriesDisplayer displayer;
    private TimeSeriesVisualisation tsVis;
    // Variables declaration - do not modify
    private javax.swing.JLabel pnlLoading;

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates new form TimeseriesFeatureRenderer.
     *
     * @param   uri  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String uri) throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromUrl(uri), false, null, null);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config) {
        this(config, false, null, null);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param   uri        DOCUMENT ME!
     * @param   converter  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String uri, final TimeseriesConverter converter) throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromUrl(uri), false, null, converter);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config     DOCUMENT ME!
     * @param  converter  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config, final TimeseriesConverter converter) {
        this(config, false, null, converter);
    }

    /**
     * Creates new form TimeseriesFeatureRenderer.
     *
     * @param   uri          DOCUMENT ME!
     * @param   refreshable  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String uri, final Refreshable refreshable) throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromUrl(uri), false, refreshable, null);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config             DOCUMENT ME!
     * @param  cacheImmedialtely  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config, final boolean cacheImmedialtely) {
        this(config, cacheImmedialtely, null, null);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param   configs            DOCUMENT ME!
     * @param   cacheImmedialtely  DOCUMENT ME!
     * @param   refreshable        DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final HashMap<TimeseriesRetrieverConfig, TimeseriesConverter> configs,
            final boolean cacheImmedialtely,
            final Refreshable refreshable) {
        this.configs = configs;
        if (configs == null) {
            throw new IllegalArgumentException("config must not be null"); // NOI18N
        }
        this.cached = cacheImmedialtely;
        this.refreshable = refreshable;
        this.displayer = new TimeseriesDisplayer();

        initComponents();
        initTimeSeriesChart();
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param   config             DOCUMENT ME!
     * @param   cacheImmedialtely  DOCUMENT ME!
     * @param   refreshable        DOCUMENT ME!
     * @param   converter          DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config,
            final boolean cacheImmedialtely,
            final Refreshable refreshable,
            final TimeseriesConverter converter) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null"); // NOI18N
        }

        this.refreshable = refreshable;
        cached = cacheImmedialtely;
        this.configs = new HashMap<TimeseriesRetrieverConfig, TimeseriesConverter>();
        this.configs.put(config, converter);
        displayer = new TimeseriesDisplayer();

        initComponents();
        initTimeSeriesChart();
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * DOCUMENT ME!
     */
    private void initTimeSeriesChart() {
        tsVis = TimeSeriesVisualisationFactory.getInstance().createVisualisation(VisualisationType.SIMPLE);
        final Controllable tsVisController = tsVis.getLookup(Controllable.class);
        tsVisController.enableSelection(false);
        displayer.execute();
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
            final Graphics2D g2 = (Graphics2D) g;

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
        // TODO: proper image size
        image = tsVis.getImage();

        // image was created, dismiss the chart
        chart = null;
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

    @Override
    public void dispose() {
        displayer.cancel(true);
    }

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

            Future<TimeSeries> tsFuture = null;
            int count = 0;
            try {
                for (final TimeseriesRetrieverConfig config : configs.keySet()) {
                    final TimeseriesConverter converter = configs.get(config);
                    tsFuture = TimeseriesRetriever.getInstance().retrieve(config, converter);
                    final TimeSeries timeseries = tsFuture.get();
                    final String name = config.getObsProp();
                    String humanReadableObsProp = "";
                    if (name != null) {
                        final String[] splittedName = name.split(":");
                        humanReadableObsProp = splittedName[splittedName.length - 1];
                    }
                    timeseries.setTSProperty(TimeSeries.OBSERVEDPROPERTY, humanReadableObsProp);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("retrieved timeseries"); // NOI18N
                    }


                    tsVis.addTimeSeries(timeseries);
                    count++;
                }
            } catch (final InterruptedException ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("chartpanel was interrupted, cancelling retriever future", ex); // NOI18N
                }

                final boolean flag = tsFuture.cancel(true);

                if (!flag) {
                    Log.error("Can not abort TimeSeries Retriever");
                }

                throw ex;
            } catch (final Exception ex) {
                LOG.error("cannot create chart", ex); // NOI18N
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
                    final JComponent comp = tsVis.getVisualisationUI();
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
         * @param   ts  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private TimeSeries copyTimeSeries(final TimeSeries ts) {
            final TimeSeries result = new TimeSeriesImpl();
            String valueKey = null;
            // copy properties
            for (final String key : ts.getTSKeys()) {
                result.setTSProperty(key, ts.getTSProperty(key));
                if (key.equals(TimeSeries.VALUE_KEYS)) {
                    if (ts.getTSProperty(key) instanceof String) {
                        valueKey = (String) ts.getTSProperty(key);
                    } else {
                        valueKey = ((String[]) ts.getTSProperty(key))[0];
                    }
                }
            }
            final TimeStamp[] clonedStamps = ts.getTimeStampsArray().clone();

            // copy values
            for (final TimeStamp t : clonedStamps) {
                result.setValue(t, valueKey, ts.getValue(t, valueKey));
            }
            return result;
        }
    }
}
