package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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

/**
 * A basic graphic object with a style set.
 * 
 * @param <O> type of primitive
 * @param <G> type of graphics canvas to render to
 * 
 * @author Elisha Peterson
 */
public class PrimitiveGraphic<O,G> extends PrimitiveGraphicSupport<O,G> {
    
    public static final String P_STYLE = "style";
    
    /** The style set for this graphic */
    protected @Nullable AttributeSet style = new AttributeSet();

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
       
    //region PROPERTIES

    @Override
    public @Nullable AttributeSet getStyle() {
        return style;
    }

    public final void setStyle(@Nullable AttributeSet sty) {
        if (this.style != sty) {
            Object old = this.style;
            this.style = sty;
            fireGraphicChanged();
            pcs.firePropertyChange(P_STYLE, old, style);
        }
    }
    
    //endregion
    
}
