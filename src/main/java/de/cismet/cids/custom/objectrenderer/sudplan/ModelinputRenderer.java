/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import de.cismet.cids.custom.sudplan.ManagerType;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class ModelinputRenderer extends AbstractManagerRenderer {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ModelinputRenderer object.
     */
    public ModelinputRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setOpaque(false);
        setPreferredSize(null);
        setLayout(new java.awt.BorderLayout());
    } // </editor-fold>//GEN-END:initComponents

    @Override
    protected ManagerType getType() {
        return ManagerType.INPUT;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
