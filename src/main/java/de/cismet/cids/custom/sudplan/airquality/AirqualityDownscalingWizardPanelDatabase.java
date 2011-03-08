/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

import java.awt.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelDatabase implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient AirqualityDownscalingVisualPanelDatabase component;
    private transient Map<String, Set<Integer>> chosenDbs;
    private transient Integer startYear;
    private transient Integer endYear;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingWizardPanelScenarios object.
     */
    public AirqualityDownscalingWizardPanelDatabase() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelDatabase(this);
        }

        return component;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String[] getAvailableDatabases() {
        return new String[] {
                "Edb1980base",
                "Edb2005ref",
                "Edb2030A1B",
                "Edb19xxZH",
                "Edb20xxBU"
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Map<String, Set<Integer>> getDatabases() {
        return chosenDbs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getStartYear() {
        return startYear;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getEndYear() {
        return endYear;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        chosenDbs = (Map<String, Set<Integer>>)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_DATABASES);

        final Date startDate = (Date)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_START_DATE);
        final Date endDate = (Date)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_END_DATE);
        final Calendar cal = GregorianCalendar.getInstance();

        cal.setTime(startDate);
        startYear = cal.get(Calendar.YEAR);
        cal.setTime(endDate);
        endYear = cal.get(Calendar.YEAR);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_DATABASES, component.getChosenDatabases());
    }

    @Override
    public boolean isValid() {
        if (component.yearEnable()) {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

            if (component.buttonEnable()) {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
            } else {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    "This database year combination is already chosen");
            }
        } else {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "The year must be a valid integer");
        }

        return true;
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
}
