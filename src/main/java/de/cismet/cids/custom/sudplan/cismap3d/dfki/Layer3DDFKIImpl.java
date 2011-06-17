/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d.dfki;

import com.dfki.av.sudplan.io.IOUtils;
import com.dfki.av.sudplan.layer.Layer;
import com.dfki.av.sudplan.layer.LayerManager;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.awt.EventQueue;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import de.cismet.cids.custom.sudplan.ProgressEvent;
import de.cismet.cids.custom.sudplan.ProgressListener;
import de.cismet.cids.custom.sudplan.cismap3d.Layer3D;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = Layer3D.class)
public final class Layer3DDFKIImpl implements Layer3D {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(Layer3DDFKIImpl.class);

    //~ Instance fields --------------------------------------------------------

    // TODO: until the add method won't tell us the new layer object when doing add from file, we need to keep track
    private final transient Map<URI, Layer> added;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Layer3DDFKIImpl object.
     */
    public Layer3DDFKIImpl() {
        added = new HashMap<URI, Layer>();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addLayer(final URI uri) {
        addLayer(uri, null);
    }

    @Override
    public void addLayer(final URI uri, final ProgressListener progressL) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("adding layer from uri: " + uri + " || progressL: " + progressL); // NOI18N
        }

        fireStarted(progressL);

        if (added.containsKey(uri)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("not adding layer from uri since it is already present: " + uri); // NOI18N
            }

            fireFinished(progressL);
        } else {
            // TODO: handle re-removal of the layer while execution is in progress
            new LayerAdder(uri, progressL).execute();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    private void fireStarted(final ProgressListener progressL) {
        if (progressL != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("fire started: " + progressL); // NOI18N
            }

            if (EventQueue.isDispatchThread()) {
                progressL.progress(new ProgressEvent(this, ProgressEvent.State.STARTED));
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            progressL.progress(new ProgressEvent(this, ProgressEvent.State.STARTED));
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    private void fireProgressed(final ProgressListener progressL) {
        if (progressL != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("fire progressed: " + progressL); // NOI18N
            }

            if (EventQueue.isDispatchThread()) {
                progressL.progress(new ProgressEvent(this, ProgressEvent.State.PROGRESSING));
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            progressL.progress(new ProgressEvent(this, ProgressEvent.State.PROGRESSING));
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    private void fireFinished(final ProgressListener progressL) {
        if (progressL != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("fire finished: " + progressL); // NOI18N
            }

            if (EventQueue.isDispatchThread()) {
                progressL.progress(new ProgressEvent(this, ProgressEvent.State.FINISHED));
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            progressL.progress(new ProgressEvent(this, ProgressEvent.State.FINISHED));
                        }
                    });
            }
        }
    }

    @Override
    public void removeLayer(final URI uri) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removing layer from uri: " + uri); // NOI18N
        }

        if (added.containsKey(uri)) {
            final LayerManager lm = Cismap3DDFKI.getInstance().getController().getLayerManager();
            lm.removeLayer(added.remove(uri));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("not removing layer from uri since it is not present: " + uri); // NOI18N
            }
        }
    }

    @Override
    public void removeAllLayers() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removing all layers"); // NOI18N
        }

        added.clear();
        final LayerManager lm = Cismap3DDFKI.getInstance().getController().getLayerManager();
        lm.removeLayers(new ArrayList<Layer>(lm.getLayers()));
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * TODO: replace with appropriate facility in upcoming cismap3d version
     *
     * @version  $Revision$, $Date$
     */
    private final class LayerAdder extends SwingWorker<Layer, Void> {

        //~ Instance fields ----------------------------------------------------

        private final transient URI uri;

        private final transient ProgressListener progressL;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LayerAdder object.
         *
         * @param  uri        DOCUMENT ME!
         * @param  progressL  DOCUMENT ME!
         */
        LayerAdder(final URI uri, final ProgressListener progressL) {
            this.uri = uri;
            this.progressL = progressL;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Layer doInBackground() throws Exception {
            fireProgressed(progressL);

            return IOUtils.createLayerFromURL(uri.toURL());
        }

        @Override
        protected void done() {
            try {
                final Layer layer = get();
                Cismap3DDFKI.getInstance().getController().getLayerManager().addLayer(layer);
                added.put(uri, layer);

                fireFinished(progressL);
            } catch (Exception ex) {
                LOG.error("cannot add layer from uri: " + uri, ex); // NOI18N
            }
        }
    }
}
