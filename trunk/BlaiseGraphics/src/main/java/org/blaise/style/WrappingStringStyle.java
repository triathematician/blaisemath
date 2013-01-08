/*
 * WrappingStringStyle.java
 * Created on Jan 2, 2013
 */

package org.blaise.style;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
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
            drawInRectangle(point, string, canvas, clip.getBounds2D());
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
        float sz = canvas.getFont().getSize2D();

        if (bounds.getWidth() < clip.getWidth() - 8 || clip.getWidth() < 3 * f.getSize2D()) {
            // entire string fits in box... draw centered
            setAnchor(Anchor.Center);
            bounds = bounds(new Point2D.Double(clip.getCenterX(), clip.getCenterY()), string, canvas);
            canvas.drawString(string, (float) bounds.getX(), (float) (bounds.getY()+bounds.getHeight()));
        } else {
            // need to wrap string
            drawInRectangle(point, string, canvas,
                    new Rectangle2D.Double(clip.getX()+clip.getWidth()*.2, clip.getY()+clip.getHeight()*.2, clip.getWidth()*.6, clip.getHeight()*.6)
                    );
        }
    }

    private void drawInRectangle(Point2D point, String string, Graphics2D canvas, Rectangle2D rClip) {
        // make font smaller if lots of words
        Font f = canvas.getFont();
        Rectangle2D bounds = bounds(new Point2D.Double(rClip.getMinX()+2, rClip.getMinY()+2), string, canvas);
        if (rClip.getWidth() * rClip.getHeight() < (f.getSize() * f.getSize() / 1.5) * string.length()
                || rClip.getWidth() < f.getSize() * 5) {
            canvas.setFont(f.deriveFont(f.getSize2D() - 2));
        }
        float sz = canvas.getFont().getSize2D();

        if (bounds.getWidth() < rClip.getWidth() - 4 || rClip.getWidth() < 3 * f.getSize2D()) {
            // entire string fits in box
            canvas.drawString(string, (float) bounds.getX(), (float) bounds.getMaxY());
        } else {
            // need to wrap string
            float y0 = (float) bounds.getMaxY();
            float y = y0;
            int pos0 = 0;
            int pos1 = 1;
            while (pos1 < string.length()) {
                while (pos1 <= string.length() && bounds(point, string.substring(pos0, pos1), canvas).getWidth() < rClip.getWidth() - 4) {
                    pos1++;
                }
                if (pos1 < string.length()) {
                    int idx = string.lastIndexOf(' ', pos1 - 1);
                    if (idx > pos0) {
                        pos1 = idx + 2;
                    }
                }
                float stry = y;
                String s = string.substring(pos0, pos1 - 1);
                y += sz + 2;
                if (y - y0 >= rClip.getHeight() - sz - 4) {
                    s = s + "...";
                    double width = canvas.getFontMetrics().getStringBounds(s, canvas).getWidth();
                    while (width > canvas.getClipBounds().getWidth() - 4) {
                        if (s.length() == 3) {
                            break;
                        }
                        s = s.substring(0, s.length() - 4) + "...";
                        width = canvas.getFontMetrics().getStringBounds(s, canvas).getWidth();
                    }
                    //                        System.out.println("clipping remainder of line: " + string.substring(pos1-1) + "  /  " + string);
                    canvas.drawString(s, (float) bounds.getX(), stry);
                    break;
                } else {
                    canvas.drawString(s, (float) bounds.getX(), stry);
                }
                pos0 = pos1 - 1;
                pos1 = pos0 + 1;
            }
        }
    }

}
