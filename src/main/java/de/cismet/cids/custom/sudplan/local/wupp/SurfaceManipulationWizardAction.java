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
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.tree.MetaCatalogueTree;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.lookup.ServiceProvider;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.math.BigDecimal;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.cismet.cids.custom.sudplan.DeltaSurfaceConflictPanel;
import de.cismet.cids.custom.sudplan.DeltaSurfacePreparePanel;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.featureinfowidget.InitialisationException;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CommonFeatureAction.class)
public class SurfaceManipulationWizardAction extends AbstractAction implements CommonFeatureAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_DELTA_SURFACE_HEIGHT = "__prop_delta_surface_height__";
    public static final String PROP_DELTA_SURFACE_TYPE = "__prop_delta_surface_type__";
    public static final String PROP_DELTA_SURFACE_NAME = "__prop_delta_surface_name__";
    public static final String PROP_DELTA_SURFACE_DESCRIPTION = "__prop_delta_surface_description__";
    public static final String PROP_INITIAL_CONFIG = "__prop_initial_config__";
    public static final String PROP_DELTA_CONFIG_IS_NEW = "__prop_delta_config_is_new__";
    public static final String PROP_DELTA_CONFIG = "__prop_delta_config_id__";
    public static final String PROP_DELTA_CONFIG_NAME = "__prop_delta_config_name__";
    public static final String PROP_DELTA_CONFIG_DESCRIPTION = "__prop_delta_config_description__";
    public static final String PROP_CONFIG_SELECTION_CHANGED = "__prop_config_selection_changed__";
    public static final String PROP_ADD_DELTA_SURFACE = "__prop_add_delta_surface__";

    private static final int MAX_COUNT_CONFLICTS_TO_WARN_USER = 30;

    private static final Logger LOG = Logger.getLogger(SurfaceManipulationWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient Feature source;
    private transient WizardDescriptor.Panel[] panels;
    private transient MetaObject[] geoCPMConfigurations;
    private transient MetaObject[] overlappingSurfaces;
    private transient MetaObject[] geoCPMBreakingEdges;
    private transient CidsBean deltaSurfaceToAdd;
    private transient boolean addToConfiguration = false;
    private transient boolean isSurfaceConflict;
    private transient boolean isBreakingedgeConflict;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SurfaceManipulationWizardAction object.
     */
    public SurfaceManipulationWizardAction() {
        super("Manipulate Surface");
    }

    /**
     * Creates a new SurfaceManipulationWizardAction object.
     *
     * @param  deltaSurface  DOCUMENT ME!
     */
    public SurfaceManipulationWizardAction(final CidsBean deltaSurface) {
        this();
        this.deltaSurfaceToAdd = deltaSurface;
        this.addToConfiguration = true;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new SurfaceManipulationWizardPanelHeight(),
                    new SurfaceManipulationWizardPanelMetadataSurface(),
                    new SurfaceManipulationWizardPanelConfigSelection(),
                    new SurfaceManipulationWizardPanelMetadataConfig()
                };
        }

        final String[] steps = new String[panels.length];
        for (int i = 0; i < panels.length; i++) {
            final Component c = panels[i].getComponent();
            // Default step name to component name of panel. Mainly useful for getting the name of the target
            // chooser to appear in the list of steps.
            steps[i] = c.getName();
            if (c instanceof JComponent) {
                // assume Swing components
                final JComponent jc = (JComponent)c;
                // Sets step number of a component
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(i));
                // Sets steps names for a panel
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                // Turn on subtitle creation on each step
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                // Show steps on the left side with the image on the
                // background
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                // Turn on numbering of all steps
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            }
        }
        return panels;
    }

    @Override
    public void setSourceFeature(final Feature source) {
        this.source = source;
    }

    @Override
    public Feature getSourceFeature() {
        return source;
    }

    @Override
    public boolean isActive() {
        assert source != null : "source must be set before requesting isActive";
        final boolean active;
        final Crs srs = CismapBroker.getInstance().getSrs();

        putValue(AbstractAction.SHORT_DESCRIPTION, "");

        active = (srs != null) && (srs.getName() != null) && srs.getName().endsWith(":31466"); // NOI18N

        if (!active) {
            return active;
        }

        if (!(source instanceof PureNewFeature)) {
            return false;
        }

        final PureNewFeature selectedSurfaceArea = (PureNewFeature)source;
        if (!(selectedSurfaceArea.getGeometryType() == PureNewFeature.geomTypes.POLYGON)) {
            return false;
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    private void addFeaturesToMap() {
        final Collection<CidsFeature> cidsFeatures = new ArrayList<CidsFeature>();
        for (final MetaObject mo : overlappingSurfaces) {
            cidsFeatures.add(new CidsFeature(mo));
        }
        for (final MetaObject mo : geoCPMBreakingEdges) {
            cidsFeatures.add(new CidsFeature(mo));
        }
        SudplanConcurrency.getSudplanGeneralPurposePool().submit(new Runnable() {

                @Override
                public void run() {
                    CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeatures(cidsFeatures);
                }
            });
    }

    @Override
    public int getSorter() {
        return 9;
    }

    @Override
    public void actionPerformed(final ActionEvent ae) {
        if (!addToConfiguration) {
            assert source != null : "cannot perform action on empty source"; // NOI18N
        }

        // Set all global search results to null because new action is performing
        geoCPMConfigurations = null;
        overlappingSurfaces = null;
        geoCPMBreakingEdges = null;

        if (!startConflictSearch()) {
            return;
        }

        isSurfaceConflict = (overlappingSurfaces != null) && (overlappingSurfaces.length > 0);
        isBreakingedgeConflict = (geoCPMBreakingEdges != null) && (geoCPMBreakingEdges.length > 0);

        if (isSurfaceConflict || isBreakingedgeConflict) {
            startConflictDialog();
        } else {
            startWizardDialog();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean startConflictSearch() {
        final DeltaSurfacePreparePanel waitPanel = new DeltaSurfacePreparePanel(3);

        final JOptionPane preparePane = new JOptionPane(
                waitPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.CANCEL_OPTION,
                null,
                new Object[] { "Cancel" });

        final JDialog prepareDialog = preparePane.createDialog(ComponentRegistry.getRegistry().getMainWindow(),
                "Prepare wizard");

        final Future<Boolean> prepareWizardTask = SudplanConcurrency.getSudplanGeneralPurposePool()
                    .submit(new Callable<Boolean>() {

                            @Override
                            public Boolean call() {
                                try {
                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("prepare wizard dialog was interrupted"); // NOI18N
                                        }
                                        return false;
                                    }

                                    waitPanel.setProgressText("Search for initial configuration...");

                                    geoCPMConfigurations = searchGeometry(
                                            source.getGeometry(),
                                            SMSUtils.TABLENAME_GEOCPM_CONFIGURATION);

                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug(
                                                "prepare wizard dialog was interrupted after geoCPM-config search"); // NOI18N
                                        }
                                        return false;
                                    }

                                    try {
                                        if ((geoCPMConfigurations == null) || (geoCPMConfigurations.length <= 0)) {
                                            final ErrorInfo errorInfo = new ErrorInfo(
                                                    "Prepare wizard error",
                                                    "Error while preparing wizard",
                                                    "The wizard could not prepared because there is no Digital Surface Model defined",
                                                    "ERROR",
                                                    null,
                                                    Level.SEVERE,
                                                    null);
                                            EventQueue.invokeAndWait(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        JXErrorPane.showDialog(prepareDialog, errorInfo);
                                                    }
                                                });

                                            return false;
                                        } else if (geoCPMConfigurations.length > 1) {
                                            final ErrorInfo errorInfo = new ErrorInfo(
                                                    "Prepare wizard error",
                                                    "Error while preparing wizard",
                                                    "The wizard could not prepared because there are several Digital Surface Model available",
                                                    "ERROR",
                                                    null,
                                                    Level.SEVERE,
                                                    null);
                                            EventQueue.invokeAndWait(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        JXErrorPane.showDialog(prepareDialog, errorInfo);
                                                    }
                                                });
                                            return false;
                                        }
                                    } catch (Exception ex) {
                                        LOG.error("Cannot show geoCPMConfiguration error", ex);
                                        return false;
                                    }

                                    waitPanel.setProgressValue(1);
                                    waitPanel.setProgressText("Search for overlapping surfaces...");

                                    overlappingSurfaces = searchGeometry(
                                            source.getGeometry(),
                                            SMSUtils.TABLENAME_DELTA_SURFACE);

                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("prepare wizard dialog was interrupted after surface search"); // NOI18N
                                        }
                                        return false;
                                    }

                                    waitPanel.setProgressValue(2);
                                    waitPanel.setProgressText("Search for overlapping breakingedges...");

                                    geoCPMBreakingEdges = searchGeometry(
                                            source.getGeometry(),
                                            SMSUtils.TABLENAME_GEOCPM_BREAKING_EDGE);

                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug(
                                                "prepare wizard dialog was interrupted after breakingedge search"); // NOI18N
                                        }
                                        return false;
                                    }

                                    waitPanel.setProgressValue(3);
                                    waitPanel.setProgressText("Search done");

                                    return true;
                                } catch (final Exception e) {
                                    try {
                                        LOG.error("Cannot prepare the wizard", e);
                                        final ErrorInfo errorInfo = new ErrorInfo(
                                                "Prepare wizard error",
                                                "Error while preparing wizard",
                                                "The wizard could not prepared",
                                                "ERROR",
                                                e,
                                                Level.SEVERE,
                                                null);
                                        EventQueue.invokeAndWait(new Runnable() {

                                                @Override
                                                public void run() {
                                                    JXErrorPane.showDialog(prepareDialog, errorInfo);
                                                }
                                            });
                                    } catch (Exception e1) {
                                        LOG.error("Cannot show error dialog", e1);
                                    } finally {
                                        return false;
                                    }
                                } finally {
                                    EventQueue.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                prepareDialog.setVisible(false);
                                            }
                                        });
                                }
                            }
                        });

        prepareDialog.setVisible(true);

        final Object prepareReturn = preparePane.getValue();
        // Wenn der Wizard normal beendet
        if (prepareReturn.equals(JOptionPane.UNINITIALIZED_VALUE)) {
            try {
                return prepareWizardTask.get().booleanValue();
            } catch (Exception ex) {
                LOG.error("cannot read prepareWizardTask value", ex);
                return false;
            }
            // Wenn die Suche abgebrochen wird
        } else {
            if (!prepareWizardTask.isDone()) {
                if (!prepareWizardTask.cancel(true)) {
                    LOG.warn("cannot cancel prepare wizard task"); // NOI18N
                }
            }
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void startConflictDialog() {
        try {
            final int surfaceCount = (overlappingSurfaces != null) ? overlappingSurfaces.length : 0;
            final int edgesCount = (geoCPMBreakingEdges != null) ? geoCPMBreakingEdges.length : 0;

            final String strShowConflicts = "Show conflicts";
            final String strStartWizard = "Start wizard";
            final String strCancel = "Cancel";

            final JButton startWizardButton = new JButton(strStartWizard);

            final DeltaSurfaceConflictPanel conflictPanel = new DeltaSurfaceConflictPanel(
                    isSurfaceConflict,
                    isBreakingedgeConflict,
                    surfaceCount,
                    edgesCount);

            final Object[] buttons = new Object[] {
                    strShowConflicts,
                    startWizardButton,
                    strCancel
                };

            final JOptionPane conflictPane = new JOptionPane(
                    conflictPanel,
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    buttons);

            final JDialog conflictDialog = conflictPane.createDialog(
                    ComponentRegistry.getRegistry().getMainWindow(),
                    "Conflict are found");

            startWizardButton.addActionListener(new WizardButtonAction(conflictPane));
            startWizardButton.setEnabled(!isBreakingedgeConflict);

            conflictDialog.setVisible(true);

            final Object conflictReturn = conflictPane.getValue();

            if ((conflictReturn != null) && (conflictReturn.equals(strShowConflicts))) {
                if ((surfaceCount + edgesCount) > MAX_COUNT_CONFLICTS_TO_WARN_USER) {
                    final String message = "There are more than " + (surfaceCount + edgesCount)
                                + " objects to show! Press the ok button to continue";
                    final JOptionPane infoMessagePane = new JOptionPane(
                            message,
                            JOptionPane.QUESTION_MESSAGE,
                            JOptionPane.OK_CANCEL_OPTION);
                    final JDialog infoMessageDialog = infoMessagePane.createDialog(ComponentRegistry.getRegistry()
                                    .getMainWindow(),
                            "Confirm to show conflicts");
                    infoMessageDialog.setVisible(true);
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                infoMessageDialog.setVisible(false);
                            }
                        });

                    if (infoMessagePane.getValue().equals(JOptionPane.OK_OPTION)) {
                        addFeaturesToMap();
                    }
                } else {
                    addFeaturesToMap();
                }
            } else if ((conflictReturn != null) && (conflictReturn.equals(strStartWizard))) {
                startWizardDialog();
            }
        } catch (final Exception ex) {
            try {
                EventQueue.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(
                                CismapBroker.getInstance().getMappingComponent(),
                                "Cannot start the conflict dialog",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                            LOG.error("Cannot start the conflict dialog", ex);
                        }
                    });
            } catch (final Exception ex1) {
                LOG.error("cannot display error dialog", ex1);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void startWizardDialog() {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}")); // NOI18N
        wizard.setTitle("Digital Surface Model manipulation");
        if (addToConfiguration) {
            final CidsBean initConfig = (CidsBean)deltaSurfaceToAdd.getProperty(
                    "delta_configuration.original_object");
            wizard.putProperty(PROP_INITIAL_CONFIG, initConfig);
            wizard.putProperty(PROP_DELTA_SURFACE_NAME, (String)deltaSurfaceToAdd.getProperty("name"));
            wizard.putProperty(
                PROP_DELTA_SURFACE_DESCRIPTION,
                (String)deltaSurfaceToAdd.getProperty("description"));
            wizard.putProperty(
                PROP_DELTA_SURFACE_HEIGHT,
                ((BigDecimal)deltaSurfaceToAdd.getProperty("height")).doubleValue());
            wizard.putProperty(
                PROP_DELTA_SURFACE_TYPE,
                (Boolean)deltaSurfaceToAdd.getProperty("sea_type"));
            wizard.putProperty(PROP_ADD_DELTA_SURFACE, deltaSurfaceToAdd);
        } else {
            wizard.putProperty(PROP_INITIAL_CONFIG, geoCPMConfigurations[0].getBean());
            wizard.putProperty(PROP_ADD_DELTA_SURFACE, null);
        }

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean finished = wizard.getValue() == WizardDescriptor.FINISH_OPTION;
        if (finished) {
            try {
                // Step 1: create new configuration or alter the description and save it
                CidsBean deltaConfiguration = createDeltaConfiguration(wizard);
                deltaConfiguration = deltaConfiguration.persist();

                DeltaConfigurationListWidged.getInstance().fireConfigsChanged();

                // Step 2: create new delta surface and save it
                CidsBean newDeltaSurface = createDeltaSurface(wizard, deltaConfiguration);
                newDeltaSurface = newDeltaSurface.persist();

                // Step 3: update Map with new delta_surface and update catalogue tree
                if (!addToConfiguration) {
                    updateMappingComponent(newDeltaSurface);
                    updateCatalogueTree();
                }

                // Step 4: show result in DescriptionPane
                final ComponentRegistry reg = ComponentRegistry.getRegistry();
                reg.getDescriptionPane().gotoMetaObject(newDeltaSurface.getMetaObject(), null);
            } catch (final Exception ex) {
                final String message = "Cannot save the surface manipulation.";
                LOG.error(message, ex);
                JOptionPane.showMessageDialog(
                    ComponentRegistry.getRegistry().getMainWindow(),
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                addToConfiguration = false;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard              DOCUMENT ME!
     * @param   deltaConfiguration  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception                DOCUMENT ME!
     * @throws  InitialisationException  DOCUMENT ME!
     */
    private CidsBean createDeltaSurface(final WizardDescriptor wizard,
            final CidsBean deltaConfiguration) throws Exception {
        final String name = (String)wizard.getProperty(PROP_DELTA_SURFACE_NAME);
        final String desc = (String)wizard.getProperty(PROP_DELTA_SURFACE_DESCRIPTION);
        final boolean isSeaType = (Boolean)wizard.getProperty(PROP_DELTA_SURFACE_TYPE);
        final double dHeight = (Double)wizard.getProperty(PROP_DELTA_SURFACE_HEIGHT);
        final BigDecimal height = new BigDecimal(dHeight);

        final Geometry geom;
        final Geometry originalgeom;

        if (addToConfiguration) {
            geom = CrsTransformer.transformToDefaultCrs((Geometry)deltaSurfaceToAdd.getProperty("geom.geo_field"));
            originalgeom = (Geometry)deltaSurfaceToAdd.getProperty("original_geom");
        } else {
            geom = CrsTransformer.transformToDefaultCrs(source.getGeometry());
            originalgeom = source.getGeometry();
        }
        geom.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());

        final CidsBean geomBean;
        final MetaClass metaClass = ClassCacheMultiple.getMetaClass(SMSUtils.DOMAIN_SUDPLAN_WUPP, "geom");
        if (metaClass != null) {
            geomBean = metaClass.getEmptyInstance().getBean();
            geomBean.setProperty("geo_field", geom);
            geomBean.persist();
        } else {
            throw new InitialisationException("Cannot initial 'GEOM' MetaClass to store a geometry from delta_surface");
        }

        final CidsBean deltaSurface = CidsBean.createNewCidsBeanFromTableName(
                SMSUtils.DOMAIN_SUDPLAN_WUPP,
                SMSUtils.TABLENAME_DELTA_SURFACE);

        deltaSurface.setProperty("name", name);
        deltaSurface.setProperty("description", desc);
        deltaSurface.setProperty("sea_type", isSeaType);
        deltaSurface.setProperty("height", height);
        deltaSurface.setProperty("delta_configuration", deltaConfiguration);
        deltaSurface.setProperty("original_geom", originalgeom);
        deltaSurface.setProperty("geom", geomBean);

        return deltaSurface;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean createDeltaConfiguration(final WizardDescriptor wizard) throws Exception {
        final CidsBean selectedConfig = (CidsBean)wizard.getProperty(PROP_DELTA_CONFIG);
        final boolean isNew = (Boolean)wizard.getProperty(PROP_DELTA_CONFIG_IS_NEW);
        final String desc = (String)wizard.getProperty(PROP_DELTA_CONFIG_DESCRIPTION);

        if (isNew) {
            final String name = (String)wizard.getProperty(PROP_DELTA_CONFIG_NAME);

            final CidsBean newDeltaConfig = CidsBean.createNewCidsBeanFromTableName(
                    SMSUtils.DOMAIN_SUDPLAN_WUPP,
                    SMSUtils.TABLENAME_DELTA_CONFIGURATION);
            newDeltaConfig.setProperty("name", name);
            newDeltaConfig.setProperty("description", desc);
            newDeltaConfig.setProperty("locked", false);
//            newDeltaConfig.setProperty("delta_breaking_edges", null);
            newDeltaConfig.setProperty("original_object", selectedConfig);

            return newDeltaConfig;
        } else {
            selectedConfig.setProperty("description", desc);

            return selectedConfig;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry   defaultGeometry DOCUMENT ME!
     * @param   tableName  searchForBreakingEdges DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MetaObject[] searchGeometry(final Geometry geometry,
            final String tableName) {
        MetaClass MC = ClassCacheMultiple.getMetaClass(
                SMSUtils.DOMAIN_SUDPLAN_WUPP,
                tableName);

        if (MC == null) {
            MC = ClassCacheMultiple.getMetaClass(SessionManager.getSession().getUser().getDomain(), tableName);
        }

        final Geometry defaultGeometry = CrsTransformer.transformToDefaultCrs(geometry);

        String query = "select " + MC.getID() + ", m." + MC.getPrimaryKey() + " from " + MC.getTableName(); // NOI18N
        query += " m, geom";                                                                                // NOI18N
        query += " WHERE m.geom = geom.id";
        query += " AND geom.geo_field && '" + defaultGeometry + "'";
        query += " AND st_intersects(geom.geo_field,'" + defaultGeometry + "')";

        try {
            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                            query,
                            SMSUtils.DOMAIN_SUDPLAN_WUPP);
            return metaObjects;
        } catch (ConnectionException ex) {
//            try {
//                final MetaObject[] metaObjects = SessionManager.getProxy()
//                            .getMetaObjectByQuery(SessionManager.getSession().getUser(),
//                                query,
//                                SessionManager.getSession().getUser().getDomain());
//                return metaObjects;
//            } catch (ConnectionException cex) {
            LOG.error("Can't connect to domain " + SMSUtils.DOMAIN_SUDPLAN_WUPP, ex);
            return null;
//            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateCatalogueTree() {
        final MetaCatalogueTree catalogueTree = ComponentRegistry.getRegistry().getCatalogueTree();
        final DefaultTreeModel catalogueTreeModel = (DefaultTreeModel)catalogueTree.getModel();
        final Enumeration<TreePath> expandedPaths = catalogueTree.getExpandedDescendants(new TreePath(
                    catalogueTreeModel.getRoot()));
        TreePath selectionPath = catalogueTree.getSelectionPath();

        final RootTreeNode rootTreeNode;
        try {
            rootTreeNode = new RootTreeNode(SessionManager.getProxy().getRoots());
        } catch (ConnectionException ex) {
            LOG.error("Updating catalogue tree after successful insertion of 'delta_surface' entity failed.", ex);
            return;
        }

        catalogueTreeModel.setRoot(rootTreeNode);
        catalogueTreeModel.reload();

        if (selectionPath == null) {
            while (expandedPaths.hasMoreElements()) {
                final TreePath expandedPath = expandedPaths.nextElement();
                if ((selectionPath == null) || (selectionPath.getPathCount() < selectionPath.getPathCount())) {
                    selectionPath = expandedPath;
                }
            }
        }
        catalogueTree.exploreSubtree(selectionPath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   deltaSurface  persistedHint DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private void updateMappingComponent(final CidsBean deltaSurface) throws IllegalArgumentException {
        final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
        mappingComponent.getFeatureCollection().removeFeature(source);
        mappingComponent.getFeatureCollection().addFeature(new CidsFeature(deltaSurface.getMetaObject()));
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$ //
     */
    class WizardButtonAction implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        private final JOptionPane optionPane;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new WizardButtonAction object.
         *
         * @param  pane  DOCUMENT ME!
         */
        public WizardButtonAction(final JOptionPane pane) {
            this.optionPane = pane;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent ae) {
            if (this.optionPane != null) {
                this.optionPane.setValue(((JButton)ae.getSource()).getText());
            }
        }
    }
}
