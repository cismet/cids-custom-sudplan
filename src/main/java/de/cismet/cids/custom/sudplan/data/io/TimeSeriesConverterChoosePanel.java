/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.data.io;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.sudplan.converter.HydrologyTimeseriesConverter;
import de.cismet.cids.custom.sudplan.converter.LinzTimeseriesConverter;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import de.cismet.cids.custom.sudplan.converter.WuppertalTimeseriesConverter;

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
        converters.add(new LinzTimeseriesConverter());
        converters.add(new WuppertalTimeseriesConverter());
        converters.add(new HydrologyTimeseriesConverter());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<TimeseriesConverter> getConverters() {
        return converters;
    }
}
