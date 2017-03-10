/*
 * WrappedTextRenderer.java
 * Created on Jan 2, 2013
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
import com.google.common.collect.Lists;
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
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Draws a string within the boundaries of a given clip. The string is wrapped
 * at word breaks as needed to stay within the clip. It is truncated if necessary,
 * and ellipsis (...) used to indicate truncation.
 *
 * @author petereb1
 */
public class WrappedTextRenderer extends TextRenderer {

    /** Provides clip boundaries */
    protected RectangularShape clipPath;
    /** Insets used for text anchoring. */
    private Insets insets = defaultInsets();

    public WrappedTextRenderer() {
    }
    
    @Override
    public String toString() {
        return String.format("WrappedTextRenderer[clip=%s]", clipPath);
    }
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** 
     * Sets clip and returns pointer to object
     * @param clip the clip to use
     * @return this
     */
    public WrappedTextRenderer clipPath(RectangularShape clip) {
        setTextBounds(clip);
        return this; 
    }

    /** 
     * Sets insets and returns pointer to object
     * @param insets the insets to use
     * @return this
     */
    public WrappedTextRenderer insets(Insets insets) {
        this.insets = insets;
        return this;
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public RectangularShape getTextBounds() {
        return clipPath;
    }

    public void setTextBounds(RectangularShape clip) {
        this.clipPath = clip;
    }

    public Insets getInsets() {
        return insets;
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    //</editor-fold>
    
    
    @Override
    public void render(AnchoredText text, AttributeSet style, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return;
        }
        
        Iterable<StyledText> lines = computeLines(text.getText(), style, clipPath, insets, canvas);
        for (StyledText t : lines) {
            TextRenderer.getInstance().render(t.getText(), t.getStyle(), canvas);
        }
    }
    
    /**
     * Default insets used for wrapping text.
     * @return insets
     */
    public static Insets defaultInsets() {
        return new Insets(2, 2, 2, 2);
    }
    
    /**
     * Use the provided input text to compute locations for text by wrapping text
     * as appropriate for the given clip.
     * @param text the text to wrap
     * @param style text style
     * @param textBounds bounding box for text
     * @param insets insets for text inside box
     * @param canvas target canvas
     * @return text to render
     */
    public static Iterable<StyledText> computeLines(String text, AttributeSet style, Shape textBounds, Insets insets, Graphics2D canvas) {
        Rectangle2D bounds = textBounds.getBounds2D();
        if (textBounds instanceof Ellipse2D) {
            Ellipse2D textClip = new Ellipse2D.Double(
                    bounds.getMinX()+insets.left, bounds.getMinY()+insets.top,
                    bounds.getWidth()-insets.left-insets.right, bounds.getHeight()-insets.top-insets.bottom);
            return computeEllipseLines(text, style, textClip, canvas);
        } else {
            Rectangle2D textClip = new Rectangle2D.Double(
                    bounds.getMinX()+insets.left, bounds.getMinY()+insets.top,
                    bounds.getWidth()-insets.left-insets.right, bounds.getHeight()-insets.top-insets.bottom);
            return computeRectangleLines(text, style, textClip, canvas);
        }
    }

    private static List<StyledText> computeEllipseLines(String text, AttributeSet style, Ellipse2D ell, Graphics2D canvas) {
        Rectangle2D bounds = canvas.getFontMetrics().getStringBounds(text, canvas);
        AttributeSet centeredStyle = AttributeSet.createWithParent(style).and(Styles.TEXT_ANCHOR, Anchor.CENTER);
        
        if (bounds.getWidth() < ell.getWidth() - 8 || ell.getWidth()*.6 < 3 * canvas.getFont().getSize2D()) {
            return Arrays.asList(new StyledText(new AnchoredText(ell.getCenterX(), ell.getCenterY(), text), centeredStyle));
        } else {
            return computeRectangleLines(text, centeredStyle,
                    new Rectangle2D.Double(
                    ell.getX()+ell.getWidth()*.15, ell.getY()+ell.getHeight()*.15,
                    ell.getWidth()*.7, ell.getHeight()*.7),
                    canvas
                    );
        }
    }

    private static List<StyledText> computeRectangleLines(String text, AttributeSet style, Rectangle2D rect, Graphics2D canvas) {
        // make font smaller if lots of words
        Font font = Styles.fontOf(style);
        if (rect.getWidth() * rect.getHeight() < (font.getSize() * font.getSize() / 1.5) * text.length()
                || rect.getWidth() < font.getSize() * 5) {
            font = font.deriveFont(font.getSize2D()-2);
        }
        canvas.setFont(font);
        
        List<String> lines = computeLineBreaks(text, font, rect.getWidth(), rect.getHeight());
        Anchor textAnchor = Styles.anchorOf(style, Anchor.CENTER);
        float sz = canvas.getFont().getSize2D();
        double y0 = getInitialY(textAnchor, rect, sz, lines.size());
        
        List<StyledText> res = Lists.newArrayList();
        AttributeSet plainStyle = style.copy();
        plainStyle.remove(Styles.ALIGN_BASELINE);
        plainStyle.remove(Styles.TEXT_ANCHOR);
        plainStyle.remove(Styles.OFFSET);
        for (String s : lines) {
            double wid = canvas.getFontMetrics().getStringBounds(s, canvas).getWidth();
            switch (textAnchor) {
                case WEST: 
                // fall through
                case SOUTHWEST: 
                // fall through
                case NORTHWEST:
                    res.add(new StyledText(new AnchoredText(rect.getX(), y0, s), plainStyle));
                    break;
                case EAST: 
                // fall through
                case SOUTHEAST: 
                // fall through
                case NORTHEAST:
                    res.add(new StyledText(new AnchoredText(rect.getMaxX()-wid, y0, s), plainStyle));
                    break;
                default:
                    // x-centered
                    res.add(new StyledText(new AnchoredText(rect.getCenterX()-wid/2.0, y0, s), plainStyle));
                    break;
            }
            y0 += sz+2;
        }
        
        return res;
    }

    /** Computes the starting y location for subsequent lines of text. */
    private static double getInitialY(Anchor textAnchor, Rectangle2D rect, float fontSize, int lineCount) {
        switch (textAnchor) {
            case NORTH: 
                // fall through
            case NORTHWEST: 
                // fall through
            case NORTHEAST:
                return rect.getY()+fontSize;
            case SOUTH: 
                // fall through
            case SOUTHWEST: 
                // fall through
            case SOUTHEAST:
                return rect.getMaxY()-(lineCount-1)*(fontSize+2);
            default:
                // y-centered
                return rect.getCenterY()-(lineCount/2.0)*(fontSize+2)+fontSize;
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
        int length = string.length();
        if (length == 0) {
            // do nothing
        } else if (width < 3*font.getSize()) {
            // if really small, show only first character
            lines.add((length <= 2 ? string.substring(0,length) : string.substring(0,1)+"...").trim());
        } else if (sBounds.getWidth() <= width-4 && !string.contains("\n")) {
            // enough to fit the entire string
            lines.add(string.trim());
        } else {
            // need to wrap string
            double totHt = (double) font.getSize()+2;
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
                    lines.add(s.trim());
                    break;
                } else {
                    lines.add(s.trim());
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

    /** Augments {@link AnchoredText} with style information. */
    public static class StyledText {
        private final AnchoredText text;
        private final AttributeSet style;

        public StyledText(AnchoredText text, AttributeSet style) {
            this.text = text;
            this.style = style;
        }

        public AnchoredText getText() {
            return text;
        }

        public AttributeSet getStyle() {
            return style;
        }
    }
    
}
