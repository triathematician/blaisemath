/*
 * ImageStyle.java
 * Created Aug 9, 2010
 */

package old.styles;

import old.styles.verified.AbstractPrimitiveStyle;
import old.primitives.GraphicImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import graphics.renderer.Anchor;

/**
 * Draws an image on a canvas
 * @author Elisha Peterson
 */
public class ImageStyle extends AbstractPrimitiveStyle<GraphicImage<Point2D.Double>> {

    /** Stores the anchor. */
    Anchor anchor = Anchor.Center;
    /** Stores the scale parameter. */
    float scale = 1f;
    /** Max width to display */
    Integer maxWidth = Integer.MAX_VALUE;
    /** Max height to display */
    Integer maxHeight = Integer.MAX_VALUE;
    /** Stores the stroke color for the boundary */
    Color borderColor = Color.BLACK;
    /** Color for highlighting */
    Color highlight = Color.RED;

    /** Default constructor. */
    public ImageStyle() { }
    /** Construct with anchor only */
    public ImageStyle(Anchor anchor) { setAnchor(anchor); }

    @Override
    public String toString() { return "ImageStyle [" + anchor + "]"; }
    public Class getTargetType() { return GraphicImage.class; }
    
    /** @return location of anchor point of string relative to provided coordinate */
    public Anchor getAnchor() { return anchor; }
    /** @param newValue new location of anchor point of string relative to provided coordinate */
    public void setAnchor(Anchor newValue) { anchor = newValue; }

    /** @return current image scale */
    public float getScale() { return scale; }
    /** @param newValue new value of image scale */
    public void setScale(float newValue) { scale = newValue; }

    /** @return current maxWidth */
    public int getMaxWidth() { return maxWidth; }
    /** @param newValue new value of maxWidth */
    public void setMaxWidth(int newValue) { maxWidth = newValue; }

    /** @return current maxHeight */
    public int getMaxHeight() { return maxHeight; }
    /** @param newValue new value of maxHeight */
    public void setMaxHeight(int newValue) { maxHeight = newValue; }

    /** @return border color */
    public Color getBorderColor() { return borderColor; }
    /** @param newValue new border color */
    public void setBorderColor(Color newValue) { borderColor = newValue; }

    /** @return highlight color */
    public Color getHighlightColor() { return highlight; }
    /** @param newValue new highlight color */
    public void setHighlightColor(Color newValue) { highlight = newValue; }


    /** @return boundaries of the string for the current settings */
    Rectangle2D.Double bounds(GraphicImage<Point2D.Double> gi) {
        if (gi.getCorner() != null)
            return new Rectangle2D.Double(gi.anchor.x, gi.anchor.y, gi.corner.x - gi.anchor.x, gi.corner.y - gi.anchor.y);

        double width = scale*gi.image.getWidth(null);
        double height = scale*gi.image.getHeight(null);

        double factor = Math.max(width/maxWidth, height/maxHeight);
        if (factor > 1) {
            width /= factor;
            height /= factor;
        }

        Point2D.Double shift = new Point2D.Double();

        switch (anchor) {
            case Northeast:
            case East:
            case Southeast:
                shift.x = -width;
                break;
            case North:
            case Center:
            case South:
                shift.x = -width / 2;
                break;
        }

        switch (anchor) {
            case Northwest:
            case North:
            case Northeast:
                shift.y = height;
                break;
            case West:
            case Center:
            case East:
                shift.y = height / 2;
                break;
        }

        return new Rectangle2D.Double(gi.anchor.x + shift.x, gi.anchor.y - shift.y, width, height);
    }

    public void draw(Graphics2D canvas, GraphicImage<Point2D.Double> primitive) {
        Rectangle2D.Double bounds = bounds(primitive);

        canvas.drawImage(primitive.image, (int) bounds.x, (int) bounds.y,
                (int) bounds.getWidth(), (int) bounds.getHeight(), null);

        if (borderColor != null || (primitive.highlight && highlight != null)) {
            canvas.setColor(primitive.highlight ? highlight : borderColor);
            canvas.setStroke(new BasicStroke(2f));
            canvas.drawRect((int) bounds.x, (int) bounds.y, (int) bounds.getWidth(), (int) bounds.getHeight());
        }
    }

    public boolean contained(GraphicImage<Point2D.Double> primitive, Graphics2D canvas, Point point) {
        return bounds(primitive).contains(point);
    }

}
