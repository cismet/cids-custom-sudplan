/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;

import java.util.Collection;

import de.cismet.cids.custom.sudplan.concurrent.ProgressWatch;
import de.cismet.cids.custom.sudplan.concurrent.Watchable;
import de.cismet.cids.custom.sudplan.server.search.UnfinishedRunSearchStatement;

import de.cismet.tools.configuration.StartupHook;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public final class SudplanStartupHook implements StartupHook {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SudplanStartupHook.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationStarted() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialising run status watcher"); // NOI18N
        }

        final UnfinishedRunSearchStatement urss = new UnfinishedRunSearchStatement();

        final Collection<MetaObject> unfinishedBeans;
        try {
            unfinishedBeans = SessionManager.getProxy().customServerSearch(urss);
        } catch (final ConnectionException ex) {
            LOG.error("cannot initialise unfinished run watch", ex); // NOI18N

            return;
        }

        for (final MetaObject mo : unfinishedBeans) {
            final Manager m = SMSUtils.loadManagerFromRun(mo.getBean(), ManagerType.MODEL);

            if (m instanceof AbstractAsyncModelManager) {
                try {
                    m.setCidsBean(mo.getBean());
                    final Watchable watchable = ((AbstractAsyncModelManager)m).createWatchable();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("submitting watchable: " + watchable); // NOI18N
                    }

                    ProgressWatch.getWatch().submit(watchable);
                } catch (final IOException ex) {
                    LOG.error("cannot create watchable for manager: " + m, ex); // NOI18N
                }
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info("ignoring manager, because it is not asynchronous: " + m); // NOI18N
                }
            }
        }
    }
}
