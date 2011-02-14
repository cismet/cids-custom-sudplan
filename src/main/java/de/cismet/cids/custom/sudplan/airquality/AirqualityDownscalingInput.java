/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.util.Date;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingInput {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingInput.class);

    //~ Instance fields --------------------------------------------------------

    private Date created;
    private String createdBy;
    private String name;
    private String scenario;
    private Integer targetYear;
    private Integer timeseriesId;
    private String timeseriesName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingInput object.
     */
    public AirqualityDownscalingInput() {
        this(null, null, null, null, null, null, null);
    }

    /**
     * Creates a new RainfallDownscalingInput object.
     *
     * @param  created         DOCUMENT ME!
     * @param  createdBy       DOCUMENT ME!
     * @param  name            DOCUMENT ME!
     * @param  scenario        DOCUMENT ME!
     * @param  targetYear      DOCUMENT ME!
     * @param  timeseriesId    DOCUMENT ME!
     * @param  timeseriesName  DOCUMENT ME!
     */
    public AirqualityDownscalingInput(final Date created,
            final String createdBy,
            final String name,
            final String scenario,
            final Integer targetYear,
            final Integer timeseriesId,
            final String timeseriesName) {
        this.created = created;
        this.createdBy = createdBy;
        this.name = name;
        this.scenario = scenario;
        this.targetYear = targetYear;
        this.timeseriesId = timeseriesId;
        this.timeseriesName = timeseriesName;
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
     * @param  timeseriesId  DOCUMENT ME!
     */
    public void setTimeseriesId(final Integer timeseriesId) {
        this.timeseriesId = timeseriesId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeseriesName  DOCUMENT ME!
     */
    public void setTimeseriesName(final String timeseriesName) {
        this.timeseriesName = timeseriesName;
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
    public Integer getTimeseriesId() {
        return timeseriesId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTimeseriesName() {
        return timeseriesName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchTimeseries() {
        final String domain = SessionManager.getSession().getUser().getDomain();

        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SMSUtils.TABLENAME_TIMESERIES);
        try {
            final MetaObject mo = SessionManager.getProxy().getMetaObject(timeseriesId, mc.getID(), domain);

            return mo.getBean();
        } catch (final ConnectionException ex) {
            LOG.warn("cannot get timeseries bean from server", ex); // NOI18N
            return null;
        }
    }
}
