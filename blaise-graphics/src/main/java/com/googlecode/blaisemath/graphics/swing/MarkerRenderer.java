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

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.coordinate.OrientedPoint2D;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Draws an oriented point on the graphics canvas.
 * See also the <a href="http://www.w3.org/TR/SVG/painting.html#Markers">related SVG documentation</a> on markers.
 * 
 * @author Elisha Peterson
 */
public class MarkerRenderer implements Renderer<Point2D, Graphics2D> {

    private static final Logger LOG = Logger.getLogger(MarkerRenderer.class.getName());

    /** Delegate for rendering the shape of the marker */
    protected Renderer<Shape, Graphics2D> shapeRenderer = new ShapeRenderer();
    
    public static Renderer<Point2D, Graphics2D> getInstance() {
        return new MarkerRenderer();
    }

    //region PROPERTIES

    public Renderer<Shape, Graphics2D> getShapeRenderer() {
        return shapeRenderer;
    }

    public void setShapeRenderer(Renderer<Shape, Graphics2D> shapeRenderer) {
        this.shapeRenderer = checkNotNull(shapeRenderer);
    }

    // </editor-fold>

    public Shape getShape(Point2D primitive, AttributeSet style) {
        Float rad = style.getFloat(Styles.MARKER_RADIUS, 4f);
        double angle = primitive instanceof OrientedPoint2D ? ((OrientedPoint2D)primitive).angle : 0;
        Object marker = style.get(Styles.MARKER);
        if (marker == null) {
            return Markers.CIRCLE.create(primitive, angle, rad);
        } else if (marker instanceof Marker) {
            return ((Marker) marker).create(primitive, angle, rad);
        } else {
            LOG.log(Level.WARNING, marker instanceof String ? "Invalid marker object string (not supported yet): {0}" : "Invalid marker object: {0}", marker);
        }
        return null;
    }
    
    @Override
    public void render(Point2D primitive, AttributeSet style, Graphics2D canvas) {
        shapeRenderer.render(getShape(primitive, style), style, canvas);
    }

    @Override
    public Rectangle2D boundingBox(Point2D primitive, AttributeSet style, Graphics2D canvas) {
        return shapeRenderer.boundingBox(getShape(primitive, style), style, canvas);
    }

    @Override
    public boolean contains(Point2D point, Point2D primitive, AttributeSet style, Graphics2D canvas) {
        return shapeRenderer.contains(point, getShape(primitive, style), style, canvas);
    }

    @Override
    public boolean intersects(Rectangle2D rect, Point2D primitive, AttributeSet style, Graphics2D canvas) {
        return shapeRenderer.intersects(rect, getShape(primitive, style), style, canvas);
    }
    
}
