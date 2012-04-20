/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import java.io.IOException;

import de.cismet.cids.dynamics.CidsBean;

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

    /**
     * Returns the run info objexct of the executable which contains default and custom properties of the execution
     * status (e.g. error messages)
     *
     * @param   <T>  type of the run info object
     *
     * @return  the deserialized run info object or null
     */
    <T extends RunInfo> T getRunInfo();
}
