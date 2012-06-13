/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import se.smhi.sudplan.client.Scenario;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SimulationInput {

    //~ Instance fields --------------------------------------------------------

    private transient Integer hydrologyWorkspaceId;
    private transient Scenario scenario;
    private transient Date startDate;
    private transient Date endDate;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  endDate  DOCUMENT ME!
     */
    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getHydrologyWorkspaceId() {
        return hydrologyWorkspaceId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hydrologyWorkspaceId  DOCUMENT ME!
     */
    public void setHydrologyWorkspaceId(final Integer hydrologyWorkspaceId) {
        this.hydrologyWorkspaceId = hydrologyWorkspaceId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Scenario getScenario() {
        return scenario;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  scenario  DOCUMENT ME!
     */
    public void setScenario(final Scenario scenario) {
        this.scenario = scenario;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  startDate  DOCUMENT ME!
     */
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }
}
