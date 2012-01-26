/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import java.util.Date;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallDownscalingInput {

    //~ Instance fields --------------------------------------------------------

    private Date created;
    private String createdBy;
    private String name;
    private String scenario;
    private Integer targetYear;
    private Integer rainfallObjectId;
    private String rainfallObjectName;
    private String rainfallObjectTableName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingInput object.
     */
    public RainfallDownscalingInput() {
        this(null, null, null, null, null, null, null, null);
    }

    /**
     * Creates a new RainfallDownscalingInput object.
     *
     * @param  created                  DOCUMENT ME!
     * @param  createdBy                DOCUMENT ME!
     * @param  name                     DOCUMENT ME!
     * @param  scenario                 DOCUMENT ME!
     * @param  targetYear               DOCUMENT ME!
     * @param  rainfallObjectId         DOCUMENT ME!
     * @param  rainfallObjectName       DOCUMENT ME!
     * @param  rainfallObjectTableName  DOCUMENT ME!
     */
    public RainfallDownscalingInput(final Date created,
            final String createdBy,
            final String name,
            final String scenario,
            final Integer targetYear,
            final Integer rainfallObjectId,
            final String rainfallObjectName,
            final String rainfallObjectTableName) {
        this.created = created;
        this.createdBy = createdBy;
        this.name = name;
        this.scenario = scenario;
        this.targetYear = targetYear;
        this.rainfallObjectId = rainfallObjectId;
        this.rainfallObjectName = rainfallObjectName;
        this.rainfallObjectTableName = rainfallObjectTableName;
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
     * @param  targetYear  DOCUMENT ME!
     */
    public void setTargetYear(final Integer targetYear) {
        this.targetYear = targetYear;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rainfallObjectId  DOCUMENT ME!
     */
    public void setRainfallObjectId(final Integer rainfallObjectId) {
        this.rainfallObjectId = rainfallObjectId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rainfallObjectName  DOCUMENT ME!
     */
    public void setRainfallObjectName(final String rainfallObjectName) {
        this.rainfallObjectName = rainfallObjectName;
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
    public Integer getTargetYear() {
        return targetYear;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getRainfallObjectId() {
        return rainfallObjectId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRainfallObjectName() {
        return rainfallObjectName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRainfallObjectTableName() {
        return rainfallObjectTableName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rainfallObjectTableName  DOCUMENT ME!
     */
    public void setRainfallObjectTableName(final String rainfallObjectTableName) {
        this.rainfallObjectTableName = rainfallObjectTableName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchRainfallObject() {
        return SMSUtils.fetchCidsBean(rainfallObjectId, rainfallObjectTableName);
    }
}
