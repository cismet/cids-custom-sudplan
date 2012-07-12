/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.cismap3d.dfki;

import com.dfki.av.sudplan.vis.VisualizationPanel;

import org.openide.util.lookup.ServiceProvider;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import java.net.URI;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.ProgressListener;
import de.cismet.cids.custom.sudplan.cismap3d.DropTarget3D;
import de.cismet.cids.custom.sudplan.cismap3d.Layer3D;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = Layer3D.class)
public final class Layer3DImpl implements Layer3D, DropTarget3D {

    //~ Instance fields --------------------------------------------------------

    private final transient VisualizationPanel visPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Layer3DImpl object.
     */
    public Layer3DImpl() {
        visPanel = Registry3D.getInstance().getVisPanel();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void addLayer(final URI uri) {
        visPanel.addLayer(uri);
    }

    @Override
    public void addLayer(final URI uri, final ProgressListener progressL) {
        visPanel.addProgressListener(visPanel);
    }

    @Override
    public void removeLayer(final URI uri) {
        visPanel.removeLayer(uri);
    }

    @Override
    public void removeAllLayers() {
        visPanel.removeAllLayers();
    }

    @Override
    public JComponent getUI() {
        return visPanel.getLayerPanel();
    }

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        dtde.dropComplete(true);
        visPanel.runVisWiz();
    }
}
