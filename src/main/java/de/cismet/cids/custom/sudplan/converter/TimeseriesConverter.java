/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.util.TimeZone;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface TimeseriesConverter extends InputStreamConverter<TimeSeries> {

    //~ Static fields/initializers ---------------------------------------------

    TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
}
