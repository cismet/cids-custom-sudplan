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
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
public class InvestigationAreaToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String name = (String)cidsBean.getProperty("name");
        if (name != null) {
            return name;
        } else {
            final StringBuilder sb = new StringBuilder("Untersuchungsgebiet ID-");
            sb.append((String)cidsBean.getProperty("id"));
            return sb.toString();
        }
    }
}
