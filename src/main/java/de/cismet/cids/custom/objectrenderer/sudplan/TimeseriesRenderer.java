/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.ui.ComponentRegistry;

import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.MalformedURLException;

import java.text.MessageFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesChartPanel;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class TimeseriesRenderer extends AbstractCidsBeanRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private transient TimeseriesChartPanel panel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkforecast;
    private javax.swing.JPanel pnlFiller;
    private javax.swing.JPanel pnlNorth;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TimeseriesRenderer.
     */
    public TimeseriesRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   resolution  DOCUMENT ME!
     *
     * @throws  RuntimeException  MalformedURLException DOCUMENT ME!
     */
    private void setTimeSeriesPanel(final Resolution resolution) {
        try {
            final String uri = (String)cidsBean.getProperty("uri"); // NOI18N
            final TimeseriesConverter converter = SMSUtils.loadConverter(cidsBean);

            TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(uri);
            if (resolution != null) {
                config = config.changeResolution(resolution);
            }

            if (this.panel != null) {
                super.remove(this.panel);
            }

            this.panel = new TimeseriesChartPanel(config, converter);
            add(this.panel, BorderLayout.CENTER);
        } catch (final IllegalStateException e) {
            if (resolution == null) {
                LOG.error("An error occured while retrieving original TimeSeries", e); // NOI18N
                throw e;
            } else {
                // most likely, there is no TimeSeries with the specified resolution
                LOG.warn("An error occured while retrieving TimeSeries with resolution " + resolution, e); // NOI18N
                final int answer = JOptionPane.showConfirmDialog(
                        ComponentRegistry.getRegistry().getMainWindow(),
                        MessageFormat.format(
                            java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle")
                                        .getString(
                                            "TimeseriesRenderer.setTimeSeriesPanel(Resolution).JOptionPane.message"),
                            resolution.getLocalisedName()),
                        java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle")
                                    .getString("TimeseriesRenderer.setTimeSeriesPanel(Resolution).JOptionPane.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                // WARNING
                // may crash system

                if (answer == JOptionPane.YES_OPTION) {
                    this.setTimeSeriesPanel(null);
                }
            }
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void init() {
        bindingGroup.unbind();
        bindingGroup.bind();

        try {
            final String uri = (String)cidsBean.getProperty("uri"); // NOI18N final TimeseriesConverter converter =
                                                                    // SMSUtils.loadConverter(cidsBean);

            // ----
            final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(uri);

            if (config.getProtocol().equals(TimeseriesRetrieverConfig.PROTOCOL_DAV)) {
                // config = config.changeResolution(Resolution.DAY);
                this.setTimeSeriesPanel(Resolution.DAY);
            } else {
                final String procedure = config.getProcedure();

                final Pattern p = Pattern.compile("prec:(\\d+[YMs])"); // NOI18N
                final Matcher m = p.matcher(procedure);

                Resolution resolution = Resolution.DAY;
                if (m.matches()) {
                    final String precision = m.group(1);
                    if (!precision.equals(Resolution.DAY.getPrecision())) {
                        if (precision.equals(Resolution.MONTH.getPrecision())) {
                            resolution = Resolution.MONTH;
                        } else if (precision.equals(Resolution.YEAR.getPrecision())) {
                            resolution = Resolution.YEAR;
                        } else if (precision.equals(Resolution.DECADE.getPrecision())) {
                            resolution = Resolution.DECADE;
                        } else {
                            LOG.warn("Unknown resolution " + precision + ". Using default resolution " + resolution); // NOI18N
                        }
                    }
                } else {
                    LOG.warn("Can not determine TimeSeries resolution. Using default resolution " + resolution);      // NOI18N
                }

                // config = config.changeResolution(resolution);
                this.setTimeSeriesPanel(resolution);
            }

            // panel = new TimeseriesChartPanel(config, converter);

            // add(panel, BorderLayout.CENTER);
        } catch (final MalformedURLException ex) {
            final String message = "cidsbean contains invalid uri"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }

    @Override
    public void dispose() {
        if (panel != null) {
            panel.dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        pnlNorth = new javax.swing.JPanel();
        chkforecast = new javax.swing.JCheckBox();
        pnlFiller = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        pnlNorth.setOpaque(false);
        pnlNorth.setLayout(new java.awt.GridBagLayout());

        chkforecast.setText(NbBundle.getMessage(TimeseriesRenderer.class, "TimeseriesRenderer.chkforecast.text")); // NOI18N
        chkforecast.setContentAreaFilled(false);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.forecast}"),
                chkforecast,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        pnlNorth.add(chkforecast, gridBagConstraints);

        pnlFiller.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlNorth.add(pnlFiller, gridBagConstraints);

        add(pnlNorth, java.awt.BorderLayout.PAGE_START);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
}
