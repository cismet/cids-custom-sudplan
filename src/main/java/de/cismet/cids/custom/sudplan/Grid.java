/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import com.vividsolutions.jts.geom.Geometry;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface Grid {

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns the grid's data as a 2D array of {@link Double}s.
     *
     * @return  the grid's data, never <code>null</code>
     */
    Double[][] getData();

    /**
     * Returns the grid's boundaries as a {@link Geometry}.
     *
     * @return  the grid's boundaries or <code>null</code> if not present/unknown
     */
    Geometry getGeometry();

    /**
     * Returns the human readable data type of the data of the grid (e.g. Pressure, Rainfall).
     *
     * @return  the human readable data type of the grid data or <code>null</code> if not present/unknown
     */
    String getDataType();

    /**
     * Returns the unit of measurement of the grid data (e.g. hPa, l/m^2).
     *
     * @return  the unit of measurement of the grid data or <code>null</code> if not present/unknown
     */
    String getUnit();
}
