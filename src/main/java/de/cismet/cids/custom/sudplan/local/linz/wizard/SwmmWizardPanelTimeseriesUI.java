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

import Sirius.server.localserver.attribute.ClassAttribute;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.local.wupp.WizardInitialisationException;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelTimeseriesUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelTimeseriesUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmWizardPanelTimeseries model;
    private transient TimeseriesTableModel timeseriesTableModel;
    private transient HashSet<Integer> lastStationIds = new HashSet<Integer>();
    private transient boolean lastForecast;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPaneTimeseries;
    private javax.swing.JTable tblTimeseries;
    private javax.swing.JPanel timeseriesPanel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public SwmmWizardPanelTimeseriesUI(final SwmmWizardPanelTimeseries model) throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                SwmmWizardPanelTimeseries.class,
                "SwmmWizardPanelTimeseries.this.name")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initialising user interface");
        }
        try {
            if ((model.isForecast() == this.lastForecast)
                        && (this.lastStationIds.size() == model.getStationIds().size())
                        && this.lastStationIds.containsAll(model.getStationIds())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("selected stations (" + model.getStationIds().size() + ") did not change, "
                                + "no need to update the timeseries table");
                }
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("selected stations changed, updating the timeseries table");
            }
            this.lastForecast = model.isForecast();
            this.lastStationIds.clear();
            this.lastStationIds.addAll(model.getStationIds());

            this.initTimeseries(model.getStationIds(), model.isForecast());
            this.timeseriesTableModel.setSelectedTimeseries(model.getTimeseriesIds());
            this.tblTimeseries.setModel(this.timeseriesTableModel);
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   stationIds  DOCUMENT ME!
     * @param   forecast    DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initTimeseries(final List<Integer> stationIds, final boolean forecast)
            throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_TIMESERIES);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch timeseries metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        assert stationIds.size() > 0 : "no station ids defined";
        sb.append(" WHERE station IN (");

        int i = 0;
        for (final int stationId : stationIds) {
            i++;
            sb.append(stationId);
            if (i < stationIds.size()) {
                sb.append(", ");
            }
        }

        sb.append(") ");

        if (forecast) {
            sb.append("AND forecast = TRUE ");
        }

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append("ORDER BY ").append(ca.getValue());                // NOI18N
        }

        final MetaObject[] metaObjects;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("executinh SQL statement: \n" + sb);
            }
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get time series  meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        this.timeseriesTableModel = new TimeseriesTableModel(metaObjects);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        timeseriesPanel = new javax.swing.JPanel();
        jScrollPaneTimeseries = new javax.swing.JScrollPane();
        tblTimeseries = new javax.swing.JTable();

        timeseriesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "SwmmWizardPanelTimeseriesUI.timeseriesPanel.border.title"))); // NOI18N

        tblTimeseries.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        jScrollPaneTimeseries.setViewportView(tblTimeseries);

        final javax.swing.GroupLayout timeseriesPanelLayout = new javax.swing.GroupLayout(timeseriesPanel);
        timeseriesPanel.setLayout(timeseriesPanelLayout);
        timeseriesPanelLayout.setHorizontalGroup(
            timeseriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                timeseriesPanelLayout.createSequentialGroup().addContainerGap().addComponent(
                    jScrollPaneTimeseries,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    452,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        timeseriesPanelLayout.setVerticalGroup(
            timeseriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                timeseriesPanelLayout.createSequentialGroup().addComponent(
                    jScrollPaneTimeseries,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    254,
                    Short.MAX_VALUE).addContainerGap()));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    timeseriesPanel,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    timeseriesPanel,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addContainerGap()));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SwmmWizardPanelTimeseries getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TimeseriesTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final MetaObject[] timeseries;
        private final boolean[] selectedTimeseries;
        private final String[] columnNames = { "Name", "Beschreibung", "Auswahl" };
        private final Class[] columnClasses = { String.class, String.class, Boolean.class };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TimeseriesTableModel object.
         *
         * @param  metaObjects  DOCUMENT ME!
         */
        private TimeseriesTableModel(final MetaObject[] metaObjects) {
            this.timeseries = metaObjects;
            this.selectedTimeseries = new boolean[timeseries.length];
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return timeseries.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return timeseries[rowIndex].getName();
                }
                case 1: {
                    return "keine Beschreibung vorhanden";
                }
                case 2: {
                    return selectedTimeseries[rowIndex];
                }
            }

            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col) {
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("Setting value at " + row + "," + col
//                            + " to " + value + " (an instance of " + value.getClass() + ")");
//            }

            if (col == 2) {
                this.selectedTimeseries[row] = (Boolean)value;
            }

            fireTableCellUpdated(row, col);

            // update selected timeseries
            model.setTimeseriesIds(this.getSelectedTimeseries());
        }

        @Override
        public String getColumnName(final int col) {
            return columnNames[col];
        }

        @Override
        public Class getColumnClass(final int col) {
            return columnClasses[col];
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            return col == 2;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private List<Integer> getSelectedTimeseries() {
            final ArrayList<Integer> selectedStationsList = new ArrayList<Integer>();

            for (int i = 0; i < this.selectedTimeseries.length; i++) {
                if (this.selectedTimeseries[i]) {
                    selectedStationsList.add(this.timeseries[i].getId());
                }
            }

            return selectedStationsList;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  selectedStationsList  DOCUMENT ME!
         */
        private void setSelectedTimeseries(final List<Integer> selectedStationsList) {
            for (int i = 0; i < this.selectedTimeseries.length; i++) {
                if (selectedStationsList.contains(timeseries[i].getId())) {
                    this.selectedTimeseries[i] = true;
                } else {
                    this.selectedTimeseries[i] = false;
                }
            }

            this.fireTableRowsUpdated(0, this.selectedTimeseries.length);
        }
    }
}
