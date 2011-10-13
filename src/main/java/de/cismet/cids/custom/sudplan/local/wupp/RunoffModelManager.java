/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.apache.log4j.Logger;

import java.io.IOException;

import de.cismet.cids.custom.sudplan.AbstractModelManager;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.geocpmrest.GeoCPMRestClient;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMInput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMOutput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Status;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunoffModelManager extends AbstractModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunoffModelManager.class);

    //~ Instance fields --------------------------------------------------------

    private final transient PollAndDownload poller;
    private final transient GeoCPMRestClient client;

    private transient String runId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunoffModelManager object.
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public RunoffModelManager() throws IOException {
        if (isFinished()) {
            client = null;
            poller = null;
        } else {
            client = new GeoCPMRestClient("http://192.168.100.12:9986/GeoCPM"); // NOI18N
            runId = ((RunoffIO)getUR()).getRunId();

            poller = new PollAndDownload();
            poller.setPriority(4);
            poller.start();
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void internalExecute() throws IOException {
        if (isFinished()) {
            return;
        }

        final RunoffIO io = (RunoffIO)getUR();
        final CidsBean geocpmBean = io.fetchGeocpmInput();
        final GeoCPMInput input = new GeoCPMInput();

//        input.content = (String)geocpmBean.getProperty("input"); // NOI18N

        runId = client.runGeoCPM(input);
        io.setRunId(runId);
    }

    @Override
    public void finalise() throws IOException {
        poller.interrupt();
    }

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (runId == null) {
            throw new IllegalStateException("cannot create output if there is no runId: null"); // NOI18N
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating output bean for run: " + cidsBean); // NOI18N
        }

        try {
            poller.join();

            final CidsBean modelOutput = SMSUtils.createModelOutput("Output of Run: " + runId, // NOI18N
                    poller.output,
                    SMSUtils.Model.GEOCPM);

            return modelOutput.persist();
        } catch (final Exception e) {
            final String message = "cannot get results for run: " + runId; // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PollAndDownload extends Thread {

        //~ Instance fields ----------------------------------------------------

        transient GeoCPMOutput output;

        private final transient Logger LOG = Logger.getLogger(PollAndDownload.class);

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new StatusPoller object.
         */
        public PollAndDownload() {
            super("GeoCPM status poller and downloader " + RunoffModelManager.this.toString()); // NOI18N
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    if (runId == null) {
                        Thread.sleep(1000);
                    } else {
                        final Status status = client.getStatus(runId);
                        switch (status.status) {
                            case Status.STATUS_RUNNING: {
                                Thread.sleep(1000);
                                break;
                            }
                            case Status.STATUS_BROKEN: {
                                RunoffModelManager.this.fireBroken();
                                interrupt();
                                break;
                            }
                            case Status.STATUS_FINISHED: {
                                RunoffModelManager.this.fireFinised();
                                output = client.getResults(runId);
                                interrupt();
                                break;
                            }
                            default: {
                                RunoffModelManager.this.fireBroken();
                                interrupt();
                                throw new IllegalStateException("illegal run status for run '" + runId + "': " // NOI18N
                                            + status.status);
                            }
                        }
                    }
                } catch (final InterruptedException ex) {
                    // loop will be interrupted
                } catch (final Exception e) {
                    LOG.warn("error in status poller and downloader: " + runId, e); // NOI18N
                }
            }
        }
    }
}
