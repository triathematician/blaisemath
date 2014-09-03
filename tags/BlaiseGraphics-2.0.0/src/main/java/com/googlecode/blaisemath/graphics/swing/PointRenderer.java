/*
 * PointRendererBasic.java
 * Created Jan 22, 2011
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
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.Markers;
import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Draws a point on the graphics canvas, using a {@link Marker} object and a {@link ShapeStyle}.
 * See also the <a href="http://www.w3.org/TR/SVG/painting.html#Markers">related SVG documentation</a> on markers.
 * 
 * @author Elisha Peterson
 */
public class PointRenderer implements Renderer<Point2D, Graphics2D> {

    /** Delegate for rendering the shape of the marker */
    protected Renderer<Shape, Graphics2D> shapeRenderer = new ShapeRenderer();

    public static Renderer<Point2D, Graphics2D> getInstance() {
        return new PointRenderer();
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

    public Shape getShape(Point2D primitive, AttributeSet style) {
        Float rad = style.getFloat(Styles.MARKER_RADIUS, 4f);
        Object mStyle = style.get(Styles.MARKER);
        if (mStyle == null) {
            return Markers.CIRCLE.create(primitive, 0, rad);
        } else if (mStyle instanceof Marker) {
            return ((Marker)mStyle).create(primitive, 0, rad);
        } else if (mStyle instanceof String) {
            Logger.getLogger(PointRenderer.class.getName()).log(Level.WARNING,
                    "Invalid marker object string (not supported yet): {0}", mStyle);
        } else {
            Logger.getLogger(PointRenderer.class.getName()).log(Level.WARNING,
                    "Invalid marker object: {0}", mStyle);
        }
        return null;
    }
    
    public void render(Point2D primitive, AttributeSet style, Graphics2D canvas) {
        shapeRenderer.render(getShape(primitive, style), style, canvas);
    }

    public boolean contains(Point2D primitive, AttributeSet style, Point2D point) {
        return shapeRenderer.contains(getShape(primitive, style), style, point);
    }

    public boolean intersects(Point2D primitive, AttributeSet style, Rectangle2D rect) {
        return shapeRenderer.intersects(getShape(primitive, style), style, rect);
    }

}
