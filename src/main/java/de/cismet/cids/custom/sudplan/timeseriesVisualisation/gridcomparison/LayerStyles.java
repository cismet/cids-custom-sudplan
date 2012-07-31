/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import org.apache.log4j.Logger;

import org.jdom.Attribute;
import org.jdom.Element;

import java.awt.Color;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison.LayerStyle.Entry;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class LayerStyles implements Configurable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LayerStyles.class);

    private static final transient String XML_CONF_ROOT = "layerStyles";
    private static final transient String XML_CONF_LAYERSTYLE = "layerStyle";
    private static final transient String XML_CONF_LAYERSTYLE_NAME = "name";
    private static final transient String XML_CONF_LAYERSTYLE_COLORMAP = "colorMap";
    private static final transient String XML_CONF_LAYERSTYLE_COLORMAP_ENTRY = "entry";
    private static final transient String XML_CONF_LAYERSTYLE_COLORMAP_ENTRY_ATTR_VALUE = "value";
    private static final transient String XML_CONF_LAYERSTYLE_COLORMAP_ENTRY_ATTR_COLOR = "color";

    public static final String PROPERTY_LAYERSTYLES = "layerStyles";

    private static final LayerStyles INSTANCE;

    static {
        INSTANCE = new LayerStyles();
    }

    //~ Instance fields --------------------------------------------------------

    private final PropertyChangeSupport propertyChangeSupport;

    private final List<LayerStyle> layerStyles;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LayerStyles object.
     */
    private LayerStyles() {
        layerStyles = new LinkedList<LayerStyle>();
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static LayerStyles instance() {
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<LayerStyle> getLayerStyles() {
        return new LinkedList<LayerStyle>(layerStyles);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  layerStyles  DOCUMENT ME!
     */
    public void setLayerStyles(final List<LayerStyle> layerStyles) {
        final List<LayerStyle> oldLayerStyles = new LinkedList<LayerStyle>(this.layerStyles);

        this.layerStyles.clear();
        this.layerStyles.addAll(layerStyles);
        Collections.sort(this.layerStyles);

        propertyChangeSupport.firePropertyChange(
            PROPERTY_LAYERSTYLES,
            oldLayerStyles,
            new LinkedList<LayerStyle>(layerStyles));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  propertyChangeListener  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  propertyChangeListener  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void configure(final Element parent) {
        layerStyles.clear();
        final List<LayerStyle> oldLayerStyles = new LinkedList<LayerStyle>(layerStyles);

        if (parent == null) {
            LOG.info("Layer styles can't be configured. It seems that the client configuration is invalid.");
            return;
        }

        final Element layerStyles = parent.getChild(XML_CONF_ROOT);
        if (layerStyles == null) {
            LOG.info("Layer styles can't be configured. It seems that the configuration is invalid.");
            return;
        }

        final List layerStyle = layerStyles.getChildren(XML_CONF_LAYERSTYLE);
        if ((layerStyle == null) || layerStyle.isEmpty()) {
            LOG.info("No layer styles found in the configuration.");
            return;
        }

        for (final Object layerStyleObj : layerStyle) {
            if (!(layerStyleObj instanceof Element)) {
                LOG.info("Configuration for layer style '"
                            + ((layerStyleObj != null) ? layerStyleObj.toString() : "null") + "' is invalid.");
                continue;
            }

            final Element layerStyleElement = (Element)layerStyleObj;
            final Element name = layerStyleElement.getChild(XML_CONF_LAYERSTYLE_NAME);
            final Element colorMap = layerStyleElement.getChild(XML_CONF_LAYERSTYLE_COLORMAP);

            if ((name == null) || (colorMap == null)) {
                LOG.info("Configuration for layer style '" + layerStyleElement + "' is invalid.");
                continue;
            }

            final List entries = colorMap.getChildren(XML_CONF_LAYERSTYLE_COLORMAP_ENTRY);
            if ((entries == null) || entries.isEmpty()) {
                LOG.info("No color map entries found for layer '" + name + "'.");
                continue;
            }

            final List<Entry> colors = new LinkedList<Entry>();

            for (final Object entryObj : entries) {
                if (!(entryObj instanceof Element)) {
                    LOG.info("Color map entry '" + ((entryObj != null) ? entryObj.toString() : "null")
                                + "' for layer style '" + name + "' is invalid.");
                    continue;
                }

                final Element entry = (Element)entryObj;
                final Attribute value = entry.getAttribute(XML_CONF_LAYERSTYLE_COLORMAP_ENTRY_ATTR_VALUE);
                final Attribute color = entry.getAttribute(XML_CONF_LAYERSTYLE_COLORMAP_ENTRY_ATTR_COLOR);
                if ((value == null) || (color == null)) {
                    LOG.info("Color map entry '" + entry + "' for layer style '" + name + "' is invalid.");
                    continue;
                }

                try {
                    colors.add(new Entry(Double.parseDouble(value.getValue()), Color.decode(color.getValue())));
                } catch (final Exception ex) {
                    LOG.warn("Something went wrong while inserting new entry for value '" + value + "' and color '"
                                + color + "'.",
                        ex);
                }
            }

            this.layerStyles.add(new LayerStyle(name.getValue(), colors));
        }

        propertyChangeSupport.firePropertyChange(
            PROPERTY_LAYERSTYLES,
            oldLayerStyles,
            new LinkedList<LayerStyle>(this.layerStyles));
    }

    @Override
    public void masterConfigure(final Element parent) {
        // NoOp
    }

    @Override
    public Element getConfiguration() throws NoWriteError {
        final Element root = new Element(XML_CONF_ROOT);

        for (final LayerStyle layerStyle : layerStyles) {
            final String name = layerStyle.getName();
            final List<Entry> colorMap = layerStyle.getColorMap();

            if (colorMap.isEmpty() || (name == null) || name.isEmpty()) {
                continue;
            }

            final Element layerStyleElement = new Element(XML_CONF_LAYERSTYLE);
            layerStyleElement.addContent(new Element(XML_CONF_LAYERSTYLE_NAME).addContent(name));

            final Element colorMapElement = new Element(XML_CONF_LAYERSTYLE_COLORMAP);

            for (final Entry colorMapEntry : colorMap) {
                final Element colorMapEntryElement = new Element(XML_CONF_LAYERSTYLE_COLORMAP_ENTRY);
                colorMapEntryElement.setAttribute(new Attribute(
                        XML_CONF_LAYERSTYLE_COLORMAP_ENTRY_ATTR_VALUE,
                        Double.toString(colorMapEntry.getValue())));
                colorMapEntryElement.setAttribute(new Attribute(
                        XML_CONF_LAYERSTYLE_COLORMAP_ENTRY_ATTR_COLOR,
                        '#'
                                + Integer.toHexString((colorMapEntry.getColor().getRGB() & 0xffffff) | 0x1000000)
                                    .substring(1)));
                colorMapElement.addContent(colorMapEntryElement);
            }

            layerStyleElement.addContent(colorMapElement);
            root.addContent(layerStyleElement);
        }

        return root;
    }
}
