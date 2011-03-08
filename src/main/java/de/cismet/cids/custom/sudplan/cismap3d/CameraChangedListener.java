/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d;

import java.util.EventListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface CameraChangedListener extends EventListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void cameraChanged();
}
