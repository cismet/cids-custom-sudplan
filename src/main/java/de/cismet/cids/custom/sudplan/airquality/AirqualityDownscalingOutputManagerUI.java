/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import Sirius.navigator.plugin.PluginRegistry;
import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.IOException;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;

import de.cismet.cids.custom.sudplan.Available;
import de.cismet.cids.custom.sudplan.LocalisedEnumComboBox;
import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.Variable;
import de.cismet.cids.custom.sudplan.airquality.AirqualityDownscalingOutput.Result;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.Disposable;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class AirqualityDownscalingOutputManagerUI extends javax.swing.JPanel implements Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingOutputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    // these four members have to be initialised here as they're used by the cbos defined below
    private final transient Available<Resolution> resolutionAvailable = new ResolutionAvailable();
    private final transient Available<Variable> variableAvailable = new VariableAvailable();
    private final transient Set<Resolution> resolutions = new HashSet<Resolution>();
    private final transient Set<Variable> variables = new HashSet<Variable>();

    private final transient ActionListener showInMapListener;
    private final transient ActionListener exportCSVListener;
    private final transient ItemListener resolutionListener;
    private final transient ItemListener variableListener;

    private final transient AirqualityDownscalingOutputManager model;
    private final transient Collection<Result> results;
    private transient AirqualityDownscalingInput input;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnExport = new javax.swing.JButton();
    private final transient javax.swing.JButton btnShowInMap = new javax.swing.JButton();
    private final transient javax.swing.JComboBox cboResolution = new LocalisedEnumComboBox<Resolution>(
            Resolution.class,
            resolutionAvailable);
    private final transient javax.swing.JComboBox cboVariable = new LocalisedEnumComboBox<Variable>(
            Variable.class,
            variableAvailable);
    private final transient javax.swing.Box.Filler gluControls = new javax.swing.Box.Filler(new java.awt.Dimension(
                0,
                0),
            new java.awt.Dimension(0, 0),
            new java.awt.Dimension(32767, 0));
    private final transient javax.swing.Box.Filler gluMain = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
            new java.awt.Dimension(0, 0),
            new java.awt.Dimension(0, 32767));
    private final transient javax.swing.JProgressBar jpbDownload = new javax.swing.JProgressBar();
    private final transient javax.swing.JLabel lblDownload = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResolution = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblVariable = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlControls = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlProgess = new javax.swing.JPanel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingOutputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingOutputManagerUI(final AirqualityDownscalingOutputManager model) {
        this.model = model;
        this.showInMapListener = new ShowInMapListener();
        this.exportCSVListener = new ExportCSVListener();
        this.resolutionListener = new ResolutionListener();
        this.variableListener = new VariableListener();

        this.results = new LinkedList<Result>();

        initComponents();

        init();

        btnShowInMap.addActionListener(WeakListeners.create(ActionListener.class, showInMapListener, btnShowInMap));
        btnExport.addActionListener(WeakListeners.create(ActionListener.class, exportCSVListener, btnExport));
        cboVariable.addItemListener(WeakListeners.create(ItemListener.class, variableListener, cboVariable));
        cboResolution.addItemListener(WeakListeners.create(ItemListener.class, resolutionListener, cboResolution));

        try {
            input = AirqualityDownscalingModelManager.inputFromRun(
                    SMSUtils.fetchCidsBean(model.getUR().getModelRunId(), SMSUtils.TABLENAME_MODELRUN));
        } catch (final IOException ex) {
            input = null;
            // TODO: User feedback.
            // TODO: Disable all elements.
        }

        pnlProgess.setVisible(false);
        gluControls.setVisible(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        results.clear();
        resolutions.clear();
        variables.clear();

        try {
            results.addAll(model.getUR().getResults());

            for (final Result result : results) {
                resolutions.add(result.getResolution());
                variables.add(result.getVariable());
            }
        } catch (Exception ex) {
            LOG.error("Couldn't get output bean.", ex); // NOI18N
            return;
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

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        gluMain.setMaximumSize(new java.awt.Dimension(0, 32767));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        add(gluMain, gridBagConstraints);

        lblVariable.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblVariable.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblVariable, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cboResolution, gridBagConstraints);

        lblResolution.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblResolution.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblResolution, gridBagConstraints);

        pnlControls.setOpaque(false);
        pnlControls.setLayout(new java.awt.GridBagLayout());

        btnExport.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.btnExport.text")); // NOI18N
        btnExport.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        pnlControls.add(btnExport, gridBagConstraints);

        btnShowInMap.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.btnShowInMap.text")); // NOI18N
        btnShowInMap.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlControls.add(btnShowInMap, gridBagConstraints);

        gluControls.setMaximumSize(new java.awt.Dimension(32767, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlControls.add(gluControls, gridBagConstraints);

        pnlProgess.setOpaque(false);
        pnlProgess.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pnlProgess.add(jpbDownload, gridBagConstraints);

        lblDownload.setText(NbBundle.getMessage(
                AirqualityDownscalingOutputManagerUI.class,
                "AirqualityDownscalingOutputManagerUI.lblDownload.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pnlProgess.add(lblDownload, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlControls.add(pnlProgess, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        add(pnlControls, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cboVariable, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void dispose() {
        // cannot dispose the internal widget as dispose is called when the listne
        // CismapBroker.getInstance().getMappingComponent().removeInternalWidget(widget.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Result getResultToShow() {
        final Resolution resolution;
        final Variable variable;
        Result resultToShow = null;

        if (cboResolution.getSelectedItem() instanceof Resolution) {
            resolution = (Resolution)cboResolution.getSelectedItem();
        } else {
            // TODO: User feedback
            LOG.warn("No resolution selected. Can't display results in map."); // NOI18N
            return resultToShow;
        }
        if (cboVariable.getSelectedItem() instanceof Variable) {
            variable = (Variable)cboVariable.getSelectedItem();
        } else {
            // TODO: User feedback
            LOG.warn("No variable selected. Can't display results in map."); // NOI18N
            return resultToShow;
        }

        for (final Result result : results) {
            if (resolution.equals(result.getResolution()) && variable.equals(result.getVariable())) {
                resultToShow = result;
                break;
            }
        }

        return resultToShow;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ShowInMapListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Result resultToShow = getResultToShow();

            if (resultToShow == null) {
                // TODO: User feedback
                LOG.warn("No result found for given resolution and variable. Can't display results in map."); // NOI18N
                return;
            }

            btnShowInMap.setEnabled(false);
            btnExport.setEnabled(false);
            jpbDownload.setIndeterminate(true);
            gluControls.setVisible(false);
            pnlProgess.setVisible(true);

            if ((model.getCidsBean() == null)
                        || !(model.getCidsBean().getProperty("id") instanceof Integer)
                        || !(model.getCidsBean().getProperty("name") instanceof String)) {
                LOG.error("Model output bean has an invalid id or name."); // NOI18N
                return;
            }

            final Crs srs = CismapBroker.getInstance().crsFromCode(input.getSrs());
            if (srs == null) {
                LOG.error("Couldn't get a Crs object from SRS code '" + input.getSrs() + "'.");
                return;
            }

            final AirqualityDownscalingResultManager manager = new AirqualityDownscalingResultManager(
                    resultToShow,
                    (Integer)model.getCidsBean().getProperty("id"),
                    (String)model.getCidsBean().getProperty("name"),
                    srs);
            final Future<SlidableWMSServiceLayerGroup> managerFuture = SudplanConcurrency.getSudplanGeneralPurposePool()
                        .submit(manager);

            SudplanConcurrency.getSudplanGeneralPurposePool().execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final SlidableWMSServiceLayerGroup layerGroup = managerFuture.get();

                            if ((manager.getException() != null) || (layerGroup == null)) {
                                final Exception exception = manager.getException();

                                LOG.error("Could not visualize result.", exception); // NOI18N

                                final ErrorInfo errorInfo = new ErrorInfo(
                                        org.openide.util.NbBundle.getMessage(
                                            AirqualityDownscalingOutputManagerUI.class,
                                            "AirqualityDownscalingOutputManagerUI.ShowInMapListener.actionPerformed(ActionEvent).run().Runnable().run().visualizationError.title"),   // NOI18N
                                        org.openide.util.NbBundle.getMessage(
                                            AirqualityDownscalingOutputManagerUI.class,
                                            "AirqualityDownscalingOutputManagerUI.ShowInMapListener.actionPerformed(ActionEvent).run().Runnable().run().visualizationError.message"), // NOI18N
                                        null,
                                        null,
                                        exception,
                                        Level.SEVERE,
                                        null);

                                JXErrorPane.showDialog(AirqualityDownscalingOutputManagerUI.this, errorInfo);
                            } else {
                                ((CismapPlugin)PluginRegistry.getRegistry().getPluginDescriptor("cismap").getPlugin())
                                        .getCapabilities()
                                        .processUrl(
                                            AirqualityDownscalingResultManager.GEOSERVER_CAPABILITIES_URL,
                                            null);
                                CismapBroker.getInstance().getMappingComponent().getMappingModel().addLayer(layerGroup);
                                ComponentRegistry.getRegistry().showComponent("cismap");
                            }
                        } catch (final Exception ex) {
                            // TODO: User feedback.
                            LOG.error("Could not visualize result.", ex); // NOI18N

                            final ErrorInfo errorInfo = new ErrorInfo(
                                    org.openide.util.NbBundle.getMessage(
                                        AirqualityDownscalingOutputManagerUI.class,
                                        "AirqualityDownscalingOutputManagerUI.ShowInMapListener.actionPerformed(ActionEvent).run().Runnable().run().visualizationError.title"),   // NOI18N
                                    org.openide.util.NbBundle.getMessage(
                                        AirqualityDownscalingOutputManagerUI.class,
                                        "AirqualityDownscalingOutputManagerUI.ShowInMapListener.actionPerformed(ActionEvent).run().Runnable().run().visualizationError.message"), // NOI18N
                                    null,
                                    null,
                                    ex,
                                    Level.SEVERE,
                                    null);

                            JXErrorPane.showDialog(AirqualityDownscalingOutputManagerUI.this, errorInfo);
                        } finally {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        pnlProgess.setVisible(false);
                                        gluControls.setVisible(true);
                                        jpbDownload.setIndeterminate(false);
                                        btnShowInMap.setEnabled(true);
                                        btnExport.setEnabled(true);
                                    }
                                });
                        }
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ExportCSVListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Result resultToShow = getResultToShow();

            if (resultToShow == null) {
                // TODO: User feedback
                LOG.warn("No result found for given resolution and variable. Can't display results in map."); // NOI18N
                return;
            }

            if ((model.getCidsBean() == null)
                        || !(model.getCidsBean().getProperty("id") instanceof Integer)
                        || !(model.getCidsBean().getProperty("name") instanceof String)) {
                LOG.error("Model output bean has an invalid id or name."); // NOI18N
                return;
            }

            final Crs srs = CismapBroker.getInstance().crsFromCode(input.getSrs());
            if (srs == null) {
                LOG.error("Couldn't get a Crs object from SRS code '" + input.getSrs() + "'.");
                return;
            }

            final AirqualityDownscalingResultManager manager = new AirqualityDownscalingResultManager(
                    resultToShow,
                    (Integer)model.getCidsBean().getProperty("id"),
                    (String)model.getCidsBean().getProperty("name"),
                    srs);

            if (!DownloadManagerDialog.showAskingForUserTitle(AirqualityDownscalingOutputManagerUI.this)) {
                return;
            }

            DownloadManager.instance()
                    .add(
                        new AirqualityDownscalingResultManager.AirqualityDownscalingResultManagerDownload(
                            manager,
                            DownloadManagerDialog.getJobname(),
                            "Airquality Downscaling",
                            "aqds",
                            ".csv"));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VariableListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                if ((cboResolution.getSelectedItem() instanceof Resolution) && (e.getItem() instanceof Variable)) {
                    final boolean enableButtons = resolutionAvailable.isAvailable((Resolution)
                            cboResolution.getSelectedItem())
                                && variableAvailable.isAvailable((Variable)e.getItem());
                    btnShowInMap.setEnabled(enableButtons);
                    btnExport.setEnabled(enableButtons);
                }
            }
        }
    }

    /**
     * FIXME: atr hack to display appropriate timerange boundaries
     *
     * @version  $Revision$, $Date$
     */
    private final class ResolutionListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                if ((e.getItem() instanceof Resolution) && (cboVariable.getSelectedItem() instanceof Variable)) {
                    final boolean enableButtons = resolutionAvailable.isAvailable((Resolution)e.getItem())
                                && variableAvailable.isAvailable((Variable)cboVariable.getSelectedItem());
                    btnShowInMap.setEnabled(enableButtons);
                    btnExport.setEnabled(enableButtons);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ResolutionAvailable implements Available<Resolution> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean isAvailable(final Resolution type) {
            return resolutions.contains(type);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VariableAvailable implements Available<Variable> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean isAvailable(final Variable type) {
            return variables.contains(type);
        }
    }
}
