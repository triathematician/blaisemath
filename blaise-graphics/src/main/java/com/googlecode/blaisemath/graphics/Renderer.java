package com.googlecode.blaisemath.graphics;

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

import com.googlecode.blaisemath.style.AttributeSet;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Render elements on a graphics context, and computes bounding boxes and intersections as needed.
 * 
 * @param <S> the type of object to render
 * @param <G> the type of object used for rendering
 * @author Elisha Peterson
 */
public interface Renderer<S,G> {
    
    /**
     * Render the given object on the given graphics canvas.
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where to render it
     */
    void render(S primitive, AttributeSet style, G canvas);
    
    /**
     * Get the bounding box for the drawn object
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where content is rendered
     * @return bounding box around the object
     */
    @Nullable Rectangle2D boundingBox(S primitive, AttributeSet style, @Nullable G canvas);
    
    /**
     * Test whether rendered primitive contains the given point.
     * @param point the point to test
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where content is rendered
     * @return true if rendered primitive contains point
     */
    boolean contains(Point2D point, S primitive, AttributeSet style, @Nullable G canvas);
    
    /**
     * Test whether rendered primitive intersects the given rectangle.
     * @param rect rectangle to test intersection with
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where content is rendered
     * @return true if rendered primitive intersects rectangle
     */
    boolean intersects(Rectangle2D rect, S primitive, AttributeSet style, @Nullable G canvas);
    
}
