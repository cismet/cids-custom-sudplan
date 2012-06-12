/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality.emissionupload;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.io.File;

import java.util.Collection;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class EmissionUploadPanelGrids extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EmissionUploadPanelGrids.class);

    //~ Instance fields --------------------------------------------------------

    private transient volatile EmissionUploadVisualPanelGrids component;
    private transient Collection<Grid> grids;
    private transient Substance substance;
    private transient File emissionGrid;
    private transient TimeVariation timeVariation;
    private transient File customTimeVariation;
    private transient GridHeight gridHeight;
    private transient String gridName;

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isValid() {
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

        boolean isGridValid = true;

        if (substance == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadPanelGrids.class,
                    "EmissionUploadPanelGrids.isValid().info.noSubstance")); // NOI18N
            isGridValid = false;
        }

        if (isGridValid) {
            if (emissionGrid == null) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        EmissionUploadPanelGrids.class,
                        "EmissionUploadPanelGrids.isValid().info.noEmissionGrid"));          // NOI18N
                isGridValid = false;
            } else {
                if (!emissionGrid.canRead()) {
                    wizard.putProperty(
                        WizardDescriptor.PROP_WARNING_MESSAGE,
                        NbBundle.getMessage(
                            EmissionUploadPanelGrids.class,
                            "EmissionUploadPanelGrids.isValid().warn.invalidEmissionGrid")); // NOI18N
                    isGridValid = false;
                }
            }
        }

        if (isGridValid && (timeVariation == null)) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadPanelGrids.class,
                    "EmissionUploadPanelGrids.isValid().info.noTimeVariation")); // NOI18N
            isGridValid = false;
        }

        if (isGridValid && TimeVariation.CUSTOM.equals(timeVariation)) {
            if (customTimeVariation == null) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        EmissionUploadPanelGrids.class,
                        "EmissionUploadPanelGrids.isValid().info.noCustomTimeVariation"));          // NOI18N
                isGridValid = false;
            } else {
                if (!customTimeVariation.canRead()) {
                    wizard.putProperty(
                        WizardDescriptor.PROP_WARNING_MESSAGE,
                        NbBundle.getMessage(
                            EmissionUploadPanelGrids.class,
                            "EmissionUploadPanelGrids.isValid().warn.invalidCustomTimeVariation")); // NOI18N
                    isGridValid = false;
                }
            }
        }

        if (isGridValid && (gridHeight == null)) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadPanelGrids.class,
                    "EmissionUploadPanelGrids.isValid().info.noGridHeight")); // NOI18N
            isGridValid = false;
        }

        if (isGridValid && ((gridName == null) || (gridName.trim().length() <= 0))) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    EmissionUploadPanelGrids.class,
                    "EmissionUploadPanelGrids.isValid().info.noGridName")); // NOI18N
            isGridValid = false;
        }

        component.enableSaveButton(isGridValid && component.isDirty());

        if (component.getGrids().isEmpty()) {
            if (isGridValid) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        EmissionUploadPanelGrids.class,
                        "EmissionUploadPanelGrids.isValid().info.noGrids")); // NOI18N
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    protected Component createComponent() {
        if (component == null) {
            component = new EmissionUploadVisualPanelGrids(this);
        }

        return component;
    }

    @Override
    protected void read(final WizardDescriptor wizard) {
        grids = (Collection<Grid>)wizard.getProperty(EmissionUploadWizardAction.PROPERTY_GRIDS);

        component.init();

        changeSupport.fireChange();
    }

    @Override
    protected void store(final WizardDescriptor wizard) {
        wizard.putProperty(EmissionUploadWizardAction.PROPERTY_GRIDS, grids);

        // Of the user decides to click "Next" if he has partially specified a new grid, we won't prevent him from
        // losing his input. But when he returns to this panel, it should be cleared.
        component.reset();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Grid> getGrids() {
        return grids;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  grids  DOCUMENT ME!
     */
    public void setGrids(final Collection<Grid> grids) {
        this.grids = grids;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public File getCustomTimeVariation() {
        return customTimeVariation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  customTimeVariation  DOCUMENT ME!
     */
    public void setCustomTimeVariation(final File customTimeVariation) {
        this.customTimeVariation = customTimeVariation;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public File getEmissionGrid() {
        return emissionGrid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  emissionGrid  DOCUMENT ME!
     */
    public void setEmissionGrid(final File emissionGrid) {
        this.emissionGrid = emissionGrid;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GridHeight getGridHeight() {
        return gridHeight;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridHeight  DOCUMENT ME!
     */
    public void setGridHeight(final GridHeight gridHeight) {
        this.gridHeight = gridHeight;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGridName() {
        return gridName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridName  DOCUMENT ME!
     */
    public void setGridName(final String gridName) {
        this.gridName = gridName;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Substance getSubstance() {
        return substance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  substance  DOCUMENT ME!
     */
    public void setSubstance(final Substance substance) {
        this.substance = substance;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TimeVariation getTimeVariation() {
        return timeVariation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeVariation  DOCUMENT ME!
     */
    public void setTimeVariation(final TimeVariation timeVariation) {
        this.timeVariation = timeVariation;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  grid  DOCUMENT ME!
     */
    public void setGrid(final Grid grid) {
        if (grid != null) {
            substance = grid.getSubstance();
            emissionGrid = grid.getEmissionGrid();
            timeVariation = grid.getTimeVariation();
            customTimeVariation = grid.getCustomTimeVariation();
            gridHeight = grid.getGridHeight();
            gridName = grid.getGridName();
        } else {
            substance = null;
            emissionGrid = null;
            timeVariation = null;
            customTimeVariation = null;
            gridHeight = null;
            gridName = null;
        }

        changeSupport.fireChange();
    }
}
