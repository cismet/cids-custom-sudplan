/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesImport;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.text.MessageFormat;

import javax.swing.JComponent;

import de.cismet.cids.navigator.utils.CidsClientToolbarItem;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsClientToolbarItem.class)
public final class TimeSeriesImportWizardAction extends AbstractCidsBeanAction implements CidsClientToolbarItem {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_INPUT_FILE = "__prop_input_file__"; // NOI18N
    public static final String PROP_CONVERTER = "__prop_converter__";   // NOI18N
    public static final String PROP_TIMESERIES = "__prop_timeseries__"; // NOI18N
    public static final String PROP_BEAN = "__prop_bean__";             // NOI18N

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesImportWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardAction object.
     */
    public TimeSeriesImportWizardAction() {
        super(java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                "TimeSeriesImportWizardAction.this.name"));
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
                    new TimeSeriesImportFileChoosePanelCtrl(),
                    new TimeSeriesConverterChoosePanelCtrl(),
                    new TimeSeriesConversionCtrl(),
                    new TimeSeriesMetaDataPanelCtrl(),
                    new TimeSeriesPersistenceCtrl()
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
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}")); // NOI18N
        wizard.setTitle(java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/timeseriesImport/Bundle")
                    .getString("TimeSeriesImportWizardAction.actionPerformed(ActionEvent).wizard.title"));

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        // if TS import has been canceled, cancel all running threads
        if (wizard.getValue() != WizardDescriptor.FINISH_OPTION) {
            for (final WizardDescriptor.Panel panel : this.panels) {
                if (panel instanceof Cancelable) {
                    ((Cancelable)panel).cancel();
                }
            }
        }
    }

    /**
     * /** * DOCUMENT ME! * * @param wizard DOCUMENT ME! * @param mo DOCUMENT ME! * * @return DOCUMENT ME! * * @throws
     * IOException DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final TimeSeriesImportWizardAction action = new TimeSeriesImportWizardAction();
        action.actionPerformed(null);
    }

    @Override
    public String getSorterString() {
        return "ZZZZZZZZ"; // NOI18N
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
