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
public final class ProgressEvent {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum State {

        //~ Enum constants -----------------------------------------------------

        STARTED, PROGRESSING, FINISHED
    }

    //~ Instance fields --------------------------------------------------------

    private final transient State state;
    private final transient int step;
    private final transient int maxSteps;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProgressEvent object.
     *
     * @param  state  DOCUMENT ME!
     */
    public ProgressEvent(final State state) {
        this(state, 0, 0);
    }

    /**
     * Creates a new ProgressEvent object.
     *
     * @param  state     DOCUMENT ME!
     * @param  step      percentFinished DOCUMENT ME!
     * @param  maxSteps  DOCUMENT ME!
     */
    public ProgressEvent(final State state, final int step, final int maxSteps) {
        this.state = state;
        this.step = step;
        this.maxSteps = maxSteps;
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
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
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
}
