/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

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
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class RemoveTimeSeriesAction extends AbstractAction {

    //~ Instance fields --------------------------------------------------------

    private HashMap<Integer, TimeSeriesCollection> tsMap;
    private XYPlot plot;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RemoveTimeSeriesAction object.
     *
     * @param  tsCollection  DOCUMENT ME!
     * @param  plot          DOCUMENT ME!
     */
    public RemoveTimeSeriesAction(final TimeSeriesCollection tsCollection, final XYPlot plot) {
        super("Remove");
        this.plot = plot;
        final int index = this.plot.indexOf(tsCollection);
        this.tsMap = new HashMap<Integer, TimeSeriesCollection>();
        this.tsMap.put(new Integer(index), tsCollection);
    }

    /**
     * Creates a new RemoveTimeSeriesAction object.
     *
     * @param  map   DOCUMENT ME!
     * @param  plot  DOCUMENT ME!
     */
    public RemoveTimeSeriesAction(final HashMap<Integer, TimeSeriesCollection> map, final XYPlot plot) {
        super("Remove all selected");
        this.tsMap = map;
        this.plot = plot;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        for (final TimeSeriesCollection tsc : tsMap.values()) {
            removeTimeSeries(tsc);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsc  DOCUMENT ME!
     */
    public void removeTimeSeries(final TimeSeriesCollection tsc) {
        final int rangeAxisCount = plot.getRangeAxisCount();
        final String rangeDesc = tsc.getSeries(0).getRangeDescription();
        final ValueAxis axisToRemove = plot.getRangeAxisForDataset(plot.indexOf(tsc));
        // remove the time series from dataset and set the dataset to null
        plot.setDataset(plot.indexOf(tsc), null);
        tsc.removeSeries(0);

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
                final int index = plot.getRangeAxisIndex(axisToRemove);
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
                    plot.setRangeAxis(index, null, true);
                }
                // TODO make Log that axis removing wanst succesful
            }
        }
    }
}
