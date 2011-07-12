/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunoffIO {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TABLENAME_GEOCPM_CONFIG = "GEOCPM_CONFIG"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient int geocpmInputId;
    private transient int timeseriesId;
    private transient String runId;

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
     * @param  runId  DOCUMENT ME!
     */
    public void setRunId(final String runId) {
        this.runId = runId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getGeocpmInput() {
        return geocpmInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmInput  DOCUMENT ME!
     */
    public void setGeocpmInput(final int geocpmInput) {
        this.geocpmInputId = geocpmInput;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getTimeseries() {
        return timeseriesId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeseriesId  DOCUMENT ME!
     */
    public void setTimeseries(final int timeseriesId) {
        this.timeseriesId = timeseriesId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTimeseries() {
        return SMSUtils.fetchCidsBean(timeseriesId, SMSUtils.TABLENAME_TIMESERIES);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchGeocpmInput() {
        return SMSUtils.fetchCidsBean(geocpmInputId, TABLENAME_GEOCPM_CONFIG);
    }
}
