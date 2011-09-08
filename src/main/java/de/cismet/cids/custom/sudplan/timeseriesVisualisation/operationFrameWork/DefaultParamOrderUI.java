/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DefaultParamOrderUI.java
 *
 * Created on 25.08.2011, 08:34:02
 */
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.operationFrameWork;

import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;

import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.Unit;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesSignature;
import de.cismet.cids.custom.sudplan.timeseriesVisualisation.TimeSeriesVisualisation;

/**
 * This is the default UI to select a subset of all available <code>TimeSeries</code> and to determine the position in
 * the parameter Array. Used from <code>AbstractTimeSeriesOperation</code> to determine the parameters for the operation
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */

//TODO Problem: DefaultParamOrderUI verlässt sich darauf dass TimeSeriesVisualisation TimeSeriesSignature implementiert und Obs prop gesetzt ist
public class DefaultParamOrderUI extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    /** A return status code - returned if Cancel button has been pressed. */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed. */
    public static final int RET_OK = 1;

    //~ Instance fields --------------------------------------------------------

    private ArrayList<JComboBox> paramList = new ArrayList<JComboBox>();
    private int paramCount;
    private TimeSeriesOperation operation;
    private TimeSeriesVisualisation tsVis;
    // End of variables declaration
    private int returnStatus = RET_CANCEL;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblExpression;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblOpDesc;
    private javax.swing.JLabel lblOpExpr;
    private javax.swing.JLabel lblOpName;
    private javax.swing.JLabel lblOperation;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel pnlCtrlButtons;
    private javax.swing.JPanel pnlLabels;
    private javax.swing.JPanel pnlOrderComponents;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DefaultParamOrderUI.
     *
     * @param  parent      DOCUMENT ME!
     * @param  modal       DOCUMENT ME!
     * @param  paramCount  DOCUMENT ME!
     * @param  c           DOCUMENT ME!
     * @param  op          DOCUMENT ME!
     * @param  tsv         DOCUMENT ME!
     */
    public DefaultParamOrderUI(final java.awt.Frame parent,
            final boolean modal,
            final int paramCount,
            final Collection<TimeSeries> c,
            final TimeSeriesOperation op,
            final TimeSeriesVisualisation tsv) {
        super(parent, modal);
        setTitle("Param Ordering");
        operation = op;
        tsVis = tsv;
        this.paramCount = paramCount;
        initComponents();
        this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight() + (paramCount * 20)));
        lblOpName.setText((String)operation.getValue(TimeSeriesOperation.NAME));
        lblOpDesc.setText((String)operation.getValue(TimeSeriesOperation.LONG_DESCRIPTION));
        lblOpExpr.setText((String)operation.getValue(TimeSeriesOperation.OP_EXPRESSION));

        // add for each param a label and cb
        for (int i = 0; i < paramCount; i++) {
            final JLabel lblParam = new JLabel();
            final char paramNr = (char)('A' + i);
            lblParam.setText("Parameter : " + paramNr); // NOI18N
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 20);
            pnlOrderComponents.add(lblParam, gridBagConstraints);

            final JComboBox cb = new JComboBox();

            cb.setModel(new javax.swing.DefaultComboBoxModel(c.toArray()));
            cb.setMinimumSize(new java.awt.Dimension(100, 18));
            cb.setPreferredSize(new java.awt.Dimension(200, 20));
            cb.setRenderer(new TimeSeriesComboBoxRenderer(tsv));
            paramList.add(cb);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 2;
            gridBagConstraints.weightx = 1.0;
            pnlOrderComponents.add(cb, gridBagConstraints);
        }
        // Close the dialog when Esc is pressed
        final String cancelName = "cancel";
        final InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        final ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    doClose(RET_CANCEL);
                }
            });
        this.setResizable(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialise the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlLabels = new javax.swing.JPanel();
        lblOperation = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblExpression = new javax.swing.JLabel();
        lblOpName = new javax.swing.JLabel();
        lblOpDesc = new javax.swing.JLabel();
        lblOpExpr = new javax.swing.JLabel();
        pnlOrderComponents = new javax.swing.JPanel();
        lblFiller = new javax.swing.JLabel();
        pnlCtrlButtons = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosing(final java.awt.event.WindowEvent evt) {
                    closeDialog(evt);
                }
            });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlLabels.setMinimumSize(new java.awt.Dimension(200, 100));
        pnlLabels.setPreferredSize(new java.awt.Dimension(400, 100));
        pnlLabels.setLayout(new java.awt.GridBagLayout());

        lblOperation.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.lblOperation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 5);
        pnlLabels.add(lblOperation, gridBagConstraints);

        lblDescription.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlLabels.add(lblDescription, gridBagConstraints);

        lblExpression.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.lblExpression.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 5);
        pnlLabels.add(lblExpression, gridBagConstraints);

        lblOpName.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.lblOpName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlLabels.add(lblOpName, gridBagConstraints);

        lblOpDesc.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.lblOpDesc.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        pnlLabels.add(lblOpDesc, gridBagConstraints);

        lblOpExpr.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.lblOpExpr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        pnlLabels.add(lblOpExpr, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(pnlLabels, gridBagConstraints);

        pnlOrderComponents.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(pnlOrderComponents, gridBagConstraints);

        lblFiller.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(lblFiller, gridBagConstraints);

        pnlCtrlButtons.setMinimumSize(new java.awt.Dimension(150, 30));
        pnlCtrlButtons.setPreferredSize(new java.awt.Dimension(150, 30));

        cancelButton.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cancelButtonActionPerformed(evt);
                }
            });
        pnlCtrlButtons.add(cancelButton);

        okButton.setText(org.openide.util.NbBundle.getMessage(
                DefaultParamOrderUI.class,
                "DefaultParamOrderUI.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    okButtonActionPerformed(evt);
                }
            });
        pnlCtrlButtons.add(okButton);
        getRootPane().setDefaultButton(okButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        getContentPane().add(pnlCtrlButtons, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  a ActionEvent
     */
    private void okButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        final TimeSeries[] paramArray = new TimeSeries[paramCount];
        for (int i = 0; i < paramCount; i++) {
            final JComboBox cb = paramList.get(i);
            paramArray[i] = (TimeSeries)cb.getSelectedItem();
            final Unit unit = SMSUtils.unitFromTimeseries(paramArray[i]);
            if (i > 0) {
                final Unit lastUnit = SMSUtils.unitFromTimeseries(paramArray[i - 1]);
                if (!unit.equals(lastUnit)) {
                    // TODO ERROR Dialog
                    JOptionPane.showMessageDialog(this, "Parameters must have the same Unit");
                    return;
                }
            }
        }

        operation.setParameters(paramArray);
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cancelButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void closeDialog(final java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    /**
     * DOCUMENT ME!
     *
     * @param  retStatus  DOCUMENT ME!
     */
    private void doClose(final int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * A special Renderer that represents a <code<TimeSeries in a combobox with the signature symbol that it gets from
     * the TimeSeriesVisualisation and the <code>TimeSeries.OBSERVERDPROPERTY</code> as the name.
     *
     * @version  $Revision$, $Date$
     */
    protected final class TimeSeriesComboBoxRenderer extends JLabel implements ListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private TimeSeriesVisualisation tsVis;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TimeSeriesComboBoxRenderer object.
         *
         * @param  tsv  DOCUMENT ME!
         */
        public TimeSeriesComboBoxRenderer(final TimeSeriesVisualisation tsv) {
            tsVis = tsv;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            if (value instanceof TimeSeries) {
                final TimeSeries selectedTS = (TimeSeries)value;
                final String name = (String)selectedTS.getTSProperty(TimeSeries.OBSERVEDPROPERTY);
                final String[] splittedName = name.split(":");
                this.setText(splittedName[splittedName.length - 1]);

                // take the symbol...
                final TimeSeriesSignature tss = tsVis.getLookup(TimeSeriesSignature.class);
                if (tss != null) {
                    final BufferedImage bi = tss.getTimeSeriesSignature(selectedTS, 16, 16);
                    final Icon ic = new ImageIcon(bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight()));
                    setIcon(ic);
                }
            }
            return this;
        }
    }
}
