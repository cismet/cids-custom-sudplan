/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.sudplan.util.EnvelopeQueryParameter;
import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import se.smhi.sudplan.client.Sample;
import se.smhi.sudplan.client.SudPlanHypeAPI;

import java.io.BufferedInputStream;
import java.io.IOException;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.commons.CismetExecutors;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesSerializer;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import de.cismet.cids.custom.sudplan.hydrology.HydrologyCache;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimeseriesRetriever {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesRetriever.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ExecutorService executor;
    private final transient Map<String, HttpClient> clientCache;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeseriesRetriever object.
     */
    private TimeseriesRetriever() {
        executor = CismetExecutors.newCachedThreadPool(SudplanConcurrency.createThreadFactory("timeseries-retriever")); // NOI18N
        clientCache = new HashMap<String, HttpClient>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   config  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  TimeseriesRetrieverException  DOCUMENT ME!
     */
    public Future<TimeSeries> retrieve(final TimeseriesRetrieverConfig config) throws TimeseriesRetrieverException {
        return retrieve(config, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   config     DOCUMENT ME!
     * @param   converter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  TimeseriesRetrieverException  DOCUMENT ME!
     */
    public Future<TimeSeries> retrieve(final TimeseriesRetrieverConfig config, final TimeseriesConverter converter)
            throws TimeseriesRetrieverException {
        validateConfig(config);

        final RetrieverFuture future = new RetrieverFuture(config, converter);

        return executor.submit(future);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   config  DOCUMENT ME!
     *
     * @throws  TimeseriesRetrieverException  DOCUMENT ME!
     */
    private void validateConfig(final TimeseriesRetrieverConfig config) throws TimeseriesRetrieverException {
        if (config == null) {
            throw new TimeseriesRetrieverException("config must not be null");   // NOI18N
        } else if (config.getLocation() == null) {
            throw new TimeseriesRetrieverException("location must not be null"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static TimeseriesRetriever getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class RetrieverFuture implements Callable<TimeSeries> {

        //~ Instance fields ----------------------------------------------------

        private final transient TimeseriesRetrieverConfig config;
        private final transient TimeseriesConverter converter;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RetrieverFuture object.
         *
         * @param  config     DOCUMENT ME!
         * @param  converter  DOCUMENT ME!
         */
        RetrieverFuture(final TimeseriesRetrieverConfig config, final TimeseriesConverter converter) {
            this.config = config;

            if (TimeseriesRetrieverConfig.PROTOCOL_DAV.equals(config.getProtocol())
                        && !(converter instanceof TimeSeriesSerializer)) {
                /*
                 * We assume that the WebDAV only holds IMPORTED TimeSeries files and all imported files have the same
                 * internal format generated by the converter TimeSeriesSerializer. As this fact was a late design
                 * decision, this workaround was introduced to avoid multiple complicated code changes (for determining
                 * the right converter) in other components.
                 */
                this.converter = TimeSeriesSerializer.getInstance();
            } else {
                this.converter = converter;
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public TimeSeries call() throws Exception {
            if (LOG.isDebugEnabled()) {
                LOG.debug("starting timeseries retrieval for config: " + config);
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            if (TimeseriesRetrieverConfig.PROTOCOL_TSTB.equals(config.getProtocol())) {
                return fromTSTB();
            } else if (TimeseriesRetrieverConfig.PROTOCOL_DAV.equals(config.getProtocol())) {
                return fromDav();
            } else if (TimeseriesRetrieverConfig.PROTOCOL_HYPE.equals(config.getProtocol())) {
                return fromHype();
            } else {
                throw new TimeseriesRetrieverException("unknown config: " + config); // NOI18N
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  TimeseriesRetrieverException  DOCUMENT ME!
         */
        private TimeSeries fromTSTB() throws TimeseriesRetrieverException {
            final DataHandler handler;
            try {
                handler = DataHandlerCache.getInstance()
                            .getSOSDataHandler(config.getHandlerLookup(), config.getLocation().toExternalForm());
            } catch (DataHandlerCacheException ex) {
                final String message = "cannot create data handler"; // NOI18N
                LOG.error(message, ex);
                throw new TimeseriesRetrieverException(message, ex);
            }

            if (handler == null) {
                throw new TimeseriesRetrieverException("cannot lookup handler: " + config.getHandlerLookup()); // NOI18N
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            final Set<Datapoint> dps = handler.getDatapoints(config.getFilterProperties(), DataHandler.Access.READ);
            final Datapoint datapoint;
            if (dps.size() < 1) {
                throw new TimeseriesRetrieverException("no datapoint for configuration: " + config);        // NOI18N
            } else if (dps.size() > 1) {
                throw new TimeseriesRetrieverException("too many datapoints for configuration: " + config); // NOI18N
            } else {
                datapoint = dps.iterator().next();
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
            }

            final TimeInterval interval;
            if (config.getInterval() == null) {
                interval = new TimeInterval(
                        TimeInterval.Openness.OPEN,
                        TimeStamp.NEGATIVE_INFINITY,
                        TimeStamp.POSITIVE_INFINITY,
                        TimeInterval.Openness.OPEN);
            } else {
                interval = config.getInterval();
            }

            if (config.getGeometry() != null) {
                final EnvelopeQueryParameter point = new EnvelopeQueryParameter();
                point.setEnvelope(config.getGeometry().getEnvelopeInternal());
                return datapoint.getTimeSeries(interval, point);
            } else {
                return datapoint.getTimeSeries(interval);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  TimeseriesRetrieverException  DOCUMENT ME!
         */
        private TimeSeries fromDav() throws TimeseriesRetrieverException {
            if (converter == null) {
                throw new TimeseriesRetrieverException("cannot fetch timeseries from dav without converter"); // NOI18N
            }

            // we don't use the cismet dav client as its "care-less" implementation leads to unpleasant behaviour in
            // case of exception/about etc.
            final HttpClient client = TimeSeriesRemoteHelper.createHttpClient(this.config.getLocation().getHost(),
                    TimeSeriesRemoteHelper.CREDS);

            String location = config.getLocation().toExternalForm();
            if (LOG.isDebugEnabled()) {
                LOG.debug("GET location1: " + location);
            }

            location += '/' + config.getOffering();
            if (LOG.isDebugEnabled()) {
                LOG.debug("GET location2: " + location);
            }

            final GetMethod get = new GetMethod(location);
            BufferedInputStream bis = null;
            try {
                client.executeMethod(get);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("GET operation has been finished with status code: " + get.getStatusCode());
                }

                bis = new BufferedInputStream(get.getResponseBodyAsStream());

                if (Thread.currentThread().isInterrupted()) {
                    throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
                }
                final TimeSeries ts = converter.convertForward(bis);

                if (Thread.currentThread().isInterrupted()) {
                    throw new TimeseriesRetrieverException("execution was interrupted"); // NOI18N
                }
                return ts;
            } catch (final Exception ex) {
                final String message = "cannot fetch timeseries from dav: " + config;    // NOI18N
                LOG.error(message, ex);

                get.abort();

                throw new TimeseriesRetrieverException(message, ex);
            } finally {
                get.releaseConnection();
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (final IOException ex) {
                        LOG.warn("cannot close inputstream", ex); // NOI18N
                    }
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  TimeseriesRetrieverException  DOCUMENT ME!
         */
        private TimeSeries fromHype() throws TimeseriesRetrieverException {
            final SudPlanHypeAPI hypeClient = HydrologyCache.getInstance().getHypeClient();
            final DateFormat df = HydrologyCache.getInstance().getHydroDateFormat();

            final TimeStamp start = config.getInterval().getStart();
            final TimeStamp end = config.getInterval().getEnd();
            try {
                final List<Sample> tsSamples = hypeClient.getTimeSeries(config.getOffering(),
                        Integer.parseInt(config.getFoi()),
                        df.format(start.asDate()),
                        df.format(end.asDate()));

                final TimeSeriesImpl ts = new TimeSeriesImpl();
                ts.setTSProperty(TimeSeries.VALUE_KEYS, new String[] { PropertyNames.VALUE });
                ts.setTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES, new String[] { Float.class.getName() });
                ts.setTSProperty(TimeSeries.VALUE_TYPES, new String[] { TimeSeries.VALUE_TYPE_NUMBER });
                ts.setTSProperty(TimeSeries.VALUE_OBSERVED_PROPERTY_URNS, new String[] { config.getObsProp() });
                ts.setTSProperty(
                    TimeSeries.VALUE_UNITS,
                    new String[] { hypeVarUnit(config.getObservedProperty()).getPropertyKey() });

                for (final Sample sample : tsSamples) {
                    final Date date = sample.getDate().toDateMidnight().toDate();
                    ts.setValue(new TimeStamp(date),
                        PropertyNames.VALUE,
                        Double.valueOf(sample.getValue()).floatValue());
                }

                return ts;
            } catch (final Exception ex) {
                final String message = "cannot fetch timeseries from hype: " + config; // NOI18N
                LOG.error(message, ex);
                throw new TimeseriesRetrieverException(message, ex);
            }
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
        private Unit hypeVarUnit(final Variable var) {
            if (Variable.COUT.equals(var)) {
                return Unit.M3S;
            } else if (Variable.CRUN.equals(var)) {
                return Unit.MM;
            } else if (Variable.CPRC.equals(var)) {
                return Unit.MM;
            } else if (Variable.CTMP.equals(var)) {
                return Unit.CELSIUS;
            } else if (Variable.GWAT.equals(var)) {
                return Unit.METERS;
            } else if (Variable.SMDF.equals(var)) {
                return Unit.MM;
            } else {
                throw new IllegalArgumentException("unknown hype var: " + var); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final transient TimeseriesRetriever INSTANCE = new TimeseriesRetriever();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
