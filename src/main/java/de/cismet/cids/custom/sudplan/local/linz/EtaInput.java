/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Input for the ETA Calculation is the output of the SWMM Model Run!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class EtaInput extends SwmmOutput {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SWMMRUN = "swmmRun";

    //~ Instance fields --------------------------------------------------------

    protected transient List<EtaConfiguration> etaConfigurations = new ArrayList<EtaConfiguration>();

    protected String etaFile;

    private int swmmRun;
    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<EtaConfiguration> getEtaConfigurations() {
        return this.etaConfigurations;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  etaConfigurations  DOCUMENT ME!
     */
    public void setEtaConfigurations(final List<EtaConfiguration> etaConfigurations) {
        this.etaConfigurations = etaConfigurations;
    }

    /**
     * Get the value of etaFile.
     *
     * @return  the value of etaFile
     */
    public String getEtaFile() {
        return etaFile;
    }

    /**
     * Set the value of etaFile.
     *
     * @param  etaFile  new value of etaFile
     */
    public void setEtaFile(final String etaFile) {
        this.etaFile = etaFile;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
// public static void main(final String[] args) {
// try {
// final ObjectMapper mapper = new ObjectMapper();
// final StringWriter writer = new StringWriter();
//
// final EtaInput etaInput = new EtaInput();
// etaInput.setSwmmProject(2);
// etaInput.setUser("Pascal Dihé");
// etaInput.setCreated(new Date());
// etaInput.setSwmmRun(382);
// etaInput.setEtaFile("linz_eta");
//
// final String[] csoNames = new String[] {
// "RDSRUE51",
// "ULKS1",
// "FUEAusl",
// "RKL_Ablauf",
// "AB_Plesching",
// "HSU12_1S5b",
// "HSU1_1RUE2",
// "ALBSP1nolink",
// "ALKSP1nolink",
// "ANFSP1nolink",
// "EDBSP1nolink",
// "ENNSP1nolink",
// "ENNSP2nolink",
// "RUEB_Traunnolink",
// "EWDSP1nolink",
// "FKDSP1nolink",
// "GLWSP1nolink",
// "GRSSP2nolink",
// "HEMSP1nolink",
// "HHSSP1nolink",
// "HOESP1nolink",
// "HOESP2nolink",
// "HZDSP1nolink",
// "KRTSP1nolink",
// "KSSSP1nolink",
// "LTBSP1nolink",
// "LTBSP2nolink",
// "LTBSP3nolink",
// "RUEB_Lunznolink",
// "NNKSP1nolink",
// "OFTSP1nolink",
// "OTHSP1nolink",
// "RUEB_Pleshnolink",
// "PNASP1nolink",
// "PUKSP1nolink",
// "RDS20_1S48nolink",
// "SMMSP1nolink",
// "STFSP1nolink",
// "STMSP1nolink",
// "STYSP1nolink",
// "RHHB_Wsee3nolink",
// "HSMSEntlnolink",
// "WLDSP1nolink",
// "WLDSP2nolink",
// "WLGSP1nolink"
// };
// final int[] csoIds = new int[] {
// 2,
// 3,
// 4,
// 5,
// 6,
// 7,
// 8,
// 9,
// 10,
// 11,
// 12,
// 13,
// 14,
// 15,
// 16,
// 17,
// 18,
// 19,
// 20,
// 21,
// 22,
// 23,
// 24,
// 25,
// 26,
// 27,
// 28,
// 29,
// 30,
// 31,
// 32,
// 33,
// 34,
// 35,
// 36,
// 37,
// 38,
// 39,
// 40,
// 41,
// 42,
// 43,
// 44,
// 45,
// 46
// };
//
// int i = 0;
// for (final String csoName : csoNames) {
// final CsoOverflow csoOverflow = new CsoOverflow();
// csoOverflow.setName(csoName);
// csoOverflow.setSwmmProject(2);
// csoOverflow.setCso(csoIds[i]);
// csoOverflow.setOverflowDuration((float)Math.random() * 100f);
// csoOverflow.setOverflowFrequency((float)Math.random() * 10f);
// csoOverflow.setOverflowVolume((float)Math.random() * 1000f);
// etaInput.getCsoOverflows().put(csoName, csoOverflow);
//
// final EtaConfiguration etaConfiguration = new EtaConfiguration();
// etaConfiguration.setEnabled(Math.random() > 0.5d);
// etaConfiguration.setName(csoName);
// etaConfiguration.setSedimentationEfficency((float)Math.random() * 10f);
// etaInput.getEtaConfigurations().add(etaConfiguration);
// i++;
// }
//
// mapper.writeValue(writer, etaInput);
// System.out.println(writer.toString());
// } catch (Exception ex) {
// Exceptions.printStackTrace(ex);
// }
// }
}
