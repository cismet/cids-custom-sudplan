/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.openide.WizardDescriptor;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DoCalibrationWizardPanelMetadata extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_NAME = "__prop_name__"; // NOI18N
    public static final String PROP_DESC = "__prop_desc__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient String name;
    private transient String description;

    //~ Methods ----------------------------------------------------------------

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

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.name = name;

        changeSupport.fireChange();
    }

    @Override
    protected Component createComponent() {
        return new DoCalibrationVisualPanelMetadata(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        setName((String)wizard.getProperty(PROP_NAME));
        setDescription((String)wizard.getProperty(PROP_DESC));

        ((DoCalibrationVisualPanelMetadata)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_NAME, name);
        wizard.putProperty(PROP_DESC, description);
    }

    @Override
    public boolean isValid() {
        if ((name == null) || name.isEmpty()) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please enter a name");

            return false;
        } else {
            if ((description == null) || description.isEmpty()) {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "You are encouraged to enter a description");
            } else {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
            }

            return true;
        }
    }
}
