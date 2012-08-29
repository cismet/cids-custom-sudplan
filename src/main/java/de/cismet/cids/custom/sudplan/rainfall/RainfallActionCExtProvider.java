/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.custom.objectactions.sudplan.ActionProviderFactory;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.interfaces.CidsBeanAction;

import de.cismet.ext.CExtContext;
import de.cismet.ext.CExtProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0, 2012/08/29
 */
@ServiceProvider(service = CExtProvider.class)
public final class RainfallActionCExtProvider implements CExtProvider<CidsBeanAction> {

    //~ Instance fields --------------------------------------------------------

    private final String ifaceClass;
    private final String concreteClass;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallActionCExtProvider object.
     */
    public RainfallActionCExtProvider() {
        ifaceClass = "de.cismet.cids.utils.interfaces.CidsBeanAction";                            // NOI18N
        concreteClass = "de.cismet.cids.custom.sudplan.rainfall.RainfallDownscalingWizardAction"; // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends CidsBeanAction> provideExtensions(final CExtContext context) {
        final List<CidsBeanAction> actions = new ArrayList<CidsBeanAction>(1);

        if (context != null) {
            final Object ctxReference = context.getProperty(CExtContext.CTX_REFERENCE);

            final Object ctxObject;
            if (ctxReference instanceof Collection) {
                final Collection ctxCollection = (Collection)ctxReference;

                if (ctxCollection.size() == 1) {
                    ctxObject = ctxCollection.iterator().next();
                } else {
                    ctxObject = null;
                }
            } else if (ctxReference instanceof Object[]) {
                final Object[] ctxArray = (Object[])ctxReference;

                if (ctxArray.length == 1) {
                    ctxObject = ctxArray[0];
                } else {
                    ctxObject = null;
                }
            } else {
                ctxObject = ctxReference;
            }

            final MetaClass mc;
            final CidsBean ctxBean;
            if (ctxObject instanceof CidsBean) {
                ctxBean = (CidsBean)ctxObject;
                mc = ctxBean.getMetaObject().getMetaClass();
            } else if (ctxObject instanceof MetaObject) {
                final MetaObject mo = (MetaObject)ctxObject;
                ctxBean = mo.getBean();
                mc = mo.getMetaClass();
            } else {
                ctxBean = null;
                mc = null;
            }

            if (((mc != null) && (ctxBean != null))
                        && (SMSUtils.TABLENAME_TIMESERIES.equals(mc.getTableName())
                            || SMSUtils.TABLENAME_IDFCURVE.equals(mc.getTableName()))) {
                final CidsBeanAction action = ActionProviderFactory.getCidsBeanAction(
                        RainfallDownscalingWizardAction.class);
                action.setCidsBean(ctxBean);
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    public Class<? extends CidsBeanAction> getType() {
        return CidsBeanAction.class;
    }

    @Override
    public boolean canProvide(final Class<?> c) {
        final String cName = c.getCanonicalName();

        return (cName == null) ? false : (ifaceClass.equals(cName) || concreteClass.equals(cName));
    }
}
