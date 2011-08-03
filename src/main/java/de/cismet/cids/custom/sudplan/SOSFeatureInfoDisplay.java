/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.RectangleInsets;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import de.cismet.cismap.commons.features.SignaturedFeature;
import de.cismet.cismap.commons.gui.featureinfowidget.AbstractFeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplayKey;
import de.cismet.cismap.commons.gui.featureinfowidget.InitialisationException;
import de.cismet.cismap.commons.gui.featureinfowidget.MultipleFeatureInfoRequestsDisplay;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.HoldFeatureChangeEvent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.HoldListener;
import de.cismet.cismap.commons.interaction.events.MapClickedEvent;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
// TODO: use timeserieschartpanel
@ServiceProvider(service = FeatureInfoDisplay.class)
public class SOSFeatureInfoDisplay extends AbstractFeatureInfoDisplay<SlidableWMSServiceLayerGroup>
        implements MultipleFeatureInfoRequestsDisplay,
            TimeSeriesRemovedListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SOSFeatureInfoDisplay.class);
    private static final String SOS_FACTORY = "SOS-Client"; // NOI18N
    public static final String KEY_SCENARIO = "scenario";   // NOI18N
    public static final String KEY_SOS_URL = "sos_url";     // NOI18N
    public static final String KEY_FROM_YEAR = "from_year"; // NOI18N
    public static final String KEY_TO_YEAR = "to_year";     // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient DataHandler sosHandler;
    private transient String scenario;
    private transient String obsProp;
    private transient String procedure;
    private transient String foi;
    private transient String offering;
    private transient URL sosUrl;
    private transient int fromYear;
    private transient int toYear;
    private transient boolean initialised;
    // has to be initialised here because of the variable declaration of the GUI
    private final transient Available<Resolution> available = new FeatureInfoAvailable();
    // will only be accessed from EDT
    private transient TimeSeriesDisplayer currentDisplayer;
    private transient TimeseriesRetrieverConfig config;
    private int timeseriesCount = 0;
    private TimeSeriesSelectionListener listener;
    // End of variables declaration
// private ArrayList<SignaturedFeature> holdFeatures = new ArrayList<SignaturedFeature>();
    private HashMap<Integer, SignaturedFeature> holdFeatures = new HashMap<Integer, SignaturedFeature>();
    private ArrayList<HoldListener> holdListeners = new ArrayList<HoldListener>();
    private TimeSeriesChartToolBar toolbar;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JComboBox cboResolution =
        new de.cismet.cids.custom.sudplan.LocalisedEnumComboBox(Resolution.class, available);
    private final transient javax.swing.JPanel controlElementsPanel = new javax.swing.JPanel();
    private final transient javax.swing.JRadioButton holdButton = new javax.swing.JRadioButton();
    private final transient javax.swing.JPanel holdButtonPanel = new javax.swing.JPanel();
    private final transient javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFiller = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFiller1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResolution = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlChart = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlToolbar = new javax.swing.JPanel();
    private final transient javax.swing.JPanel resolutionPanel = new javax.swing.JPanel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SOSFeatureInfoDisplay.
     */
    public SOSFeatureInfoDisplay() {
        super(new FeatureInfoDisplayKey(
                SlidableWMSServiceLayerGroup.class,
                // TODO this is no general info display for the slidable layer so we need to exactly specify where to
                // use this
                FeatureInfoDisplayKey.ANY_SERVER,
                FeatureInfoDisplayKey.ANY_LAYER));
        initComponents();
        toolbar = new TimeSeriesChartToolBar();
        initialised = false;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.GridBagLayout());

        lblFiller1.setText(NbBundle.getMessage(SOSFeatureInfoDisplay.class, "SOSFeatureInfoDisplay.lblFiller1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(lblFiller1, gridBagConstraints);

        pnlChart.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlChart, gridBagConstraints);

        controlElementsPanel.setLayout(new java.awt.GridBagLayout());

        holdButtonPanel.setLayout(new java.awt.GridBagLayout());

        holdButton.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.holdButton.text")); // NOI18N
        holdButton.setFocusPainted(false);
        holdButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    holdButtonActionPerformed(evt);
                }
            });
        holdButtonPanel.add(holdButton, new java.awt.GridBagConstraints());

        controlElementsPanel.add(holdButtonPanel, new java.awt.GridBagConstraints());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.jLabel1.text")); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(10, 0));
        jLabel1.setMinimumSize(new java.awt.Dimension(10, 0));
        jLabel1.setPreferredSize(new java.awt.Dimension(10, 0));
        controlElementsPanel.add(jLabel1, new java.awt.GridBagConstraints());

        resolutionPanel.setLayout(new java.awt.GridBagLayout());

        lblFiller.setText(NbBundle.getMessage(SOSFeatureInfoDisplay.class, "SOSFeatureInfoDisplay.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        resolutionPanel.add(lblFiller, gridBagConstraints);

        lblResolution.setText(NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.lblResolution.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        resolutionPanel.add(lblResolution, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        resolutionPanel.add(cboResolution, gridBagConstraints);

        controlElementsPanel.add(resolutionPanel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        add(controlElementsPanel, gridBagConstraints);

        pnlToolbar.setMinimumSize(new java.awt.Dimension(350, 30));
        pnlToolbar.setPreferredSize(new java.awt.Dimension(400, 30));
        pnlToolbar.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(pnlToolbar, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void holdButtonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_holdButtonActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_holdButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   layer             evt DOCUMENT ME!
     * @param   parentTabbedPane  DOCUMENT ME!
     *
     * @throws  InitialisationException  DOCUMENT ME!
     */
    @Override
    public void init(final SlidableWMSServiceLayerGroup layer, final JTabbedPane parentTabbedPane)
            throws InitialisationException {
        parseKeywords(layer.getLayerInformation().getKeywords());

        sosHandler = Demo.getInstance().getSOSDH(); // TODO: <- for demo
        // DataHandlerFactory.Lookup.lookup(SOS_FACTORY); // NOI18N
// sosHandler = new SOSClientDataHandler();

        if (sosHandler == null) {
            final String message = "cannot lookup datahander factory: " + SOS_FACTORY; // NOI18N
            LOG.error(message);
            throw new InitialisationException(message);
        }

        sosHandler.setId(SOS_FACTORY);
        try {
            final BeanInfo info = Introspector.getBeanInfo(sosHandler.getClass(), Introspector.USE_ALL_BEANINFO);
            for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals("endpoint")) { // NOI18N
                    pd.getWriteMethod().invoke(sosHandler, sosUrl);
                }
            }

            sosHandler.open();
        } catch (final Exception e) {
            final String message = "cannot initialise sos handler"; // NOI18N
            LOG.error(message, e);
            throw new InitialisationException(message, e);
        }

        initialised = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   keywords  DOCUMENT ME!
     *
     * @throws  InitialisationException   DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private void parseKeywords(final String... keywords) throws InitialisationException {
        if (keywords == null) {
            final String message = "keywords must not be null"; // NOI18N
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        // clear the values to be sure they will be initialised
        scenario = obsProp = offering = foi = procedure = null;
        sosUrl = null;
        fromYear = toYear = -1;

        for (final String keyword : keywords) {
            if (keyword == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("skipping keyword because it is null"); // NOI18N
                }
            } else {
                final String[] keyValue = keyword.split("=");         // NOI18N

                if (keyValue.length == 2) {
                    final String key = keyValue[0];
                    final String value = keyValue[1];

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("found key: '" + key + "' and value: '" + value + "'"); // NOI18N
                    }

                    if (KEY_SCENARIO.equals(key)) {
                        scenario = value;
                    } else if (TimeSeries.OBSERVEDPROPERTY.equals(key)) {
                        obsProp = value;
                    } else if (TimeSeries.PROCEDURE.equals(key)) {
                        procedure = value;
                    } else if (TimeSeries.FEATURE_OF_INTEREST.equals(key)) {
                        foi = value;
                    } else if (TimeSeries.OFFERING.equals(key)) {
                        offering = value;
                    } else if (KEY_SOS_URL.equals(key)) {
                        try {
                            sosUrl = new URL(value);
                        } catch (final MalformedURLException ex) {
                            final String message = "invalid sos url: " + value; // NOI18N
                            LOG.error(message, ex);
                            throw new InitialisationException(message, ex);
                        }
                    } else if (KEY_FROM_YEAR.equals(key)) {
                        fromYear = Integer.parseInt(value);
                    } else if (KEY_TO_YEAR.equals(key)) {
                        toYear = Integer.parseInt(value);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("unreconised key: " + key);               // NOI18N
                        }
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("skipping keyword '" + keyword + "' as does not contain a valid option"); // NOI18N
                    }
                }
            }
        }

        try {
            validateState();
        } catch (final IllegalStateException e) {
            final String message = "invalid initialisation state"; // NOI18N
            LOG.error(message, e);
            throw new InitialisationException(message, e);
        }

        config = new TimeseriesRetrieverConfig(SOS_FACTORY, sosUrl, procedure, foi, obsProp, offering, null, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void validateState() throws IllegalStateException {
        if (scenario == null) {
            throw new IllegalStateException("scenario must not be null");                                              // NOI18N
        }
        if (obsProp == null) {
            throw new IllegalStateException("observed property must not be null");                                     // NOI18N
        }
        if (procedure == null) {
            throw new IllegalStateException("procedure property must not be null");                                    // NOI18N
        }
        if (foi == null) {
            throw new IllegalStateException("foi property must not be null");                                          // NOI18N
        }
        if (offering == null) {
            throw new IllegalStateException("offering property must not be null");                                     // NOI18N
        }
        if (sosUrl == null) {
            throw new IllegalStateException("sos url must not be null");                                               // NOI18N
        }
        if ((fromYear < 1900) || (fromYear > 2100) || (fromYear > toYear)) {
            throw new IllegalStateException("fromYear must be between 1900 and 2100 and must be smaller than toYear"); // NOI18N
        }
        if ((toYear < 1900) || (toYear > 2100) || (fromYear > toYear)) {
            throw new IllegalStateException("toYear must be between 1900 and 2100 and must be bigger than fromYear");  // NOI18N
        }
    }

    @Override
    public void showFeatureInfo(final MapClickedEvent mce) {
        if (EventQueue.isDispatchThread()) {
            if (!initialised) {
                throw new IllegalStateException("cannot process events before this instance is initialised"); // NOI18N
            }

            if ((currentDisplayer == null) || currentDisplayer.isDone() || currentDisplayer.cancel(true)) {
                currentDisplayer = new TimeSeriesDisplayer(mce);
            } else {
                final String message = "cannot cancel current displayer task"; // NOI18N
                LOG.error(message);
                throw new IllegalStateException(message);
            }

            currentDisplayer.execute();
        } else {
            throw new IllegalStateException("not allowed to call this method from any other thread than EDT"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   timeseries  datapoint DOCUMENT ME!
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

        final TimeStamp[] timeStamps = timeseries.getTimeStampsArray();                       // getTimeStamps();
        final org.jfree.data.time.TimeSeries data = new org.jfree.data.time.TimeSeries(name); // NOI18N

        final Envelope envelope = (Envelope)timeseries.getTSProperty(TimeSeries.GEOMETRY);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Time Series Geometry max X / Y, min X/Y: " + envelope.getMaxX() + "/" + envelope.getMaxY() + ", "
                        + envelope.getMinX() + "/" + envelope.getMinY());
        }

        if (envelope == null) {
            return null;
        } else if (envelope.contains(currentDisplayer.getMce().getxCoord(), currentDisplayer.getMce().getyCoord())) {
            final double width = envelope.getWidth();
            final double height = envelope.getHeight();
            final double xCoord = currentDisplayer.getMce().getxCoord();
            final double yCoord = currentDisplayer.getMce().getyCoord();
            final double xRelation = ((xCoord - envelope.getMinX()) / width);
            final double yRelation = ((yCoord - envelope.getMinY()) / height);

            for (final TimeStamp ts : timeStamps) {
                final Float[][] values = ((Float[][])timeseries.getValue(ts, valueKey));
                // assume this is a rectangular grid
                final int i = (int)(values.length * xRelation);
                final int j = (int)(values[i].length * yRelation);
                final float value = values[i][j];
                data.add(new Day(ts.asDate()), value);
            }
            // set the Unit of the timeseries as the rangedescription.
            data.setRangeDescription(SMSUtils.unitFromTimeseries(timeseries).getLocalisedName());
            final TimeSeriesDatasetAdapter dataset = new TimeSeriesDatasetAdapter(data);
            // TODO nicht das mce speichern sondern die geom..
            final GeometryFactory gf = new GeometryFactory();
            final Point p = gf.createPoint(new Coordinate(xCoord, yCoord));
            dataset.setGeometry(p);
//            dataset.setMapClickedEvent(currentDisplayer.getMce());
            return dataset;
        } else {
            LOG.warn("time series geometry does not contain mouse click coordinates"); // NOI18N
            return null;
        }
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
        final Variable observed = Variable.getVariable(obsProp);
        if (Variable.PRECIPITATION.equals(observed)) {
            chart = ChartFactory.createXYBarChart(
                    "Rainfall data",
                    "Time",
                    true,                                                              // date axis
                    unit.getLocalisedName(),
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,                                                             // legend
                    true,                                                              // tooltips
                    false);                                                            // urls
        } else if (Variable.TEMPERATURE.equals(observed) || Variable.O3.equals(observed)) {
            chart = ChartFactory.createTimeSeriesChart(
                    "Timeseries data",
                    "Time",
                    unit.getLocalisedName(),
                    dataset,
                    true,
                    true,
                    false);
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

        final SelectionXYLineRenderer renderer = new SelectionXYLineRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);
        final NumberAxis axis = (NumberAxis)plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        plot.setRenderer(renderer);
        listener = new TimeSeriesSelectionListener(chart.getXYPlot());
        timeseriesCount = 0;
        return chart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g  DOCUMENT ME!
     * @param   s  DOCUMENT ME!
     * @param   p  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SignaturedFeature createFeatureSignature(final Geometry g, final Shape s, final Paint p) {
        final SignaturedFeature feature = new SignaturedFeature(g);
        // create an image containing the time series shape as overlay icon
        if ((s == null) || (p == null)) {
            return feature;
        }
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        int width = 0;
        int height = 0;

        // get meta iformation for the size of the overlay
        final Properties iconProps = new Properties();
        try {
            final InputStream in = getClass().getResourceAsStream(
                    "/de/cismet/cismap/commons/gui/res/featureInfoIcon.properties");                             // NOI18N
            if (in != null) {
                iconProps.load(in);
                in.close();
            } else {
                LOG.warn(
                    "Could not laod featureInfoIcon.properties file. Default values for overlay area are used"); // NOI18N
            }
        } catch (IOException ex) {
            LOG.error(
                "Could not read featureInfoIcon.properties file. Default values for overlay area are used",
                ex);                                                                                             // NOI18N
        }

        if (iconProps.isEmpty()
                    || (!iconProps.containsKey("overlayWidth") && !iconProps.containsKey("overlayHeigth")) // NOI18N
        ) {                                                                                                // NOI18N
            // TODO ERROR LOG EXCPETION
            LOG.warn(
                "featureInfoicon.properties file does not contain all needed keys. Default values for overlay area are used"); // NOI18N
        } else {
            try {
                width = Integer.parseInt((String)iconProps.get("overlayWidth"));                                               // NOI18N
                height = Integer.parseInt((String)iconProps.get("overlayHeigth"));                                             // NOI18N
                bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            } catch (NumberFormatException ex) {
                LOG.error(
                    "Error while retrieving properties for overlay area. Default values for overlay area are used",            // NOI18N
                    ex);
            }
        }

        final Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setPaint(p);
//        g2.drawRect(0, 0, bi.getWidth(), bi.getHeight());
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, 8, 16, 8);
        feature.setOverlayIcon(bi);
        // paint the time series symbol
        final AffineTransform saveXform = g2.getTransform();
        final AffineTransform at = new AffineTransform();
        final AffineTransform scaleTrans = new AffineTransform();

        scaleTrans.scale(1.5, 1.5);
        final Shape scaledShape = scaleTrans.createTransformedShape(s);
//        g2.setColor(Color.red);
//        g2.drawRect(0, 0, (int)scaledShape.getBounds().getWidth(), (int)scaledShape.getBounds().getHeight());
        final double imageXMittelpunkt = bi.getWidth() / 2;
        final double imageYMittelpunkt = bi.getHeight() / 2;
        final double shapeXMittelpunkt = (scaledShape.getBounds().getWidth() / 2) - 4.5;
        final double shapeYMittelpunkt = (scaledShape.getBounds().getHeight() / 2) - 4.5;
        at.translate(imageXMittelpunkt - (shapeXMittelpunkt), imageYMittelpunkt - (shapeYMittelpunkt));
        g2.transform(at);

        g2.setPaint(p);
        g2.fill(scaledShape);
//        g2.setColor(Color.black);
//        g2.drawRect(0, 0, (int)scaledShape.getBounds().getWidth(), (int)scaledShape.getBounds().getHeight());
        g2.transform(saveXform);

        return feature;
//        return new SignaturedFeature(g);
    }

    @Override
    public boolean isOnHold() {
        return this.holdButton.isSelected();
    }

    @Override
    public Collection<SignaturedFeature> getHoldFeautres() {
        return this.holdFeatures.values();
    }

    @Override
    public void addHoldListener(final HoldListener hl) {
        holdListeners.add(hl);
    }

    @Override
    public void removeHoldListener(final HoldListener hl) {
        holdListeners.remove(hl);
    }

    @Override
    public void fireHoldFeatureChanged() {
        final ArrayList<SignaturedFeature> featureList = new ArrayList<SignaturedFeature>();
        // TODO anderer weg um gelöschte herauszufinden, da auch null in map sein kann wenn neben envelope geklcikt
        // wurde
        for (int i = 0; i <= timeseriesCount; i++) {
            if (holdFeatures.get(i) != null) {
                featureList.add(holdFeatures.get(i));
            }
        }
        for (final HoldListener hl : holdListeners) {
            hl.holdFeautresChanged(new HoldFeatureChangeEvent(featureList, this));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  index  holdFeatures DOCUMENT ME!
     */
// public void setHoldFeatures(final ArrayList<SignaturedFeature> holdFeatures) {
// this.holdFeatures = holdFeatures;
// }
    @Override
    public void timeSeriesRemoved(final int index) {
        // remove the corresponding feature from holdFeature Collection
        holdFeatures.remove(index);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class FeatureInfoAvailable implements Available<Resolution> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean isAvailable(final Resolution type) {
            return Resolution.DECADE.equals(type);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class TimeSeriesDisplayer extends SwingWorker<JFreeChart, Void> {

        //~ Instance fields ----------------------------------------------------

        private final transient MapClickedEvent mce;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TimeSeriesDisplayer object.
         *
         * @param  mce  xCoordinate DOCUMENT ME!
         */
        public TimeSeriesDisplayer(final MapClickedEvent mce) {
            this.mce = mce;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public MapClickedEvent getMce() {
            return mce;
        }

        @Override
        protected JFreeChart doInBackground() throws Exception {
            final TimeSeries timeseries = TimeseriesRetriever.getInstance().retrieve(config).get();
            final IntervalXYDataset newDataset = createDataset(timeseries, obsProp);

            JFreeChart existingChart = null;
            CustomChartPanel chartPanel = null;
            final Component[] components = pnlChart.getComponents();
            boolean chartPanelhasChart = false;
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof CustomChartPanel) {
                    chartPanel = (CustomChartPanel)components[i];
                    if (chartPanel.getChart() != null) {
                        existingChart = chartPanel.getChart();
                        chartPanelhasChart = true;
                    }
                }
            }
            if (newDataset == null) {
                LOG.warn("Could not get a time series.  "); // NOI18N
                timeseriesCount++;
                return null;
            }

            if (holdButton.isSelected() && chartPanelhasChart) {
                // if holdButton pressed, modifiy the dataset and actualize the chart
                if (existingChart.getPlot() instanceof XYPlot) {
                    timeseriesCount++;
                    final XYPlot plot = (XYPlot)existingChart.getPlot();
                    final TimeSeriesCollection newData = (TimeSeriesCollection)newDataset;
                    final org.jfree.data.time.TimeSeries newTimeseries = newData.getSeries(0);

                    plot.setDataset(timeseriesCount, newData);
                    final SelectionXYLineRenderer renderer = new SelectionXYLineRenderer(true, true, false);
                    renderer.addTSSelectionListener(listener);
                    plot.setRenderer(timeseriesCount, renderer);
                    // if there are different units we have to create a multi axis chart
                    boolean newTSVariable = true;
                    for (int i = 0; i < (plot.getDatasetCount() - 1); i++) {
                        final TimeSeriesCollection tsCollection = (TimeSeriesCollection)plot.getDataset(i);
                        if (tsCollection != null) {
                            final org.jfree.data.time.TimeSeries ts = tsCollection.getSeries(0);
                            if (newTimeseries.getRangeDescription().equals(ts.getRangeDescription())) {
                                newTSVariable = false;
                                break;
                            }
                        }
                    }
                    if (newTSVariable) {
                        // time series doesnt fit to any axisting axis, so create a new one
                        final NumberAxis axis = new NumberAxis(newTimeseries.getRangeDescription());
                        axis.setAutoRange(true);
                        axis.setAutoRangeIncludesZero(false);
//                            axis.setLabelPaint((plot.getRenderer(clickCount)).getSeriesPaint(0));
                        plot.setRangeAxis(timeseriesCount, axis);
                        plot.mapDatasetToRangeAxis(timeseriesCount, timeseriesCount);
                    }
                    return existingChart;
                }
            }
            return createChart(newDataset, SMSUtils.unitFromTimeseries(timeseries));
        }

        @Override
        protected void done() {
            final GeometryFactory gf = new GeometryFactory();
            final double xCoord = getMce().getxCoord();
            final double yCoord = getMce().getyCoord();
            final Point pointGeom = gf.createPoint(new Coordinate(xCoord, yCoord));
            try {
                final JFreeChart chart = get();
                if (chart == null) {
                    holdFeatures.put(timeseriesCount, createFeatureSignature(pointGeom, null, null));
                    fireHoldFeatureChanged();
                    return;
                }
                pnlChart.removeAll();
                final CustomChartPanel chartPanel = new CustomChartPanel(chart);
                chartPanel.addChartMouseListener(listener);
                chart.getPlot().addChangeListener(listener);
                chartPanel.addTimeSeriesRemovedListener(SOSFeatureInfoDisplay.this);
                final XYPlot plot = (XYPlot)chart.getPlot();
                toolbar.setChartPanel(chartPanel);
                pnlToolbar.removeAll();
                final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridBagConstraints.anchor = GridBagConstraints.CENTER;
                gridBagConstraints.insets = new Insets(5, 5, 5, 5);
                pnlToolbar.add(toolbar, gridBagConstraints);
                final XYItemRenderer renderer = plot.getRenderer(timeseriesCount);
                final Shape s = renderer.getLegendItem(timeseriesCount, 0).getShape();
                final Paint paint = renderer.getLegendItem(timeseriesCount, 0).getFillPaint();

                if ((s != null) && (paint != null)) {
                    if (holdButton.isSelected()) {
//                    holdFeatures.add(createFeatureSignature(pointGeom, s, paint));
                        holdFeatures.put(timeseriesCount, createFeatureSignature(pointGeom, s, paint));
                    } else {
                        holdFeatures.clear();
//                    holdFeatures.add(createFeatureSignature(pointGeom, s, paint));
                        holdFeatures.put(timeseriesCount, createFeatureSignature(pointGeom, s, paint));
                    }
                } else {
                    holdFeatures.put(timeseriesCount, null);
                }
                fireHoldFeatureChanged();
                pnlChart.add(chartPanel, BorderLayout.CENTER);

                Container parent = SOSFeatureInfoDisplay.this;
                Container current = parent.getParent();
                while (current != null) {
                    parent = current;
                    current = parent.getParent();
                }
                parent.invalidate();
                parent.validate();
            } catch (final InterruptedException ex) {
                final String message = "in done nothing should be interrupted anymore"; // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            } catch (final ExecutionException ex) {
                final String message = "execution exception in worker thread";          // NOI18N
                LOG.error(message, ex.getCause());
                throw new IllegalStateException(message, ex.getCause());
            }
        }
    }
}
