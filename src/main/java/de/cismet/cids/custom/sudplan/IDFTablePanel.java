/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.Exceptions;

import java.awt.Component;

import java.io.IOException;
import java.io.StringReader;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import de.cismet.cids.custom.sudplan.converter.Euler2ComputationWizardAction;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Rainevent;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class IDFTablePanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(IDFTablePanel.class);

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean cidsBeanIDFcurve;
    private transient int selectedColIndex;
    private transient int selectedRowStart;
    private transient int selectedRowEnd;
    private final JPopupMenu popup;
    private final JMenuItem menuItem;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblIDF;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form IDFTablePanel.
     *
     * @param  cidsBeanIDFcurve  curve DOCUMENT ME!
     */
    public IDFTablePanel(final CidsBean cidsBeanIDFcurve) {
        this.cidsBeanIDFcurve = cidsBeanIDFcurve;

        initComponents();

        init();

        popup = new JPopupMenu();
        popup.add(menuItem = new JMenuItem(
                    org.openide.util.NbBundle.getMessage(
                        IDFTablePanel.class,
                        "IDFTablePanle(CidsBean).menuItem.computation")));
        menuItem.addActionListener(new Euler2ComputationWizardAction(this));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBeanIDFcurve() {
        return cidsBeanIDFcurve;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeanIDFcurve  DOCUMENT ME!
     */
    public void setCidsBeanIDFcurve(final CidsBean cidsBeanIDFcurve) {
        this.cidsBeanIDFcurve = cidsBeanIDFcurve;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSelectedColIndex() {
        return selectedColIndex;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedColIndex  DOCUMENT ME!
     */
    public void setSelectedColIndex(final int selectedColIndex) {
        this.selectedColIndex = selectedColIndex;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSelectedRowEnd() {
        return selectedRowEnd;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedRowEnd  DOCUMENT ME!
     */
    public void setSelectedRowEnd(final int selectedRowEnd) {
        this.selectedRowEnd = selectedRowEnd;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSelectedRowStart() {
        return selectedRowStart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedRowStart  DOCUMENT ME!
     */
    public void setSelectedRowStart(final int selectedRowStart) {
        this.selectedRowStart = selectedRowStart;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void init() {
        tblIDF.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblIDF.getTableHeader().setReorderingAllowed(false);
        tblIDF.setCellSelectionEnabled(true);

        final String json = (String)cidsBeanIDFcurve.getProperty("uri"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();
        final IDFCurve curve;
        try {
            curve = mapper.readValue(new StringReader(json), IDFCurve.class);
        } catch (IOException ex) {
            final String message = "cannot read idf data from uri";      // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }

        final List<Integer> frequencies = curve.getFrequencies();
        final Object[] columnHeaders = new Object[frequencies.size() + 1];
        columnHeaders[0] = "Duration / Frequency";
        System.arraycopy(frequencies.toArray(), 0, columnHeaders, 1, columnHeaders.length - 1);

        final DefaultTableModel dtm = new DefaultTableModel(curve.getDurationIntensityRows(), columnHeaders) {

                @Override
                public boolean isCellEditable(final int row, final int column) {
                    return false;
                }
            };
        tblIDF.setModel(dtm);

        final IDFCellRenderer renderer = new IDFCellRenderer();

        final TableColumnModel tcm = tblIDF.getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); ++i) {
            tcm.getColumn(i).setCellRenderer(renderer);
            tcm.getColumn(i).setHeaderRenderer(renderer);
        }

        tblIDF.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent lse) {
                    if (lse.getValueIsAdjusting()) {
                        return;
                    }
                    final int colIndexStart = tblIDF.getSelectedColumn();
                    final int colIndexEnd = tblIDF.getColumnModel().getSelectionModel().getMaxSelectionIndex();
                    final int rowIndexStart = tblIDF.getSelectedRow();
                    final int rowIndexEnd = tblIDF.getSelectionModel().getMaxSelectionIndex();

                    if ((colIndexStart != colIndexEnd) || (rowIndexStart != 0)) {
                        menuItem.setEnabled(false);
                    } else {
                        menuItem.setEnabled(true);

                        setSelectedColIndex(colIndexEnd);
                        setSelectedRowStart(rowIndexStart);
                        setSelectedRowEnd(rowIndexEnd);

                        /*
                         * final List<Double> data = new ArrayList<Double>();
                         *
                         * for (int i = rowIndexStart; i <= rowIndexEnd; i++) { final Double value =
                         * (Double)curve.getDurationIntensityRows()[i][colIndexEnd]; if (value != null) {
                         * data.add(value); } }
                         **/

                    }
                }
            });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        tblIDF = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        tblIDF.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        tblIDF.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    tblIDFMousePressed(evt);
                }
                @Override
                public void mouseReleased(final java.awt.event.MouseEvent evt) {
                    tblIDFMouseReleased(evt);
                }
            });
        jScrollPane1.setViewportView(tblIDF);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblIDFMouseReleased(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblIDFMouseReleased
        showPopup(evt);
    }                                                                       //GEN-LAST:event_tblIDFMouseReleased

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblIDFMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblIDFMousePressed
        showPopup(evt);
    }                                                                      //GEN-LAST:event_tblIDFMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void showPopup(final java.awt.event.MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class IDFCellRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            final Component c;

            if ((column == 0) || (row == -1)) {
                final JButton b = new JButton(value.toString());
                if (column == 0) {
                    b.setHorizontalAlignment(JButton.RIGHT);
                } else {
                    b.setHorizontalAlignment(JButton.CENTER);
                }

                c = b;
            } else {
                c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    final JLabel label = (JLabel)c;

                    label.setHorizontalAlignment(JLabel.CENTER);
                }
            }

            return c;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class IDFSelectionModel extends DefaultListSelectionModel {

        // TODO: tbd
    }
}
