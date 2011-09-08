/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import com.vividsolutions.jts.geom.Geometry;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Adapter class for JFreeChart and the TS-API. This Dataset can be used as a JFreeChart Dataset and contains additional
 * information like the <code>TimeSeries <code>object that it relies to as also the geometry of it</code></code> A
 * TimeSeriesAdapterDataset ever contains only one TimeSeries
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesDatasetAdapter extends TimeSeriesCollection {

    //~ Instance fields --------------------------------------------------------

// private
    private Geometry geom;
    private at.ac.ait.enviro.tsapi.timeseries.TimeSeries originTimeSeries;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesDatasetAdapter object.
     *
     * @param  series  a JFreeChart TimeSeries objects
     */
    public TimeSeriesDatasetAdapter(final TimeSeries series) {
        super(series);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * sets the geometry for this TimeSeries.
     *
     * @param  g  the Geometry
     */
    public void setGeometry(final Geometry g) {
        this.geom = g;
    }

    /**
     * get method for the geometry.
     *
     * @return  the Geometry of this object
     */
    public Geometry getGeometry() {
        return this.geom;
    }

    /**
     * get the <code>TimeSeries</code> object this adapter relies to.
     *
     * @return  the origin <code>TimeSeries</code>
     */
    public at.ac.ait.enviro.tsapi.timeseries.TimeSeries getOriginTimeSeries() {
        return originTimeSeries;
    }

    /**
     * sets the <code>TimeSeries</code> object this adapter relies to.
     *
     * @param  originTimeSeries  the origin <code>TimeSeries</code>
     */
    public void setOriginTimeSeries(final at.ac.ait.enviro.tsapi.timeseries.TimeSeries originTimeSeries) {
        this.originTimeSeries = originTimeSeries;
    }
}
