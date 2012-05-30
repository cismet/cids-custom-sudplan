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

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.MessageFormat;

import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.JComponent;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsClientToolbarItem;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsClientToolbarItem.class)
public class EmissionUploadWizardAction extends AbstractCidsBeanAction implements CidsClientToolbarItem {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TABLENAME_EMISSION_DATABASE = "EMISSION_DATABASE"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(EmissionUploadWizardAction.class);

    public static final transient String PROPERTY_GRIDS = "grids";             // NOI18N
    public static final transient String PROPERTY_NAME = "name";               // NOI18N
    public static final transient String PROPERTY_DESCRIPTION = "description"; // NOI18N
    public static final transient String PROPERTY_SRS = "srs";                 // NOI18N
    public static final transient String PROPERTY_URL = "url";                 // NOI18N
    public static final transient String PROPERTY_SUCCESSFUL = "successful";   // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmissionUploadWizardAction object.
     */
    public EmissionUploadWizardAction() {
        super(
            "",                                                                                 // NOI18N
            ImageUtilities.loadImageIcon(
                "de/cismet/cids/custom/sudplan/airquality/emissionupload/emissionUpload16.png", // NOI18N
                false));

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
        URL url = null;
        try {
            url = new URL("http://85.24.165.10/cgi-bin/sudplan_emscenario_add.cgi"); // NOI18N
        } catch (final MalformedURLException ex) {
            LOG.error("Given URL is invalid. Upload would fail.", ex);               // NOI18N
            // TODO: User feedback.
            return;
        }

        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.putProperty(PROPERTY_GRIDS, new LinkedList<Grid>());
        wizard.putProperty(PROPERTY_NAME, "");               // NOI18N
        wizard.putProperty(PROPERTY_DESCRIPTION, "");        // NOI18N
        wizard.putProperty(PROPERTY_SRS, CismapBroker.getInstance().getSrs());
        wizard.putProperty(PROPERTY_URL, url);
        wizard.putProperty(PROPERTY_SUCCESSFUL, Boolean.FALSE);
        wizard.setTitleFormat(new MessageFormat("{0}"));     // NOI18N
        wizard.setTitle(NbBundle.getMessage(
                EmissionUploadWizardAction.class,
                "EmissionUploadWizardAction.wizard.title")); // NOI18N

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
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
        } else if ((Boolean)wizard.getProperty(PROPERTY_SUCCESSFUL)) {
            try {
                final CidsBean emissionDatabase = createEmissionDatabase((String)wizard.getProperty(PROPERTY_NAME),
                        (String)wizard.getProperty(PROPERTY_DESCRIPTION),
                        null);
                emissionDatabase.persist();
            } catch (Exception ex) {
                LOG.error("Can't create or persist new CidsBean for emission database '"
                            + wizard.getProperty(PROPERTY_NAME) + "'.", // NOI18N
                    ex);
                // TODO: User feedback.
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   name         DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   geometry     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private CidsBean createEmissionDatabase(final String name, final String description, final Geometry geometry)
            throws IOException {
        if ((name == null) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty."); // NOI18N
        }

        final MetaClass metaClass = ClassCacheMultiple.getMetaClass(SessionManager.getSession().getUser().getDomain(),
                TABLENAME_EMISSION_DATABASE);

        try {
            final CidsBean cidsBean = metaClass.getEmptyInstance().getBean();

            cidsBean.setProperty("name", name);               // NOI18N
            cidsBean.setProperty("description", description); // NOI18N
            cidsBean.setProperty("geometry", geometry);       // NOI18N

            return cidsBean;
        } catch (final Exception e) {
            final String message = "Can't create CidsBean for emission database '" + name
                        + "' (Description: '"                               // NOI18N //NOI18N
                        + description + "', Geometry: '" + geometry + "'."; // NOI18N //NOI18N
            LOG.error(message, e);
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
