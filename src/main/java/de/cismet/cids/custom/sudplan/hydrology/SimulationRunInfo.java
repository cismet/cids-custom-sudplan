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
public final class SimulationRunInfo extends DefaultRunInfo {

    //~ Instance fields --------------------------------------------------------

    private transient String simulationId;
    private transient String executionId;
    private transient Integer basinId;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getExecutionId() {
        return executionId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  executionId  DOCUMENT ME!
     */
    public void setExecutionId(final String executionId) {
        this.executionId = executionId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSimulationId() {
        return simulationId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  simulationId  DOCUMENT ME!
     */
    public void setSimulationId(final String simulationId) {
        this.simulationId = simulationId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getBasinId() {
        return basinId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  basinId  DOCUMENT ME!
     */
    public void setBasinId(final Integer basinId) {
        this.basinId = basinId;
    }
}
