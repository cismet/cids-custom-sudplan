/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.FeatureCollection;

import java.util.Collection;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface WFSQueryInfo {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getCapabilitiesUrl();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    QualifiedName getFeatureQName();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getStatusMessage();

    /**
     * DOCUMENT ME!
     *
     * @param   featureType  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String createFeatureQuery(final FeatureType featureType);

    /**
     * DOCUMENT ME!
     *
     * @param   featureCollection  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WFSRetrievalException  DOCUMENT ME!
     */
    Collection<Feature> createFeatures(final FeatureCollection featureCollection) throws WFSRetrievalException;
}
