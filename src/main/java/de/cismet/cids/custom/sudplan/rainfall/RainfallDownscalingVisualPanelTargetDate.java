/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.rainfall;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class RainfallDownscalingVisualPanelTargetDate extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient RainfallDownscalingWizardPanelTargetDate model;
    private final transient DocumentListener docL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblYear;
    private javax.swing.JSlider sldYears;
    private javax.swing.JTextField txtYear;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RainfallDownscalingVisualPanelTargetDate.
     *
     * @param  model  DOCUMENT ME!
     */
    public RainfallDownscalingVisualPanelTargetDate(final RainfallDownscalingWizardPanelTargetDate model) {
        this.model = model;
        this.docL = new DocumentListenerImpl();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                RainfallDownscalingVisualPanelTargetDate.class,
                "RainfallDownscalingVisualPanelTargetDate.this.name")); // NOI18N

        initComponents();

        txtYear.getDocument().addDocumentListener(WeakListeners.document(docL, txtYear.getDocument()));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        sldYears.setMinimum(model.getBeginYear());
        sldYears.setMaximum(model.getEndYear());

        if (model.getTargetYear() == null) {
            txtYear.setText("2050"); // NOI18N
        } else {
            txtYear.setText(model.getTargetYear().toString());
        }
        model.fireChangeEvent();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getYear() {
        return txtYear.getText();
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

        sldYears = new javax.swing.JSlider();
        lblYear = new javax.swing.JLabel();
        txtYear = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        sldYears.setMajorTickSpacing(10);
        sldYears.setMaximum(2085);
        sldYears.setMinimum(2025);
        sldYears.setMinorTickSpacing(5);
        sldYears.setPaintLabels(true);
        sldYears.setPaintTicks(true);
        sldYears.setMinimumSize(new java.awt.Dimension(200, 52));
        sldYears.setPreferredSize(new java.awt.Dimension(280, 52));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 7);
        add(sldYears, gridBagConstraints);

        lblYear.setText(NbBundle.getMessage(
                RainfallDownscalingVisualPanelTargetDate.class,
                "RainfallDownscalingVisualPanelTargetDate.lblYear.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        add(lblYear, gridBagConstraints);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                sldYears,
                org.jdesktop.beansbinding.ELProperty.create("${value}"),
                txtYear,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(txtYear, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DocumentListenerImpl implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            model.fireChangeEvent();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            model.fireChangeEvent();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            model.fireChangeEvent();
        }
    }
}
