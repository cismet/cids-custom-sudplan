/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class IDFCurve {

    //~ Instance fields --------------------------------------------------------

    // duration, frequency, intensity
    private transient SortedMap<Integer, SortedMap<Integer, Double>> data;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IDFCurve object.
     */
    public IDFCurve() {
        data = new TreeMap<Integer, SortedMap<Integer, Double>>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   duration   DOCUMENT ME!
     * @param   frequency  DOCUMENT ME!
     * @param   intensity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @JsonIgnore
    public boolean add(final int duration, final int frequency, final double intensity) {
        synchronized (this) {
            if (!data.containsKey(duration)) {
                data.put(duration, new TreeMap<Integer, Double>());
            }

            final Map<Integer, Double> freqToIntensity = data.get(duration);
            if (freqToIntensity.containsKey(frequency)) {
                return false;
            } else {
                freqToIntensity.put(frequency, intensity);

                return true;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @JsonIgnore
    public String toTSTBFormat() {
        final StringBuilder dura = new StringBuilder();
        final StringBuilder freq = new StringBuilder();
        final StringBuilder inte = new StringBuilder();

        synchronized (this) {
            final Iterator<Integer> itDurations = data.keySet().iterator();
            while (itDurations.hasNext()) {
                final Integer duration = itDurations.next();
                final SortedMap<Integer, Double> freqToIntensity = data.get(duration);
                final Iterator<Integer> itFrequencies = freqToIntensity.keySet().iterator();
                while (itFrequencies.hasNext()) {
                    final Integer frequency = itFrequencies.next();
                    final Double intensity = freqToIntensity.get(frequency);

                    dura.append(duration).append(':');
                    freq.append(frequency).append(':');
                    inte.append(intensity).append(':');
                }
            }
        }

        dura.deleteCharAt(dura.length() - 1);
        freq.deleteCharAt(freq.length() - 1);
        inte.deleteCharAt(inte.length() - 1);

        return dura.toString() + " " + freq.toString() + " " + inte.toString(); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SortedMap<Integer, SortedMap<Integer, Double>> getData() {
        return data;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  data  DOCUMENT ME!
     */
    public void setData(final SortedMap<Integer, SortedMap<Integer, Double>> data) {
        this.data = data;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @JsonIgnore
    public List<Integer> getFrequencies() {
        // assumes cube, will be sorted
        return new ArrayList<Integer>(data.get(data.firstKey()).keySet());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @JsonIgnore
    public Object[][] getDurationIntensityRows() {
        final Set<Integer> durations = data.keySet();
        final Iterator<Integer> itDurations = durations.iterator();

        final Object[][] rows = new Object[durations.size()][];
        int i = 0;
        while (itDurations.hasNext()) {
            final Integer duration = itDurations.next();
            final Collection<Double> intensities = data.get(duration).values();
            final Object[] row = new Object[intensities.size() + 1];
            row[0] = duration;
            int j = 1;
            for (final Double intensity : intensities) {
                row[j++] = intensity;
            }

            rows[i++] = row;
        }

        return rows;
    }
}
