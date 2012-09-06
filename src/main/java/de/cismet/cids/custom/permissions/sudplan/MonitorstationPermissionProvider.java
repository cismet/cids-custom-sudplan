
package de.cismet.cids.custom.permissions.sudplan;

import Sirius.server.newuser.User;
import de.cismet.cids.dynamics.AbstractCustomBeanPermissionProvider;

/**
 *
 * @author martin.scholl@cismet.de
 */
public final class MonitorstationPermissionProvider extends AbstractCustomBeanPermissionProvider
{

    @Override
    public boolean getCustomWritePermissionDecisionforUser(User u)
    {
        final String type = (String)cidsBean.getProperty("type"); // NOI18N
        
        if(type == null || type.startsWith("LI-")){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean getCustomReadPermissionDecisionforUser(User u)
    {
        return true;
    }
}
