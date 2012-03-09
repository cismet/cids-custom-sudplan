/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataExport;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.io.File;

import de.cismet.cids.custom.sudplan.dataImport.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimeSeriesExportWizardPanelFile extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_EXPORT_FILE = "__prop_export_file__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient File exportFile;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Component createComponent() {
        return new TimeSeriesExportVisualPanelFile(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public File getExportFile() {
        return exportFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  exportFile  DOCUMENT ME!
     */
    public void setExportFile(final File exportFile) {
        this.exportFile = exportFile;

        changeSupport.fireChange();
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        exportFile = (File)wizard.getProperty(PROP_EXPORT_FILE);

        ((TimeSeriesExportVisualPanelFile)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_EXPORT_FILE, exportFile);
    }

    @Override
    public boolean isValid() {
        if (exportFile == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    TimeSeriesExportWizardPanelFile.class,
                    "TimeSeriesExportWizardPanelFile.isValid().chooseFile"));

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            if (exportFile.exists()) {
                if (exportFile.isFile()) {
                    wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

                    if (exportFile.canWrite()) {
                        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

                        return true;
                    } else {
                        wizard.putProperty(
                            WizardDescriptor.PROP_WARNING_MESSAGE,
                            NbBundle.getMessage(
                                TimeSeriesExportWizardPanelFile.class,
                                "TimeSeriesExportWizardPanelFile.isValid().notWritable"));

                        return false;
                    }
                } else {
                    wizard.putProperty(
                        WizardDescriptor.PROP_INFO_MESSAGE,
                        NbBundle.getMessage(
                            TimeSeriesExportWizardPanelFile.class,
                            "TimeSeriesExportWizardPanelFile.isValid().chooseFile"));

                    return false;
                }
            } else {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

                return true;
            }
        }
    }
}
