/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.apache.log4j.Logger;

import se.smhi.sudplan.client.Scenario;
import se.smhi.sudplan.client.SudPlanHypeAPI;
import se.smhi.sudplan.client.exception.UnrecoverableException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.HashSet;
import java.util.Set;

import de.cismet.cids.dynamics.CidsBean;

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

    //~ Instance fields --------------------------------------------------------

    private final transient SudPlanHypeAPI hypeClient;
    private final transient Scenario calibrationScenario;
    private final transient Set<Scenario> simulationScenarios;
    private final transient DateFormat hydroDateFormat;

    private transient CidsBean currentWorkspace;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HydrologyCache object.
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private HydrologyCache() {
        hypeClient = new SudPlanHypeAPI("79.125.2.136"); // NOI18N
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

        hydroDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // NOI18N
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
