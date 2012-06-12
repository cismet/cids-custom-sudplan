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

import java.awt.EventQueue;

import java.util.Arrays;
import java.util.MissingResourceException;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingVisualPanelScenario extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingVisualPanelScenario.class);

    private static final transient DefaultListModel MODEL_LOADING = new DefaultListModel();

    static {
        MODEL_LOADING.addElement(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelScenario.class,
                "AirqualityDownscalingVisualPanelScenario.MODEL_LOADING")); // NOI18N
    }

    //~ Instance fields --------------------------------------------------------

    private final transient AirqualityDownscalingWizardPanelScenario model;
    private final transient ListSelectionListener changeModelListener;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionValue;
    private javax.swing.JLabel lblScenarios;
    private javax.swing.JList lstScenarios;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingVisualPanelScenarios.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingVisualPanelScenario(final AirqualityDownscalingWizardPanelScenario model) {
        this.model = model;
        changeModelListener = new ChangeModelListener();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelScenario.class,
                "AirqualityDownscalingVisualPanelScenario.this.name")); // NOI18N

        initComponents();

        lstScenarios.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                changeModelListener,
                lstScenarios));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        // It's important to get the selected scenario before invoking the ListSelectionListener, e. g. by calling
        // clear(). The ListSelectionListener would reset the selected scenario.
        final String scenarioFromModel = model.getScenario();
        final String[] scenarios = model.getScenariosFromSPS();

        lstScenarios.setEnabled((scenarios != null) && (scenarios.length > 0));

        if ((scenarios == null) || (scenarios.length <= 0)) {
            lstScenarios.setModel(MODEL_LOADING);
            lstScenarios.clearSelection();
            return;
        } else {
            if ((lstScenarios.getModel() == null) || lstScenarios.getModel().equals(MODEL_LOADING)) {
                lstScenarios.setModel(new DefaultListModel());
            }
        }

        Arrays.sort(scenarios);

        final DefaultListModel listModel = (DefaultListModel)lstScenarios.getModel();
        listModel.clear();

        for (final String scenario : scenarios) {
            listModel.addElement(scenario);
        }

        if (scenarioFromModel == null) {
            lstScenarios.setSelectedIndex(0);
            model.setScenario((String)lstScenarios.getSelectedValue());
        } else {
            lstScenarios.setSelectedValue(scenarioFromModel, true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblScenarios = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstScenarios = new javax.swing.JList();
        lblDescription = new javax.swing.JLabel();
        lblDescriptionValue = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(200, 150));
        setPreferredSize(new java.awt.Dimension(450, 300));
        setLayout(new java.awt.GridBagLayout());

        lblScenarios.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelScenario.class,
                "AirqualityDownscalingVisualPanelScenario.lblScenarios.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(lblScenarios, gridBagConstraints);

        lstScenarios.setModel(new DefaultListModel());
        lstScenarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstScenarios);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 337;
        gridBagConstraints.ipady = 213;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelScenario.class,
                "AirqualityDownscalingVisualPanelScenario.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(lblDescription, gridBagConstraints);

        lblDescriptionValue.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelScenario.class,
                "AirqualityDownscalingVisualPanelScenario.lblDescriptionValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblDescriptionValue, gridBagConstraints);
    }                                                                                  // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final JDialog dialog = new JDialog();
                    dialog.setContentPane(
                        new AirqualityDownscalingVisualPanelScenario(new AirqualityDownscalingWizardPanelScenario()));
                    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    dialog.setLocationRelativeTo(null);
                    dialog.pack();
                    dialog.setVisible(true);
                }
            });
    }

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
            if (MODEL_LOADING.equals(lstScenarios.getModel()) || e.getValueIsAdjusting()) {
                lblDescriptionValue.setText(null);
                return;
            }

            final String selectedModel = (String)lstScenarios.getSelectedValue();

            model.setScenario(selectedModel);

            String description;
            if (selectedModel != null) {
                try {
                    description = NbBundle.getMessage(
                            AirqualityDownscalingVisualPanelScenario.class,
                            "AirqualityDownscalingVisualPanelScenario.lblDescriptionValue.text."
                                    + lstScenarios.getSelectedValue().toString());                            // NOI18N
                } catch (final MissingResourceException ex) {
                    LOG.info("Couldn't find a description for model '" + selectedModel + "'.", ex);           // NOI18N
                    description = NbBundle.getMessage(
                            AirqualityDownscalingVisualPanelScenario.class,
                            "AirqualityDownscalingVisualPanelScenario.lblDescriptionValue.text.unknownModel", // NOI18N
                            selectedModel);
                }
            } else {
                description = NbBundle.getMessage(
                        AirqualityDownscalingVisualPanelScenario.class,
                        "AirqualityDownscalingVisualPanelScenario.lblDescriptionValue.text.noModel");         // NOI18N
            }

            lblDescriptionValue.setText(description);
        }
    }
}