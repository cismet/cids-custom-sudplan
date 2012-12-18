/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.cismap.navigatorplugin.CidsFeature;
import de.cismet.cismap.navigatorplugin.MapVisualisationProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ModelOutputMapVisualisationProvider implements MapVisualisationProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Feature getFeature(final CidsBean bean) {
        SudplanConcurrency.getSudplanGeneralPurposePool().execute(new ModelOutputRasterLayerAdder(bean));

        return new CidsFeature(bean.getMetaObject());
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class ModelOutputRasterLayerAdder implements Runnable {

        //~ Static fields/initializers -----------------------------------------

        /** LOGGER. */
        private static final transient Logger LOG = Logger.getLogger(ModelOutputRasterLayerAdder.class);

        //~ Instance fields ----------------------------------------------------

        private final transient CidsBean moBean;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ModelOutputRasterLayerAdder object.
         *
         * @param  moBean  DOCUMENT ME!
         */
        public ModelOutputRasterLayerAdder(final CidsBean moBean) {
            this.moBean = moBean;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            try {
                final Manager manager = SMSUtils.loadManagerFromModel((CidsBean)moBean.getProperty("model"), // NOI18N
                        ManagerType.OUTPUT);

                LOG.info("this modeloutput does not contain supportive layer data: " + moBean); // NOI18N
            } catch (final Exception e) {
                LOG.warn("cannot add supporting layer to map for bean: " + moBean, e);          // NOI18N
            }
        }
    }
}
