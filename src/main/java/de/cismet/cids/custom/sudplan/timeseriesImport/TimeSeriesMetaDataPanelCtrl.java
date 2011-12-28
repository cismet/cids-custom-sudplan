/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesImport;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;

import java.awt.Component;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesMetaDataPanelCtrl extends AbstractWizardPanelCtrl {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TimeSeriesMetaDataPanelCtrl.class);

    //~ Instance fields --------------------------------------------------------

    private final transient TimeSeriesMetaDataPanel comp;
    private transient CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesImportFileChoosePanelCtrl object.
     */
    public TimeSeriesMetaDataPanelCtrl() {
        this.comp = new TimeSeriesMetaDataPanel(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        return this.comp;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering read(WizardDescriptor)"); // NOI18N
        }

        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SMSUtils.TABLENAME_TIMESERIES);

        if (mc == null) {
            LOG.error(
                "Was not able to retrieve MetaClass for domain '"
                        + domain                        // NOI18N
                        + "' and table '"
                        + SMSUtils.TABLENAME_TIMESERIES // NOI18N
                        + "'");                         // NOI18N
        } else {
            this.cidsBean = mc.getEmptyInstance().getBean();
            this.comp.init();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving read(WizardDescriptor)"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering store(WizardDescriptor)"); // NOI18N
        }

        wizard.putProperty(TimeSeriesImportWizardAction.PROP_BEAN, this.cidsBean);

        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving store(WizardDescriptor)"); // NOI18N
        }
    }

    @Override
    public boolean isValid() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering isValid()"); // NOI18N
        }
        if (this.cidsBean == null) {
            return false;
        }

        final Object nameObj = this.cidsBean.getProperty("name");    // NOI18N
        final Object station = this.cidsBean.getProperty("station"); // NOI18N

        if ((nameObj == null) || (station == null)) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                    "TimeSeriesMetaDataPanelCtrl.isValid().wizard.putProperty(String,String).nameAndStation"));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Leaving isValid() with retun value false"); // NOI18N
            }
            return false;
        }

        final String name = (String)nameObj;
        if (name.trim().isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                    "TimeSeriesMetaDataPanelCtrl.isValid().wizard.putProperty(String,String).name"));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Leaving isValid() with retun value false"); // NOI18N
            }
            return false;
        }

        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(
            WizardDescriptor.PROP_INFO_MESSAGE,
            java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/timeseriesImport/Bundle").getString(
                "TimeSeriesMetaDataPanelCtrl.isValid().wizard.putProperty(String,String).persisting"));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving isValid() with retun value true"); // NOI18N
        }
        return true;
    }
}