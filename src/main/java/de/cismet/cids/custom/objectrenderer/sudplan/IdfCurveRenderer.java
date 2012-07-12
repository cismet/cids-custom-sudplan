/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.IDFChartPanel;
import de.cismet.cids.custom.sudplan.IDFTablePanel;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class IdfCurveRenderer extends AbstractCidsBeanRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(IdfCurveRenderer.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkForecast;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JLabel lblUnitValue;
    private javax.swing.JLabel lblYear;
    private javax.swing.JLabel lblYearValue;
    private javax.swing.JPanel pnlFiller;
    private javax.swing.JPanel pnlIdf;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form IdfCurveRenderer.
     */
    public IdfCurveRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        pnlFiller = new javax.swing.JPanel();
        pnlIdf = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        chkForecast = new javax.swing.JCheckBox();
        lblYear = new javax.swing.JLabel();
        lblYearValue = new javax.swing.JLabel();
        lblUnit = new javax.swing.JLabel();
        lblUnitValue = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlFiller.setOpaque(false);
        pnlFiller.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlFiller, gridBagConstraints);

        pnlIdf.setOpaque(false);
        pnlIdf.setLayout(new java.awt.BorderLayout());

        lblStatus.setText(NbBundle.getMessage(IdfCurveRenderer.class, "IdfCurveRenderer.lblStatus.text")); // NOI18N
        pnlIdf.add(lblStatus, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(pnlIdf, gridBagConstraints);

        chkForecast.setText(NbBundle.getMessage(IdfCurveRenderer.class, "IdfCurveRenderer.chkForecast.text")); // NOI18N
        chkForecast.setContentAreaFilled(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.forecast}"),
                chkForecast,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkForecast, gridBagConstraints);

        lblYear.setText(NbBundle.getMessage(IdfCurveRenderer.class, "IdfCurveRenderer.lblYear.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblYear, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.year}"),
                lblYearValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblYearValue, gridBagConstraints);

        lblUnit.setText(org.openide.util.NbBundle.getMessage(IdfCurveRenderer.class, "IdfCurveRenderer.lblUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUnit, gridBagConstraints);

        lblUnitValue.setText(NbBundle.getMessage(IdfCurveRenderer.class, "IdfCurveRenderer.lblUnitValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblUnitValue, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    protected void init() {
        bindingGroup.unbind();
        bindingGroup.bind();

        try {
            pnlIdf.removeAll();
            pnlIdf.add(new IDFTablePanel(cidsBean));
            pnlFiller.removeAll();
            pnlFiller.add(new IDFChartPanel(cidsBean), BorderLayout.CENTER);
        } catch (final Exception ex) {
            LOG.error("cannot initialise IDF renderer", ex); // NOI18N

            lblStatus.setText("ERROR: " + ex.getMessage()); // NOI18N
        }
    }
}
