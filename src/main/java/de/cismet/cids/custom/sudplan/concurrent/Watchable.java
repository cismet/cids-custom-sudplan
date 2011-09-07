/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.concurrent;

import java.io.IOException;

import de.cismet.cids.custom.sudplan.ProgressEvent;
import de.cismet.cids.custom.sudplan.ProgressListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface Watchable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    ProgressEvent requestStatus() throws IOException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ProgressListener getStatusCallback();
}
