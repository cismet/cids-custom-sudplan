/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import org.apache.log4j.Logger;

import java.io.IOException;

import de.cismet.cids.custom.sudplan.AbstractModelManager;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaModelManager extends AbstractModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaModelManager.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void internalExecute() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected CidsBean createOutputBean() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String getReloadId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
