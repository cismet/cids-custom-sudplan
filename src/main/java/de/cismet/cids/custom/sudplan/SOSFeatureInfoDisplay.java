/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.plugin.PluginRegistry;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.umd.cs.piccolo.PLayer;

import org.apache.log4j.Logger;

import org.jfree.util.Log;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics2D;
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
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSelectionNotification;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSignature;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
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
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplayKey;
import de.cismet.cismap.commons.gui.featureinfowidget.InitialisationException;
import de.cismet.cismap.commons.gui.featureinfowidget.MultipleFeatureInfoRequestsDisplay;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.HoldFeatureChangeEvent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.HoldListener;
import de.cismet.cismap.commons.interaction.events.MapClickedEvent;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

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
            TimeSeriesListChangedListener,
            TimeSeriesSelectionListener {

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
    // End of variables declaration
// private ArrayList<SignaturedFeature> holdFeatures = new ArrayList<SignaturedFeature>();
    private final HashMap<Integer, SignaturedFeature> holdFeatures = new HashMap<Integer, SignaturedFeature>();
    private final ArrayList<HoldListener> holdListeners = new ArrayList<HoldListener>();
    private JToolBar toolbar;
    private final TimeSeriesVisualisation tsVis;
    private int overlayWidth = 0;
    private int overlayHeight = 0;
    private boolean displayVisible = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JComboBox cboResolution =
        new de.cismet.cids.custom.sudplan.LocalisedEnumComboBox(Resolution.class, available);
    private final transient javax.swing.JCheckBox holdCheckBox = new javax.swing.JCheckBox();
    private final transient javax.swing.JLabel lblFiller = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFiller1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFiller3 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResolution = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlChart = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlControlElements = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlHoldButton = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlResolution = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlToolbar = new javax.swing.JPanel();
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
        tsVis = TimeSeriesVisualisationFactory.getInstance().createVisualisation(VisualisationType.SIMPLE);
        tsVis.addTimeSeriesListChangeListener(WeakListeners.create(TimeSeriesListChangedListener.class, this, tsVis));
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
        } catch (IOException ex) {
            LOG.error(
                "Could not read featureInfoIcon.properties file. Default values for overlay area are used",
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
        if (tsn != null) {
            tsn.addTimeSeriesSelectionListener(WeakListeners.create(TimeSeriesSelectionListener.class, this, tsVis));
        }
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

        pnlControlElements.setLayout(new java.awt.GridBagLayout());

        pnlHoldButton.setLayout(new java.awt.GridBagLayout());

        holdCheckBox.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.holdCheckBox.text"));        // NOI18N
        holdCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.holdCheckBox.toolTipText")); // NOI18N
        pnlHoldButton.add(holdCheckBox, new java.awt.GridBagConstraints());

        pnlControlElements.add(pnlHoldButton, new java.awt.GridBagConstraints());

        lblFiller3.setText(org.openide.util.NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.lblFiller3.text")); // NOI18N
        lblFiller3.setMaximumSize(new java.awt.Dimension(10, 0));
        lblFiller3.setMinimumSize(new java.awt.Dimension(10, 0));
        lblFiller3.setPreferredSize(new java.awt.Dimension(10, 0));
        pnlControlElements.add(lblFiller3, new java.awt.GridBagConstraints());

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
        add(pnlControlElements, gridBagConstraints);

        pnlToolbar.setMinimumSize(new java.awt.Dimension(500, 30));
        pnlToolbar.setPreferredSize(new java.awt.Dimension(500, 30));
        pnlToolbar.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlToolbar, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param   layer             evt DOCUMENT ME!
     * @param   parentTabbedPane  DOCUMENT ME!
     *
     * @throws  InitialisationException  DOCUMENT ME!
     */
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

    @Override
    public boolean isOnHold() {
        return this.holdCheckBox.isSelected();
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
    public void fireHoldFeatureChanged() {
        if (displayVisible) {
            final ArrayList<SignaturedFeature> featureList = new ArrayList<SignaturedFeature>();
            // TODO anderer weg um gelöschte herauszufinden, da auch null in map sein kann wenn neben envelope geklcikt
            // wurde
            for (final SignaturedFeature f : holdFeatures.values()) {
                featureList.add(f);
            }
            for (final HoldListener hl : holdListeners) {
                hl.holdFeautresChanged(new HoldFeatureChangeEvent(featureList, this));
            }
        }
    }

    @Override
    public void timeSeriesListChanged(final TimeSeriesListChangedEvent evt) {
        if (evt.getID() == TimeSeriesListChangedEvent.TIME_SERIES_REMOVED) {
            final TimeSeries ts = (TimeSeries)evt.getSource();
            boolean holdFeaturesChanged = false;
            // TODO prevent concurrent modification

            final Iterator<Integer> it;
            synchronized (holdFeatures) {
                it = holdFeatures.keySet().iterator();
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
        }
    }

    @Override
    public void selectionChanged(final TimeSeriesSelectionEvent evt) {
        final Collection<TimeSeries> selectedTS = evt.getSelectedTs();
        final CismapPlugin cismapPlugin = (CismapPlugin)PluginRegistry.getRegistry().getPlugin("cismap"); // NOI18N
        final MappingComponent mc = cismapPlugin.getMappingComponent();
        mc.getTmpFeatureLayer().removeAllChildren();
        mc.getRubberBandLayer().removeAllChildren();

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
            mc.repaint();
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
//        final PureNewFeature feature = new PureNewFeature(g);
        final TimeSeriesFeature feature = new TimeSeriesFeature(g, bi);
//        feature.setName("timeSeries Object");
        return feature;
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
    private final class TimeSeriesDisplayer extends SwingWorker<TimeSeries, Void> {

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
        protected TimeSeries doInBackground() throws Exception {
            final TimeSeries timeseries = TimeseriesRetriever.getInstance().retrieve(config).get();
            final Object valueKeyObject = timeseries.getTSProperty(TimeSeries.VALUE_KEYS);
            final String name = config.getObsProp();
            String humanReadableObsProp = "";
            if (name != null) {
                final String[] splittedName = name.split(":");
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
                LOG.debug("Time Series Geometry max X / Y, min X/Y: " + envelope.getMaxX() + "/" + envelope.getMaxY()
                            + ", " // NOI18N
                            + envelope.getMinX() + "/" + envelope.getMinY()); // NOI18N
            }

            if (envelope == null) {
                return null;
            } else if (envelope.contains(
                            currentDisplayer.getMce().getxCoord(),
                            currentDisplayer.getMce().getyCoord())) {
                final double width = envelope.getWidth();
                final double height = envelope.getHeight();
                final double xCoord = currentDisplayer.getMce().getxCoord();
                final double yCoord = currentDisplayer.getMce().getyCoord();
                final double xRelation = ((xCoord - envelope.getMinX()) / width);
                final double yRelation = ((yCoord - envelope.getMinY()) / height);
                final TimeSeries simpleTS = timeseries.slice(TimeInterval.ALL_INTERVAL);
                simpleTS.setTSProperty(TimeSeries.OBSERVEDPROPERTY, humanReadableObsProp);
                for (final TimeStamp ts : timeStamps) {
                    final Float[][] values = ((Float[][])timeseries.getValue(ts, valueKey));
                    // assume this is a rectangular grid
                    final int i = (int)(values.length * xRelation);
                    final int j = (int)(values[i].length * yRelation);
                    final float value = values[i][j];
                    simpleTS.setValue(ts, valueKey, value);
                }
                return simpleTS;
            }
            return null;
        }

        @Override
        protected void done() {
            final GeometryFactory gf = new GeometryFactory();
            final double xCoord = getMce().getxCoord();
            final double yCoord = getMce().getyCoord();
            final Point pointGeom = gf.createPoint(new Coordinate(xCoord, yCoord));
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
                    pnlChart.add(tsVis.getVisualisationUI(), BorderLayout.CENTER);
                }
                if (toolbar == null) {
                    toolbar = tsVis.getToolbar();
                    pnlToolbar.add(toolbar, BorderLayout.CENTER);
                    pnlToolbar.invalidate();
                    pnlToolbar.revalidate();
                }

                Container parent = SOSFeatureInfoDisplay.this;
                Container current = parent.getParent();
                while (current != null) {
                    parent = current;
                    current = parent.getParent();
                }
                parent.invalidate();
                parent.validate();
//                SwingUtilities.invokeLater(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            tsVis.resizeScrollbar();
//                        }
//                    });
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
//        final BufferedImage bi = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
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
