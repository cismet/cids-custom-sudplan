/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXHyperlink;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.local.linz.SwmmInputManagerUI;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public class SwmmProjectRenderer extends AbstractCidsBeanRenderer implements TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmProjectRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmProjectTitleComponent titleComponent = new SwmmProjectTitleComponent();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel configPanel;
    private javax.swing.JTextArea configurationArea;
    private javax.swing.JPanel etaRunPanel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionText;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitleText;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JPanel swmmRunPanel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RunRenderer.
     */
    public SwmmProjectRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        final List<CidsBean> swmmScenarios = (List)cidsBean.getProperty("swmm_scenarios"); // NOI18N
        final List<CidsBean> etaScenarios = (List)cidsBean.getProperty("eta_scenarios");   // NOI18N
        final HashMap beansMap = new HashMap(swmmScenarios.size() + etaScenarios.size());
        final ScenarioListener scenarioListener = new ScenarioListener(beansMap);

        for (final CidsBean swmmBean : swmmScenarios) {
            final String key = "SWMM::" + swmmBean.getProperty("id");
            beansMap.put(key, swmmBean);
            final JXHyperlink hyperLink = new JXHyperlink();
            hyperLink.setText((String)swmmBean.getProperty("name")); // NOI18N
            hyperLink.setActionCommand(key);
            hyperLink.addActionListener(WeakListeners.create(
                    ActionListener.class,
                    scenarioListener,
                    hyperLink));
            this.swmmRunPanel.add(hyperLink, gridBagConstraints);
            gridBagConstraints.gridy++;
        }

        gridBagConstraints.gridx = 0;
        for (final CidsBean etaBean : etaScenarios) {
            final String key = "ETA::" + etaBean.getProperty("id");
            beansMap.put(key, etaBean);
            final JXHyperlink hyperLink = new JXHyperlink();
            hyperLink.setText((String)etaBean.getProperty("name")); // NOI18N
            hyperLink.setActionCommand(key);
            hyperLink.addActionListener(WeakListeners.create(
                    ActionListener.class,
                    scenarioListener,
                    hyperLink));
            this.etaRunPanel.add(hyperLink, gridBagConstraints);
            gridBagConstraints.gridy++;
        }

        this.lblTitleText.setText(cidsBean.getProperty("title").toString());
        this.lblDescriptionText.setText(cidsBean.getProperty("description").toString());
        this.configurationArea.setText(cidsBean.getProperty("options").toString());
        titleComponent.setCidsBean(cidsBean);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        lblTitleText = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblDescriptionText = new javax.swing.JLabel();
        configPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        configurationArea = new javax.swing.JTextArea();
        previewPanel = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();
        swmmRunPanel = new javax.swing.JPanel();
        etaRunPanel = new javax.swing.JPanel();

        setOpaque(false);

        lblTitle.setText(NbBundle.getMessage(SwmmProjectRenderer.class, "SwmmProjectRenderer.lblTitle.text")); // NOI18N

        lblDescription.setText(NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.lblDescription.text")); // NOI18N

        configPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.configPanel.title"))); // NOI18N
        configPanel.setOpaque(false);
        configPanel.setLayout(new java.awt.GridLayout(1, 0));

        configurationArea.setColumns(20);
        configurationArea.setLineWrap(true);
        configurationArea.setRows(6);
        jScrollPane2.setViewportView(configurationArea);

        configPanel.add(jScrollPane2);

        previewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.previewPanel.border.title"))); // NOI18N
        previewPanel.setOpaque(false);
        previewPanel.setLayout(new java.awt.GridLayout(1, 0));

        previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewLabel.setIcon(new javax.swing.JLabel() {

                @Override
                public javax.swing.Icon getIcon() {
                    try {
                        return new javax.swing.ImageIcon(
                                new java.net.URL(
                                    "http://sudplan.cismet.de/geoserver/wms?SERVICE=WMS&&VERSION=1.1.1&REQUEST=GetMap&BBOX=13.966774023257251,48.045408747871186,14.539433634959217,48.535798950568086&WIDTH=355&HEIGHT=304&SRS=EPSG:4326&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=Sudplan-Linz:Linz_TUG_WS_2011-05-09_SUBCATCHMENTS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_CONDUITS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_WEIRS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_PUMPS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_JUNCTIONS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_STORAGE_UNITS,Sudplan-Linz:Linz_TUG_WS_2011-05-09_OUTFALLS&STYLES=Subcatchment,Conduit,line,pump,Junction,Storage_Unit,Outfall"));
                    } catch (java.net.MalformedURLException e) {
                    }
                    return null;
                }
            }.getIcon());
        previewLabel.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectRenderer.class,
                "SwmmProjectRenderer.previewLabel.text")); // NOI18N
        previewLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        previewPanel.add(previewLabel);

        swmmRunPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.swmmRunPanel.border.title"))); // NOI18N
        swmmRunPanel.setOpaque(false);
        swmmRunPanel.setLayout(new java.awt.GridBagLayout());

        etaRunPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmProjectRenderer.class,
                    "SwmmProjectRenderer.etaRunPanel.border.title"))); // NOI18N
        etaRunPanel.setOpaque(false);
        etaRunPanel.setLayout(new java.awt.GridBagLayout());

        final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(19, 19, 19).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(
                        configPanel,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        360,
                        Short.MAX_VALUE).add(
                        layout.createSequentialGroup().add(
                            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                lblDescription).add(
                                lblTitle,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                68,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(50, 50, 50).add(
                            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                lblDescriptionText,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE).add(
                                lblTitleText,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE))).add(
                        swmmRunPanel,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(
                        previewPanel,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        360,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        etaRunPanel,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap(
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        org.jdesktop.layout.GroupLayout.TRAILING,
                        lblTitleText,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        14,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        lblTitle,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        14,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.UNRELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(
                        lblDescription,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        lblDescriptionText,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(
                        previewPanel,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        300,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        configPanel,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        swmmRunPanel,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        etaRunPanel,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public JComponent getTitleComponent() {
        return titleComponent;
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);

        titleComponent.setTitle(title);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ScenarioListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        final HashMap<String, CidsBean> beansMap;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ScenarioListener object.
         *
         * @param  beansMap  DOCUMENT ME!
         */
        public ScenarioListener(final HashMap<String, CidsBean> beansMap) {
            this.beansMap = beansMap;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if ((beansMap != null) && beansMap.containsKey(e.getActionCommand())) {
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObject(beansMap.get(e.getActionCommand()).getMetaObject(), null);
            } else {
                LOG.warn("beans map does not contain cids bean '" + e.getActionCommand() + "'");
            }
        }
    }
}
