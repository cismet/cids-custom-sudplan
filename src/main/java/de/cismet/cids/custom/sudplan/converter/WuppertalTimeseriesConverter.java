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

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.math.RoundingMode;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import de.cismet.cids.custom.sudplan.Unit;
import de.cismet.cids.custom.sudplan.Variable;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = Converter.class)
public final class WuppertalTimeseriesConverter implements TimeseriesConverter, FormatHint {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(WuppertalTimeseriesConverter.class);

    private static final String TOKEN_STATION = "Station";            // NOI18N
    private static final String TOKEN_STATION_NO = "Stationsnummer";  // NOI18N
    private static final String TOKEN_SUB_DESCR = "Unterbezeichnung"; // NOI18N
    private static final String TOKEN_PARAM = "Parameter";            // NOI18N
    private static final String TOKEN_UNIT = "Einheit";               // NOI18N
    private static final String TOKEN_SENSOR = "Geber";               // NOI18N
    private static final DateFormat DATEFORMAT;
    private static final NumberFormat NUMBERFORMAT;

    static {
        DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); // NOI18N
        NUMBERFORMAT = NumberFormat.getInstance(Locale.GERMAN);
        NUMBERFORMAT.setMaximumFractionDigits(2);
        NUMBERFORMAT.setMinimumFractionDigits(2);
        NUMBERFORMAT.setRoundingMode(RoundingMode.HALF_UP);
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
            br = new BufferedReader(new InputStreamReader(from));

            String line = br.readLine();

            final TimeSeriesImpl ts = new TimeSeriesImpl();
            ts.setTSProperty(TimeSeries.VALUE_KEYS, new String[] { PropertyNames.VALUE });
            ts.setTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES, new String[] { Float.class.getName() });
            ts.setTSProperty(TimeSeries.VALUE_TYPES, new String[] { TimeSeries.VALUE_TYPE_NUMBER });

            while (line != null) {
                final String[] split = line.split(";");                // NOI18N
                if (split.length == 1) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("token without value: " + split[0]); // NOI18N
                    }
                } else {
                    if (split.length > 2) {
                        // usually, there should be only 2 splits, but there  might be more (e.g. for comments)
                        LOG.warn("illegal line format: " + line + " -> only first 2 splits are considered"); // NOI18N
                    }

                    final String key = split[0];
                    final String value = split[1];

                    if (TOKEN_STATION.equals(key)) {
                    } else if (TOKEN_STATION_NO.equals(key)) {
                        // TODO: where to put this
                        ts.setTSProperty(PropertyNames.DESCRIPTION, value);
                    } else if (TOKEN_SUB_DESCR.equals(key)) {
                        // TODO: where to put this
                    } else if (TOKEN_PARAM.equals(key)) {
                        if (value.equals("Niederschlag")) { // NOI18N
                            ts.setTSProperty(
                                TimeSeries.VALUE_OBSERVED_PROPERTY_URNS,
                                new String[] { Variable.PRECIPITATION.getPropertyKey() });
                        } else {
                            ts.setTSProperty(TimeSeries.VALUE_OBSERVED_PROPERTY_URNS, new String[] { value });
                        }
                    } else if (TOKEN_UNIT.equals(key)) {
                        if (value.equals("mm/h")) {         // NOI18N
                            ts.setTSProperty(TimeSeries.VALUE_UNITS, new String[] { Unit.MM_H.getPropertyKey() });
                        } else {
                            ts.setTSProperty(TimeSeries.VALUE_UNITS, new String[] { value });
                        }
                    } else if (TOKEN_SENSOR.equals(key)) {
                        // TODO: where to put this
                    } else {
                        final Date date = DATEFORMAT.parse(key);
                        final float val = NUMBERFORMAT.parse(value.trim()).floatValue();
                        ts.setValue(new TimeStamp(date), PropertyNames.VALUE, val);
                    }
                }

                if (Thread.currentThread().isInterrupted()) {
                    LOG.warn("execution was interrupted"); // NOI18N

                    return null;
                }

                line = br.readLine();
            }

            return ts;
        } catch (final Exception ex) {
            final String message = "cannot convert from input stream"; // NOI18N
            LOG.error(message, ex);
            throw new ConversionException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   to      DOCUMENT ME!
     * @param   params  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException  DOCUMENT ME!
     */
    @Override
    public InputStream convertBackward(final TimeSeries to, final String... params) throws ConversionException {
        try {
            final Object valueKeyObject = to.getTSProperty(TimeSeries.VALUE_KEYS);
            final String valueKey;
            if (valueKeyObject instanceof String) {
                valueKey = (String)valueKeyObject;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found valuekey: " + valueKey);                   // NOI18N
                }
            } else if (valueKeyObject instanceof String[]) {
                final String[] valueKeys = (String[])valueKeyObject;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found multiple valuekeys: " + valueKeys.length); // NOI18N
                }

                if (valueKeys.length == 1) {
                    valueKey = valueKeys[0];
                } else {
                    throw new IllegalStateException("found too many valuekeys");              // NOI18N
                }
            } else {
                throw new IllegalStateException("unknown value key type: " + valueKeyObject); // NOI18N
            }

            final StringBuilder sb = new StringBuilder();
            final String lineSep = System.getProperty("line.separator"); // NOI18N

            if (to.getTSProperty(PropertyNames.DESCRIPTION) != null) {
                sb.append(TOKEN_STATION_NO);
                sb.append(';');
                sb.append(to.getTSProperty(PropertyNames.DESCRIPTION));
                sb.append(lineSep);
            }
            if (to.getTSProperty(TimeSeries.VALUE_OBSERVED_PROPERTY_URNS) != null) {
                try {
                    final String param = ((String[])to.getTSProperty(TimeSeries.VALUE_OBSERVED_PROPERTY_URNS))[0];
                    sb.append(TOKEN_PARAM);
                    sb.append(';');
                    sb.append(param);
                    sb.append(lineSep);
                } catch (final Exception e) {
                    LOG.warn("cannot set observed property", e); // NOI18N
                }
            }
            if (to.getTSProperty(TimeSeries.VALUE_UNITS) != null) {
                try {
                    final String unit = ((String[])to.getTSProperty(TimeSeries.VALUE_UNITS))[0];
                    sb.append(TOKEN_UNIT);
                    sb.append(';');
                    sb.append(unit);
                    sb.append(lineSep);
                } catch (final Exception e) {
                    LOG.warn("cannot set unit", e);              // NOI18N
                }
            }

            final Iterator<TimeStamp> it = to.getTimeStamps().iterator();
            while (it.hasNext()) {
                final TimeStamp stamp = it.next();
                final Float value = (Float)to.getValue(stamp, valueKey);

                sb.append(DATEFORMAT.format(stamp.asDate()));
                sb.append(";      "); // NOI18N
                sb.append(NUMBERFORMAT.format(value));
                sb.append(lineSep);
            }

            return new ByteArrayInputStream(sb.toString().getBytes());
        } catch (final Exception e) {
            final String message = "cannot convert timeseries data"; // NOI18N
            LOG.error(message, e);
            throw new ConversionException(message, e);
        }
    }

    @Override
    public String toString() {
        return getFormatDisplayName();
    }

    @Override
    public String getFormatName() {
        return "wupp-timeseries-converter"; // NOI18N
    }

    @Override
    public String getFormatDisplayName() {
        return NbBundle.getMessage(
                WuppertalTimeseriesConverter.class,
                "WuppertalTimeseriesConverter.this.name"); // NOI18N
    }

    @Override
    public String getFormatHtmlName() {
        return null;
    }

    @Override
    public String getFormatDescription() {
        return NbBundle.getMessage(
                WuppertalTimeseriesConverter.class,
                "WuppertalTimeseriesConverter.getFormatDescription().description"); // NOI18N
    }

    @Override
    public String getFormatHtmlDescription() {
        return NbBundle.getMessage(
                WuppertalTimeseriesConverter.class,
                "WuppertalTimeseriesConverter.getFormatHtmlDescription().description"); // NOI18N
    }

    @Override
    public Object getFormatExample() {
        return NbBundle.getMessage(
                WuppertalTimeseriesConverter.class,
                "WuppertalTimeseriesConverter.getFormatExample().description"); // NOI18N
    }
}
