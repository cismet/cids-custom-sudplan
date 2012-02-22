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
public final class GeocpmConfigurationToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder sb = new StringBuilder(); // NOI18N

        final String name = (String)cidsBean.getProperty("name"); // NOI18N

        if (name == null) {
            sb.append("GeoCPM Konfiguration ").append(cidsBean.getProperty("id"));
        } else {
            sb.append(name);
        }

        sb.append(" [Original Konfiguration]");

        return sb.toString();
    }
}
