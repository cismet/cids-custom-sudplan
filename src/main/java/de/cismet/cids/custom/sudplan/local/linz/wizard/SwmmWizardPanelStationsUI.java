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
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;
import de.cismet.cids.custom.sudplan.local.wupp.WizardInitialisationException;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelStationsUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelStationsUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmWizardPanelStations model;
    private transient StationsTableModel stationsTableModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chbForecast;
    private javax.swing.JPanel forecastPanel;
    private javax.swing.JScrollPane jScrollPaneStations;
    private javax.swing.JPanel stationsPanel;
    private javax.swing.JTable tblStations;
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
    public SwmmWizardPanelStationsUI(final SwmmWizardPanelStations model) throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                SwmmWizardPanelStations.class,
                "SwmmWizardPanelStations.this.name"));

        this.initStations();
        this.tblStations.setModel(this.stationsTableModel);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        this.stationsTableModel.setSelectedStations(model.getStationsIds());

        this.bindingGroup.unbind();
        this.bindingGroup.bind();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initStations() throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_MONITOR_STATION);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch swmm project metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ")
                .append(mc.getID())
                .append(',')
                .append(mc.getTableName())
                .append('.')
                .append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName()).append(',').append(SwmmInput.TABLENAME_MONITOR_STATION_TYPE);
        sb.append(" WHERE ")
                .append(mc.getTableName())
                .append('.')
                .append(SwmmInput.FK_MONITOR_STATION_TYPE)
                .append(" = ")
                .append(SwmmInput.TABLENAME_MONITOR_STATION_TYPE)
                .append(".id")
                .append(" AND ")
                .append(SwmmInput.TABLENAME_MONITOR_STATION_TYPE)
                .append('.')
                .append(SwmmInput.FLD_MONITOR_STATION_TYPE)
                .append(" LIKE '")
                .append(SwmmInput.MONITOR_STATION_TYPE)
                .append('\'');

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn");                        // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());                                      // NOI18N
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing SQL statement: \n" + sb);
        }
        final MetaObject[] metaObjects;
        try {
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get monitoring station  meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        this.stationsTableModel = new StationsTableModel(metaObjects);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        stationsPanel = new javax.swing.JPanel();
        jScrollPaneStations = new javax.swing.JScrollPane();
        tblStations = new javax.swing.JTable();
        forecastPanel = new javax.swing.JPanel();
        chbForecast = new javax.swing.JCheckBox();

        stationsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelStationsUI.class,
                    "SwmmWizardPanelStationsUI.stationsPanel.border.title"))); // NOI18N

        tblStations.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        jScrollPaneStations.setViewportView(tblStations);

        final javax.swing.GroupLayout stationsPanelLayout = new javax.swing.GroupLayout(stationsPanel);
        stationsPanel.setLayout(stationsPanelLayout);
        stationsPanelLayout.setHorizontalGroup(
            stationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                stationsPanelLayout.createSequentialGroup().addContainerGap().addComponent(
                    jScrollPaneStations,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    425,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        stationsPanelLayout.setVerticalGroup(
            stationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                jScrollPaneStations,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                184,
                javax.swing.GroupLayout.PREFERRED_SIZE));

        forecastPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelStationsUI.class,
                    "SwmmWizardPanelStationsUI.forecastPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            chbForecast,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelStationsUI.class,
                "SwmmWizardPanelStationsUI.chbForecast.text")); // NOI18N

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.forecast}"),
                chbForecast,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                "forecast");
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        final javax.swing.GroupLayout forecastPanelLayout = new javax.swing.GroupLayout(forecastPanel);
        forecastPanel.setLayout(forecastPanelLayout);
        forecastPanelLayout.setHorizontalGroup(
            forecastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                forecastPanelLayout.createSequentialGroup().addComponent(chbForecast).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        forecastPanelLayout.setVerticalGroup(
            forecastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                forecastPanelLayout.createSequentialGroup().addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addComponent(chbForecast).addContainerGap()));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(
                        stationsPanel,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(
                        forecastPanel,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    stationsPanel,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(
                    forecastPanel,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SwmmWizardPanelStations getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class StationsTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final MetaObject[] stations;
        private final boolean[] selectedStations;
        private final String[] columnNames = { "Name", "Beschreibung", "Auswahl" };
        private final Class[] columnClasses = { String.class, String.class, Boolean.class };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new StationsTableModel object.
         *
         * @param  metaObjects  DOCUMENT ME!
         */
        private StationsTableModel(final MetaObject[] metaObjects) {
            this.stations = metaObjects;
            this.selectedStations = new boolean[stations.length];
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return stations.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return stations[rowIndex].getName();
                }
                case 1: {
                    return "keine Beschreibung vorhanden";
                }
                case 2: {
                    return selectedStations[rowIndex];
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
                this.selectedStations[row] = (Boolean)value;
            }

            fireTableCellUpdated(row, col);

            // update selected stations
            model.setStationsIds(this.getSelectedStations());
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
        private List<Integer> getSelectedStations() {
            final ArrayList<Integer> selectedStationsList = new ArrayList<Integer>();

            for (int i = 0; i < this.selectedStations.length; i++) {
                if (this.selectedStations[i]) {
                    selectedStationsList.add(this.stations[i].getId());
                }
            }

            return selectedStationsList;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  selectedStationsList  DOCUMENT ME!
         */
        private void setSelectedStations(final List<Integer> selectedStationsList) {
            for (int i = 0; i < this.selectedStations.length; i++) {
                if (selectedStationsList.contains(stations[i].getId())) {
                    this.selectedStations[i] = true;
                } else {
                    this.selectedStations[i] = false;
                }
            }

            this.fireTableRowsUpdated(0, this.selectedStations.length);
        }
    }
}
