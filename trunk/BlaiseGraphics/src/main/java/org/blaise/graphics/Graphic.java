/*
 * Graphic.java
 * Created Jan 16, 2011
 */

package org.blaise.graphics;

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


import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.blaise.style.StyleHintSet;
import org.blaise.util.ContextMenuInitializer;

/**
 * <p>
 *      Defines an entry that may be drawn on a graphics canvas.
 *      This draw functionality is implemented in the {@link Graphic#draw(java.awt.Graphics2D)} method.
 * </p>
 * <p>
 *      Key additional features are:
 * </p>
 * <ul>
 *      <li>A <em>parent</em> (via get and set methods),
 *          which is a {@link GraphicComposite}
 *          and provides access to default styles of various types.</li>
 *      <li>Visibility settings (via get and set methods).
 *          See {@link VisibilityHint} for the parameters.</li>
 *      <li>Three methods based on a point on the canvas:
 *          <ul>
 *              <li> {@link Graphic#contains(java.awt.Point)}, testing whether the entry contains a point</li>
 *              <li> {@link Graphic#getTooltip(java.awt.Point)}, returning the tooltip for a point (or null)</li>
 *              <li> {@link Graphic#getMouseListener(java.awt.Point)}, returning an object that can handle mouse actions</li>
 *          </ul>
 *      </li>
 *
 * @author Elisha Peterson
 */
 public interface Graphic extends ContextMenuInitializer<Graphic> {

     //
     // COMPOSITION API
     //

    /**
      * Return parent of the entry
      * @return parent, possibly null
      */
    GraphicComposite getParent();
    
    /**
     * Sets parent of the entry
     * @param parent the parent
     */
    void setParent(GraphicComposite parent);


    //
    // STYLE & DRAWING API
    //

    /**
     * Draws the primitive on the specified graphics canvas, using current style.
     * @param canvas graphics canvas
     */
    void draw(Graphics2D canvas);

    /**
     * Return set of visibility hints for the graphic.
     * @return visibility hints
     */
    StyleHintSet getStyleHints();


    //
    // LOCATOR API
    //

    /**
     * Method used to determine whether the graphic receives {@link MouseEvent}s
     * and will be asked to provide a tooltip at the given point.
     * The graphic's {@link MouseListener}s and {@link MouseMotionListener}s will
     * have the opportunity to receive events if the graphic is the topmost element
     * containing the event's point.
     *
     * @param point the window point
     * @return true if the entry contains the point, else false
     */
    boolean contains(Point2D point);

    /**
     * Checks to see if the graphic intersects the area within specified rectangle.
     * @param box rectangle to check against
     * @return true if it intersects, false otherwise
     */
    boolean intersects(Rectangle2D box);


    //
    // SELECTION API
    //

    /**
     * Return true if graphic can be selected. If this flag is set to true,
     * the locator API will be used to map selection gestures (e.g. click to
     * select, or select graphics in box).
     * @return selection flag
     */
    boolean isSelectionEnabled();


    //
    // TOOLTIP API
    //

    /**
     * Return true if tips are enabled/supported
     * @return true if yes
     */
    boolean isTooltipEnabled();

    /**
     * Return tooltip for the specified point
     * @param point the point
     * @return the tooltip at the specified location (may be null)
     */
    String getTooltip(Point2D point);


    //
    // CONTEXT MENU API
    //

    /**
     * Whether graphic supports context menu building
     * @return true if yes
     */
    boolean isContextMenuEnabled();

    
    //
    // MOUSE HANDLING API
    //

    /**
     * Whether the object should receive mouse events.
     * @return true if yes, false otherwise
     */
    boolean isMouseEnabled();

    /**
     * Adds a mouse listener to the graphic
     * @param handler listener
     */
    void addMouseListener(MouseListener handler);

    /**
     * Removes a mouse listener from the graphic
     * @param handler listener
     */
    void removeMouseListener(MouseListener handler);

    /**
     * Return list of mouse listeners registered with the graphic
     * @return listeners
     */
    MouseListener[] getMouseListeners();

    /**
     * Adds a mouse motion listener to the graphic
     * @param handler listener
     */
    void addMouseMotionListener(MouseMotionListener handler);

    /**
     * Removes a mouse motion listener from the graphic
     * @param handler listener
     */
    void removeMouseMotionListener(MouseMotionListener handler);

    /**
     * Return list of mouse motion listeners registered with the graphic
     * @return listeners
     */
    MouseMotionListener[] getMouseMotionListeners();

}
