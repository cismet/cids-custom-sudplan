/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UnionCatchmentAreaFeature object.
     *
     * @param   feature            DOCUMENT ME!
     * @param   renderFeatureName  DOCUMENT ME!
     *
     * @throws  GeometryException  DOCUMENT ME!
     */
    public UnionCatchmentAreaFeature(final Feature feature, final QualifiedName renderFeatureName)
            throws GeometryException {
        super(feature, renderFeatureName);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends Action> getActions() {
        return Arrays.asList(new AssignTimeseriesWizardAction(feature));
    }

    @Override
    public String getType() {
        return "Catchment Area Union";
    }
}
