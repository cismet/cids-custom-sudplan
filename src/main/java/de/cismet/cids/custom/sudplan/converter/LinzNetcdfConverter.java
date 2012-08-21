/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.math.RoundingMode;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = Converter.class)
public final class LinzNetcdfConverter implements TimeseriesConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LinzNetcdfConverter.class);
    public static final transient String NAN = "nan";
    private static final transient DateFormat DATEFORMAT;
    private static final transient NumberFormat NUMBERFORMAT;

    static {
        NUMBERFORMAT = NumberFormat.getInstance(Locale.US);
        NUMBERFORMAT.setMaximumFractionDigits(1);
        NUMBERFORMAT.setMinimumFractionDigits(1);
        NUMBERFORMAT.setRoundingMode(RoundingMode.HALF_UP);

        DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   from    DOCUMENT ME!
     * @param   params  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException  DOCUMENT ME!
     */
    @Override
    public TimeSeries convertForward(final InputStream from, final String... params) throws ConversionException {
        final BufferedReader br;
        try {
            if (params.length != 2) {
                final String message =
                    "Wrong number of parameters, exepected two parameter: offering and observed property";
                LOG.error(message);
                throw new ConversionException(message);
            }

            final String offering = params[0];
            final String offeringMetaData = offering + ".json";
            final String observedProperty = params[1];
            final boolean isEvent = offering.toLowerCase().contains("events");

            LOG.info("importing time series for '" + observedProperty + "' from '" + offering + "' (events="
                        + isEvent + ")");

            final TimeSeriesImpl ts = new TimeSeriesImpl();
            final JsonFactory jfactory = new JsonFactory();
            final JsonParser jParser = jfactory.createJsonParser(this.getClass().getResourceAsStream(offeringMetaData));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Meta Data loaded from '"
                            + this.getClass().getResource(offeringMetaData) + "'");
            }

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(jParser);

            final JsonNode children = rootNode.get("opensdm:children");
            final Iterator<JsonNode> childrenIterator = children.iterator();
            int position = 0;
            while (childrenIterator.hasNext()) {
                final JsonNode child = childrenIterator.next();
                final String variable_name = child.get("opensdm:data").get("nc:variable_name").asText();
                if (observedProperty.equals(variable_name)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("variable '" + observedProperty + "' found in meta data at position #" + position);
                    }
                    ts.setTSProperty(TimeSeries.VALUE_KEYS, new String[] { PropertyNames.VALUE });
                    ts.setTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES, new String[] { Float.class.getName() });
                    ts.setTSProperty(TimeSeries.VALUE_TYPES, new String[] { TimeSeries.VALUE_TYPE_NUMBER });
                    ts.setTSProperty(
                        TimeSeries.VALUE_UNITS,
                        new String[] { child.get("opensdm:data").get("cf:units").asText() });
                    ts.setTSProperty(
                        TimeSeries.VALUE_OBSERVED_PROPERTY_URNS,
                        new String[] { child.get("opensdm:data").get("cf:long_name").asText() });
                    ts.setTSProperty(
                        TimeSeries.DESCRIPTION_KEYS,
                        new String[] { child.get("opensdm:data").get("cf:description").asText() });
                    ts.setTSProperty(
                        PropertyNames.DESCRIPTION,
                        child.get("opensdm:data").get("cf:description").asText());
                    break;
                }
                position++;
            }

            if (position == children.size()) {
                final String message = "observed property '" + observedProperty
                            + "' not found in '" + offering + "'";
                LOG.error(message);
                throw new ConversionException(message);
            }

            br = new BufferedReader(new InputStreamReader(from));

            String line = br.readLine();
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSV File Header: " + line);
            }

            final long start = System.currentTimeMillis();
            while ((line = br.readLine()) != null) {
                final String[] split = line.split(","); // NOI18N
//                if (split.length == 1) {
//                    if (LOG.isDebugEnabled()) {
//                        LOG.warn("token without value: " + split[0]); // NOI18N
//                    }
//                } else if (split.length < position) {
//                    LOG.warn("wrong number of rows (" + split.length + ") in line '" + line + "'");
//                } else {

                if (isEvent) {
                    final String value = split[position];

                    if (!NAN.equals(value)) {
                        final GregorianCalendar calendar = new GregorianCalendar();

                        final String event_start = split[1];
                        final String event_end = split[2];

                        final Date startDate = DATEFORMAT.parse(event_start);
                        final Date endDate = DATEFORMAT.parse(event_end);

                        final float val = NUMBERFORMAT.parse(value).floatValue();

                        calendar.setTime(startDate);
                        calendar.add(Calendar.SECOND, -1);
                        ts.setValue(new TimeStamp(calendar.getTime()), PropertyNames.VALUE, 0.0f);

                        ts.setValue(new TimeStamp(startDate), PropertyNames.VALUE, val);
                        ts.setValue(new TimeStamp(endDate), PropertyNames.VALUE, val);

                        calendar.setTime(endDate);
                        calendar.add(Calendar.SECOND, +1);
                        ts.setValue(new TimeStamp(calendar.getTime()), PropertyNames.VALUE, 0.0f);
                    }
                } else {
                    final String value = split[position];

                    if (!NAN.equals(value)) {
                        final String key = split[0];
                        final Date date = DATEFORMAT.parse(key);
                        final float val = NUMBERFORMAT.parse(value).floatValue();

                        ts.setValue(new TimeStamp(date), PropertyNames.VALUE, val);
                    }
                }

                if (Thread.currentThread().isInterrupted()) {
                    LOG.warn("execution was interrupted"); // NOI18N
                    return null;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("reading timeseries took " + (System.currentTimeMillis() - start) + "ms");
            }

            return ts;
        } catch (final Exception ex) {
            final String message = "cannot convert from input stream"; // NOI18N
            LOG.error(message, ex);
            throw new ConversionException(message, ex);
        }
    }

    @Override
    public String toString() {
        return "Linz-Hydraulics Converter";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   to      DOCUMENT ME!
     * @param   params  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException            DOCUMENT ME!
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    @Override
    public InputStream convertBackward(final TimeSeries to, final String... params) throws ConversionException {
        throw new UnsupportedOperationException("convertBackward operation not supported by " + this.toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        final LinzNetcdfConverter linzWwtpHydraulicsConverter = new LinzNetcdfConverter();

        try {
            // JSONParser parser = new
            // JSONParser(LinzNetcdfConverter.class.getResourceAsStream("LinzNetcdfConverter.json"));

            final JsonFactory jfactory = new JsonFactory();

            /**
             * * read from file **
             */
            final JsonParser jParser = jfactory.createJsonParser(LinzNetcdfConverter.class.getResourceAsStream(
                        "linz-wwtp-hydraulics.json"));
            if (LOG.isDebugEnabled()) {
                LOG.debug(LinzNetcdfConverter.class.getResource("linz-wwtp-hydraulics.json"));
            }

            if (LOG.isDebugEnabled()) {
                // LOG.debug(jParser.nextToken());
            }

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(jParser);
            if (LOG.isDebugEnabled()) {
                // LOG.debug(rootNode);
            }

            final String variableName = "Q_inflow_total_at";

            final JsonNode children = rootNode.get("opensdm:children");
            final Iterator<JsonNode> childrenIterator = children.iterator();
            while (childrenIterator.hasNext()) {
                final JsonNode child = childrenIterator.next();
                final String variable_name = child.get("opensdm:data").get("nc:variable_name").asText();

                if (variableName.equals(variable_name)) {
                }

                if (LOG.isDebugEnabled()) {
                    // LOG.debug(ci.next().get("@id").toString());
                    LOG.debug(variable_name);
                }
            }

            System.exit(0);

            final TimeSeries t = linzWwtpHydraulicsConverter.convertForward(new FileInputStream(
                        new File("P:\\SUDPLAN\\WP7 - Linz Pilot\\Workshop_2011-05-09\\linz-wwtp-hydraulics-1.csv")));

            LOG.info(t.getTSKeys());
            LOG.info(t.getTimeStampsArray()[0]);
        } catch (Throwable t) {
            LinzNetcdfConverter.LOG.fatal(t, t);
            System.exit(1);
        }
    }
}
