/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class WFSRetrievalException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>WFSRetrievalException</code> without detail message.
     */
    public WFSRetrievalException() {
    }

    /**
     * Constructs an instance of <code>WFSRetrievalException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public WFSRetrievalException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>WFSRetrievalException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public WFSRetrievalException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
