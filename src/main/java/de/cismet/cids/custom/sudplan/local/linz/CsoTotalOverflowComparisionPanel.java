/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.TableOrder;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.*;

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.client.tools.DevelopmentTools;

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
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  swmmResults  DOCUMENT ME!
     */
    public void setSwmmResults(final Collection<CidsBean> swmmResults) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setSwmmResults: " + swmmResults.size());
        }

        if (swmmResults.isEmpty()) {
            LOG.warn("empty SWMM results list");
            return;
        }

        final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;

        // int i = 0;
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (final CidsBean swmmResultBean : swmmResults) {
            final Object overflowVolume = swmmResultBean.getProperty("overflow_volume");
            final Collection etaResults = (Collection)swmmResultBean.getProperty("eta_results");
            for (final Object etaResult : etaResults) {
                final CidsBean etaResultBean = (CidsBean)etaResult;
                final String etaRunName = (String)etaResultBean.getProperty("name");
                final Object totalOverflowVolume = etaResultBean.getProperty("total_overflow_volume");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("plotting eta results: " + totalOverflowVolume + " / " + overflowVolume);
                }
                dataset.addValue(((totalOverflowVolume != null) ? (Float)totalOverflowVolume : 0),
                    etaRunName,
                    this.totalOverflowVolumeLabel);
                dataset.addValue(((overflowVolume != null) ? (Float)overflowVolume : 0),
                    etaRunName,
                    this.overflowVolumeLabel);
            }
        }

        System.out.println(dataset.getRowCount());
        System.out.println(dataset.getColumnCount());

        final JFreeChart chart = ChartFactory.createMultiplePieChart(
                null,
                dataset,
                TableOrder.BY_ROW,
                true, // legend?
                true, // tooltips?
                false // URLs?
                );

        final MultiplePiePlot plot = (MultiplePiePlot)chart.getPlot();
        plot.setBackgroundAlpha(1.0f);
        // plot.setBackgroundPaint(new Color(228, 228, 197));
        final JFreeChart subchart = plot.getPieChart();

        final PiePlot subChartsPlot = (PiePlot)subchart.getPlot();
        subChartsPlot.setExplodePercent(dataset.getColumnKey(1).toString(), 0.25);
        subChartsPlot.setSectionPaint(dataset.getColumnKey(0).toString(), new Color(142, 91, 62));
        subChartsPlot.setSectionPaint(dataset.getColumnKey(1).toString(), new Color(168, 107, 76));
        subChartsPlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{1}",
                new DecimalFormat("0.0"),
                new DecimalFormat("0.0")));
        subChartsPlot.setBackgroundPaint(new Color(228, 228, 197));

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(255, 255, 255, 0));
        chartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());
    } // </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            BasicConfigurator.configure();
            final CidsBean[] swmmResults = DevelopmentTools.createCidsBeansFromRMIConnectionOnLocalhost(
                    "SUDPLAN",
                    "Administratoren",
                    "admin",
                    "cismetz12",
                    "linz_swmm_result");

//            final ArrayList<CidsBean> etaBeans = new ArrayList<CidsBean>();
//            final ArrayList<CidsBean> swmmBeans = new ArrayList<CidsBean>();
//            final CidsBean swmmBean = new CidsBean();
//            swmmBean.setProperty("overflow_volume", 20000f);
//            final CidsBean etaBean1 = new CidsBean();
//            etaBean1.setProperty("total_overflow_volume", 600000f);
//            etaBeans.add(etaBean1);
//            swmmBean.setProperty("eta_results", etaBeans);
//            swmmBeans.add(swmmBean);

            System.out.println(swmmResults.length);
            final CsoTotalOverflowComparisionPanel csoTotalOverflowComparisionPanel =
                new CsoTotalOverflowComparisionPanel();
            csoTotalOverflowComparisionPanel.setPreferredSize(new java.awt.Dimension(600, 400));
            csoTotalOverflowComparisionPanel.setSwmmResults(Arrays.asList(swmmResults));
            final JFrame frame = new JFrame("EfficiencyRatesComparisionPanel");
            frame.setContentPane(csoTotalOverflowComparisionPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
