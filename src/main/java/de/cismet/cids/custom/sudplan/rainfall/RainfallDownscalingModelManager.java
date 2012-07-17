/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import Sirius.server.middleware.types.MetaClass;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.NbBundle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.SMSUtils.Model;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.CrsTransformer;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
// NOTE: this could be implemented much smoother but wth... ftt
public final class RainfallDownscalingModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RainfallDownscalingModelManager.class);
    public static final String PARAM_CLIMATE_SCENARIO = "climate_scenario";             // NOI18N
    public static final String PARAM_SOURCE_RAIN = "source_rain";                       // NOI18N
    public static final String PARAM_CENTER_TIME = "center_time";                       // NOI18N
    public static final String RF_SOS_LOOKUP = "rainfall_sos_lookup";                   // NOI18N
    public static final String RF_SPS_LOOKUP = "rainfall_sps_lookup";                   // NOI18N
    public static final String RF_SOS_URL = "http://sudplan.ait.ac.at:8084/";           // NOI18N
    public static final String RF_SPS_URL = "http://sudplan.ait.ac.at:8085/";           // NOI18N
    public static final String RF_TS_DS_PROCEDURE = "Rain_Timeseries_Downscaling";      // NOI18N
    public static final String RF_IDF_DS_PROCEDURE = "IDF_Rain_Timeseries_Downscaling"; // NOI18N
    public static final int MAX_STEPS = 5;

    //~ Instance fields --------------------------------------------------------

    private transient Datapoint runningTask;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (!(getWatchable() instanceof RainfallDSWatchable)) {
            throw new IllegalStateException("cannot create output if there is no valid watchable"); // NOI18N
        }

        final RainfallDSWatchable watchable = (RainfallDSWatchable)getWatchable();
        final Manager m = SMSUtils.loadManagerFromRun(cidsBean, ManagerType.INPUT);
        m.setCidsBean((CidsBean)cidsBean.getProperty("modelinput")); // NOI18N
        final RainfallDownscalingInput input = (RainfallDownscalingInput)m.getUR();
        final CidsBean rfBean = input.fetchRainfallObject();
        final MetaClass tsClass = rfBean.getMetaObject().getMetaClass();

        CidsBean dsBean = tsClass.getEmptyInstance().getBean();
        try {
            final String rfObjName = (String)rfBean.getProperty("name"); // NOI18N

            if (SMSUtils.TABLENAME_TIMESERIES.equals(input.getRainfallObjectTableName())) {
                final String urlString = watchable.getOrigResolutionResult().toExternalForm();
                final String baseurl = urlString.substring(0, urlString.lastIndexOf('/'));
                final String resultFullName = urlString.substring(urlString.lastIndexOf('/') + 1);
                final String resultName = resultFullName.substring(0, resultFullName.lastIndexOf('_'));
                final String resultRes = resultFullName.substring(resultFullName.lastIndexOf('_') + 1);
                final TimeseriesRetrieverConfig config = new TimeseriesRetrieverConfig(
                        TimeseriesRetrieverConfig.PROTOCOL_DAV,
                        null,
                        new URL(baseurl),
                        "urn:ogc:object:"  // NOI18N
                                + resultName
                                + ":prec:" // NOI18N
                                + resultRes,
                        null,
                        Variable.PRECIPITATION.getPropertyKey(),
                        resultFullName,
                        null,
                        null);
                dsBean.setProperty("uri", config.toUrl()); // NOI18N
                dsBean.setProperty("station", rfBean.getProperty("station")); // NOI18N
            } else {
                final ObjectMapper mapper = new ObjectMapper();
                final StringWriter sw = new StringWriter();

                mapper.writeValue(sw, watchable.getResultCurve());

                dsBean.setProperty("uri", sw.toString());               // NOI18N
                dsBean.setProperty("geom", rfBean.getProperty("geom")); // NOI18N
                dsBean.setProperty("year", input.getTargetYear());      // NOI18N
            }

            dsBean.setProperty("name", rfObjName + " downscaled (taskid=" + watchable.getRunId() + ")"); // NOI18N
            dsBean.setProperty("converter", rfBean.getProperty("converter"));                            // NOI18N
            dsBean.setProperty("description", "Downscaled timeseries");                                  // NOI18N
            dsBean.setProperty("forecast", Boolean.TRUE);                                                // NOI18N

            dsBean = dsBean.persist();
        } catch (final Exception ex) {
            final String message = "Cannot create downscaled bean"; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        final RainfallDownscalingOutput output = new RainfallDownscalingOutput();
        try {
            output.setModelInputId((Integer)cidsBean.getProperty("modelinput.id"));           // NOI18N
            output.setModelRunId((Integer)cidsBean.getProperty("id"));                        // NOI18N
            output.setRfObjInputId(input.getRainfallObjectId());
            output.setRfObjInputName(NbBundle.getMessage(
                    RainfallDownscalingModelManager.class,
                    "RainfallDownscalingModelManager.createOutputBean().output.inputName"));  // NOI18N
            output.setRfObjResultId((Integer)dsBean.getProperty("id"));                       // NOI18N
            output.setRfObjResultName(NbBundle.getMessage(
                    RainfallDownscalingModelManager.class,
                    "RainfallDownscalingModelManager.createOutputBean().output.resultName")); // NOI18N
            output.setRfObjTableName(input.getRainfallObjectTableName());
        } catch (final Exception e) {
            final String message = "cannot create model output";                              // NOI18N
            LOG.error(message, e);
            this.fireBroken(message);
            throw new IOException(message, e);
        }

        return SMSUtils.createModelOutput(NbBundle.getMessage(
                    RainfallDownscalingModelManager.class,
                    "RainfallDownscalingModelManager.createOutputBean().output.name", // NOI18N
                    watchable.getRunId()),
                output,
                Model.RF_DS);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private String dispatchDownscaling(final Map.Entry<DataHandler, Properties> input) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("preparing downscaling run"); // NOI18N
        }

        final DataHandler spsHandler;
        try {
            spsHandler = DataHandlerCache.getInstance().getSPSDataHandler(RF_SPS_LOOKUP, RF_SPS_URL);
        } catch (final DataHandlerCacheException ex) {
            final String message = "cannot fetch datahandler"; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        final Properties filter = new Properties();

        // input == null means that it is IDF downscaling
        if (input == null) {
            filter.put(TimeSeries.PROCEDURE, RF_IDF_DS_PROCEDURE);
        } else {
            filter.put(TimeSeries.PROCEDURE, RF_TS_DS_PROCEDURE);
        }

        final Datapoint dp = spsHandler.createDatapoint(filter, null, DataHandler.Access.READ_WRITE);

        final RainfallDownscalingInput rfInput = inputFromRun(cidsBean);
        final CidsBean rfObj = rfInput.fetchRainfallObject();

        final TimeSeries ts = new TimeSeriesImpl(dp.getProperties());
        final TimeStamp now = new TimeStamp();
        ts.setValue(now, PARAM_CLIMATE_SCENARIO, rfInput.getScenario());

        if (input == null) {
            ts.setValue(now, "coordinate_system", "EPSG:4326");                                                // NOI18N
            final Geometry geom = (Geometry)rfObj.getProperty("geom.geo_field");                               // NOI18N
            ts.setValue(now, "coordinate_x", String.valueOf(geom.getCentroid().getX()));                       // NOI18N
            ts.setValue(now, "coordinate_y", String.valueOf(geom.getCentroid().getY()));                       // NOI18N
            ts.setValue(now, "historical_year", (Integer)rfObj.getProperty("year") + "-01-01T00:00:00-00:00"); // NOI18N
            ts.setValue(now, "future_year", rfInput.getTargetYear() + "-01-01T00:00:00-00:00");                // NOI18N
            ts.setValue(now, "idf_data", convertIDF(rfObj));                                                   // NOI18N
        } else {
            // the f***n uploaded ts only has a procedure and no offering, and on top of the s**t pile this is not the
            // input param for the sps either. we have to strip some bull****
            final String[] split = input.getValue().getProperty(TimeSeries.PROCEDURE).split(":"); // NOI18N
            final String sourceRain = split[split.length - 2] + ":" + split[split.length - 1];    // NOI18N

            ts.setValue(now, PARAM_SOURCE_RAIN, sourceRain);
            ts.setValue(now, PARAM_CENTER_TIME, rfInput.getTargetYear() + "-01-01T00:00:00-00:00"); // NOI18N
            ts.setValue(now, "frequency_adjust", String.valueOf(rfInput.isFrequencyAdjustment()));  // NOI18N
        }

        ts.setValue(now, PropertyNames.TaskAction, PropertyNames.TaskActionStart);

        // start sps task
        if (LOG.isDebugEnabled()) {
            LOG.debug("start params: [" + rfInput.getScenario() // NOI18N
                        + " | " + rfInput.getTargetYear() + "-01-01T10:10:10-00:00" // NOI18N
                        + " | " + PropertyNames.TaskActionStart); // NOI18N
        }

        dp.putTimeSeries(ts);

        // the f****n handler does not know about the new task, thus asking for it results in no datapoints, we do a
        // special implementation to overcome this s**t
        runningTask = dp;

        return (String)dp.getProperties().get(PropertyNames.TASK_ID);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idfBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private String convertIDF(final CidsBean idfBean) {
        final ObjectMapper mapper = new ObjectMapper();
        final String uri = (String)idfBean.getProperty("uri"); // NOI18N

        final IDFCurve idf;
        try {
            idf = mapper.readValue(uri, IDFCurve.class);
        } catch (Exception ex) {
            final String message = "cannot read idf data from uri"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        return idf.toTSTBFormat();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException                    DOCUMENT ME!
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    private Map.Entry<DataHandler, Properties> uploadTimeseries() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("start uploading timeseries" + this); // NOI18N
        }

        final RainfallDownscalingInput rfInput = inputFromRun(cidsBean);
        final CidsBean tsBean = rfInput.fetchRainfallObject();

        if (tsBean == null) {
            final String message = "Cannot read timeseries, timeseries bean is null";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        assert tsBean != null : "Timeseries is null"; // NOI18N

        final String tsUri = (String)tsBean.getProperty("uri");                    // NOI18N
        final TimeseriesRetrieverConfig config;
        if (tsUri == null) {
            final String message = "Cannot read timeseries from uri, uri is null"; // NOI18N
            LOG.error(message);                                                    // NOI18N
            this.fireBroken(message);
            throw new IOException(message);
        } else {
            config = TimeseriesRetrieverConfig.fromUrl(tsUri);
        }

        assert config != null : "invalid must not be null"; // NOI18N

        final DataHandler inputDH;
        try {
            inputDH = DataHandlerCache.getInstance().getSOSDataHandler(RF_SOS_LOOKUP, RF_SOS_URL);
        } catch (final DataHandlerCacheException ex) {
            final String message = "cannot fetch datahandler"; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        final Map<String, Object> dpProps = new HashMap<String, Object>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("loading sensor ml"); // NOI18N
        }

        // TODO: maybe omitted?
        final String sensorML = readResource("rainfallCSSensorML.xml"); // NOI18N
        dpProps.put(TimeSeries.SENSORML, sensorML);                     // NOI18N

        final TimeSeries ts;
        try {
            final Future<TimeSeries> tsFuture = TimeseriesRetriever.getInstance()
                        .retrieve(config, TimeSeriesSerializer.getInstance());

            ts = tsFuture.get();

            final CidsBean stationBean = (CidsBean)tsBean.getProperty("station");                        // NOI18N
            if (stationBean != null) {
                final CidsBean geomBean = (CidsBean)stationBean.getProperty("geom");                     // NOI18N
                if (geomBean != null) {
                    final Geometry geom = (Geometry)geomBean.getProperty("geo_field");                   // NOI18N
                    if (geom != null) {
                        final Geometry epsgGeom = CrsTransformer.transformToGivenCrs(geom, "EPSG:4326"); // NOI18N
                        ts.setTSProperty(TimeSeries.GEOMETRY, epsgGeom.getEnvelopeInternal());
                    }
                }
            }

            ts.setTSProperty(
                PropertyNames.SPATIAL_RESOLUTION,
                new Integer[] { 1, 1 });
            ts.setTSProperty(PropertyNames.TEMPORAL_RESOLUTION, "NONE");            // NOI18N
            ts.setTSProperty(PropertyNames.COORDINATE_SYSTEM, "EPSG:4326");         // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot fetch timeseries for config: " + config; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        final Datapoint dp = inputDH.createDatapoint(new Properties(), dpProps, DataHandler.Access.READ_WRITE);

        dp.putTimeSeries(ts);

        final Properties properties = dp.getFilter();
        if (LOG.isDebugEnabled()) {
            LOG.debug("upload timeseries finished: dh=" + inputDH + " || props=" + properties + " || " + this); // NOI18N
        }

        return new Map.Entry<DataHandler, Properties>() {

                @Override
                public DataHandler getKey() {
                    return inputDH;
                }

                @Override
                public Properties getValue() {
                    return properties;
                }

                @Override
                public Properties setValue(final Properties value) {
                    throw new UnsupportedOperationException("immutable entry"); // NOI18N
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param   name  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public String readResource(final String name) {
        final InputStream is = this.getClass().getResourceAsStream(name);
        if (is == null) {
            throw new IllegalStateException("cannot get resource as stream: " + name); // NOI18N
        }

        final BufferedInputStream bis = new BufferedInputStream(is);
        final StringBuilder sb = new StringBuilder(1024);
        final byte[] chars = new byte[1024];
        try {
            int bytesRead = bis.read(chars);
            while (bytesRead > -1) {
                sb.append(new String(chars, 0, bytesRead));
                bytesRead = bis.read(chars);
            }

            return sb.toString();
        } catch (final IOException ex) {
            final String message = "cannot read from resource: " + name; // NOI18N
            LOG.error(message, ex);

            throw new IllegalStateException(message, ex);
        } finally {
            try {
                bis.close();
            } catch (final IOException e) {
                LOG.warn("cannot close input stream", e); // NOI18N
            }
        }
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
    private RainfallDownscalingInput inputFromRun(final CidsBean runBean) throws IOException {
        final Object resource = SMSUtils.inputFromRun(runBean);
        if (!(resource instanceof RainfallDownscalingInput)) {
            throw new IllegalStateException("manager resource is not suited for rainfall downscaling"); // NOI18N
        }

        return (RainfallDownscalingInput)resource;
    }

    @Override
    protected String getReloadId() {
        return "rainfall.*"; // NOI18N
    }

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing rainfall downscaling"); // NOI18N
        }

        fireProgressed(
            0,
            MAX_STEPS,
            NbBundle.getMessage(
                RainfallDownscalingModelManager.class,
                "RainfallDownscalingModelManager.prepareExecution().progress.prepare"));

        final RainfallDownscalingInput input = inputFromRun(cidsBean);
        final Map.Entry<DataHandler, Properties> spsInput;

        fireProgressed(
            1,
            MAX_STEPS,
            NbBundle.getMessage(
                RainfallDownscalingModelManager.class,
                "RainfallDownscalingModelManager.prepareExecution().progress.upload"));
        // upload is only needed in case of timeseries downscaling
        if (SMSUtils.TABLENAME_TIMESERIES.equals(input.getRainfallObjectTableName())) {
            spsInput = uploadTimeseries();
        } else {
            spsInput = null;
        }

        fireProgressed(
            2,
            MAX_STEPS,
            NbBundle.getMessage(
                RainfallDownscalingModelManager.class,
                "RainfallDownscalingModelManager.prepareExecution().progress.dispatch"));
        final String runId = dispatchDownscaling(spsInput);

        fireProgressed(
            3,
            MAX_STEPS,
            NbBundle.getMessage(
                RainfallDownscalingModelManager.class,
                "RainfallDownscalingModelManager.prepareExecution().progress.save"));
        final RainfallRunInfo runInfo = new RainfallRunInfo(runId, RF_SPS_LOOKUP, RF_SPS_URL);
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, runInfo);

            cidsBean.setProperty("runinfo", writer.toString());      // NOI18N
            cidsBean = cidsBean.persist();
        } catch (final Exception ex) {
            final String message = "cannot store runinfo: " + runId; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }
        // now set to indeterminate
        fireProgressed(
            -1,
            -1,
            NbBundle.getMessage(
                RainfallDownscalingModelManager.class,
                "RainfallDownscalingModelManager.prepareExecution().progress.running",
                runInfo.getTaskId()));
    }

    @Override
    public RainfallDSWatchable createWatchable() throws IOException {
        if (cidsBean == null) {
            throw new IllegalStateException("cidsBean not set"); // NOI18N
        }

        final RainfallRunInfo runInfo = this.getRunInfo();

        if (runInfo == null) {
            throw new IllegalStateException("run info not set"); // NOI18N
        }

        if (runInfo.isCanceled() || runInfo.isBroken()) {
            final String message = "run '" + cidsBean + "' is canceled or broken, ignoring run";
            LOG.warn(message);
            throw new IllegalStateException(message);                               // NOI18N
        } else {
            try {
                if (runningTask == null) {
                    final DataHandler dh = DataHandlerCache.getInstance()
                                .getSPSDataHandler(
                                    runInfo.getHandlerLookup(),
                                    runInfo.getHandlerUrl());
                    return new RainfallDSWatchable(cidsBean, dh, runInfo.getTaskId());
                } else {
                    return new RainfallDSWatchable(cidsBean, runInfo.getTaskId(), runningTask);
                }
            } catch (final Exception ex) {
                final String message = "Cannot read runInfo from run: " + cidsBean; // NOI18N
                LOG.error(message, ex);
                this.fireBroken(message);
                throw new IOException(message, ex);
            }
        }
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    @Override
    public RainfallRunInfo getRunInfo() {
        return SMSUtils.<RainfallRunInfo>getRunInfo(cidsBean, RainfallRunInfo.class);
    }
}
