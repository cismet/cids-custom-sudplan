/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.log4j.Logger;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class SwmmProjectRenderer extends AbstractCidsBeanRenderer implements TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmProjectRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmProjectTitleComponent titleComponent = new SwmmProjectTitleComponent();

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        titleComponent.setCidsBean(cidsBean);
    }

    @Override
    public JComponent getTitleComponent() {
        return titleComponent;
    }

    @Override
    public void setTitle(final String title) {
        LOG.fatal("set Title = " + title);

        super.setTitle(title);

        titleComponent.setTitle(title);
    }
}
