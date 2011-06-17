/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunoffInputManager implements Manager {

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean cidsBean;
    private transient volatile RunoffInputManagerUI ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public RunoffIO getUR() throws IOException {
        return null;
    }

    @Override
    public void apply() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Feature getFeature() throws IOException {
        return null;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
    }

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new RunoffInputManagerUI(this);
                }
            }
        }

        return ui;
    }
}
