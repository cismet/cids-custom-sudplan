/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimeseriesRetrieverConfig {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesRetrieverConfig.class);

    public static final String TSTB_TOKEN = "tstb"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final String handlerLookup;
    private final URL sosLocation;

    private final String procedure;
    private final String foi;
    private final String obsProp;
    private final String offering;

    private final Geometry geometry;
    private final TimeInterval interval;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeseriesRetrieverConfig object.
     *
     * @param   handlerLookup  DOCUMENT ME!
     * @param   sosLocation    DOCUMENT ME!
     * @param   procedure      DOCUMENT ME!
     * @param   foi            DOCUMENT ME!
     * @param   obsProp        DOCUMENT ME!
     * @param   offering       DOCUMENT ME!
     * @param   geometry       DOCUMENT ME!
     * @param   interval       DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public TimeseriesRetrieverConfig(final String handlerLookup,
            final URL sosLocation,
            final String procedure,
            final String foi,
            final String obsProp,
            final String offering,
            final Geometry geometry,
            final TimeInterval interval) {
        if ((handlerLookup == null) || (sosLocation == null)) {
            throw new IllegalArgumentException("handlerLookup or sosLocation must not be null"); // NOI18N
        }

        this.handlerLookup = handlerLookup;
        this.sosLocation = sosLocation;
        this.procedure = procedure;
        this.foi = foi;
        this.obsProp = obsProp;
        this.offering = offering;
        this.geometry = geometry;
        this.interval = interval;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public URL getSosLocation() {
        return sosLocation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFoi() {
        return foi;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getObsProp() {
        return obsProp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public Parameter getObservedProperty() {
        if (obsProp == null) {
            return null;
        }

        for (final Parameter prop : Parameter.values()) {
            if (obsProp.equals(prop.getPropertyKey())) {
                return prop;
            }
        }

        throw new IllegalStateException("unknown observed property: " + obsProp);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getOffering() {
        return offering;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getHandlerLookup() {
        return handlerLookup;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TimeInterval getInterval() {
        return interval;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Properties getFilterProperties() {
        final Properties properties = new Properties();

        if (procedure != null) {
            properties.put(TimeSeries.PROCEDURE, procedure);
        }

        if (foi != null) {
            properties.put(TimeSeries.FEATURE_OF_INTEREST, foi);
        }

        if (obsProp != null) {
            properties.put(TimeSeries.OBSERVEDPROPERTY, obsProp);
        }

        if (offering != null) {
            properties.put(TimeSeries.OFFERING, offering);
        }

        return properties;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tstbUrl  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public static TimeseriesRetrieverConfig fromTSTBUrl(final String tstbUrl) throws MalformedURLException {
        if ((tstbUrl == null) || tstbUrl.isEmpty()) {
            return null;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("processing: " + tstbUrl);
        }

        final String[] tstbToken = tstbUrl.split(":", 2);                   // NOI18N
        if ((tstbToken.length < 2) || !tstbToken[0].equals(TSTB_TOKEN)) {   // NOI18N
            throw new MalformedURLException("invalid tstburl: " + tstbUrl); // NOI18N
        }

        final TimeseriesRetrieverConfig config;
        final String[] lookupToken = tstbToken[1].split("@", 2);     // NOI18N
        if (lookupToken.length == 2) {
            final String handlerLookup = lookupToken[0];
            if (LOG.isDebugEnabled()) {
                LOG.debug("found handler lookup: " + handlerLookup); // NOI18N
            }

            final String[] locationToken = lookupToken[1].split("\\?", 2);                              // NOI18N
            if (locationToken.length == 1) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("creating new config from handler lookup and localtion: " + handlerLookup // NOI18N
                                + " || "                                                                // NOI18N
                                + locationToken[0]);
                }

                config = new TimeseriesRetrieverConfig(
                        handlerLookup,
                        new URL(locationToken[0]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
            } else if (locationToken.length == 2) {
                final String location = locationToken[0];
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found location: " + location);
                }

                final String[] params = locationToken[1].split("&"); // NOI18N
                String procedure = null;
                String foi = null;
                String obsProp = null;
                String offering = null;
                Geometry geometry = null;
                final TimeInterval interval = null;
                for (final String param : params) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("found param: " + param);          // NOI18N
                    }

                    final String[] kvSplit = param.split("=", 2);                                                      // NOI18N
                    final String key;
                    final String value;
                    if (kvSplit.length == 2) {
                        key = kvSplit[0];
                        value = kvSplit[1];
                    } else {
                        throw new MalformedURLException("invalid tstburl, invalid param '" + param + "': " + tstbUrl); // NOI18N
                    }

                    if (TimeSeries.PROCEDURE.equals(key)) {
                        procedure = value;
                    } else if (TimeSeries.FEATURE_OF_INTEREST.equals(key)) {
                        foi = value;
                    } else if (TimeSeries.OBSERVEDPROPERTY.equals(key)) {
                        obsProp = value;
                    } else if (TimeSeries.OFFERING.equals(key)) {
                        offering = value;
                    } else if (TimeSeries.GEOMETRY.equals(key)) {
                        final WKTReader reader = new WKTReader(new GeometryFactory());
                        try {
                            geometry = reader.read(value);
                        } catch (final ParseException ex) {
                            final String message = "cannot read geometry from value: " + ex; // NOI18N
                            LOG.error(message, ex);
                            throw new MalformedURLException(message);
                        }
                    } else if (false) {
                        // TODO: add TimeInterval support
                    } else {
                        throw new MalformedURLException("invalid tstburl, invalid token '" + key + "': " + tstbUrl); // NOI18N
                    }
                }

                config = new TimeseriesRetrieverConfig(
                        handlerLookup,
                        new URL(location),
                        procedure,
                        foi,
                        obsProp,
                        offering,
                        geometry,
                        interval);
            } else {
                throw new MalformedURLException("invalid tstburl: " + tstbUrl); // NOI18N
            }
        } else {
            throw new MalformedURLException("invalid tstburl, missing handler lookup: " + tstbUrl); // NOI18N
        }

        return config;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String toTSTBUrl() {
        final StringBuilder sb = new StringBuilder(TSTB_TOKEN);
        sb.append(':').append(handlerLookup);
        sb.append('@').append(sosLocation);
        if (!((procedure == null) && (foi == null) && (obsProp == null) && (offering == null) && (geometry == null))) {
            sb.append('?');
            if (procedure != null) {
                sb.append(TimeSeries.PROCEDURE).append('=').append(procedure).append('&');
            }
            if (foi != null) {
                sb.append(TimeSeries.FEATURE_OF_INTEREST).append('=').append(foi).append('&');
            }
            if (obsProp != null) {
                sb.append(TimeSeries.OBSERVEDPROPERTY).append('=').append(obsProp).append('&');
            }
            if (offering != null) {
                sb.append(TimeSeries.OFFERING).append('=').append(offering).append('&');
            }
            if (geometry != null) {
                sb.append(TimeSeries.GEOMETRY).append('=').append(geometry);
            }
            // TODO: add Timeinterval support

            if (sb.charAt(sb.length() - 1) == '&') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toTSTBUrl() + "[" + super.toString() + "]"; // NOI18N
    }
}
