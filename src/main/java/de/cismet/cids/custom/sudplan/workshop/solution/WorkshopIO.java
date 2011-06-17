/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.workshop.solution;

import java.util.Arrays;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class WorkshopIO {

    //~ Instance fields --------------------------------------------------------

    private final int[] integers;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkshopIO object.
     *
     * @param  integers  DOCUMENT ME!
     */
    public WorkshopIO(final int[] integers) {
        this.integers = integers;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int[] getIntegers() {
        return integers;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + Arrays.toString(integers);
    }
}
