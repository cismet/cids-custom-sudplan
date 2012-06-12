/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.wfs.capabilities.FeatureType;

import de.cismet.security.AccessHandler.ACCESS_METHODS;

import de.cismet.security.WebAccessManager;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractWFSFeatureRetrievalAction extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(AbstractWFSFeatureRetrievalAction.class);

    //~ Instance fields --------------------------------------------------------

    /** lock object for the featureType access sync. */
    private final transient Object lock;

    /**
     * feature factory will be initialised within the sync block that is dependent on the basin feature type, thus it is
     * not necessary to make it volatile.
     */
    private transient volatile FeatureType featureType;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractWFSFeatureRetrievalAction object.
     */
    public AbstractWFSFeatureRetrievalAction() {
        this(null);
    }

    /**
     * Creates a new AbstractWFSFeatureRetrievalAction object.
     *
     * @param  name  DOCUMENT ME!
     */
    public AbstractWFSFeatureRetrievalAction(final String name) {
        super(name);

        lock = new Object();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getCapabilitiesUrl();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract QualifiedName getFeatureQName();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getStatusMessage();
    /**
     * DOCUMENT ME!
     *
     * @param   featureType  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String createFeatureQuery(final FeatureType featureType);
    /**
     * DOCUMENT ME!
     *
     * @param   featureCollection  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WFSRetrievalException  DOCUMENT ME!
     */
    protected abstract Collection<? extends Feature> createFeatures(final FeatureCollection featureCollection)
            throws WFSRetrievalException;

    /**
     * DOCUMENT ME!
     *
     * @param  actionEvent  DOCUMENT ME!
     */
    @Override
    public final void actionPerformed(final ActionEvent actionEvent) {
        final StatusPanel statusPanel = new StatusPanel("Please wait");
        final JOptionPane pane = new JOptionPane(
                statusPanel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.CANCEL_OPTION,
                null,
                new Object[] { "Cancel" });
        statusPanel.setBusy(true);
        statusPanel.setStatusMessage(getStatusMessage());

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

                            if (featureType == null) {
                                synchronized (lock) {
                                    if (featureType == null) {
                                        featureType = WFSUtils.getFeatureType(getCapabilitiesUrl(), getFeatureQName());
                                    }
                                }
                            }

                            if (Thread.currentThread().isInterrupted()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("feature retriever was interrupted"); // NOI18N
                                }

                                return;
                            }

                            final String query = createFeatureQuery(featureType);

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
                                LOG.debug(
                                    "found this many features: "
                                            + gmlDoc.getFeatureCount()); // NOI18N
                            }

                            final FeatureCollection fc = gmlDoc.parse();

                            if (fc.size() < 1) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("no features found");
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
                                return;
                            }

                            final Collection<? extends Feature> features = createFeatures(fc);

                            if (Thread.currentThread().isInterrupted()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("feature retriever was interrupted"); // NOI18N
                                }

                                return;
                            }

                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
                                        mc.getFeatureCollection().addFeatures(features);
                                        mc.zoomToFeatureCollection();
                                    }
                                });
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
                            } catch (Exception ex1) {
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

        dialog.setVisible(true);

        if (pane.getValue() != null) {
            // the cancel button has been pressed
            if (!task.isDone()) {
                if (!task.cancel(true)) {
                    LOG.warn("cannot cancel feature retriever task"); // NOI18N
                }
            }
        }
    }
}
