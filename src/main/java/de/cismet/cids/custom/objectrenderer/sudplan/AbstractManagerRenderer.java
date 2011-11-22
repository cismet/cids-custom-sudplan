/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.io.IOException;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public abstract class AbstractManagerRenderer extends AbstractCidsBeanRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractManagerRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private transient Manager manager;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    @Override
    protected void init() {
        setTitle((String)cidsBean.getProperty("name"));                     // NOI18N
        final CidsBean modelBean = (CidsBean)cidsBean.getProperty("model"); // NOI18N
        manager = SMSUtils.loadManagerFromModel(modelBean, getType());
        manager.setCidsBean(cidsBean);

        if (EventQueue.isDispatchThread()) {
            setManagerUI(manager);
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        setManagerUI(manager);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract ManagerType getType();

    /**
     * DOCUMENT ME!
     *
     * @param  manager  DOCUMENT ME!
     */
    protected void setManagerUI(final Manager manager) {
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(manager.getUI(), BorderLayout.CENTER);
        this.revalidate();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    @Override
    public void dispose() {
        super.dispose();

        try {
            manager.finalise();
        } catch (final IOException ex) {
            final String message = "cannot finalise manager: " + this; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }
}
