/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import java.awt.EventQueue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ProgressSupport {

    //~ Instance fields --------------------------------------------------------

    private final transient Set<ProgressListener> listeners;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProgressSupport object.
     */
    public ProgressSupport() {
        this.listeners = new HashSet<ProgressListener>(3);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    public void addProgressListener(final ProgressListener progressL) {
        listeners.add(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    public void removeProgressListener(final ProgressListener progressL) {
        listeners.remove(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    public void fireEvent(final ProgressEvent event) {
        final Iterator<ProgressListener> it;

        synchronized (listeners) {
            it = new HashSet<ProgressListener>(listeners).iterator();
        }

        while (it.hasNext()) {
            final ProgressListener next = it.next();
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        next.progress(event);
                    }
                });
        }
    }
}
