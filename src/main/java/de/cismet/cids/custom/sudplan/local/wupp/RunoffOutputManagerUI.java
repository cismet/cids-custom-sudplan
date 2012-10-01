/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import java.awt.EventQueue;

import java.io.IOException;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class RunoffOutputManagerUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunoffOutputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient RunoffOutputManager model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane edpInfo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHeadingInfo;
    private javax.swing.JLabel lblHeadingPreview;
    private de.cismet.cismap.commons.gui.MappingComponent map;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfo;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfo1;
    private de.cismet.tools.gui.RoundedPanel pnlInfo;
    private javax.swing.JPanel pnlInfoContent;
    private de.cismet.tools.gui.RoundedPanel pnlResult;
    private javax.swing.JPanel pnlWMS;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RunoffOutputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public RunoffOutputManagerUI(final RunoffOutputManager model) {
        initComponents();

        this.model = model;

        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        try {
            final SimulationResult output = model.getUR();

            final Runnable run = new Runnable() {

                    @Override
                    public void run() {
                        edpInfo.setText(output.getGeocpmInfo());
                        initMap(output, model.loadBBoxFromInput());
                    }
                };

            if (EventQueue.isDispatchThread()) {
                run.run();
            } else {
                EventQueue.invokeLater(run);
            }
        } catch (final IOException ex) {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        edpInfo.setText("ERROR: " + ex); // NOI18N
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sr    DOCUMENT ME!
     * @param  bbox  DOCUMENT ME!
     */
    private void initMap(final SimulationResult sr, final XBoundingBox bbox) {
        try {
            final ActiveLayerModel mappingModel = new ActiveLayerModel();
            mappingModel.setSrs(new Crs(SMSUtils.EPSG_WUPP, SMSUtils.EPSG_WUPP, SMSUtils.EPSG_WUPP, true, true));
            mappingModel.addHome(bbox);

            final SimpleWMS ortho = new SimpleWMS(new SimpleWmsGetMapUrl(
                        GeoCPMOptions.getInstance().getProperty("template.getmap.orthophoto").replace( // NOI18N
                            "<cismap:srs>",                                                            // NOI18N
                            "EPSG:31466")));                                                           // NOI18N
            ortho.setName("Wuppertal Ortophoto");                                                      // NOI18N
            mappingModel.addLayer(ortho);

            final String resultUri = model.prepareGetMapRequest(sr)
                        .toExternalForm()
                        .replace(
                            "<cismap:srs>", // NOI18N
                            "EPSG:31466"); // NOI18N

            final SimpleWMS rLayer = new SimpleWMS(new SimpleWmsGetMapUrl(resultUri));
            rLayer.setName(sr.getTaskId());
            rLayer.addRetrievalListener(new RetrievalListener() {

                    private final String text = lblHeadingPreview.getText();

                    @Override
                    public void retrievalStarted(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingPreview.setText(text + " ( Loading ... )");
                                }
                            });
                    }

                    @Override
                    public void retrievalProgress(final RetrievalEvent e) {
                        // noop
                    }

                    @Override
                    public void retrievalComplete(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingPreview.setText(text + " ( Double click preview to add to map )");
                                }
                            });
                    }

                    @Override
                    public void retrievalAborted(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingPreview.setText(text + " ( Retrieval Aborted )");
                                }
                            });
                    }

                    @Override
                    public void retrievalError(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingPreview.setText(text + " ( Retrieval Error )");
                                }
                            });
                    }
                });

            mappingModel.addLayer(rLayer);

            map.setMappingModel(mappingModel);
            map.gotoInitialBoundingBox();

            map.unlock();
            map.setInteractionMode(MappingComponent.ZOOM);
            map.addCustomInputListener("MUTE", new PBasicInputEventHandler() { // NOI18N

                    @Override
                    public void mouseClicked(final PInputEvent evt) {
                        try {
                            if (evt.getClickCount() > 1) {
                                String name;
                                final Callable<String> nameGetter = new Callable<String>() {

                                        @Override
                                        public String call() throws Exception {
                                            return (String)SMSUtils.runFromIO(model.getCidsBean()).getProperty("name"); // NOI18N
                                        }
                                    };

                                final Future<String> nameFuture = SudplanConcurrency.getSudplanGeneralPurposePool()
                                            .submit(nameGetter);
                                try {
                                    name = nameFuture.get(300, TimeUnit.MILLISECONDS);
                                } catch (final Exception ex) {
                                    LOG.warn("cannot get name info in time", ex); // NOI18N
                                    name = sr.getTaskId();
                                }

                                model.addResultLayerToMap(
                                    new SimpleWmsGetMapUrl(model.prepareGetMapRequest(sr).toExternalForm()),
                                    bbox,
                                    name);
                            }
                        } catch (final Exception ex) {
                            LOG.warn("cannot add layer to map", ex); // NOI18N
                        }
                    }
                });
            map.setInteractionMode("MUTE");                          // NOI18N
        } catch (final Exception e) {
            LOG.error("cannot initialise mapping component", e);     // NOI18N
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

        pnlInfo = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfo = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadingInfo = new javax.swing.JLabel();
        pnlInfoContent = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        edpInfo = new javax.swing.JEditorPane();
        pnlResult = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfo1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadingPreview = new javax.swing.JLabel();
        pnlWMS = new javax.swing.JPanel();
        map = new de.cismet.cismap.commons.gui.MappingComponent();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlInfo.setMinimumSize(new java.awt.Dimension(350, 350));
        pnlInfo.setPreferredSize(new java.awt.Dimension(350, 350));
        pnlInfo.setLayout(new java.awt.GridBagLayout());

        panHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setLayout(new java.awt.FlowLayout());

        lblHeadingInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingInfo.setText(org.openide.util.NbBundle.getMessage(
                RunoffOutputManagerUI.class,
                "RunoffOutputManagerUI.lblHeadingInfo.text")); // NOI18N
        panHeadInfo.add(lblHeadingInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlInfo.add(panHeadInfo, gridBagConstraints);

        pnlInfoContent.setOpaque(false);
        pnlInfoContent.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(554, 554));

        edpInfo.setEditable(false);
        edpInfo.setMinimumSize(new java.awt.Dimension(550, 550));
        edpInfo.setPreferredSize(new java.awt.Dimension(550, 550));
        jScrollPane1.setViewportView(edpInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        pnlInfoContent.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlInfo.add(pnlInfoContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlInfo, gridBagConstraints);

        pnlResult.setLayout(new java.awt.GridBagLayout());

        panHeadInfo1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setLayout(new java.awt.FlowLayout());

        lblHeadingPreview.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingPreview.setText(org.openide.util.NbBundle.getMessage(
                RunoffOutputManagerUI.class,
                "RunoffOutputManagerUI.lblHeadingPreview.text")); // NOI18N
        panHeadInfo1.add(lblHeadingPreview);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlResult.add(panHeadInfo1, gridBagConstraints);

        pnlWMS.setOpaque(false);
        pnlWMS.setLayout(new java.awt.BorderLayout());

        map.setMinimumSize(new java.awt.Dimension(200, 200));
        map.setPreferredSize(new java.awt.Dimension(350, 350));
        pnlWMS.add(map, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        pnlResult.add(pnlWMS, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlResult, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents
}
