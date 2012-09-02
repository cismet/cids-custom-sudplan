/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.io.File;

import java.text.MessageFormat;

import de.cismet.cids.custom.sudplan.data.io.AbstractWizardPanelCtrl;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class WizardPanelChooseFile extends AbstractWizardPanelCtrl {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_INPUT_FILE = "__prop_input_file__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient VisualPanelChooseFile comp;

    private transient File importFile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesImportFileChoosePanelCtrl object.
     */
    public WizardPanelChooseFile() {
        this.comp = new VisualPanelChooseFile(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        return this.comp;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        this.comp.init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_INPUT_FILE, this.importFile);
    }

    @Override
    public boolean isValid() {
        final String fileName = this.comp.getFileName();
        if ((fileName == null) || fileName.trim().isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    WizardPanelChooseFile.class,
                    "WizardPanelChooseFile.isValid().wizard.putProperty(String,String).noFile"));
            return false;
        }

        final File file = new File(fileName);
        if (!file.exists()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                MessageFormat.format(
                    NbBundle.getMessage(
                        WizardPanelChooseFile.class,
                        "WizardPanelChooseFile.isValid().wizard.putProperty(String,String).noExistence"),
                    fileName));
            return false;
        }

        if (!file.canRead()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                MessageFormat.format(
                    NbBundle.getMessage(
                        WizardPanelChooseFile.class,
                        "WizardPanelChooseFile.isValid().wizard.putProperty(String,String).noRead"),
                    fileName));
            return false;
        }

        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        this.importFile = file;
        return true;
    }
}
