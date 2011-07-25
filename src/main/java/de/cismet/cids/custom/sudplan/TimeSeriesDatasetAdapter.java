/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import de.cismet.cismap.commons.interaction.events.MapClickedEvent;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesDatasetAdapter extends TimeSeriesCollection {

    //~ Instance fields --------------------------------------------------------

    private MapClickedEvent mce;
    private Geometry geom;
//    private

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesDatasetAdapter object.
     *
     * @param  g  evt DOCUMENT ME!
     */
// public TimeSeriesDatasetAdapter(final MapClickedEvent evt) {
// mce = evt;
// }

    /**
     * Creates a new TimeSeriesDatasetAdapter object.
     *
     * @param  g  evt DOCUMENT ME!
     */
    public TimeSeriesDatasetAdapter(final Geometry g) {
        geom = g;
    }

    /**
     * Creates a new TimeSeriesDatasetAdapter object.
     *
     * @param  series  DOCUMENT ME!
     */
    public TimeSeriesDatasetAdapter(final TimeSeries series) {
        super(series);
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
}
