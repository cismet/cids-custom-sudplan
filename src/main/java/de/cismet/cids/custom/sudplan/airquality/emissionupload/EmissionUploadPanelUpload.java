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
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.EventQueue;

import java.net.URL;

import java.util.Collection;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.EmissionUpload.Action;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.EmissionUpload.State;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.dataImport.AbstractWizardPanel;

import de.cismet.cismap.commons.Crs;

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

    private transient volatile StatusPanel component;
    private transient EmissionUpload emissionUpload;
    private transient Future emissionUploadFuture;
    private transient EmissionUpload.State state;
    private transient EmissionUpload.Action action;
    private transient Collection<Exception> exceptions;

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

        if ((exceptions != null) && !exceptions.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadPanelUpload.class,
                    "EmissionUploadPanelUpload.isValid().warn.exceptions")); // NOI18N

            return false;
                // TODO: More user feedback?
        }

        return EmissionUpload.State.DONE.equals(state);
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        if ((emissionUpload != null) && EmissionUpload.State.RUNNING.equals(emissionUpload.getState())) {
            return;
        }

        final String description = (String)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_DESCRIPTION);
        final String emissionScenarioName = (String)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_NAME);
        final Collection<Grid> grids = (Collection<Grid>)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_GRIDS);
        final Crs srs = (Crs)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_SRS);
        final URL url = (URL)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_URL);

        emissionUpload = new EmissionUpload(this, grids, emissionScenarioName, srs, description, url);

        emissionUploadFuture = SudplanConcurrency.getSudplanGeneralPurposePool().submit(emissionUpload);
        refreshState();

        changeSupport.fireChange();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshState() {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (emissionUpload == null) {
                        component.setBusy(false);
                        component.setStatusMessage(EmissionUpload.Action.WAITING.toString());
                    } else {
                        component.setBusy(EmissionUpload.State.RUNNING.equals(emissionUpload.getState()));

                        if (EmissionUpload.State.RUNNING.equals(emissionUpload.getState())) {
                            component.setStatusMessage(emissionUpload.getAction().toString());
                        } else if (EmissionUpload.State.DONE.equals(emissionUpload.getState())) {
                            // TODO: Save upload as CidsBean
                            component.setStatusMessage(
                                NbBundle.getMessage(
                                    EmissionUploadPanelUpload.class,
                                    "EmissionUploadPanelUpload.refreshState().component.done")); // NOI18N
                            wizard.putProperty(EmissionUploadWizardAction.PROPERTY_SUCCESSFUL, Boolean.TRUE);
                        } else if (EmissionUpload.State.ERRONEOUS.equals(emissionUpload.getState())) {
                            final StringBuilder messages = new StringBuilder();
                            messages.append("<ul>");                                             // NOI18N
                            for (final Exception exception : exceptions) {
                                messages.append("<li>");                                         // NOI18N
                                messages.append(exception.getMessage());
                                messages.append("</li>");                                        // NOI18N
                            }
                            messages.append("</ul>");                                            // NOI18N

                            component.setStatusMessage(
                                NbBundle.getMessage(
                                    EmissionUploadPanelUpload.class,
                                    "EmissionUploadPanelUpload.refreshState().component.erroneous", // NOI18N
                                    messages.toString()));
                        }
                    }
                }
            });
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new StatusPanel(NbBundle.getMessage(
                        EmissionUploadPanelUpload.class,
                        "EmissionUploadPanelUpload.createComponent().component.name")); // NOI18N
        }

        return component;
    }

    @Override
    public boolean cancel() {
        if (emissionUploadFuture != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cancelling upload of emission scenario."); // NOI18N
            }

            final boolean cancelled = emissionUploadFuture.cancel(true);

            if (!cancelled) {
                return emissionUploadFuture.isDone();
            }
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Exception> getExceptions() {
        return exceptions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  exceptions  DOCUMENT ME!
     */
    public void setExceptions(final Collection<Exception> exceptions) {
        this.exceptions = exceptions;

        refreshState();
        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public State getState() {
        return state;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  state  DOCUMENT ME!
     */
    public void setState(final State state) {
        this.state = state;

        refreshState();
        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Action getAction() {
        return action;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  action  DOCUMENT ME!
     */
    public void setAction(final Action action) {
        this.action = action;

        refreshState();
        changeSupport.fireChange();
    }
}
