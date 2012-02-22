/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.sudplan;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class DeltaConfigurationToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder sb = new StringBuilder("Änderungskonfiguration ");

        final String name = (String)cidsBean.getProperty("name"); // NOI18N
        if (name == null) {
            sb.append(cidsBean.getProperty("id"));                // NOI18N
        } else {
            sb.append('\'').append(name).append('\'');
        }

        sb.append(" [Basis-Konfiguration ");

        final CidsBean gBean = (CidsBean)cidsBean.getProperty("original_object"); // NOI18N
        final String gName = (String)gBean.getProperty("name");                   // NOI18N
        if (gName == null) {
            sb.append(gBean.getProperty("id"));                                   // NOI18N
        } else {
            sb.append('\'').append(gName).append('\'');
        }

        sb.append("]"); // NOI18N

        return sb.toString();
    }
}
