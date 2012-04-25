/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.concurrent;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.cismet.cids.custom.sudplan.ProgressEvent;
import de.cismet.cids.custom.sudplan.ProgressEvent.State;
import de.cismet.cids.custom.sudplan.commons.CismetExecutors;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ProgressWatch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ProgressWatch.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ScheduledExecutorService poller;
    private final transient ExecutorService deregisterDispatcher;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProgressWatch object.
     */
    private ProgressWatch() {
        poller = Executors.newScheduledThreadPool(5, SudplanConcurrency.createThreadFactory("progress-watch")); // NOI18N
        deregisterDispatcher = CismetExecutors.newSingleThreadExecutor(
                SudplanConcurrency.createThreadFactory("deregister-dispatcher"));                               // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  watchable  rootResource DOCUMENT ME!
     */
    public void submit(final Watchable watchable) {
        final Deregister deregister = new Deregister();
        final Runnable poll = new Poll(deregister, watchable);

        final ScheduledFuture future = poller.scheduleWithFixedDelay(poll, 30, 30, TimeUnit.SECONDS);
        deregister.self = future;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ProgressWatch getWatch() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final ProgressWatch INSTANCE = new ProgressWatch();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class Deregister implements Runnable {

        //~ Instance fields ----------------------------------------------------

        ScheduledFuture self;

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            if (self != null) {
                self.cancel(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class Poll implements Runnable {

        //~ Static fields/initializers -----------------------------------------

        private static final int MAX_RETRIES = 5;

        //~ Instance fields ----------------------------------------------------

        private final transient Deregister deregister;
        private final transient Watchable watchable;

        private transient int retryCount;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GeoCPMPoll object.
         *
         * @param   deregister  DOCUMENT ME!
         * @param   watchable   progL DOCUMENT ME!
         *
         * @throws  IllegalArgumentException  DOCUMENT ME!
         */
        public Poll(final Deregister deregister, final Watchable watchable) {
            if (watchable == null) {
                throw new IllegalArgumentException("no Watchable provided, watchable must not be null"); // NOI18N
            }
            if (deregister == null) {
                LOG.warn("no Deregister object submitted, Poll will only stop in case of an error");     // NOI18N
            }

            this.deregister = deregister;
            this.watchable = watchable;
            this.retryCount = 0;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            try {
                final ProgressEvent status = watchable.requestStatus();
                final ProgressEvent progress = new ProgressEvent(
                        watchable,
                        status.getState(),
                        status.getStep(),
                        status.getMaxSteps(),
                        status.getMessage());

                watchable.getStatusCallback().progress(progress);

                if (ProgressEvent.State.FINISHED == status.getState()) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("progresswatch finished for watchable because status is FINISHED: " + watchable); // NOI18N
                    }

                    if (deregister != null) {
                        deregisterDispatcher.submit(deregister);
                    }
                } else if (ProgressEvent.State.BROKEN == status.getState()) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("progresswatch finished for watchable because status is BROKEN: " + watchable); // NOI18N
                    }

                    if (deregister != null) {
                        deregisterDispatcher.submit(deregister);
                    }
                }
            } catch (final IOException e) {
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    LOG.error("error in status poll: " + watchable, e); // NOI18N

                    final ProgressEvent progress = new ProgressEvent(watchable, State.BROKEN);

                    watchable.getStatusCallback().progress(progress);

                    if (deregister != null) {
                        deregisterDispatcher.submit(deregister);
                    }
                } else {
                    LOG.warn("error in status poll, retrying (no. " + retryCount + "/" + MAX_RETRIES + "): " // NOI18N
                                + watchable,
                        e);
                }
            }
        }
    }
}
