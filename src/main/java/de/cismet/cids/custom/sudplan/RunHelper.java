/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.tree.MetaCatalogueTree;

import org.apache.log4j.Logger;

import javax.swing.tree.DefaultTreeModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.BlacklistClassloading;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunHelper.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum ManagerType {

        //~ Enum constants -----------------------------------------------------

        INPUT, MODEL, OUTPUT
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunHelper object.
     */
    private RunHelper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   runBean  DOCUMENT ME!
     * @param   type     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Manager loadManagerFromRun(final CidsBean runBean, final ManagerType type) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("loading manager for bean '" + runBean + "' and type: " + type); // NOI18N
        }

        return loadManagerFromModel((CidsBean)runBean.getProperty("model"), type); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelBean  DOCUMENT ME!
     * @param   type       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static Manager loadManagerFromModel(final CidsBean modelBean, final ManagerType type) {
        final CidsBean managerBean;
        switch (type) {
            case INPUT: {
                managerBean = (CidsBean)modelBean.getProperty("inputmanager");    // NOI18N
                break;
            }
            case MODEL: {
                managerBean = (CidsBean)modelBean.getProperty("modelmanager");    // NOI18N
                break;
            }
            case OUTPUT: {
                managerBean = (CidsBean)modelBean.getProperty("outputmanager");   // NOI18N
                break;
            }
            default: {
                throw new IllegalStateException("unknown manager type: " + type); // NOI18N
            }
        }

        final String definition = (String)managerBean.getProperty("definition"); // NOI18N

        final Class managerClass = BlacklistClassloading.forName(definition);
        if (managerClass == null) {
            throw new IllegalStateException("manager not in classpath: " + definition);          // NOI18N
        } else if (Manager.class.isAssignableFrom(managerClass)) {
            final Manager manager;
            try {
                manager = (Manager)managerClass.newInstance();
            } catch (final Exception ex) {
                final String message = "cannot properly create manager instance: " + definition; // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }

            return manager;
        } else {
            throw new IllegalStateException("given class does not implement manager interface: " + definition); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     */
    public static void reloadCatalogTree() {
        final MetaCatalogueTree tree = ComponentRegistry.getRegistry().getCatalogueTree();
        final DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

        try {
            final RootTreeNode root = new RootTreeNode(SessionManager.getProxy().getRoots());
            model.setRoot(root);
            model.reload();
        } catch (final ConnectionException ex) {
            LOG.warn("could not reload tree", ex); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   runBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String createIONameSnippet(final CidsBean runBean) {
        final StringBuilder sb = new StringBuilder();

        sb.append((String)((CidsBean)runBean.getProperty("model")).getProperty("name")); // NOI18N
        sb.append("(Run: ");
        sb.append((String)runBean.getProperty("name"));                                  // NOI18N
        sb.append(')');

        return sb.toString();
    }
}
