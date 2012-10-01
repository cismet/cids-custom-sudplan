/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.openide.util.NbBundle;

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
            return NbBundle.getMessage(
                    DownscalingScenario.class,
                    "DownscalingScenario.getHtmlDescription(String).ccsm3a1b.description");
        } else if (AQ_ECHAM_A1B3.equals(scenario) || RF_ECHAM5_A1B3.equals(scenario)
                    || HD_ECHAM5_A1B3.equals(scenario)) {
            return NbBundle.getMessage(
                    DownscalingScenario.class,
                    "DownscalingScenario.getHtmlDescription(String).echam5a1b3.description");
        } else if (RF_ECHAM5_A21.equals(scenario)) {
            return NbBundle.getMessage(
                    DownscalingScenario.class,
                    "DownscalingScenario.getHtmlDescription(String).echam5a21.description");
        } else if (AQ_HADLEY_A1B.equals(scenario) || RF_HADLEY_A1B.equals(scenario) || HD_HADLEY_A1B.equals(scenario)) {
            return NbBundle.getMessage(
                    DownscalingScenario.class,
                    "DownscalingScenario.getHtmlDescription(String).hadleya1b.description");
        } else if (AQ_CNRM_A1B.equals(scenario)) {
            return NbBundle.getMessage(
                    DownscalingScenario.class,
                    "DownscalingScenario.getHtmlDescription(String).cnrma1b.description");
        } else {
            return NbBundle.getMessage(
                    DownscalingScenario.class,
                    "DownscalingScenario.getHtmlDescription(String).unknown.description",
                    scenario);
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
