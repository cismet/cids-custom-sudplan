/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d;

import java.net.URI;

import de.cismet.cids.custom.sudplan.UIProvider;

import de.cismet.commons.utils.ProgressListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface Layer3D extends UIProvider {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  uri  DOCUMENT ME!
     */
    void addLayer(URI uri);

    /**
     * DOCUMENT ME!
     *
     * @param  uri        DOCUMENT ME!
     * @param  progressL  DOCUMENT ME!
     */
    void addLayer(URI uri, ProgressListener progressL);

    /**
     * DOCUMENT ME!
     *
     * @param  uri  DOCUMENT ME!
     */
    void removeLayer(URI uri);

    /**
     * DOCUMENT ME!
     */
    void removeAllLayers();

    /**
     * DOCUMENT ME!
     *
     * @param  capabilities  DOCUMENT ME!
     * @param  layername     DOCUMENT ME!
     * @param  opacity       DOCUMENT ME!
     */
    void addWMSLayer(URI capabilities, String layername, double opacity);
}
