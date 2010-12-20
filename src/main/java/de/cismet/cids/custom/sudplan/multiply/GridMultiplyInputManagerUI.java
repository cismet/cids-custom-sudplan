/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import java.awt.EventQueue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.Grid;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GridMultiplyInputManagerUI extends MultiplyInputManagerUI {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridMultiplyInputManagerUI.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridMultiplyInputManagerUI object.
     *
     * @param  model  DOCUMENT ME!
     */
    public GridMultiplyInputManagerUI(final GridMultiplyInputManager model) {
        super(model);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    void init() {
        if (EventQueue.isDispatchThread()) {
            super.init();
            triggerEnable(true);
            try {
                final StringWriter sw = new StringWriter();
                MultiplyHelper.gridToWriter(sw, ((GridMultiplyInputManager)model).getMultiplierGrid());
                jepMultipliers.setText(sw.toString());
            } catch (final IOException e) {
                LOG.error("cannot initialise components", e); // NOI18N
                jepMultipliers.setText("initialisation failed: " + e);
                triggerEnable(false);
            }
        } else {
            throw new IllegalStateException("initialisation shall be done in EDT"); // NOI18N
        }
    }

    @Override
    void store() throws IOException {
        final StringReader sr = new StringReader(jepMultipliers.getText());
        final Grid grid = MultiplyHelper.gridFromReader(sr);

        final URI location;
        try {
            location = new URI(txtInputLocation.getText());
        } catch (final URISyntaxException ex) {
            LOG.error("invalid uri: " + txtInputLocation.getText(), ex); // NOI18N
            JOptionPane.showMessageDialog(
                this,
                "Invalid URI: "
                        + txtInputLocation.getText(),
                "Error",
                JOptionPane.ERROR_MESSAGE);

            return;
        }

        final int multiplicand;
        try {
            multiplicand = Integer.parseInt(jspMultiplicand.getValue().toString());
        } catch (final NumberFormatException e) {
            LOG.error("invalid multiplicand: " + jspMultiplicand.getValue(), e); // NOI18N
            JOptionPane.showMessageDialog(
                this,
                "Invalid multiplicand: "
                        + jspMultiplicand.getValue(),
                "Error",
                JOptionPane.ERROR_MESSAGE);

            return;
        }

        try {
            model.setLocation(location);
            ((GridMultiplyInputManager)model).setMultiplierGrid(grid);
            model.setMultiplicand(multiplicand);
        } catch (final IOException e) {
            final String message = "cannot set new values"; // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }
}
