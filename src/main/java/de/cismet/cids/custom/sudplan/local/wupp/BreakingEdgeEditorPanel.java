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

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.WeakListeners;

import java.awt.EventQueue;

import java.math.BigDecimal;

import java.util.Collection;
import java.util.logging.Level;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class BreakingEdgeEditorPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(BreakingEdgeEditorPanel.class);

    //~ Instance fields --------------------------------------------------------

    protected transient CidsBean originalBean;
    private transient boolean isOriginalBreakingEdge;
    private transient CidsBean selectedConfig;
    private final transient ListSelectionListener selL;
    private final transient DocumentListener docLHeight;

    private final transient BEHeightConverter beHeightConverter = new BEHeightConverter();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel lblNHeight;
    private javax.swing.JLabel lblOHeight;
    private javax.swing.JLabel lblOriginalHeight;
    private javax.swing.JLabel lblUnit1;
    private javax.swing.JLabel lblUnit2;
    private javax.swing.JSlider sldHeight;
    private javax.swing.JTextField txtNewHeight;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BreakingEdgeEditorPanel.
     */
    public BreakingEdgeEditorPanel() {
        selL = new selectionListener();
        docLHeight = new DocumentListenerHeight();

        initComponents();

        txtNewHeight.setDocument(new DecimalDocument());
        txtNewHeight.getDocument().addDocumentListener(WeakListeners.document(docLHeight, txtNewHeight.getDocument()));

        DeltaConfigurationListWidged.getInstance()
                .addSelectionListener(WeakListeners.create(
                        ListSelectionListener.class,
                        selL,
                        DeltaConfigurationListWidged.getInstance()));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean                DeltaBean DOCUMENT ME!
     * @param  isOriginalBreakingEdge  DOCUMENT ME!
     */
    public void init(final CidsBean cidsBean, final boolean isOriginalBreakingEdge) {
        if (cidsBean == null) {
            return;
        }

        this.isOriginalBreakingEdge = isOriginalBreakingEdge;
        this.originalBean = cidsBean;

        final BigDecimal originalHeight;
        final BigDecimal deltaHeight;
        if (this.isOriginalBreakingEdge) {
            originalHeight = (BigDecimal)this.originalBean.getProperty("height");
            deltaHeight = BigDecimal.ZERO;
        } else {
            originalHeight = (BigDecimal)this.originalBean.getProperty("original_object.height");
            deltaHeight = (BigDecimal)this.originalBean.getProperty("height");
        }

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    lblOriginalHeight.setText(String.valueOf(originalHeight.multiply(new BigDecimal(100)).intValue()));
                    if (isOriginalBreakingEdge) {
                        txtNewHeight.setText("0");
                    } else {
                        txtNewHeight.setText(String.valueOf(deltaHeight.multiply(new BigDecimal(100)).intValue()));
                    }
                }
            };

        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }

        setSaveButtonEnabled();
    }

    /**
     * DOCUMENT ME!
     */
    private void setSaveButtonEnabled() {
        final boolean enable;
        final String message;

        if (isOriginalBreakingEdge) {
            if (selectedConfig == null) {
                message = "Please select a configuration from the configuration widged";
                enable = false;
            } else if ((txtNewHeight.getText() == null) || txtNewHeight.getText().isEmpty()) {
                message = "Please specify a height";
                enable = false;
            } else {
                message = null;
                enable = true;
            }
        } else {
            message = null;
            enable = true;
        }
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    btnSave.setToolTipText(message);
                    btnSave.setEnabled(enable);
                }
            };
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
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

        sldHeight = new javax.swing.JSlider();
        txtNewHeight = new javax.swing.JTextField();
        lblNHeight = new javax.swing.JLabel();
        lblOHeight = new javax.swing.JLabel();
        lblOriginalHeight = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        lblUnit1 = new javax.swing.JLabel();
        lblUnit2 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        sldHeight.setMajorTickSpacing(20);
        sldHeight.setMaximum(500);
        sldHeight.setMinorTickSpacing(10);
        sldHeight.setPaintTicks(true);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                txtNewHeight,
                org.jdesktop.beansbinding.ELProperty.create("${text}"),
                sldHeight,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setConverter(new SLDConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(sldHeight, gridBagConstraints);

        txtNewHeight.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNewHeight.setText(org.openide.util.NbBundle.getMessage(
                BreakingEdgeEditorPanel.class,
                "BreakingEdgeEditorPanel.txtNewHeight.text")); // NOI18N
        txtNewHeight.setMaximumSize(new java.awt.Dimension(50, 28));
        txtNewHeight.setMinimumSize(new java.awt.Dimension(50, 28));
        txtNewHeight.setPreferredSize(new java.awt.Dimension(50, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(txtNewHeight, gridBagConstraints);

        lblNHeight.setText(org.openide.util.NbBundle.getMessage(
                BreakingEdgeEditorPanel.class,
                "BreakingEdgeEditorPanel.lblNHeight.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(lblNHeight, gridBagConstraints);

        lblOHeight.setText(org.openide.util.NbBundle.getMessage(
                BreakingEdgeEditorPanel.class,
                "BreakingEdgeEditorPanel.lblOHeight.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        add(lblOHeight, gridBagConstraints);

        lblOriginalHeight.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblOriginalHeight.setText(org.openide.util.NbBundle.getMessage(
                BreakingEdgeEditorPanel.class,
                "BreakingEdgeEditorPanel.lblOriginalHeight.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblOriginalHeight, gridBagConstraints);

        btnSave.setText(org.openide.util.NbBundle.getMessage(
                BreakingEdgeEditorPanel.class,
                "BreakingEdgeEditorPanel.btnSave.text")); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSaveActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btnSave, gridBagConstraints);

        lblUnit1.setText(org.openide.util.NbBundle.getMessage(
                BreakingEdgeEditorPanel.class,
                "BreakingEdgeEditorPanel.lblUnit1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUnit1, gridBagConstraints);

        lblUnit2.setText(org.openide.util.NbBundle.getMessage(
                BreakingEdgeEditorPanel.class,
                "BreakingEdgeEditorPanel.lblUnit2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUnit2, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSaveActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSaveActionPerformed
        if (originalBean == null) {
            return;
        }
        if (isOriginalBreakingEdge && (selectedConfig == null)) {
            return;
        }

        SudplanConcurrency.getSudplanGeneralPurposePool().execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        final BigDecimal newHeight = beHeightConverter.convertReverse(txtNewHeight.getText());

                        if (isOriginalBreakingEdge) {
                            final int originalBKID = (Integer)originalBean.getProperty("id");
                            final Collection<CidsBean> deltaBreakingEdges = (Collection<CidsBean>)
                                selectedConfig.getProperty("delta_breaking_edges");
                            CidsBean deltaBk = null;
                            for (final CidsBean bk : deltaBreakingEdges) {
                                final int tempOriginalBKID = (Integer)bk.getProperty("original_object.id");
                                if (tempOriginalBKID == originalBKID) {
                                    deltaBk = bk;
                                    break;
                                }
                            }
                            if (deltaBk == null) {
                                deltaBk = CidsBean.createNewCidsBeanFromTableName(
                                        SMSUtils.DOMAIN_SUDPLAN_WUPP,
                                        SMSUtils.TABLENAME_DELTA_BREAKING_EDGE);

                                final String defaultName = "Neu";
                                deltaBk.setProperty("name", defaultName);
                                deltaBk.setProperty(TOOL_TIP_TEXT_KEY, ui);
                                deltaBk.setProperty("original_object", originalBean);
                                deltaBk.setProperty("height", newHeight);
                                deltaBk = deltaBk.persist();
                                deltaBreakingEdges.add(deltaBk);
                                selectedConfig.persist();
                            } else {
                                deltaBk.setProperty("height", newHeight);
                                deltaBk.persist();
                            }
                        } else {
                            originalBean.setProperty("height", newHeight);
                            originalBean.persist();
                        }
                    } catch (Exception e) {
                        LOG.error("cannot create delta breaking edge", e);
                        final ErrorInfo errorInfo = new ErrorInfo(
                                "Create delta breaking edge error",
                                "Error while creating delta breaking edge",
                                null,
                                "ERROR",
                                e,
                                Level.SEVERE,
                                null);
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    JXErrorPane.showDialog(ComponentRegistry.getRegistry().getMainWindow(), errorInfo);
                                }
                            });
                    }
                }
            });

        // TODO update the bean in all other parts like object katalogue, editor, renderer ...

    } //GEN-LAST:event_btnSaveActionPerformed

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class selectionListener implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent lse) {
            selectedConfig = DeltaConfigurationListWidged.getInstance().getSelectedConfig();
            setSaveButtonEnabled();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DocumentListenerHeight implements DocumentListener {

        //~ Instance fields ----------------------------------------------------

        private final SLDConverter sldConverter;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DocumentListenerHeight object.
         */
        public DocumentListenerHeight() {
            sldConverter = new SLDConverter();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent de) {
            final Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        sldHeight.setValue(sldConverter.convertForward(txtNewHeight.getText()));
                    }
                };

            if (EventQueue.isDispatchThread()) {
                r.run();
            } else {
                EventQueue.invokeLater(r);
            }
            setSaveButtonEnabled();
        }

        @Override
        public void removeUpdate(final DocumentEvent de) {
            final Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        sldHeight.setValue(sldConverter.convertForward(txtNewHeight.getText()));
                    }
                };

            if (EventQueue.isDispatchThread()) {
                r.run();
            } else {
                EventQueue.invokeLater(r);
            }
            setSaveButtonEnabled();
        }

        @Override
        public void changedUpdate(final DocumentEvent de) {
            final Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        sldHeight.setValue(sldConverter.convertForward(txtNewHeight.getText()));
                    }
                };

            if (EventQueue.isDispatchThread()) {
                r.run();
            } else {
                EventQueue.invokeLater(r);
            }
            setSaveButtonEnabled();
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
    private static final class SLDConverter extends Converter<String, Integer> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Integer convertForward(final String value) {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public String convertReverse(final Integer value) {
            return String.valueOf(value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class DecimalDocument extends javax.swing.text.PlainDocument {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DecimalDocument object.
         */
        public DecimalDocument() {
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertString(final int offset, final String str, final javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            final String valid = "0123456789";
            for (int i = 0; i < str.length(); i++) {
                if (valid.indexOf(str.charAt(i)) == -1) {
                    return;
                }
                super.insertString(offset, str, a);
            }
        }
    }
}
