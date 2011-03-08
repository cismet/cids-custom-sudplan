/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingOutput {

    //~ Instance fields --------------------------------------------------------

    private transient int modelInputId;
    private transient int modelRunId;
    private transient String taskId;
    private transient String tstburl;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingOutput object.
     */
    public AirqualityDownscalingOutput() {
        this(-1, -1, null, null);
    }

    /**
     * Creates a new AirqualityDownscalingOutput object.
     *
     * @param  modelInputId  DOCUMENT ME!
     * @param  modelRunId    DOCUMENT ME!
     * @param  taskId        DOCUMENT ME!
     * @param  tstburl       DOCUMENT ME!
     */
    public AirqualityDownscalingOutput(final int modelInputId,
            final int modelRunId,
            final String taskId,
            final String tstburl) {
        this.modelInputId = modelInputId;
        this.modelRunId = modelRunId;
        this.taskId = taskId;
        this.tstburl = tstburl;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getModelInputId() {
        return modelInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  modelInputId  DOCUMENT ME!
     */
    public void setModelInputId(final int modelInputId) {
        this.modelInputId = modelInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getModelRunId() {
        return modelRunId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  modelRunId  DOCUMENT ME!
     */
    public void setModelRunId(final int modelRunId) {
        this.modelRunId = modelRunId;
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTstburl() {
        return tstburl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tstburl  DOCUMENT ME!
     */
    public void setTstburl(final String tstburl) {
        this.tstburl = tstburl;
    }
}
