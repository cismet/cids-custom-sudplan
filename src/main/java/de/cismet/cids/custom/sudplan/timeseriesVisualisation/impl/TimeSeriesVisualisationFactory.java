/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl;

import org.openide.util.NbBundle;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork.TimeSeriesOperation;

/**
 * Factory for the creation of <code>TimeSeriesVisualisation</code> instances.
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeSeriesVisualisationFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static TimeSeriesVisualisationFactory instance;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesVisualisationFactory object.
     */
    private TimeSeriesVisualisationFactory() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * returns the singleton instance of TimeSeriesVisualisationFactory.
     *
     * @return  DOCUMENT ME!
     */
    public static TimeSeriesVisualisationFactory getInstance() {
        if (instance == null) {
            instance = new TimeSeriesVisualisationFactory();
            return instance;
        } else {
            return instance;
        }
    }

    /**
     * creates a <code>TimeSeriesVisualisation</code> for the specified <code>VisualisationType.</code>
     *
     * @param   t  the type, SIMPLE or GRIDDED
     *
     * @return  a <code>TimeSeriesVisualisation</code> instance
     */
    public TimeSeriesVisualisation createVisualisation(final VisualisationType t) {
        if (t.equals(VisualisationType.SIMPLE)) {
            final SimpleTSVisualisation simpleVis = new SimpleTSVisualisation();
            simpleVis.setProperty(
                TimeSeriesVisualisation.TITLE_KEY,
                NbBundle.getMessage(
                    TimeSeriesVisualisationFactory.class,
                    "TimeSeriesVisualisationFactory.title")); // NOI18N
            // set configuration of the visualisation
            simpleVis.showLegend(true);
            simpleVis.enableContextMenu(true);
            simpleVis.enableToolTips(false);
            simpleVis.enableSelection(true);
            simpleVis.enableZoom(true);

            // add operations to the visualisation
            final TimeSeriesOperation diffOp = new DifferenceOperation(simpleVis);
            simpleVis.addTimeSeriesOperation(diffOp);

            final TimeSeriesOperation normDiffOp = new NormalizedDifferenceOperation(simpleVis);
            simpleVis.addTimeSeriesOperation(normDiffOp);
            return simpleVis;
        } else if (t.equals(VisualisationType.GRIDDED)) {
        }
        return null;
    }
}
