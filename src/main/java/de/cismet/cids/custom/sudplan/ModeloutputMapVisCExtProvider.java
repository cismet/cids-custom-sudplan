/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.server.middleware.types.MetaClass;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

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
public final class ModeloutputMapVisCExtProvider extends AbstractMapVisCExtProvider {

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

        if ((mc == null) || !SMSUtils.TABLENAME_MODELOUTPUT.equals(mc.getTableName())) {
            return new ArrayList(0);
        } else {
            return Arrays.asList(new ModelOutputMapVisualisationProvider());
        }
    }
}
