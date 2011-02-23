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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.search.Query;
import Sirius.server.search.SearchResult;
import Sirius.server.sql.SystemStatement;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Properties;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.BlacklistClassloading;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SMSUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SMSUtils.class);

    public static final String TABLENAME_TIMESERIES = "TIMESERIES";   // NOI18N
    public static final String TABLENAME_MODELINPUT = "MODELINPUT";   // NOI18N
    public static final String TABLENAME_MODELOUTPUT = "MODELOUTPUT"; // NOI18N
    public static final String TABLENAME_MODELRUN = "RUN";            // NOI18N
    public static final String TABLENAME_MODEL = "MODEL";             // NOI18N

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum Model {

        //~ Enum constants -----------------------------------------------------

        AQ_DS("Airquality Downscaling"), // NOI18N
        RF_DS("Rainfall Downscaling"),   // NOI18N
        HY_CAL("Hydrology Calibration"); // NOI18N

        //~ Instance fields ----------------------------------------------------

        private final String name;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Model object.
         *
         * @param  name  DOCUMENT ME!
         */
        private Model(final String name) {
            this.name = name;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return super.toString() + "[" + getName() + "]"; // NOI18N
        }
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SMSUtils object.
     */
    private SMSUtils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Creates a transient instance of modelinput or modeloutput.
     *
     * @param   name      the model input/output name
     * @param   resource  the model input/output unified resource that will be stored as a json serialisation
     * @param   type      the model type which will use the resulting model input / which generated the model output
     * @param   input     true for model input, false for model output
     *
     * @return  a transient model input/output cidsbean, never null
     *
     * @throws  IOException  if a new model input/output instance cannot be created for any reason
     */
    public static CidsBean createModelIO(final String name,
            final Object resource,
            final Model type,
            final boolean input) throws IOException {
        // TODO: is the userdomain the correct one?
        final MetaClass modelIOClass = ClassCacheMultiple.getMetaClass(
                SessionManager.getSession().getUser().getDomain(),
                input ? TABLENAME_MODELINPUT : TABLENAME_MODELOUTPUT);
        final ObjectMapper mapper = new ObjectMapper();
        final StringWriter writer = new StringWriter();
        try {
            // TODO: here could be an encoding issue
            mapper.writeValue(writer, resource);
            final CidsBean modelIOBean = modelIOClass.getEmptyInstance().getBean();
            modelIOBean.setProperty("name", name);            // NOI18N
            modelIOBean.setProperty("ur", writer.toString()); // NOI18N
            modelIOBean.setProperty("model", getModel(type)); // NOI18N

            return modelIOBean;
        } catch (final Exception ex) {
            final String message = "cannot create new instance of modelinput"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    /**
     * Simply delegates to
     * {@link #createModelIO(java.lang.String, java.lang.Object, de.cismet.cids.custom.sudplan.SMSUtils.Model, true)}.
     *
     * @param   name      the model input name
     * @param   resource  the model input unified resource that will be stored as a json serialisation
     * @param   type      the model type which will use the resulting model input
     *
     * @return  a transient model input cidsbean, never null
     *
     * @throws  IOException  if a new model input instance cannot be created for any reason
     */
    public static CidsBean createModelInput(final String name, final Object resource, final Model type)
            throws IOException {
        return createModelIO(name, resource, type, true);
    }

    /**
     * Simply delegates to
     * {@link #createModelIO(java.lang.String, java.lang.Object, de.cismet.cids.custom.sudplan.SMSUtils.Model, false)}.
     *
     * @param   name      the model output name
     * @param   resource  the model output unified resource that will be stored as a json serialisation
     * @param   type      the model type which generated the resulting model output
     *
     * @return  a transient model output cidsbean, never null
     *
     * @throws  IOException  if a new model output instance cannot be created for any reason
     */
    public static CidsBean createModelOutput(final String name, final Object resource, final Model type)
            throws IOException {
        return createModelIO(name, resource, type, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  UnsupportedOperationException DOCUMENT ME!
     */
    public static CidsBean getModel(final Model type) throws IOException {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null"); // NOI18N
        }

        // TODO: correct domain?
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass modelClass = ClassCacheMultiple.getMetaClass(domain, TABLENAME_MODEL);

        final StringBuilder sb = new StringBuilder("SELECT ");               // NOI18N
        sb.append(modelClass.getID()).append(" as class_id, ");              // NOI18N
        sb.append(modelClass.getPrimaryKey()).append(" as object_id");       // NOI18N
        sb.append(" from ").append(modelClass.getTableName());               // NOI18N
        sb.append(" where name like '").append(type.getName()).append('\''); // NOI18N

        final Query query = new Query(new SystemStatement(true, -1, "", false, SearchResult.OBJECT, sb.toString()),
                domain);
        try {
            final MetaObject[] results = SessionManager.getProxy().getMetaObject(query);

            if (results.length != 1) {
                throw new IllegalStateException("multiple results found"); // NOI18N
            }

            return results[0].getBean();
        } catch (ConnectionException ex) {
            final String message = "cannot retrieve model for type: " + type; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   name         DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   modelInput   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static CidsBean createModelRun(final String name, final String description, final CidsBean modelInput)
            throws IOException {
        if ((name == null) || name.isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty");                                      // NOI18N
        }
        if ((modelInput == null) || (modelInput.getProperty("model") == null)) {                                       // NOI18N
            throw new IllegalArgumentException("modelinput must not be null and shall contain a corresponding model"); // NOI18N
        }

        // TODO: is the userdomain the correct one?
        final MetaClass modelinputClass = ClassCacheMultiple.getMetaClass(
                SessionManager.getSession().getUser().getDomain(),
                TABLENAME_MODELRUN);

        try {
            final CidsBean runBean = modelinputClass.getEmptyInstance().getBean();

            runBean.setProperty("name", name);                             // NOI18N
            runBean.setProperty("description", description);               // NOI18N
            runBean.setProperty("modelinput", modelInput);                 // NOI18N
            runBean.setProperty("model", modelInput.getProperty("model")); // NOI18N

            return runBean;
        } catch (final Exception e) {
            final String message = "cannot create model run: name=" + name + " || desc=" + description // NOI18N
                        + " || modelinput=" + modelInput;                                              // NOI18N
            LOG.error(message, e);
            throw new IOException(name, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   runBean  DOCUMENT ME!
     * @param   type     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Manager loadManagerFromRun(final CidsBean runBean, final Manager.ManagerType type) {
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
    public static Manager loadManagerFromModel(final CidsBean modelBean, final Manager.ManagerType type) {
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
        final TreePath path = tree.getSelectionPath();
        final DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

        try {
            final RootTreeNode root = new RootTreeNode(SessionManager.getProxy().getRoots());
            model.setRoot(root);
            model.reload();
            tree.exploreSubtree(path.getParentPath());
        } catch (final Exception ex) {
            LOG.warn("could not reload tree", ex); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   properties  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String toTSTBCompatiblePropListing(final Properties properties) {
        final StringBuilder sb = new StringBuilder();

        for (final String key : properties.stringPropertyNames()) {
            final String value = properties.getProperty(key);

            sb.append('\'').append(key).append('\'');
            sb.append("=>");
            sb.append('\'').append(value).append('\'');
            sb.append(',');
        }

        // delete last ',' if at least one value has been added
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("created property string: " + sb.toString()); // NOI18N
        }

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   properties  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Properties fromTSTBCompatiblePropListing(final String properties) {
        final Properties props = new Properties();

        if ((properties != null) && !properties.isEmpty()) {
            final String[] kvSplit = properties.split(","); // NOI18N
            for (final String kv : kvSplit) {
                final String[] kvp = kv.split("=>");        // NOI18N
                if (kvp.length == 2) {
                    final String k = kvp[0].trim();
                    final String v = kvp[1].trim();
                    if ((k.length() > 2) && (v.length() > 2)) {
                        final String key = k.substring(1, k.length() - 1);
                        final String value = v.substring(1, v.length() - 1);

                        props.put(key, value);
                    } else {
                        LOG.warn("ignoring illegal property: " + kv); // NOI18N
                    }
                } else {
                    LOG.warn("ignoring illegal property: " + kv);     // NOI18N
                }
            }
        }

        return props;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id         DOCUMENT ME!
     * @param   tablename  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean fetchCidsBean(final int id, final String tablename) {
        final String domain = SessionManager.getSession().getUser().getDomain();

        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, tablename);
        try {
            final MetaObject mo = SessionManager.getProxy().getMetaObject(id, mc.getID(), domain);

            return mo.getBean();
        } catch (final ConnectionException ex) {
            LOG.warn("cannot get timeseries bean from server", ex); // NOI18N
            return null;
        }
    }
}