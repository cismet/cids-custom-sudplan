/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.sudplan;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;

import java.awt.EventQueue;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.EmissionUpload;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.EmissionUploadDialog;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.EmissionUploadVisualPanelEmissionScenario;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.EmissionUploadWizardAction;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.GridHeight;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.Substance;
import de.cismet.cids.custom.sudplan.airquality.emissionupload.TimeVariation;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.Converter;

import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.ByteArrayDownload;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class EmissionDatabaseEditor extends AbstractCidsBeanRenderer implements EditorSaveListener,
    TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EmissionDatabaseEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final transient boolean editable;
    private Object recentlySelectedEmissionGrid;
    private final transient SilentSelectionModel silentSelectionModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpload;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblEmissionGrids;
    private javax.swing.JLabel lblGeneralInformation;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSRS;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstEmissionGrids;
    private javax.swing.JPanel pnlControls;
    private de.cismet.cids.custom.objecteditors.sudplan.EmissionDatabaseGridEditor pnlEmissionGrid;
    private javax.swing.JPanel pnlTitle;
    private de.cismet.tools.gui.RoundedPanel rpEmissionGrids;
    private de.cismet.tools.gui.RoundedPanel rpGeneralInformation;
    private javax.swing.JScrollPane scpDescription;
    private javax.swing.JScrollPane scpEmissionGrids;
    private de.cismet.tools.gui.SemiRoundedPanel srpEmissionGrids;
    private de.cismet.tools.gui.SemiRoundedPanel srpGeneralInformation;
    private javax.swing.Box.Filler strControls;
    private javax.swing.JTextArea txaDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSRS;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EmissionDatabaseEditor.
     */
    public EmissionDatabaseEditor() {
        this(true);
    }

    /**
     * Creates a new EmissionDatabaseEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public EmissionDatabaseEditor(final boolean editable) {
        this.editable = editable;

        silentSelectionModel = new SilentSelectionModel();

        initComponents();

        if (editable) {
            ((AbstractDocument)txtName.getDocument()).setDocumentFilter(
                new EmissionUploadVisualPanelEmissionScenario.EmissionScenarioNameFilter());
        }

        txtName.setEditable(editable);
        txtSRS.setEditable(editable);
        txaDescription.setEditable(editable);
        btnAdd.setEnabled(editable);
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

        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnUpload = new javax.swing.JButton();
        btnCopy = new javax.swing.JButton();
        btnDownload = new javax.swing.JButton();
        rpGeneralInformation = new de.cismet.tools.gui.RoundedPanel();
        srpGeneralInformation = new de.cismet.tools.gui.SemiRoundedPanel();
        lblGeneralInformation = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        scpDescription = new javax.swing.JScrollPane();
        txaDescription = new javax.swing.JTextArea();
        lblSRS = new javax.swing.JLabel();
        txtSRS = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        rpEmissionGrids = new de.cismet.tools.gui.RoundedPanel();
        srpEmissionGrids = new de.cismet.tools.gui.SemiRoundedPanel();
        lblEmissionGrids = new javax.swing.JLabel();
        scpEmissionGrids = new javax.swing.JScrollPane();
        lstEmissionGrids = new javax.swing.JList();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        strControls = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 0),
                new java.awt.Dimension(32767, 32767));
        btnSave = new javax.swing.JButton();
        pnlEmissionGrid = new EmissionDatabaseGridEditor(editable);

        pnlTitle.setOpaque(false);
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(java.awt.Color.white);
        lblTitle.setText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.lblTitle.text"));     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(lblTitle, gridBagConstraints);

        btnUpload.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/airquality/emissionupload/upload_16.png"))); // NOI18N
        btnUpload.setToolTipText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.btnUpload.toolTipText"));                                                   // NOI18N
        btnUpload.setBorderPainted(false);
        btnUpload.setContentAreaFilled(false);
        btnUpload.setFocusPainted(false);
        btnUpload.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnUploadActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(btnUpload, gridBagConstraints);

        btnCopy.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/airquality/emissionupload/copy_16.png"))); // NOI18N
        btnCopy.setToolTipText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.btnCopy.toolTipText"));                                                   // NOI18N
        btnCopy.setBorderPainted(false);
        btnCopy.setContentAreaFilled(false);
        btnCopy.setFocusPainted(false);
        btnCopy.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCopyActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(btnCopy, gridBagConstraints);

        btnDownload.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/airquality/emissionupload/download_16.png"))); // NOI18N
        btnDownload.setToolTipText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.btnDownload.toolTipText"));                                                   // NOI18N
        btnDownload.setBorderPainted(false);
        btnDownload.setContentAreaFilled(false);
        btnDownload.setFocusPainted(false);
        btnDownload.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDownloadActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(btnDownload, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        rpGeneralInformation.setLayout(new java.awt.GridBagLayout());

        srpGeneralInformation.setBackground(java.awt.Color.black);
        srpGeneralInformation.setLayout(new java.awt.GridBagLayout());

        lblGeneralInformation.setBackground(new java.awt.Color(0, 0, 0));
        lblGeneralInformation.setForeground(new java.awt.Color(255, 255, 255));
        lblGeneralInformation.setText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.lblGeneralInformation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpGeneralInformation.add(lblGeneralInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        rpGeneralInformation.add(srpGeneralInformation, gridBagConstraints);

        lblName.setText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        rpGeneralInformation.add(lblName, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setRows(5);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.description}"),
                txaDescription,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpDescription.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        rpGeneralInformation.add(scpDescription, gridBagConstraints);

        lblSRS.setText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.lblSRS.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        rpGeneralInformation.add(lblSRS, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.srs}"),
                txtSRS,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        rpGeneralInformation.add(txtSRS, gridBagConstraints);

        lblDescription.setText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.lblDescription.text")); // NOI18N
        lblDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 5);
        rpGeneralInformation.add(lblDescription, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        rpGeneralInformation.add(txtName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(rpGeneralInformation, gridBagConstraints);

        rpEmissionGrids.setLayout(new java.awt.GridBagLayout());

        srpEmissionGrids.setBackground(java.awt.Color.black);
        srpEmissionGrids.setForeground(java.awt.Color.white);
        srpEmissionGrids.setLayout(new java.awt.GridBagLayout());

        lblEmissionGrids.setForeground(java.awt.Color.white);
        lblEmissionGrids.setText(org.openide.util.NbBundle.getMessage(
                EmissionDatabaseEditor.class,
                "EmissionDatabaseEditor.lblEmissionGrids.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpEmissionGrids.add(lblEmissionGrids, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        rpEmissionGrids.add(srpEmissionGrids, gridBagConstraints);

        lstEmissionGrids.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstEmissionGrids.setSelectionModel(silentSelectionModel);

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.grids}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstEmissionGrids);
        bindingGroup.addBinding(jListBinding);

        lstEmissionGrids.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstEmissionGridsValueChanged(evt);
                }
            });
        scpEmissionGrids.setViewportView(lstEmissionGrids);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        rpEmissionGrids.add(scpEmissionGrids, gridBagConstraints);

        pnlControls.setOpaque(false);
        pnlControls.setLayout(new java.awt.GridBagLayout());

        btnAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/airquality/emissionupload/edit_add.png"))); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlControls.add(btnAdd, gridBagConstraints);

        btnRemove.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/airquality/emissionupload/edit_remove.png"))); // NOI18N
        btnRemove.setEnabled(false);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlControls.add(btnRemove, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlControls.add(strControls, gridBagConstraints);

        btnSave.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/airquality/emissionupload/edit_save.png"))); // NOI18N
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSaveActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlControls.add(btnSave, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 5);
        rpEmissionGrids.add(pnlControls, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        rpEmissionGrids.add(pnlEmissionGrid, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(rpEmissionGrids, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstEmissionGridsValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstEmissionGridsValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }

        if (lstEmissionGrids.getSelectedValue() instanceof CidsBean) {
            if (pnlEmissionGrid.isDirty()) {
                final int userDecision = JOptionPane.showConfirmDialog(
                        this,
                        java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objecteditors/sudplan/Bundle")
                                    .getString(
                                        "EmissionDatabaseEditor.lstEmissionGridsValueChanged(ListSelectionEvent).JOptionPane.message"),
                        java.util.ResourceBundle.getBundle("de/cismet/cids/custom/objecteditors/sudplan/Bundle")
                                    .getString(
                                        "EmissionDatabaseEditor.lstEmissionGridsValueChanged(ListSelectionEvent).JOptionPane.title"),
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (userDecision == JOptionPane.CANCEL_OPTION) {
                    silentSelectionModel.setIsSilent(true);
                    lstEmissionGrids.setSelectedValue(recentlySelectedEmissionGrid, true);
                    silentSelectionModel.setIsSilent(false);
                    return;
                }

                if (userDecision == JOptionPane.YES_OPTION) {
                    pnlEmissionGrid.persistDisplayedEmissionGrid();
                }
            }

            pnlEmissionGrid.setCidsBean((CidsBean)lstEmissionGrids.getSelectedValue(),
                (Boolean)cidsBean.getProperty("uploaded")); // NOI18N
        } else {
            pnlEmissionGrid.setCidsBean(null);
        }

        recentlySelectedEmissionGrid = lstEmissionGrids.getSelectedValue();

        btnRemove.setEnabled(editable && (lstEmissionGrids.getSelectedValue() instanceof CidsBean));
        btnSave.setEnabled(editable && (lstEmissionGrids.getSelectedValue() instanceof CidsBean));
    } //GEN-LAST:event_lstEmissionGridsValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnUploadActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnUploadActionPerformed
        if (cidsBean == null) {
            return;
        }

        final Object uploadedObj = cidsBean.getProperty("uploaded"); // NOI18N

        final Boolean uploaded;
        if (uploadedObj instanceof Boolean) {
            uploaded = (Boolean)uploadedObj;
        } else {
            uploaded = Boolean.FALSE;
        }

        if (uploaded) {
            return;
        }

        try {
            cidsBean.setProperty("file", Converter.toString(EmissionUpload.zip(cidsBean))); // NOI18N
            cidsBean.persist();
        } catch (final Exception ex) {
            LOG.warn("Couldn't generate zip file for emission database.", ex);              // NOI18N
            return;
        }

        final EmissionUploadDialog uploadDialog = new EmissionUploadDialog(ComponentRegistry.getRegistry()
                        .getMainWindow(),
                cidsBean);
        uploadDialog.pack();
        uploadDialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        uploadDialog.setVisible(true);
        uploadDialog.toFront();
    } //GEN-LAST:event_btnUploadActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveActionPerformed
        final List<CidsBean> grids = cidsBean.getBeanCollectionProperty("grids"); // NOI18N
        grids.remove((CidsBean)lstEmissionGrids.getSelectedValue());
    }                                                                             //GEN-LAST:event_btnRemoveActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddActionPerformed
        final String gridName = JOptionPane.showInputDialog(
                this,
                NbBundle.getMessage(
                    EmissionDatabaseEditor.class,
                    "EmissionDatabaseEditor.btnAddActionPerformed(ActionEvent).JOptionPane.message"),
                NbBundle.getMessage(
                    EmissionDatabaseEditor.class,
                    "EmissionDatabaseEditor.btnAddActionPerformed(ActionEvent).JOptionPane.title"),
                JOptionPane.QUESTION_MESSAGE);

        if ((gridName == null) || gridName.trim().isEmpty()) {
            return;
        }

        final MetaClass metaClassEmissionDatabaseGrid;

        try {
            metaClassEmissionDatabaseGrid = ClassCacheMultiple.getMetaClass(SessionManager.getSession().getUser()
                            .getDomain(),
                    EmissionUploadWizardAction.TABLENAME_EMISSION_DATABASE_GRID);
        } catch (final Exception ex) {
            final String errorMessage = "The meta classes can't be retrieved."; // NOI18N

            LOG.warn(errorMessage, ex);

            try {
                final ErrorInfo errorInfo = new ErrorInfo(
                        "Error",                                 // NOI18N
                        "Couldn't add a new emission database.", // NOI18N
                        errorMessage,
                        "ERROR",                                 // NOI18N
                        ex,
                        Level.SEVERE,
                        null);

                EventQueue.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            JXErrorPane.showDialog(EmissionDatabaseEditor.this, errorInfo);
                        }
                    });
            } catch (final Exception ex1) {
                LOG.error("Can't display error dialog", ex1); // NOI18N
            }
            return;
        }

        try {
            final CidsBean grid = metaClassEmissionDatabaseGrid.getEmptyInstance().getBean();

            grid.setProperty("name", gridName);                                                // NOI18N
            grid.setProperty("substance", Substance.NOX.getRepresentationFile());              // NOI18N
            grid.setProperty("timevariation", TimeVariation.CONSTANT.getRepresentationFile()); // NOI18N
            grid.setProperty("height", GridHeight.ZERO.getRepresentationFile());               // NOI18N

            cidsBean.getBeanCollectionProperty("grids").add(grid);                                      // NOI18N
        } catch (final Exception ex) {
            final String errorMessage = "Something went wrong while initializing a new emission grid."; // NOI18N

            LOG.warn(errorMessage, ex);

            try {
                final ErrorInfo errorInfo = new ErrorInfo(
                        "Error",                                 // NOI18N
                        "Couldn't add a new emission database.", // NOI18N
                        errorMessage,
                        "ERROR",                                 // NOI18N
                        ex,
                        Level.SEVERE,
                        null);

                EventQueue.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            JXErrorPane.showDialog(EmissionDatabaseEditor.this, errorInfo);
                        }
                    });
            } catch (final Exception ex1) {
                LOG.error("Can't display error dialog", ex1); // NOI18N
            }
        }
    }                                                         //GEN-LAST:event_btnAddActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSaveActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSaveActionPerformed
        pnlEmissionGrid.persistDisplayedEmissionGrid();
    }                                                                           //GEN-LAST:event_btnSaveActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCopyActionPerformed(final java.awt.event.ActionEvent evt) {                        //GEN-FIRST:event_btnCopyActionPerformed
        final String name = JOptionPane.showInputDialog(
                this,
                NbBundle.getMessage(
                    EmissionDatabaseEditor.class,
                    "EmissionDatabaseEditor.btnCopyActionPerformed(ActionEvent).JOptionPane.message"), // NOI18N
                NbBundle.getMessage(
                    EmissionDatabaseEditor.class,
                    "EmissionDatabaseEditor.btnCopyActionPerformed(ActionEvent).JOptionPane.title"),   // NOI18N
                JOptionPane.QUESTION_MESSAGE);

        if ((name == null) || name.trim().isEmpty()) {
            LOG.info("User aborted creation of a new emission database."); // NOI18N
        }

        final CidsBean newBean;
        try {
            newBean = cloneCidsBean(cidsBean, true);
            newBean.setProperty("name", name);                      // NOI18N
            newBean.setProperty("uploaded", Boolean.FALSE);         // NOI18N
            newBean.persist();
        } catch (final Exception ex) {
            final String errorMessage = "Couldn't clone CidsBean."; // NOI18N

            LOG.error(errorMessage, ex);

            try {
                final ErrorInfo errorInfo = new ErrorInfo(
                        "Error",                                     // NOI18N
                        "The emission database couldn't be copied.", // NOI18N
                        errorMessage,
                        "ERROR",                                     // NOI18N
                        ex,
                        Level.SEVERE,
                        null);

                EventQueue.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            JXErrorPane.showDialog(EmissionDatabaseEditor.this, errorInfo);
                        }
                    });
            } catch (final Exception ex1) {
                LOG.error("Can't display error dialog", ex1); // NOI18N
            }

            return;
        }

        ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(newBean.getMetaObject(), ""); // NOI18N

        ComponentRegistry.getRegistry().getCatalogueTree().requestRefreshNode("airquality.edb"); // NOI18N
    }                                                                                            //GEN-LAST:event_btnCopyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDownloadActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDownloadActionPerformed
        byte[] fileContent = null;

        if ((cidsBean != null) && (cidsBean.getProperty("file") instanceof String)) { // NOI18N
            fileContent = Converter.fromString((String)cidsBean.getProperty("file")); // NOI18N
        } else {
            try {
                fileContent = EmissionUpload.zip(cidsBean);
            } catch (Exception ex) {
                final String errorMessage = "Couldn't zip emission database.";        // NOI18N

                LOG.warn(errorMessage, ex);

                try {
                    final ErrorInfo errorInfo = new ErrorInfo(
                            "Error",                                         // NOI18N
                            "The emission database couldn't be downloaded.", // NOI18N
                            errorMessage,
                            "WARN",                                          // NOI18N
                            ex,
                            Level.SEVERE,
                            null);

                    EventQueue.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                JXErrorPane.showDialog(EmissionDatabaseEditor.this, errorInfo);
                            }
                        });
                } catch (final Exception ex1) {
                    LOG.error("Can't display error dialog", ex1); // NOI18N
                }
            }
        }

        if ((fileContent == null) || (fileContent.length <= 0)) {
            LOG.info("Nothing to download for emission database."); // NOI18N
            // TODO: User feeedback.
        }

        if (!DownloadManagerDialog.showAskingForUserTitle(EmissionDatabaseEditor.this)) {
            return;
        }

        final String title;
        final String filename;
        if (cidsBean.getProperty("name") instanceof String) { // NOI18N
            title = (String)cidsBean.getProperty("name");     // NOI18N
            filename = (String)cidsBean.getProperty("name");  // NOI18N
        } else {
            title = "Emission database";                      // NOI18N
            filename = "emissionDatabase";                    // NOI18N
        }

        DownloadManager.instance()
                .add(
                    new ByteArrayDownload(fileContent, title, DownloadManagerDialog.getJobname(), filename, ".zip")); // NOI18N
    }                                                                                                                 //GEN-LAST:event_btnDownloadActionPerformed

    @Override
    protected void init() {
        bindingGroup.unbind();

        btnUpload.setEnabled(false);

        if (cidsBean != null) {
            btnUpload.setEnabled((cidsBean.getProperty("uploaded") instanceof Boolean) // NOI18N
                        && !((Boolean)cidsBean.getProperty("uploaded"))); // NOI18N
            lblTitle.setText((String)cidsBean.getProperty("name"));   // NOI18N
        }

        bindingGroup.bind();
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        // NoOp
    }

    @Override
    public boolean prepareForSave() {
        if (pnlEmissionGrid.isDirty()) {
            pnlEmissionGrid.persistDisplayedEmissionGrid();
        }

        return true;
    }

    @Override
    public JComponent getTitleComponent() {
        return pnlTitle;
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);

        lblTitle.setText(title);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean        DOCUMENT ME!
     * @param   cloneBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static CidsBean cloneCidsBean(final CidsBean bean, final boolean cloneBeans) throws Exception {
        if (bean == null) {
            return null;
        }
        final CidsBean clone = bean.getMetaObject().getMetaClass().getEmptyInstance().getBean();

        for (final String propName : bean.getPropertyNames()) {
            if (!propName.toLowerCase().equals("id")) { // NOI18N
                final Object o = bean.getProperty(propName);

                if (o instanceof CidsBean) {
                    if (cloneBeans) {
                        clone.setProperty(propName, cloneCidsBean((CidsBean)o, true));
                    } else {
                        clone.setProperty(propName, (CidsBean)o);
                    }
                } else if (o instanceof Collection) {
                    final List<CidsBean> list = (List<CidsBean>)o;
                    final List<CidsBean> newList = clone.getBeanCollectionProperty(propName);

                    for (final CidsBean tmpBean : list) {
                        if (cloneBeans) {
                            newList.add(cloneCidsBean(tmpBean, true));
                        } else {
                            newList.add(tmpBean);
                        }
                    }
                } else if (o instanceof Geometry) {
                    clone.setProperty(propName, ((Geometry)o).clone());
                } else if (o instanceof Long) {
                    clone.setProperty(propName, new Long(o.toString()));
                } else if (o instanceof Double) {
                    clone.setProperty(propName, new Double(o.toString()));
                } else if (o instanceof Integer) {
                    clone.setProperty(propName, new Integer(o.toString()));
                } else if (o instanceof Boolean) {
                    clone.setProperty(propName, new Boolean(o.toString()));
                } else if (o instanceof String) {
                    clone.setProperty(propName, o);
                } else {
                    if (o != null) {
                        LOG.error("Unknown property type '" + o.getClass().getName() + "' of property '" + propName
                                    + "'."); // NOI18N
                    }
                    clone.setProperty(propName, o);
                }
            }
        }

        return clone;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class SilentSelectionModel extends DefaultListSelectionModel {

        //~ Instance fields ----------------------------------------------------

        private boolean isSilent = false;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  isSilent  DOCUMENT ME!
         */
        public void setIsSilent(final boolean isSilent) {
            this.isSilent = isSilent;
        }

        @Override
        protected void fireValueChanged(final int firstIndex, final int lastIndex, final boolean isAdjusting) {
            if (isSilent) {
                return;
            }

            super.fireValueChanged(firstIndex, lastIndex, isAdjusting);
        }
    }
}
