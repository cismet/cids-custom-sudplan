/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.exception.ExceptionManager;
import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.io.IOException;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.concurrent.ProgressWatch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.commons.utils.ProgressEvent;
import de.cismet.commons.utils.ProgressListener;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractAsyncModelManager extends AbstractModelManager implements ProgressListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractAsyncModelManager.class);

    //~ Instance fields --------------------------------------------------------

    private transient AbstractModelRunWatchable watchable;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void progress(final ProgressEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("progressevent must not be null"); // NOI18N
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(cidsBean + " progress: '" + event.getMessage() + "' (" + event.getStep()
                        + "/" + event.getMaxSteps() + ") = " + event.getState()
                        + ", source = '" + event.getSource() + "' (" + this + ")");
        }
        if (event.getSource() instanceof AbstractModelRunWatchable) {
            final AbstractModelRunWatchable amrw = (AbstractModelRunWatchable)event.getSource();

            if (ProgressEvent.State.FINISHED.equals(event.getState())) {
                if (needsDownload()) {
                    downloadResults(amrw);
                } else {
                    watchable = amrw;
                    fireFinised();
                }
            } else if (ProgressEvent.State.BROKEN.equals(event.getState())) {
                if (event.getMessage() != null) {
                    fireBroken(event.getMessage());
                } else {
                    fireBroken();
                }
            } else if (ProgressEvent.State.PROGRESSING.equals(event.getState())) {
                fireProgressed(event.getStep(), event.getMaxSteps(), event.getMessage());
            }
        } else {
            LOG.warn("cannot process event, event source not of type AbstractModelRunWatch: " + event); // NOI18N

            fireBroken();
        }
    }

    @Override
    protected void internalExecute() throws IOException {
        prepareExecution();

        ProgressWatch.getWatch().submit(createWatchable());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    protected abstract void prepareExecution() throws IOException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public abstract AbstractModelRunWatchable createWatchable() throws IOException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract boolean needsDownload();

    /**
     * DOCUMENT ME!
     *
     * @param  amrw  DOCUMENT ME!
     */
    private void downloadResults(final AbstractModelRunWatchable amrw) {
        try {
            final int answer = JOptionPane.showConfirmDialog(ComponentRegistry.getRegistry().getMainWindow(),
                    NbBundle.getMessage(
                        AbstractAsyncModelManager.class,
                        "AbstractAsyncModelManager.downloadResults(AbstractModelRunWatchable).doDownloadDialog.message", // NOI18N
                        amrw.getCidsBean().getProperty("name")), // NOI18N
                    NbBundle.getMessage(
                        AbstractAsyncModelManager.class,
                        "AbstractAsyncModelManager.downloadResults(AbstractModelRunWatchable).doDownloadDialog.title"), // NOI18N
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (JOptionPane.YES_OPTION == answer) {
                final JDialog dialog = DownloadManagerDialog.instance(ComponentRegistry.getRegistry().getMainWindow());
                StaticSwingTools.showDialog(dialog);
                dialog.toFront();

                final Observer dlObs = new DownloadObserver(amrw);
                amrw.addObserver(dlObs);
                DownloadManager.instance().add(amrw);
            }
        } catch (final ConnectionException ex) {
            ExceptionManager.getManager()
                    .showExceptionDialog(
                        ExceptionManager.WARNING,
                        "RUN notification issue",                                 // NOI18N
                        "Could not fetch RUN cidsbean that just now finished execution!", // NOI18N
                        ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected AbstractModelRunWatchable getWatchable() {
        return watchable;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class DownloadObserver implements Observer {

        //~ Instance fields ----------------------------------------------------

        private final transient AbstractModelRunWatchable amrw;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DownloadObserver object.
         *
         * @param  amrw  DOCUMENT ME!
         */
        public DownloadObserver(final AbstractModelRunWatchable amrw) {
            this.amrw = amrw;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void update(final Observable o, final Object arg) {
            if (amrw.equals(o)) {
                if (Download.State.COMPLETED.equals(arg)
                            || Download.State.COMPLETED_WITH_ERROR.equals(arg)) {
                    amrw.deleteObserver(this);

                    try {
                        final CidsBean runBean = amrw.getCidsBean();
                        final Manager m = SMSUtils.loadManagerFromRun(runBean, ManagerType.MODEL);
                        if (m instanceof AbstractAsyncModelManager) {
                            final AbstractAsyncModelManager aamm = (AbstractAsyncModelManager)m;
                            aamm.setCidsBean(runBean);
                            aamm.watchable = amrw;
                            if (Download.State.COMPLETED_WITH_ERROR.equals(arg)) {
                                aamm.fireBroken();
                            } else {
                                aamm.fireFinised();
                            }
                        } else {
                            final String message = "ModelManager not of type AbstractAsyncModelManager"; // NOI18N
                            LOG.error(message);
                            throw new IllegalStateException(message);
                        }
                    } catch (final ConnectionException ex) {
                        LOG.error("cannot get cidsbean from AbstractModelRun", ex);                      // NOI18N
                    }
                }
            }
        }
    }
}
