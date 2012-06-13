/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.decorator.SortOrder;

import org.openide.util.NbBundle;

import java.text.MessageFormat;

import javax.swing.DefaultListModel;

import de.cismet.cids.custom.sudplan.NamedCidsBeanComparator;
import de.cismet.cids.custom.sudplan.NamedCidsBeanListCellRenderer;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class DoSimulationVisualPanelSelectWorkspace extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WORKSPACE_MO_QUERY =
        "SELECT {0}, hw.{1} FROM hydrology_workspace hw, run r WHERE hw.basin_id = {2} AND hw.calibration = r.id AND r.finished IS NOT NULL"; // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(AssignTimeseriesVisualPanelSelectCalibration.class);

    //~ Instance fields --------------------------------------------------------

    private final transient DoSimulationWizardPanelSelectWorkspace model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient org.jdesktop.swingx.JXList lstWorkspaces = new org.jdesktop.swingx.JXList();
    private final transient javax.swing.JPanel pnlWorkspaces = new javax.swing.JPanel();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AssignTimeseriesVisualPanelSelectCalibration.
     *
     * @param  model  DOCUMENT ME!
     */
    public DoSimulationVisualPanelSelectWorkspace(final DoSimulationWizardPanelSelectWorkspace model) {
        this.model = model;

        initComponents();

        listInit();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DoSimulationWizardPanelSelectWorkspace getModel() {
        return model;
    }

    /**
     * DOCUMENT ME!
     */
    private void listInit() {
        lstWorkspaces.setComparator(new NamedCidsBeanComparator());
        lstWorkspaces.setCellRenderer(new NamedCidsBeanListCellRenderer());
    }

    /**
     * DOCUMENT ME!
     */
    void init() {
        bindingGroup.unbind();

        final DefaultListModel listModel = new DefaultListModel();
        lstWorkspaces.setModel(listModel);

        final User user = SessionManager.getSession().getUser();
        try {
            final MetaClass mc = ClassCacheMultiple.getMetaClass(user.getDomain(),
                    SMSUtils.TABLENAME_HYDROLOGY_WORKSPACE);
            final String query = MessageFormat.format(
                    WORKSPACE_MO_QUERY,
                    mc.getID(),
                    mc.getPrimaryKey(),
                    String.valueOf(model.getBasinId()));
            final MetaObject[] mos = SessionManager.getProxy().getMetaObjectByQuery(user, query);

            for (final MetaObject mo : mos) {
                listModel.addElement(mo.getBean());
            }

            lstWorkspaces.setSortOrder(SortOrder.ASCENDING);

            if (model.getSelectedWorkspace() != null) {
                lstWorkspaces.setSelectedValue(model.getSelectedWorkspace(), true);
            }
        } catch (final ConnectionException ex) {
            LOG.error("cannot fetch local model meta objects", ex); // NOI18N
            listModel.addElement("Error while searching for local models: " + ex);
        }

        bindingGroup.bind();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        setName(NbBundle.getMessage(
                DoSimulationVisualPanelSelectWorkspace.class,
                "DoSimulationVisualPanelSelectWorkspace.name")); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlWorkspaces.setBorder(javax.swing.BorderFactory.createTitledBorder(
                NbBundle.getMessage(
                    DoSimulationVisualPanelSelectWorkspace.class,
                    "AssignTimeseriesVisualPanelSelectCalibration.pnlCalibrations.border.title"))); // NOI18N
        pnlWorkspaces.setOpaque(false);
        pnlWorkspaces.setLayout(new java.awt.GridBagLayout());

        lstWorkspaces.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.selectedWorkspace}"),
                lstWorkspaces,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(lstWorkspaces);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pnlWorkspaces.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        add(pnlWorkspaces, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
}
