/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.Disposable;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public abstract class AbstractManagerRenderer extends AbstractCidsBeanRenderer {

    //~ Instance fields --------------------------------------------------------

    private transient Manager manager;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        super.setCidsBean(cidsBean);

        setTitle((String)cidsBean.getProperty("name")); // NOI18N

        init();
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
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
    abstract Manager.ManagerType getType();

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
     */
    @Override
    public void dispose() {
        super.dispose();

        if (manager instanceof Disposable) {
            ((Disposable)manager).dispose();
        }
    }
}
