/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
}
