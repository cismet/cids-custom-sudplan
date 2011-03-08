/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.openide.util.NbBundle;

import java.io.Serializable;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Unit extends LocalisedEnum<Unit> implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    public static final Unit MM = new Unit(
            "urn:ogc:def:uom:OGC:mm", // NOI18N
            NbBundle.getMessage(Unit.class, "Unit.MILLIMETERS.localisedName")); // NOI18N
    public static final Unit PPM = new Unit(
            "urn:ogc:def:uom:OGC:ppm", // NOI18N
            NbBundle.getMessage(Unit.class, "Unit.PARTICLES_PER_MILLION.localisedName")); // NOI18N
    public static final Unit PPB = new Unit(
            "urn:ogc:def:uom:OGC:ppb", // NOI18N
            NbBundle.getMessage(Unit.class, "Unit.PARTICLES_PER_BILLION.localisedName")); // NOI18N
    public static final Unit KELVIN = new Unit(
            "urn:ogc:def:uom:OGC:K", // NOI18N
            NbBundle.getMessage(Unit.class, "Unit.KELVIN.localisedName")); // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient String propertyKey;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Unit object.
     */
    private Unit() {
        this(null, null);
    }

    /**
     * Creates a new Unit object.
     *
     * @param  propertyKey  DOCUMENT ME!
     */
    private Unit(final String propertyKey) {
        this(propertyKey, null);
    }

    /**
     * Creates a new Unit object.
     *
     * @param  propertyKey    DOCUMENT ME!
     * @param  localisedName  DOCUMENT ME!
     */
    private Unit(final String propertyKey, final String localisedName) {
        super(localisedName);
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

    @Override
    protected Unit[] internalValues() {
        return values();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Unit[] values() {
        return new Unit[] { MM, PPB, PPM, KELVIN };
    }
}
