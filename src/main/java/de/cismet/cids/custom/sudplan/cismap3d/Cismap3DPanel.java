/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d;

import Sirius.navigator.types.treenode.DefaultMetaTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import javax.vecmath.Vector3d;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.cismap3d.dfki.ProgressPanel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.interfaces.DefaultMetaTreeNodeVisualizationService;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.FeatureCollectionAdapter;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.gui.BasicGuiComponentProvider;
import de.cismet.tools.gui.CustomButtonProvider;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
@ServiceProviders(
    value = {
            @ServiceProvider(service = BasicGuiComponentProvider.class),
            @ServiceProvider(service = DefaultMetaTreeNodeVisualizationService.class)
        }
)
// NOTE: if performance is going down, lower startup time and memory footprint by aggregating inner classes
public class Cismap3DPanel extends javax.swing.JPanel implements BasicGuiComponentProvider,
    CustomButtonProvider,
    DefaultMetaTreeNodeVisualizationService {

    //~ Static fields/initializers ---------------------------------------------

    public static final String CLASSNAME_CISMAP3DCONTENT = "cismap3dcontent"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(Cismap3DPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final transient List<JComponent> buttons;
    private final transient ActionListener homeL;
    private final transient ActionListener toggleCamL;
    private final transient ActionListener eastToWestL;
    private final transient ActionListener westToEastL;
    private final transient ActionListener southToNorthL;
    private final transient ActionListener northToSouthL;
    private final transient ActionListener toBBoxL;
    private final transient ActionListener fromBBoxL;

    private final transient Canvas3D canvas3D;
    private final transient Layer3D layer3D;
    private final transient MovingCameraFeature camFeature;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblNo3d;
    // End of variables declaration//GEN-END:variables

    private final transient PropertyChangeListener interactionModeChangeL;
    private final transient FeatureCollectionListener featureRemovalL;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Cismap3DPanel.
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public Cismap3DPanel() {
        homeL = new HomeListener();
        toggleCamL = new CameraToggleListener();
        eastToWestL = new EastToWestListener();
        westToEastL = new WestToEastListener();
        southToNorthL = new SouthToNorthListener();
        northToSouthL = new NorthToSouthListener();
        toBBoxL = new ToBBoxListener();
        fromBBoxL = new FromBBoxListener();
        interactionModeChangeL = new InteractionModeChangeListener();
        featureRemovalL = new FeatureRemovalListener();

        camFeature = new MovingCameraFeature();
        buttons = createButtons();

        initComponents();

        canvas3D = Lookup.getDefault().lookup(Canvas3D.class);
        layer3D = Lookup.getDefault().lookup(Layer3D.class);

        if (canvas3D == null) {
            LOG.info("no canvas 3d implementation can be found");    // NOI18N
        } else {
            LOG.info("found canvas 3d implementation: " + canvas3D); // NOI18N

            if (layer3D == null) {
                throw new IllegalStateException("canvas 3D without layer 3D illegal"); // NOI18N
            }

            removeAll();
            setLayout(new BorderLayout());
            add(canvas3D.getUI(), BorderLayout.CENTER);
        }

        setName("Map 3D");

        final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
        mc.addPropertyChangeListener(WeakListeners.propertyChange(interactionModeChangeL, mc));

        // TODO: use weak listener
        mc.getFeatureCollection().addFeatureCollectionListener(featureRemovalL);
    }

    //~ Methods ----------------------------------------------------------------

    // TODO: to be removed as soon as featurecollectionlistener is weak
    @Override
    protected void finalize() throws Throwable {
        try {
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            mc.getFeatureCollection().removeFeatureCollectionListener(featureRemovalL);
        } catch (final Exception e) {
            try {
                LOG.error("cannot finalize Cismap3DPanel", e); // NOI18N
            } catch (final Exception ex) {
                // if logging fails we're in big trouble and the VM should be shut down anyway. we don't throw anything
                // so that finalization can be done on the super implementation
            }
        }

        super.finalize();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<JComponent> createButtons() {
        final List<JComponent> list = new ArrayList<JComponent>(1);

        final JButton home = createButton("home_16.gif", homeL); // NOI18N

        final JButton toggleCam = createButton("camera_toggle_16.png", toggleCamL); // NOI18N

        final JButton left = createButton("arrow_left_16.png", eastToWestL);   // NOI18N
        final JButton right = createButton("arrow_right_16.png", westToEastL);
        final JButton up = createButton("arrow_up_16.png", southToNorthL);     // NOI18N
        final JButton down = createButton("arrow_down_16.png", northToSouthL); // NOI18N

        final JButton toBBox = createButton("to_bbox_16.png", toBBoxL);       // NOI18N
        final JButton fromBBox = createButton("from_bbox_16.png", fromBBoxL); // NOI18N

        list.add(toBBox);
        list.add(fromBBox);
        list.add(up);
        list.add(left);
        list.add(down);
        list.add(right);
        list.add(toggleCam);
        list.add(home);

        return Collections.unmodifiableList(list);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   iconName  DOCUMENT ME!
     * @param   actionL   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JButton createButton(final String iconName, final ActionListener actionL) {
        final ImageIcon icon = SMSUtils.loadImageIcon(getClass(), iconName);
        final JButton button = new JButton(icon);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(WeakListeners.create(ActionListener.class, actionL, button));

        return button;
    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        lblNo3d = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblNo3d.setFont(new java.awt.Font("Lucida Grande", 1, 48));                              // NOI18N
        lblNo3d.setText(NbBundle.getMessage(Cismap3DPanel.class, "Cismap3DPanel.lblNo3d.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(lblNo3d, gridBagConstraints);
    }                                                                                            // </editor-fold>//GEN-END:initComponents

    @Override
    public String getId() {
        return "cismap_3d"; // NOI18N
    }

    @Override
    public String getDescription() {
        return "Cismap 3D Visualisation Panel";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public GuiType getType() {
        return GuiType.GUICOMPONENT;
    }

    @Override
    public Object getPositionHint() {
        return Integer.valueOf(1);
    }

    @Override
    public void setLinkObject(final Object link) {
    }

    @Override
    public Collection<JComponent> getCustomButtons() {
        return buttons;
    }

    @Override
    public void removeVisualization(final DefaultMetaTreeNode dmtn) throws Exception {
        removeVisualization(Arrays.asList(dmtn));
    }

    @Override
    public void removeVisualization(final Collection<DefaultMetaTreeNode> c) throws Exception {
        if (canvas3D != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("remove visualisation: " + c); // NOI18N
            }

            assert c != null : "collection must not be null"; // NOI18N

            if (LOG.isDebugEnabled()) {
                LOG.debug("remove visualisation: " + c); // NOI18N
            }

            for (final DefaultMetaTreeNode dmtn : c) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("remove visualisation: " + dmtn); // NOI18N
                }

                if (dmtn instanceof ObjectTreeNode) {
                    final CidsBean cids3dBean = get3DBean(((ObjectTreeNode)dmtn).getMetaObject());

                    if (cids3dBean == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring non-3d bean: " + dmtn); // NOI18N
                        }
                    } else {
                        layer3D.removeLayer(get3DURI(cids3dBean));
                    }
                } else {
                    LOG.warn("unsupported node type: " + dmtn);         // NOI18N
                }
            }
        }
    }

    @Override
    public void addVisualization(final DefaultMetaTreeNode dmtn) throws Exception {
        addVisualization(Arrays.asList(dmtn));
    }

    @Override
    public void addVisualization(final Collection<DefaultMetaTreeNode> c) throws Exception {
        if (canvas3D != null) {
            assert c != null : "collection must not be null"; // NOI18N

            if (LOG.isDebugEnabled()) {
                LOG.debug("add visualisation: " + c); // NOI18N
            }

            for (final DefaultMetaTreeNode dmtn : c) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("add visualisation: " + dmtn); // NOI18N
                }

                if (dmtn instanceof ObjectTreeNode) {
                    final CidsBean cids3dBean = get3DBean(((ObjectTreeNode)dmtn).getMetaObject());

                    if (cids3dBean == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring non-3d bean: " + dmtn); // NOI18N
                        }
                    } else {
                        layer3D.addLayer(get3DURI(cids3dBean), ProgressPanel.createProgressListeningDialog());
                    }
                } else {
                    LOG.warn("unsupported node type: " + dmtn);         // NOI18N
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   mo  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static CidsBean get3DBean(final MetaObject mo) {
        if (mo == null) {
            throw new IllegalArgumentException("metaobject must not be null"); // NOI18N
        }

        final MetaClass mc = mo.getMetaClass();

        assert mc != null : "mo without metaclass"; // NOI18N

        if (mc.getName().equalsIgnoreCase(CLASSNAME_CISMAP3DCONTENT)) {
            return mo.getBean();
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cids3dBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static URI get3DURI(final CidsBean cids3dBean) {
        if (cids3dBean == null) {
            throw new IllegalArgumentException("cids3dbean must not be null"); // NOI18N
        }

        final String uriString = (String)cids3dBean.getProperty("uri"); // NOI18N

        try {
            return new URI(uriString);
        } catch (URISyntaxException ex) {
            final String message = "illegal 3d bean uri format"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private Geometry getCurrent2DBBox() {
        final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();

        final XBoundingBox bbox;
        if (mc.getCurrentBoundingBox() instanceof XBoundingBox) {
            bbox = (XBoundingBox)mc.getCurrentBoundingBox();
        } else {
            throw new IllegalStateException("MappingComponent must be capable of sending XBoundingBox"); // NOI18N
        }

        return bbox.getGeometry();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ToBBoxListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            canvas3D.setBoundingBox(getCurrent2DBBox());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class FromBBoxListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            final XBoundingBox bbox = new XBoundingBox(canvas3D.getBoundingBox());
            mc.gotoBoundingBoxWithHistory(bbox);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class EastToWestListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (canvas3D != null) {
                final Geometry bbox = getCurrent2DBBox();
                canvas3D.setBoundingBox(bbox);
                final Point centroid = bbox.getCentroid();
                final Coordinate[] llur = SMSUtils.getLlAndUr(bbox);
                final Coordinate middleEast = new Coordinate(centroid.getX() + (centroid.getX() - llur[0].x),
                        centroid.getY());
                final Point mep = centroid.getFactory().createPoint(middleEast);
                canvas3D.setCameraPosition(mep);
                canvas3D.setCameraDirection(new Vector3d(-1d, 0d, -0.5d));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class WestToEastListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (canvas3D != null) {
                final Geometry bbox = getCurrent2DBBox();
                canvas3D.setBoundingBox(bbox);
                canvas3D.setCameraPosition(bbox.getCentroid());
                canvas3D.setCameraDirection(new Vector3d(1d, 0d, -0.5d));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class SouthToNorthListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (canvas3D != null) {
                final Geometry bbox = getCurrent2DBBox();
                canvas3D.setBoundingBox(bbox);
                canvas3D.setCameraPosition(bbox.getCentroid());
                canvas3D.setCameraDirection(new Vector3d(0d, 1d, -0.5d));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class NorthToSouthListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (canvas3D != null) {
                final Geometry bbox = getCurrent2DBBox();
                canvas3D.setBoundingBox(bbox);
                canvas3D.setCameraPosition(bbox.getCentroid());
                canvas3D.setCameraDirection(new Vector3d(0d, -1d, -0.5d));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CameraToggleListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            final FeatureCollection fc = mc.getFeatureCollection();

            boolean removed = false;
            for (final Feature f : fc.getAllFeatures()) {
                if (f.equals(camFeature)) {
                    fc.removeFeature(camFeature);
                    removed = true;
                    break;
                }
            }

            if (!removed) {
                fc.addFeature(camFeature);
                fc.holdFeature(camFeature);
                mc.gotoBoundingBoxWithHistory(new XBoundingBox(canvas3D.getBoundingBox()));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class FeatureRemovalListener extends FeatureCollectionAdapter {

        //~ Methods ------------------------------------------------------------

        @Override
        public void featuresAdded(final FeatureCollectionEvent fce) {
            for (final Feature feature : fce.getEventFeatures()) {
                if (feature instanceof CidsFeature) {
                    final CidsBean cids3dBean = get3DBean(((CidsFeature)feature).getMetaObject());

                    if (cids3dBean == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring feature addition, bean not 3d bean: " + feature);
                        }
                    } else {
                        // TODO: hack to not remove the visualisation from the 3d if the feature is held
                        fce.getFeatureCollection().holdFeature(feature);
                    }
                }
            }
        }

        @Override
        public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
            // TODO: alter to layer3d.removeall as soon as the implementation is changed to use layers instead of
            // objects.
            // don't call layer3d.removeall since this held features shall not be removed from 3d map
            featuresRemoved(fce);
        }

        @Override
        public void featuresRemoved(final FeatureCollectionEvent fce) {
            for (final Feature feature : fce.getEventFeatures()) {
                if (feature instanceof CidsFeature) {
                    final CidsBean cids3dBean = get3DBean(((CidsFeature)feature).getMetaObject());

                    if (cids3dBean == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring feature removal, bean not 3d bean: " + feature);
                        }
                    } else {
                        // TODO: hack to not remove the visualisation from the 3d if the feature is held
                        final FeatureCollection fc = fce.getFeatureCollection();

                        if (fc.isHoldFeature(feature)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("feature is held, not removing it");
                            }
                        } else {
                            final URI uri = get3DURI(cids3dBean);

                            layer3D.removeLayer(uri);
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class HomeListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (canvas3D != null) {
                canvas3D.home();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class InteractionModeChangeListener implements PropertyChangeListener {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(InteractionModeChangeListener.class);

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (MappingComponent.PROPERTY_MAP_INTERACTION_MODE.equals(evt.getPropertyName())) {
                if (MappingComponent.ZOOM.equals(evt.getNewValue())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("translating property change evt to zoom mode: " + evt); // NOI18N
                    }

                    canvas3D.setInteractionMode(Canvas3D.InteractionMode.ZOOM);
                } else if (MappingComponent.PAN.equals(evt.getNewValue())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("translating property change evt to pan mode: " + evt); // NOI18N
                    }

                    canvas3D.setInteractionMode(Canvas3D.InteractionMode.PAN);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("translating property change evt to rotate mode: " + evt); // NOI18N
                    }

                    canvas3D.setInteractionMode(Canvas3D.InteractionMode.ROTATE);
                }
            }
        }
    }
}