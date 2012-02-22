/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.codehaus.jackson.map.ObjectMapper;

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

    private transient CidsBean modelInputBean;
    private transient volatile RunoffInputManagerUI ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public RunoffInput getUR() throws IOException {
        final String json = (String)modelInputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, RunoffInput.class);
    }

    @Override
    public void finalise() throws IOException {
        // not needed
    }

    @Override
    public Feature getFeature() throws IOException {
        return null;
    }

    @Override
    public CidsBean getCidsBean() {
        return modelInputBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.modelInputBean = cidsBean;
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
