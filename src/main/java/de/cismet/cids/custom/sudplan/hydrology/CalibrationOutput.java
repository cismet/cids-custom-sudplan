/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CalibrationOutput {

    //~ Instance fields --------------------------------------------------------

    private transient Integer resultTs;
    private transient Integer inputTs;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getInputTs() {
        return inputTs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  inputTs  DOCUMENT ME!
     */
    public void setInputTs(final Integer inputTs) {
        this.inputTs = inputTs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getResultTs() {
        return resultTs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  resultTs  DOCUMENT ME!
     */
    public void setResultTs(final Integer resultTs) {
        this.resultTs = resultTs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchResultTs() {
        return SMSUtils.fetchCidsBean(resultTs, "TIMESERIES"); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchInputTs() {
        return SMSUtils.fetchCidsBean(inputTs, "TIMESERIES"); // NOI18N
    }
}
