/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.awt.image.BufferedImage;

/**
 * Make it possible to determine a symbol of a <code>TimeSeries</code> contained in <code>
 * TimeSeriesVisualisation</code>. Use the <code>getLookup()</code> method to find out if the <code>
 * TimeSeriesVisualisation</code> implements this Interface. It is needed to determine a Overlay Icon that is used to
 * show the spatial context of a <code>TimeSeries</code> in cismap. See <code>MultiFeatureInfoRequestDisplay</code> in
 * package de.cismet.cismap.commons.gui.featureinfowidget for further details.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesSignature {

    //~ Methods ----------------------------------------------------------------

    /**
     * determines a Symbol of a time series as visual Signature.
     *
     * @param   timeseries  the time series for that the signature shall be determined
     * @param   heigth      the height of the returned Image
     * @param   width       the width of the returned Image
     *
     * @return  the signature symbol of the time series
     */
    BufferedImage getTimeSeriesSignature(TimeSeries timeseries, int heigth, int width);
}
