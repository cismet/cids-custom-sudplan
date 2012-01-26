/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataImport;

import org.openide.WizardDescriptor;

import java.awt.Component;

import java.io.File;

import java.text.MessageFormat;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesImportFileChoosePanelCtrl extends AbstractWizardPanelCtrl {

    //~ Instance fields --------------------------------------------------------

    private final transient TimeSeriesImportFileChoosePanel comp;

    private transient File importFile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesImportFileChoosePanelCtrl object.
     */
    public TimeSeriesImportFileChoosePanelCtrl() {
        this.comp = new TimeSeriesImportFileChoosePanel(this);
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
        wizard.putProperty(TimeSeriesImportWizardAction.PROP_INPUT_FILE, this.importFile);
    }

    @Override
    public boolean isValid() {
        final String fileName = this.comp.getFileName();
        if ((fileName == null) || fileName.trim().isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                    "TimeSeriesImportFileChoosePanelCtrl.isValid().wizard.putProperty(String,String).noFile"));
            return false;
        }

        final File file = new File(fileName);
        if (!file.exists()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                MessageFormat.format(
                    java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                        "TimeSeriesImportFileChoosePanelCtrl.isValid().wizard.putProperty(String,String).noExistence"),
                    fileName));
            return false;
        }

        if (!file.canRead()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                MessageFormat.format(
                    java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                        "TimeSeriesImportFileChoosePanelCtrl.isValid().wizard.putProperty(String,String).noRead"),
                    fileName));
            return false;
        }

        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        this.importFile = file;
        return true;
    }
}
