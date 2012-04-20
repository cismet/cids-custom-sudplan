/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import de.cismet.cids.custom.sudplan.DefaultRunInfo;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class SwmmRunInfo extends DefaultRunInfo {

    //~ Instance fields --------------------------------------------------------

    private String runId = "-1";
    private String spsUrl = "http://sudplan.ait.ac.at:8082/";

    private String modelName = "swmm+r";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMRunInfo object.
     */
    public SwmmRunInfo() {
    }

    /**
     * Creates a new GeoCPMRunInfo object.
     *
     * @param  runId      DOCUMENT ME!
     * @param  spsUrl     DOCUMENT ME!
     * @param  modelName  downloaded DOCUMENT ME!
     */
    public SwmmRunInfo(final String runId, final String spsUrl, final String modelName) {
        this.runId = runId;
        this.spsUrl = spsUrl;
        this.modelName = modelName;
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
    public String getSpsUrl() {
        return spsUrl;
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
     * @param  clientUrl  spsUrl DOCUMENT ME!
     */
    public void setSpsUrl(final String clientUrl) {
        this.spsUrl = clientUrl;
    }

    /**
     * Get the value of modelName.
     *
     * @return  the value of modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Set the value of modelName.
     *
     * @param  modelName  new value of modelName
     */
    public void setModelName(final String modelName) {
        this.modelName = modelName;
    }
}
