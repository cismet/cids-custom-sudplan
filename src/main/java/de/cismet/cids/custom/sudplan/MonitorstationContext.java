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
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class MonitorstationContext extends LocalisedEnum<MonitorstationContext> {

    //~ Static fields/initializers ---------------------------------------------

    public static final MonitorstationContext AQ = new MonitorstationContext("AQ", "Air Quality");
    public static final MonitorstationContext HD = new MonitorstationContext("HD", "Hydrology");
    public static final MonitorstationContext RF = new MonitorstationContext("RF", "Rainfall");
    public static final MonitorstationContext LI_RF = new MonitorstationContext("LI-RF", "Linz Rainfall");
    public static final MonitorstationContext LI_WI = new MonitorstationContext("LI-WI", "Linz WWTP Inflow Sensors");
    public static final MonitorstationContext LI_WO = new MonitorstationContext("LI-WO", "Linz WWTP Outflow Sensors");
    public static final MonitorstationContext LI_HY = new MonitorstationContext("LI-HY", "Linz Hydraulics Sensors");

    //~ Instance fields --------------------------------------------------------

    private final String key;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MonitorstationContext object.
     *
     * @param  key            DOCUMENT ME!
     * @param  localisedName  DOCUMENT ME!
     */
    public MonitorstationContext(final String key, final String localisedName) {
        super(localisedName);

        this.key = key;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKey() {
        return key;
    }
    
    public boolean isCSContext(){
        return !key.startsWith("LI"); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static MonitorstationContext getMonitorstationContext(final String key) {
        for (final MonitorstationContext c : values()) {
            if (c.getKey().equals(key)) {
                return c;
            }
        }

        throw new IllegalArgumentException("unknown context: " + key); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MonitorstationContext[] values() {
        return new MonitorstationContext[] { AQ, HD, RF, LI_RF, LI_WI, LI_WO, LI_HY };
    }

    @Override
    protected MonitorstationContext[] internalValues() {
        return values();
    }
}
