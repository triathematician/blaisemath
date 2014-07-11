/*
 * PathStyleDecorated.java
 * Created Aug 27, 2011
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import static com.google.common.base.Preconditions.*;
import java.awt.Color;

/**
 * A style that generally defers to a base style for width and stroke.
 * The resulting width is the <i>product</i> of the width of the base style
 * and the width of this class's style.
 * 
 * @author elisha
 */
public class PathStyleDecorated extends PathStyleBasic {
    
    protected PathStyle base;

    public PathStyleDecorated() {
    }
    
    @Override
    public String toString() {
        return String.format("PathStyleDecorated[baseStyle=%s, stroke=%s, strokeWidth=%.1f]", 
                base, stroke, strokeWidth);
    }

    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    public PathStyleDecorated baseStyle(PathStyle style) {
        setBaseStyle(style);
        return this;
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public PathStyle getBaseStyle() {
        return base;
    }

    public void setBaseStyle(PathStyle base) {
        this.base = checkNotNull(base);
    }
    
    //</editor-fold>
    

    @Override
    public Color getStroke() {
        return stroke == null ? base.getStroke() : stroke;
    }

    @Override
    public float getStrokeWidth() {
        return strokeWidth * base.getStrokeWidth();
    }   
    
}
