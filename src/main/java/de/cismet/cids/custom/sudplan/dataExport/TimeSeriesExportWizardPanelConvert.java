/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataExport;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.EventQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;
import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import de.cismet.cids.custom.sudplan.dataImport.TimeSeriesImportWizardAction;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimeSeriesExportWizardPanelConvert extends AbstractWizardPanel implements Cancellable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeSeriesExportWizardPanelConvert.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Object lock;

    private transient Future exportTask;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesExportWizardPanelConvert object.
     */
    public TimeSeriesExportWizardPanelConvert() {
        this.lock = new Object();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Component createComponent() {
        return new StatusPanel(NbBundle.getMessage(
                    TimeSeriesExportWizardPanelConvert.class,
                    "TimeSeriesExportWizardPanelConvert.createComponent().statusPanel.name")); // NOI18N
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        synchronized (lock) {
            final File exportFile = (File)wizard.getProperty(TimeSeriesExportWizardPanelFile.PROP_EXPORT_FILE);
            final TimeseriesConverter timeseriesConverter = (TimeseriesConverter)wizard.getProperty(
                    TimeSeriesImportWizardAction.PROP_CONVERTER);
            final TimeSeries timeSeries = (TimeSeries)wizard.getProperty(TimeSeriesExportWizardAction.PROP_TIMESERIES);
            final TimeseriesRetrieverConfig trc = (TimeseriesRetrieverConfig)wizard.getProperty(
                    TimeSeriesExportWizardAction.PROP_TS_RETRIEVER_CFG);

            assert exportFile != null : "export file must not be null";                                 // NOI18N
            assert timeseriesConverter != null : "converter must not be null";                          // NOI18N
            assert (timeSeries != null) || (trc != null) : "timeseries or tsr config must not be null"; // NOI18N

            exportTask = SudplanConcurrency.getSudplanGeneralPurposePool().submit(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                final TimeSeries toConvert;
                                if (timeSeries == null) {
                                    setStatusEDT(
                                        true,
                                        NbBundle.getMessage(
                                            TimeSeriesExportWizardPanelConvert.class,
                                            "TimeSeriesExportWizardPanelConvert.read(WizardDescriptor).exportTask.status.receivingData")); // NOI18N
                                    toConvert = TimeseriesRetriever.getInstance().retrieve(trc).get();
                                } else {
                                    toConvert = timeSeries;
                                }

                                setStatusEDT(
                                    true,
                                    NbBundle.getMessage(
                                        TimeSeriesExportWizardPanelConvert.class,
                                        "TimeSeriesExportWizardPanelConvert.read(WizardDescriptor).exportTask.status.exporting")); // NOI18N

                                final InputStream is = timeseriesConverter.convertBackward(toConvert);
                                final FileOutputStream fos = new FileOutputStream(exportFile);

                                final byte[] buff = new byte[8192];
                                int read;
                                while ((read = is.read(buff)) > 0) {
                                    fos.write(buff, 0, read);
                                }

                                is.close();
                                fos.close();

                                setStatusEDT(
                                    false,
                                    NbBundle.getMessage(
                                        TimeSeriesExportWizardPanelConvert.class,
                                        "TimeSeriesExportWizardPanelConvert.read(WizardDescriptor).exportTask.status.exportSuccessful")); // NOI18N

                                synchronized (lock) {
                                    TimeSeriesExportWizardPanelConvert.this.exportTask = null;
                                }
                            } catch (final Throwable ex) {
                                LOG.error("cannot export timeseries", ex);                                                            // NOI18N
                                setStatusEDT(
                                    false,
                                    NbBundle.getMessage(
                                        TimeSeriesExportWizardPanelConvert.class,
                                        "TimeSeriesExportWizardPanelConvert.read(WizardDescriptor).exportTask.status.exportingError", // NOI18N
                                        ex.getMessage()));

                                if (ex instanceof Error) {
                                    throw (Error)ex;
                                }
                            } finally {
                                changeSupport.fireChange();
                            }
                        }
                    });
        }
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        // noop
    }

    @Override
    public boolean cancel() {
        synchronized (lock) {
            if (exportTask != null) {
                if (!exportTask.cancel(true)) {
                    if (exportTask.isDone()) {
                        // noop
                    } else {
                        LOG.warn("export task could not be cancelled"); // NOI18N

                        return false;
                    }
                }
            }

            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  busy     DOCUMENT ME!
     * @param  message  DOCUMENT ME!
     */
    private void setStatusEDT(final boolean busy, final String message) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((StatusPanel)getComponent()).setBusy(busy);
                    ((StatusPanel)getComponent()).setStatusMessage(message);
                }
            });
    }
}
