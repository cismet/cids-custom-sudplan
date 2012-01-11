/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public final class TimeSeriesRendererUtil {

    //~ Static fields/initializers ---------------------------------------------

    private static final Pattern REGEX_PREC = Pattern.compile("prec:(\\d+[YMs])"); // NOI18N
    private static final Logger LOG = Logger.getLogger(TimeSeriesRendererUtil.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesRendererUtil object.
     */
    private TimeSeriesRendererUtil() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns the best suitable {@link Resolution} for TimeSeries preview visualization.
     *
     * @param   config 
     *
     * @return  best suitable preview {@link Resolution}
     */
    public static Resolution getPreviewResolution(final TimeseriesRetrieverConfig config) {
        if (config.getProtocol().equals(TimeseriesRetrieverConfig.PROTOCOL_DAV)) {
            return Resolution.DAY;
        } else {
            final String procedure = config.getProcedure();
            final Matcher m = REGEX_PREC.matcher(procedure);

            if (m.matches()) {
                final String precision = m.group(1);
                if (!precision.equals(Resolution.DAY.getPrecision())) {
                    if (precision.equals(Resolution.MONTH.getPrecision())) {
                        return Resolution.MONTH;
                    } else if (precision.equals(Resolution.YEAR.getPrecision())) {
                        return Resolution.YEAR;
                    } else if (precision.equals(Resolution.DECADE.getPrecision())) {
                        return Resolution.DECADE;
                    } else {
                        LOG.warn("Unknown resolution " + precision + ". Using default resolution "
                                    + Resolution.DAY.getLocalisedName()); // NOI18N
                    }
                }
            } else {
                LOG.warn("Can not determine TimeSeries resolution. Using default resolution "
                            + Resolution.DAY.getLocalisedName());         // NOI18N
            }

            return Resolution.DAY;
        }
    }
}
