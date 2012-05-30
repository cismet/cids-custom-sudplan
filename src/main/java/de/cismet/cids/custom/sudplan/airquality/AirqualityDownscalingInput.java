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
    private String description;
    private String scenario;
    private Date startDate;
    private Date endDate;
    private Coordinate lowerleft;
    private Coordinate upperright;
    private Integer gridcellSize;
    private Long gridcellCountX;
    private Long gridcellCountY;
    private String database;
    private String srs;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingInput object.
     */
    public AirqualityDownscalingInput() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * Creates a new RainfallDownscalingInput object.
     *
     * @param  created         DOCUMENT ME!
     * @param  createdBy       DOCUMENT ME!
     * @param  name            DOCUMENT ME!
     * @param  description     DOCUMENT ME!
     * @param  scenario        DOCUMENT ME!
     * @param  startDate       DOCUMENT ME!
     * @param  endDate         DOCUMENT ME!
     * @param  lowerleft       DOCUMENT ME!
     * @param  upperright      DOCUMENT ME!
     * @param  gridcellSize    DOCUMENT ME!
     * @param  gridcellCountX  DOCUMENT ME!
     * @param  gridcellCountY  DOCUMENT ME!
     * @param  database        DOCUMENT ME!
     * @param  srs             DOCUMENT ME!
     */
    public AirqualityDownscalingInput(final Date created,
            final String createdBy,
            final String name,
            final String description,
            final String scenario,
            final Date startDate,
            final Date endDate,
            final Coordinate lowerleft,
            final Coordinate upperright,
            final Integer gridcellSize,
            final Long gridcellCountX,
            final Long gridcellCountY,
            final String database,
            final String srs) {
        this.created = created;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.scenario = scenario;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lowerleft = lowerleft;
        this.upperright = upperright;
        this.gridcellSize = gridcellSize;
        this.gridcellCountX = gridcellCountX;
        this.gridcellCountY = gridcellCountY;
        this.database = database;
        this.srs = srs;
    }

    //~ Methods ----------------------------------------------------------------

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
     * @param  created  DOCUMENT ME!
     */
    public void setCreated(final Date created) {
        this.created = created;
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
     * @param  createdBy  DOCUMENT ME!
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDatabase() {
        return database;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  database  DOCUMENT ME!
     */
    public void setDatabase(final String database) {
        this.database = database;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  DOCUMENT ME!
     */
    public void setDescription(final String description) {
        this.description = description;
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
    public Long getGridcellCountX() {
        return gridcellCountX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellCountX  DOCUMENT ME!
     */
    public void setGridcellCountX(final Long gridcellCountX) {
        this.gridcellCountX = gridcellCountX;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Long getGridcellCountY() {
        return gridcellCountY;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellCountY  DOCUMENT ME!
     */
    public void setGridcellCountY(final Long gridcellCountY) {
        this.gridcellCountY = gridcellCountY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGridcellSize() {
        return gridcellSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellSize  DOCUMENT ME!
     */
    public void setGridcellSize(final Integer gridcellSize) {
        this.gridcellSize = gridcellSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coordinate getLowerleft() {
        return lowerleft;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lowerleft  DOCUMENT ME!
     */
    public void setLowerleft(final Coordinate lowerleft) {
        this.lowerleft = lowerleft;
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
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.name = name;
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
    public Coordinate getUpperright() {
        return upperright;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  upperright  DOCUMENT ME!
     */
    public void setUpperright(final Coordinate upperright) {
        this.upperright = upperright;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSrs() {
        return srs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  srs  DOCUMENT ME!
     */
    public void setSrs(final String srs) {
        this.srs = srs;
    }
}
