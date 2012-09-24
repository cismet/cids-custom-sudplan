/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import se.smhi.sudplan.client.Scenario;
import se.smhi.sudplan.client.SudPlanHypeAPI;

import java.io.IOException;
import java.io.StringWriter;

import java.text.DateFormat;

import de.cismet.cids.custom.sudplan.AbstractAsyncModelManager;
import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.ModelManager;
import de.cismet.cids.custom.sudplan.RunInfo;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SMSUtils.Model;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SimulationModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(SimulationModelManager.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing hydrology simulation"); // NOI18N
        }

        fireProgressed(-1, -1, "Preparing model execution"); // NOI18N

        final SimulationInput input = inputFromRun(cidsBean);

        final CidsBean hwBean = SMSUtils.fetchCidsBean(input.getHydrologyWorkspaceId(),
                SMSUtils.TABLENAME_HYDROLOGY_WORKSPACE);
        final CidsBean calBean = (CidsBean)hwBean.getProperty("calibration");                          // NOI18N
        final ModelManager calManager = (ModelManager)SMSUtils.loadManagerFromRun(calBean, ManagerType.MODEL);
        calManager.setCidsBean(calBean);
        final RunInfo calRuninfo = calManager.getRunInfo();
        if (!(calRuninfo instanceof CalibrationRunInfo)) {
            throw new IllegalStateException("calibration runinfo not instance of CalibrationRunInfo"); // NOI18N
        }

        final String localModelId = ((CalibrationRunInfo)calRuninfo).getLocalModelId();

        final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();
        final SimulationRunInfo runinfo = new SimulationRunInfo();

        try {
            final Scenario scenario = input.getScenario();
            final String simulationId = hypeClient.createSimulation();
            hypeClient.setDefaultScenario(simulationId, scenario);
            hypeClient.useCalibrationFrom(simulationId, localModelId);
            hypeClient.setPointOfInterest(simulationId, (Integer)hwBean.getProperty("basin_id")); // NOI18N

            final DateFormat df = HydrologyCache.getInstance().getHydroDateFormat();
            hypeClient.setSimulationTime(
                simulationId,
                scenario.getBdate(),
                df.format(input.getStartDate()),
                df.format(input.getEndDate()));

            final String executionId = hypeClient.runSimulation(simulationId);

            runinfo.setExecutionId(executionId);
            runinfo.setSimulationId(simulationId);
            runinfo.setBasinId((Integer)hwBean.getProperty("basin_id")); // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot perform simulation";          // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, runinfo);

            cidsBean.setProperty("runinfo", writer.toString());                           // NOI18N
            cidsBean = cidsBean.persist();
        } catch (final Exception ex) {
            final String message = "cannot store runinfo: " + hwBean.getProperty("name"); // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        return new SimulationWatchable(cidsBean, getRunInfo());
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (!(getWatchable() instanceof SimulationWatchable)) {
            throw new IllegalStateException("cannot create output if there is no valid watchable"); // NOI18N
        }

        try {
            final SimulationInput input = inputFromRun(cidsBean);
            final CidsBean hwBean = SMSUtils.fetchCidsBean(input.getHydrologyWorkspaceId(), "HYDROLOGY_WORKSPACE"); // NOI18N

            final String domain = SessionManager.getSession().getUser().getDomain();
            final MetaClass tsClass = ClassCacheMultiple.getMetaClass(domain, "TIMESERIES"); // NOI18N

            final SimulationWatchable watchable = (SimulationWatchable)getWatchable();
            final SimulationOutput output = new SimulationOutput();

            for (final TimeseriesRetrieverConfig cfg : watchable.getResults()) {
                CidsBean tsBean = tsClass.getEmptyInstance().getBean();
                tsBean.setProperty("name", cfg.getOffering());
                tsBean.setProperty("uri", cfg.toUrl());        // NOI18N
                tsBean.setProperty("forecast", Boolean.FALSE); // NOI18N
                tsBean = tsBean.persist();

                output.addTimeseries(tsBean.getMetaObject().getID());
            }

            return SMSUtils.createModelOutput("Result of simulation run " + cidsBean.getProperty("name"),
                    output,
                    Model.HY_SIM);
        } catch (final Exception ex) {
            final String message = "cannot create output"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    @Override
    protected String getReloadId() {
        try {
            final SimulationInput input = inputFromRun(cidsBean);

            return "hydrology.localmodel." + input.getHydrologyWorkspaceId() + "simulation.*"; // NOI18N
        } catch (final IOException ex) {
            LOG.warn("cannot fetch input from run, reload id cannot be built", ex);            // NOI18N

            return null;
        }
    }

    @Override
    public SimulationRunInfo getRunInfo() {
        return SMSUtils.getRunInfo(cidsBean, SimulationRunInfo.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   runBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException            DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private SimulationInput inputFromRun(final CidsBean runBean) throws IOException {
        final Object resource = SMSUtils.inputFromRun(runBean);

        if (!(resource instanceof SimulationInput)) {
            throw new IllegalStateException("illegal simulation input resource: " + resource); // NOI18N
        }

        return (SimulationInput)resource;
    }
}
