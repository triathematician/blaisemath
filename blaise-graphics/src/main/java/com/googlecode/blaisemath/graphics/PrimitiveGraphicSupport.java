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

import static com.google.common.base.Preconditions.checkArgument;
import com.googlecode.blaisemath.coordinate.CoordinateBean;
import com.googlecode.blaisemath.coordinate.DraggableCoordinate;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 *  Adds a primitive object and a renderer to a {@link Graphic}. Also
 *  implements default drag functionality that will be supported when the primitive
 *  is either a {@link Point2D} or a {@link DraggableCoordinate}. Attempts to
 *  make other kinds of graphics draggable will result in an exception being thrown.
 *  Implementations must provide the style used for rendering the primitive.
 *
 * @param <O> type of object being drawn
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
 */
public abstract class PrimitiveGraphicSupport<O,G> extends Graphic<G> {
    
    public static final String P_PRIMITIVE = "primitive";
    public static final String P_RENDERER = "renderer";
    
    /** What is being drawn */
    protected @Nullable O primitive;
    /** Draws the primitive on the graphics canvas */
    protected @Nullable Renderer<O,G> renderer = null;
    
    /** Whether graphic can be dragged */
    protected boolean dragEnabled = false;
    /** Handles the drag movement */
    protected GraphicMouseDragHandler dragger = null;
    
    //region PROPERTIES

    /**
     * Return the shape for the graphic.
     * @return shape
     */
    public @Nullable O getPrimitive() {
        return primitive;
    }

    /**
     * Set the shape for the graphic.
     * @param primitive shape
     */
    public void setPrimitive(@Nullable O primitive) {
        if (this.primitive != primitive) {
            Object old = this.primitive;
            this.primitive = primitive;

            // if the primitive changes to something not supporting drag, make sure its turned off
            if (!isDragCapable()) {
                setDragEnabled(false);
            }

            fireGraphicChanged();
            pcs.firePropertyChange(P_PRIMITIVE, old, primitive);
        }
    }

    public @Nullable Renderer<O, G> getRenderer() {
        return renderer;
    }

    public void setRenderer(@Nullable Renderer<O, G> renderer) {
        if (this.renderer != renderer) {
            Object old = this.renderer;
            this.renderer = renderer;
            fireGraphicChanged();
            pcs.firePropertyChange(P_RENDERER, old, renderer);
        }
    }
    
    //endregion

    //region RENDERING
    
    @Override
    public void renderTo(G canvas) {
        if (renderer != null && primitive != null) {
            renderer.render(primitive, renderStyle(), canvas);
        }
    }

    @Override
    public @Nullable Rectangle2D boundingBox(@Nullable G canvas) {
        return renderer == null || primitive == null ? null : renderer.boundingBox(primitive, renderStyle(), canvas);
    }

    @Override
    public boolean contains(Point2D point, @Nullable G canvas) {
        return renderer != null && primitive != null && renderer.contains(point, primitive, renderStyle(), canvas);
    }

    @Override
    public boolean intersects(Rectangle2D box, @Nullable G canvas) {
        return renderer != null && primitive != null && renderer.intersects(box, primitive, renderStyle(), canvas);
    }

    //endregion

    //region DRAGGING

    public boolean isDragCapable() {
        return primitive instanceof Point2D || primitive instanceof Shape
            || (primitive instanceof DraggableCoordinate && ((CoordinateBean)primitive).getPoint() instanceof Point2D);
    }
    
    public boolean isDragEnabled() {
        return dragEnabled;
    }
    
    public void setDragEnabled(boolean val) {
        if (dragEnabled != val) {
            checkArgument(!val || isDragCapable());
            this.dragEnabled = val;
            if (dragEnabled) {
                if (primitive instanceof Shape) {
                    dragger = new ShapeDragHandler();
                } else {
                    DraggableCoordinate bean = primitive instanceof DraggableCoordinate ? (DraggableCoordinate) primitive
                        : primitive instanceof Point2D ? new ProxyPointDraggable()
                        : null;
                    assert bean != null;
                    dragger = new GraphicMouseMoveHandler(bean);
                }
                addMouseListener(dragger);
                addMouseMotionListener(dragger);
            } else {
                if (dragger != null) {
                    removeMouseListener(dragger);
                    removeMouseMotionListener(dragger);
                    dragger = null;
                }
            }
        }
    }

    //endregion

    //region INNER CLASSES

    /** A draggable point generating events when it's position changes. */
    private class ProxyPointDraggable implements DraggableCoordinate<Point2D> {
        @Override
        public Point2D getPoint() {
            return (Point2D) primitive;
        }

        @Override
        public void setPoint(Point2D p) {
            ((Point2D) primitive).setLocation(p);
        }

        @Override
        public void setPoint(Point2D initial, Point2D dragStart, Point2D dragFinish) {
            ((Point2D) primitive).setLocation(
                    initial.getX() + dragFinish.getX() - dragStart.getX(),
                    initial.getY() + dragFinish.getY() - dragStart.getY());
            fireGraphicChanged();
        }
    }

    /** A draggable shape generating events when it's position changes. */
    private class ShapeDragHandler extends GraphicMouseDragHandler {
        private Shape initialShape = null;
        private double x0 = 0;
        private double y0 = 0;

        @Override
        public void mouseDragInitiated(GraphicMouseEvent e, Point2D start) {
            initialShape = (Shape) primitive;
            if (initialShape instanceof RectangularShape) {
                x0 = ((RectangularShape) initialShape).getX();
                y0 = ((RectangularShape) initialShape).getY();
            } else if (initialShape instanceof Line2D) {
                x0 = ((Line2D) initialShape).getX1();
                y0 = ((Line2D) initialShape).getY1();
            }
        }

        @Override
        public void mouseDragInProgress(GraphicMouseEvent e, Point2D start) {
            double dx = e.getGraphicLocation().getX()-start.getX();
            double dy = e.getGraphicLocation().getY()-start.getY();
            if (dx == 0 && dy == 0) {
                return;
            }
            if (initialShape instanceof RectangularShape) {
                RectangularShape rsh = (RectangularShape) initialShape;
                rsh.setFrame(x0+dx, y0+dy, rsh.getWidth(), rsh.getHeight());
            } else if (initialShape instanceof Line2D) {
                Line2D line = (Line2D) initialShape;
                setPrimitive((O) new Line2D.Double(x0 + dx, y0 + dy, line.getX2() + dx, line.getY2() + dy));
            } else {
                AffineTransform at = new AffineTransform();
                at.translate(dx, dy);
                setPrimitive((O) at.createTransformedShape(initialShape));
            }
            fireGraphicChanged();
        }

        @Override
        public void mouseDragCompleted(GraphicMouseEvent e, Point2D start) {
            mouseDragInProgress(e, start);
        }
    }

    //endregion

}
