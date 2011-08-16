/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import com.vividsolutions.jts.geom.Geometry;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * This Class extends TimeSeriesCollection about the geometry. The geometry is needed to show the spatial context of the
 * time series on map.
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
     * @param  series  DOCUMENT ME!
     */
    public TimeSeriesDatasetAdapter(final TimeSeries series) {
        super(series);
    }

    /**
     * Creates a new TimeSeriesDatasetAdapter object.
     *
     * @param  g         evt DOCUMENT ME!
     * @param  originTS  DOCUMENT ME!
     */
// public TimeSeriesDatasetAdapter(final MapClickedEvent evt) {
// mce = evt;
// }

    /**
     * Creates a new TimeSeriesDatasetAdapter object.
     *
     * @param  g         evt DOCUMENT ME!
     * @param  originTS  DOCUMENT ME!
     */
    public TimeSeriesDatasetAdapter(final Geometry g, final at.ac.ait.enviro.tsapi.timeseries.TimeSeries originTS) {
        geom = g;
        originTimeSeries = originTS;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  g  DOCUMENT ME!
     */
// public MapClickedEvent getMapClickedEvent() {
// return mce;
// }

    /**
     * DOCUMENT ME!
     *
     * @param  g  mce DOCUMENT ME!
     */
// public void setMapClickedEvent(final MapClickedEvent mce) {
// this.mce = mce;
// }

    public void setGeometry(final Geometry g) {
        this.geom = g;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getGeometry() {
        return this.geom;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public at.ac.ait.enviro.tsapi.timeseries.TimeSeries getOriginTimeSeries() {
        return originTimeSeries;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  originTimeSeries  DOCUMENT ME!
     */
    public void setOriginTimeSeries(final at.ac.ait.enviro.tsapi.timeseries.TimeSeries originTimeSeries) {
        this.originTimeSeries = originTimeSeries;
    }
}
