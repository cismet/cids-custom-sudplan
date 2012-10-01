/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.decorator.SortOrder;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import se.smhi.sudplan.client.Scenario;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Comparator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.sudplan.DownscalingScenario;

import de.cismet.tools.BrowserLauncher;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class DoSimulationVisualPanelSelectScenario extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(DoSimulationVisualPanelSelectScenario.class);

    //~ Instance fields --------------------------------------------------------

    private final transient DoSimulationWizardPanelSelectScenario model;
    private final transient ListSelectionListener listL;
    private final transient MouseListener descL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
            new java.awt.Dimension(0, 0),
            new java.awt.Dimension(0, 32767));
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDescriptionValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblScenarios = new javax.swing.JLabel();
    private final transient org.jdesktop.swingx.JXList lstScenarios = new org.jdesktop.swingx.JXList();
    private final transient javax.swing.JPanel pnlScenarios = new javax.swing.JPanel();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DoSimulationVisualPanelSelectScenario.
     *
     * @param  model  DOCUMENT ME!
     */
    public DoSimulationVisualPanelSelectScenario(final DoSimulationWizardPanelSelectScenario model) {
        this.model = model;
        this.listL = new ListSelectionListenerImpl();
        this.descL = new DescriptionClickListener();

        initComponents();

        initList();

        lstScenarios.addListSelectionListener(WeakListeners.create(ListSelectionListener.class, listL, lstScenarios));
        lblDescriptionValue.addMouseListener(WeakListeners.create(MouseListener.class, descL, lblDescriptionValue));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void initList() {
        lstScenarios.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final Component c = super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);

                    if (c instanceof JLabel) {
                        final JLabel label = (JLabel)c;
                        final Scenario s = (Scenario)value;

                        label.setText(s.getScenarioId());
                    }

                    return c;
                }
            });

        lstScenarios.setComparator(new Comparator<Scenario>() {

                @Override
                public int compare(final Scenario o1, final Scenario o2) {
                    return o1.getScenarioId().compareTo(o2.getScenarioId());
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (model.getSelectedScenario() == null) {
            lstScenarios.setSelectedIndex(0);
        } else {
            lstScenarios.setSelectedValue(model.getSelectedScenario(), true);
        }

        lstScenarios.setSortOrder(SortOrder.ASCENDING);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DoSimulationWizardPanelSelectScenario getModel() {
        return model;
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
                DoSimulationVisualPanelSelectScenario.class,
                "DoSimulationVisualPanelSelectScenario.name")); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlScenarios.setMinimumSize(new java.awt.Dimension(260, 96));
        pnlScenarios.setOpaque(false);
        pnlScenarios.setPreferredSize(new java.awt.Dimension(260, 96));
        pnlScenarios.setLayout(new java.awt.GridBagLayout());

        lstScenarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${model.availableScenarios}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstScenarios);
        bindingGroup.addBinding(jListBinding);
        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.selectedScenario}"),
                lstScenarios,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(lstScenarios);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pnlScenarios.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 20, 3, 20);
        add(pnlScenarios, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                DoSimulationVisualPanelSelectScenario.class,
                "DoSimulationVisualPanelSelectScenario.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 23, 5, 20);
        add(lblDescription, gridBagConstraints);

        lblDescriptionValue.setText(NbBundle.getMessage(
                DoSimulationVisualPanelSelectScenario.class,
                "DoSimulationVisualPanelSelectScenario.lblDescriptionValue.text"));        // NOI18N
        lblDescriptionValue.setToolTipText(NbBundle.getMessage(
                DoSimulationVisualPanelSelectScenario.class,
                "DoSimulationVisualPanelSelectScenario.lblDescriptionValue.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 23, 20, 20);
        add(lblDescriptionValue, gridBagConstraints);

        lblScenarios.setText(NbBundle.getMessage(
                DoSimulationVisualPanelSelectScenario.class,
                "DoSimulationVisualPanelSelectScenario.lblScenarios.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 23, 2, 5);
        add(lblScenarios, gridBagConstraints);

        filler1.setMaximumSize(new java.awt.Dimension(0, 32767));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DescriptionClickListener extends MouseAdapter {

        //~ Methods ------------------------------------------------------------

        @Override
        public void mouseEntered(final MouseEvent e) {
            lblDescriptionValue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            lblDescriptionValue.setCursor(null);
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            final String url = DownscalingScenario.getDetailLink(lstScenarios.getSelectedValue().toString());

            try {
                BrowserLauncher.openURL(url);
            } catch (final Exception ex) {
                LOG.warn("cannot open link", ex); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ListSelectionListenerImpl implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                final Object selectedValue = lstScenarios.getSelectedValue();

                if (selectedValue == null) {
                    lblDescriptionValue.setText(NbBundle.getMessage(
                            DoSimulationVisualPanelSelectScenario.class,
                            "DoSimulationVisualPanelSelectScenario.lblDescriptionValue.text")); // NOI18N
                } else {
                    final String scenario = ((Scenario)selectedValue).getScenarioId();
                    lblDescriptionValue.setText(DownscalingScenario.getHtmlDescription(scenario));
                }
            }
        }
    }
}
