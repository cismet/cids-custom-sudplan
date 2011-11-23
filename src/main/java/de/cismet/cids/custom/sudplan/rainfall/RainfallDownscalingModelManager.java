/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.handler.DatapointListener;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.AbstractModelManager;
import de.cismet.cids.custom.sudplan.DataHandlerCache;
import de.cismet.cids.custom.sudplan.DataHandlerCacheException;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.CrsTransformer;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallDownscalingModelManager extends AbstractModelManager {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_MODEL_ID = "ts:sps:sensor_id"; // NOI18N
    public static final String PROP_TASK_ID = "ts:sps:task_id";    // NOI18N
    // TODO: probably align this id with the one in the model database
    public static final String RAIN_TIMESERIES_DOWNSCALING_ID = "Rain_Timeseries_Downscaling"; // NOI18N

    private static final String ENDPOINT = "endpoint";

    private static final transient Logger LOG = Logger.getLogger(RainfallDownscalingModelManager.class);
    private static final String PARAM_REF_RAIN = "param:reference_rain";                    // NOI18N
    private static final String PARAM_RESULT_RAIN = "param:result_rain";                    // NOI18N
    private static final String PARAM_REF_RAIN_30 = "param:result_rain_30";                 // NOI18N
    private static final String PARAM_RESULT_RAIN_30 = "param:result_rain_30_downscaled";   // NOI18N
    private static final String PARAM_REF_RAIN_1D = "param:result_rain_1day";               // NOI18N
    private static final String PARAM_RESULT_RAIN_1D = "param:result_rain_1day_downscaled"; // NOI18N

    public static final String PARAM_CLIMATE_SCENARIO = "climate_scenario";
    public static final String PARAM_SOURCE_RAIN = "source_rain";
    public static final String PARAM_CENTER_TIME = "center_time";

    private static final String RF_SOS_LOOKUP = "rainfall_sos_lookup";           // NOI18N
    private static final String RF_SPS_LOOKUP = "rainfall_sps_lookup";           // NOI18N
    private static final String RF_SOS_URL = "http://enviro3.ait.ac.at:8081/";   // NOI18N
    private static final String RF_SPS_URL = "http://enviro3.ait.ac.at:8082/";   // NOI18N
    private static final String RF_DS_PROCEDURE = "Rain_Timeseries_Downscaling"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient ResultProcessor.CreateOutputRunner outputRunner;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void internalExecute() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing rainfall downscaling"); // NOI18N
        }

        fireProgressed(-1, -1);

        final Map.Entry<DataHandler, Properties> spsInput = uploadTimeseries();

        // actually start the downscaling a ResultProcessor instance will take care of the creation of a modeloutput as
        // soon as the execution has finished
        downscale(spsInput, spsInput.getKey());
    }

    @Override
    protected CidsBean createOutputBean() {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (outputRunner == null) {
            throw new IllegalStateException("output runner has not been created yet"); // NOI18N
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating output bean for run: " + cidsBean);                                    // NOI18N
        }
        try {
            outputRunner.join();
        } catch (final InterruptedException ex) {
            throw new IllegalStateException("interrupted while waiting for outputrunner to join", ex); // NOI18N
        }

        return outputRunner.modelOutput;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   input   DOCUMENT ME!
     * @param   output  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void downscale(final Map.Entry<DataHandler, Properties> input, final DataHandler output)
            throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("preparing downscaling run"); // NOI18N
        }

        final DataHandler spsHandler;
        try {
            spsHandler = DataHandlerCache.getInstance().getSPSDataHandler(RF_SPS_LOOKUP, new URL(RF_SPS_URL));
        } catch (final MalformedURLException ex) {
            final String message = "invalid rainfall sos url: " + RF_SOS_URL; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        } catch (final DataHandlerCacheException ex) {
            final String message = "cannot fetch datahandler";                // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        final Properties filter = new Properties();
        filter.put(TimeSeries.PROCEDURE, RF_DS_PROCEDURE);

        final Datapoint dp = spsHandler.createDatapoint(filter, null, DataHandler.Access.READ_WRITE);

        final TimeSeries ts = new TimeSeriesImpl(dp.getProperties());
        final TimeStamp now = new TimeStamp();
        ts.setValue(now, PARAM_CLIMATE_SCENARIO, "climate_echam5a1b3_prec_30m");
        ts.setValue(now, PARAM_SOURCE_RAIN, input.getValue().getProperty(TimeSeries.OFFERING));
        ts.setValue(now, PARAM_CENTER_TIME, "2050-11-05T10:10:10-00:00");
        ts.setValue(now, PropertyNames.TaskAction, PropertyNames.TaskActionStart);

        dp.putTimeSeries(ts);

        final TimeInterval ti = new TimeInterval(
                TimeInterval.Openness.OPEN,
                TimeStamp.NEGATIVE_INFINITY,
                TimeStamp.POSITIVE_INFINITY,
                TimeInterval.Openness.OPEN);
        final TimeSeries status = dp.getTimeSeries(ti);

        final int i = 1 + 1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean   DOCUMENT ME!
     * @param   where  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private static Object getInfo(final Object bean, final String where) throws IOException {
        try {
            final BeanInfo info = Introspector.getBeanInfo(bean.getClass(), Introspector.USE_ALL_BEANINFO);
            for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals(where)) {
                    return pd.getReadMethod().invoke(bean);
                }
            }

            throw new IOException("unknown property: " + where); // NOI18N
        } catch (final Exception exception) {
            final String message = "cannot get info";            // NOI18N
            LOG.error(message, exception);
            throw new IOException(message, exception);
        }
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
        // TODO: real SMS behaviour would now upload the timeseries of the cidsbean to the SOS
        // for the atr we only return the handle of the timeseries that comes from the dummy

        final RainfallDownscalingInput rfInput = inputFromRun(cidsBean);
        final CidsBean tsBean = rfInput.fetchTimeseries();

        assert tsBean != null : "timeseries is null"; // NOI18N

        final String tsUri = (String)tsBean.getProperty("uri");                 // NOI18N
        final TimeseriesRetrieverConfig config;
        if (tsUri == null) {
            final String message = "cannot read timeseries from uri, uri null"; // NOI18N
            LOG.error(message);                                                 // NOI18N
            throw new IOException(message);
        } else {
            config = TimeseriesRetrieverConfig.fromUrl(tsUri);
        }

        assert config != null : "invalid must not be null"; // NOI18N

        final DataHandler inputDH;
        try {
            inputDH = DataHandlerCache.getInstance().getSOSDataHandler(RF_SOS_LOOKUP, new URL(RF_SOS_URL));
        } catch (final MalformedURLException ex) {
            final String message = "invalid rainfall sos url: " + RF_SOS_URL; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        } catch (final DataHandlerCacheException ex) {
            final String message = "cannot fetch datahandler";                // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        final Map<String, Object> dpProps = new HashMap<String, Object>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("loading sensor ml"); // NOI18N
        }

        final String sensorML = readResource("rainfallCSSensorML.xml"); // NOI18N
        dpProps.put(TimeSeries.SENSORML, sensorML);                     // NOI18N

        final TimeSeries ts;
        try {
            final TimeseriesConverter converter = SMSUtils.loadConverter(tsBean);
            final Future<TimeSeries> tsFuture = TimeseriesRetriever.getInstance().retrieve(config, converter);

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

            ts.setTSProperty(PropertyNames.SPATIAL_RESOLUTION, new Integer[] { 1, 1 });
            ts.setTSProperty(PropertyNames.TEMPORAL_RESOLUTION, "NONE");            // NOI18N
            ts.setTSProperty(PropertyNames.COORDINATE_SYSTEM, "EPSG:4326");         // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot fetch timeseries for config: " + config; // NOI18N
            LOG.error(message, ex);
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
        int bytesRead = 0;
        try {
            bytesRead = bis.read(chars);
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
                LOG.warn("cannot close input stream", e);
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
    public static RainfallDownscalingInput inputFromRun(final CidsBean runBean) throws IOException {
        final Manager manager = SMSUtils.loadManagerFromRun(runBean, ManagerType.INPUT);
        manager.setCidsBean((CidsBean)runBean.getProperty("modelinput")); // NOI18N

        final Object resource = manager.getUR();
        if (!(resource instanceof RainfallDownscalingInput)) {
            throw new IllegalStateException("manager resource is not suited for rainfall downscaling"); // NOI18N
        }

        return (RainfallDownscalingInput)resource;
    }

    @Override
    protected String getReloadId() {
        return null;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ResultProcessor implements DatapointListener {

        //~ Static fields/initializers -----------------------------------------

        private static final String COMPLETED = "completed"; // NOI18N

        //~ Instance fields ----------------------------------------------------

        private final transient Map<String, Properties> resultFilters;
        private final transient Datapoint task;
        private final transient DataHandler resultHander;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ResultProcessor object.
         *
         * @param  resultFilters  DOCUMENT ME!
         * @param  task           DOCUMENT ME!
         * @param  resultHandler  DOCUMENT ME!
         * @param  runBean        DOCUMENT ME!
         */
        public ResultProcessor(final Map<String, Properties> resultFilters,
                final Datapoint task,
                final DataHandler resultHandler,
                final CidsBean runBean) {
            this.resultFilters = resultFilters;
            this.task = task;
            this.resultHander = resultHandler;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void dataChanged(final Datapoint datapoint, final TimeInterval timeinterval) {
            assert datapoint != null : "datapoint must not be null";       // NOI18N
            assert timeinterval != null : "timeinterval must not be null"; // NOI18N

            final TimeSeries eventTS = datapoint.getTimeSeries(timeinterval);
            final TimeStamp lastStamp = eventTS.getTimeStamps().last();

            assert lastStamp != null : "last stamp must not be null"; // NOI18N

            final String state = (String)eventTS.getValue(lastStamp, "state"); // NOI18N
            if (LOG.isDebugEnabled()) {
                LOG.debug("current task state: " + state);                     // NOI18N
            }

            if (COMPLETED.equals(state)) {
                task.removeListener(ResultProcessor.this);
                outputRunner = new CreateOutputRunner();

                // FIXME: atr hack to simulate longer model run time
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    LOG.warn("interrupted", ex); // NOI18N
                }

                outputRunner.start();
                fireFinised();
            }
        }

        //~ Inner Classes ------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @version  $Revision$, $Date$
         */
        private final class CreateOutputRunner extends Thread {

            //~ Instance fields ------------------------------------------------

            private transient CidsBean modelOutput;

            //~ Methods --------------------------------------------------------

            @Override
            public void run() {
                final String lookup = resultHander.getId();
                final URL location;
                try {
                    location = (URL)getInfo(resultHander, ENDPOINT);
                } catch (final Exception ex) {
                    LOG.error("cannot fetch result sos location from datahandler", ex); // NOI18N
                    // TODO: joptionpane
                    return;
                }

                final TimeseriesRetrieverConfig resultConfig = getConfig(
                        lookup,
                        location,
                        resultFilters.get(PARAM_RESULT_RAIN));
                final TimeseriesRetrieverConfig ref30Config = getConfig(
                        lookup,
                        location,
                        resultFilters.get(PARAM_REF_RAIN_30));
                final TimeseriesRetrieverConfig result30Config = getConfig(
                        lookup,
                        location,
                        resultFilters.get(PARAM_RESULT_RAIN_30));
                final TimeseriesRetrieverConfig ref1dConfig = getConfig(
                        lookup,
                        location,
                        resultFilters.get(PARAM_REF_RAIN_1D));
                final TimeseriesRetrieverConfig result1dConfig = getConfig(
                        lookup,
                        location,
                        resultFilters.get(PARAM_RESULT_RAIN_1D));

                try {
                    final RainfallDownscalingInput input = inputFromRun(cidsBean);
                    final CidsBean refBean = input.fetchTimeseries();
                    CidsBean resultBean = createResultBean(
                            "Downscaled result("
                                    + refBean.getProperty("name")
                                    + ")",
                            resultConfig.toTSTBUrl(),
                            refBean);
                    CidsBean result30Bean = createResultBean(
                            "Downscaled 30 min result("
                                    + refBean.getProperty("name")
                                    + ")",
                            result30Config.toTSTBUrl(),
                            refBean);
                    CidsBean ref30Bean = createResultBean(
                            "Reference 30 min("
                                    + refBean.getProperty("name")
                                    + ")",
                            ref30Config.toTSTBUrl(),
                            refBean);
                    CidsBean result1dBean = createResultBean(
                            "Downscaled 1 day result("
                                    + refBean.getProperty("name")
                                    + ")",
                            result1dConfig.toTSTBUrl(),
                            refBean);
                    CidsBean ref1dBean = createResultBean(
                            "Reference 1 day("
                                    + refBean.getProperty("name")
                                    + ")",
                            ref1dConfig.toTSTBUrl(),
                            refBean);

                    resultBean = resultBean.persist();
                    result30Bean = result30Bean.persist();
                    ref30Bean = ref30Bean.persist();
                    result1dBean = result1dBean.persist();
                    ref1dBean = ref1dBean.persist();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("created resulting timeseries"); // NOI18N
                    }

                    final RainfallDownscalingOutput output = createOutput(
                            ref30Bean,
                            result30Bean,
                            resultBean,
                            result1dBean,
                            ref1dBean);

                    modelOutput = SMSUtils.createModelOutput(
                            "Downscaling results of ("
                                    + cidsBean.getProperty("name")
                                    + ")",
                            output,
                            SMSUtils.Model.RF_DS);
                    modelOutput = modelOutput.persist();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("created resulting model output"); // NOI18N
                    }
                } catch (final Exception ex) {
                    LOG.error("cannot create rainfall output", ex);  // NOI18N
                    // TODO: joptionpane
                }
            }

            /**
             * DOCUMENT ME!
             *
             * @param   ref30Bean     DOCUMENT ME!
             * @param   result30Bean  DOCUMENT ME!
             * @param   resultBean    DOCUMENT ME!
             * @param   result1dBean  DOCUMENT ME!
             * @param   ref1dBean     DOCUMENT ME!
             *
             * @return  DOCUMENT ME!
             */
            private RainfallDownscalingOutput createOutput(final CidsBean ref30Bean,
                    final CidsBean result30Bean,
                    final CidsBean resultBean,
                    final CidsBean result1dBean,
                    final CidsBean ref1dBean) {
                return new RainfallDownscalingOutput(
                        ((CidsBean)cidsBean.getProperty("modelinput")).getMetaObject().getID(),     // NOI18N
                        cidsBean.getMetaObject().getID(),
                        resultBean.getMetaObject().getID(),
                        (String)resultBean.getProperty("name"),                                     // NOI18N
                        result30Bean.getMetaObject().getID(),
                        (String)result30Bean.getProperty("name"),                                   // NOI18N
                        ref30Bean.getMetaObject().getID(),
                        (String)ref30Bean.getProperty("name"),                                      // NOI18N
                        ref1dBean.getMetaObject().getID(),
                        (String)ref1dBean.getProperty("name"),                                      // NOI18N
                        result1dBean.getMetaObject().getID(),
                        (String)result1dBean.getProperty("name"));                                  // NOI18N
            }

            /**
             * DOCUMENT ME!
             *
             * @param   name     DOCUMENT ME!
             * @param   uri      DOCUMENT ME!
             * @param   station  DOCUMENT ME!
             *
             * @return  DOCUMENT ME!
             *
             * @throws  IOException  DOCUMENT ME!
             */
            private CidsBean createResultBean(final String name, final String uri, final CidsBean station)
                    throws IOException {
                try {
                    final MetaClass mcTimeseries = ClassCacheMultiple.getMetaClass(
                            SessionManager.getSession().getUser().getDomain(),
                            SMSUtils.TABLENAME_TIMESERIES);
                    final CidsBean result = mcTimeseries.getEmptyInstance().getBean();

                    result.setProperty("name", name);
                    result.setProperty("uri", uri);
                    result.setProperty("station", station);

                    return result;
                } catch (Exception e) {
                    final String message = "cannot create result bean: " // NOI18N
                                + "name=" + name                         // NOI18N
                                + "uri=" + uri                           // NOI18N
                                + "station=" + station;                  // NOI18N
                    LOG.error(message, e);
                    throw new IOException(message, e);
                }
            }

            /**
             * DOCUMENT ME!
             *
             * @param   lookup    DOCUMENT ME!
             * @param   location  DOCUMENT ME!
             * @param   props     DOCUMENT ME!
             *
             * @return  DOCUMENT ME!
             */
            private TimeseriesRetrieverConfig getConfig(final String lookup,
                    final URL location,
                    final Properties props) {
                final String foi = props.getProperty(TimeSeries.FEATURE_OF_INTEREST);
                final String obs = props.getProperty(TimeSeries.OBSERVEDPROPERTY);
                final String off = props.getProperty(TimeSeries.OFFERING);
                final String proc = props.getProperty(TimeSeries.PROCEDURE);

                return new TimeseriesRetrieverConfig(
                        TimeseriesRetrieverConfig.PROTOCOL_TSTB,
                        lookup,
                        location,
                        proc,
                        foi,
                        obs,
                        off,
                        null,
                        TimeInterval.ALL_INTERVAL);
            }
        }
    }
}
