/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import de.cismet.cids.custom.sudplan.AbstractModelManager;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.Variable;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingModelManager extends AbstractModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingModelManager.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void internalExecute() throws IOException {
        // FIXME: atr hack
        fireProgressed(-1, -1);
        try {
            Thread.sleep(2000);
        } catch (final Exception e) {
            // skip
        }

        fireFinised();
    }

    @Override
    protected CidsBean createOutputBean() throws IOException {
        final AirqualityDownscalingOutput out = new AirqualityDownscalingOutput();
        out.setModelRunId((Integer)cidsBean.getProperty("id"));                                         // NOI18N
        out.setModelInputId((Integer)((CidsBean)cidsBean.getProperty("modelinput")).getProperty("id")); // NOI18N

        final TimeseriesRetrieverConfig config;
        try {
            config = new TimeseriesRetrieverConfig(
                    TimeseriesRetrieverConfig.PROTOCOL_TSTB,
                    "SOS-Dummy-Handler",                                 // NOI18N
                    new URL("http://dummy.org"),                         // NOI18N
                    "urn:ogc:object:AIRVIRO:O3",                         // NOI18N
                    "urn:MyOrg:feature:grid3",                           // NOI18N
                    Variable.O3.getPropertyKey(),
                    "AIRVIRO-O3-coverage",                               // NOI18N
                    null,
                    TimeInterval.ALL_INTERVAL);
        } catch (MalformedURLException ex) {
            final String message = "cannot create retriever config";     // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        out.setTstburl(config.toTSTBUrl());

        final CidsBean outputBean = SMSUtils.createModelOutput(
                "Downscaling results of ("
                        + cidsBean.getProperty("name")
                        + ")",
                out,
                SMSUtils.Model.AQ_DS);

        try {
            return outputBean.persist();
        } catch (final Exception e) {
            final String message = "cannot create output bean"; // NOI18N
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    @Override
    protected String getReloadId() {
        return null;
    }
}
