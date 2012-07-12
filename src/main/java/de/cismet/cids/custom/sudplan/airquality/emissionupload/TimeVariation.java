/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import org.openide.util.NbBundle;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public enum TimeVariation {

    //~ Enum constants ---------------------------------------------------------

    CONSTANT(NbBundle.getMessage(TimeVariation.class, "TimeVariation.CONSTANT"), "constant"), // NOI18N
    TRAFFIC(NbBundle.getMessage(TimeVariation.class, "TimeVariation.TRAFFIC"), "traffic"),    // NOI18N
    CUSTOM(NbBundle.getMessage(TimeVariation.class, "TimeVariation.CUSTOM"), "custom");       // NOI18N

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
    private TimeVariation(final String representationUI, final String representationFile) {
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
     * @param   timeVariation  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static TimeVariation timeVariationFor(final String timeVariation) {
        for (final TimeVariation value : values()) {
            if (value.representationFile.equals(timeVariation) || value.representationUI.equals(timeVariation)) {
                return value;
            }
        }

        return null;
    }
}
