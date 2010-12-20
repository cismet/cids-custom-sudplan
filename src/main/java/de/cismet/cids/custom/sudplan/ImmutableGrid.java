/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import com.vividsolutions.jts.geom.Geometry;

import java.io.Serializable;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ImmutableGrid implements Grid, Serializable {

    //~ Instance fields --------------------------------------------------------

    private final Double[][] data;
    private final Geometry geometry;
    private final String dataType;
    private final String unit;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImmutableGrid object.
     *
     * @param  data  DOCUMENT ME!
     */
    public ImmutableGrid(final Double[][] data) {
        this(data, null, null, null);
    }

    /**
     * Creates a new ImmutableGrid object.
     *
     * @param  data      DOCUMENT ME!
     * @param  geometry  DOCUMENT ME!
     */
    public ImmutableGrid(final Double[][] data, final Geometry geometry) {
        this(data, geometry, null, null);
    }

    /**
     * Creates a new ImmutableGrid object.
     *
     * @param  data      DOCUMENT ME!
     * @param  geometry  DOCUMENT ME!
     * @param  dataType  DOCUMENT ME!
     * @param  unit      DOCUMENT ME!
     */
    public ImmutableGrid(final Double[][] data, final Geometry geometry, final String dataType, final String unit) {
        this.data = (data == null) ? new Double[0][] : data;
        this.geometry = geometry;
        this.dataType = dataType;
        this.unit = unit;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Double[][] getData() {
        return data;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public String getDataType() {
        return dataType;
    }

    @Override
    public String getUnit() {
        return unit;
    }
}
