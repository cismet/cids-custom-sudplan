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
public final class TimeseriesRetrieverException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>TimeseriesRetrieverException</code> without detail message.
     */
    public TimeseriesRetrieverException() {
    }

    /**
     * Constructs an instance of <code>TimeseriesRetrieverException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public TimeseriesRetrieverException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>TimeseriesRetrieverException</code> with the specified detail message and the
     * specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public TimeseriesRetrieverException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
