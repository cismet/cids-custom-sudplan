/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import net.opengis.sps.v_1_0_0.InputDescriptor;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.DataHandlerCache;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallDownscalingWizardPanelTargetDate implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RainfallDownscalingWizardPanelTargetDate.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient RainfallDownscalingVisualPanelTargetDate component;

    private transient Integer targetYear;
    private transient Integer beginYear;
    private transient Integer endYear;
    private transient Exception spsError;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public RainfallDownscalingWizardPanelTargetDate() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new RainfallDownscalingVisualPanelTargetDate(this);
        }

        return component;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getTargetYear() {
        return targetYear;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        targetYear = (Integer)wizard.getProperty(RainfallDownscalingWizardAction.PROP_TARGET_YEAR);

        try {
            final DataHandler dh = DataHandlerCache.getInstance()
                        .getSPSDataHandler(
                            RainfallDownscalingModelManager.RF_SPS_LOOKUP,
                            RainfallDownscalingModelManager.RF_SPS_URL);
            final Properties filter = new Properties();
            filter.put(TimeSeries.PROCEDURE, RainfallDownscalingModelManager.RF_TS_DS_PROCEDURE);
            final Datapoint dp = dh.createDatapoint(filter, null, DataHandler.Access.READ);
            final InputDescriptor id = (InputDescriptor)dp.getProperties().get("jaxb_desc:center_time"); // NOI18N
            final List<String> beginEnd = id.getDefinition()
                        .getCommonData()
                        .getTime()
                        .getConstraint()
                        .getAllowedTimes()
                        .getIntervalOrValueList()
                        .get(0)
                        .getValue();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");                  // NOI18N
            final Date begin = sdf.parse(beginEnd.get(0));
            final Date end = sdf.parse(beginEnd.get(1));
            final Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(begin);
            beginYear = cal.get(Calendar.YEAR);
            cal.setTime(end);
            endYear = cal.get(Calendar.YEAR);
        } catch (final Exception ex) {
            LOG.error("error during begin and end year retrieval from SPS", ex);                         // NOI18N
            beginYear = 0;
            endYear = 1;
            spsError = ex;
        }

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        // we are sure that isValid() has checked the year
        wizard.putProperty(RainfallDownscalingWizardAction.PROP_TARGET_YEAR, Integer.parseInt(component.getYear()));
    }

    @Override
    public boolean isValid() {
        if (spsError != null) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                "An error occurred during SPS communication");

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        }

        final String choosenYear = component.getYear();
        boolean valid;

        try {
            final Integer year = Integer.parseInt(choosenYear);
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

            if ((year < beginYear) || (year > endYear)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    NbBundle.getMessage(
                        RainfallDownscalingWizardPanelTargetDate.class,
                        "RainfallDownscalingWizardPanelTargetDate.isValid().yearNotInRange")); // NOI18N
                valid = false;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
                valid = true;
            }
        } catch (final NumberFormatException numberFormatException) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    RainfallDownscalingWizardPanelTargetDate.class,
                    "RainfallDownscalingWizardPanelTargetDate.isValid().onlyNumbers"));        // NOI18N
            valid = false;
        }

        return valid;
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    /**
     * DOCUMENT ME!
     */
    protected void fireChangeEvent() {
        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getBeginYear() {
        return beginYear;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beginYear  DOCUMENT ME!
     */
    public void setBeginYear(final Integer beginYear) {
        this.beginYear = beginYear;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getEndYear() {
        return endYear;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  endYear  DOCUMENT ME!
     */
    public void setEndYear(final Integer endYear) {
        this.endYear = endYear;
    }
}
