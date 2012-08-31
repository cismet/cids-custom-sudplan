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
    private transient int rfObjResultId;
    private transient String rfObjResultName;
    private transient int rfObjInputId;
    private transient String rfObjInputName;
    private transient String rfObjTableName;
    private transient Float[][] rfStatisticalData;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingOutput object.
     */
    public RainfallDownscalingOutput() {
    }

    /**
     * Creates a new RainfallDownscalingOutput object.
     *
     * @param  modelInputId       DOCUMENT ME!
     * @param  modelRunId         DOCUMENT ME!
     * @param  rfObjResultId      DOCUMENT ME!
     * @param  rfObjResultName    DOCUMENT ME!
     * @param  rfObjInputId       DOCUMENT ME!
     * @param  rfObjInputName     DOCUMENT ME!
     * @param  rfObjTableName     DOCUMENT ME!
     * @param  rfStatisticalData  DOCUMENT ME!
     */
    public RainfallDownscalingOutput(final int modelInputId,
            final int modelRunId,
            final int rfObjResultId,
            final String rfObjResultName,
            final int rfObjInputId,
            final String rfObjInputName,
            final String rfObjTableName,
            final Float[][] rfStatisticalData) {
        this.modelInputId = modelInputId;
        this.modelRunId = modelRunId;
        this.rfObjResultId = rfObjResultId;
        this.rfObjResultName = rfObjResultName;
        this.rfObjInputId = rfObjInputId;
        this.rfObjInputName = rfObjInputName;
        this.rfObjTableName = rfObjTableName;
        this.rfStatisticalData = rfStatisticalData;
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
    public CidsBean fetchResultRFObj() {
        return SMSUtils.fetchCidsBean(rfObjResultId, rfObjTableName);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchInputRFObj() {
        return SMSUtils.fetchCidsBean(rfObjInputId, rfObjTableName);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRfObjInputId() {
        return rfObjInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfObjInputId  DOCUMENT ME!
     */
    public void setRfObjInputId(final int rfObjInputId) {
        this.rfObjInputId = rfObjInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRfObjInputName() {
        return rfObjInputName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfObjInputName  DOCUMENT ME!
     */
    public void setRfObjInputName(final String rfObjInputName) {
        this.rfObjInputName = rfObjInputName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRfObjResultId() {
        return rfObjResultId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfObjResultId  DOCUMENT ME!
     */
    public void setRfObjResultId(final int rfObjResultId) {
        this.rfObjResultId = rfObjResultId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRfObjResultName() {
        return rfObjResultName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfObjResultName  DOCUMENT ME!
     */
    public void setRfObjResultName(final String rfObjResultName) {
        this.rfObjResultName = rfObjResultName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRfObjTableName() {
        return rfObjTableName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfObjTableName  DOCUMENT ME!
     */
    public void setRfObjTableName(final String rfObjTableName) {
        this.rfObjTableName = rfObjTableName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Float[][] getRfStatisticalData() {
        return rfStatisticalData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfStatisticalData  DOCUMENT ME!
     */
    public void setRfStatisticalData(final Float[][] rfStatisticalData) {
        this.rfStatisticalData = rfStatisticalData;
    }
}
