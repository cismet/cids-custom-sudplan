/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

import java.util.Properties;

/**
 * Defines a set of propeties for a <code>TimeSeriesVisualisation</code>.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesChartProperty extends Properties {

    //~ Static fields/initializers ---------------------------------------------

    /** Property Key for the title of the <code>TimeSeriesViualisation</code>. */
    public static final String TITLE_KEY = "title";
    /** Property Key for the x axis title of the <code>TimeSeriesVisualisation.</code> */
    public static final String X_AXIS_TITLE_KEY = "xAxisTitle";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesChartProperty object.
     */
    public TimeSeriesChartProperty() {
        this.put(TITLE_KEY, "");
        this.put(X_AXIS_TITLE_KEY, "");
    }
}
