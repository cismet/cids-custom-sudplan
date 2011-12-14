/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesImport;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;

import java.awt.Component;

import java.net.URL;

import java.util.concurrent.Future;

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
public class TimeSeriesPersistenceCtrl extends AbstractWizardPanelCtrl {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesPersistenceCtrl.class);

    // TODO: Remove hardcoded target location and user credentials
    private static final String DAV_HOST = "http://sudplan.cismet.de/tsDav/"; // NOI18N

    private static final String URL_PREFIX = "dav:";                                               // NOI18N
    private static final String URL_SUFFX = "?ts:observed_property=urn:ogc:def:property:OGC:prec"; // NOI18N

    private static final Credentials CREDS = new UsernamePasswordCredentials("tsDav", "RHfio2l4wrsklfghj"); // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient TimeSeriesStatusPanel comp;
    private transient volatile boolean hasFinished;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesImportFileChoosePanelCtrl object.
     */
    public TimeSeriesPersistenceCtrl() {
        this.comp = new TimeSeriesStatusPanel();
        this.comp.setName(java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/timeseriesImport/Bundle")
                    .getString("TimeSeriesPersistenceCtrl.comp.name"));
        this.hasFinished = false;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        return this.comp;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        comp.setStatusMessage(java.util.ResourceBundle.getBundle(
                "de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.begin"));
        comp.setBusy(true);

        CismetThreadPool.execute(new Runnable() {

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

                        final String tsName = (String)tsBean.getProperty("name"); // NOI18N

                        // we don't know the resolution of the imported TimeSeries
                        final String unknownResFileName = tsName + "_unknown"; // NOI18N
                        // the aggregated Timeseries has been aggregated by 1 day (=86400s)
                        final String aggTsFileName = tsName + "_86400s"; // NOI18N

                        final TimeseriesTransmitter transmitter = TimeseriesTransmitter.getInstance();

                        // send aggregated TimeSeries to DAV
                        URL url = new URL(DAV_HOST + aggTsFileName);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("start transmitting file " + aggTsFileName + " to " + url); // NOI18N
                        }
                        final Future<Boolean> successTrans1 = transmitter.put(url, aggTS, CREDS);

                        // send original TimeSeries to DAV
                        url = new URL(DAV_HOST + unknownResFileName);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("start transmitting file " + unknownResFileName + " to " + url); // NOI18N
                        }
                        final Future<Boolean> successTrans2 = transmitter.put(url, ts, CREDS);

                        // if both transmission have successful, persist the corresponding bean
                        if (successTrans1.get() && successTrans2.get()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("transmissions to " + url.getHost() + " have been finished successfully"); // NOI18N
                            }

                            // the file with the unknown resolution is associated with the bean as
                            // it represents the original data
                            tsBean.setProperty("uri", URL_PREFIX + url.toURI().toString() + URL_SUFFX); // NOI18N

                            final Object converter = wizard.getProperty(TimeSeriesImportWizardAction.PROP_CONVERTER);
                            tsBean.setProperty("converter", converter.getClass().getName()); // NOI18N

                            if (LOG.isDebugEnabled()) {
                                LOG.debug("start persisting bean");               // NOI18N
                            }
                            tsBean.persist();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("bean has bean finished successfully"); // NOI18N
                            }

                            comp.setStatusMessage(
                                java.util.ResourceBundle.getBundle(
                                    "de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                                    "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.success"));
                        } else {
                            LOG.error(
                                "an error has occurred while transmitting TimeSeries files to host "
                                        + url.getHost()); // NOI18N
                            comp.setStatusMessage(
                                java.util.ResourceBundle.getBundle(
                                    "de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                                    "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.transmissionError"));
                        }
                    } catch (final Exception e) {
                        LOG.error("an error has occurred while persisting imported TimeSeries", e);
                        comp.setStatusMessage(
                            java.util.ResourceBundle.getBundle(
                                "de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                                "TimeSeriesPersistenceCtrl.read(WizardDescriptor).comp.statusMessage.error"));
                    }

                    hasFinished = true;

                    fireChangeEvent();
                    comp.setBusy(false);
                }
            });
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
    }

    @Override
    public boolean isValid() {
        return this.hasFinished;
    }
}
