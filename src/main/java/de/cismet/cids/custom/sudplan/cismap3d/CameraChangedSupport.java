/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d;

import java.awt.EventQueue;

import java.util.HashSet;
import java.util.Iterator;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CameraChangedSupport {

    //~ Instance fields --------------------------------------------------------

    private final HashSet<CameraChangedListener> listeners;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CameraChangedSupport object.
     */
    public CameraChangedSupport() {
        listeners = new HashSet<CameraChangedListener>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  ccl  DOCUMENT ME!
     */
    public void addCameraChangedListener(final CameraChangedListener ccl) {
        listeners.add(ccl);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ccl  DOCUMENT ME!
     */
    public void removeCameraChangedListener(final CameraChangedListener ccl) {
        listeners.remove(ccl);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cce  DOCUMENT ME!
     */
    public void fireCameraChanged(final CameraChangedEvent cce) {
        final Iterator<CameraChangedListener> it;

        synchronized (listeners) {
            it = new HashSet<CameraChangedListener>(listeners).iterator();
        }

        while (it.hasNext()) {
            final CameraChangedListener ccl = it.next();
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        ccl.cameraChanged(cce);
                    }
                });
        }
    }
}
