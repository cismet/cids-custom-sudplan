/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import at.ac.ait.enviro.sudplan.clientutil.SudplanSOSHelper;
import at.ac.ait.enviro.sudplan.clientutil.SudplanSPSHelper;
import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.util.text.ISO8601DateFormat;

import com.vividsolutions.jts.geom.Envelope;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.text.DateFormat;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.concurrent.ProgressWatch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaModelManager.class);

    //~ Instance fields --------------------------------------------------------

    private final String modelSosEndpoint = "http://sudplan.ait.ac.at:8081/";
    /** FIXME: entfernen, sobald Instanz nicht mehr benötigt wird */
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating output bean for run: " + cidsBean); // NOI18N
        }

        final SwmmWatchable watch = (SwmmWatchable)this.getWatchable();
        final String runId = watch.getSwmmRunInfo().getRunId();

        try {
            final EtaInput etaInput = (EtaInput)this.getUR();
            if (etaInput.getSwmmRun() == -1) {
                throw new IOException("ETA run without SWMM run");
            }

            CidsBean swmmRun = SMSUtils.fetchCidsBean(etaInput.getSwmmRun(), SMSUtils.TABLENAME_MODELRUN);
            final Manager inputManager = SMSUtils.loadManagerFromRun(swmmRun, ManagerType.INPUT);
            inputManager.setCidsBean((CidsBean)swmmRun.getProperty("modelinput")); // NOI18N

            final SwmmOutput swmmOutput = watch.getSwmmOutput();
            swmmOutput.setSwmmProject(etaInput.getSwmmProject());
            swmmOutput.synchronizeCsoIds(etaInput.getCsoOverflows());
            final CidsBean swmmModelOutput = SMSUtils.createModelOutput("Output of SWMM Run: " + runId, // NOI18N
                    swmmOutput,
                    SMSUtils.Model.SWMM);

            final CidsBean etaModelOutput = SMSUtils.createModelOutput("Output of ETA Run: " + runId, // NOI18N
                    watch.getEtaOutput(),
                    SMSUtils.Model.LINZ_ETA);

            final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
            swmmRun.setProperty(
                "finished", // NOI18N
                dateConverter.convertReverse(GregorianCalendar.getInstance().getTime()));
            swmmRun.setProperty("modeloutput", swmmModelOutput); // NOI18N
            swmmRun = swmmRun.persist();

            return etaModelOutput.persist();
        } catch (final Exception e) {
            final String message = "cannot get results for run: " + runId; // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    @Override
    protected String getReloadId() {
        try {
            final SwmmInput swmmInput = (SwmmInput)getUR();
            return "project_id" + swmmInput.getSwmmProject() + "_scenarios"; // NOI18N
        } catch (final Exception e) {
            LOG.warn("cannot fetch reload id", e);                           // NOI18N

            return null;
        }
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        // FIXME: - spsTask instance
        return new SwmmWatchable(this.cidsBean);
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    @Override
    protected void prepareExecution() throws IOException {
        final EtaInput etaInput = (EtaInput)this.getUR();

        if (etaInput.getSwmmRun() == -1) {
            throw new IOException("ETA run without SWMM run");
        }

        CidsBean swmmRun = SMSUtils.fetchCidsBean(etaInput.getSwmmRun(), SMSUtils.TABLENAME_MODELRUN);
        final Manager inputManager = SMSUtils.loadManagerFromRun(swmmRun, ManagerType.INPUT);
        inputManager.setCidsBean((CidsBean)swmmRun.getProperty("modelinput")); // NOI18N
        final SwmmInput swmmInput = (SwmmInput)inputManager.getUR();

        final EtaRunInfo etaRunInfo = new EtaRunInfo();
        final SwmmRunInfo swmmRunInfo = new SwmmRunInfo();

        LOG.info("executing run for model " + swmmInput.getInpFile());

        assert !swmmInput.getTimeseriesURLs().isEmpty() : "improperly configures swmm run, no timiseries configured";
        final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromTSTBUrl(swmmInput.getTimeseriesURLs(0));
        LOG.info("STEP 1: retrieving timeseries from " + swmmInput.getTimeseriesURLs(0));

        LOG.info("List available sensor data (In the moment only historical rain data are available)");
        final SudplanSOSHelper sensorSOSHelper = new SudplanSOSHelper(config.getLocation().toString());

        LOG.info("Download timeseries data for offering '" + config.getOffering()
                    + "', time intervall '" + config.getInterval() + "'");

        final TimeSeries rainTS = sensorSOSHelper.getTimeseries(config.getOffering(), config.getInterval());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Downloaded {} values" + rainTS.getTimeStamps().size());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Upload rain timeseries as model input to " + modelSosEndpoint);
        }
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
            LOG.warn("Inserting missing Timeseries property}" + PropertyNames.DESCRIPTION);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.COORDINATE_SYSTEM)) {
            rainTS.setTSProperty(PropertyNames.COORDINATE_SYSTEM, "EPSG:3423");
            LOG.warn("Inserting missing Timeseries property" + PropertyNames.COORDINATE_SYSTEM);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.SPATIAL_RESOLUTION)) {
            rainTS.setTSProperty(
                PropertyNames.SPATIAL_RESOLUTION,
                new Integer[] { 1 }); // Need to be 1 value!!
            LOG.warn("Inserting missing Timeseries property" + PropertyNames.SPATIAL_RESOLUTION);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.TEMPORAL_RESOLUTION)) {
            rainTS.setTSProperty(PropertyNames.TEMPORAL_RESOLUTION, "NONE");
            LOG.warn("Inserting missing Timeseries property" + PropertyNames.TEMPORAL_RESOLUTION);
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_JAVA_CLASS_NAMES)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_JAVA_CLASS_NAMES,
                new String[] { Float.class.getName() });
            LOG.warn("Inserting missing Timeseries property" + TimeSeries.VALUE_JAVA_CLASS_NAMES);
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_TYPES)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_TYPES,
                new String[] { TimeSeries.VALUE_TYPE_NUMBER });
            LOG.warn("Inserting missing Timeseries property" + TimeSeries.VALUE_TYPES);
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_UNITS)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_UNITS,
                new String[] { "urn:ogc:def:uom:OGC:mm" });
            LOG.warn("Inserting missing Timeseries property" + TimeSeries.VALUE_UNITS);
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.GEOMETRY)) {
            rainTS.setTSProperty(TimeSeries.GEOMETRY, new Envelope(14.18, 14.38, 48.24, 48.34));
            LOG.warn("Inserting missing Timeseries property" + TimeSeries.GEOMETRY);
        }

        rainTS.setTSProperty(PropertyNames.DESCRIPTION, "Data from " + config.getOffering());
        final String modelOffering = modelSOSHelper.putNewTimeseries(rainTS);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Uploaded model input");

            LOG.debug("connecting to model SPS " + swmmRunInfo.getSpsUrl() + " and executing model "
                        + swmmRunInfo.getModelName());
        }
        final SudplanSPSHelper modelSPSHelper = new SudplanSPSHelper(swmmRunInfo.getSpsUrl());
        final DateFormat isoDf = new ISO8601DateFormat();
        this.spsTask = modelSPSHelper.createTask(swmmRunInfo.getModelName());

        try {
            spsTask.setParameter("start", isoDf.format(swmmInput.getStartDateDate()));
            spsTask.setParameter("end", isoDf.format(swmmInput.getEndDateDate()));
        } catch (Throwable t) {
            LOG.error(t.getMessage());
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
            final String message = "cannot store runinfo: " + swmmRunInfo.getRunId(); // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }
}
