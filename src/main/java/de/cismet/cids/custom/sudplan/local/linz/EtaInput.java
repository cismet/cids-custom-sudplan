/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import java.util.ArrayList;
import java.util.List;

/**
 * Input for the ETA Calculation is the output of the SWMM Model Run!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class EtaInput extends SwmmOutput {

    //~ Instance fields --------------------------------------------------------

    protected transient List<EtaConfiguration> etaConfigurations = new ArrayList<EtaConfiguration>();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<EtaConfiguration> getEtaConfigurations() {
        return this.etaConfigurations;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  etaConfigurations  DOCUMENT ME!
     */
    public void setEtaConfigurations(final List<EtaConfiguration> etaConfigurations) {
        this.etaConfigurations = etaConfigurations;
    }
}
