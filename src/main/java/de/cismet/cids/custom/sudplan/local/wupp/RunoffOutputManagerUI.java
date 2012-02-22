/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.NbBundle;

import java.awt.EventQueue;

import java.io.IOException;

import javax.swing.JPanel;

import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class RunoffOutputManagerUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunoffOutputManagerUI.class);

    private static final String ORTHO_URL =
        "http://geoportal.wuppertal.de:8083/deegree/wms?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=<cismap:srs>&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=R102:luftbild2010&STYLES=default"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient RunoffOutputManager model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane edpInfo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHeading;
    private javax.swing.JLabel lblHeading1;
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
                        initMap(output, loadBBoxFromInput(model.getCidsBean()));
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
     * @param   outputBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private XBoundingBox loadBBoxFromInput(final CidsBean outputBean) {
        final RunoffInputManager m = (RunoffInputManager)SMSUtils.loadManagerFromModel((CidsBean)outputBean.getProperty(
                    "model"),
                ManagerType.INPUT);

        final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", SMSUtils.TABLENAME_MODELINPUT);

        if (mc == null) {
            throw new IllegalStateException("cannot fetch model input metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(", o.").append(mc.getPrimaryKey());                             // NOI18N
        sb.append(" FROM ").append(mc.getTableName()).append(" o, ").append(SMSUtils.TABLENAME_MODELRUN).append(" r"); // NOI18N
        sb.append(" WHERE o.").append(mc.getPrimaryKey()).append(" = r.modelinput");                                   // NOI18N
        sb.append(" AND r.modeloutput = ").append(outputBean.getProperty("id"));                                       // NOI18N

        final MetaObject[] metaObjects;
        try {
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get timeseries meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        if (metaObjects.length != 1) {
            throw new IllegalStateException("did not find exactly one input to this output: " + outputBean); // NOI18N
        }

        m.setCidsBean(metaObjects[0].getBean());
        final RunoffInput io;
        try {
            io = m.getUR();
        } catch (final IOException ex) {
            throw new IllegalStateException("cannot fetch runoff input from ur", ex); // NOI18N
        }

        final CidsBean geocpmBean;
        if (io.getDeltaInputId() < 0) {
            geocpmBean = io.fetchGeocpmInput();
        } else {
            geocpmBean = io.fetchDeltaInput();
        }

        final Geometry geom = (Geometry)geocpmBean.getProperty("geom.geo_field"); // NOI18N
        final Geometry geom31466 = CrsTransformer.transformToGivenCrs(geom.getEnvelope(), SMSUtils.EPSG_WUPP);

        return new XBoundingBox(geom31466, SMSUtils.EPSG_WUPP, true);
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

            final SimpleWMS ortho = new SimpleWMS(new SimpleWmsGetMapUrl(ORTHO_URL));
            ortho.setName("Wuppertal Ortophoto"); // NOI18N
            mappingModel.addLayer(ortho);

            final SimpleWMS rLayer = new SimpleWMS(new SimpleWmsGetMapUrl(
                        sr.getWmsResults().toExternalForm()
                                + "&format_options=layout:geocpm_result_overlay"));     // NOI18N
            rLayer.setName(NbBundle.getMessage(
                    RunoffOutputManagerUI.class,
                    "RunoffOutputManagerUI.initMap(SimulationResult).resultLayer.name", // NOI18N
                    sr.getGeocpmInfo()));
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
                                final SimpleWMS layer = new SimpleWMS(
                                        new SimpleWmsGetMapUrl(sr.getWmsResults().toExternalForm()));
                                layer.setName(
                                    NbBundle.getMessage(
                                        RunoffOutputManagerUI.class,
                                        "RunoffOutputManagerUI.initMap(SimulationResult).resultLayer.name", // NOI18N
                                        sr.getGeocpmInfo()));
                                CismapBroker.getInstance().getMappingComponent().getMappingModel().addLayer(layer);
                                SMSUtils.showMappingComponent();
                            }
                        } catch (final Exception ex) {
                            LOG.warn("cannot add layer to map", ex); // NOI18N
                        }
                    }
                });
            map.setInteractionMode("MUTE"); // NOI18N
        } catch (final Exception e) {
            LOG.error("cannot initialise mapping component", e); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final ObjectMapper m = new ObjectMapper();
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
        lblHeading = new javax.swing.JLabel();
        pnlInfoContent = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        edpInfo = new javax.swing.JEditorPane();
        pnlResult = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfo1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeading1 = new javax.swing.JLabel();
        pnlWMS = new javax.swing.JPanel();
        map = new de.cismet.cismap.commons.gui.MappingComponent();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlInfo.setLayout(new java.awt.GridBagLayout());

        panHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setLayout(new java.awt.FlowLayout());

        lblHeading.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading.setText(org.openide.util.NbBundle.getMessage(
                RunoffOutputManagerUI.class,
                "RunoffOutputManagerUI.lblHeading.text")); // NOI18N
        panHeadInfo.add(lblHeading);

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

        edpInfo.setEditable(false);
        edpInfo.setPreferredSize(null);
        jScrollPane1.setViewportView(edpInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlInfo, gridBagConstraints);

        pnlResult.setLayout(new java.awt.GridBagLayout());

        panHeadInfo1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setLayout(new java.awt.FlowLayout());

        lblHeading1.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading1.setText(org.openide.util.NbBundle.getMessage(
                RunoffOutputManagerUI.class,
                "RunoffOutputManagerUI.lblHeading1.text")); // NOI18N
        panHeadInfo1.add(lblHeading1);

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
        pnlResult.add(pnlWMS, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 11, 5);
        add(pnlResult, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents
}
