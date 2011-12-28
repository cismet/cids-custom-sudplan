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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    private final transient javax.swing.JCheckBox chkDyna = new javax.swing.JCheckBox();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JList jlsAvailableInput = new javax.swing.JList();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RunGeoCPMVisualPanelInput.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public RunGeoCPMVisualPanelInput(final RunGeoCPMWizardPanelInput model) throws WizardInitialisationException {
        this.model = model;
        listL = new SelectionListener();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                RunGeoCPMVisualPanelInput.class,
                "RunGeoCPMVisualPanelInput.this.name")); // NOI18N

        initComponents();

        // TODO: create default bindable jlist
        initInputList();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initInputList() throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, RunGeoCPMWizardAction.TABLENAME_GEOCPM_CONFIG);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch geocpm config metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());               // NOI18N
        }

        final MetaObject[] metaObjects;
        try {
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get input meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        final DefaultListModel dlm = new DefaultListModel();
        for (int i = 0; i < metaObjects.length; ++i) {
            dlm.addElement(metaObjects[i].getBean());
        }

        jlsAvailableInput.setModel(dlm);
        jlsAvailableInput.setCellRenderer(new NameRenderer());
        jlsAvailableInput.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                listL,
                jlsAvailableInput));
    }

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (model.getInput() == null) {
            jlsAvailableInput.getSelectionModel().clearSelection();
        }

        // why is this not sufficient to clear the selection if rainevent is null
        jlsAvailableInput.setSelectedValue(model.getInput(), true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jlsAvailableInput.setBorder(javax.swing.BorderFactory.createTitledBorder(
                NbBundle.getMessage(
                    RunGeoCPMVisualPanelInput.class,
                    "RunGeoCPMVisualPanelInput.jlsAvailableInput.border.title"))); // NOI18N
        jlsAvailableInput.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jlsAvailableInput);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        chkDyna.setText(NbBundle.getMessage(RunGeoCPMVisualPanelInput.class, "RunGeoCPMVisualPanelInput.chkDyna.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkDyna, gridBagConstraints);
    }                                                                                                                    // </editor-fold>//GEN-END:initComponents

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
                model.setInput((CidsBean)jlsAvailableInput.getSelectedValue());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class NameRenderer extends DefaultListCellRenderer {

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
                final String name = (String)obj.getProperty("name"); // NOI18N
                label.setText(name);
            }

            return comp;
        }
    }
}
