/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.workshop.solution;

import org.apache.log4j.Logger;

import java.io.IOException;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class WorkshopOutputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(WorkshopOutputManager.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean cidsBean;

    private transient volatile WorkshopOutputManagerUI ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public WorkshopIO getUR() throws IOException {
        if (cidsBean == null) {
            throw new IllegalStateException("manager not initialised, cidsbean context missing"); // NOI18N
        }

        final String ur = (String)cidsBean.getProperty("ur"); // NOI18N

        if (ur == null) {
            return null;
        } else {
            final String[] integerStrings = ur.split(";"); // NOI18N
            final int[] integers = new int[integerStrings.length];

            try {
                for (int i = 0; i < integers.length; ++i) {
                    integers[i] = Integer.parseInt(integerStrings[i]);
                }

                final WorkshopIO input = new WorkshopIO(integers);

                return input;
            } catch (final Exception e) {
                final String message = "cannot create workshop model output"; // NOI18N
                LOG.error(message, e);
                throw new IOException(message, e);
            }
        }
    }

    @Override
    public void finalise() throws IOException {
        // nothing to do here
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
    public WorkshopOutputManagerUI getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new WorkshopOutputManagerUI(this);
                }
            }
        }

        return ui;
    }
}
