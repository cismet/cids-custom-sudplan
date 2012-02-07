/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import de.cismet.cids.custom.sudplan.geoserver.AttributesAwareGSFeatureTypeEncoder;
import de.cismet.cids.custom.sudplan.geoserver.GSAttributeEncoder;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class SwmmResultGeoserverUpdater {

    //~ Static fields/initializers ---------------------------------------------

    public static final Logger LOG = Logger.getLogger(SwmmResultGeoserverUpdater.class);
    public static final String DOMAIN = "SUDPLAN";
    public static final String CREATE_VIEW_STATEMENT_TEMPLATE = "CREATE OR REPLACE VIEW %VIEW% AS "
                + "SELECT CSO.\"name\", SWMM_RESULT.\"name\" AS \"scenario_name\", SWMM_RESULT.overflow_volume, GEOM.geo_field AS \"geom\" FROM \"public\".linz_cso CSO "
                + "JOIN \"public\".geom AS GEOM ON GEOM.id = CSO.geom AND GEOM.geo_field IS NOT NULL "
                + "JOIN \"public\".linz_swmm_scenarios AS SWMM_RUN ON CSO.id = SWMM_RUN.linz_cso_reference "
                + "JOIN \"public\".linz_swmm_result AS SWMM_RESULT ON SWMM_RESULT.id = SWMM_RUN.linz_swmm_result AND SWMM_RESULT.swmm_scenario_id = ";
    public static final String GEOSERVER_DATASTORE = "sudplan_linz";
    public static final String GEOSERVER_WORKSPACE = "sudplan";
    public static final String GEOSERVER_SLD = "swmm_result";
    public static final String VIEW_NAME_BASE = "view_swmm_result_";
    public static final String BB_QUERY = " select "
                + " ST_XMIN(st_extent(geom)) as lat_lon_xmin,"
                + " ST_YMIN(st_extent(geom)) as lat_lon_ymin,"
                + " ST_XMAX(st_extent(geom)) as lat_lon_xmax,"
                + " ST_YMAX(st_extent(geom)) as lat_lon_ymax"
                + " from " + VIEW_NAME_BASE;
    public static final String CRS = "GEOGCS[&quot;WGS 84&quot;, "
                + "   DATUM[&quot;World Geodetic System 1984&quot;, "
                + "     SPHEROID[&quot;WGS 84&quot;, 6378137.0, 298.257223563, AUTHORITY[&quot;EPSG&quot;,&quot;7030&quot;]], "
                + "     AUTHORITY[&quot;EPSG&quot;,&quot;6326&quot;]], "
                + "   PRIMEM[&quot;Greenwich&quot;, 0.0, AUTHORITY[&quot;EPSG&quot;,&quot;8901&quot;]], "
                + "   UNIT[&quot;degree&quot;, 0.017453292519943295], "
                + "  AXIS[&quot;Geodetic longitude&quot;, EAST], "
                + "   AXIS[&quot;Geodetic latitude&quot;, NORTH], "
                + "   AUTHORITY[&quot;EPSG&quot;,&quot;4326&quot;]]";
//        public static final String CRS = "GEOGCS[\"WGS 84\", "
//                    + "   DATUM[\"World Geodetic System 1984\", "
//                    + "     SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], "
//                    + "     AUTHORITY[\"EPSG\",\"6326\"]], "
//                    + "   PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], "
//                    + "   UNIT[\"degree\", 0.017453292519943295], "
//                    + "  AXIS[\"Geodetic longitude\", EAST], "
//                    + "   AXIS[\"Geodetic latitude\", NORTH], "
//                    + "   AUTHORITY[\"EPSG\",\"4326\"]]";
    public static final String SRS = "EPSG:4326";

    //~ Instance fields --------------------------------------------------------

    private final String restUser;
    private final String restPassword;
    private final String restUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String dbUrl;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmResultToGeoserverLayer object.
     */
    public SwmmResultGeoserverUpdater() {
        this.dbUser = "postgres";
        this.dbPassword = "cismetz12";
        this.dbUrl = "jdbc:postgresql://sudplan.cismet.de:5433/sudplan_linz";
        this.restUser = "admin";
        // this.restPassword = "2904raJRGa";
        this.restPassword = "cismetz12";
        // this.restUrl = "http://sudplan.cismet.de:8080/geoserver";
        this.restUrl = "http://sudplanwp6.cismet.de/geoserver/";
    }

    /**
     * Creates a new SwmmResultToGeoserverLayer object.
     *
     * @param  dbUser        DOCUMENT ME!
     * @param  dbPassword    DOCUMENT ME!
     * @param  dbUrl         DOCUMENT ME!
     * @param  restUser      DOCUMENT ME!
     * @param  restPassword  DOCUMENT ME!
     * @param  restUrl       DOCUMENT ME!
     * @param  workspace     DOCUMENT ME!
     */
    public SwmmResultGeoserverUpdater(final String dbUser,
            final String dbPassword,
            final String dbUrl,
            final String restUser,
            final String restPassword,
            final String restUrl,
            final String workspace) {
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
        this.restUser = restUser;
        this.restPassword = restPassword;
        this.restUrl = restUrl;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   swmmRunId    DOCUMENT ME!
     * @param   swmmRunName  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void importToGeoServer(final int swmmRunId,
            final String swmmRunName) throws Exception {
        final String viewName = VIEW_NAME_BASE + swmmRunId;
        LOG.info("creating view '" + viewName + "' for SWMM RUN '" + swmmRunName + "'");

        Class.forName("org.postgresql.Driver");
        final Connection connection = DriverManager.getConnection(
                this.dbUrl,
                this.dbUser,
                this.dbPassword);

        final String createViewSQL = (CREATE_VIEW_STATEMENT_TEMPLATE.replaceAll("%VIEW%", String.valueOf(viewName)))
                    + swmmRunId + ';';
        if (LOG.isDebugEnabled()) {
            LOG.debug(createViewSQL);
        }

        final Statement statement = connection.createStatement();
        statement.execute(createViewSQL);

        final GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(
                this.restUrl,
                this.restUser,
                this.restPassword);

        final AttributesAwareGSFeatureTypeEncoder featureType = new AttributesAwareGSFeatureTypeEncoder();
        featureType.setName(viewName); // view name
        featureType.setTitle(swmmRunName);
        featureType.setEnabled(true);
        featureType.setSRS(SRS);
        featureType.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED);

        GSAttributeEncoder attribute = new GSAttributeEncoder();
        attribute.addEntry("name", "geom");
        attribute.addEntry("minOccurs", "0");
        attribute.addEntry("maxOccurs", "1");
        attribute.addEntry("nillable", "false");
        attribute.addEntry("binding", "com.vividsolutions.jts.geom.Geometry");
        featureType.addAttribute(attribute);

        attribute = new GSAttributeEncoder();
        attribute.addEntry("name", "name");
        attribute.addEntry("minOccurs", "0");
        attribute.addEntry("maxOccurs", "1");
        attribute.addEntry("nillable", "true");
        attribute.addEntry("binding", "java.lang.String");
        featureType.addAttribute(attribute);

        attribute = new GSAttributeEncoder();
        attribute.addEntry("name", "scenario_name");
        attribute.addEntry("minOccurs", "0");
        attribute.addEntry("maxOccurs", "1");
        attribute.addEntry("nillable", "true");
        attribute.addEntry("binding", "java.lang.String");
        featureType.addAttribute(attribute);

        attribute = new GSAttributeEncoder();
        attribute.addEntry("name", "overflow_volume");
        attribute.addEntry("minOccurs", "0");
        attribute.addEntry("maxOccurs", "1");
        attribute.addEntry("nillable", "true");
        attribute.addEntry("binding", "java.lang.Float");
        featureType.addAttribute(attribute);

        final String getBBoxSQL = BB_QUERY + swmmRunId;
        if (LOG.isDebugEnabled()) {
            LOG.debug(getBBoxSQL);
        }
        final ResultSet result = statement.executeQuery(getBBoxSQL);

        if (!result.next()) {
            final String message = "view " + viewName + " does not deliver any records";
            LOG.error(message);
            throw new Exception(message);
        }

        featureType.setNativeBoundingBox(result.getDouble("lat_lon_xmin"),
            result.getDouble("lat_lon_ymin"),
            result.getDouble("lat_lon_xmax"),
            result.getDouble("lat_lon_ymax"),
            CRS);

        featureType.setLatLonBoundingBox(result.getDouble("lat_lon_xmin"),
            result.getDouble("lat_lon_ymin"),
            result.getDouble("lat_lon_xmax"),
            result.getDouble("lat_lon_ymax"),
            CRS);

        final GSLayerEncoder layer = new GSLayerEncoder();
        layer.setEnabled(true);
        layer.setDefaultStyle(GEOSERVER_SLD);

        LOG.info("publishing layer '" + swmmRunName + "' to geoserver " + this.restUrl);
        if (!publisher.publishDBLayer(GEOSERVER_WORKSPACE, GEOSERVER_DATASTORE, featureType, layer)) {
            final String message = "GeoServer import of swmm result '" + swmmRunName + "' was not successful";
            LOG.error(message);
            throw new Exception(message);
        }

        LOG.info("GeoServer import of swmm result '" + swmmRunName + "' successful");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        // TODO code application logic here

        BasicConfigurator.configure();
        try {
            final SwmmResultGeoserverUpdater SwmmResultGeoserverUpdater = new SwmmResultGeoserverUpdater();

            SwmmResultGeoserverUpdater.importToGeoServer(461, "LinzV1-1995-5J");
        } catch (final Throwable ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
