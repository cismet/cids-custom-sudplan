/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class SwmmOutput {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TABLENAME_LINZ_CSO = "LINZ_CSO"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    protected transient Map<String, CsoOverflow> csoOverflows = new HashMap<String, CsoOverflow>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmOutput object.
     */
    public SwmmOutput() {
    }

    //~ Methods ----------------------------------------------------------------

    // private transient csoParameters
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, CsoOverflow> getCsoOverflows() {
        return this.csoOverflows;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  csoOverflows  DOCUMENT ME!
     */
    public void setCsoOverflows(final Map<String, CsoOverflow> csoOverflows) {
        this.csoOverflows = csoOverflows;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> fetchCsos() {
        assert this.csoOverflows != null : "csoOverflows list is null";
        final List<CidsBean> csoOverflowBeans = new ArrayList<CidsBean>(this.csoOverflows.size());
        for (final CsoOverflow csoOverflow : this.csoOverflows.values()) {
            csoOverflowBeans.add(SMSUtils.fetchCidsBean(csoOverflow.getCso(), TABLENAME_LINZ_CSO));
        }

        return csoOverflowBeans;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class CsoOverflow {

        //~ Instance fields ----------------------------------------------------

        protected transient float overflowVolume;
        protected transient float overflowFrequency;
        protected transient float overflowDuration;
        protected transient int cso;
        protected transient String name;

        //~ Methods ------------------------------------------------------------

        /**
         * Get the value of overflowVolume.
         *
         * @return  the value of overflowVolume
         */
        public float getOverflowVolume() {
            return overflowVolume;
        }

        /**
         * Set the value of overflowVolume.
         *
         * @param  volume  overflowVolume new value of overflowVolume
         */
        public void setOverflowVolume(final float volume) {
            this.overflowVolume = volume;
        }

        /**
         * Get the value of overflowFrequency.
         *
         * @return  the value of overflowFrequency
         */
        public float getOverflowFrequency() {
            return overflowFrequency;
        }

        /**
         * Set the value of overflowFrequency.
         *
         * @param  frequency  new value of overflowFrequency
         */
        public void setOverflowFrequency(final float frequency) {
            this.overflowFrequency = frequency;
        }

        /**
         * Get the value of cso.
         *
         * @return  the value of cso
         */
        public int getCso() {
            return cso;
        }

        /**
         * Set the value of cso.
         *
         * @param  cso  new value of cso
         */
        public void setCso(final int cso) {
            this.cso = cso;
        }

        /**
         * Get the value of name.
         *
         * @return  the value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Set the value of name.
         *
         * @param  node  name new value of name
         */
        public void setName(final String node) {
            this.name = node;
        }

        /**
         * Get the value of overflowDuration.
         *
         * @return  the value of overflowDuration
         */
        public float getOverflowDuration() {
            return overflowDuration;
        }

        /**
         * Set the value of overflowDuration.
         *
         * @param  overflowDuration  new value of overflowDuration
         */
        public void setOverflowDuration(final float overflowDuration) {
            this.overflowDuration = overflowDuration;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean fetchCso() {
            return SMSUtils.fetchCidsBean(this.getCso(), TABLENAME_LINZ_CSO);
        }
    }
}
