/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@Deprecated
public final class ExecutableThreadPool {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ExecutableThreadPool.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ExecutorService executor;

    // TODO: implement list clearing, cycling etc
    private final transient List<ExecutionState> execStates;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ExecutableThreadPool object.
     */
    private ExecutableThreadPool() {
        executor = Executors.newCachedThreadPool();
        execStates = new ArrayList<ExecutionState>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ExecutableThreadPool getInstance() {
        return LazyInititaliser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  executable  DOCUMENT ME!
     */
    public void waitFirst(final Executable executable) {
        final Future<Void> future = getFirst(executable);

        if (future == null) {
            return;
        }

        while (true) {
            try {
                future.get();

                return;
            } catch (final Exception e) {
                if (future.isDone()) {
                    return;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("caught exception while waiting but task not finished yet, waiting more", e); // NOI18N
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  executable  DOCUMENT ME!
     */
    public void execute(final Executable executable) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("submitting executable: " + executable); // NOI18N
        }

        final Future<Void> future = executor.submit(new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        executable.execute();

                        return null;
                    }
                });
        // we have to put the cidsbean here, or even better just classid, objectid because that is the information that
        // is relevant for this run. the executable will remain the same instance for different objects
        put(executable, future);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   executable  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InExecutionException  DOCUMENT ME!
     */
    public Exception getFirstException(final Executable executable) throws InExecutionException {
        final Future<Void> future = getFirst(executable);

        if (future == null) {
            return null;
        }

        if (!future.isDone()) {
            throw new InExecutionException("executable is still running: " + executable, executable); // NOI18N
        }

        try {
            future.get();

            return null;
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                return (Exception)cause;
            } else if (cause instanceof Error) {
                throw (Error)cause;
            } else {
                throw new IllegalStateException("cause is native throwable, which is illegal", cause); // NOI18N
            }
        } catch (final InterruptedException e) {
            throw new IllegalStateException("get was interrupted despite future was already done", e); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   executable  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public Exception getFirstExceptionBlock(final Executable executable) {
        waitFirst(executable);

        try {
            return getFirstException(executable);
        } catch (InExecutionException ex) {
            throw new IllegalStateException("execution not finished despite we waited for it"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   executable  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Future<Void> getFirst(final Executable executable) {
        final ExecutionState[] states = execStates.toArray(new ExecutionState[execStates.size()]);

        for (final ExecutionState state : states) {
            if (state.getKey().equals(executable)) {
                return state.getValue();
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  executable  DOCUMENT ME!
     * @param  future      DOCUMENT ME!
     */
    private void put(final Executable executable, final Future<Void> future) {
        assert executable != null : "executable cannot be null"; // NOI18N
        assert future != null : "future cannot be null";         // NOI18N

        execStates.add(new ExecutionState(executable, future));
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class ExecutionState implements Map.Entry<Executable, Future<Void>> {

        //~ Instance fields ----------------------------------------------------

        private final transient Executable executable;
        private final transient Future<Void> future;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ExecutionState object.
         *
         * @param  executable  DOCUMENT ME!
         * @param  future      DOCUMENT ME!
         */
        public ExecutionState(final Executable executable, final Future<Void> future) {
            this.executable = executable;
            this.future = future;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Executable getKey() {
            return executable;
        }

        @Override
        public Future<Void> getValue() {
            return future;
        }

        @Override
        public Future<Void> setValue(final Future<Void> value) {
            throw new UnsupportedOperationException("ExecutionState entries are immutable"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInititaliser {

        //~ Static fields/initializers -----------------------------------------

        private static final ExecutableThreadPool INSTANCE = new ExecutableThreadPool();
    }
}
