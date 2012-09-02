/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.data.io;

import org.openide.WizardDescriptor;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesConverterChoosePanelCtrl extends AbstractConverterChoosePanelCtrl {

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
    public AbstractConverterChoosePanel getComponent() {
        return this.comp;
    }
}
