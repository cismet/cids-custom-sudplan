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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.io.File;

import java.util.Observable;

import de.cismet.cids.custom.sudplan.concurrent.Watchable;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.gui.downloadmanager.Download;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractModelRunWatchable extends Observable implements Watchable, Download {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractAsyncModelManager.class);

    //~ Instance fields --------------------------------------------------------

    private final transient CidsBean cidsBean;

    private final transient int metaclassId;
    private final transient int metaobjectId;

    private transient State currentState;
    private transient Exception downloadException;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractModelRunWatchable object.
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public AbstractModelRunWatchable(final CidsBean cidsBean) {
        if (cidsBean == null) {
            throw new IllegalArgumentException("cidsbean must not be null"); // NOI18N
        }

        final MetaClass mc = cidsBean.getMetaObject().getMetaClass();

        assert mc != null : "bean without meta class"; // NOI18N

        if (!mc.getName().equalsIgnoreCase("run")) {
            throw new IllegalArgumentException("this class can only be used with cidsBeans of type RUN"); // NOI18N
        }

        this.cidsBean = cidsBean;
        this.metaclassId = mc.getID();
        this.metaobjectId = cidsBean.getMetaObject().getID();
    }

    /**
     * Creates a new AbstractModelRunWatchable object.
     *
     * @param   metaclassId   DOCUMENT ME!
     * @param   metaobjectId  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public AbstractModelRunWatchable(final int metaclassId, final int metaobjectId) {
        if ((metaclassId < 1) || (metaobjectId < 1)) {
            throw new IllegalArgumentException(
                "neither metaclassId nor metaobjectId must be < 1: [metaclassId=" // NOI18N
                        + metaclassId
                        + " || metaobjectId="
                        + metaobjectId
                        + "]");                                                   // NOI18N
        }

        final MetaClass mc = ClassCacheMultiple.getMetaClass(SessionManager.getSession().getUser().getDomain(),
                metaclassId);

        assert mc != null : "bean without meta class"; // NOI18N

        if (!mc.getName().equalsIgnoreCase("run")) {
            throw new IllegalArgumentException("this class can only be used with cidsBeans of type RUN"); // NOI18N
        }

        this.cidsBean = null;
        this.metaclassId = metaclassId;
        this.metaobjectId = metaobjectId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ProgressListener getStatusCallback() {
        final CidsBean runBean;
        try {
            runBean = getCidsBean();
        } catch (ConnectionException ex) {
            final String message = "cannot load cidsbean"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        final Manager manager = SMSUtils.loadManagerFromRun(runBean, ManagerType.MODEL);

        if (manager instanceof ProgressListener) {
            manager.setCidsBean(runBean);

            return (ProgressListener)manager;
        } else {
            throw new IllegalStateException("cannot use manager instance that is not a ProgressListener"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException    DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public CidsBean getCidsBean() throws ConnectionException {
        if (cidsBean == null) {
            final String domain = SessionManager.getSession().getUser().getDomain();
            final MetaObject mo = SessionManager.getProxy().getMetaObject(metaobjectId, metaclassId, domain);

            if (mo == null) {
                throw new IllegalStateException("cannot fetch metaobject: [metaclassId=" + metaclassId // NOI18N
                            + " || metaobjectId=" + metaobjectId + "]"); // NOI18N
            }

            return mo.getBean();
        } else {
            return cidsBean;
        }
    }

    @Override
    public int getDownloadsTotal() {
        return 1;
    }

    @Override
    public int getDownloadsCompleted() {
        return State.COMPLETED.equals(getStatus()) ? 1 : 0;
    }

    @Override
    public int getDownloadsErroneous() {
        return State.COMPLETED_WITH_ERROR.equals(getStatus()) ? 1 : 0;
    }

    @Override
    public Exception getCaughtException() {
        return downloadException;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    protected void setDownloadException(final Exception e) {
        this.downloadException = e;
    }

    @Override
    public File getFileToSaveTo() {
        return null;
    }

    @Override
    public State getStatus() {
        return currentState;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  state  DOCUMENT ME!
     */
    protected void setStatus(final State state) {
        if (currentState != state) {
            this.currentState = state;
            setChanged();
            notifyObservers(state);
        }
    }
}
