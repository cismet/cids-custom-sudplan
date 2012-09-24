/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ImportGeoCPMWizardPanelMetadata implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ImportGeoCPMWizardPanelCFGSelect.class);

    public static final String PROP_GEOCPM_BEAN = "__prop_geocpm_bean__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;

    private transient volatile ImportGeoCPMVisualPanelMetadata component;
    private transient Exception initialisationException;

    private transient CidsBean cidsBean;

    private final transient PropertyChangeListener beanL;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportGeoCPMWizardPanelCFGSelect object.
     */
    public ImportGeoCPMWizardPanelMetadata() {
        changeSupport = new ChangeSupport(this);
        beanL = new PropertyChangetoChangeListener();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    component = new ImportGeoCPMVisualPanelMetadata(this);
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

        cidsBean = (CidsBean)wizard.getProperty(PROP_GEOCPM_BEAN);

        if (cidsBean == null) {
            try {
                // FIXME: hardcoded domain
                final MetaClass mc = ClassCacheMultiple.getMetaClass(
                        SMSUtils.DOMAIN_SUDPLAN_WUPP,
                        "geocpm_configuration");                        // NOI18N
                final MetaObject mo = mc.getEmptyInstance();
                cidsBean = mo.getBean();
            } catch (final Exception ex) {
                LOG.error("cannot initialise wizard visual panel", ex); // NOI18N
                initialisationException = ex;

                changeSupport.fireChange();
            }
        }

        cidsBean.addPropertyChangeListener(beanL);
        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        wizard.putProperty(PROP_GEOCPM_BEAN, cidsBean);

        cidsBean.removePropertyChangeListener(beanL);
    }

    @Override
    public boolean isValid() {
        if (initialisationException == null) {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

            if (cidsBean.getProperty("name") == null) { // NOI18N
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        ImportGeoCPMWizardPanelMetadata.class,
                        "ImportGeoCPMWizardPanelMetadata.isValid().info.enterName"));

                return false;
            } else if (cidsBean.getProperty("investigation_area") == null) { // NOI18N
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        ImportGeoCPMWizardPanelMetadata.class,
                        "ImportGeoCPMWizardPanelMetadata.isValid().info.chooseArea"));

                return false;
            } else if (cidsBean.getProperty("description") == null) { // NOI18N
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        ImportGeoCPMWizardPanelMetadata.class,
                        "ImportGeoCPMWizardPanelMetadata.isValid().info.enterDescription"));

                return true;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

                return true;
            }
        } else {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    ImportGeoCPMWizardPanelMetadata.class,
                    "ImportGeoCPMWizardPanelMetadata.isValid().error.errorCreatingBean(exMessage)",
                    initialisationException));

            return false;
        }
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
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PropertyChangetoChangeListener implements PropertyChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            changeSupport.fireChange();
        }
    }
}
