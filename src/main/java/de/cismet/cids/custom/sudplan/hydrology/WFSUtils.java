/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;

import java.io.IOException;

import java.lang.reflect.Field;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;
import de.cismet.cismap.commons.wfs.capabilities.WFSCapabilities;
import de.cismet.cismap.commons.wfs.capabilities.WFSCapabilitiesFactory;
import de.cismet.cismap.commons.wfs.capabilities.deegree.DeegreeFeatureType;

/**
 * this is a utility class for wfs purposes.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class WFSUtils {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(WFSUtils.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WFSUtils object.
     */
    private WFSUtils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   capabilitiesUrl  DOCUMENT ME!
     * @param   qname            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static FeatureType getFeatureType(final String capabilitiesUrl, final QualifiedName qname)
            throws IOException {
        try {
            final WFSCapabilitiesFactory factory = new WFSCapabilitiesFactory();

            final WFSCapabilities wfsCapabilities = factory.createCapabilities(capabilitiesUrl);
            // FIXME: evil actions lead to the cake... the feature types without fetching their description, normal
            // facilities will do getFeatureInfo for every feature type, which is very inefficient and slow
            final Field field = wfsCapabilities.getClass().getDeclaredField("cap"); // NOI18N
            field.setAccessible(true);
            final org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities dCaps =
                (org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities)field.get(
                    wfsCapabilities);
            final WFSFeatureType basinType = dCaps.getFeatureTypeList().getFeatureType(qname);

            if (basinType == null) {
                throw new IllegalStateException("WFS does not serve feature with given qname: " + qname); // NOI18N
            }

            return new DeegreeFeatureType(basinType, wfsCapabilities);
        } catch (final Exception e) {
            final String message = "cannot fetch feature type for capabilities url and qname: [" // NOI18N
                        + capabilitiesUrl
                        + "|"                                                                    // NOI18N
                        + qname + "]";                                                           // NOI18N
            LOG.error(message, e);

            throw new IOException(message, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   feature  DOCUMENT ME!
     * @param   qname    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static Object getFeaturePropertyValue(final org.deegree.model.feature.Feature feature,
            final QualifiedName qname) {
        if ((feature == null) || (qname == null)) {
            throw new IllegalArgumentException("feature or qname must not be null"); // NOI18N
        }

        final FeatureProperty[] props = feature.getProperties(qname);

        if ((props == null) || (props.length < 1)) {
            return null;
        } else if (props.length > 1) {
            throw new IllegalStateException("found more than one property for qname:" + qname); // NOI18N
        } else {
            return props[0].getValue();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   feature  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeometryException  DOCUMENT ME!
     */
    public static Geometry extractGeometry(final org.deegree.model.feature.Feature feature) throws GeometryException {
        return extractGeometry(feature, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   feature       DOCUMENT ME!
     * @param   geomAttrName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeometryException         DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static Geometry extractGeometry(final org.deegree.model.feature.Feature feature,
            final QualifiedName geomAttrName) throws GeometryException {
        if (feature == null) {
            throw new IllegalArgumentException("feature must not be null"); // NOI18N
        }

        final Geometry geom;
        if (geomAttrName == null) {
            geom = JTSAdapter.export(feature.getDefaultGeometryPropertyValue());
        } else {
            final Object value = getFeaturePropertyValue(feature, geomAttrName);
            if (value instanceof org.deegree.model.spatialschema.Geometry) {
                geom = JTSAdapter.export((org.deegree.model.spatialschema.Geometry)value);
            } else {
                throw new GeometryException(
                    "feature does not contain geometry attribute value for given qname: [feature=" // NOI18N
                            + feature
                            + "|qname="                                                            // NOI18N
                            + geomAttrName
                            + "|value="                                                            // NOI18N
                            + value
                            + "]");                                                                // NOI18N
            }
        }

        final CoordinateSystem coordSys = feature.getDefaultGeometryPropertyValue().getCoordinateSystem();
        if (coordSys == null) {
            throw new IllegalStateException("feature without a coordinate system: " + feature); // NOI18N
        }

        final int srid = CrsTransformer.extractSridFromCrs(coordSys.getIdentifier());
        final GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);

        return gf.createGeometry(geom);
    }
}
