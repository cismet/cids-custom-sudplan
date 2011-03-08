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

import java.io.IOException;

import java.sql.Timestamp;

import java.util.GregorianCalendar;

import javax.swing.JComponent;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractModelManager implements ModelManager, Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractModelManager.class);

    //~ Instance fields --------------------------------------------------------

    protected transient CidsBean cidsBean;

    private final transient ProgressSupport progressSupport;

    private transient volatile JComponent ui;

    private transient Thread finisher;

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

        // execution
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

    @Override
    public void apply() throws IOException {
        // default gui does not alter the bean, nothing has to be applied
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
        progressSupport.removeProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    protected void fireStarted() {
        if (isStarted()) {
            return;
        }

        final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
        try {
            cidsBean.setProperty("started", dateConverter.convertReverse(GregorianCalendar.getInstance().getTime())); // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot set started flag in runbean";                                              // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        progressSupport.fireEvent(new ProgressEvent(ProgressEvent.State.STARTED));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  step      percent DOCUMENT ME!
     * @param  maxSteps  DOCUMENT ME!
     */
    protected void fireProgressed(final int step, final int maxSteps) {
        assert isStarted() : "cannot progress when not started yet"; // NOI18N

        if (isFinished()) {
            return;
        }

        progressSupport.fireEvent(new ProgressEvent(ProgressEvent.State.PROGRESSING, step, maxSteps));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    protected void fireFinised() {
        if (isFinished()) {
            return;
        }

        final SqlTimestampToUtilDateConverter dateConverter = new SqlTimestampToUtilDateConverter();
        try {
            cidsBean.setProperty("finished", dateConverter.convertReverse(GregorianCalendar.getInstance().getTime())); // NOI18N
            cidsBean.setProperty("modeloutput", createOutputBean());                                                   // NOI18N
            cidsBean = cidsBean.persist();

//            SMSUtils.reloadCatalogTree();

            final MetaObject outputobject = ((CidsBean)cidsBean.getProperty("modeloutput")).getMetaObject(); // NOI18N

            assert outputobject != null : "bean without metaobject";                     // NOI18N
            assert outputobject.getMetaClass() != null : "metaobject without metaclass"; // NOI18N

            ComponentRegistry.getRegistry()
                    .getDescriptionPane()
                    .gotoMetaObject(outputobject.getMetaClass(), outputobject.getID(), ""); // NOI18N
        } catch (final Exception ex) {
            final String message = "cannot save new values in runbean";                     // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        progressSupport.fireEvent(new ProgressEvent(ProgressEvent.State.FINISHED));
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

    @Override
    public void dispose() {
        if (finisher != null) {
            finisher.interrupt();
        }
    }
}
