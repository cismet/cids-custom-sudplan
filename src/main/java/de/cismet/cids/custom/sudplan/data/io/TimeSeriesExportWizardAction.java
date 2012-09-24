/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.data.io;

import Sirius.navigator.ui.ComponentRegistry;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.text.MessageFormat;

import javax.swing.Action;
import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TimeSeriesExportWizardAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_TIMESERIES = "__prop_timeseries__";             // NOI18N
    public static final String PROP_TS_RETRIEVER_CFG = "__prop_ts_retriever_cfg__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    protected transient WizardDescriptor.Panel[] panels;

    private transient TimeSeries timeSeries;

    private transient TimeseriesRetrieverConfig timeseriesRetrieverConfig;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardAction object.
     */
    public TimeSeriesExportWizardAction() {
        super("", ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/data/io/ts_export.png", false)); // NOI18N

        putValue(
            Action.SHORT_DESCRIPTION,
            NbBundle.getMessage(TimeSeriesExportWizardAction.class, "TimeSeriesExportWizardAction.shortDescription")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * EDT only !
     *
     * @return  DOCUMENT ME!
     */
    protected WizardDescriptor.Panel[] getPanels() {
        assert EventQueue.isDispatchThread() : "can only be called from EDT"; // NOI18N

        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new TimeSeriesConverterChoosePanelCtrl(),
                    new WizardPanelFileExport(),
                    new TimeSeriesExportWizardPanelConvert()
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
     * @param  timeSeries  DOCUMENT ME!
     */
    public void setTimeSeries(final TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TimeseriesRetrieverConfig getTimeseriesRetrieverConfig() {
        return timeseriesRetrieverConfig;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeseriesRetrieverConfig  DOCUMENT ME!
     */
    public void setTimeseriesRetrieverConfig(final TimeseriesRetrieverConfig timeseriesRetrieverConfig) {
        this.timeseriesRetrieverConfig = timeseriesRetrieverConfig;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && ((timeSeries != null) || (timeseriesRetrieverConfig != null));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());
        wizard.setTitleFormat(new MessageFormat("{0}"));                                    // NOI18N
        wizard.setTitle(NbBundle.getMessage(
                TimeSeriesExportWizardAction.class,
                "TimeSeriesExportWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N

        assert (timeSeries != null) || (timeseriesRetrieverConfig != null) : "time series must not be null"; // NOI18N

        wizard.putProperty(PROP_TIMESERIES, timeSeries);
        wizard.putProperty(PROP_TS_RETRIEVER_CFG, timeseriesRetrieverConfig);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        // if TS import has been canceled, cancel all running threads
        if (wizard.getValue() != WizardDescriptor.FINISH_OPTION) {
            for (final WizardDescriptor.Panel panel : this.panels) {
                if (panel instanceof Cancellable) {
                    ((Cancellable)panel).cancel();
                }
            }
        }
    }
}
