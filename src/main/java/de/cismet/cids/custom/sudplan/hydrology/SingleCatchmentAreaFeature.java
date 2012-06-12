/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

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

    //~ Instance fields --------------------------------------------------------

    protected final transient Feature feature;

    private final transient QualifiedName renderFeatureName;
    private final transient ImageIcon icon;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SingleCatchmentAreaFeature object.
     *
     * @param   feature  DOCUMENT ME!
     *
     * @throws  GeometryException  DOCUMENT ME!
     */
    public SingleCatchmentAreaFeature(final Feature feature) throws GeometryException {
        this(feature, null);
    }

    /**
     * Creates a new SingleCatchmentAreaFeature object.
     *
     * @param   feature            DOCUMENT ME!
     * @param   renderFeatureName  DOCUMENT ME!
     *
     * @throws  GeometryException         DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public SingleCatchmentAreaFeature(final Feature feature, final QualifiedName renderFeatureName)
            throws GeometryException {
        if (feature == null) {
            throw new IllegalArgumentException("feature must not be null"); // NOI18N
        }

        this.feature = feature;
        this.renderFeatureName = renderFeatureName;
        this.icon = ImageUtilities.loadImageIcon(
                "de/cismet/cids/custom/sudplan/hydrology/catchment_area_16.png", // NOI18N
                false);

        final Geometry geom = WFSUtils.extractGeometry(feature, renderFeatureName);

        final Geometry areaGeometry;
        // FIXME: performance hack as long as wfs delivers union geometries with holes
        if ((renderFeatureName != null) && "upstream_geom".equals(renderFeatureName.getLocalName())) { // NOI18N
            Polygon candidate = null;
            for (int i = 0; i < geom.getNumGeometries(); ++i) {
                final Geometry g = geom.getGeometryN(i);
                if ((g instanceof Polygon)
                            && ((candidate == null) || (candidate.getNumPoints() < g.getNumPoints()))) {
                    candidate = (Polygon)g;
                }
            }

            if (candidate == null) {
                throw new IllegalStateException("no outer ring found"); // NOI18N
            }

            final GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
            areaGeometry = gf.createPolygon((LinearRing)candidate.getExteriorRing(), new LinearRing[0]);
        } else {
            areaGeometry = geom;
        }

        setGeometry(areaGeometry);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends Action> getActions() {
        return Arrays.asList(
                new ShowUpstreamAreasForAreaAction(feature),
                null,
                new CreateLocalModelWizardAction(feature),
                new AssignTimeseriesWizardAction(feature),
                new DoCalibrationWizardAction());
    }

    @Override
    public ImageIcon getIconImage() {
        return icon;
    }

    @Override
    public String getName() {
        return feature.getId();
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
