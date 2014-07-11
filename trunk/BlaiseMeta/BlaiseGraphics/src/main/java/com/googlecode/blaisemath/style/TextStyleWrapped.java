/*
 * TextStyleWrapped.java
 * Created on Jan 2, 2013
 */

package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws a string within clipping boundaries defined by a graphics canvas.
 * For this to work properly, clip must be set on the canvas prior to drawing.
 *
 * @author petereb1
 */
public class TextStyleWrapped extends TextStyleBasic {

    /** Provides clip boundaries */
    protected RectangularShape clipPath;

    public TextStyleWrapped() {
    }
    
    @Override
    public String toString() {
        return String.format("TextStyleWrapped[clip=%s, color=%s, font=%s, offset=(%.1f,%.1f), anchor=%s]", 
                clipPath, fill, font, offset.getX(), offset.getY(), textAnchor);
    }
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** Sets clip & returns pointer to object */
    public TextStyleWrapped clipPath(RectangularShape clip) {
        setClipPath(clip);
        return this; 
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public RectangularShape getClipPath() {
        return clipPath;
    }

    public void setClipPath(RectangularShape clip) {
        this.clipPath = clip;
    }

    //</editor-fold>
    
    
    @Override
    public void draw(Point2D point, String string, Graphics2D canvas) {
        if (string == null || string.length() == 0) {
            return;
        }
        canvas.setFont(getFont() == null ? canvas.getFont().deriveFont(getFontSize()) : getFont());
        canvas.setColor(getFill());
        Shape curClip = canvas.getClip();
        Area newClip = new Area();
        newClip.add(new Area(curClip));
        newClip.intersect(new Area(clipPath));
        canvas.setClip(newClip);
        if (clipPath instanceof Ellipse2D) {
            drawInEllipse(point, string, canvas, (Ellipse2D) clipPath);
        } else {
            drawInRectangle(string, canvas, clipPath.getBounds2D());
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

        setTextAnchor(Anchor.CENTER);
        if (bounds.getWidth() < clip.getWidth() - 8 || clip.getWidth()*.6 < 3 * f.getSize2D()) {
            // entire string fits in box... draw centered
            bounds = bounds(new Point2D.Double(clip.getCenterX(), clip.getCenterY()), string, canvas);
            canvas.drawString(string, (float) bounds.getX(), (float) (bounds.getY()+bounds.getHeight()));
        } else {
            // need to wrap string
            drawInRectangle(string, canvas,
                    new Rectangle2D.Double(
                    clip.getX()+clip.getWidth()*.15, clip.getY()+clip.getHeight()*.15,
                    clip.getWidth()*.7, clip.getHeight()*.7)
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
        List<String> lines = computeLineBreaks(string, canvas, rClip.getWidth(), rClip.getHeight());
        double y0;
        switch (textAnchor) {
            case NORTH: 
                // fall through
            case NORTHWEST: 
                // fall through
            case NORTHEAST:
                y0 = rClip.getY()+sz+2;
                break;
            case SOUTH: 
                // fall through
            case SOUTHWEST: 
                // fall through
            case SOUTHEAST:
                y0 = rClip.getMaxY()-lines.size()*(sz+2)+sz;
                break;
            default:
                y0 = rClip.getCenterY()-(lines.size()/2.0)*(sz+2)+sz;
                    break;
        }
        for (String s : lines) {
            double wid = canvas.getFontMetrics().getStringBounds(s, canvas).getWidth();
            switch (textAnchor) {
                case WEST: 
                // fall through
                case SOUTHWEST: 
                // fall through
                case NORTHWEST:
                    canvas.drawString(s, (float)(rClip.getX()+2), (float) y0);
                    break;
                case EAST: 
                // fall through
                case SOUTHEAST: 
                // fall through
                case NORTHEAST:
                    canvas.drawString(s, (float)(rClip.getMaxX()-wid-2), (float) y0);
                    break;
                default:
                    canvas.drawString(s, (float)(rClip.getCenterX()-wid/2.0), (float) y0);
                    break;
            }
            y0 += sz+2;
        }
    }

    /**
     * Create set of lines representing the word-wrapped version of the string. Words are
     * wrapped at spaces if possible, and always wrapped at line breaks. Lines are constrained to be within given width and height.
     * If the string is too long to fit in the given space, it is truncated and "..." appended.
     * Assumes lines are separated by current font size + 2.
     * @param string initial string
     * @param canvas where string will be drawn (reference needed for font metrics)
     * @param width width of bounding box
     * @param height height of bounding box
     * @return lines
     */
    public static List<String> computeLineBreaks(String string, Graphics2D canvas, double width, double height) {
        float sz = canvas.getFont().getSize2D();
        Rectangle2D sBounds = canvas.getFontMetrics().getStringBounds(string, canvas);

        List<String> lines = new ArrayList<String>();
        if (string.length() == 0) {
            // do nothing
        } else if (width < 3*sz) {
            // if really small, show only first character
            lines.add(string.substring(0,1)+"...");
        } else if (sBounds.getWidth() <= width-4 && !string.contains("\n")) {
            // enough to fit the entire string
            lines.add(string);
        } else {
            // need to wrap string
            double totHt = sz+2;
            int pos0 = 0;
            int pos1 = 1;
            while (pos1 < string.length()) {
                while (pos1 <= string.length() && string.charAt(pos1-1) != '\n' && canvas.getFontMetrics().getStringBounds(string.substring(pos0,pos1), canvas).getWidth() < width-4) {
                    pos1++;
                }
                if (pos1 >= string.length()) {
                    pos1 = string.length()+1;
                } else if (string.charAt(pos1-1)=='\n') {
                    // wrap at the line break
                } else {
                    // wrap at the previous space
                    int idx = string.lastIndexOf(' ', pos1 - 1);
                    if (idx > pos0) {
                        pos1 = idx + 2;
                    }
                }
                String s = string.substring(pos0, pos1 - 1);
                totHt += sz+2;
                if (totHt >= height-2) {
                    // will be the last line, may need to truncate
                    if (pos1-1 < string.length()) {
                        s += "...";
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
                if (pos0 < string.length() && string.charAt(pos0)=='\n') {
                    pos0++;
                }
                pos1 = pos0 + 1;
            }
        }
        return lines;
    }

}
