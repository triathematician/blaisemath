/*
 * StyleContext.java
 * Created Jan 22, 2011
 */
package com.googlecode.blaisemath.style.context;

import com.googlecode.blaisemath.style.PathStyle;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.style.ShapeStyle;
import com.googlecode.blaisemath.style.Style;
import com.googlecode.blaisemath.style.TextStyle;

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

/**
 * <p>
 *   Provides methods that can be used to retrieve styles specialized for drawing
 *   basic shapes, points, and text on a graphics canvas.
 *   In a sense, this is similar to the functionality provided by a <em>Cascading Style Sheet</em>.
 * </p>
 * 
 * @param <S> type of object to be styled
 *
 * @author Elisha Peterson
 */
public interface StyleContext<S> {
    
    /**
     * Return style of given type for given source object.
     * @param <T> type of style
     * @param cls class of style to return
     * @param src object to be styled
     * @param hints style hints to apply
     * @return the style
     */
    <T extends Style> T getStyle(Class<T> cls, S src, StyleHintSet hints);

    /** 
     * Return style used for solid shapes (fill and stroke)
     * @param src object to be styled
     * @param hints style hints to apply
     * @return a solid shape style
     */
    ShapeStyle getShapeStyle(S src, StyleHintSet hints);
    
    /** 
     * Return style used for paths (stroke only)
     * @param src object to be styled
     * @param hints style hints to apply
     * @return a path style 
     */
    PathStyle getPathStyle(S src, StyleHintSet hints);
    
    /** 
     * Return style used for points
     * @param src object to be styled
     * @param hints style hints to apply
     * @return a point style 
     */
    PointStyle getPointStyle(S src, StyleHintSet hints);
    
    /**
     * Return style used for strings
     * @param src object to be styled
     * @param hints style hints to apply
     * @return a string style 
     */
    TextStyle getTextStyle(S src, StyleHintSet hints);

}
