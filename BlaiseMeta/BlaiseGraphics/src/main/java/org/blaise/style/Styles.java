/*
 * Styles.java
 * Created May 9, 2013
 */
package org.blaise.style;

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


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import javax.annotation.Nullable;

/**
 * Factory class providing convenience methods for easily creating styles.
 * 
 * @author Elisha
 */
public final class Styles {
    
    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);
    /** Default composite */
    public static final Composite DEFAULT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
    
    public static final ShapeStyle DEFAULT_SHAPE_STYLE = fillStroke(Color.white, Color.black);
    public static final PathStyle DEFAULT_PATH_STYLE = strokeWidth(Color.black, 1f);
    public static final PointStyle DEFAULT_POINT_STYLE = new PointStyleBasic();
    public static final TextStyle DEFAULT_TEXT_STYLE = new TextStyleBasic();
    
    // utility class
    private Styles() {
    }
   
    /**
     * Create a basic shape style with given fill & stroke
     * @param fill fill color
     * @param stroke stroke color
     * @return shape style
     */
    public static ShapeStyleBasic fillStroke(@Nullable Color fill, @Nullable Color stroke) {
        return new ShapeStyleBasic().fill(fill).stroke(stroke);
    }
    
    /**
     * Create a path style with a stroke color and width
     * @param stroke stroke color
     * @param width stroke width
     * @return path style
     */
    public static PathStyleBasic strokeWidth(Color stroke, float width) {
        return new PathStyleBasic().stroke(stroke).strokeWidth(width);
    }
    
    
}
