/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.sudplan;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.SqlTimestampToStringConverter;
import de.cismet.cids.custom.sudplan.local.wupp.RunoffOutputManagerUI;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class GeocpmConfigurationEditor extends AbstractCidsBeanRenderer implements EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TIN_URL =
        "http://sudplanwp6.cismet.de/geoserver/wms?SERVICE=WMS&&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=<cismap:srs>&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=sudplan:GeoCPM%20TIN&STYLES=geocpm_triangle_style";                   // NOI18N
    public static final String BE_URL =
        "http://sudplanwp6.cismet.de/geoserver/wms?SERVICE=WMS&&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=<cismap:srs>&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=sudplan:GeoCPM%20Breaking%20Edges&STYLES=geocpm_breaking_edge_style"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(GeocpmConfigurationEditor.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient de.cismet.cids.editors.DefaultBindableReferenceCombo cboInvestigationArea =
        new de.cismet.cids.editors.DefaultBindableReferenceCombo();
    private final transient javax.swing.JCheckBox chkLastValues = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkMergeTriangles = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkSaveFlowCurves = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkSaveMarked = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkSaveVelocityCurves = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkTimeStepRestriction = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkWriteEdge = new javax.swing.JCheckBox();
    private final transient javax.swing.JCheckBox chkWriteNode = new javax.swing.JCheckBox();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JLabel lblCalcBegin = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCalcBeginValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCalcEnd = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblCalcEndValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeadingInfo = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeadingMap = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblHeadingMetadata = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblInvestigationArea = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblLastValues = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblMergeTriangles = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblMinCalcTriangleSize = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblMinCalcTriangleSizeValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblNumberOfThreads = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblNumberOfThreadsValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQIn = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQInValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQOut = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblQOutValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResultSaveLimit = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblResultSaveLimitValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSaveFlowCurves = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSaveMarked = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblSaveVelocityCurves = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblTimeStepRestriction = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblWriteEdge = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblWriteNode = new javax.swing.JLabel();
    private final transient de.cismet.cismap.commons.gui.MappingComponent map =
        new de.cismet.cismap.commons.gui.MappingComponent();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo1 =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient de.cismet.tools.gui.SemiRoundedPanel panHeadInfo2 =
        new de.cismet.tools.gui.SemiRoundedPanel();
    private final transient javax.swing.JPanel pnlFiller = new javax.swing.JPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlInfo = new de.cismet.tools.gui.RoundedPanel();
    private final transient javax.swing.JPanel pnlInfoContent = new javax.swing.JPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlMap = new de.cismet.tools.gui.RoundedPanel();
    private final transient de.cismet.tools.gui.RoundedPanel pnlMetadata = new de.cismet.tools.gui.RoundedPanel();
    private final transient javax.swing.JPanel pnlMetadataContent = new javax.swing.JPanel();
    private final transient javax.swing.JTextArea txaDescription = new javax.swing.JTextArea();
    private final transient javax.swing.JTextField txtName = new javax.swing.JTextField();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GeocpmConfigurationEditor.
     */
    public GeocpmConfigurationEditor() {
        this(true);
    }

    /**
     * Creates a new GeocpmConfigurationEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public GeocpmConfigurationEditor(final boolean editable) {
        initComponents();

        txtName.setEditable(editable);
        txaDescription.setEditable(editable);
        cboInvestigationArea.setEnabled(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
            bindingGroup,
            cidsBean);
        bindingGroup.unbind();
        bindingGroup.bind();

        initMap();
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
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

        pnlMetadata.setLayout(new java.awt.GridBagLayout());

        panHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setLayout(new java.awt.FlowLayout());

        lblHeadingMetadata.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingMetadata.setText(org.openide.util.NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblHeadingMetadata.text")); // NOI18N
        panHeadInfo.add(lblHeadingMetadata);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlMetadata.add(panHeadInfo, gridBagConstraints);

        pnlMetadataContent.setOpaque(false);
        pnlMetadataContent.setLayout(new java.awt.GridBagLayout());

        lblName.setText(NbBundle.getMessage(GeocpmConfigurationEditor.class, "GeocpmConfigurationEditor.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 5);
        pnlMetadataContent.add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 3);
        pnlMetadataContent.add(txtName, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 5);
        pnlMetadataContent.add(lblDescription, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.description}"),
                txaDescription,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMetadataContent.add(jScrollPane1, gridBagConstraints);

        lblInvestigationArea.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblInvestigationArea.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 5);
        pnlMetadataContent.add(lblInvestigationArea, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.investigation_area}"),
                cboInvestigationArea,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 3);
        pnlMetadataContent.add(cboInvestigationArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        pnlMetadata.add(pnlMetadataContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlMetadata, gridBagConstraints);

        pnlInfo.setLayout(new java.awt.GridBagLayout());

        panHeadInfo1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setLayout(new java.awt.FlowLayout());

        lblHeadingInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingInfo.setText(org.openide.util.NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblHeadingInfo.text")); // NOI18N
        panHeadInfo1.add(lblHeadingInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlInfo.add(panHeadInfo1, gridBagConstraints);

        pnlInfoContent.setOpaque(false);
        pnlInfoContent.setLayout(new java.awt.GridBagLayout());

        lblCalcBegin.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblCalcBegin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblCalcBegin, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.calc_begin}"),
                lblCalcBeginValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new SqlTimestampToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblCalcBeginValue, gridBagConstraints);

        lblCalcEnd.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblCalcEnd.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblCalcEnd, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.calc_end}"),
                lblCalcEndValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new SqlTimestampToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblCalcEndValue, gridBagConstraints);

        lblWriteNode.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblWriteNode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblWriteNode, gridBagConstraints);

        lblWriteEdge.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblWriteEdge.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblWriteEdge, gridBagConstraints);

        chkWriteNode.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkWriteNode.text")); // NOI18N
        chkWriteNode.setContentAreaFilled(false);
        chkWriteNode.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.write_node}"),
                chkWriteNode,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkWriteNode, gridBagConstraints);

        chkWriteEdge.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkWriteEdge.text")); // NOI18N
        chkWriteEdge.setContentAreaFilled(false);
        chkWriteEdge.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.write_edge}"),
                chkWriteEdge,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkWriteEdge, gridBagConstraints);

        lblLastValues.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblLastValues.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblLastValues, gridBagConstraints);

        lblSaveMarked.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblSaveMarked.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblSaveMarked, gridBagConstraints);

        chkLastValues.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkLastValues.text")); // NOI18N
        chkLastValues.setContentAreaFilled(false);
        chkLastValues.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.last_values}"),
                chkLastValues,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkLastValues, gridBagConstraints);

        chkSaveMarked.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkSaveMarked.text")); // NOI18N
        chkSaveMarked.setContentAreaFilled(false);
        chkSaveMarked.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.save_marked}"),
                chkSaveMarked,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkSaveMarked, gridBagConstraints);

        lblMergeTriangles.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblMergeTriangles.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblMergeTriangles, gridBagConstraints);

        chkMergeTriangles.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkMergeTriangles.text")); // NOI18N
        chkMergeTriangles.setContentAreaFilled(false);
        chkMergeTriangles.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.merge_triangles}"),
                chkMergeTriangles,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkMergeTriangles, gridBagConstraints);

        lblMinCalcTriangleSize.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblMinCalcTriangleSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblMinCalcTriangleSize, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.min_calc_triangle_size}"),
                lblMinCalcTriangleSizeValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblMinCalcTriangleSizeValue, gridBagConstraints);

        lblTimeStepRestriction.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblTimeStepRestriction.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblTimeStepRestriction, gridBagConstraints);

        lblSaveVelocityCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblSaveVelocityCurves.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblSaveVelocityCurves, gridBagConstraints);

        lblSaveFlowCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblSaveFlowCurves.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblSaveFlowCurves, gridBagConstraints);

        chkTimeStepRestriction.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkTimeStepRestriction.text")); // NOI18N
        chkTimeStepRestriction.setContentAreaFilled(false);
        chkTimeStepRestriction.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.time_step_restriction}"),
                chkTimeStepRestriction,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkTimeStepRestriction, gridBagConstraints);

        chkSaveVelocityCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkSaveVelocityCurves.text")); // NOI18N
        chkSaveVelocityCurves.setContentAreaFilled(false);
        chkSaveVelocityCurves.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.save_velocity_curves}"),
                chkSaveVelocityCurves,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkSaveVelocityCurves, gridBagConstraints);

        chkSaveFlowCurves.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.chkSaveFlowCurves.text")); // NOI18N
        chkSaveFlowCurves.setContentAreaFilled(false);
        chkSaveFlowCurves.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.save_flow_curves}"),
                chkSaveFlowCurves,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(chkSaveFlowCurves, gridBagConstraints);

        lblResultSaveLimit.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblResultSaveLimit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblResultSaveLimit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.result_save_limit}"),
                lblResultSaveLimitValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblResultSaveLimitValue, gridBagConstraints);

        lblNumberOfThreads.setText(NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblNumberOfThreads.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblNumberOfThreads, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.number_of_threads}"),
                lblNumberOfThreadsValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblNumberOfThreadsValue, gridBagConstraints);

        lblQIn.setText(NbBundle.getMessage(GeocpmConfigurationEditor.class, "GeocpmConfigurationEditor.lblQIn.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblQIn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.q_out}"),
                lblQInValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblQInValue, gridBagConstraints);

        lblQOut.setText(NbBundle.getMessage(GeocpmConfigurationEditor.class, "GeocpmConfigurationEditor.lblQOut.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblQOut, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.q_in}"),
                lblQOutValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInfoContent.add(lblQOutValue, gridBagConstraints);

        pnlFiller.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlInfoContent.add(pnlFiller, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        pnlInfo.add(pnlInfoContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlInfo, gridBagConstraints);

        pnlMap.setLayout(new java.awt.GridBagLayout());

        panHeadInfo2.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo2.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo2.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo2.setLayout(new java.awt.FlowLayout());

        lblHeadingMap.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingMap.setText(org.openide.util.NbBundle.getMessage(
                GeocpmConfigurationEditor.class,
                "GeocpmConfigurationEditor.lblHeadingMap.text")); // NOI18N
        panHeadInfo2.add(lblHeadingMap);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlMap.add(panHeadInfo2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        pnlMap.add(map, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlMap, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        // noop
    }

    @Override
    public boolean prepareForSave() {
        if (cidsBean.getProperty("investigation_area") == null) {                              // NOI18N
            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(
                    GeocpmConfigurationEditor.class,
                    "GeocpmConfigurationEditor.prepareForSave().noInvestigationArea.message"), // NOI18N
                NbBundle.getMessage(
                    GeocpmConfigurationEditor.class,
                    "GeocpmConfigurationEditor.prepareForSave().noInvestigationArea.title"),   // NOI18N
                JOptionPane.INFORMATION_MESSAGE);

            return false;
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        try {
            final Geometry geom = (Geometry)cidsBean.getProperty("geom.geo_field"); // NOI18N
            final Geometry geom31466 = CrsTransformer.transformToGivenCrs(geom.getEnvelope(), SMSUtils.EPSG_WUPP);

            final XBoundingBox bbox = new XBoundingBox(geom31466, SMSUtils.EPSG_WUPP, true);
            final ActiveLayerModel mappingModel = new ActiveLayerModel();
            mappingModel.setSrs(new Crs(SMSUtils.EPSG_WUPP, SMSUtils.EPSG_WUPP, SMSUtils.EPSG_WUPP, true, true));
            mappingModel.addHome(bbox);

            final SimpleWMS ortho = new SimpleWMS(new SimpleWmsGetMapUrl(RunoffOutputManagerUI.ORTHO_URL));
            ortho.setName("Wuppertal Ortophoto"); // NOI18N

            final SimpleWMS beLayer = new SimpleWMS(new SimpleWmsGetMapUrl(BE_URL));
            beLayer.setName(NbBundle.getMessage(
                    GeocpmConfigurationEditor.class,
                    "GeocpmConfigurationEditor.initMap().beLayer.name")); // NOI18N

            final RetrievalListener rl = new RetrievalListener() {

                    private final transient String text = lblHeadingMap.getText();

                    @Override
                    public void retrievalStarted(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(
                                        text
                                                + NbBundle.getMessage(
                                                    GeocpmConfigurationEditor.class,
                                                    "GeocpmConfigurationEditor.initMap().retrievalListener.retrievalStarted.loadingSuffix")); // NOI18N
                                }
                            });
                    }

                    @Override
                    public void retrievalProgress(final RetrievalEvent e) {
                        // noop
                    }

                    @Override
                    public void retrievalComplete(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(
                                        text
                                                + NbBundle.getMessage(
                                                    GeocpmConfigurationEditor.class,
                                                    "GeocpmConfigurationEditor.initMap().retrievalListener.retrievalStarted.finishedSuffix")); // NOI18N
                                }
                            });
                    }

                    @Override
                    public void retrievalAborted(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(
                                        text
                                                + NbBundle.getMessage(
                                                    GeocpmConfigurationEditor.class,
                                                    "GeocpmConfigurationEditor.initMap().retrievalListener.retrievalStarted.abortedSuffix")); // NOI18N
                                }
                            });
                    }

                    @Override
                    public void retrievalError(final RetrievalEvent e) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    lblHeadingMap.setText(
                                        text
                                                + NbBundle.getMessage(
                                                    GeocpmConfigurationEditor.class,
                                                    "GeocpmConfigurationEditor.initMap().retrievalListener.retrievalStarted.errorSuffix"));
                                }
                            });
                    }
                };

            beLayer.addRetrievalListener(rl);

            mappingModel.addLayer(ortho);
            mappingModel.addLayer(beLayer);

            map.setMappingModel(mappingModel);
            map.gotoInitialBoundingBox();

            map.unlock();
            map.setInteractionMode(MappingComponent.ZOOM);
            map.addCustomInputListener("MUTE", new PBasicInputEventHandler() { // NOI18N

                    @Override
                    public void mouseClicked(final PInputEvent evt) {
                        try {
                            if (evt.getClickCount() > 1) {
                                final SimpleWMS tin = new SimpleWMS(new SimpleWmsGetMapUrl(TIN_URL));
                                final SimpleWMS be = new SimpleWMS(new SimpleWmsGetMapUrl(BE_URL));
                                tin.setName(
                                    NbBundle.getMessage(
                                        GeocpmConfigurationEditor.class,
                                        "GeocpmConfigurationEditor.initMap().inputListener.mouseClicked.tinLayer.name")); // NOI18N
                                be.setName(
                                    NbBundle.getMessage(
                                        GeocpmConfigurationEditor.class,
                                        "GeocpmConfigurationEditor.initMap().beLayer.name"));                             // NOI18N

                                CismapBroker.getInstance().getMappingComponent().getMappingModel().addLayer(tin);
                                CismapBroker.getInstance().getMappingComponent().getMappingModel().addLayer(be);
                                SMSUtils.showMappingComponent();
                                CismapBroker.getInstance().getMappingComponent().gotoBoundingBoxWithHistory(bbox);
                            }
                        } catch (final Exception ex) {
                            LOG.warn("cannot add layer to map", ex); // NOI18N
                        }
                    }
                });
            map.setInteractionMode("MUTE");                          // NOI18N
        } catch (final Exception e) {
            LOG.error("cannot initialise mapping component", e);     // NOI18N
        }
    }
}
