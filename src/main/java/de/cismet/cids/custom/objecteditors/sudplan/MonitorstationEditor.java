/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.sudplan;

import Sirius.navigator.tools.MetaObjectChangeEvent;
import Sirius.navigator.tools.MetaObjectChangeSupport;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.MonitorstationContext;
import de.cismet.cids.custom.sudplan.Variable;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class MonitorstationEditor extends AbstractCidsBeanRenderer implements EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(MonitorstationEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final transient CreateCtxListener ctxL;

    private transient MetaObject oldMo;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JComboBox cboContext = new javax.swing.JComboBox();
    private final transient de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor cboGeometry =
        new de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor();
    private final transient javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
            new java.awt.Dimension(0, 0),
            new java.awt.Dimension(32767, 0));
    private final transient javax.swing.Box.Filler filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
            new java.awt.Dimension(0, 0),
            new java.awt.Dimension(0, 32767));
    private final transient javax.swing.JLabel lblContext = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblGeometry = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlContent = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlVariables = new javax.swing.JPanel();
    private final transient javax.swing.JTextField txtName = new javax.swing.JTextField();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MonitorstationEditor.
     */
    public MonitorstationEditor() {
        this.ctxL = new CreateCtxListener();

        initComponents();

        initVariables();
        initContext();

        cboContext.addItemListener(WeakListeners.create(ItemListener.class, ctxL, cboContext));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void initVariables() {
        final Variable[] vars = Variable.values();
        final GridLayout varLayout = new GridLayout(Math.round(vars.length / 2.0f), 2, 5, 5);
        pnlVariables.setLayout(varLayout);
        for (final Variable var : vars) {
            final VarCheckBox box = new VarCheckBox(var);
            box.setContentAreaFilled(false);
            box.addItemListener(WeakListeners.create(ItemListener.class, ctxL, cboContext));
            pnlVariables.add(box);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initContext() {
        cboContext.removeAllItems();
        for (final MonitorstationContext context : MonitorstationContext.values()) {
            if (context.isCSContext()) {
                cboContext.addItem(context);
            }
        }
        cboContext.setRenderer(new ContextRenderer());
    }

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

        filler1.setMaximumSize(new java.awt.Dimension(32767, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        add(filler1, gridBagConstraints);

        filler2.setMaximumSize(new java.awt.Dimension(0, 32767));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        add(filler2, gridBagConstraints);

        pnlContent.setOpaque(false);
        pnlContent.setLayout(new java.awt.GridBagLayout());

        lblName.setText(NbBundle.getMessage(MonitorstationEditor.class, "MonitorstationEditor.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlContent.add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlContent.add(txtName, gridBagConstraints);

        lblGeometry.setText(NbBundle.getMessage(MonitorstationEditor.class, "MonitorstationEditor.lblGeometry.text")); // NOI18N
        lblGeometry.setName(NbBundle.getMessage(MonitorstationEditor.class, "MonitorstationEditor.lblGeometry.name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlContent.add(lblGeometry, gridBagConstraints);

        cboGeometry.setName(NbBundle.getMessage(MonitorstationEditor.class, "MonitorstationEditor.cboGeometry.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geom}"),
                cboGeometry,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(cboGeometry.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlContent.add(cboGeometry, gridBagConstraints);

        pnlVariables.setBorder(javax.swing.BorderFactory.createTitledBorder(
                NbBundle.getMessage(MonitorstationEditor.class, "MonitorstationEditor.pnlVariables.border.title"))); // NOI18N
        pnlVariables.setToolTipText(NbBundle.getMessage(
                MonitorstationEditor.class,
                "MonitorstationEditor.pnlVariables.toolTipText"));                                                   // NOI18N
        pnlVariables.setOpaque(false);

        final org.jdesktop.layout.GroupLayout pnlVariablesLayout = new org.jdesktop.layout.GroupLayout(pnlVariables);
        pnlVariables.setLayout(pnlVariablesLayout);
        pnlVariablesLayout.setHorizontalGroup(
            pnlVariablesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                0,
                251,
                Short.MAX_VALUE));
        pnlVariablesLayout.setVerticalGroup(
            pnlVariablesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlContent.add(pnlVariables, gridBagConstraints);

        lblContext.setText(NbBundle.getMessage(MonitorstationEditor.class, "MonitorstationEditor.lblContext.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlContent.add(lblContext, gridBagConstraints);

        cboContext.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Air Quality", "Hydrology", "Rainfall" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlContent.add(cboContext, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    @Override
    protected void init() {
        oldMo = cidsBean.getMetaObject();

        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
            bindingGroup,
            cidsBean);
        bindingGroup.unbind();
        bindingGroup.bind();

        initType();
    }

    /**
     * DOCUMENT ME!
     */
    private void initType() {
        final String type = (String)cidsBean.getProperty("type"); // NOI18N
        clearCheckboxes();

        if (type != null) {
            if ("R".equals(type)) {
                LOG.warn("old monitor station type: " + cidsBean);

                return;
            }

            final String[] split = type.split(":", 2); // NOI18N

            assert split.length == 2 : "illegal type definition (token): " + type; // NOI18N

            final String ctxKey = split[0];
            boolean ctxSet = false;
            for (final MonitorstationContext context : MonitorstationContext.values()) {
                if (context.getKey().equals(ctxKey)) {
                    cboContext.setSelectedItem(context);
                    ctxSet = true;
                    break;
                }
            }

            assert ctxSet : "illegal type definition (context): " + type; // NOI18N

            final String[] vars = split[1].split(","); // NOI18N
            for (final String var : vars) {
                setVarSelected(Variable.getVariable(var), true);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   var       DOCUMENT ME!
     * @param   selected  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private boolean setVarSelected(final Variable var, final boolean selected) {
        for (final Component c : pnlVariables.getComponents()) {
            if (c instanceof VarCheckBox) {
                final VarCheckBox box = (VarCheckBox)c;
                if (box.getVar().equals(var)) {
                    box.setSelected(selected);

                    return true;
                }
            }
        }

        throw new IllegalArgumentException("var not present: " + var); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<Variable> getSelectedVars() {
        final List<Variable> vars = new ArrayList<Variable>();
        for (final Component c : pnlVariables.getComponents()) {
            if (c instanceof VarCheckBox) {
                final VarCheckBox box = (VarCheckBox)c;
                if (box.isSelected()) {
                    vars.add(box.getVar());
                }
            }
        }

        return vars;
    }

    /**
     * DOCUMENT ME!
     */
    private void clearCheckboxes() {
        for (final Component c : pnlVariables.getComponents()) {
            if (c instanceof JCheckBox) {
                final JCheckBox box = (JCheckBox)c;
                box.setSelected(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @Override
    public void editorClosed(final EditorClosedEvent event) {
        if (EditorSaveStatus.SAVE_SUCCESS == event.getStatus()) {
            final MetaObject newMo = event.getSavedBean().getMetaObject();

            // FIXME: this is not ok, the old mo is currently not present anymore, however, in our case everything
            // works as expected
            if (LOG.isDebugEnabled()) {
                LOG.debug("old metaobject id = " + oldMo.getID()); // NOI18N
            }

            if (oldMo.getID() < 0) {
                final MetaObjectChangeEvent moce = new MetaObjectChangeEvent(this, null, newMo);
                MetaObjectChangeSupport.getDefault().fireMetaObjectAdded(moce);
            } else {
                final MetaObjectChangeEvent moce = new MetaObjectChangeEvent(this, oldMo, newMo);
                MetaObjectChangeSupport.getDefault().fireMetaObjectChanged(moce);
            }

            // TODO: soft refresh when the editor won't do a hard refresh anymore
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean prepareForSave() {
        if (cboGeometry.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a geometry",
                "Missing geometry",
                JOptionPane.INFORMATION_MESSAGE);

            return false;
        }

        return true;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CreateCtxListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            createCtx();

            if ((e.getSource() instanceof JCheckBox) && (cidsBean.getProperty("type") == null)) {
                // we have to use invoke later in the edt because the dialog would cause a selection event to be thrown 
                // again, resulting in a checkbox that cannot be deselected
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(
                                MonitorstationEditor.this,
                                "Please select at least one variable",
                                "Missing variable",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void createCtx() {
            final StringBuilder typeSb = new StringBuilder();

            final MonitorstationContext mCtx = (MonitorstationContext)cboContext.getSelectedItem();
            typeSb.append(mCtx.getKey());
            typeSb.append(':');

            final List<Variable> selectedVars = getSelectedVars();
            if (selectedVars.isEmpty()) {
                try {
                    cidsBean.setProperty("type", null);      // NOI18N
                } catch (final Exception ex) {
                    LOG.error("cannot set type string", ex); // NOI18N
                    final ErrorInfo info = new ErrorInfo(
                            "Error",
                            "Error while setting type property",
                            null,
                            "Error",
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(MonitorstationEditor.this, info);
                }

                return;
            }

            for (final Variable var : selectedVars) {
                typeSb.append(var.getPropertyKey());
                typeSb.append(',');
            }

            typeSb.deleteCharAt(typeSb.length() - 1);
            try {
                cidsBean.setProperty("type", typeSb.toString()); // NOI18N
            } catch (final Exception ex) {
                LOG.error("cannot set type string", ex);         // NOI18N
                final ErrorInfo info = new ErrorInfo(
                        "Error",
                        "Error while setting type property",
                        null,
                        "Error",
                        ex,
                        Level.SEVERE,
                        null);
                JXErrorPane.showDialog(MonitorstationEditor.this, info);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ContextRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   list          DOCUMENT ME!
         * @param   value         DOCUMENT ME!
         * @param   index         DOCUMENT ME!
         * @param   isSelected    DOCUMENT ME!
         * @param   cellHasFocus  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (c instanceof JLabel) {
                final JLabel label = (JLabel)c;
                final String name = ((MonitorstationContext)value).getLocalisedName();
                label.setText(name);
            }

            return c;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class VarCheckBox extends JCheckBox {

        //~ Instance fields ----------------------------------------------------

        private final transient Variable var;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VarCheckBox object.
         *
         * @param  var  DOCUMENT ME!
         */
        public VarCheckBox(final Variable var) {
            super(var.getLocalisedName());

            this.var = var;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Variable getVar() {
            return var;
        }
    }
}
