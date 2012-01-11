/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;

import java.net.MalformedURLException;

import java.text.MessageFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesChartPanel;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
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
    private javax.swing.JButton btnOriginalTS;
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

            final TimeseriesRetrieverConfig tsrConfig = config;

            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (panel != null) {
                            TimeseriesRenderer.this.remove(panel);
                        }

                        TimeseriesRenderer.this.panel = new TimeseriesChartPanel(tsrConfig, converter);
                        TimeseriesRenderer.this.add(TimeseriesRenderer.this.panel, BorderLayout.CENTER);

                        TimeseriesRenderer.this.invalidate();
                        TimeseriesRenderer.this.validate();
                    }
                });
        } catch (final IllegalStateException e) {
            if (resolution == null) {
                LOG.error("An error occured while retrieving original TimeSeries", e); // NOI18N
                throw e;
            } else {
                // most likely, there is no TimeSeries with the specified resolution
                LOG.warn("An error occured while retrieving TimeSeries with resolution " + resolution, e);            // NOI18N
                final int answer = JOptionPane.showConfirmDialog(
                        ComponentRegistry.getRegistry().getMainWindow(),
                        MessageFormat.format(
                            java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle")
                                        .getString(
                                            "TimeseriesRenderer.setTimeSeriesPanel(Resolution).JOptionPane.message"), // NOI18N
                            resolution.getLocalisedName()),
                        java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle")
                                    .getString("TimeseriesRenderer.setTimeSeriesPanel(Resolution).JOptionPane.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

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
            final String uri = (String)cidsBean.getProperty("uri"); // NOI18N 
            final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(uri);
            this.setTimeSeriesPanel(TimeSeriesRendererUtil.getPreviewResolution(config));
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
        btnOriginalTS = new javax.swing.JButton();
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

        btnOriginalTS.setText(org.openide.util.NbBundle.getMessage(
                TimeseriesRenderer.class,
                "TimeseriesRenderer.btnOriginalTS.text")); // NOI18N
        btnOriginalTS.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnOriginalTSActionPerformed(evt);
                }
            });
        pnlNorth.add(btnOriginalTS, new java.awt.GridBagConstraints());

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

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOriginalTSActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOriginalTSActionPerformed

        final int answer = JOptionPane.showConfirmDialog(
                ComponentRegistry.getRegistry().getMainWindow(),
                java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle").getString(
                    "TimeSeriesRenderer.btnOriginalTSActionPerformed(ActionEvent).message"),
                java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objectrenderer/sudplan/Bundle").getString(
                    "TimeSeriesRenderer.btnOriginalTSActionPerformed(ActionEvent).title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            this.setTimeSeriesPanel(null);
        }
    }//GEN-LAST:event_btnOriginalTSActionPerformed
}
