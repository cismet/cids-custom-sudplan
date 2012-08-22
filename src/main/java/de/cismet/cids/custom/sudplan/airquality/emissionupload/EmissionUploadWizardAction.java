/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsClientToolbarItem;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.Converter;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsClientToolbarItem.class)
public class EmissionUploadWizardAction extends AbstractCidsBeanAction implements CidsClientToolbarItem {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EmissionUploadWizardAction.class);
    private static final transient ImageIcon ICON;

    public static final transient String TABLENAME_EMISSION_DATABASE = "EMISSION_DATABASE";           // NOI18N
    public static final transient String TABLENAME_EMISSION_DATABASE_GRID = "EMISSION_DATABASE_GRID"; // NOI18N

    public static final transient String PROPERTY_GRIDS = "grids";             // NOI18N
    public static final transient String PROPERTY_NAME = "name";               // NOI18N
    public static final transient String PROPERTY_DESCRIPTION = "description"; // NOI18N
    public static final transient String PROPERTY_SRS = "srs";                 // NOI18N
    public static final transient String PROPERTY_ACTION = "action";           // NOI18N
    public static final transient String PROPERTY_ACTION_UPLOAD = "upload";    // NOI18N
    public static final transient String PROPERTY_ACTION_SAVE = "save";        // NOI18N

    static {
        ImageIcon intermediateIcon = null;

        try {
            intermediateIcon = ImageUtilities.loadImageIcon(
                    "de/cismet/cids/custom/sudplan/airquality/emissionupload/emissionUpload16.png", // NOI18N
                    false);
        } catch (final Exception ex) {
            LOG.warn(
                "The icon 'de/cismet/cids/custom/sudplan/airquality/emissionupload/emissionUpload16.png' can not be loaded.",
                ex);
        }

        if (intermediateIcon != null) {
            ICON = intermediateIcon;
        } else {
            ICON = new ImageIcon();
        }
    }

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmissionUploadWizardAction object.
     */
    public EmissionUploadWizardAction() {
        super("", ICON);

        putValue(
            Action.SHORT_DESCRIPTION,
            NbBundle.getMessage(EmissionUploadWizardAction.class, "EmissionUploadWizardAction.shortDescription"));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        assert EventQueue.isDispatchThread() : "can only be called from EDT"; // NOI18N

        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new EmissionUploadPanelGrids(),
                    new EmissionUploadPanelEmissionScenario(),
                    new EmissionUploadPanelUpload()
                };

            final String[] stepTitles = new String[panels.length];

            for (int i = 0; i < panels.length; i++) {
                final Component component = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                stepTitles[i] = component.getName();

                if (component instanceof JComponent) {
                    // assume Swing components
                    final JComponent jComponent = (JComponent)component;
                    // Sets step number of a component
                    jComponent.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(i));
                    // Sets steps names for a panel
                    jComponent.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, stepTitles);
                    // Turn on subtitle creation on each step
                    jComponent.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jComponent.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jComponent.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }

        return panels;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.putProperty(PROPERTY_GRIDS, new LinkedList<Grid>());
        wizard.putProperty(PROPERTY_NAME, "");               // NOI18N
        wizard.putProperty(PROPERTY_DESCRIPTION, "");        // NOI18N
        wizard.putProperty(PROPERTY_SRS, CismapBroker.getInstance().getSrs());
        wizard.putProperty(PROPERTY_ACTION, PROPERTY_ACTION_UPLOAD);
        wizard.setTitleFormat(new MessageFormat("{0}"));     // NOI18N
        wizard.setTitle(NbBundle.getMessage(
                EmissionUploadWizardAction.class,
                "EmissionUploadWizardAction.wizard.title")); // NOI18N

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.setIconImage(ICON.getImage());
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;

        if (cancelled) {
            for (final Object o : getPanels()) {
                if (o instanceof Cancellable) {
                    ((Cancellable)o).cancel();
                }
            }

            return;
        }

        final CidsBean emissionDatabase;

        try {
            emissionDatabase = createEmissionDatabase((String)wizard.getProperty(PROPERTY_NAME),
                    (String)wizard.getProperty(PROPERTY_DESCRIPTION),
                    ((Crs)wizard.getProperty(PROPERTY_SRS)).getCode(),
                    null,
                    (List<Grid>)wizard.getProperty(PROPERTY_GRIDS));

            emissionDatabase.persist();
        } catch (final Exception ex) {
            final String errorMessage = "Can't create or persist new CidsBean for emission database '"
                        + wizard.getProperty(PROPERTY_NAME) + "'.";

            LOG.error(errorMessage, // NOI18N
                ex);

            try {
                final ErrorInfo errorInfo = new ErrorInfo(
                        "Error",
                        "Couldn't save the emission database.",
                        errorMessage,
                        "ERROR",
                        ex,
                        Level.SEVERE,
                        null);

                EventQueue.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            JXErrorPane.showDialog(ComponentRegistry.getRegistry().getMainWindow(), errorInfo);
                        }
                    });
            } catch (final Exception ex1) {
                LOG.error("Can't display error dialog", ex1); // NOI18N
            }

            return;
        }

        if (PROPERTY_ACTION_UPLOAD.equals(wizard.getProperty(PROPERTY_ACTION))) {
            final EmissionUploadDialog uploadDialog = new EmissionUploadDialog(ComponentRegistry.getRegistry()
                            .getMainWindow(),
                    emissionDatabase);
            uploadDialog.pack();
            StaticSwingTools.showDialog(uploadDialog);
            uploadDialog.toFront();
        }
    }

    /**
     * Creates a new CidsBean from given emission database. The returned CidsBean isn't persisted.
     *
     * @param   name         Name of emission database.
     * @param   description  Description of emission database.
     * @param   srs          SRS of emission database (e. g. "EPSG:3021").
     * @param   geometry     Name of emission database.
     * @param   grids        Emission grids.
     *
     * @return  A CidsBean (table "emission_database") for the given emission database.
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  Exception                 DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private CidsBean createEmissionDatabase(final String name,
            final String description,
            final String srs,
            final Geometry geometry,
            final List<Grid> grids) throws IOException, Exception {
        if ((name == null) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty."); // NOI18N
        }

        final MetaClass metaClassEmissionDatabase;
        final MetaClass metaClassEmissionDatabaseGrid;

        try {
            metaClassEmissionDatabase = ClassCacheMultiple.getMetaClass(SessionManager.getSession().getUser()
                            .getDomain(),
                    TABLENAME_EMISSION_DATABASE);
            metaClassEmissionDatabaseGrid = ClassCacheMultiple.getMetaClass(SessionManager.getSession().getUser()
                            .getDomain(),
                    TABLENAME_EMISSION_DATABASE_GRID);
        } catch (final Exception ex) {
            throw new Exception("The meta classes can't be retrieved.", ex);
        }

        try {
            final CidsBean cidsBean = metaClassEmissionDatabase.getEmptyInstance().getBean();
            final List<CidsBean> emissonGrids = cidsBean.getBeanCollectionProperty("grids");

            for (final Grid grid : grids) {
                final CidsBean cidsBeanGrid = metaClassEmissionDatabaseGrid.getEmptyInstance().getBean();

                cidsBeanGrid.setProperty("name", grid.getGridName());
                cidsBeanGrid.setProperty("substance", grid.getSubstance().getRepresentationFile());
                cidsBeanGrid.setProperty("timevariation", grid.getTimeVariation().getRepresentationFile());
                cidsBeanGrid.setProperty("height", grid.getGridHeight().getRepresentationFile());
                cidsBeanGrid.setProperty("grid", Converter.toString(EmissionUpload.read(grid.getEmissionGrid())));
                cidsBeanGrid.setProperty(
                    "customtimevariation",
                    Converter.toString(EmissionUpload.read(grid.getCustomTimeVariation())));

                emissonGrids.add(cidsBeanGrid);
            }

            cidsBean.setProperty("name", name);                                             // NOI18N
            cidsBean.setProperty("description", description);                               // NOI18N
            cidsBean.setProperty("srs", srs);                                               // NOI18N
            cidsBean.setProperty("geometry", geometry);                                     // NOI18N
            cidsBean.setProperty("uploaded", Boolean.FALSE);                                // NOI18N
            cidsBean.setProperty("file", Converter.toString(EmissionUpload.zip(cidsBean))); // NOI18N

            return cidsBean;
        } catch (final Exception e) {
            final String message = "Can't create CidsBean for emission database '" + name
                        + "' (Description: '"                               // NOI18N //NOI18N
                        + description + "', Geometry: '" + geometry + "'."; // NOI18N //NOI18N
            throw new IOException(message, e);
        }
    }

    @Override
    public String getSorterString() {
        return "emissionupload"; // NOI18N
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
