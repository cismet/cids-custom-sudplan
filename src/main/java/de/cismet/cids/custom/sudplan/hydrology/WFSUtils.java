/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.ui.ComponentRegistry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.EventQueue;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Field;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;
import de.cismet.cismap.commons.wfs.capabilities.WFSCapabilities;
import de.cismet.cismap.commons.wfs.capabilities.WFSCapabilitiesFactory;
import de.cismet.cismap.commons.wfs.capabilities.deegree.DeegreeFeatureType;

import de.cismet.security.AccessHandler.ACCESS_METHODS;

import de.cismet.security.WebAccessManager;

/**
 * this is a utility class for wfs purposes.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class WFSUtils {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(WFSUtils.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WFSUtils object.
     */
    private WFSUtils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   capabilitiesUrl  DOCUMENT ME!
     * @param   qname            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static FeatureType getFeatureType(final String capabilitiesUrl, final QualifiedName qname)
            throws IOException {
        try {
            final WFSCapabilitiesFactory factory = new WFSCapabilitiesFactory();

            final WFSCapabilities wfsCapabilities = factory.createCapabilities(capabilitiesUrl);
            // FIXME: evil actions lead to the cake... the feature types without fetching their description, normal
            // facilities will do getFeatureInfo for every feature type, which is very inefficient and slow
            final Field field = wfsCapabilities.getClass().getDeclaredField("cap"); // NOI18N
            field.setAccessible(true);
            final org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities dCaps =
                (org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities)field.get(
                    wfsCapabilities);
            final WFSFeatureType basinType = dCaps.getFeatureTypeList().getFeatureType(qname);

            if (basinType == null) {
                throw new IllegalStateException("WFS does not serve feature with given qname: " + qname); // NOI18N
            }

            return new DeegreeFeatureType(basinType, wfsCapabilities);
        } catch (final Exception e) {
            final String message = "cannot fetch feature type for capabilities url and qname: [" // NOI18N
                        + capabilitiesUrl
                        + "|"                                                                    // NOI18N
                        + qname + "]";                                                           // NOI18N
            LOG.error(message, e);

            throw new IOException(message, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   feature  DOCUMENT ME!
     * @param   qname    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static Object getFeaturePropertyValue(final org.deegree.model.feature.Feature feature,
            final QualifiedName qname) {
        if ((feature == null) || (qname == null)) {
            throw new IllegalArgumentException("feature or qname must not be null"); // NOI18N
        }

        final FeatureProperty[] props = feature.getProperties(qname);

        if ((props == null) || (props.length < 1)) {
            return null;
        } else if (props.length > 1) {
            throw new IllegalStateException("found more than one property for qname:" + qname); // NOI18N
        } else {
            return props[0].getValue();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   feature  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeometryException  DOCUMENT ME!
     */
    public static Geometry extractGeometry(final org.deegree.model.feature.Feature feature) throws GeometryException {
        return extractGeometry(feature, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   feature       DOCUMENT ME!
     * @param   geomAttrName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeometryException         DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static Geometry extractGeometry(final org.deegree.model.feature.Feature feature,
            final QualifiedName geomAttrName) throws GeometryException {
        if (feature == null) {
            throw new IllegalArgumentException("feature must not be null"); // NOI18N
        }

        final Geometry geom;
        if (geomAttrName == null) {
            geom = JTSAdapter.export(feature.getDefaultGeometryPropertyValue());
        } else {
            final Object value = getFeaturePropertyValue(feature, geomAttrName);
            if (value instanceof org.deegree.model.spatialschema.Geometry) {
                geom = JTSAdapter.export((org.deegree.model.spatialschema.Geometry)value);
            } else {
                throw new GeometryException(
                    "feature does not contain geometry attribute value for given qname: [feature=" // NOI18N
                            + feature
                            + "|qname="                                                            // NOI18N
                            + geomAttrName
                            + "|value="                                                            // NOI18N
                            + value
                            + "]");                                                                // NOI18N
            }
        }

        final CoordinateSystem coordSys = feature.getDefaultGeometryPropertyValue().getCoordinateSystem();
        if (coordSys == null) {
            throw new IllegalStateException("feature without a coordinate system: " + feature); // NOI18N
        }

        final int srid = CrsTransformer.extractSridFromCrs(coordSys.getIdentifier());
        final GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);

        return gf.createGeometry(geom);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   queryInfo  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Collection<Feature> fetchFeatures(final WFSQueryInfo queryInfo) {
        final String cancelOption = "Cancel";
        final StatusPanel statusPanel = new StatusPanel("Please wait");
        final JOptionPane pane = new JOptionPane(
                statusPanel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.CANCEL_OPTION,
                null,
                new Object[] { cancelOption });
        statusPanel.setBusy(true);
        statusPanel.setStatusMessage(queryInfo.getStatusMessage());

        final JDialog dialog = pane.createDialog(ComponentRegistry.getRegistry().getMainWindow(), "Please wait");

        final Future<Collection<Feature>> task = SudplanConcurrency.getSudplanGeneralPurposePool()
                    .submit(new Callable<Collection<Feature>>() {

                            @Override
                            public Collection<Feature> call() {
                                try {
                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("feature retriever was interrupted"); // NOI18N
                                        }

                                        return null;
                                    }

                                    final FeatureType featureType = WFSUtils.getFeatureType(
                                            queryInfo.getCapabilitiesUrl(),
                                            queryInfo.getFeatureQName());

                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("feature retriever was interrupted"); // NOI18N
                                        }

                                        return null;
                                    }

                                    final String query = queryInfo.createFeatureQuery(featureType);

                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("feature retriever was interrupted"); // NOI18N
                                        }

                                        return null;
                                    }

                                    final InputStream resp = WebAccessManager.getInstance()
                                        .doRequest(
                                            featureType.getWFSCapabilities().getURL(),
                                            query,
                                            ACCESS_METHODS.POST_REQUEST);

                                    if (Thread.currentThread().isInterrupted()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("feature retriever was interrupted"); // NOI18N
                                        }

                                        return null;
                                    }

                                    final GMLFeatureCollectionDocument gmlDoc = new GMLFeatureCollectionDocument();
                                    gmlDoc.load(
                                        resp,
                                        ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI.toString());

                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug(
                                            "found this many features: "
                                            + gmlDoc.getFeatureCount()); // NOI18N
                                    }

                                    final FeatureCollection fc = gmlDoc.parse();

                                    if (fc.size() < 1) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("no features found"); // NOI18N
                                        }

                                        EventQueue.invokeAndWait(new Runnable() {

                                                @Override
                                                public void run() {
                                                    final JOptionPane noResultsPane = new JOptionPane(
                                                            "Query did not find any valid results",
                                                            JOptionPane.INFORMATION_MESSAGE,
                                                            JOptionPane.OK_OPTION,
                                                            null,
                                                            new Object[] { "OK" });
                                                    final JDialog noResultsDialog = noResultsPane.createDialog(
                                                            dialog,
                                                            "No results");

                                                    noResultsDialog.setVisible(true);
                                                }
                                            });

                                        // nothing to do anymore, bail out
                                        return null;
                                    }

                                    return queryInfo.createFeatures(fc);
                                } catch (final Exception ex) {
                                    LOG.error("cannot fetch features", ex); // NOI18N
                                    try {
                                        final ErrorInfo errorInfo = new ErrorInfo(
                                                "WFS error",
                                                "Error while performing WFS request",
                                                "The WFS request could not be performed because of an unexpected error",
                                                "ERROR",
                                                ex,
                                                Level.SEVERE,
                                                null);

                                        EventQueue.invokeAndWait(new Runnable() {

                                                @Override
                                                public void run() {
                                                    JXErrorPane.showDialog(dialog, errorInfo);
                                                }
                                            });
                                    } catch (final Exception ex1) {
                                        LOG.error("cannot display error dialog", ex1); // NOI18N
                                    }

                                    return null;
                                } finally {
                                    EventQueue.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                dialog.setVisible(false);
                                            }
                                        });
                                }
                            }
                        });

        dialog.setVisible(true);

        if (cancelOption.equals(pane.getValue())) {
            if (!task.isDone()) {
                if (!task.cancel(true)) {
                    LOG.warn("cannot cancel feature retriever task"); // NOI18N
                }

                return null;
            }
        }

        try {
            return task.get();
        } catch (final InterruptedException ex) {
            // cannot occur
            LOG.fatal(
                "illegal state: the thread could not have been interrupted as it should have already finished",
                ex);                                                                                        // NOI18N
            assert false : "the thread could not have been interrupted as it should have already finished"; // NOI18N

            return null;
        } catch (final ExecutionException ex) {
            LOG.fatal("illegal state: the thread generated an exception that was not already handled", ex); // NOI18N
            assert false : "the thread generated an exception that was not already handled";                // NOI18N

            return null;
        }
    }
}
