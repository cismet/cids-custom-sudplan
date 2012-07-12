/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.hydrology;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import java.util.Collection;

import javax.swing.AbstractAction;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractWFSFeatureRetrievalAction extends AbstractAction implements WFSQueryInfo {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(AbstractWFSFeatureRetrievalAction.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractWFSFeatureRetrievalAction object.
     */
    public AbstractWFSFeatureRetrievalAction() {
        this(null);
    }

    /**
     * Creates a new AbstractWFSFeatureRetrievalAction object.
     *
     * @param  name  DOCUMENT ME!
     */
    public AbstractWFSFeatureRetrievalAction(final String name) {
        super(name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  actionEvent  DOCUMENT ME!
     */
    @Override
    public final void actionPerformed(final ActionEvent actionEvent) {
        final Collection<Feature> features = WFSUtils.fetchFeatures(this);

        if (features != null) {
            final MappingComponent mc = CismapBroker.getInstance().getMappingComponent();
            mc.getFeatureCollection().addFeatures(features);
            mc.zoomToFeatureCollection();
        }
    }
}
