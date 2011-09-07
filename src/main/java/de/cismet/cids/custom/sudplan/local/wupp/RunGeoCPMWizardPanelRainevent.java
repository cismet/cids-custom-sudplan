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
public final class RunGeoCPMWizardPanelRainevent implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunGeoCPMWizardPanelInput.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient CidsBean rainevent;

    private transient volatile RunGeoCPMVisualPanelRainevent component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public RunGeoCPMWizardPanelRainevent() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getRainevent() {
        return rainevent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  input  DOCUMENT ME!
     */
    public void setRainevent(final CidsBean input) {
        this.rainevent = input;
        changeSupport.fireChange();
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new RunGeoCPMVisualPanelRainevent(this);
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
        final CidsBean raineventBean = (CidsBean)wizard.getProperty(RunGeoCPMWizardAction.PROP_RAINEVENT_BEAN);

        setRainevent(raineventBean);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(RunGeoCPMWizardAction.PROP_RAINEVENT_BEAN, rainevent);
    }

    @Override
    public boolean isValid() {
        return rainevent != null;
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
