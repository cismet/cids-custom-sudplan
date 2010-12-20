/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import de.cismet.cids.custom.sudplan.Grid;
import de.cismet.cids.custom.sudplan.ImmutableGrid;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GridMultiplyInputManager extends MultiplyInputManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridMultiplyInputManager.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridMultiplyInputManager object.
     */
    public GridMultiplyInputManager() {
        super();
        ui = new GridMultiplyInputManagerUI(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    Double[] getMultipliers() throws IOException {
        // ignore
        return new Double[0];
    }

    @Override
    void setMultipliers(final Double[] multipliers) throws IOException {
        // ignore
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    Grid getMultiplierGrid() throws IOException {
        final Grid grid;
        final File multipliersInput = getFactorFile(new File(getLocation()), FILENAME_MULTIPLIERS, false);

        if (multipliersInput == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("no multipliers file found, multipliers empty"); // NOI18N
            }
            grid = new ImmutableGrid(null);
        } else {
            grid = MultiplyHelper.gridFromFile(multipliersInput);
        }

        return grid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   grid  multipliers DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    void setMultiplierGrid(final Grid grid) throws IOException {
        MultiplyHelper.gridToFile(getFactorFile(new File(getLocation()), FILENAME_MULTIPLIERS, true), grid);
    }
}
