/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.deegree.datatypes.QualifiedName;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.StringWriter;

import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class AssignTimeseriesWizardAction extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SELECTED_CALIBRATION_INPUT = "__prop_current_calibration__"; // NOI18N
    public static final String PROP_BASIN_ID = "__prop_basin_id__";                              // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(AssignTimeseriesWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    /** the feature where the action is performed. */
    private final transient org.deegree.model.feature.Feature areaFeature;
    private final transient QualifiedName catchmentIdName;

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AssignTimeseriesWizardAction object.
     *
     * @param  areaFeature      DOCUMENT ME!
     * @param  catchmentIdName  DOCUMENT ME!
     */
    public AssignTimeseriesWizardAction(final org.deegree.model.feature.Feature areaFeature,
            final QualifiedName catchmentIdName) {
        super("Assign Timeseries"); // NOI18N

        this.areaFeature = areaFeature;
        this.catchmentIdName = catchmentIdName;
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
                    new AssignTimeseriesWizardPanelSelectCalibration(),
                    new AssignTimeseriesWizardPanelSelectTS()
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
     * @param   e  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object value = WFSUtils.getFeaturePropertyValue(areaFeature, catchmentIdName);

        final int subId;
        // the id is an integer, but the property value is a string
        if (value instanceof String) {
            subId = Integer.valueOf((String)value);
        } else {
            throw new IllegalStateException("property value not instanceof string: " + value); // NOI18N
        }

        actionPerformed(subId);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   subId  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    protected void actionPerformed(final int subId) {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}")); // NOI18N
        wizard.setTitle("Assign Time Series");           // NOI18N

        final CidsBean currWorkspace = HydrologyCache.getInstance().getCurrentWorkspace();
        if (currWorkspace != null) {
            wizard.putProperty(PROP_SELECTED_CALIBRATION_INPUT, currWorkspace.getProperty("calibration.modelinput")); // NOI18N
        }

        wizard.putProperty(PROP_BASIN_ID, subId);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final CidsBean selectedCalInput = (CidsBean)wizard.getProperty(PROP_SELECTED_CALIBRATION_INPUT);
            final CidsBean selectedTimeseries = (CidsBean)wizard.getProperty(
                    AssignTimeseriesWizardPanelSelectTS.PROP_SELECTED_TS);

            final CalibrationInputManager cim = new CalibrationInputManager();
            cim.setCidsBean(selectedCalInput);

            final CalibrationInput ci;
            try {
                ci = cim.getUR();
                ci.putTimeseries(subId, selectedTimeseries.getMetaObject().getID());

                final StringWriter writer = new StringWriter();
                final ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(writer, ci);

                // the dialog is modal, so we don't have to care about concurrent modification, unless some background
                // thread does it for no apparent reason, to cut a long story short: mark theworkspace for reload to
                // ensure freshness, if it is still the selected one
                boolean isCurrentWorkspace = false;
                if ((currWorkspace != null)
                            && selectedCalInput.equals(currWorkspace.getProperty("calibration.modelinput"))) { // NOI18N
                    isCurrentWorkspace = true;
                }

                selectedCalInput.setProperty("ur", writer.toString()); // NOI18N
                selectedCalInput.persist();

                if (isCurrentWorkspace) {
                    HydrologyCache.getInstance().reloadCurrentWorkspace();
                } else {
                    final CidsBean hwBean = HydrologyCache.getInstance().getWorkspaceFromCalInput(selectedCalInput);
                    HydrologyCache.getInstance().setCurrentWorkspace(hwBean);
                }

                ComponentRegistry.getRegistry()
                        .getCatalogueTree()
                        .requestRefreshNode("hydrology.localmodel." // NOI18N
                            + HydrologyCache.getInstance().getCurrentWorkspace().getMetaObject().getID()
                            + ".calibration"); // NOI18N
            } catch (final Exception ex) {
                final String message = "cannot assign timeseries to catchment area " + subId; // NOI18N
                LOG.error(message, ex);
                // TODO: throw proper exception
                throw new RuntimeException(message, ex);
            }
        }
    }
}
