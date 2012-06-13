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
public final class SimulationOutputManager extends AbstractIOManager<SimulationOutput> {

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Class<SimulationOutput> getIOClass() {
        return SimulationOutput.class;
    }

    @Override
    protected JComponent createUI() {
        return new SimulationOutputManagerUI(this);
    }
}
