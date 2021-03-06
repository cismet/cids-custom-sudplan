/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.ui.ComponentRegistry;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.log4j.Logger;

import org.jfree.chart.JFreeChart;
import org.jfree.util.Log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.net.MalformedURLException;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import de.cismet.cids.custom.objectrenderer.sudplan.TimeSeriesRendererUtil;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.Controllable;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.TimeSeriesChartToolBar;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.TimeSeriesVisualisationFactory;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.VisualisationType;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.ShowOrigTimeseriesListener;

import de.cismet.cids.dynamics.Disposable;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TimeseriesChartPanel extends javax.swing.JPanel implements Disposable, ShowOrigTimeseriesListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesChartPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final transient HashMap<TimeseriesRetrieverConfig, TimeseriesConverter> configs;
    private final transient HashMap<TimeSeries, TimeseriesRetrieverConfig> tsMap =
        new HashMap<TimeSeries, TimeseriesRetrieverConfig>();
    private transient JFreeChart chart;
    private transient BufferedImage image;
    private transient volatile Boolean cached;
    private final transient Refreshable refreshable;
    private final transient TimeseriesDisplayer displayer;
    private TimeSeriesVisualisation tsVis;
    private JPanel pnlToolbar = new JPanel();
    private boolean showPrevRes;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.sudplan.LoadingLabel lblLoading;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TimeseriesFeatureRenderer.
     *
     * @param   uri                    DOCUMENT ME!
     * @param   showPreviewResolution  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String uri, final boolean showPreviewResolution) throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromUrl(uri), false, null, null, showPreviewResolution);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config                 DOCUMENT ME!
     * @param  showPreviewResolution  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config, final boolean showPreviewResolution) {
        this(config, false, null, null, showPreviewResolution);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param   uri                    DOCUMENT ME!
     * @param   converter              DOCUMENT ME!
     * @param   showPreviewResolution  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String uri,
            final TimeseriesConverter converter,
            final boolean showPreviewResolution) throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromUrl(uri), false, null, converter, showPreviewResolution);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config                 DOCUMENT ME!
     * @param  converter              DOCUMENT ME!
     * @param  showPreviewResolution  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config,
            final TimeseriesConverter converter,
            final boolean showPreviewResolution) {
        this(config, false, null, converter, showPreviewResolution);
    }

    /**
     * Creates new form TimeseriesFeatureRenderer.
     *
     * @param   uri                    DOCUMENT ME!
     * @param   refreshable            DOCUMENT ME!
     * @param   showPreviewResolution  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final String uri, final Refreshable refreshable, final boolean showPreviewResolution)
            throws MalformedURLException {
        this(TimeseriesRetrieverConfig.fromUrl(uri), false, refreshable, null, showPreviewResolution);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param  config                 DOCUMENT ME!
     * @param  cacheImmedialtely      DOCUMENT ME!
     * @param  showPreviewResolution  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config,
            final boolean cacheImmedialtely,
            final boolean showPreviewResolution) {
        this(config, cacheImmedialtely, null, null, showPreviewResolution);
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param   configs                DOCUMENT ME!
     * @param   cacheImmedialtely      DOCUMENT ME!
     * @param   refreshable            DOCUMENT ME!
     * @param   showPreviewResolution  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final HashMap<TimeseriesRetrieverConfig, TimeseriesConverter> configs,
            final boolean cacheImmedialtely,
            final Refreshable refreshable,
            final boolean showPreviewResolution) {
        this.configs = configs;
        if (configs == null) {
            throw new IllegalArgumentException("config must not be null"); // NOI18N
        }
        this.showPrevRes = showPreviewResolution;
        this.cached = cacheImmedialtely;
        this.refreshable = refreshable;
        this.displayer = new TimeseriesDisplayer();
        initComponents();
        initTimeSeriesChart();
    }

    /**
     * Creates a new TimeseriesChartPanel object.
     *
     * @param   config                 DOCUMENT ME!
     * @param   cacheImmediately       DOCUMENT ME!
     * @param   refreshable            DOCUMENT ME!
     * @param   converter              DOCUMENT ME!
     * @param   showPreviewResolution  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public TimeseriesChartPanel(final TimeseriesRetrieverConfig config,
            final boolean cacheImmediately,
            final Refreshable refreshable,
            final TimeseriesConverter converter,
            final boolean showPreviewResolution) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null"); // NOI18N
        }
        this.showPrevRes = showPreviewResolution;
        this.refreshable = refreshable;
        cached = cacheImmediately;
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
//        tsVisController.enableSelection(false);
        tsVisController.enableContextMenu(true);
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
        // TODO: proper image size
// image = tsVis.getImage();

        // image was created, dismiss the chart
        chart = null;
    }

    /**
     * DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        lblLoading = new de.cismet.cids.custom.sudplan.LoadingLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
        add(lblLoading, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void dispose() {
        displayer.cancel(true);
        lblLoading.dispose();
    }

    @Override
    public void showOrigTS(final TimeSeries ts) {
        final TimeseriesRetrieverConfig cfg = tsMap.get(ts);

        tsVis.removeTimeSeries(ts);
        this.removeAll();
        this.initComponents();
        final OriginalTimeSeriesLoader loader = new OriginalTimeSeriesLoader(cfg);
        loader.execute();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class OriginalTimeSeriesLoader extends SwingWorker<Void, Void> {

        //~ Instance fields ----------------------------------------------------

        private TimeseriesRetrieverConfig config;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new OriginalTimeSeriesLoader object.
         *
         * @param  cfg  DOCUMENT ME!
         */
        public OriginalTimeSeriesLoader(final TimeseriesRetrieverConfig cfg) {
            this.config = cfg;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Void doInBackground() throws Exception {
            Future<TimeSeries> tsFuture = null;
            try {
                final TimeseriesConverter converter;
                if (TimeseriesRetrieverConfig.PROTOCOL_DAV.equals(config.getProtocol())) {
                    converter = TimeSeriesSerializer.getInstance();
                } else {
                    converter = configs.get(config);
                }

                // set preview resolution

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
                    LOG.debug("retrieved timeseries");                                        // NOI18N
                }
                tsMap.put(timeseries, config);
                tsVis.addTimeSeries(timeseries);
//                    count++;
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

        @Override
        protected void done() {
            try {
                if (!cached) {
                    final JComponent comp = tsVis.getVisualisationUI();
                    comp.setBorder(new LineBorder(Color.black, 1));
                    remove(lblLoading);
                    lblLoading.dispose();
                    final java.awt.GridBagConstraints gridBagConstraints;
                    gridBagConstraints = new java.awt.GridBagConstraints();
                    gridBagConstraints.gridx = 0;
                    gridBagConstraints.gridy = 1;
                    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                    gridBagConstraints.weightx = 1.0;
                    gridBagConstraints.weighty = 1.0;
                    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
                    add(comp, gridBagConstraints);

                    pnlToolbar.setMinimumSize(new java.awt.Dimension(10, 32));
                    pnlToolbar.setOpaque(false);
                    pnlToolbar.setPreferredSize(new java.awt.Dimension(10, 32));
                    pnlToolbar.setLayout(new java.awt.BorderLayout());
                    gridBagConstraints.gridx = 0;
                    gridBagConstraints.gridy = 0;
                    gridBagConstraints.weightx = 0;
                    gridBagConstraints.weighty = 0;
                    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
                    add(pnlToolbar, gridBagConstraints);
                    final TimeSeriesChartToolBar toolbar = (TimeSeriesChartToolBar)tsVis.getToolbar();
                    toolbar.enableMapButton(false);
                    toolbar.enableOperationsMenue(false);
                    toolbar.setOpaque(false);

                    toolbar.addShowOrigTSListener(TimeseriesChartPanel.this);
                    pnlToolbar.add(toolbar, BorderLayout.WEST);

                    final Controllable tsVisController = tsVis.getLookup(Controllable.class);
                    tsVisController.enableContextMenu(true);
                    tsVisController.enableToolTips(true);

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
                setLayout(new BorderLayout());
                add(new JLabel("ERROR"), BorderLayout.CENTER); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class TimeseriesDisplayer extends SwingWorker<Void, Void> {

        //~ Instance fields ----------------------------------------------------

        private boolean showOrigButtonNeeded = false;
        private final Pattern regex = Pattern.compile(".*prec:(\\d+[YMs])");

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
            Resolution resolution = null;
            TimeseriesRetrieverConfig config = null;
            int count = 0;
            try {
                for (final TimeseriesRetrieverConfig tmp : configs.keySet()) {
                    final TimeseriesConverter converter;
                    config = tmp;
                    // set preview resolution
                    final String procedure = tmp.getProcedure();
                    final Matcher m = regex.matcher(procedure);

                    if (m.matches()) {
                        final String precision = m.group(1);
                        if ((precision.equals(Resolution.DAY.getPrecision())
                                        || precision.equals(Resolution.HOUR.getPrecision()))) {
                            showOrigButtonNeeded = true;
                        }
                    } else {
                        showOrigButtonNeeded = true;
                    }

                    // if TimeSeries data is located on DAV, we can assume that it is encoded in the
                    // internal format (TimeSeriesSerializer)
                    if (TimeseriesRetrieverConfig.PROTOCOL_DAV.equals(config.getProtocol())) {
                        converter = TimeSeriesSerializer.getInstance();
                    } else {
                        converter = configs.get(config);
                    }

                    resolution = TimeSeriesRendererUtil.getPreviewResolution(config);
                    if (TimeseriesChartPanel.this.showPrevRes && (resolution != null)) {
                        config = config.changeResolution(resolution);
                    }

                    tsFuture = TimeseriesRetriever.getInstance().retrieve(config, converter);
                    final TimeSeries timeseries = tsFuture.get();
                    final String name = config.getObsProp();

                    String humanReadableObsProp = "";
                    final Variable var = config.getObservedProperty();
                    if ((name != null) && ((var == null) || (var == Variable.UNKNOWN))) {
                        final String[] splittedName = name.split(":");
                        humanReadableObsProp = splittedName[splittedName.length - 1];
                    } else if (var != null) {
                        humanReadableObsProp = config.getObservedProperty().getLocalisedName();
                    }

                    timeseries.setTSProperty(TimeSeries.OBSERVEDPROPERTY, humanReadableObsProp);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("retrieved timeseries"); // NOI18N
                    }
                    tsMap.put(timeseries, tmp);
                    tsVis.addTimeSeries(timeseries);
                    count++;
                }
            } catch (final IllegalStateException e) {
                if (resolution == null) {
                    LOG.error("An error occured while retrieving original TimeSeries", e); // NOI18N
                    throw e;
                } else {
                    // most likely, there is no TimeSeries with the specified resolution
                    LOG.warn("An error occured while retrieving TimeSeries with resolution " + resolution, e); // NOI18N
                    final int answer = JOptionPane.showConfirmDialog(
                            ComponentRegistry.getRegistry().getMainWindow(),
                            MessageFormat.format(
                                java.util.ResourceBundle.getBundle(
                                    "de/cismet/cids/custom/objectrenderer/sudplan/Bundle").getString(
                                    "TimeseriesRenderer.setTimeSeriesPanel(Resolution).JOptionPane.message"),  // NOI18N
                                resolution.getLocalisedName()),
                            java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle")
                                        .getString(
                                            "TimeseriesRenderer.setTimeSeriesPanel(Resolution).JOptionPane.title"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (answer == JOptionPane.YES_OPTION) {
                        final OriginalTimeSeriesLoader loader = new OriginalTimeSeriesLoader(config);
                        loader.execute();
                    }
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
                    comp.setBorder(new LineBorder(Color.black, 1));
                    remove(lblLoading);
                    lblLoading.dispose();
                    final java.awt.GridBagConstraints gridBagConstraints;
                    gridBagConstraints = new java.awt.GridBagConstraints();
                    gridBagConstraints.gridx = 0;
                    gridBagConstraints.gridy = 1;
                    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                    gridBagConstraints.weightx = 1.0;
                    gridBagConstraints.weighty = 1.0;
                    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
                    add(comp, gridBagConstraints);

                    pnlToolbar.setMinimumSize(new java.awt.Dimension(10, 32));
                    pnlToolbar.setOpaque(false);
                    pnlToolbar.setPreferredSize(new java.awt.Dimension(10, 32));
                    pnlToolbar.setLayout(new java.awt.BorderLayout());
                    gridBagConstraints.gridx = 0;
                    gridBagConstraints.gridy = 0;
                    gridBagConstraints.weightx = 0;
                    gridBagConstraints.weighty = 0;
                    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
                    add(pnlToolbar, gridBagConstraints);
                    final TimeSeriesChartToolBar toolbar = (TimeSeriesChartToolBar)tsVis.getToolbar();
                    toolbar.enableMapButton(false);
                    toolbar.enableOperationsMenue(false);
                    toolbar.setOpaque(false);
                    if (!showOrigButtonNeeded) {
                        toolbar.setShowOrigButtonEnabled(false);
                    }

                    toolbar.addShowOrigTSListener(TimeseriesChartPanel.this);
                    pnlToolbar.add(toolbar, BorderLayout.WEST);

                    final Controllable tsVisController = tsVis.getLookup(Controllable.class);
                    tsVisController.enableContextMenu(true);
                    tsVisController.enableToolTips(true);

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
                setLayout(new BorderLayout());
                add(new JLabel("ERROR"), BorderLayout.CENTER); // NOI18N
            }
        }
    }
}
