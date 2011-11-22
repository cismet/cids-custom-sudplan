/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectactions.sudplan;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import de.cismet.cids.utils.interfaces.CidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ActionProviderFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Set<CidsBeanAction> instances = new HashSet<CidsBeanAction>();

    private static final transient Logger LOG = Logger.getLogger(ActionProviderFactory.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ActionProviderFactory object.
     */
    private ActionProviderFactory() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   clazz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBeanAction getCidsBeanAction(final Class<? extends CidsBeanAction> clazz) {
        if (clazz == null) {
            return null;
        }

        CidsBeanAction action = null;
        synchronized (instances) {
            for (final Object instance : instances) {
                if (instance.getClass().equals(clazz)) {
                    action = (CidsBeanAction)instance;
                    break;
                }
            }

            if (action == null) {
                try {
                    action = clazz.newInstance();
                    instances.add(action);
                } catch (final Exception ex) {
                    LOG.warn("cannot create action", ex); // NOI18N
                }
            }
        }

        return action;
    }
}
