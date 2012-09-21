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
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class DefaultRunInfo implements RunInfo {

    //~ Instance fields --------------------------------------------------------

    private boolean broken = false;
    private String brokenMessage;
    private boolean canceled = false;
    private String canceledMessage;

    private boolean finished = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DefaultRunInfo object.
     */
    public DefaultRunInfo() {
    }

    /**
     * Creates a new DefaultRunInfo object.
     *
     * @param  broken         DOCUMENT ME!
     * @param  brokenMessage  DOCUMENT ME!
     */
    public DefaultRunInfo(final boolean broken, final String brokenMessage) {
        this.broken = broken;
        this.brokenMessage = brokenMessage;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of broken.
     *
     * @return  the value of broken
     */
    @Override
    public boolean isBroken() {
        return broken;
    }

    /**
     * Set the value of broken.
     *
     * @param  broken  new value of broken
     */
    @Override
    public void setBroken(final boolean broken) {
        this.broken = broken;
    }

    /**
     * Get the value of brokenMessage.
     *
     * @return  the value of brokenMessage
     */
    @Override
    public String getBrokenMessage() {
        return brokenMessage;
    }

    /**
     * Set the value of brokenMessage.
     *
     * @param  brokenMessage  new value of brokenMessage
     */
    @Override
    public void setBrokenMessage(final String brokenMessage) {
        this.brokenMessage = brokenMessage;
    }

    /**
     * Get the value of canceled.
     *
     * @return  the value of canceled
     */
    @Override
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Set the value of canceled.
     *
     * @param  canceled  new value of canceled
     */
    @Override
    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }

    /**
     * Get the value of canceledMessage.
     *
     * @return  the value of canceledMessage
     */
    @Override
    public String getCanceledMessage() {
        return canceledMessage;
    }

    /**
     * Set the value of canceledMessage.
     *
     * @param  canceledMessage  new value of canceledMessage
     */
    @Override
    public void setCanceledMessage(final String canceledMessage) {
        this.canceledMessage = canceledMessage;
    }

    /**
     * Get the value of finished.
     *
     * @return  the value of finished
     */
    @Override
    public boolean isFinished() {
        return finished;
    }

    /**
     * Set the value of finished.
     *
     * @param  finished  new value of finished
     */
    @Override
    public void setFinished(final boolean finished) {
        this.finished = finished;
    }
}
