package com.googlecode.blaisemath.graphics.swing

import com.google.common.cache.CacheBuilder
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.geom.AffineTransformBuilder
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.swing.AnimationStep
import com.googlecode.blaisemath.util.swing.CanvasPainter
import com.googlecode.blaisemath.util.swing.MoreSwingUtilities
import junit.framework.TestCase
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.geom.RectangularShape
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.swing.Timer

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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
*/ /**
 * Enables pan and zoom of a graphics canvas, by changing the [AffineTransform]
 * associated with the canvas.
 *
 * @author Elisha Peterson
 */
class PanAndZoomHandler private constructor(comp: JGraphicComponent?) : MouseAdapter(), CanvasPainter<Graphics2D?> {
    /** The component for the mouse handling  */
    private val component: JGraphicComponent?

    /** Hint box for zooming  */
    private var zoomBox: Rectangle2D.Double? = null

    /** Location mouse was first pressed at.  */
    private var pressedAt: Point? = null

    /** Stores keyboard modifiers for mouse.  */
    private var mode: String? = null

    /** Old bounds for the window.  */
    private var oldLocalBounds: Rectangle2D? = null
    override fun paint(component: Component?, canvas: Graphics2D?) {
        if (zoomBox != null) {
            ShapeRenderer.Companion.getInstance().render(zoomBox, ZOOM_BOX_STYLE, canvas)
        }
    }

    //region MOUSE HANDLING
    private fun initMouseGesture(e: MouseEvent?) {
        mode = MouseEvent.getModifiersExText(e.getModifiersEx())
        if (RECTANGLE_RESIZE_MODE == mode || PAN_MODE == mode || RESTRICTED_MOVEMENT_MODE == mode) {
            pressedAt = e.getPoint()
        }
        if (RECTANGLE_RESIZE_MODE == mode) {
            zoomBox = Rectangle2D.Double(pressedAt.x, pressedAt.y, 0, 0)
        } else if (PAN_MODE == mode || RESTRICTED_MOVEMENT_MODE == mode) {
            // pan mode
            oldLocalBounds = getLocalBounds(component)
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        if (!e.isConsumed()) {
            initMouseGesture(e)
        }
    }

    override fun mouseDragged(e: MouseEvent?) {
        if (e.isConsumed()) {
            return
        }
        if (pressedAt == null) {
            initMouseGesture(e)
        }
        val mouseMods = MouseEvent.getModifiersExText(e.getModifiersEx())
        if (RECTANGLE_RESIZE_MODE == mode) {
            mouseDraggedResizeMode(e.getPoint())
        } else if (PAN_MODE == mode || RESTRICTED_MOVEMENT_MODE == mode) {
            val restrictedMovementMode = RESTRICTED_MOVEMENT_MODE == mouseMods || RESTRICTED_MOVEMENT_MODE_ALT == mouseMods
            mouseDraggedPanMode(e.getPoint(), restrictedMovementMode)
        }
    }

    private fun mouseDraggedResizeMode(winPt: Point?) {
        if (winPt.x < pressedAt.x) {
            zoomBox.x = winPt.x.toDouble()
            zoomBox.width = -winPt.x as Double + pressedAt.x
        } else {
            zoomBox.x = pressedAt.x.toDouble()
            zoomBox.width = winPt.x as Double - pressedAt.x
        }
        if (winPt.y < pressedAt.y) {
            zoomBox.y = winPt.y.toDouble()
            zoomBox.height = -winPt.y as Double + pressedAt.y
        } else {
            zoomBox.y = pressedAt.y.toDouble()
            zoomBox.height = winPt.y as Double - pressedAt.y
        }
        component.repaint()
    }

    private fun mouseDraggedPanMode(winPt: Point?, restrictedMovementMode: Boolean) {
        if (restrictedMovementMode) {
            if (Math.abs(winPt.y - pressedAt.y) < Math.abs(winPt.x - pressedAt.x)) {
                winPt.y = pressedAt.y
            } else {
                winPt.x = pressedAt.x
            }
        }
        val dx = (winPt.x - pressedAt.x) * component.getInverseTransform().getScaleX()
        val dy = (winPt.y - pressedAt.y) * component.getInverseTransform().getScaleY()
        setDesiredLocalBounds(component,
                Rectangle2D.Double(
                        oldLocalBounds.getX() - dx, oldLocalBounds.getY() - dy,
                        oldLocalBounds.getWidth(), oldLocalBounds.getHeight()))
    }

    override fun mouseReleased(e: MouseEvent?) {
        if (!e.isConsumed()) {
            mouseDragged(e)
            if (pressedAt != null && RECTANGLE_RESIZE_MODE == mode) {
                zoomBoxAnimated(component, zoomBox)
            }
        }
        zoomBox = null
        pressedAt = null
        oldLocalBounds = null
        mode = null
    }

    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        if (e.isConsumed()) {
            return
        }
        val mouseLoc = Point2D.Double(e.getPoint().x, e.getPoint().y)

        // ensure the point is within the window
        val bounds: RectangularShape? = component.getBounds()
        mouseLoc.x = Math.max(mouseLoc.x, bounds.getMinX())
        mouseLoc.x = Math.min(mouseLoc.x, bounds.getMaxX())
        mouseLoc.y = Math.max(mouseLoc.y, bounds.getMinY())
        mouseLoc.y = Math.min(mouseLoc.y, bounds.getMaxY())
        zoomPoint(component, component.toGraphicCoordinate(mouseLoc), if (e.getWheelRotation() > 0) 1.05 else 0.95)
    }

    companion object {
        private val LOG = Logger.getLogger(PanAndZoomHandler::class.java.name)

        /** Default number of steps to use in animating pan/zoom  */
        private const val ANIM_STEPS = 25

        /** How long between animation steps  */
        private const val ANIM_DELAY_MILLIS = 5

        /** Basic pan mode  */
        private val PAN_MODE: String? = "Button1"

        /** Mouse mode for rectangle resize  */
        private val RECTANGLE_RESIZE_MODE: String? = "Alt+Button1"

        /** Mode for restricted movement  */
        private val RESTRICTED_MOVEMENT_MODE: String? = "Shift+Button1"

        /** Allow user to release mouse button and still do movement  */
        private val RESTRICTED_MOVEMENT_MODE_ALT: String? = "Shift"

        /** Renderer for zoom box  */
        private val ZOOM_BOX_STYLE = Styles.fillStroke(
                Color(255, 128, 128, 128), Color(255, 196, 196, 128))

        /** Cache of recent animation timers  */
        private val TIMERS = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build<JGraphicComponent?, Timer?>()

        /**
         * Initialize handler for given component
         * @param cpt graphic component
         */
        fun install(cpt: JGraphicComponent?) {
            val handler = PanAndZoomHandler(cpt)
            cpt.addMouseListener(handler)
            cpt.addMouseMotionListener(handler)
            cpt.addMouseWheelListener(handler)
            cpt.getOverlays().add(handler)
        }
        //endregion
        //region TRANSFORMS
        /**
         * Get current boundaries displayed in component.
         * @param gc associated component
         * @return local bounds associated with the component
         */
        fun getLocalBounds(gc: JGraphicComponent?): Rectangle2D.Double? {
            var inverse = gc.getInverseTransform()
            if (inverse == null) {
                gc.setTransform(AffineTransform())
                inverse = AffineTransform()
            }
            // apply inverse transform to min point of component bounds and max point of component bounds
            val insets = gc.getInsets()
            val bounds = Rectangle(insets.left, insets.top,
                    gc.getWidth() - insets.left - insets.right, gc.getHeight() - insets.top - insets.bottom)
            val min = inverse.transform(Point2D.Double(bounds.minX, bounds.minY), null)
            val max = inverse.transform(Point2D.Double(bounds.maxX, bounds.maxY), null)
            return Rectangle2D.Double(min.x, min.y, max.x - min.x, max.y - min.y)
        }

        /**
         * Updates component transform so given rectangle is included within. Updates
         * to the component are made on the EDT.
         * @param comp associated component
         * @param rect local bounds
         */
        @InvokedFromThread("multiple")
        fun setDesiredLocalBounds(comp: JGraphicComponent?, rect: Rectangle2D?) {
            val insets = comp.getInsets()
            val bounds = Rectangle(insets.left, insets.top, comp.getWidth() - insets.left - insets.right, comp.getHeight() - insets.top - insets.bottom)
            setDesiredLocalBounds(comp, bounds, rect)
        }

        /**
         * Updates component transform so given rectangle is included within. Updates
         * to the component are made on the EDT. Allows setting custom bounds for the
         * component, in case the component is not yet visible or sized.
         * @param comp associated component
         * @param compBounds bounds to use for the component
         * @param rect local bounds
         */
        @InvokedFromThread("multiple")
        fun setDesiredLocalBounds(comp: JGraphicComponent?, compBounds: Rectangle?, rect: Rectangle2D?) {
            MoreSwingUtilities.invokeOnEventDispatchThread { comp.setTransform(AffineTransformBuilder.transformingTo(compBounds, rect)) }
        }
        //endregion
        //region ZOOM OPERATIONS
        /**
         * Cancel previous animation timer.
         * @param gc component for the zoom operation
         */
        private fun cancelZoomTimer(gc: JGraphicComponent?) {
            val timer = TIMERS.getIfPresent(gc)
            if (timer != null && timer.isRunning) {
                timer.stop()
                TIMERS.invalidate(gc)
            }
        }

        /**
         * Caches provided animation timer.
         * @param timer to cache
         * @param gc component for the zoom operation
         */
        private fun cacheZoomTimer(timer: Timer?, gc: JGraphicComponent?) {
            TIMERS.put(gc, timer)
        }

        /**
         * Sets bounds based on the zoom about a given point.
         * The effective zoom point is between current center and mouse position...
         * close to center =%gt; 100% at the given point, close to edge =%gt; 10% at
         * the given point.
         * @param gc associated component
         * @param localZoomPoint focal point for zoom
         * @param factor how much to zoom
         */
        fun zoomPoint(gc: JGraphicComponent?, localZoomPoint: Point2D?, factor: Double) {
            val localBounds = getLocalBounds(gc)
            val cx = .1 * localZoomPoint.getX() + .9 * localBounds.getCenterX()
            val cy = .1 * localZoomPoint.getY() + .9 * localBounds.getCenterY()
            val wx = localBounds.getWidth()
            val wy = localBounds.getHeight()
            setDesiredLocalBounds(gc, Rectangle2D.Double(
                    cx - .5 * factor * wx, cy - .5 * factor * wy,
                    factor * wx, factor * wy))
        }
        /**
         * Zooms in for the given component (about the center).
         * @param gc associated component
         * @param animate if true, result will animate
         * @return timer running the animation (null if not animating)
         */
        /**
         * Zooms in for the given component (about the center), animated.
         * @param gc associated component
         * @return timer running the animation
         */
        @JvmOverloads
        fun zoomIn(gc: JGraphicComponent?, animate: Boolean = true): Timer? {
            val rect = getLocalBounds(gc)
            val center = Point2D.Double(rect.getCenterX(), rect.getCenterY())
            return if (animate) {
                zoomCoordBoxAnimated(gc,
                        Point2D.Double(center.x - .25 * rect.getWidth(), center.y - .25 * rect.getHeight()),
                        Point2D.Double(center.x + .25 * rect.getWidth(), center.y + .25 * rect.getHeight()))
            } else {
                setDesiredLocalBounds(gc, Rectangle2D.Double(center.x - .25 * rect.getWidth(),
                        center.y - .25 * rect.getHeight(), .5 * rect.getWidth(), .5 * rect.getHeight()))
                null
            }
        }
        /**
         * Zooms out for the given component (about the center).
         * @param gc associated component
         * @param animate if true, result will animate
         * @return timer running the animation
         */
        /**
         * Zooms out for the given component (about the center), animated.
         * @param gc associated component
         * @return timer running the animation
         */
        @JvmOverloads
        fun zoomOut(gc: JGraphicComponent?, animate: Boolean = true): Timer? {
            val rect = getLocalBounds(gc)
            val center = Point2D.Double(rect.getCenterX(), rect.getCenterY())
            return if (animate) {
                zoomCoordBoxAnimated(gc,
                        Point2D.Double(center.x - rect.getWidth(), center.y - rect.getHeight()),
                        Point2D.Double(center.x + rect.getWidth(), center.y + rect.getHeight()))
            } else {
                setDesiredLocalBounds(gc, Rectangle2D.Double(center.x - rect.getWidth(),
                        center.y - rect.getHeight(), 2 * rect.getWidth(), 2 * rect.getHeight()))
                null
            }
        }

        /**
         * Creates an animating zoom using a particular timer, about the center of
         * the screen.
         * @param gc associated component
         * @param factor how far to zoom, representing the new scale
         * @return timer running the animation
         */
        fun zoomCenterAnimated(gc: JGraphicComponent?, factor: Double): Timer? {
            val rect = getLocalBounds(gc)
            val center = Point2D.Double(rect.getCenterX(), rect.getCenterY())
            return zoomPointAnimated(gc, center, factor)
        }

        /**
         * Creates an animating zoom using a particular timer.
         * @param gc associated component
         * @param p the coordinate of the point to center zoom about, in local
         * coordinates
         * @param factor how far to zoom, representing the new scale
         * @return timer running the animation
         */
        fun zoomPointAnimated(gc: JGraphicComponent?, p: Point2D.Double?, factor: Double): Timer? {
            cancelZoomTimer(gc)
            val rect = getLocalBounds(gc)
            val cx = .1 * p.x + .9 * rect.getCenterX()
            val cy = .1 * p.y + .9 * rect.getCenterY()
            val wx = rect.getWidth()
            val wy = rect.getHeight()
            val timer = AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, object : AnimationStep() {
                @InvokedFromThread("AnimationStep")
                override fun run(idx: Int, pct: Double) {
                    val zoomValue = 1.0 + (factor - 1.0) * pct
                    setDesiredLocalBounds(gc, Rectangle2D.Double(
                            cx - .5 * zoomValue * wx, cy - .5 * zoomValue * wy,
                            wx + zoomValue * wx, wy + zoomValue * wy))
                }
            })
            cacheZoomTimer(timer, gc)
            return timer
        }

        /**
         * Zooms to the boundaries of a particular box.
         * @param gc associated component
         * @param zoomBoxWindow the boundary of the zoom box (in window coordinates)
         * @return timer running the animation
         */
        fun zoomBoxAnimated(gc: JGraphicComponent?, zoomBoxWindow: Rectangle2D?): Timer? {
            return zoomCoordBoxAnimated(gc,
                    gc.toGraphicCoordinate(Point2D.Double(zoomBoxWindow.getMinX(), zoomBoxWindow.getMinY())),
                    gc.toGraphicCoordinate(Point2D.Double(zoomBoxWindow.getMaxX(), zoomBoxWindow.getMaxY())))
        }

        /**
         * Zooms to the boundaries of a particular box.
         * @param gc associated component
         * @param newMin min of zoom box
         * @param newMax max of zoom box
         * @return timer running the animation
         */
        fun zoomCoordBoxAnimated(gc: JGraphicComponent?, newMin: Point2D?, newMax: Point2D?): Timer? {
            cancelZoomTimer(gc)
            val rect = getLocalBounds(gc)
            val xMin = rect.getX()
            val yMin = rect.getY()
            val xMax = rect.getMaxX()
            val yMax = rect.getMaxY()
            val nxMin = newMin.getX()
            val nyMin = newMin.getY()
            val nxMax = newMax.getX()
            val nyMax = newMax.getY()
            val timer = AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, object : AnimationStep() {
                @InvokedFromThread("AnimationStep")
                override fun run(idx: Int, pct: Double) {
                    val x1 = xMin + (nxMin - xMin) * pct
                    val y1 = yMin + (nyMin - yMin) * pct
                    val x2 = xMax + (nxMax - xMax) * pct
                    val y2 = yMax + (nyMax - yMax) * pct
                    setDesiredLocalBounds(gc, Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1))
                }
            })
            cacheZoomTimer(timer, gc)
            return timer
        } //endregion
    }

    /**
     * Initialize with given component. This method is private as the
     * [.install]
     * method should be used.
     * @param comp component
     */
    init {
        component = Objects.requireNonNull(comp)
    }
}