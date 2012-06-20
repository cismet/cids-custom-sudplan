/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import com.amazonaws.services.simpleworkflow.flow.core.Cancelable;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.EventQueue;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.StatusPanel;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class Euler2ComputationWizardPanelCompute extends AbstractWizardPanel implements Cancellable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(Euler2ComputationWizardPanelCompute.class);

    //~ Instance fields --------------------------------------------------------

    private transient boolean stored;

    private final transient Object lock;

    private CidsBean rainevent;

    private transient Future exportTask;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Euler2ComputationWizardPanelCompute object.
     */
    public Euler2ComputationWizardPanelCompute() {
        this.lock = new Object();
        this.stored = false;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Component createComponent() {
        return new StatusPanel(NbBundle.getMessage(
                    Euler2ComputationWizardPanelCompute.class,
                    "Euler2ComputationWizardPanelCompute.createComponent().statusPanel.name"));
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        synchronized (lock) {
            final String name = String.valueOf(wizard.getProperty(
                        Euler2ComputationWizardPanelAttributes.PROP_EULER2_NAME));
            final String desc = String.valueOf(wizard.getProperty(
                        Euler2ComputationWizardPanelAttributes.PROP_EULER2_DESC));
            final SortedMap<Integer, Double> raindata = (SortedMap<Integer, Double>)wizard.getProperty(
                    Euler2ComputationWizardAction.PROP_SELECTED_RAINDATA);
            final boolean forecast = (Boolean)wizard.getProperty(Euler2ComputationWizardAction.PROP_FORECAST);

            // final SortedMap<Integer, Double> raindata = new TreeMap<Integer, Double>();

            /*
             * raindata.put(5, 10.6453584374349); raindata.put(10, 14.6873133853342); raindata.put(15,
             * 17.0517054594597); raindata.put(20, 18.7292683332336); raindata.put(30, 21.093660407359);
             * raindata.put(45, 23.4580524814845);
             **/
            final int interval = 5;

            assert name != null : "name must not be null";
            assert desc != null : "desc must not be null";
            assert raindata != null : "raindata must not be null";

            // final Rainevent rainevent = new Rainevent();
            // rainevent.setInterval(interval);

            exportTask = SudplanConcurrency.getSudplanGeneralPurposePool().submit(new Runnable() {

                        @Override
                        public void run() {
                            setStatusEDT(
                                true,
                                NbBundle.getMessage(
                                    Euler2ComputationWizardPanelCompute.class,
                                    "Euler2ComputationWizardPanelCompute.read(WizardDescriptor).exportTask.status.beginCompute"));
                            final SortedMap<Integer, Double> result = euler2Computation(raindata, interval);
                            setStatusEDT(
                                true,
                                org.openide.util.NbBundle.getMessage(
                                    Euler2ComputationWizardPanelCompute.class,
                                    "Euler2ComputationWizardPanelCompute.read(WizardDescriptor).exportTask.status.EndCompute"));
                            try {
                                // rainevent.setPrecipitations(new ArrayList<Double>(result.values()));
                                final String domain = SessionManager.getSession().getUser().getDomain();
                                final String table = SMSUtils.TABLENAME_RAINEVENT;

                                rainevent = CidsBean.createNewCidsBeanFromTableName(domain, table);

                                rainevent.setProperty("name", name);
                                rainevent.setProperty("description", desc);
                                rainevent.setProperty("interval", interval);
                                rainevent.setProperty("forecast", forecast);

                                final StringBuilder data = new StringBuilder();
                                final Iterator iterator = result.keySet().iterator();
                                while (iterator.hasNext()) {
                                    final int key = (Integer)iterator.next();
                                    final double value = (Double)result.get(key);
                                    data.append(value);
                                    if (key != result.lastKey()) {
                                        data.append(":");
                                    }
                                }
                                // final String values = result.values().toString();

                                rainevent.setProperty("data", data.toString());

                                // rainevent = rainevent.persist();

                                setStatusEDT(
                                    false,
                                    org.openide.util.NbBundle.getMessage(
                                        Euler2ComputationWizardPanelCompute.class,
                                        "Euler2ComputationWizardPanelCompute.read(WizardDescriptor).exportTask.status.computeSuccess"));

                                synchronized (lock) {
                                    Euler2ComputationWizardPanelCompute.this.exportTask = null;
                                }
                            } catch (Exception ex) {
                                LOG.error("can not create rainevent", ex);
                                setStatusEDT(
                                    false,
                                    org.openide.util.NbBundle.getMessage(
                                        Euler2ComputationWizardPanelCompute.class,
                                        "Euler2ComputationWizardPanelCompute.read(WizardDescriptor).exportTask.status.errorCreatingRainevent"));
                            } finally {
                                changeSupport.fireChange();
                            }
                        }
                    });
        }
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        if (!stored) {
            try {
                rainevent = rainevent.persist();
                final ComponentRegistry reg = ComponentRegistry.getRegistry();
                reg.getDescriptionPane().gotoMetaObject(rainevent.getMetaObject(), null);
            } catch (Exception ex) {
                LOG.error("can not create rainevent", ex);
                setStatusEDT(
                    false,
                    org.openide.util.NbBundle.getMessage(
                        Euler2ComputationWizardPanelCompute.class,
                        "Euler2ComputationWizardPanelCompute.read(WizardDescriptor).exportTask.status.errorCreatingRainevent"));
            }
            stored = true;
        }
    }

    @Override
    public boolean cancel() {
        synchronized (lock) {
            if (exportTask != null) {
                if (!exportTask.cancel(true)) {
                    if (exportTask.isDone()) {
                        // do nothing, its too late
                    } else {
                        LOG.warn("export task could not be cancelled"); // NOI18N

                        return false;
                    }
                }
            }

            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   raindata  DOCUMENT ME!
     * @param   interval  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SortedMap<Integer, Double> euler2Computation(final SortedMap<Integer, Double> raindata,
            final int interval) {
        final int max_duration = raindata.lastKey();
        final int steps = max_duration / interval;
        final int begin_duration = (steps / 3) * interval; // ((max_duration * 30 / 100) % 5) * 5;

        final SortedMap<Integer, Double> euler2DataModel = transformToEuler2DataModel(raindata, interval);
        final SortedMap<Integer, Double> result = new TreeMap<Integer, Double>();
        int t = begin_duration;
        final int size = euler2DataModel.size();
        for (int i = 0; i < size; i++) {
            final int highestRain_Key = getHighestPrecipitationIntensity_Key(euler2DataModel);
            if ((t > 0) && (t <= begin_duration)) {
                result.put(t, euler2DataModel.get(highestRain_Key));
                t -= interval;
            } else if (t <= 0) {
                t = begin_duration + interval;
                i--;
                continue;
            } else {
                result.put(t, euler2DataModel.get(highestRain_Key));
                t += interval;
            }
            euler2DataModel.remove(highestRain_Key);
        }

        // Transform the values from unit mm to l/(ha*s)
        final double divisor = 0.006d;
        final Iterator iterator = result.keySet().iterator();
        while (iterator.hasNext()) {
            final Integer key = (Integer)iterator.next();
            final double value = result.get(key);
            final double new_Value = value / interval / divisor;
            result.put(key, new_Value);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   raindata  DOCUMENT ME!
     * @param   interval  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SortedMap<Integer, Double> transformToEuler2DataModel(final SortedMap<Integer, Double> raindata,
            final int interval) {
        // First: transform the values from unit mm/h to mm
        // Example 1: duration 5 min, frequency 100 a, intensity 210.34 mm/h
        // -> 210.34 mm/(60min/5min) -> 210.34 mm/12 = 17.5283 mm
        // Example 2: duration 10 min, frequency 100 a, intensity 145.76 mm/h
        // -> 145.76 mm/(60min/10min) -> 145.76 mm/6 = 24.2933 mm

        final int minutes = 60;

        final Iterator iterator = raindata.keySet().iterator();
        while (iterator.hasNext()) {
            final Integer key = (Integer)iterator.next();
            final double value = raindata.get(key);
            final double divisor = (double)minutes / (double)key;
            final double mm_Value = value / divisor;
            raindata.put(key, mm_Value);
        }

        // Secound: The rainfall amounts of the individual time intervals result through subtraction from the rainfall
        // sums
        int key = interval;
        int pre_Key;
        int counter = 0;
        final int steps = (Integer)raindata.lastKey() / interval;
        double diff_Value = raindata.get(key);
        final SortedMap euler2Model = new TreeMap();
        euler2Model.put(key, diff_Value);
        key += interval;

        for (int i = 1; i < steps; i++) {
            if (raindata.containsKey(key)) {
                if (counter > 0) {
                    pre_Key = key - (interval * (counter + 1));
                    diff_Value = raindata.get(key) - raindata.get(pre_Key);
                    diff_Value = diff_Value / (counter + 1);
                    for (int j = 0; j <= counter; j++) {
                        pre_Key += interval;
                        euler2Model.put(pre_Key, diff_Value);
                    }
                    counter = 0;
                } else {
                    pre_Key = key - interval;
                    diff_Value = raindata.get(key) - raindata.get(pre_Key);
                    euler2Model.put(key, diff_Value);
                }
            } else {
                counter++;
            }
            key += interval;
        }

        return euler2Model;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   euler2DataModel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Integer getHighestPrecipitationIntensity_Key(final SortedMap<Integer, Double> euler2DataModel) {
        double value = -1;
        int key = -1;
        final Iterator iterator = euler2DataModel.keySet().iterator();
        while (iterator.hasNext()) {
            final Integer temp_key = (Integer)iterator.next();
            final double temp_value = euler2DataModel.get(temp_key);
            if (value < temp_value) {
                key = temp_key;
                value = temp_value;
            }
        }
        return key;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  busy     DOCUMENT ME!
     * @param  message  DOCUMENT ME!
     */
    private void setStatusEDT(final boolean busy, final String message) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((StatusPanel)getComponent()).setBusy(busy);
                    ((StatusPanel)getComponent()).setStatusMessage(message);
                }
            });
    }
}
