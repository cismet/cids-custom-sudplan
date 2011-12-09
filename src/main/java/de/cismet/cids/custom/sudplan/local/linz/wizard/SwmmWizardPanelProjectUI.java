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
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.dialog.DateChooser;

import Sirius.server.localserver.attribute.ClassAttribute;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;
import de.cismet.cids.custom.sudplan.local.wupp.WizardInitialisationException;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelProjectUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelProjectUI.class);
    private static final transient String START_DATE_ACTION = "startDate";
    private static final transient String END_DATE_ACTION = "endDate";

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmWizardPanelProject model;
    private final transient ItemListener projectListener;
    private final transient ActionListener dateListener;
    private transient DateChooser dateChooser;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEndDate;
    private javax.swing.JButton btnStartDate;
    private javax.swing.JCheckBox chbEta;
    private javax.swing.JComboBox cobProjects;
    private javax.swing.JPanel configurationPanel;
    private javax.swing.JTextField fldEndDate;
    private javax.swing.JTextField fldInpFile;
    private javax.swing.JTextField fldStartDate;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionText;
    private javax.swing.JLabel lblEndDate;
    private javax.swing.JLabel lblEta;
    private javax.swing.JLabel lblInpFile;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblStartDate;
    private javax.swing.JPanel projectPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public SwmmWizardPanelProjectUI(final SwmmWizardPanelProject model) throws WizardInitialisationException {
        this.model = model;
        this.projectListener = new ProjectListener();
        this.dateListener = new DateListener();
        this.dateChooser = new DateChooser();

        initComponents();
        // name of the wizard step
        this.setName(NbBundle.getMessage(
                SwmmWizardPanelProject.class,
                "SwmmWizardPanelProject.this.name")); // NOI18N

        this.initProjectList();

        this.btnStartDate.setActionCommand(START_DATE_ACTION);
        this.btnStartDate.addActionListener(WeakListeners.create(
                ActionListener.class,
                this.dateListener,
                this.btnStartDate));

        this.btnEndDate.setActionCommand(END_DATE_ACTION);
        this.btnEndDate.addActionListener(WeakListeners.create(
                ActionListener.class,
                this.dateListener,
                this.btnEndDate));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        this.cobProjects.setSelectedIndex(-1);
        this.cobProjects.setSelectedItem(model.getSwmmProject());

        // set default name of inp file (not required if setSelectedItem triggers an event)
// if ((model.getSwmmInput().getInpFile() == null) || model.getSwmmInput().getInpFile().isEmpty()) {
// final String inpFile = (String) model.getSwmmProject().getProperty("title") + ".inp";
// model.getSwmmInput().setInpFile(inpFile);
// }

        // this should perform all the updates
        this.bindingGroup.unbind();
        this.bindingGroup.bind();

        // this.fldStartDate.setText(model.getSwmmInput().getStartDate());
        // this.fldEndDate.setText(model.getSwmmInput().getEndDate());
        // this.lblDescriptionText.setText((String)model.getSwmmProject().getProperty("description"));
        // this.fldInpFile.setText(model.getSwmmInput().getInpFile());

    }

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initProjectList() throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_SWMM_PROJECT);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch swmm project metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());               // NOI18N
        }

        final MetaObject[] metaObjects;
        try {
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get swmm project meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        for (int i = 0; i < metaObjects.length; ++i) {
            comboBoxModel.addElement(metaObjects[i].getBean());
        }

        this.cobProjects.setModel(comboBoxModel);
        this.cobProjects.setRenderer(new NameRenderer());
        this.cobProjects.addItemListener(WeakListeners.create(
                ItemListener.class,
                this.projectListener,
                this.cobProjects));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        projectPanel = new javax.swing.JPanel();
        lblProject = new javax.swing.JLabel();
        cobProjects = new javax.swing.JComboBox();
        lblDescription = new javax.swing.JLabel();
        lblDescriptionText = new javax.swing.JLabel();
        configurationPanel = new javax.swing.JPanel();
        lblStartDate = new javax.swing.JLabel();
        lblEndDate = new javax.swing.JLabel();
        lblEta = new javax.swing.JLabel();
        fldStartDate = new javax.swing.JTextField();
        fldEndDate = new javax.swing.JTextField();
        lblInpFile = new javax.swing.JLabel();
        fldInpFile = new javax.swing.JTextField();
        chbEta = new javax.swing.JCheckBox();
        btnStartDate = new javax.swing.JButton();
        btnEndDate = new javax.swing.JButton();

        projectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelProjectUI.class,
                    "SwmmWizardPanelProjectUI.projectPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblProject,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblProject.text")); // NOI18N

        cobProjects.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LINZ @ Workshop 09.05.2011" }));

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDescription,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblDescription.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDescriptionText,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblDescriptionText.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmProject.description}"),
                lblDescriptionText,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        final javax.swing.GroupLayout projectPanelLayout = new javax.swing.GroupLayout(projectPanel);
        projectPanel.setLayout(projectPanelLayout);
        projectPanelLayout.setHorizontalGroup(
            projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                projectPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblDescription).addComponent(lblProject)).addGap(34, 34, 34).addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblDescriptionText,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        320,
                        Short.MAX_VALUE).addComponent(
                        cobProjects,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        185,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
        projectPanelLayout.setVerticalGroup(
            projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                projectPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        lblProject).addComponent(
                        cobProjects,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        lblDescription).addComponent(
                        lblDescriptionText,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        14,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        configurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelProjectUI.class,
                    "SwmmWizardPanelProjectUI.configurationPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStartDate,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblStartDate.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEndDate,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblEndDate.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEta,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblEta.text")); // NOI18N

        fldStartDate.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.startDate}"),
                fldStartDate,
                org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"),
                "startDate");
        bindingGroup.addBinding(binding);

        fldEndDate.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.endDate}"),
                fldEndDate,
                org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"),
                "endDate");
        bindingGroup.addBinding(binding);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblInpFile,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblInpFile.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.inpFile}"),
                fldInpFile,
                org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"),
                "inpFile");
        bindingGroup.addBinding(binding);

        chbEta.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chbEta,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.chbEta.text")); // NOI18N
        chbEta.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(btnStartDate, "..."); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnEndDate, "..."); // NOI18N

        final javax.swing.GroupLayout configurationPanelLayout = new javax.swing.GroupLayout(configurationPanel);
        configurationPanel.setLayout(configurationPanelLayout);
        configurationPanelLayout.setHorizontalGroup(
            configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                configurationPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblStartDate).addComponent(lblEndDate).addComponent(lblInpFile)
                                .addComponent(lblEta)).addGap(33, 33, 33).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        configurationPanelLayout.createSequentialGroup().addGroup(
                            configurationPanelLayout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING,
                                false).addComponent(fldEndDate).addComponent(
                                fldStartDate,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                154,
                                Short.MAX_VALUE)).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                            configurationPanelLayout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING).addComponent(btnStartDate).addComponent(
                                btnEndDate))).addComponent(
                        fldInpFile,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        181,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(chbEta)).addContainerGap(
                    124,
                    Short.MAX_VALUE)));
        configurationPanelLayout.setVerticalGroup(
            configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                configurationPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(
                                    fldInpFile,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblInpFile)).addGap(
                    11,
                    11,
                    11).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblStartDate).addGroup(
                        configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(
                                        fldStartDate,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnStartDate)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblEndDate).addComponent(
                        fldEndDate,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnEndDate)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblEta, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                .addComponent(chbEta)).addContainerGap()));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(
                        projectPanel,
                        javax.swing.GroupLayout.Alignment.LEADING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(
                        configurationPanel,
                        javax.swing.GroupLayout.Alignment.LEADING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    projectPanel,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(
                    configurationPanel,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(21, Short.MAX_VALUE)));

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SwmmWizardPanelProject getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DateListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            String title;
            Date date = null;
            final boolean startDate = e.getActionCommand().equals(START_DATE_ACTION);
            final SwmmInput swmmInput = getModel().getSwmmInput();

            if (startDate) {
                title = NbBundle.getMessage(
                        SwmmWizardPanelProjectUI.class,
                        "SwmmWizardPanelProjectUI.dateChooser.startDate");

                try {
                    date = swmmInput.getStartDateDate();
                } catch (Throwable t) {
                    LOG.warn("could not fetch start date DATE", t);
                }
            } else {
                title = NbBundle.getMessage(
                        SwmmWizardPanelProjectUI.class,
                        "SwmmWizardPanelProjectUI.dateChooser.endDate");

                try {
                    date = swmmInput.getEndDateDate();
                } catch (Throwable t) {
                    LOG.warn("could not fetch end date DATE", t);
                }
            }

            dateChooser.setLocationRelativeTo(SwmmWizardPanelProjectUI.this);
            dateChooser.setTitle(title);

            if (date != null) {
                dateChooser.show(date);
            } else {
                dateChooser.show();
            }

            if (dateChooser.isDateAccepted()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("set date: " + SwmmInput.DATE_FORMAT.format(dateChooser.getDate()));
                }

                if (startDate) {
                    getModel().getSwmmInput().setStartDate(
                        SwmmInput.DATE_FORMAT.format(dateChooser.getDate()));

                    // beansbinding:
                    // fldStartDate.setText(getModel().getSwmmInput().getStartDate());
                } else {
                    getModel().getSwmmInput().setEndDate(
                        SwmmInput.DATE_FORMAT.format(dateChooser.getDate()));
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ProjectListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("swmm project '" + e.getItem().toString() + "' selected, updating swmm model input");
                }

                final CidsBean swmmProject = (CidsBean)e.getItem();
                model.setSwmmProject(swmmProject);
                model.getSwmmInput().setSwmmProject((Integer)swmmProject.getProperty("id"));

                // um aufrufe nach getSwmmInput() zu minimieren (löst einen change event aus)
                final SwmmInput swmmInput = model.getSwmmInput();

                // set default inp file name
                if ((swmmInput.getInpFile() == null) || swmmInput.getInpFile().isEmpty()) {
                    final String inpFile = (String)model.getSwmmProject().getProperty("title") + ".inp";
                    swmmInput.setInpFile(inpFile);
                }

                // fldInpFile.setText(inpFile);
                // lblDescriptionText.setText((String)swmmProject.getProperty("description"));

                bindingGroup.unbind();
                bindingGroup.bind();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class NameRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if ((comp instanceof JLabel) && (value instanceof CidsBean)) {
                final JLabel label = (JLabel)comp;
                final CidsBean obj = (CidsBean)value;
                final String name = (String)obj.getProperty("title"); // NOI18N
                label.setText(name);
            }

            return comp;
        }
    }
}
