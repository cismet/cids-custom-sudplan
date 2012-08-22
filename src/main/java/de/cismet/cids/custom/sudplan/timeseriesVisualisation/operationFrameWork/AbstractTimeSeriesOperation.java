/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork;

import Sirius.navigator.ui.ComponentRegistry;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.openide.util.Exceptions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * A <code>AbstractTimeSeriesOperation</code> represents the default implementation for <code>TimeSeriesOperation</code>
 * Create a subclass of it to implement your own TimeSeriesOperation and add the operation code into the method <code>
 * calculate()</code>. The AbstractTimeSeriesOperation class takes already care of handling a list of available
 * TimeSeries, storing and notifying <code>TimeSeriesOperatinResultListener <code>after the operation was executed.It
 * uses an <code>DefaultParamOrderUI</code> to determine the parameters. The execution of the Operation is done in
 * SwingWorker thread.</code></code>
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public abstract class AbstractTimeSeriesOperation extends AbstractAction implements TimeSeriesOperation {

    //~ Instance fields --------------------------------------------------------

    /** represents the Number of needed parameters to execute this operation. */
    protected int paramCount;
    /** represents a Collection of all available time series. */
    protected Collection<TimeSeries> tsList;
    private ArrayList<TimeSeriesOperationResultListener> resultListeners =
        new ArrayList<TimeSeriesOperationResultListener>();
    private TimeSeries[] params;
    private TimeSeriesVisualisation tsVis;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractTimeSeriesOperation object.
     *
     * @param  paramCount  DOCUMENT ME!
     * @param  name        the title of the <code>TimeSeriesOperation</code>
     * @param  icon        the icon of the <code>TimeSeriesOperation</code>
     * @param  tsv         the <code>TimeSeriesVisualisation</code> this is used to determine the parent frame of the
     *                     <code>DefaultParamOderUI</code>
     */
    public AbstractTimeSeriesOperation(final int paramCount,
            final String name,
            final Icon icon,
            final TimeSeriesVisualisation tsv) {
        super(name, icon);
        params = new TimeSeries[paramCount];
        tsVis = tsv;
        this.paramCount = paramCount;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addTimeSeriesOperationResultListener(final TimeSeriesOperationResultListener l) {
        resultListeners.add(l);
    }

    @Override
    public void removeTimeSeriesOperationResultListener(final TimeSeriesOperationResultListener l) {
        resultListeners.remove(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    private void fireTimeSeriesOperationResult(final Collection<TimeSeries> c) {
        for (final TimeSeriesOperationResultListener l : resultListeners) {
            l.submitResult(c);
        }
    }

    @Override
    public void setavailableTimeSeriesList(final Collection<TimeSeries> c) {
        if (c.size() >= paramCount) {
            // TODO check if in the list are enough timeseries of the same unit...
            final HashMap<String, Collection<TimeSeries>> unitCountMap = new HashMap<String, Collection<TimeSeries>>();
            for (final TimeSeries ts : c) {
                final String unit = SMSUtils.unitFromTimeseries(ts).getLocalisedName();
                if (unit != null) {
                    if (unitCountMap.containsKey(unit)) {
                        final Collection<TimeSeries> candidates = unitCountMap.get(unit);
                        candidates.add(ts);
                        unitCountMap.put(unit, candidates);
                    } else {
                        final Collection<TimeSeries> candidates = new ArrayList<TimeSeries>();
                        candidates.add(ts);
                        unitCountMap.put(unit, candidates);
                    }
                }
            }
            // TODO just create a own list with canididate time series
            final ArrayList<TimeSeries> candidateTimeSeries = new ArrayList<TimeSeries>();
            for (final String unit : unitCountMap.keySet()) {
                if (unitCountMap.get(unit).size() >= paramCount) {
                    final Collection<TimeSeries> tss = unitCountMap.get(unit);
                    candidateTimeSeries.addAll(tss);
                }
            }
            if (candidateTimeSeries.size() >= paramCount) {
                tsList = candidateTimeSeries;
                this.setEnabled(true);
            }
            return;
        }
        this.setEnabled(false);
    }

    @Override
    public void setParameters(final TimeSeries[] orderedParams) {
        params = orderedParams;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        JButton btn = null;
        if (e.getSource() instanceof JButton) {
            btn = (JButton)e.getSource();
        }
        final Frame f = ComponentRegistry.getRegistry().getMainWindow();
        final DefaultParamOrderUI dialog = new DefaultParamOrderUI(f, true, paramCount, tsList, this, tsVis);
        dialog.pack();
        StaticSwingTools.showDialog(dialog);
        
        if (dialog.getReturnStatus() == (DefaultParamOrderUI.RET_OK)) {
            final OperationCalculator calculator = new OperationCalculator();
            calculator.execute();
        }
    }

    /**
     * This method is called to determine the result of the operation calculation.
     *
     * @param   params  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract Collection<TimeSeries> calculate(TimeSeries[] params);

    //~ Inner Classes ----------------------------------------------------------

    /**
     * SwingWorker that executes the <code>calculate() <code>method in a seperate Thread.</code></code>
     *
     * @version  $Revision$, $Date$
     */
    private final class OperationCalculator extends SwingWorker<Collection<TimeSeries>, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new OperationCalculator object.
         */
        public OperationCalculator() {
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Collection<TimeSeries> doInBackground() throws Exception {
            return calculate(params);
        }

        @Override
        protected void done() {
            try {
                final Collection<TimeSeries> result = get();
                fireTimeSeriesOperationResult(result);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
