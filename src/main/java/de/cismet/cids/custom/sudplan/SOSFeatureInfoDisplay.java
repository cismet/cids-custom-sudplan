/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.DataHandlerFactory;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeDuration;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import com.vividsolutions.jts.geom.Envelope;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.renderer.DefaultListRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import de.cismet.cismap.commons.gui.featureinfowidget.AbstractFeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplay;
import de.cismet.cismap.commons.gui.featureinfowidget.FeatureInfoDisplayKey;
import de.cismet.cismap.commons.gui.featureinfowidget.InitialisationException;
import de.cismet.cismap.commons.interaction.events.MapClickedEvent;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = FeatureInfoDisplay.class)
public class SOSFeatureInfoDisplay extends AbstractFeatureInfoDisplay {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SOSFeatureInfoDisplay.class);

    private static final String SOS_FACTORY = "SOS-SUDPLAN-Dummy"; // NOI18N

    public static final String KEY_SCENARIO = "scenario";                                              // NOI18N
    public static final String KEY_SOS_URL = "sos_url";                                                // NOI18N
    public static final String KEY_OBSERVED_PROP = "observed_property";                                // NOI18N
    public static final String KEY_FROM_YEAR = "from_year";                                            // NOI18N
    public static final String KEY_TO_YEAR = "to_year";                                                // NOI18N
    public static final String OBSERVED_PROP_URN_TEMPERATURE = "urn:ogc:def:property:OGC:Temperature"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient DataHandler sosHandler;
    private transient TimeInterval timeInterval;
    private transient String scenario;
    private transient String obsProp;
    private transient String sosUrl;
    private transient int fromYear;
    private transient int toYear;
    private transient boolean initialised;

    // will only be accessed from EDT
    private transient TimeSeriesDisplayer currentDisplayer;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JComboBox cboResolution = new javax.swing.JComboBox();
    private final transient javax.swing.JLabel lblFiller = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResolution = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlChart = new javax.swing.JPanel();
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cboResolution, gridBagConstraints);

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

        lblResolution.setText(NbBundle.getMessage(
                SOSFeatureInfoDisplay.class,
                "SOSFeatureInfoDisplay.lblResolution.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblResolution, gridBagConstraints);

        lblFiller.setText(NbBundle.getMessage(SOSFeatureInfoDisplay.class, "SOSFeatureInfoDisplay.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(lblFiller, gridBagConstraints);
    }                                                                                                                // </editor-fold>//GEN-END:initComponents

    @Override
    public void init(final Object layer, final JTabbedPane parentTabbedPane) throws InitialisationException {
        if (acceptLayer(layer.getClass())) {
            final SlidableWMSServiceLayerGroup layerGroup = (SlidableWMSServiceLayerGroup)layer;

            parseKeywords(layerGroup.getLayerInformation().getKeywords());

            final GregorianCalendar fromCal = new GregorianCalendar(fromYear, 0, 1);
            final GregorianCalendar toCal = new GregorianCalendar(toYear, 11, 31);
            timeInterval = new TimeInterval(
                    TimeInterval.Openness.OPEN,
                    new TimeStamp(fromCal.getTimeInMillis()),
                    new TimeStamp(toCal.getTimeInMillis()),
                    TimeInterval.Openness.OPEN);

            sosHandler = DataHandlerFactory.Lookup.lookup(SOS_FACTORY); // NOI18N

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
                        pd.getWriteMethod().invoke(sosHandler, new URL(sosUrl));
                    }
                }

                sosHandler.open();
            } catch (final Exception e) {
                final String message = "cannot initialise sos handler"; // NOI18N
                LOG.error(message, e);
                throw new InitialisationException(message, e);
            }

            initialised = true;
        } else {
            final String message = "invalid layer object: " + layer; // NOI18N
            LOG.error(message);
            throw new InitialisationException(message);
        }
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
        scenario = obsProp = sosUrl = null;
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
                    } else if (KEY_OBSERVED_PROP.equals(key)) {
                        obsProp = value;
                    } else if (KEY_SOS_URL.equals(key)) {
                        sosUrl = value;
                    } else if (KEY_FROM_YEAR.equals(key)) {
                        fromYear = Integer.parseInt(value);
                    } else if (KEY_TO_YEAR.equals(key)) {
                        toYear = Integer.parseInt(value);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("unreconised key: " + key); // NOI18N
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

        initResolution();
    }

    /**
     * DOCUMENT ME!
     */
    private void initResolution() {
        for (final Resolution r : Resolution.values()) {
            cboResolution.addItem(r);
        }

        cboResolution.setRenderer(new ResolutionRenderer());
        cboResolution.addActionListener(new ResolutionActionListener());
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
     * @return  DOCUMENT ME!
     *
     * @throws  InitialisationException  DOCUMENT ME!
     */
    private Datapoint createDataPoint() throws InitialisationException {
        final Properties filter = new Properties();
        filter.put(TimeSeries.PROCEDURE, "urn:ogc:object:AIRVIRO:SMHI:2");     // NOI18N
        filter.put(TimeSeries.FEATURE_OF_INTEREST, "urn:MyOrg:feature:grid2"); // NOI18N
        filter.put(TimeSeries.OBSERVEDPROPERTY, OBSERVED_PROP_URN_TEMPERATURE);
        filter.put(TimeSeries.OFFERING, "coverage-2");                         // NOI18N

        final Set<Datapoint> datapoints = sosHandler.getDatapoints(filter, DataHandler.Access.READ);
        if (datapoints.size() < 1) {
            final String message = "no timeseries data offered for " // NOI18N
                        + "scenario '" + scenario + "' and "         // NOI18N
                        + "observed_property '" + obsProp + "'";     // NOI18N
            LOG.error(message);
            throw new InitialisationException(message);
        } else if (datapoints.size() > 1) {
            final String message = "too many offerings for "         // NOI18N
                        + "scenario '" + scenario + "' and "         // NOI18N
                        + "observed_property '" + obsProp + "'";     // NOI18N
            LOG.error(message);
            throw new InitialisationException(message);
        }

        final Datapoint datapoint = datapoints.iterator().next();
        if (datapoint == null) {
            final String message = "datapoint is null for "      // NOI18N
                        + "scenario '" + scenario + "' and "     // NOI18N
                        + "observed_property '" + obsProp + "'"; // NOI18N
            LOG.error(message);
            throw new InitialisationException(message);
        }

        return datapoint;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   datapoint  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private XYDataset createDataset(final Datapoint datapoint) {
        final TimeSeries timeseries = datapoint.getTimeSeries(timeInterval);
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

        final TimeStamp[] timeStamps = timeseries.getTimeStampsArray();                                // getTimeStamps();
        final org.jfree.data.time.TimeSeries data = new org.jfree.data.time.TimeSeries("Temperature"); // NOI18N

        final Envelope envelope = (Envelope)timeseries.getTSProperty(TimeSeries.GEOMETRY);
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

            return new TimeSeriesCollection(data);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TimeStamp[] getTimeStamps() {
        final Resolution currentRes = (Resolution)cboResolution.getSelectedItem();

        final ArrayList<TimeStamp> timestamps = new ArrayList<TimeStamp>();

        TimeStamp current = timeInterval.getStart();

        while (current != null) {
            timestamps.add(current);
            current = nextTimeStamp(current, currentRes);

            // stop iteration if the subsequent timestamp is not within the timeinterval
            if (current.asDate().after(timeInterval.getEnd().asDate())) {
                current = null;
            }
        }

        return timestamps.toArray(new TimeStamp[timestamps.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   timestamp  DOCUMENT ME!
     * @param   r          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static TimeStamp nextTimeStamp(final TimeStamp timestamp, final Resolution r) {
        switch (r) {
            case DECADE: {
                return TimeStamp.add(timestamp, new TimeDuration(TimeDuration.DurationUnit.YEARS, 10));
            }
            case YEAR: {
                return TimeStamp.add(timestamp, new TimeDuration(TimeDuration.DurationUnit.YEARS, 1));
            }
            case MONTH: {
                return TimeStamp.add(timestamp, new TimeDuration(TimeDuration.DurationUnit.MONTHS, 1));
            }
            case DAY: {
                return TimeStamp.add(timestamp, new TimeDuration(86400000));
            }
            case HOUR: {
                return TimeStamp.add(timestamp, new TimeDuration(3600000));
            }
            default: {
                throw new IllegalStateException("unknown resolution"); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dataset  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "European scale data",
                "Years",
                "Kelvin",
                dataset,
                true,
                true,
                false);

        chart.setBackgroundPaint(getBackground());

        final XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.BLUE);
        plot.setRangeGridlinePaint(Color.BLUE);
        plot.setAxisOffset(new RectangleInsets(15d, 15d, 15d, 15d));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        final XYItemRenderer renderer = plot.getRenderer();
        if (renderer instanceof XYLineAndShapeRenderer) {
            final XYLineAndShapeRenderer xyRenderer = (XYLineAndShapeRenderer)renderer;
            xyRenderer.setBaseShapesVisible(true);
            xyRenderer.setBaseShapesFilled(true);
        }

        final DateAxis axis = (DateAxis)plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy"));

        return chart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   resolution  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isAvailable(final Resolution resolution) {
        return Resolution.DECADE.equals(resolution); // || Resolution.YEAR.equals(resolution);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ResolutionActionListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        private transient Resolution currentItem = (Resolution)cboResolution.getItemAt(0);

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Resolution selected = (Resolution)cboResolution.getSelectedItem();
            if (isAvailable(selected)) {
                currentItem = selected;
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
            final Datapoint datapoint = createDataPoint();
            final XYDataset dataset = createDataset(datapoint);

            return createChart(dataset);
        }

        @Override
        protected void done() {
            pnlChart.removeAll();

            try {
                pnlChart.add(new ChartPanel(get()), BorderLayout.CENTER);
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
     * @version  $Revision$, $Date$
     */
    private final class ResolutionRenderer extends DefaultListRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if ((value instanceof Resolution) && (c instanceof JLabel)) {
                final Resolution resolution = (Resolution)value;
                final JLabel label = (JLabel)c;

                switch (resolution) {
                    case DECADE: {
                        label.setText(NbBundle.getMessage(
                                SOSFeatureInfoDisplay.class,
                                "SOSFeatureInfoDisplay.ResolutionRenderer.getListCellRendererComponent(JList,Object,int,boolean,boolean).tenYearly"));               // NOI18N
                        break;
                    }
                    case YEAR: {
                        label.setText(NbBundle.getMessage(
                                SOSFeatureInfoDisplay.class,
                                "SOSFeatureInfoDisplay.ResolutionRenderer.getListCellRendererComponent(JList,Object,int,boolean,boolean).yearly"));                  // NOI18N
                        break;
                    }
                    case MONTH: {
                        label.setText(NbBundle.getMessage(
                                SOSFeatureInfoDisplay.class,
                                "SOSFeatureInfoDisplay.ResolutionRenderer.getListCellRendererComponent(JList,Object,int,boolean,boolean).monthly"));                 // NOI18N
                        break;
                    }
                    case DAY: {
                        label.setText(NbBundle.getMessage(
                                SOSFeatureInfoDisplay.class,
                                "SOSFeatureInfoDisplay.ResolutionRenderer.getListCellRendererComponent(JList,Object,int,boolean,boolean).daily"));                   // NOI18N
                        break;
                    }
                    case HOUR: {
                        label.setText(NbBundle.getMessage(
                                SOSFeatureInfoDisplay.class,
                                "SOSFeatureInfoDisplay.ResolutionRenderer.getListCellRendererComponent(JList,Object,int,boolean,boolean).hourly"));                  // NOI18N
                        break;
                    }
                    default: {
                        label.setText(
                            NbBundle.getMessage(
                                        SOSFeatureInfoDisplay.class,
                                        "SOSFeatureInfoDisplay.ResolutionRenderer.getListCellRendererComponent(JList,Object,int,boolean,boolean).unknownResolution") // NOI18N
                                    + resolution);
                        break;
                    }
                }

                label.setEnabled(isAvailable(resolution));
            }

            return c;
        }
    }
}
