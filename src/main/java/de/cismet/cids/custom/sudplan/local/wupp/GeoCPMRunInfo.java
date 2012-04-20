/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import de.cismet.cids.custom.sudplan.DefaultRunInfo;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMRunInfo extends DefaultRunInfo {

    //~ Instance fields --------------------------------------------------------

    private String runId;
    private String clientUrl;
    private boolean downloaded;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMRunInfo object.
     */
    public GeoCPMRunInfo() {
        this(null);
    }

    /**
     * Creates a new GeoCPMRunInfo object.
     *
     * @param  clientUrl  DOCUMENT ME!
     */
    public GeoCPMRunInfo(final String clientUrl) {
        this(null, clientUrl);
    }

    /**
     * Creates a new GeoCPMRunInfo object.
     *
     * @param  runId      DOCUMENT ME!
     * @param  clientUrl  DOCUMENT ME!
     */
    public GeoCPMRunInfo(final String runId, final String clientUrl) {
        this(runId, clientUrl, false);
    }

    /**
     * Creates a new GeoCPMRunInfo object.
     *
     * @param  runId       DOCUMENT ME!
     * @param  clientUrl   DOCUMENT ME!
     * @param  downloaded  DOCUMENT ME!
     */
    public GeoCPMRunInfo(final String runId, final String clientUrl, final boolean downloaded) {
        this.runId = runId;
        this.clientUrl = clientUrl;
        this.downloaded = downloaded;
    }

    //~ Methods ----------------------------------------------------------------

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
    public String getClientUrl() {
        return clientUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDownloaded() {
        return downloaded;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  runId  DOCUMENT ME!
     */
    public void setRunId(final String runId) {
        this.runId = runId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  clientUrl  DOCUMENT ME!
     */
    public void setClientUrl(final String clientUrl) {
        this.clientUrl = clientUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  downloaded  DOCUMENT ME!
     */
    public void setDownloaded(final boolean downloaded) {
        this.downloaded = downloaded;
    }
}
