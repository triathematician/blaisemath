package com.googlecode.blaisemath.graphics.swing
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
 */
import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.geom.*
import com.googlecode.blaisemath.util.swing.AnimationStep
import com.googlecode.blaisemath.util.swing.AnimationStep.Companion.animate
import com.googlecode.blaisemath.util.swing.invokeOnEventDispatchThread
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.Timer
import kotlin.math.abs

/**
 * Enables pan and zoom of a graphics canvas, by changing the [AffineTransform] associated with the canvas.
 * @author Elisha Peterson
 */
class PanAndZoomHandler private constructor(val component: JGraphicComponent) : MouseAdapter(), CanvasPainter<Graphics2D> {

    /** Hint box for zooming  */
    private var zoomBox: Rectangle2D.Double? = null
    /** Location mouse was first pressed at.  */
    private var pressedAt: Point? = null
    /** Stores keyboard modifiers for mouse.  */
    private var mode: String? = null
    /** Old bounds for the window.  */
    private var oldLocalBounds: Rectangle2D? = null

    override fun paint(component: Component, canvas: Graphics2D) {
        zoomBox?.let { ShapeRenderer().render(it, ZOOM_BOX_STYLE, canvas) }
    }

    //region MOUSE HANDLING

    private fun initMouseGesture(e: MouseEvent) {
        mode = MouseEvent.getModifiersExText(e.modifiersEx)
        if (RECTANGLE_RESIZE_MODE == mode || PAN_MODE == mode || RESTRICTED_MOVEMENT_MODE == mode) {
            pressedAt = e.point
        }
        if (RECTANGLE_RESIZE_MODE == mode) {
            zoomBox = rectangle2(pressedAt!!.x, pressedAt!!.y, 0, 0)
        } else if (PAN_MODE == mode || RESTRICTED_MOVEMENT_MODE == mode) {
            // pan mode
            oldLocalBounds = getLocalBounds(component)
        }
    }

    override fun mousePressed(e: MouseEvent) {
        if (!e.isConsumed()) initMouseGesture(e)
    }

    override fun mouseDragged(e: MouseEvent) {
        if (e.isConsumed) return
        if (pressedAt == null) initMouseGesture(e)

        val mouseMods = MouseEvent.getModifiersExText(e.modifiersEx)
        if (RECTANGLE_RESIZE_MODE == mode) {
            mouseDraggedResizeMode(e.point)
        } else if (PAN_MODE == mode || RESTRICTED_MOVEMENT_MODE == mode) {
            val restrictedMovementMode = RESTRICTED_MOVEMENT_MODE == mouseMods || RESTRICTED_MOVEMENT_MODE_ALT == mouseMods
            mouseDraggedPanMode(e.point, restrictedMovementMode)
        }
    }

    private fun mouseDraggedResizeMode(winPt: Point) {
        if (winPt.x < pressedAt!!.x) {
            zoomBox!!.x = winPt.x.toDouble()
            zoomBox!!.width = -winPt.x.toDouble() + pressedAt!!.x
        } else {
            zoomBox!!.x = pressedAt!!.x.toDouble()
            zoomBox!!.width = winPt.x.toDouble() - pressedAt!!.x
        }
        if (winPt.y < pressedAt!!.y) {
            zoomBox!!.y = winPt.y.toDouble()
            zoomBox!!.height = -winPt.y.toDouble() + pressedAt!!.y
        } else {
            zoomBox!!.y = pressedAt!!.y.toDouble()
            zoomBox!!.height = winPt.y.toDouble() - pressedAt!!.y
        }
        component.repaint()
    }

    private fun mouseDraggedPanMode(winPt: Point, restrictedMovementMode: Boolean) {
        if (restrictedMovementMode) {
            if (abs(winPt.y - pressedAt!!.y) < abs(winPt.x - pressedAt!!.x)) {
                winPt.y = pressedAt!!.y
            } else {
                winPt.x = pressedAt!!.x
            }
        }
        val dx = (winPt.x - pressedAt!!.x) * component.getInverseTransform()!!.scaleX
        val dy = (winPt.y - pressedAt!!.y) * component.getInverseTransform()!!.scaleY
        setDesiredLocalBounds(component, oldLocalBounds!! - Point2(dx, dy))
    }

    override fun mouseReleased(e: MouseEvent) {
        if (!e.isConsumed) {
            mouseDragged(e)
            if (pressedAt != null && RECTANGLE_RESIZE_MODE == mode) zoomBoxAnimated(component, zoomBox!!)
        }
        zoomBox = null
        pressedAt = null
        oldLocalBounds = null
        mode = null
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        if (e.isConsumed) return
        val mouseLoc = point2(e.x, e.y)

        // ensure the point is within the window
        val bounds = component.bounds
        mouseLoc.x = Math.max(mouseLoc.x, bounds.getMinX())
        mouseLoc.x = Math.min(mouseLoc.x, bounds.getMaxX())
        mouseLoc.y = Math.max(mouseLoc.y, bounds.getMinY())
        mouseLoc.y = Math.min(mouseLoc.y, bounds.getMaxY())
        zoomPoint(component, component.toGraphicCoordinate(mouseLoc), if (e.wheelRotation > 0) 1.05 else 0.95)
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

//        /** Cache of recent animation timers  */
//        private val TIMERS: Cache<JGraphicComponent, Timer> = CacheBuilder.newBuilder()
//                .expireAfterWrite(10, TimeUnit.SECONDS)
//                .build()

        /**
         * Initialize handler for given component
         */
        fun install(cpt: JGraphicComponent) {
            val handler = PanAndZoomHandler(cpt)
            cpt.addMouseListener(handler)
            cpt.addMouseMotionListener(handler)
            cpt.addMouseWheelListener(handler)
            cpt.overlays.add(handler)
        }

        //endregion

        //region TRANSFORMS

        /**
         * Get current boundaries displayed in component.
         * @param gc associated component
         * @return local bounds associated with the component
         */
        fun getLocalBounds(gc: JGraphicComponent): Rectangle2 {
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
         * Updates component transform so given rectangle is included within. Updates to the component are made on the EDT.
         */
        @InvokedFromThread("multiple")
        fun setDesiredLocalBounds(comp: JGraphicComponent, rect: Rectangle2D) {
            val insets = comp.insets
            val bounds = Rectangle(insets.left, insets.top, comp.width - insets.left - insets.right, comp.height - insets.top - insets.bottom)
            setDesiredLocalBounds(comp, bounds, rect)
        }

        /**
         * Updates component transform so given rectangle is included within. Updates to the component are made on the EDT.
         * Allows setting custom bounds for the component, in case the component is not yet visible or sized.
         */
        @InvokedFromThread("multiple")
        fun setDesiredLocalBounds(comp: JGraphicComponent, compBounds: Rectangle, rect: Rectangle2D) {
            invokeOnEventDispatchThread { comp.setTransform(scaleRectTransform(compBounds, rect)) }
        }

        /**
         * Create a transform that maps the "scaleFrom" rectangle into the "scaleTo" region.
         * @param scaleTo region to scale to
         * @param scaleFrom region to scale from
         * @return transform
         */
        fun scaleRectTransform(scaleTo: Rectangle2D, scaleFrom: Rectangle2D): AffineTransform {
            if (scaleTo.getWidth() == 0.0 || scaleTo.getHeight() == 0.0 || scaleFrom.getWidth() == 0.0 || scaleFrom.getHeight() == 0.0) {
                LOG.log(Level.INFO, "Scaling with zero area rectangles: {0}, {1}. Returning identity transform.", arrayOf<Any?>(scaleFrom, scaleTo))
                return AffineTransform()
            }
            val scaleX = scaleFrom.getWidth() / scaleTo.getWidth()
            val scaleY = scaleFrom.getHeight() / scaleTo.getHeight()
            val scale = Math.max(scaleX, scaleY)
            val res = AffineTransform()
            res.translate(scaleTo.getCenterX(), scaleTo.getCenterY())
            res.scale(1 / scale, 1 / scale)
            res.translate(-scaleFrom.getCenterX(), -scaleFrom.getCenterY())
            return res
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
         * Sets bounds based on the zoom about a given point. The effective zoom point is between current center and mouse position...
         * close to center =%gt; 100% at the given point, close to edge =%gt; 10% at the given point.
         */
        fun zoomPoint(gc: JGraphicComponent, localZoomPoint: Point2D, factor: Double) {
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
         * Zooms in for the given component (about the center), animated.
         */
        @JvmOverloads
        fun zoomIn(gc: JGraphicComponent, animate: Boolean = true): Timer? {
            val rect = getLocalBounds(gc)
            val center = rect.center
            return if (animate) {
                zoomCoordBoxAnimated(gc, center - Point2(.25 * rect.getWidth(), .25 * rect.getHeight()),
                        center + Point2(.25 * rect.getWidth(), +.25 * rect.getHeight()))
            } else {
                setDesiredLocalBounds(gc, Rectangle2(center.x - .25 * rect.getWidth(), center.y - .25 * rect.getHeight(), .5 * rect.getWidth(), .5 * rect.getHeight()))
                null
            }
        }

        /**
         * Zooms out for the given component (about the center), animated.
         */
        @JvmOverloads
        fun zoomOut(gc: JGraphicComponent, animate: Boolean = true): Timer? {
            val rect = getLocalBounds(gc)
            val center = rect.center
            return if (animate) {
                zoomCoordBoxAnimated(gc,center - Point2(rect.getWidth(), rect.getHeight()), center + Point2(rect.getWidth(), rect.getHeight()))
            } else {
                setDesiredLocalBounds(gc, Rectangle2(center.x - rect.getWidth(), center.y - rect.getHeight(), 2 * rect.getWidth(), 2 * rect.getHeight()))
                null
            }
        }

        /**
         * Creates an animating zoom using a particular timer, about the center of the screen.
         */
        fun zoomCenterAnimated(gc: JGraphicComponent, factor: Double): Timer? {
            val rect = getLocalBounds(gc)
            return zoomPointAnimated(gc, rect.center, factor)
        }

        /**
         * Creates an animating zoom using a particular timer.
         */
        fun zoomPointAnimated(gc: JGraphicComponent, p: Point2, factor: Double): Timer? {
            cancelZoomTimer(gc)
            val rect = getLocalBounds(gc)
            val cx = .1 * p.x + .9 * rect.centerX
            val cy = .1 * p.y + .9 * rect.centerY
            val wx = rect.getWidth()
            val wy = rect.getHeight()
            val timer = animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, object : AnimationStep() {
                @InvokedFromThread("AnimationStep")
                override fun run(idx: Int, pct: Double) {
                    val zoomValue = 1.0 + (factor - 1.0) * pct
                    setDesiredLocalBounds(gc, Rectangle2(
                            cx - .5 * zoomValue * wx, cy - .5 * zoomValue * wy,
                            wx + zoomValue * wx, wy + zoomValue * wy))
                }
            })
            cacheZoomTimer(timer, gc)
            return timer
        }

        /**
         * Zooms to the boundaries of a particular box.
         */
        fun zoomBoxAnimated(gc: JGraphicComponent, zoomBoxWindow: Rectangle2D) = zoomCoordBoxAnimated(gc,
                gc.toGraphicCoordinate(zoomBoxWindow.min), gc.toGraphicCoordinate(zoomBoxWindow.max))

        /**
         * Zooms to the boundaries of a particular box.
         */
        fun zoomCoordBoxAnimated(gc: JGraphicComponent, newMin: Point2D, newMax: Point2D): Timer? {
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
            val timer = animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, object : AnimationStep() {
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
        }

        //endregion
    }
}