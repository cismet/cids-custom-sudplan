/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.data.io;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.StringWriter;

import java.text.MessageFormat;

import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.IDFCurve;
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
public final class IDFImportWizardAction extends AbstractCidsBeanAction implements CidsClientToolbarItem {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(IDFImportWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardAction object.
     */
    public IDFImportWizardAction() {
        super("", ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/data/io/idf_import.png", false)); // NOI18N

        putValue(
            Action.SHORT_DESCRIPTION,
            NbBundle.getMessage(IDFImportWizardAction.class, "IDFImportWizardAction.shortDescription")); // NOI18N
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
                    new WizardPanelChooseFile(),
                    new IDFImportWizardPanelChooseConverter(),
                    new WizardPanelConversion(),
                    new WizardPanelMetadata(SMSUtils.TABLENAME_IDFCURVE)
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
        wizard.setTitle("IDF Import");

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        if (wizard.getValue() == WizardDescriptor.FINISH_OPTION) {
            try {
                final IDFCurve idfCurve = (IDFCurve)wizard.getProperty(WizardPanelConversion.PROP_CONVERTED);
                final CidsBean idfBean = (CidsBean)wizard.getProperty(WizardPanelMetadata.PROP_BEAN);
                final Object converter = wizard.getProperty(AbstractConverterChoosePanelCtrl.PROP_CONVERTER);
                final ObjectMapper mapper = new ObjectMapper();
                final StringWriter writer = new StringWriter();

                mapper.writeValue(writer, idfCurve);

                idfBean.setProperty("uri", writer.toString());                    // NOI18N
                idfBean.setProperty("converter", converter.getClass().getName()); // NOI18N
                idfBean.persist();

                // TODO: reload catalogue
            } catch (final Exception ex) {
                final String message = "could not persist idf bean"; // NOI18N
                LOG.error(message, ex);

                final ErrorInfo info = new ErrorInfo("Persist error", message, null, "ERROR", ex, Level.SEVERE, null);
                JXErrorPane.showDialog(ComponentRegistry.getRegistry().getMainWindow(), info);
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
