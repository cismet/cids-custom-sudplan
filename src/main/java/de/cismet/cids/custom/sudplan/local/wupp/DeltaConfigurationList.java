/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.sudplan.local.wupp;

import javax.swing.event.ListSelectionListener;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public interface DeltaConfigurationList {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void fireConfigsChanged();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    CidsBean getSelectedConfig();

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    void addSelectionListener(ListSelectionListener l);

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    void removeSelectionListener(ListSelectionListener l);
}
