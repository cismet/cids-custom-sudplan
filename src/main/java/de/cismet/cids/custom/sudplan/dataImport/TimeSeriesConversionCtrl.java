/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataImport;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesConversionCtrl extends AbstractWizardPanelCtrl implements Cancellable {

    //~ Instance fields --------------------------------------------------------

    private final transient StatusPanel comp;
    private transient volatile TimeSeries ts;
    private transient Future<?> runningTask;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesImportFileChoosePanelCtrl object.
     */
    public TimeSeriesConversionCtrl() {
        this.comp = new StatusPanel(NbBundle.getMessage(
                    TimeSeriesConversionCtrl.class,
                    "TimeSeriesConversionPanelCtrl.comp.name")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        return this.comp;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        this.comp.setBusy(true);
        this.comp.setStatusMessage(java.util.ResourceBundle.getBundle(
                "de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                "TimeSeriesConversionPanelCtrl.comp.setConversionStatus().start"));

        this.runningTask = CismetThreadPool.submit(new Runnable() {

                    @Override
                    public void run() {
                        final File importFile = (File)wizard.getProperty(TimeSeriesImportWizardAction.PROP_INPUT_FILE);
                        final TimeseriesConverter converter = (TimeseriesConverter)wizard.getProperty(
                                TimeSeriesImportWizardAction.PROP_CONVERTER);

                        try {
                            final FileInputStream fin = new FileInputStream(importFile);
                            final BufferedInputStream bin = new BufferedInputStream(fin);

                            ts = converter.convertForward(bin);
                            comp.setStatusMessage(
                                java.util.ResourceBundle.getBundle(
                                    "de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                                    "TimeSeriesConversionPanelCtrl.comp.setConversionStatus().finish"));
                        } catch (final Exception e) {
                            // TODO distinguish different exception types for better error messages

                            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, e);
                            comp.setStatusMessage(
                                java.util.ResourceBundle.getBundle(
                                    "de/cismet/cids/custom/sudplan/dataImport/Bundle").getString(
                                    "TimeSeriesConversionPanelCtrl.comp.setConversionStatus().error"));
                            wizard.setValid(false);
                        }

                        TimeSeriesConversionCtrl.this.fireChangeEvent();
                        comp.setBusy(false);

                        synchronized (TimeSeriesConversionCtrl.this) {
                            TimeSeriesConversionCtrl.this.runningTask = null;
                        }
                    }
                });
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(TimeSeriesImportWizardAction.PROP_TIMESERIES, this.ts);
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
    }

    @Override
    public boolean isValid() {
        return this.ts != null;
    }

    @Override
    public synchronized boolean cancel() {
        if (this.runningTask != null) {
            this.runningTask.cancel(true);
            this.runningTask = null;
        }

        return true;
    }
}
