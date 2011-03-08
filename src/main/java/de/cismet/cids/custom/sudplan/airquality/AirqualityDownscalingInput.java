/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingInput {

    //~ Instance fields --------------------------------------------------------

    private Date created;
    private String createdBy;
    private String name;
    private String scenario;
    private Date startDate;
    private Date endDate;
    private Coordinate llCoord;
    private Coordinate urCoord;
    private Integer gridSize;
    private Map<String, Set<Integer>> databases;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingInput object.
     */
    public AirqualityDownscalingInput() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * Creates a new RainfallDownscalingInput object.
     *
     * @param  created    DOCUMENT ME!
     * @param  createdBy  DOCUMENT ME!
     * @param  name       DOCUMENT ME!
     * @param  scenario   DOCUMENT ME!
     * @param  startDate  targetYear DOCUMENT ME!
     * @param  endDate    timeseriesId DOCUMENT ME!
     * @param  llCoord    timeseriesName DOCUMENT ME!
     * @param  urCoord    DOCUMENT ME!
     * @param  gridSize   DOCUMENT ME!
     * @param  databases  DOCUMENT ME!
     */
    public AirqualityDownscalingInput(final Date created,
            final String createdBy,
            final String name,
            final String scenario,
            final Date startDate,
            final Date endDate,
            final Coordinate llCoord,
            final Coordinate urCoord,
            final Integer gridSize,
            final Map<String, Set<Integer>> databases) {
        this.created = created;
        this.createdBy = createdBy;
        this.name = name;
        this.scenario = scenario;
        this.startDate = startDate;
        this.endDate = endDate;
        this.llCoord = llCoord;
        this.urCoord = urCoord;
        this.gridSize = gridSize;
        this.databases = databases;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  created  DOCUMENT ME!
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  createdBy  DOCUMENT ME!
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  scenario  DOCUMENT ME!
     */
    public void setScenario(final String scenario) {
        this.scenario = scenario;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCreated() {
        return created;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getScenario() {
        return scenario;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, Set<Integer>> getDatabases() {
        return databases;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  databases  DOCUMENT ME!
     */
    public void setDatabases(final Map<String, Set<Integer>> databases) {
        this.databases = databases;
    }

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
    public Integer getGridSize() {
        return gridSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridSize  DOCUMENT ME!
     */
    public void setGridSize(final Integer gridSize) {
        this.gridSize = gridSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coordinate getLlCoord() {
        return llCoord;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  llCoord  DOCUMENT ME!
     */
    public void setLlCoord(final Coordinate llCoord) {
        this.llCoord = llCoord;
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coordinate getUrCoord() {
        return urCoord;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  urCoord  DOCUMENT ME!
     */
    public void setUrCoord(final Coordinate urCoord) {
        this.urCoord = urCoord;
    }
}
