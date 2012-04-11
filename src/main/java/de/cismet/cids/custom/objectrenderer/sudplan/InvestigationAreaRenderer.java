/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import javax.swing.JComponent;

import de.cismet.cids.custom.objecteditors.sudplan.InvestigationAreaEditor;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class InvestigationAreaRenderer extends InvestigationAreaEditor implements TitleComponentProvider {

    //~ Instance fields --------------------------------------------------------

    private final transient DefaultTitleComponent dtc;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new InvestigationAreaRenderer object.
     */
    public InvestigationAreaRenderer() {
        super(false);

        dtc = new DefaultTitleComponent();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public JComponent getTitleComponent() {
        return dtc;
    }

    @Override
    public String getTitle() {
        return dtc.getTitle();
    }

    @Override
    public void setTitle(final String title) {
        dtc.setTitle(title);
    }
}
