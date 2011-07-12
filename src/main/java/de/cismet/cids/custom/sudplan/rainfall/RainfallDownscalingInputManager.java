/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

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
public final class RainfallDownscalingInputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RainfallDownscalingInputManager.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean modelInputBean;
    private transient volatile RainfallDownscalingInputManagerUI ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    try {
                        ui = new RainfallDownscalingInputManagerUI(getUR());
                    } catch (final IOException ex) {
                        final String message = "cannot read RainfallDownscalingInput from unified resource"; // NOI18N
                        LOG.error(message, ex);
                        throw new IllegalStateException(message, ex);
                    }
                }
            }
        }

        return ui;
    }

    @Override
    public RainfallDownscalingInput getUR() throws IOException {
        final String json = (String)modelInputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, RainfallDownscalingInput.class);
    }

    @Override
    public void finalise() throws IOException {
        // no need to finalise any changes since the ui does not change anything
    }

    @Override
    public Feature getFeature() throws IOException {
        // no feature attached

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
}
