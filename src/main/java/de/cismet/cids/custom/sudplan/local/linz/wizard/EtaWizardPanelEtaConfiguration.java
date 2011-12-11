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

import de.cismet.cids.custom.sudplan.local.linz.EtaConfiguration;
import de.cismet.cids.custom.sudplan.local.linz.EtaInput;
import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;
import de.cismet.cids.custom.sudplan.local.wupp.WizardInitialisationException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class EtaWizardPanelEtaConfiguration implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaWizardPanelEtaConfiguration.class);

    //~ Instance fields --------------------------------------------------------

    protected transient SwmmInput swmmInput;
    protected transient EtaInput etaInput;

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    /** local swmm project variable. */

    private transient volatile EtaWizardPanelEtaConfigurationUI component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public EtaWizardPanelEtaConfiguration() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new EtaWizardPanelEtaConfigurationUI(this);
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
        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_ETA_INPUT) != null : "eta input is null";
        this.etaInput = (EtaInput)wizard.getProperty(
                SwmmPlusEtaWizardAction.PROP_ETA_INPUT);

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
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_ETA_INPUT, this.etaInput);
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT, this.swmmInput);
    }

    @Override
    public boolean isValid() {
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
    public List<EtaConfiguration> getEtaConfigurations() {
        return this.etaInput.getEtaConfigurations();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  etaConfigurations  DOCUMENT ME!
     */
    public void setEtaConfigurations(final List<EtaConfiguration> etaConfigurations) {
        this.etaInput.setEtaConfigurations(etaConfigurations);
        // this.changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSwmmProjectId() {
        return this.swmmInput.getSwmmProject();
    }
}
