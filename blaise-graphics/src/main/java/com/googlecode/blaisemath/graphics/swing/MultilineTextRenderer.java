/*
 * MultilineTextRenderer.java
 * Created on Jan 2, 2013
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

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
        Font font = Styles.fontOf(style);       
        canvas.setFont(font);
        canvas.setColor(style.getColor(Styles.FILL));
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        FontRenderContext frc = canvas.getFontRenderContext();
        Anchor textAnchor = Styles.anchorOf(style, Anchor.SOUTHWEST);
        
        double lineHeight = font.getLineMetrics("", frc).getHeight();
        Rectangle2D bounds = boundingBox(text, style);  
        Point2D offset = style.getPoint(Styles.OFFSET, new Point());
        assert offset != null;
        double x0 = text.getX() + offset.getX();
        double y0 = bounds.getMaxY();
        for (String line : Lists.reverse(Arrays.asList(lines(text)))) {
            double wid = font.getStringBounds(line, frc).getWidth();
            double dx = textAnchor.getRectOffset(wid, 0).getX();
            canvas.drawString(line, (float)(x0+dx), (float) y0);
            y0 -= lineHeight;
        }
    }

    @Override
    public Rectangle2D boundingBox(AnchoredText text, AttributeSet style) {
        return boundingBox(text, style, null);
    }
    
    public Rectangle2D boundingBox(AnchoredText text, AttributeSet style, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(text.getText())) {
            return null;
        }

        Font font = Styles.fontOf(style);
        if (canvas != null) {
            canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }
        FontRenderContext frc = canvas == null ? new FontRenderContext(font.getTransform(), true, false)
                : canvas.getFontRenderContext();
        
        double width = 0;
        String[] lines = lines(text);
        for (String line : lines) {
            width = Math.max(width, font.getStringBounds(line, frc).getWidth());
        }
        double lineHeight = font.getLineMetrics("", frc).getHeight();
        double height = lineHeight*lines.length;
        height -= (lineHeight - font.getSize()*72/Toolkit.getDefaultToolkit().getScreenResolution());
        
        Anchor textAnchor = Styles.anchorOf(style, Anchor.SOUTHWEST);       
        Point2D offset = style.getPoint(Styles.OFFSET, new Point());
        assert offset != null;
        Point2D shift = textAnchor.getRectOffset(width, height);
        return new Rectangle2D.Double(
                text.getX() + offset.getX() + shift.getX(), 
                text.getY() + offset.getY() + shift.getY()-height, 
                width, height);
    }
    
}
