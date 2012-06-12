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
public final class CreateLocalModelWizardPanelMetadata extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_MODEL_NAME = "__prop_model_name__"; // NOI18N
    public static final String PROP_MODEL_DESC = "__prop_model_desc__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient int basinId;
    private transient String name;
    private transient String description;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Component createComponent() {
        return new CreateLocalModelVisualPanelMetadata(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        basinId = (Integer)wizard.getProperty(CreateLocalModelWizardAction.PROP_POI_ID);
        name = (String)wizard.getProperty(PROP_MODEL_NAME);
        description = (String)wizard.getProperty(PROP_MODEL_DESC);
        ((CreateLocalModelVisualPanelMetadata)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_MODEL_NAME, name);
        wizard.putProperty(PROP_MODEL_DESC, description);
    }

    @Override
    public boolean isValid() {
        // TODO: implement
        return super.isValid();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getBasinId() {
        return basinId;
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
}
