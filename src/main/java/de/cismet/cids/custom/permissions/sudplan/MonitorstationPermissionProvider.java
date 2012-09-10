/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.permissions.sudplan;

import Sirius.server.newuser.User;

import de.cismet.cids.dynamics.AbstractCustomBeanPermissionProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class MonitorstationPermissionProvider extends AbstractCustomBeanPermissionProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean getCustomWritePermissionDecisionforUser(final User u) {
        final String type = (String)cidsBean.getProperty("type"); // NOI18N

        if ((type == null) || type.startsWith("LI-")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean getCustomReadPermissionDecisionforUser(final User u) {
        return true;
    }
}
