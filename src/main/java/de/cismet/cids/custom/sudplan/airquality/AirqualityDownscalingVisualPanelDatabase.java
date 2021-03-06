/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingVisualPanelDatabase extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingVisualPanelDatabase.class);

    private static final transient DefaultListModel MODEL_LOADING = new DefaultListModel();

    static {
        MODEL_LOADING.addElement(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelScenario.class,
                "AirqualityDownscalingVisualPanelDatabase.MODEL_LOADING")); // NOI18N
    }

    //~ Instance fields --------------------------------------------------------

    private final transient AirqualityDownscalingWizardPanelDatabase model;
    private final transient ListSelectionListener changeModelListener;
    private final transient Comparator<CidsBean> emissionDatabaseComparator;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblAvailableDbs;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionValue;
    private javax.swing.JList lstDatabases;
    private javax.swing.JPanel pnlDatabases;
    private javax.swing.JPanel pnlDescription;
    private javax.swing.JScrollPane scpDatabases;
    private javax.swing.JSplitPane splContainer;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingVisualPanelScenarios.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingVisualPanelDatabase(final AirqualityDownscalingWizardPanelDatabase model) {
        this.model = model;
        changeModelListener = new ChangeModelListener();

        this.setName(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.this.name")); // NOI18N

        initComponents();

        lstDatabases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstDatabases.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                changeModelListener,
                lstDatabases));

        emissionDatabaseComparator = new EmissionDatabaseComparator();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        // It's important to get the selected database before invoking the ListSelectionListener, e. g. by calling
        // clear(). The ListSelectionListener would reset the selected database.
        final String databaseFromModel = model.getDatabase();
        final List<CidsBean> databases = model.getDatabases();

        lstDatabases.setEnabled((databases != null) && (!databases.isEmpty()));

        if ((databases == null) || (databases.isEmpty())) {
            lstDatabases.setModel(MODEL_LOADING);
            lstDatabases.clearSelection();
            return;
        } else {
            if ((lstDatabases.getModel() == null) || lstDatabases.getModel().equals(MODEL_LOADING)) {
                lstDatabases.setModel(new DefaultListModel());
            }
        }

        Collections.sort(databases, emissionDatabaseComparator);

        final DefaultListModel listModel = (DefaultListModel)lstDatabases.getModel();
        listModel.clear();

        for (final CidsBean database : databases) {
            listModel.addElement(database);
        }

        if (databaseFromModel == null) {
            lstDatabases.setSelectedIndex(0);
            model.setDatabase((String)((CidsBean)lstDatabases.getSelectedValue()).getProperty("name")); // NOI18N
        } else {
            lstDatabases.setSelectedValue(databaseFromModel, true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        splContainer = new javax.swing.JSplitPane();
        pnlDatabases = new javax.swing.JPanel();
        lblAvailableDbs = new javax.swing.JLabel();
        scpDatabases = new javax.swing.JScrollPane();
        lstDatabases = new javax.swing.JList();
        pnlDescription = new javax.swing.JPanel();
        lblDescription = new javax.swing.JLabel();
        lblDescriptionValue = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setMinimumSize(new java.awt.Dimension(200, 150));
        setPreferredSize(new java.awt.Dimension(450, 300));
        setLayout(new java.awt.BorderLayout());

        splContainer.setBorder(null);
        splContainer.setDividerSize(0);
        splContainer.setResizeWeight(0.5);
        splContainer.setEnabled(false);

        pnlDatabases.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        pnlDatabases.setLayout(new java.awt.BorderLayout(0, 10));

        lblAvailableDbs.setLabelFor(lstDatabases);
        lblAvailableDbs.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.lblAvailableDbs.text")); // NOI18N
        pnlDatabases.add(lblAvailableDbs, java.awt.BorderLayout.PAGE_START);

        lstDatabases.setModel(new DefaultListModel());
        lstDatabases.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstDatabases.setCellRenderer(new EmissionDatabaseRenderer());
        scpDatabases.setViewportView(lstDatabases);

        pnlDatabases.add(scpDatabases, java.awt.BorderLayout.CENTER);

        splContainer.setLeftComponent(pnlDatabases);

        pnlDescription.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        pnlDescription.setLayout(new java.awt.BorderLayout(0, 10));

        lblDescription.setLabelFor(lblDescriptionValue);
        lblDescription.setText(org.openide.util.NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.lblDescription.text")); // NOI18N
        pnlDescription.add(lblDescription, java.awt.BorderLayout.PAGE_START);

        lblDescriptionValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblDescriptionValue.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pnlDescription.add(lblDescriptionValue, java.awt.BorderLayout.CENTER);

        splContainer.setRightComponent(pnlDescription);

        add(splContainer, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ChangeModelListener implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (MODEL_LOADING.equals(lstDatabases.getModel()) || e.getValueIsAdjusting()) {
                lblDescriptionValue.setText(""); // NOI18N
                return;
            }

            final CidsBean selectedDatabase = (CidsBean)lstDatabases.getSelectedValue();

            if (selectedDatabase != null) {
                model.setDatabase((String)selectedDatabase.getProperty("name")); // NOI18N

                if (selectedDatabase.getProperty("description") instanceof String) {  // NOI18N
                    lblDescriptionValue.setText("<html><p>"
                                + (String)selectedDatabase.getProperty("description") // NOI18N
                                + "</p></html>");                                     // NOI18N
                }
            } else {
                model.setDatabase(null);
                lblDescriptionValue.setText("");                                      // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class EmissionDatabaseComparator implements Comparator<CidsBean> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final CidsBean o1, final CidsBean o2) {
            if ((o1 == null) && (o2 == null)) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            final Object nameObject1 = o1.getProperty("name"); // NOI18N
            final Object nameObject2 = o2.getProperty("name"); // NOI18N

            if (!(nameObject1 instanceof String) && !(nameObject2 instanceof String)) {
                return 0;
            }
            if (!(nameObject1 instanceof String)) {
                return -1;
            }
            if (!(nameObject2 instanceof String)) {
                return 1;
            }

            return ((String)nameObject1).compareToIgnoreCase((String)nameObject2);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class EmissionDatabaseRenderer extends JLabel implements ListCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GridRenderer object.
         */
        public EmissionDatabaseRenderer() {
            setOpaque(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            if (isSelected) {
                setBackground(UIManager.getDefaults().getColor("List.selectionBackground")); // NOI18N
                setForeground(UIManager.getDefaults().getColor("List.selectionForeground")); // NOI18N
            } else {
                setBackground(UIManager.getDefaults().getColor("List.background"));          // NOI18N
                setForeground(UIManager.getDefaults().getColor("List.foreground"));          // NOI18N
            }

            if (value instanceof CidsBean) {
                final Object name = ((CidsBean)value).getProperty("name"); // NOI18N
                if (name instanceof String) {
                    setText((String)name);
                } else {
                    // TODO: Something better?!
                    setText("Erroneous emission database"); // NOI18N
                }
            }

            return this;
        }
    }
}
