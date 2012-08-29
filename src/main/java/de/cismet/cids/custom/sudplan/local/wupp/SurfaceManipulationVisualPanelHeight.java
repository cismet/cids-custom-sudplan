/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.apache.log4j.Logger;

import org.openide.util.WeakListeners;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Locale;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import de.cismet.cids.custom.sudplan.Unit;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class SurfaceManipulationVisualPanelHeight extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SurfaceManipulationVisualPanelHeight.class);
    private static final String NUMBER_FORMAT = "#,##0.00";
    private static final double SPINNER_MAX_NUMBER = 1000.0d;
    private static final double SPINNER_MIN_NUMBER = -1000.0d;
    private static final double SPINNER_STEPS = 0.50d;

    //~ Instance fields --------------------------------------------------------

    private final transient SurfaceManipulationWizardPanelHeight model;
    private final transient ChangeListener chL;
    private final transient ActionListener aL;
//    private final transient SpinnerModelImpl spinnerModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgType;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lblHeight;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JPanel pnlType;
    private javax.swing.JRadioButton rbAdjacentSurface;
    private javax.swing.JRadioButton rbSeaLevel;
    private javax.swing.JSpinner spnHeight;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SurfaceManipulationVisualPanelHeight.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public SurfaceManipulationVisualPanelHeight(final SurfaceManipulationWizardPanelHeight model) {
        this.model = model;
        if (this.model == null) {
            throw new IllegalStateException("model instance must not be null");
        }

        this.setName("Change height");

        initComponents();
        spnHeight.setModel(new SpinnerNumberModel(0, -Double.MAX_VALUE, Double.MAX_VALUE, SPINNER_STEPS));
        final JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor)spnHeight.getEditor();
        final JFormattedTextField ftf = defEditor.getTextField();
        final InternationalFormatter intFormatter = (InternationalFormatter)ftf.getFormatter();
        final DecimalFormat decimalFormat = (DecimalFormat)intFormatter.getFormat();
        decimalFormat.applyPattern(NUMBER_FORMAT);

        final DecimalFormatSymbols frSymbols = new DecimalFormatSymbols(Locale.FRENCH); // 2 345 678.5
        frSymbols.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(frSymbols);

//        spnHeight.setEditor(new JSpinner.NumberEditor(spnHeight, NUMBER_FORMAT));

        chL = new ChangeListenerImpl();
        spnHeight.addChangeListener(WeakListeners.change(chL, spnHeight));
        spnHeight.setToolTipText("Red color for reducing / Blue color for increasing the Surface");

        aL = new ActionListenerImpl();
        rbAdjacentSurface.addActionListener(WeakListeners.create(ActionListener.class, aL, rbAdjacentSurface));
        rbSeaLevel.addActionListener(WeakListeners.create(ActionListener.class, aL, rbSeaLevel));
        lblUnit.setText(Unit.M.getLocalisedName());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void init() {
        final Number height = model.getHeight();
        if (height == null) {
            model.setHeight(Double.valueOf(0.0d));
            spnHeight.setValue(Double.valueOf(0.0d));
        } else {
            spnHeight.setValue(height);
        }

        Boolean seaType = model.isSeaType();
        // model.setHeight(spinnerModel.getDoubleValue());
        if (seaType == null) {
            seaType = false;
            model.setSeaType(seaType.booleanValue());
        }

        rbAdjacentSurface.setSelected(!seaType.booleanValue());
        rbSeaLevel.setSelected(seaType.booleanValue());

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    spnHeight.requestFocus();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param   spinner  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JFormattedTextField getTextField(final JSpinner spinner) {
        final JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            return ((JSpinner.DefaultEditor)editor).getTextField();
        } else {
            LOG.error("Unexpected editor type: " + spnHeight.getEditor().getClass()
                        + " isn't a descendant of DefaultEditor");
            return null;
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

        bgType = new javax.swing.ButtonGroup();
        lblHeight = new javax.swing.JLabel();
        spnHeight = new javax.swing.JSpinner();
        pnlType = new javax.swing.JPanel();
        rbSeaLevel = new javax.swing.JRadioButton();
        rbAdjacentSurface = new javax.swing.JRadioButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 32767));
        lblUnit = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lblHeight.setText(org.openide.util.NbBundle.getMessage(
                SurfaceManipulationVisualPanelHeight.class,
                "SurfaceManipulationVisualPanelHeight.lblHeight.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblHeight, gridBagConstraints);

        spnHeight.setPreferredSize(new java.awt.Dimension(200, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(spnHeight, gridBagConstraints);

        pnlType.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SurfaceManipulationVisualPanelHeight.class,
                    "SurfaceManipulationVisualPanelHeight.pnlType.border.title"))); // NOI18N
        pnlType.setLayout(new java.awt.GridBagLayout());

        bgType.add(rbSeaLevel);
        rbSeaLevel.setText(org.openide.util.NbBundle.getMessage(
                SurfaceManipulationVisualPanelHeight.class,
                "SurfaceManipulationVisualPanelHeight.rbSeaLevel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlType.add(rbSeaLevel, gridBagConstraints);

        bgType.add(rbAdjacentSurface);
        rbAdjacentSurface.setText(org.openide.util.NbBundle.getMessage(
                SurfaceManipulationVisualPanelHeight.class,
                "SurfaceManipulationVisualPanelHeight.rbAdjacentSurface.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlType.add(rbAdjacentSurface, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlType.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlType, gridBagConstraints);

        lblUnit.setText(org.openide.util.NbBundle.getMessage(
                SurfaceManipulationVisualPanelHeight.class,
                "SurfaceManipulationVisualPanelHeight.lblUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUnit, gridBagConstraints);
    }                                                                  // </editor-fold>//GEN-END:initComponents

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
                        new SurfaceManipulationVisualPanelHeight(new SurfaceManipulationWizardPanelHeight()));
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
    private class ChangeListenerImpl implements ChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void stateChanged(final ChangeEvent ce) {
            final Double value = (Double)((JSpinner.NumberEditor)spnHeight.getEditor()).getModel().getNumber();

            final JFormattedTextField ftf = getTextField(spnHeight);
            if (value.doubleValue() < 0.0d) {
                ftf.setForeground(Color.red);
            } else if (value.doubleValue() > 0.0d) {
                ftf.setForeground(Color.blue);
            } else {
                ftf.setForeground(Color.black);
            }
            model.setHeight(value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ActionListenerImpl implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent ae) {
            model.setSeaType(rbSeaLevel.isSelected());
        }
    }
}
