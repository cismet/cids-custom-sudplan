/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.GeometryException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.openide.util.lookup.ServiceProvider;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MapPopupAction;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = MapPopupAction.class)
public final class ShowCatchmentAreaForPointAction extends AbstractWFSFeatureRetrievalAction implements MapPopupAction {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(ShowCatchmentAreaForPointAction.class);

    /** URL to the Hydrology Geoserver. */
    public static final String HYDRO_WFS_HOST = "http://79.125.2.136:49225/geoserver/ows"; // NOI18N

    /** URL to the Hydrology capabilities. */
    public static final String HYDRO_WFS_CAPABILITIES = HYDRO_WFS_HOST + "?service=wfs&request=GetCapabilities"; // NOI18N

    /** Dummy uri for qnames and stuff. */
    public static final URI HYDRO_WFS_QNAME_URI;

    /** The prefix for all the qnames of the Sudplan Hydro WFS. */
    public static final String HYDRO_WFS_QNAME_PREFIX = "SUDPLAN"; // NOI18N

    static {
        try {
            HYDRO_WFS_QNAME_URI = new URI("http://SUDPLAN"); // NOI18N
        } catch (final URISyntaxException ex) {
            // it's a hardcoded uri, there cannot be an error
            final String message = "unexpected uri syntax error";        // NOI18N
            LOG.fatal(message, ex);
            throw new IncompatibleClassChangeError(message + ": " + ex); // NOI18N
        }
    }

    //~ Instance fields --------------------------------------------------------

    /** The point where the action is invoked on. */
    private transient Point point;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ShowCatchmentAreaForPointAction object.
     */
    public ShowCatchmentAreaForPointAction() {
        super("Show Catchment Area");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getPosition() {
        return 1;
    }

    @Override
    public String getCapabilitiesUrl() {
        return HYDRO_WFS_CAPABILITIES;
    }

    @Override
    public QualifiedName getFeatureQName() {
        return new QualifiedName(HYDRO_WFS_QNAME_PREFIX, "basin", HYDRO_WFS_QNAME_URI); // NOI18N
    }

    @Override
    public String getStatusMessage() {
        return "Fetching Catchment Area";
    }

    @Override
    public String createFeatureQuery(final FeatureType featureType) {
        final Geometry geom = CrsTransformer.transformToGivenCrs(getPoint(), "EPSG:4326"); // NOI18N
        final Coordinate coord = geom.getCoordinate();

        final Element root = featureType.getWFSCapabilities().getServiceFacade().getGetFeatureQuery(featureType);
        root.setAttribute("maxFeatures", "1"); // NOI18N

        final Namespace wfsNs = Namespace.getNamespace("http://www.opengis.net/wfs"); // NOI18N
        final Namespace ogcNs = Namespace.getNamespace("http://www.opengis.net/ogc"); // NOI18N
        final Namespace gmlNs = Namespace.getNamespace("http://www.opengis.net/gml"); // NOI18N

        final Element query = root.getChild("Query", wfsNs); // NOI18N
        query.setAttribute("srsName", "EPSG:4326");          // NOI18N

        final Element filter = query.getChild("Filter", ogcNs); // NOI18N
        filter.removeChildren("BBOX", ogcNs);                   // NOI18N

        final Element intersects = new Element("Intersects", ogcNs); // NOI18N
        filter.addContent(intersects);
        final Element propName = new Element("PropertyName", ogcNs); // NOI18N
        propName.setText("SUDPLAN:the_geom");                        // NOI18N
        intersects.addContent(propName);
        final Element pointElement = new Element("Point", gmlNs);    // NOI18N
        intersects.addContent(pointElement);

        // coordinates
        final Element pos = new Element("coordinates", gmlNs); // NOI18N
        pointElement.addContent(pos);

        // coordinates
        pos.setText(coord.x + "," + coord.y); // NOI18N

        final XMLOutputter raw = new XMLOutputter(Format.getRawFormat());

        if (LOG.isDebugEnabled()) {
            final XMLOutputter pretty = new XMLOutputter(Format.getPrettyFormat());
            LOG.debug("created feature query: " + pretty.outputString(root)); // NOI18N
        }

        return raw.outputString(root);
    }

    @Override
    public Collection<Feature> createFeatures(final FeatureCollection featureCollection) throws WFSRetrievalException {
        if (featureCollection.size() == 1) {
            final ArrayList<Feature> features = new ArrayList<Feature>(1);
            try {
                features.add(new SingleCatchmentAreaFeature(
                        featureCollection.getFeature(0),
                        new QualifiedName(
                            ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                            "subid", // NOI18N
                            ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI)));
            } catch (final GeometryException ex) {
                final String message = "cannot create single catchment area feature"; // NOI18N
                LOG.error(message, ex);

                throw new WFSRetrievalException(message, ex);
            }

            return features;
        } else {
            throw new WFSRetrievalException("only one feature in collection expected"); // NOI18N
        }
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public void setPoint(final Point newPoint) {
        this.point = newPoint;
    }

    @Override
    public boolean isActive(final boolean featuresSubjacent) {
        return !featuresSubjacent;
    }

    /**
     * The comparison via compareTo shall only used to determine the action's position within a menu. It is not intended
     * to be used as equality check, because <code>x.compareTo(y) == 0</code> may not result in <code>
     * x.equals(y)</code>.
     *
     * @param   other  The other action to be compared
     *
     * @return  a negative integer if this object shall be above the given object, a positive integer if this object
     *          shall be below the given object or <code>0</code> if the relative position to each other cannot be
     *          determined/does not matter.
     */
    @Override
    public int compareTo(final MapPopupAction other) {
        return getPosition() - other.getPosition();
    }
}
