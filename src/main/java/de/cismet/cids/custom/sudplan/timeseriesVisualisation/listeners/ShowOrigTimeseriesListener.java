/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

/**
 *
 * @author dmeiers
 */
public interface ShowOrigTimeseriesListener {
    
    public void showOrigTS(TimeSeries ts);
    
}
