/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class Euler2ComputationWizardPanelAttributes extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_EULER2_NAME = "__prop_euler2_name__";
    public static final String PROP_EULER2_DESC = "__prop_euler2_desc__";

    //~ Instance fields --------------------------------------------------------

    private transient String strName;
    private transient String strDesc;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDesc() {
        return strDesc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strDesc  DOCUMENT ME!
     */
    public void setDesc(final String strDesc) {
        this.strDesc = strDesc;
        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return strName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strName  DOCUMENT ME!
     */
    public void setName(final String strName) {
        this.strName = strName;
        changeSupport.fireChange();
    }

    @Override
    protected Component createComponent() {
        return new Euler2ComputationVisualPanelAttributes(this);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        setName((String)wizard.getProperty(PROP_EULER2_NAME));
        setDesc((String)wizard.getProperty(PROP_EULER2_DESC));
        // ((Euler2ComputationVisualPanelAttributes)getComponent()).init();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(PROP_EULER2_NAME, strName);
        wizard.putProperty(PROP_EULER2_DESC, strDesc);
    }

    @Override
    public boolean isValid() {
        if ((getName() == null) || getName().equals("")) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    Euler2ComputationWizardPanelAttributes.class,
                    "Euler2ComputationWizardPanelAttributes.isValid().name"));
            return false;
        } else {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                null);
            return true;
        }
    }
}
