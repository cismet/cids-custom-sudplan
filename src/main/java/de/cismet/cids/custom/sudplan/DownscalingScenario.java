/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DownscalingScenario {

    //~ Static fields/initializers ---------------------------------------------

    public static final String AQ_CNRM_A1B = "cnrma1bcleo4.52";    // NOI18N
    public static final String AQ_ECHAM_A1B3 = "echam5a1b3rcp4.5"; // NOI18N
    public static final String AQ_HADLEY_A1B = "hadleya1brcp4.5";  // NOI18N
    public static final String RF_CCSM3_A1B = "ccsm3a1b";          // NOI18N
    public static final String RF_ECHAM5_A1B3 = "echam5a1b3";      // NOI18N
    public static final String RF_ECHAM5_A21 = "echam5a21";        // NOI18N
    public static final String RF_HADLEY_A1B = "hadleya1b";        // NOI18N
    public static final String HD_ECHAM5_A1B3 = "ECHAM5_A1B_3";    // NOI18N
    public static final String HD_HADLEY_A1B = "HadCM3Q0_A1B";     // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DownscalingScenario object.
     */
    private DownscalingScenario() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   scenario  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getHtmlDescription(final String scenario) {
        // FIXME: hardcoded description
        if (RF_CCSM3_A1B.equals(scenario)) {
            return "<html>"                                                                                                             // NOI18N
                        + "<b><a>CSSM3 A1B</a>:</b><br/>"                                                                               // NOI18N
                        + "<i>IPCC emission scenario:</i> A1B (intermediate level of future CO2 emissions, peak at 2050)<br/>"          // NOI18N
                        + "<i>Global model:</i> CSSM3 (University Corporation for Atmospheric Research, USA)<br/>"                      // NOI18N
                        + "<i>Regional model:</i> RCA3 (SMHI, Sweden)"                                                                  // NOI18N
                        + "</html>";                                                                                                    // NOI18N
        } else if (AQ_ECHAM_A1B3.equals(scenario) || RF_ECHAM5_A1B3.equals(scenario)
                    || HD_ECHAM5_A1B3.equals(scenario)) {
            return "<html>"                                                                                                             // NOI18N
                        + "<b><a>ECHAM5 A1B 3</a>:</b><br/>"                                                                            // NOI18N
                        + "<i>IPCC emission scenario:</i> A1B (intermediate level of future CO2 emissions, peak at 2050)<br/>"          // NOI18N
                        + "<i>Global model:</i> ECHAM5 (Max-Planck Institute, Germany)<br/>"                                            // NOI18N
                        + "<i>Regional model:</i> RCA3 (SMHI, Sweden)"                                                                  // NOI18N
                        + "</html>";                                                                                                    // NOI18N
        } else if (RF_ECHAM5_A21.equals(scenario)) {
            return "<html>"                                                                                                             // NOI18N
                        + "<b><a>ECHAM5 A2 1</a>:</b><br/>"                                                                             // NOI18N
                        + "<i>IPCC emission scenario:</i> A2 (high level of future CO2 emissions, continuous increase until 2100)<br/>" // NOI18N
                        + "<i>Global model:</i> ECHAM5 (Max-Planck Institute, Germany)<br/>"                                            // NOI18N
                        + "<i>Regional model:</i> RCA3 (SMHI, Sweden)"                                                                  // NOI18N
                        + "</html>";                                                                                                    // NOI18N
        } else if (AQ_HADLEY_A1B.equals(scenario) || RF_HADLEY_A1B.equals(scenario) || HD_HADLEY_A1B.equals(scenario)) {
            return "<html>"                                                                                                             // NOI18N
                        + "<b><a>HADCM3 A1B</a>:</b><br/>"                                                                              // NOI18N
                        + "<i>IPCC emission scenario:</i> A1B (intermediate level of future CO2 emissions, peak at 2050)<br/>"          // NOI18N
                        + "<i>Global model:</i> HADCM3 (Hadley Centre, UK)<br/>"                                                        // NOI18N
                        + "<i>Regional model:</i> RCA3 (SMHI, Sweden)"                                                                  // NOI18N
                        + "</html>";                                                                                                    // NOI18N
        } else if (AQ_CNRM_A1B.equals(scenario)) {
            return "<html>"                                                                                                             // NOI18N
                        + "<b><a>CNRM A1B</a>:</b><br/>"                                                                                // NOI18N
                        + "<i>IPCC emission scenario:</i> A1B (intermediate level of future CO2 emissions, peak at 2050)<br/>"          // NOI18N
                        + "<i>Global model:</i> CNRM-CM3 (Center National Weather Research, Meteo-France<br/>"                          // NOI18N
                        + "<i>Regional model:</i> RCA3 (SMHI, Sweden)"                                                                  // NOI18N
                        + "</html>";                                                                                                    // NOI18N
        } else {
            return "<html>unknown scenario: " + scenario + "</html>";                                                                   // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   scenario  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getDetailLink(final String scenario) {
        if (RF_CCSM3_A1B.equals(scenario)) {
            return
                "http://sudplan.eu/About-SUDPLAN/Pan-European-input-data/Climate-scenarios/climate-scenario-ccsm3-a1b-1.24357";     // NOI18N
        } else if (AQ_ECHAM_A1B3.equals(scenario) || RF_ECHAM5_A1B3.equals(scenario)
                    || HD_ECHAM5_A1B3.equals(scenario)) {
            return
                "http://sudplan.eu/About-SUDPLAN/Pan-European-input-data/Climate-scenarios/climate-scenarios-echam5-a1b-3-1.24332"; // NOI18N
        } else if (RF_ECHAM5_A21.equals(scenario)) {
            return
                "http://sudplan.eu/About-SUDPLAN/Pan-European-input-data/Climate-scenarios/climate-scenario-echam5-a2-1-1.24349";   // NOI18N
        } else if (AQ_HADLEY_A1B.equals(scenario) || RF_HADLEY_A1B.equals(scenario) || HD_HADLEY_A1B.equals(scenario)) {
            return
                "http://sudplan.eu/About-SUDPLAN/Pan-European-input-data/Climate-scenarios/climate-scenario-hadcm3-a1b-1.24351";    // NOI18N
        } else if (AQ_CNRM_A1B.equals(scenario)) {
            return
                "http://sudplan.eu/About-SUDPLAN/Pan-European-input-data/Climate-scenarios/climate-scenarios-cnrm-a1b-1.24939";     // NOI18N
        } else {
            return null;
        }
    }
}
