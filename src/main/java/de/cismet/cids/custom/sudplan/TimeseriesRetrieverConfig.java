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
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimeseriesRetrieverConfig {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesRetrieverConfig.class);

    public static final String PROTOCOL_TSTB = "tstb";     // NOI18N
    public static final String PROTOCOL_DAV = "dav";       // NOI18N
    public static final String PROTOCOL_HYPE = "hype";     // NOI18N
    public static final String PROTOCOL_NETCDF = "netcdf"; // NOI18N

    public static final String NETCDF_LIMITED = "limited"; // NOI18N
    public static final DateFormat NETCDF_DATEFORMAT = new SimpleDateFormat("yyyyMMdd");

    private static final String TOKEN_TIMEINTERVAL = "ts:interval";
    private static final Pattern PATTERN = Pattern.compile("[\\],\\[](\\w+);(\\w+)[\\],\\[]");
    private static final DateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    static {
        UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    //~ Instance fields --------------------------------------------------------

    private final String protocol;
    private final String handlerLookup;
    private final URL location;

    private final String procedure;
    private final String foi;
    private final String obsProp;
    private final String offering;

    private final Geometry geometry;
    private TimeInterval interval;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeseriesRetrieverConfig object.
     *
     * @param   protocol       DOCUMENT ME!
     * @param   handlerLookup  DOCUMENT ME!
     * @param   location       DOCUMENT ME!
     * @param   procedure      DOCUMENT ME!
     * @param   foi            DOCUMENT ME!
     * @param   obsProp        DOCUMENT ME!
     * @param   offering       DOCUMENT ME!
     * @param   geometry       DOCUMENT ME!
     * @param   interval       DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public TimeseriesRetrieverConfig(final String protocol,
            final String handlerLookup,
            final URL location,
            final String procedure,
            final String foi,
            final String obsProp,
            final String offering,
            final Geometry geometry,
            final TimeInterval interval) {
        if ((protocol == null) || (location == null)) {
            throw new IllegalArgumentException("handlerLookup or sosLocation must not be null"); // NOI18N
        }

        this.protocol = protocol;
        this.handlerLookup = handlerLookup;
        this.location = location;
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
     * @param   resolution  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public TimeseriesRetrieverConfig changeResolution(final Resolution resolution) {
        if (resolution == null) {
            throw new NullPointerException("Resolution must not be null");
        }

        final String changedOffering;
        final String changedProcedure;
        if (PROTOCOL_NETCDF.equals(this.getProtocol())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("changing resolution of " + PROTOCOL_NETCDF + " timeseries to "
                            + resolution.getLocalisedName());
            }
            if (Resolution.HOUR.getPrecision().equals(resolution.getPrecision())) {
                changedOffering = this.offering;
                changedProcedure = NETCDF_LIMITED;
            } else {
                LOG.warn(PROTOCOL_NETCDF + " does not support resolution '"
                            + resolution.getPrecision() + "'");
                return this;
            }
        } else if (PROTOCOL_DAV.equals(this.getProtocol())) {
            changedOffering = this.offering.replaceFirst("_unknown$", '_' + resolution.getPrecision());                  // NOI18N
            changedProcedure = this.procedure.replaceFirst("prec:unknown", "prec:" + resolution.getPrecision());         // NOI18N
        } else {
            changedOffering = this.offering.replaceFirst("prec_\\d+[YMdhms]", "prec_" + resolution.getOfferingSuffix()); // NOI18N
            changedProcedure = this.procedure.replaceFirst("prec:\\d+[YMs]", "prec:" + resolution.getPrecision());       // NOI18N
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(MessageFormat.format(
                    "Changed offering {0} to {1} and procedure {2} to {3}",
                    new Object[] { this.offering, changedOffering, this.procedure, changedProcedure }));
        }

        final TimeseriesRetrieverConfig config = new TimeseriesRetrieverConfig(
                this.protocol,
                this.handlerLookup,
                this.location,
                changedProcedure,
                this.foi,
                this.obsProp,
                changedOffering,
                this.geometry,
                this.interval);

        return config;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public URL getLocation() {
        return location;
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
     */
    public Variable getObservedProperty() {
        if (obsProp == null) {
            return null;
        }

        for (final Variable prop : Variable.values()) {
            if (obsProp.equals(prop.getPropertyKey())) {
                return prop;
            }
        }

        LOG.warn("unknown observed property: " + obsProp); // NOI18N
        return Variable.UNKNOWN;
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
     * @param  interval  DOCUMENT ME!
     */
    public void setInterval(final TimeInterval interval) {
        this.interval = interval;
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
        return fromUrl(tstbUrl, PROTOCOL_TSTB);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   davUrl  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public static TimeseriesRetrieverConfig fromDavUrl(final String davUrl) throws MalformedURLException {
        return fromUrl(davUrl, PROTOCOL_DAV);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   hypeUrl  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public static TimeseriesRetrieverConfig fromHypeUrl(final String hypeUrl) throws MalformedURLException {
        return fromUrl(hypeUrl, PROTOCOL_HYPE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   netcdfUrl  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public static TimeseriesRetrieverConfig fromNetcdfUrl(final String netcdfUrl) throws MalformedURLException {
        return fromUrl(netcdfUrl, PROTOCOL_NETCDF);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public static TimeseriesRetrieverConfig fromUrl(final String url) throws MalformedURLException {
        if (url == null) {
            return null;
        }

        if (url.startsWith(PROTOCOL_TSTB)) {
            return fromUrl(url, PROTOCOL_TSTB);
        } else if (url.startsWith(PROTOCOL_DAV)) {
            return fromUrl(url, PROTOCOL_DAV);
        } else if (url.startsWith(PROTOCOL_HYPE)) {
            return fromUrl(url, PROTOCOL_HYPE);
        } else if (url.startsWith(PROTOCOL_NETCDF)) {
            return fromUrl(url, PROTOCOL_NETCDF);
        } else {
            throw new MalformedURLException("unknown protocol: " + url); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url    DOCUMENT ME!
     * @param   token  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    private static TimeseriesRetrieverConfig fromUrl(final String url, final String token)
            throws MalformedURLException {
        if ((url == null) || url.isEmpty()) {
            return null;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("processing: " + url);
        }

        final String[] tokenSplit = url.split(":", 2);                                     // NOI18N
        if ((tokenSplit.length < 2) || !tokenSplit[0].equals(token)) {                     // NOI18N
            throw new MalformedURLException("invalid url: " + url + " | token: " + token); // NOI18N
        }

        final TimeseriesRetrieverConfig config;
        final String[] lookupToken = tokenSplit[1].split("@", 2); // NOI18N
        final String handlerLookup;
        final String remaining;
        if (lookupToken.length == 1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("no handler lookup present");           // NOI18N
            }

            handlerLookup = null;
            remaining = lookupToken[0];
        } else if (lookupToken.length == 2) {
            handlerLookup = lookupToken[0];
            remaining = lookupToken[1];
            if (LOG.isDebugEnabled()) {
                LOG.debug("found handler lookup: " + handlerLookup);                       // NOI18N
            }
        } else {
            throw new MalformedURLException("invalid url: " + url + " | token: " + token); // NOI18N
        }

        final String[] locationToken = remaining.split("\\?", 2);                                  // NOI18N
        if (locationToken.length == 1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("creating new config from handler lookup and location: " + handlerLookup // NOI18N
                            + " || "                                                               // NOI18N
                            + locationToken[0]);
            }

            config = new TimeseriesRetrieverConfig(
                    token,
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
            TimeInterval interval = null;
            for (final String param : params) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found param: " + param);          // NOI18N
                }

                final String[] kvSplit = param.split("=", 2);                                                  // NOI18N
                final String key;
                final String value;
                if (kvSplit.length == 2) {
                    key = kvSplit[0];
                    value = kvSplit[1];
                } else {
                    throw new MalformedURLException("invalid tstburl, invalid param '" + param + "': " + url); // NOI18N
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
                } else if (TOKEN_TIMEINTERVAL.equals(key)) {
                    final Matcher matcher = PATTERN.matcher(value);

                    final TimeStamp start;
                    final TimeStamp end;

                    if (!matcher.matches()) {
                        throw new MalformedURLException("Time interval is not specified properly");
                    }

                    try {
                        start = new TimeStamp(UTC_DATE_FORMAT.parse(matcher.group(1)));
                        end = new TimeStamp(UTC_DATE_FORMAT.parse(matcher.group(2)));
                    } catch (final java.text.ParseException pe) {
                        throw new MalformedURLException("TimeStamps in TimeInterval do not have a "
                                    + "correct format: \"yyyyMMdd'T'HHmmss\"");
                    }

                    final boolean isLeftOpen = value.startsWith("]");
                    final boolean isRightOpen = value.endsWith("[");

                    if (isLeftOpen) {
                        if (isRightOpen)                                                                     // open interval
                        {
                            interval = TimeInterval.createOpenInterval(start, end);
                        } else                                                                               // left open interval
                        {
                            interval = TimeInterval.createLeftOpenInterval(start, end);
                        }
                    } else {
                        if (isRightOpen)                                                                     // right open interval
                        {
                            interval = TimeInterval.createRightOpenInterval(start, end);
                        } else                                                                               // closed interval
                        {
                            interval = TimeInterval.createClosedInterval(start, end);
                        }
                    }
                } else {
                    throw new MalformedURLException("invalid tstburl, invalid token '" + key + "': " + url); // NOI18N
                }
            }

            config = new TimeseriesRetrieverConfig(
                    token,
                    handlerLookup,
                    new URL(location),
                    procedure,
                    foi,
                    obsProp,
                    offering,
                    geometry,
                    interval);
        } else {
            throw new MalformedURLException("invalid url: " + url + " | token: " + token); // NOI18N
        }

        return config;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String toUrl() {
        final StringBuilder sb = new StringBuilder(protocol);
        sb.append(':').append(handlerLookup);
        sb.append('@').append(location);

        if (!((procedure == null)
                        && (foi == null)
                        && (obsProp == null)
                        && (offering == null)
                        && (geometry == null)
                        && (interval == null))) {
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
                sb.append(TimeSeries.GEOMETRY).append('=').append(geometry).append('&');
            }

            if (interval != null) {
                sb.append(TOKEN_TIMEINTERVAL).append('=');

                if (interval.isLeftOpen()) {
                    sb.append("]");
                } else {
                    sb.append("[");
                }

                sb.append(UTC_DATE_FORMAT.format(interval.getStart().asDate()));
                sb.append(";");
                sb.append(UTC_DATE_FORMAT.format(interval.getEnd().asDate()));

                if (interval.isRightOpen()) {
                    sb.append("[");
                } else {
                    sb.append("]");
                }

                sb.append('&');
            }

            if (sb.charAt(sb.length() - 1) == '&') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toUrl() + "[" + super.toString() + "]"; // NOI18N
    }
}
