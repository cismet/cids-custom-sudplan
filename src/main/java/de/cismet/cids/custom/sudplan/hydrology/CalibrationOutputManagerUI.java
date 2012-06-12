/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.util.concurrent.Future;

import javax.swing.JLabel;

import de.cismet.cids.custom.sudplan.Resolution;
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
public class CalibrationOutputManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CalibrationOutputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient CalibrationOutput model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient de.cismet.cids.custom.sudplan.LoadingLabel lblLoading =
        new de.cismet.cids.custom.sudplan.LoadingLabel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CalibrationOutputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public CalibrationOutputManagerUI(final CalibrationOutput model) {
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

                        final CidsBean resultTsBean = model.fetchResultTs();
                        final String url = (String)resultTsBean.getProperty("uri"); // NOI18N
                        final TimeseriesRetrieverConfig cfg = TimeseriesRetrieverConfig.fromUrl(url)
                                    .changeResolution(Resolution.DAY);
                        final Future<TimeSeries> tsFuture = TimeseriesRetriever.getInstance().retrieve(cfg);
                        final TimeSeries resultTs = tsFuture.get();

                        vis.addTimeSeries(resultTs);

                        if (model.getInputTs() == null) {
                            LOG.warn("there is no input timeseries for output: " + model);  // NOI18N
                        } else {
                            final CidsBean inputTsBean = model.fetchInputTs();
                            final String urlInput = (String)inputTsBean.getProperty("uri"); // NOI18N
                            final TimeseriesRetrieverConfig cfgInput = TimeseriesRetrieverConfig.fromUrl(urlInput);
                            final Future<TimeSeries> tsFutInput = TimeseriesRetriever.getInstance().retrieve(cfgInput);
                            final TimeSeries inputTs = tsFutInput.get();

                            vis.addTimeSeries(inputTs);
                        }

                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    CalibrationOutputManagerUI.this.remove(lblLoading);
                                    lblLoading.dispose();
                                    CalibrationOutputManagerUI.this.setLayout(new BorderLayout());
                                    CalibrationOutputManagerUI.this.add(vis.getVisualisationUI(), BorderLayout.CENTER);
                                    CalibrationOutputManagerUI.this.invalidate();
                                    CalibrationOutputManagerUI.this.validate();
                                }
                            });
                    } catch (final Exception ex) {
                        final String message = "cannot create output visualisation"; // NOI18N
                        LOG.error(message, ex);

                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    CalibrationOutputManagerUI.this.remove(lblLoading);
                                    lblLoading.dispose();
                                    CalibrationOutputManagerUI.this.setLayout(new BorderLayout());
                                    CalibrationOutputManagerUI.this.add(
                                        new JLabel(message + ": " + ex), // NOI18N
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

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblLoading.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
