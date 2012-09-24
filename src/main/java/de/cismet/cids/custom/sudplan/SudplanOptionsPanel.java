/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.jdom.Element;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import de.cismet.lookupoptions.AbstractOptionsPanel;
import de.cismet.lookupoptions.OptionsPanelController;

import de.cismet.tools.configuration.NoWriteError;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = OptionsPanelController.class)
public class SudplanOptionsPanel extends AbstractOptionsPanel {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblAQSosUrl;
    private javax.swing.JLabel lblAQSpsUrl;
    private javax.swing.JLabel lblAqEdbUrl;
    private javax.swing.JLabel lblHDHypeIp;
    private javax.swing.JLabel lblRFSosUrl;
    private javax.swing.JLabel lblRFSpsUrl;
    private javax.swing.JPanel pnlAQ;
    private javax.swing.JPanel pnlCS;
    private javax.swing.JPanel pnlHD;
    private javax.swing.JPanel pnlRF;
    private javax.swing.JTextField txtAQSosUrl;
    private javax.swing.JTextField txtAQSpsUrl;
    private javax.swing.JTextField txtAqEdbUrl;
    private javax.swing.JTextField txtHDHypeIp;
    private javax.swing.JTextField txtRFSosUrl;
    private javax.swing.JTextField txtRFSpsUrl;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SudplanOptionsPanel.
     */
    public SudplanOptionsPanel() {
        super("Common Services", SudplanOptionsCategory.class);

        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void configure(final Element parent) {
        doConfigure(parent);
    }

    @Override
    public Element getConfiguration() throws NoWriteError {
        final Element options = new Element("sudplanOptions"); // NOI18N
        final Element cs = new Element("commonServices");      // NOI18N
        options.addContent(cs);

        final Element aq = new Element("airquality");   // NOI18N
        final Element aqSosUrl = new Element("sosUrl"); // NOI18N
        final Element aqSpsUrl = new Element("spsUrl"); // NOI18N
        final Element aqEdbUrl = new Element("edbUrl"); // NOI18N
        aqSosUrl.setText(txtAQSosUrl.getText());
        aqSpsUrl.setText(txtAQSpsUrl.getText());
        aqEdbUrl.setText(txtAqEdbUrl.getText());
        aq.addContent(aqSosUrl);
        aq.addContent(aqSpsUrl);
        aq.addContent(aqEdbUrl);
        cs.addContent(aq);

        final Element hd = new Element("hydrology");  // NOI18N
        final Element hypeIp = new Element("hypeIp"); // NOI18N
        hypeIp.setText(txtHDHypeIp.getText());
        hd.addContent(hypeIp);
        cs.addContent(hd);

        final Element rf = new Element("rainfall");     // NOI18N
        final Element rfSosUrl = new Element("sosUrl"); // NOI18N
        final Element rfSpsUrl = new Element("spsUrl"); // NOI18N
        rfSosUrl.setText(txtRFSosUrl.getText());
        rfSpsUrl.setText(txtRFSpsUrl.getText());
        rf.addContent(rfSosUrl);
        rf.addContent(rfSpsUrl);
        cs.addContent(rf);

        return options;
    }

    @Override
    public void masterConfigure(final Element parent) {
        doConfigure(parent);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    private void doConfigure(final Element parent) {
        if (parent == null) {
            // no option section present, simply leave
            return;
        }

        final Element options = parent.getChild("sudplanOptions"); // NOI18N
        if (options == null) {
            // no options present, simply leave
            return;
        }

        final SudplanOptions opts = SudplanOptions.getInstance();
        final Element cs = options.getChild("commonServices"); // NOI18N

        final Element aq = cs.getChild("airquality");   // NOI18N
        final Element aqSosUrl = aq.getChild("sosUrl"); // NOI18N
        final Element aqSpsUrl = aq.getChild("spsUrl"); // NOI18N
        final Element aqEdbUrl = aq.getChild("edbUrl"); // NOI18N
        final String aqSos = aqSosUrl.getText();
        final String aqSps = aqSpsUrl.getText();
        final String aqEdb = aqEdbUrl.getText();
        txtAQSosUrl.setText(aqSos);
        txtAQSpsUrl.setText(aqSps);
        txtAqEdbUrl.setText(aqEdb);
        opts.setAqSosUrl(aqSos);
        opts.setAqSpsUrl(aqSps);
        opts.setAqEdbUrl(aqEdb);

        final Element hd = cs.getChild("hydrology");  // NOI18N
        final Element hypeIp = hd.getChild("hypeIp"); // NOI18N
        final String hype = hypeIp.getText();
        txtHDHypeIp.setText(hype);
        opts.setHdHypeIp(hype);

        final Element rf = cs.getChild("rainfall");     // NOI18N
        final Element rfSosUrl = rf.getChild("sosUrl"); // NOI18N
        final Element rfSpsUrl = rf.getChild("spsUrl"); // NOI18N
        final String rfSos = rfSosUrl.getText();
        final String rfSps = rfSpsUrl.getText();
        txtRFSosUrl.setText(rfSos);
        txtRFSpsUrl.setText(rfSps);
        opts.setRfSosUrl(rfSos);
        opts.setRfSpsUrl(rfSps);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlCS = new javax.swing.JPanel();
        pnlAQ = new javax.swing.JPanel();
        lblAQSosUrl = new javax.swing.JLabel();
        lblAQSpsUrl = new javax.swing.JLabel();
        txtAQSosUrl = new javax.swing.JTextField();
        txtAQSpsUrl = new javax.swing.JTextField();
        lblAqEdbUrl = new javax.swing.JLabel();
        txtAqEdbUrl = new javax.swing.JTextField();
        pnlHD = new javax.swing.JPanel();
        lblHDHypeIp = new javax.swing.JLabel();
        txtHDHypeIp = new javax.swing.JTextField();
        pnlRF = new javax.swing.JPanel();
        lblRFSosUrl = new javax.swing.JLabel();
        lblRFSpsUrl = new javax.swing.JLabel();
        txtRFSosUrl = new javax.swing.JTextField();
        txtRFSpsUrl = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlCS.setOpaque(false);
        pnlCS.setLayout(new java.awt.GridBagLayout());

        pnlAQ.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.pnlAQ.border.title"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pnlAQ.setOpaque(false);
        pnlAQ.setLayout(new java.awt.GridBagLayout());

        lblAQSosUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.lblAQSosUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAQ.add(lblAQSosUrl, gridBagConstraints);

        lblAQSpsUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.lblAQSpsUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAQ.add(lblAQSpsUrl, gridBagConstraints);

        txtAQSosUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.txtAQSosUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAQ.add(txtAQSosUrl, gridBagConstraints);

        txtAQSpsUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.txtAQSpsUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAQ.add(txtAQSpsUrl, gridBagConstraints);

        lblAqEdbUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.lblAqEdbUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAQ.add(lblAqEdbUrl, gridBagConstraints);

        txtAqEdbUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.txtAqEdbUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlAQ.add(txtAqEdbUrl, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlCS.add(pnlAQ, gridBagConstraints);

        pnlHD.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.pnlHD.border.title"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pnlHD.setOpaque(false);
        pnlHD.setLayout(new java.awt.GridBagLayout());

        lblHDHypeIp.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.lblHDHypeIp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlHD.add(lblHDHypeIp, gridBagConstraints);

        txtHDHypeIp.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.txtHDHypeIp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlHD.add(txtHDHypeIp, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlCS.add(pnlHD, gridBagConstraints);

        pnlRF.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.pnlRF.border.title"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pnlRF.setOpaque(false);
        pnlRF.setLayout(new java.awt.GridBagLayout());

        lblRFSosUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.lblRFSosUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlRF.add(lblRFSosUrl, gridBagConstraints);

        lblRFSpsUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.lblRFSpsUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlRF.add(lblRFSpsUrl, gridBagConstraints);

        txtRFSosUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.txtRFSosUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlRF.add(txtRFSosUrl, gridBagConstraints);

        txtRFSpsUrl.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.txtRFSpsUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlRF.add(txtRFSpsUrl, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlCS.add(pnlRF, gridBagConstraints);

        jLabel1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/information.png")));                  // NOI18N
        jLabel1.setText(NbBundle.getMessage(SudplanOptionsPanel.class, "SudplanOptionsPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        pnlCS.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlCS, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents
}
