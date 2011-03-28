/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d.dfki;

import org.apache.log4j.Logger;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.RasterLayerSupportedFeature;
import de.cismet.cismap.commons.raster.wms.featuresupportlayer.SimpleFeatureSupporterRasterServiceUrl;
import de.cismet.cismap.commons.raster.wms.featuresupportlayer.SimpleFeatureSupportingRasterLayer;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class Cismap3DFeatureLayerAddition extends SimpleFeatureSupportingRasterLayer {

    //~ Static fields/initializers ---------------------------------------------

    // TODO: some superclass has a protected LOG variable for some reason
    // TODO: some superclasses use non-static loggers
    private static final transient Logger LOG = Logger.getLogger(Cismap3DFeatureLayerAddition.class);

    //~ Instance fields --------------------------------------------------------

    // TODO: why is this property in super class package private instead of protected?
    private final SimpleFeatureSupporterRasterServiceUrl url;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Cismap3DFeatureLayerAddition object.
     *
     * @param  cfla  DOCUMENT ME!
     */
    public Cismap3DFeatureLayerAddition(final Cismap3DFeatureLayerAddition cfla) {
        super(cfla);
        if (LOG.isDebugEnabled()) {
            LOG.debug("new copy: " + cfla); // NOI18N
        }

        this.url = cfla.url;
    }

    /**
     * Creates a new Cismap3DFeatureLayerAddition object.
     *
     * @param  sfu  DOCUMENT ME!
     */
    public Cismap3DFeatureLayerAddition(final SimpleFeatureSupporterRasterServiceUrl sfu) {
        super(sfu);
        if (LOG.isDebugEnabled()) {
            LOG.debug("new object from url: " + sfu); // NOI18N
        }

        this.url = sfu;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public synchronized void retrieve(final boolean forced) {
        url.setFilter(getLayerFilter());

        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieval url: " + url); // NOI18N
        }

        super.retrieve(forced);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getLayerFilter() {
        final FeatureCollection fc = getFeatureCollection();
        final StringBuilder sb = new StringBuilder("&LAYERS="); // NOI18N

        for (final Feature f : fc.getAllFeatures()) {
            if (f instanceof RasterLayerSupportedFeature) {
                final RasterLayerSupportedFeature rlsf = (RasterLayerSupportedFeature)f;

                if ((rlsf.getSupportingRasterService() == null) || !rlsf.getSupportingRasterService().equals(this)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ignoring non-3D feature: " + f); // NOI18N
                    }
                } else {
                    final String layername = rlsf.getSpecialLayerName();

                    if ((layername == null) || layername.isEmpty()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring empty layername: " + f);
                        }
                    } else {
                        sb.append(layername).append(',');
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ignoring non-rasterlayersupportfeature: " + f); // NOI18N
                }
            }
        }

        // delete the appended comma
        sb.deleteCharAt(sb.length() - 1);

        // we don't care whether there has not bean any layer added or not. if not nothing shall be visualised anyway
        return sb.toString();
    }
}
