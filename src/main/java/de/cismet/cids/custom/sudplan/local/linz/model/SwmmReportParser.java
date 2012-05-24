/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.model;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.io.InputStream;

import java.util.Date;

import de.cismet.cids.custom.sudplan.ProgressListener;
import de.cismet.cids.custom.sudplan.ProgressSupport;
import de.cismet.cids.custom.sudplan.local.linz.CsoOverflow;
import de.cismet.cids.custom.sudplan.local.linz.SwmmOutput;

/**
 * Parser for SWMM Report (RPT) Files.
 *
 * @author   Pascal Dihé
 * @author   Jimmy Lauter
 * @version  $Revision$, $Date$
 */
public class SwmmReportParser {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaComputationModel.class);
    public static final int MAX_PROGRESS_STEPS = 5;

    //~ Instance fields --------------------------------------------------------

    private final transient ProgressSupport progressSupport;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmReportParser object.
     */
    public SwmmReportParser() {
        progressSupport = new ProgressSupport();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Parses the result of a SWMM Clalucation (RPT File), extracts all parameters required for the ETA Calculation
     * (efficency values) and returns the SWMM output bean with all properties set.
     *
     * @param   swmmOutput      pre-initialized swmm output bean
     * @param   swmmReportFile  report file to be parsed
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public SwmmOutput parseRPT(final SwmmOutput swmmOutput, final InputStream swmmReportFile) throws Exception {
        // TODO: parse RPT,. set Properties

        // Beipsiel: Overflow Werte in den Cso Overflow beans setzen:
        final String[] csoNames = new String[] {
                "RDSRUE51",
                "ULKS1",
                "FUEAusl",
                "RKL_Ablauf",
                "AB_Plesching",
                "HSU12_1S5b",
                "HSU1_1RUE2",
                "ALBSP1nolink",
                "ALKSP1nolink",
                "ANFSP1nolink",
                "EDBSP1nolink",
                "ENNSP1nolink",
                "ENNSP2nolink",
                "RUEB_Traunnolink",
                "EWDSP1nolink",
                "FKDSP1nolink",
                "GLWSP1nolink",
                "GRSSP2nolink",
                "HEMSP1nolink",
                "HHSSP1nolink",
                "HOESP1nolink",
                "HOESP2nolink",
                "HZDSP1nolink",
                "KRTSP1nolink",
                "KSSSP1nolink",
                "LTBSP1nolink",
                "LTBSP2nolink",
                "LTBSP3nolink",
                "RUEB_Lunznolink",
                "NNKSP1nolink",
                "OFTSP1nolink",
                "OTHSP1nolink",
                "RUEB_Pleshnolink",
                "PNASP1nolink",
                "PUKSP1nolink",
                "RDS20_1S48nolink",
                "SMMSP1nolink",
                "STFSP1nolink",
                "STMSP1nolink",
                "STYSP1nolink",
                "RHHB_Wsee3nolink",
                "HSMSEntlnolink",
                "WLDSP1nolink",
                "WLDSP2nolink",
                "WLGSP1nolink"
            };

        int i = 0;
        for (final String csoName : csoNames) {
            final CsoOverflow csoOverflow = new CsoOverflow();
            csoOverflow.setName(csoName);
            csoOverflow.setSwmmProject(swmmOutput.getSwmmProject());
            csoOverflow.setOverflowDuration((float)Math.random() * 100f);
            csoOverflow.setOverflowFrequency((float)Math.random() * 10f);
            csoOverflow.setOverflowVolume((float)Math.random() * 1000f);
            swmmOutput.getCsoOverflows().put(csoName, csoOverflow);
            i++;
        }

        return swmmOutput;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    public void addProgressListener(final ProgressListener progressL) {
        progressSupport.addProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progressL  DOCUMENT ME!
     */
    public void removeProgressListener(final ProgressListener progressL) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeProgressListener: " + progressL);
        }
        progressSupport.removeProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final SwmmReportParser swmmReportParser = new SwmmReportParser();
        final InputStream swmmReportFile = null; // TODO
        SwmmOutput swmmOutput = new SwmmOutput();
        swmmOutput.setSwmmProject(2);
        swmmOutput.setUser("Pascal Dihé");
        swmmOutput.setCreated(new Date());
        try {
            swmmOutput = swmmReportParser.parseRPT(swmmOutput, swmmReportFile);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
