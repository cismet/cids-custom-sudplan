/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.NbBundle;

import java.awt.EventQueue;

import java.io.IOException;
import java.io.StringWriter;

import java.sql.Timestamp;

import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.commons.CismetExecutors;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractModelManager implements ModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractModelManager.class);
    // NOTE: maybe we need a per-cidsbean dispatch thread, but this should do
    private static final ExecutorService progressDispatcher;

    static {
        progressDispatcher = CismetExecutors.newSingleThreadExecutor(
                SudplanConcurrency.createThreadFactory("model-progress-dispatcher")); // NOI18N
    }

    //~ Instance fields --------------------------------------------------------

    protected transient CidsBean cidsBean;
    private final transient ProgressSupport progressSupport;
    private transient volatile JComponent ui;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractModelManager object.
     */
    public AbstractModelManager() {
        progressSupport = new ProgressSupport();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new DefaultModelManagerUI(this);
                }
            }
        }

        return ui;
    }

    @Override
    public void execute() throws IOException {
        fireStarted();

        internalExecute();

        final String reloadId = getReloadId();
        if (reloadId == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("won't perform reload after model run started, since no id is provided"); // NOI18N
            }
        } else {
            ComponentRegistry.getRegistry().getCatalogueTree().requestRefreshNode(reloadId);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    protected abstract void internalExecute() throws IOException;

    /**
     * By default returns the unified resource of the inputmanager.
     *
     * @return  the unified resource of the inputmanager
     *
     * @throws  IOException  if the {@link CidsBean} has not been set yet or any error occurs while loading the
     *                       inputmanager or its unified resource
     */
    @Override
    public Object getUR() throws IOException {
        if (cidsBean == null) {
            throw new IOException("unified resource is only available when cidsBean has been set"); // NOI18N
        }

        final Manager inputManager = SMSUtils.loadManagerFromRun(cidsBean, ManagerType.INPUT);
        inputManager.setCidsBean((CidsBean)cidsBean.getProperty("modelinput")); // NOI18N

        return inputManager.getUR();
    }

    /**
     * This default implementation does nothing, so <code>super</code> call is not necessary.
     *
     * @throws  IOException  never
     */
    @Override
    public void finalise() throws IOException {
        // noop
    }

    @Override
    public Feature getFeature() throws IOException {
        // no feature attached by default
        return null;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
    }

    @Override
    public void addProgressListener(final ProgressListener progressL) {
        progressSupport.addProgressListener(progressL);
    }

    @Override
    public void removeProgressListener(final ProgressListener progressL) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeProgressListener: " + progressL);
        }
        progressSupport.removeProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    protected void fireStarted() {
        final Runnable fireStarted = new Runnable() {

                @Override
                public void run() {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("fireStarted: " + AbstractModelManager.this); // NOI18N
                    }

                    if (isStarted()) {
                        LOG.warn("cannot start when already started"); // NOI18N
                        return;
                    }

                    final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
                    try {
                        cidsBean.setProperty(
                            "started", // NOI18N
                            dateConverter.convertReverse(GregorianCalendar.getInstance().getTime()));
                    } catch (final Exception ex) {
                        final String message = "cannot set started flag in runbean"; // NOI18N
                        LOG.error(message, ex);
                        throw new IllegalStateException(message, ex);
                    }

                    progressSupport.fireEvent(new ProgressEvent(
                            AbstractModelManager.this,
                            ProgressEvent.State.STARTED));
                }
            };

        progressDispatcher.submit(fireStarted);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  step      percent DOCUMENT ME!
     * @param  maxSteps  DOCUMENT ME!
     */
    protected void fireProgressed(final int step, final int maxSteps) {
        this.fireProgressed(step, maxSteps, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   step      percent DOCUMENT ME!
     * @param   maxSteps  DOCUMENT ME!
     * @param   message   DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    protected void fireProgressed(final int step, final int maxSteps, final String message) {
        final Runnable fireProgressed = new Runnable() {

                @Override
                public void run() {
                    if (!isStarted()) {
                        throw new IllegalStateException("cannot progress when not started yet"); // NOI18N
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("fireProgressed: " + message); // NOI18N
                    }

                    if (isFinished()) {
                        LOG.warn("cannot progress when already finished"); // NOI18N
                        return;
                    }

                    progressSupport.fireEvent(new ProgressEvent(
                            AbstractModelManager.this,
                            ProgressEvent.State.PROGRESSING,
                            step,
                            maxSteps,
                            message));
                }
            };

        progressDispatcher.submit(fireProgressed);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    protected void fireBroken(final String message) {
        final Runnable fireBroken = new Runnable() {

                @Override
                public void run() {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("fireBroken: " + message); // NOI18N
                    }

                    if (isFinished()) {
                        LOG.warn("will not fire broken when run is finished");
                        return;
                    }

                    final StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("<html><b>");
                    errorMessage.append(NbBundle.getMessage(
                            AbstractModelManager.class,
                            "AbstractModelManager.fireBroken().brokenDialog.message", // NOI18N
                            cidsBean.getProperty("name"))); // NOI18N)
                    errorMessage.append("</b><br/>");
                    errorMessage.append("<p>").append(message).append("</p></html>");

                    JOptionPane.showMessageDialog(ComponentRegistry.getRegistry().getMainWindow(),
                        errorMessage.toString(),
                        NbBundle.getMessage(
                            AbstractModelManager.class,
                            "AbstractModelManager.fireBroken().brokenDialog.title"), // NOI18N
                        JOptionPane.WARNING_MESSAGE);

                    RunInfo runInfo = getRunInfo();
                    if (runInfo == null) {
                        runInfo = new DefaultRunInfo(true, message);
                    } else {
                        runInfo.setBroken(true);
                        runInfo.setBrokenMessage(message);
                    }

                    try {
                        final StringWriter writer = new StringWriter();
                        final ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(writer, runInfo);
                        cidsBean.setProperty("runinfo", writer.toString());
                    } catch (final Exception ex) {
                        LOG.error("could not set run info: " + ex.getMessage(), ex);
                    }

                    try {
                        cidsBean = cidsBean.persist();
                    } catch (final Exception ex) {
                        LOG.error("could not persists cids bean: " + ex.getMessage(), ex);
                    }

                    final ComponentRegistry reg = ComponentRegistry.getRegistry();
                    final String reloadId = getReloadId();
                    if (reloadId == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("won't perform reload after model run finished, since no id is provided"); // NOI18N
                        }
                    } else {
                        reg.getCatalogueTree().requestRefreshNode(reloadId);
                    }

                    final CidsBeanRenderer currentRenderer = reg.getDescriptionPane().currentRenderer();
                    if (currentRenderer == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("current renderer is null, won't reflect bean changes to ui"); // NOI18N
                        }
                    } else {
                        final CidsBean currentBean = currentRenderer.getCidsBean();
                        if ((cidsBean.getMetaObject().getClassID() == currentBean.getMetaObject().getClassID())
                                    && (cidsBean.getMetaObject().getID() == currentBean.getMetaObject().getID())) {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        currentRenderer.setCidsBean(cidsBean);
                                    }
                                });
                        }
                    }

                    progressSupport.fireEvent(new ProgressEvent(
                            AbstractModelManager.this,
                            ProgressEvent.State.BROKEN,
                            message));
                }
            };

        progressDispatcher.submit(fireBroken);
    }

    /**
     * DOCUMENT ME!
     */
    protected void fireBroken() {
        this.fireBroken(NbBundle.getMessage(
                AbstractModelManager.class,
                "AbstractModelManager.fireBroken().brokenDialog.message.unknownerror"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    protected void fireFinised() {
        final Runnable fireFinished = new Runnable() {

                @Override
                public void run() {
                    if (!isStarted()) {
                        throw new IllegalStateException("cannot be finished when not started yet"); // NOI18N
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("fireFinished: " + AbstractModelManager.this); // NOI18N
                    }

                    if (isFinished()) {
                        return;
                    }

                    final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("creating model output Bean");
                        }

                        cidsBean.setProperty(
                            "finished", // NOI18N
                            dateConverter.convertReverse(GregorianCalendar.getInstance().getTime()));

                        final CidsBean modelOutput = createOutputBean();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("model output Bean '" + modelOutput + "' succesfully created");
                        }

                        cidsBean.setProperty("modeloutput", modelOutput); // NOI18N

                        RunInfo runInfo = getRunInfo();
                        if (runInfo == null) {
                            runInfo = new DefaultRunInfo();
                        } else {
                            runInfo.setFinished(true);
                        }

                        try {
                            final StringWriter writer = new StringWriter();
                            final ObjectMapper mapper = new ObjectMapper();
                            mapper.writeValue(writer, runInfo);
                            cidsBean.setProperty("runinfo", writer.toString());
                        } catch (final Exception ex) {
                            LOG.error("could not set run info: " + ex.getMessage(), ex);
                        }

                        cidsBean = cidsBean.persist();

                        final MetaObject outputobject = ((CidsBean)cidsBean.getProperty("modeloutput")).getMetaObject(); // NOI18N

                        assert outputobject != null : "bean without metaobject";                     // NOI18N
                        assert outputobject.getMetaClass() != null : "metaobject without metaclass"; // NOI18N

                        final ComponentRegistry reg = ComponentRegistry.getRegistry();

                        final CidsBeanRenderer currentRenderer = reg.getDescriptionPane().currentRenderer();
                        if (currentRenderer == null) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("current renderer is null, won't reflect bean changes to ui"); // NOI18N
                            }
                        } else {
                            final CidsBean currentBean = currentRenderer.getCidsBean();
                            if ((cidsBean.getMetaObject().getClassID() == currentBean.getMetaObject().getClassID())
                                        && (cidsBean.getMetaObject().getID() == currentBean.getMetaObject().getID())) {
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            currentRenderer.setCidsBean(cidsBean);
                                        }
                                    });
                            }
                        }

                        final String reloadId = getReloadId();
                        if (reloadId == null) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("won't perform reload after model run finished, since no id is provided"); // NOI18N
                            }
                        } else {
                            reg.getCatalogueTree().requestRefreshNode(reloadId);
                        }

                        final int answer = JOptionPane.showConfirmDialog(
                                reg.getMainWindow(),
                                NbBundle.getMessage(
                                    AbstractModelManager.class,
                                    "AbstractModelManager.fireFinished().finishedDialog.message", // NOI18N
                                    cidsBean.getProperty("name")), // NOI18N
                                NbBundle.getMessage(
                                    AbstractModelManager.class,
                                    "AbstractModelManager.fireFinished().finishedDialog.title"), // NOI18N
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                        if (JOptionPane.YES_OPTION == answer) {
                            reg.getDescriptionPane().gotoMetaObject(outputobject, "");                  // NOI18N
                        }
                    } catch (final Exception ex) {
                        final String message = "cannot save new values in runbean: " + ex.getMessage(); // NOI18N
                        LOG.error(message, ex);

                        RunInfo runInfo = getRunInfo();
                        if (runInfo == null) {
                            runInfo = new DefaultRunInfo();
                        } else {
                            runInfo.setFinished(false);
                            runInfo.setBroken(true);
                            runInfo.setBrokenMessage(message);
                        }

                        try {
                            cidsBean.setProperty("finished", null);

                            final StringWriter writer = new StringWriter();
                            final ObjectMapper mapper = new ObjectMapper();
                            mapper.writeValue(writer, runInfo);
                            cidsBean.setProperty("runinfo", writer.toString());
                        } catch (final Exception exp) {
                            LOG.error("could not set run info: " + exp.getMessage(), exp);
                        }

                        throw new IllegalStateException(message, ex);
                    }

                    progressSupport.fireEvent(new ProgressEvent(
                            AbstractModelManager.this,
                            ProgressEvent.State.FINISHED));
                }
            };

        progressDispatcher.submit(fireFinished);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    protected abstract CidsBean createOutputBean() throws IOException;

    /**
     * This method shall return an artificial id that can be used to reload the appropriate node in the catalogue tree
     * that is related to this model run. So the underlying implementation is aware of its relative position in the
     * catalogue tree and thus can generate the appropriate artificial id that shall be reloaded.
     *
     * @return  the artificial id used to reload the catalogue tree or null if there is nothing to reload
     */
    protected abstract String getReloadId();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected boolean isStarted() {
        final Timestamp ts = (Timestamp)cidsBean.getProperty("started"); // NOI18N

        return ts != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected boolean isFinished() {
        final Timestamp ts = (Timestamp)cidsBean.getProperty("finished"); // NOI18N

        return ts != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected boolean isCanceled() {
        final RunInfo runInfo = this.getRunInfo();
        return (runInfo != null) ? runInfo.isCanceled() : false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected boolean isBroken() {
        final RunInfo runInfo = this.getRunInfo();
        return (runInfo != null) ? runInfo.isBroken() : false;
    }

    @Override
    public RunInfo getRunInfo() {
        return SMSUtils.getRunInfo(cidsBean, DefaultRunInfo.class);
    }
}
