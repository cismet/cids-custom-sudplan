/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class MultiplyHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(MultiplyHelper.class);

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
    public static List<Double> doublesFromFile(final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null"); // NOI18N
        }

        final List<Double> doubles = new ArrayList<Double>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            while (line != null) {
                try {
                    doubles.add(Double.parseDouble(line));
                } catch (final NumberFormatException nfe) {
                    LOG.warn("unparseable input line: " + line); // NOI18N
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

        return doubles;
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
        if (numbers == null) {
            return;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, false));
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
}
