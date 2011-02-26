/**
 * GraphicImage.java
 * Created on Aug 9, 2010
 */

package old.primitives;

import java.awt.Image;
import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>GraphicImage</code> is a graphics primitive that contains an image,
 *   an anchor point, and (eventually) transform parameters.
 * </p>
 *
 * @author Elisha Peterson
 * @param <C> coordinate system used for anchor
 */
public class GraphicImage<C> extends Point2D.Double {
    
    /** Image */
    public Image image;
    /** Anchor point */
    public C anchor;
    /** Second point (if non-null the image will be sized between these two points) */
    public C corner;
    /** Highlight/select toggle */
    public boolean highlight = false;

    /**
     * Construct graphic image, i.e. an image anchored at a particular point.
     * @param anchor where the string is located
     * @param image the image to be displayed
     */
    public GraphicImage(C anchor, Image image) {
        setAnchor(anchor);
        setImage(image);
    }

    /**
     * Construct graphic image, i.e. an image anchored at a particular point.
     * @param anchor where the string is located
     * @param image the image to be displayed
     * @param highlight whether to "highlight" image
     */
    public GraphicImage(C anchor, Image image, boolean highlight) {
        setAnchor(anchor);
        setImage(image);
        this.highlight = highlight;
    }

    @Override public String toString() {
        return "GraphicImage[" + anchor + ", " + image + "]";
    }

    /** @return the image associated with the graphic image primitive */
    public Image getImage() { return image; }
    /** Set the style associated with the graphic string primitive. */
    public void setImage(Image image) { this.image = image; }

    /** @return highlight status */
    public boolean isHighlight() { return highlight; }
    /** Set highlight status */
    public void setHighlight(boolean highlight) { this.highlight = highlight; }

    /** @return the coordinate anchor point of the graphic image primitive */
    public C getAnchor() { return anchor; }
    /** Set the anchor point of the graphic string primitive. */
    public void setAnchor(C newAnchor) {
        anchor = newAnchor;
        if (anchor instanceof Point2D)
            setLocation((Point2D) newAnchor);
    }

    /** @return the coordinate anchor point of the graphic image primitive */
    public C getCorner() { return corner; }
    /** Set the anchor point of the graphic string primitive. */
    public void setCorner(C newCorner) {
        corner = newCorner;
    }


    @Override
    public void setLocation(double x, double y) {
        super.setLocation(x, y);
        if (anchor instanceof Point2D)
            ((Point2D)anchor).setLocation(x, y);
    }
    @Override
    public void setLocation(Point2D p) {
        super.setLocation(p);
        if (anchor instanceof Point2D)
            ((Point2D)anchor).setLocation(x, y);
    }
}
