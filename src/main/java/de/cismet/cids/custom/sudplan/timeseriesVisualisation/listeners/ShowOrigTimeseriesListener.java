/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface ShowOrigTimeseriesListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  ts  DOCUMENT ME!
     */
    void showOrigTS(TimeSeries ts);
}
