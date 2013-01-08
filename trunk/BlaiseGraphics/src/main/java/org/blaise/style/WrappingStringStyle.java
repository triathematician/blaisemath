/*
 * WrappingStringStyle.java
 * Created on Jan 2, 2013
 */

package org.blaise.style;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Draws a string within clipping boundaries defined by a graphics canvas.
 * For this to work properly, clip must be set on the canvas prior to drawing.
 *
 * @author petereb1
 */
public class WrappingStringStyle extends BasicStringStyle {

    /** Provides clip boundaries */
    RectangularShape clip;

    public RectangularShape getClip() {
        return clip;
    }

    public void setClip(RectangularShape clip) {
        this.clip = clip;
    }

    @Override
    public void draw(Point2D point, String string, Graphics2D canvas, Set<VisibilityHint> visibility) {
        if (string == null || string.length() == 0) {
            return;
        }
        canvas.setFont(getFont() == null ? canvas.getFont().deriveFont(getFontSize()) : getFont());
        canvas.setColor(StyleUtils.applyHints(getColor(), visibility));
        Shape curClip = canvas.getClip();
        canvas.setClip(clip);
        if (clip instanceof Ellipse2D) {
            drawInEllipse(point, string, canvas, (Ellipse2D) clip);
        } else {
            drawInRectangle(string, canvas, clip.getBounds2D());
        }
        canvas.setClip(curClip);
    }

    private void drawInEllipse(Point2D point, String string, Graphics2D canvas, Ellipse2D clip) {
        Font f = canvas.getFont();
        Rectangle2D bounds = bounds(point, string, canvas);
        if (clip.getWidth() * clip.getHeight() < (f.getSize() * f.getSize() / 1.5) * string.length()
                || clip.getWidth() < f.getSize() * 5) {
            canvas.setFont(f.deriveFont(f.getSize2D() - 2));
        }

        setAnchor(Anchor.Center);
        if (bounds.getWidth() < clip.getWidth() - 8 || clip.getWidth()*.6 < 3 * f.getSize2D()) {
            // entire string fits in box... draw centered
            bounds = bounds(new Point2D.Double(clip.getCenterX(), clip.getCenterY()), string, canvas);
            canvas.drawString(string, (float) bounds.getX(), (float) (bounds.getY()+bounds.getHeight()));
        } else {
            // need to wrap string
            drawInRectangle(string, canvas,
                    new Rectangle2D.Double(clip.getX()+clip.getWidth()*.2, clip.getY()+clip.getHeight()*.2, clip.getWidth()*.6, clip.getHeight()*.6)
                    );
        }
    }

    private void drawInRectangle(String string, Graphics2D canvas, Rectangle2D rClip) {
        // make font smaller if lots of words
        Font f = canvas.getFont();
        if (rClip.getWidth() * rClip.getHeight() < (f.getSize() * f.getSize() / 1.5) * string.length()
                || rClip.getWidth() < f.getSize() * 5) {
            canvas.setFont(f.deriveFont(f.getSize2D() - 2));
        }
        float sz = canvas.getFont().getSize2D();
        List<String> lines = wrappedString(string, canvas, rClip.getWidth(), rClip.getHeight());
        double y0 = rClip.getY();
        switch (anchor) {
            case North: case Northwest: case Northeast:
                y0 += sz+2;
                break;
            case South: case Southwest: case Southeast:
                y0 += rClip.getHeight()-lines.size()*(sz+2)+sz;
                break;
            default:
                y0 += rClip.getCenterY()-(lines.size()/2.0)*(sz+2)+sz;
        }
        for (String s : lines) {
            double wid = canvas.getFontMetrics().getStringBounds(s, canvas).getWidth();
            switch (anchor) {
                case West: case Southwest: case Northwest:
                    canvas.drawString(s, (float)(rClip.getX()+2), (float) y0);
                    break;
                case East: case Southeast: case Northeast:
                    canvas.drawString(s, (float)(rClip.getX()+rClip.getWidth()-wid-2), (float) y0);
                    break;
                default:
                    canvas.drawString(s, (float)(rClip.getX()+rClip.getCenterX()-wid/2.0), (float) y0);
            }
            y0 += sz+2;
        }
    }

    /**
     * Create set of lines representing the word-wrapped version of the string. Words are
     * wrapped at spaces if possible. Lines are constrained to be within given width and height.
     * If the string is too long to fit in the given space, it is truncated and "..." appended.
     * Assumes lines are separated by current font size + 2.
     * @param string initial string
     * @param canvas where string will be drawn (reference needed for font metrics)
     * @param width width of bounding box
     * @param height height of bounding box
     * @return lines
     */
    private static List<String> wrappedString(String string, Graphics2D canvas, double width, double height) {
        float sz = canvas.getFont().getSize2D();
        Rectangle2D sBounds = canvas.getFontMetrics().getStringBounds(string, canvas);

        List<String> lines = new ArrayList<String>();
        if (string.length() == 0) {
            // do nothing
        } else if (width < 3*sz) {
            lines.add(string.substring(0,1)+"...");
        } else if (sBounds.getWidth() <= width - 4) {
            lines.add(string);
        } else {
            // need to wrap string
            double lineHt = sz+2;
            int pos0 = 0;
            int pos1 = 1;
            while (pos1 < string.length()) {
                while (pos1 <= string.length() && canvas.getFontMetrics().getStringBounds(string.substring(pos0,pos1), canvas).getWidth() < width-4) {
                    pos1++;
                }
                if (pos1 < string.length()) {
                    int idx = string.lastIndexOf(' ', pos1 - 1);
                    if (idx > pos0) {
                        pos1 = idx + 2;
                    }
                }
                String s = string.substring(pos0, pos1 - 1);
                lineHt += sz+2;
                if (lineHt >= height-2) {
                    // will be the last line, may need to truncate
                    if (pos1-1 < string.length()) {
                        s = s + "...";
                    }
                    while (s.length() >= 4
                            && canvas.getFontMetrics().getStringBounds(s, canvas).getWidth() > width-4) {
                        s = s.substring(0, s.length() - 4) + "...";
                    }
                    lines.add(s);
                    break;
                } else {
                    lines.add(s);
                }
                pos0 = pos1 - 1;
                pos1 = pos0 + 1;
            }
        }
        return lines;
    }

}
