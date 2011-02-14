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
public final class InExecutionException extends Exception {

    //~ Instance fields --------------------------------------------------------

    private final transient Executable executable;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>InExecutionException</code> without detail message.
     */
    public InExecutionException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>InExecutionException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public InExecutionException(final String msg) {
        this(msg, null, null);
    }

    /**
     * Creates a new InExecutionException object.
     *
     * @param  executable  DOCUMENT ME!
     */
    public InExecutionException(final Executable executable) {
        this(null, null, executable);
    }

    /**
     * Constructs an instance of <code>InExecutionException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public InExecutionException(final String msg, final Throwable cause) {
        this(msg, cause, null);
    }

    /**
     * Creates a new InExecutionException object.
     *
     * @param  message     DOCUMENT ME!
     * @param  executable  DOCUMENT ME!
     */
    public InExecutionException(final String message, final Executable executable) {
        this(message, null, executable);
    }

    /**
     * Creates a new InExecutionException object.
     *
     * @param  message     DOCUMENT ME!
     * @param  cause       DOCUMENT ME!
     * @param  executable  DOCUMENT ME!
     */
    public InExecutionException(final String message, final Throwable cause, final Executable executable) {
        super(message, cause);
        this.executable = executable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Executable getExecutable() {
        return executable;
    }
}
