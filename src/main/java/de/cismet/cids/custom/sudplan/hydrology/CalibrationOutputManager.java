/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.apache.log4j.Logger;

import java.io.IOException;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.AbstractIOManager;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CalibrationOutputManager extends AbstractIOManager<CalibrationOutput> {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CalibrationOutputManager.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Class<CalibrationOutput> getIOClass() {
        return CalibrationOutput.class;
    }

    @Override
    protected JComponent createUI() {
        try {
            return new CalibrationOutputManagerUI(this);
        } catch (final IOException ex) {
            final String message = "cannot create ui from ur"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }
}
