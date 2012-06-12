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
public final class AssignTimeseriesWizardPanelSelectCalibration extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(AssignTimeseriesWizardPanelSelectCalibration.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean selectedCalibration;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSelectedCalibration() {
        return selectedCalibration;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedCalibration  DOCUMENT ME!
     */
    public void setSelectedCalibration(final CidsBean selectedCalibration) {
        this.selectedCalibration = selectedCalibration;

        changeSupport.fireChange();
    }

    @Override
    protected Component createComponent() {
        return new AssignTimeseriesVisualPanelSelectCalibration(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        setSelectedCalibration((CidsBean)wizard.getProperty(
                AssignTimeseriesWizardAction.PROP_SELECTED_CALIBRATION_INPUT));

        ((AssignTimeseriesVisualPanelSelectCalibration)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(AssignTimeseriesWizardAction.PROP_SELECTED_CALIBRATION_INPUT, selectedCalibration);
    }

    @Override
    public boolean isValid() {
        if (selectedCalibration == null) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please select a calibration");

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            return true;
        }
    }
}
