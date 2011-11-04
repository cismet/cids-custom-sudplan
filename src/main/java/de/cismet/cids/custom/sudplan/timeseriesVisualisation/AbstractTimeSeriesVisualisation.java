/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationChangedEvent;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public abstract class AbstractTimeSeriesVisualisation implements TimeSeriesVisualisation {

    //~ Instance fields --------------------------------------------------------

    protected final Properties props = new Properties();
    protected final ArrayList<TimeSeriesListChangedListener> tsListeners =
        new ArrayList<TimeSeriesListChangedListener>();
    protected final ArrayList<PropertyChangeListener> propListener = new ArrayList<PropertyChangeListener>();
    protected final ArrayList<TimeSeriesOperationListChangedListener> operationListeners =
        new ArrayList<TimeSeriesOperationListChangedListener>();

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addTimeSeriesListChangeListener(final TimeSeriesListChangedListener l) {
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
    public abstract void removeTimeSeriesOperation(TimeSeriesOperation op);
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
        final Iterator<TimeSeriesOperationListChangedListener> it;
        synchronized (tsListeners) {
            it = new ArrayList<TimeSeriesOperationListChangedListener>(operationListeners).iterator();
        }

        while (it.hasNext()) {
            final TimeSeriesOperationListChangedListener listener = it.next();
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        listener.timeSeriesOperationChanged(evt);
                    }
                });
        }
    }

    @Override
    public abstract <T> T getLookup(Class<T> clazz);
    @Override
    public abstract JComponent getVisualisationUI();
    @Override
    public abstract JToolBar getToolbar();

    /**
     * notifies all <code>TimeSeriesListChangedListener</code> about the event.
     *
     * @param  evt  the event occurred
     */
    protected void fireTimeSeriesChanged(final TimeSeriesListChangedEvent evt) {
        final Iterator<TimeSeriesListChangedListener> it;
        synchronized (tsListeners) {
            it = new ArrayList<TimeSeriesListChangedListener>(tsListeners).iterator();
        }

        while (it.hasNext()) {
            final TimeSeriesListChangedListener listener = it.next();
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        listener.timeSeriesListChanged(evt);
                    }
                });
        }
    }

    @Override
    public abstract BufferedImage getImage();
}
