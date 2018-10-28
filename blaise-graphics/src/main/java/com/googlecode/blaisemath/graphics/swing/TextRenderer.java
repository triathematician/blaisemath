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
import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Renders a string of text on a canvas. An anchor is used to position the text relative to a point. The default anchor
 * is SOUTHWEST, with the text drawn to the right/above the point.
 * 
 * @author Elisha Peterson
 */
public class TextRenderer implements Renderer<AnchoredText, Graphics2D> {
    
    /** Assumed monitor resolution, used in bounding box calculations */
    private static final int DOTS_PER_INCH = 72;
    private static final Logger LOG = Logger.getLogger(TextRenderer.class.getName());
    private static final TextRenderer INST = new TextRenderer();
    
    /**
     * Get default static instance of the renderer.
     * @return renderer
     */
    public static TextRenderer getInstance() {
        return INST;
    }
    
    @Override
    public void render(AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        render(Collections.singleton(primitive), style, canvas);
    }
    
    /**
     * Render a collection of text primitives at one time.
     * @param primitives the primitives to render
     * @param style the style used for rendering
     * @param canvas where to render it
     */
    public void render(Iterable<AnchoredText> primitives, AttributeSet style, Graphics2D canvas) {
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        canvas.setColor(style.getColor(Styles.FILL, Color.black));
        canvas.setFont(Styles.fontOf(style));
        
        for (AnchoredText at : primitives) {
            if (!Strings.isNullOrEmpty(at.getText())) {
                Rectangle2D bounds = boundingBox(at, style, canvas);
                canvas.drawString(at.getText(), (float) bounds.getX(), (float) (bounds.getMaxY()));
            }
        }
    }

    @Override
    public boolean contains(Point2D point, AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        Rectangle2D bounds = boundingBox(primitive, style, canvas);
        return bounds != null && bounds.contains(point);
    }

    @Override
    public boolean intersects(Rectangle2D rect, AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        Rectangle2D bounds = boundingBox(primitive, style, canvas);
        return bounds != null && bounds.intersects(rect);
    }

    @Override
    public @Nullable Rectangle2D boundingBox(AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(primitive.getText())) {
            return null;
        }
        
        Font font = Styles.fontOf(style);
        FontRenderContext frc = canvas == null ? new FontRenderContext(font.getTransform(), true, false)
                : canvas.getFontRenderContext();
        double width = font.getStringBounds(primitive.getText(), frc).getWidth();
        double height = font.getSize()*DOTS_PER_INCH/Toolkit.getDefaultToolkit().getScreenResolution();
        
        Anchor textAnchor = Styles.anchorOf(style, Anchor.SOUTHWEST);
        Point2D offset = style.getPoint2D(Styles.OFFSET, new Point());
        assert offset != null;
        return textAnchor.rectangleAnchoredAt(
                primitive.getX() + offset.getX(),
                primitive.getY() + offset.getY(),
                width, height);
    }
    
}
