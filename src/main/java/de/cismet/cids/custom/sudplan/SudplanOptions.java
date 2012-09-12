/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * DOCUMENT ME!
 *
 * @author   Martin Scholl
 * @version  $Revision$, $Date$
 */
public final class SudplanOptions {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_AQ_SOS_URL = "aqSosUrl"; // NOI18N
    public static final String PROP_AQ_SPS_URL = "aqSpsUrl"; // NOI18N
    public static final String PROP_AQ_EDB_URL = "aqEdbUrl"; // NOI18N
    public static final String PROP_HD_HYPE_IP = "hdHypeIp"; // NOI18N
    public static final String PROP_RF_SOS_URL = "rfSosUrl"; // NOI18N
    public static final String PROP_RF_SPS_URL = "rfSpsUrl"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient Map<String, Object> properties;
    private final transient ReentrantReadWriteLock rwLock;

    private final transient PropertyChangeSupport pcSupport;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SudplanOptions object.
     */
    private SudplanOptions() {
        pcSupport = new PropertyChangeSupport(this);
        properties = new HashMap<String, Object>();
        rwLock = new ReentrantReadWriteLock();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static SudplanOptions getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pcl  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener pcl) {
        pcSupport.addPropertyChangeListener(pcl);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pcl  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener pcl) {
        pcSupport.removePropertyChangeListener(pcl);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, Object> clearProperties() {
        try {
            rwLock.writeLock().lock();

            final Map<String, Object> ret = new HashMap<String, Object>(properties);

            properties.clear();

            return ret;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   property  DOCUMENT ME!
     * @param   newValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProperty(final String property, final Object newValue) {
        try {
            rwLock.writeLock().lock();

            final Object oldValue = properties.put(property, newValue);

            pcSupport.firePropertyChange(property, oldValue, newValue);

            return oldValue;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   property  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getProperty(final String property) {
        return getProperty(property, false, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   property     DOCUMENT ME!
     * @param   waitForInit  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getProperty(final String property, final boolean waitForInit) {
        return getProperty(property, waitForInit, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   property     DOCUMENT ME!
     * @param   waitForInit  DOCUMENT ME!
     * @param   timeout      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getProperty(final String property, final boolean waitForInit, final long timeout) {
        boolean locked = true;
        try {
            rwLock.readLock().lock();

            final Object value = properties.get(property);
            if ((value == null) && waitForInit) {
                // lives only in the context of this thread and the thread that causes the property change (because of
                // the closure)
                final Object initWaiter = new Object();

                /**
                 * DOCUMENT ME!
                 *
                 * @version  $Revision$, $Date$
                 */
                class NotifyingPCL implements PropertyChangeListener {

                    private Object value = null;

                    /**
                     * DOCUMENT ME!
                     *
                     * @param  evt  DOCUMENT ME!
                     */
                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        if ((evt.getNewValue() != null) && property.equals(evt.getPropertyName()) && (value == null)) {
                            synchronized (initWaiter) {
                                value = evt.getNewValue();
                                initWaiter.notify();
                            }
                        }
                    }
                }

                final NotifyingPCL pcl = new NotifyingPCL();
                addPropertyChangeListener(pcl);
                locked = false;
                rwLock.readLock().unlock();
                synchronized (initWaiter) {
                    try {
                        // we're waiting until the property change and loose the initWaiter monitor
                        initWaiter.wait(timeout);
                    } catch (final InterruptedException ex) {
                        // we don't care about an interrupt, the caller is simply unlucky and will not receive an
                        // initialised value
                    }

                    // the initWaiter monitor is restored again, so we can remove the pcl and as we return the
                    // pcl.value before we leave the sync block, we can be sure that the value will not change
                    // anymore
                    removePropertyChangeListener(pcl);

                    // the value may be null if the thread was waken up but the property change did not happen yet,
                    // either because of the timeout that was reached or some other condition causing the thread to wake
                    // up again (see Object.wait())
                    return pcl.value;
                }
            } else {
                return value;
            }
        } finally {
            if (locked) {
                rwLock.readLock().unlock();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAqSosUrl() {
        return (String)getProperty(PROP_AQ_SOS_URL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aqSosUrl  DOCUMENT ME!
     */
    public void setAqSosUrl(final String aqSosUrl) {
        setProperty(PROP_AQ_SOS_URL, aqSosUrl);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAqSpsUrl() {
        return (String)getProperty(PROP_AQ_SPS_URL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aqSpsUrl  DOCUMENT ME!
     */
    public void setAqSpsUrl(final String aqSpsUrl) {
        setProperty(PROP_AQ_SPS_URL, aqSpsUrl);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getHdHypeIp() {
        return (String)getProperty(PROP_HD_HYPE_IP);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hdHypeIp  DOCUMENT ME!
     */
    public void setHdHypeIp(final String hdHypeIp) {
        setProperty(PROP_HD_HYPE_IP, hdHypeIp);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRfSosUrl() {
        return (String)getProperty(PROP_RF_SOS_URL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfSosUrl  DOCUMENT ME!
     */
    public void setRfSosUrl(final String rfSosUrl) {
        setProperty(PROP_RF_SOS_URL, rfSosUrl);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getRfSpsUrl() {
        return (String)getProperty(PROP_RF_SPS_URL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rfSpsUrl  DOCUMENT ME!
     */
    public void setRfSpsUrl(final String rfSpsUrl) {
        setProperty(PROP_RF_SPS_URL, rfSpsUrl);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAqEdbUrl() {
        return (String)getProperty(PROP_AQ_EDB_URL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aqEdbUrl  DOCUMENT ME!
     */
    public void setAqEdbUrl(final String aqEdbUrl) {
        setProperty(PROP_AQ_EDB_URL, rwLock);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final SudplanOptions INSTANCE = new SudplanOptions();
    }
}
