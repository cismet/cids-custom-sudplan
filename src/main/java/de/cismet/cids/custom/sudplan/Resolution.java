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
            NbBundle.getMessage(Resolution.class, "Resolution.DECADE.localisedName")); // NOI18N
    public static final Resolution YEAR = new Resolution(
            NbBundle.getMessage(Resolution.class, "Resolution.YEAR.localisedName"));   // NOI18N
    public static final Resolution MONTH = new Resolution(
            NbBundle.getMessage(Resolution.class, "Resolution.MONTH.localisedName"));  // NOI18N
    public static final Resolution DAY = new Resolution(
            NbBundle.getMessage(Resolution.class, "Resolution.DAILY.localisedName"));  // NOI18N
    public static final Resolution HOUR = new Resolution(
            NbBundle.getMessage(Resolution.class, "Resolution.HOURLY.localisedName")); // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Resolution object.
     *
     * @param  localisedName  DOCUMENT ME!
     */
    private Resolution(final String localisedName) {
        super(localisedName);
    }

    //~ Methods ----------------------------------------------------------------

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
