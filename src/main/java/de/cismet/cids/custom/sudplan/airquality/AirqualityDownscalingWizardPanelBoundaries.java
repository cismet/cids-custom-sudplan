/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import com.vividsolutions.jts.geom.Coordinate;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelBoundaries implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient AirqualityDownscalingVisualPanelBoundaries component;

    private transient Coordinate llCoordinate;
    private transient Coordinate urCoordinate;

    private transient Integer gridSize;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingWizardPanelScenarios object.
     */
    public AirqualityDownscalingWizardPanelBoundaries() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelBoundaries(this);
        }

        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Coordinate getLLCoordinate() {
        return llCoordinate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Coordinate getURCoordinate() {
        return urCoordinate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getGridSize() {
        return gridSize;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        llCoordinate = (Coordinate)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_LL_COORD);
        urCoordinate = (Coordinate)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_UR_COORD);
        gridSize = (Integer)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_GRID_SIZE);

        assert llCoordinate != null : "ll coordinate must not be null"; // NOI18N
        assert urCoordinate != null : "ur coordinate must not be null"; // NOI18N
        assert gridSize != null : "grid size must not be null";         // NOI18N

        snapCoordinates();

        component.init();
    }

    /**
     * DOCUMENT ME!
     */
    private void snapCoordinates() {
        // TODO:
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        // we are sure that isValid() has checked the coordinates
        final Coordinate ll = new Coordinate(Double.valueOf(component.getLLX()), Double.valueOf(component.getLLY()));
        final Coordinate ur = new Coordinate(Double.valueOf(component.getURX()), Double.valueOf(component.getURY()));

        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_LL_COORD, ll);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_UR_COORD, ur);
    }

    @Override
    public boolean isValid() {
        boolean valid;

        final String llx = component.getLLX();
        final String lly = component.getLLY();
        final String urx = component.getURX();
        final String ury = component.getURY();

        if (((llx == null) || llx.isEmpty())
                    || ((lly == null) || lly.isEmpty())
                    || ((urx == null) || urx.isEmpty())
                    || ((ury == null) || ury.isEmpty())) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelBoundaries.class,
                    "AirqualityDownscalingVisualPanelBoundaries.isValid().emptyCoordinate")); // NOI18N
            valid = false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
            valid = true;
        }

        try {
            Double.parseDouble(llx);
            Double.parseDouble(lly);
            Double.parseDouble(urx);
            Double.parseDouble(ury);

            // everything is ok
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
            valid = true;
        } catch (final NumberFormatException e) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelBoundaries.class,
                    "AirqualityDownscalingVisualPanelBoundaries.isValid().notADouble")); // NOI18N
            valid = false;
        }

        return valid;
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    /**
     * DOCUMENT ME!
     */
    protected void fireChangeEvent() {
        changeSupport.fireChange();
    }
}
