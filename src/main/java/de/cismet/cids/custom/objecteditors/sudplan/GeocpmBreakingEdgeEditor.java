/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.sudplan;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.math.BigDecimal;

import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.objecteditors.sudplan.DeltaBreakingEdgeEditor.BEHeightConverter;
import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.local.wupp.DeltaConfigurationList;
import de.cismet.cids.custom.tostringconverter.sudplan.DeltaConfigurationToStringConverter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = DeltaConfigurationList.class)
public class GeocpmBreakingEdgeEditor extends AbstractCidsBeanRenderer implements EditorSaveListener,
    DeltaConfigurationList {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeocpmBreakingEdgeEditor.class);

    public static final String ORTHO_URL =
        "http://geoportal.wuppertal.de:8083/deegree/wms?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=<cismap:srs>&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=R102:luftbild2010&STYLES=default";

    //~ Instance fields --------------------------------------------------------

    private final transient BEHeightConverter heightConv;

    private transient CidsBean currentDeltaBEBean;

    private final transient ListSelectionListener selL;

    private final transient ActionListener newL;

    private final transient DocumentListener cfgNameChangeL;

    private final transient PropertyChangeListener dbeChangedL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnNew = new javax.swing.JButton();
    private final transient de.cismet.cids.custom.objecteditors.sudplan.DeltaBreakingEdgeEditor
        deltaBreakingEdgeEditor = new de.cismet.cids.custom.objecteditors.sudplan.DeltaBreakingEdgeEditor();
    private final transient de.cismet.cids.custom.objecteditors.sudplan.DeltaConfigurationEditor deltaCfgEditor =
        new de.cismet.cids.custom.objecteditors.sudplan.DeltaConfigurationEditor();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
    private final transient javax.swing.JLabel lblHeading = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeading1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeading2 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeadingMap = new javax.swing.JLabel();
    private final transient org.jdesktop.swingx.JXList lstDeltaCfgs = new org.jdesktop.swingx.JXList();
    private final transient de.cismet.cismap.commons.gui.MappingComponent mcBreakingEdge =
        new de.cismet.cismap.commons.gui.MappingComponent();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo1 =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo2 =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfoMap =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlBreakingEdge = new de.cismet.tools.gui.RoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlBreakingEdgeMap =
        new de.cismet.tools.gui.RoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlCfg = new de.cismet.tools.gui.RoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlCfgs = new de.cismet.tools.gui.RoundedPanel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GeocpmBreakingEdgeEditor.
     */
    public GeocpmBreakingEdgeEditor() {
        selL = new SelectionListener();
        newL = new NewDeltaCfgListener();
        cfgNameChangeL = new CfgNameChangeListener();
        dbeChangedL = new DBEPropertyChangedListener();

        initComponents();
        btnNew.setText("");
//        btnNew.setIcon(new javax.swing.ImageIcon(
//                getClass().getResource("/de/cismet/cids/custom/objecteditors/sudplan/geocpm_delta_new_16.png")));
        btnNew.setToolTipText("Create a new delta configuration");
//        lblHeading1.setIcon(new javax.swing.ImageIcon(
//                getClass().getResource("/de/cismet/cids/custom/sudplan/geocpm_delta_16.png")));
//        lblHeading2.setIcon(new javax.swing.ImageIcon(
//                getClass().getResource("/de/cismet/cids/custom/sudplan/breaking_edge_beta_16.png")));

        heightConv = new BEHeightConverter();

        lstDeltaCfgs.setComparator(new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    if ((o1 == null) && (o2 == null)) {
                        return 0;
                    } else if ((o1 == null) && (o2 != null)) {
                        return -1;
                    } else if ((o1 != null) && (o2 == null)) {
                        return 1;
                    } else {
                        return ((String)o1.getProperty("name")).compareTo((String)o2.getProperty("name")); // NOI18N
                    }
                }
            });
        lstDeltaCfgs.setCellRenderer(new DeltaCfgCellRenderer());

        lstDeltaCfgs.addListSelectionListener(WeakListeners.create(ListSelectionListener.class, selL, lstDeltaCfgs));

        btnNew.addActionListener(WeakListeners.create(ActionListener.class, newL, btnNew));
        deltaCfgEditor.addNameChangeListener(cfgNameChangeL);
    }

    //~ Methods ----------------------------------------------------------------

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

        pnlCfgs.setLayout(new java.awt.GridBagLayout());

        panHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setLayout(new java.awt.FlowLayout());

        lblHeading.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading.setText(org.openide.util.NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblHeading.text")); // NOI18N
        panHeadInfo.add(lblHeading);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlCfgs.add(panHeadInfo, gridBagConstraints);

        jScrollPane1.setViewportView(lstDeltaCfgs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        pnlCfgs.add(jScrollPane1, gridBagConstraints);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);

        btnNew.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/sudplan/geocpm_delta_new_16.png")));    // NOI18N
        btnNew.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.btnNew.text")); // NOI18N
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnNew);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 2, 10);
        pnlCfgs.add(jToolBar1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlCfgs, gridBagConstraints);

        pnlCfg.setLayout(new java.awt.GridBagLayout());

        panHeadInfo1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setLayout(new java.awt.FlowLayout());

        lblHeading1.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/sudplan/geocpm_delta_16.png"))); // NOI18N
        lblHeading1.setText(org.openide.util.NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblHeading1.text"));                                                // NOI18N
        panHeadInfo1.add(lblHeading1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlCfg.add(panHeadInfo1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlCfg.add(deltaCfgEditor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlCfg, gridBagConstraints);

        pnlBreakingEdge.setLayout(new java.awt.GridBagLayout());

        panHeadInfo2.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo2.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo2.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo2.setLayout(new java.awt.FlowLayout());

        lblHeading2.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/sudplan/breaking_edge_beta_16.png"))); // NOI18N
        lblHeading2.setText(org.openide.util.NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblHeading2.text"));                                                      // NOI18N
        panHeadInfo2.add(lblHeading2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlBreakingEdge.add(panHeadInfo2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlBreakingEdge.add(deltaBreakingEdgeEditor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlBreakingEdge, gridBagConstraints);

        pnlBreakingEdgeMap.setLayout(new java.awt.GridBagLayout());

        panHeadInfoMap.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfoMap.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfoMap.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfoMap.setLayout(new java.awt.FlowLayout());

        lblHeadingMap.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingMap.setText(org.openide.util.NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblHeadingMap.text")); // NOI18N
        panHeadInfoMap.add(lblHeadingMap);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlBreakingEdgeMap.add(panHeadInfoMap, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlBreakingEdgeMap.add(mcBreakingEdge, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlBreakingEdgeMap, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    protected void init() {
        try {
            final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "delta_configuration");   // NOI18N
            if (mc == null) {
                throw new IllegalStateException(
                    "illegal domain for this operation, mc 'delta_configuration@SUDPLAN-WUPP' not found"); // NOI18N
            }

            final Integer id = (Integer)cidsBean.getProperty("geocpm_configuration_id.id");         // NOI18N
            if (id == null) {
                throw new IllegalStateException("cannot get geocpm configuration id: " + cidsBean); // NOI18N
            }

            final String query = "select " + mc.getID() + "," + mc.getPrimaryKey() + " from " // NOI18N
                        + mc.getTableName()
                        + " where original_object = " + id;                                   // NOI18N

            final MetaObject[] deltaCfgObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                            query,
                            SMSUtils.DOMAIN_SUDPLAN_WUPP);

            final DefaultListModel model = new DefaultListModel();
            for (final MetaObject mo : deltaCfgObjects) {
                model.addElement(mo.getBean());
            }

            lstDeltaCfgs.setModel(model);
            lstDeltaCfgs.setSortOrder(SortOrder.ASCENDING);

            // sets default values
            setCurrentDeltaBreakingEdge(null);
            initMap();
        } catch (final Exception ex) {
            final String message = "cannot initialise editor"; // NOI18N
            LOG.error(message, ex);

            throw new IllegalStateException(message, ex);
        }
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        // noop
    }

    @Override
    public boolean prepareForSave() {
        // saving is kind of a cfg change as it changed selection to nothing
        beforeDeltaCfgChange((CidsBean)lstDeltaCfgs.getSelectedValue());

        final DefaultListModel dlm = (DefaultListModel)lstDeltaCfgs.getModel();
        final Enumeration<?> e = dlm.elements();

        CidsBean deltaCfgBean = null;
        try {
            while (e.hasMoreElements()) {
                deltaCfgBean = (CidsBean)e.nextElement();
                deltaCfgBean.persist();
            }
            final DeltaConfigurationList deltaConfigurationList = Lookup.getDefault()
                        .lookup(DeltaConfigurationList.class);
            deltaConfigurationList.fireConfigsChanged();
        } catch (final Exception ex) {
            final String message = "cannot persist config: " + deltaCfgBean; // NOI18N
            LOG.error(message, ex);

            JXErrorPane.showDialog(
                this,
                new ErrorInfo(
                    "Fehler beim Speichern",
                    "Beim Speichern ist ein Fehler aufgetreten.",
                    "Das Speichern von Änderungskonfiguration '"
                            + deltaCfgBean
                            + " ist fehlgeschlagen.",
                    "EDITOR",
                    ex,
                    Level.WARNING,
                    null));

            return false;
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dbeBean  DOCUMENT ME!
     */
    private void setCurrentDeltaBreakingEdge(final CidsBean dbeBean) {
        if (currentDeltaBEBean != null) {
            currentDeltaBEBean.removePropertyChangeListener(dbeChangedL);
        }

        currentDeltaBEBean = dbeBean;

        if (currentDeltaBEBean != null) {
            currentDeltaBEBean.addPropertyChangeListener(dbeChangedL);
        }

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    deltaBreakingEdgeEditor.setCidsBean(dbeBean);
                }
            };

        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void computeChangeStatus() {
        // TODO: keep track of changes and update appropriately
        cidsBean.setArtificialChangeFlag(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  deltaCfgBean  DOCUMENT ME!
     */
    private void beforeDeltaCfgChange(final CidsBean deltaCfgBean) {
        if (currentDeltaBEBean != null) {
            final String height = heightConv.convertForward((BigDecimal)cidsBean.getProperty("height"));
            final String newHeight = heightConv.convertForward((BigDecimal)currentDeltaBEBean.getProperty("height"));

            if (height.equals(newHeight)) {
                final Collection<CidsBean> dbes = (Collection)deltaCfgBean.getProperty("delta_breaking_edges"); // NOI18N
                dbes.remove(currentDeltaBEBean);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        try {
            // FIXME do not use a fix buffer
            final Geometry geom = (Geometry)cidsBean.getProperty("geom.geo_field");
            final Geometry geom31466 = CrsTransformer.transformToGivenCrs(geom.getEnvelope(), SMSUtils.EPSG_WUPP)
                        .buffer(60);
            // geom31466 = geom31466.buffer(50);
            final XBoundingBox bbox = new XBoundingBox(geom31466, SMSUtils.EPSG_WUPP, true);

            final ActiveLayerModel mappingModel = new ActiveLayerModel();
            mappingModel.setSrs(new Crs(SMSUtils.EPSG_WUPP, SMSUtils.EPSG_WUPP, SMSUtils.EPSG_WUPP, true, true));

            mappingModel.addHome(bbox);

            final SimpleWMS ortho = new SimpleWMS(new SimpleWmsGetMapUrl(ORTHO_URL));

            ortho.setName("Wuppertal Ortophoto"); // NOI18N

            final RetrievalListener rl = new RetrievalListener() {

                    private final transient String text = lblHeadingMap.getText();

                    @Override
                    public void retrievalStarted(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(text + "( Loading... )");
                                }
                            });
                    }

                    @Override
                    public void retrievalProgress(final RetrievalEvent e) {
                    }

                    @Override
                    public void retrievalComplete(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(text + "( Double click preview to add )");
                                }
                            });
                    }

                    @Override
                    public void retrievalAborted(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(text + "( Retrieval Aborted )");
                                }
                            });
                    }

                    @Override
                    public void retrievalError(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(text + "( Retrieval Error )");
                                }
                            });
                    }
                };

            ortho.addRetrievalListener(rl);

            mappingModel.addLayer(ortho);

            mcBreakingEdge.setMappingModel(mappingModel);

            mcBreakingEdge.gotoInitialBoundingBox();

            mcBreakingEdge.unlock();
            mcBreakingEdge.setInteractionMode(MappingComponent.ZOOM);
            final CidsFeature feature = new CidsFeature(cidsBean.getMetaObject());
            mcBreakingEdge.getFeatureCollection().addFeature(feature);
            mcBreakingEdge.addCustomInputListener("DoubleClick", new PBasicInputEventHandler() {

                    @Override
                    public void mouseClicked(final PInputEvent pie) {
                        if (pie.getClickCount() > 1) {
                            SMSUtils.showMappingComponent();
                            CismapBroker.getInstance().getMappingComponent().gotoBoundingBoxWithHistory(bbox);
                            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(feature);
                        }
                    }
                });
            mcBreakingEdge.setInteractionMode("DoubleClick");
        } catch (Exception e) {
            LOG.error("cannot initialise mapping component", e);
        }
    }

    @Override
    public void fireConfigsChanged() {
        try {
            if (cidsBean == null) {
                return;
            }
            final Object selectedConfig = lstDeltaCfgs.getSelectedValue();
            final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "delta_configuration");   // NOI18N
            if (mc == null) {
                throw new IllegalStateException(
                    "illegal domain for this operation, mc 'delta_configuration@SUDPLAN-WUPP' not found"); // NOI18N
            }

            final Integer id = (Integer)cidsBean.getProperty("geocpm_configuration_id.id");         // NOI18N
            if (id == null) {
                throw new IllegalStateException("cannot get geocpm configuration id: " + cidsBean); // NOI18N
            }

            final String query = "select " + mc.getID() + "," + mc.getPrimaryKey() + " from " // NOI18N
                        + mc.getTableName()
                        + " where original_object = " + id;                                   // NOI18N

            final MetaObject[] deltaCfgObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                            query,
                            SMSUtils.DOMAIN_SUDPLAN_WUPP);

            final DefaultListModel model = new DefaultListModel();
            for (final MetaObject mo : deltaCfgObjects) {
                model.addElement(mo.getBean());
            }

            lstDeltaCfgs.setModel(model);
            lstDeltaCfgs.setSortOrder(SortOrder.ASCENDING);
            lstDeltaCfgs.setSelectedValue(selectedConfig, true);
        } catch (final Exception ex) {
            final String message = "cannot initialise editor"; // NOI18N
            LOG.error(message, ex);

            throw new IllegalStateException(message, ex);
        }
    }

    @Override
    public CidsBean getSelectedConfig() {
        // nothing to do
        return null;
    }

    @Override
    public void addSelectionListener(final ListSelectionListener l) {
        // nothing to do
    }

    @Override
    public void removeSelectionListener(final ListSelectionListener l) {
        // nothing to do
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DBEPropertyChangedListener implements PropertyChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            computeChangeStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CfgNameChangeListener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            computeChangeStatus();

            // FIXME: quick 'n' dirty
            lstDeltaCfgs.repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class NewDeltaCfgListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "delta_configuration");   // NOI18N
                if (mc == null) {
                    throw new IllegalStateException(
                        "illegal domain for this operation, mc 'delta_configuration@SUDPLAN-WUPP' not found"); // NOI18N
                }

                final CidsBean origBean = (CidsBean)cidsBean.getProperty("geocpm_configuration_id");          // NOI18N
                if (origBean == null) {
                    throw new IllegalStateException("cannot get original geocpm configuration: " + cidsBean); // NOI18N
                }

                final CidsBean newCfgBean = mc.getEmptyInstance().getBean();
                newCfgBean.setProperty("original_object", origBean); // NOI18N
                newCfgBean.setProperty("name", "Neue Konfiguration");
                newCfgBean.setProperty("description", "Bitte Beschreibung einfügen");

                ((DefaultListModel)lstDeltaCfgs.getModel()).addElement(newCfgBean);
                lstDeltaCfgs.setSortOrder(SortOrder.ASCENDING);
                lstDeltaCfgs.setSelectedValue(newCfgBean, true);

                computeChangeStatus();
            } catch (final Exception ex) {
                final String message = "cannot create new delta configuration"; // NOI18N
                LOG.error(message, ex);

                throw new IllegalStateException(message, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class SelectionListener implements ListSelectionListener {

        //~ Instance fields ----------------------------------------------------

        private transient CidsBean currentDeltaCfgBean;

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            final CidsBean bean = (CidsBean)lstDeltaCfgs.getSelectedValue();

            final Collection<CidsBean> dbes = (Collection)bean.getProperty("delta_breaking_edges"); // NOI18N

            if (currentDeltaCfgBean != null) {
                beforeDeltaCfgChange(currentDeltaCfgBean);
            }

            if (bean != null) {
                deltaCfgEditor.setCidsBean(bean);

                boolean createNew = true;
                for (final CidsBean dbeBean : dbes) {
                    if (dbeBean.getProperty("original_object.id").equals(cidsBean.getProperty("id"))) { // NOI18N
                        setCurrentDeltaBreakingEdge(dbeBean);
                        createNew = false;

                        break;
                    }
                }

                if (createNew) {
                    final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "delta_breaking_edge");   // NOI18N
                    if (mc == null) {
                        throw new IllegalStateException(
                            "illegal domain for this operation, mc 'delta_breaking_edge@SUDPLAN-WUPP' not found"); // NOI18N
                    }

                    try {
                        final CidsBean newBean = mc.getEmptyInstance().getBean();
                        newBean.setProperty("original_object", cidsBean);              // NOI18N
                        newBean.setProperty("height", cidsBean.getProperty("height")); // NOI18N

                        setCurrentDeltaBreakingEdge(newBean);

                        dbes.add(currentDeltaBEBean);
                    } catch (final Exception ex) {
                        final String message = "cannot initialise new delta breaking edge"; // NOI18N
                        LOG.error(message, ex);

                        throw new IllegalStateException(message, ex);
                    }
                }
            }

            currentDeltaCfgBean = bean;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class DeltaCfgCellRenderer extends DefaultListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final transient DeltaConfigurationToStringConverter toString;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DeltaCfgCellRenderer object.
         */
        public DeltaCfgCellRenderer() {
            toString = new DeltaConfigurationToStringConverter();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel) {
                final JLabel l = (JLabel)c;
                l.setText(toString.convert(((CidsBean)value).getMetaObject()));
                if (value instanceof CidsBean) {
                    final CidsBean bean = (CidsBean)value;
                    if ((bean != null) && (bean.getMetaObject() != null)
                                && (bean.getMetaObject().getMetaClass() != null)) {
                        // l.setIcon(new ImageIcon(bean.getMetaObject().getMetaClass().getIconData()));
                        l.setIcon(new javax.swing.ImageIcon(
                                getClass().getResource("/de/cismet/cids/custom/sudplan/geocpm_delta_16.png")));
                    }
                }
            }

            return c;
        }
    }
}
