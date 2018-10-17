/**
 * PrimitiveGraphic.java
 * Created Jul 31, 2014
 */

package com.googlecode.blaisemath.graphics.core;

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


import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.AttributeSet;
import javax.annotation.Nullable;

/**
 * A basic graphic object with a style set.
 * 
 * @param <O> type of primitive
 * @param <G> type of graphics canvas to render to
 * 
 * @author Elisha Peterson
 */
public class PrimitiveGraphic<O,G> extends PrimitiveGraphicSupport<O,G> {
    
    public static final String STYLE_PROP = "style";
    
    /** The style set for this graphic */
    @Nullable 
    protected AttributeSet style = new AttributeSet();

    public PrimitiveGraphic() {
    }

    public PrimitiveGraphic(O primitive, @Nullable AttributeSet style, Renderer<O, G> renderer) {
        setPrimitive(primitive);
        setStyle(style);
        setRenderer(renderer);
    }

    @Override
    public String toString() {
        return "PrimitiveGraphic{" + primitive + '}';
    }
       
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    @Override
    @Nullable 
    public AttributeSet getStyle() {
        return style;
    }

    public final void setStyle(@Nullable AttributeSet sty) {
        if (this.style != sty) {
            Object old = this.style;
            this.style = sty;
            fireGraphicChanged();
            pcs.firePropertyChange(STYLE_PROP, old, style);
        }
    }
    
    //</editor-fold>
    
}
