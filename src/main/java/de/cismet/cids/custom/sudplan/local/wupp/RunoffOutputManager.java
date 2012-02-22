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
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunoffOutputManager implements Manager {

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean modelOutputBean;
    private transient volatile RunoffOutputManagerUI ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public SimulationResult getUR() throws IOException {
        final String json = (String)modelOutputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, SimulationResult.class);
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
        return modelOutputBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.modelOutputBean = cidsBean;
    }

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new RunoffOutputManagerUI(this);
                }
            }
        }

        return ui;
    }
}
