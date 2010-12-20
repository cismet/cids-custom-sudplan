/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cs;

import at.ac.ait.enviro.sosclient.SOSDummyClient;
import at.ac.ait.enviro.tsapi.handler.DataHandler;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.URI;

import java.util.Set;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SOSOutputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SOSOutputManager.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean outputBean;
    private final transient SOSOutputManagerUI ui;

    private final transient DataHandler client;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SOSOutputManager object.
     */
    public SOSOutputManager() {
        ui = new SOSOutputManagerUI(this);
        client = new SOSDummyClient();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public JComponent getUI() {
        return ui;
    }

    @Override
    public URI getLocation() throws IOException {
        try {
            return new URI((String)outputBean.getProperty("uri")); // NOI18N
        } catch (final Exception e) {
            final String message = "cannot create uri";            // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    @Override
    public void apply() throws IOException {
        // NOOP, read-only
    }

    @Override
    public Feature getFeature() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CidsBean getCidsBean() {
        return outputBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    void doer() throws IOException {
        final DataHandler dh = new SOSDummyClient();
        dh.open();
        final Set<String> filterNames = dh.getFilterNames(DataHandler.Access.READ);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getFeatureOfInterest() {
        return client.getFilterValues("ts:feature_of_interest", null, DataHandler.Access.READ).iterator().next();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getObservedProperty() {
        return client.getFilterValues("ts:observed_property", null, DataHandler.Access.READ).iterator().next();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getOffering() {
        return client.getFilterValues("ts:offering", null, DataHandler.Access.READ).iterator().next();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getProcedure() {
        return client.getFilterValues("ts:procedure", null, DataHandler.Access.READ).iterator().next();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Geometry getGeometry() {
        final Envelope env = (Envelope)client.getDatapoints(null, DataHandler.Access.READ).iterator().next()
                    .getProperties()
                    .get("ts:geometry");

        final GeometryFactory factory = new GeometryFactory();

        return factory.toGeometry(env);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDescription() {
        return client.getDatapoints(null, DataHandler.Access.READ)
                    .iterator()
                    .next()
                    .getProperties()
                    .get("ts:description")
                    .toString();
    }

    Date getFromDate() {
        return (Date)client.getDatapoints(null, DataHandler.Access.READ)
                    .iterator()
                    .next()
                    .getProperties()
                    .get("ts:available_data_min");
    }

    Date getToDate() {
        return (Date)client.getDatapoints(null, DataHandler.Access.READ)
                    .iterator()
                    .next()
                    .getProperties()
                    .get("ts:available_data_max");
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.outputBean = cidsBean;
    }
}
