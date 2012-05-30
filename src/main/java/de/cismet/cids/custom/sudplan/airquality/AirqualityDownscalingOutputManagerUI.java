/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.net.MalformedURLException;
import java.net.URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.MessageFormat;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.concurrent.Future;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.sudplan.Available;
import de.cismet.cids.custom.sudplan.LocalisedEnumComboBox;
import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverException;
import de.cismet.cids.custom.sudplan.Variable;
import de.cismet.cids.custom.sudplan.airquality.AirqualityDownscalingOutput.Result;
import de.cismet.cids.custom.sudplan.geoserver.AttributesAwareGSFeatureTypeEncoder;
import de.cismet.cids.custom.sudplan.geoserver.GSAttributeEncoder;
import de.cismet.cids.custom.sudplan.geoserver.GSPathAwareLayerEncoder;

import de.cismet.cids.dynamics.Disposable;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class AirqualityDownscalingOutputManagerUI extends javax.swing.JPanel implements Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingOutputManagerUI.class);

    private static final transient String DB_URL = "jdbc:postgresql://sudplan.cismet.de:5433/sudplan"; // NOI18N
    private static final transient String DB_USER = "postgres";                                        // NOI18N
    private static final transient String DB_PASSWORD = "cismetz12";                                   // NOI18N
    private static final transient String DB_TABLE = "downscaled_airquality";                          // NOI18N
    private static final transient String DB_VIEW_SEPARATOR = "_";                                     // NOI18N
    private static final transient String DB_VIEW_NAME_PATTERN = "aqds" + DB_VIEW_SEPARATOR
                + "view" + DB_VIEW_SEPARATOR                                                           // NOI18N
                + "{0}" + DB_VIEW_SEPARATOR                                                            // NOI18N
                + "{1}" + DB_VIEW_SEPARATOR                                                            // NOI18N
                + "{2}" + DB_VIEW_SEPARATOR                                                            // NOI18N
                + "{3}";                                                                               // NOI18N
    private static final transient String DB_STMT_CREATE_VIEW = " CREATE VIEW "
                + "{0} AS "                                                                            // NOI18N
                + " SELECT modeloutput_id, variable, resolution, \"timestamp\", geometry, value, unit, offering "
                + " FROM "                                                                             // NOI18N
                + DB_TABLE
                + " WHERE modeloutput_id = {1} "
                + " AND variable ILIKE ''{2}'' "                                                       // NOI18N
                + " AND resolution ILIKE ''{3}''"
                + " AND \"timestamp\" = {4}";                                                          // NOI18N
    // TODO: Dynymic SRS?
    private static final transient String DB_QUERY_BOUNDINGBOX = "SELECT "
                + " ST_XMIN(st_extent(geometry)) AS native_xmin,"                                       // NOI18N
                + " ST_YMIN(st_extent(geometry)) AS native_ymin,"
                + " ST_XMAX(st_extent(geometry)) AS native_xmax,"                                       // NOI18N
                + " ST_YMAX(st_extent(geometry)) AS native_ymax,"
                + " ST_XMIN(TRANSFORM(ST_SetSRID(st_extent(geometry), 3021), 4326)) AS lat_lon_xmin,"   // NOI18N
                + " ST_YMIN(TRANSFORM(ST_SetSRID(st_extent(geometry), 3021), 4326)) AS lat_lon_ymin,"
                + " ST_XMAX(TRANSFORM(ST_SetSRID(st_extent(geometry), 3021), 4326)) AS lat_lon_xmax,"   // NOI18N
                + " ST_YMAX(TRANSFORM(ST_SetSRID(st_extent(geometry), 3021), 4326)) AS lat_lon_ymax"
                + " FROM ";                                                                             // NOI18N
    private static final transient String GEOSERVER_REST_URL = "http://sudplanwp6.cismet.de/geoserver"; // NOI18N
    private static final transient String GEOSERVER_REST_PASSWORD = "cismetz12";                        // NOI18N
//    private static final transient String GEOSERVER_REST_URL = "http://localhost:8080/geoserver"; // NOI18N
//    private static final transient String GEOSERVER_REST_PASSWORD = "geoserver";                  // NOI18N
    private static final transient String GEOSERVER_REST_USER = "admin";          // NOI18N
    private static final transient String GEOSERVER_CRS = "PROJCS[\"RT90 2.5 gon V\","
                + "    GEOGCS[\"RT90\","                                          // NOI18N
                + "        DATUM[\"Rikets_koordinatsystem_1990\","
                + "            SPHEROID[\"Bessel 1841\",6377397.155,299.1528128," // NOI18N
                + "                AUTHORITY[\"EPSG\",\"7004\"]],"
                + "            AUTHORITY[\"EPSG\",\"6124\"]],"                    // NOI18N
                + "        PRIMEM[\"Greenwich\",0,"
                + "            AUTHORITY[\"EPSG\",\"8901\"]],"                    // NOI18N
                + "        UNIT[\"degree\",0.01745329251994328,"
                + "            AUTHORITY[\"EPSG\",\"9122\"]],"                    // NOI18N
                + "        AUTHORITY[\"EPSG\",\"4124\"]],"
                + "    UNIT[\"metre\",1,"                                         // NOI18N
                + "        AUTHORITY[\"EPSG\",\"9001\"]],"
                + "    PROJECTION[\"Transverse_Mercator\"],"                      // NOI18N
                + "    PARAMETER[\"latitude_of_origin\",0],"
                + "    PARAMETER[\"central_meridian\",15.80827777777778],"        // NOI18N
                + "    PARAMETER[\"scale_factor\",1],"
                + "    PARAMETER[\"false_easting\",1500000],"                     // NOI18N
                + "    PARAMETER[\"false_northing\",0],"
                + "    AUTHORITY[\"EPSG\",\"3021\"],"                             // NOI18N
                + "    AXIS[\"Y\",EAST],"
                + "    AXIS[\"X\",NORTH]]";                                       // NOI18N
    private static final transient String GEOSERVER_SLD = "aqds";                 // NOI18N
    private static final transient String GEOSERVER_DATASTORE = "sudplandb";      // NOI18N
    private static final transient String GEOSERVER_WORKSPACE = "sudplantest";    // NOI18N
    // TODO: Make dynamic?!
    private static final transient String GEOSERVER_SRS = "EPSG:3021";    // NOI18N

    // these two classes have to be initialised
                                                                          // here as they're used by the cbos defined
                                                                          // below

    //~ Instance fields --------------------------------------------------------

    // NOI18N

    // these two classes have to be initialised here as they're used by the cbos defined below
    private final transient Available<Resolution> resolutionAvailable = new ResolutionAvailable();
    private final transient Available<Variable> variableAvailable = new VariableAvailable();

    private final transient ItemListener resolutionListener;
    private final transient ItemListener variableListener;
    private final transient ActionListener showInMapListener;

    private final transient AirqualityDownscalingOutputManager model;
    private transient Result resultToShow;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnShowInMap = new javax.swing.JButton();
    private final transient javax.swing.JComboBox cboResolution = new LocalisedEnumComboBox<Resolution>(
            Resolution.class,
            resolutionAvailable);
    private final transient javax.swing.JComboBox cboVariable = new LocalisedEnumComboBox<Variable>(
            Variable.class,
            variableAvailable);
    private final transient javax.swing.JProgressBar jpbDownload = new javax.swing.JProgressBar();
    private final transient javax.swing.JLabel lblDownload = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFrom = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResolution = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblTo = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblVariable = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlDownloadAndShow = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlProgess = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlTimePeriod = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlVariableAndResolution = new javax.swing.JPanel();
    private final transient javax.swing.JScrollPane scpOfferings = new javax.swing.JScrollPane();
    private final transient javax.swing.JTextArea txaOfferings = new javax.swing.JTextArea();
    private final transient org.jdesktop.swingx.JXDatePicker xdpEndDate = new org.jdesktop.swingx.JXDatePicker();
    private final transient org.jdesktop.swingx.JXDatePicker xdpStartDate = new org.jdesktop.swingx.JXDatePicker();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingOutputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingOutputManagerUI(final AirqualityDownscalingOutputManager model) {
        this.model = model;
        this.showInMapListener = new ShowInMapListener();
        this.resolutionListener = new ResolutionListener();
        this.variableListener = new VariableListener();

        initComponents();

        init();

        btnShowInMap.addActionListener(WeakListeners.create(ActionListener.class, showInMapListener, btnShowInMap));
        cboVariable.addItemListener(WeakListeners.create(ItemListener.class, variableListener, cboVariable));
        cboResolution.addItemListener(WeakListeners.create(ItemListener.class, resolutionListener, cboResolution));

        // FIXME: doing two subsequent changes assures that the item listener will be triggered
        cboVariable.setSelectedItem(Variable.O3);
        cboVariable.setSelectedItem(Variable.NO2);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        final Collection<Result> results;

        try {
            results = model.getUR().getResults();
        } catch (Exception ex) {
            LOG.error("Couldn't get output bean.", ex); // NOI18N
            return;
        }

        final StringBuilder resultsAsText = new StringBuilder();

        for (final Result result : results) {
            if (resultToShow == null) {
                resultToShow = result;
            } else {
                final Resolution currentResolution = resultToShow.getResolution();
                final Resolution candidateResolution = result.getResolution();

                if (Resolution.DECADE.equals(currentResolution)) {
                    continue;
                } else if (Resolution.YEAR.equals(currentResolution) && Resolution.DECADE.equals(candidateResolution)) {
                    resultToShow = result;
                } else if (Resolution.MONTH.equals(currentResolution)
                            && (Resolution.DECADE.equals(candidateResolution)
                                || Resolution.YEAR.equals(candidateResolution))) {
                    resultToShow = result;
                } else if (Resolution.DAY.equals(currentResolution)
                            && (Resolution.DECADE.equals(candidateResolution)
                                || Resolution.YEAR.equals(candidateResolution)
                                || Resolution.MONTH.equals(candidateResolution))) {
                    resultToShow = result;
                } else if (Resolution.HOUR.equals(currentResolution)
                            && !(Resolution.HOUR.equals(candidateResolution))) {
                    resultToShow = result;
                }
            }

            resultsAsText.append("URL: '");            // NOI18N
            resultsAsText.append(result.getUrl());
            resultsAsText.append("'; Type: '");        // NOI18N
            resultsAsText.append(result.getType());
            resultsAsText.append("'; Description: '"); // NOI18N
            resultsAsText.append(result.getDescription());
            resultsAsText.append("'; Offering: '");    // NOI18N
            resultsAsText.append(result.getOffering());
            resultsAsText.append("'\n");               // NOI18N
        }

        if (resultToShow != null) {
            resultsAsText.append("Result for preview: URL: '"); // NOI18N
            resultsAsText.append(resultToShow.getUrl());
            resultsAsText.append("'; Type: '");                 // NOI18N
            resultsAsText.append(resultToShow.getType());
            resultsAsText.append("'; Description: '");          // NOI18N
            resultsAsText.append(resultToShow.getDescription());
            resultsAsText.append("'; Offering: '");             // NOI18N
            resultsAsText.append(resultToShow.getOffering());
            resultsAsText.append("'\n");                        // NOI18N
        } else {
            btnShowInMap.setEnabled(false);
        }

        txaOfferings.setText(resultsAsText.toString());
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

        pnlVariableAndResolution.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    AirqualityDownscalingOutputManagerUI.class,
                    "AirqualityDownscalingOutputManagerUI.pnlVariableAndResolution.border.title"))); // NOI18N
        pnlVariableAndResolution.setOpaque(false);
        pnlVariableAndResolution.setLayout(new java.awt.GridBagLayout());

        lblVariable.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblVariable.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVariableAndResolution.add(lblVariable, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVariableAndResolution.add(cboVariable, gridBagConstraints);

        lblResolution.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblResolution.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVariableAndResolution.add(lblResolution, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlVariableAndResolution.add(cboResolution, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(pnlVariableAndResolution, gridBagConstraints);

        pnlTimePeriod.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    AirqualityDownscalingOutputManagerUI.class,
                    "AirqualityDownscalingOutputManagerUI.pnlTimePeriod.border.title"))); // NOI18N
        pnlTimePeriod.setOpaque(false);
        pnlTimePeriod.setLayout(new java.awt.GridBagLayout());

        lblTo.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblTo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimePeriod.add(lblTo, gridBagConstraints);

        lblFrom.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblFrom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimePeriod.add(lblFrom, gridBagConstraints);

        xdpStartDate.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimePeriod.add(xdpStartDate, gridBagConstraints);

        xdpEndDate.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTimePeriod.add(xdpEndDate, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(pnlTimePeriod, gridBagConstraints);

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

        txaOfferings.setColumns(20);
        txaOfferings.setRows(5);
        scpOfferings.setViewportView(txaOfferings);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scpOfferings, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void dispose() {
        // cannot dispose the internal widget as dispose is called when the listne
        // CismapBroker.getInstance().getMappingComponent().removeInternalWidget(widget.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            DevelopmentTools.createRendererInFrameFromRMIConnectionOnLocalhost(
                "SUDPLAN",         // NOI18N
                "Administratoren", // NOI18N
                "admin",           // NOI18N
                "cismetadmin",     // NOI18N
                "modeloutput",     // NOI18N
                305,
                "Titel",           // NOI18N
                1024,
                768);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VariableListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                for (final Resolution r : Resolution.values()) {
                    if (resolutionAvailable.isAvailable(r)) {
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
    private final class ResolutionListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                final Resolution res = (Resolution)e.getItem();
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
            // TODO: Show resultToShow in map
            btnShowInMap.setEnabled(false);
            jpbDownload.setIndeterminate(true);

            final Integer modelId;
            if ((model.getCidsBean() != null) && (model.getCidsBean().getProperty("id") instanceof Integer)) { // NOI18N
                modelId = (Integer)model.getCidsBean().getProperty("id");                                      // NOI18N
                new Downloader(resultToShow, modelId).start();
            } else {
                LOG.error("Model output bean has an invalid id.");                                             // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class Downloader extends Thread {

        //~ Instance fields ----------------------------------------------------

        private final Result result;
        private final Integer modelId;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Downloader object.
         *
         * @param  result   DOCUMENT ME!
         * @param  modelId  DOCUMENT ME!
         */
        public Downloader(final Result result, final Integer modelId) {
            this.result = result;
            this.modelId = modelId;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private TimeSeries retrieveTimeSeries() {
            TimeSeries result = null;
            final TimeseriesRetrieverConfig config;

            try {
                config = new TimeseriesRetrieverConfig(
                        TimeseriesRetrieverConfig.PROTOCOL_TSTB,
                        AirqualityDownscalingModelManager.AQ_SOS_LOOKUP,
                        new URL(this.result.getUrl()),
                        null,
                        null,
                        null,
                        this.result.getOffering(),
                        null,
                        new TimeInterval(
                            TimeInterval.Openness.OPEN,
                            TimeStamp.NEGATIVE_INFINITY,
                            TimeStamp.POSITIVE_INFINITY,
                            TimeInterval.Openness.OPEN));
            } catch (MalformedURLException ex) {
                final String message = "Can't create retriever config."; // NOI18N
                LOG.error(message, ex);
                return result;
            }

            final Future<TimeSeries> tsFuture;
            try {
                tsFuture = TimeseriesRetriever.getInstance().retrieve(config);
            } catch (final TimeseriesRetrieverException ex) {
                LOG.error("Error creating TimeSeries retriever for config '" + config + "'.", ex); // NOI18N //NOI18N
                return result;
            }

            try {
                result = tsFuture.get();
            } catch (final Exception ex) {
                LOG.error("Error retrieving timeseries for config '" + config + "'.", ex); // NOI18N //NOI18N
            }

            return result;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   timeseries  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private Geometry[][] createGeometries(final TimeSeries timeseries) {
            final Geometry[][] result = new Geometry[0][0];
            final Envelope envelope;
            final int gridcellCountX;
            final int gridcellCountY;
            final double gridcellSize;

            final Object envelopeObject = timeseries.getTSProperty(TimeSeries.GEOMETRY);
            // TODO: Make SRS dynamic?!
            final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING),
                    3021);

            if (envelopeObject instanceof Envelope) {
                envelope = (Envelope)envelopeObject;
            } else {
                LOG.error("Timeseries doesn't have a geometry."); // NOI18N
                return result;
            }

            final Object spatialResolutionObject = timeseries.getTSProperty(PropertyNames.SPATIAL_RESOLUTION);
            if ((spatialResolutionObject instanceof Integer[]) && (((Integer[])spatialResolutionObject).length == 2)) {
                gridcellCountX = ((Integer[])spatialResolutionObject)[0];
                gridcellCountY = ((Integer[])spatialResolutionObject)[1];
                gridcellSize = envelope.getWidth() / gridcellCountX;
            } else {
                LOG.error("The spatial resolution of selected timeseries is invalid."); // NOI18N
                return result;
            }

            final Geometry[][] geometries = new Geometry[gridcellCountX][gridcellCountY];
            for (int x = 0; x < gridcellCountX; x++) {
                for (int y = 0; y < gridcellCountY; y++) {
                    final Envelope cellEnvelope = new Envelope(
                            envelope.getMinX()
                                    + (x * gridcellSize),
                            envelope.getMinX()
                                    + ((x + 1) * gridcellSize),
                            envelope.getMinY()
                                    + (y * gridcellSize),
                            envelope.getMinY()
                                    + ((y + 1) * gridcellSize));
                    geometries[x][y] = geometryFactory.toGeometry(cellEnvelope);
                }
            }

            return geometries;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  SQLException            DOCUMENT ME!
         * @throws  ClassNotFoundException  DOCUMENT ME!
         */
        private Connection openConnection() throws SQLException, ClassNotFoundException {
            Class.forName("org.postgresql.Driver"); // NOI18N
            final Connection connection = DriverManager.getConnection(
                    DB_URL,
                    DB_USER,
                    DB_PASSWORD);
            connection.setAutoCommit(false);
            return connection;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   connection  valueKey DOCUMENT ME!
         * @param   floatData   insertStatement DOCUMENT ME!
         * @param   variable    DOCUMENT ME!
         * @param   stamp       DOCUMENT ME!
         * @param   geometries  DOCUMENT ME!
         * @param   unit        DOCUMENT ME!
         * @param   viewName    createViewStatement DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  SQLException  DOCUMENT ME!
         */
        private Double[][] writeValuesToDatabase(
                final Connection connection,
                final Float[][] floatData,
                final String variable,
                final long stamp,
                final Geometry[][] geometries,
                final String unit,
                final String viewName) throws SQLException {
            final Statement insertStatement = connection.createStatement();
            final Statement boundingBoxQuery = connection.createStatement();

            // TODO: Drop values if already inserted by a (broken) previous download.
            final StringBuilder insertStatementBuilder = new StringBuilder(
                    "INSERT INTO downscaled_airquality(modeloutput_id, variable, resolution, \"timestamp\", geometry, value, unit) VALUES("); // NOI18N

            insertStatementBuilder.append(modelId);
            insertStatementBuilder.append(",'");  // NOI18N
            insertStatementBuilder.append(variable);
            insertStatementBuilder.append("','"); // NOI18N
            insertStatementBuilder.append(result.getResolution().getOfferingSuffix());
            insertStatementBuilder.append("',");  // NOI18N
            insertStatementBuilder.append(stamp);
            insertStatementBuilder.append(",");   // NOI18N

            for (int i = 0; i < floatData.length; ++i) {
                for (int j = 0; j < floatData[i].length; ++j) {
                    final StringBuilder insertValueBuilder = new StringBuilder(
                            insertStatementBuilder.toString());

                    insertValueBuilder.append("setSrid('"); // NOI18N
                    insertValueBuilder.append(geometries[i][j].toText());
                    // TODO: Dynamic SRID?
                    insertValueBuilder.append("'::geometry, 3021)"); // NOI18N
                    insertValueBuilder.append(",");                  // NOI18N
                    insertValueBuilder.append(Float.toString(floatData[i][j]));
                    insertValueBuilder.append(",'");                 // NOI18N
                    insertValueBuilder.append(unit);
                    insertValueBuilder.append("');");                // NOI18N

                    insertStatement.addBatch(insertValueBuilder.toString());
                }
            }

            insertStatement.addBatch(MessageFormat.format(
                    DB_STMT_CREATE_VIEW,
                    viewName,
                    modelId,
                    variable,
                    result.getResolution().getOfferingSuffix(),
                    Long.toString(stamp)));
            insertStatement.executeBatch();

            insertStatement.close();

            final ResultSet resultSet = boundingBoxQuery.executeQuery(DB_QUERY_BOUNDINGBOX + viewName);

            if (!resultSet.next()) {
                // TODO!!!
            }

            final Double[][] result = new Double[2][4];

            result[0][0] = resultSet.getDouble("native_xmin");  // NOI18N
            result[0][1] = resultSet.getDouble("native_ymin");  // NOI18N
            result[0][2] = resultSet.getDouble("native_xmax");  // NOI18N
            result[0][3] = resultSet.getDouble("native_ymax");  // NOI18N
            result[1][0] = resultSet.getDouble("lat_lon_xmin"); // NOI18N
            result[1][1] = resultSet.getDouble("lat_lon_ymin"); // NOI18N
            result[1][2] = resultSet.getDouble("lat_lon_xmax"); // NOI18N
            result[1][3] = resultSet.getDouble("lat_lon_ymax"); // NOI18N

            boundingBoxQuery.close();

            connection.commit();

            return result;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   name   DOCUMENT ME!
         * @param   stamp  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private AttributesAwareGSFeatureTypeEncoder createFeatureType(final String name,
                final TimeStamp stamp) {
            final AttributesAwareGSFeatureTypeEncoder featureType = new AttributesAwareGSFeatureTypeEncoder();
            featureType.setName(name);
            featureType.setEnabled(true);
            featureType.setSRS(GEOSERVER_SRS);
            featureType.setProjectionPolicy(ProjectionPolicy.FORCE_DECLARED);

            final StringBuilder title = new StringBuilder();
            final Calendar now = new GregorianCalendar();
            now.setTimeInMillis(stamp.asMilis());
            if (Resolution.DECADE.equals(result.getResolution())) {
                title.append(now.get(Calendar.YEAR));
                title.append("\u2013"); // NOI18N
                now.add(Calendar.YEAR, 10);
                title.append(now.get(Calendar.YEAR));
            } else if (Resolution.YEAR.equals(result.getResolution())) {
                title.append(now.get(Calendar.YEAR));
            } else if (Resolution.MONTH.equals(result.getResolution())) {
                title.append(now.get(Calendar.MONTH));
                title.append('.');
                title.append(now.get(Calendar.YEAR));
            } else if (Resolution.DAY.equals(result.getResolution())) {
                title.append(now.get(Calendar.DAY_OF_MONTH));
                title.append('.');
                title.append(now.get(Calendar.MONTH));
                title.append('.');
                title.append(now.get(Calendar.YEAR));
            } else if (Resolution.HOUR.equals(result.getResolution())) {
                title.append(now.get(Calendar.DAY_OF_MONTH));
                title.append('.');
                title.append(now.get(Calendar.MONTH));
                title.append('.');
                title.append(now.get(Calendar.YEAR));
                title.append(' ');
                title.append(now.get(Calendar.HOUR_OF_DAY));
                title.append('h');
            } else {
                title.append(stamp.asMilis());
            }

            featureType.setTitle(title.toString());

            GSAttributeEncoder attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "modeloutput_id");       // NOI18N
            attribute.addEntry("minOccurs", "1");               // NOI18N
            attribute.addEntry("maxOccurs", "1");               // NOI18N
            attribute.addEntry("nillable", "false");            // NOI18N
            attribute.addEntry("binding", "java.lang.Integer"); // NOI18N
            featureType.addAttribute(attribute);

            attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "variable");            // NOI18N
            attribute.addEntry("minOccurs", "1");              // NOI18N
            attribute.addEntry("maxOccurs", "1");              // NOI18N
            attribute.addEntry("nillable", "false");           // NOI18N
            attribute.addEntry("binding", "java.lang.String"); // NOI18N
            featureType.addAttribute(attribute);

            attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "resolution");          // NOI18N
            attribute.addEntry("minOccurs", "1");              // NOI18N
            attribute.addEntry("maxOccurs", "1");              // NOI18N
            attribute.addEntry("nillable", "false");           // NOI18N
            attribute.addEntry("binding", "java.lang.String"); // NOI18N
            featureType.addAttribute(attribute);

            attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "timestamp");         // NOI18N
            attribute.addEntry("minOccurs", "1");            // NOI18N
            attribute.addEntry("maxOccurs", "1");            // NOI18N
            attribute.addEntry("nillable", "false");         // NOI18N
            attribute.addEntry("binding", "java.lang.Long"); // NOI18N
            featureType.addAttribute(attribute);

            attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "geometry");                                // NOI18N
            attribute.addEntry("minOccurs", "1");                                  // NOI18N
            attribute.addEntry("maxOccurs", "1");                                  // NOI18N
            attribute.addEntry("nillable", "false");                               // NOI18N
            attribute.addEntry("binding", "com.vividsolutions.jts.geom.Geometry"); // NOI18N
            featureType.addAttribute(attribute);

            attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "value");              // NOI18N
            attribute.addEntry("minOccurs", "1");             // NOI18N
            attribute.addEntry("maxOccurs", "1");             // NOI18N
            attribute.addEntry("nillable", "false");          // NOI18N
            attribute.addEntry("binding", "java.lang.Float"); // NOI18N
            featureType.addAttribute(attribute);

            attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "unit");                // NOI18N
            attribute.addEntry("minOccurs", "0");              // NOI18N
            attribute.addEntry("maxOccurs", "1");              // NOI18N
            attribute.addEntry("nillable", "true");            // NOI18N
            attribute.addEntry("binding", "java.lang.String"); // NOI18N
            featureType.addAttribute(attribute);

            attribute = new GSAttributeEncoder();
            attribute.addEntry("name", "offering");            // NOI18N
            attribute.addEntry("minOccurs", "0");              // NOI18N
            attribute.addEntry("maxOccurs", "1");              // NOI18N
            attribute.addEntry("nillable", "true");            // NOI18N
            attribute.addEntry("binding", "java.lang.String"); // NOI18N
            featureType.addAttribute(attribute);

            return featureType;
        }

        @Override
        public void run() {
            final TimeSeries timeseries = retrieveTimeSeries();
            if (timeseries == null) {
                LOG.error("Error retrieving timeseries."); // NOI18N
                return;
            }

            final Geometry[][] geometries = createGeometries(timeseries);

            final Object valueKeysObject = timeseries.getTSProperty(TimeSeries.VALUE_KEYS);
            final Object unitsObject = timeseries.getTSProperty(TimeSeries.VALUE_UNITS);
            final String valueKey;
            final String unit;

            if ((valueKeysObject instanceof String[]) && (((String[])valueKeysObject).length == 1)
                        && (unitsObject instanceof String[])
                        && (((String[])unitsObject).length > 0)) {
                valueKey = ((String[])valueKeysObject)[0];
                final String unitFromTimeseries = ((String[])unitsObject)[0];
                unit = unitFromTimeseries.substring(unitFromTimeseries.lastIndexOf(":") + 1); // NOI18N
            } else {
                LOG.error("The valueKey or unit of selected timeseries is invalid.");         // NOI18N
                return;
            }

            String variable;
            if ((result.getVariable() != null) && (result.getVariable().getPropertyKey() != null)) {
                variable = result.getVariable().getPropertyKey();
                variable = variable.substring(variable.lastIndexOf(":") + 1); // NOI18N
            } else {
                LOG.error("The variable of the result is invalid.");          // NOI18N
                return;
            }

            final GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(
                    GEOSERVER_REST_URL,
                    GEOSERVER_REST_USER,
                    GEOSERVER_REST_PASSWORD);
            final Connection connection;
            try {
                connection = openConnection();
            } catch (Exception ex) {
                LOG.error("Couldn't connect to database.", ex); // NOI18N
                return;
            }

            try {
                // TODO: for demo purposes assume it is a yearly grid
                for (final TimeStamp stamp : timeseries.getTimeStamps()) {
                    final String viewName = MessageFormat.format(
                                DB_VIEW_NAME_PATTERN,
                                modelId,
                                variable,
                                result.getResolution().getOfferingSuffix(),
                                Long.toString(stamp.asMilis()))
                                .toLowerCase();

                    final Double[][] boundaries = writeValuesToDatabase(
                            connection,
                            (Float[][])timeseries.getValue(stamp, valueKey),
                            variable,
                            stamp.asMilis(),
                            geometries,
                            unit,
                            viewName);

                    final AttributesAwareGSFeatureTypeEncoder featureType = createFeatureType(
                            viewName,
                            stamp);

                    // retrieve bounding boxes from generated view
                    featureType.setNativeBoundingBox(
                        boundaries[0][0],
                        boundaries[0][1],
                        boundaries[0][2],
                        boundaries[0][3],
                        GEOSERVER_CRS);

                    featureType.setLatLonBoundingBox(
                        boundaries[1][0],
                        boundaries[1][1],
                        boundaries[1][2],
                        boundaries[1][3],
                        GEOSERVER_CRS);

                    final GSPathAwareLayerEncoder layer = new GSPathAwareLayerEncoder();
                    layer.setEnabled(true);
                    layer.setPath("/"
                                + model.getCidsBean().getProperty("name")   // NOI18N
                                + "/"
                                + result.getResolution().getLocalisedName() // NOI18N
                                + "/"
                                + result.getVariable().getLocalisedName()   // NOI18N
                                + "[]");                                    // NOI18N
                    // TODO: SLD according to variable or style interprets variable property.
                    layer.setDefaultStyle(GEOSERVER_SLD);

                    if (!publisher.publishDBLayer(GEOSERVER_WORKSPACE, GEOSERVER_DATASTORE, featureType, layer)) {
                        throw new RuntimeException("GeoServer import was not successful"); // NOI18N
                    }
                }
            } catch (Exception ex) {
                LOG.error("Something went wrong while downloading results.", ex);          // NOI18N
            } finally {
                try {
                    connection.close();
                } catch (Exception ex) {
                    LOG.warn("Could not close connection to database '" + DB_URL + "'.", ex); // NOI18N
                }
            }

            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        jpbDownload.setIndeterminate(false);
                        jpbDownload.setValue(100);
                        jpbDownload.setString("completed"); // NOI18N
//                        SMSUtils.showMappingComponent();
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
