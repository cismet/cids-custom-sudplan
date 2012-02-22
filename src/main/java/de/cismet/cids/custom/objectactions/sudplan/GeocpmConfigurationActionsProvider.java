/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectactions.sudplan;

import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.custom.sudplan.local.wupp.RunGeoCPMWizardAction;

import de.cismet.cids.utils.interfaces.CidsBeanAction;
import de.cismet.cids.utils.interfaces.CidsBeanActionsProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeocpmConfigurationActionsProvider implements CidsBeanActionsProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<CidsBeanAction> getActions() {
        return Arrays.asList(ActionProviderFactory.getCidsBeanAction(RunGeoCPMWizardAction.class));
    }
}
