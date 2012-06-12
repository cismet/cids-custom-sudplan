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

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;
import de.cismet.cids.custom.sudplan.converter.Converter;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class IDFImportWizardPanelChooseConverter extends AbstractWizardPanel {

    //~ Instance fields --------------------------------------------------------

    private transient Converter converter;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void read(final WizardDescriptor wizard) {
        converter = (Converter)wizard.getProperty(TimeSeriesImportWizardAction.PROP_CONVERTER);

        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please choose a converter");

        ((IDFImportVisualPanelChooseConverter)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(TimeSeriesImportWizardAction.PROP_CONVERTER, converter);

        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Converter getConverter() {
        return converter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  converter  DOCUMENT ME!
     */
    public void setConverter(final Converter converter) {
        this.converter = converter;
        changeSupport.fireChange();
    }

    @Override
    protected Component createComponent() {
        return new IDFImportVisualPanelChooseConverter(this);
    }
}
