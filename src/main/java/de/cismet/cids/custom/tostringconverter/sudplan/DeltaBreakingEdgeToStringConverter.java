/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.sudplan;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DeltaBreakingEdgeToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder sb = new StringBuilder("Änderungsbruchkante ");

        final String name = (String)cidsBean.getProperty("name"); // NOI18N
        if (name == null) {
            sb.append(cidsBean.getProperty("id"));                // NOI18N
        } else {
            sb.append('\'').append(name).append('\'');
        }

        sb.append(" [Basis-Bruchkante ").append(cidsBean.getProperty("original_object")).append("]");

        return sb.toString();
    }
}
