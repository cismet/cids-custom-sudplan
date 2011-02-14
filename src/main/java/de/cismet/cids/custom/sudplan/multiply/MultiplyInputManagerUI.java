/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.EventQueue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class MultiplyInputManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(MultiplyInputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    protected final transient MultiplyInputManager model;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JLabel jLabel2;
    protected javax.swing.JLabel jLabel3;
    protected javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JEditorPane jepMultipliers;
    protected javax.swing.JSpinner jspMultiplicand;
    protected javax.swing.JTextField txtInputLocation;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MultiplyInputManagerUI.
     *
     * @param  model  DOCUMENT ME!
     */
    public MultiplyInputManagerUI(final MultiplyInputManager model) {
        this.model = model;
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    void store() throws IOException {
        final StringReader sr = new StringReader(jepMultipliers.getText());
        final Double[] multipliers = MultiplyHelper.doublesFromReader(sr);

        final URI location;
        try {
            location = new URI(txtInputLocation.getText());
        } catch (final URISyntaxException ex) {
            LOG.error("invalid uri: " + txtInputLocation.getText(), ex); // NOI18N
            JOptionPane.showMessageDialog(
                this,
                "Invalid URI: "
                        + txtInputLocation.getText(),
                "Error",
                JOptionPane.ERROR_MESSAGE);

            return;
        }

        final int multiplicand;
        try {
            multiplicand = Integer.parseInt(jspMultiplicand.getValue().toString());
        } catch (final NumberFormatException e) {
            LOG.error("invalid multiplicand: " + jspMultiplicand.getValue(), e); // NOI18N
            JOptionPane.showMessageDialog(
                this,
                "Invalid multiplicand: "
                        + jspMultiplicand.getValue(),
                "Error",
                JOptionPane.ERROR_MESSAGE);

            return;
        }

        try {
            model.setLocation(location);
            model.setMultipliers(multipliers);
            model.setMultiplicand(multiplicand);
        } catch (final IOException e) {
            final String message = "cannot set new values"; // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    void init() {
        if (EventQueue.isDispatchThread()) {
            triggerEnable(true);
            try {
                final StringWriter sw = new StringWriter();
                MultiplyHelper.numbersToWriter(sw, model.getMultipliers());
                jepMultipliers.setText(sw.toString());

                jspMultiplicand.setValue(model.getMulitplicand());

                txtInputLocation.setText(model.getUR().toString());
            } catch (final IOException e) {
                LOG.error("cannot initialise components", e); // NOI18N
                jepMultipliers.setText("initialisation failed: " + e);
                triggerEnable(false);
            }
        } else {
            throw new IllegalStateException("initialisation shall be done in EDT"); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    protected void triggerEnable(final boolean enable) {
        setEnabled(enable);
        jepMultipliers.setEnabled(enable);
        jspMultiplicand.setEnabled(enable);
        txtInputLocation.setEnabled(enable);
        jLabel1.setEnabled(enable);
        jLabel2.setEnabled(enable);
        jLabel3.setEnabled(enable);
        jScrollPane1.setEnabled(enable);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jepMultipliers = new javax.swing.JEditorPane();
        jspMultiplicand = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtInputLocation = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(
                NbBundle.getMessage(MultiplyInputManagerUI.class, "MultiplyInputManagerUI.border.title"))); // NOI18N
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(0, 0));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(NbBundle.getMessage(MultiplyInputManagerUI.class, "MultiplyInputManagerUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 10);
        add(jLabel1, gridBagConstraints);

        jScrollPane1.setViewportView(jepMultipliers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 375;
        gridBagConstraints.ipady = 291;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 10);
        add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 45;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 10);
        add(jspMultiplicand, gridBagConstraints);

        jLabel2.setText(NbBundle.getMessage(MultiplyInputManagerUI.class, "MultiplyInputManagerUI.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 10);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText(NbBundle.getMessage(MultiplyInputManagerUI.class, "MultiplyInputManagerUI.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 10);
        add(jLabel3, gridBagConstraints);

        txtInputLocation.setText(NbBundle.getMessage(
                MultiplyInputManagerUI.class,
                "MultiplyInputManagerUI.txtInputLocation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 10);
        add(txtInputLocation, gridBagConstraints);
    }                                                             // </editor-fold>//GEN-END:initComponents
}
