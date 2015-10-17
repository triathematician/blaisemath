/**
 * PrimitiveArrayGraphicSupport.java
 * Created Aug 1, 2014
 */
package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import static com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport.RENDERER_PROP;
import com.googlecode.blaisemath.util.DraggableCoordinate;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *  Adds an array of primitive objects and a renderer to a {@link Graphic}. Also
 *  implements default drag functionality that will be supported when the primitive
 *  is either a {@link Point2D} or a {@link DraggableCoordinate}. Attempts to
 *  make other kinds of graphics draggable will result in an exception being thrown.
 * </p>
 * 
 * @param <O> type of object being drawn
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha
 */
public abstract class PrimitiveArrayGraphicSupport<O,G> extends Graphic<G> {
    
    /** What is being drawn */
    protected O[] primitive;
    /** Draws the primitive on the graphics canvas */
    protected Renderer<O,G> renderer;
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /**
     * Return the shape for the graphic.
     * @return shape
     */
    public O[] getPrimitive() {
        return primitive;
    }

    /**
     * Set the shape for the graphic.
     * @param primitive shape
     */
    public void setPrimitive(O[] primitive) {
        if (this.primitive != primitive) {
            Object old = this.primitive;
            this.primitive = primitive;
            fireGraphicChanged();
            pcs.firePropertyChange(PrimitiveGraphicSupport.PRIMITIVE_PROP, old, primitive);
        }
    }
    
    /**
     * Return the i'th primitive
     * @param i index of primitive
     * @return primitive
     */
    public O getPrimitive(int i) {
        return primitive[i];
    }
    
    /**
     * Set the i'th primitive
     * @param i index of primitive
     * @param prim the primitive
     */
    public void setPrimitive(int i, O prim) {
        if (primitive[i] != prim) {
            Object old = this.primitive[i];
            this.primitive[i] = prim;
            fireGraphicChanged();
            pcs.fireIndexedPropertyChange(PrimitiveGraphicSupport.PRIMITIVE_PROP, i, old, prim);
        }
    }

    public Renderer<O, G> getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer<O, G> renderer) {
        if (this.renderer != renderer) {
            Object old = this.renderer;
            this.renderer = renderer;
            fireGraphicChanged();
            pcs.firePropertyChange(RENDERER_PROP, old, renderer);
        }
    }
    
    //</editor-fold>
    
    //
    // RENDERING
    //

    public int indexOf(Point2D nearby) {
        if (renderer == null) {
            return -1;
        }
        AttributeSet style = renderStyle();
        for (int i = primitive.length-1; i >= 0; i--) {
            if (renderer.contains(primitive[i], style, nearby)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public void renderTo(G canvas) {
        if (renderer == null) {
            return;
        }
        AttributeSet style = renderStyle();
        for (O o : primitive) {
            renderer.render(o, style, canvas);
        }
    }

    @Override
    public Rectangle2D boundingBox() {
        Rectangle2D res = null;
        AttributeSet style = renderStyle();
        for (O o : primitive) {
            Rectangle2D oBounds = renderer.boundingBox(o, style);
            res = res == null ? oBounds : res.createUnion(oBounds);
        }
        return res;
    }

    @Override
    public boolean contains(Point2D point) {
        return indexOf(point) != -1;
    }

    @Override
    public boolean intersects(Rectangle2D box) {
        if (renderer == null) {
            return false;
        }
        AttributeSet style = renderStyle();
        for (O o : primitive) {
            if (renderer.intersects(o, style, box)) {
                return true;
            }
        }
        return false;
    }

}
