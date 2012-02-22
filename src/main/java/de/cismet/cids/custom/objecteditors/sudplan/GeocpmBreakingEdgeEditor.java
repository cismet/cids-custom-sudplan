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

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
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

    private final transient DocumentListener dbeChangedL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnNew = new javax.swing.JButton();
    private final transient de.cismet.cids.custom.objecteditors.sudplan.DeltaConfigurationEditor deltaCfgEditor =
        new de.cismet.cids.custom.objecteditors.sudplan.DeltaConfigurationEditor();
    private final transient javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    private final transient javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
    private final transient javax.swing.JLabel lblCm1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCm2 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeading = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeading1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeading2 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblOrigHeight = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblOrigHeightValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblType = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblTypeValue = new javax.swing.JLabel();
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
    private final transient javax.swing.JSlider sldHeight = new javax.swing.JSlider();
    private final transient javax.swing.JTextArea txaDescription = new javax.swing.JTextArea();
    private final transient javax.swing.JTextField txtName = new javax.swing.JTextField();
    private final transient javax.swing.JTextField txtNewHeight = new javax.swing.JTextField();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GeocpmBreakingEdgeEditor.
     */
    public GeocpmBreakingEdgeEditor() {
        selL = new SelectionListener();
        newL = new NewDeltaCfgListener();
        cfgNameChangeL = new CfgNameChangeListener();
        dbeChangedL = new DBEChangedListener();

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
        txtNewHeight.getDocument().addDocumentListener(WeakListeners.document(dbeChangedL, txtNewHeight.getDocument()));
        txtName.getDocument().addDocumentListener(WeakListeners.document(dbeChangedL, txtName.getDocument()));
        txaDescription.getDocument()
                .addDocumentListener(WeakListeners.document(dbeChangedL, txaDescription.getDocument()));
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

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
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
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
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
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
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlBreakingEdge.add(panHeadInfo2, gridBagConstraints);

        lblName.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBreakingEdge.add(lblName, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBreakingEdge.add(lblDescription, gridBagConstraints);

        lblType.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.lblType.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBreakingEdge.add(lblType, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.type}"),
                lblTypeValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        binding.setConverter(new BETypeConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBreakingEdge.add(lblTypeValue, gridBagConstraints);

        lblOrigHeight.setText(NbBundle.getMessage(
                GeocpmBreakingEdgeEditor.class,
                "GeocpmBreakingEdgeEditor.lblOrigHeight.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBreakingEdge.add(lblOrigHeight, gridBagConstraints);

        lblOrigHeightValue.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblOrigHeightValue.setMaximumSize(new java.awt.Dimension(50, 16));
        lblOrigHeightValue.setMinimumSize(new java.awt.Dimension(50, 16));
        lblOrigHeightValue.setPreferredSize(new java.awt.Dimension(50, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.height}"),
                lblOrigHeightValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<not set>");
        binding.setSourceUnreadableValue("<unreadable>");
        binding.setConverter(new BEHeightConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 3);
        pnlBreakingEdge.add(lblOrigHeightValue, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setRows(5);
        jScrollPane2.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBreakingEdge.add(jScrollPane2, gridBagConstraints);

        txtName.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.txtName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBreakingEdge.add(txtName, gridBagConstraints);

        jLabel1.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        pnlBreakingEdge.add(jLabel1, gridBagConstraints);

        txtNewHeight.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNewHeight.setMaximumSize(new java.awt.Dimension(50, 28));
        txtNewHeight.setMinimumSize(new java.awt.Dimension(50, 28));
        txtNewHeight.setPreferredSize(new java.awt.Dimension(50, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sldHeight,
                org.jdesktop.beansbinding.ELProperty.create("${value}"),
                txtNewHeight,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 0);
        pnlBreakingEdge.add(txtNewHeight, gridBagConstraints);

        sldHeight.setMajorTickSpacing(20);
        sldHeight.setMaximum(500);
        sldHeight.setMinorTickSpacing(10);
        sldHeight.setPaintTicks(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 1);
        pnlBreakingEdge.add(sldHeight, gridBagConstraints);

        lblCm1.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.lblCm1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBreakingEdge.add(lblCm1, gridBagConstraints);

        lblCm2.setText(NbBundle.getMessage(GeocpmBreakingEdgeEditor.class, "GeocpmBreakingEdgeEditor.lblCm2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBreakingEdge.add(lblCm2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlBreakingEdge, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    protected void init() {
        bindingGroup.unbind();
        bindingGroup.bind();

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
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(), query);

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
        this.currentDeltaBEBean = dbeBean;

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    if (dbeBean == null) {
                        txtName.setText("<Bitte Konfigration auswählen>");
                        txaDescription.setText("<Bitte Konfiguration auswählen>");
                        txtNewHeight.setText("<0>");
                    } else {
                        final BigDecimal height = (BigDecimal)dbeBean.getProperty("height"); // NOI18N
                        txtNewHeight.setText(((height == null) ? "0"
                                                               : String.valueOf(heightConv.convertForward(height))));
                        txtName.setText((String)dbeBean.getProperty("name"));                // NOI18N
                        txaDescription.setText((String)dbeBean.getProperty("description"));  // NOI18N
                    }
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
            final String height = lblOrigHeightValue.getText();
            final String newHeight = txtNewHeight.getText();

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
    private final class DBEChangedListener implements DocumentListener {

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
            if (currentDeltaBEBean != null) {
                // FIXME: rather expensive update impl, be more efficient in case of performance issues
                try {
                    // no binding right now, have to do it manually

                    // it is intentional to use == instead of equals()
                    if (txtNewHeight.getDocument() == e.getDocument()) {
                        currentDeltaBEBean.setProperty("height", heightConv.convertReverse(txtNewHeight.getText())); // NOI18N
                    } else if (txaDescription.getDocument() == e.getDocument()) {
                        currentDeltaBEBean.setProperty("description", txaDescription.getText());                     // NOI18N
                    } else if (txtName.getDocument() == e.getDocument()) {
                        currentDeltaBEBean.setProperty("name", txtName.getText());                                   // NOI18N
                    } else {
                        LOG.warn("document listener was triggered, but no known component association: " + e);
                    }
                } catch (final Exception ex) {
                    final String message = "cannot set new bean values";                                             // NOI18N
                    LOG.warn(message, ex);
                    throw new IllegalStateException(message, ex);
                }

                computeChangeStatus();
            }
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
    public static final class BETypeConverter extends Converter<Integer, String> {

        //~ Methods ------------------------------------------------------------

        @Override
        public String convertForward(final Integer value) {
            if (value == 0) {
                return "Gehwegbruchkante";
            } else if (value == 1) {
                return "Häuserbruchkante";
            } else {
                throw new IllegalStateException("unknown breaking edge type"); // NOI18N
            }
        }

        @Override
        public Integer convertReverse(final String value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class BEHeightConverter extends Converter<BigDecimal, String> {

        //~ Methods ------------------------------------------------------------

        @Override
        public String convertForward(final BigDecimal value) {
            return String.valueOf(value.multiply(new BigDecimal(100)).intValue());
        }

        @Override
        public BigDecimal convertReverse(final String value) {
            if ((value == null) || value.isEmpty()) {
                return new BigDecimal(0);
            } else {
                return new BigDecimal(value).divide(new BigDecimal(100));
            }
        }
    }

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
