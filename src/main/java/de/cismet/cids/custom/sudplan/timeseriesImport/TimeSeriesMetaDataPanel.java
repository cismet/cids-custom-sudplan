/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * TimeSeriesImportFileChoosePanel.java
 *
 * Created on 07.12.2011, 14:37:26
 */
package de.cismet.cids.custom.sudplan.timeseriesImport;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesMetaDataPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TimeSeriesMetaDataPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final transient TimeSeriesMetaDataPanelCtrl ctrl;
    private transient CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cboStation;
    private javax.swing.JCheckBox chkForecast;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblForecast;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblStation;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TimeSeriesImportFileChoosePanel.
     *
     * @param   ctrl  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public TimeSeriesMetaDataPanel(final TimeSeriesMetaDataPanelCtrl ctrl) {
        if (ctrl == null) {
            throw new NullPointerException("Given TimeSeriesConverterChoosePanelCtrl instance "
                        + "must not be null"); // NOI18N
        }

        initComponents();

        this.setName(java.util.ResourceBundle.getBundle("de/cismet/cids/custom/sudplan/timeseriesImport/Bundle")
                    .getString("TimeSeriesMetaDataPanel.this.name"));
        this.ctrl = ctrl;

        final DocumentListener docListener = new DocumentListener() {

                @Override
                public void insertUpdate(final DocumentEvent de) {
                    ctrl.fireChangeEvent();
                }

                @Override
                public void removeUpdate(final DocumentEvent de) {
                    ctrl.fireChangeEvent();
                }

                @Override
                public void changedUpdate(final DocumentEvent de) {
                    ctrl.fireChangeEvent();
                }
            };

        this.txtName.getDocument().addDocumentListener(docListener);
        this.txtDescription.getDocument().addDocumentListener(docListener);

        this.cboStation.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    ctrl.fireChangeEvent();
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    public void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering init()");
        }

        this.cidsBean = this.ctrl.getCidsBean();
        this.txtName.setText("");
        this.txtDescription.setText("");
        this.cboStation.setSelectedIndex(-1);
        this.chkForecast.setSelected(false);

        try {
            this.cidsBean.setProperty("forecast", Boolean.FALSE);
        } catch (Exception e) {
            LOG.error("ERROR", e);
        }

        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
            bindingGroup,
            cidsBean);
        this.bindingGroup.unbind();
        this.bindingGroup.bind();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving init()");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTimeSeriesName() {
        return this.txtName.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDesciption() {
        return this.txtDescription.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getStation() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isForecast() {
        return this.chkForecast.isSelected();
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

        jPanel1 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblStation = new javax.swing.JLabel();
        lblForecast = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtDescription = new javax.swing.JTextField();
        cboStation = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        chkForecast = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblName.setText(org.openide.util.NbBundle.getMessage(
                TimeSeriesMetaDataPanel.class,
                "TimeSeriesMetaDataPanel.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(lblName, gridBagConstraints);

        lblDescription.setText(org.openide.util.NbBundle.getMessage(
                TimeSeriesMetaDataPanel.class,
                "TimeSeriesMetaDataPanel.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(lblDescription, gridBagConstraints);

        lblStation.setText(org.openide.util.NbBundle.getMessage(
                TimeSeriesMetaDataPanel.class,
                "TimeSeriesMetaDataPanel.lblStation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(lblStation, gridBagConstraints);

        lblForecast.setText(org.openide.util.NbBundle.getMessage(
                TimeSeriesMetaDataPanel.class,
                "TimeSeriesMetaDataPanel.lblForecast.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(lblForecast, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(txtName, gridBagConstraints);

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(txtDescription, gridBagConstraints);

        cboStation.setMinimumSize(new java.awt.Dimension(225, 25));
        cboStation.setPreferredSize(new java.awt.Dimension(225, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.station}"),
                cboStation,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(cboStation, gridBagConstraints);

        chkForecast.setText(org.openide.util.NbBundle.getMessage(
                TimeSeriesMetaDataPanel.class,
                "TimeSeriesMetaDataPanel.chkForecast.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.forecast}"),
                chkForecast,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 9, 9, 9);
        jPanel1.add(chkForecast, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
}
