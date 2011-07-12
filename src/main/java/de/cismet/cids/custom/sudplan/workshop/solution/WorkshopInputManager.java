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
public final class WorkshopInputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(WorkshopInputManager.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean cidsBean;

    private transient volatile WorkshopInputManagerUI ui;

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
                final String message = "cannot create workshop model input"; // NOI18N
                LOG.error(message, e);
                throw new IOException(message, e);
            }
        }
    }

    @Override
    public void finalise() throws IOException {
        final int[] newInts = getUI().getIntegers();
        final StringBuilder sb = new StringBuilder();

        for (final int i : newInts) {
            sb.append(i).append(';');
        }

        try {
            cidsBean.setProperty("ur", sb.toString());        // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot save model input"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
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
    public WorkshopInputManagerUI getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new WorkshopInputManagerUI(this);
                }
            }
        }

        return ui;
    }
}
