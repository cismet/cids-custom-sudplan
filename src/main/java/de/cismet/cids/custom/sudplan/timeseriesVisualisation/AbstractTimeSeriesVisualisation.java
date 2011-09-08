/*
 * Copyright (C) 2011 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 *
 * @author dmeiers
 */
public abstract class AbstractTimeSeriesVisualisation implements TimeSeriesVisualisation {

    protected final Properties props = new Properties();
    protected final ArrayList<TimeSeriesListChangedListener> tsListeners = new ArrayList<TimeSeriesListChangedListener>();
    protected final ArrayList<PropertyChangeListener> propListener = new ArrayList<PropertyChangeListener>();
    protected final ArrayList<TimeSeriesOperationListChangedListener> operationListeners =
            new ArrayList<TimeSeriesOperationListChangedListener>();

    @Override
    public  void addTimeSeriesListChangeListener(final TimeSeriesListChangedListener l) {
        tsListeners.add(l);
    }

    @Override
    public void removeTimeSeriesListChangeListener(final TimeSeriesListChangedListener l) {
        tsListeners.remove(l);
    }

    
    @Override
    public abstract void addTimeSeries(TimeSeries ts);

    @Override
    public abstract void removeTimeSeries(TimeSeries ts);

    @Override
    public abstract void clearTimeSeries();

    @Override
    public abstract Collection<TimeSeries> getTimeSeriesCollection();

    @Override
    public void setProperty(final String key, final String value) {
        props.put(key, value);
    }

    @Override
    public String getProperty(final String key) {
        return props.getProperty(key);
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        propListener.add(l);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        propListener.remove(l);
    }

    @Override
    public abstract void addTimeSeriesOperation(TimeSeriesOperation op);
    
    @Override
    public abstract void removeTimeSeriesOperation(TimeSeriesOperation op) ;

    @Override
    public abstract void clearTimeSeriesOperations();

    @Override
    public void addTimeSeriesOperationListListener(final TimeSeriesOperationListChangedListener l) {
        operationListeners.add(l);
    }

    @Override
    public void removeTimeSeriesOperationListListener(final TimeSeriesOperationListChangedListener l) {
        operationListeners.remove(l);
    }
    
        /**
     * notifies all registered <code>TimeSeriesOperationListChangedListener <code>about the event.</code></code>
     *
     * @param  evt  the event occurred
     */
    protected void fireTSOperationsChanged(final TimeSeriesOperationChangedEvent evt) {
        for (final TimeSeriesOperationListChangedListener l : operationListeners) {
            l.timeSeriesOperationChanged(evt);
        }
    }

    @Override
    public abstract <T> T getLookup(Class<T> clazz);

    @Override
    public abstract JComponent getVisualisationUI() ;

    @Override
    public abstract JToolBar getToolbar() ;

    /**
     * notifies all <code>TimeSeriesListChangedListener</code> about the event.
     *
     * @param  evt  the event occured
     */
    protected void fireTimeSeriesChanged(final TimeSeriesListChangedEvent evt) {
        for (final TimeSeriesListChangedListener l : tsListeners) {
            l.timeSeriesListChanged(evt);
        }
    }
}
