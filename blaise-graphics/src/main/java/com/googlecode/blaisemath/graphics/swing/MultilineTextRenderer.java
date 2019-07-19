package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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
import com.googlecode.blaisemath.geom.Anchor;
import com.googlecode.blaisemath.geom.AnchoredText;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.graphics.Renderer;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Draw text on multiple lines, using line breaks provided with the text. By default, the text is anchored at the upper
 * left, so that text is drawn to the right and below the anchor point. For alternate anchors, all lines of text are
 * positioned in the same way, and the text may be centered, left-aligned, or right-aligned, depending on the anchor.
 *
 * @author Elisha Peterson
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
    public boolean contains(Point2D point, AnchoredText primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        return boundingBox(primitive, style, canvas).contains(point);
    }

    @Override
    public boolean intersects(Rectangle2D rect, AnchoredText primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        return boundingBox(primitive, style, canvas).intersects(rect);
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
        Rectangle2D bounds = boundingBox(text, style, canvas);
        Point2D offset = style.getPoint2D(Styles.OFFSET, new Point());
        assert offset != null;
        double x0 = bounds.getMinX();
        double y0 = bounds.getMaxY();
        for (String line : Lists.reverse(Arrays.asList(lines(text)))) {
            double wid = font.getStringBounds(line, frc).getWidth();
            double dx = textAnchor.offsetForRectangle(bounds.getWidth() - wid, 0).getX()
                    + 0.5*(bounds.getWidth() - wid);
            canvas.drawString(line, (float)(x0+dx), (float) y0);
            y0 -= lineHeight;
        }
    }

    @Override
    public Rectangle2D boundingBox(AnchoredText text, AttributeSet style, @Nullable Graphics2D canvas) {
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
        height -= (lineHeight - font.getSize()*72.0/Toolkit.getDefaultToolkit().getScreenResolution());
        
        Anchor textAnchor = Styles.anchorOf(style, Anchor.NORTHWEST);
        Point2D offset = style.getPoint2D(Styles.OFFSET, new Point());
        assert offset != null;
        return textAnchor.rectangleAnchoredAt(text.getX() + offset.getX(), text.getY() + offset.getY(),
                width, height);
    }
    
}
