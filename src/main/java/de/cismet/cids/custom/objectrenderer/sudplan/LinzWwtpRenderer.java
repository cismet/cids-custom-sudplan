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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.Border;


import de.cismet.cids.custom.sudplan.ImageUtil;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.CismetThreadPool;

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

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LinzWwtpRenderer.class);
    private static final String CARD_1 = "CARD1";
    private static final String CARD_2 = "CARD2";

    //~ Instance fields --------------------------------------------------------

    private CardLayout cardLayout = null;
    private CidsBean cidsBean = null;
    private String title;
    private final Timer timer;
    private LinzWwtpRenderer.ImageResizeWorker currentResizeWorker;
    private boolean firstPageShowing = true;
    private transient BufferedImage firstPageImage;
    private transient BufferedImage secondPageImage;
    private final transient LinzWwtpTitleComponent linzWwtpTitleComponent = new LinzWwtpTitleComponent();

    private boolean resizeListenerEnabled = true;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblPage1;
    private javax.swing.JLabel lblPage2;
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

        try {
            firstPageImage = ImageIO.read(getClass().getResource(
                        "/de/cismet/cids/custom/sudplan/local/linz/wwtp1.png"));
            secondPageImage = ImageIO.read(getClass().getResource(
                        "/de/cismet/cids/custom/sudplan/local/linz/wwtp2.png"));
        } catch (Throwable t) {
            firstPageImage = null;
            LOG.error(t);
        }

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new LinzWwtpRenderer.ImageResizeWorker();
                            CismetThreadPool.execute(currentResizeWorker);
                        }
                    }
                });
        timer.setRepeats(false);

        this.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    timer.restart();
                }
            });
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
        lblPage1 = new javax.swing.JLabel();
        panPage2 = new javax.swing.JPanel();
        lblPage2 = new javax.swing.JLabel();

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        setBackground(new java.awt.Color(204, 204, 204));
        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panPage1.setBackground(new java.awt.Color(255, 255, 255));
        panPage1.setOpaque(false);
        panPage1.setLayout(new java.awt.GridLayout(1, 1));

        lblPage1.setBackground(new java.awt.Color(255, 255, 255));
        lblPage1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPage1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/wwtp1.png"))); // NOI18N
        lblPage1.setText(org.openide.util.NbBundle.getMessage(
                LinzWwtpRenderer.class,
                "LinzWwtpRenderer.lblPage1.text"));                                              // NOI18N
        lblPage1.setIconTextGap(0);
        panPage1.add(lblPage1);

        add(panPage1, "CARD1");

        panPage2.setOpaque(false);
        panPage2.setLayout(new java.awt.BorderLayout());

        lblPage2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPage2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/linz/wwtp2.png"))); // NOI18N
        lblPage2.setText(org.openide.util.NbBundle.getMessage(
                LinzWwtpRenderer.class,
                "LinzWwtpRenderer.lblPage2.text"));                                              // NOI18N
        panPage2.add(lblPage2, java.awt.BorderLayout.CENTER);

        add(panPage2, "CARD2");
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblBackMouseClicked(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseClicked
        btnBackActionPerformed(null);
    }//GEN-LAST:event_lblBackMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(this, CARD_1);
        firstPageShowing = true;
        timer.restart();
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        lblBack.setEnabled(false);
        lblForw.setEnabled(true);
    }//GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        cardLayout.show(this, CARD_2);
        firstPageShowing = false;
        timer.restart();
        btnBack.setEnabled(true);
        btnForward.setEnabled(false);
        lblBack.setEnabled(true);
        lblForw.setEnabled(false);
    }//GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblForwMouseClicked(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblForwMouseClicked
        btnForwardActionPerformed(null);
    }//GEN-LAST:event_lblForwMouseClicked

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
        if (currentResizeWorker != null) {
            currentResizeWorker.cancel(true);
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
        // lblTitle.setText(this.title);
        linzWwtpTitleComponent.setTitle(title);
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    @Override
    public JComponent getTitleComponent() {
        // return panTitle;
        return this.linzWwtpTitleComponent;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageResizeWorker extends SwingWorker<ImageIcon, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageResizeWorker object.
         */
        public ImageResizeWorker() {
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            ImageIcon result = null;
            if (firstPageShowing) {
                result = new ImageIcon(ImageUtil.adjustScale(firstPageImage, panPage1, 0, 0));
            } else {
                result = new ImageIcon(ImageUtil.adjustScale(secondPageImage, panPage2, 0, 0));
            }

            return result;
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    resizeListenerEnabled = false;
                    final ImageIcon result = get();

                    if (firstPageShowing) {
                        lblPage1.setIcon(result);
                    } else {
                        lblPage2.setIcon(result);
                    }
                } catch (InterruptedException ex) {
                    LOG.warn(ex, ex);
                } catch (ExecutionException ex) {
                    LOG.error(ex, ex);
                    lblPage1.setText("Fehler beim Skalieren!");
                } finally {
                    if (currentResizeWorker == this) {
                        currentResizeWorker = null;
                    }
                    resizeListenerEnabled = true;
                }
            }
        }
    }
}
