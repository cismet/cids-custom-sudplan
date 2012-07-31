/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class Distribution {

    //~ Instance fields --------------------------------------------------------

    private double min;
    private double max;
    private double mean;
    private double scaleCenter;
    private double scaleFactor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Distribution object.
     *
     * @param  min   DOCUMENT ME!
     * @param  max   DOCUMENT ME!
     * @param  mean  DOCUMENT ME!
     */
    public Distribution(final double min, final double max, final double mean) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.scaleCenter = (max - min) / 2;
        this.scaleFactor = 1F;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMax() {
        return max;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMean() {
        return mean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMin() {
        return min;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getScaleCenter() {
        return scaleCenter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  scaleCenter  DOCUMENT ME!
     */
    public void setScaleCenter(final double scaleCenter) {
        this.scaleCenter = scaleCenter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  scaleFactor  DOCUMENT ME!
     */
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Distribution other = (Distribution)obj;
        if (Double.doubleToLongBits(this.min) != Double.doubleToLongBits(other.min)) {
            return false;
        }
        if (Double.doubleToLongBits(this.max) != Double.doubleToLongBits(other.max)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mean) != Double.doubleToLongBits(other.mean)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (13 * hash) + (int)(Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = (13 * hash) + (int)(Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        hash = (13 * hash) + (int)(Double.doubleToLongBits(this.mean) ^ (Double.doubleToLongBits(this.mean) >>> 32));
        return hash;
    }
}
