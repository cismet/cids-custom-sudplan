/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Holds common properties for run info objects.
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    /**
     * Get the value of finished.
     *
     * @return  the value of finished
     */
    boolean isFinished();

    /**
     * Set the value of finished.
     *
     * @param  finished  new value of finished
     */
    void setFinished(boolean finished);
}
