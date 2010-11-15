/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import java.io.IOException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface Executable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    void execute() throws IOException;

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    void addProgressListener(final ProgressListener progressL);

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    void removeProgressListener(final ProgressListener progressL);
}
