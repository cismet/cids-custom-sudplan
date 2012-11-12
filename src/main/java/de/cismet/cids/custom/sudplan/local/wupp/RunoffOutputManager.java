/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.NbBundle;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunoffOutputManager implements Manager {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(RunoffOutputManager.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean modelOutputBean;
    private transient volatile RunoffOutputManagerUI ui;

    private transient XBoundingBox bbox;

    //~ Methods ----------------------------------------------------------------

    @Override
    public SimulationResult getUR() throws IOException {
        final String json = (String)modelOutputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, SimulationResult.class);
    }

    @Override
    public void finalise() throws IOException {
        // not needed
    }

    @Override
    public Feature getFeature() throws IOException {
        return null;
    }

    @Override
    public CidsBean getCidsBean() {
        return modelOutputBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.modelOutputBean = cidsBean;
    }

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new RunoffOutputManagerUI(this);
                }
            }
        }

        return ui;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public XBoundingBox loadBBoxFromInput() {
        if (bbox == null) {
            final RunoffInputManager m = (RunoffInputManager)SMSUtils.loadManagerFromModel((CidsBean)
                    modelOutputBean.getProperty(
                        "model"),
                    ManagerType.INPUT);

            final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", SMSUtils.TABLENAME_MODELINPUT);

            if (mc == null) {
                throw new IllegalStateException("cannot fetch model input metaclass"); // NOI18N
            }

            final StringBuilder sb = new StringBuilder();

            sb.append("SELECT ").append(mc.getID()).append(", o.").append(mc.getPrimaryKey()); // NOI18N
            sb.append(" FROM ")
                    .append(mc.getTableName())
                    .append(" o, ")
                    .append(SMSUtils.TABLENAME_MODELRUN)
                    .append(" r");                                                             // NOI18N
            sb.append(" WHERE o.").append(mc.getPrimaryKey()).append(" = r.modelinput");       // NOI18N
            sb.append(" AND r.modeloutput = ").append(modelOutputBean.getProperty("id"));      // NOI18N

            final MetaObject[] metaObjects;
            try {
                metaObjects = SessionManager.getProxy()
                            .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                                    sb.toString(),
                                    SMSUtils.DOMAIN_SUDPLAN_WUPP);
            } catch (final ConnectionException ex) {
                final String message = "cannot get timeseries meta objects from database"; // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }

            if (metaObjects.length != 1) {
                throw new IllegalStateException("did not find exactly one input to this output: " + modelOutputBean); // NOI18N
            }

            m.setCidsBean(metaObjects[0].getBean());
            final RunoffInput io;
            try {
                io = m.getUR();
            } catch (final IOException ex) {
                throw new IllegalStateException("cannot fetch runoff input from ur", ex); // NOI18N
            }

            final CidsBean geocpmBean;
            if (io.getDeltaInputId() < 0) {
                geocpmBean = io.fetchGeocpmInput();
            } else {
                geocpmBean = (CidsBean)io.fetchDeltaInput().getProperty("original_object"); // NOI18N
            }

            final Geometry geom = (Geometry)geocpmBean.getProperty("geom.geo_field"); // NOI18N
            final Geometry geom31466 = CrsTransformer.transformToGivenCrs(geom.getEnvelope(), SMSUtils.EPSG_WUPP);

            bbox = new XBoundingBox(geom31466, SMSUtils.EPSG_WUPP, true);
        }

        return bbox;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sr  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    public URL prepareGetMapRequest(final SimulationResult sr) throws MalformedURLException {
        final String wmsGetMapLink = sr.getWmsGetCapabilitiesRequest()
                    .replace(
                        "request=GetCapabilities",
                        "request=GetMap&"
                        + "BBOX=<cismap:boundingBox>&"
                        + "WIDTH=<cismap:width>&"
                        + "HEIGHT=<cismap:height>&"
                        + "SRS=<cismap:srs>&"
                        + "FORMAT=image/png&TRANSPARENT="
                        + "TRUE&"
                        + "BGCOLOR=0xF0F0F0&"
                        + "EXCEPTIONS=application/vnd.ogc.se_xml"
                        + "&LAYERS="
                        + sr.getLayerName());

        return new URL(wmsGetMapLink);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  getMapUrl  DOCUMENT ME!
     * @param  bbox       DOCUMENT ME!
     * @param  name       DOCUMENT ME!
     */
    public void addResultLayerToMap(final SimpleWmsGetMapUrl getMapUrl, final XBoundingBox bbox, final String name) {
        final SimpleWMS layer = new SimpleWMS(getMapUrl);
        layer.setName(
            NbBundle.getMessage(
                RunoffOutputManager.class,
                "RunoffOutputManager.addResultLayerToMap(SimpleWmsGetMapUrl,XBoundingBox,String).resultLayer.name", // NOI18N
                name));
        CismapBroker.getInstance().getMappingComponent().getMappingModel().addLayer(layer);
        SMSUtils.showMappingComponent();
        CismapBroker.getInstance().getMappingComponent().gotoBoundingBoxWithHistory(bbox);
    }
}
