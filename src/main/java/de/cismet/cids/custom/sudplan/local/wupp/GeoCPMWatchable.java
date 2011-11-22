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

import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.cids.custom.sudplan.ProgressEvent;
import de.cismet.cids.custom.sudplan.ProgressListener;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.geocpmrest.GeoCPMRestClient;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMOutput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Status;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.Equals;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMWatchable extends AbstractModelRunWatchable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeoCPMWatchable.class);

    //~ Instance fields --------------------------------------------------------

    private final transient GeoCPMRestClient client;
    private final transient String runId;
    private final transient ProgressListener progL;

    private transient GeoCPMOutput output;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMWatchable object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  client    DOCUMENT ME!
     * @param  runId     DOCUMENT ME!
     */
    public GeoCPMWatchable(final CidsBean cidsBean, final GeoCPMRestClient client, final String runId) {
        this(cidsBean, client, runId, null);
    }

    /**
     * Creates a new GeoCPMWatchable object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  client    DOCUMENT ME!
     * @param  runId     DOCUMENT ME!
     * @param  progL     DOCUMENT ME!
     */
    public GeoCPMWatchable(
            final CidsBean cidsBean,
            final GeoCPMRestClient client,
            final String runId,
            final ProgressListener progL) {
        super(cidsBean);

        this.client = client;
        this.runId = runId;
        this.progL = progL;

        setStatus(State.WAITING);
    }

    /**
     * Creates a new GeoCPMWatchable object.
     *
     * @param  metaclassId   DOCUMENT ME!
     * @param  metaobjectId  DOCUMENT ME!
     * @param  clientUrl     DOCUMENT ME!
     * @param  runId         DOCUMENT ME!
     */
    public GeoCPMWatchable(final int metaclassId, final int metaobjectId, final String clientUrl, final String runId) {
        this(metaclassId, metaobjectId, clientUrl, runId, null);
    }

    /**
     * Creates a new GeoCPMWatchable object.
     *
     * @param  metaclassId   DOCUMENT ME!
     * @param  metaobjectId  DOCUMENT ME!
     * @param  clientUrl     DOCUMENT ME!
     * @param  runId         DOCUMENT ME!
     * @param  progL         DOCUMENT ME!
     */
    public GeoCPMWatchable(final int metaclassId,
            final int metaobjectId,
            final String clientUrl,
            final String runId,
            final ProgressListener progL) {
        super(metaclassId, metaobjectId);

        this.client = new GeoCPMRestClient(clientUrl);
        this.runId = runId;
        this.progL = progL;

        setStatus(State.WAITING);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ProgressEvent requestStatus() throws IOException {
        final Status status = client.getStatus(runId);
        switch (status.status) {
            case Status.STATUS_RUNNING: {
                return new ProgressEvent(this, ProgressEvent.State.PROGRESSING);
            }
            case Status.STATUS_BROKEN: {
                return new ProgressEvent(this, ProgressEvent.State.BROKEN);
            }
            case Status.STATUS_FINISHED: {
                return new ProgressEvent(this, ProgressEvent.State.FINISHED);
            }
            default: {
                throw new IOException("illegal run status for run '" + runId + "': " + status.status); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRunId() {
        return runId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GeoCPMOutput getOutput() {
        return output;
    }

    @Override
    public ProgressListener getStatusCallback() {
        if (progL == null) {
            return super.getStatusCallback();
        } else {
            return progL;
        }
    }

    @Override
    public void startDownload() {
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    setStatus(State.RUNNING);

                    try {
                        output = client.getResults(runId);
                    } catch (final Exception e) {
                        LOG.error("could not download run results", e); // NOI18N

                        setDownloadException(e);
                        setStatus(State.COMPLETED_WITH_ERROR);
                    }

                    setStatus(State.COMPLETED);
                }
            };

        SudplanConcurrency.getSudplanDownloadPool().submit(r);
    }

    @Override
    public String getTitle() {
        return "Result of GeoCPM run '" + runId + "'"; // NOI18N
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof GeoCPMWatchable) {
            final GeoCPMWatchable w = (GeoCPMWatchable)obj;

            return Equals.nullEqual(w.client, client)
                        && Equals.nullEqual(w.output, output)
                        && Equals.nullEqual(w.runId, runId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (37 * hash) + ((this.client != null) ? this.client.hashCode() : 0);
        hash = (37 * hash) + ((this.runId != null) ? this.runId.hashCode() : 0);
        hash = (37 * hash) + ((this.output != null) ? this.output.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "GeoCPM Watchable " + "[runId=" + runId    // NOI18N
                    + ", client=" + client                // NOI18N
                    + ", output=" + output                // NOI18N
                    + ", progL=" + progL                  // NOI18N
                    + ", this=" + super.toString() + "]"; // NOI18N
    }
}
