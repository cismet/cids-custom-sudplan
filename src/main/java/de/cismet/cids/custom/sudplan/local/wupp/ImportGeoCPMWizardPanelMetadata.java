/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

import java.awt.Component;

import javax.swing.event.ChangeListener;

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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportGeoCPMWizardPanelCFGSelect object.
     */
    public ImportGeoCPMWizardPanelMetadata() {
        changeSupport = new ChangeSupport(this);
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
            final Integer geocpmId = (Integer)wizard.getProperty(ImportGeoCPMWizardPanelUpload.PROP_GEOCPM_ID);
            assert geocpmId != null : "geocpm id must not be null"; // NOI18N

            try {
                // FIXME: hardcoded domain
                final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "geocpm_configuration");
                final MetaObject mo = SessionManager.getProxy().getMetaObject(geocpmId, mc.getID(), "SUDPLAN-WUPP");
                cidsBean = mo.getBean();

                component.init();
            } catch (final Exception ex) {
                LOG.error("cannot initialise wizard visual panel", ex); // NOI18N
                initialisationException = ex;

                changeSupport.fireChange();
            }
        }
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        wizard.putProperty(PROP_GEOCPM_BEAN, cidsBean);
    }

    @Override
    public boolean isValid() {
        if (initialisationException == null) {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

            if (cidsBean.getProperty("name") == null) {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please enter a name");

                return false;
            } else if (cidsBean.getProperty("investigation_area") == null) {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please choose an area");

                return false;
            } else if (cidsBean.getProperty("description") == null) {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Descriptions are appreciated");

                return true;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

                return true;
            }
        } else {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                "Error while fetching GeoCPM cidsBean: "
                        + initialisationException);

            return false;
        }
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
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
}
