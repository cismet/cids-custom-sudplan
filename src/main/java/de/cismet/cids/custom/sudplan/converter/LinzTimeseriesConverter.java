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

import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
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
public final class LinzTimeseriesConverter extends TimeseriesConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LinzTimeseriesConverter.class);

    private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // NOI18N
    private static final NumberFormat NUMBERFORMAT = NumberFormat.getInstance(Locale.US);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   from  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException  DOCUMENT ME!
     */
    @Override
    public TimeSeries convertForward(final InputStream from) throws ConversionException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(from));

            String line = br.readLine();

            final TimeSeriesImpl ts = new TimeSeriesImpl();
            ts.setTSProperty(TimeSeries.VALUE_KEYS, new String[] { PropertyNames.VALUE });
            ts.setTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES, new String[] { Float.class.getName() });
            ts.setTSProperty(TimeSeries.VALUE_TYPES, new String[] { TimeSeries.VALUE_TYPE_NUMBER });
            // FIXME: hardcoded unit and observed property
            ts.setTSProperty(TimeSeries.VALUE_UNITS, new String[] { Unit.MM.getPropertyKey() });
            ts.setTSProperty(
                TimeSeries.VALUE_OBSERVED_PROPERTY_URNS,
                new String[] { Variable.PRECIPITATION.getPropertyKey() });
            // must be present
            ts.setTSProperty(PropertyNames.DESCRIPTION, "imported_linz_timeseries_" + System.currentTimeMillis());

            while (line != null) {
                final String[] split = line.split("   ");              // NOI18N
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

                    final Date date = DATEFORMAT.parse(key);
                    final float val = NUMBERFORMAT.parse(value.trim()).floatValue();
                    ts.setValue(new TimeStamp(date), PropertyNames.VALUE, val);
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
     * @param   to  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException            DOCUMENT ME!
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    @Override
    public InputStream convertBackward(final TimeSeries to) throws ConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "Linz Converter";
    }
}
