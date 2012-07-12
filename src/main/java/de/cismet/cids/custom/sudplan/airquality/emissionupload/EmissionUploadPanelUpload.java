/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class EmissionUploadPanelUpload extends AbstractWizardPanel implements Cancellable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EmissionUploadPanelUpload.class);

    //~ Instance fields --------------------------------------------------------

    private transient volatile EmissionUploadVisualPanelUpload component;
    private transient String action;

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

        return true;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        action = (String)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_ACTION);

        component.init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(EmissionUploadWizardAction.PROPERTY_ACTION, action);
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new EmissionUploadVisualPanelUpload(this);
        }

        return component;
    }

    @Override
    public boolean cancel() {
        action = null;

        return true;
    }

    /**
     * /** * DOCUMENT ME! * * @return DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAction() {
        return action;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  action  DOCUMENT ME!
     */
    public void setAction(final String action) {
        this.action = action;
        changeSupport.fireChange();
    }
}
