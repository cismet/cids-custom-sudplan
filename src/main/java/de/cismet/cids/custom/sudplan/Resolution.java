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
            NbBundle.getMessage(Resolution.class, "Resolution.DECADE.localisedName"),
            "10Y",
            "10Y"); // NOI18N
    public static final Resolution YEAR = new Resolution(
            NbBundle.getMessage(Resolution.class, "Resolution.YEAR.localisedName"),
            "1Y",
            "1Y");  // NOI18N
    public static final Resolution MONTH = new Resolution(
            NbBundle.getMessage(Resolution.class, "Resolution.MONTH.localisedName"),
            "1M",
            "1M");  // NOI18N
    public static final Resolution DAY = new Resolution(
            NbBundle.getMessage(Resolution.class, "Resolution.DAILY.localisedName"),
            "86400s",
            "1d");  // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final String precision;
    private final String offeringSuffix;

    //~ Constructors -----------------------------------------------------------

// public static final Resolution HOUR = new Resolution(
// NbBundle.getMessage(Resolution.class, "Resolution.HOURLY.localisedName")); // NOI18N

    /**
     * Creates a new Resolution object.
     *
     * @param  localisedName   DOCUMENT ME!
     * @param  precision       DOCUMENT ME!
     * @param  offeringSuffix  DOCUMENT ME!
     */
    private Resolution(final String localisedName, final String precision, final String offeringSuffix) {
        super(localisedName);

        this.precision = precision;
        this.offeringSuffix = offeringSuffix;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPrecision() {
        return this.precision;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getOfferingSuffix() {
        return this.offeringSuffix;
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
        return new Resolution[] { DECADE, YEAR, MONTH, DAY }; // , HOUR };
    }
}
