/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.extensionfactories.sudplan;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.objectextension.ObjectExtensionFactory;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.tools.BlacklistClassloading;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ModeloutputExtensionFactory extends ObjectExtensionFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ModeloutputExtensionFactory.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void extend(final CidsBean bean) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("extending bean: " + bean); // NOI18N
        }

        try {
            final CidsBean modelBean = (CidsBean)bean.getProperty("model");                 // NOI18N
            final CidsBean managerBean = (CidsBean)modelBean.getProperty("outputmanager");  // NOI18N
            final String managerDefinition = (String)managerBean.getProperty("definition"); // NOI18N
            final Class managerClass = BlacklistClassloading.forName(managerDefinition);
            final Manager manager = (Manager)managerClass.newInstance();
            manager.setCidsBean(bean);

            final Feature feature = manager.getFeature();
            if (feature != null) {
                bean.setProperty("ext_geom", feature.getGeometry()); // NOI18N
            }
        } catch (final Exception ex) {
            LOG.error("could not extend bean: " + bean, ex);         // NOI18N
        }
    }
}
