/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Stroke;

import java.net.URL;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.XStyledFeature;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
final class GridFeature extends DefaultStyledFeature implements XStyledFeature {

    //~ Static fields/initializers ---------------------------------------------

    static final transient ImageIcon ICONIMAGE;

    static {
        final URL imageIconURL = GridFeature.class.getResource(
                "/de/cismet/cids/custom/sudplan/airquality/GridFeature.iconImage.png"); // NOI18N
        if (imageIconURL != null) {
            ICONIMAGE = new ImageIcon(imageIconURL);
        } else {
            ICONIMAGE = new ImageIcon();
        }
    }

    //~ Instance fields --------------------------------------------------------

    private final transient GeometryFactory geometryFactory;
    private Long gridcellCountX;
    private Long gridcellCountY;
    private Integer gridcellSize;
    private Coordinate lowerleft;
    private Coordinate upperright;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridFeature object.
     *
     * @param  gridcellCountX  DOCUMENT ME!
     * @param  gridcellCountY  DOCUMENT ME!
     * @param  gridcellSize    DOCUMENT ME!
     * @param  lowerleft       DOCUMENT ME!
     * @param  upperright      DOCUMENT ME!
     */
    public GridFeature(final Long gridcellCountX,
            final Long gridcellCountY,
            final Integer gridcellSize,
            final Coordinate lowerleft,
            final Coordinate upperright) {
        this.gridcellCountX = gridcellCountX;
        this.gridcellCountY = gridcellCountY;
        this.gridcellSize = gridcellSize;
        this.lowerleft = lowerleft;
        this.upperright = upperright;

        // TODO: Externalize.
        // TODO: Don't hardwire the SRID.
        geometryFactory = new GeometryFactory(new PrecisionModel(), 3021);

        setLinePaint(Color.blue);
        setLineWidth(1);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void calculateGeometry() {
        if ((upperright == null) || (gridcellCountX == null) || (gridcellCountY == null) || (gridcellSize == null)) {
            setGeometry(null);
            return;
        }

        final Collection<Geometry> geometries = new LinkedList<Geometry>();

        final Coordinate[] boundingBoxCoordinates = new Coordinate[5];
        boundingBoxCoordinates[0] = lowerleft;
        boundingBoxCoordinates[1] = new Coordinate(lowerleft.x, upperright.y);
        boundingBoxCoordinates[2] = upperright;
        boundingBoxCoordinates[3] = new Coordinate(upperright.x, lowerleft.y);
        boundingBoxCoordinates[4] = lowerleft;

        geometries.add(geometryFactory.createLineString(boundingBoxCoordinates));

        if ((gridcellCountX != null) && (gridcellCountX.intValue() > 1)) {
            for (int i = 0; i < (gridcellCountX - 1); i++) {
                final double x = lowerleft.x + ((i + 1) * gridcellSize);
                final Coordinate[] lineCoordinates = new Coordinate[2];
                lineCoordinates[0] = new Coordinate(x, lowerleft.y);
                lineCoordinates[1] = new Coordinate(x, upperright.y);

                geometries.add(geometryFactory.createLineString(lineCoordinates));
            }
        }

        if ((gridcellCountY != null) && (gridcellCountY.intValue() > 1)) {
            for (int i = 0; i < (gridcellCountY - 1); i++) {
                final double y = lowerleft.y + ((i + 1) * gridcellSize);
                final Coordinate[] lineCoordinates = new Coordinate[2];
                lineCoordinates[0] = new Coordinate(lowerleft.x, y);
                lineCoordinates[1] = new Coordinate(upperright.x, y);
                geometries.add(geometryFactory.createLineString(lineCoordinates));
            }
        }

        setGeometry(geometryFactory.buildGeometry(geometries));
    }

    @Override
    public Geometry getGeometry() {
        if (super.getGeometry() == null) {
            calculateGeometry();
        }

        return super.getGeometry();
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(GridFeature.class, "GridFeature.name"); // NOI18N
    }

    @Override
    public ImageIcon getIconImage() {
        return ICONIMAGE;
    }

    @Override
    public String getType() {
        return ""; // NOI18N
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public Stroke getLineStyle() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Long getGridcellCountX() {
        return gridcellCountX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellCountX  DOCUMENT ME!
     */
    public void setGridcellCountX(final Long gridcellCountX) {
        this.gridcellCountX = gridcellCountX;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Long getGridcellCountY() {
        return gridcellCountY;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellCountY  DOCUMENT ME!
     */
    public void setGridcellCountY(final Long gridcellCountY) {
        this.gridcellCountY = gridcellCountY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGridcellSize() {
        return gridcellSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gridcellSize  DOCUMENT ME!
     */
    public void setGridcellSize(final Integer gridcellSize) {
        this.gridcellSize = gridcellSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coordinate getLowerleft() {
        return lowerleft;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lowerleft  DOCUMENT ME!
     */
    public void setLowerleft(final Coordinate lowerleft) {
        this.lowerleft = lowerleft;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coordinate getUpperright() {
        return upperright;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  upperright  DOCUMENT ME!
     */
    public void setUpperright(final Coordinate upperright) {
        this.upperright = upperright;
    }
}
