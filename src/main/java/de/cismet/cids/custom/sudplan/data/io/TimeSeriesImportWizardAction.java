/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.data.io;

import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.tree.MetaCatalogueTree;

import org.apache.log4j.Logger;

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

import de.cismet.cids.custom.sudplan.MonitorstationContext;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

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

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesImportWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardAction object.
     */
    public TimeSeriesImportWizardAction() {
        super("", ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/data/io/ts_import.png", false)); // NOI18N

        putValue(
            Action.SHORT_DESCRIPTION,
            NbBundle.getMessage(TimeSeriesImportWizardAction.class, "TimeSeriesImportWizardAction.shortDescription")); // NOI18N
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
                    new WizardPanelChooseFileImport(),
                    new TimeSeriesConverterChoosePanelCtrl(),
                    new WizardPanelConversionForward(),
                    new WizardPanelMetadata(SMSUtils.TABLENAME_TIMESERIES),
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
        wizard.setTitleFormat(new MessageFormat("{0}"));                                    // NOI18N
        wizard.setTitle(NbBundle.getMessage(
                TimeSeriesImportWizardAction.class,
                "TimeSeriesImportWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        // if TS import has been canceled, cancel all running threads
        if (wizard.getValue() == WizardDescriptor.FINISH_OPTION) {
            // refresh the catalogue
            final CidsBean tsBean = (CidsBean)wizard.getProperty(WizardPanelMetadata.PROP_BEAN);
            final CidsBean station = (CidsBean)tsBean.getProperty("station"); // NOI18N
            final String type = (String)station.getProperty("type");          // NOI18N
            final int indexOfColon = type.indexOf(':');
            if (indexOfColon < 0) {
                LOG.warn("unrecognized monitorstation type: " + type);        // NOI18N
            } else {
                try {
                    final MonitorstationContext ctx = MonitorstationContext.getMonitorstationContext(type.substring(
                                0,
                                indexOfColon));

                    final String refreshIdStation;
                    final String refreshIdTimeseries;
                    if (MonitorstationContext.AQ.equals(ctx)) {
                        refreshIdStation = "airquality.monitorstation." + station.getMetaObject().getID(); // NOI18N
                        refreshIdTimeseries = "airquality.timeseries";                                     // NOI18N
                    } else if (MonitorstationContext.HD.equals(ctx)) {
                        refreshIdStation = "hydrology.monitorstation." + station.getMetaObject().getID();  // NOI18N
                        refreshIdTimeseries = "hydrology.timeseries";                                      // NOI18N
                    } else if (MonitorstationContext.RF.equals(ctx)) {
                        refreshIdStation = "rainfall.monitorstation." + station.getMetaObject().getID();   // NOI18N
                        refreshIdTimeseries = "rainfall.timeseries";                                       // NOI18N
                    } else {
                        throw new IllegalArgumentException("unknown monitorstation context: " + ctx);      // NOI18N
                    }

                    final MetaCatalogueTree tree = ComponentRegistry.getRegistry().getCatalogueTree();
                    tree.requestRefreshNode(refreshIdStation);
                    tree.requestRefreshNode(refreshIdTimeseries);
                } catch (final IllegalArgumentException ex) {
                    LOG.warn("unrecognized monitorstation context: " + type.substring(0, indexOfColon), ex); // NOI18N
                }
            }
        } else {
            for (final WizardDescriptor.Panel panel : this.panels) {
                if (panel instanceof Cancellable) {
                    ((Cancellable)panel).cancel();
                }
            }
        }
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
