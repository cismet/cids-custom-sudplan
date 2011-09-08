/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.beans.PropertyChangeListener;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.listeners.TimeSeriesOperationListChangedListener;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

/**
 * Central class of the TimeSeriesVisualiation Framework. This Interface can be seen as data model and controller of a
 * TimeSeriesVisualisation.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface TimeSeriesVisualisation { 

    //~ Instance fields --------------------------------------------------------

    /** Property key for the title of the visualisation*/
    public static final String TITLE_KEY = "VisualisationTitle";
    /** Property key for the x axis title of the visualisation*/
    public static final String X_AXIS_TITLE = "TimeAxisTitle";
    //~ Methods ----------------------------------------------------------------

    /**
     * adds a <code>TimeSeries</code> object to the visualisation. Notifies all registered TimeSeriesListChangedListeners
     *
     * @param  ts  a <code>TimeSeries</code> object
     */
    void addTimeSeries(TimeSeries ts);

    /**
     * removes a <code>TimeSeries</code> object from the visualisation. Notifies all registered
     * TimeSeriesListChangedListeners
     *
     * @param  ts  a time series
     */
    void removeTimeSeries(TimeSeries ts);

    /**
     * removes all <code>TimeSeries</code> objects from the visualisation. Notifies all registered
     * TimeSeriesListChangedListeners
     */
    void clearTimeSeries();

    /**
     * get method for the set of stored <code>TimeSeriesObjects.</code>
     *
     * @return  a Collection that contains all stored <code>TimeSeries</code> objects
     */
    Collection<TimeSeries> getTimeSeriesCollection();

    /**
     * adds a <code>TimeSeriesListChangeListener</code> to the visualisation.
     *
     * @param  l  the <code>TimeSeriesListChangeListener</code>
     */
    void addTimeSeriesListChangeListener(TimeSeriesListChangedListener l);

    /**
     * removes a <code>TimeSeriesListChangeListener</code> to the visualisation.
     *
     * @param  l  <code>TimeSeriesListChangeListener</code>
     */
    void removeTimeSeriesListChangeListener(TimeSeriesListChangedListener l);

    /**
     * sets the value for a property.
     *
     * @param  key    the property key
     * @param  value  the property value
     */
    void setProperty(final String key, final String value);

    /**
     * returns the value of a Property represented by <code>key.</code>
     *
     * @param   key  the property key
     *
     * @return  the property value
     */
    String getProperty(final String key);

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Adds a new <code>TimeSeriesOperation</code> object to the visualisation. Notifies all registered <code>
     * TimeSeriesOperationsListChangedListeners</code>
     *
     * @param  op  the <code>TimeSeriesOperation</code> to add
     */
    void addTimeSeriesOperation(TimeSeriesOperation op);

    /**
     * Removes a new <code>TimeSeriesOperation</code> object from the visualisation. Notifies all registered <code>
     * TimeSeriesOperationsListChangedListeners</code>
     *
     * @param  op  the <code>TimeSeriesOperation</code> to remove
     */
    void removeTimeSeriesOperation(TimeSeriesOperation op);

    /**
     * Removes all <code>TimeSeriesOperation</code> objetcs from the visualisation. Notifies all registered <code>
     * TimeSeriesOperationsListChangedListeners</code>
     */
    void clearTimeSeriesOperations();

    /**
     * registers a <code>TimeSeriesOperationListListener</code> to the visualisation.
     *
     * @param  l  the <code>TimeSeriesOperationListListener</code> to add
     */
    void addTimeSeriesOperationListListener(TimeSeriesOperationListChangedListener l);

    /**
     * removes a registered <code>TimeSeriesOperationListListener</code> from the visualisation.
     *
     * @param  l  the <code>TimeSeriesOperationListListener</code> to remove
     */
    void removeTimeSeriesOperationListListener(TimeSeriesOperationListChangedListener l);

    /**
     * Lookup method to determine additional implemented Interfaces of a <code>TimeSeriesVisualisation</code> object.
     *
     * @param   <T>    The interface
     * @param   clazz  class object of the Interface
     *
     * @return  an Object of
     *          <object>
     *            <param>class<T>
     *          </object>
     */
    <T> T getLookup(Class<T> clazz);

    /**
     * each TimeSeriesVisualisation must define a UI component. This component represents the View of the visualisation
     *
     * @return  the visualisation UI
     */
    JComponent getVisualisationUI();

    /**
     * returns <code>JToolbar</code> for the visualisation.
     *
     * @return  the visualisations tool bar null if the visualisation does not define a tool bar
     */
    JToolBar getToolbar();
}
