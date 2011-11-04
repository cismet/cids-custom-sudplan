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

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Validator;
import org.jdesktop.beansbinding.Validator.Result;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class ModelEditor extends javax.swing.JPanel implements CidsBeanRenderer, EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ModelEditor.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean cidsBean;

    private transient String title;
    private final transient FocusListener selL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblInputManagerClass;
    private javax.swing.JLabel lblModelManagerClass;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOutputManagerClass;
    private javax.swing.JPanel pnlManagers;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtInputManagerClass;
    private javax.swing.JTextField txtModelManagerClass;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOutputManagerClass;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form ModelEditor.
     */
    public ModelEditor() {
        this.selL = new SelectTextListener();

        initComponents();

        txtInputManagerClass.addFocusListener(WeakListeners.create(FocusListener.class, selL, txtInputManagerClass));
        txtModelManagerClass.addFocusListener(WeakListeners.create(FocusListener.class, selL, txtModelManagerClass));
        txtOutputManagerClass.addFocusListener(WeakListeners.create(FocusListener.class, selL, txtOutputManagerClass));
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

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        pnlManagers = new javax.swing.JPanel();
        lblInputManagerClass = new javax.swing.JLabel();
        txtInputManagerClass = new javax.swing.JTextField();
        lblModelManagerClass = new javax.swing.JLabel();
        txtModelManagerClass = new javax.swing.JTextField();
        lblOutputManagerClass = new javax.swing.JLabel();
        txtOutputManagerClass = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblName.setText(NbBundle.getMessage(ModelEditor.class, "ModelEditor.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtName, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(ModelEditor.class, "ModelEditor.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDescription, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.description}"),
                txtDescription,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtDescription, gridBagConstraints);

        pnlManagers.setBorder(javax.swing.BorderFactory.createTitledBorder(
                NbBundle.getMessage(ModelEditor.class, "ModelEditor.pnlManagers.border.title"))); // NOI18N
        pnlManagers.setOpaque(false);
        pnlManagers.setLayout(new java.awt.GridBagLayout());

        lblInputManagerClass.setText(NbBundle.getMessage(ModelEditor.class, "ModelEditor.lblInputManagerClass.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlManagers.add(lblInputManagerClass, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.inputmanager.definition}"),
                txtInputManagerClass,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<enter fully qualified class name>");
        binding.setValidator(new ManagerValidator(txtInputManagerClass));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlManagers.add(txtInputManagerClass, gridBagConstraints);

        lblModelManagerClass.setText(NbBundle.getMessage(ModelEditor.class, "ModelEditor.lblModelManagerClass.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlManagers.add(lblModelManagerClass, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.modelmanager.definition}"),
                txtModelManagerClass,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<enter fully qualified class name>");
        binding.setValidator(new ManagerValidator(txtModelManagerClass));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlManagers.add(txtModelManagerClass, gridBagConstraints);

        lblOutputManagerClass.setText(NbBundle.getMessage(ModelEditor.class, "ModelEditor.lblOutputManagerClass.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlManagers.add(lblOutputManagerClass, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.outputmanager.definition}"),
                txtOutputManagerClass,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<enter fully qualified class name>");
        binding.setValidator(new ManagerValidator(txtOutputManagerClass));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlManagers.add(txtOutputManagerClass, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlManagers, gridBagConstraints);

        jPanel1.setOpaque(false);

        final org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 500, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 100, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
            bindingGroup,
            cidsBean);
        bindingGroup.unbind();
        bindingGroup.bind();

        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, "manager");        // NOI18N
        if (cidsBean.getProperty("inputmanager") == null) {                             // NOI18N
            try {
                cidsBean.setProperty("inputmanager", mc.getEmptyInstance().getBean());  // NOI18N
            } catch (final Exception ex) {
                final String message = "cannot create empty manager object";            // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }
        }
        if (cidsBean.getProperty("modelmanager") == null) {                             // NOI18N
            try {
                cidsBean.setProperty("modelmanager", mc.getEmptyInstance().getBean());  // NOI18N
            } catch (final Exception ex) {
                final String message = "cannot create empty manager object";            // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }
        }
        if (cidsBean.getProperty("outputmanager") == null) {                            // NOI18N
            try {
                cidsBean.setProperty("outputmanager", mc.getEmptyInstance().getBean()); // NOI18N
            } catch (final Exception ex) {
                final String message = "cannot create empty manager object";            // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }
        }
    }

    @Override
    public void dispose() {
        // ignore
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        // noop
    }

    @Override
    public boolean prepareForSave() {
        final CidsBean inputM = (CidsBean)cidsBean.getProperty("inputmanager");   // NOI18N
        final CidsBean modelM = (CidsBean)cidsBean.getProperty("modelmanager");   // NOI18N
        final CidsBean outputM = (CidsBean)cidsBean.getProperty("outputmanager"); // NOI18N

        final ManagerValidator val = new ManagerValidator();
        try {
            final String inDef = (String)inputM.getProperty("definition");
            final String moDef = (String)modelM.getProperty("definition");
            final String outDef = (String)outputM.getProperty("definition");

            if ((val.validate(inDef) != null) || (val.validate(moDef) != null) || (val.validate(outDef) != null)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Correct invalid class names before saving!",
                    "Invalid manager",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }

            inputM.setProperty("type", "JAVA");  // NOI18N
            modelM.setProperty("type", "JAVA");  // NOI18N
            outputM.setProperty("type", "JAVA"); // NOI18N

            final String domain = SessionManager.getSession().getUser().getDomain();
            final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, "category");
            final CidsBean catBean = SessionManager.getProxy().getMetaObject(1, mc.getID(), domain).getBean();
            cidsBean.setProperty("category", catBean); // NOI18N
        } catch (final Exception ex) {
            LOG.error("error preparing for save", ex); // NOI18N
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
    private static final class ManagerValidator extends Validator<String> {

        //~ Instance fields ----------------------------------------------------

        private final transient Pattern pattern = Pattern.compile("([a-z][\\w]*(\\.[a-z][\\w]*)*\\.)?[A-Z][\\w]*"); // NOI18N

        private final transient JTextField toBeValidated;
        private final transient Color defaultColor = UIManager.getColor("TextField.background"); // NOI18N
        private final transient Color errorColor = Color.RED;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ManagerValidator object.
         */
        public ManagerValidator() {
            this(null);
        }

        /**
         * Creates a new ManagerValidator object.
         *
         * @param  toBeValidated  DOCUMENT ME!
         */
        public ManagerValidator(final JTextField toBeValidated) {
            this.toBeValidated = toBeValidated;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Result validate(final String value) {
            if ((value != null) && pattern.matcher(value).matches()) {
                if (toBeValidated != null) {
                    toBeValidated.setBackground(defaultColor);
                }

                return null;
            } else {
                if (toBeValidated != null) {
                    toBeValidated.setBackground(errorColor);
                }

                return new Result(null, "You entered an invalid class name: " + value);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class SelectTextListener implements FocusListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void focusGained(final FocusEvent e) {
            if (e.getComponent() instanceof JTextField) {
                final JTextField txt = (JTextField)e.getComponent();
                txt.setSelectionStart(0);
                txt.setSelectionEnd(txt.getText().length());
            }
        }

        @Override
        public void focusLost(final FocusEvent e) {
            // ignore
        }
    }
}