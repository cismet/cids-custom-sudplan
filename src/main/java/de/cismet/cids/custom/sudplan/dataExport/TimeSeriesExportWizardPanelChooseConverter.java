/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataExport;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesExportWizardPanelChooseConverter extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    /** Creates a new TimeSeriesImportFileChoosePanelCtrl object. */

    public static final String PROP_TS_CONVERTER = "__prop_ts_converter__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient TimeseriesConverter timeseriesConverter;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TimeseriesConverter getTimeseriesConverter() {
        return timeseriesConverter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeseriesConverter  DOCUMENT ME!
     */
    public void setTimeseriesConverter(final TimeseriesConverter timeseriesConverter) {
        this.timeseriesConverter = timeseriesConverter;

        changeSupport.fireChange();
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        setTimeseriesConverter((TimeseriesConverter)wizard.getProperty(PROP_TS_CONVERTER));

        ((TimeSeriesExportVisualPanelChooseConverter)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_TS_CONVERTER, timeseriesConverter);
    }

    @Override
    public boolean isValid() {
        if (timeseriesConverter == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    TimeSeriesExportWizardPanelChooseConverter.class,
                    "TimeSeriesExportWizardPanelChooseConverter.isValid().chooseConverter")); // NOI18N

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            return true;
        }
    }

    @Override
    protected Component createComponent() {
        return new TimeSeriesExportVisualPanelChooseConverter(this);
    }
}
