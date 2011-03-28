/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RainfallDownscalingWizardAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SCENARIO = "__prop_scenario__";       // NOI18N
    public static final String PROP_TARGET_YEAR = "__prop_target_year__"; // NOI18N
    public static final String PROP_NAME = "__prop_name__";               // NOI18N
    public static final String PROP_DESCRIPTION = "__prop_description__"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(RainfallDownscalingWizardAction.class);

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardAction object.
     */
    public RainfallDownscalingWizardAction() {
        super("Perform rainfall downscaling");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * EDT only !
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        assert EventQueue.isDispatchThread() : "can only be called from EDT"; // NOI18N

        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new RainfallDownscalingWizardPanelScenarios(),
                    new RainfallDownscalingWizardPanelTargetDate(),
                    new RainfallDownscalingWizardPanelMetadata()
                };
            final String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                final Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) {
                    // assume Swing components
                    final JComponent jc = (JComponent)c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(i));
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the
                    // background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }

        return panels;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final CidsBean cidsBean = getCidsBean();
        assert cidsBean != null : "cidsbean not set";                            // NOI18N
        assert cidsBean.getMetaObject() != null : "cidsbean without metaobject"; // NOI18N

        final MetaObject mo = cidsBean.getMetaObject();
        final MetaClass mc = mo.getMetaClass();

        assert mc != null : "metaobject without metaclass"; // NOI18N

        if (SMSUtils.TABLENAME_TIMESERIES.equals(mc.getTableName())) {
            final WizardDescriptor wizard = new WizardDescriptor(getPanels());
            wizard.setTitleFormat(new MessageFormat("{0}"));                                       // NOI18N
            wizard.setTitle(NbBundle.getMessage(
                    RainfallDownscalingWizardAction.class,
                    "RainfallDownscalingWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N
            final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
            dialog.pack();
            dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
            dialog.setVisible(true);
            dialog.toFront();

            final boolean cancelled = wizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                try {
                    CidsBean modelInput = createModelInput(wizard, mo);
                    CidsBean modelRun = createModelRun(wizard, modelInput);

                    modelRun = modelRun.persist();
                    modelInput = (CidsBean)modelRun.getProperty("modelinput"); // NOI18N

                    SMSUtils.executeAndShowRun(modelRun);
                } catch (final Exception ex) {
                    final String message = "Cannot perform downscaling";
                    LOG.error(message, ex);
                    JOptionPane.showMessageDialog(ComponentRegistry.getRegistry().getMainWindow(),
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            LOG.warn("can only perform this action of objects of metaclass timeseries"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard  DOCUMENT ME!
     * @param   mo      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createModelInput(final WizardDescriptor wizard, final MetaObject mo) throws IOException {
        final String scenario = (String)wizard.getProperty(PROP_SCENARIO);
        final Integer targetYear = (Integer)wizard.getProperty(PROP_TARGET_YEAR);

        assert scenario != null : "scenario was not set";      // NOI18N
        assert targetYear != null : "target year was not set"; // NOI18N

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new rainfall modelinput: " // NOI18N
                        + "scenario=" + scenario     // NOI18N
                        + " || targetYear=" + targetYear // NOI18N
                        + " || mo=" + mo);           // NOI18N
        }

        final Date created = GregorianCalendar.getInstance().getTime();
        final String user = SessionManager.getSession().getUser().getName();

        final String wizName = (String)wizard.getProperty(PROP_NAME);
        final String name = "Rainfall downscaling input (" + wizName + ")";

        final String timeseriesName = (String)mo.getBean().getProperty("name"); // NOI18N
        final Integer timeseriesId = mo.getId();

        final RainfallDownscalingInput input = new RainfallDownscalingInput(
                created,
                user,
                name,
                scenario,
                targetYear,
                timeseriesId,
                timeseriesName);

        return SMSUtils.createModelInput(name, input, SMSUtils.Model.RF_DS);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizard     DOCUMENT ME!
     * @param   inputBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private CidsBean createModelRun(final WizardDescriptor wizard, final CidsBean inputBean) throws IOException {
        final String name = (String)wizard.getProperty(PROP_NAME);
        final String description = (String)wizard.getProperty(PROP_DESCRIPTION);

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating new rainfall modelrun: " // NOI18N
                        + "name=" + name           // NOI18N
                        + " || description=" + description // NOI18N
                        + " || cidsbean=" + inputBean); // NOI18N
        }

        return SMSUtils.createModelRun(name, description, inputBean);
    }
}
