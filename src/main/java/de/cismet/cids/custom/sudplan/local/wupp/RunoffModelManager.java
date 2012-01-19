/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

import de.cismet.cids.custom.sudplan.AbstractAsyncModelManager;
import de.cismet.cids.custom.sudplan.AbstractModelRunWatchable;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.geocpmrest.GeoCPMRestClient;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMInput;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class RunoffModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(RunoffModelManager.class);

    public static final String CLIENT_URL = "http://192.168.100.12:9986/GeoCPM"; // NOI18N

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void prepareExecution() throws IOException {
        if (isFinished()) {
            return;
        }

        final RunoffIO io = (RunoffIO)getUR();
        final CidsBean geocpmBean = io.fetchGeocpmInput();
        final CidsBean rainevent = io.fetchRainevent();
        final GeoCPMInput input = new GeoCPMInput();

        input.configName = (String)geocpmBean.getProperty("filename"); // NOI18N
        input.rainevent = (String)rainevent.getProperty("data");       // NOI18N

        final GeoCPMRestClient client = new GeoCPMRestClient(CLIENT_URL);
        final String runId = null; // client.runGeoCPM(input);

        final GeoCPMRunInfo runinfo = new GeoCPMRunInfo(runId, CLIENT_URL);
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, runinfo);

            cidsBean.setProperty("runinfo", writer.toString());      // NOI18N
            cidsBean = cidsBean.persist();
        } catch (final Exception ex) {
            final String message = "cannot store runinfo: " + runId; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (!(getWatchable() instanceof GeoCPMWatchable)) {
            throw new IllegalStateException("cannot create output if there is no valid watchable"); // NOI18N
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating output bean for run: " + cidsBean); // NOI18N
        }

        final GeoCPMWatchable watch = (GeoCPMWatchable)getWatchable();

        final String runId = watch.getRunId();

        try {
            final CidsBean modelOutput = SMSUtils.createModelOutput("Output of Run: " + runId, // NOI18N
                    watch.getOutput(),
                    SMSUtils.Model.GEOCPM);

            return modelOutput.persist();
        } catch (final Exception e) {
            final String message = "cannot get results for run: " + runId; // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String info = (String)cidsBean.getProperty("runinfo"); // NOI18N

        try {
            final GeoCPMRunInfo runInfo = mapper.readValue(info, GeoCPMRunInfo.class);

            return new GeoCPMWatchable(cidsBean, new GeoCPMRestClient(runInfo.getClientUrl()), runInfo.getRunId());
        } catch (final Exception ex) {
            final String message = "cannot read runInfo from run: " + cidsBean; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
    }

    @Override
    protected String getReloadId() {
        try {
            final RunoffIO io = (RunoffIO)getUR();
            final CidsBean geocpmBean = io.fetchGeocpmInput();

            final CidsBean iaBean = (CidsBean)geocpmBean.getProperty("investigation_area"); // NOI18N
            final int iaId = iaBean.getMetaObject().getID();

            return "ia_id" + iaId + "_scenarios";  // NOI18N
        } catch (final Exception e) {
            LOG.warn("cannot fetch reload id", e); // NOI18N

            return null;
        }
    }
}
