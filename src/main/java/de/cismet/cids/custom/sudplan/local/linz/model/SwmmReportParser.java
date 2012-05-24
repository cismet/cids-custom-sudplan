/*
 * Copyright (C) 2012 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.sudplan.local.linz.model;

import de.cismet.cids.custom.sudplan.ProgressListener;
import de.cismet.cids.custom.sudplan.ProgressSupport;
import de.cismet.cids.custom.sudplan.local.linz.CsoOverflow;
import de.cismet.cids.custom.sudplan.local.linz.SwmmOutput;
import java.io.InputStream;
import java.util.Date;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 * Parser for SWMM Report (RPT) Files.
 *
 * @author Pascal Dihé
 * @author Jimmy Lauter
 */
public class SwmmReportParser
{

    private static final transient Logger LOG = Logger.getLogger(EtaComputationModel.class);
    public static final int MAX_PROGRESS_STEPS = 5;
    //~ Instance fields --------------------------------------------------------
    private final transient ProgressSupport progressSupport;

    public SwmmReportParser()
    {
        progressSupport = new ProgressSupport();
    }

    /**
     * Parses the result of a SWMM Clalucation (RPT File), extracts all
     * parameters required for the ETA Calculation (efficency values) and
     * returns the SWMM output bean with all properties set.
     *
     * @param swmmOutput pre-initialized swmm output bean
     * @param swmmReportFile report file to be parsed
     * @return
     */
    public SwmmOutput parseRPT(final SwmmOutput swmmOutput, final InputStream swmmReportFile) throws Exception
    {
        //TODO: parse RPT,. set Properties


        // Beipsiel: Overflow Werte in den Cso Overflow beans setzen:
        final String[] csoNames = new String[]
        {
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
        for (final String csoName : csoNames)
        {
            final CsoOverflow csoOverflow = new CsoOverflow();
            csoOverflow.setName(csoName);
            csoOverflow.setSwmmProject(swmmOutput.getSwmmProject());
            csoOverflow.setOverflowDuration((float) Math.random() * 100f);
            csoOverflow.setOverflowFrequency((float) Math.random() * 10f);
            csoOverflow.setOverflowVolume((float) Math.random() * 1000f);
            swmmOutput.getCsoOverflows().put(csoName, csoOverflow);
            i++;
        }

        return swmmOutput;
    }

    /**
     * DOCUMENT ME!
     *
     * @param progressL DOCUMENT ME!
     */
    public void addProgressListener(final ProgressListener progressL)
    {
        progressSupport.addProgressListener(progressL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param progressL DOCUMENT ME!
     */
    public void removeProgressListener(final ProgressListener progressL)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("removeProgressListener: " + progressL);
        }
        progressSupport.removeProgressListener(progressL);
    }

    public static void main(String args[])
    {
        SwmmReportParser swmmReportParser = new SwmmReportParser();
        InputStream swmmReportFile = null; // TODO
        SwmmOutput swmmOutput = new SwmmOutput();
        swmmOutput.setSwmmProject(2);
        swmmOutput.setUser("Pascal Dihé");
        swmmOutput.setCreated(new Date());
        try
        {
            swmmOutput = swmmReportParser.parseRPT(swmmOutput, swmmReportFile);
        } catch (Exception ex)
        {
            Exceptions.printStackTrace(ex);
        }
    }
}
