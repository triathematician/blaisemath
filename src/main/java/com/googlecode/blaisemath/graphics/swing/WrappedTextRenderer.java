/*
 * WrappedTextRenderer.java
 * Created on Jan 2, 2013
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.google.common.base.Strings;
import com.googlecode.blaisemath.style.Anchor;
import static com.googlecode.blaisemath.style.Anchor.EAST;
import static com.googlecode.blaisemath.style.Anchor.NORTH;
import static com.googlecode.blaisemath.style.Anchor.NORTHEAST;
import static com.googlecode.blaisemath.style.Anchor.NORTHWEST;
import static com.googlecode.blaisemath.style.Anchor.SOUTH;
import static com.googlecode.blaisemath.style.Anchor.SOUTHEAST;
import static com.googlecode.blaisemath.style.Anchor.SOUTHWEST;
import static com.googlecode.blaisemath.style.Anchor.WEST;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.geom.LabeledPoint;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws a string within the boundaries of a given clip.
 *
 * @author petereb1
 */
public class WrappedTextRenderer extends TextRenderer {

    /** Provides clip boundaries */
    protected RectangularShape clipPath;

    public WrappedTextRenderer() {
    }
    
    @Override
    public String toString() {
        return String.format("TextStyleWrapped[clip=%s]", clipPath);
    }
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** Sets clip & returns pointer to object */
    public WrappedTextRenderer clipPath(RectangularShape clip) {
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
    public void render(LabeledPoint text, AttributeSet style, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return;
        }
        
        Shape curClip = canvas.getClip();
        Area newClip = new Area();
        newClip.add(new Area(curClip));
        newClip.intersect(new Area(clipPath));
        canvas.setClip(newClip);
        
        // set font size, decreasing it a bit if there's a lot of text relative to the clip area
        Font f = Styles.getFont(style);
        Rectangle2D clipBounds = newClip.getBounds2D();
        if (clipBounds.getWidth() * clipBounds.getHeight() < (f.getSize() * f.getSize() / 1.5) * text.getText().length()
                || clipBounds.getWidth() < f.getSize() * 5) {
            f = f.deriveFont(f.getSize2D() - 2);
        }
        canvas.setFont(f);
        
        canvas.setColor(style.getColor(Styles.FILL));
        if (clipPath instanceof Ellipse2D) {
            drawInEllipse(text.getText(), style, (Ellipse2D) clipPath, canvas);
        } else {
            drawInRectangle(text.getText(), style, clipPath.getBounds2D(), canvas);
        }
        canvas.setClip(curClip);
    }

    private void drawInEllipse(String text, AttributeSet style, Ellipse2D clip, Graphics2D canvas) {
        Rectangle2D bounds = canvas.getFontMetrics().getStringBounds(text, canvas);
        if (bounds.getWidth() < clip.getWidth() - 8 || clip.getWidth()*.6 < 3 * canvas.getFont().getSize2D()) {
            // entire string fits in box... draw centered
            double xText = clip.getCenterX()-bounds.getWidth()/2;
            double yText = clip.getCenterY()+bounds.getHeight()/2;
            super.render(new LabeledPoint(xText, yText, text), style, canvas);
        } else {
            // need to wrap string
            drawInRectangle(text, style,
                    new Rectangle2D.Double(
                    clip.getX()+clip.getWidth()*.15, clip.getY()+clip.getHeight()*.15,
                    clip.getWidth()*.7, clip.getHeight()*.7),
                    canvas
                    );
        }
    }

    private void drawInRectangle(String string, AttributeSet style, Rectangle2D rect, Graphics2D canvas) {
        // make font smaller if lots of words
        Font font = Styles.getFont(style);
        if (rect.getWidth() * rect.getHeight() < (font.getSize() * font.getSize() / 1.5) * string.length()
                || rect.getWidth() < font.getSize() * 5) {
            font = font.deriveFont(font.getSize2D()-2);
        }
        canvas.setFont(font);
        float sz = canvas.getFont().getSize2D();
        List<String> lines = computeLineBreaks(string, font, rect.getWidth(), rect.getHeight());
        double y0;
        
        Anchor textAnchor = Styles.getAnchor(style, Anchor.CENTER);
        switch (textAnchor) {
            case NORTH: 
                // fall through
            case NORTHWEST: 
                // fall through
            case NORTHEAST:
                y0 = rect.getY()+sz+2;
                break;
            case SOUTH: 
                // fall through
            case SOUTHWEST: 
                // fall through
            case SOUTHEAST:
                y0 = rect.getMaxY()-lines.size()*(sz+2)+sz;
                break;
            default:
                y0 = rect.getCenterY()-(lines.size()/2.0)*(sz+2)+sz;
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
                    canvas.drawString(s, (float)(rect.getX()+2), (float) y0);
                    break;
                case EAST: 
                // fall through
                case SOUTHEAST: 
                // fall through
                case NORTHEAST:
                    canvas.drawString(s, (float)(rect.getMaxX()-wid-2), (float) y0);
                    break;
                default:
                    canvas.drawString(s, (float)(rect.getCenterX()-wid/2.0), (float) y0);
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
     * @param font the font to be drawn in
     * @param width width of bounding box
     * @param height height of bounding box
     * @return lines
     */
    public static List<String> computeLineBreaks(String string, Font font, double width, double height) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D sBounds = font.getStringBounds(string, frc);

        List<String> lines = new ArrayList<String>();
        if (string.length() == 0) {
            // do nothing
        } else if (width < 3*font.getSize()) {
            // if really small, show only first character
            lines.add(string.substring(0,1)+"...");
        } else if (sBounds.getWidth() <= width-4 && !string.contains("\n")) {
            // enough to fit the entire string
            lines.add(string);
        } else {
            // need to wrap string
            double totHt = font.getSize()+2;
            int pos0 = 0;
            int pos1 = 1;
            while (pos1 < string.length()) {
                while (pos1 <= string.length() && string.charAt(pos1-1) != '\n' 
                        && font.getStringBounds(string.substring(pos0,pos1), frc).getWidth() < width-4) {
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
                totHt += font.getSize()+2;
                if (totHt >= height-2) {
                    // will be the last line, may need to truncate
                    if (pos1-1 < string.length()) {
                        s += "...";
                    }
                    while (s.length() >= 4
                            && font.getStringBounds(s, frc).getWidth() > width-4) {
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
