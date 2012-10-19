/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.timeseriesVisualisation.gridcomparison;

import java.awt.Color;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.cismet.cismap.commons.raster.wms.WMSServiceLayer;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class LayerStyle implements Comparable {

    //~ Static fields/initializers ---------------------------------------------

    public static final transient String TEMPLATETOKEN_CUSTOMSTYLE_COLORMAP = "<cismap:colorMap>";
    public static final transient String TEMPLATE_CUSTOMSTYLE = " <StyledLayerDescriptor version=\"1.0.0\""
                + "     xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\""
                + "     xmlns=\"http://www.opengis.net/sld\""
                + "     xmlns:ogc=\"http://www.opengis.net/ogc\""
                + "     xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                + "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "   <NamedLayer>"
                + "     <Name>" + WMSServiceLayer.TEMPLATETOKEN_CUSTOMSTYLE_LAYERNAME + "</Name>"
                + "     <UserStyle>"
                + "       <Title>" + WMSServiceLayer.TEMPLATETOKEN_CUSTOMSTYLE_TITLE + "</Title>"
                + "       <FeatureTypeStyle>"
                + "         <Rule>"
                + "           <PolygonSymbolizer>"
                + "             <Geometry>"
                + "               <ogc:PropertyName>geometry</ogc:PropertyName>"
                + "             </Geometry>"
                + " "
                + "             <Fill>"
                + "               <CssParameter name=\"fill\">"
                + "                 <ogc:Function name=\"Interpolate\">"
                + "                   <!-- Property to transform -->"
                + "                   <ogc:PropertyName>value</ogc:PropertyName>"
                + " "
                + TEMPLATETOKEN_CUSTOMSTYLE_COLORMAP
                + " "
                + "                   <!-- Interpolation method -->"
                + "                   <ogc:Literal>color</ogc:Literal>"
                + " "
                + "                   <!-- Interpolation mode - defaults to linear -->"
                + "                   <!--<ogc:Literal>color</ogc:Literal>"
                + "                   <ogc:Literal>cubic</ogc:Literal>"
                + "                   <ogc:Literal>cosine</ogc:Literal>-->"
                + "                 </ogc:Function>"
                + "               </CssParameter>"
                + "             </Fill>"
                + " "
                + "             <Stroke>"
                + "               <CssParameter name=\"stroke\">"
                + "                 <ogc:Function name=\"Interpolate\">"
                + "                   <!-- Property to transform -->"
                + "                   <ogc:PropertyName>value</ogc:PropertyName>"
                + " "
                + TEMPLATETOKEN_CUSTOMSTYLE_COLORMAP
                + " "
                + "                   <!-- Interpolation method -->"
                + "                   <ogc:Literal>color</ogc:Literal>"
                + " "
                + "                   <!-- Interpolation mode - defaults to linear -->"
                + "                   <!--<ogc:Literal>color</ogc:Literal>"
                + "                   <ogc:Literal>cubic</ogc:Literal>"
                + "                   <ogc:Literal>cosine</ogc:Literal>-->"
                + "                 </ogc:Function>"
                + "               </CssParameter>"
                + "             </Stroke>"
                + "           </PolygonSymbolizer>"
                + "         </Rule>"
                + "       </FeatureTypeStyle>"
                + "     </UserStyle>"
                + "   </NamedLayer>"
                + " </StyledLayerDescriptor>";

    //~ Instance fields --------------------------------------------------------

    private String name;
    private List<Entry> colorMap;
    private String title;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LayerStyle object.
     */
    public LayerStyle() {
        name = "";
        title = "";
        colorMap = new LinkedList<Entry>();
    }

    /**
     * Creates a new LayerStyle object.
     *
     * @param  name      DOCUMENT ME!
     * @param  colorMap  DOCUMENT ME!
     */
    public LayerStyle(final String name, final List<Entry> colorMap) {
        this(name, "", colorMap);
    }

    /**
     * Creates a new LayerStyle object.
     *
     * @param  name      DOCUMENT ME!
     * @param  title     DOCUMENT ME!
     * @param  colorMap  DOCUMENT ME!
     */
    public LayerStyle(final String name, final String title, final List<Entry> colorMap) {
        this.name = name;
        this.title = title;
        this.colorMap = new LinkedList<Entry>(colorMap);

        Collections.sort(this.colorMap);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Entry> getColorMap() {
        return new LinkedList<Entry>(colorMap);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  colorMap  DOCUMENT ME!
     */
    public void setColorMap(final List<Entry> colorMap) {
        this.colorMap = new LinkedList<Entry>(colorMap);

        Collections.sort(this.colorMap);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String generateColorMapString() {
        final StringBuilder result = new StringBuilder();

        for (final Entry entry : colorMap) {
            result.append("<ogc:Literal>").append(entry.getValue()).append("</ogc:Literal>");
            // See http://www.javalobby.org/java/forums/t19183.html
            // One idea is to use: "Integer.toHexString(color.getRGB())". But:
            // "First of all this assumes the color to have a "FF"-contribution from the Alpha-component (which is not
            // always the case). Secondly it does not handle zeros to the left correctly. Remember that "CCC" is
            // different from "000CCC" when it comes to web-colors."
            result.append("<ogc:Literal>#")
                    .append(Integer.toHexString((entry.getColor().getRGB() & 0xffffff) | 0x1000000).substring(1))
                    .append("</ogc:Literal>");
        }

        return result.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSLDForLayer() {
        return TEMPLATE_CUSTOMSTYLE.replaceAll(WMSServiceLayer.TEMPLATETOKEN_CUSTOMSTYLE_TITLE, title)
                    .replaceAll(TEMPLATETOKEN_CUSTOMSTYLE_COLORMAP, generateColorMapString());
    }

    @Override
    public int compareTo(final Object o) {
        if (!(o instanceof LayerStyle)) {
            return Integer.MAX_VALUE;
        }

        final LayerStyle layerStyle = (LayerStyle)o;

        if ((name == null) && (layerStyle.name == null)) {
            return 0;
        } else if ((name == null) && (layerStyle.name != null)) {
            return Integer.MIN_VALUE;
        } else if ((name != null) && (layerStyle.name == null)) {
            return Integer.MAX_VALUE;
        }

        final int result = name.compareTo(layerStyle.name);

        if (result == 0) {
            return new Integer(colorMap.size()).compareTo(new Integer(layerStyle.colorMap.size()));
        }

        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final LayerStyle other = (LayerStyle)obj;

        if ((this.name == null) ? (other.name != null) : (!this.name.equals(other.name))) {
            return false;
        }

        if ((this.title == null) ? (other.title != null) : (!this.title.equals(other.title))) {
            return false;
        }

        if ((this.colorMap != other.colorMap) && ((this.colorMap == null) || !this.colorMap.equals(other.colorMap))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = (47 * hash) + ((this.name != null) ? this.name.hashCode() : 0);
        hash = (47 * hash) + ((this.title != null) ? this.title.hashCode() : 0);
        hash = (47 * hash) + ((this.colorMap != null) ? this.colorMap.hashCode() : 0);

        return hash;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class Entry implements Comparable {

        //~ Instance fields ----------------------------------------------------

        private Double value;
        private Color color;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Entry object.
         *
         * @param  value  DOCUMENT ME!
         * @param  color  DOCUMENT ME!
         */
        public Entry(final Double value, final Color color) {
            this.value = value;
            this.color = color;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Color getColor() {
            return color;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Double getValue() {
            return value;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  color  DOCUMENT ME!
         */
        public void setColor(final Color color) {
            this.color = color;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  value  DOCUMENT ME!
         */
        public void setValue(final Double value) {
            this.value = value;
        }

        @Override
        public int compareTo(final Object o) {
            if (!(o instanceof Entry)) {
                return Integer.MAX_VALUE;
            }

            final Entry entry2 = (Entry)o;

            if ((value == null) && (entry2.value == null)) {
                return 0;
            } else if ((value == null) && (entry2.value != null)) {
                return Integer.MIN_VALUE;
            } else if ((value != null) && (entry2.value == null)) {
                return Integer.MAX_VALUE;
            }

            return value.compareTo(entry2.value);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            final Entry other = (Entry)obj;

            if ((this.value == null) ? (other.value != null) : (!this.value.equals(other.value))) {
                return false;
            }

            if ((this.color != other.color) && ((this.color == null) || !this.color.equals(other.color))) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = (97 * hash) + ((this.value != null) ? this.value.hashCode() : 0);
            hash = (97 * hash) + ((this.color != null) ? this.color.hashCode() : 0);

            return hash;
        }
    }
}
