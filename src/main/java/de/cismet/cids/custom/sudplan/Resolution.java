/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.openide.util.NbBundle;

import java.io.Serializable;

/**
 * Custom enumeration of all available Resolutions.
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
// TODO: refactor as soon as abstract enums are supported
// TODO: accuracy of this class is questionable, consider refactoring
public final class Resolution extends LocalisedEnum<Resolution> implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    public static final Resolution DECADE = new Resolution(
            86400,
            3650,
            3650,
            NbBundle.getMessage(Resolution.class, "Resolution.DECADE.localisedName")); // NOI18N
    public static final Resolution YEAR = new Resolution(
            8640,
            365,
            365,
            NbBundle.getMessage(Resolution.class, "Resolution.YEAR.localisedName"));   // NOI18N
    public static final Resolution MONTH = new Resolution(
            720,
            30,
            30,
            NbBundle.getMessage(Resolution.class, "Resolution.MONTH.localisedName"));  // NOI18N
    public static final Resolution DAY = new Resolution(
            24,
            1,
            1,
            NbBundle.getMessage(Resolution.class, "Resolution.DAILY.localisedName"));  // NOI18N
    public static final Resolution HOUR = new Resolution(
            1,
            0,
            0.041666666666666667,
            NbBundle.getMessage(Resolution.class, "Resolution.HOURLY.localisedName")); // NOI18N

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
     * @param  localisedName        DOCUMENT ME!
     */
    private Resolution(final int durationInHours,
            final int durationInDays,
            final double exactDurationInDays,
            final String localisedName) {
        super(localisedName);
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

    @Override
    protected Resolution[] internalValues() {
        return values();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Resolution[] values() {
        return new Resolution[] { DECADE, YEAR, MONTH, DAY, HOUR };
    }
}
