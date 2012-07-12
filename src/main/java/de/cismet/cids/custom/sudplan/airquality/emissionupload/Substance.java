/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public enum Substance {

    //~ Enum constants ---------------------------------------------------------

    NOX("NO\u2093", "NOX"), NH3("NH\u2083", "NH3"), SO2("SO\u2082", "SO2"), CO("CO", "CO"), NMVOC("VOC", "NMVOC"), // NOI18N
    PM10("PM10", "PM10");                                                                                          // NOI18N

    //~ Instance fields --------------------------------------------------------

    private String representationUI;
    private String representationFile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Substances object.
     *
     * @param  representationUI    DOCUMENT ME!
     * @param  representationFile  DOCUMENT ME!
     */
    private Substance(final String representationUI, final String representationFile) {
        this.representationUI = representationUI;
        this.representationFile = representationFile;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRepresentationFile() {
        return representationFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRepresentationUI() {
        return representationUI;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   substance  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Substance substanceFor(final String substance) {
        for (final Substance value : values()) {
            if (value.representationFile.equals(substance) || value.representationUI.equals(substance)) {
                return value;
            }
        }

        return null;
    }
}
