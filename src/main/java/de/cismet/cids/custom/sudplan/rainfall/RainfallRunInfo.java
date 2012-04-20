/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import de.cismet.cids.custom.sudplan.DefaultRunInfo;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallRunInfo extends DefaultRunInfo {

    //~ Instance fields --------------------------------------------------------

    private transient String taskId;
    private transient String handlerLookup;
    private transient String handlerUrl;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallRunInfo object.
     */
    public RainfallRunInfo() {
    }

    /**
     * Creates a new RainfallRunInfo object.
     *
     * @param  taskId         DOCUMENT ME!
     * @param  handlerLookup  DOCUMENT ME!
     * @param  handlerUrl     DOCUMENT ME!
     */
    public RainfallRunInfo(final String taskId, final String handlerLookup, final String handlerUrl) {
        this.taskId = taskId;
        this.handlerLookup = handlerLookup;
        this.handlerUrl = handlerUrl;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getHandlerLookup() {
        return handlerLookup;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  handlerLookup  DOCUMENT ME!
     */
    public void setHandlerLookup(final String handlerLookup) {
        this.handlerLookup = handlerLookup;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getHandlerUrl() {
        return handlerUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  handlerUrl  DOCUMENT ME!
     */
    public void setHandlerUrl(final String handlerUrl) {
        this.handlerUrl = handlerUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskId  DOCUMENT ME!
     */
    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }
}
