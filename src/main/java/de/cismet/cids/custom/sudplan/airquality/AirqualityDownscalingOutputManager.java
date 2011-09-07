/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.apache.log4j.Logger;

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
public final class AirqualityDownscalingOutputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingInputManager.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean modelOutputBean;
    private transient volatile AirqualityDownscalingOutputManagerUI ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new AirqualityDownscalingOutputManagerUI(this);
                }
            }
        }

        return ui;
    }

    @Override
    public AirqualityDownscalingOutput getUR() throws IOException {
        final String json = (String)modelOutputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, AirqualityDownscalingOutput.class);
    }

    @Override
    public void finalise() throws IOException {
        ui.dispose();
    }

    @Override
    public Feature getFeature() throws IOException {
        // no feature attached

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
}
