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
public final class RunoffInput {

    //~ Instance fields --------------------------------------------------------

    private transient int geocpmInputId;
    private transient int raineventId;
    private transient int deltaInputId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunoffInput object.
     */
    public RunoffInput() {
        this(-1, -1, -1);
    }

    /**
     * Creates a new RunoffInput object.
     *
     * @param  raineventId  DOCUMENT ME!
     */
    public RunoffInput(final int raineventId) {
        this(-1, raineventId, -1);
    }

    /**
     * Creates a new RunoffInput object.
     *
     * @param  raineventId   DOCUMENT ME!
     * @param  deltaInputId  DOCUMENT ME!
     */
    public RunoffInput(final int raineventId, final int deltaInputId) {
        this(-1, raineventId, deltaInputId);
    }

    /**
     * Creates a new RunoffInput object.
     *
     * @param  geocpmInputId  DOCUMENT ME!
     * @param  raineventId    DOCUMENT ME!
     * @param  deltaInputId   DOCUMENT ME!
     */
    public RunoffInput(final int geocpmInputId, final int raineventId, final int deltaInputId) {
        this.geocpmInputId = geocpmInputId;
        this.raineventId = raineventId;
        this.deltaInputId = deltaInputId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getDeltaInputId() {
        return deltaInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  deltaInputId  DOCUMENT ME!
     */
    public void setDeltaInputId(final int deltaInputId) {
        this.deltaInputId = deltaInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getGeocpmInputId() {
        return geocpmInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmInputId  DOCUMENT ME!
     */
    public void setGeocpmInputId(final int geocpmInputId) {
        this.geocpmInputId = geocpmInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRaineventId() {
        return raineventId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  raineventId  DOCUMENT ME!
     */
    public void setRaineventId(final int raineventId) {
        this.raineventId = raineventId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchRainevent() {
        return SMSUtils.fetchCidsBean(raineventId, SMSUtils.TABLENAME_RAINEVENT);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchGeocpmInput() {
        if (geocpmInputId < 0) {
            return null;
        } else {
            return SMSUtils.fetchCidsBean(geocpmInputId, SMSUtils.TABLENAME_GEOCPM_CONFIGURATION);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchDeltaInput() {
        if (deltaInputId < 0) {
            return null;
        } else {
            return SMSUtils.fetchCidsBean(deltaInputId, SMSUtils.TABLENAME_DELTA_CONFIGURATION);
        }
    }
}
