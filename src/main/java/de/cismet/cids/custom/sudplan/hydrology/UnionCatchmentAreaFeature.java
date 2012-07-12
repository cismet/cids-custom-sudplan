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

import java.util.Arrays;
import java.util.Collection;

import javax.swing.Action;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UnionCatchmentAreaFeature extends SingleCatchmentAreaFeature {

    //~ Instance fields --------------------------------------------------------

    private final AssignTimeseriesWizardAction assignAction;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UnionCatchmentAreaFeature object.
     *
     * @param   feature            DOCUMENT ME!
     * @param   renderFeatureName  DOCUMENT ME!
     *
     * @throws  GeometryException      DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public UnionCatchmentAreaFeature(final Feature feature, final QualifiedName renderFeatureName)
            throws GeometryException {
        super(feature, renderFeatureName);

        assignAction = new UnionAssignTimeseriesWizardAction(feature);

        final Geometry geom = WFSUtils.extractGeometry(feature, renderFeatureName);

        // FIXME: performance hack as long as wfs delivers union geometries with holes
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
        final Geometry areaGeometry = gf.createPolygon((LinearRing)candidate.getExteriorRing(), new LinearRing[0]);

        setGeometry(areaGeometry);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends Action> getActions() {
        return Arrays.asList(assignAction);
    }

    @Override
    public String getName() {
        return "Upstream union of " + super.getName();
    }

    @Override
    public String getType() {
        return "Catchment Area Union";
    }
}
