/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.event.ActionEvent;

import java.util.HashMap;

import javax.swing.AbstractAction;

/**
 * Action that removes one or multiple time series from a a chart. Deletes the time series only if after the removal
 * still one time series remains. Takes also care of multiple Axis.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class RemoveTimeSeriesAction extends AbstractAction {

    //~ Instance fields --------------------------------------------------------

    private HashMap<Integer, TimeSeriesCollection> tsMap;
    private XYPlot plot;
    private CustomChartPanel chartPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Configures the action to remove one time series.
     *
     * @param  tsCollection  the time series to remove
     * @param  plot          the plot that contains the time series
     * @param  cp            DOCUMENT ME!
     */
    public RemoveTimeSeriesAction(final TimeSeriesCollection tsCollection,
            final XYPlot plot,
            final CustomChartPanel cp) {
        super("Remove");
        this.plot = plot;
        final int index = this.plot.indexOf(tsCollection);
        this.tsMap = new HashMap<Integer, TimeSeriesCollection>();
        this.tsMap.put(new Integer(index), tsCollection);
        chartPanel = cp;
    }

    /**
     * Configures the Action to Remove a Set of TimeSeries from the chart.
     *
     * @param  map   The map of series to remove
     * @param  plot  The Plot which contains the series
     * @param  cp    DOCUMENT ME!
     */
    public RemoveTimeSeriesAction(final HashMap<Integer, TimeSeriesCollection> map,
            final XYPlot plot,
            final CustomChartPanel cp) {
        super("Remove all selected");
        this.tsMap = map;
        this.plot = plot;
        chartPanel = cp;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        final int datasetCount = getNonNullDatasetCount();
        if (datasetCount > tsMap.size()) {
            for (final TimeSeriesCollection tsc : tsMap.values()) {
                removeTimeSeries(tsc);
            }
        } else {
            // TODO LOG
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsc  DOCUMENT ME!
     */
    private void removeTimeSeries(final TimeSeriesCollection tsc) {
        final int rangeAxisCount = plot.getRangeAxisCount();
        final String rangeDesc = tsc.getSeries(0).getRangeDescription();
        final ValueAxis axisToRemove = plot.getRangeAxisForDataset(plot.indexOf(tsc));
        // remove the time series from dataset and set the dataset to null
        final int index = plot.indexOf(tsc);
        plot.setDataset(index, null);
        tsc.removeSeries(0);
        chartPanel.fireTimeSeriesRemoved(index);

        if (rangeAxisCount > 1) {
            // there are multiple axis so look if the axis is still needed
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
                final int axisIndex = plot.getRangeAxisIndex(axisToRemove);
                final ValueAxis primaryAxis = plot.getRangeAxis(0);
                // take care of removing the primary (first) axis
                if (primaryAxis.equals(axisToRemove)) {
                    for (int i = 1; i < plot.getRangeAxisCount(); i++) {
                        final ValueAxis tmpAxis = plot.getRangeAxis(i);
                        if (tmpAxis != null) {
                            plot.setRangeAxis(0, tmpAxis, false);
                            plot.setRangeAxis(i, null, false);
                            plot.mapDatasetToRangeAxis(i, 0);
                            plot.setRangeAxisLocation(0, AxisLocation.TOP_OR_LEFT, false);
                            return;
                        }
                    }
                } else {
                    plot.setRangeAxis(axisIndex, null, true);
                }
                // TODO make Log that axis removing wanst succesful
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
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
