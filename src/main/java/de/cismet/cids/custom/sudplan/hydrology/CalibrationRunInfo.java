/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import de.cismet.cids.custom.sudplan.DefaultRunInfo;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CalibrationRunInfo extends DefaultRunInfo {

    //~ Instance fields --------------------------------------------------------

    private transient String localModelId;
    private transient String submodelExecutionId;
    private transient String calibrationExecutionId;
    private transient int basinId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CalibrationRunInfo object.
     */
    public CalibrationRunInfo() {
    }

    /**
     * Creates a new CalibrationRunInfo object.
     *
     * @param  broken         DOCUMENT ME!
     * @param  brokenMessage  DOCUMENT ME!
     */
    public CalibrationRunInfo(final boolean broken, final String brokenMessage) {
        super(broken, brokenMessage);
    }

    /**
     * Creates a new CalibrationRunInfo object.
     *
     * @param  submodelExecutionId     DOCUMENT ME!
     * @param  calibrationExecutionId  DOCUMENT ME!
     */
    public CalibrationRunInfo(final String submodelExecutionId, final String calibrationExecutionId) {
        this.submodelExecutionId = submodelExecutionId;
        this.calibrationExecutionId = calibrationExecutionId;
    }

    /**
     * Creates a new CalibrationRunInfo object.
     *
     * @param  submodelExecutionId     DOCUMENT ME!
     * @param  calibrationExecutionId  DOCUMENT ME!
     * @param  broken                  DOCUMENT ME!
     * @param  brokenMessage           DOCUMENT ME!
     */
    public CalibrationRunInfo(final String submodelExecutionId,
            final String calibrationExecutionId,
            final boolean broken,
            final String brokenMessage) {
        super(broken, brokenMessage);
        this.submodelExecutionId = submodelExecutionId;
        this.calibrationExecutionId = calibrationExecutionId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCalibrationExecutionId() {
        return calibrationExecutionId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  calibrationExecutionId  DOCUMENT ME!
     */
    public void setCalibrationExecutionId(final String calibrationExecutionId) {
        this.calibrationExecutionId = calibrationExecutionId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSubmodelExecutionId() {
        return submodelExecutionId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  submodelExecutionId  DOCUMENT ME!
     */
    public void setSubmodelExecutionId(final String submodelExecutionId) {
        this.submodelExecutionId = submodelExecutionId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLocalModelId() {
        return localModelId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  localModelId  DOCUMENT ME!
     */
    public void setLocalModelId(final String localModelId) {
        this.localModelId = localModelId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getBasinId() {
        return basinId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  basinId  DOCUMENT ME!
     */
    public void setBasinId(final int basinId) {
        this.basinId = basinId;
    }
}
