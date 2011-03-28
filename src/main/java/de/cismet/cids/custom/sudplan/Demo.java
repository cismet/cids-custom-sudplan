/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.DataHandler.Access;
import at.ac.ait.enviro.tsapi.handler.DataHandlerFactory;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@Deprecated
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
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public DataHandler getSOSDH() {
        final DataHandler dh = DataHandlerFactory.Lookup.lookup("SOS-SUDPLAN-Dummy");
        try {
            final BeanInfo info = Introspector.getBeanInfo(dh.getClass(), Introspector.USE_ALL_BEANINFO);
            for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals("endpoint")) {
                    pd.getWriteMethod().invoke(dh, new URL("http://dummy.org"));
                }
            }
            dh.open();
        } catch (final Exception ex) {
            throw new IllegalStateException("cannot open datahandler", ex);
        }

        // setup filters and properties for createDatapoint
        final Properties dstFilter = new Properties();
        dstFilter.put(TimeSeries.OFFERING, "Station_3202_1day");
        dstFilter.put(TimeSeries.FEATURE_OF_INTEREST, "urn:MyOrg:feature:linz");
        dstFilter.put(TimeSeries.PROCEDURE, "urn:ogc:object:LINZ:prec:A1B_1day_agg");
        dstFilter.put(TimeSeries.OBSERVEDPROPERTY, Variable.PRECIPITATION.getPropertyKey());

        final Map<String, Object> dstProps = new HashMap<String, Object>();
        dstProps.put(
            "ts:sps:reference_rain",
            "'ts:feature_of_interest'=>'urn:MyOrg:feature:linz', "
                    + "'ts:procedure' => 'urn:ogc:object:LINZ:prec:10m', "
                    + "'ts:observed_property' => '"
                    + Variable.PRECIPITATION.getPropertyKey()
                    + "', "
                    + "'ts:offering' => 'Station_3202_10min'");

        // create datapoint
        dh.createDatapoint(dstFilter, dstProps, Access.READ);

        return dh;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DataHandler getCleanSOSDH() {
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
