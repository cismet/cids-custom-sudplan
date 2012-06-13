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

import se.smhi.sudplan.client.Scenario;

import java.awt.Component;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DoSimulationWizardPanelSelectTimerange extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SELECTED_START_DATE = "__prop_selected_start_date"; // NOI18N
    public static final String PROP_SELECTED_END_DATE = "__prop_selected_end_date";     // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(DoSimulationWizardPanelSelectTimerange.class);

    //~ Instance fields --------------------------------------------------------

    private transient Date selectedStartDate;
    private transient Date selectedEndDate;

    private transient Date minStartDate;
    private transient Date maxEndDate;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getSelectedEndDate() {
        return selectedEndDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedEndDate  DOCUMENT ME!
     */
    public void setSelectedEndDate(final Date selectedEndDate) {
        this.selectedEndDate = selectedEndDate;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getSelectedStartDate() {
        return selectedStartDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedStartDate  DOCUMENT ME!
     */
    public void setSelectedStartDate(final Date selectedStartDate) {
        this.selectedStartDate = selectedStartDate;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getMaxEndDate() {
        return maxEndDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getMinStartDate() {
        return minStartDate;
    }

    @Override
    protected Component createComponent() {
        return new DoSimulationVisualPanelSelectTimerange(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        final DateFormat df = HydrologyCache.getInstance().getHydroDateFormat();

        final Scenario scenario = (Scenario)wizard.getProperty(
                DoSimulationWizardPanelSelectScenario.PROP_SELECTED_SCENARIO);
        try {
            maxEndDate = df.parse(scenario.getEdate());
            minStartDate = df.parse(scenario.getCdate());
        } catch (final ParseException ex) {
            final String message = "cannot fetch start or end date from scenario"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        setSelectedStartDate((Date)wizard.getProperty(PROP_SELECTED_START_DATE));
        setSelectedEndDate((Date)wizard.getProperty(PROP_SELECTED_END_DATE));

        ((DoSimulationVisualPanelSelectTimerange)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_SELECTED_START_DATE, selectedStartDate);
        wizard.putProperty(PROP_SELECTED_END_DATE, selectedEndDate);
    }

    @Override
    public boolean isValid() {
        if (selectedStartDate == null) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please select a start date");

            return false;
        } else if (selectedEndDate == null) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please select an end date");

            return false;
        } else {
            final DateFormat df = DateFormat.getDateInstance();
            if (selectedStartDate.before(minStartDate)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    "Please select a start date not before "
                            + df.format(minStartDate));

                return false;
            } else if (selectedEndDate.after(maxEndDate)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    "Please select an end date not after "
                            + df.format(maxEndDate));

                return false;
            } else if (selectedStartDate.after(selectedEndDate)) {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "The start date must be before the end date");

                return false;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

                return true;
            }
        }
    }
}
