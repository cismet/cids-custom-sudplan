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

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.DataHandlerFactory;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.handler.DatapointListener;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import org.apache.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.io.IOException;

import java.net.URL;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.cismet.cids.custom.sudplan.AbstractModelManager;
import de.cismet.cids.custom.sudplan.Demo;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

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
    private static final String PARAM_REF_RAIN = "param:ref_rain";                        // NOI18N
    private static final String PARAM_RESULT_RAIN = "param:result_rain";                  // NOI18N
    private static final String PARAM_REF_RAIN_30 = "param:result_rain_30";               // NOI18N
    private static final String PARAM_RESULT_RAIN_30 = "param:result_rain_30_downscaled"; // NOI18N

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

        // TODO: for real usage we have to lookup the config from somewhere else
        final DataHandler spsDH = DataHandlerFactory.Lookup.lookup("SPS-SUDPLAN-Dummy"); // NOI18N
        putInfo(spsDH, "sourceDH", input.getKey());                                      // NOI18N
        putInfo(spsDH, "targetDH", output);                                              // NOI18N
        spsDH.open();
        Demo.getInstance().setDSSOSDH(output);

        final Properties taskFilter = new Properties();
        taskFilter.put(PROP_MODEL_ID, RAIN_TIMESERIES_DOWNSCALING_ID);
        final Datapoint task = spsDH.createDatapoint(taskFilter, null, DataHandler.Access.READ_WRITE);

        // hardcoded for the demo
        final Properties[] props = new Properties[3];
        props[0] = new Properties();
        props[1] = new Properties();
        props[2] = new Properties();

        props[0].put(TimeSeries.FEATURE_OF_INTEREST, "urn:MyOrg:feature:linz");     // NOI18N
        props[0].put(TimeSeries.PROCEDURE, "urn:ogc:object:LINZ:rain:A1B");         // NOI18N
        props[0].put(TimeSeries.OBSERVEDPROPERTY, "urn:ogc:def:property:OGC:rain"); // NOI18N
        props[0].put(TimeSeries.OFFERING, "Station_3202_10min_Downscaled");         // NOI18N

        props[1].put(TimeSeries.FEATURE_OF_INTEREST, "urn:MyOrg:feature:linz");     // NOI18N
        props[1].put(TimeSeries.PROCEDURE, "urn:ogc:object:LINZ:rain:A1B");         // NOI18N
        props[1].put(TimeSeries.OBSERVEDPROPERTY, "urn:ogc:def:property:OGC:rain"); // NOI18N
        props[1].put(TimeSeries.OFFERING, "Station_3202_30min");                    // NOI18N

        props[2].put(TimeSeries.FEATURE_OF_INTEREST, "urn:MyOrg:feature:linz");     // NOI18N
        props[2].put(TimeSeries.PROCEDURE, "urn:ogc:object:LINZ:rain:A1B");         // NOI18N
        props[2].put(TimeSeries.OBSERVEDPROPERTY, "urn:ogc:def:property:OGC:rain"); // NOI18N
        props[2].put(TimeSeries.OFFERING, "Station_3202_30min_Downscaled");         // NOI18N

        final Map<String, Properties> results = new HashMap<String, Properties>(6);
        results.put(PARAM_RESULT_RAIN, props[0]);
        results.put(PARAM_REF_RAIN_30, props[1]);
        results.put(PARAM_RESULT_RAIN_30, props[2]);
        results.put(PARAM_REF_RAIN, input.getValue());

        // we register the resultprocessor for the now running task, we cannot use weak listeners because there is no
        // place to hold strong reference to it
        task.addListener(new ResultProcessor(results, task, output, cidsBean));

        // unique id of the task
        final String taskId = task.getFilter().getProperty(PROP_TASK_ID);

        final TimeSeries inputTimeSeries = new TimeSeriesImpl(task.getProperties());
        final TimeStamp now = new TimeStamp();

        final RainfallDownscalingInput rfInput = inputFromRun(cidsBean);

        inputTimeSeries.setValue(now, "param:climate_scenario", rfInput.getScenario());
        inputTimeSeries.setValue(now, "param:reference_rain", SMSUtils.toTSTBCompatiblePropListing(input.getValue()));
        inputTimeSeries.setValue(
            now,
            "param:center_time",
            new TimeStamp(new GregorianCalendar(rfInput.getTargetYear(), 0, 1).getTime()).toString());

        inputTimeSeries.setValue(now, PARAM_RESULT_RAIN, SMSUtils.toTSTBCompatiblePropListing(props[0]));
        inputTimeSeries.setValue(now, PARAM_REF_RAIN_30, SMSUtils.toTSTBCompatiblePropListing(props[1]));
        inputTimeSeries.setValue(
            now,
            PARAM_RESULT_RAIN_30,
            SMSUtils.toTSTBCompatiblePropListing(props[2]));

        if (LOG.isDebugEnabled()) {
            LOG.debug("starting the downscaling run"); // NOI18N
        }

        inputTimeSeries.setValue(now, "action", "start");
        task.putTimeSeries(inputTimeSeries);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean   DOCUMENT ME!
     * @param   where  DOCUMENT ME!
     * @param   what   DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private static void putInfo(final Object bean, final String where, final Object what) throws IOException {
        try {
            final BeanInfo info = Introspector.getBeanInfo(bean.getClass(), Introspector.USE_ALL_BEANINFO);
            for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals(where)) {
                    pd.getWriteMethod().invoke(bean, what);
                }
            }
        } catch (final Exception exception) {
            final String message = "cannot put info"; // NOI18N
            LOG.error(message, exception);
            throw new IOException(message, exception);
        }
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
            LOG.debug("start uploading timeseries" + this);
        }
        // TODO: real SMS behaviour would now upload the timeseries of the cidsbean to the SOS
        // for the atr we only return the handle of the timeseries that comes from the dummy

        final RainfallDownscalingInput rfInput = inputFromRun(cidsBean);
        final CidsBean tsBean = rfInput.fetchTimeseries();

        assert tsBean != null : "timeseries is null"; // NOI18N

        final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromTSTBUrl((String)tsBean.getProperty(
                    "uri")); // NOI18N

        assert config != null : "invalid config"; // NOI18N

        final DataHandler inputDH = Demo.getInstance().getSOSDH(); // TODO: <- for demo
                                                                   // DataHandlerFactory.Lookup.lookup(config.getHandlerLookup());
        inputDH.setId(config.getHandlerLookup());
        putInfo(inputDH, ENDPOINT, config.getSosLocation());       // NOI18N

        // final Properties properties = config.getFilterProperties();
        // hardcoded for the demo
        final Properties properties = new Properties();
        properties.put(TimeSeries.FEATURE_OF_INTEREST, "urn:MyOrg:feature:linz");     // NOI18N
        properties.put(TimeSeries.PROCEDURE, "urn:ogc:object:LINZ:rain:1");           // NOI18N
        properties.put(TimeSeries.OBSERVEDPROPERTY, "urn:ogc:def:property:OGC:rain"); // NOI18N
        properties.put(TimeSeries.OFFERING, "Station_3202_10min");                    // NOI18N

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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ResultProcessor implements DatapointListener {

        //~ Static fields/initializers -----------------------------------------

        private static final String COMPLETED = "completed";

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

                    resultBean = resultBean.persist();
                    result30Bean = result30Bean.persist();
                    ref30Bean = ref30Bean.persist();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("created resulting timeseries"); // NOI18N
                    }

                    final RainfallDownscalingOutput output = createOutput(ref30Bean, result30Bean, resultBean);

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
             *
             * @return  DOCUMENT ME!
             */
            private RainfallDownscalingOutput createOutput(final CidsBean ref30Bean,
                    final CidsBean result30Bean,
                    final CidsBean resultBean) {
                return new RainfallDownscalingOutput(
                        ((CidsBean)cidsBean.getProperty("modelinput")).getMetaObject().getID(),     // NOI18N
                        cidsBean.getMetaObject().getID(),
                        resultBean.getMetaObject().getID(),
                        (String)resultBean.getProperty("name"),                                     // NOI18N
                        result30Bean.getMetaObject().getID(),
                        (String)result30Bean.getProperty("name"),                                   // NOI18N
                        ref30Bean.getMetaObject().getID(),
                        (String)ref30Bean.getProperty("name"));                                     // NOI18N
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
