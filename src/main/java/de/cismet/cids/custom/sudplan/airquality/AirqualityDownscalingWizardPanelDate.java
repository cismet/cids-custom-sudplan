/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;

import java.util.Date;

import de.cismet.cids.custom.sudplan.dataImport.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelDate extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingWizardPanelDate.class);

    //~ Instance fields --------------------------------------------------------

    private transient AirqualityDownscalingVisualPanelDate component;

    private transient Date startDate;
    private transient Date endDate;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final AirqualityDownscalingWizardPanelDate panel = new AirqualityDownscalingWizardPanelDate();
                    panel.createComponent();
                    final WizardDescriptor wizard = new WizardDescriptor(new WizardDescriptor.Panel[] { panel });
                    final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    dialog.toFront();
                }
            });
    }

    @Override
    public Component createComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelDate(this);
        }

        return component;
    }

    @Override
    public void read(final WizardDescriptor wizard) {
        startDate = (Date)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_START_DATE);
        endDate = (Date)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_END_DATE);

        component.init();
    }

    @Override
    public void store(final WizardDescriptor wizard) {
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_START_DATE, startDate);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_END_DATE, endDate);
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        boolean valid = true;

        if ((startDate == null) || (endDate == null)) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelDate.class,
                    "AirqualityDownscalingWizardPanelDate.isValid().noStartOrEndDate"));  // NOI18N
            valid = false;
        } else {
            if (startDate.after(endDate)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    NbBundle.getMessage(
                        AirqualityDownscalingWizardPanelDate.class,
                        "AirqualityDownscalingWizardPanelDate.isValid().startAfterEnd")); // NOI18N
                valid = false;
            }
        }

        return valid;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  endDate  DOCUMENT ME!
     */
    public void setEndDate(final Date endDate) {
        this.endDate = endDate;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  startDate  DOCUMENT ME!
     */
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;

        changeSupport.fireChange();
    }
}
