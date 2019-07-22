package com.googlecode.blaisemath.graphics.swing.render;

/*-
 * #%L
 * blaise-graphics
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

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.graphics.Renderer;
import com.googlecode.blaisemath.primitive.ClippedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Renders a clipped image to a swing canvas. Draws the image first, then draws a depiction of the clip if desired.
 * @author Elisha Peterson
 */
public class ClippedImageRenderer implements Renderer<ClippedImage, Graphics2D> {
    
    private static final Renderer<Shape, Graphics2D> CLIP_RENDERER = PathRenderer.getInstance();

    @Override
    public void render(ClippedImage primitive, AttributeSet style, Graphics2D canvas) {
        Shape curClip = canvas.getClip();
        Area customClip = new Area(curClip);
        customClip.intersect(new Area(primitive.getShape()));
        canvas.setClip(customClip);
        canvas.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        canvas.drawImage(primitive.getImage(), primitive.imageTransform(), null);
        canvas.setClip(curClip);
        CLIP_RENDERER.render(primitive.getShape(), style, canvas);
    }

    @Override
    public Rectangle2D boundingBox(ClippedImage primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        return CLIP_RENDERER.boundingBox(primitive.getShape(), style, canvas);
    }

    @Override
    public boolean contains(Point2D point, ClippedImage primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        return CLIP_RENDERER.contains(point, primitive.getShape(), style, canvas);
    }

    @Override
    public boolean intersects(Rectangle2D rect, ClippedImage primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        return CLIP_RENDERER.intersects(rect, primitive.getShape(), style, canvas);
    }

}
