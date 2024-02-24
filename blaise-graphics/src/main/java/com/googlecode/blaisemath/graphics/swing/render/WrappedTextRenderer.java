package com.googlecode.blaisemath.graphics.swing.render;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import com.googlecode.blaisemath.primitive.StyledText;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.primitive.Anchor;
import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Draws a string within the boundaries of a given clip. The string is wrapped at word breaks as needed to stay within
 * the clip. It is truncated if necessary, and ellipsis (...) used to indicate truncation. When providing an anchor with
 * the style, the anchor positions the text inside the clip path relative to the anchor. So if the text is drawn in
 * a rectangle and the anchor is CENTER, the text will be drawn at the center of the rectangle; if the anchor is WEST,
 * the text will be drawn centered vertically and right-aligned next to the right boundary of the rectangle.
 *
 * @author Elisha Peterson
 */
public class WrappedTextRenderer extends TextRenderer {

    /** Provides clip boundaries */
    protected RectangularShape clipPath;
    /** Insets used for text anchoring. */
    private Insets insets = defaultInsets();
    /** Flag to show text in a circle/ellipse all on a single line (no wrapping) if not enough space */
    private boolean allowFullTextOnCircle = false;
    /** Minimum width factor (multiple of font size) at which to abbreviate attempt to render text, and just show first character */
    private int minWidthFactor = 2;
    /** Maximum number of points to reduce font size by for smaller rectangles. Set to 0 to keep font size the same. Defaults to 2. */
    private int maxReduceFontSize = 2;
    
    @Override
    public String toString() {
        return String.format("WrappedTextRenderer[clip=%s]", clipPath);
    }
    
    //region BUILDER PATTERNS

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
    
    //endregion

    //region PROPERTIES
    
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

    public boolean isAllowFullTextOnCircle() {
        return allowFullTextOnCircle;
    }

    public void setAllowFullTextOnCircle(boolean allowFullTextOnCircle) {
        this.allowFullTextOnCircle = allowFullTextOnCircle;
    }

    public int getMaxReduceFontSize() {
        return maxReduceFontSize;
    }

    public void setMaxReduceFontSize(int maxReduceFontSize) {
        this.maxReduceFontSize = maxReduceFontSize;
    }

    public int getMinWidthFactor() {
        return minWidthFactor;
    }

    public void setMinWidthFactor(int minWidthFactor) {
        this.minWidthFactor = minWidthFactor;
    }

    //endregion
    
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

    @Override
    public Rectangle2D boundingBox(AnchoredText text, AttributeSet style, @Nullable Graphics2D canvas) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return null;
        }
        Iterable<StyledText> lines = computeLines(text.getText(), style, clipPath, getInsets(), canvas);
        Rectangle2D res = null;
        for (StyledText t : lines) {
            Rectangle2D box = TextRenderer.getInstance().boundingBox(t.getText(), t.getStyle(), canvas);
            res = res == null ? box : res.createUnion(box);
        }
        return res;
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
    public Iterable<StyledText> computeLines(String text, AttributeSet style, Shape textBounds, Insets insets, @Nullable Graphics2D canvas) {
        if (canvas == null) {
            canvas = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics();
            canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }

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

    //region COMPUTE LINE BREAKS

    private List<StyledText> computeEllipseLines(String text, AttributeSet style, Ellipse2D ell, Graphics2D canvas) {
        canvas.setFont(Styles.fontOf(style));
        Rectangle2D bounds = canvas.getFontMetrics().getStringBounds(text, canvas);
        
        AttributeSet centeredStyle = AttributeSet.withParent(style).and(Styles.TEXT_ANCHOR, Anchor.CENTER);
        boolean showOnOneLine = allowFullTextOnCircle && (bounds.getWidth() < ell.getWidth() - 8 || ell.getWidth()*.6 < 3 * canvas.getFont().getSize2D());
        if (showOnOneLine) {
            return Collections.singletonList(new StyledText(new AnchoredText(ell.getCenterX(), ell.getCenterY(), text), centeredStyle));
        } else {
            return computeRectangleLines(text, centeredStyle,
                    new Rectangle2D.Double(
                            ell.getX() + ell.getWidth() * .15, ell.getY() + ell.getHeight() * .15,
                            ell.getWidth() * .7, ell.getHeight() * .7),
                    canvas
            );
        }
    }

    private List<StyledText> computeRectangleLines(String text, AttributeSet style, Rectangle2D rect, Graphics2D canvas) {
        // make font smaller if lots of words
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        Font font = Styles.fontOf(style);
        
        if (maxReduceFontSize > 0) {
            int fontSize = font.getSize();
            // will reduce font size for narrow rectangles
            boolean narrowRectangle = rect.getWidth() < fontSize * 5;
            // will reduce font size for smaller rectangles
            double areaRatio = rect.getWidth() * rect.getHeight() / ((fontSize * fontSize / 1.5) * text.length());
            // reduce font size
            if (areaRatio < 1) {
                float newFontSize = (float) Math.max(font.getSize2D() - maxReduceFontSize, font.getSize2D() * areaRatio);
                font = font.deriveFont(newFontSize);
            } else if (narrowRectangle) {
                font = font.deriveFont(font.getSize2D() - Math.min(2, maxReduceFontSize));
            }
            canvas.setFont(font);
        }
        
        List<String> lines = computeLineBreaks(text, font, rect.getWidth(), rect.getHeight());
        Anchor textAnchor = Styles.anchorOf(style, Anchor.CENTER);
        float sz = canvas.getFont().getSize2D();
        double y0 = getInitialY(textAnchor, rect, sz, lines.size());
        
        List<StyledText> res = Lists.newArrayList();
        AttributeSet plainStyle = style.flatCopy();
        plainStyle.put(Styles.FONT_SIZE, font.getSize2D());
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
    public List<String> computeLineBreaks(String string, Font font, double width, double height) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D sBounds = font.getStringBounds(string, frc);

        List<String> lines = new ArrayList<>();
        int length = string.length();
        if (length == 0) {
            // do nothing
        } else if (width < minWidthFactor*font.getSize()) {
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
            while (pos1 <= string.length()) {
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

    //endregion

}
