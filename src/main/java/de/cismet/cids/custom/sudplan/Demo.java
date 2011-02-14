/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.DataHandlerFactory;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Demo {

    //~ Instance fields --------------------------------------------------------

    private transient DataHandler dsSOSDH;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Demo object.
     */
    private Demo() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Demo getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DataHandler getSOSDH() {
        return DataHandlerFactory.Lookup.lookup("SOS-SUDPLAN-Dummy");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public synchronized DataHandler getDSSOSDH() {
        return dsSOSDH;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dh  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public synchronized void setDSSOSDH(final DataHandler dh) {
        if (dsSOSDH != null) {
            throw new IllegalStateException("dssosdh already set"); // NOI18N
        }

        this.dsSOSDH = dh;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final Demo INSTANCE = new Demo();
    }
}
