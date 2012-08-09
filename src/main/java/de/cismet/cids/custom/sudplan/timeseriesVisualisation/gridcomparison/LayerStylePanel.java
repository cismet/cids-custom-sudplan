/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Color;
import java.awt.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyle.Entry;

import de.cismet.tools.gui.ColorEditor;
import de.cismet.tools.gui.ColorIcon;
import de.cismet.tools.gui.ColorRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class LayerStylePanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LayerStylePanel.class);

    //~ Instance fields --------------------------------------------------------

    private final transient DocumentListener changeModelNameListener;
    private final transient EnableButtonListener enableButtonListener;
    private final transient ColorMapModel colorMapModel;
    private LayerStyle layerStyle;
    private boolean dirty;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddColor;
    private javax.swing.JButton btnRemoveColor;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lblColorMap;
    private javax.swing.JLabel lblName;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JScrollPane scpColorMap;
    private javax.swing.JTable tblColorMap;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LayerStylePanel.
     */
    public LayerStylePanel() {
        colorMapModel = new ColorMapModel();
        changeModelNameListener = new ChangeModelListener();
        layerStyle = new LayerStyle();

        setDirty(false);

        initComponents();

        enableButtonListener = new EnableButtonListener(btnRemoveColor);

        txtName.getDocument()
                .addDocumentListener(WeakListeners.document(changeModelNameListener, txtName.getDocument()));

        tblColorMap.setDefaultEditor(Color.class, new ColorEditor());
        tblColorMap.setDefaultRenderer(Color.class, new ColorRenderer(false));
        tblColorMap.getSelectionModel()
                .addListSelectionListener(WeakListeners.create(
                        ListSelectionListener.class,
                        enableButtonListener,
                        tblColorMap.getSelectionModel()));
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

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblColorMap = new javax.swing.JLabel();
        pnlControls = new javax.swing.JPanel();
        btnAddColor = new javax.swing.JButton();
        btnRemoveColor = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        scpColorMap = new javax.swing.JScrollPane();
        tblColorMap = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        lblName.setText(org.openide.util.NbBundle.getMessage(LayerStylePanel.class, "LayerStylePanel.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(lblName, gridBagConstraints);

        txtName.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        add(txtName, gridBagConstraints);

        lblColorMap.setText(org.openide.util.NbBundle.getMessage(
                LayerStylePanel.class,
                "LayerStylePanel.lblColorMap.text")); // NOI18N
        lblColorMap.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        add(lblColorMap, gridBagConstraints);

        pnlControls.setLayout(new java.awt.GridBagLayout());

        btnAddColor.setIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/de/cismet/cids/custom/sudplan/timeseriesVisualisation/gridcomparison/edit_add.png"))); // NOI18N
        btnAddColor.setEnabled(false);
        btnAddColor.setFocusPainted(false);
        btnAddColor.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddColorActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        pnlControls.add(btnAddColor, gridBagConstraints);

        btnRemoveColor.setIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/de/cismet/cids/custom/sudplan/timeseriesVisualisation/gridcomparison/edit_remove.png"))); // NOI18N
        btnRemoveColor.setEnabled(false);
        btnRemoveColor.setFocusPainted(false);
        btnRemoveColor.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveColorActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 5);
        pnlControls.add(btnRemoveColor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlControls.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(pnlControls, gridBagConstraints);

        tblColorMap.setModel(colorMapModel);
        tblColorMap.setEnabled(false);
        tblColorMap.setFillsViewportHeight(true);
        tblColorMap.setShowHorizontalLines(false);
        scpColorMap.setViewportView(tblColorMap);
        tblColorMap.getColumnModel()
                .getSelectionModel()
                .setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        add(scpColorMap, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddColorActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddColorActionPerformed
        colorMapModel.addEmptyRow();
    }                                                                               //GEN-LAST:event_btnAddColorActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveColorActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveColorActionPerformed
        final int[] selectedRows = tblColorMap.getSelectedRows();
        colorMapModel.removeRows(selectedRows);

        setDirty(dirty |= selectedRows.length > 0);
    } //GEN-LAST:event_btnRemoveColorActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public LayerStyle saveLayerStyle() {
        layerStyle.setName(txtName.getText());
        layerStyle.setColorMap(colorMapModel.getColorMap());

        setDirty(false);

        return layerStyle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  layerStyle  DOCUMENT ME!
     */
    public void setLayerStyle(final LayerStyle layerStyle) {
        if (layerStyle == null) {
            return;
        }

        this.layerStyle = layerStyle;

        txtName.setText(layerStyle.getName());
        colorMapModel.setColorMap(layerStyle.getColorMap());

        setDirty(false);

        setEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dirty  DOCUMENT ME!
     */
    private void setDirty(final boolean dirty) {
        final boolean oldDirty = this.dirty;

        if (this.dirty ^ dirty) {
            this.dirty = dirty;
            firePropertyChange("dirty", oldDirty, dirty);
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        txtName.setEnabled(enabled);
        tblColorMap.setEnabled(enabled);
        btnAddColor.setEnabled(enabled);

        super.setEnabled(enabled);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class ChangeModelListener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            updateModelproperty();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            updateModelproperty();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            updateModelproperty();
        }

        /**
         * DOCUMENT ME!
         */
        private void updateModelproperty() {
            setDirty(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class ColorMapModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final List<Entry> colorMap = new LinkedList<Entry>();

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  colorMap  DOCUMENT ME!
         */
        public void setColorMap(final List<Entry> colorMap) {
            this.colorMap.clear();
            this.colorMap.addAll(colorMap);

            fireTableStructureChanged();
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public List<Entry> getColorMap() {
            return colorMap;
        }

        @Override
        public int getRowCount() {
            return colorMap.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (colorMap.size() <= rowIndex) {
                return null;
            }

            final Entry entry = colorMap.get(rowIndex);
            if (columnIndex == 0) {
                return entry.getValue();
            } else {
                return entry.getColor();
            }
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == 1) {
                return Color.class;
            }

            return Double.class;
        }

        @Override
        public String getColumnName(final int column) {
            if (column == 1) {
                return NbBundle.getMessage(
                        ColorMapModel.class,
                        "LayerStylePanel.ColorMapModel.getColumnName(int).color");
            }

            return NbBundle.getMessage(ColorMapModel.class, "LayerStylePanel.ColorMapModel.getColumnName(int).value");
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return true;
        }

        @Override
        public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
            if (((columnIndex == 0) && !(aValue instanceof Double))
                        || ((columnIndex == 1) && !(aValue instanceof Color))) {
                return;
            }
            if (colorMap.size() <= rowIndex) {
                return;
            }

            final Entry entry = colorMap.get(rowIndex);

            if (columnIndex == 0) {
                entry.setValue((Double)aValue);
                Collections.sort(colorMap);
                fireTableStructureChanged();
            } else if (columnIndex == 1) {
                entry.setColor((Color)aValue);
                fireTableCellUpdated(rowIndex, columnIndex);
            }

            setDirty(true);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  selectedRow  DOCUMENT ME!
         */
        public void removeRow(final int selectedRow) {
            if (colorMap.size() <= selectedRow) {
                return;
            }

            colorMap.remove(selectedRow);

            fireTableRowsDeleted(selectedRow, selectedRow);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  selectedRows  DOCUMENT ME!
         */
        public void removeRows(final int[] selectedRows) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            final List<Entry> entriesToRemove = new LinkedList<Entry>();
            for (final int selectedRow : selectedRows) {
                if (colorMap.size() <= selectedRow) {
                    continue;
                }

                entriesToRemove.add(colorMap.get(selectedRow));

                if (min > selectedRow) {
                    min = selectedRow;
                }
                if (max < selectedRow) {
                    max = selectedRow;
                }
            }

            colorMap.removeAll(entriesToRemove);

            fireTableRowsDeleted(min, max);
        }

        /**
         * DOCUMENT ME!
         */
        public void addEmptyRow() {
            colorMap.add(new Entry(new Double(0D), Color.green));

            Collections.sort(colorMap);

            fireTableStructureChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class EnableButtonListener implements ListSelectionListener {

        //~ Instance fields ----------------------------------------------------

        private final JButton button;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EnableButtonListener object.
         *
         * @param  button  DOCUMENT ME!
         */
        public EnableButtonListener(final JButton button) {
            this.button = button;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            button.setEnabled(e.getLastIndex() > 0);
        }
    }
}
