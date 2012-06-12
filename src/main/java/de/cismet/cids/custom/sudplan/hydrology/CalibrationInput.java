/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@JsonIgnoreProperties(
    ignoreUnknown = true,
    value = { "timeseries" }
)
public final class CalibrationInput {

    //~ Instance fields --------------------------------------------------------

    private transient HashMap<Integer, Integer> basinToTimeseries;
    private transient int hydrologyWorkspaceId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CalibrationInput object.
     */
    public CalibrationInput() {
        basinToTimeseries = new HashMap<Integer, Integer>();
    }

    /**
     * Creates a new CalibrationInput object.
     *
     * @param  hydrologyWorkspaceId  DOCUMENT ME!
     */
    public CalibrationInput(final int hydrologyWorkspaceId) {
        this();

        this.hydrologyWorkspaceId = hydrologyWorkspaceId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  basinId       DOCUMENT ME!
     * @param  timeseriesId  DOCUMENT ME!
     */
    public void putTimeseries(final int basinId, final Integer timeseriesId) {
        basinToTimeseries.put(basinId, timeseriesId);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   basinId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getTimeseries(final int basinId) {
        return basinToTimeseries.get(basinId);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<Entry<Integer, Integer>> getTimeseries() {
        return basinToTimeseries.entrySet();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<Integer, Integer> getBasinToTimeseries() {
        return basinToTimeseries;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  basinToTimeseries  DOCUMENT ME!
     */
    public void setBasinToTimeseries(final HashMap<Integer, Integer> basinToTimeseries) {
        this.basinToTimeseries = basinToTimeseries;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getHydrologyWorkspaceId() {
        return hydrologyWorkspaceId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hydrologyWorkspaceId  DOCUMENT ME!
     */
    public void setHydrologyWorkspaceId(final int hydrologyWorkspaceId) {
        this.hydrologyWorkspaceId = hydrologyWorkspaceId;
    }
}
