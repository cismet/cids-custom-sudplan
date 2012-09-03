/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.server.middleware.types.MetaClass;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.navigatorplugin.MapVisualisationProvider;

import de.cismet.ext.CExtContext;
import de.cismet.ext.CExtProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(
    service = CExtProvider.class,
    position = 1000
)
public final class HydroCExtProvider implements CExtProvider {

    //~ Instance fields --------------------------------------------------------

    private final String ifaceClass;
    private final String concreteClass;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HydroCExtProvider object.
     */
    public HydroCExtProvider() {
        ifaceClass = "de.cismet.cismap.navigatorplugin.MapVisualisationProvider";           // NOI18N
        concreteClass = "de.cismet.cismap.navigatorplugin.DefaultMapVisualisationProvider"; // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection provideExtensions(final CExtContext context) {
        final Object ref = context.getProperty(CExtContext.CTX_REFERENCE);
        final MetaClass mc;
        if (ref instanceof MetaClass) {
            mc = (MetaClass)ref;
        } else if (ref instanceof CidsBean) {
            mc = ((CidsBean)ref).getMetaObject().getMetaClass();
        } else {
            mc = null;
        }

        if ((mc == null) || !SMSUtils.TABLENAME_HYDROLOGY_WORKSPACE.equals(mc.getTableName())) {
            return new ArrayList(0);
        } else {
            return Arrays.asList(new HydroWorkspaceMapVisualisationProvider());
        }
    }

    @Override
    public Class getType() {
        return MapVisualisationProvider.class;
    }

    @Override
    public boolean canProvide(final Class c) {
        final String cName = c.getCanonicalName();

        return (cName == null) ? false : (ifaceClass.equals(cName) || concreteClass.equals(cName));
    }
}
