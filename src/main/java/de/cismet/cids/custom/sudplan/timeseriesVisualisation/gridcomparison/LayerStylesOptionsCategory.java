/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.cismet.lookupoptions.AbstractOptionsCategory;
import de.cismet.lookupoptions.OptionsCategory;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = OptionsCategory.class)
public class LayerStylesOptionsCategory extends AbstractOptionsCategory {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getName() {
        return NbBundle.getMessage(LayerStylesOptionsCategory.class, "LayerStylesOptionsCategory.name");
    }

    @Override
    public Icon getIcon() {
        final Image image = ImageUtilities.loadImage(
                "de/cismet/cids/custom/sudplan/timeseriesVisualisation/gridcomparison/layerStyles.png");
        if (image != null) {
            return new ImageIcon(image);
        } else {
            return null;
        }
    }
}
