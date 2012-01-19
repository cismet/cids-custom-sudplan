/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import at.ac.ait.enviro.sudplan.clientutil.SudplanSPSHelper;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class EtaWatchable extends SwmmWatchable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EtaWatchable object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  spsTask   DOCUMENT ME!
     */
    public EtaWatchable(final CidsBean cidsBean, final SudplanSPSHelper.Task spsTask) {
        super(cidsBean);
    }
}
