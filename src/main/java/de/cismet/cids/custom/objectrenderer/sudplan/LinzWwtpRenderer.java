/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.sudplan;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import java.awt.CardLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.border.Border;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class LinzWwtpRenderer extends javax.swing.JPanel implements BorderProvider,
    CidsBeanRenderer,
    FooterComponentProvider,
    TitleComponentProvider,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final String CARD_1 = "CARD1";
    private static final String CARD_2 = "CARD2";

    //~ Instance fields --------------------------------------------------------

    CardLayout cardLayout = null;
    CidsBean cidsBean = null;
    String title;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private javax.swing.JPanel panPage1;
    private javax.swing.JPanel panPage2;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LinzWwtpRenderer.
     */
    public LinzWwtpRenderer() {
        initComponents();
        final LayoutManager layoutManager = getLayout();
        if (layoutManager instanceof CardLayout) {
            cardLayout = (CardLayout)layoutManager;
            cardLayout.show(this, CARD_1);
        }
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

        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panPage1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panPage2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.BorderLayout());

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridBagLayout());

        panFooterLeft.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14));                                                            // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        lblBack.setText(org.openide.util.NbBundle.getMessage(LinzWwtpRenderer.class, "LinzWwtpRenderer.lblBack.text")); // NOI18N
        lblBack.setEnabled(false);
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblBackMouseClicked(evt);
                }
            });
        panFooterLeft.add(lblBack);

        btnBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/arrow-left.png"))); // NOI18N
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panFooterLeft.add(btnBack);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panButtons.add(panFooterLeft, gridBagConstraints);

        panFooterRight.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterRight.setOpaque(false);
        panFooterRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/arrow-right.png"))); // NOI18N
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new java.awt.Dimension(30, 30));
        btnForward.setMinimumSize(new java.awt.Dimension(30, 30));
        btnForward.setPreferredSize(new java.awt.Dimension(30, 30));
        btnForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnForwardActionPerformed(evt);
                }
            });
        panFooterRight.add(btnForward);

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14));                                                            // NOI18N
        lblForw.setForeground(new java.awt.Color(255, 255, 255));
        lblForw.setText(org.openide.util.NbBundle.getMessage(LinzWwtpRenderer.class, "LinzWwtpRenderer.lblForw.text")); // NOI18N
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblForwMouseClicked(evt);
                }
            });
        panFooterRight.add(lblForw);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panButtons.add(panFooterRight, gridBagConstraints);

        panFooter.add(panButtons, java.awt.BorderLayout.CENTER);

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText(org.openide.util.NbBundle.getMessage(
                LinzWwtpRenderer.class,
                "LinzWwtpRenderer.lblTitle.text"));           // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        setLayout(new java.awt.CardLayout());

        panPage1.setOpaque(false);
        panPage1.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/wwtp1.png")));                        // NOI18N
        jLabel1.setText(org.openide.util.NbBundle.getMessage(LinzWwtpRenderer.class, "LinzWwtpRenderer.jLabel1.text")); // NOI18N
        panPage1.add(jLabel1, java.awt.BorderLayout.CENTER);

        add(panPage1, "CARD1");

        panPage2.setLayout(new java.awt.BorderLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/wwtp2.png")));                        // NOI18N
        jLabel2.setText(org.openide.util.NbBundle.getMessage(LinzWwtpRenderer.class, "LinzWwtpRenderer.jLabel2.text")); // NOI18N
        panPage2.add(jLabel2, java.awt.BorderLayout.CENTER);

        add(panPage2, "CARD2");
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblBackMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblBackMouseClicked
        btnBackActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblBackMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(this, CARD_1);
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        lblBack.setEnabled(false);
        lblForw.setEnabled(true);
    }                                                                           //GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnForwardActionPerformed
        cardLayout.show(this, CARD_2);
        btnBack.setEnabled(true);
        btnForward.setEnabled(false);
        lblBack.setEnabled(true);
        lblForw.setEnabled(false);
    }                                                                              //GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblForwMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblForwMouseClicked
        btnForwardActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblForwMouseClicked

    @Override
    public Border getCenterrBorder() {
        return null;
    }

    @Override
    public Border getFooterBorder() {
        return null;
    }

    @Override
    public Border getTitleBorder() {
        return null;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
        lblTitle.setText(this.title);
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.showTestFrame(new LinzWwtpRenderer(), 1024, 768);
    }
}
