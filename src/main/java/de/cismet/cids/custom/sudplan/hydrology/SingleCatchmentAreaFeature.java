/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.Feature;
import org.deegree.model.spatialschema.GeometryException;

import org.openide.util.ImageUtilities;

import java.awt.Stroke;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.XStyledFeature;

import de.cismet.tools.gui.ActionsProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class SingleCatchmentAreaFeature extends DefaultStyledFeature implements ActionsProvider, XStyledFeature {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(SingleCatchmentAreaFeature.class);

    //~ Instance fields --------------------------------------------------------

    protected final transient Feature feature;

    private final transient QualifiedName renderFeatureName;
    private final transient QualifiedName catchmentIdName;
    private final transient ImageIcon icon;

    private final transient String name;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SingleCatchmentAreaFeature object.
     *
     * @param   feature          DOCUMENT ME!
     * @param   catchmentIdName  DOCUMENT ME!
     *
     * @throws  GeometryException  DOCUMENT ME!
     */
    public SingleCatchmentAreaFeature(final Feature feature, final QualifiedName catchmentIdName)
            throws GeometryException {
        this(feature, null, catchmentIdName);
    }

    /**
     * Creates a new SingleCatchmentAreaFeature object.
     *
     * @param   feature            DOCUMENT ME!
     * @param   renderFeatureName  DOCUMENT ME!
     * @param   catchmentIdName    DOCUMENT ME!
     *
     * @throws  GeometryException         DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public SingleCatchmentAreaFeature(final Feature feature,
            final QualifiedName renderFeatureName,
            final QualifiedName catchmentIdName) throws GeometryException {
        if (feature == null) {
            throw new IllegalArgumentException("feature must not be null"); // NOI18N
        }

        this.feature = feature;
        this.renderFeatureName = renderFeatureName;
        this.catchmentIdName = catchmentIdName;
        this.icon = ImageUtilities.loadImageIcon(
                "de/cismet/cids/custom/sudplan/hydrology/catchment_area_16.png", // NOI18N
                false);

        final Object value = WFSUtils.getFeaturePropertyValue(feature, catchmentIdName);

        if (value instanceof String) {
            name = "Catchment Area " + value;
        } else {
            LOG.warn("cannot fetch catchment area id from feature: " + feature); // NOI18N
            name = feature.getId();
        }

        final Geometry geom = WFSUtils.extractGeometry(feature, renderFeatureName);

        setGeometry(geom);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends Action> getActions() {
        return Arrays.asList(
                new ShowUpstreamAreasForAreaAction(feature),
                null,
                new CreateLocalModelWizardAction(feature),
                new AssignTimeseriesWizardAction(feature, catchmentIdName),
                new DoCalibrationWizardAction(),
                new DoSimulationWizardAction(feature));
    }

    @Override
    public ImageIcon getIconImage() {
        return icon;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "Catchment Area";
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public Stroke getLineStyle() {
        return null;
    }
}
