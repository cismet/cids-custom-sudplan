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
public final class CalibrationInputManager extends AbstractIOManager<CalibrationInput> {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CalibrationInputManager.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Class<CalibrationInput> getIOClass() {
        return CalibrationInput.class;
    }

    @Override
    protected JComponent createUI() {
        try {
            return new CalibrationInputManagerUI(getUR());
        } catch (final IOException ex) {
            final String message = "cannot create ui from ur"; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }
}
