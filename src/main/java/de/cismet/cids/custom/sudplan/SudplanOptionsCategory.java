/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.openide.util.lookup.ServiceProvider;

import javax.swing.Icon;

import de.cismet.lookupoptions.AbstractOptionsCategory;
import de.cismet.lookupoptions.OptionsCategory;
import org.openide.util.ImageUtilities;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = OptionsCategory.class)
public final class SudplanOptionsCategory extends AbstractOptionsCategory {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Icon getIcon() {
        // FIXME: create icon
        return ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/", false);
    }

    @Override
    public String getName() {
        return "Sudplan";
    }

    @Override
    public String getTooltip() {
        return "Sudplan specific options"; // NOI18N
    }
}
