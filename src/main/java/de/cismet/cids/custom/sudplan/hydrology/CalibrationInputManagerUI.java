/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;

import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.swing.JLabel;

import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.TimeSeriesVisualisationFactory;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.impl.VisualisationType;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class CalibrationInputManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CalibrationInputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient CalibrationInput model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.sudplan.LoadingLabel lblLoading;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CalibrationInputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public CalibrationInputManagerUI(final CalibrationInput model) {
        this.model = model;

        initComponents();

        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        final TimeSeriesVisualisation vis = TimeSeriesVisualisationFactory.getInstance()
                                    .createVisualisation(VisualisationType.SIMPLE);

                        for (final Entry<Integer, Integer> entry : model.getTimeseries()) {
                            final Integer basinId = entry.getKey();
                            final Integer tsId = entry.getValue();

                            final CidsBean tsBean = SMSUtils.fetchCidsBean(tsId, SMSUtils.TABLENAME_TIMESERIES);
                            final String url = (String)tsBean.getProperty("uri"); // NOI18N
                            final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(url)
                                        .changeResolution(Resolution.DAY);
                            final Future<TimeSeries> tsFuture = TimeseriesRetriever.getInstance().retrieve(config);
                            final TimeSeries ts = tsFuture.get();
                            ts.setTSProperty(
                                PropertyNames.DESCRIPTION,
                                "Basin "                                          // NOI18N
                                        + basinId
                                        + ": "                                    // NOI18N
                                        + ts.getTSProperty(PropertyNames.DESCRIPTION));

                            vis.addTimeSeries(ts);
                        }

                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    CalibrationInputManagerUI.this.remove(lblLoading);
                                    lblLoading.dispose();
                                    CalibrationInputManagerUI.this.setLayout(new BorderLayout());
                                    CalibrationInputManagerUI.this.add(vis.getVisualisationUI(), BorderLayout.CENTER);

                                    Container parent = CalibrationInputManagerUI.this;
                                    Container current = getParent();
                                    while (current != null) {
                                        parent = current;
                                        current = parent.getParent();
                                    }
                                    parent.invalidate();
                                    parent.validate();
                                }
                            });
                    } catch (final Exception e) {
                        final String message = "cannot create input ui"; // NOI18N
                        LOG.error(message, e);

                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    CalibrationInputManagerUI.this.remove(lblLoading);
                                    lblLoading.dispose();
                                    CalibrationInputManagerUI.this.setLayout(new BorderLayout());
                                    CalibrationInputManagerUI.this.add(
                                        new JLabel(message + ": " + e), // NOI18N
                                        BorderLayout.CENTER);
                                }
                            });
                    }
                }
            };

        SudplanConcurrency.getSudplanGeneralPurposePool().execute(r);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        lblLoading = new de.cismet.cids.custom.sudplan.LoadingLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
        add(lblLoading, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents
}
