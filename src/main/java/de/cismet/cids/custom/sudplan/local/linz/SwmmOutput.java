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
import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.Exceptions;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.IOException;
import java.io.StringWriter;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class SwmmOutput {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmOutput.class);
    public static final String TABLENAME_LINZ_CSO = "LINZ_CSO"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient Map<String, CsoOverflow> csoOverflows = new HashMap<String, CsoOverflow>();
    private transient Date created;
    private transient String user;
    private transient int swmmRun;
    private transient String swmmRunName;
    /** Statistical rainfall intensity with a duration of 12 h and return period once per year (r720,1). */
    private transient float r720;
    /** Total volume of surface runoff (Wet Weather Inflow), VQr. */
    private transient float totalRunoffVolume;
    /** Total volume of overflow discharge. (VQo) */
    private transient float totalOverflowVolume;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmOutput object.
     */
    public SwmmOutput() {
    }

    //~ Methods ----------------------------------------------------------------

    // private transient csoParameters
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, CsoOverflow> getCsoOverflows() {
        return this.csoOverflows;
    }

    /**
     * Get the value of swmmRun.
     *
     * @return  the value of swmmRun
     */
    public int getSwmmRun() {
        return swmmRun;
    }

    /**
     * Set the value of swmmRun.
     *
     * @param  swmmRun  new value of swmmRun
     */
    public void setSwmmRun(final int swmmRun) {
        this.swmmRun = swmmRun;
    }

    /**
     * Get the value of swmmRunName.
     *
     * @return  the value of swmmRunName
     */
    public String getSwmmRunName() {
        return swmmRunName;
    }

    /**
     * Set the value of swmmRunName.
     *
     * @param  swmmRunName  new value of swmmRunName
     */
    public void setSwmmRunName(final String swmmRunName) {
        this.swmmRunName = swmmRunName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  csoOverflows  DOCUMENT ME!
     */
    public void setCsoOverflows(final Map<String, CsoOverflow> csoOverflows) {
        this.csoOverflows = csoOverflows;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> fetchCsos() {
        assert this.csoOverflows != null : "csoOverflows list is null";
        final List<CidsBean> csoOverflowBeans = new ArrayList<CidsBean>(this.csoOverflows.size());
        for (final CsoOverflow csoOverflow : this.csoOverflows.values()) {
            csoOverflowBeans.add(SMSUtils.fetchCidsBean(csoOverflow.getCso(), TABLENAME_LINZ_CSO));
        }

        return csoOverflowBeans;
    }

    /**
     * Get the value of created.
     *
     * @return  the value of created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Set the value of created.
     *
     * @param  created  new value of created
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * Get the value of user.
     *
     * @return  the value of user
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the value of user.
     *
     * @param  user  new value of user
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectId  DOCUMENT ME!
     */
    @JsonIgnore
    public void setSwmmProject(final int projectId) {
        if ((this.csoOverflows != null) && !this.csoOverflows.isEmpty()) {
            for (final CsoOverflow csoOverflow : this.csoOverflows.values()) {
                csoOverflow.setSwmmProject(projectId);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @JsonIgnore
    public int getSwmmProject() {
        if ((this.csoOverflows != null) && !this.csoOverflows.isEmpty()) {
            return this.csoOverflows.values().iterator().next().getSwmmProject();
        }

        return -1;
    }

    /**
     * Get the value of r720 Niederschlagshöhe in mm bei einer Regendauer von 12 Stunden (720 Minuten) mit einer
     * Wiederkehrzeit von 1 Jahr.
     *
     * @return  the value of r720
     */
    public float getR720() {
        return r720;
    }

    /**
     * Set the value of r720.
     *
     * @param  r720  new value of r720
     */
    public void setR720(final float r720) {
        this.r720 = r720;
    }

    /**
     * Get the value of totalRunoffVolume Summe der Regenabflussmengen eines Jahres (m³/a) (Total volume of surface
     * runoff).
     *
     * @return  the value of totalRunoffVolume
     */
    public float getTotalRunoffVolume() {
        return totalRunoffVolume;
    }

    /**
     * Set the value of totalRunoffVolume.
     *
     * @param  totalRunoffVolume  new value of totalRunoffVolume
     */
    public void setTotalRunoffVolume(final float totalRunoffVolume) {
        this.totalRunoffVolume = totalRunoffVolume;
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
     * Synchronizes the overflow results (per cso) with the local ids of the cso objects. The id is retrieved from the
     * eta cso configuration.
     *
     * @param  etaConfigurations  csoOverflows DOCUMENT ME!
     */
    @JsonIgnore
    public void synchronizeCsoIds(final List<EtaConfiguration> etaConfigurations) {
        if ((this.csoOverflows != null) && !this.csoOverflows.isEmpty()) {
            if (this.csoOverflows.size() == etaConfigurations.size()) {
                for (final EtaConfiguration etaConfiguration : etaConfigurations) {
                    final String name = etaConfiguration.getName();
                    if (this.csoOverflows.containsKey(name)) {
                        this.csoOverflows.get(name).setCso(etaConfiguration.getCso());
                    } else {
                        LOG.warn("cso '" + name + "' not found in local cso map!");
                    }
                }
            } else {
                LOG.warn("CSO map size missmatch: " + this.csoOverflows.size()
                            + " vs. " + etaConfigurations.size());
            }
        } else {
            LOG.warn("target cso map empty!");
        }
    }
//    /**
//     * DOCUMENT ME!
//     *
//     * @param  args  DOCUMENT ME!
//     */
//    public static void main(final String[] args) {
//        try {
//            final ObjectMapper mapper = new ObjectMapper();
//            final StringWriter writer = new StringWriter();
//
//            final SwmmOutput swmmOutput = new SwmmOutput();
//            swmmOutput.setSwmmProject(2);
//            swmmOutput.setUser("Pascal Dihé");
//            swmmOutput.setCreated(new Date());
//
//            final String[] csoNames = new String[] {
//                    "RDSRUE51",
//                    "ULKS1",
//                    "FUEAusl",
//                    "RKL_Ablauf",
//                    "AB_Plesching",
//                    "HSU12_1S5b",
//                    "HSU1_1RUE2",
//                    "ALBSP1nolink",
//                    "ALKSP1nolink",
//                    "ANFSP1nolink",
//                    "EDBSP1nolink",
//                    "ENNSP1nolink",
//                    "ENNSP2nolink",
//                    "RUEB_Traunnolink",
//                    "EWDSP1nolink",
//                    "FKDSP1nolink",
//                    "GLWSP1nolink",
//                    "GRSSP2nolink",
//                    "HEMSP1nolink",
//                    "HHSSP1nolink",
//                    "HOESP1nolink",
//                    "HOESP2nolink",
//                    "HZDSP1nolink",
//                    "KRTSP1nolink",
//                    "KSSSP1nolink",
//                    "LTBSP1nolink",
//                    "LTBSP2nolink",
//                    "LTBSP3nolink",
//                    "RUEB_Lunznolink",
//                    "NNKSP1nolink",
//                    "OFTSP1nolink",
//                    "OTHSP1nolink",
//                    "RUEB_Pleshnolink",
//                    "PNASP1nolink",
//                    "PUKSP1nolink",
//                    "RDS20_1S48nolink",
//                    "SMMSP1nolink",
//                    "STFSP1nolink",
//                    "STMSP1nolink",
//                    "STYSP1nolink",
//                    "RHHB_Wsee3nolink",
//                    "HSMSEntlnolink",
//                    "WLDSP1nolink",
//                    "WLDSP2nolink",
//                    "WLGSP1nolink"
//                };
//            final int[] csoIds = new int[] {
//                    2,
//                    3,
//                    4,
//                    5,
//                    6,
//                    7,
//                    8,
//                    9,
//                    10,
//                    11,
//                    12,
//                    13,
//                    14,
//                    15,
//                    16,
//                    17,
//                    18,
//                    19,
//                    20,
//                    21,
//                    22,
//                    23,
//                    24,
//                    25,
//                    26,
//                    27,
//                    28,
//                    29,
//                    30,
//                    31,
//                    32,
//                    33,
//                    34,
//                    35,
//                    36,
//                    37,
//                    38,
//                    39,
//                    40,
//                    41,
//                    42,
//                    43,
//                    44,
//                    45,
//                    46
//                };
//
//            int i = 0;
//            for (final String csoName : csoNames) {
//                final CsoOverflow csoOverflow = new CsoOverflow();
//                csoOverflow.setName(csoName);
//                csoOverflow.setSwmmProject(2);
//                csoOverflow.setCso(csoIds[i]);
//                csoOverflow.setOverflowDuration((float)Math.random() * 100f);
//                csoOverflow.setOverflowFrequency((float)Math.random() * 10f);
//                csoOverflow.setOverflowVolume((float)Math.random() * 1000f);
//                swmmOutput.getCsoOverflows().put(csoName, csoOverflow);
//                i++;
//            }
//
//            mapper.writeValue(writer, swmmOutput);
//            System.out.println(writer.toString());
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }
}
