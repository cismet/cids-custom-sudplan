/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import java.util.EventObject;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ProgressEvent extends EventObject {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum State {

        //~ Enum constants -----------------------------------------------------

        STARTED, PROGRESSING, FINISHED, BROKEN, CANCELED
    }

    //~ Instance fields --------------------------------------------------------

    private final transient State state;
    private final transient int step;
    private final transient int maxSteps;

    private final transient String message;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProgressEvent object.
     *
     * @param  eventObject  DOCUMENT ME!
     * @param  state        DOCUMENT ME!
     */
    public ProgressEvent(final Object eventObject, final State state) {
        this(eventObject, state, 0, 0);
    }

    /**
     * Creates a new ProgressEvent object.
     *
     * @param  eventObject  DOCUMENT ME!
     * @param  state        DOCUMENT ME!
     * @param  message      DOCUMENT ME!
     */
    public ProgressEvent(final Object eventObject, final State state, final String message) {
        this(eventObject, state, 0, 0, message);
    }

    /**
     * Creates a new ProgressEvent object.
     *
     * @param  eventObject  DOCUMENT ME!
     * @param  state        DOCUMENT ME!
     * @param  step         the current step or 0 if it is indeterminate
     * @param  maxSteps     the current maxsteps of 0 if it is indeterminate
     */
    public ProgressEvent(final Object eventObject, final State state, final int step, final int maxSteps) {
        this(eventObject, state, step, maxSteps, null);
    }

    /**
     * Creates a new ProgressEvent object.
     *
     * @param  eventObject  DOCUMENT ME!
     * @param  state        DOCUMENT ME!
     * @param  step         the current step or 0 if it is indeterminate
     * @param  maxSteps     the current maxsteps of 0 if it is indeterminate
     * @param  message      DOCUMENT ME!
     */
    public ProgressEvent(final Object eventObject,
            final State state,
            final int step,
            final int maxSteps,
            final String message) {
        super(eventObject);

        this.state = state;
        this.step = step;
        this.maxSteps = maxSteps;
        this.message = message;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public State getState() {
        return state;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getPercentFinished() {
        final int percent;

        if (indeterminate()) {
            percent = -1;
        } else {
            percent = step * 100 / maxSteps;
        }

        return percent;
    }

    /**
     * Returns the indeterminate state of the <code>ProgressEvent</code>. An <code>ProgressEvent</code> is considered
     * indeterminate if either {@link #getStep()} of {@link #maxSteps()} is <code>0</code>.
     *
     * @return  <code>true</code> if either {@link #getStep()} of {@link #maxSteps()} is <code>0</code>
     */
    public boolean indeterminate() {
        return (step == 0) || (maxSteps == 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getStep() {
        return step;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxSteps() {
        return maxSteps;
    }

    /**
     * Get the value of message.
     *
     * @return  the value of message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getState() + ": " + this.getMessage();
    }
}
