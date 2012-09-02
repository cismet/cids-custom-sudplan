/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataImport;

import de.cismet.cids.custom.sudplan.converter.AbstractConverterChoosePanel;
import de.cismet.cids.custom.sudplan.converter.AbstractConverterChoosePanelCtrl;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class IDFImportWizardPanelChooseConverter extends AbstractConverterChoosePanelCtrl {

    //~ Instance fields --------------------------------------------------------

    private final transient IDFImportVisualPanelChooseConverter component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IDFImportWizardPanelChooseConverter object.
     */
    public IDFImportWizardPanelChooseConverter() {
        component = new IDFImportVisualPanelChooseConverter(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public AbstractConverterChoosePanel getComponent() {
        return component;
    }
}
