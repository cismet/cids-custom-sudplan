/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import java.io.IOException;

import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface Manager extends CidsBeanStore, UIProvider {

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns the unified resource that is attached to this <code>Manager</code>. A unified resource can be anything
     * that is essential for the specific <code>Manager</code> implementation.
     *
     * @return  the unified resource attachted to this <code>Manager</code> or <code>null</code> if there is none.
     *
     * @throws  IOException  if any error occurs during execution
     */
    Object getUR() throws IOException;

    /**
     * This method shall be used to signalise a <code>Manager</code> to signalise the manager that it is about to be
     * unloaded and possible changes should be applied and cleanup shall be done. An implementing <code>Manager</code>
     * shall be in an consistent/persistent state after this method has returned.
     *
     * @throws  IOException  if any error occurs during execution
     */
    void finalise() throws IOException;

    /**
     * Returns a {@link Feature} that is attached to this <code>Manager</code> or <code>null</code> if this manager has
     * no attached <code>Feature</code>.
     *
     * @return  the attached <code>Feature</code> or <code>null</code>
     *
     * @throws  IOException  if any error occurs during execution
     */
    Feature getFeature() throws IOException;
}
