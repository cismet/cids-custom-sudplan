/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.util.text.ISO8601DateFormat;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;

import org.jfree.util.Log;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSelectionNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSignature;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.TimeSeriesChartToolBar;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.TimeSeriesVisualisationFactory;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.VisualisationType;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesSelectionListener;

import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.SignaturedFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.featureinfowidget.AbstractFeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.AggregateableFeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplayKey;
import de.cismet.cismap.commons.gui.featureinfowidget.InitialisationException;
import de.cismet.cismap.commons.gui.featureinfowidget.MultipleFeatureInfoRequestsDisplay;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.HoldFeatureChangeEvent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.HoldListener;
import de.cismet.cismap.commons.interaction.CismapBroker;
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
public final class SOSFeatureInfoDisplay extends AbstractFeatureInfoDisplay<SlidableWMSServiceLayerGroup>
        implements MultipleFeatureInfoRequestsDisplay,
            AggregateableFeatureInfoDisplay {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SOSFeatureInfoDisplay.class);
    private static final String SOS_FACTORY = "SOSClientDataHandler"; // NOI18N
    public static final String KEY_SCENARIO = "scenario";             // NOI18N
    public static final String KEY_SOS_URL = "sos_url";               // NOI18N
    public static final String KEY_FROM_YEAR = "from_year";           // NOI18N
    public static final String KEY_TO_YEAR = "to_year";               // NOI18N

    //~ Instance fields --------------------------------------------------------

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
    private final transient Available<Resolution> available = new ResolutionAvailable();
    // will only be accessed from EDT
    private transient TimeSeriesDisplayer currentDisplayer;
    private int timeseriesCount = 0;
    private final transient Map<Integer, SignaturedFeature> holdFeatures;
    private final transient Set<HoldListener> holdListeners;
    private final transient TimeSeriesVisualisation tsVis;
    private final transient TimeSeriesListChangedListener tsListChangedL;
    private final transient TimeSeriesSelectionListener tsSelectionL;
    private List<AggregateableFeatureInfoDisplay> aggregateableDisplays;
    private JToolBar toolbar;
    private int overlayWidth = 0;
    private int overlayHeight = 0;
    private boolean displayVisible = false;
    private JTabbedPane tabPane;
    private transient Coordinate currentCoordinate;
    private final transient ActionListener resL;
    private final transient ActionListener holdL;
    private final transient ActionListener aggrL;
    private final transient Object displayLock;
    /**
     * Used by {@link TimeSeriesDisplayer} for busy indication. Note that Serialization should not be necessary as only
     * one {@link TimeSeriesDisplayer} instance can access this attribute simultaneously.
     */
    private final LockableUI lockableUI;
    private transient Resolution currentItem;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnAggregateTimeSeriesVisualisations = new javax.swing.JButton();
    private final transient javax.swing.JComboBox cboResolution =
        new de.cismet.cids.custom.sudplan.LocalisedEnumComboBox(Resolution.class, available);
    private final transient javax.swing.JPanel contentPanel = new javax.swing.JPanel();
    private final transient javax.swing.JCheckBox holdCheckBox = new javax.swing.JCheckBox();
    private final transient javax.swing.JLabel lblFiller = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFiller1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFiller3 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFiller4 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResolution = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlAggregateButton = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlChart = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlControlElements = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlHoldButton = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlResolution = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlToolbar = new javax.swing.JPanel();
    private final transient javax.swing.JToolBar toolBarModelOverview = new javax.swing.JToolBar();
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

        displayLock = new Object();
        holdFeatures = new HashMap<Integer, SignaturedFeature>();
        holdListeners = new HashSet<HoldListener>();
        tsListChangedL = new TimeServiesListChangedListenerImpl();
        resL = new ResolutionChangedListener();
        aggrL = new AggregateButtonActionListener();
        holdL = new HoldChangedListener();

        currentItem = Resolution.DECADE;

        initComponents();

        tsVis = TimeSeriesVisualisationFactory.getInstance().createVisualisation(VisualisationType.SIMPLE);
        tsVis.addTimeSeriesListChangeListener(
            WeakListeners.create(TimeSeriesListChangedListener.class, tsListChangedL, tsVis));

        // try to get properties for size of the overlay
        overlayWidth = 16;
        overlayHeight = 16;
        // try to get metainformation for overlay position, width, color from properties file and override the
        // default values if succesfull
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
        } catch (final IOException ex) {
            LOG.error(
                "Could not read featureInfoIcon.properties file. Default values for overlay area are used",      // NOI18N
                ex);                                                                                             // NOI18N
        }

        if (iconProps.isEmpty() || !(iconProps.containsKey("overlayWidth") && iconProps.containsKey("overlayHeigth"))) {       // NOI18N
            LOG.warn(
                "featureInfoIcon.properties file does not contain all needed keys. Default values for overlay area are used"); // NOI18N
        } else {
            try {
                overlayWidth = Integer.parseInt((String)iconProps.get("overlayWidth"));                                        // NOI18N
                overlayHeight = Integer.parseInt((String)iconProps.get("overlayHeigth"));                                      // NOI18N
            } catch (NumberFormatException ex) {
                Log.error(
                    "Error while retrieving properties for overlay area. Default values for overlay area are used",            // NOI18N
                    ex);
            }
        }

        final TimeSeriesSelectionNotification tsn = tsVis.getLookup(TimeSeriesSelectionNotification.class);
        if (tsn == null) {
            tsSelectionL = null;
        } else {
            tsSelectionL = new TimeSeriesSelectionListenerImpl();
            tsn.addTimeSeriesSelectionListener(
                WeakListeners.create(TimeSeriesSelectionListener.class, tsSelectionL, tsn));
        }

        initialised = false;

        // install components for busy indication
        super.remove(this.contentPanel);
        this.lockableUI = new LockableUI();
        final ColorConvertOp grayScale = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        final BufferedImageOpEffect effect = new BufferedImageOpEffect(grayScale);
        this.lockableUI.setLockedEffects(effect);
        final JXLayer layer = new JXLayer(this.contentPanel, lockableUI);
        super.add(layer);

        cboResolution.addActionListener(WeakListeners.create(ActionListener.class, resL, cboResolution));
        holdCheckBox.addActionListener(WeakListeners.create(ActionListener.class, holdL, holdCheckBox));
        btnAggregateTimeSeriesVisualisations.addActionListener(WeakListeners.create(
                ActionListener.class,
                aggrL,
                btnAggregateTimeSeriesVisualisations));
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

        setLayout(new java.awt.BorderLayout());

        contentPanel.setLayout(new java.awt.GridBagLayout());

        lblFiller1.setText(NbBundle.getMessage(SOSFeatureInfoDisplay.class, "SOSFeatureInfoDisplay.lblFiller1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        contentPanel.add(lblFiller1, gridBagConstraints);

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
        contentPanel.add(pnlChart, gridBagConstraints);

        pnlControlElements.setLayout(new java.awt.GridBagLayout());

        pnlAggregateButton.setLayout(new java.awt.GridBagLayout());

        toolBarModelOverview.setBorder(null);
        toolBarModelOverview.setFloatable(false);
        toolBarModelOverview.setRollover(true);

        btnAggregateTimeSeriesVisualisations.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/chart_line_link.png"))); // NOI18N
        btnAggregateTimeSeriesVisualisations.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.btnAggregateTimeSeriesVisualisations.text_1"));          // NOI18N
        btnAggregateTimeSeriesVisualisations.setToolTipText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.btnAggregateTimeSeriesVisualisations.toolTipText"));     // NOI18N
        btnAggregateTimeSeriesVisualisations.setActionCommand(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.btnAggregateTimeSeriesVisualisations.actionCommand"));   // NOI18N
        btnAggregateTimeSeriesVisualisations.setEnabled(false);
        btnAggregateTimeSeriesVisualisations.setFocusPainted(false);
        btnAggregateTimeSeriesVisualisations.setFocusable(false);
        btnAggregateTimeSeriesVisualisations.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAggregateTimeSeriesVisualisations.setPreferredSize(new java.awt.Dimension(24, 24));
        btnAggregateTimeSeriesVisualisations.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarModelOverview.add(btnAggregateTimeSeriesVisualisations);

        pnlAggregateButton.add(toolBarModelOverview, new java.awt.GridBagConstraints());

        pnlControlElements.add(pnlAggregateButton, new java.awt.GridBagConstraints());

        lblFiller3.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.lblFiller3.text")); // NOI18N
        lblFiller3.setMaximumSize(new java.awt.Dimension(10, 0));
        lblFiller3.setMinimumSize(new java.awt.Dimension(10, 0));
        lblFiller3.setPreferredSize(new java.awt.Dimension(10, 0));
        pnlControlElements.add(lblFiller3, new java.awt.GridBagConstraints());

        pnlHoldButton.setLayout(new java.awt.GridBagLayout());

        holdCheckBox.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.holdCheckBox.text"));        // NOI18N
        holdCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.holdCheckBox.toolTipText")); // NOI18N
        pnlHoldButton.add(holdCheckBox, new java.awt.GridBagConstraints());

        pnlControlElements.add(pnlHoldButton, new java.awt.GridBagConstraints());

        lblFiller4.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.lblFiller4.text")); // NOI18N
        lblFiller4.setMaximumSize(new java.awt.Dimension(10, 0));
        lblFiller4.setMinimumSize(new java.awt.Dimension(10, 0));
        lblFiller4.setPreferredSize(new java.awt.Dimension(10, 0));
        pnlControlElements.add(lblFiller4, new java.awt.GridBagConstraints());

        pnlResolution.setLayout(new java.awt.GridBagLayout());

        lblFiller.setText(NbBundle.getMessage(SOSFeatureInfoDisplay.class, "SOSFeatureInfoDisplay.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlResolution.add(lblFiller, gridBagConstraints);

        lblResolution.setText(NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.lblResolution.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlResolution.add(lblResolution, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlResolution.add(cboResolution, gridBagConstraints);

        pnlControlElements.add(pnlResolution, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        contentPanel.add(pnlControlElements, gridBagConstraints);

        pnlToolbar.setMinimumSize(new java.awt.Dimension(500, 30));
        pnlToolbar.setPreferredSize(new java.awt.Dimension(500, 30));
        pnlToolbar.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        contentPanel.add(pnlToolbar, gridBagConstraints);

        add(contentPanel, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

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
        tabPane = parentTabbedPane;
        parseKeywords(layer.getLayerInformation().getKeywords());

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

                    if (TimeSeries.OBSERVEDPROPERTY.equals(key)) {
                        obsProp = value;
                    } else if (TimeSeries.PROCEDURE.equals(key)) {
                        procedure = value;
                    } else if (TimeSeries.FEATURE_OF_INTEREST.equals(key)) {
                        foi = value;
                    } else if (TimeSeries.OFFERING.equals(key)) {
                        offering = value;
                        final String[] split = offering.split("_");                                      // NOI18N
                        if (split.length == 4) {
                            scenario = split[1];
                        } else if (split.length == 1) {
                            scenario = split[0];
                        } else {
                            throw new InitialisationException("invalid offering encoding: " + offering); // NOI18N
                        }
                    } else if (KEY_SOS_URL.equals(key)) {
                        try {
                            sosUrl = new URL(value);
                        } catch (final MalformedURLException ex) {
                            final String message = "invalid sos url: " + value;                          // NOI18N
                            LOG.error(message, ex);
                            throw new InitialisationException(message, ex);
                        }
                    } else if (TimeSeries.AVAILABLE_DATA_MIN.equals(key)) {
                        try {
                            final DateFormat df = new ISO8601DateFormat();
                            final Date date = df.parse(value);
                            final Calendar cal = GregorianCalendar.getInstance();
                            cal.setTime(date);
                            fromYear = cal.get(Calendar.YEAR);
                        } catch (final ParseException ex) {
                            final String message = "invalid available data minimum: " + value;           // NOI18N
                            LOG.error(message, ex);
                            throw new InitialisationException(message, ex);
                        }
                    } else if (TimeSeries.AVAILABLE_DATA_MAX.equals(key)) {
                        try {
                            final DateFormat df = new ISO8601DateFormat();
                            final Date date = df.parse(value);
                            final Calendar cal = GregorianCalendar.getInstance();
                            cal.setTime(date);
                            toYear = cal.get(Calendar.YEAR);
                        } catch (final ParseException ex) {
                            final String message = "invalid available data maximum: " + value;           // NOI18N
                            LOG.error(message, ex);
                            throw new InitialisationException(message, ex);
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("unreconised key: " + key);                                        // NOI18N
                        }
                    }
                } else if (keyword.startsWith(KEY_SOS_URL)) {
                    // in case of complete url (e.g. capabilities link) there are more '=' in the string
                    final String urlString = keyword.substring(keyword.indexOf('=') + 1);
                    try {
                        sosUrl = new URL(urlString);
                    } catch (final MalformedURLException e) {
                        final String message = "invalid sos url: " + urlString; // NOI18N
                        LOG.error(message, e);
                        throw new InitialisationException(message, e);
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

            currentCoordinate = new Coordinate(mce.getxCoord(), mce.getyCoord());

            if ((currentDisplayer == null) || currentDisplayer.isDone() || currentDisplayer.cancel(true)) {
                currentDisplayer = new TimeSeriesDisplayer(currentCoordinate);
            } else {
                final String message = "cannot cancel current displayer task";                                 // NOI18N
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
     * @return  DOCUMENT ME!
     */
    public TimeSeriesVisualisation getTsVis() {
        return tsVis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   pointGeom  DOCUMENT ME!
     * @param   bi         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SignaturedFeature createFeatureSignature(final Geometry pointGeom, final BufferedImage bi) {
        final SignaturedFeature feature = new SignaturedFeature(pointGeom);
        if (bi != null) {
            feature.setOverlayIcon(bi);
        }
        return feature;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    public void setHoldFlag(final boolean b) {
        holdCheckBox.setSelected(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  b  DOCUMENT ME!
     */
    public void enableHoldFlag(final boolean b) {
        holdCheckBox.setEnabled(b);
    }

    @Override
    public boolean isOnHold() {
        return (holdCheckBox != null) && holdCheckBox.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Collection<SignaturedFeature> getHoldFeatures() {
        return this.holdFeatures.values();
    }

    @Override
    public void addHoldListener(final HoldListener hl) {
        if (hl != null) {
            synchronized (holdListeners) {
                holdListeners.add(hl);
            }
        }
    }

    @Override
    public void removeHoldListener(final HoldListener hl) {
        if (hl != null) {
            synchronized (holdListeners) {
                holdListeners.remove(hl);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void fireHoldFeatureChanged() {
        if (isDisplayVisible()) {
            final Iterator<HoldListener> it;

            synchronized (holdListeners) {
                it = new HashSet<HoldListener>(holdListeners).iterator();
            }

            final HoldFeatureChangeEvent event = new HoldFeatureChangeEvent(holdFeatures.values(), this);

            while (it.hasNext()) {
                final HoldListener listener = it.next();
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            listener.holdFeaturesChanged(event);
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   g   DOCUMENT ME!
     * @param   bi  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Feature createFeature(final Geometry g, final BufferedImage bi) {
        final TimeSeriesFeature feature = new TimeSeriesFeature(g, bi);

        return feature;
    }

    @Override
    public void setDisplayVisble(final boolean aFlag) {
        displayVisible = aFlag;
        if (displayVisible) {
            fireHoldFeatureChanged();
        }
    }

    @Override
    public boolean isDisplayVisible() {
        return displayVisible;
    }

    @Override
    public String getAggregateTypeID() {
        return obsProp;
    }

    @Override
    public void setAggregatableDisplayList(final List<AggregateableFeatureInfoDisplay> list) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("set aggregationDisplay list");
        }
        synchronized (displayLock) {
            aggregateableDisplays = list;
        }

        if (checkAggregateDisplaysTimeSeriesCount()) {
            setAggregateButtonForAllDisplaysEnabled(true);
        } else {
            setAggregateButtonForAllDisplaysEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    private void setAggregateButtonForAllDisplaysEnabled(final boolean aFlag) {
        final Iterator<AggregateableFeatureInfoDisplay> it;

        synchronized (displayLock) {
            it = new HashSet<AggregateableFeatureInfoDisplay>(aggregateableDisplays).iterator();
        }

        while (it.hasNext()) {
            final AggregateableFeatureInfoDisplay d = it.next();
            if (d instanceof SOSFeatureInfoDisplay) {
                final SOSFeatureInfoDisplay sosDisplay = (SOSFeatureInfoDisplay)d;
                sosDisplay.setAggregateButtonEnabled(aFlag);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    public void setAggregateButtonEnabled(final boolean aFlag) {
        btnAggregateTimeSeriesVisualisations.setEnabled(aFlag);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkAggregateDisplaysTimeSeriesCount() {
        final Iterator<AggregateableFeatureInfoDisplay> it;
        synchronized (displayLock) {
            it = new HashSet<AggregateableFeatureInfoDisplay>(aggregateableDisplays).iterator();
        }
        while (it.hasNext()) {
            final AggregateableFeatureInfoDisplay d = it.next();

            if (d instanceof SOSFeatureInfoDisplay) {
                final SOSFeatureInfoDisplay sosDisplay = (SOSFeatureInfoDisplay)d;
                final int tsCount = sosDisplay.getTsVis().getTimeSeriesCollection().size();
                if (tsCount != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class HoldChangedListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (isOnHold()) {
                final Resolution currentRes = (Resolution)cboResolution.getSelectedItem();
                if (!Resolution.DECADE.equals(currentRes)) {
                    cboResolution.setSelectedItem(Resolution.DECADE);

                    if (SOSFeatureInfoDisplay.this.currentCoordinate != null) {
                        if ((currentDisplayer == null) || currentDisplayer.isDone() || currentDisplayer.cancel(true)) {
                            final Resolution resolution = (Resolution)SOSFeatureInfoDisplay.this.cboResolution
                                        .getSelectedItem();
                            tsVis.clearTimeSeries();
                            currentDisplayer = new TimeSeriesDisplayer(currentCoordinate, resolution);
                        } else {
                            final String message = "cannot cancel current displayer task"; // NOI18N
                            LOG.error(message);
                            throw new IllegalStateException(message);
                        }
                        currentDisplayer.execute();
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ResolutionChangedListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Resolution selected = (Resolution)cboResolution.getSelectedItem();
            if (available.isAvailable(selected)) {
                currentItem = selected;

                if (SOSFeatureInfoDisplay.this.currentCoordinate != null) {
                    if ((currentDisplayer == null) || currentDisplayer.isDone() || currentDisplayer.cancel(true)) {
                        final Resolution resolution = (Resolution)SOSFeatureInfoDisplay.this.cboResolution
                                    .getSelectedItem();
                        currentDisplayer = new TimeSeriesDisplayer(currentCoordinate, resolution);
                    } else {
                        final String message = "cannot cancel current displayer task"; // NOI18N
                        LOG.error(message);
                        throw new IllegalStateException(message);
                    }
                    currentDisplayer.execute();
                }
            } else {
                cboResolution.setSelectedItem(currentItem);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class AggregateButtonActionListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("action listener aggregate variables button");
            }

            final TimeSeriesVisualisation methodOverviewVis = TimeSeriesVisualisationFactory.getInstance()
                        .createVisualisation(VisualisationType.SIMPLE);
            final Iterator<AggregateableFeatureInfoDisplay> it;

            synchronized (displayLock) {
                it = new HashSet<AggregateableFeatureInfoDisplay>(aggregateableDisplays).iterator();
            }
            while (it.hasNext()) {
                final AggregateableFeatureInfoDisplay d = it.next();
                if (d instanceof SOSFeatureInfoDisplay) {
                    final SOSFeatureInfoDisplay sosDisplay = (SOSFeatureInfoDisplay)d;
                    final Collection<TimeSeries> companionTimeSeries = sosDisplay.getTsVis().getTimeSeriesCollection();
                    for (final TimeSeries ts : companionTimeSeries) {
                        methodOverviewVis.addTimeSeries(ts);
                    }
                }
            }
            final String[] var = obsProp.split(":");
            final String tabTitle = NbBundle.getMessage(
                    SOSFeatureInfoDisplay.class,
                    "SOSFeatureInfoDisplay.modelComparisonTabTitle") + " - " + var[var.length - 1]; // NOI18N
            final JPanel p = new JPanel();
            p.add(new JButton(tabTitle));
            final int tabIndex = tabPane.indexOfTab(tabTitle);
            final SOSModelComparisonFeatureInfoDisplay tabComponent = new SOSModelComparisonFeatureInfoDisplay(
                    methodOverviewVis,
                    tabPane);
            if (tabIndex == -1) {
                tabPane.add(tabTitle,
                    tabComponent);
                tabPane.setTabComponentAt(tabPane.indexOfTab(tabTitle),
                    new JLabel(tabTitle));
            } else {
                tabPane.setComponentAt(tabIndex, tabComponent);
            }
            tabPane.setSelectedIndex(tabPane.indexOfTab(tabTitle));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ResolutionAvailable implements Available<Resolution> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean isAvailable(final Resolution type) {
            if (isOnHold()) {
                return Resolution.DECADE.equals(type);
            } else {
                return Resolution.DECADE.equals(type) || Resolution.YEAR.equals(type) || Resolution.MONTH.equals(type)
                            || Resolution.DAY.equals(type);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class TimeSeriesSelectionListenerImpl implements TimeSeriesSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void selectionChanged(final TimeSeriesSelectionEvent evt) {
            final Collection<TimeSeries> selectedTS = evt.getSelectedTs();

            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            mc.getRubberBandLayer().removeAllChildren();
            mc.getTmpFeatureLayer().removeAllChildren();
            mc.repaint();
            final TimeSeriesSignature tss = tsVis.getLookup(TimeSeriesSignature.class);
            if (tss != null) {
                for (final TimeSeries ts : selectedTS) {
                    final BufferedImage bi = tss.getTimeSeriesSignature(ts, overlayWidth, overlayHeight);
                    final Geometry g = (Geometry)ts.getTSProperty(TimeSeries.GEOMETRY);
                    final PFeature pf = new PFeature(createFeature(g, bi), mc);
                    mc.addStickyNode(pf);
                    mc.getTmpFeatureLayer().addChild(pf);
                }
                mc.rescaleStickyNodes();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class TimeServiesListChangedListenerImpl implements TimeSeriesListChangedListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void timeSeriesListChanged(final TimeSeriesListChangedEvent evt) {
            if (evt.getID() == TimeSeriesListChangedEvent.TIME_SERIES_REMOVED) {
                timeseriesCount--;
                final TimeSeries ts = (TimeSeries)evt.getSource();
                boolean holdFeaturesChanged = false;
                // TODO prevent concurrent modification

                final Iterator<Integer> it;
                synchronized (holdFeatures) {
                    it = new HashSet<Integer>(holdFeatures.keySet()).iterator();
                }
                int featureToRemove = -1;
                while (it.hasNext()) {
                    final Integer i = it.next();
                    final SignaturedFeature f = holdFeatures.get(i);
                    if (f.getGeometry().equals((Geometry)ts.getTSProperty(TimeSeries.GEOMETRY))) {
                        featureToRemove = i;
                        holdFeaturesChanged = true;
                        break;
                    }
                }

                if (holdFeaturesChanged) {
                    holdFeatures.remove(featureToRemove);
                    fireHoldFeatureChanged();
                }

                if (tsVis.getTimeSeriesCollection().size() == 1) {
                    if (checkAggregateDisplaysTimeSeriesCount()) {
                        setAggregateButtonForAllDisplaysEnabled(true);
                    }
                } else {
                    setAggregateButtonForAllDisplaysEnabled(false);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class TimeSeriesDisplayer extends SwingWorker<TimeSeries, Void> {

        //~ Instance fields ----------------------------------------------------

        private final transient Coordinate coordinate;
        private final transient Resolution resolution;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TimeSeriesDisplayer object.
         *
         * @param  coordinate  point mce xCoordinate DOCUMENT ME!
         */
        public TimeSeriesDisplayer(final Coordinate coordinate) {
            this(coordinate, null);
        }

        /**
         * Creates a new TimeSeriesDisplayer object.
         *
         * @param  coordinate  DOCUMENT ME!
         * @param  resolution  DOCUMENT ME!
         */
        public TimeSeriesDisplayer(final Coordinate coordinate, final Resolution resolution) {
            this.coordinate = coordinate;
            this.resolution = resolution;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected TimeSeries doInBackground() throws Exception {
            // lock panel and indicate work in progress
            lockableUI.setLocked(true);
            setAggregateButtonForAllDisplaysEnabled(false);
            // ------------------------------------------------------

            if (this.resolution != null) {
                offering = offering.replaceFirst("_\\d+[YMd]$", "_" + this.resolution.getOfferingSuffix()); // NOI18N
                procedure = procedure.replaceFirst(":\\d+[YMs]$", ":" + this.resolution.getPrecision());    // NOI18N
            }

            // TODO: crs transform to WGS84
            final GeometryFactory factory = new GeometryFactory();
            final Geometry point = factory.createPoint(this.coordinate);

            final TimeseriesRetrieverConfig config = new TimeseriesRetrieverConfig(
                    TimeseriesRetrieverConfig.PROTOCOL_TSTB,
                    SOS_FACTORY,
                    sosUrl,
                    procedure,
                    foi,
                    obsProp,
                    offering,
                    point,
                    new TimeInterval(
                        TimeInterval.Openness.OPEN,
                        TimeStamp.NEGATIVE_INFINITY,
                        TimeStamp.POSITIVE_INFINITY,
                        TimeInterval.Openness.OPEN));

            final TimeSeries timeseries = TimeseriesRetriever.getInstance().retrieve(config).get();
            final Object valueKeyObject = timeseries.getTSProperty(TimeSeries.VALUE_KEYS);
            final String name = obsProp;
            String humanReadableObsProp = "";                                   // NOI18N
            if (name != null) {
                final String[] splittedName = name.split(":");                  // NOI18N
                humanReadableObsProp = splittedName[splittedName.length - 1];
            }
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

            final Envelope envelope = (Envelope)timeseries.getTSProperty(TimeSeries.GEOMETRY);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Time Series Geometry max X / Y, min X/Y: " + envelope.getMaxX() + "/"
                            + envelope.getMaxY() // NOI18N
                            + ", "               // NOI18N
                            + envelope.getMinX() + "/" + envelope.getMinY()); // NOI18N
            }

            if (envelope == null) {
                return null;
            }

            // FIXME: PE HD hack because SOS does not deliver correct time series, should be testing for correct
            // coordinates (see history) (mscholl)
            final TimeSeries simpleTS = timeseries.slice(new TimeInterval(
                        TimeInterval.Openness.OPEN,
                        TimeStamp.NEGATIVE_INFINITY,
                        TimeStamp.POSITIVE_INFINITY,
                        TimeInterval.Openness.OPEN));
            simpleTS.setTSProperty(TimeSeries.OBSERVEDPROPERTY, humanReadableObsProp);
            for (final TimeStamp ts : timeStamps) {
                simpleTS.setValue(ts, valueKey, timeseries.getValue(ts, valueKey));
            }

            return simpleTS;
        }

        @Override
        protected void done() {
            final GeometryFactory gf = new GeometryFactory();
            final Point pointGeom = gf.createPoint(this.coordinate);

            try {
                final TimeSeries timeseries = get();
                if (timeseries == null) {
                    timeseriesCount++;
                    holdFeatures.put(timeseriesCount, createFeatureSignature(pointGeom, null));
                    fireHoldFeatureChanged();
                    return;
                }
                timeseries.setTSProperty(TimeSeries.GEOMETRY, pointGeom);
                if (!isOnHold()) {
                    tsVis.clearTimeSeries();
                    timeseriesCount = 0;
                }
                tsVis.addTimeSeries(timeseries);
                timeseriesCount++;
                final TimeSeriesSignature tss = tsVis.getLookup(TimeSeriesSignature.class);
                if (tss != null) {
                    final BufferedImage bi = tss.getTimeSeriesSignature(timeseries, overlayWidth, overlayHeight);

                    if (!isOnHold()) {
                        holdFeatures.clear();
                    }
                    holdFeatures.put(timeseriesCount, createFeatureSignature(pointGeom, bi));
                    fireHoldFeatureChanged();
                }

                if (!pnlChart.isAncestorOf(tsVis.getVisualisationUI())) {
                    pnlChart.removeAll();
                    pnlChart.add(new JScrollPane(tsVis.getVisualisationUI()), BorderLayout.CENTER);
                }
                if (toolbar == null) {
                    toolbar = tsVis.getToolbar();
                    if (toolbar instanceof TimeSeriesChartToolBar) {
                        ((TimeSeriesChartToolBar)toolbar).setShowOrigButtonEnabled(false);
                    }
                    pnlToolbar.add(toolbar, BorderLayout.CENTER);
                    pnlToolbar.invalidate();
                    pnlToolbar.revalidate();
                }

                if (tsVis.getTimeSeriesCollection().size() == 1) {
                    if (checkAggregateDisplaysTimeSeriesCount()) {
                        setAggregateButtonForAllDisplaysEnabled(true);
                    }
                } else {
                    setAggregateButtonForAllDisplaysEnabled(false);
                }

                Container parent = SOSFeatureInfoDisplay.this;
                Container current = parent.getParent();
                while (current != null) {
                    parent = current;
                    current = parent.getParent();
                }
                parent.invalidate();
                parent.validate();
            } catch (final CancellationException ex) {
                final String message = "action was cancelled";                          // NOI18N
                if (LOG.isDebugEnabled()) {
                    LOG.debug(message, ex);
                }
            } catch (final InterruptedException ex) {
                final String message = "in done nothing should be interrupted anymore"; // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            } catch (final ExecutionException ex) {
                final String message = "execution exception in worker thread";          // NOI18N
                LOG.error(message, ex.getCause());

                // As an error occured, the new resolution couldn't be loaded. For this reason,
                // cboResolution is set to the resolution that is always available
                cboResolution.setSelectedItem(Resolution.DECADE);

                JOptionPane.showMessageDialog(
                    SOSFeatureInfoDisplay.this,
                    java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/Bundle").getString(
                        "SosFeatureInfoDisplay.TimeSeriesDisplayer.done().JOptionPane.showMessageDialog(Component, String, String,int).errorMsg.message"),
                    java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/Bundle").getString(
                        "SosFeatureInfoDisplay.TimeSeriesDisplayer.done().JOptionPane.showMessageDialog(Component, String, String,int).errorMsg.title"),
                    JOptionPane.ERROR_MESSAGE);

                throw new IllegalStateException(message, ex.getCause());
            } finally {
                // unlock panel
                lockableUI.setLocked(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author   dmeiers
     * @version  $Revision$, $Date$
     */
    protected final class TimeSeriesFeature extends DefaultStyledFeature {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(TimeSeriesFeature.class);
        private FeatureAnnotationSymbol featureAnnotationSymbol;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TimeSeriesFeature object.
         *
         * @param  g    the geometry of the time series
         * @param  bi2  s the time series shape (from legend)
         */
        public TimeSeriesFeature(final Geometry g, final BufferedImage bi2) {
            super();
            setGeometry(g);
            BufferedImage featureIcon = null;
            try {
                final InputStream is = getClass().getResourceAsStream(
                        "/de/cismet/cismap/commons/gui/res/featureInfo.png"); // NOI18N
                featureIcon = ImageIO.read(is);
            } catch (final IOException ex) {
                LOG.warn("cannot load timeseries feature icon", ex);          // NOI18N
            }
            // set the overlay on the lower left edge of the icon as default..
            int xPos = featureIcon.getWidth() - overlayWidth;
            int yPos = featureIcon.getHeight() - overlayHeight;
            int bgR = 255;
            int bgG = 255;
            int bgB = 255;
            Color standardBG = new Color(bgR, bgG, bgB);

            // try to get metainformation for overlay position, width, color from properties file and override the
            // default values if succesfull
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
                        || !(iconProps.containsKey("overlayPositionX")                                                             // NOI18N
                            && iconProps.containsKey("overlayPositionY")
                            && iconProps.containsKey("overlayBackgroundColorR")
                            && iconProps.containsKey("overlayBackgroundColorG")
                            && iconProps.containsKey("overlayBackgroundColorB"))) {                                                // NOI18N
                LOG.warn(
                    "featureInfoIcon.properties file does not contain all needed keys. Default values for overlay area are used"); // NOI18N
            } else {
                try {
                    xPos = Integer.parseInt((String)iconProps.get("overlayPositionX"));                                            // NOI18N
                    yPos = Integer.parseInt((String)iconProps.get("overlayPositionY"));                                            // NOI18N
                    bgR = Integer.parseInt((String)iconProps.get("overlayBackgroundColorR"));
                    bgG = Integer.parseInt((String)iconProps.get("overlayBackgroundColorG"));
                    bgB = Integer.parseInt((String)iconProps.get("overlayBackgroundColorB"));
                    standardBG = new Color(bgR, bgG, bgB);
                } catch (NumberFormatException ex) {
                    Log.error(
                        "Error while retrieving properties for overlay area. Default values for overlay area are used",            // NOI18N
                        ex);
                }
            }
            final Graphics2D g2 = (Graphics2D)featureIcon.getSubimage(xPos, yPos, overlayWidth, overlayHeight)
                        .getGraphics();

            // paint the time series symbol
            g2.drawImage(bi2, 0, 0, standardBG, null);

            final FeatureAnnotationSymbol symb = new FeatureAnnotationSymbol(featureIcon);
            symb.setSweetSpotX(0.5);
            symb.setSweetSpotY(0.9);
            featureAnnotationSymbol = symb;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Creates a new TimeSeriesFeature object.
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public FeatureAnnotationSymbol getPointAnnotationSymbol() {
            return featureAnnotationSymbol;
        }
    }
}
