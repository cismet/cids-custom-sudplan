/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DataHandlerCacheException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>DataHandlerCacheException</code> without detail message.
     */
    public DataHandlerCacheException() {
    }

    /**
     * Constructs an instance of <code>DataHandlerCacheException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public DataHandlerCacheException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>DataHandlerCacheException</code> with the specified detail message and the
     * specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public DataHandlerCacheException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
