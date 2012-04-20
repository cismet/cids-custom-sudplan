/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

/**
 * Holds common properties for run info objects.
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public interface RunInfo {

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of broken.
     *
     * @return  the value of broken
     */
    boolean isBroken();

    /**
     * Set the value of broken.
     *
     * @param  broken  new value of broken
     */
    void setBroken(boolean broken);

    /**
     * Get the value of brokenMessage.
     *
     * @return  the value of brokenMessage
     */
    String getBrokenMessage();

    /**
     * Set the value of brokenMessage.
     *
     * @param  brokenMessage  new value of brokenMessage
     */
    void setBrokenMessage(String brokenMessage);

    /**
     * Get the value of canceled.
     *
     * @return  the value of canceled
     */
    boolean isCanceled();

    /**
     * Set the value of canceled.
     *
     * @param  canceled  new value of canceled
     */
    void setCanceled(boolean canceled);

    /**
     * Get the value of canceledMessage.
     *
     * @return  the value of canceledMessage
     */
    String getCanceledMessage();

    /**
     * Set the value of canceledMessage.
     *
     * @param  canceledMessage  new value of canceledMessage
     */
    void setCanceledMessage(String canceledMessage);
}
