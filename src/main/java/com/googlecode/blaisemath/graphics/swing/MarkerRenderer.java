/**
 * MarkerRenderer.java
 * Created Jul 31, 2014
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


import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.geom.OrientedPoint2D;
import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws an oriented point on a graphics canvas.
 * 
 * @author Elisha
 */
public class MarkerRenderer implements Renderer<OrientedPoint2D, Graphics2D> {

    /** Delegate for rendering the shape of the marker */
    protected Renderer<Shape, Graphics2D> shapeRenderer = new ShapeRenderer();
    
    public static MarkerRenderer getInstance() {
        return new MarkerRenderer();
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public Renderer<Shape, Graphics2D> getShapeRenderer() {
        return shapeRenderer;
    }

    public void setShapeRenderer(Renderer<Shape, Graphics2D> shapeRenderer) {
        this.shapeRenderer = checkNotNull(shapeRenderer);
    }

    // </editor-fold>

    public Shape getShape(OrientedPoint2D primitive, AttributeSet style) {
        Float rad = style.getFloat(Styles.MARKER_RADIUS, 4f);
        Object marker = style.get(Styles.MARKER);
        Marker drawMarker = marker instanceof Marker ? (Marker) marker : Markers.CIRCLE;
        return drawMarker.create(primitive, primitive.angle, rad);
    }
    
    public void render(OrientedPoint2D primitive, AttributeSet style, Graphics2D canvas) {
        shapeRenderer.render(getShape(primitive, style), style, canvas);
    }

    public boolean contains(OrientedPoint2D primitive, AttributeSet style, Point2D point) {
        return shapeRenderer.contains(getShape(primitive, style), style, point);
    }

    public boolean intersects(OrientedPoint2D primitive, AttributeSet style, Rectangle2D rect) {
        return shapeRenderer.intersects(getShape(primitive, style), style, rect);
    }
    
}
