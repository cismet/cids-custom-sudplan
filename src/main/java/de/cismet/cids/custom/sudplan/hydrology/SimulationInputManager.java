/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.AbstractIOManager;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SimulationInputManager extends AbstractIOManager<SimulationInput> {

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Class<SimulationInput> getIOClass() {
        return SimulationInput.class;
    }

    @Override
    protected JComponent createUI() {
        return new SimulationInputManagerUI(this);
    }
}
