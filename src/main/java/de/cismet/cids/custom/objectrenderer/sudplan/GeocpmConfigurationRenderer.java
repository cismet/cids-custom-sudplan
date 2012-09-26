/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import javax.swing.JComponent;

import de.cismet.cids.custom.objecteditors.sudplan.GeocpmConfigurationEditor;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeocpmConfigurationRenderer extends GeocpmConfigurationEditor implements TitleComponentProvider,
    RequestsFullSizeComponent {

    //~ Instance fields --------------------------------------------------------

    private final transient GeoCPMCfgTitleComponent titleComponent;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RaineventRenderer object.
     */
    public GeocpmConfigurationRenderer() {
        super(false);

        titleComponent = new GeoCPMCfgTitleComponent();
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
