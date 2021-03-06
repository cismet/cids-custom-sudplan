/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import org.jdom.Element;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

import java.awt.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.sudplan.SudplanOptionsCategory;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyle.Entry;

import de.cismet.lookupoptions.AbstractOptionsPanel;
import de.cismet.lookupoptions.OptionsPanelController;

import de.cismet.tools.configuration.NoWriteError;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = OptionsPanelController.class)
public class LayerStylesOptionsPanel extends AbstractOptionsPanel implements OptionsPanelController {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LayerStylesOptionsPanel.class);

    private static final String OPTION_NAME = NbBundle.getMessage(
            LayerStylesOptionsPanel.class,
            "LayerStylesOptionsPanel.OPTION_NAME"); // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final DirtyListener dirtyListener;
    private final DefaultListModel layerStylesModel;
    private boolean somethingChanged = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddLayerStyle;
    private javax.swing.JButton btnRemoveLayerStyle;
    private javax.swing.JButton btnSaveLayerStyle;
    private javax.swing.Box.Filler gluFiller;
    private javax.swing.JLabel lblLayerStyles;
    private javax.swing.JLabel lblNoteGridComparator;
    private javax.swing.JList lstLayerStyles;
    private javax.swing.JPanel pnlControls;
    private de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStylePanel pnlLayerStyle;
    private javax.swing.JPanel pnlLayerStyles;
    private javax.swing.JScrollPane scpLayerStyles;
    private javax.swing.JSplitPane splContainer;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LayerStylesOptionsPanel.
     */
    public LayerStylesOptionsPanel() {
        super(OPTION_NAME, SudplanOptionsCategory.class);

        layerStylesModel = new DefaultListModel();

        initComponents();

        dirtyListener = new DirtyListener();
        pnlLayerStyle.addPropertyChangeListener(WeakListeners.propertyChange(dirtyListener, pnlLayerStyle));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getOrder() {
        return 2;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        splContainer = new javax.swing.JSplitPane();
        pnlLayerStyles = new javax.swing.JPanel();
        scpLayerStyles = new javax.swing.JScrollPane();
        lstLayerStyles = new javax.swing.JList();
        pnlControls = new javax.swing.JPanel();
        btnSaveLayerStyle = new javax.swing.JButton();
        btnAddLayerStyle = new javax.swing.JButton();
        btnRemoveLayerStyle = new javax.swing.JButton();
        gluFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        lblLayerStyles = new javax.swing.JLabel();
        pnlLayerStyle = new de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStylePanel();
        lblNoteGridComparator = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new java.awt.BorderLayout(0, 10));

        splContainer.setBorder(null);
        splContainer.setDividerSize(0);
        splContainer.setResizeWeight(0.5);

        pnlLayerStyles.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        pnlLayerStyles.setLayout(new java.awt.BorderLayout());

        lstLayerStyles.setModel(layerStylesModel);
        lstLayerStyles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstLayerStyles.setCellRenderer(new LayerStyleRenderer());
        lstLayerStyles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstLayerStylesValueChanged(evt);
                }
            });
        scpLayerStyles.setViewportView(lstLayerStyles);

        pnlLayerStyles.add(scpLayerStyles, java.awt.BorderLayout.CENTER);

        pnlControls.setLayout(new java.awt.GridBagLayout());

        btnSaveLayerStyle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/de/cismet/cids/custom/sudplan/timeseriesVisualisation/gridcomparison/edit_save.png"))); // NOI18N
        btnSaveLayerStyle.setEnabled(false);
        btnSaveLayerStyle.setFocusPainted(false);
        btnSaveLayerStyle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSaveLayerStyleActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 2);
        pnlControls.add(btnSaveLayerStyle, gridBagConstraints);

        btnAddLayerStyle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/de/cismet/cids/custom/sudplan/timeseriesVisualisation/gridcomparison/edit_add.png"))); // NOI18N
        btnAddLayerStyle.setFocusPainted(false);
        btnAddLayerStyle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddLayerStyleActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 2);
        pnlControls.add(btnAddLayerStyle, gridBagConstraints);

        btnRemoveLayerStyle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/de/cismet/cids/custom/sudplan/timeseriesVisualisation/gridcomparison/edit_remove.png"))); // NOI18N
        btnRemoveLayerStyle.setEnabled(false);
        btnRemoveLayerStyle.setFocusPainted(false);
        btnRemoveLayerStyle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveLayerStyleActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        pnlControls.add(btnRemoveLayerStyle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlControls.add(gluFiller, gridBagConstraints);

        pnlLayerStyles.add(pnlControls, java.awt.BorderLayout.PAGE_END);

        lblLayerStyles.setText(org.openide.util.NbBundle.getMessage(
                LayerStylesOptionsPanel.class,
                "LayerStylesOptionsPanel.lblLayerStyles.text")); // NOI18N
        lblLayerStyles.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 5, 0));
        pnlLayerStyles.add(lblLayerStyles, java.awt.BorderLayout.PAGE_START);

        splContainer.setLeftComponent(pnlLayerStyles);

        pnlLayerStyle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        splContainer.setRightComponent(pnlLayerStyle);

        add(splContainer, java.awt.BorderLayout.CENTER);

        lblNoteGridComparator.setText(org.openide.util.NbBundle.getMessage(
                LayerStylesOptionsPanel.class,
                "LayerStylesOptionsPanel.lblNoteGridComparator.text")); // NOI18N
        add(lblNoteGridComparator, java.awt.BorderLayout.PAGE_END);
    }                                                                   // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSaveLayerStyleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSaveLayerStyleActionPerformed
        final LayerStyle layerStyle = pnlLayerStyle.saveLayerStyle();

        if (!layerStylesModel.contains(layerStyle)) {
            layerStylesModel.addElement(layerStyle);
        }

        showSelectedLayerStyle();

        somethingChanged = true;
    } //GEN-LAST:event_btnSaveLayerStyleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLayerStyleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddLayerStyleActionPerformed
        final LayerStyle layerStyle = new LayerStyle("New layer style", new LinkedList<Entry>());
        layerStylesModel.addElement(layerStyle);

        if (lstLayerStyles.getSelectionModel().isSelectionEmpty()) {
            lstLayerStyles.setSelectedIndex(layerStylesModel.indexOf(layerStyle));
        }
    } //GEN-LAST:event_btnAddLayerStyleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLayerStyleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveLayerStyleActionPerformed
        final ListSelectionListener[] listeners = lstLayerStyles.getListSelectionListeners();

        for (final ListSelectionListener listener : listeners) {
            lstLayerStyles.removeListSelectionListener(listener);
        }

        final Object[] selectedValues = lstLayerStyles.getSelectedValues();
        for (final Object selectedValue : selectedValues) {
            layerStylesModel.removeElement(selectedValue);
        }

        for (final ListSelectionListener listener : listeners) {
            lstLayerStyles.addListSelectionListener(listener);
        }

        showSelectedLayerStyle();

        somethingChanged = true;
    } //GEN-LAST:event_btnRemoveLayerStyleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstLayerStylesValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstLayerStylesValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }

        showSelectedLayerStyle();
    } //GEN-LAST:event_lstLayerStylesValueChanged

    /**
     * DOCUMENT ME!
     */
    protected void showSelectedLayerStyle() {
        if (pnlLayerStyle.isDirty() && btnSaveLayerStyle.isEnabled()) {
            // We want to avoid saving invalid information.
            final int saveLayerStyle = JOptionPane.showConfirmDialog(
                    this,
                    NbBundle.getMessage(
                        LayerStylesOptionsPanel.class,
                        "LayerStylesOptionsPanel.showSelectedLayerStyle().JOptionPane.confirmSave.message"), // NOI18N
                    NbBundle.getMessage(
                        LayerStylesOptionsPanel.class,
                        "LayerStylesOptionsPanel.showSelectedLayerStyle().JOptionPane.confirmSave.title"), // NOI18N
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (saveLayerStyle == JOptionPane.YES_OPTION) {
                pnlLayerStyle.saveLayerStyle();
            }
        }

        final int[] selectedIndices = lstLayerStyles.getSelectedIndices();

        btnAddLayerStyle.setEnabled(selectedIndices.length > 0);
        btnSaveLayerStyle.setEnabled(false);
        btnRemoveLayerStyle.setEnabled(selectedIndices.length > 0);

        pnlLayerStyle.setEnabled(true);
        if (selectedIndices.length > 1) {
            pnlLayerStyle.setEnabled(false);
        } else if (selectedIndices.length == 0) {
            pnlLayerStyle.setLayerStyle(new LayerStyle("New layer style", new LinkedList<Entry>()));
        } else {
            pnlLayerStyle.setLayerStyle((LayerStyle)layerStylesModel.elementAt(selectedIndices[0]));
        }

        revalidate();
        repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void reset() {
        pnlLayerStyle.setLayerStyle(new LayerStyle());
        lstLayerStyles.clearSelection();
        showSelectedLayerStyle();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enabled  DOCUMENT ME!
     */
    public void enableSaveButton(final boolean enabled) {
        btnSaveLayerStyle.setEnabled(enabled);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<LayerStyle> getGrids() {
        final Collection<LayerStyle> result = new ArrayList<LayerStyle>(layerStylesModel.size());

        final Enumeration grids = layerStylesModel.elements();
        while (grids.hasMoreElements()) {
            result.add((LayerStyle)grids.nextElement());
        }

        return result;
    }

    @Override
    public void applyChanges() {
        final List<LayerStyle> layerStyles = new LinkedList<LayerStyle>();

        for (final Object layerStyleObj : layerStylesModel.toArray()) {
            layerStyles.add((LayerStyle)layerStyleObj);
        }

        LayerStyles.instance().setLayerStyles(layerStyles);

        somethingChanged = false;
    }

    @Override
    public boolean isChanged() {
        return somethingChanged;
    }

    @Override
    public void update() {
        layerStylesModel.clear();

        for (final LayerStyle layerStyle : LayerStyles.instance().getLayerStyles()) {
            layerStylesModel.addElement(layerStyle);
        }
    }

    @Override
    public void configure(final Element parent) {
        LayerStyles.instance().configure(parent);
    }

    @Override
    public Element getConfiguration() throws NoWriteError {
        return LayerStyles.instance().getConfiguration();
    }

    @Override
    public void masterConfigure(final Element parent) {
        LayerStyles.instance().masterConfigure(parent);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class LayerStyleRenderer extends JLabel implements ListCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GridRenderer object.
         */
        public LayerStyleRenderer() {
            setOpaque(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            if (isSelected) {
                setBackground(UIManager.getDefaults().getColor("List.selectionBackground")); // NOI18N
                setForeground(UIManager.getDefaults().getColor("List.selectionForeground")); // NOI18N
            } else {
                setBackground(UIManager.getDefaults().getColor("List.background"));          // NOI18N
                setForeground(UIManager.getDefaults().getColor("List.foreground"));          // NOI18N
            }

            if (value instanceof LayerStyle) {
                setText(((LayerStyle)value).getName());
            }

            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class DirtyListener implements PropertyChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if ("dirty".equalsIgnoreCase(evt.getPropertyName()) && (evt.getNewValue() instanceof Boolean)) {
                final Boolean dirty = (Boolean)evt.getNewValue();

                if (dirty) {
                    somethingChanged = true;
                }

                enableSaveButton(dirty);
            }
        }
    }
}
