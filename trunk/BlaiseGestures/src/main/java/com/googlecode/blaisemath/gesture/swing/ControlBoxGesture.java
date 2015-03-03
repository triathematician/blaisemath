/**
 * BoundingBoxGesture.java
 * Created Dec 13, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 Elisha Peterson
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
import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGestureSupport;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.MarkerRenderer;
import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Markers;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AffineTransformBuilder;
import com.googlecode.blaisemath.util.AnchoredImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Displays a bounding box around the object, with controls for resizing it.
 * 
 * @author elisha
 */
public class ControlBoxGesture extends MouseGestureSupport {
    
    private static final int CAPTURE_RAD = 5;
    
    protected final JGraphicComponent view;
    
    protected final PrimitiveGraphicSupport graphic;
    private final AttributeSet boxStyle = Styles.strokeWidth(new Color(0,0,255,64), 1f);
    private final AttributeSet controlStyle = Styles.fillStroke(new Color(0,0,255,64), null)
            .and(Styles.MARKER, Markers.CIRCLE)
            .and(Styles.MARKER_RADIUS, CAPTURE_RAD);
    private final AttributeSet selectedControlStyle = Styles.fillStroke(new Color(0,0,255,128), null)
            .and(Styles.MARKER, Markers.CIRCLE)
            .and(Styles.MARKER_RADIUS, CAPTURE_RAD);
    
    /** Current active control */
    private ControlPoint controlPoint;
    /** If the current gesture is to move */
    protected boolean move = false;
    /** Shape at start of drag */
    private Shape startShape;
    
    public ControlBoxGesture(GestureOrchestrator orchestrator, PrimitiveGraphicSupport graphic) {
        super(orchestrator, "Bounding box", "Move and resize shape");
        
        checkArgument(orchestrator.getComponent() instanceof JGraphicComponent, 
                "Orchestrator must use a JGraphicComponent");
        view = (JGraphicComponent) orchestrator.getComponent();
        
        this.graphic = graphic;
    }
    
    /**
     * Checks whether the gesture supports the given primitive.
     * @param primitive to check
     * @return true if supported, false otherwise
     */
    public static boolean supports(Object primitive) {
        return primitive instanceof Shape || primitive instanceof AnchoredImage;
    }
    
    private Rectangle2D box() {
        return graphic.boundingBox();
    }

    @Override
    public void paint(Graphics2D g) {
        AffineTransform at = view.getTransform() == null ? new AffineTransform() : view.getTransform();
        Shape transformed = at.createTransformedShape(box());
        ShapeRenderer.getInstance().render(transformed, boxStyle, g);
        Rectangle2D bds = transformed.getBounds2D();
        for (ControlPoint cp : ControlPoint.values()) {
            Point2D pt = cp.location(bds);
            AttributeSet sty = cp == controlPoint ? selectedControlStyle : controlStyle;
            MarkerRenderer.getInstance().render(pt, sty, g);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        Object prim = graphic.getPrimitive();
        if (prim instanceof Shape) {
            startShape = (Shape) graphic.getPrimitive();
        } else if (prim instanceof AnchoredImage) {
            startShape = ((AnchoredImage) prim).getBounds(null);
        } else {
            Logger.getLogger(ControlBoxGesture.class.getName()).log(Level.INFO, "Resize not supported: {0}", prim);
            return;
        }
        controlPoint = capture(startShape.getBounds2D(), pressPoint);
        move = controlPoint == null && startShape.contains(pressPoint);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        double dx = locPoint.getX() - pressPoint.getX();
        double dy = locPoint.getY() - pressPoint.getY();
        if (controlPoint != null) {
            AffineTransform transf = controlPoint.resize(startShape.getBounds2D(), dx, dy);
            applyTransformToGraphic(transf);
        } else if (move) {
            applyTransformToGraphic(new AffineTransformBuilder().translate(dx, dy).build());
        }
    }
    
    private void applyTransformToGraphic(AffineTransform transf) {
        Object prim = graphic.getPrimitive();
        if (prim instanceof Shape) {
            Shape shape = transf.createTransformedShape(startShape);
            graphic.setPrimitive(shape);
        } else if (prim instanceof AnchoredImage) {
            AnchoredImage img = (AnchoredImage) prim;
            Rectangle2D newBounds = transf.createTransformedShape(startShape).getBounds2D();
            graphic.setPrimitive(new AnchoredImage(newBounds.getX(), newBounds.getY(), newBounds.getWidth(), newBounds.getHeight(),
                    img.getOriginalImage(), img.getReference()));
        } else {
            Logger.getLogger(ControlBoxGesture.class.getName()).log(Level.INFO, "Resize not supported: {0}", prim);
            return;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        controlPoint = null;
        startShape = null;
    }

    @Override
    public void finish() {
        controlPoint = null;
        startShape = null;
    }
    
    //
    // ControlPoint handling
    //

    // get the control point for the given press point
    private ControlPoint capture(Rectangle2D box, Point2D pressPoint) {
        AffineTransform transf = view.getTransform();
        double cap = transf == null ? CAPTURE_RAD 
                : CAPTURE_RAD / Math.max(transf.getScaleX(), transf.getScaleY());
        for (ControlPoint cp : ControlPoint.values()) {
            if (cp.captures(box, pressPoint, cap)) {
                return cp;
            }
        }
        return null;
    }
    
    private static enum ControlPoint {
        NW {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getMinX(), box.getMinY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(initial.getMinX()+dx, initial.getMinY()+dy)
                        .scale((initial.getWidth()-dx)/initial.getWidth(), (initial.getHeight()-dy)/initial.getHeight())
                        .translate(-initial.getMinX(), -initial.getMinY())
                        .build();
            }
        },
        N {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getCenterX(), box.getMinY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(0, initial.getMinY()+dy)
                        .scale(1, (initial.getHeight()-dy)/initial.getHeight())
                        .translate(0, -initial.getMinY())
                        .build();
            }
        },
        NE {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getMaxX(), box.getMinY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(initial.getMinX(), initial.getMinY()+dy)
                        .scale((initial.getWidth()+dx)/initial.getWidth(), (initial.getHeight()-dy)/initial.getHeight())
                        .translate(-initial.getMinX(), -initial.getMinY())
                        .build();
            }
        },
        W {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getMinX(), box.getCenterY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(initial.getMinX()+dx, 0)
                        .scale((initial.getWidth()-dx)/initial.getWidth(), 1)
                        .translate(-initial.getMinX(), 0)
                        .build();
            }
        },
        E {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getMaxX(), box.getCenterY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(initial.getMinX(), 0)
                        .scale((initial.getWidth()+dx)/initial.getWidth(), 1)
                        .translate(-initial.getMinX(), 0)
                        .build();
            }
        },
        SW {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getMinX(), box.getMaxY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(initial.getMinX()+dx, initial.getMinY())
                        .scale((initial.getWidth()-dx)/initial.getWidth(), (initial.getHeight()+dy)/initial.getHeight())
                        .translate(-initial.getMinX(), -initial.getMinY())
                        .build();
            }
        },
        S {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getCenterX(), box.getMaxY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(0, initial.getMinY())
                        .scale(1, (initial.getHeight()+dy)/initial.getHeight())
                        .translate(0, -initial.getMinY())
                        .build();
            }
        },
        SE {
            @Override
            Point2D location(Rectangle2D box) {
                return new Point2D.Double(box.getMaxX(), box.getMaxY());
            }
            @Override
            AffineTransform resize(Rectangle2D initial, double dx, double dy) {
                return new AffineTransformBuilder()
                        .translate(initial.getMinX(), initial.getMinY())
                        .scale((initial.getWidth()+dx)/initial.getWidth(), (initial.getHeight()+dy)/initial.getHeight())
                        .translate(-initial.getMinX(), -initial.getMinY())
                        .build();
            }
        };

        /** Get location of control point, for the given bounds. */
        abstract Point2D location(Rectangle2D box);
        
        /** Test whether control point can use the given press point */
        private boolean captures(Rectangle2D box, Point2D pressPoint, double rad) {
            double dist = location(box).distance(pressPoint);
            return dist < CAPTURE_RAD;
        }

        /**
         * Resize box based on drag.
         * @param initial box size when drag started
         * @param dx cumulative delta x during drag
         * @param dy cumulative delta y during drag
         * @return transform for converting initial rectangle to new bounds
         */
        abstract AffineTransform resize(Rectangle2D initial, double dx, double dy);
    }
    
}
