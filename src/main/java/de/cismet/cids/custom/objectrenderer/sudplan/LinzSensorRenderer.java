/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.commons.httpclient.Credentials;
import org.apache.log4j.Logger;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.EventQueue;

import java.io.IOException;

import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeSeriesRemoteHelper;
import de.cismet.cids.custom.sudplan.TimeseriesRetriever;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import de.cismet.cids.custom.sudplan.local.linz.SwmmInput;
import de.cismet.cids.custom.sudplan.local.linz.wizard.EtaWizardPanelEtaConfigurationUI;
import de.cismet.cids.custom.sudplan.local.linz.wizard.SwmmPlusEtaWizardAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   p-a-s-c-a-l
 * @version  $Revision$, $Date$
 */
public class LinzSensorRenderer extends AbstractCidsBeanRenderer implements TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LinzSensorRenderer.class);
    public static final String NETCDF_HOST = TimeSeriesRemoteHelper.NETCDF_HOST;
    public static final Credentials NETCDF_CREDS = TimeSeriesRemoteHelper.NETCDF_CREDS;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum Sensor {

        //~ Enum constants -----------------------------------------------------

        INFLOW("inflow", "Linz WWTP Inflow Events"),    // NOI18N
        OUTFLOW("outflow", "Linz WWTP Outflow Events"), // NOI18N
        HYDRAULICS("hydraulics", "Linz Hydraulics Events"); // NOI18N

        //~ Instance fields ----------------------------------------------------

        private final String type;
        private String stationName;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Sensor object.
         *
         * @param  type         DOCUMENT ME!
         * @param  stationName  DOCUMENT ME!
         */
        private Sensor(final String type, final String stationName) {
            this.type = type;
            this.stationName = stationName;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getName() {
            return type;
        }

        /**
         * Get the value of stationName.
         *
         * @return  the value of stationName
         */
        public String getStationName() {
            return stationName;
        }

        /**
         * Set the value of stationName.
         *
         * @param  stationName  new value of stationName
         */
        public void setStationName(final String stationName) {
            this.stationName = stationName;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    //~ Instance fields --------------------------------------------------------

    private final transient LinzSensorTitleComponent linzSensorTitleComponent = new LinzSensorTitleComponent();
    // End of variables declaration
    private transient Sensor currentSensorType;
    private EventDetectionUpdater eventDetectionUpdater = null;
    // Variables declaration - do not modify
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel eventDetectionPanel;
    private javax.swing.JScrollPane jScrollPaneEventDetection;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JTable tblEventDetection;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LinzWwtpRenderer.
     */
    public LinzSensorRenderer() {
        initComponents();
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

        eventDetectionPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        jScrollPaneEventDetection = new javax.swing.JScrollPane();
        tblEventDetection = new javax.swing.JTable();
        progressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(204, 204, 204));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        eventDetectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    LinzSensorRenderer.class,
                    "LinzSensorRenderer.eventDetectionPanel.border.title"))); // NOI18N
        eventDetectionPanel.setLayout(new java.awt.GridBagLayout());

        cardPanel.setLayout(new java.awt.CardLayout());

        tblEventDetection.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        jScrollPaneEventDetection.setViewportView(tblEventDetection);

        cardPanel.add(jScrollPaneEventDetection, "events");

        progressPanel.setLayout(new java.awt.GridBagLayout());

        progressBar.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 25);
        progressPanel.add(progressBar, gridBagConstraints);

        progressLabel.setText(org.openide.util.NbBundle.getMessage(
                LinzSensorRenderer.class,
                "LinzSensorRenderer.progressLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        progressPanel.add(progressLabel, gridBagConstraints);

        cardPanel.add(progressPanel, "progress");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        eventDetectionPanel.add(cardPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(eventDetectionPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);
        this.linzSensorTitleComponent.setTitle(title);
    }

    @Override
    public JComponent getTitleComponent() {
        return linzSensorTitleComponent;
    }

    @Override
    protected void init() {
        try {
            assert this.getCidsBean() != null : "Sensor Bean must not be null!";

            final String sensorName = this.getCidsBean().getProperty("name").toString().toLowerCase();
            if (LOG.isDebugEnabled()) {
                LOG.debug("lodaing event for sensor '" + sensorName + "'");
            }
            if (Sensor.HYDRAULICS.getName().indexOf(sensorName) != -1) {
                currentSensorType = Sensor.HYDRAULICS;
            } else if (Sensor.INFLOW.getName().indexOf(sensorName) != -1) {
                currentSensorType = Sensor.INFLOW;
            } else if (Sensor.OUTFLOW.getName().indexOf(sensorName) != -1) {
                currentSensorType = Sensor.OUTFLOW;
            } else {
                throw new Exception("Unsopported Sensor Type '" + sensorName
                            + "', expected " + Sensor.HYDRAULICS + ", " + Sensor.OUTFLOW + " or " + Sensor.INFLOW);
            }

            if ((eventDetectionUpdater != null) && eventDetectionUpdater.isRunning()) {
                LOG.warn("An Event Detection update thread is still running");
                // eventDetectionUpdater.stopIt();
            } else {
                Mnemonics.setLocalizedText(
                    progressLabel,
                    NbBundle.getMessage(
                        EtaWizardPanelEtaConfigurationUI.class,
                        "LinzSensorRenderer.progressLabel.text")); // NOI18N
                progressBar.setIndeterminate(true);
                ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");

                eventDetectionUpdater = new EventDetectionUpdater(currentSensorType);
                SudplanConcurrency.getSudplanGeneralPurposePool().execute(eventDetectionUpdater);
            }
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
            progressBar.setIndeterminate(false);
            org.openide.awt.Mnemonics.setLocalizedText(
                progressLabel,
                org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelEtaConfigurationUI.class,
                    "LinzSensorRenderer.progressLabel.error")); // NOI18N
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sensor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private MetaObject[] getTimeseriesBeans(final Sensor sensor) throws IOException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_TIMESERIES);
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving event timeseries (" + SwmmPlusEtaWizardAction.TABLENAME_TIMESERIES
                        + ")for sensor '" + sensor + "' from monitorstation '"
                        + sensor.getStationName() + "' from domain '" + domain + "'");
        }

        if (mc == null) {
            throw new IOException("cannot find TIMESERIES metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ")
                .append(mc.getID())
                .append(',')
                .append(mc.getTableName())
                .append('.')
                .append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ")
                .append(mc.getTableName())
                .append(',')
                .append(SwmmPlusEtaWizardAction.TABLENAME_MONITOR_STATION);
        sb.append(" WHERE ")
                .append(mc.getTableName())
                .append('.')
                .append("station")
                .append(" = ")
                .append(SwmmInput.TABLENAME_MONITOR_STATION)
                .append(".id")
                .append(" AND ")
                .append(SwmmInput.TABLENAME_MONITOR_STATION)
                .append('.')
                .append("name")
                .append(" LIKE '")
                .append(sensor.getStationName())
                .append('\'');

        if (LOG.isDebugEnabled()) {
            LOG.debug("executing SQL statement: \n" + sb);
        }

        final MetaObject[] metaObjects;

        try {
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            throw new IOException("cannot get timeseries  objects from database: " + ex.getMessage(), ex);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(metaObjects.length + " timeseries retrieved for station '" + sensor.getStationName() + "'");
        }
        return metaObjects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sensor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private EventDetectionTableModel loadEvents(final Sensor sensor) throws IOException {
        LOG.info("loading events for '" + sensor + '\'');
        final MetaObject[] timeseriesMoList = getTimeseriesBeans(sensor);

        if (timeseriesMoList.length == 0) {
            throw new IOException("no timeseries found for '" + sensor
                        + "' (" + sensor.getStationName() + ")");
        }

        final TimeseriesRetriever retriever = TimeseriesRetriever.getInstance();
        final TimeSeries[] timeseriesList = new TimeSeries[timeseriesMoList.length];

        try {
            int i = 0;
            for (final MetaObject timeseriesMo : timeseriesMoList) {
                final CidsBean timeseriesBean = timeseriesMo.getBean();
                final String uri = (String)timeseriesBean.getProperty("uri"); // NOI18N
                if (LOG.isDebugEnabled()) {
                    LOG.debug("loading remote timeseries for '" + timeseriesBean.getProperty("name") + "' from '"
                                + uri + "'");
                }

                final TimeseriesConverter converter = SMSUtils.loadConverter(timeseriesBean);
                final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(uri);

                final Future<TimeSeries> result = retriever.retrieve(config, converter);
                timeseriesList[i] = result.get();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("remote timeseries #" + i + " for '" + timeseriesBean.getProperty("name")
                                + "' successully loaded");
                }
                i++;
            }
        } catch (Exception ex) {
            throw new IOException("could not load timeseries '" + sensor
                        + "' (" + sensor.getStationName() + "): " + ex.getLocalizedMessage(),
                ex);
        }

        return new EventDetectionTableModel(timeseriesList);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class EventDetectionTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(LinzSensorRenderer.EventDetectionTableModel.class);
        private final TimeSeries[] eventTimeseries;
        private final TimeStamp[] timeStamps;
        private final String[] columnNames;
        private final Class[] columnClasses;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EtaConfigurationTableModel object.
         *
         * @param   eventTimeseries  etaConfigurations metaObjects DOCUMENT ME!
         *
         * @throws  IllegalStateException  DOCUMENT ME!
         */
        private EventDetectionTableModel(final TimeSeries[] eventTimeseries) {
            this.eventTimeseries = eventTimeseries;
            this.columnNames = new String[eventTimeseries.length + 1];
            this.columnNames[0] = org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelEtaConfigurationUI.class,
                    "LinzSensorRenderer.tblEventDetection.column.date");
            this.columnClasses = new Class[eventTimeseries.length + 1];
            this.columnClasses[0] = String.class;

            int i = 1;
            for (final TimeSeries timeSeries : eventTimeseries) {
                final Object valueKeyObject = timeSeries.getTSProperty(TimeSeries.VALUE_KEYS);
                final String valueKey;
                if (valueKeyObject instanceof String) {
                    valueKey = (String)valueKeyObject;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("found valuekey: " + valueKey);                                 // NOI18N
                    }
                } else if (valueKeyObject instanceof String[]) {
                    final String[] valueKeys = (String[])valueKeyObject;
                    if (valueKeys.length > 1) {
                        LOG.warn("found multiple valuekeys: " + valueKeys.length);                // NOI18N
                    }
                    valueKey = valueKeys[0];
                } else {
                    throw new IllegalStateException("unknown value key type: " + valueKeyObject); // NOI18N
                }
                columnNames[i] = valueKey;
                columnClasses[i] = Float.class;
                i++;
            }

            // take the #st time series for the date column
            timeStamps = eventTimeseries[0].getTimeStampsArray();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return timeStamps.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return this.timeStamps[rowIndex];
                }
                case 1: {
                    return eventTimeseries[rowIndex].getValue(
                            this.timeStamps[rowIndex],
                            this.columnNames[columnIndex]);
                }
            }

            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col) {
            LOG.warn("operation setValueAt(...) not supported by " + this.getClass().getName());
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
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class EventDetectionUpdater implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(LinzSensorRenderer.EventDetectionUpdater.class);
        private transient boolean run = true;
        private EventDetectionTableModel eventDetectionTableModel;
        private final Sensor sensor;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EventDetectionUpdater object.
         *
         * @param  sensor  DOCUMENT ME!
         */
        public EventDetectionUpdater(final Sensor sensor) {
            this.sensor = sensor;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        public void stopIt() {
            run = false;
            LOG.warn("EventDetectionUpdater stopped");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isRunning() {
            return run;
        }

        @Override
        public void run() {
            if (run) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("EventDetectionUpdater: loading events for sensor '" + sensor + "'");
                    }
                    eventDetectionTableModel = loadEvents(sensor);
                } catch (Throwable t) {
                    LOG.error("EventDetectionUpdater: could not retrieve evetn detection values: " + t.getMessage(), t);
                    run = false;
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setIndeterminate(false);
                                org.openide.awt.Mnemonics.setLocalizedText(
                                    progressLabel,
                                    org.openide.util.NbBundle.getMessage(
                                        EtaWizardPanelEtaConfigurationUI.class,
                                        "LinzSensorRenderer.progressLabel.error")); // NOI18N
                            }
                        });
                }

                if (run) {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("EventDetectionUpdater: updating loaded results in GUI");
                                }
                                LinzSensorRenderer.this.tblEventDetection.setModel(eventDetectionTableModel);
                                ((CardLayout)cardPanel.getLayout()).show(cardPanel, "events");
                                run = false;
                            }
                        });
                } else {
                    LOG.warn("EventDetectionUpdater stopped, ignoring retrieved results");
                }
            }
        }
    }
}
