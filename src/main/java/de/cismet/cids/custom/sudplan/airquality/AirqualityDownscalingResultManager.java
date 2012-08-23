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
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.net.MalformedURLException;
import java.net.URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.MessageFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.NavigableSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.swing.JPanel;

import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverException;
import de.cismet.cids.custom.sudplan.airquality.AirqualityDownscalingOutput.Result;
import de.cismet.cids.custom.sudplan.geoserver.AttributesAwareGSFeatureTypeEncoder;
import de.cismet.cids.custom.sudplan.geoserver.GSAttributeEncoder;
import de.cismet.cids.custom.sudplan.geoserver.GSPathAwareLayerEncoder;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;
import de.cismet.cismap.commons.wms.capabilities.Layer;
import de.cismet.cismap.commons.wms.capabilities.WMSCapabilities;
import de.cismet.cismap.commons.wms.capabilities.WMSCapabilitiesFactory;

import de.cismet.tools.PasswordEncrypter;
import de.cismet.tools.PropertyReader;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;
import de.cismet.tools.gui.downloadmanager.DownloadManager;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class AirqualityDownscalingResultManager implements Callable<SlidableWMSServiceLayerGroup> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingResultManager.class);

    private static final PropertyReader propertyReader;
    private static final String FILE_PROPERTY = "/de/cismet/cids/custom/sudplan/airquality/airquality.properties";

    private static final transient String DB_URL;
    private static final transient String DB_USER;
    private static final transient String DB_PASSWORD;
    private static final transient String DB_TABLE;

    private static final transient String GEOSERVER_REST_URL;
    private static final transient String GEOSERVER_REST_USER;
    private static final transient String GEOSERVER_REST_PASSWORD;
    private static final transient String GEOSERVER_WORKSPACE;
    private static final transient String GEOSERVER_DATASTORE;

    private static final transient String GEOSERVER_CAPABILITIES_URL;
    private static final transient String GEOSERVER_SLD;

    static {
        propertyReader = new PropertyReader(FILE_PROPERTY);

        DB_URL = propertyReader.getProperty("DB_URL");                       // NOI18N
        DB_USER = propertyReader.getProperty("DB_USER");                     // NOI18N
        DB_PASSWORD = String.valueOf(PasswordEncrypter.decrypt(
                    propertyReader.getProperty("DB_PASSWORD").toCharArray(), // NOI18N
                    false));
        DB_TABLE = propertyReader.getProperty("DB_TABLE");                   // NOI18N

        GEOSERVER_REST_URL = propertyReader.getProperty("GEOSERVER_REST_URL");                                      // NOI18N
        GEOSERVER_REST_USER = propertyReader.getProperty("GEOSERVER_REST_USER");                                    // NOI18N
        GEOSERVER_REST_PASSWORD = String.valueOf(PasswordEncrypter.decrypt(
                    propertyReader.getProperty("GEOSERVER_REST_PASSWORD").toCharArray(),                            // NOI18N
                    false));
        GEOSERVER_WORKSPACE = propertyReader.getProperty("GEOSERVER_WORKSPACE");                                    // NOI18N
        GEOSERVER_DATASTORE = propertyReader.getProperty("GEOSERVER_DATASTORE");                                    // NOI18N
        GEOSERVER_CAPABILITIES_URL = MessageFormat.format(propertyReader.getProperty("GEOSERVER_CAPABILITIES_URL"), // NOI18N
                GEOSERVER_REST_URL);
        GEOSERVER_SLD = propertyReader.getProperty("GEOSERVER_SLD");                                                // NOI18N
    }

    private static final transient String DB_VIEW_SEPARATOR = "_"; // NOI18N
    private static final transient String DB_VIEW_NAME_PATTERN = "aqds" + DB_VIEW_SEPARATOR
                + "view" + DB_VIEW_SEPARATOR                       // NOI18N
                + "{0}" + DB_VIEW_SEPARATOR                        // NOI18N
                + "{1}" + DB_VIEW_SEPARATOR                        // NOI18N
                + "{2}" + DB_VIEW_SEPARATOR                        // NOI18N
                + "{3}";                                           // NOI18N
    private static final transient String DB_STMT_CREATE_VIEW = " CREATE OR REPLACE VIEW "
                + "{0} AS "                                        // NOI18N
                + " SELECT modeloutput_id, variable, resolution, \"timestamp\", geometry, value, unit, offering"
                + " ,(SELECT max(value) FROM downscaled_airquality"
                // + "    WHERE modeloutput_id = {1} "
                + "    WHERE variable ILIKE ''{2}'' " // NOI18N


//                + "      AND resolution ILIKE ''{3}''"
//                + "      AND \"timestamp\" = {4}"
                + ") AS maxValue,"
                + " (SELECT min(value) FROM downscaled_airquality"
                // + "    WHERE modeloutput_id = {1} "
                + "    WHERE variable ILIKE ''{2}'' " // NOI18N


//                + "      AND resolution ILIKE ''{3}''"
//                + "      AND \"timestamp\" = {4}"
                + ") AS minValue"
                + " FROM "                       // NOI18N
                + DB_TABLE
                + " WHERE modeloutput_id = {1} "
                + " AND variable ILIKE ''{2}'' " // NOI18N
                + " AND resolution ILIKE ''{3}''"
                + " AND \"timestamp\" = {4}";    // NOI18N

    private static final transient String DB_QUERY_BOUNDINGBOX_TOKEN_SRS = "<aqds:srs>";
    private static final transient String DB_QUERY_BOUNDINGBOX = "SELECT "
                + " ST_XMIN(st_extent(geometry)) AS native_xmin," // NOI18N
                + " ST_YMIN(st_extent(geometry)) AS native_ymin,"
                + " ST_XMAX(st_extent(geometry)) AS native_xmax," // NOI18N
                + " ST_YMAX(st_extent(geometry)) AS native_ymax,"
                + " ST_XMIN(TRANSFORM(ST_SetSRID(st_extent(geometry), " + DB_QUERY_BOUNDINGBOX_TOKEN_SRS
                + "), 4326)) AS lat_lon_xmin,"                    // NOI18N
                + " ST_YMIN(TRANSFORM(ST_SetSRID(st_extent(geometry), " + DB_QUERY_BOUNDINGBOX_TOKEN_SRS
                + "), 4326)) AS lat_lon_ymin,"
                + " ST_XMAX(TRANSFORM(ST_SetSRID(st_extent(geometry), " + DB_QUERY_BOUNDINGBOX_TOKEN_SRS
                + "), 4326)) AS lat_lon_xmax,"                    // NOI18N
                + " ST_YMAX(TRANSFORM(ST_SetSRID(st_extent(geometry), " + DB_QUERY_BOUNDINGBOX_TOKEN_SRS
                + "), 4326)) AS lat_lon_ymax"
                + " FROM ";                                       // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final AirqualityDownscalingOutput.Result result;
    private final Integer modelId;
    private final String name;
    private final Crs srs;

    private transient Exception exception;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingResultManager object.
     *
     * @param  result   DOCUMENT ME!
     * @param  modelId  DOCUMENT ME!
     * @param  name     DOCUMENT ME!
     * @param  srs      DOCUMENT ME!
     */
    public AirqualityDownscalingResultManager(final AirqualityDownscalingOutput.Result result,
            final Integer modelId,
            final String name,
            final Crs srs) {
        this.result = result;
        this.modelId = modelId;
        this.name = name;
        this.srs = srs;
    }

    //~ Methods ----------------------------------------------------------------

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
                CrsTransformer.extractSridFromCrs(srs.getCode()));

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
     * @throws  SQLException  DOCUMENT ME!
     */
    private void writeValuesToDatabase(
            final Connection connection,
            final Float[][] floatData,
            final String variable,
            final long stamp,
            final Geometry[][] geometries,
            final String unit,
            final String viewName) throws SQLException {
        final Statement insertStatement = connection.createStatement();

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
                insertValueBuilder.append("'::geometry, "); // NOI18N
                insertValueBuilder.append(CrsTransformer.extractSridFromCrs(srs.getCode()));
                insertValueBuilder.append("),");            // NOI18N
                insertValueBuilder.append(Float.toString(floatData[i][j]));
                insertValueBuilder.append(",'");            // NOI18N
                insertValueBuilder.append(unit);
                insertValueBuilder.append("');");           // NOI18N

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
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connection  DOCUMENT ME!
     * @param   viewName    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  SQLException  DOCUMENT ME!
     */
    private Double[][] selectBoundaries(final Connection connection, final String viewName) throws SQLException {
        final Statement boundingBoxQuery = connection.createStatement();
        final ResultSet resultSet = boundingBoxQuery.executeQuery(DB_QUERY_BOUNDINGBOX.replaceAll(
                    DB_QUERY_BOUNDINGBOX_TOKEN_SRS,
                    String.valueOf(CrsTransformer.extractSridFromCrs(srs.getCode()))) + viewName);

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
        featureType.setSRS(srs.getCode());
        featureType.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED);

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
            title.append(now.get(Calendar.MONTH) + 1);
            title.append('.');
            title.append(now.get(Calendar.YEAR));
        } else if (Resolution.DAY.equals(result.getResolution())) {
            title.append(now.get(Calendar.DAY_OF_MONTH));
            title.append('.');
            title.append(now.get(Calendar.MONTH) + 1);
            title.append('.');
            title.append(now.get(Calendar.YEAR));
        } else if (Resolution.HOUR.equals(result.getResolution())) {
            title.append(now.get(Calendar.DAY_OF_MONTH));
            title.append('.');
            title.append(now.get(Calendar.MONTH) + 1);
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

        attribute = new GSAttributeEncoder();
        attribute.addEntry("name", "maxvalue");           // NOI18N
        attribute.addEntry("minOccurs", "1");             // NOI18N
        attribute.addEntry("maxOccurs", "1");             // NOI18N
        attribute.addEntry("nillable", "false");          // NOI18N
        attribute.addEntry("binding", "java.lang.Float"); // NOI18N
        featureType.addAttribute(attribute);

        attribute = new GSAttributeEncoder();
        attribute.addEntry("name", "minvalue");           // NOI18N
        attribute.addEntry("minOccurs", "1");             // NOI18N
        attribute.addEntry("maxOccurs", "1");             // NOI18N
        attribute.addEntry("nillable", "false");          // NOI18N
        attribute.addEntry("binding", "java.lang.Float"); // NOI18N
        featureType.addAttribute(attribute);

        return featureType;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void visualize() throws Exception {
        final TimeSeries timeseries = retrieveTimeSeries();
        if (timeseries == null) {
            final String message = "Error retrieving timeseries."; // NOI18N
            LOG.error(message);
            throw new Exception(message);
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
            unit = unitFromTimeseries.substring(unitFromTimeseries.lastIndexOf(":") + 1);     // NOI18N
        } else {
            final String message = "The valueKey or unit of selected timeseries is invalid."; // NOI18N
            LOG.error(message);
            throw new Exception(message);
        }

        String variable;
        if ((result.getVariable() != null) && (result.getVariable().getPropertyKey() != null)) {
            variable = result.getVariable().getPropertyKey();
            variable = variable.substring(variable.lastIndexOf(":") + 1);    // NOI18N
        } else {
            final String message = "The variable of the result is invalid."; // NOI18N
            LOG.error(message);
            throw new Exception(message);
        }

        final GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(
                GEOSERVER_REST_URL,
                GEOSERVER_REST_USER,
                GEOSERVER_REST_PASSWORD);
        final Connection connection;
        try {
            connection = openConnection();
        } catch (Exception ex) {
            final String message = "Couldn't connect to database."; // NOI18N
            LOG.error(message, ex);
            throw new Exception(message, ex);
        }

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float mean = 0F;
        try {
            final NavigableSet<TimeStamp> timeStamps = timeseries.getTimeStamps();

            for (final TimeStamp stamp : timeStamps) {
                final Float[][] grid = (Float[][])timeseries.getValue(stamp, valueKey);
                float sumGrid = 0F;

                for (final Float[] values : grid) {
                    float sumValues = 0F;
                    for (final Float value : values) {
                        if (min > value) {
                            min = value;
                        }
                        if (max < value) {
                            max = value;
                        }

                        sumValues += value;
                    }

                    sumGrid += (sumValues / values.length);
                }

                mean += (sumGrid / grid.length);
            }

            mean /= timeStamps.size();
        } catch (final Exception ex) {
            final String message = "Something went wrong while downloading results."; // NOI18N
            LOG.error(message, ex);
            throw new Exception(message, ex);
        }

        try {
            for (final TimeStamp stamp : timeseries.getTimeStamps()) {
                final Float[][] values = (Float[][])timeseries.getValue(stamp, valueKey);
                final String viewName = MessageFormat.format(
                            DB_VIEW_NAME_PATTERN,
                            modelId,
                            variable,
                            result.getResolution().getOfferingSuffix(),
                            Long.toString(stamp.asMilis()))
                            .toLowerCase();

                writeValuesToDatabase(
                    connection,
                    values,
                    variable,
                    stamp.asMilis(),
                    geometries,
                    unit,
                    viewName);

                final Double[][] boundaries = selectBoundaries(connection, viewName);

                connection.commit();

                final AttributesAwareGSFeatureTypeEncoder featureType = createFeatureType(
                        viewName,
                        stamp);

                // retrieve bounding boxes from generated view
                featureType.setNativeBoundingBox(
                    boundaries[0][0],
                    boundaries[0][1],
                    boundaries[0][2],
                    boundaries[0][3],
                    srs.getCode());
//                    GEOSERVER_CRS);

                featureType.setLatLonBoundingBox(
                    boundaries[1][0],
                    boundaries[1][1],
                    boundaries[1][2],
                    boundaries[1][3],
                    "EPSG:4326");
//                    GEOSERVER_CRS);

                featureType.addKeyword("min:" + min);
                featureType.addKeyword("max:" + max);
                featureType.addKeyword("mean:" + mean);

                final GSPathAwareLayerEncoder layer = new GSPathAwareLayerEncoder();
                layer.setEnabled(true);
                layer.setPath("/"
                            + name
                            + "/"
                            + result.getResolution().getLocalisedName() // NOI18N
                            + "/"
                            + result.getVariable().getLocalisedName()   // NOI18N
                            + "[]");                                    // NOI18N
                // TODO: SLD according to variable or style interprets variable property.
                layer.setDefaultStyle(GEOSERVER_SLD);

                if (!publisher.publishDBLayer(GEOSERVER_WORKSPACE, GEOSERVER_DATASTORE, featureType, layer)) {
                    throw new Exception("GeoServer import was not successful"); // NOI18N
                }
            }
        } catch (Exception ex) {
            final String message = "Something went wrong while downloading results."; // NOI18N
            LOG.error(message, ex);
            throw new Exception(message, ex);
        } finally {
            try {
                connection.close();
            } catch (Exception ex) {
                LOG.warn("Could not close connection to database '" + DB_URL + "'.", ex); // NOI18N
            }
        }
    }

    @Override
    public SlidableWMSServiceLayerGroup call() {
        SlidableWMSServiceLayerGroup result = null;

        try {
            visualize();

            final WMSCapabilities wmsCapabilities =
                new WMSCapabilitiesFactory().createCapabilities(GEOSERVER_CAPABILITIES_URL);

            if (wmsCapabilities.getLayer() == null) {
                throw new Exception("The capabilities provided by Geoserver (" + GEOSERVER_CAPABILITIES_URL
                            + ") are empty."); // NOI18N
            }

            Layer modelLayer = null;
            for (final Layer layer : wmsCapabilities.getLayer().getChildren()) {
                if (layer.getName().equals(name)) {
                    modelLayer = layer;
                    break;
                }
            }

            if (modelLayer == null) {
                throw new Exception("Geoserver's capabilities don't contain recently created layer."); // NOI18N
            }

            Layer resolutionLayer = null;
            for (final Layer layer : modelLayer.getChildren()) {
                if (layer.getName().equals(this.result.getResolution().getLocalisedName())) {
                    resolutionLayer = layer;
                    break;
                }
            }

            if (resolutionLayer == null) {
                throw new Exception("Geoserver's capabilities don't contain recently created layer."); // NOI18N
            }

            Layer variableLayer = null;
            for (final Layer layer : resolutionLayer.getChildren()) {
                if (layer.getName().equals(this.result.getVariable().getLocalisedName() + "[]")) {
                    variableLayer = layer;
                    break;
                }
            }

            if (variableLayer == null) {
                throw new Exception("Geoserver's capabilities don't contain recently created layer."); // NOI18N
            }

            final String name = variableLayer.getName();
            final String completePath = modelLayer.getName() + "/" + resolutionLayer.getName() + "/"
                        + variableLayer.getName();

            result = new SlidableWMSServiceLayerGroup(
                    name,
                    completePath,
                    Arrays.asList(variableLayer.getChildren()),
                    wmsCapabilities,
                    GEOSERVER_CAPABILITIES_URL,
                    srs);
        } catch (Exception ex) {
            exception = ex;
            return result;
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Exception getException() {
        return exception;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected static final class AirqualityDownscalingResultManagerDownload extends AbstractDownload {

        //~ Instance fields ----------------------------------------------------

        private final AirqualityDownscalingResultManager airqualityDownscalingResultManager;
        private final Result result;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AirqualityDownscalingResultManagerDownload object.
         *
         * @param  airqualityDownscalingResultManager  DOCUMENT ME!
         * @param  directory                           DOCUMENT ME!
         * @param  title                               DOCUMENT ME!
         * @param  filename                            DOCUMENT ME!
         * @param  extension                           DOCUMENT ME!
         */
        public AirqualityDownscalingResultManagerDownload(
                final AirqualityDownscalingResultManager airqualityDownscalingResultManager,
                final String directory,
                final String title,
                final String filename,
                final String extension) {
            this.airqualityDownscalingResultManager = airqualityDownscalingResultManager;
            this.result = airqualityDownscalingResultManager.result;
            this.directory = directory;
            this.title = title;

            if (DownloadManager.instance().isEnabled()) {
                determineDestinationFile(filename, extension);
                status = State.WAITING;
            } else {
                status = State.COMPLETED_WITH_ERROR;
                caughtException = new Exception("DownloadManager is disabled. Cancelling download.");
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            if (status != State.WAITING) {
                return;
            }

            status = State.RUNNING;
            stateChanged();

            final TimeSeries timeseries = airqualityDownscalingResultManager.retrieveTimeSeries();
            if (timeseries == null) {
                error(new Exception("Error retrieving timeseries.")); // NOI18N
                return;
            }

            final Geometry[][] geometries = airqualityDownscalingResultManager.createGeometries(timeseries);

            final Object valueKeysObject = timeseries.getTSProperty(TimeSeries.VALUE_KEYS);
            final Object unitsObject = timeseries.getTSProperty(TimeSeries.VALUE_UNITS);
            final String valueKey;
            final String unit;

            if ((valueKeysObject instanceof String[]) && (((String[])valueKeysObject).length == 1)
                        && (unitsObject instanceof String[])
                        && (((String[])unitsObject).length > 0)) {
                valueKey = ((String[])valueKeysObject)[0];
                final String unitFromTimeseries = ((String[])unitsObject)[0];
                unit = unitFromTimeseries.substring(unitFromTimeseries.lastIndexOf(":") + 1);    // NOI18N
            } else {
                error(new Exception("The valueKey or unit of selected timeseries is invalid.")); // NOI18N
                return;
            }

            String variable;
            if ((result.getVariable() != null) && (result.getVariable().getPropertyKey() != null)) {
                variable = result.getVariable().getPropertyKey();
                variable = variable.substring(variable.lastIndexOf(":") + 1);   // NOI18N
            } else {
                error(new Exception("The variable of the result is invalid.")); // NOI18N
                return;
            }

            Writer out = null;
            try {
                out = new BufferedWriter(new FileWriter(fileToSaveTo));

                for (final TimeStamp stamp : timeseries.getTimeStamps()) {
                    final Float[][] values = (Float[][])timeseries.getValue(stamp, valueKey);

                    for (int x = 0; x < values.length; x++) {
                        for (int y = 0; y < values[x].length; y++) {
                            out.write(variable);
                            out.write(';');
                            out.write(result.getResolution().getOfferingSuffix());
                            out.write(';');
                            out.write(Long.toString(stamp.asMilis()));
                            out.write(';');
                            out.write(geometries[x][y].toText());
                            out.write(';');
                            out.write(values[x][y].toString());
                            out.write(';');
                            out.write(unit);
                            out.write('\n');
                        }
                    }
                }
            } catch (Exception ex) {
                error(ex);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        log.warn("Exception occured while closing file.", ex); // NOI18N
                    }
                }
            }

            if (status == State.RUNNING) {
                status = State.COMPLETED;
                stateChanged();
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof AirqualityDownscalingResultManagerDownload)) {
                return false;
            }

            final AirqualityDownscalingResultManagerDownload other = (AirqualityDownscalingResultManagerDownload)obj;

            boolean result = true;

            if ((this.fileToSaveTo == null) ? (other.fileToSaveTo != null)
                                            : (!this.fileToSaveTo.equals(other.fileToSaveTo))) {
                result &= false;
            }

            return result;
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = (43 * hash) + ((this.fileToSaveTo != null) ? this.fileToSaveTo.hashCode() : 0);

            return hash;
        }

        @Override
        public JPanel getExceptionPanel(final Exception exception) {
            return null;
        }
    }
}
