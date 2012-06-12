/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;

import java.awt.Component;

import java.io.IOException;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AssignTimeseriesWizardPanelSelectTS extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SELECTED_TS = "__prop_selected_ts__"; // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(AssignTimeseriesWizardPanelSelectTS.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean selectedTimeseries;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSelectedTimeseries() {
        return selectedTimeseries;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeseries  DOCUMENT ME!
     */
    public void setSelectedTimeseries(final CidsBean timeseries) {
        this.selectedTimeseries = timeseries;

        changeSupport.fireChange();
    }

    @Override
    protected Component createComponent() {
        return new AssignTimeseriesVisualPanelSelectTS(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        final CalibrationInputManager cim = new CalibrationInputManager();
        final CidsBean selectedCalibration = (CidsBean)wizard.getProperty(
                AssignTimeseriesWizardAction.PROP_SELECTED_CALIBRATION_INPUT);
        cim.setCidsBean(selectedCalibration);

        final CalibrationInput ci;
        try {
            ci = cim.getUR();
        } catch (final IOException ex) {
            final String message = "cannot fetch calibration input"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        final int basinId = (Integer)wizard.getProperty(AssignTimeseriesWizardAction.PROP_BASIN_ID);
        final Integer tsId = ci.getTimeseries(basinId);
        if (tsId != null) {
            final CidsBean tsBean = SMSUtils.fetchCidsBean(tsId, "timeseries"); // NOI18N
            wizard.putProperty(AssignTimeseriesWizardPanelSelectTS.PROP_SELECTED_TS, tsBean);
        }
        setSelectedTimeseries((CidsBean)wizard.getProperty(PROP_SELECTED_TS));

        ((AssignTimeseriesVisualPanelSelectTS)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_SELECTED_TS, selectedTimeseries);
    }

    @Override
    public boolean isValid() {
        if (selectedTimeseries == null) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please select a timeseries");

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            return true;
        }
    }
}
