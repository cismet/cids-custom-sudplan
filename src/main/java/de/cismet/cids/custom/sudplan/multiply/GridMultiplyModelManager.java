/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import de.cismet.cids.custom.sudplan.Grid;
import de.cismet.cids.custom.sudplan.ImmutableGrid;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GridMultiplyModelManager extends MultiplyModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridMultiplyModelManager.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void execute() throws IOException {
        fireStarted();
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("interrupted", ex);
            }
        }

        final File inputFolder;
        try {
            inputFolder = new File(new URI(getInputFolderURI()));
        } catch (final URISyntaxException ex) {
            final String message = "cannot create file from input folder uri: " + getInputFolderURI(); // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        final File multipliersInput = new File(inputFolder, MultiplyInputManager.FILENAME_MULTIPLIERS);
        final Grid input = MultiplyHelper.gridFromFile(multipliersInput);
        final Double[][] multipliers = input.getData();

        final int maxSteps = multipliers.length + 2;
        fireProgressed(1, maxSteps);

        final File multiplicandInput = new File(inputFolder, MultiplyInputManager.FILENAME_MULTIPLICAND);
        final int multiplicand = MultiplyHelper.intFromFile(multiplicandInput);

        final Double[][] results = new Double[multipliers.length][];
        for (int i = 0; i < multipliers.length; ++i) {
            results[i] = new Double[multipliers[i].length];
            for (int j = 0; j < multipliers[i].length; ++j) {
                results[i][j] = multipliers[i][j] * multiplicand;
            }
            fireProgressed(i + 1, maxSteps);
//            try {
//                Thread.sleep(1000);
//            } catch (final InterruptedException ex) {
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("interrupted", ex); // NOI18N
//                }
//            }
        }

        final File outputFile = new File(getUR());
        MultiplyHelper.gridToFile(outputFile, new ImmutableGrid(results, input.getGeometry()));
        fireProgressed(maxSteps, maxSteps);
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("interrupted", ex); // NOI18N
            }
        }

        fireFinised();
    }
}
