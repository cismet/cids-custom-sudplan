/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation;

/**
 * Offers the capability to control the behaviour of a <code>TimeSeriesVisualisation</code>. Use the <code>
 * getLookup()</code> method to find out if the <code>TimeSeriesVisualisation</code> implements this Interface. This
 * Interface was designed for the use in <code>SimpleTSVisualisation</code> and the there used JFreeChart API. Feel free
 * to extend this interface with your own controlling features by subclassing it
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public interface Controllable {

    //~ Methods ----------------------------------------------------------------

    /**
     * enables a context menue for the visualisation.
     *
     * @param  aFlag  a flag
     */
    void enableContextMenu(boolean aFlag);

    /**
     * get method fot the state of the context menue.
     *
     * @return  true if the ContextMenue is enabled, false otherwise
     */
    boolean iscontextMenuEnabled();

    /**
     * sets a flag that determines if the visualisation shows a legend default value true.
     *
     * @param  aFlag  the flag
     */
    void showLegend(boolean aFlag);

    /**
     * get method for the visible state of the legend.
     *
     * @return  returns true if legend is visible, false otherwise
     */
    boolean islegendVisible();

    /**
     * enables tooltips for the visualisation default value false in fact of perfomace reasons default false.
     *
     * @param  aFlag  the flag
     */
    void enableToolTips(boolean aFlag);

    /**
     * get method for the enabled state of tooltips.
     *
     * @return  returns true if ToolTips are enabled, false otherwise
     */
    boolean istoolTipsEnabled();

    /**
     * enables zoom functionality which allows the user a more detailed view of the data. adds or removes the button for
     * resetting the zoom from toolbar default value true
     *
     * @param  aFlag  the flag
     */
    void enableZoom(boolean aFlag);

    /**
     * get method for the enabled state of zoom.
     *
     * @return  returns true if zoom is enabled, false otherwise
     */
    boolean isZoomEnabled();

    /**
     * enables selection of time series. adds or remove button for deleting selected timeseries, select all and deselect
     * all button default true
     *
     * @param  aFlag  the flag
     */
    void enableSelection(boolean aFlag);

    /**
     * get method for the enabled state of selection.
     *
     * @return  returns true, if the selection is enabled, false otherwise
     */
    boolean isSelectionEnabled();
}
