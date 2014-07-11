/**
 * ShapeStyle.java
 * Created on Aug 4, 2009
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

import java.awt.Color;
import java.awt.Shape;
import javax.annotation.Nullable;

/**
 * <p>
 *   Used to draw a path (or several paths) on a {@code java.awt.Graphics2D} object.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface PathStyle extends ShapeStyle {

    /**
     * Return path color
     * @return color
     */
    Color getStroke();

    /**
     * Return path thickness
     * @return thickness of path
     */
    float getStrokeWidth();

    /**
     * Return shape of path drawn, useful for testing
     * when the mouse has moved over the path.
     * @param primitive shape primitive
     * @return path shape
     */
    @Nullable 
    Shape shapeOfPath(Shape primitive);

}
