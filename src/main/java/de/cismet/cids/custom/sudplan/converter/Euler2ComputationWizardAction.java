/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.converter;

import Sirius.navigator.ui.ComponentRegistry;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;

import java.io.IOException;
import java.io.StringReader;

import java.text.MessageFormat;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.IDFCurve;
import de.cismet.cids.custom.sudplan.IDFTablePanel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class Euler2ComputationWizardAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SELECTED_RAINDATA = "__prop_selected_raindata__";
    public static final String PROP_FORECAST = "__prop_forecast__";

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;

    private final transient IDFCurve idfcurve;
    private final transient IDFTablePanel model;
    private transient SortedMap<Integer, Double> raindata;
    private transient boolean forecast;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Euler2ComputationWizardAction object.
     *
     * @param   model  rainData DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public Euler2ComputationWizardAction(final IDFTablePanel model) {
        this.model = model;

        final CidsBean cidsBeanIDFcurve = this.model.getCidsBeanIDFcurve();
        if (cidsBeanIDFcurve.getProperty("forecast") == null) {
            forecast = false;
        } else {
            forecast = (Boolean)cidsBeanIDFcurve.getProperty("forecast");
        }

        final String json = (String)cidsBeanIDFcurve.getProperty("uri"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();
        final IDFCurve curve;
        try {
            curve = mapper.readValue(new StringReader(json), IDFCurve.class);
        } catch (IOException ex) {
            final String message = "cannot read idf data from uri";      // NOI18N
            throw new IllegalStateException(message, ex);
        }
        this.idfcurve = curve;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new Euler2ComputationWizardPanelAttributes(),
                    new Euler2ComputationWizardPanelCompute()
                };
            final String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                final Component c = panels[i].getComponent();
                steps[i] = c.getName();
                if (c instanceof JComponent) {
                    final JComponent jc = (JComponent)c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(i));
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    @Override
    public void actionPerformed(final ActionEvent ae) {
        final WizardDescriptor wizard = new WizardDescriptor(getPanels());

        wizard.setTitleFormat(new MessageFormat("{0}"));
        wizard.setTitle(NbBundle.getMessage(
                Euler2ComputationWizardAction.class,
                "Euler2ComputationWizardAction.actionPerformed(ActionEvent).wizard.title"));

        final int colIndexEnd = model.getSelectedColIndex();
        final int rowIndexStart = model.getSelectedRowStart();
        final int rowIndexEnd = model.getSelectedRowEnd();

        final SortedMap<Integer, Double> data = new TreeMap<Integer, Double>();

        for (int i = rowIndexStart; i <= rowIndexEnd; i++) {
            final Integer duration = (Integer)idfcurve.getDurationIntensityRows()[i][0];
            final Double value = (Double)idfcurve.getDurationIntensityRows()[i][colIndexEnd];
            if ((duration != null) && (value != null)) {
                data.put(duration, value);
            }
        }

        raindata = data;
        wizard.putProperty(PROP_SELECTED_RAINDATA, raindata);
        wizard.putProperty(PROP_FORECAST, forecast);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        if (wizard.getValue() != WizardDescriptor.FINISH_OPTION) {
            for (final WizardDescriptor.Panel panel : this.panels) {
                if (panel instanceof Cancellable) {
                    ((Cancellable)panel).cancel();
                }
            }
        }
    }
}
