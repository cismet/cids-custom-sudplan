/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import javax.swing.JComponent;

import de.cismet.cids.custom.objecteditors.sudplan.GeocpmConfigEditor;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeocpmConfigRenderer extends GeocpmConfigEditor implements TitleComponentProvider {

    //~ Instance fields --------------------------------------------------------

    private final transient RunGeoCPMTitleComponent titleComponent;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RaineventRenderer object.
     */
    public GeocpmConfigRenderer() {
        super(false);

        titleComponent = new RunGeoCPMTitleComponent();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        super.init();

        titleComponent.setCidsBean(cidsBean);
    }

    @Override
    public JComponent getTitleComponent() {
        return titleComponent;
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);

        titleComponent.setTitle(title);
    }
}
