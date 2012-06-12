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
import java.io.InputStream;

import java.util.Date;
import java.util.List;

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

    private static final float WWTP_SIZE = 500000f;
    private static final float PE_SEP = 10000f;
    private static final float PE_COMBINED = 300000f;
    // Required CSO efficiency on table 1 and 2 at page 12 - Design basis of the WWTP (PE)
    private static final float WWTP_LOW_CASE = 5000.0f;
    private static final float WWTP_HEIGH_CASE = 50000.0f;
    // Required CSO efficiency on table 1 and 2 at page 12 - Rainfall intensity
    private static final float RAIN_INTENSITY_LOW_CASE = 30.0f;
    private static final float RAIN_INTENSITY_HEIGHT_CASE = 50.0f;
    // requirements for dissolved pollutants on table 1 at page 12
    private static final float R720_UNDER_30_LOW_CASE = 50.0f;
    private static final float R720_UNDER_30_HIGHT_CASE = 60.0f;
    private static final float R720_HIGHER_50_LOW_CASE = 40.0f;
    private static final float R720_HIGHER_50_HEIGHT_CASE = 50.0f;

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
        // definitions
        final float r720_1 = etaInput.getR720_1();
        final float wwtp_size = WWTP_SIZE;
        final float PEsep = PE_SEP;
        final float PEcombined = PE_COMBINED;

        // eta configuration conteins the sedomentation efficiency
        final List<EtaConfiguration> etaConfigurations = etaInput.getEtaConfigurations();
        LOG.info("computating ETA values for SWMM Result '" + etaInput.getSwmmRunName() + "'");

        final EtaOutput etaOutput = new EtaOutput();
        etaOutput.setCreated(new Date());
        etaOutput.setSwmmRun(etaInput.getSwmmRun());
        etaOutput.setUser(etaInput.getUser());
        etaOutput.setR720(etaInput.getR720_1());

        float sum_TotalVolume = 0.0f;
        float sum_SedAFS = 0.0f;

        for (final String rptKey : etaInput.getCsoOverflows().keySet()) {
            for (final EtaConfiguration eta : etaConfigurations) {
                if (rptKey.equalsIgnoreCase(eta.getName()) && eta.isEnabled()) {
                    final float totVol = etaInput.getCsoOverflows().get(rptKey).getTotalVolume();
                    sum_SedAFS += totVol * eta.getSedimentationEfficency();
                    sum_TotalVolume += totVol;
                }
            }
        }

        final float VQo = sum_TotalVolume;
        final float VQr = etaInput.getVQr();

        final float eta_Hyd_actual = ((VQr - VQo) / VQr) * 100.0f;
        final float eta_Sed_actual = eta_Hyd_actual + (sum_SedAFS / VQr);

        // Calculate required efficiency rates

        float cso_eff_r720_lower30mm = -1.0f;
        float cso_eff_r720_higher50mm = -1.0f;
        float eta_gel = -1.0f;
        float eta_afs = -1.0f;

        // Table 1 - Page 12
        if (wwtp_size <= WWTP_LOW_CASE) {
            cso_eff_r720_lower30mm = R720_UNDER_30_LOW_CASE;
            cso_eff_r720_higher50mm = R720_HIGHER_50_LOW_CASE;
        } else if (wwtp_size >= WWTP_HEIGH_CASE) {
            cso_eff_r720_lower30mm = R720_UNDER_30_HIGHT_CASE;
            cso_eff_r720_higher50mm = R720_HIGHER_50_HEIGHT_CASE;
        } else {
            cso_eff_r720_lower30mm = R720_UNDER_30_LOW_CASE
                        + ((R720_UNDER_30_HIGHT_CASE - R720_UNDER_30_LOW_CASE)
                            / (WWTP_HEIGH_CASE - WWTP_LOW_CASE)
                            * (wwtp_size - WWTP_LOW_CASE));
            cso_eff_r720_higher50mm = R720_HIGHER_50_LOW_CASE
                        + ((R720_HIGHER_50_HEIGHT_CASE - R720_HIGHER_50_LOW_CASE)
                            / (WWTP_HEIGH_CASE - WWTP_LOW_CASE)
                            * (wwtp_size - WWTP_LOW_CASE));
        }

        if (r720_1 <= RAIN_INTENSITY_LOW_CASE) {
            eta_gel = cso_eff_r720_lower30mm;
        } else if (r720_1 >= RAIN_INTENSITY_HEIGHT_CASE) {
            eta_gel = cso_eff_r720_higher50mm;
        } else {
            eta_gel = cso_eff_r720_higher50mm
                        + ((cso_eff_r720_lower30mm - cso_eff_r720_higher50mm)
                            / (RAIN_INTENSITY_HEIGHT_CASE - RAIN_INTENSITY_LOW_CASE)
                            * (RAIN_INTENSITY_HEIGHT_CASE - r720_1));
        }

        // Table 2 - Page 12
        eta_afs = eta_gel + 15;

        // Increase required efficiency for connected seperate systems
        final float seperateSYS = 5.0f * PEsep / PEcombined;
        if (seperateSYS > 1) {
            if ((eta_gel + seperateSYS) > 65.0f) {
                eta_gel = 65.0f;
            } else {
                eta_gel = eta_gel + seperateSYS;
            }
            if ((eta_afs + seperateSYS) > 80.0f) {
                eta_afs = 80.0f;
            } else {
                eta_afs = eta_afs + seperateSYS;
            }
        }

        etaOutput.setEtaHydActual(eta_Hyd_actual);
        etaOutput.setEtaSedActual(eta_Sed_actual);
        etaOutput.setEtaHydRequired(eta_gel);
        etaOutput.setEtaSedRequired(eta_afs);

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
                    // "RUEB_Lunznolink",
                    "NNKSP1nolink",
                    "OFTSP1nolink",
                    "OTHSP1nolink",
                    // "RUEB_Pleshnolink",
                    "PNASP1nolink",
                    "PUKSP1nolink",
                    // "RDS20_1S48nolink",
                    "SMMSP1nolink",
                    "STFSP1nolink",
                    "STMSP1nolink",
                    "STYSP1nolink",
                    // "RHHB_Wsee3nolink",
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
                etaConfiguration.setEnabled(true);
                if (csoName.equals("RKL_Ablauf")) {
                    etaConfiguration.setEnabled(false);
                }

                etaConfiguration.setName(csoName);

                if (csoName.equals("ULKS1")
                            || csoName.equals("AB_Plesching")
                            || csoName.equals("ALKSP1nolink")
                            || csoName.equals("ANFSP1nolink")
                            || csoName.equals("EDBSP1nolink")
                            || csoName.equals("ENNSP1nolink")
                            || csoName.equals("ENNSP2nolink")
                            || csoName.equals("RUEB_Traunnolink")
                            || csoName.equals("EWDSP1nolink")
                            || csoName.equals("FKDSP1nolink")
                            || csoName.equals("GLWSP1nolink")
                            || csoName.equals("GRSSP2nolink")
                            || csoName.equals("HEMSP1nolink")
                            || csoName.equals("HZDSP1nolink")
                            || csoName.equals("KRTSP1nolink")
                            || csoName.equals("NNKSP1nolink")
                            || csoName.equals("OTHSP1nolink")
                            || csoName.equals("PNASP1nolink")
                            || csoName.equals("SMMSP1nolink")
                            || csoName.equals("STYSP1nolink")
                            || csoName.equals("WLDSP1nolink")
                            || csoName.equals("WLDSP2nolink")
                            || csoName.equals("WLGSP1nolink")) {
                    etaConfiguration.setSedimentationEfficency(21.0f);
                } else {
                    etaConfiguration.setSedimentationEfficency(0.0f);
                }
                etaInput.getEtaConfigurations().add(etaConfiguration);
                i++;
            }

            etaInput.setR720_1((float)29.0232183);

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
