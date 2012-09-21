/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;

import at.ac.ait.enviro.sudplan.clientutil.SudplanSOSHelper;
import at.ac.ait.enviro.sudplan.clientutil.SudplanSPSHelper;
import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.util.text.ISO8601DateFormat;

import com.vividsolutions.jts.geom.Envelope;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.text.DateFormat;

import java.util.*;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.local.linz.wizard.SwmmPlusEtaWizardAction;
import de.cismet.cids.custom.sudplan.rainfall.RainfallDownscalingModelManager;
import de.cismet.cids.custom.sudplan.rainfall.RainfallRunInfo;
import de.cismet.cids.custom.sudplan.server.trigger.SwmmResultGeoserverUpdater;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaModelManager.class);
    public static final String TABLENAME_LINZ_ETA_RESULT = "linz_eta_result";

    //~ Methods ----------------------------------------------------------------

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (!(getWatchable() instanceof EtaWatchable)) {
            throw new IllegalStateException("cannot create output if there is no valid watchable ("
                        + getWatchable().getClass() + ")"); // NOI18N
        }

        try {
            final EtaWatchable etaWatchable = (EtaWatchable)this.getWatchable();
            final EtaOutput etaOutput = etaWatchable.getEtaOutput();

            etaOutput.setEtaRun((Integer)this.cidsBean.getProperty("id"));
            etaOutput.setEtaRunName((String)this.cidsBean.getProperty("name"));

            final CidsBean etaModelOutput = SMSUtils.createModelOutput("Modellergebnisse " + etaOutput.getEtaRunName(), // NOI18N
                    etaOutput,
                    SMSUtils.Model.LINZ_ETA);

            final String domain = SessionManager.getSession().getUser().getDomain();
            final MetaClass etaResultClass = ClassCacheMultiple.getMetaClass(domain, TABLENAME_LINZ_ETA_RESULT);

            final CidsBean etaResultBean;
            etaResultBean = etaResultClass.getEmptyInstance().getBean();
            etaResultBean.setProperty("name", etaOutput.getEtaRunName());
            etaResultBean.setProperty("eta_scenario_id", etaOutput.getEtaRun());
            etaResultBean.setProperty("swmm_scenario_id", etaOutput.getSwmmRun());
            etaResultBean.setProperty("eta_sed_required", etaOutput.getEtaSedRequired());
            etaResultBean.setProperty("eta_sed_actual", etaOutput.getEtaSedActual());
            etaResultBean.setProperty("eta_hyd_required", etaOutput.getEtaHydRequired());
            etaResultBean.setProperty("eta_hyd_actual", etaOutput.getEtaHydActual());
            etaResultBean.setProperty("r720", etaOutput.getR720());
            etaResultBean.setProperty("total_overflow_volume", etaOutput.getTotalOverflowVolume());
            etaResultBean.persist();

            return etaModelOutput.persist();
        } catch (final Exception e) {
            final String message = "cannot get results for ETA Run '" + this.cidsBean + "': " + e.getMessage();
            LOG.error(message, e);
            this.fireBroken(message);
            throw new IOException(message, e);
        }
    }

    @Override
    protected String getReloadId() {
        return "local.linz.*";
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        if (cidsBean == null) {
            throw new IllegalStateException("cidsBean not set"); // NOI18N
        }

        final EtaRunInfo runInfo = this.getRunInfo();
        if (runInfo == null) {
            throw new IllegalStateException("run info not set"); // NOI18N
        }

        if (runInfo.isCanceled() || runInfo.isBroken()) {
            final String message = "run '" + cidsBean + "' is canceled  or broken, ignoring run";
            LOG.warn(message);
            throw new IllegalStateException(message); // NOI18N
        }

        return new EtaWatchable(this);
    }

    @Override
    protected boolean needsDownload() {
        return false;
    }

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing ETA Calculation"); // NOI18N
        }

        // now set to indeterminate
        fireProgressed(
            -1,
            -1,
            NbBundle.getMessage(
                EtaModelManager.class,
                "EtaModelManager.prepareExecution().progress.running"));

        final EtaInput etaInput = (EtaInput)this.getUR();

        if (etaInput.getSwmmRun() == -1) {
            final String message = "ETA run '" + this.cidsBean + "' without SWMM run";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        final EtaRunInfo etaRunInfo = new EtaRunInfo();
        etaRunInfo.setSwmmRunId(etaInput.getSwmmRun());

        LOG.info("preparing the execution of '" + this.cidsBean + "' for SWMM Run #" + etaRunInfo.getSwmmRunId());
        if ((etaInput.getEtaConfigurations() == null) || etaInput.getEtaConfigurations().isEmpty()) {
            final String message = "ETA Run '" + this.cidsBean + "' without proper eta configurations!";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, etaRunInfo);
            cidsBean.setProperty("runinfo", writer.toString());                           // NOI18N
            cidsBean = cidsBean.persist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("ETA RunInfo for ETA Run '" + cidsBean + "' saved");
            }
        } catch (final Exception ex) {
            final String message = "Cannot store runinfo of  ETA Run '" + cidsBean + "'"; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }
    }

    @Override
    public EtaRunInfo getRunInfo() {
        return SMSUtils.<EtaRunInfo>getRunInfo(cidsBean, EtaRunInfo.class);
    }
}
