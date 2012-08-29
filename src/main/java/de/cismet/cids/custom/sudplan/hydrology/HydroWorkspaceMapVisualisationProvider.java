/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.FeatureCollection;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;

import de.cismet.cismap.navigatorplugin.MapVisualisationProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class HydroWorkspaceMapVisualisationProvider implements MapVisualisationProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(HydroWorkspaceMapVisualisationProvider.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public Feature getFeature(final CidsBean bean) {
        final String subId = String.valueOf(bean.getProperty("basin_id")); // NOI18N
        final WFSQueryInfo queryInfo = new WFSQueryInfo() {

                private final transient ShowCatchmentAreaForPointAction scafpa = new ShowCatchmentAreaForPointAction();

                @Override
                public String createFeatureQuery(final FeatureType featureType) {
                    if (subId == null) {
                        throw new IllegalArgumentException("subid must not be null"); // NOI18N
                    }

                    final Element root = featureType.getWFSCapabilities()
                                .getServiceFacade()
                                .getGetFeatureQuery(featureType);
                    root.setAttribute("maxFeatures", "1"); // NOI18N

                    final Namespace wfsNs = Namespace.getNamespace("http://www.opengis.net/wfs"); // NOI18N
                    final Namespace ogcNs = Namespace.getNamespace("http://www.opengis.net/ogc"); // NOI18N

                    final Element query = root.getChild("Query", wfsNs); // NOI18N
                    query.setAttribute("srsName", "EPSG:4326");          // NOI18N

                    final Element filter = query.getChild("Filter", ogcNs); // NOI18N
                    filter.removeChildren("BBOX", ogcNs);                   // NOI18N

                    final Element equals = new Element("PropertyIsEqualTo", ogcNs); // NOI18N
                    filter.addContent(equals);
                    final Element propName = new Element("PropertyName", ogcNs);    // NOI18N
                    propName.setText("SUDPLAN:subid");                              // NOI18N
                    equals.addContent(propName);
                    final Element literal = new Element("Literal", ogcNs);          // NOI18N
                    literal.setText(subId);
                    equals.addContent(literal);

                    final XMLOutputter raw = new XMLOutputter(Format.getRawFormat());

                    if (LOG.isDebugEnabled()) {
                        final XMLOutputter pretty = new XMLOutputter(Format.getPrettyFormat());
                        LOG.debug("created feature query: " + pretty.outputString(root)); // NOI18N
                    }

                    return raw.outputString(root);
                }

                @Override
                public String getCapabilitiesUrl() {
                    return scafpa.getCapabilitiesUrl();
                }

                @Override
                public QualifiedName getFeatureQName() {
                    return scafpa.getFeatureQName();
                }

                @Override
                public String getStatusMessage() {
                    return scafpa.getStatusMessage();
                }

                @Override
                public Collection<Feature> createFeatures(final FeatureCollection featureCollection)
                        throws WFSRetrievalException {
                    return scafpa.createFeatures(featureCollection);
                }
            };

        final Collection<Feature> features = WFSUtils.fetchFeatures(queryInfo);

        if (features == null) {
            return null;
        } else if (features.size() == 1) {
            return features.iterator().next();
        } else {
            throw new IllegalStateException(
                "the wfs request delivered not exactly one feature"); // NOI18N
        }
    }
}
