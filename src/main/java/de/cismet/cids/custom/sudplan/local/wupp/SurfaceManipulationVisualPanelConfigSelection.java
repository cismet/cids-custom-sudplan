/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.tostringconverter.sudplan.DeltaConfigurationToStringConverter;
import de.cismet.cids.custom.tostringconverter.sudplan.GeocpmConfigurationToStringConverter;
import de.cismet.cids.custom.tostringconverter.sudplan.InvestigationAreaToStringConverter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class SurfaceManipulationVisualPanelConfigSelection extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SurfaceManipulationVisualPanelConfigSelection.class);

    private static final String LIST_SEPERATOR = "__prop_list_seperator__";

    //~ Instance fields --------------------------------------------------------

    private final transient SurfaceManipulationWizardPanelConfigSelection model;
    private final transient ListSelectionListener selL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList lstConfigurations;
    private javax.swing.JPanel pnlConfigurations;
    private javax.swing.JScrollPane spConfigurations;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SurfaceManipulationVisualPanelConfigSelection.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public SurfaceManipulationVisualPanelConfigSelection(final SurfaceManipulationWizardPanelConfigSelection model) {
        this.model = model;
        if (this.model == null) {
            throw new IllegalStateException("model instance must not be null");
        }
        selL = new ListSelectionListenerImpl();

        this.setName("Select Surface Model");
        initComponents();

        lstConfigurations.setCellRenderer(new ListCellRendererImpl());
        lstConfigurations.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                selL,
                lstConfigurations));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void init() {
        final CidsBean initialConfig = model.getInitialConfig();
        final CidsBean deltaSurfaceToAdd = model.getDeltaSurfaceToAdd();

        final DefaultListModel lstModel = new DefaultListModel();

        final DefaultListModel loadModel = new DefaultListModel();
        loadModel.addElement("Loading available models");
        lstConfigurations.setModel(loadModel);

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
//                    final int[] indexes = new int[initialModels.length];
//                    int i = 0;
//                    for (final MetaObject mo : initialModels) {
//                        final CidsBean cidsBean = initialConfig.getBean();
                    lstModel.addElement(initialConfig);
//                        indexes[i++] = (Integer)cidsBean.getProperty("id");
//                    }

                    final MetaClass MC = ClassCacheMultiple.getMetaClass(
                            SMSUtils.DOMAIN_SUDPLAN_WUPP,
                            SMSUtils.TABLENAME_DELTA_CONFIGURATION);

//                    if (MC == null) {
//                        MC = ClassCacheMultiple.getMetaClass(
//                                SessionManager.getSession().getUser().getDomain(),
//                                SMSUtils.TABLENAME_DELTA_CONFIGURATION);
//                    }
                    if (MC == null) {
                        LOG.error(
                            "cannot get MetaClass from Domain '"
                                    + SMSUtils.DOMAIN_SUDPLAN_WUPP
                                    + "' with Table '"
                                    + SMSUtils.TABLENAME_DELTA_CONFIGURATION
                                    + "'");
                    }

                    boolean firstElement = true;
                    boolean isSelectionValid = false;
                    final CidsBean selectedModel = model.getConfigModel();

                    String query;
                    if (deltaSurfaceToAdd == null) {
                        query = "select " + MC.getID() + ", m." + MC.getPrimaryKey() + " from "
                                    + MC.getTableName();
                        query += " m";
                        query += " WHERE m.original_object = " + initialConfig.getProperty("id");
                    } else {
                        query = "select " + MC.getID() + ", m." + MC.getPrimaryKey() + " from ";
                        query += MC.getTableName() + " m, " + SMSUtils.TABLENAME_DELTA_SURFACE + " ds"; // NOI18N
                        query += " WHERE ds.id = " + (Integer)deltaSurfaceToAdd.getProperty("id") + " AND "
                                    + "m.id != ds.delta_configuration AND "
                                    + "m.original_object = " + initialConfig.getProperty("id");
                    }

                    MetaObject[] deltaConfigs;
                    try {
                        deltaConfigs = SessionManager.getProxy()
                                    .getMetaObjectByQuery(
                                            SessionManager.getSession().getUser(),
                                            query,
                                            SMSUtils.DOMAIN_SUDPLAN_WUPP);
                    } catch (ConnectionException ex) {
                        LOG.error("cannot connect to " + SMSUtils.DOMAIN_SUDPLAN_WUPP, ex);
                        deltaConfigs = null;
                    }
                    if ((selectedModel != null) && selectedModel.equals(initialConfig)) {
                        isSelectionValid = true;
                    }
                    for (final MetaObject mo : deltaConfigs) {
                        if (firstElement) {
                            lstModel.addElement(LIST_SEPERATOR);
                            firstElement = false;
                        }

                        final CidsBean bean = mo.getBean();
                        lstModel.addElement(bean);

                        if (!isSelectionValid && (selectedModel != null) && selectedModel.equals(bean)) {
                            isSelectionValid = true;
                        }
                    }
//                    }

                    lstConfigurations.setModel(lstModel);

                    if ((selectedModel != null) && isSelectionValid) {
                        try {
                            lstConfigurations.setSelectedValue(selectedModel, true);
                        } catch (Exception e) {
                            LOG.error("can't select the model in jlist", e);
                        }
                    } else {
                        lstConfigurations.clearSelection();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("selectedModel is null or deleted/changed in database");
                        }
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
        java.awt.GridBagConstraints gridBagConstraints;

        pnlConfigurations = new javax.swing.JPanel();
        spConfigurations = new javax.swing.JScrollPane();
        lstConfigurations = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        pnlConfigurations.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SurfaceManipulationVisualPanelConfigSelection.class,
                    "SurfaceManipulationVisualPanelConfigSelection.pnlConfigurations.border.title"))); // NOI18N
        pnlConfigurations.setLayout(new java.awt.GridBagLayout());

        lstConfigurations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spConfigurations.setViewportView(lstConfigurations);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlConfigurations.add(spConfigurations, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlConfigurations, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ListCellRendererImpl extends DefaultListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final transient GeocpmConfigurationToStringConverter geoCPMToString;
        private final transient DeltaConfigurationToStringConverter deltaToString;
        private final transient InvestigationAreaToStringConverter investToString;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ListModelImpl object.
         */
        public ListCellRendererImpl() {
            geoCPMToString = new GeocpmConfigurationToStringConverter();
            deltaToString = new DeltaConfigurationToStringConverter();
            investToString = new InvestigationAreaToStringConverter();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value == null) {
                return this;
            }

            if (value instanceof String) {
                final String s = (String)value;
                if (s.equals(LIST_SEPERATOR)) {
                    return new JSeparator(JSeparator.HORIZONTAL);
                }
                return this;
            }

            final StringBuilder sb = new StringBuilder();

            if (value instanceof CidsBean) {
                final CidsBean cidsBean = (CidsBean)value;
                if (cidsBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                                SMSUtils.TABLENAME_GEOCPM_CONFIGURATION)) {
//                    sb.append("<html><font color=red>New </font>");
                    sb.append(geoCPMToString.convert(cidsBean.getMetaObject()));
//                    sb.append("</font> - ");
                    sb.append(" - ");
                    final CidsBean invest = (CidsBean)cidsBean.getProperty("investigation_area");
                    sb.append(investToString.convert(invest.getMetaObject()));
//                    sb.append("</html>");
                } else if (cidsBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                                SMSUtils.TABLENAME_DELTA_CONFIGURATION)) {
                    sb.append(deltaToString.convert(cidsBean.getMetaObject()));
                }
                setIcon(new ImageIcon(cidsBean.getMetaObject().getMetaClass().getIconData()));
            }
            setText(sb.toString());
            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ListSelectionListenerImpl implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) {
                return;
            }
            final Object o = lstConfigurations.getSelectedValue();

            if (o instanceof String) {
                final String s = (String)o;
                if (o.equals(LIST_SEPERATOR)) {
                    model.setConfigModel(null, true);
                }
            } else if (o instanceof CidsBean) {
                final CidsBean cidsBean = (CidsBean)o;
                if ((model.getConfigModel() != null) && cidsBean.equals(model.getConfigModel())) {
                    model.setConfigModel(cidsBean, false);
                } else {
                    model.setConfigModel(cidsBean, true);
                }

//                if (cidsBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
//                                SMSUtils.TABLENAME_GEOCPM_CONFIGURATION)) {
//                    model.setIsConfigModelNew(true);
//                } else {
//                    model.setIsConfigModelNew(false);
//                }
            }
        }
    }
}
