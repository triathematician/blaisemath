package com.googlecode.blaisemath.graphics.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.GraphicUtils;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.svg.SVGElement;
import com.googlecode.blaisemath.svg.SVGRoot;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses an {@link SVGElement} as a primitive to be rendered on a {@link JGraphicComponent}.
 * Allows setting bounding boxes, so that the SVG element can be scaled or moved on the canvas.
 * 
 * @author elisha
 */
public class SVGGraphic extends GraphicComposite<Graphics2D> {

    private static final Logger LOG = Logger.getLogger(SVGGraphic.class.getName());
    private static final boolean RENDER_BOUNDS = false;
    
    /** Source SVG element to be drawn */
    private SVGElement element;
    /** Describes where on the canvas the element will be drawn */
    private Rectangle2D graphicBounds;
    /** The graphic object that will be rendered */
    private Graphic<Graphics2D> primitiveElement;
    
    /**
     * Create a new instance for the provided SVG.
     * @param element SVG to draw
     * @return graphic
     */
    public static SVGGraphic create(SVGElement element) {
        checkNotNull(element);
        SVGGraphic res = new SVGGraphic();
        res.setElement(element);
        return res;
    }
    
    /**
     * Create a new instance for the provided SVG.
     * @param svg svg as a string
     * @return graphic
     */
    public static SVGGraphic create(String svg) {
        try {
            return create(SVGRoot.load(svg));
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Invalid SVG", ex);
            return new SVGGraphic();
        }
    }
    
    private void updateGraphics() {
        Graphic<Graphics2D> nue = SVGElementGraphicConverter.getInstance()
                .convert(element);
        if (primitiveElement == null) {
            addGraphic(nue);
        } else {
            replaceGraphics(Collections.singleton(primitiveElement), Collections.singleton(nue));
        }
        primitiveElement = nue;
        
        if (element instanceof SVGRoot) {
            int wid = ((SVGRoot) element).getWidth();
            int ht = ((SVGRoot) element).getHeight();
            this.graphicBounds = new Rectangle2D.Double(0, 0, wid, ht);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public SVGElement getElement() {
        return element;
    }
    
    public void setElement(SVGElement el) {
        Object old = this.element;
        if (old != el) {
            this.element = el;
            updateGraphics();
        }
    }

    public Rectangle2D getGraphicBounds() {
        return graphicBounds;
    }

    public void setGraphicBounds(Rectangle2D graphicBounds) {
        this.graphicBounds = graphicBounds;
        fireGraphicChanged();
    }
    
    //</editor-fold>
    
    /** Generate transform used to scale/translate the SVG */
    private AffineTransform transform() {
        if (graphicBounds == null || !(element instanceof SVGRoot)) {
            return null;
        }
        Rectangle2D viewBox = ((SVGRoot) element).getViewBoxAsRectangle();
        return viewBox == null ? null : PanAndZoomHandler.scaleRectTransform(graphicBounds, viewBox);
    }
    
    /** Inverse transform */
    private AffineTransform inverseTransform() {
        if (graphicBounds == null || !(element instanceof SVGRoot)) {
            return null;
        }
        Rectangle2D viewBox = ((SVGRoot) element).getViewBoxAsRectangle();
        return viewBox == null ? null : PanAndZoomHandler.scaleRectTransform(viewBox, graphicBounds);
    }

    @Override
    public Rectangle2D boundingBox() {
        AffineTransform tx = transform();
        Rectangle2D norm = GraphicUtils.boundingBox(entries);
        return tx == null ? norm : tx.createTransformedShape(norm).getBounds2D();
    }

    @Override
    public boolean contains(Point2D point) {
        AffineTransform tx = inverseTransform();
        return tx == null ? super.contains(point)
                : super.contains(tx.transform(point, null));
    }

    @Override
    public boolean intersects(Rectangle2D box) {
        AffineTransform tx = inverseTransform();
        return tx == null ? super.intersects(box) 
                : super.intersects(tx.createTransformedShape(box).getBounds2D());
    }

    @Override
    public void renderTo(Graphics2D canvas) {
        AffineTransform tx = transform();
        if (RENDER_BOUNDS && graphicBounds != null) {
            canvas.setColor(Color.red);
            canvas.draw(graphicBounds);
        }
        if (tx == null) {
            super.renderTo(canvas);
        } else {
            AffineTransform original = canvas.getTransform();
            canvas.transform(transform());
            super.renderTo(canvas);
            canvas.setTransform(original);
        }
    }

}
