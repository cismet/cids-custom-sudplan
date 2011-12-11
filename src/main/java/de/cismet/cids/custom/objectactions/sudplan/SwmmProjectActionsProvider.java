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

import de.cismet.cids.custom.sudplan.local.linz.wizard.SwmmPlusEtaWizardAction;

import de.cismet.cids.utils.interfaces.CidsBeanAction;
import de.cismet.cids.utils.interfaces.CidsBeanActionsProvider;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmProjectActionsProvider implements CidsBeanActionsProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<CidsBeanAction> getActions() {
        return Arrays.asList(ActionProviderFactory.getCidsBeanAction(SwmmPlusEtaWizardAction.class));
    }
}
