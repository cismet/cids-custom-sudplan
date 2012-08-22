/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.ui.ComponentRegistry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.WeakListeners;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import java.io.InputStream;

import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.WorldToScreenTransform;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;

import de.cismet.security.AccessHandler.ACCESS_METHODS;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UnionAssignTimeseriesWizardAction extends AssignTimeseriesWizardAction {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(UnionAssignTimeseriesWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private final transient PInputEventListener popupL;

    private transient Point point;

    private transient Feature feature;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UnionAssignTimeseriesWizardAction object.
     *
     * @param  areaFeature  DOCUMENT ME!
     */
    public UnionAssignTimeseriesWizardAction(final org.deegree.model.feature.Feature areaFeature) {
        super(
            areaFeature,
            new QualifiedName(
                ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                "subid", // NOI18N
                ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI));

        this.popupL = new PopupListener();

        final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
        mc.addInputEventListener(WeakListeners.create(PInputEventListener.class, popupL, mc));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        // the action will always be in EDT and after the popup trigger, thus the point will always be the correct one
        final ShowCatchmentAreaForPointAction scafpa = new ShowCatchmentAreaForPointAction();
        scafpa.setPoint(point);

        final StatusPanel statusPanel = new StatusPanel("Please wait");
        final JOptionPane pane = new JOptionPane(
                statusPanel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.CANCEL_OPTION,
                null,
                new Object[] { "Cancel" });
        statusPanel.setBusy(true);
        statusPanel.setStatusMessage(scafpa.getStatusMessage());

        final JDialog dialog = pane.createDialog(ComponentRegistry.getRegistry().getMainWindow(), "Please wait");

        final Future task = SudplanConcurrency.getSudplanGeneralPurposePool().submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (Thread.currentThread().isInterrupted()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("feature retriever was interrupted"); // NOI18N
                                }

                                return;
                            }

                            final FeatureType featureType = WFSUtils.getFeatureType(
                                    scafpa.getCapabilitiesUrl(),
                                    scafpa.getFeatureQName());

                            if (Thread.currentThread().isInterrupted()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("feature retriever was interrupted"); // NOI18N
                                }

                                return;
                            }

                            final String query = scafpa.createFeatureQuery(featureType);

                            if (Thread.currentThread().isInterrupted()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("feature retriever was interrupted"); // NOI18N
                                }

                                return;
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

                                return;
                            }

                            final GMLFeatureCollectionDocument gmlDoc = new GMLFeatureCollectionDocument();
                            gmlDoc.load(resp, ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI.toString());

                            if (LOG.isDebugEnabled()) {
                                LOG.debug("found this many features: " + gmlDoc.getFeatureCount()); // NOI18N
                            }

                            final FeatureCollection fc = gmlDoc.parse();

                            if (fc.size() == 1) {
                                feature = fc.getFeature(0);
                            } else {
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
                                            StaticSwingTools.showDialog(noResultsDialog);
                                        }
                                    });

                                feature = null;
                            }
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

        StaticSwingTools.showDialog(dialog);

        if (pane.getValue() != null) {
            if (!task.isDone()) {
                // the cancel button has been pressed
                if (!task.cancel(true)) {
                    LOG.warn("cannot cancel feature retriever task"); // NOI18N
                }

                return;
            }
        }

        final Object value = WFSUtils.getFeaturePropertyValue(
                feature,
                new QualifiedName(
                    ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_PREFIX,
                    "subid", // NOI18N
                    ShowCatchmentAreaForPointAction.HYDRO_WFS_QNAME_URI));

        final int subId;
        // the id is an integer, but the property value is a string
        if (value instanceof String) {
            subId = Integer.valueOf((String)value);
        } else {
            throw new IllegalStateException("property value not instanceof string: " + value); // NOI18N
        }

        actionPerformed(subId);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PopupListener implements PInputEventListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void processEvent(final PInputEvent aEvent, final int type) {
            // we need only popuptrigger events
            if (aEvent.isPopupTrigger()) {
                final Point2D pos = aEvent.getPosition();
                final WorldToScreenTransform wtst = CismapBroker.getInstance().getMappingComponent().getWtst();
                final Coordinate coord = new Coordinate(wtst.getSourceX(pos.getX()), wtst.getSourceY(pos.getY()));
                final GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING),
                        CrsTransformer.getCurrentSrid());

                point = gf.createPoint(coord);
            }
        }
    }
}
