/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public abstract class AbstractConverterChoosePanel<T extends Converter> extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractConverterChoosePanel.class);

    //~ Instance fields --------------------------------------------------------

    private final transient AbstractConverterChoosePanelCtrl ctrl;
    private final transient ItemListener converterL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JComboBox cboConverterChooser = new javax.swing.JComboBox();
    private final transient javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    private final transient javax.swing.JLabel lblConverter = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFormatDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFormatDescriptionValue = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFormatExample = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblFormatExampleValue = new javax.swing.JLabel();
    private final transient javax.swing.JPanel pnlFormatExample = new javax.swing.JPanel();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractConverterChoosePanel object.
     *
     * @param   ctrl  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public AbstractConverterChoosePanel(final AbstractConverterChoosePanelCtrl ctrl) {
        if (ctrl == null) {
            throw new NullPointerException("Given AbstractConverterChoosePanelCtrl instance " // NOI18N
                        + "must not be null");                     // NOI18N
        }

        this.converterL = new ConverterItemListener();

        initComponents();

        this.setName(NbBundle.getMessage(AbstractConverterChoosePanel.class, "AbstractConverterChoosePanel.name"));
        this.ctrl = ctrl;

        cboConverterChooser.addItemListener(WeakListeners.create(ItemListener.class, converterL, cboConverterChooser));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void init() {
        this.cboConverterChooser.removeAllItems();

        for (final T converter : getConverters()) {
            this.cboConverterChooser.addItem(converter);
        }

        this.cboConverterChooser.setSelectedIndex(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract List<T> getConverters();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public T getConverter() {
        return (T)this.cboConverterChooser.getSelectedItem();
    }

    /**
     * DOCUMENT ME!
     */

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cboConverterChooser.setMinimumSize(new java.awt.Dimension(300, 27));
        cboConverterChooser.setPreferredSize(new java.awt.Dimension(300, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cboConverterChooser, gridBagConstraints);

        lblConverter.setText(org.openide.util.NbBundle.getMessage(
                AbstractConverterChoosePanel.class,
                "AbstractConverterChoosePanel.lblConverter.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblConverter, gridBagConstraints);

        lblFormatDescription.setText(org.openide.util.NbBundle.getMessage(
                AbstractConverterChoosePanel.class,
                "AbstractConverterChoosePanel.lblFormatDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblFormatDescription, gridBagConstraints);

        lblFormatDescriptionValue.setFont(new java.awt.Font("Tahoma", 0, 10));   // NOI18N
        lblFormatDescriptionValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFormatDescriptionValue.setText(org.openide.util.NbBundle.getMessage(
                AbstractConverterChoosePanel.class,
                "AbstractConverterChoosePanel.lblFormatDescriptionValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel1.add(lblFormatDescriptionValue, gridBagConstraints);

        lblFormatExample.setText(org.openide.util.NbBundle.getMessage(
                AbstractConverterChoosePanel.class,
                "AbstractConverterChoosePanel.lblFormatExample.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblFormatExample, gridBagConstraints);

        pnlFormatExample.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormatExample.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        pnlFormatExample.setLayout(new java.awt.GridBagLayout());

        lblFormatExampleValue.setFont(new java.awt.Font("Tahoma", 0, 10));   // NOI18N
        lblFormatExampleValue.setText(org.openide.util.NbBundle.getMessage(
                AbstractConverterChoosePanel.class,
                "AbstractConverterChoosePanel.lblFormatExampleValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlFormatExample.add(lblFormatExampleValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(pnlFormatExample, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ConverterItemListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                final T converter = (T)e.getItem();
                if (converter instanceof FormatHint) {
                    final FormatHint hint = (FormatHint)converter;

                    if (hint.getFormatHtmlDescription() == null) {
                        lblFormatDescriptionValue.setText(hint.getFormatDescription());
                    } else {
                        lblFormatDescriptionValue.setText(hint.getFormatHtmlDescription());
                    }

                    final Object formatExample = hint.getFormatExample();
                    if (formatExample instanceof String) {
                        lblFormatExampleValue.setText((String)formatExample);
                    } else if (formatExample instanceof Component) {
                        pnlFormatExample.removeAll();
                        pnlFormatExample.add((Component)formatExample);
                    } else {
                        LOG.warn("unsupported example format: " + formatExample); // NOI18N
                        resetExample();
                    }
                } else {
                    lblFormatDescriptionValue.setText(NbBundle.getMessage(
                            AbstractConverterChoosePanel.class,
                            "AbstractConverterChoosePanel.lblFormatDescriptionValue.text"));

                    resetExample();
                }
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void resetExample() {
            lblFormatExampleValue.setText(NbBundle.getMessage(
                    AbstractConverterChoosePanel.class,
                    "AbstractConverterChoosePanel.lblFormatExampleValue.text"));
            pnlFormatExample.removeAll();

            final GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.insets = new Insets(10, 10, 10, 10);

            pnlFormatExample.add(lblFormatExampleValue, constraints);
        }
    }
}
