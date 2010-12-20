/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.sudplan.Grid;
import de.cismet.cids.custom.sudplan.ImmutableGrid;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class MultiplyHelper {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PARSER_INTRO_GRID = "#EXTGRID"; // NOI18N
    public static final String PARSER_INTRO_UNIT = "#EXTFILE"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(MultiplyHelper.class);

    private static final String PARSER_INTRO_GEOMETRY = "#AREA"; // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MultiplyHelper object.
     */
    private MultiplyHelper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static Double[] doublesFromFile(final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null"); // NOI18N
        }

        return doublesFromReader(new FileReader(file));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   reader  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static Double[] doublesFromReader(final Reader reader) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("reader must not be null"); // NOI18N
        }

        final List<Double> doubles = new ArrayList<Double>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);

            String line = br.readLine();
            while (line != null) {
                try {
                    doubles.add(Double.parseDouble(line));
                } catch (final NumberFormatException nfe) {
                    LOG.warn("unparseable input line: " + line, nfe); // NOI18N
                }

                line = br.readLine();
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    LOG.warn("could not close inputstream"); // NOI18N
                }
            }
        }

        return doubles.toArray(new Double[doubles.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static Grid gridFromFile(final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null"); // NOI18N
        }

        return gridFromReader(new FileReader(file));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   reader  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static Grid gridFromReader(final Reader reader) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("reader must not be null"); // NOI18N
        }

        final List<Double[]> doubles = new ArrayList<Double[]>();
        Geometry geometry = null;
        final String dataType = null;
        final String unit = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);

            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("#")) // NOI18N
                {
                    if (line.startsWith(PARSER_INTRO_GEOMETRY)) {
                        geometry = toGeometry(line);
                    } else if (line.startsWith(PARSER_INTRO_UNIT)) {
                        // TODO: implement
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring line '" + line + "', tbd"); // NOI18N
                        }
                    } else if (line.startsWith(PARSER_INTRO_GRID)) {
                        // TODO: implement
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring line '" + line + "', tbd");           // NOI18N
                        }
                    } else {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("unknown line introduction, ignoring: " + line); // NOI18N
                        }
                    }
                } else {
                    // assume that eny line not starting with '#' is grid data
                    try {
                        final String[] split = line.split(" "); // NOI18N
                        final Double[] doubleline = new Double[split.length];
                        for (int i = 0; i < split.length; ++i) {
                            doubleline[i] = Double.parseDouble(split[i]);
                        }
                        doubles.add(doubleline);
                    } catch (final NumberFormatException nfe) {
                        // TODO: probably an exception has to be thrown to guarantee integrity
                        LOG.warn("unparseable input line: " + line, nfe); // NOI18N
                    }
                }

                line = br.readLine();
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    LOG.warn("could not close inputstream", e); // NOI18N
                }
            }
        }

        return new ImmutableGrid(doubles.toArray(new Double[doubles.size()][]), geometry, dataType, unit);
    }

    /**
     * Inverses {@link #toString(com.vividsolutions.jts.geom.Geometry)}.
     *
     * @param   string {@link String} to parse
     *
     * @return  a {@link Geometry} parsed from the <code>String</code> or <code>null</code> if there was any issue
     */
    private static Geometry toGeometry(final String string) {
        if (string == null) {
            return null;
        }

        final String[] split = string.split(" ");

        if (split.length < 5) {
            if (LOG.isInfoEnabled()) {
                LOG.info("cannot find introductory string and coordingates: array too short: " + string); // NOI18N
            }

            return null;
        } else if (!PARSER_INTRO_GEOMETRY.equals(split[0])) {
            if (LOG.isInfoEnabled()) {
                LOG.info("string not introduced by '" + PARSER_INTRO_GEOMETRY + "': " + string); // NOI18N
            }

            return null;
        } else {
            final Double ulLatitude;
            final Double ulLongitude;
            final Double lrLatitude;
            final Double lrLongitude;

            // we assume the last four tokens are the coordinate values
            try {
                ulLatitude = Double.parseDouble(split[split.length - 1]);
                ulLongitude = Double.parseDouble(split[split.length - 4]);
                lrLatitude = Double.parseDouble(split[split.length - 2]);
                lrLongitude = Double.parseDouble(split[split.length - 3]);
            } catch (final NumberFormatException nfe) {
                LOG.warn("could not parse last four tokens: " + string, nfe); // NOI18N

                return null;
            }

            final GeometryFactory factory = new GeometryFactory();

            final Coordinate[] bbox = new Coordinate[5];
            bbox[0] = new Coordinate(ulLongitude, ulLatitude);
            bbox[1] = new Coordinate(ulLongitude, lrLatitude);
            bbox[2] = new Coordinate(lrLongitude, lrLatitude);
            bbox[3] = new Coordinate(lrLongitude, ulLatitude);
            bbox[4] = new Coordinate(ulLongitude, ulLatitude);
            final LinearRing ring = new LinearRing(new CoordinateArraySequence(bbox), factory);

            if (LOG.isDebugEnabled()) {
                LOG.debug("created linear ring from string: " + string + " || ring: " + ring); // NOI18N
            }

            return factory.createPolygon(ring, new LinearRing[0]);
        }
    }

    /**
     * Inverses {@link #toGeometry(java.lang.String)}.
     *
     * @param   geometry  the {@link Geometry} to create a {@link String} from
     *
     * @return  the created <code>String</code>
     */
    private static String toString(final Geometry geometry) {
        Coordinate ul = null;
        Coordinate lr = null;
        final Coordinate[] coord = geometry.getCoordinates();
        for (int i = 0; i < coord.length; ++i) {
            final Coordinate c1 = coord[i];
            // if i is the last index compare to the first coordinate
            final int next = (i < (coord.length - 1)) ? (i + 1) : 0;
            final Coordinate c2 = coord[next];
            if ((c1.x <= c2.x) && (c1.y >= c2.y) && ((ul == null) || ((c1.x <= ul.x) && (c1.y >= ul.y)))) {
                ul = c1;
            } else if ((c1.x >= c2.x) && (c1.y <= c2.y) && ((lr == null) || ((c1.x >= lr.x) && (c1.y <= lr.y)))) {
                lr = c1;
            }
        }

        assert (ul != null) && (lr != null) && !ul.equals(lr) : "error computing upper left and lower right coordinates"; // NOI18N

        final StringBuilder sb = new StringBuilder();
        sb.append(PARSER_INTRO_GEOMETRY).append(' ');
        sb.append(ul.x).append(' ');
        sb.append(lr.x).append(' ');
        sb.append(lr.y).append(' ');
        sb.append(ul.y);

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static int intFromFile(final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null"); // NOI18N
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            final String line = br.readLine();
            if (line == null) {
                final String message = "int not present: " + file;                        // NOI18N
                LOG.warn(message);
                throw new IOException(line);
            } else {
                try {
                    return Integer.parseInt(line);
                } catch (final NumberFormatException nfe) {
                    final String message = "cannot read multiplicand from line: " + line; // NOI18N
                    LOG.error(message, nfe);
                    throw new IOException(message, nfe);
                }
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    LOG.warn("could not close inputstream", e);                           // NOI18N
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file     DOCUMENT ME!
     * @param   numbers  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static void numbersToFile(final File file, final Number... numbers) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null"); // NOI18N
        }

        numbersToWriter(new FileWriter(file, false), numbers);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   writer   DOCUMENT ME!
     * @param   numbers  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static void numbersToWriter(final Writer writer, final Number... numbers) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null"); // NOI18N
        }
        if (numbers == null) {
            return;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(writer);
            for (final Number number : numbers) {
                bw.write(String.valueOf(number));
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (final IOException e) {
                    LOG.warn("could not close inputstream", e); // NOI18N
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     * @param   grid  numbers DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static void gridToFile(final File file, final Grid grid) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null"); // NOI18N
        }

        gridToWriter(new FileWriter(file, false), grid);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   writer  DOCUMENT ME!
     * @param   grid    numbers DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static void gridToWriter(final Writer writer, final Grid grid) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null"); // NOI18N
        }
        if (grid == null) {
            return;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(writer);

            if (grid.getGeometry() != null) {
                bw.write(toString(grid.getGeometry()));
                bw.newLine();
            }

            if ((grid.getUnit() != null) || (grid.getDataType() != null)) {
                // TODO: implement
            }

            for (final Double[] doubleline : grid.getData()) {
                for (final Double value : doubleline) {
                    bw.write(String.valueOf(value));
                    bw.write(' ');
                }
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (final IOException e) {
                    LOG.warn("could not close inputstream", e); // NOI18N
                }
            }
        }
    }
}
