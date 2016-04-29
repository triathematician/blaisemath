/*
 * MultilineTextRenderer.java
 * Created on Jan 2, 2013
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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
import static com.googlecode.blaisemath.graphics.swing.TextRenderer.anchorFromStyle;
import static com.googlecode.blaisemath.graphics.swing.TextRenderer.anchorShift;
import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * <p>
 *   Draw text on multiple lines, using standard line breaks.
 *   The text is left-aligned, but shifted so that the anchor point is at the
 *   associated location of the text's bounding box.
 * </p>
 * 
 * @author petereb1
 */
public class MultilineTextRenderer implements Renderer<AnchoredText, Graphics2D> {
    private static final Logger LOG = Logger.getLogger(MultilineTextRenderer.class.getName());

    private static final MultilineTextRenderer INST = new MultilineTextRenderer();
    
    public static Renderer<AnchoredText, Graphics2D> getInstance() {
        return INST;
    }
    
    @Override
    public String toString() {
        return "MultilineTextRenderer";
    }

    @Override
    public boolean contains(AnchoredText primitive, AttributeSet style, Point2D point) {
        return boundingBox(primitive, style).contains(point);
    }

    @Override
    public boolean intersects(AnchoredText primitive, AttributeSet style, Rectangle2D rect) {
        return boundingBox(primitive, style).intersects(rect);
    }
    
    private static String[] lines(AnchoredText text) {
        return text.getText().split("\n|\r\n");
    }

    @Override
    public void render(AnchoredText text, AttributeSet style, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return;
        }
        Font font = Styles.getFont(style);       
        canvas.setFont(font);
        canvas.setColor(style.getColor(Styles.FILL));

        FontRenderContext frc = new FontRenderContext(font.getTransform(), true, true);
        Anchor textAnchor = anchorFromStyle(style);   
        
        double lineHeight = canvas.getFontMetrics().getHeight();
        Rectangle2D bounds = boundingBox(text, style);
        double y0 = bounds.getMaxY();
        for (String line : Lists.reverse(Arrays.asList(lines(text)))) {
            double wid = font.getStringBounds(line, frc).getWidth();
            double dx = anchorShift(textAnchor, wid, 0).x;
            canvas.drawString(line, (float)(text.getX()+dx), (float) y0);
            y0 -= lineHeight;
        }
    }

    @Override
    public Rectangle2D boundingBox(AnchoredText text, AttributeSet style) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return null;
        }

        Font font = Styles.getFont(style);
        FontRenderContext frc = new FontRenderContext(font.getTransform(), true, true);
        
        double width = 0;
        double height = 0;
        double leading = 0;
        for (String line : lines(text)) {
            TextLayout tl = new TextLayout(text.getText(), font, frc);
            width = Math.max(width, font.getStringBounds(line, frc).getWidth());
            height += tl.getAscent()+tl.getDescent()+tl.getLeading();
            leading = tl.getLeading();
        }
        height -= leading;
        
        Anchor textAnchor = anchorFromStyle(style);        
        Point2D offset = style.getPoint(Styles.OFFSET, new Point());
        assert offset != null;
        if (textAnchor == Anchor.SOUTHWEST) {
            return new Rectangle2D.Double(
                    text.getX() + offset.getX(), 
                    text.getY() + offset.getY()-height, 
                    width, height);
        }

        Point2D.Double shift = anchorShift(textAnchor, width, height);

        return new Rectangle2D.Double(
                text.getX() + offset.getX() + shift.x, 
                text.getY() + offset.getY() + shift.y-height, 
                width, height);
    }
    
}
