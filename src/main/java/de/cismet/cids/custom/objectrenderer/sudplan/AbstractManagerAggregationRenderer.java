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

import javax.swing.JComponent;
import javax.swing.JLabel;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanAggregationRenderer;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanCollectionStore;

import de.cismet.cids.tools.metaobjectrenderer.Titled;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractManagerAggregationRenderer extends AbstractCidsBeanAggregationRenderer
        implements TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractManagerAggregationRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private transient Manager manager;

    // FIXME: should create a default title component
    private transient volatile ManagerTitleComponent titleComp;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        String managerClass = null;
        boolean allSameManagers = true;

        final String mProperty;
        final String titleSuffix;
        switch (getType()) {
            case INPUT: {
                mProperty = "inputmanager";                                    // NOI18N
                titleSuffix = "Inputs";
                break;
            }
            case OUTPUT: {
                mProperty = "outputmanager";                                   // NOI18N
                titleSuffix = "Outputs";
                break;
            }
            case MODEL: {
                mProperty = "modelmanager";                                    // NOI18N
                titleSuffix = "Runs";
                break;
            }
            default: {
                throw new IllegalStateException("unknown type: " + getType()); // NOI18N
            }
        }

        for (final CidsBean bean : cidsBeans) {
            final String candidateClass = (String)bean.getProperty("model." + mProperty + ".definition"); // NOI18N
            if (candidateClass == null) {
                allSameManagers = false;
                break;
            } else {
                if (managerClass == null) {
                    managerClass = candidateClass;
                } else if (!managerClass.equals(candidateClass)) {
                    allSameManagers = false;
                    break;
                }
            }
        }

        if (allSameManagers) {
            final Manager candidate = SMSUtils.loadManagerFromDefinition(managerClass);

            if (candidate instanceof CidsBeanCollectionStore) {
                this.manager = candidate;
                ((CidsBeanCollectionStore)this.manager).setCidsBeans(cidsBeans);
                getTitleComponent().setTitle(cidsBeans.size() + " " + titleSuffix); // NOI18N
            } else {
                LOG.warn("manager '" + candidate + "' (" + candidate.getClass()     // NOI18N
                            + ") is not of type CidsBeanCollectionStore");          // NOI18N
            }
        }

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    setManagerUI(manager);
                }
            };
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
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

        if (manager == null) {
            // FIXME: proper error panel
            this.add(new JLabel("Manager does not support aggregation"), BorderLayout.CENTER); // NOI18N
        } else {
            final JComponent ui = manager.getUI();
            if (ui instanceof Titled) {
                getTitleComponent().setTitle(((Titled)ui).getTitle());
            }
            this.add(ui, BorderLayout.CENTER);
        }
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

    @Override
    public ManagerTitleComponent getTitleComponent() {
        if (titleComp == null) {
            synchronized (this) {
                if (titleComp == null) {
                    titleComp = new ManagerTitleComponent();
                }
            }
        }

        return titleComp;
    }
}
