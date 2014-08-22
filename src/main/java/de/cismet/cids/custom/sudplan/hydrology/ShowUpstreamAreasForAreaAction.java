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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.EventQueue;

import java.io.InputStream;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import de.cismet.cismap.commons.MappingModel;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;

import de.cismet.commons.security.AccessHandler.ACCESS_METHODS;

import de.cismet.security.WebAccessManager;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ShowUpstreamAreasForAreaAction extends AbstractWFSFeatureRetrievalAction {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(ShowUpstreamAreasForAreaAction.class);

    private static final String UPSTREAM_SUPPORTIVE_GETMAP = "http://79.125.2.136:49225/geoserver/wms/GetMap"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    //J-
    // TODO: use resource file
    private static final String UPSTREAM_POST_GETMAP_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
            +"<GetMap xmlns:ows=\"http://www.opengis.net/ows\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.1.1\" service=\"WMS\">"
            +   "<StyledLayerDescriptor version=\"1.0.0\" "
            +       "xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\" "
            +       "xmlns=\"http://www.opengis.net/sld\" "
            +       "xmlns:ogc=\"http://www.opengis.net/ogc\" "
            +       "xmlns:gml=\"http://www.opengis.net/gml\" "
            +       "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
            +       "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"

            +       "<NamedLayer>"
            +           "<Name>SUDPLAN:basin</Name>"

            +           "<UserStyle>"
            +               "<Title>SUDPLAN:basin</Title>"

            +               "<FeatureTypeStyle>"

            +                   "<Rule>"
            +                       "<Name>Upstream_Section</Name>"
            +                       "<Title>Upstream Section</Title>"

            +                       "<Filter>"
            +                           "<Intersects>"
            +                               "<PropertyName>the_geom</PropertyName>"
            +                               "<gml:Polygon>"
            +                                   "<gml:outerBoundaryIs>"
            +                                       "<gml:LinearRing>"
            +                                           "<gml:coordinates>{0}</gml:coordinates>"
            +                                       "</gml:LinearRing>"
            +                                   "</gml:outerBoundaryIs>"
            +                               "</gml:Polygon>"
            +                           "</Intersects>"
            +                       "</Filter>"

            +                       "<PolygonSymbolizer>"
            +                           "<Fill>"
            +                               "<CssParameter name=\"fill\">#c71585</CssParameter>"
            +                               "<CssParameter name=\"fill-opacity\">1.0</CssParameter>"
            +                           "</Fill>"
            +                           "<Stroke>"
            +                               "<CssParameter name=\"stroke\">#000000</CssParameter>"
            +                               "<CssParameter name=\"stroke-width\">1</CssParameter>"
            +                           "</Stroke>"
            +                       "</PolygonSymbolizer>"
            +                   "</Rule>"

            +               "</FeatureTypeStyle>"

            +           "</UserStyle>"
            +       "</NamedLayer>"
            +   "</StyledLayerDescriptor>"
            +   "<BoundingBox srsName=\"<cismap:srs>\">"
            +       "<gml:coord>"
            +           "<gml:X><cismap:boundingBox_ll_x></gml:X>"
            +           "<gml:Y><cismap:boundingBox_ll_y></gml:Y>"
            +       "</gml:coord>"
            +       "<gml:coord>"
            +           "<gml:X><cismap:boundingBox_ur_x></gml:X>"
            +           "<gml:Y><cismap:boundingBox_ur_y></gml:Y>"
            +       "</gml:coord>"
            +   "</BoundingBox>"
            +   "<Output>"
            +       "<Format>image/png</Format>"
            +       "<Size>"
            +           "<Width><cismap:width></Width>"
            +           "<Height><cismap:height></Height>"
            +       "</Size>"
            +       "<Transparent>true</Transparent>"
            +   "</Output>"
            +   "<Exceptions>application/vnd.ogc.se+xml</Exceptions>"

            +"</GetMap>";
    //J+

    /** The feature for which the upstream areas shall be fetched. */
    private final transient org.deegree.model.feature.Feature areaFeature;

    /**
     * NOTE: In case of race conditions this variable has to be made thread local. Under normal circumstances the
     * createFeatureQuery will not be called a second time before the createFeatures operation is called. So normally
     * the sequence is createFeatureQuery, createFeatures, createFeatureQuery, createFeatures, etc. If this condition is
     * not met anymore this variable may cause issues, because it is set in the createFeatureQuery operation and used in
     * the createFeatures operation. Be careful when changing the (super) implementation.
     */
    private transient String resultFeatureGeomName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ShowUpstreamAreasForAreaAction object.
     *
     * @param  areaFeature  DOCUMENT ME!
     */
    public ShowUpstreamAreasForAreaAction(final org.deegree.model.feature.Feature areaFeature) {
        super("Show Upstream Areas"); // NOI18N

        this.areaFeature = areaFeature;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getCapabilitiesUrl() {
        return ShowCatchmentAreaForPointAction.HYDRO_WFS_CAPABILITIES;
    }

    @Override
    public QualifiedName getFeatureQName() {
        return new QualifiedName(
                ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                "upstream_geom",
                ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI); // NOI18N
    }

    @Override
    public String getStatusMessage() {
        return "Fetching Upstream Areas";
    }

    @Override
    public String getNoResultsMessage() {
        return "No upstream areas";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   featureType  DOCUMENT ME!
     * @param   exceedCount  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    // TODO: extract WFS util operation
    private boolean isFeatureCountExceeded(final FeatureType featureType, final int exceedCount) {
        try {
            final Element root = featureType.getWFSCapabilities().getServiceFacade().getGetFeatureQuery(featureType);
            root.setAttribute("maxFeatures", String.valueOf(exceedCount + 1));

            final Namespace wfsNs = Namespace.getNamespace("http://www.opengis.net/wfs");
            final Namespace ogcNs = Namespace.getNamespace("http://www.opengis.net/ogc");

            final Element query = root.getChild("Query", wfsNs); // NOI18N
            query.setAttribute("srsName", "EPSG:4326");          // NOI18N

            final Element filter = query.getChild("Filter", ogcNs); // NOI18N
            final Element equal = new Element("PropertyIsEqualTo", ogcNs);
            filter.setContent(equal);

            final Element propName = new Element("PropertyName", ogcNs); // NOI18N
            propName.setText("SUDPLAN:subid");                           // NOI18N
            equal.addContent(propName);

            final Element literal = new Element("Literal", ogcNs);                                 // NOI18N
            final Object value = WFSUtils.getFeaturePropertyValue(
                    areaFeature,
                    new QualifiedName(
                        ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                        "subid",                                                                   // NOI18N
                        ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI));
            if (value instanceof String) {
                literal.setText((String)value);
            } else {
                throw new IllegalStateException("property value not instanceof string: " + value); // NOI18N
            }
            equal.addContent(literal);

            final List<Element> children = query.getChildren("PropertyName", wfsNs);
            final ListIterator<Element> it = children.listIterator();
            while (it.hasNext()) {
                final Element current = it.next();
                if (!current.getText().endsWith("id")) {
                    it.remove();
                    current.detach();
                }
            }

            final XMLOutputter raw = new XMLOutputter(Format.getRawFormat());

            if (LOG.isDebugEnabled()) {
                final XMLOutputter pretty = new XMLOutputter(Format.getPrettyFormat());
                LOG.debug("created feature query: " + pretty.outputString(root)); // NOI18N
            }
            final InputStream resp = WebAccessManager.getInstance()
                        .doRequest(
                            featureType.getWFSCapabilities().getURL(),
                            raw.outputString(root),
                            ACCESS_METHODS.POST_REQUEST);
            final GMLFeatureCollectionDocument gmlDoc = new GMLFeatureCollectionDocument();
            gmlDoc.load(resp, ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI.toString());
            final FeatureCollection fc = gmlDoc.parse();

            return fc.size() > exceedCount;
        } catch (final Exception ex) {
            final String message = "cannot determine upstream area feature count"; // NOI18N
            LOG.error(message, ex);
            // FIXME: throw proper exception
            throw new RuntimeException(message, ex);
        }
    }

    @Override
    public String createFeatureQuery(final FeatureType featureType) {
        final Namespace wfsNs = Namespace.getNamespace("http://www.opengis.net/wfs");
        final Namespace ogcNs = Namespace.getNamespace("http://www.opengis.net/ogc");

        // this is a rather complicated query creation, because we have to issue some other requests to decide on the
        // definitive query
        final Element root;
        if (isFeatureCountExceeded(featureType, 10)) {
            try {
                final FeatureType unionFt = WFSUtils.getFeatureType(featureType.getWFSCapabilities().getURL()
                                .toString(),
                        new QualifiedName(
                            ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                            "upstream_union_geom", // NOI18N
                            ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI));
                root = unionFt.getWFSCapabilities().getServiceFacade().getGetFeatureQuery(unionFt);

                resultFeatureGeomName = "upstream_geom"; // NOI18N

                // add wms layer if necessary
            } catch (final Exception e) {
                final String message = "cannot prepare map combined upstream query"; // NOI18N
                LOG.error(message, e);
                throw new RuntimeException(message, e);
            }
        } else {
            root = featureType.getWFSCapabilities().getServiceFacade().getGetFeatureQuery(featureType);

            resultFeatureGeomName = "upstream_basin_geom"; // NOI18N
        }

        root.setAttribute("maxFeatures", "30");
        final Element query = root.getChild("Query", wfsNs); // NOI18N
        query.setAttribute("srsName", "EPSG:4326");          // NOI18N

        final Element filter = query.getChild("Filter", ogcNs); // NOI18N
        final Element equal = new Element("PropertyIsEqualTo", ogcNs);
        filter.setContent(equal);

        final Element propName = new Element("PropertyName", ogcNs); // NOI18N
        propName.setText("SUDPLAN:subid");                           // NOI18N
        equal.addContent(propName);

        final Element literal = new Element("Literal", ogcNs);                                 // NOI18N
        final Object value = WFSUtils.getFeaturePropertyValue(
                areaFeature,
                new QualifiedName("SUDPLAN", "subid", ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI));
        if (value instanceof String) {
            literal.setText((String)value);
        } else {
            throw new IllegalStateException("property value not instanceof string: " + value); // NOI18N
        }

        equal.addContent(literal);

        final XMLOutputter raw = new XMLOutputter(Format.getRawFormat());

        if (LOG.isDebugEnabled()) {
            final XMLOutputter pretty = new XMLOutputter(Format.getPrettyFormat());
            LOG.debug("created feature query: " + pretty.outputString(root)); // NOI18N
        }

        return raw.outputString(root);
    }

    @Override
    public Collection<Feature> createFeatures(final FeatureCollection featureCollection) throws WFSRetrievalException {
        try {
            final QualifiedName renderFeatureName = new QualifiedName(
                    "SUDPLAN",
                    resultFeatureGeomName,
                    ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI);
            final ArrayList<Feature> features = new ArrayList<Feature>(featureCollection.size());

            // only one geom was fetched, the upstream geometry
            if ("upstream_geom".equals(resultFeatureGeomName)) {
                assert featureCollection.size() == 1 : "illegal state: not exactly one upstream geom in collection"; // NOI18N

                final org.deegree.model.feature.Feature unionFeature = featureCollection.getFeature(0);
                addSupportiveWMSLayer(unionFeature, renderFeatureName);
                features.add(new UnionCatchmentAreaFeature(unionFeature, renderFeatureName));
            } else {
                for (int i = 0; i < featureCollection.size(); ++i) {
                    features.add(new SingleCatchmentAreaFeature(
                            featureCollection.getFeature(i),
                            renderFeatureName,
                            new QualifiedName(
                                ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                                "upstream_subid", // NOI18N
                                ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI)));
                }
            }

            return features;
        } catch (final Exception e) {
            final String message = "cannot create features"; // NOI18N
            LOG.error(message, e);
            throw new WFSRetrievalException(message, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   feature            DOCUMENT ME!
     * @param   renderFeatureName  DOCUMENT ME!
     *
     * @throws  WFSRetrievalException  DOCUMENT ME!
     */
    private void addSupportiveWMSLayer(final org.deegree.model.feature.Feature feature,
            final QualifiedName renderFeatureName) throws WFSRetrievalException {
        try {
            final Geometry geom = WFSUtils.extractGeometry(feature, renderFeatureName);
            if ((renderFeatureName != null) && "upstream_geom".equals(renderFeatureName.getLocalName())) { // NOI18N
                Polygon candidate = null;
                for (int i = 0; i < geom.getNumGeometries(); ++i) {
                    final Geometry g = geom.getGeometryN(i);
                    if ((g instanceof Polygon)
                                && ((candidate == null) || (candidate.getNumPoints() < g.getNumPoints()))) {
                        candidate = (Polygon)g;
                    }
                }

                if (candidate == null) {
                    throw new IllegalStateException("no outer ring found"); // NOI18N
                }

                final GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
                final Geometry boundaryGeom = gf.createPolygon((LinearRing)candidate.getExteriorRing(),
                        new LinearRing[0]);

                final Geometry bufferedGeom = boundaryGeom.buffer(-0.1);
                Geometry bufferCandidate = null;
                if (bufferedGeom instanceof MultiPolygon) {
                    for (int i = 0; i < bufferedGeom.getNumGeometries(); ++i) {
                        final Geometry g = bufferedGeom.getGeometryN(i);
                        if ((g instanceof Polygon)
                                    && ((bufferCandidate == null)
                                        || (bufferCandidate.getNumPoints() < g.getNumPoints()))) {
                            bufferCandidate = (Polygon)g;
                        }
                    }
                } else {
                    // it is still instance of Polygon
                    bufferCandidate = bufferedGeom;
                }
                bufferCandidate = gf.createPolygon((LinearRing)((Polygon)bufferCandidate).getExteriorRing(),
                        new LinearRing[0]);
                bufferCandidate = TopologyPreservingSimplifier.simplify(bufferCandidate, 0.1);

                final StringBuilder sb = new StringBuilder();

                for (final Coordinate coord : bufferCandidate.getCoordinates()) {
                    sb.append(coord.x);
                    sb.append(',');
                    sb.append(coord.y);
                    sb.append(' ');
                }

                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                final String getMapTemplate = MessageFormat.format(UPSTREAM_POST_GETMAP_TEMPLATE, sb.toString());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("created wms payload template: " + getMapTemplate); // NOI18N
                }

                final SimpleWmsGetMapUrl getMapUrl = new SimpleWmsGetMapUrl(UPSTREAM_SUPPORTIVE_GETMAP, getMapTemplate);
                final SimpleWMS wms = new SimpleWMS(getMapUrl);

                wms.setName("Upstream Area (Basin "
                            + WFSUtils.getFeaturePropertyValue(
                                feature,
                                new QualifiedName(
                                    ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                                    "subid",
                                    ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI)) + ")");
                wms.setTranslucency(0.5f);

                final MappingModel mappingModel = CismapBroker.getInstance().getMappingComponent().getMappingModel();

                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            mappingModel.addLayer(wms);
                        }
                    });
            }
        } catch (final Exception ex) {
            final String message = "cannot create supportive WMS layer for feature: " + feature; // NOI18N
            LOG.error(message, ex);

            throw new WFSRetrievalException(message, ex);
        }
    }
}
