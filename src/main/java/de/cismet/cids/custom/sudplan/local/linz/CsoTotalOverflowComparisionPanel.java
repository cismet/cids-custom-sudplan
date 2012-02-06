/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import java.util.Collection;

import javax.swing.JPanel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class CsoTotalOverflowComparisionPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(CsoTotalOverflowComparisionPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final String overflowVolumeLabel = NbBundle.getMessage(
            CsoTotalOverflowComparisionPanel.class,
            "CsoTotalOverflowComparisionPanel.chart.overflowVolume");
    private final String totalOverflowVolumeLabel = NbBundle.getMessage(
            CsoTotalOverflowComparisionPanel.class,
            "CsoTotalOverflowComparisionPanel.chart.totalOverflowVolume");

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EfficiencyRatesComparisionPanel.
     */
    public CsoTotalOverflowComparisionPanel() {
        initComponents();

        NbBundle.getMessage(
            CsoTotalOverflowComparisionPanel.class,
            "CsoTotalOverflowComparisionPanel.chart.overflowVolume");
        NbBundle.getMessage(
            CsoTotalOverflowComparisionPanel.class,
            "CsoTotalOverflowComparisionPanel.chart.totalOverflowVolume");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   etaScenarioName      DOCUMENT ME!
     * @param   overflowVolume       DOCUMENT ME!
     * @param   totalOverflowVolume  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected JFreeChart getTotalOverflowChart(final String etaScenarioName,
            final float overflowVolume,
            final float totalOverflowVolume) {
        // create a dataset...
        final DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(this.totalOverflowVolumeLabel, totalOverflowVolume);
        dataset.setValue(this.overflowVolumeLabel, overflowVolume);

        final JFreeChart chart = ChartFactory.createPieChart(
                etaScenarioName,
                dataset,
                false, // legend?
                true, // tooltips?
                false // URLs?
                );

        return chart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmResults  DOCUMENT ME!
     */
    public void setSwmmResults(final Collection<CidsBean> swmmResults) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setSwmmResults: " + swmmResults.size());
        }

        final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (final CidsBean swmmResultBean : swmmResults) {
            final Object overflowVolume = swmmResultBean.getProperty("overflow_volume");
            final Collection etaResults = (Collection)swmmResultBean.getProperty("eta_results");
            for (final Object etaResult : etaResults) {
                final CidsBean etaResultBean = (CidsBean)etaResult;
                final String name = (String)etaResultBean.getProperty("name");
                final Object totalOverflowVolume = etaResultBean.getProperty("total_overflow_volume");

                final JFreeChart totalOverflowChart = this.getTotalOverflowChart(
                        name,
                        ((overflowVolume != null) ? (Float)overflowVolume : 0),
                        ((totalOverflowVolume != null) ? (Float)totalOverflowVolume : 0));

                final ChartPanel chartPanel = new ChartPanel(totalOverflowChart);
                chartPanel.setPreferredSize(new Dimension(200, 150));
                chartPanel.setMaximumDrawHeight(150);
                chartPanel.setMaximumDrawWidth(200);
                this.add(chartPanel, gridBagConstraints);
                gridBagConstraints.gridx++;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setMinimumSize(new java.awt.Dimension(150, 150));
        setLayout(new java.awt.GridBagLayout());
    } // </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
