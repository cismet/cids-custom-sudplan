/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXHyperlink;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal dihe
 * @version  $Revision$, $Date$
 */
public class SwmmInputManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmInputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmInputManager inputManager;
    private transient SwmmInputListener swmmInputListener;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel endDateLabel;
    private javax.swing.JLabel lbEndDate;
    private javax.swing.JLabel lbStartDate;
    private javax.swing.JLabel lbTimeseries;
    private javax.swing.JLabel lblSwmmProject;
    private javax.swing.JLabel startDateLabel;
    private org.jdesktop.swingx.JXHyperlink swmmProjectLink;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RunoffInputManagerUI.
     *
     * @param  inputManager  DOCUMENT ME!
     */
    public SwmmInputManagerUI(final SwmmInputManager inputManager) {
        this.inputManager = inputManager;

        initComponents();
        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        try {
            final SwmmInput swmmInput = inputManager.getUR();
            final CidsBean swmmProjectBean = swmmInput.fetchSwmmProject();
            final List<CidsBean> timeseriesBeans = swmmInput.fetchTimeseries();
            final HashMap<String, CidsBean> beansMap = new HashMap<String, CidsBean>(timeseriesBeans.size() + 1);

            this.startDateLabel.setText(SwmmInput.UTC_DATE_FORMAT.format(swmmInput.getStartDate()) + " UTC");
            this.endDateLabel.setText(SwmmInput.UTC_DATE_FORMAT.format(swmmInput.getEndDate()) + " UTC");

            beansMap.put("-1", swmmProjectBean);
            for (final CidsBean timeseriesBean : timeseriesBeans) {
                beansMap.put(timeseriesBean.getProperty("id").toString(), timeseriesBean);
            }

            this.swmmInputListener = new SwmmInputListener(beansMap);

            swmmProjectLink.setText((String)swmmProjectBean.getProperty("title")); // NOI18N
            swmmProjectLink.addActionListener(WeakListeners.create(
                    ActionListener.class,
                    swmmInputListener,
                    swmmProjectLink));
            swmmProjectLink.setActionCommand("-1");

            final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);

            for (final CidsBean timeseriesBean : timeseriesBeans) {
                final JXHyperlink timeseriesLink = new JXHyperlink();
                timeseriesLink.setText((String)timeseriesBean.getProperty("name")); // NOI18N
                timeseriesLink.setActionCommand(timeseriesBean.getProperty("id").toString());
                timeseriesLink.addActionListener(WeakListeners.create(
                        ActionListener.class,
                        swmmInputListener,
                        timeseriesLink));
                add(timeseriesLink, gridBagConstraints);

                gridBagConstraints.gridy++;
            }
        } catch (final IOException ex) {
            // swmmProjectLink.setText("ERROR: " + ex);                               // NOI18N
            // // NOI18N
            LOG.error("cannot initialise swmm input manager ui", ex); // NOI18N
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

        lblSwmmProject = new javax.swing.JLabel();
        lbTimeseries = new javax.swing.JLabel();
        swmmProjectLink = new org.jdesktop.swingx.JXHyperlink();
        lbStartDate = new javax.swing.JLabel();
        lbEndDate = new javax.swing.JLabel();
        startDateLabel = new javax.swing.JLabel();
        endDateLabel = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblSwmmProject.setText(NbBundle.getMessage(SwmmInputManagerUI.class, "SwmmInputManagerUI.lblSwmmProject.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblSwmmProject, gridBagConstraints);

        lbTimeseries.setText(NbBundle.getMessage(SwmmInputManagerUI.class, "SwmmInputManagerUI.lbTimeseries.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbTimeseries, gridBagConstraints);

        swmmProjectLink.setText(NbBundle.getMessage(
                SwmmInputManagerUI.class,
                "SwmmInputManagerUI.swmmProjectLink.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(swmmProjectLink, gridBagConstraints);

        lbStartDate.setText(NbBundle.getMessage(SwmmInputManagerUI.class, "SwmmInputManagerUI.lbStartDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbStartDate, gridBagConstraints);

        lbEndDate.setText(NbBundle.getMessage(SwmmInputManagerUI.class, "SwmmInputManagerUI.lbEndDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbEndDate, gridBagConstraints);

        startDateLabel.setText(org.openide.util.NbBundle.getMessage(
                SwmmInputManagerUI.class,
                "SwmmInputManagerUI.startDateLabel.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(startDateLabel, gridBagConstraints);
        startDateLabel.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        SwmmInputManagerUI.class,
                        "SwmmInputManagerUI.startDateLabel.AccessibleContext.accessibleName")); // NOI18N

        endDateLabel.setText(org.openide.util.NbBundle.getMessage(
                SwmmInputManagerUI.class,
                "SwmmInputManagerUI.endDateLabel.text"));                                // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(endDateLabel, gridBagConstraints);
        endDateLabel.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        SwmmInputManagerUI.class,
                        "SwmmInputManagerUI.jLabel1.AccessibleContext.accessibleName")); // NOI18N
    }                                                                                    // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class SwmmInputListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        final HashMap<String, CidsBean> beansMap;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SwmmInputListener object.
         *
         * @param  beansMap  DOCUMENT ME!
         */
        public SwmmInputListener(final HashMap<String, CidsBean> beansMap) {
            this.beansMap = beansMap;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (beansMap.containsKey(e.getActionCommand())) {
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObject(beansMap.get(e.getActionCommand()).getMetaObject(), null);
            } else {
                LOG.warn("beans map does not contain cids bean '" + e.getActionCommand() + "'");
            }
        }
    }
}
