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

import com.googlecode.blaisemath.geom.Anchor;
import com.googlecode.blaisemath.geom.AnchoredImage;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.graphics.core.Renderer;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Renderer for drawing images on a canvas. Anchor is used to position the icon relative to a point. The default anchor
 *  * is NORTHWEST, with the image drawn to the right/below the point.
 * 
 * @author Elisha Peterson
 */
public class ImageRenderer implements Renderer<AnchoredImage, Graphics2D> {

    private static final ImageRenderer INST = new ImageRenderer();
    
    public static Renderer<AnchoredImage, Graphics2D> getInstance() {
        return INST;
    }

    @Override
    public void render(AnchoredImage primitive, AttributeSet style, Graphics2D canvas) {
        Rectangle2D rect = boundingBox(primitive, style, canvas);
        canvas.drawImage(primitive.getImage(), (int) (rect.getX()), (int) (rect.getY()), null);
    }

    @Override
    public Rectangle2D boundingBox(AnchoredImage primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        Anchor anchor = Styles.anchorOf(style, Anchor.NORTHWEST);
        return anchor.rectangleAnchoredAt(primitive, primitive.getWidth(), primitive.getHeight());
    }

    @Override
    public boolean contains(Point2D point, AnchoredImage primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        return boundingBox(primitive, style, canvas).contains(point);
    }

    @Override
    public boolean intersects(Rectangle2D rect, AnchoredImage primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        return boundingBox(primitive, style, canvas).intersects(rect);
    }

}
