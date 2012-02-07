/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FotosEditor.java
 *
 * Created on 10.08.2010, 16:47:00
 */
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.commons.io.IOUtils;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.lang.ref.SoftReference;

import java.net.URLEncoder;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

import javax.swing.*;
import javax.swing.Timer;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.ImageUtil;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.netutil.Proxy;

import de.cismet.security.WebDavClient;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.CurvedFlowBackgroundPanel;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class LinzCsoRenderer extends AbstractCidsBeanRenderer implements TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LinzCsoRenderer.class);
    private static final ImageIcon ERROR_ICON = new ImageIcon(LinzCsoRenderer.class.getResource(
                "/de/cismet/cids/custom/objectrenderer/sudplan/file-broken.png"));
    private static final ImageIcon FOLDER_ICON = new ImageIcon(LinzCsoRenderer.class.getResource(
                "/de/cismet/cids/custom/objectrenderer/sudplan/inode-directory.png"));
    private static final String WEB_DAV_USER = "tsDav";
    private static final String WEB_DAV_PASSWORD = "RHfio2l4wrsklfghj";
    private static final String WEB_DAV_DIRECTORY = "http://sudplan.cismet.de/tsDav/linz-images/";
    private static final int CACHE_SIZE = 20;
    private static final Map<String, SoftReference<BufferedImage>> IMAGE_CACHE =
        new LinkedHashMap<String, SoftReference<BufferedImage>>(CACHE_SIZE) {

            @Override
            protected boolean removeEldestEntry(final Entry<String, SoftReference<BufferedImage>> eldest) {
                return size() >= CACHE_SIZE;
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final transient LinzCsoTitleComponent titleComponent = new LinzCsoTitleComponent();

    private final Timer timer;
    private final CardLayout cardLayout;
    private ImageResizeWorker currentResizeWorker;
    private BufferedImage image;
    private boolean listListenerEnabled = true;
    private boolean resizeListenerEnabled;
    private final WebDavClient webDavClient;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private de.cismet.cids.custom.sudplan.local.linz.CsoOverflowComparisionPanel csoOverflowComparisionPanel;
    private de.cismet.cids.custom.sudplan.local.linz.CsoTotalOverflowComparisionPanel csoTotalOverflowComparisionPanel;
    private de.cismet.tools.gui.RoundedPanel etaScenarioPanel;
    private org.jdesktop.swingx.JXBusyLabel lblBusy;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionText;
    private javax.swing.JLabel lblEtaScenarios;
    private javax.swing.JLabel lblFotoList;
    private javax.swing.JLabel lblHeading;
    private javax.swing.JLabel lblHeadingPhotos;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNameText;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblSwmmScenarios;
    private javax.swing.JLabel lblVolume;
    private javax.swing.JLabel lblVolumeText;
    private javax.swing.JList lstFotos;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panCard;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfo;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfo1;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfoScenario;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfoScenario1;
    private javax.swing.JPanel panPreview;
    private de.cismet.tools.gui.RoundedPanel photosPanel;
    private de.cismet.tools.gui.RoundedPanel propertiesPanel;
    private javax.swing.JScrollPane scpFotoList;
    private javax.swing.JScrollPane scrollPane;
    private de.cismet.tools.gui.RoundedPanel swmmScenarioPanel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form FotosEditor.
     */
    public LinzCsoRenderer() {
        this(true);
    }

    /**
     * Creates a new FotodokumentationEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public LinzCsoRenderer(final boolean editable) {
        this.listListenerEnabled = true;
        this.resizeListenerEnabled = true;
        this.webDavClient = new WebDavClient(Proxy.fromPreferences(), WEB_DAV_USER, WEB_DAV_PASSWORD);
        initComponents();

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
//                    if (isShowing()) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new ImageResizeWorker();
                            CismetThreadPool.execute(currentResizeWorker);
//                    } else {
//                        timer.restart();
//                    }
                        }
                    }
                });
        timer.setRepeats(false);
        cardLayout = (CardLayout)panCard.getLayout();
        photosPanel.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    if ((image != null) && !lblBusy.isBusy()) {
                        showWait(true);
                        timer.restart();
                    }
                }
            });

        lblPicture.setIcon(FOLDER_ICON);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void dispose() {
        if (currentResizeWorker != null) {
            currentResizeWorker.cancel(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private BufferedImage downloadImageFromWebDAV(final String fileName) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("downloadImageFromWebDAV:" + fileName);
        }
        final String encodedFileName = encodeURL(fileName);
        final InputStream iStream = webDavClient.getInputStream(WEB_DAV_DIRECTORY
                        + encodedFileName);
        if (log.isDebugEnabled()) {
            log.debug("original: " + fileName + "\nweb dav path: " + WEB_DAV_DIRECTORY + encodedFileName);
        }
        try {
            final ImageInputStream iiStream = ImageIO.createImageInputStream(iStream);
            final Iterator<ImageReader> itReader = ImageIO.getImageReaders(iiStream);
            final ImageReader reader = itReader.next();
            final ProgressMonitor monitor = new ProgressMonitor(this, "Bild wird übertragen...", "", 0, 100);
//            monitor.setMillisToPopup(500);
            reader.addIIOReadProgressListener(new IIOReadProgressListener() {

                    @Override
                    public void sequenceStarted(final ImageReader source, final int minIndex) {
                    }

                    @Override
                    public void sequenceComplete(final ImageReader source) {
                    }

                    @Override
                    public void imageStarted(final ImageReader source, final int imageIndex) {
                        monitor.setProgress(monitor.getMinimum());
                    }

                    @Override
                    public void imageProgress(final ImageReader source, final float percentageDone) {
                        if (monitor.isCanceled()) {
                            try {
                                iiStream.close();
                            } catch (IOException ex) {
                                // NOP
                            }
                        } else {
                            monitor.setProgress(Math.round(percentageDone));
                        }
                    }

                    @Override
                    public void imageComplete(final ImageReader source) {
                        monitor.setProgress(monitor.getMaximum());
                    }

                    @Override
                    public void thumbnailStarted(final ImageReader source,
                            final int imageIndex,
                            final int thumbnailIndex) {
                    }

                    @Override
                    public void thumbnailProgress(final ImageReader source, final float percentageDone) {
                    }

                    @Override
                    public void thumbnailComplete(final ImageReader source) {
                    }

                    @Override
                    public void readAborted(final ImageReader source) {
                        monitor.close();
                    }
                });

            final ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(iiStream, true, true);
            final BufferedImage result;
            try {
                result = reader.read(0, param);
            } finally {
                reader.dispose();
                iiStream.close();
            }
            return result;
        } finally {
            IOUtils.closeQuietly(iStream);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String encodeURL(final String url) {
        try {
            if (url == null) {
                return null;
            }
            final String[] tokens = url.split("/", -1);
            StringBuilder encodedURL = null;

            for (final String tmp : tokens) {
                if (encodedURL == null) {
                    encodedURL = new StringBuilder(URLEncoder.encode(tmp, "UTF-8"));
                } else {
                    encodedURL.append("/").append(URLEncoder.encode(tmp, "UTF-8"));
                }
            }

            if (encodedURL != null) {
                // replace all + with %20 because the method URLEncoder.encode() replaces all spaces with '+', but
                // the web dav client interprets %20 as a space.
                return encodedURL.toString().replaceAll("\\+", "%20");
            } else {
                return "";
            }
        } catch (final UnsupportedEncodingException e) {
            log.error("Unsupported encoding.", e);
        }
        return url;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        propertiesPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfo = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeading = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblNameText = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblDescriptionText = new javax.swing.JLabel();
        lblVolume = new javax.swing.JLabel();
        lblVolumeText = new javax.swing.JLabel();
        scpFotoList = new javax.swing.JScrollPane();
        lstFotos = new javax.swing.JList();
        lblFotoList = new javax.swing.JLabel();
        photosPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfo1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadingPhotos = new javax.swing.JLabel();
        panCard = new javax.swing.JPanel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75, 75));
        panPreview = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        final RoundedPanel rp = new RoundedPanel();
        rp.setBackground(new java.awt.Color(51, 51, 51));
        rp.setAlpha(255);
        panButtons = rp;
        panFooterLeft = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        swmmScenarioPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfoScenario = new de.cismet.tools.gui.SemiRoundedPanel();
        lblSwmmScenarios = new javax.swing.JLabel();
        csoOverflowComparisionPanel = new de.cismet.cids.custom.sudplan.local.linz.CsoOverflowComparisionPanel();
        etaScenarioPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfoScenario1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblEtaScenarios = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        csoTotalOverflowComparisionPanel =
            new de.cismet.cids.custom.sudplan.local.linz.CsoTotalOverflowComparisionPanel();

        setLayout(new java.awt.GridBagLayout());

        propertiesPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setLayout(new java.awt.FlowLayout());

        lblHeading.setForeground(new java.awt.Color(255, 255, 255));
        lblHeading.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblHeading.text")); // NOI18N
        panHeadInfo.add(lblHeading);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        propertiesPanel.add(panHeadInfo, gridBagConstraints);

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11));                                                          // NOI18N
        lblName.setText(org.openide.util.NbBundle.getMessage(LinzCsoRenderer.class, "LinzCsoRenderer.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblNameText, gridBagConstraints);

        lblDescription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescription.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblDescription.text"));            // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblDescription, gridBagConstraints);

        lblDescriptionText.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblDescriptionText.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblDescriptionText, gridBagConstraints);

        lblVolume.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVolume.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblVolume.text"));            // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblVolume, gridBagConstraints);

        lblVolumeText.setName(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblVolumeText.name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblVolumeText, gridBagConstraints);

        scpFotoList.setPreferredSize(new java.awt.Dimension(258, 150));

        lstFotos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFotos.setMinimumSize(new java.awt.Dimension(100, 150));
        lstFotos.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFotosValueChanged(evt);
                }
            });
        scpFotoList.setViewportView(lstFotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(scpFotoList, gridBagConstraints);

        lblFotoList.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFotoList.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblFotoList.text"));            // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblFotoList, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(propertiesPanel, gridBagConstraints);

        photosPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfo1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setLayout(new java.awt.FlowLayout());

        lblHeadingPhotos.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingPhotos.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblHeadingPhotos.text")); // NOI18N
        panHeadInfo1.add(lblHeadingPhotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        photosPanel.add(panHeadInfo1, gridBagConstraints);

        panCard.setOpaque(false);
        panCard.setLayout(new java.awt.CardLayout());

        lblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBusy.setMaximumSize(new java.awt.Dimension(140, 40));
        lblBusy.setMinimumSize(new java.awt.Dimension(140, 40));
        lblBusy.setPreferredSize(new java.awt.Dimension(140, 40));
        panCard.add(lblBusy, "busy");

        panPreview.setOpaque(false);
        panPreview.setLayout(new java.awt.GridBagLayout());

        lblPicture.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPicture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblPicture.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panPreview.add(lblPicture, gridBagConstraints);

        panCard.add(panPreview, "preview");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        photosPanel.add(panCard, gridBagConstraints);

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setMaximumSize(new java.awt.Dimension(120, 40));
        panButtons.setMinimumSize(new java.awt.Dimension(120, 40));
        panButtons.setOpaque(false);
        panButtons.setPreferredSize(new java.awt.Dimension(120, 40));
        panButtons.setLayout(new java.awt.GridBagLayout());

        panFooterLeft.setMaximumSize(new java.awt.Dimension(20, 40));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(20, 40));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(20, 40));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        btnBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/sudplan/arrow-left.png")));          // NOI18N
        btnBack.setBorder(null);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/sudplan/arrow-left-pressed.png")));  // NOI18N
        btnBack.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/sudplan/arrow-left-selected.png"))); // NOI18N
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panFooterLeft.add(btnBack);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panButtons.add(panFooterLeft, gridBagConstraints);

        panFooterRight.setMaximumSize(new java.awt.Dimension(20, 40));
        panFooterRight.setMinimumSize(new java.awt.Dimension(20, 40));
        panFooterRight.setOpaque(false);
        panFooterRight.setPreferredSize(new java.awt.Dimension(20, 40));
        panFooterRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/sudplan/arrow-right.png")));          // NOI18N
        btnForward.setBorder(null);
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new java.awt.Dimension(30, 30));
        btnForward.setMinimumSize(new java.awt.Dimension(30, 30));
        btnForward.setPreferredSize(new java.awt.Dimension(30, 30));
        btnForward.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/sudplan/arrow-right-pressed.png")));  // NOI18N
        btnForward.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/sudplan/arrow-right-selected.png"))); // NOI18N
        btnForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnForwardActionPerformed(evt);
                }
            });
        panFooterRight.add(btnForward);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panButtons.add(panFooterRight, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        photosPanel.add(panButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        add(photosPanel, gridBagConstraints);

        swmmScenarioPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfoScenario.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfoScenario.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario.setLayout(new java.awt.FlowLayout());

        lblSwmmScenarios.setForeground(new java.awt.Color(255, 255, 255));
        lblSwmmScenarios.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblSwmmScenarios.text")); // NOI18N
        panHeadInfoScenario.add(lblSwmmScenarios);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        swmmScenarioPanel.add(panHeadInfoScenario, gridBagConstraints);

        csoOverflowComparisionPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        csoOverflowComparisionPanel.setLayout(new java.awt.GridLayout(1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        swmmScenarioPanel.add(csoOverflowComparisionPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(swmmScenarioPanel, gridBagConstraints);

        etaScenarioPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfoScenario1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfoScenario1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario1.setLayout(new java.awt.FlowLayout());

        lblEtaScenarios.setForeground(new java.awt.Color(255, 255, 255));
        lblEtaScenarios.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblEtaScenarios.text")); // NOI18N
        panHeadInfoScenario1.add(lblEtaScenarios);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        etaScenarioPanel.add(panHeadInfoScenario1, gridBagConstraints);

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 200));

        csoTotalOverflowComparisionPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        csoTotalOverflowComparisionPanel.setLayout(new java.awt.FlowLayout());
        scrollPane.setViewportView(csoTotalOverflowComparisionPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        etaScenarioPanel.add(scrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(etaScenarioPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  wait  DOCUMENT ME!
     */
    private void showWait(final boolean wait) {
        if (log.isDebugEnabled()) {
            log.debug("showWait:" + wait);
        }
        if (wait) {
            if (!lblBusy.isBusy()) {
                cardLayout.show(panCard, "busy");
                lblPicture.setIcon(null);
                lblBusy.setBusy(true);
                lstFotos.setEnabled(false);
                btnBack.setEnabled(false);
                btnForward.setEnabled(false);
            }
        } else {
            cardLayout.show(panCard, "preview");
            lblBusy.setBusy(false);
            lstFotos.setEnabled(true);
            defineButtonStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    private void indicateError(final String tooltip) {
        log.error("indicateError:" + tooltip);
        lblPicture.setIcon(ERROR_ICON);
        lblPicture.setText("Fehler beim Übertragen des Bildes!");
        lblPicture.setToolTipText(tooltip);
    }

    /**
     * DOCUMENT ME!
     */
    private void loadFoto() {
        if (log.isDebugEnabled()) {
            log.debug("loadFoto: " + lstFotos.getSelectedValue());
        }
        final String photo = lstFotos.getSelectedValue().toString();
        boolean cacheHit = false;
        if (photo != null) {
            final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(photo);
            if (cachedImageRef != null) {
                final BufferedImage cachedImage = cachedImageRef.get();
                if (cachedImage != null) {
                    cacheHit = true;
                    image = cachedImage;
                    showWait(true);
                    timer.restart();
                }
            }
            if (!cacheHit) {
                CismetThreadPool.execute(new LoadSelectedImageWorker(photo));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFotosValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFotosValueChanged
//        if (isShowing()) {
        if (!evt.getValueIsAdjusting() && listListenerEnabled) {
            loadFoto();
        }
//        }
    } //GEN-LAST:event_lstFotosValueChanged

    /**
     * DOCUMENT ME!
     */
    public void defineButtonStatus() {
        if (log.isDebugEnabled()) {
            log.debug("defineButtonStatus");
        }
        final int selectedIdx = lstFotos.getSelectedIndex();
        btnBack.setEnabled(selectedIdx > 0);
        btnForward.setEnabled((selectedIdx < (lstFotos.getModel().getSize() - 1)) && (selectedIdx > -1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() - 1);
    }                                                                           //GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnForwardActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() + 1);
    }                                                                              //GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   originalFile  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String generateWebDAVFileName(final File originalFile) {
        final String[] fileNameSplit = originalFile.getName().split("\\.");
        String webFileName = "FOTO-" + System.currentTimeMillis() + "-" + Math.abs(originalFile.getName().hashCode());
        if (fileNameSplit.length > 1) {
            final String ext = fileNameSplit[fileNameSplit.length - 1];
            webFileName += "." + ext;
        }
        return webFileName;
    }

    @Override
    protected void init() {
        if ((cidsBean != null)) {
            this.lblNameText.setText(
                (cidsBean.getProperty("name") != null) ? cidsBean.getProperty("name").toString() : "");
            this.lblDescriptionText.setText(
                (cidsBean.getProperty("description") != null) ? cidsBean.getProperty("description").toString() : "");
            this.lblVolumeText.setText(
                (cidsBean.getProperty("volume") != null) ? cidsBean.getProperty("volume").toString() : "0.0");

            if (cidsBean.getProperty("photos") != null) {
                final String[] photos = cidsBean.getProperty("photos").toString().split(";");
                final DefaultListModel model = new DefaultListModel();
                for (final String photo : photos) {
                    model.addElement(photo);
                }
                lstFotos.setModel(model);

                if (lstFotos.getModel().getSize() > 0) {
                    lstFotos.setSelectedIndex(0);
                } else {
                    cardLayout.show(panCard, "preview");
                }
            } else {
                log.warn("CSO has no photos assigned");
                lstFotos.setModel(new DefaultListModel());
            }

            if (cidsBean.getProperty("swmm_results") != null) {
                final Collection<CidsBean> swmmResults = (Collection)cidsBean.getProperty("swmm_results");
                this.csoOverflowComparisionPanel.setSwmmResults(swmmResults);
                this.csoTotalOverflowComparisionPanel.setSwmmResults(swmmResults);
            }

            defineButtonStatus();
        }
    }

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
    final class ImageResizeWorker extends SwingWorker<ImageIcon, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageResizeWorker object.
         */
        public ImageResizeWorker() {
            // TODO image im EDT auslesen und final speichern!
            if (image != null) {
                lblPicture.setText("Wird neu skaliert...");
                lstFotos.setEnabled(false);
            }
//            log.fatal("RESIZE Image!", new Exception());
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            if (image != null) {
//                if (panButtons.getSize().getWidth() + 10 < panPreview.getSize().getWidth()) {
                // ImageIcon result = new ImageIcon(ImageUtil.adjustScale(image, panPreview, 20, 20));
                final ImageIcon result = new ImageIcon(ImageUtil.adjustScale(image, panCard, 20, 20));
                return result;
//                } else {
//                    return new ImageIcon(image);
//                }
            } else {
                return null;
            }
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    resizeListenerEnabled = false;
                    final ImageIcon result = get();
                    lblPicture.setIcon(result);
                    lblPicture.setText("");
                    lblPicture.setToolTipText(null);
                } catch (InterruptedException ex) {
                    log.warn(ex, ex);
                } catch (ExecutionException ex) {
                    log.error(ex, ex);
                    lblPicture.setText("Fehler beim Skalieren!");
                } finally {
                    showWait(false);
                    if (currentResizeWorker == this) {
                        currentResizeWorker = null;
                    }
                    resizeListenerEnabled = true;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class LoadSelectedImageWorker extends SwingWorker<BufferedImage, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String file;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadSelectedImageWorker object.
         *
         * @param  toLoad  DOCUMENT ME!
         */
        public LoadSelectedImageWorker(final String toLoad) {
            this.file = toLoad;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected BufferedImage doInBackground() throws Exception {
            if ((file != null) && (file.length() > 0)) {
                return downloadImageFromWebDAV(file);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                image = get();
                if (image != null) {
                    IMAGE_CACHE.put(file, new SoftReference<BufferedImage>(image));
                    timer.restart();
                } else {
                    indicateError("Bild konnte nicht geladen werden: Unbekanntes Bildformat");
                }
            } catch (InterruptedException ex) {
                image = null;
                log.warn(ex, ex);
            } catch (ExecutionException ex) {
                image = null;
                log.error(ex, ex);
                String causeMessage = "";
                final Throwable cause = ex.getCause();
                if (cause != null) {
                    causeMessage = cause.getMessage();
                }
                indicateError(causeMessage);
            } finally {
                if (image == null) {
                    showWait(false);
                }
            }
        }
    }
}
