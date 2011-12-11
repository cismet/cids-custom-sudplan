/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

import java.awt.Component;

import java.util.List;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;
import de.cismet.cids.custom.sudplan.local.wupp.WizardInitialisationException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelTimeseries implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelTimeseries.class);

    //~ Instance fields --------------------------------------------------------

    protected SwmmInput swmmInput;

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    /** local swmm project variable. */

    private transient List<Integer> stationIds;

    private transient volatile SwmmWizardPanelTimeseriesUI component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public SwmmWizardPanelTimeseries() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new SwmmWizardPanelTimeseriesUI(this);
                    } catch (final WizardInitialisationException ex) {
                        LOG.error("cannot create Timeseries wizard panel component", ex); // NOI18N
                    }
                }
            }
        }

        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("read settings");
        }

        wizard = (WizardDescriptor)settings;
        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_STATION_IDS) != null : "station ids list is null";
        this.stationIds = (List<Integer>)wizard.getProperty(
                SwmmPlusEtaWizardAction.PROP_STATION_IDS);

        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT) != null : "swmm input is null";
        this.swmmInput = (SwmmInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("store settings");
        }
        wizard = (WizardDescriptor)settings;
        // wizard.putProperty(SwmmPlusEtaWizardAction.PROP_STATION_IDS, this.stationIds);
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT, this.swmmInput);
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        if (LOG.isDebugEnabled()) {
            LOG.debug("isValid");
        }
        if (this.swmmInput.getTimeseries().isEmpty()) {
            // FIXME: i18n
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                "Bitte wählen Sie mindestens eine Regenzeitreihe aus");
            valid = false;
        } else {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                null);
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
     *
     * @return  DOCUMENT ME!
     */
    public WizardDescriptor getWizard() {
        return wizard;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Integer> getStationIds() {
        return stationIds;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Integer> getTimeseriesIds() {
        return this.swmmInput.getTimeseries();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isForecast() {
        return this.swmmInput.isForecast();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stationIds  DOCUMENT ME!
     */
    public void setTimeseriesIds(final List<Integer> stationIds) {
        this.swmmInput.setTimeseries(stationIds);
        this.changeSupport.fireChange();
    }
}
