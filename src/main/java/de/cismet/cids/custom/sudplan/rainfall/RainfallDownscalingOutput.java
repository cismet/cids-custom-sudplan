/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallDownscalingOutput {

    //~ Instance fields --------------------------------------------------------

    private transient int modelInputId;
    private transient int modelRunId;
    private transient int tsResultId;
    private transient String tsResultName;
    private transient int tsResult30Id;
    private transient String tsResult30Name;
    private transient int tsInput30Id;
    private transient String tsInput30Name;
    private transient int tsInput1dId;
    private transient String tsInput1dName;
    private transient int tsResult1dId;
    private transient String tsResult1dName;
    private transient int tsInputId;
    private transient String tsInputName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingOutput object.
     */
    public RainfallDownscalingOutput() {
    }

    /**
     * Creates a new RainfallDownscalingOutput object.
     *
     * @param  modelInputId    DOCUMENT ME!
     * @param  modelRunId      DOCUMENT ME!
     * @param  tsResultId      DOCUMENT ME!
     * @param  tsResultName    DOCUMENT ME!
     * @param  tsResult30Id    DOCUMENT ME!
     * @param  tsResult30Name  DOCUMENT ME!
     * @param  tsInput30Id     DOCUMENT ME!
     * @param  tsInput30Name   DOCUMENT ME!
     * @param  tsInput1dId     DOCUMENT ME!
     * @param  tsInput1dName   DOCUMENT ME!
     * @param  tsResult1dId    DOCUMENT ME!
     * @param  tsResult1dName  DOCUMENT ME!
     * @param  tsInputId       DOCUMENT ME!
     * @param  tsInputName     DOCUMENT ME!
     */
    public RainfallDownscalingOutput(final int modelInputId,
            final int modelRunId,
            final int tsResultId,
            final String tsResultName,
            final int tsResult30Id,
            final String tsResult30Name,
            final int tsInput30Id,
            final String tsInput30Name,
            final int tsInput1dId,
            final String tsInput1dName,
            final int tsResult1dId,
            final String tsResult1dName,
            final int tsInputId,
            final String tsInputName) {
        this.modelInputId = modelInputId;
        this.modelRunId = modelRunId;
        this.tsResultId = tsResultId;
        this.tsResultName = tsResultName;
        this.tsResult30Id = tsResult30Id;
        this.tsResult30Name = tsResult30Name;
        this.tsInput30Id = tsInput30Id;
        this.tsInput30Name = tsInput30Name;
        this.tsInput1dId = tsInput1dId;
        this.tsInput1dName = tsInput1dName;
        this.tsResult1dId = tsResult1dId;
        this.tsResult1dName = tsResult1dName;
        this.tsInputId = tsInputId;
        this.tsInputName = tsInputName;
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
    public int getTsInput30Id() {
        return tsInput30Id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsInput30Id  DOCUMENT ME!
     */
    public void setTsInput30Id(final int tsInput30Id) {
        this.tsInput30Id = tsInput30Id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsInput30Name() {
        return tsInput30Name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTsInput30() {
        return SMSUtils.fetchCidsBean(tsInput30Id, SMSUtils.TABLENAME_TIMESERIES);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsInput30Name  DOCUMENT ME!
     */
    public void setTsInput30Name(final String tsInput30Name) {
        this.tsInput30Name = tsInput30Name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getTsResult30Id() {
        return tsResult30Id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTsResult30() {
        return SMSUtils.fetchCidsBean(tsResult30Id, SMSUtils.TABLENAME_TIMESERIES);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsResult30Id  DOCUMENT ME!
     */
    public void setTsResult30Id(final int tsResult30Id) {
        this.tsResult30Id = tsResult30Id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsResult30Name() {
        return tsResult30Name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsResult30Name  DOCUMENT ME!
     */
    public void setTsResult30Name(final String tsResult30Name) {
        this.tsResult30Name = tsResult30Name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getTsResultId() {
        return tsResultId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsResultId  DOCUMENT ME!
     */
    public void setTsResultId(final int tsResultId) {
        this.tsResultId = tsResultId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsResultName() {
        return tsResultName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsResultName  DOCUMENT ME!
     */
    public void setTsResultName(final String tsResultName) {
        this.tsResultName = tsResultName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTsResult() {
        return SMSUtils.fetchCidsBean(tsResultId, SMSUtils.TABLENAME_TIMESERIES);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getTsInput1dId() {
        return tsInput1dId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsInput1dName() {
        return tsInput1dName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTsInput1d() {
        return SMSUtils.fetchCidsBean(tsInput1dId, SMSUtils.TABLENAME_TIMESERIES);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsInput1dId  DOCUMENT ME!
     */
    public void setTsInput1dId(final int tsInput1dId) {
        this.tsInput1dId = tsInput1dId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsInput1dName  DOCUMENT ME!
     */
    public void setTsInput1dName(final String tsInput1dName) {
        this.tsInput1dName = tsInput1dName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getTsResult1dId() {
        return tsResult1dId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsResult1dName() {
        return tsResult1dName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTsResult1d() {
        return SMSUtils.fetchCidsBean(tsResult1dId, SMSUtils.TABLENAME_TIMESERIES);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsResult1dId  DOCUMENT ME!
     */
    public void setTsResult1dId(final int tsResult1dId) {
        this.tsResult1dId = tsResult1dId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsResult1dName  DOCUMENT ME!
     */
    public void setTsResult1dName(final String tsResult1dName) {
        this.tsResult1dName = tsResult1dName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getTsInputId() {
        return tsInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsInputId  DOCUMENT ME!
     */
    public void setTsInputId(final int tsInputId) {
        this.tsInputId = tsInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTsInput() {
        return SMSUtils.fetchCidsBean(tsInputId, SMSUtils.TABLENAME_TIMESERIES);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsInputName() {
        return tsInputName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsInputName  DOCUMENT ME!
     */
    public void setTsInputName(final String tsInputName) {
        this.tsInputName = tsInputName;
    }
}
