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

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesConverterChoosePanelCtrl extends AbstractWizardPanelCtrl {

    //~ Instance fields --------------------------------------------------------

    private final transient TimeSeriesConverterChoosePanel comp;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesImportFileChoosePanelCtrl object.
     */
    public TimeSeriesConverterChoosePanelCtrl() {
        this.comp = new TimeSeriesConverterChoosePanel(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        return this.comp;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        this.comp.init();
        wizard.putProperty(
            WizardDescriptor.PROP_INFO_MESSAGE,
            java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                "TimeSeriesConverterChoosePanelCtrl.read(WizardDescriptor).wizard.putProperty(String,String)"));
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(TimeSeriesImportWizardAction.PROP_CONVERTER, this.comp.getConverter());
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
