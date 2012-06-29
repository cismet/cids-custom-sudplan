/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import com.lowagie.text.Rectangle;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import java.io.IOException;
import java.io.StringReader;

import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import de.cismet.cids.custom.sudplan.converter.EulerComputationWizardAction;

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
    private static final int HEADER_TEXT_VERTICAL = 0;
    private static final int HEADER_TEXT_HORIZONTAL = 1;

    //~ Instance fields --------------------------------------------------------

    private transient CidsBean cidsBeanIDFcurve;
    private transient int selectedColIndex;
    private transient int selectedRowStart;
    private transient int selectedRowEnd;
    private final JPopupMenu popup;
    private final JMenuItem menuItem;
    private final ActionListener popupL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane spIDF;
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
        popupL = new EulerComputationWizardAction(this);
        menuItem.addActionListener(WeakListeners.create(ActionListener.class, popupL, menuItem));
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

        // (new TableRowHeader(tblIDF, spIDF));

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
        columnHeaders[0] = "<html>Frequency /</br>Duration</html>";
        System.arraycopy(frequencies.toArray(), 0, columnHeaders, 1, columnHeaders.length - 1);

        final IDFTableModel model = new IDFTableModel(curve.getDurationIntensityRows(), columnHeaders);

//        final DefaultTableModel dtm = new DefaultTableModel(curve.getDurationIntensityRows(), columnHeaders) {
//
//                @Override
//                public boolean isCellEditable(final int row, final int column) {
//                    return false;
//                }
//            };
        tblIDF.setModel(model);

        final IDFCellRenderer renderer = new IDFCellRenderer();

        final TableColumnModel tcm = tblIDF.getColumnModel();
        for (int i = 0; i < tcm.getColumnCount(); ++i) {
            tcm.getColumn(i).setCellRenderer(renderer);
            tcm.getColumn(i).setHeaderRenderer(renderer);
        }
        final TableHeaderExtensions leftHeader = new TableHeaderExtensions(tblIDF, spIDF, HEADER_TEXT_VERTICAL);
        final TableHeaderExtensions topHeader = new TableHeaderExtensions(tblIDF, spIDF, HEADER_TEXT_HORIZONTAL);

//        spIDF.setPreferredSize(new Dimension(tblIDF.getPreferredSize().width, h));

        spIDF.setRowHeaderView(leftHeader);
        spIDF.setColumnHeaderView(new JButton("Frequence") {

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(20, 100);
                }
            }); // topHeader);

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

        spIDF = new javax.swing.JScrollPane();
        tblIDF = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        spIDF.setPreferredSize(new java.awt.Dimension(452, 300));

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
        spIDF.setViewportView(tblIDF);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spIDF, gridBagConstraints);
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
    private static final class IDFTableModel implements TableModel {

        //~ Instance fields ----------------------------------------------------

        private Object[][] data;
        private Object[] columns;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new IDFTableModel object.
         *
         * @param  data     DOCUMENT ME!
         * @param  columns  DOCUMENT ME!
         */
        public IDFTableModel(final Object[][] data, final Object[] columns) {
            this.data = data;
            this.columns = columns;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            if ((data != null) && (data.length > 0)) {
                return data.length;
            }
            return 0;
        }

        @Override
        public int getColumnCount() {
            if ((columns != null) && (columns.length > 0)) {
                return columns.length;
            }
            return 0;
        }

        @Override
        public String getColumnName(final int i) {
            if ((columns != null) && (i < columns.length)) {
                final String frequence = String.valueOf(columns[i]);

                final StringBuilder s = new StringBuilder(frequence);
                if (frequence.equals("1")) {
                    s.append(" year");
                } else {
                    s.append(" years");
                }
                return s.toString();
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(final int i) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(final int i, final int i1) {
            return false;
        }

        @Override
        public Object getValueAt(final int i, final int i1) {
            if ((data != null) && (i < data.length) && (i1 < data[i].length)) {
                final String value = String.valueOf(data[i][i1]);
                final StringBuilder s = new StringBuilder(value);
                if ((i1 == 0) && (i >= 0)) {
                    s.append(" min");
                }
                return s.toString();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object o, final int i, final int i1) {
        }

        @Override
        public void addTableModelListener(final TableModelListener tl) {
        }

        @Override
        public void removeTableModelListener(final TableModelListener tl) {
        }
    }

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

                    label.setHorizontalAlignment(JLabel.RIGHT);
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TableHeaderExtensions extends JComponent implements AdjustmentListener {

        //~ Instance fields ----------------------------------------------------

        private JTable table;
        private JScrollPane parent;
        private Header header;
        private int position;
//        private TableHeadersExtension headerLeft;
//        private TableHeadersExtension headerRight;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TableRowHeader object.
         *
         * @param  table         DOCUMENT ME!
         * @param  parent        DOCUMENT ME!
         * @param  textPosition  DOCUMENT ME!
         */
        public TableHeaderExtensions(final JTable table, final JScrollPane parent, final int textPosition) {
            this.table = table;
            this.parent = parent;
            this.position = textPosition;
            this.parent.getVerticalScrollBar().addAdjustmentListener(this);
            this.parent.getHorizontalScrollBar().addAdjustmentListener(this);
            header = new Header(20, 40, textPosition, "Duration");
            setPreferredSize(new Dimension(20, 60));
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
//            final java.awt.Rectangle rec = TableRowHeader.this.getViewRect();
            if (position == HEADER_TEXT_VERTICAL) {
                int height = 0; // table.getRowHeight(0);
                for (int i = 0; i < table.getRowCount(); i++) {
                    height += table.getRowHeight(i);
                }
                SwingUtilities.paintComponent(g, header, this, 0, 0, 20, height);
            } else {
                int width = 0;
                for (int i = 0; i < table.getColumnCount(); i++) {
                    width += table.getColumnModel().getColumn(i).getWidth();
                }
                SwingUtilities.paintComponent(g, header, this, 0, 0, width, 20);
            }
//            setPreferredSize(new Dimension(20, height));
//            add(headerLeft);
//            headerLeft.setPreferredSize(getPreferredSize());
        }

        @Override
        public void adjustmentValueChanged(final AdjustmentEvent ae) {
            repaint();
        }

        //~ Inner Classes ------------------------------------------------------

        /**
         * Stellt den Knopf für eine Reihe dar.
         *
         * @version  $Revision$, $Date$
         */
        private class Header extends JButton {

            //~ Constructors ---------------------------------------------------

            /**
             * DOCUMENT ME!
             *
             * @param  width     DOCUMENT ME!
             * @param  height    row DOCUMENT ME!
             * @param  position  DOCUMENT ME!
             * @param  title     DOCUMENT ME!
             */
            public Header(final int width, final int height, final int position, final String title) {
                setText(title); // "Duration in min");
                this.setText("Duration");
                this.setForeground(tblIDF.getForeground());
                this.setBackground(tblIDF.getBackground());
                this.setHorizontalAlignment(SwingConstants.CENTER);
                this.setVerticalTextPosition(SwingConstants.CENTER);
                this.setHorizontalTextPosition(SwingConstants.CENTER);
                this.setVerticalAlignment(SwingConstants.CENTER);
                this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

                int h = 0;
                for (int i = 0; i < tblIDF.getRowCount(); i++) {
                    h += tblIDF.getRowHeight(i);
                }
                h += tblIDF.getTableHeader().getHeight();

                Font font = this.getFont();
//                TextLayout thisTl = new TextLayout(title, font,
                font = font.deriveFont(Font.BOLD);
                if (position == HEADER_TEXT_VERTICAL) {
                    final AffineTransform at = new AffineTransform();
                    at.rotate(-1.57d);
                    at.translate(-30, 5);
                    font = font.deriveFont(at);
                }
                this.setFont(font);
                this.setPreferredSize(new Dimension(20, 20));

//                setHorizontalAlignment(CENTER);
//                setVerticalTextPosition(CENTER);
//                setHorizontalTextPosition(CENTER);
//                setVerticalAlignment(CENTER);
//                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            }
        }
    }
}
