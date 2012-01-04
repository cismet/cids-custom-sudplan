/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TimeSeriesTrashBin is used within the TimeSeries Import Wizard. It's purpose to handle files which were transmitted
 * by a cancelled import process (see {@link TimeSeriesTrashBin#markForRemoteDeletion(java.lang.String) }). Those
 * transmitted files are saved here to be deleted when Sudplan application is is closed. As it might happen that the
 * user wants to perform another import but with a TimeSeries name which was used in a foregoing cancelled import in
 * which the files were transmitted before successful cancellation, it is necessary to check if there is already such a
 * file (specified by URI) saved in TimeSeriesTrashBin. If so, all files contained in TimeSeriesTrashBin are removed
 * from the remote location to allow the transmission of the new TimeSeries file (see
 * {@link TimeSeriesTrashBin#checkAndClean(java.lang.String) }).
 *
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  1.0, 04.01.2012
 */
public final class TimeSeriesTrashBin {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TimeSeriesTrashBin.class);

    private static final TimeSeriesTrashBin INSTANCE = new TimeSeriesTrashBin();

    //~ Instance fields --------------------------------------------------------

    private final List<String> filesToBeDeleted;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesTrashBin object.
     */
    private TimeSeriesTrashBin() {
        this.filesToBeDeleted = new ArrayList<String>();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                    @Override
                    public void run() {
                        performRemoteDeletion();
                    }
                }));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Singletone factory method.
     *
     * @return  TimeSeriesTrashBin instance
     */
    public static TimeSeriesTrashBin getInstance() {
        return INSTANCE;
    }

    /**
     * Deletes all marked (see {@link TimeSeriesTrashBin#markForRemoteDeletion(java.lang.String) }) files from the
     * remote Location (WebDAV).
     */
    private void performRemoteDeletion() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering performRemoteDeletion()");
        }
        final HttpClient client = TimeSeriesRemoteHelper.createHttpClient();

        DeleteMethod del;
        synchronized (this) {
            for (final String uri : this.filesToBeDeleted) {
                del = new DeleteMethod(uri);
                try {
                    client.executeMethod(del);
                } catch (final Exception ex) {
                    LOG.error("An error occured while deleting remote file " + uri, ex);
                    del.abort();
                } finally {
                    del.releaseConnection();
                }
            }

            this.filesToBeDeleted.clear();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving performRemoteDeletion()");
        }
    }

    /**
     * Method to save tedious work: It performs standard String param checks and throws corresponding RuntimeExceptions,
     * if necessary.
     *
     * @param   param  param to be checked
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private void checkParam(final String param) {
        if (param == null) {
            throw new NullPointerException();
        }

        if (param.trim().isEmpty()) {
            throw new IllegalArgumentException("Given param must not be empty");
        }
    }

    /**
     * This method is intended to be used before a new file (referenced by URI) shall be transferred to the DAV. If the
     * given URI is already marked for deletion (e.g. because of foregoing canceled transmissions and/or errors), the
     * corresponding has to be deleted first. In doing so, all already marked files are deleted as well.
     *
     * @param  uri  file URI
     */
    public synchronized void checkAndClean(final String uri) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering checkAndClean(String) with uri=" + uri);
        }

        this.checkParam(uri);

        if (this.filesToBeDeleted.contains(uri)) {
            performRemoteDeletion();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving checkAndClean(String)");
        }
    }

    /**
     * Actual logic of marker methods.
     *
     * @param  uri  file URI
     */
    private void add(final String uri) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering add(String) with uri=" + uri);
        }

        this.checkParam(uri);

        if (!this.filesToBeDeleted.contains(uri)) {
            this.filesToBeDeleted.add(uri);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving add(String)");
        }
    }

    /**
     * Marks specified file URI to be deleted when the Sudplan application is shutdown.
     *
     * @param  uri  file URI
     */
    public synchronized void markForRemoteDeletion(final String uri) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering markForRemoteDeletion(String) with uri=" + uri);
        }

        this.add(uri);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving markForRemoteDeletion(String)");
        }
    }

    /**
     * Marks specified file URIs to be deleted when the Sudplan application is shutdown.
     *
     * @param   uris  file URIs
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public synchronized void markForRemoteDeletion(final Collection<String> uris) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering markForRemoteDeletion(Collection<String>) with uri=" + uris);
        }

        if (uris == null) {
            throw new NullPointerException("Given Collection of (String) URIs must not be null");
        }

        if (uris.isEmpty()) {
            throw new IllegalArgumentException("Given Collection of (String) URIs must not be empty");
        }

        for (final String uri : uris) {
            this.add(uri);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving markForRemoteDeletion(Collection<String>)");
        }
    }
}
