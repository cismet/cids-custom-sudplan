/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CommonFeatureAction.class)
public final class AirqualityDownscalingWizardAction extends AbstractAction implements CommonFeatureAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SCENARIO = "__prop_scenario__";       // NOI18N
    public static final String PROP_LL_COORD = "__prop_ll_coord__";       // NOI18N
    public static final String PROP_UR_COORD = "__prop_ur_coord__";       // NOI18N
    public static final String PROP_GRID_SIZE = "__prop_grid_size__";     // NOI18N
    public static final String PROP_START_DATE = "__prop_start_date__";   // NOI18N
    public static final String PROP_END_DATE = "__prop_end_date__";       // NOI18N
    public static final String PROP_DATABASES = "__prop_databases__";     // NOI18N
    public static final String PROP_NAME = "__prop_name__";               // NOI18N
    public static final String PROP_DESCRIPTION = "__prop_description__"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    private transient Feature source;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingWizardAction object.
     */
    public AirqualityDownscalingWizardAction() {
        super("Perform Airquality downscaling");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * EDT only !
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        assert EventQueue.isDispatchThread() : "can only be called from EDT"; // NOI18N

        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new AirqualityDownscalingWizardPanelGridSize(),
                    new AirqualityDownscalingWizardPanelBoundaries(),
                    new AirqualityDownscalingWizardPanelScenarios(),
                    new AirqualityDownscalingWizardPanelTargetDate(),
                    new AirqualityDownscalingWizardPanelDatabase(),
                    new AirqualityDownscalingWizardPanelMetadata()
                };
            final String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                final Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
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
        }

        return panels;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        assert source != null : "cannot perform action on empty source"; // NOI18N

        final Coordinate[] llUr = getLlUrCoordinates();

        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}"));                                         // NOI18N
        wizard.setTitle(NbBundle.getMessage(
                AirqualityDownscalingWizardAction.class,
                "AirqualityDownscalingWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N
        wizard.putProperty(PROP_LL_COORD, llUr[0]);
        wizard.putProperty(PROP_UR_COORD, llUr[1]);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            try {
                final CidsBean modelInput = createModelInput(wizard);
                CidsBean modelRun = createModelRun(wizard, modelInput);

                modelRun = modelRun.persist();

                SMSUtils.executeAndShowRun(modelRun);
            } catch (final Exception ex) {
                final String message = "cannot perform airquality downscaling";
                LOG.error(message, ex);
                JOptionPane.showMessageDialog(ComponentRegistry.getRegistry().getMainWindow(),
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Coordinate[] getLlUrCoordinates() {
        final Coordinate[] llUr = new Coordinate[2];

        Coordinate ll = source.getGeometry().getCoordinate();
        Coordinate ur = source.getGeometry().getCoordinate();

        for (final Coordinate candidate : source.getGeometry().getCoordinates()) {
            if ((candidate.x < ll.x) && (candidate.y < ll.y)) {
                ll = candidate;
            } else if ((candidate.x > ur.x) && (candidate.y > ur.y)) {
                ur = candidate;
            }
        }

        llUr[0] = ll;
        llUr[1] = ur;

        return llUr;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createModelInput(final WizardDescriptor wizard) throws IOException {
        final String scenario = (String)wizard.getProperty(PROP_SCENARIO);
        final String wizName = (String)wizard.getProperty(PROP_NAME);
        final Date startDate = (Date)wizard.getProperty(PROP_START_DATE);
        final Date endDate = (Date)wizard.getProperty(PROP_END_DATE);
        final Coordinate llCoord = (Coordinate)wizard.getProperty(PROP_LL_COORD);
        final Coordinate urCoord = (Coordinate)wizard.getProperty(PROP_UR_COORD);
        final Integer gridSize = (Integer)wizard.getProperty(PROP_GRID_SIZE);
        final Map<String, Set<Integer>> databases = (Map<String, Set<Integer>>)wizard.getProperty(PROP_DATABASES);

        assert scenario != null : "scenario was not set";    // NOI18N
        assert wizName != null : "wizname was not set";      // NOI18N
        assert startDate != null : "startDate was not set";  // NOI18N
        assert endDate != null : "endDate was not set";      // NOI18N
        assert llCoord != null : "llcoord was not set";      // NOI18N
        assert urCoord != null : "urcoord was not set";      // NOI18N
        assert gridSize != null : "gridSize was not set";    // NOI18N
        assert databases != null : "databases were not set"; // NOI18N

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new airquality modelinput: scenario=" + scenario); // NOI18N
        }

        final Date created = GregorianCalendar.getInstance().getTime();
        final String user = SessionManager.getSession().getUser().getName();
        final String name = "Airquality downscaling input (" + wizName + ")";

        final AirqualityDownscalingInput input = new AirqualityDownscalingInput(
                created,
                user,
                name,
                scenario,
                startDate,
                endDate,
                llCoord,
                urCoord,
                gridSize,
                databases);

        return SMSUtils.createModelInput(name, input, SMSUtils.Model.AQ_DS);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard     DOCUMENT ME!
     * @param   inputBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createModelRun(final WizardDescriptor wizard, final CidsBean inputBean) throws IOException {
        final String name = (String)wizard.getProperty(PROP_NAME);
        final String description = (String)wizard.getProperty(PROP_DESCRIPTION);

        assert name != null : "name was not set";               // NOI18N
        assert description != null : "description was not set"; // NOI18N

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new modelrun: " // NOI18N
                        + "name=" + name  // NOI18N
                        + " || description=" + description // NOI18N
                        + " || cidsbean=" + inputBean); // NOI18N
        }

        return SMSUtils.createModelRun(name, description, inputBean);
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
        assert source != null : "source must be set before requesting isActive"; // NOI18N

        boolean active;
        if (source instanceof CidsFeature) {
            active = false;
        } else {
            final Geometry geom = source.getGeometry();

            assert geom != null : "feature must have a geometry"; // NOI18N

            if (true) {                                           // geom.isRectangle()) {
                active = true;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("action only supports rectangles"); // NOI18N
                }

                active = false;
            }
        }

        return active;
    }

    @Override
    public int getSorter() {
        return 9;
    }
}
