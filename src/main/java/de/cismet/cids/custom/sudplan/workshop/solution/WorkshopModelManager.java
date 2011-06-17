/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.workshop.solution;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.cismet.cids.custom.sudplan.AbstractModelManager;
import de.cismet.cids.custom.sudplan.Manager;
import de.cismet.cids.custom.sudplan.ManagerType;
import de.cismet.cids.custom.sudplan.RunHelper;
import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class WorkshopModelManager extends AbstractModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final File MODEL_EXECUTABLE = new File("/Users/mscholl/Desktop/workshop/local_model/model.sh");
    private static final File INPUTFILE = new File("/Users/mscholl/Desktop/workshop/local_model/input.in");
    private static final File OUTPUTFILE = new File("/Users/mscholl/Desktop/workshop/local_model/output.out");

    private static final transient Logger LOG = Logger.getLogger(WorkshopModelManager.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void internalExecute() throws IOException {
        fireProgressed(-1, -1);

        prepareExecution();

        final String command = MODEL_EXECUTABLE.getAbsolutePath() + " " + INPUTFILE.getAbsolutePath() + " "
                    + OUTPUTFILE.getAbsolutePath();

        final Process process = Runtime.getRuntime().exec(command);

        final int result;
        try {
            result = process.waitFor();
        } catch (final InterruptedException ex) {
            final String message = "error during model execution"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        if (result == 0) {
            fireFinised();
        } else {
            throw new IOException("model run execution unsuccessful: " + 1); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void prepareExecution() throws IOException {
        if (INPUTFILE.exists()) {
            if (!INPUTFILE.delete()) {
                throw new IOException("cannot cleanup inputfile"); // NOI18N
            }
        }

        final Manager manager = SMSUtils.loadManagerFromRun(cidsBean, ManagerType.INPUT);
        manager.setCidsBean((CidsBean)cidsBean.getProperty("modelinput")); // NOI18N

        final Object resource = manager.getUR();

        final WorkshopIO io;
        if (resource instanceof WorkshopIO) {
            io = (WorkshopIO)resource;
        } else {
            throw new IllegalStateException("run has illegal modelinput"); // NOI18N
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(INPUTFILE));
            for (final int i : io.getIntegers()) {
                bw.append(String.valueOf(i));
                bw.newLine();
            }
        } finally {
            bw.close();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private String fetchResults() throws IOException {
        if (!OUTPUTFILE.exists()) {
            throw new IOException("cannot find output file: " + OUTPUTFILE); // NOI18N
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(OUTPUTFILE));
            final StringBuilder sb = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                sb.append(line).append(';');

                line = br.readLine();
            }

            return sb.toString();
        } finally {
            br.close();
        }
    }

    @Override
    protected CidsBean createOutputBean() throws IOException {
        final MetaClass modelOutputClass = ClassCacheMultiple.getMetaClass(
                SessionManager.getSession().getUser().getDomain(),
                "modeloutput");                                                                          // NOI18N
        try {
            final CidsBean modelOutputBean = modelOutputClass.getEmptyInstance().getBean();
            modelOutputBean.setProperty("name", "Output of " + RunHelper.createIONameSnippet(cidsBean)); // NOI18N
            modelOutputBean.setProperty("ur", fetchResults());                                           // NOI18N
            modelOutputBean.setProperty("model", cidsBean.getProperty("model"));                         // NOI18N

            return modelOutputBean;
        } catch (final Exception ex) {
            final String message = "cannot create new instance of modelinput"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }
}
