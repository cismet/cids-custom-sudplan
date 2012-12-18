/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.data.io;

import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Martin Scholl
 * @version  $Revision$, $Date$
 */
public class TimeSeriesConverterChoosePanel extends AbstractConverterChoosePanel<TimeseriesConverter> {

    //~ Instance fields --------------------------------------------------------

    private final transient List<TimeseriesConverter> converters;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeSeriesConverterChoosePanel object.
     *
     * @param  ctrl  DOCUMENT ME!
     */
    public TimeSeriesConverterChoosePanel(final AbstractConverterChoosePanelCtrl ctrl) {
        super(ctrl);

        // TODO: use lookup
        converters = new ArrayList<TimeseriesConverter>();
        converters.addAll(Lookup.getDefault().lookupAll(TimeseriesConverter.class));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<TimeseriesConverter> getConverters() {
        return converters;
    }
}
