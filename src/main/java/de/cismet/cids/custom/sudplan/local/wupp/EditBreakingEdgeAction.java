/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.navigator.method.MethodManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.permission.PermissionHolder;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class EditBreakingEdgeAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EditBreakingEdgeAction.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        final MetaObjectNode mon = new MetaObjectNode(getCidsBean());

        if (MethodManager.getManager().checkPermission(mon, PermissionHolder.WRITEPERMISSION)) {
            ComponentRegistry.getRegistry().showComponent(ComponentRegistry.ATTRIBUTE_EDITOR);
            ComponentRegistry.getRegistry().getAttributeEditor().setTreeNode(mon);
        } else {
            LOG.warn("insufficient permission to edit breaking edge"); // NOI18N
        }
    }
}
