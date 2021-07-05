package com.googlecode.blaisemath.graphics.swing

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.googlecode.blaisemath.geom.TransformedCoordinateSpace
import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.GraphicMouseEvent
import com.googlecode.blaisemath.graphics.GraphicUtils
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.StyleContext
import com.googlecode.blaisemath.util.SetSelectionModel
import com.googlecode.blaisemath.util.swing.CanvasPainter
import junit.framework.TestCase
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.function.Consumer
import javax.swing.JComponent

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
 * Swing component that collects and draws shapes on a screen.
 * The shapes and their styles are enclosed within a [JGraphicRoot] class,
 * which also sets up the requisite mouse handling and manages the drawing.
 *
 * @see JGraphicRoot
 *
 *
 * @author Elisha Peterson
 */
class JGraphicComponent : JComponent(), TransformedCoordinateSpace {
    /** The visible shapes.  */
    protected val root: JGraphicRoot?

    /** Affine transform applied to graphics canvas before drawing (enables pan and zoom).  */
    protected var transform: AffineTransform? = null

    /** Store inverse transform  */
    protected var inverseTransform: AffineTransform? = null

    /** Underlay painters  */
    protected val underlays: MutableList<CanvasPainter<*>?>? = Lists.newArrayList()

    /** Overlay painters  */
    protected val overlays: MutableList<CanvasPainter<*>?>? = Lists.newArrayList()

    /** Used for selecting graphics  */
    protected val selector: JGraphicSelectionHandler<*>? = JGraphicSelectionHandler<Any?>(this)

    /** Whether antialias is enabled  */
    protected var antialias = true
    //region PROPERTIES
    /**
     * Return graphic root managing the shapes to be rendered
     * @return shapes root
     */
    fun getGraphicRoot(): JGraphicRoot? {
        return root
    }

    /**
     * Return the default render factory used to draw shapes
     * @return current style provider used to draw shapes in the component
     */
    fun getStyleContext(): StyleContext? {
        return root.getStyleContext()
    }

    /**
     * Sets the default render factory used to draw shapes
     * @param factory render factory
     * @throws IllegalArgumentException if the factory is null
     */
    fun setStyleContext(factory: StyleContext?) {
        root.setStyleContext(factory)
    }

    /**
     * If the mouse control allowing for selection of graphic objects is currently active
     * @return true if enabled, false if not
     */
    fun isSelectionEnabled(): Boolean {
        return selector.isSelectionEnabled()
    }

    /**
     * Enable/disable the mouse control allowing for selection of graphic objects.
     * @param b true to enable, false to disable
     */
    fun setSelectionEnabled(b: Boolean) {
        selector.setSelectionEnabled(b)
    }

    fun getSelectionModel(): SetSelectionModel<Graphic<Graphics2D?>?>? {
        return selector.getSelectionModel()
    }

    /**
     * Return true if antialias is enabled
     * @return antialias setting
     */
    fun isAntialiasOn(): Boolean {
        return antialias
    }

    /**
     * Sets antialias status
     * @param aa antialias status
     */
    fun setAntialiasOn(aa: Boolean) {
        antialias = aa
        repaint()
    }
    //endregion
    //region GRAPHICS MUTATORS
    /**
     * Add graphics to the component
     * @param add graphics to add
     */
    fun addGraphics(add: Iterable<out Graphic<Graphics2D?>?>?) {
        root.addGraphics(add)
    }

    /**
     * Add a single graphic to the component
     * @param gfc graphic to add
     */
    fun addGraphic(gfc: Graphic<Graphics2D?>?) {
        root.addGraphic(gfc)
    }

    /**
     * Remove graphics from the component
     * @param remove graphics to remove
     */
    fun removeGraphics(remove: Iterable<out Graphic<Graphics2D?>?>?) {
        root.removeGraphics(remove)
    }

    /**
     * Remove a single graphic from the component
     * @param gfc graphic to remove
     */
    fun removeGraphic(gfc: Graphic<Graphics2D?>?) {
        root.removeGraphic(gfc)
    }

    /**
     * Remove all graphics from the component.
     */
    fun clearGraphics() {
        root.clearGraphics()
    }

    //endregion
    //region CANVAS TRANSFORM
    override fun getTransform(): AffineTransform? {
        return transform
    }

    override fun getInverseTransform(): AffineTransform? {
        return inverseTransform
    }

    /**
     * Set the transform used for drawing objects on the canvas.
     * @param at the transform (null for identity transform)
     * @throws IllegalArgumentException if the transform is non-null but not invertible
     */
    override fun setTransform(at: AffineTransform?) {
        Preconditions.checkArgument(at == null || at.determinant != 0.0)
        val old = transform
        if (old !== at) {
            transform = at
            inverseTransform = try {
                at?.createInverse()
            } catch (ex: NoninvertibleTransformException) {
                throw IllegalStateException("Already checked that the argument is invertible...", ex)
            }
            firePropertyChange(P_TRANSFORM, old, at)
            repaint()
        }
    }

    /**
     * Reset transform to the default.
     */
    fun resetTransform() {
        setTransform(null)
    }
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The insets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     *
     * @param outsets additional space to leave around the graphics
     * @param animate if true, zoom operation will animate
     */
    //endregion
    //region ZOOM OPERATIONS
    /**
     * Set transform to include all components in the graphic tree. Does nothing
     * if there are no graphics. Animates zoom operation.
     */
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The insets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Animates zoom operation.
     *
     * @param outsets additional space to leave around the graphics
     */
    @JvmOverloads
    fun zoomToAll(outsets: Insets? = Insets(0, 0, 0, 0), animate: Boolean = true) {
        val bounds = getGraphicRoot().boundingBox(canvas())
        if (bounds != null && animate) {
            animatedZoomWithOutsets(bounds, outsets)
        } else bounds?.let { instantZoomWithOutsets(it, outsets) }
    }

    /**
     * Zooms in in to the graphics canvas (animated).
     */
    fun zoomIn() {
        PanAndZoomHandler.Companion.zoomIn(this, true)
    }

    /**
     * Zooms in in to the graphics canvas.
     * @param animate if true, zoom operation will animate
     */
    fun zoomIn(animate: Boolean) {
        PanAndZoomHandler.Companion.zoomIn(this, animate)
    }

    /**
     * Zooms out of the graphics canvas (animated).
     */
    fun zoomOut() {
        PanAndZoomHandler.Companion.zoomOut(this, true)
    }

    /**
     * Zooms out of the graphics canvas.
     * @param animate if true, zoom operation will animate
     */
    fun zoomOut(animate: Boolean) {
        PanAndZoomHandler.Companion.zoomOut(this, animate)
    }
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The outsets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Zoom is animated.
     *
     * @param locCoordOutsets additional space to leave around the graphics (in local coordinate space)
     * @param animate if true, zoom operation will animate
     */
    /**
     * Set transform to include all selected components. Does nothing if nothing
     * is selected. Zoom is animated.
     */
    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The outsets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Zoom is animated.
     *
     * @param locCoordOutsets additional space to leave around the graphics (in local coordinate space)
     */
    @JvmOverloads
    fun zoomToSelected(locCoordOutsets: Insets? = Insets(0, 0, 0, 0), animate: Boolean = true) {
        val bounds = GraphicUtils.boundingBox(getSelectionModel().getSelection(), canvas())
        if (bounds != null && animate) {
            animatedZoomWithOutsets(bounds, locCoordOutsets)
        } else bounds?.let { instantZoomWithOutsets(it, locCoordOutsets) }
    }

    /**
     * Utility method to animate the zoom operation to the target local bounds.
     * @param bounds local bounds
     * @param locCoordOutsets outsets beyond the local bounds
     */
    private fun animatedZoomWithOutsets(bounds: Rectangle2D?, locCoordOutsets: Insets?) {
        val minX = bounds.getMinX() - locCoordOutsets.left
        val maxX = Math.max(minX, bounds.getMaxX() + locCoordOutsets.right)
        val minY = bounds.getMinY() - locCoordOutsets.top
        val maxY = Math.max(minY, bounds.getMaxY() + locCoordOutsets.bottom)
        PanAndZoomHandler.Companion.zoomCoordBoxAnimated(this,
                Point2D.Double(minX, minY),
                Point2D.Double(maxX, maxY))
    }

    /**
     * Utility method to instantly change the zoom to the target local bounds.
     * @param bounds local bounds
     * @param locCoordOutsets outsets beyond the local bounds
     */
    private fun instantZoomWithOutsets(bounds: Rectangle2D?, locCoordOutsets: Insets?) {
        val minX = bounds.getMinX() - locCoordOutsets.left
        val maxX = Math.max(minX, bounds.getMaxX() + locCoordOutsets.right)
        val minY = bounds.getMinY() - locCoordOutsets.top
        val maxY = Math.max(minY, bounds.getMaxY() + locCoordOutsets.bottom)
        PanAndZoomHandler.Companion.setDesiredLocalBounds(this,
                Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY))
    }
    //endregion
    //region GRAPHICS QUERIES
    /**
     * Return the tooltip associated with the mouse event's point.
     * This will look for the topmost [Graphic] beneath the mouse and return that.
     * @param event the event with the point for the tooltip
     * @return tooltip for the point
     */
    override fun getToolTipText(event: MouseEvent?): String? {
        val ct = root.getTooltip(toGraphicCoordinate(event.getPoint()), null)
        return ct ?: if ("" == super.getToolTipText()) null else super.getToolTipText()
    }

    /**
     * Convert window point location to graphic root location
     * @param winLoc window location
     * @return graphic coordinate system location
     */
    override fun toGraphicCoordinate(winLoc: Point2D?): Point2D? {
        return if (inverseTransform == null) winLoc else inverseTransform.transform(winLoc, null)
    }

    /**
     * Convert mouse event to local coordinate space
     * @param winEvent event in windows coordinate space
     * @return event w/ location in local coordinate space
     */
    fun toGraphicCoordinateSpace(winEvent: MouseEvent?): GraphicMouseEvent? {
        val loc = toGraphicCoordinate(winEvent.getPoint())
        return GraphicMouseEvent(winEvent, loc, null)
    }

    /**
     * Return the graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    fun graphicAt(winLoc: Point?): Graphic<Graphics2D?>? {
        return root.graphicAt(toGraphicCoordinate(winLoc), canvas())
    }

    /**
     * Return the functional graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    fun functionalGraphicAt(winLoc: Point?): Graphic<*>? {
        return root.mouseGraphicAt(toGraphicCoordinate(winLoc), canvas())
    }

    /**
     * Return the selectable graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    fun selectableGraphicAt(winLoc: Point?): Graphic<*>? {
        return root.selectableGraphicAt(toGraphicCoordinate(winLoc), canvas())
    }
    //endregion
    //region PAINT
    /**
     * Get instance of canvas to use for style location checking.
     * @return canvas
     */
    fun canvas(): Graphics2D? {
        // TODO
        return null
    }

    /**
     * Return modifiable list of overlay painters
     * @return list
     */
    fun getOverlays(): MutableList<CanvasPainter<*>?>? {
        return overlays
    }

    /**
     * Return modifiable list of underlay painters
     * @return list
     */
    fun getUnderlays(): MutableList<CanvasPainter<*>?>? {
        return underlays
    }

    /**
     * Paints the graphics to the specified canvas.
     * @param g graphics object
     */
    override fun paintChildren(g: Graphics?) {
        renderTo(g as Graphics2D?)
        super.paintChildren(g)
    }

    /**
     * Renders all shapes in root to specified graphics object.
     * @param canvas graphics canvas to render to
     */
    fun renderTo(canvas: Graphics2D?) {
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                if (antialias) RenderingHints.VALUE_ANTIALIAS_ON else RenderingHints.VALUE_ANTIALIAS_OFF)
        if (isOpaque) {
            canvas.setColor(background)
            canvas.fillRect(0, 0, width, height)
        }
        renderUnderlay(canvas)
        if (transform == null) {
            root.renderTo(canvas)
        } else {
            val priorTransform = canvas.getTransform()
            canvas.transform(transform)
            root.renderTo(canvas)
            canvas.setTransform(priorTransform)
        }
        renderOverlay(canvas)
    }

    /**
     * Hook to render underlay elements. Called after the background is drawn,
     * but before anything else.
     * @param canvas the canvas to render to
     */
    protected fun renderUnderlay(canvas: Graphics2D?) {
        underlays.forEach(Consumer { p: CanvasPainter<*>? -> p.paint(this, canvas) })
    }

    /**
     * Hook to render overlay elements. Called after everything else is drawn.
     * @param canvas the canvas to render to
     */
    protected fun renderOverlay(canvas: Graphics2D?) {
        overlays.forEach(Consumer { p: CanvasPainter<*>? -> p.paint(this, canvas) })
    } //endregion

    companion object {
        val P_TRANSFORM: String? = "transform"
    }

    /**
     * Construction of a generic graphics view component.
     */
    init {
        root = JGraphicRoot(this)
        selector.setSelectionEnabled(false)
        addMouseListener(selector)
        addMouseMotionListener(selector)
        overlays.add(selector)
        isDoubleBuffered = true
        background = Color.WHITE
        isOpaque = true
        preferredSize = Dimension(300, 200)
        // this line enables tooltips
        toolTipText = ""
    }
}