/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz.model;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Date;
import java.util.List;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.local.linz.EtaConfiguration;
import de.cismet.cids.custom.sudplan.local.linz.EtaInput;
import de.cismet.cids.custom.sudplan.local.linz.EtaOutput;
import de.cismet.cids.custom.sudplan.local.linz.SwmmOutput;

/**
 * Local Model for ETA Calculation.
 *
 * @author   Pascal Dihé
 * @author   Jimmy Lauter
 * @version  $Revision$, $Date$
 */
public class EtaComputationModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaComputationModel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EtaComputationModel object.
     */
    public EtaComputationModel() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   etaInput  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public EtaOutput computateEta(final EtaInput etaInput) throws Exception {
        // eta configuration conteins the sedomentation efficyncy
        final List<EtaConfiguration> etaConfigurations = etaInput.getEtaConfigurations();
        LOG.info("computating ETA values for SWMM Result '" + etaInput.getSwmmRunName() + "'");

        final EtaOutput etaOutput = new EtaOutput();
        etaOutput.setCreated(new Date());
        etaOutput.setSwmmRun(etaInput.getSwmmRun());
        etaOutput.setUser(etaInput.getUser());
        etaOutput.setR720(etaInput.getR720_1());

        // TODO eta computation

        return etaOutput;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final SwmmReportParser swmmReportParser = new SwmmReportParser();
            final InputStream swmmReportFile = new FileInputStream("c:\\linz_v1_2011-08-29_neu.rpt");

            SwmmOutput swmmOutput = new SwmmOutput();
            swmmOutput.setSwmmProject(2);
            swmmOutput.setUser("Pascal Dihé");
            swmmOutput.setCreated(new Date());

            swmmOutput = swmmReportParser.parseRPT(swmmOutput, swmmReportFile);

            final EtaInput etaInput = new EtaInput(swmmOutput);

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
            final int[] csoIds = new int[] {
                    2,
                    3,
                    4,
                    5,
                    6,
                    7,
                    8,
                    9,
                    10,
                    11,
                    12,
                    13,
                    14,
                    15,
                    16,
                    17,
                    18,
                    19,
                    20,
                    21,
                    22,
                    23,
                    24,
                    25,
                    26,
                    27,
                    28,
                    29,
                    30,
                    31,
                    32,
                    33,
                    34,
                    35,
                    36,
                    37,
                    38,
                    39,
                    40,
                    41,
                    42,
                    43,
                    44,
                    45,
                    46
                };

            int i = 0;
            for (final String csoName : csoNames) {
                final EtaConfiguration etaConfiguration = new EtaConfiguration();
                etaConfiguration.setEnabled(Math.random() > 0.5d);
                etaConfiguration.setName(csoName);
                etaConfiguration.setSedimentationEfficency((float)Math.random() * 10f);
                etaInput.getEtaConfigurations().add(etaConfiguration);
                i++;
            }

            final EtaComputationModel etaComputationModel = new EtaComputationModel();
            final EtaOutput etaOutput = etaComputationModel.computateEta(etaInput);

            System.out.println("EtaHydActual: " + etaOutput.getEtaHydActual());
            System.out.println("tEtaHydRequired: " + etaOutput.getEtaHydRequired());
            System.out.println("EtaSedActual: " + etaOutput.getEtaSedActual());
            System.out.println("EtaSedRequired: " + etaOutput.getEtaSedRequired());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
