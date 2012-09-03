/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.Icon;

import de.cismet.lookupoptions.AbstractOptionsCategory;
import de.cismet.lookupoptions.OptionsCategory;

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
    public int getOrder() {
        return -100000000;
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("de/cismet/cids/custom/sudplan/sudplan.png", false); // NOI18N
    }

    @Override
    public String getName() {
        return "Sudplan"; // NOI18N
    }

    @Override
    public String getTooltip() {
        return "Sudplan specific options";
    }
}
