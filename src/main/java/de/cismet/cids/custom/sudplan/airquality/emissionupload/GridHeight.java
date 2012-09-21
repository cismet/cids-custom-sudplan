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
public enum GridHeight {

    //~ Enum constants ---------------------------------------------------------

    ZERO(NbBundle.getMessage(GridHeight.class, "GridHeight.ZERO"), "0-40"),       // NOI18N
    FORTY(NbBundle.getMessage(GridHeight.class, "GridHeight.FORTY"), "40-80"),    // NOI18N
    EIGHTY(NbBundle.getMessage(GridHeight.class, "GridHeight.EIGHTY"), "80-160"), // NOI18N
    HUNDREDSIXTY(NbBundle.getMessage(GridHeight.class, "GridHeight.HUNDREDSIXTY"), ">160"); // NOI18N

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
    private GridHeight(final String representationUI, final String representationFile) {
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
     * @param   gridHeight  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GridHeight gridHeightFor(final String gridHeight) {
        for (final GridHeight value : values()) {
            if (value.representationFile.equals(gridHeight) || value.representationUI.equals(gridHeight)) {
                return value;
            }
        }

        return null;
    }
}
