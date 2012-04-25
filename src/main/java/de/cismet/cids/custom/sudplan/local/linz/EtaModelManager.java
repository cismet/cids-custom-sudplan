/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import at.ac.ait.enviro.sudplan.clientutil.SudplanSOSHelper;
import at.ac.ait.enviro.sudplan.clientutil.SudplanSPSHelper;
import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.util.text.ISO8601DateFormat;

import com.vividsolutions.jts.geom.Envelope;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.text.DateFormat;

import java.util.*;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.local.linz.wizard.SwmmPlusEtaWizardAction;
import de.cismet.cids.custom.sudplan.rainfall.RainfallDownscalingModelManager;
import de.cismet.cids.custom.sudplan.rainfall.RainfallRunInfo;
import de.cismet.cids.custom.sudplan.server.trigger.SwmmResultGeoserverUpdater;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaModelManager.class);
    public static final String TABLENAME_CSOS = SwmmPlusEtaWizardAction.TABLENAME_CSOS;
    public static final String TABLENAME_LINZ_SWMM_RESULT = "linz_swmm_result";
    public static final String TABLENAME_LINZ_ETA_RESULT = "linz_eta_result";
    public static final int MAX_STEPS = 5;

    //~ Instance fields --------------------------------------------------------

    private final String modelSosEndpoint = "http://sudplan.ait.ac.at:8081/";
    private SudplanSPSHelper.Task spsTask;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (!(getWatchable() instanceof SwmmWatchable)) {
            throw new IllegalStateException("cannot create output if there is no valid watchable"); // NOI18N
        }

        final SwmmWatchable watch = (SwmmWatchable)this.getWatchable();
        final String spsRunId = watch.getSwmmRunInfo().getRunId();

        // we need the ta input to sync ids and the set the swmm output
        try {
            final EtaInput etaInput = (EtaInput)this.getUR();
            if (etaInput.getSwmmRun() == -1) {
                final String message = "ETA run without SWMM run";
                LOG.error(message);
                this.fireBroken(message);
                throw new IOException(message);
            }

            LOG.info("creating output beans for ETA RUN '" + cidsBean + "' ("
                        + cidsBean.getProperty("id") + ") and SWMM RUN '" + etaInput.getSwmmRunName()
                        + "' (" + etaInput.getSwmmRun() + ")"); // NOI18N

            // update the swmm run too
            // .................................................................
            CidsBean swmmRun = SMSUtils.fetchCidsBean(etaInput.getSwmmRun(), SMSUtils.TABLENAME_MODELRUN);
            // ???:

            final SwmmOutput swmmOutput = watch.getSwmmOutput();
            swmmOutput.setSwmmProject(etaInput.getSwmmProject());
            swmmOutput.setSwmmRun(etaInput.getSwmmRun());
            swmmOutput.setSwmmRunName(etaInput.getSwmmRunName());
            swmmOutput.synchronizeCsoIds(etaInput.getEtaConfigurations());
            final CidsBean swmmModelOutput = SMSUtils.createModelOutput("Modellergebnisse "
                            + swmmOutput.getSwmmRunName(), // NOI18N
                    swmmOutput,
                    SMSUtils.Model.SWMM);
            // here we create the swmmm model output and update the swmm run
            // this is normally performed by the swmm model manager (watchable)
            // FIXME: separate when independet eta calculation is available
            final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
            swmmRun.setProperty(
                "finished", // NOI18N
                dateConverter.convertReverse(GregorianCalendar.getInstance().getTime()));
            swmmRun.setProperty("modeloutput", swmmModelOutput); // NOI18N
            swmmRun = swmmRun.persist();
            // .................................................................

            // TODO: update the eta input with the swmm output
            // final Manager inputManager = SMSUtils.loadManagerFromRun(swmmRun, ManagerType.INPUT);
            // inputManager.setCidsBean((CidsBean)swmmRun.getProperty("modelinput")); // NOI18N
            // etaInput.setCsoOverflows(swmmOutput.getCsoOverflows());
            // how to persist?

            // update eta output
            final EtaOutput etaOutput = watch.getEtaOutput();
            etaOutput.setSwmmRun(etaInput.getSwmmRun());
            // this.cidsBean == etaRun
            etaOutput.setEtaRun((Integer)this.cidsBean.getProperty("id"));
            etaOutput.setEtaRunName((String)this.cidsBean.getProperty("name"));
            final float totalOverflowVolume = this.computeTotalOverflowVolume(etaInput, swmmOutput);
            etaOutput.setTotalOverflowVolume(totalOverflowVolume);
            final CidsBean etaModelOutput = SMSUtils.createModelOutput("Modellergebnisse " + etaOutput.getEtaRunName(), // NOI18N
                    etaOutput,
                    SMSUtils.Model.LINZ_ETA);

            this.updateCSOs(swmmOutput, etaOutput);
            return etaModelOutput.persist();
        } catch (final Exception e) {
            final String message = "cannot get results for SPS SWMM run: " + spsRunId; // NOI18N
            LOG.error(message, e);
            this.fireBroken(message);
            throw new IOException(message, e);
        }
    }

    /**
     * updates the model results (swmm+eta) attached to the CSO objects.
     *
     * @param   swmmOutput  DOCUMENT ME!
     * @param   etaOutput   totalOverflowVolume DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void updateCSOs(final SwmmOutput swmmOutput, final EtaOutput etaOutput) throws IOException {
        LOG.info("updating " + swmmOutput.getCsoOverflows().size() + " CSOs with model results for SWMM Run {"
                    + etaOutput.getSwmmRun() + "} and ETA RUN {" + etaOutput.getEtaRun() + "}");
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass swmmResultClass = ClassCacheMultiple.getMetaClass(domain, TABLENAME_LINZ_SWMM_RESULT);
        final MetaClass etaResultClass = ClassCacheMultiple.getMetaClass(domain, TABLENAME_LINZ_ETA_RESULT);
        CidsBean etaResultBean = null;

        try {
            // first we create one eta reuslt isntance for all CSOs
            etaResultBean = etaResultClass.getEmptyInstance().getBean();
            etaResultBean.setProperty("name", etaOutput.getEtaRunName());
            etaResultBean.setProperty("eta_scenario_id", etaOutput.getEtaRun());
            etaResultBean.setProperty("swmm_scenario_id", etaOutput.getSwmmRun());
            etaResultBean.setProperty("eta_sed_required", etaOutput.getEtaSedRequired());
            etaResultBean.setProperty("eta_sed_actual", etaOutput.getEtaSedActual());
            etaResultBean.setProperty("eta_hyd_required", etaOutput.getEtaHydRequired());
            etaResultBean.setProperty("eta_hyd_actual", etaOutput.getEtaHydActual());
            etaResultBean.setProperty("r720", etaOutput.getR720());
            etaResultBean.setProperty("total_overflow_volume", etaOutput.getTotalOverflowVolume());

            etaResultBean = etaResultBean.persist();
        } catch (Throwable t) {
            final String message = "could not save eta result '" + etaOutput.getEtaRunName() + "': " + t.getMessage();
            LOG.error(message, t);
            this.fireBroken(message);
            throw new IOException(message, t);
        }

        for (final CsoOverflow csoOverflow : swmmOutput.getCsoOverflows().values()) {
            // we know the ids! :-)
            final CidsBean csoBean = SMSUtils.fetchCidsBean(csoOverflow.getCso(), TABLENAME_CSOS);
            if (csoBean != null) {
                try {
                    // todo: once swmm + eta are separate runs, we have to split saving  eta + swmm results

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("adding swmm & eta results (" + swmmOutput.getSwmmRunName() + ") to CSO '"
                                    + csoOverflow.getName() + "'");
                    }

                    final CidsBean swmmResultBean = swmmResultClass.getEmptyInstance().getBean();
                    swmmResultBean.setProperty("name", swmmOutput.getSwmmRunName());
                    swmmResultBean.setProperty("swmm_scenario_id", swmmOutput.getSwmmRun());
                    swmmResultBean.setProperty("overflow_frequency", csoOverflow.getOverflowFrequency());
                    swmmResultBean.setProperty("overflow_duration", csoOverflow.getOverflowDuration());
                    swmmResultBean.setProperty("overflow_volume", csoOverflow.getOverflowVolume());

                    if (etaResultBean != null) {
                        final Collection<CidsBean> etaResults = (Collection)swmmResultBean.getProperty("eta_results"); // NOI18N
                        etaResults.add(etaResultBean);
                    } else {
                        LOG.warn("no eta result bean available for swmm result '" + swmmOutput.getSwmmRunName() + "'");
                    }

                    final Collection<CidsBean> swmmResults = (Collection)csoBean.getProperty("swmm_results"); // NOI18N
                    swmmResults.add(swmmResultBean);
                    csoBean.persist();
                } catch (Exception ex) {
                    final String message = "could not update  CSO '" + csoOverflow.getName() + "': " + ex.getMessage();
                    LOG.error(message, ex);
                    this.fireBroken(message);
                    throw new IOException(message, ex);
                }
            } else {
                LOG.error("CSO '" + csoOverflow.getName() + "' with id " + csoOverflow.getCso()
                            + " not found in database!");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   etaInput    DOCUMENT ME!
     * @param   swmmOutput  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private float computeTotalOverflowVolume(final EtaInput etaInput, final SwmmOutput swmmOutput) {
        float totalOverflowVolume = 0;
        if (LOG.isDebugEnabled()) {
            LOG.debug("computing total overflow volume for " + etaInput.getEtaConfigurations().size() + " CSOs");
        }
        if (etaInput.getEtaConfigurations().size() != swmmOutput.getCsoOverflows().size()) {
            LOG.warn("cannot compute Total Overflow Volume, eta cso input ("
                        + etaInput.getEtaConfigurations().size() + ") and swmm overflow output "
                        + "(" + swmmOutput.getCsoOverflows() + ") size missmatch");
        } else {
            int i = 0;
            for (final EtaConfiguration etaConfiguration : etaInput.getEtaConfigurations()) {
                final CsoOverflow csoOverflow = swmmOutput.getCsoOverflows().get(etaConfiguration.getName());
                if (csoOverflow != null) {
                    if (etaConfiguration.isEnabled()) {
                        totalOverflowVolume += csoOverflow.getOverflowVolume();
                        i++;
                    }
                } else {
                    LOG.warn("cannot consider Overflow Volume of cso '"
                                + etaConfiguration.getName() + "': not in result list of overflows");
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(i + " out of " + etaInput.getEtaConfigurations().size()
                            + " CSOs considered in total overflow volume calculation");
            }
        }

        return totalOverflowVolume;
    }

    @Override
    protected String getReloadId() {
        return "local.linz.*";
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        if (cidsBean == null) {
            throw new IllegalStateException("cidsBean not set"); // NOI18N
        }

        final SwmmRunInfo runInfo = this.getRunInfo();
        if (runInfo == null) {
            throw new IllegalStateException("run info not set"); // NOI18N
        }

        if (runInfo.isCanceled() || runInfo.isBroken()) {
            final String message = "run '" + cidsBean + "' is canceled  or broken, ignoring run";
            LOG.warn(message);
            throw new IllegalStateException(message); // NOI18N
        }

        return new EtaWatchable(this.cidsBean);
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing SWMM+ETA"); // NOI18N
        }

        fireProgressed(
            0,
            MAX_STEPS,
            NbBundle.getMessage(EtaModelManager.class,
                "EtaModelManager.prepareExecution().progress.prepare"));

        final EtaInput etaInput = (EtaInput)this.getUR();

        if (etaInput.getSwmmRun() == -1) {
            final String message = "ETA run without SWMM run";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        CidsBean swmmRun = SMSUtils.fetchCidsBean(etaInput.getSwmmRun(), SMSUtils.TABLENAME_MODELRUN);
        final Manager inputManager = SMSUtils.loadManagerFromRun(swmmRun, ManagerType.INPUT);
        inputManager.setCidsBean((CidsBean)swmmRun.getProperty("modelinput")); // NOI18N
        final SwmmInput swmmInput = (SwmmInput)inputManager.getUR();

        final EtaRunInfo etaRunInfo = new EtaRunInfo();
        final SwmmRunInfo swmmRunInfo = new SwmmRunInfo();

        LOG.info("executing run for model " + swmmInput.getInpFile());

        assert !swmmInput.getTimeseriesURLs().isEmpty() : "improperly configures swmm run, no timiseries configured";
        final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(swmmInput.getTimeseriesURLs(0));
        LOG.info("STEP 1: retrieving timeseries from " + swmmInput.getTimeseriesURLs(0));

        TimeSeries rainTS;
        if (config.getProtocol().equals(TimeseriesRetrieverConfig.PROTOCOL_TSTB)) {
            LOG.info("downloading timeseries from SOS: " + config);

            fireProgressed(
                1,
                MAX_STEPS,
                NbBundle.getMessage(EtaModelManager.class,
                    "EtaModelManager.prepareExecution().progress.download.sos"));

            final SudplanSOSHelper sensorSOSHelper = new SudplanSOSHelper(config.getLocation().toString());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Download timeseries data for offering '" + config.getOffering()
                            + "', time intervall '" + config.getInterval() + "'");
            }

            rainTS = sensorSOSHelper.getTimeseries(config.getOffering(), config.getInterval());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Downloaded {} values" + rainTS.getTimeStamps().size());
            }
        } else if (config.getProtocol().equals(TimeseriesRetrieverConfig.PROTOCOL_DAV)) {
            LOG.info("downloading timeseries from WEBDAV: " + config);

            fireProgressed(
                1,
                MAX_STEPS,
                NbBundle.getMessage(
                    EtaModelManager.class,
                    "EtaModelManager.prepareExecution().progress.download.webdav"));

            try {
                final Future<TimeSeries> rainTsFuture = TimeseriesRetriever.getInstance().retrieve(config);
                rainTS = rainTsFuture.get();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("finished downloading timeseries from WEBDAV");
                }
            } catch (Throwable t) {
                final String message = "Could not download rain timeseries from '"
                            + config.getProtocol() + "'";
                LOG.error(message, t);
                this.fireBroken(message);
                throw new IOException(message, t);
            }
        } else {
            final String message = "Unsupported timeseries protocol: '" + config.getProtocol() + "'";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Upload rain timeseries as model input to " + modelSosEndpoint);
        }

        fireProgressed(
            2,
            MAX_STEPS,
            NbBundle.getMessage(EtaModelManager.class,
                "EtaModelManager.prepareExecution().progress.upload"));

        final SudplanSOSHelper modelSOSHelper = new SudplanSOSHelper(modelSosEndpoint);
        // Creating a new timeseries datapoint on the SOS-T is a 2 step process:
        // 1. createDatapoint (needs SensorML)
        // 2. putTimeserise (timeseries with all needed properties - and, yes, values too)
        //
        // Get SensorML from somewhere..
        final Map<String, Object> dpProps = new HashMap<String, Object>();
        final BufferedReader r = new BufferedReader(new InputStreamReader(
                    EtaModelManager.class.getResourceAsStream("smlSensor.xml")));
        final StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = r.readLine()) != null) {
            sb.append(s);
        }
        r.close();
        final String sensorml = sb.toString();
        rainTS.setTSProperty(TimeSeries.SENSORML, sensorml);

        // Check if all needed properties are available.. They should already be contained in the TimeSeries I got from
        // the SOSLinzSensorServer Additionaly to the normal properties (see ts-docu) a minimum set is for compatibility
        // with SOS and SPS needed: TimeSeries.GEOMETRY, "ts:coordinate_system",....
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_OBSERVED_PROPERTY_URNS)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_OBSERVED_PROPERTY_URNS,
                new String[] {
                    config.getObsProp()
                    // "urn:ogc:def:property:OGC:1.0:precipitation"
                });
            LOG.warn("Inserting missing Timeseries property" + TimeSeries.VALUE_OBSERVED_PROPERTY_URNS);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.DESCRIPTION)) {
            rainTS.setTSProperty(PropertyNames.DESCRIPTION, "Rain as input to the Linz model");
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.DESCRIPTION);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.COORDINATE_SYSTEM)) {
            rainTS.setTSProperty(PropertyNames.COORDINATE_SYSTEM, "EPSG:3423");
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.COORDINATE_SYSTEM);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.SPATIAL_RESOLUTION)) {
            rainTS.setTSProperty(
                PropertyNames.SPATIAL_RESOLUTION,
                new Integer[] { 1 }); // Need to be 1 value!!
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.SPATIAL_RESOLUTION);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.TEMPORAL_RESOLUTION)) {
            rainTS.setTSProperty(PropertyNames.TEMPORAL_RESOLUTION, "NONE");
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.TEMPORAL_RESOLUTION + "' = " + "NONE");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_JAVA_CLASS_NAMES)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_JAVA_CLASS_NAMES,
                new String[] { Float.class.getName() });
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.VALUE_JAVA_CLASS_NAMES + "' = "
                        + Float.class.getName());
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_TYPES)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_TYPES,
                new String[] { TimeSeries.VALUE_TYPE_NUMBER });
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.VALUE_TYPES + "'");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_UNITS)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_UNITS,
                new String[] { "urn:ogc:def:uom:OGC:mm" });
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.VALUE_UNITS + "' = urn:ogc:def:uom:OGC:mm");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.GEOMETRY)) {
            rainTS.setTSProperty(TimeSeries.GEOMETRY, new Envelope(14.18, 14.38, 48.24, 48.34));
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.GEOMETRY
                        + "' = 14.18, 14.38, 48.24, 48.34");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.AVAILABLE_DATA_MIN)) {
            rainTS.setTSProperty(TimeSeries.AVAILABLE_DATA_MIN, rainTS.getTimeStamps().first().asDate());
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.AVAILABLE_DATA_MIN + "' = "
                        + rainTS.getTimeStamps().first().asDate());
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.AVAILABLE_DATA_MAX)) {
            rainTS.setTSProperty(TimeSeries.AVAILABLE_DATA_MAX, rainTS.getTimeStamps().last().asDate());
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.AVAILABLE_DATA_MAX + "' = "
                        + rainTS.getTimeStamps().last().asDate());
        }

        rainTS.setTSProperty(PropertyNames.DESCRIPTION, "Data from " + config.getOffering());
        final String modelOffering = modelSOSHelper.putNewTimeseries(rainTS);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Uploaded model input");

            LOG.debug("connecting to model SPS " + swmmRunInfo.getSpsUrl() + " and executing model "
                        + swmmRunInfo.getModelName());
        }

        fireProgressed(
            3,
            MAX_STEPS,
            NbBundle.getMessage(EtaModelManager.class,
                "EtaModelManager.prepareExecution().progress.dispatch"));

        final SudplanSPSHelper modelSPSHelper = new SudplanSPSHelper(swmmRunInfo.getSpsUrl());
        final DateFormat isoDf = new ISO8601DateFormat();
        this.spsTask = modelSPSHelper.createTask(swmmRunInfo.getModelName());

        try {
            spsTask.setParameter("start", isoDf.format(swmmInput.getStartDate()));
            spsTask.setParameter("end", isoDf.format(swmmInput.getEndDate()));
        } catch (Throwable t) {
            LOG.error(t.getMessage());
            this.fireBroken(t.getMessage());
            throw new IOException(t.getMessage(), t);
        }

        spsTask.setParameter("dat", modelOffering);
        spsTask.setParameter("inp", swmmInput.getInpFile());

        // FIXME: eta calculation on client side
        spsTask.setParameter("eta", "linz_v1");

        // and start the task
        spsTask.start();

        swmmRunInfo.setRunId(spsTask.getTaskID());
        etaRunInfo.setRunId(spsTask.getTaskID());
        if (LOG.isDebugEnabled()) {
            LOG.debug("model run started with task id" + swmmRunInfo.getRunId());
        }

        fireProgressed(
            4,
            MAX_STEPS,
            NbBundle.getMessage(EtaModelManager.class,
                "EtaModelManager.prepareExecution().progress.save"));

        try {
            final ObjectMapper mapper = new ObjectMapper();
            StringWriter writer = new StringWriter();

            mapper.writeValue(writer, swmmRunInfo);
            swmmRun.setProperty("runinfo", writer.toString()); // NOI18N
            swmmRun = swmmRun.persist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("SWMM RunInfo for task '" + swmmRunInfo.getRunId() + "' of SWMM Run '"
                            + swmmRun.getMetaObject().getName() + "' saved");
            }

            writer = new StringWriter();
            mapper.writeValue(writer, etaRunInfo);
            cidsBean.setProperty("runinfo", writer.toString());                       // NOI18N
            cidsBean = cidsBean.persist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("ETA RunInfo for task '" + etaRunInfo.getRunId() + "' of ETA Run '"
                            + cidsBean.getMetaObject().getName() + "' saved");
            }
        } catch (final Exception ex) {
            final String message = "Cannot store runinfo: " + swmmRunInfo.getRunId(); // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        // now set to indeterminate
        fireProgressed(
            -1,
            -1,
            NbBundle.getMessage(
                EtaModelManager.class,
                "EtaModelManager.prepareExecution().progress.running",
                etaRunInfo.getRunId()));
    }

    @Override
    public EtaRunInfo getRunInfo() {
        return SMSUtils.<EtaRunInfo>getRunInfo(cidsBean, EtaRunInfo.class);
    }
}
