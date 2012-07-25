/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import org.apache.log4j.Logger;

import org.codehaus.jackson.annotate.JsonIgnore;

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

    protected static final transient Logger LOG = Logger.getLogger(EtaInput.class);
    public static final String PROP_SWMMRUN = "swmmRun";

    //~ Instance fields --------------------------------------------------------

    protected transient List<EtaConfiguration> etaConfigurations;
    protected String etaFile;
    /** Total volume of overflow discharge. (VQo) */
    private transient float totalOverflowVolume = -1.0f;
    private final transient PropertyChangeSupport propertyChangeSupport;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EtaInput object.
     */
    public EtaInput() {
        etaConfigurations = new ArrayList<EtaConfiguration>();
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Creates a new EtaInput object.
     *
     * @param  swmmOutput  DOCUMENT ME!
     */
    public EtaInput(final SwmmOutput swmmOutput) {
        this();
        this.fromSwmmOutput(swmmOutput);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  swmmOutput  DOCUMENT ME!
     */
    @JsonIgnore
    public final void fromSwmmOutput(final SwmmOutput swmmOutput) {
        this.setR720(swmmOutput.getR720());
        this.setSwmmProject(swmmOutput.getSwmmProject());
        this.setSwmmRun(swmmOutput.getSwmmRun());
        this.setSwmmRunName(swmmOutput.getSwmmRunName());
        this.setTotalRunoffVolume(swmmOutput.getTotalRunoffVolume());
        this.setCsoOverflows(swmmOutput.getCsoOverflows());

        if (this.etaConfigurations.isEmpty()) {
            LOG.warn("no ETA Configurations found, creating default ETA Configurations from CSO Overflows");
            for (final CsoOverflow csoOverflow : this.csoOverflows.values()) {
                final EtaConfiguration etaConfiguration = new EtaConfiguration(csoOverflow.getName(),
                        csoOverflow.getCso());

                if (etaConfiguration.getName().equalsIgnoreCase("ULKS1")) {
                    etaConfiguration.setSedimentationEfficency(20);
                } else if (etaConfiguration.getName().equalsIgnoreCase("RKL_Ablauf")) {
                    etaConfiguration.setEnabled(false);
                } else if (etaConfiguration.getName().equalsIgnoreCase("AB_Plesching")) {
                    etaConfiguration.setSedimentationEfficency(20);
                } else if (etaConfiguration.getName().equalsIgnoreCase("RHHB_Weikerlsee3nolink")) {
                    etaConfiguration.setSedimentationEfficency(20);
                }

                this.etaConfigurations.add(etaConfiguration);
            }
        }

        this.computeTotalOverflowVolume();
    }

    /**
     * DOCUMENT ME!
     */
    public final void computeTotalOverflowVolume() {
        if ((this.csoOverflows != null) && !this.getCsoOverflows().isEmpty()
                    && (etaConfigurations != null) && !etaConfigurations.isEmpty()) {
        } else {
            LOG.warn("cannot compute TotalOverflowVolume, csoOverflows or etaConfigurations are empty");
            this.totalOverflowVolume = -1;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("computing total overflow volume for " + etaConfigurations.size() + " CSOs");
        }
        if (this.csoOverflows.size() != etaConfigurations.size()) {
            LOG.warn("CSO map size missmatch: " + this.getCsoOverflows().size()
                        + " vs. " + etaConfigurations.size());
        }

        this.totalOverflowVolume = 0;
        int i = 0;
        for (final EtaConfiguration etaConfiguration : etaConfigurations) {
            if (etaConfiguration.isEnabled()) {
                final String name = etaConfiguration.getName();
                if (this.csoOverflows.containsKey(name)) {
                    final CsoOverflow csoOverflow = this.csoOverflows.get(name);
                    totalOverflowVolume += csoOverflow.getOverflowVolume();
                    i++;
                } else {
                    LOG.warn("cannot consider Overflow Volume of cso '"
                                + etaConfiguration.getName() + "': not in result list of overflows");
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ignoring CSO '" + etaConfiguration + "' in computation of total overflow volume");
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(i + " out of " + getEtaConfigurations().size()
                        + " CSOs considered in total overflow volume calculation");
        }
    }
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
     * Get the value of totalOverflowVolume Summe der entlasteten Mischwassermengen eines Jahres (m³/a) (Total volume of
     * overflow discharge).
     *
     * @return  the value of totalOverflowVolume
     */
    public float getTotalOverflowVolume() {
        return totalOverflowVolume;
    }

    /**
     * Set the value of totalOverflowVolume.
     *
     * @param  totalOverflowVolume  new value of totalOverflowVolume
     */
    public void setTotalOverflowVolume(final float totalOverflowVolume) {
        this.totalOverflowVolume = totalOverflowVolume;
    }

    /**
     * Get the value of etaFile.
     *
     * @return      the value of etaFile
     *
     * @deprecated  DOCUMENT ME!
     */
    public String getEtaFile() {
        return etaFile;
    }

    /**
     * Set the value of etaFile.
     *
     * @param       etaFile  new value of etaFile
     *
     * @deprecated  DOCUMENT ME!
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
     * @param args DOCUMENT ME!
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
