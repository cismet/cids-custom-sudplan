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
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public enum Resolution {

    //~ Enum constants ---------------------------------------------------------

    DECADE(86400, 3650, 3650), YEAR(8640, 365, 365), MONTH(720, 30, 30), DAY(24, 1, 1),
    HOUR(1, 0, 0.041666666666666667);

    //~ Instance fields --------------------------------------------------------

    private final int durationInHours;

    private final int durationInDays;

    private final double exactDurationInDays;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Resolution object.
     *
     * @param  durationInHours      DOCUMENT ME!
     * @param  durationInDays       DOCUMENT ME!
     * @param  exactDurationInDays  DOCUMENT ME!
     */
    private Resolution(final int durationInHours, final int durationInDays, final double exactDurationInDays) {
        this.durationInHours = durationInHours;
        this.durationInDays = durationInDays;
        this.exactDurationInDays = exactDurationInDays;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getDurationInDays() {
        return durationInDays;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getDurationInHours() {
        return durationInHours;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getExactDurationInDays() {
        return exactDurationInDays;
    }
}
