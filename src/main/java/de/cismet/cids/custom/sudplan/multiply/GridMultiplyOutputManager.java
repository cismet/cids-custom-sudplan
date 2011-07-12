/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Grid;
import de.cismet.cids.custom.sudplan.ImmutableGrid;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.RunHelper;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cismap.commons.features.DefaultRasterDocumentFeature;
import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GridMultiplyOutputManager implements Manager, Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridMultiplyOutputManager.class);

    //~ Instance fields --------------------------------------------------------

    protected transient GridMultiplyOutputManagerUI ui;

    private transient CidsBean cidsBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridMultiplyOutputManager object.
     */
    public GridMultiplyOutputManager() {
        ui = new GridMultiplyOutputManagerUI(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Creates a new MultiplyOutputManager object.
     *
     * @return  DOCUMENT ME!
     */

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getUI() {
        return ui;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Override
    public URI getUR() throws IOException {
        try {
            return new URI((String)cidsBean.getProperty("uri")); // NOI18N
        } catch (final Exception e) {
            final String message = "cannot create uri";          // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    @Override
    public void finalise() throws IOException {
        // NOOP
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        ui.init();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    Grid getMultiplierGrid() throws IOException {
        final Grid grid;
        final File multipliersInput = new File(getUR());

        if (multipliersInput == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("no multipliers file found, multipliers empty"); // NOI18N
            }
            grid = new ImmutableGrid(null);
        } else {
            grid = MultiplyHelper.gridFromFile(multipliersInput);
        }

        return grid;
    }

    @Override
    public void dispose() {
        ui.dispose();
    }

    @Override
    public Feature getFeature() throws IOException {
        final Grid grid = getMultiplierGrid();

        if (grid.getGeometry() == null) {
            return null;
        } else {
//            final BufferedImage image = SMSUtils.toBufferedImage(RunHelper.gridToImage(grid, 0));

            return new DefaultRasterDocumentFeature(null, grid.getGeometry());
        }
    }
}
