/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.wizard;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelUploadUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelUploadUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient UploadWizardPanelUpload model;
    private SwmmUploader swmmUploader = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar();
    private final transient javax.swing.JButton uploadButton = new javax.swing.JButton();
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RainfallDownscalingVisualPanelTargetDate.
     *
     * @param  model  DOCUMENT ME!
     */
    public UploadWizardPanelUploadUI(final UploadWizardPanelUpload model) {
        this.model = model;

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                UploadWizardPanelUploadUI.class,
                "UploadWizardPanelUpload.this.name")); // NOI18N

        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (model.isUploadInProgress()) {
            LOG.warn("model run is still in progress");
        } else {
            this.uploadButton.setEnabled(!model.isUploadComplete() || model.isUploadErroneous());
            this.progressBar.setValue(model.isUploadComplete() ? 100 : 0);
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

        setLayout(new java.awt.GridBagLayout());

        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 25);
        add(progressBar, gridBagConstraints);

        uploadButton.setText(org.openide.util.NbBundle.getMessage(
                UploadWizardPanelUploadUI.class,
                "UploadWizardPanelUploadUI.uploadButton.text")); // NOI18N
        uploadButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    uploadButtonActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 5);
        add(uploadButton, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void uploadButtonActionPerformed(final java.awt.event.ActionEvent evt) //GEN-FIRST:event_uploadButtonActionPerformed
    {                                                                              //GEN-HEADEREND:event_uploadButtonActionPerformed

        if (this.swmmUploader != null) {
            LOG.warn("restarting previously canceled or erroneous upload");
        }

        if (model.isUploadInProgress()) {
            LOG.error("unexpected call to upload action, previous upload still in progress!");
            return;
        }

        LOG.info("uploading SWMM INP File '" + model.getInpFile() + "' to WebDAv at "
                    + "'" + UploadWizardAction.SWMM_WEBDAV_HOST + "'");

        this.swmmUploader = null;
        progressBar.setValue(0);

        try {
            final File inpFile = new File(model.getInpFile());
            // progressBar.setMaximum((int)inpFile.length());

            swmmUploader = new SwmmUploader(inpFile);
            swmmUploader.addPropertyChangeListener(new UploadProgressListener());
            CismetThreadPool.execute(swmmUploader);
        } catch (Exception ex) {
            LOG.error("could not upload SWMM INP File '" + model.getInpFile() + "' to WebDAv at "
                        + "'" + UploadWizardAction.SWMM_WEBDAV_HOST + "': " + ex.getMessage(),
                ex);
            model.setUploadErroneous(true);
        }
    } //GEN-LAST:event_uploadButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public UploadWizardPanelUpload getModel() {
        return this.model;
    }

    /**
     * DOCUMENT ME!
     *
     * @return      DOCUMENT ME!
     *
     * @throws      Exception  DOCUMENT ME!
     *
     * @deprecated  DOCUMENT ME!
     */
    public OutputStream getInpFileOutputStream() throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("connecting to SWMM Model Webdav Server '" + UploadWizardAction.SWMM_WEBDAV_HOST + "'");
        }

        try {
            // Wir basteln uns einen Certificat-Manager der alles erlaubt
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(final java.security.cert.X509Certificate[] certs,
                                final String authType) {
                            // No need to implement.
                        }

                        @Override
                        public void checkServerTrusted(final java.security.cert.X509Certificate[] certs,
                                final String authType) {
                            // No need to implement.
                        }
                    }
                };

            // Sag Java: wir vertrauen der Verbindung
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            final URL url = new URL(UploadWizardAction.SWMM_WEBDAV_HOST);
            final HttpsURLConnection http = (HttpsURLConnection)url.openConnection();
            Authenticator.setDefault(new Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                UploadWizardAction.SWMM_WEBDAV_USER,
                                UploadWizardAction.SWMM_WEBDAV_PASSWORD.toCharArray());
                    }
                });

            http.setAllowUserInteraction(true);
            http.setRequestMethod("PUT");
            http.connect();

            return http.getOutputStream();
        } catch (Exception ex) {
            LOG.error("could not create connection to SWMM Model Webdav '"
                        + UploadWizardAction.SWMM_WEBDAV_HOST + "': " + ex.getMessage());

//            wizard.putProperty(
//                    WizardDescriptor.PROP_ERROR_MESSAGE,
//                    NbBundle.getMessage(UploadWizardPanelProject.class,
//                    "UploadWizardPanelUpload.isValid().error", ex.getLocalizedMessage()));

            throw ex;
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class UploadProgressListener implements PropertyChangeListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if ("state".equals(evt.getPropertyName())
                        && (SwingWorker.StateValue.STARTED == evt.getNewValue())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SWMM Upload worker started");
                }
                model.setUploadInProgress(true);
                uploadButton.setEnabled(false);
            } else if ("state".equals(evt.getPropertyName())
                        && (SwingWorker.StateValue.DONE == evt.getNewValue())) {
                // model.setUploadComplete(true);
                // uploadButton.setEnabled(false);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SWMM Upload worker completed");
                }
            } else if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer)evt.getNewValue());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class SwmmUploader extends SwingWorker<Void, Void> {

        //~ Instance fields ----------------------------------------------------

        private final File inpFile;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SwmmUploader object.
         *
         * @param   inpFile  inpFileInputStream DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        SwmmUploader(final File inpFile) throws Exception {
            this.inpFile = inpFile;
            if (LOG.isDebugEnabled()) {
                LOG.debug("SWMM Uploader for Input File '"
                            + inpFile.getCanonicalPath() + "' created");
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Void doInBackground() throws Exception {
            final UsernamePasswordCredentials defaultcreds = new UsernamePasswordCredentials(
                    UploadWizardAction.SWMM_WEBDAV_USER,
                    UploadWizardAction.SWMM_WEBDAV_PASSWORD);

            final SSLSocketFactory sslsf = new SSLSocketFactory(new TrustStrategy() {

                        @Override
                        public boolean isTrusted(final java.security.cert.X509Certificate[] chain,
                                final String authType) throws CertificateException {
                            return true;
                        }
                    });
            final Scheme httpScheme = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
            final Scheme httpsScheme = new Scheme("https", 443, sslsf);
            final SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(httpScheme);
            schemeRegistry.register(httpsScheme);

            final ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);

            final HttpContext localContext = new BasicHttpContext();
            final DefaultHttpClient httpClient = new DefaultHttpClient(cm);

            httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY),
                defaultcreds);

            final URL targetLocation = new URL(
                    UploadWizardAction.SWMM_WEBDAV_HOST
                            + inpFile.getName());

            // unglaublich aber wahr: Wenn der HTTP Client ein PUT (z.B. Upload auf ein WebDAV) mit authentication
            // macht, so wird auch für den ersten autentication request der komplette request body  mitgeschickt. Nach
            // erfolgreicher authentifizierung wird der request body nochmal gesendet. 3 Probleme: 1) doppelter traffic!
            // 2) verwendet man eine unbuffered request entity (z.B. in dem man die content-lenght setzt) schlägt der
            // eigentlich upload nach der authentifizierung fehl, da kein buffer vorhanden ist, der gelsen werden kann
            // Verwendet man eine buffered request entity z.B. in dem man die content-lenght auf auto setzt), wird das
            // file vor dem upload komplett in den speicher geladen. Workaround: Vor dem PUT ein GET ausführen, dass die
            // Authentifizierung macht
            final HttpGet getMethod = new HttpGet(new URL(UploadWizardAction.SWMM_WEBDAV_HOST).toExternalForm());

            HttpResponse response = httpClient.execute(getMethod, localContext);
            if (LOG.isDebugEnabled()) {
                LOG.debug("pre-put authentication with GET returned '"
                            + response.getStatusLine() + "'");
            }

            if ((response.getStatusLine().getStatusCode() != 200)) {
                LOG.warn("pre-put authentication with GET failed with status code: "
                            + response.getStatusLine().getStatusCode());
            }

            getMethod.abort();

            final HttpPut putMethod = new HttpPut(targetLocation.toExternalForm());
            final InputStream fileInputStream = new FileInputStream(inpFile);

            final InputStreamEntity inputStreamRequestEntity = new InputStreamEntity(
                    fileInputStream,
                    inpFile.length()) {

                    @Override
                    public void writeTo(final OutputStream out) throws IOException {
                        if (this.getContent() != null) {
                            final double length = inpFile.length();
                            final byte[] tmp = new byte[4096];
                            double totalBytes = 0;
                            int i = 0;
                            while (!isCancelled() && ((i = this.getContent().read(tmp)) >= 0)) {
                                out.write(tmp, 0, i);
                                totalBytes += i;

                                final int progress = (int)Math.round((totalBytes / length) * 100);
                                setProgress((progress <= 100) ? progress : 100);
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(totalBytes + " bytes written to '" + inpFile.getName()
                                            + "' (" + inpFile.length() + " bytes expected)");
                            }
                        } else {
                            throw new IllegalStateException("Content must be set before entity is written");
                        }
                    }
                };

            // unglaublich aber wahr, wir dürfen die content lenght nicht angeben, sonst wird nicht gebuffert. der
            // buffer ist aber notwendig, weil der client alles 2x hochlädt !1!!!!!!!111111 final InputStreamEntity
            // inputStreamRequestEntity = new InputStreamEntity(fileInputStream, inpFile.length());
            putMethod.setEntity(inputStreamRequestEntity);

            if (LOG.isDebugEnabled()) {
                LOG.debug("starting upload of file '" + inpFile.getName() + "'");
            }
            response = httpClient.execute(putMethod, localContext);
            if (LOG.isDebugEnabled()) {
                LOG.debug("upload of file '" + inpFile.getName() + "' completed");
            }

            if (this.isCancelled()) {
                LOG.warn("SwmmUploader cancelled");
                // model.setUploadCanceled(true);
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            uploadButton.setEnabled(true);
                            // progressBar.setIndeterminate(false);
                            progressBar.setValue(0);
                        }
                    });
            }

            final int statusCode = response.getStatusLine().getStatusCode();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Leaving upload '" + inpFile.getName() + "' with status code: " + statusCode);
            }

            if ((statusCode != 201) && (statusCode != 202)) {
                final String message = "Upload of file '"
                            + inpFile.getName() + "' not successful, server returned status '"
                            + response.getStatusLine() + "'";
                throw new Exception(message);
            }

            try {
                fileInputStream.close();
                httpClient.getConnectionManager().shutdown();
            } catch (Throwable t) {
                LOG.warn("could not close input stream of file '" + inpFile.getName() + "'", t);
            }

            setProgress(100);
            return null;
        }

        @Override
        protected void done() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("done()");
            }
            try {
                get();
                model.setUploadComplete(true);
                uploadButton.setEnabled(false);
            } catch (Exception ex) {
                LOG.error("error during executing upload of SWMM INP File '" + model.getInpFile() + "' to WebDAV at "
                            + "'" + UploadWizardAction.SWMM_WEBDAV_HOST + "': " + ex.getMessage(),
                    ex);

                model.setUploadErroneous(true);
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            // progressBar.setIndeterminate(false);
                            progressBar.setValue(0);
                            uploadButton.setEnabled(true);
                        }
                    });
            }
        }
    }
}
