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

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class IDFImportWizardPanelChooseFile extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient File inputFile;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void read(final WizardDescriptor wizard) {
        inputFile = (File)wizard.getProperty(TimeSeriesImportWizardAction.PROP_INPUT_FILE);

        ((IDFImportVisualPanelChooseFile)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(TimeSeriesImportWizardAction.PROP_INPUT_FILE, inputFile);
    }

    @Override
    protected Component createComponent() {
        return new IDFImportVisualPanelChooseFile(this);
    }

    @Override
    public boolean isValid() {
        if (inputFile == null) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please choose an IDF file");

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            if (inputFile.isFile()) {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

                if (inputFile.canRead()) {
                    return true;
                } else {
                    wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Chosen file cannot be read");

                    return false;
                }
            } else {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Chosen path does not denote a file");

                return false;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public File getInputFile() {
        return inputFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  inputFile  DOCUMENT ME!
     */
    public void setInputFile(final File inputFile) {
        this.inputFile = inputFile;
        changeSupport.fireChange();
    }
}
