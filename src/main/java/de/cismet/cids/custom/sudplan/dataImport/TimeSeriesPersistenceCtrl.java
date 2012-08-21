/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataImport;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.tree.MetaCatalogueTree;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.TimeSeriesRemoteHelper;
import de.cismet.cids.custom.sudplan.TimeSeriesTrashBin;
import de.cismet.cids.custom.sudplan.TimeseriesTransmitter;
import de.cismet.cids.custom.sudplan.converter.TimeSeriesAggregator;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesPersistenceCtrl extends AbstractWizardPanelCtrl implements Cancellable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesPersistenceCtrl.class);

    private static final String URL_PREFIX = "dav:"; // NOI18N private static final String URL_SUFFX =
                                                     // "?ts:observed_property=urn:ogc:def:property:OGC:prec"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient StatusPanel comp;
    private transient volatile boolean hasFinished;
    private transient Future<?> runningTask;
    private transient List<URL> transmittedFiles;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesImportFileChoosePanelCtrl object.
     */
    public TimeSeriesPersistenceCtrl() {
        this.comp = new StatusPanel(NbBundle.getMessage(
                    TimeSeriesPersistenceCtrl.class,
                    "TimeSeriesPersistenceCtrl.comp.name")); // NOI18N
        this.hasFinished = false;
        this.transmittedFiles = new ArrayList<URL>(2);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        return this.comp;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        comp.setStatusMessage(NbBundle.getMessage(
                TimeSeriesPersistenceCtrl.class,
                "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.begin")); // NOI18N
        comp.setBusy(true);

        this.runningTask = CismetThreadPool.submit(new Runnable() {

                    @Override
                    public void run() {
                        final CidsBean tsBean = (CidsBean)wizard.getProperty(TimeSeriesImportWizardAction.PROP_BEAN);

                        try {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("start persisting processing..."); // NOI18N
                            }

                            final TimeSeries ts = (TimeSeries)wizard.getProperty(
                                    TimeSeriesImportWizardAction.PROP_TIMESERIES);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("start aggregation of TimeSeries " + ts);                // NOI18N
                            }
                            final TimeSeries aggTS = TimeSeriesAggregator.aggregateByDay(ts);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("aggregation has been finished successfully: " + aggTS); // NOI18N

                                LOG.debug("start transmitting imported and aggregated TimeSeries instances"); // NOI18N
                            }

                            String tsName = (String)tsBean.getProperty("name"); // NOI18N
                            tsName = URLEncoder.encode(tsName, "UTF-8");        // NOI18N

                            // we don't know the resolution of the imported TimeSeries
                            final String unknownResFileName = tsName + "_unknown"; // NOI18N
                            // the aggregated TimeSeries has been aggregated by 1 day (=86400s)
                            final String aggTsFileName = tsName + "_86400s"; // NOI18N

                            final TimeseriesTransmitter transmitter = TimeseriesTransmitter.getInstance();

                            // send aggregated TimeSeries to DAV
                            URL url = new URL(TimeSeriesRemoteHelper.DAV_HOST + '/' + aggTsFileName);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("start transmitting file " + aggTsFileName + " to " + url); // NOI18N
                            }

                            TimeSeriesTrashBin.getInstance().checkAndClean(url.toURI().toString());

                            final Future<Boolean> successTrans1 = transmitter.put(
                                    url,
                                    aggTS,
                                    TimeSeriesRemoteHelper.DAV_CREDS);

                            synchronized (TimeSeriesPersistenceCtrl.this) {
                                transmittedFiles.add(url);
                            }

                            // send original TimeSeries to DAV
                            url = new URL(TimeSeriesRemoteHelper.DAV_HOST + '/' + unknownResFileName);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("start transmitting file " + unknownResFileName + " to " + url); // NOI18N
                            }

                            TimeSeriesTrashBin.getInstance().checkAndClean(url.toURI().toString());

                            final Future<Boolean> successTrans2 = transmitter.put(
                                    url,
                                    ts,
                                    TimeSeriesRemoteHelper.DAV_CREDS);

                            synchronized (TimeSeriesPersistenceCtrl.this) {
                                transmittedFiles.add(url);
                            }

                            // if both transmission have successful, persist the corresponding bean
                            if (successTrans1.get() && successTrans2.get()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("transmissions to " + url.getHost() + " have been finished successfully"); // NOI18N
                                }

                                // the file with the unknown resolution is associated with the bean as
                                // it represents the original data
                                tsBean.setProperty(
                                    "uri",                                      // NOI18N
                                    URL_PREFIX
                                            + TimeSeriesRemoteHelper.DAV_HOST
                                            + '?'
                                            + TimeSeries.OBSERVEDPROPERTY
                                            + "=urn:ogc:def:property:OGC:prec&" // NOI18N
                                            + TimeSeries.PROCEDURE
                                            + "=urn:ogc:object:"
                                            + tsName
                                            + ":prec:unknown&"                  // NOI18N
                                            + TimeSeries.OFFERING
                                            + '='
                                            + unknownResFileName);              // NOI18N

                                final Object converter = wizard.getProperty(
                                        TimeSeriesImportWizardAction.PROP_CONVERTER);
                                tsBean.setProperty("converter", converter.getClass().getName()); // NOI18N

                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("start persisting bean"); // NOI18N
                                }

                                tsBean.persist();

                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("bean has been successfully persisted"); // NOI18N
                                }

                                comp.setStatusMessage(
                                    java.util.ResourceBundle.getBundle(
                                        "de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                                        "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.success"));

                                hasFinished = true;
                                fireChangeEvent();
                            } else {
                                LOG.error(
                                    "an error has occurred while transmitting TimeSeries files to host "
                                            + url.getHost()); // NOI18N
                                comp.setStatusMessage(
                                    java.util.ResourceBundle.getBundle(
                                        "de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                                        "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.transmissionError"));

                                deleteTransmittedFiles();
                            }
                        } catch (final Exception e) {
                            LOG.error("an error has occurred while persisting imported TimeSeries", e);
                            comp.setStatusMessage(
                                java.util.ResourceBundle.getBundle(
                                    "de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                                    "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.error"));

                            deleteTransmittedFiles();
                        }

                        updateCatalogueTree();
                        comp.setBusy(false);

                        synchronized (TimeSeriesPersistenceCtrl.this) {
                            TimeSeriesPersistenceCtrl.this.runningTask = null;
                            TimeSeriesPersistenceCtrl.this.transmittedFiles.clear();
                        }
                    }
                });
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteTransmittedFiles() {
        synchronized (this) {
            final TimeSeriesTrashBin bin = TimeSeriesTrashBin.getInstance();
            for (final URL tmpUrl : transmittedFiles) {
                try {
                    bin.markForRemoteDeletion(tmpUrl.toURI().toString());
                } catch (final URISyntaxException ex) {
                    LOG.error("Could not delete transmitted file " + tmpUrl, ex);
                }
            }

            transmittedFiles.clear();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void updateCatalogueTree() {
        final MetaCatalogueTree catalogueTree = ComponentRegistry.getRegistry().getCatalogueTree();
        final DefaultTreeModel catalogueTreeModel = (DefaultTreeModel)catalogueTree.getModel();

        RootTreeNode rootTreeNode = null;
        try {
            rootTreeNode = new RootTreeNode(SessionManager.getProxy().getRoots());
        } catch (ConnectionException ex) {
            LOG.error("Updating catalogue tree after successful TimeSeries import failed.", ex);
            return;
        }

        catalogueTreeModel.setRoot(rootTreeNode);
        catalogueTreeModel.reload();

        TreePath selectionPath = catalogueTree.getSelectionPath();
        final Enumeration<TreePath> expandedPaths = catalogueTree.getExpandedDescendants(new TreePath(
                    catalogueTreeModel.getRoot()));

        if (selectionPath == null) {
            while (expandedPaths.hasMoreElements()) {
                final TreePath expandedPath = expandedPaths.nextElement();
                if ((selectionPath == null) || (selectionPath.getPathCount() < selectionPath.getPathCount())) {
                    selectionPath = expandedPath;
                }
            }
        }
        catalogueTree.exploreSubtree(selectionPath);
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
    }

    @Override
    public boolean isValid() {
        return this.hasFinished;
    }

    @Override
    public synchronized boolean cancel() {
        if (this.runningTask != null) {
            this.runningTask.cancel(true);
            this.runningTask = null;
            this.deleteTransmittedFiles();
        }

        return true;
    }
}
