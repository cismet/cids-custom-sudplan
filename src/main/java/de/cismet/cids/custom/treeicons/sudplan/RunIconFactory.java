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

import org.apache.log4j.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.cismet.cids.custom.sudplan.DefaultRunInfo;
import de.cismet.cids.custom.sudplan.RunInfo;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunIconFactory implements CidsTreeObjectIconFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunIconFactory.class);

    private static final Icon RUN_BROKEN = new ImageIcon(RunIconFactory.class.getResource(
                "/de/cismet/cids/custom/sudplan/run_broken_16.png"));
    private static final Icon RUN_RUNNING = new ImageIcon(RunIconFactory.class.getResource(
                "/de/cismet/cids/custom/sudplan/run_running_16.png"));
    private static final Icon RUN_FINISHED = new ImageIcon(RunIconFactory.class.getResource(
                "/de/cismet/cids/custom/sudplan/run_finished_16.png"));
    private static final Icon RUN_DEFAULT = new ImageIcon(RunIconFactory.class.getResource(
                "/de/cismet/cids/custom/sudplan/run_16.png"));

    //~ Methods ----------------------------------------------------------------

    @Override
    public Icon getClosedPureNodeIcon(final PureTreeNode ptn) {
        // default icon
        return null;
    }

    @Override
    public Icon getOpenPureNodeIcon(final PureTreeNode ptn) {
        // default icon
        return null;
    }

    @Override
    public Icon getLeafPureNodeIcon(final PureTreeNode ptn) {
        // default icon
        return null;
    }

    @Override
    public Icon getOpenObjectNodeIcon(final ObjectTreeNode otn) {
        return getLeafObjectNodeIcon(otn);
    }

    @Override
    public Icon getClosedObjectNodeIcon(final ObjectTreeNode otn) {
        return getLeafObjectNodeIcon(otn);
    }

    @Override
    public Icon getLeafObjectNodeIcon(final ObjectTreeNode otn) {
        try {
            final CidsBean cidsBean = otn.getMetaObject().getBean();
            final RunInfo runInfo = SMSUtils.getRunInfo(cidsBean, DefaultRunInfo.class);
            if ((cidsBean.getProperty("finished") != null) || runInfo.isFinished()) {
                return RUN_FINISHED;
            } else if (runInfo.isBroken() || runInfo.isCanceled()) {
                return RUN_BROKEN;
            } else if (cidsBean.getProperty("started") != null) {
                return RUN_RUNNING;
            } else {
                return RUN_DEFAULT;
            }
        } catch (final Exception e) {
            LOG.error("cannot create run status icon", e);

            return RUN_DEFAULT;
        }
    }

    @Override
    public Icon getClassNodeIcon(final ClassTreeNode dmtn) {
        // default icon
        return null;
    }
}
