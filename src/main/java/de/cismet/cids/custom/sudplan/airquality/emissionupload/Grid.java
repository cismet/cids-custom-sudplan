/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import java.io.File;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class Grid {

    //~ Instance fields --------------------------------------------------------

    private Substance substance;
    private File emissionGrid;
    private TimeVariation timeVariation;
    private File customTimeVariation;
    private GridHeight gridHeight;
    private String gridName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Grid object.
     */
    public Grid() {
        this.substance = Substance.NOX;
        this.emissionGrid = null;
        this.timeVariation = TimeVariation.CONSTANT;
        this.customTimeVariation = null;
        this.gridHeight = GridHeight.ZERO;
        this.gridName = "";
    }

    /**
     * Creates a new Grid object.
     *
     * @param  substance            DOCUMENT ME!
     * @param  emissionGrid         DOCUMENT ME!
     * @param  timeVariation        DOCUMENT ME!
     * @param  customTimeVariation  DOCUMENT ME!
     * @param  gridHeight           DOCUMENT ME!
     * @param  gridName             DOCUMENT ME!
     */
    public Grid(final Substance substance,
            final File emissionGrid,
            final TimeVariation timeVariation,
            final File customTimeVariation,
            final GridHeight gridHeight,
            final String gridName) {
        this.substance = substance;
        this.emissionGrid = emissionGrid;
        this.timeVariation = timeVariation;
        this.customTimeVariation = customTimeVariation;
        this.gridHeight = gridHeight;
        this.gridName = gridName;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public File getCustomTimeVariation() {
        return customTimeVariation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  customTimeVariation  DOCUMENT ME!
     */
    public void setCustomTimeVariation(final File customTimeVariation) {
        this.customTimeVariation = customTimeVariation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public File getEmissionGrid() {
        return emissionGrid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  emissionGrid  DOCUMENT ME!
     */
    public void setEmissionGrid(final File emissionGrid) {
        this.emissionGrid = emissionGrid;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GridHeight getGridHeight() {
        return gridHeight;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridHeight  DOCUMENT ME!
     */
    public void setGridHeight(final GridHeight gridHeight) {
        this.gridHeight = gridHeight;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGridName() {
        return gridName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridName  DOCUMENT ME!
     */
    public void setGridName(final String gridName) {
        this.gridName = gridName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Substance getSubstance() {
        return substance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  substance  DOCUMENT ME!
     */
    public void setSubstance(final Substance substance) {
        this.substance = substance;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TimeVariation getTimeVariation() {
        return timeVariation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeVariation  DOCUMENT ME!
     */
    public void setTimeVariation(final TimeVariation timeVariation) {
        this.timeVariation = timeVariation;
    }
}
