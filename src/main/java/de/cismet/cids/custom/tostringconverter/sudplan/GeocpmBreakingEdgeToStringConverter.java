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
public final class GeocpmBreakingEdgeToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder sb = new StringBuilder("Bruchkante ");

        // never null
        sb.append(cidsBean.getProperty("index")); // NOI18N
        sb.append(" [Konfiguration ").append(cidsBean.getProperty("geocpm_configuration_id.id")).append("]");

        return sb.toString();
    }
}
