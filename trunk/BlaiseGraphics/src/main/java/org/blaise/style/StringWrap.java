/*
 * StringWrap.java
 * Created on Jan 2, 2013
 */

package org.blaise.style;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Tools for wrapping strings to fit within a shape.
 *
 * @author petereb1
 */
public class StringWrap {

    private static final int MIN_ELLIPSE_MARGIN = 3;

    /**
     * Draws string within a circle/ellipse.
     * @param s string to draw
     * @param a anchor point
     * @param f font for draw
     * @param canvas graphics canvas
     * @param clip shape to draw in
     */
    public void drawInEllipse(String s, Anchor a, Font f, Graphics2D canvas, Ellipse2D clip) {
        // set up font
        canvas.setFont(f);
        float sz = f.getSize();
        FontMetrics fm = canvas.getFontMetrics();

        // if possible, include entire string in center of ellipse
        Rectangle2D sRect = fm.getStringBounds(s, canvas);
        if (sRect.getWidth() < clip.getWidth()-2*MIN_ELLIPSE_MARGIN) {
            // todo
            float x = -1;
            float y = -1;
            canvas.drawString(s, x, y);
            return;
        }

        // otherwise, wrap string
    }

    private static final int MIN_RECT_MARGIN = 3;

    /**
     * Draws string within a rectangle.
     * @param s string to draw
     * @param a anchor point
     * @param f font for draw
     * @param canvas graphics canvas
     * @param clip shape to draw in
     */
    public void drawInRectangle(String s, Anchor a, Font f, Graphics2D canvas, Rectangle2D clip) {
        // set up font
        canvas.setFont(f);
        float sz = f.getSize();
        FontMetrics fm = canvas.getFontMetrics();

        // if possible, include entire string in center of rectangle
        Rectangle2D sRect = fm.getStringBounds(s, canvas);
        if (sRect.getWidth() < clip.getWidth()-2*MIN_RECT_MARGIN) {
            // todo
            float x = -1;
            float y = -1;
            canvas.drawString(s, x, y);
            return;
        }

        // otherwise, wrap string
        List<Integer> wrapPos = new ArrayList<Integer>();
        
    }

}
