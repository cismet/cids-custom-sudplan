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

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CommonFeatureAction.class)
public final class AirqualityDownscalingWizardAction extends AbstractAction implements CommonFeatureAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SCENARIO = "__prop_scenario__";                  // NOI18N
    public static final String PROP_SCENARIOS = "__prop_scenarios__";                // NOI18N
    public static final String PROP_GRID_LOWERLEFT = "__prop_grid_lowerleft__";      // NOI18N
    public static final String PROP_GRID_UPPERRIGHT = "__prop_grid_upperright__";    // NOI18N
    public static final String PROP_GRIDCELL_SIZE = "__prop_grid_cell_size__";       // NOI18N
    public static final String PROP_GRIDCELL_COUNT_X = "__prop_grid_cell_count_x__"; // NOI18N
    public static final String PROP_GRIDCELL_COUNT_Y = "__prop_grid_cell_count_y__"; // NOI18N
    public static final String PROP_START_DATE = "__prop_start_date__";              // NOI18N
    public static final String PROP_END_DATE = "__prop_end_date__";                  // NOI18N //NOI18N
    public static final String PROP_DATABASE = "__prop_database__";
    public static final String PROP_DATABASES = "__prop_databases__";                // NOI18N
    public static final String PROP_NAME = "__prop_name__";                          // NOI18N
    public static final String PROP_DESCRIPTION = "__prop_description__";            // NOI18N

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    private transient Feature source;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingWizardAction object.
     */
    public AirqualityDownscalingWizardAction() {
        super(NbBundle.getMessage(
                AirqualityDownscalingWizardAction.class,
                "AirqualityDownscalingWizardAction.AirqualityDownscalingWizardAction().name")); // NOI18N
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
                    new AirqualityDownscalingWizardPanelGrid(),
                    new AirqualityDownscalingWizardPanelScenario(),
                    new AirqualityDownscalingWizardPanelDate(),
                    new AirqualityDownscalingWizardPanelDatabase(),
                    new AirqualityDownscalingWizardPanelMetadata()
                };
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

        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}"));                                         // NOI18N
        wizard.setTitle(NbBundle.getMessage(
                AirqualityDownscalingWizardAction.class,
                "AirqualityDownscalingWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N

        if (source instanceof GridFeature) {
            final GridFeature gridFeature = (GridFeature)source;
            wizard.putProperty(PROP_GRID_LOWERLEFT, gridFeature.getLowerleft());
            wizard.putProperty(PROP_GRID_UPPERRIGHT, gridFeature.getUpperright());
            wizard.putProperty(PROP_GRIDCELL_SIZE, gridFeature.getGridcellSize());
            wizard.putProperty(PROP_GRIDCELL_COUNT_X, gridFeature.getGridcellCountX());
            wizard.putProperty(PROP_GRIDCELL_COUNT_Y, gridFeature.getGridcellCountY());
        } else {
            final Coordinate[] lowerleftAndUpperright = SMSUtils.getLlAndUr(source.getGeometry());
            lowerleftAndUpperright[0].x = Math.floor(lowerleftAndUpperright[0].x);
            lowerleftAndUpperright[0].y = Math.floor(lowerleftAndUpperright[0].y);
            lowerleftAndUpperright[1].x = Math.floor(lowerleftAndUpperright[1].x);
            lowerleftAndUpperright[1].y = Math.floor(lowerleftAndUpperright[1].y);

            wizard.putProperty(PROP_GRID_LOWERLEFT, lowerleftAndUpperright[0]);
            wizard.putProperty(PROP_GRID_UPPERRIGHT, lowerleftAndUpperright[1]);
            wizard.putProperty(PROP_GRIDCELL_SIZE, Integer.valueOf(1000));
            wizard.putProperty(
                PROP_GRIDCELL_COUNT_X,
                Math.round(Math.floor((lowerleftAndUpperright[1].x - lowerleftAndUpperright[0].x) / 1000)));
            wizard.putProperty(
                PROP_GRIDCELL_COUNT_Y,
                Math.round(Math.floor((lowerleftAndUpperright[1].y - lowerleftAndUpperright[0].y) / 1000)));
        }

        try {
            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(source);
        } catch (final Exception ex) {
            LOG.warn("Could not remove source feature from mapping component.", ex); // NOI18N
        }

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
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
                final String message = "Cannot perform airquality downscaling."; // NOI18N
                LOG.error(message, ex);
                JOptionPane.showMessageDialog(ComponentRegistry.getRegistry().getMainWindow(),
                    message,
                    "Error",                                                     // NOI18N
                    JOptionPane.ERROR_MESSAGE);
            }
        }
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
        final String nameFromWizard = (String)wizard.getProperty(PROP_NAME);
        final String description = (String)wizard.getProperty(PROP_DESCRIPTION);
        final Date startDate = (Date)wizard.getProperty(PROP_START_DATE);
        final Date endDate = (Date)wizard.getProperty(PROP_END_DATE);
        final Coordinate lowerleft = (Coordinate)wizard.getProperty(PROP_GRID_LOWERLEFT);
        final Coordinate upperright = (Coordinate)wizard.getProperty(PROP_GRID_UPPERRIGHT);
        final Integer gridcellSize = (Integer)wizard.getProperty(PROP_GRIDCELL_SIZE);
        final Long gridcellCountX = (Long)wizard.getProperty(PROP_GRIDCELL_COUNT_X);
        final Long gridcellCountY = (Long)wizard.getProperty(PROP_GRIDCELL_COUNT_Y);
        final String database = (String)wizard.getProperty(PROP_DATABASE);

        assert scenario != null : "scenario was not set";       // NOI18N
        assert nameFromWizard != null : "wizname was not set";  // NOI18N
        assert description != null : "wizname was not set";     // NOI18N
        assert startDate != null : "startDate was not set";     // NOI18N
        assert endDate != null : "endDate was not set";         // NOI18N
        assert lowerleft != null : "llcoord was not set";       // NOI18N
        assert upperright != null : "urcoord was not set";      // NOI18N
        assert gridcellSize != null : "gridSize was not set";   // NOI18N
        assert gridcellCountX != null : "gridSize was not set"; // NOI18N
        assert gridcellCountY != null : "gridSize was not set"; // NOI18N
        assert database != null : "database was not set";       // NOI18N

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new airquality modelinput: scenario=" + scenario); // NOI18N //NOI18N
        }

        final Date created = GregorianCalendar.getInstance().getTime();
        final String user = SessionManager.getSession().getUser().getName();
        final String name = "Input of '" + nameFromWizard + "'"; // NOI18N

        // TODO: Remove hardwired SRS
        final AirqualityDownscalingInput input = new AirqualityDownscalingInput(
                created,
                user,
                nameFromWizard,
                description,
                scenario,
                startDate,
                endDate,
                lowerleft,
                upperright,
                gridcellSize,
                gridcellCountX,
                gridcellCountY,
                database,
                CismapBroker.getInstance().getSrs().getCode());

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
                        + " || description=" + description // NOI18N //NOI18N
                        + " || cidsbean=" + inputBean); // NOI18N //NOI18N //NOI18N
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

        final Crs srs = CismapBroker.getInstance().getSrs();
        // TODO: Dynamic SRID?!
        active = (srs != null) && (srs.isMetric());

        if (!active) {
            return active;
        }

        if (!(source instanceof GridFeature)) {
            final Geometry geometry = source.getGeometry();

            if (geometry == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No geometry defined to start airquality downscaling wizard."); // NOI18N
                }

                active = false;
            } else if (!geometry.isRectangle()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Airquality downscaling can only be performed on rectangles."); // NOI18N
                }

                active = false;
            } else {
                final Coordinate[] lowerleftAndUpperright = SMSUtils.getLlAndUr(geometry);
                final double width = lowerleftAndUpperright[1].x - lowerleftAndUpperright[0].x;
                final double height = lowerleftAndUpperright[1].y - lowerleftAndUpperright[0].y;

                // TODO: Rethink epsilon usage.
                if (((Math.abs(width) - 1000D) < 0.0001) || ((Math.abs(height) - 1000D) < 0.0001)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Airquality downscaling can only be performed on quadrangles which sides are at least 1000m long."); // NOI18N
                    }

                    active = false;
                }
            }
        }

        return active;
    }

    @Override
    public int getSorter() {
        return 9;
    }
}
