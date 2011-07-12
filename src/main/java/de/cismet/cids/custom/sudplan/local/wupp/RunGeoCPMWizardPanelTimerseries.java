/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

import java.awt.Component;

import javax.swing.event.ChangeListener;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunGeoCPMWizardPanelTimerseries implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunGeoCPMWizardPanelInput.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient CidsBean timeseries;

    private transient volatile RunGeoCPMVisualPanelTimeseries component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public RunGeoCPMWizardPanelTimerseries() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getTimeseries() {
        return timeseries;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  input  DOCUMENT ME!
     */
    public void setTimeseries(final CidsBean input) {
        this.timeseries = input;
        changeSupport.fireChange();
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new RunGeoCPMVisualPanelTimeseries(this);
                    } catch (final WizardInitialisationException ex) {
                        LOG.error("cannot create wizard panel component", ex); // NOI18N
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
        wizard = (WizardDescriptor)settings;
        final CidsBean timeseriesBean = (CidsBean)wizard.getProperty(RunGeoCPMWizardAction.PROP_TIMESERIES_BEAN);

        setTimeseries(timeseriesBean);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(RunGeoCPMWizardAction.PROP_TIMESERIES_BEAN, timeseries);
    }

    @Override
    public boolean isValid() {
        return timeseries != null;
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
}
