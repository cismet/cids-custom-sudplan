/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import se.smhi.sudplan.client.Scenario;
import se.smhi.sudplan.client.SudPlanHypeAPI;
import se.smhi.sudplan.client.exception.UnrecoverableException;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.HashSet;
import java.util.Set;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SudplanOptions;
import de.cismet.cids.custom.sudplan.Variable;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class HydrologyCache {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(HydrologyCache.class);

    private static final String WORKSPACE_MO_QUERY =
        "SELECT {0}, hw.{1} FROM hydrology_workspace hw, run r WHERE hw.calibration = r.id AND r.modelinput = {2}"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient SudPlanHypeAPI hypeClient;
    private final transient Scenario calibrationScenario;
    private final transient Set<Scenario> simulationScenarios;
    private final transient DateFormat hydroDateFormat;
    private final transient String[] vars;

    private transient CidsBean currentWorkspace;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HydrologyCache object.
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private HydrologyCache() {
        hypeClient = new SudPlanHypeAPI(SudplanOptions.getInstance().getHdHypeIp());
        simulationScenarios = new HashSet<Scenario>();

        Scenario calScenario = null;
        try {
            for (final Scenario s : hypeClient.listScenarios()) {
                if ("NORMAL".equals(s.getScenarioId())) { // NOI18N
                    calScenario = s;
                } else {
                    simulationScenarios.add(s);
                }
            }

            if (calScenario == null) {
                throw new IllegalStateException("cannot fetch calibration scenario from hype client"); // NOI18N
            } else {
                calibrationScenario = calScenario;
            }
        } catch (final UnrecoverableException ex) {
            final String message = "hype client is completely broken, hydrology cache unusable";       // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        hydroDateFormat = new SimpleDateFormat("yyyy-MM-dd");                   // NOI18N
        vars = new String[] { "cout", "crun", "cprc", "ctmp", "gwat", "smdf" }; // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HydrologyCache getInstance() {
        return LazyInitializer.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCurrentWorkspace() {
        return currentWorkspace;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentWorkspace  DOCUMENT ME!
     */
    public void setCurrentWorkspace(final CidsBean currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SudPlanHypeAPI getHypeClient() {
        return hypeClient;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Scenario getCalibrationScenario() {
        return calibrationScenario;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<Scenario> getSimulationScenarios() {
        return simulationScenarios;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DateFormat getHydroDateFormat() {
        return hydroDateFormat;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String[] getVars() {
        return vars;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   var  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public Variable getVariableForVar(final String var) {
        if (var == null) {
            return null;
        } else if ("cout".equals(var)) {
            return Variable.COUT;
        } else if ("crun".equals(var)) {
            return Variable.CRUN;
        } else if ("cprc".equals(var)) {
            return Variable.CPRC;
        } else if ("ctmp".equals(var)) {
            return Variable.CTMP;
        } else if ("gwat".equals(var)) {
            return Variable.GWAT;
        } else if ("smdf".equals(var)) {
            return Variable.SMDF;
        } else {
            throw new IllegalArgumentException("unknown var: " + var); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   variable  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public String getVarForVariable(final Variable variable) {
        if (variable == null) {
            return null;
        } else if (Variable.COUT.equals(variable)) {
            return "cout";
        } else if (Variable.CRUN.equals(variable)) {
            return "crun";
        } else if (Variable.CPRC.equals(variable)) {
            return "cprc";
        } else if (Variable.CTMP.equals(variable)) {
            return "ctmp";
        } else if (Variable.GWAT.equals(variable)) {
            return "gwat";
        } else if (Variable.SMDF.equals(variable)) {
            return "smdf";
        } else {
            throw new IllegalArgumentException("unsupported variable: " + variable); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   calInput  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public CidsBean getWorkspaceFromCalInput(final CidsBean calInput) throws ConnectionException {
        final User user = SessionManager.getSession().getUser();
        final MetaClass hwClass = ClassCacheMultiple.getMetaClass(user.getDomain(),
                SMSUtils.TABLENAME_HYDROLOGY_WORKSPACE);

        final String query = MessageFormat.format(
                WORKSPACE_MO_QUERY,
                hwClass.getID(),
                hwClass.getPrimaryKey(),
                calInput.getMetaObject().getID());

        final MetaObject[] mos = SessionManager.getProxy().getMetaObjectByQuery(user, query);
        if (mos.length == 1) {
            return mos[0].getBean();
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public void reloadCurrentWorkspace() throws ConnectionException {
        if (getCurrentWorkspace() != null) {
            final MetaObject womo = getCurrentWorkspace().getMetaObject();
            final String domain = SessionManager.getSession().getUser().getDomain();
            final MetaObject newWomo = SessionManager.getProxy().getMetaObject(womo.getID(), womo.getClassID(), domain);

            setCurrentWorkspace(newWomo.getBean());
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitializer {

        //~ Static fields/initializers -----------------------------------------

        private static final HydrologyCache INSTANCE = new HydrologyCache();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitializer object.
         */
        private LazyInitializer() {
        }
    }
}
