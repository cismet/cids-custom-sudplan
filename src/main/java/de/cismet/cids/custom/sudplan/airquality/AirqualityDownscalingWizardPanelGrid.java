/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import com.vividsolutions.jts.geom.Coordinate;

import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelGrid extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingWizardPanelGrid.class);

    //~ Instance fields --------------------------------------------------------

    private transient AirqualityDownscalingVisualPanelGrid component;
    private transient Coordinate lowerleft = new Coordinate(0D, 0D);
    private transient Coordinate upperright = new Coordinate(4000D, 3000D);
    private transient Integer gridcellSize = new Integer(1000);
    private transient Long gridcellCountX = new Long(4);
    private transient Long gridcellCountY = new Long(3);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final AirqualityDownscalingWizardPanelGrid panel = new AirqualityDownscalingWizardPanelGrid();
                    panel.createComponent();
                    final WizardDescriptor wizard = new WizardDescriptor(new WizardDescriptor.Panel[] { panel });
                    wizard.putProperty(AirqualityDownscalingWizardAction.PROP_GRID_LOWERLEFT, new Coordinate(0, 0));
                    wizard.putProperty(
                        AirqualityDownscalingWizardAction.PROP_GRID_UPPERRIGHT,
                        new Coordinate(3000, 5000));
                    wizard.putProperty(AirqualityDownscalingWizardAction.PROP_GRIDCELL_SIZE, Integer.valueOf(1000));

                    final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizard);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    dialog.toFront();
                }
            });
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelGrid(this);
        }

        return component;
    }

    @Override
    public void read(final WizardDescriptor wizard) {
        lowerleft = (Coordinate)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_GRID_LOWERLEFT);
        upperright = (Coordinate)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_GRID_UPPERRIGHT);
        gridcellSize = (Integer)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_GRIDCELL_SIZE);
        gridcellCountX = (Long)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_GRIDCELL_COUNT_X);
        gridcellCountY = (Long)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_GRIDCELL_COUNT_Y);

        component.init();
    }

    @Override
    public void store(final WizardDescriptor wizard) {
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_GRID_LOWERLEFT, lowerleft);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_GRID_UPPERRIGHT, upperright);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_GRIDCELL_SIZE, gridcellSize);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_GRIDCELL_COUNT_X, gridcellCountX);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_GRIDCELL_COUNT_Y, gridcellCountY);
    }

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        if (gridcellSize == null) {
            // It's the case when the user enters text instead of an integer.
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelGrid.class,
                    "AirqualityDownscalingWizardPanelGrid.isValid().notAnInteger")); // NOI18N);
            return false;
        }

        final String choosenGridSize = gridcellSize.toString();
        boolean valid;

        if ((choosenGridSize == null) || choosenGridSize.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelGrid.class,
                    "AirqualityDownscalingWizardPanelGrid.isValid().gridSizeEmpty")); // NOI18N
            valid = false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
            valid = true;
        }

        try {
            final Integer size = Integer.parseInt(choosenGridSize);
            // no exception, it is ok
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

            if ((size < 1000) || (size > 10000)) {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    NbBundle.getMessage(
                        AirqualityDownscalingWizardPanelGrid.class,
                        "AirqualityDownscalingWizardPanelGrid.isValid().gridSizeBounds"));
                valid = false;
            } else {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
                valid = true;
            }
        } catch (final NumberFormatException e) {
            wizard.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelGrid.class,
                    "AirqualityDownscalingWizardPanelGrid.isValid().notAnInteger")); // NOI18N);
            valid = false;
        }

        return valid;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGridcellSize() {
        return gridcellSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellSize  DOCUMENT ME!
     */
    public void setGridcellSize(final Integer gridcellSize) {
        this.gridcellSize = gridcellSize;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Long getGridcellCountX() {
        return gridcellCountX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellCountX  DOCUMENT ME!
     */
    public void setGridcellCountX(final Long gridcellCountX) {
        this.gridcellCountX = gridcellCountX;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Long getGridcellCountY() {
        return gridcellCountY;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellCountY  DOCUMENT ME!
     */
    public void setGridcellCountY(final Long gridcellCountY) {
        this.gridcellCountY = gridcellCountY;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coordinate getLowerleft() {
        return lowerleft;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lowerleft  DOCUMENT ME!
     */
    public void setLowerleft(final Coordinate lowerleft) {
        this.lowerleft = lowerleft;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coordinate getUpperright() {
        return upperright;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  upperright  DOCUMENT ME!
     */
    public void setUpperright(final Coordinate upperright) {
        this.upperright = upperright;

        changeSupport.fireChange();
    }
}
