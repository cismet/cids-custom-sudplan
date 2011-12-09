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
public final class SwmmWizardPanelStations implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelStations.class);

    //~ Instance fields --------------------------------------------------------

    protected SwmmInput swmmInput;

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    /** local swmm project variable. */

    private transient List<Integer> stationIds;

    private transient volatile SwmmWizardPanelStationsUI component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public SwmmWizardPanelStations() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new SwmmWizardPanelStationsUI(this);
                    } catch (final WizardInitialisationException ex) {
                        LOG.error("cannot create monitoring station wizard panel component", ex); // NOI18N
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
        final List<Integer> stationIdsList = (List<Integer>)wizard.getProperty(
                SwmmPlusEtaWizardAction.PROP_STATION_IDS);
        this.setStationIds(stationIdsList);

        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm input bean is null";
        final SwmmInput swmmInputObject = (SwmmInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT);
        this.setSwmmInput(swmmInputObject);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("store settings");
        }
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_STATION_IDS, this.stationIds);
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT, this.getSwmmInput());
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        if (this.stationIds.isEmpty()) {
            // FIXME: i18n
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                "Bitte wählen Sie mindestens eine Regenmessstation aus");
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
     * @param  stationIds  DOCUMENT ME!
     */
    public void setStationIds(final List<Integer> stationIds) {
        this.stationIds = stationIds;
        this.changeSupport.fireChange();
    }

    /**
     * Get the value of swmmInput.
     *
     * @return  the value of swmmInput
     */
    public SwmmInput getSwmmInput() {
        return swmmInput;
    }

    /**
     * Set the value of swmmInput.
     *
     * @param  swmmInput  new value of swmmInput
     */
    public void setSwmmInput(final SwmmInput swmmInput) {
        this.swmmInput = swmmInput;
    }
}
