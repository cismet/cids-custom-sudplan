/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.Available;
import de.cismet.cids.custom.sudplan.Grid;
import de.cismet.cids.custom.sudplan.ImmutableGrid;
import de.cismet.cids.custom.sudplan.LocalisedEnumComboBox;
import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverException;
import de.cismet.cids.custom.sudplan.Variable;

import de.cismet.cids.dynamics.Disposable;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class AirqualityDownscalingOutputManagerUI extends javax.swing.JPanel implements Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingOutputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    // these two classes have to be initialised here as they're used by the cbos defined below
    private final transient Available<Resolution> resAvailable = new ResolutionAvailable();
    private final transient Available<Variable> varAvailable = new VariableAvailable();
    private final transient ItemListener resL;
    private final transient ItemListener varL;

    private final transient ActionListener showL;

    private final transient AirqualityDownscalingOutputManager model;

    private transient TimeSeries timeseries;

    private transient GridSliderWidget widget;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnShowInMap = new javax.swing.JButton();
    private final transient javax.swing.JComboBox cboResolution = new LocalisedEnumComboBox<Resolution>(
            Resolution.class,
            resAvailable);
    private final transient javax.swing.JComboBox cboVariable = new LocalisedEnumComboBox<Variable>(
            Variable.class,
            varAvailable);
    private final transient com.toedter.calendar.JDateChooser jdcEndDate = new com.toedter.calendar.JDateChooser();
    private final transient com.toedter.calendar.JDateChooser jdcStartDate = new com.toedter.calendar.JDateChooser();
    private final transient javax.swing.JProgressBar jpbDownload = new javax.swing.JProgressBar();
    private final transient javax.swing.JLabel lblDownload = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFrom = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResolution = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblTo = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblVariable = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlDownloadAndShow = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlProgess = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlTimerange = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlVarAndRes = new javax.swing.JPanel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingOutputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingOutputManagerUI(final AirqualityDownscalingOutputManager model) {
        this.model = model;
        this.showL = new ShowInMapListener();
        this.resL = new ResolutionItemListener();
        this.varL = new VariableItemListener();

        initComponents();

        init();

        btnShowInMap.addActionListener(WeakListeners.create(ActionListener.class, showL, btnShowInMap));
        cboVariable.addItemListener(WeakListeners.create(ItemListener.class, varL, cboVariable));
        cboResolution.addItemListener(WeakListeners.create(ItemListener.class, resL, cboResolution));

        // FIXME: doing two subsequent changes assures that the item listener will be triggered
        cboVariable.setSelectedItem(Variable.O3);
        cboVariable.setSelectedItem(Variable.NO2);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlVarAndRes.setBorder(javax.swing.BorderFactory.createTitledBorder("Variable and Resolution"));
        pnlVarAndRes.setOpaque(false);
        pnlVarAndRes.setLayout(new java.awt.GridBagLayout());

        lblVariable.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblVariable.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVarAndRes.add(lblVariable, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVarAndRes.add(cboVariable, gridBagConstraints);

        lblResolution.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblResolution.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVarAndRes.add(lblResolution, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVarAndRes.add(cboResolution, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(pnlVarAndRes, gridBagConstraints);

        pnlTimerange.setBorder(javax.swing.BorderFactory.createTitledBorder("Timerange"));
        pnlTimerange.setOpaque(false);
        pnlTimerange.setLayout(new java.awt.GridBagLayout());

        jdcStartDate.setEnabled(false);
        jdcStartDate.setMinimumSize(new java.awt.Dimension(130, 28));
        jdcStartDate.setOpaque(false);
        jdcStartDate.setPreferredSize(new java.awt.Dimension(130, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimerange.add(jdcStartDate, gridBagConstraints);

        jdcEndDate.setEnabled(false);
        jdcEndDate.setMinimumSize(new java.awt.Dimension(130, 28));
        jdcEndDate.setOpaque(false);
        jdcEndDate.setPreferredSize(new java.awt.Dimension(130, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimerange.add(jdcEndDate, gridBagConstraints);

        lblTo.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblTo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimerange.add(lblTo, gridBagConstraints);

        lblFrom.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblFrom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimerange.add(lblFrom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(pnlTimerange, gridBagConstraints);

        pnlDownloadAndShow.setBorder(javax.swing.BorderFactory.createTitledBorder("Download and Show"));
        pnlDownloadAndShow.setOpaque(false);
        pnlDownloadAndShow.setLayout(new java.awt.GridBagLayout());

        btnShowInMap.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.btnShowInMap.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlDownloadAndShow.add(btnShowInMap, gridBagConstraints);

        pnlProgess.setOpaque(false);
        pnlProgess.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pnlProgess.add(jpbDownload, gridBagConstraints);

        lblDownload.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblDownload.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pnlProgess.add(lblDownload, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlDownloadAndShow.add(pnlProgess, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(pnlDownloadAndShow, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void dispose() {
        // cannot dispose the internal widget as dispose is called when the listne
// CismapBroker.getInstance().getMappingComponent().removeInternalWidget(widget.getName());
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VariableItemListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                for (final Resolution r : Resolution.values()) {
                    if (resAvailable.isAvailable(r)) {
                        cboResolution.setSelectedItem(r);
                        break;
                    }
                }
            }
        }
    }

    /**
     * FIXME: atr hack to display appropriate timerange boundaries
     *
     * @version  $Revision$, $Date$
     */
    private final class ResolutionItemListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                final Resolution res = (Resolution)e.getItem();

                final GregorianCalendar startCal;
                final GregorianCalendar endCal;

                if (Resolution.DECADE.equals(res)) {
                    startCal = new GregorianCalendar(1965, 0, 1);
                    endCal = new GregorianCalendar(2095, 11, 31);
                } else {
                    startCal = new GregorianCalendar(2031, 0, 1);
                    endCal = new GregorianCalendar(2031, 11, 31);
                }

                jdcStartDate.setDate(startCal.getTime());
                jdcEndDate.setDate(endCal.getTime());
            } else {
                jdcEndDate.setDate(null);
                jdcStartDate.setDate(null);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ShowInMapListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            btnShowInMap.setEnabled(false);
            jpbDownload.setIndeterminate(true);
            if (Variable.O3.equals(cboVariable.getSelectedItem())) {
                final DownloaderO3 loader = new DownloaderO3();
                loader.start();
            } else if (Variable.NO2.equals(cboVariable.getSelectedItem())) {
                final DownloaderNO2 loader = new DownloaderNO2();
                loader.start();
            } else {
                LOG.error("cannot load variable"); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DownloaderO3 extends Thread {

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            final TimeseriesRetrieverConfig config;
            try {
                config = new TimeseriesRetrieverConfig(
                        "SOS-Dummy-Handler",                                        // NOI18N
                        new URL("http://dummy.org"),                                // NOI18N
                        "urn:ogc:object:STHLM:O3:A1B3:10y",                         // NOI18N
                        "urn:MyOrg:feature:grid3",                                  // NOI18N
                        Variable.O3.getPropertyKey(),
                        "STHLM-O3-A1B3-coverage-10y",                               // NOI18N
                        null,
                        TimeInterval.ALL_INTERVAL);
            } catch (MalformedURLException ex) {
                final String message = "cannot create retriever config";            // NOI18N
                LOG.error(message, ex);
                return;
            }

            final Future<TimeSeries> tsFuture;
            try {
                tsFuture = TimeseriesRetriever.getInstance().retrieve(config);
            } catch (final TimeseriesRetrieverException ex) {
                LOG.error("error creating TS retriever for config: " + config, ex); // NOI18N
                return;
            }

            try {
                timeseries = tsFuture.get();
            } catch (final Exception ex) {
                LOG.error("error retrieving timeseries: " + config); // NOI18N
                return;
            }

            final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 3021);
            final Coordinate[] bbox = new Coordinate[5];
            bbox[0] = new Coordinate(1580000, 6546000);
            bbox[1] = new Coordinate(1580000, 6648000);
            bbox[2] = new Coordinate(1682000, 6648000);
            bbox[3] = new Coordinate(1682000, 6546000);
            bbox[4] = new Coordinate(1580000, 6546000);
            final LinearRing ring = new LinearRing(new CoordinateArraySequence(bbox), factory);
            final Geometry geometry = factory.createPolygon(ring, new LinearRing[0]);

            // TODO: for demo purposes assume it is a yearly grid
            final Map<Date, Grid> gridmap = new HashMap<Date, Grid>();
            for (final TimeStamp stamp : timeseries.getTimeStamps()) {
                final Float[][] floatData = (Float[][])timeseries.getValue(stamp, "ts:value");
                final Double[][] doubleData = new Double[floatData.length][];
                for (int i = 0; i < floatData.length; ++i) {
                    doubleData[i] = new Double[floatData[i].length];
                    for (int j = 0; j < floatData[i].length; ++j) {
                        doubleData[i][j] = (double)floatData[i][j];
                    }
                }
                gridmap.put(stamp.asDate(), new ImmutableGrid(doubleData, geometry, "Ozone", "ppb"));
            }
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            mc.getFeatureCollection().removeAllFeatures();
            for (final Crs crs : mc.getCrsList()) {
                if ("EPSG:3021".equals(crs.getCode())) {
                    CismapBroker.getInstance().setSrs(crs);
                    break;
                }
            }

            final String name = (String)model.getCidsBean().getProperty("name");
            widget = new GridSliderWidget(name, gridmap, Resolution.DECADE);
            mc.addInternalWidget(name, MappingComponent.POSITION_NORTHEAST, widget);
            mc.getFeatureCollection().addFeature(widget);
            mc.zoomToAFeatureCollection(Arrays.asList((Feature)widget), true, false);

            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        jpbDownload.setIndeterminate(false);
                        jpbDownload.setValue(100);
                        jpbDownload.setString("completed");
                        SMSUtils.showMappingComponent();
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DownloaderNO2 extends Thread {

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            final TimeseriesRetrieverConfig config;
            try {
                config = new TimeseriesRetrieverConfig(
                        "SOS-Dummy-Handler",                                         // NOI18N
                        new URL("http://dummy.org"),                                 // NOI18N
                        "urn:ogc:object:STHLM:NO2:2031-1m",                          // NOI18N
                        "urn:MyOrg:feature:grid3",                                   // NOI18N
                        Variable.NO2.getPropertyKey(),
                        "STHLM-NO2-coverage-2031-1m",                                // NOI18N
                        null,
                        TimeInterval.ALL_INTERVAL);
            } catch (MalformedURLException ex) {
                final String message = "cannot create retriever config";             // NOI18N
                LOG.error(message, ex);
                return;
            }

            final Future<TimeSeries> tsFuture;
            try {
                tsFuture = TimeseriesRetriever.getInstance().retrieve(config);
            } catch (final TimeseriesRetrieverException ex) {
                LOG.error("error creating TS retriever for config: " + config, ex); // NOI18N
                return;
            }

            try {
                timeseries = tsFuture.get();
            } catch (final Exception ex) {
                LOG.error("error retrieving timeseries: " + config); // NOI18N
                return;
            }

            final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 3021);
            final Coordinate[] bbox = new Coordinate[5];
            bbox[0] = new Coordinate(1580000, 6546000);
            bbox[1] = new Coordinate(1580000, 6648000);
            bbox[2] = new Coordinate(1682000, 6648000);
            bbox[3] = new Coordinate(1682000, 6546000);
            bbox[4] = new Coordinate(1580000, 6546000);
            final LinearRing ring = new LinearRing(new CoordinateArraySequence(bbox), factory);
            final Geometry geometry = factory.createPolygon(ring, new LinearRing[0]);

            // TODO: for demo purposes assume it is a yearly grid
            final Map<Date, Grid> gridmap = new HashMap<Date, Grid>();
            for (final TimeStamp stamp : timeseries.getTimeStamps()) {
                final Float[][] floatData = (Float[][])timeseries.getValue(stamp, "ts:value");
                final Double[][] doubleData = new Double[floatData.length][];
                for (int i = 0; i < floatData.length; ++i) {
                    doubleData[i] = new Double[floatData[i].length];
                    for (int j = 0; j < floatData[i].length; ++j) {
                        doubleData[i][j] = (double)floatData[i][j];
                    }
                }
                gridmap.put(stamp.asDate(), new ImmutableGrid(doubleData, geometry, "NO₂", "ppb"));
            }

            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            mc.getFeatureCollection().removeAllFeatures();
            for (final Crs crs : mc.getCrsList()) {
                if ("EPSG:3021".equals(crs.getCode())) {
                    CismapBroker.getInstance().setSrs(crs);
                    break;
                }
            }

            final String name = (String)model.getCidsBean().getProperty("name");
            widget = new GridSliderWidget(name, gridmap, Resolution.MONTH);
            mc.addInternalWidget(name, MappingComponent.POSITION_NORTHEAST, widget);
            mc.getFeatureCollection().addFeature(widget);
            mc.zoomToAFeatureCollection(Arrays.asList((Feature)widget), true, false);

            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        jpbDownload.setIndeterminate(false);
                        jpbDownload.setValue(100);
                        jpbDownload.setString("completed");
                        SMSUtils.showMappingComponent();
                    }
                });
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
//            return Resolution.DECADE.equals(type);
            if (cboVariable == null) {
                return false;
            }

            if (Variable.O3.equals(cboVariable.getSelectedItem())) {
                return Resolution.DECADE.equals(type);
            } else if (Variable.NO2.equals(cboVariable.getSelectedItem())) {
                return Resolution.MONTH.equals(type);
            } else {
                return false;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VariableAvailable implements Available<Variable> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean isAvailable(final Variable type) {
            return Variable.O3.equals(type) || Variable.NO2.equals(type);
        }
    }
}
