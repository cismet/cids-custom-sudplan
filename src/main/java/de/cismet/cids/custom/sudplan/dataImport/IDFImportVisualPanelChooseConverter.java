/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.dataImport;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.sudplan.converter.AbstractConverterChoosePanel;
import de.cismet.cids.custom.sudplan.converter.AbstractConverterChoosePanelCtrl;
import de.cismet.cids.custom.sudplan.converter.IDFConverter;
import de.cismet.cids.custom.sudplan.converter.LinzIDFConverter;
import de.cismet.cids.custom.sudplan.converter.WuppertalIDFConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Martin Scholl
 * @version  $Revision$, $Date$
 */
public class IDFImportVisualPanelChooseConverter extends AbstractConverterChoosePanel<IDFConverter> {

    //~ Instance fields --------------------------------------------------------

    private final transient List<IDFConverter> converters;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IDFImportVisualPanelChooseConverter object.
     *
     * @param  ctrl  DOCUMENT ME!
     */
    public IDFImportVisualPanelChooseConverter(final AbstractConverterChoosePanelCtrl ctrl) {
        super(ctrl);

        // TODO: use lookup
        converters = new ArrayList<IDFConverter>();
        converters.add(new LinzIDFConverter());
        converters.add(new WuppertalIDFConverter());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<IDFConverter> getConverters() {
        return converters;
    }
}
