/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class CsoOverflow {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SWMMPROJECT = "swmmProject";

    //~ Instance fields --------------------------------------------------------

    protected transient float overflowVolume;
    protected transient float overflowFrequency;
    protected transient float overflowDuration;
    protected transient int cso;
    protected transient String name;
    private int swmmProject;
    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of overflowVolume.
     *
     * @return  the value of overflowVolume
     */
    public float getOverflowVolume() {
        return overflowVolume;
    }

    /**
     * Set the value of overflowVolume.
     *
     * @param  volume  overflowVolume new value of overflowVolume
     */
    public void setOverflowVolume(final float volume) {
        this.overflowVolume = volume;
    }

    /**
     * Get the value of overflowFrequency.
     *
     * @return  the value of overflowFrequency
     */
    public float getOverflowFrequency() {
        return overflowFrequency;
    }

    /**
     * Set the value of overflowFrequency.
     *
     * @param  frequency  new value of overflowFrequency
     */
    public void setOverflowFrequency(final float frequency) {
        this.overflowFrequency = frequency;
    }

    /**
     * Get the value of cso.
     *
     * @return  the value of cso
     */
    public int getCso() {
        return cso;
    }

    /**
     * Set the value of cso.
     *
     * @param  cso  new value of cso
     */
    public void setCso(final int cso) {
        this.cso = cso;
    }

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
     * @param  node  name new value of name
     */
    public void setName(final String node) {
        this.name = node;
    }

    /**
     * Get the value of overflowDuration.
     *
     * @return  the value of overflowDuration
     */
    public float getOverflowDuration() {
        return overflowDuration;
    }

    /**
     * Set the value of overflowDuration.
     *
     * @param  overflowDuration  new value of overflowDuration
     */
    public void setOverflowDuration(final float overflowDuration) {
        this.overflowDuration = overflowDuration;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchCso() {
        return SMSUtils.fetchCidsBean(this.getCso(), SwmmOutput.TABLENAME_LINZ_CSO);
    }

    /**
     * Get the value of swmmProject.
     *
     * @return  the value of swmmProject
     */
    public int getSwmmProject() {
        return swmmProject;
    }

    /**
     * Set the value of swmmProject.
     *
     * @param  swmmProject  new value of swmmProject
     */
    public void setSwmmProject(final int swmmProject) {
        final int oldSwmmProject = this.swmmProject;
        this.swmmProject = swmmProject;
        propertyChangeSupport.firePropertyChange(PROP_SWMMPROJECT, oldSwmmProject, swmmProject);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}