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
public final class RunGeoCPMWizardPanelInput implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunGeoCPMWizardPanelInput.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient CidsBean input;

    private transient volatile RunGeoCPMVisualPanelInput component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public RunGeoCPMWizardPanelInput() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getInput() {
        return input;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  input  DOCUMENT ME!
     */
    public void setInput(final CidsBean input) {
        this.input = input;
        changeSupport.fireChange();
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    component = new RunGeoCPMVisualPanelInput(this);
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
        final CidsBean inputBean = (CidsBean)wizard.getProperty(RunGeoCPMWizardAction.PROP_INPUT_BEAN);

        setInput(inputBean);
        try {
            component.init();
        } catch (final WizardInitialisationException ex) {
            LOG.error("cannot create wizard panel component", ex); // NOI18N
        }
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(RunGeoCPMWizardAction.PROP_INPUT_BEAN, input);
    }

    @Override
    public boolean isValid() {
        return input != null;
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
