/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import org.apache.log4j.Logger;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;

import java.util.HashMap;

import javax.swing.AbstractAction;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation; 

/**
 * Removes one or multiple time series from a a chart. Deletes the time series only if after the removal still one time
 * series remains. Deletes Axes that are no longer necessary. Is used in <code>TimeSeriesChartToolbar</code> and the
 * customised context menu in <code>CustomChartPanel</code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class RemoveTimeSeriesAction extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RemoveTimeSeriesAction.class);

    //~ Instance fields --------------------------------------------------------

    private HashMap<Integer, TimeSeriesDatasetAdapter> tsMap;
    private XYPlot plot;
    private TimeSeriesVisualisation tsVis;

    //~ Constructors -----------------------------------------------------------

    /**
     * Configures this action to remove one time series.
     *
     * @param  tsCollection  the time series to remove
     * @param  plot          the <code>XYPlot</code> that contains the chart where the datasets are drawn
     * @param  tsVis         the <code>TimeSeriesVisualisation</code> this action relies to
     */
    public RemoveTimeSeriesAction(final TimeSeriesDatasetAdapter tsCollection,
            final XYPlot plot,
            final TimeSeriesVisualisation tsVis) {
        super(NbBundle.getMessage(
                RemoveTimeSeriesAction.class,
                "RemoveTimeSeriesAction.singleRemove.name")); // NOI18N
        this.tsVis = tsVis;
        this.plot = plot;
        final int index = this.plot.indexOf(tsCollection);
        this.tsMap = new HashMap<Integer, TimeSeriesDatasetAdapter>();
        this.tsMap.put(new Integer(index), tsCollection);
    }

    /**
     * Configures this Action to remove a set of TimeSeries from the chart.
     *
     * @param  map    the map of series to remove
     * @param  plot   the plot that contains the chart where the datasets are drawn
     * @param  tsVis  the <code>TimeSeriesVisualisation</code> this action relies to
     */
    public RemoveTimeSeriesAction(final HashMap<Integer, TimeSeriesDatasetAdapter> map,
            final XYPlot plot,
            final TimeSeriesVisualisation tsVis) {
        super(NbBundle.getMessage(
                TimeSeriesChartToolBar.class,
                "RemoveTimeSeriesAction.multiRemove.name")); // NOI18N
        this.tsVis = tsVis;
        this.tsMap = map;
        this.plot = plot;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * performs the removal of the all <code>TimeSeriesDatasetAdapter</code> that this Action is configured for.
     *
     * @param  e  the <code>ActioEvent</code>
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final int datasetCount = getNonNullDatasetCount();
        if (datasetCount > tsMap.size()) {
            for (final TimeSeriesDatasetAdapter tsc : tsMap.values()) {
                removeTimeSeries(tsc);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("To many Dataset to remove, removal would cause in an empty chart. Aborting"); // NOI18N
            }
        }
    }

    /**
     * deletes the <code>TimeSeriesDatasetAdapter</code> dataset from chart so that it is no longer displayed. deletes
     * also the corresponding <code>TimeSeries</code> from the <code>TimeSeriesVisualisation</code>
     *
     * @param  tsc  <code>TimeSeriesDatasetAdapter</code> to remove
     */
    private void removeTimeSeries(final TimeSeriesDatasetAdapter tsc) {
        final int rangeAxisCount = plot.getRangeAxisCount();
        final String rangeDesc = tsc.getSeries(0).getRangeDescription();
        final ValueAxis axisToRemove = plot.getRangeAxisForDataset(plot.indexOf(tsc));
        // remove the time series from visualisation
        tsVis.removeTimeSeries(tsc.getOriginTimeSeries());

        if (rangeAxisCount > 1) {
            // there are multiple axis so look if the axis is still needed
            if (LOG.isDebugEnabled()) {
                LOG.debug("Chart has multiple Y-Axes. Checking if the are all needed"); // NOI18N
            }
            boolean axisNeeded = false;
            for (int i = 0; i < plot.getDatasetCount(); i++) {
                final TimeSeriesCollection tmpTsc = (TimeSeriesCollection)plot.getDataset(i);
                if (tmpTsc == null) {
                    continue;
                }
                final TimeSeries ts = tmpTsc.getSeries(0);
                if (ts.getRangeDescription().equals(rangeDesc)) {
                    axisNeeded = true;
                    break;
                }
            }

            if (!axisNeeded) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Checking done - Axis no longer nedded. removing it"); // NOI18N
                }
                final int axisIndex = plot.getRangeAxisIndex(axisToRemove);
                final ValueAxis primaryAxis = plot.getRangeAxis(0);
                // take care of removing the primary (first) axis
                if (primaryAxis.equals(axisToRemove)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Y-Axis that shall be deleted is the primary axis. Replacing the primary axis"); // NOI18N
                    }
                    for (int i = 1; i < plot.getRangeAxisCount(); i++) {
                        final ValueAxis tmpAxis = plot.getRangeAxis(i);
                        if (tmpAxis != null) {
                            plot.setRangeAxis(0, tmpAxis, false);
                            plot.setRangeAxis(i, null, false);
                            for (int j = 0; j < plot.getDatasetCount(); j++) {
                                final ValueAxis axis = plot.getRangeAxisForDataset(j);
                                if ((axis != null) && axis.equals(tmpAxis)) {
                                    plot.mapDatasetToRangeAxis(j, 0);
                                }
                            }
                            plot.setRangeAxisLocation(0, AxisLocation.TOP_OR_LEFT, false);
                            return;
                        }
                    }
                } else {
                    plot.setRangeAxis(axisIndex, null, true);
                }
                // TODO make Log that axis removing wanst succesful
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Checking done, Axis still needed");                                                 // NOI18N
                }
            }
        }
    }

    /**
     * returns the number of existing datasets that are displayed in the <code>XYPlot</code>. This method is needed in
     * fact that the removal of datasets leads to null values in the set of displayed datasets
     *
     * @return  the dataset count
     */
    private int getNonNullDatasetCount() {
        int result = 0;
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            if (plot.getDataset(i) != null) {
                result++;
            }
        }
        return result;
    }
}
