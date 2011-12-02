/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;
import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmInput {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TABLENAME_SWMM_PROJECT = "SWMM_PROJECT"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient String startDate;
    private transient String endDate;
    
    private transient int swmmProjectId;
    //private transient int timeseriesId;
    private transient List<Integer> timeseriesIds = new ArrayList<Integer>();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSwmmProject() {
        return swmmProjectId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmProjectId  geocpmInput DOCUMENT ME!
     */
    public void setSwmmProject(final int swmmProjectId) {
        this.swmmProjectId = swmmProjectId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Integer> getTimeseries() {
        return timeseriesIds;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeseriesId  raineventId DOCUMENT ME!
     */
    public void setTimeseries(final List<Integer> timeseriesIds) {
        this.timeseriesIds = timeseriesIds;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }


    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> fetchTimeseries() {
        
        assert this.timeseriesIds != null : "timeseries list is null";
        List<CidsBean> timeseriesBeans = new ArrayList<CidsBean>(this.timeseriesIds.size());
        for(int timeseriesId : this.timeseriesIds)
        {
            timeseriesBeans.add(SMSUtils.fetchCidsBean(timeseriesId, SMSUtils.TABLENAME_TIMESERIES));
        }
        
        return timeseriesBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchSwmmProject() {
        return SMSUtils.fetchCidsBean(this.getSwmmProject(), TABLENAME_SWMM_PROJECT);
    }
}
