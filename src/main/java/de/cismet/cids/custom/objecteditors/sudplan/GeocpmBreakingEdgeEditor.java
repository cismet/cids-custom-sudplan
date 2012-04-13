/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.sudplan;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.math.BigDecimal;

import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.logging.Level;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.objecteditors.sudplan.DeltaBreakingEdgeEditor.BEHeightConverter;
import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.tostringconverter.sudplan.DeltaConfigurationToStringConverter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class GeocpmBreakingEdgeEditor extends AbstractCidsBeanRenderer implements EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeocpmBreakingEdgeEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final transient BEHeightConverter heightConv;

    private transient CidsBean currentDeltaBEBean;

    private final transient ListSelectionListener selL;

    private final transient ActionListener newL;

    private final transient DocumentListener cfgNameChangeL;

    private final transient PropertyChangeListener dbeChangedL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnNew = new javax.swing.JButton();
    private final transient de.cismet.cids.custom.objecteditors.sudplan.DeltaBreakingEdgeEditor
        deltaBreakingEdgeEditor = new de.cismet.cids.custom.objecteditors.sudplan.DeltaBreakingEdgeEditor();
    private final transient de.cismet.cids.custom.objecteditors.sudplan.DeltaConfigurationEditor deltaCfgEditor =
        new de.cismet.cids.custom.objecteditors.sudplan.DeltaConfigurationEditor();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
    private final transient javax.swing.JLabel lblHeading = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeading1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeading2 = new javax.swing.JLabel();
    private final transient org.jdesktop.swingx.JXList lstDeltaCfgs = new org.jdesktop.swingx.JXList();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo1 =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo2 =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlBreakingEdge = new de.cismet.tools.gui.RoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlCfg = new de.cismet.tools.gui.RoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlCfgs = new de.cismet.tools.gui.RoundedPanel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GeocpmBreakingEdgeEditor.
     */
    public GeocpmBreakingEdgeEditor() {
        selL = new SelectionListener();
        newL = new NewDeltaCfgListener();
        cfgNameChangeL = new CfgNameChangeListener();
        dbeChangedL = new DBEPropertyChangedListener();

        initComponents();

        heightConv = new BEHeightConverter();

        lstDeltaCfgs.setComparator(new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    if ((o1 == null) && (o2 == null)) {
                        return 0;
                    } else if ((o1 == null) && (o2 != null)) {
                        return -1;
                    } else if ((o1 != null) && (o2 == null)) {
                        return 1;
                    } else {
                        return ((String)o1.getProperty("name")).compareTo((String)o2.getProperty("name")); // NOI18N
                    }
                }
            });
        lstDeltaCfgs.setCellRenderer(new DeltaCfgCellRenderer());

        lstDeltaCfgs.addListSelectionListener(WeakListeners.create(ListSelectionListener.class, selL, lstDeltaCfgs));

        btnNew.addActionListener(WeakListeners.create(ActionListener.class, newL, btnNew));
        deltaCfgEditor.addNameChangeListener(cfgNameChangeL);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlCfgs.setLayout(new java.awt.GridBagLayout());

        panHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setLayout(new java.awt.FlowLayout());

        lblHeading.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading.setText(org.openide.util.NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblHeading.text")); // NOI18N
        panHeadInfo.add(lblHeading);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlCfgs.add(panHeadInfo, gridBagConstraints);

        jScrollPane1.setViewportView(lstDeltaCfgs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        pnlCfgs.add(jScrollPane1, gridBagConstraints);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);

        btnNew.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.btnNew.text")); // NOI18N
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnNew);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 2, 10);
        pnlCfgs.add(jToolBar1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlCfgs, gridBagConstraints);

        pnlCfg.setLayout(new java.awt.GridBagLayout());

        panHeadInfo1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setLayout(new java.awt.FlowLayout());

        lblHeading1.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading1.setText(org.openide.util.NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblHeading1.text")); // NOI18N
        panHeadInfo1.add(lblHeading1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlCfg.add(panHeadInfo1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlCfg.add(deltaCfgEditor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlCfg, gridBagConstraints);

        pnlBreakingEdge.setLayout(new java.awt.GridBagLayout());

        panHeadInfo2.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo2.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo2.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo2.setLayout(new java.awt.FlowLayout());

        lblHeading2.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading2.setText(org.openide.util.NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblHeading2.text")); // NOI18N
        panHeadInfo2.add(lblHeading2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlBreakingEdge.add(panHeadInfo2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlBreakingEdge.add(deltaBreakingEdgeEditor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlBreakingEdge, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    protected void init() {
        try {
            final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "delta_configuration");   // NOI18N
            if (mc == null) {
                throw new IllegalStateException(
                    "illegal domain for this operation, mc 'delta_configuration@SUDPLAN-WUPP' not found"); // NOI18N
            }

            final Integer id = (Integer)cidsBean.getProperty("geocpm_configuration_id.id");         // NOI18N
            if (id == null) {
                throw new IllegalStateException("cannot get geocpm configuration id: " + cidsBean); // NOI18N
            }

            final String query = "select " + mc.getID() + "," + mc.getPrimaryKey() + " from " // NOI18N
                        + mc.getTableName()
                        + " where original_object = " + id;                                   // NOI18N

            final MetaObject[] deltaCfgObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                            query,
                            SMSUtils.DOMAIN_SUDPLAN_WUPP);

            final DefaultListModel model = new DefaultListModel();
            for (final MetaObject mo : deltaCfgObjects) {
                model.addElement(mo.getBean());
            }

            lstDeltaCfgs.setModel(model);
            lstDeltaCfgs.setSortOrder(SortOrder.ASCENDING);

            // sets default values
            setCurrentDeltaBreakingEdge(null);
        } catch (final Exception ex) {
            final String message = "cannot initialise editor"; // NOI18N
            LOG.error(message, ex);

            throw new IllegalStateException(message, ex);
        }
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        // noop
    }

    @Override
    public boolean prepareForSave() {
        // saving is kind of a cfg change as it changed selection to nothing
        beforeDeltaCfgChange((CidsBean)lstDeltaCfgs.getSelectedValue());

        final DefaultListModel dlm = (DefaultListModel)lstDeltaCfgs.getModel();
        final Enumeration<?> e = dlm.elements();

        CidsBean deltaCfgBean = null;
        try {
            while (e.hasMoreElements()) {
                deltaCfgBean = (CidsBean)e.nextElement();
                deltaCfgBean.persist();
            }
        } catch (final Exception ex) {
            final String message = "cannot persist config: " + deltaCfgBean; // NOI18N
            LOG.error(message, ex);

            JXErrorPane.showDialog(
                this,
                new ErrorInfo(
                    "Fehler beim Speichern",
                    "Beim Speichern ist ein Fehler aufgetreten.",
                    "Das Speichern von Änderungskonfiguration '"
                            + deltaCfgBean
                            + " ist fehlgeschlagen.",
                    "EDITOR",
                    ex,
                    Level.WARNING,
                    null));

            return false;
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dbeBean  DOCUMENT ME!
     */
    private void setCurrentDeltaBreakingEdge(final CidsBean dbeBean) {
        if (currentDeltaBEBean != null) {
            currentDeltaBEBean.removePropertyChangeListener(dbeChangedL);
        }

        currentDeltaBEBean = dbeBean;

        if (currentDeltaBEBean != null) {
            currentDeltaBEBean.addPropertyChangeListener(dbeChangedL);
        }

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    deltaBreakingEdgeEditor.setCidsBean(dbeBean);
                }
            };

        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void computeChangeStatus() {
        // TODO: keep track of changes and update appropriately
        cidsBean.setArtificialChangeFlag(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  deltaCfgBean  DOCUMENT ME!
     */
    private void beforeDeltaCfgChange(final CidsBean deltaCfgBean) {
        if (currentDeltaBEBean != null) {
            final String height = heightConv.convertForward((BigDecimal)cidsBean.getProperty("height"));
            final String newHeight = heightConv.convertForward((BigDecimal)currentDeltaBEBean.getProperty("height"));

            if (height.equals(newHeight)) {
                final Collection<CidsBean> dbes = (Collection)deltaCfgBean.getProperty("delta_breaking_edges"); // NOI18N
                dbes.remove(currentDeltaBEBean);
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DBEPropertyChangedListener implements PropertyChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            computeChangeStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CfgNameChangeListener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            computeChangeStatus();

            // FIXME: quick 'n' dirty
            lstDeltaCfgs.repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class NewDeltaCfgListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "delta_configuration");   // NOI18N
                if (mc == null) {
                    throw new IllegalStateException(
                        "illegal domain for this operation, mc 'delta_configuration@SUDPLAN-WUPP' not found"); // NOI18N
                }

                final CidsBean origBean = (CidsBean)cidsBean.getProperty("geocpm_configuration_id");          // NOI18N
                if (origBean == null) {
                    throw new IllegalStateException("cannot get original geocpm configuration: " + cidsBean); // NOI18N
                }

                final CidsBean newCfgBean = mc.getEmptyInstance().getBean();
                newCfgBean.setProperty("original_object", origBean); // NOI18N
                newCfgBean.setProperty("name", "Neue Konfiguration");
                newCfgBean.setProperty("description", "Bitte Beschreibung einfügen");

                ((DefaultListModel)lstDeltaCfgs.getModel()).addElement(newCfgBean);
                lstDeltaCfgs.setSortOrder(SortOrder.ASCENDING);
                lstDeltaCfgs.setSelectedValue(newCfgBean, true);

                computeChangeStatus();
            } catch (final Exception ex) {
                final String message = "cannot create new delta configuration"; // NOI18N
                LOG.error(message, ex);

                throw new IllegalStateException(message, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class SelectionListener implements ListSelectionListener {

        //~ Instance fields ----------------------------------------------------

        private transient CidsBean currentDeltaCfgBean;

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            final CidsBean bean = (CidsBean)lstDeltaCfgs.getSelectedValue();

            final Collection<CidsBean> dbes = (Collection)bean.getProperty("delta_breaking_edges"); // NOI18N

            if (currentDeltaCfgBean != null) {
                beforeDeltaCfgChange(currentDeltaCfgBean);
            }

            if (bean != null) {
                deltaCfgEditor.setCidsBean(bean);

                boolean createNew = true;
                for (final CidsBean dbeBean : dbes) {
                    if (dbeBean.getProperty("original_object.id").equals(cidsBean.getProperty("id"))) { // NOI18N
                        setCurrentDeltaBreakingEdge(dbeBean);
                        createNew = false;

                        break;
                    }
                }

                if (createNew) {
                    final MetaClass mc = ClassCacheMultiple.getMetaClass("SUDPLAN-WUPP", "delta_breaking_edge");   // NOI18N
                    if (mc == null) {
                        throw new IllegalStateException(
                            "illegal domain for this operation, mc 'delta_breaking_edge@SUDPLAN-WUPP' not found"); // NOI18N
                    }

                    try {
                        final CidsBean newBean = mc.getEmptyInstance().getBean();
                        newBean.setProperty("original_object", cidsBean);              // NOI18N
                        newBean.setProperty("height", cidsBean.getProperty("height")); // NOI18N

                        setCurrentDeltaBreakingEdge(newBean);

                        dbes.add(currentDeltaBEBean);
                    } catch (final Exception ex) {
                        final String message = "cannot initialise new delta breaking edge"; // NOI18N
                        LOG.error(message, ex);

                        throw new IllegalStateException(message, ex);
                    }
                }
            }

            currentDeltaCfgBean = bean;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class DeltaCfgCellRenderer extends DefaultListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final transient DeltaConfigurationToStringConverter toString;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DeltaCfgCellRenderer object.
         */
        public DeltaCfgCellRenderer() {
            toString = new DeltaConfigurationToStringConverter();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel) {
                final JLabel l = (JLabel)c;
                l.setText(toString.convert(((CidsBean)value).getMetaObject()));
            }

            return c;
        }
    }
}