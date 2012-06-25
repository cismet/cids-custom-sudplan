/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import java.util.HashSet;
import java.util.Set;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SimulationOutput {

    //~ Instance fields --------------------------------------------------------

    private transient Set<Integer> resultTsIds;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimulationOutput object.
     */
    public SimulationOutput() {
        this.resultTsIds = new HashSet<Integer>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    public void addTimeseries(final Integer id) {
        resultTsIds.add(id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<Integer> getResultTsIds() {
        return resultTsIds;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  resultTsIds  DOCUMENT ME!
     */
    public void setResultTsIds(final Set<Integer> resultTsIds) {
        this.resultTsIds = resultTsIds;
    }
}
