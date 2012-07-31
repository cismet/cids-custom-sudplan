/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.awt.EventQueue;

import java.net.URL;

import java.util.MissingResourceException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

import de.cismet.tools.gui.BasicGuiComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = BasicGuiComponentProvider.class)
public class GridComparisonWidgetProvider implements BasicGuiComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GridComparisonWidgetProvider.class);

    private static transient GridComparisonWidget WIDGET;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GridComparisonWidget getWidget() {
        if (WIDGET == null) {
            WIDGET = new GridComparisonWidget();
            WIDGET.reloadLayers();
        }

        return WIDGET;
    }

    @Override
    public String getId() {
        return "sudplan.gridcomparison"; // NOI18N
    }

    @Override
    public String getDescription() {
        String description = "Grid comparison widget";

        try {
            description = NbBundle.getMessage(
                    GridComparisonWidgetProvider.class,
                    "GridComparisonWidgetProvider.description");
        } catch (final MissingResourceException ex) {
            LOG.info("I18Nized message for 'GridComparisonWidgetProvider.description' not found.", ex);
        }

        return description;
    }

    @Override
    public String getName() {
        return getWidget().getName();
    }

    @Override
    public Icon getIcon() {
        final URL urlToIcon = getClass().getResource("GridComparisonWidgetProvider_icon.png");

        if (urlToIcon == null) {
            LOG.warn("The icon for the grid comparison widget can't be loaded.");
        }

        Icon result;
        if (urlToIcon != null) {
            result = new ImageIcon(urlToIcon);
        } else {
            result = new ImageIcon();
        }

        return result;
    }

    @Override
    public JComponent getComponent() {
        return getWidget();
    }

    @Override
    public GuiType getType() {
        return BasicGuiComponentProvider.GuiType.GUICOMPONENT;
    }

    @Override
    public Object getPositionHint() {
        return CismapPlugin.ViewSection.LAYER;
    }

    @Override
    public void setLinkObject(final Object link) {
    }
}
