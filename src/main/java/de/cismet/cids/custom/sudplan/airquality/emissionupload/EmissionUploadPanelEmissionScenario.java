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
import org.openide.util.NbBundle;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

import de.cismet.cismap.commons.Crs;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class EmissionUploadPanelEmissionScenario extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EmissionUploadPanelGrids.class);

    //~ Instance fields --------------------------------------------------------

    private transient volatile EmissionUploadVisualPanelEmissionScenario component;
    private transient String emissionScenarioName;
    private transient Crs srs;
    private transient String description;

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

        boolean valid = true;

        if (emissionScenarioName == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadVisualPanelEmissionScenario.class,
                    "EmissionUploadVisualPanelEmissionScenario.isValid().name.null"));    // NOI18N
            valid = false;
        } else if (!emissionScenarioName.matches("[0-9a-zA-Z_]{1,10}")) {                 // NOI18N
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadVisualPanelEmissionScenario.class,
                    "EmissionUploadVisualPanelEmissionScenario.isValid().name.invalid")); // NOI18N
            valid = false;
        }

        if (srs == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadVisualPanelEmissionScenario.class,
                    "EmissionUploadVisualPanelEmissionScenario.isValid().srs.null")); // NOI18N
            valid = false;
        }

        return valid;
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new EmissionUploadVisualPanelEmissionScenario(this);
        }

        return component;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        emissionScenarioName = (String)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_NAME);
        srs = (Crs)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_SRS);
        description = (String)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_DESCRIPTION);

        component.init();

        changeSupport.fireChange();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(EmissionUploadWizardAction.PROPERTY_NAME, emissionScenarioName);
        wizard.putProperty(EmissionUploadWizardAction.PROPERTY_SRS, srs);
        wizard.putProperty(EmissionUploadWizardAction.PROPERTY_DESCRIPTION, description);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  DOCUMENT ME!
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getEmissionScenarioName() {
        return emissionScenarioName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  emissionScenarioName  DOCUMENT ME!
     */
    public void setEmissionScenarioName(final String emissionScenarioName) {
        this.emissionScenarioName = emissionScenarioName;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Crs getSrs() {
        return srs;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  srs  DOCUMENT ME!
     */
    public void setSrs(final Crs srs) {
        this.srs = srs;

        changeSupport.fireChange();
    }
}
