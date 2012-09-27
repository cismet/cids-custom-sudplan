/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.treeicons.sudplan;

import Sirius.navigator.types.treenode.ClassTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;

import org.openide.util.ImageUtilities;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Cismap3dcontentIconFactory implements CidsTreeObjectIconFactory {

    //~ Instance fields --------------------------------------------------------

    private final transient ImageIcon icon;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Cismap3dcontentIconFactory object.
     */
    public Cismap3dcontentIconFactory() {
        icon = ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/cismap3d/home_16.gif", false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Icon getClosedPureNodeIcon(final PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getOpenPureNodeIcon(final PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getLeafPureNodeIcon(final PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getOpenObjectNodeIcon(final ObjectTreeNode otn) {
        return icon;
    }

    @Override
    public Icon getClosedObjectNodeIcon(final ObjectTreeNode otn) {
        return icon;
    }

    @Override
    public Icon getLeafObjectNodeIcon(final ObjectTreeNode otn) {
        return icon;
    }

    @Override
    public Icon getClassNodeIcon(final ClassTreeNode dmtn) {
        return null;
    }
}
