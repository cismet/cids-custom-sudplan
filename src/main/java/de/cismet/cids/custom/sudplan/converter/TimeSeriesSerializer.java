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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Internal converter for imported TimeSeries.
 *
 * @author   Benjamin Friedrich benjamin.friedrich@cismet.de
 * @version  1.0, 04.01.2012
 */
public final class TimeSeriesSerializer extends TimeseriesConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final String SECTION_TS_PROPS = ">>> TS_PROPERTIES <<<".intern(); // NOI18N
    private static final String SECTION_TS_DATA = ">>> TS_DATA <<<".intern();        // NOI18N
    private static final String FIELD_SEP_AS_STRING = "\t";                          // NOI18N
    private static final char FIELD_SEP_AS_CHAR = '\t';                              // NOI18N

    private static final String DATEFORMAT = "dd.MM.yyyy HH:mm:ss:SSS"; // NOI18N

    private static final String TYPE_INTEGER = Integer.class.getName().intern();
    private static final String TYPE_STRING = String.class.getName().intern();
    private static final String TYPE_STRING_ARRAY = String[].class.getName().intern();

    private static final Logger LOG = Logger.getLogger(TimeSeriesSerializer.class);

    private static final TimeSeriesSerializer INSTANCE = new TimeSeriesSerializer();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesSerializer object.
     */
    private TimeSeriesSerializer() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static TimeSeriesSerializer getInstance() {
        return INSTANCE;
    }

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
        try {
            return deserializeTimeSeries(from);
        } catch (final Exception e) {
            LOG.error("An error occured in convertForward()", e);
            throw new ConversionException("An error occured in convertForward()", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   to  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException  DOCUMENT ME!
     */
    @Override
    public InputStream convertBackward(final TimeSeries to) throws ConversionException {
        try {
            return serializeTimeSeriesToInputStream(to);
        } catch (final Exception e) {
            LOG.error("An error occured in convertBackward()", e);
            throw new ConversionException("An error occured in convertBackward()", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ts  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static InputStream serializeTimeSeriesToInputStream(final TimeSeries ts) {
        return new ByteArrayInputStream(serializeTimeSeries(ts));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ts  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public static byte[] serializeTimeSeries(final TimeSeries ts) {
        if (ts == null) {
            throw new NullPointerException("Given TimeSeries instance must not be null");
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1000);
        final DeflaterOutputStream zOut = new DeflaterOutputStream(byteOut);
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zOut));

        try {
            // --- Serialize interesting ts properties

            writer.write(SECTION_TS_PROPS);
            writer.newLine();

            Object prop;
            final Set<String> tsKeys = ts.getTSKeys();
            for (final String k : tsKeys) {
                writer.write(k);

                prop = ts.getTSProperty(k);

                if (prop instanceof String) {
                    writer.write(FIELD_SEP_AS_CHAR);
                    writer.write(TYPE_STRING);
                    writer.write(FIELD_SEP_AS_CHAR);
                    writer.write(String.valueOf(prop));
                    writer.newLine();
                } else if (prop instanceof Integer) {
                    writer.write(FIELD_SEP_AS_CHAR);
                    writer.write(TYPE_INTEGER);
                    writer.write(FIELD_SEP_AS_CHAR);
                    writer.write(String.valueOf(prop));
                    writer.newLine();
                } else if (prop instanceof String[]) {
                    writer.write(FIELD_SEP_AS_CHAR);
                    writer.write(TYPE_STRING_ARRAY);

                    final String[] values = (String[])prop;
                    for (int i = 0; i < values.length; i++) {
                        writer.write(FIELD_SEP_AS_CHAR);
                        writer.write(values[i]);
                    }
                    writer.newLine();
                } else {
                    LOG.warn("Key: " + k + "   Value: " + prop
                                + "-> Ignored as type is not supported for serialization: "
                                + prop.getClass().getName());
                }
            }

            // --- Serialize data

            writer.write(SECTION_TS_DATA);

            final TimeStamp[] timestamps = ts.getTimeStampsArray();

            TimeStamp stamp;
            for (int i = 0; i < timestamps.length; i++) {
                stamp = timestamps[i];

                writer.newLine();
                writer.write(dateFormat.format(stamp.asDate()));
                writer.write(FIELD_SEP_AS_CHAR);
                writer.write(String.valueOf(ts.getValue(stamp, PropertyNames.VALUE)));
            }

            writer.flush();
        } catch (Exception e) {
            LOG.error("An error occured while serializing TimeSeries: " + ts, e);
            return new byte[0];
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                LOG.error("An error occured while closing Writer", e);
                return new byte[0];
            }
        }

        return byteOut.toByteArray();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     * @throws  RuntimeException      DOCUMENT ME!
     */
    public static TimeSeries deserializeTimeSeries(final InputStream in) {
        if (in == null) {
            throw new NullPointerException("Given InputStream must not be null");
        }

        final InflaterInputStream zin = new InflaterInputStream(new BufferedInputStream(in));
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream(1000);

        try {
            final byte[] buff = new byte[1000];
            int numReadIn = 0;
            while ((numReadIn = zin.read(buff, 0, buff.length)) != -1) {
                bOut.write(buff, 0, numReadIn);
            }
            zin.close();
            bOut.flush();
            bOut.close();
        } catch (final Exception e) {
            // TODO throw subclass of RuntimeException
            LOG.error("An error occured while decompressing serialized data", e);
            throw new RuntimeException("An error occured while decompressing serialized data", e);
        }

        // --- start processing decompressed data

        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        final ByteArrayInputStream bin2 = new ByteArrayInputStream(bOut.toByteArray());
        final LineNumberReader reader = new LineNumberReader(new InputStreamReader(bin2));

        final TimeSeriesImpl ts = new TimeSeriesImpl();

        try {
            String line;

            // -- start processing ts properties

            line = reader.readLine();
            if ((line != null) && (line.intern() == SECTION_TS_PROPS)) {
                String[] splitted;
                while (((line = reader.readLine()) != null) && (line.intern() != SECTION_TS_DATA)) {
                    splitted = line.split(FIELD_SEP_AS_STRING);
                    if (splitted.length == 3) {
                        if (splitted[1].intern() == TYPE_STRING) {
                            ts.setTSProperty(splitted[0], splitted[2]);
                        } else if (splitted[1].intern() == TYPE_STRING_ARRAY) {
                            ts.setTSProperty(splitted[0], new String[] { splitted[2] });
                        } else if (splitted[1].intern() == TYPE_INTEGER) {
                            ts.setTSProperty(splitted[0], Integer.parseInt(splitted[2]));
                        } else {
                            throw new IllegalArgumentException("Data type " + splitted[1] + " is not supported");
                        }
                    } else if (splitted.length > 3) {
                        ts.setTSProperty(splitted[0], Arrays.copyOfRange(splitted, 2, splitted.length - 2));
                    } else {
                        throw new IllegalArgumentException(
                            "TimeSeries ts properties representation is corrupted. See line'"
                                    + line
                                    + "'");
                    }
                }
            } else {
                throw new IllegalArgumentException("TimeSeries representation is corrupted. Expected '"
                            + SECTION_TS_PROPS
                            + "'. Got: '" + line + "'");
            }

            // -- start processing ts data

            if ((line != null) && (line.intern() == SECTION_TS_DATA)) {
                String[] splitted;
                while ((line = reader.readLine()) != null) {
                    splitted = line.split(FIELD_SEP_AS_STRING);
                    if (splitted.length == 2) {
                        try {
                            ts.setValue(new TimeStamp(dateFormat.parse(splitted[0])),
                                PropertyNames.VALUE,
                                Float.parseFloat(splitted[1]));
                        } catch (final Exception e) {
                            LOG.error("TimeSeries data representation is corrupted.", e);
                            throw new IllegalArgumentException("TimeSeries data representation is corrupted.", e);
                        }
                    } else {
                        throw new IllegalArgumentException("TimeSeries data representation is corrupted. See line'"
                                    + line + "'");
                    }
                }
            } else {
                throw new IllegalArgumentException("TimeSeries representation is corrupted. Expected '"
                            + SECTION_TS_PROPS
                            + "'. Got: '" + line + "'");
            }
        } catch (final IOException e) {
            LOG.error("An IO error occured while deserializing TimeSeries representation", e);
            return null;
        } finally {
            try {
                reader.close();
            } catch (final Exception e) {
                LOG.error("An error occured while closing Reader", e);
                return null;
            }
        }

        return ts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tsRepresentation  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static TimeSeries deserializeTimeSeries(final byte[] tsRepresentation) {
        if (tsRepresentation == null) {
            throw new NullPointerException("Given byte representation of a TimeSeries must not be null");
        }

        // TODO more reasonable length check
        if (tsRepresentation.length == 0) {
            throw new IllegalArgumentException("Given byte representation of a TimeSeries"
                        + " does not consist of sufficient number of bytes");
        }

        return deserializeTimeSeries(new ByteArrayInputStream(tsRepresentation));
    }

    @Override
    public String toString() {
        return "Interner Converter";
    }
}
