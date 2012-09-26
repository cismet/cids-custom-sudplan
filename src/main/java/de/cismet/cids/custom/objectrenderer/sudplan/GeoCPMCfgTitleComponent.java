/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.log4j.Logger;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URI;

import de.cismet.cids.custom.objectactions.sudplan.ActionProviderFactory;
import de.cismet.cids.custom.sudplan.cismap3d.Layer3D;
import de.cismet.cids.custom.sudplan.local.wupp.RunGeoCPMWizardAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.interfaces.CidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class GeoCPMCfgTitleComponent extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient ActionListener to3DL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRunGeoCPM;
    private javax.swing.JButton btnTo3DMap;
    private javax.swing.JLabel lblTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GeoCPMCfgTitleComponent.
     */
    public GeoCPMCfgTitleComponent() {
        this.to3DL = new To3DMapActionListener();

        initComponents();

        btnTo3DMap.addActionListener(WeakListeners.create(ActionListener.class, to3DL, btnTo3DMap));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    public void setTitle(final String title) {
        if (EventQueue.isDispatchThread()) {
            lblTitle.setText(title);
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        lblTitle.setText(title);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void setCidsBean(final CidsBean cidsBean) {
        if (btnRunGeoCPM.getAction() instanceof CidsBeanAction) {
            final CidsBeanAction cba = (CidsBeanAction)btnRunGeoCPM.getAction();
            cba.setCidsBean(cidsBean);

            // trigger the action enable
            cba.isEnabled();
        }

        setTitle((String)cidsBean.getProperty("name")); // NOI18N
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblTitle = new javax.swing.JLabel();
        btnRunGeoCPM = new javax.swing.JButton();
        btnTo3DMap = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));                                                          // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText(NbBundle.getMessage(GeoCPMCfgTitleComponent.class, "GeoCPMCfgTitleComponent.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblTitle, gridBagConstraints);

        btnRunGeoCPM.setAction(ActionProviderFactory.getCidsBeanAction(RunGeoCPMWizardAction.class));
        btnRunGeoCPM.setText(NbBundle.getMessage(
                GeoCPMCfgTitleComponent.class,
                "GeoCPMCfgTitleComponent.btnRunGeoCPM.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btnRunGeoCPM, gridBagConstraints);

        btnTo3DMap.setText(NbBundle.getMessage(
                GeoCPMCfgTitleComponent.class,
                "GeoCPMCfgTitleComponent.btnTo3DMap.text")); // NOI18N
        add(btnTo3DMap, new java.awt.GridBagConstraints());
    }                                                        // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class To3DMapActionListener implements ActionListener {

        //~ Static fields/initializers -----------------------------------------

        /** LOGGER. */
        private static final transient Logger LOG = Logger.getLogger(To3DMapActionListener.class);

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final String caps =
                "http://sudplanwp6.cismet.de/geoserver/wms?SERVICE=WMS&EXCEPTIONS=application/vnd.ogc.se_xml&VERSION=1.1.1&REQUEST=GetCapabilities";
            final String tinLayer = "sudplan:GeoCPM_TIN";
            final String beLayer = "sudplan:GeoCPM_be";

            final Layer3D layer3D = Lookup.getDefault().lookup(Layer3D.class);
            if (layer3D != null) {
                try {
                    final URI uri = new URI(caps);
                    layer3D.addWMSLayer(uri, tinLayer, 1);
                    layer3D.addWMSLayer(uri, beLayer, 1);
                } catch (final Exception ex) {
                    LOG.warn("cannot add layers", ex);
                }
            }
        }
    }
}
