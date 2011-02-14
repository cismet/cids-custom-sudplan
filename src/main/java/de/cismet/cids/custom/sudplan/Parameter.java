/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public enum Parameter {

    //~ Enum constants ---------------------------------------------------------

    TEMPERATURE("urn:ogc:def:property:OGC:Temperature"), // NOI18N
    PRECIPITATION("urn:ogc:def:property:OGC:rain"),      // NOI18N
    NO2("urn:ogc:def:property:OGC:NO2"),                 // NOI18N
    SO2, PM10, PM2_5, O3, EVAPORATION, SOIL_MOISTURE, MEAN_Q, LOCAL_Q;

    //~ Instance fields --------------------------------------------------------

    private final String propertyKey;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Parameter object.
     */
    private Parameter() {
        this(null);
    }

    /**
     * Creates a new Parameter object.
     *
     * @param  propertyKey  DOCUMENT ME!
     */
    private Parameter(final String propertyKey) {
        this.propertyKey = propertyKey;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPropertyKey() {
        return propertyKey;
    }
}
