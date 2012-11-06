/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval.Openness;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import se.smhi.sudplan.client.Scenario;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.io.IOException;

import java.net.URL;

import java.text.DateFormat;

import java.util.HashMap;

import javax.swing.JLabel;

import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SudplanOptions;
import de.cismet.cids.custom.sudplan.TimeseriesChartPanel;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.Variable;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

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
    private final transient CidsBean outputBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient de.cismet.cids.custom.sudplan.LoadingLabel lblLoading =
        new de.cismet.cids.custom.sudplan.LoadingLabel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CalibrationOutputManagerUI.
     *
     * @param   manager  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public CalibrationOutputManagerUI(final CalibrationOutputManager manager) throws IOException {
        this.model = manager.getUR();
        this.outputBean = manager.getCidsBean();

        initComponents();

        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        try {
            final HashMap<TimeseriesRetrieverConfig, TimeseriesConverter> configs =
                new HashMap<TimeseriesRetrieverConfig, TimeseriesConverter>(4);

            final CidsBean resultTsBean = model.fetchResultTs();
            final String url = (String)resultTsBean.getProperty("uri"); // NOI18N
            final TimeseriesRetrieverConfig cfg = TimeseriesRetrieverConfig.fromUrl(url);
            configs.put(cfg, SMSUtils.loadConverter(resultTsBean));

            if (model.getInputTs() == null) {
                LOG.warn("there is no input timeseries for output: " + model);  // NOI18N
            } else {
                final CidsBean inputTsBean = model.fetchInputTs();
                final String urlInput = (String)inputTsBean.getProperty("uri"); // NOI18N
                final TimeseriesRetrieverConfig cfgInput = TimeseriesRetrieverConfig.fromUrl(urlInput);
                configs.put(cfgInput, SMSUtils.loadConverter(inputTsBean));
            }

            final Scenario calScenario = HydrologyCache.getInstance().getCalibrationScenario();
            final DateFormat hypeFormat = HydrologyCache.getInstance().getHydroDateFormat();

            final TimeInterval interval = new TimeInterval();
            interval.setLeft(Openness.OPEN);
            interval.setRight(Openness.OPEN);
            interval.setStart(new TimeStamp(hypeFormat.parse(calScenario.getCdate())));
            interval.setEnd(new TimeStamp(hypeFormat.parse(calScenario.getEdate())));

            final CalibrationModelManager cmm = (CalibrationModelManager)SMSUtils.loadModelManagerInstanceFromIO(
                    outputBean);
            final TimeseriesRetrieverConfig cfgUncalibrated = new TimeseriesRetrieverConfig(
                    TimeseriesRetrieverConfig.PROTOCOL_HYPE,
                    null,
                    new URL("http://" + SudplanOptions.getInstance().getHdHypeIp()), // NOI18N
                    "urn:ogc:object:cal_normal:cout:"
                            + Resolution.DAY.getPrecision(), // NOI18N,
                    String.valueOf(cmm.getRunInfo().getBasinId()),
                    Variable.COUT.getPropertyKey(),
                    "normal-cout-day-1981-01-01",
                    null,
                    interval);

            configs.put(cfgUncalibrated, null);

            final Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        final TimeseriesChartPanel tcp = new TimeseriesChartPanel(configs, false, null, false);
                        remove(lblLoading);
                        lblLoading.dispose();
                        setLayout(new BorderLayout());
                        add(tcp, BorderLayout.CENTER);
                    }
                };

            if (EventQueue.isDispatchThread()) {
                r.run();
            } else {
                EventQueue.invokeLater(r);
            }
        } catch (final Exception e) {
            final String message = "cannot create output visualisation"; // NOI18N
            LOG.error(message, e);

            final Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        remove(lblLoading);
                        lblLoading.dispose();
                        setLayout(new BorderLayout());
                        add(new JLabel(message + ": " + e), BorderLayout.CENTER); // NOI18N
                    }
                };

            if (EventQueue.isDispatchThread()) {
                r.run();
            } else {
                EventQueue.invokeLater(r);
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
