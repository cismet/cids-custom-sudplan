/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaConfiguration {

    //~ Instance fields --------------------------------------------------------

    protected transient boolean enabled;
    protected transient float sedimentationEfficency;
    protected transient String name;
    protected String etaFile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EtaConfiguration object.
     */
    public EtaConfiguration() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of name.
     *
     * @return  the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     *
     * @param  name  new value of name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the value of sedimentationEfficency.
     *
     * @return  the value of sedimentationEfficency
     */
    public float getSedimentationEfficency() {
        return sedimentationEfficency;
    }

    /**
     * Set the value of sedimentationEfficency.
     *
     * @param  sedimentationEfficency  new value of sedimentationEfficency
     */
    public void setSedimentationEfficency(final float sedimentationEfficency) {
        this.sedimentationEfficency = sedimentationEfficency;
    }

    /**
     * Get the value of enabled.
     *
     * @return  the value of enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the value of enabled.
     *
     * @param  enabled  new value of enabled
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the value of etaFile.
     *
     * @return  the value of etaFile
     */
    public String getEtaFile() {
        return etaFile;
    }

    /**
     * Set the value of etaFile.
     *
     * @param  etaFile  new value of etaFile
     */
    public void setEtaFile(final String etaFile) {
        this.etaFile = etaFile;
    }
}
