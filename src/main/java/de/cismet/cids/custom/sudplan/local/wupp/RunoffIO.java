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
public final class RunoffIO {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TABLENAME_GEOCPM_CONFIG = "GEOCPM_CONFIG"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient int geocpmInputId;
    private transient int raineventId;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getGeocpmInput() {
        return geocpmInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmInput  DOCUMENT ME!
     */
    public void setGeocpmInput(final int geocpmInput) {
        this.geocpmInputId = geocpmInput;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRainevent() {
        return raineventId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  raineventId  DOCUMENT ME!
     */
    public void setRainevent(final int raineventId) {
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
        return SMSUtils.fetchCidsBean(geocpmInputId, TABLENAME_GEOCPM_CONFIG);
    }
}
