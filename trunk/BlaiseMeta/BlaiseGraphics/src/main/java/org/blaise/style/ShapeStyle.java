/**
 * ShapeStyle.java
 * Created on Aug 4, 2009
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

/**
 * <p>
 *   Used to draw a shape (or several shapes) on a {@code java.awt.Graphics2D} object.
 *   This class will provide visual effects that do not determine the shape of the object.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface ShapeStyle extends Style {

    /**
     * Draws a shape on the provided canvas.
     * @param primitive the shape to draw
     * @param canvas the canvas on which to paint
     */
    void draw(java.awt.Shape primitive, java.awt.Graphics2D canvas);

}
