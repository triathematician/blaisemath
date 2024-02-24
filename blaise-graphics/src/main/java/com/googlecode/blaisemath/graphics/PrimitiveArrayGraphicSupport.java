package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import static com.googlecode.blaisemath.graphics.PrimitiveGraphicSupport.P_RENDERER;
import static java.util.Arrays.asList;

import com.googlecode.blaisemath.coordinate.DraggableCoordinate;
import com.googlecode.blaisemath.style.AttributeSet;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Adds an array of primitive objects and a renderer to a {@link Graphic}. Also
 * implements default drag functionality that will be supported when the primitive
 * is either a {@link Point2D} or a {@link DraggableCoordinate}. Attempts to
 * make other kinds of graphics draggable will result in an exception being thrown.
 *
 * @param <O> type of object being drawn
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
 */
public abstract class PrimitiveArrayGraphicSupport<O, G> extends Graphic<G> {
    
    /** What is being drawn */
    protected O[] primitive;
    /** Draws the primitive on the graphics canvas */
    protected Renderer<O, G> renderer;
    
    //region PROPERTIES

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
            pcs.firePropertyChange(PrimitiveGraphicSupport.P_PRIMITIVE, old, primitive);
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
            pcs.fireIndexedPropertyChange(PrimitiveGraphicSupport.P_PRIMITIVE, i, old, prim);
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
            pcs.firePropertyChange(P_RENDERER, old, renderer);
        }
    }
    
    //endregion

    public int indexOf(Point2D nearby, G canvas) {
        if (renderer == null) {
            return -1;
        }
        AttributeSet style = renderStyle();
        for (int i = primitive.length-1; i >= 0; i--) {
            if (renderer.contains(nearby, primitive[i], style, canvas)) {
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
    public Rectangle2D boundingBox(@Nullable G canvas) {
        AttributeSet style = renderStyle();
        return GraphicUtils.boundingBox(asList(primitive), p -> renderer.boundingBox(p, style, canvas), null);
    }

    @Override
    public boolean contains(Point2D point, @Nullable G canvas) {
        return indexOf(point, canvas) != -1;
    }

    @Override
    public boolean intersects(Rectangle2D box, @Nullable G canvas) {
        if (renderer == null) {
            return false;
        }
        AttributeSet style = renderStyle();
        return Arrays.stream(primitive).anyMatch(o -> renderer.intersects(box, o, style, canvas));
    }

}
