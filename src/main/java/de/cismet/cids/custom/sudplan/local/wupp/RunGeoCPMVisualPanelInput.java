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

import Sirius.server.localserver.attribute.ClassAttribute;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.tostringconverter.sudplan.DeltaConfigurationToStringConverter;
import de.cismet.cids.custom.tostringconverter.sudplan.GeocpmConfigurationToStringConverter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class RunGeoCPMVisualPanelInput extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunGeoCPMVisualPanelInput.class);

    //~ Instance fields --------------------------------------------------------

    private final transient RunGeoCPMWizardPanelInput model;

    private final transient ListSelectionListener listL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JList lstAvailableInput = new javax.swing.JList();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RunGeoCPMVisualPanelInput.
     *
     * @param  model  DOCUMENT ME!
     */
    public RunGeoCPMVisualPanelInput(final RunGeoCPMWizardPanelInput model) {
        this.model = model;
        listL = new SelectionListener();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                RunGeoCPMVisualPanelInput.class,
                "RunGeoCPMVisualPanelInput.this.name")); // NOI18N

        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    // TODO: create clean init code and use jtree for visualisation
    private void initInputList() throws WizardInitialisationException {
        // FIXME: hardcoded domain
        MetaClass mc = ClassCacheMultiple.getMetaClass(
                SMSUtils.DOMAIN_SUDPLAN_WUPP,
                SMSUtils.TABLENAME_GEOCPM_CONFIGURATION);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch geocpm configuration metaclass"); // NOI18N
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());         // NOI18N
        }

        MetaObject[] metaObjects;
        try {
            metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                                sb.toString(),
                                SMSUtils.DOMAIN_SUDPLAN_WUPP);
        } catch (final ConnectionException ex) {
            final String message = "cannot get input meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        final DefaultListModel dlm = new DefaultListModel();
        for (int i = 0; i < metaObjects.length; ++i) {
            dlm.addElement(metaObjects[i].getBean());
        }

        mc = ClassCacheMultiple.getMetaClass(SMSUtils.DOMAIN_SUDPLAN_WUPP,
                SMSUtils.TABLENAME_DELTA_CONFIGURATION);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch delta configuration metaclass"); // NOI18N
        }

        sb = new StringBuilder();
        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N
        sb.append(" where locked = false");

        ca = mc.getClassAttribute("sortingColumn");        // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue()); // NOI18N
        }

        try {
            metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                                sb.toString(),
                                SMSUtils.DOMAIN_SUDPLAN_WUPP);
        } catch (final ConnectionException ex) {
            final String message = "cannot get input meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        for (int i = 0; i < metaObjects.length; ++i) {
            dlm.addElement(metaObjects[i].getBean());
        }

        lstAvailableInput.setModel(dlm);
        lstAvailableInput.setCellRenderer(new NameRenderer());
        lstAvailableInput.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                listL,
                lstAvailableInput));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    void init() throws WizardInitialisationException {
        // TODO: create default bindable jlist
        initInputList();

        if (model.getInput() == null) {
            lstAvailableInput.getSelectionModel().clearSelection();
        }

        // why is this not sufficient to clear the selection if rainevent is null
        lstAvailableInput.setSelectedValue(model.getInput(), true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        final java.awt.GridBagConstraints gridBagConstraints;

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lstAvailableInput.setBorder(javax.swing.BorderFactory.createTitledBorder(
                NbBundle.getMessage(
                    RunGeoCPMVisualPanelInput.class,
                    "RunGeoCPMVisualPanelInput.lstAvailableInput.border.title"))); // NOI18N
        lstAvailableInput.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstAvailableInput);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class SelectionListener implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                model.setInput((CidsBean)lstAvailableInput.getSelectedValue());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class NameRenderer extends DefaultListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final transient GeocpmConfigurationToStringConverter geocpmConv =
            new GeocpmConfigurationToStringConverter();
        private final transient DeltaConfigurationToStringConverter deltaConv =
            new DeltaConfigurationToStringConverter();

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if ((comp instanceof JLabel) && (value instanceof CidsBean)) {
                final JLabel label = (JLabel)comp;
                final CidsBean obj = (CidsBean)value;
                final MetaObject mo = obj.getMetaObject();
                final MetaClass mc = mo.getMetaClass();

                String name = null;
                try {
                    if (SMSUtils.TABLENAME_GEOCPM_CONFIGURATION.equalsIgnoreCase(mc.getTableName())) {
                        name = geocpmConv.convert(mo);
                    } else if (SMSUtils.TABLENAME_DELTA_CONFIGURATION.equalsIgnoreCase(mc.getTableName())) {
                        name = deltaConv.convert(mo);
                    } else {
                        throw new IllegalStateException("unknown metaclass"); // NOI18N
                    }
                } catch (final Exception e) {
                    LOG.warn("cannot fetch name for bean: " + obj, e);        // NOI18N

                    name = (String)obj.getProperty("name"); // NOI18N
                }

                label.setText(name);

                assert mc != null : "metaobject without metaclass"; // NOI18N

                final byte[] iconData = mc.getObjectIconData();
                if (iconData != null) {
                    label.setIcon(new ImageIcon(iconData));
                }
            }

            return comp;
        }
    }
}
