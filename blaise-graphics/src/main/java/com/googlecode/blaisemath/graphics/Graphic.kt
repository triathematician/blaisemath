package com.googlecode.blaisemath.graphics

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import junit.framework.TestCase
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.*
import javax.swing.JPopupMenu
import javax.swing.event.EventListenerList

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
 * An object along with style and renderer information allowing it to be drawn
 * on a graphics canvas. Key additional features are:
 *
 *  * A *parent* (via get and set methods), which is a [GraphicComposite] and provides access to
 * default styles of various types.
 *  * Visibility settings (via get and set methods). See [StyleHints] for the parameters.
 *  * Three methods based on a point on the canvas:
 *
 *  *  [.boundingBox], providing a box that encloses the graphic
 *  *  [.contains], testing whether the entry contains a point
 *  *  [.getTooltip], returning the tooltip for a point (or null)
 *
 *
 *
 * Implementations must provide the object to be rendered, as well as the
 * render functionality, and they must implement their own drag functionality.
 *
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G> */
abstract class Graphic<G> {
    /** Stores the parent of this entry  */
    protected var parent: GraphicComposite<G?>? = null

    /** Modifiers (ordered) that are applied to the style before drawing.  */
    protected val styleHints: MutableSet<String?>? = Sets.newLinkedHashSet()

    /** Default text of tooltip  */
    protected var defaultTooltip: String? = null

    /** Context initializers  */
    protected val contextMenuInitializers: MutableList<ContextMenuInitializer<Graphic<G?>?>?>? = Lists.newArrayList()

    /** Adds highlights to the graphic on mouseover.  */
    protected val highlighter: GraphicMouseHighlightHandler? = GraphicMouseHighlightHandler()

    /** Handles property listening  */
    protected val pcs: PropertyChangeSupport? = PropertyChangeSupport(this)

    /** Stores event eventHandlers for the entry  */
    protected val eventHandlers: EventListenerList? = EventListenerList()
    //region PROPERTIES
    /**
     * Return parent of the entry
     * @return parent, possibly null
     */
    fun getParent(): GraphicComposite<*>? {
        return parent
    }

    /**
     * Set parent of the entry
     * @param p the new parent, possibly null
     */
    open fun setParent(p: GraphicComposite<*>?) {
        parent = p
    }

    /**
     * Return set of style hints for the graphic.
     * @return style hints
     */
    fun getStyleHints(): MutableSet<String?>? {
        return Collections.unmodifiableSet(styleHints)
    }

    /**
     * Sets style hints of graphic
     * @param hints new style hints
     */
    fun setStyleHints(vararg hints: String?) {
        setStyleHints(Arrays.asList(*hints))
    }

    /**
     * Sets style hints of graphic
     * @param hints new style hints
     */
    fun setStyleHints(hints: Iterable<String?>?) {
        styleHints.clear()
        Iterables.addAll(styleHints, hints)
        fireGraphicChanged()
    }

    /**
     * Set status of a particular visibility hint.
     * @param hint hint
     * @param status status of hint
     */
    fun setStyleHint(hint: String?, status: Boolean) {
        val change = if (status) styleHints.add(hint) else styleHints.remove(hint)
        if (change) {
            fireGraphicChanged()
        }
    }

    /**
     * Whether graphic supports context menu building
     * @return true if yes
     */
    fun isContextMenuEnabled(): Boolean {
        return styleHints.contains(HINT_POPUP_ENABLED)
    }

    fun setContextMenuEnabled(`val`: Boolean) {
        setStyleHint(HINT_POPUP_ENABLED, `val`)
    }

    fun addContextMenuInitializer(init: ContextMenuInitializer<Graphic<G?>?>?) {
        if (!contextMenuInitializers.contains(init)) {
            contextMenuInitializers.add(init)
            setContextMenuEnabled(true)
        }
    }

    fun removeContextMenuInitializer(init: ContextMenuInitializer<Graphic<G?>?>?) {
        contextMenuInitializers.remove(init)
        if (contextMenuInitializers.isEmpty()) {
            setContextMenuEnabled(false)
        }
    }

    fun clearContextMenuInitializers() {
        contextMenuInitializers.clear()
        setContextMenuEnabled(false)
    }

    /**
     * Return true if graphic can be selected. If this flag is set to true, the
     * locator API will be used to map selection gestures (e.g. click to select,
     * or select graphics in box).
     * @return selection flag
     */
    fun isSelectionEnabled(): Boolean {
        return styleHints.contains(HINT_SELECTION_ENABLED)
    }

    fun setSelectionEnabled(`val`: Boolean) {
        setStyleHint(HINT_SELECTION_ENABLED, `val`)
    }

    fun isHighlightEnabled(): Boolean {
        return Arrays.asList(*eventHandlers.getListenerList()).contains(highlighter)
    }

    fun setHighlightEnabled(`val`: Boolean) {
        if (`val` != isHighlightEnabled()) {
            if (`val`) {
                addMouseListener(highlighter)
            } else {
                removeMouseListener(highlighter)
            }
        }
    }

    /**
     * Return true if tips are enabled/supported
     * @return true if yes
     */
    fun isTooltipEnabled(): Boolean {
        return styleHints.contains(HINT_TOOLTIP_ENABLED)
    }

    fun setTooltipEnabled(`val`: Boolean) {
        setStyleHint(HINT_TOOLTIP_ENABLED, `val`)
    }

    /**
     * Return the default tooltip for this object
     * @return tip
     */
    fun getDefaultTooltip(): String? {
        return defaultTooltip
    }

    /**
     * Sets the tooltip for this entry. Also updates the enabled tip flag to true.
     * @param tooltip the tooltip
     */
    fun setDefaultTooltip(tooltip: String?) {
        setTooltipEnabled(true)
        defaultTooltip = tooltip
    }

    /**
     * Whether the object should receive mouse events.
     * @return true if yes, false otherwise
     */
    fun isMouseDisabled(): Boolean {
        return styleHints.contains(HINT_MOUSE_DISABLED)
    }

    fun setMouseDisabled(`val`: Boolean) {
        setStyleHint(HINT_MOUSE_DISABLED, `val`)
    }
    //endregion
    //region COMPUTED PROPERTIES and LOOKUPS
    /**
     * Return style attributes of the graphic to be used for rendering.
     * The result will have all style hints automatically applied. Any attributes
     * of the parent style are inherited.
     *
     * @return style
     */
    fun renderStyle(): AttributeSet? {
        var renderStyle = getStyle()
        if (renderStyle == null) {
            renderStyle = AttributeSet()
        }
        val renderHints = getStyleHints()
        if (parent != null) {
            val parStyle = parent.getStyle()
            if (parStyle != null && parStyle !== renderStyle.parent.orElse(null)) {
                renderStyle = renderStyle.flatCopy().immutableWithParent(parStyle)
            }
            val parStyleHints = parent.getStyleHints()
            val useHints = if (parStyleHints == null) renderHints else Sets.union(renderHints, parStyleHints)
            renderStyle = parent.getStyleContext().applyModifiers(renderStyle, useHints)
        }
        return renderStyle
    }

    /**
     * Initialize the context menu by adding any actions appropriate for the given parameters.
     * @param menu context menu
     * @param src source graphic displaying the context menu
     * @param point mouse location
     * @param focus focus graphic
     * @param selection selected graphics
     * @param canvas graphics canvas
     */
    open fun initContextMenu(menu: JPopupMenu?, src: Graphic<G?>?, point: Point2D?, focus: Any?, selection: MutableSet<Graphic<G?>?>?, canvas: G?) {
        for (cmi in contextMenuInitializers) {
            cmi.initContextMenu(menu, src, point, focus, selection)
        }
    }

    /**
     * Return tooltip for the specified point
     * @param p the point
     * @param canvas canvas
     * @return the tooltip at the specified location (may be null)
     */
    open fun getTooltip(p: Point2D?, canvas: G?): String? {
        return if (isTooltipEnabled()) defaultTooltip else null
    }
    //endregion
    //region ABSTRACT METHODS - STYLE, RENDER, POSITION
    /**
     * Return style set of this graphic
     * @return graphic style
     */
    abstract fun getStyle(): AttributeSet?

    /**
     * Draws the primitive on the specified graphics canvas, using current style.
     * @param canvas graphics canvas
     */
    abstract fun renderTo(canvas: G?)

    /**
     * Method that provides the bounding box enclosing the graphic.
     * @return bounding box
     * @param canvas where graphic is rendered, or null if not rendered
     */
    abstract fun boundingBox(canvas: G?): Rectangle2D?

    /**
     * Method used to determine whether the graphic receives mouse events
     * and will be asked to provide a tooltip at the given point. The graphic's
     * [MouseListener]s and [MouseMotionListener]s will have the
     * opportunity to receive events if the graphic is the topmost element
     * containing the event's point.
     *
     * @param point the window point
     * @param canvas where graphic is rendered
     * @return true if the entry contains the point, else false
     */
    abstract fun contains(point: Point2D?, canvas: G?): Boolean

    /**
     * Checks to see if the graphic intersects the area within specified
     * rectangle.
     *
     * @param box rectangle to check against
     * @param canvas where graphic is rendered
     * @return true if it intersects, false otherwise
     */
    abstract fun intersects(box: Rectangle2D?, canvas: G?): Boolean
    //endregion
    //region EVENTS
    /** Notify interested listeners of a change.  */
    protected open fun fireGraphicChanged() {
        if (parent != null) {
            parent.graphicChanged(this)
        }
    }

    fun addPropertyChangeListener(pl: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(pl)
    }

    fun addPropertyChangeListener(string: String?, pl: PropertyChangeListener?) {
        pcs.addPropertyChangeListener(string, pl)
    }

    fun removePropertyChangeListener(pl: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(pl)
    }

    fun removePropertyChangeListener(string: String?, pl: PropertyChangeListener?) {
        pcs.removePropertyChangeListener(string, pl)
    }

    /**
     * Adds a mouse listener to the graphic
     * @param handler listener
     */
    fun addMouseListener(handler: MouseListener?) {
        Objects.requireNonNull(handler)
        eventHandlers.add(MouseListener::class.java, handler)
    }

    /**
     * Removes a mouse listener from the graphic
     * @param handler listener
     */
    fun removeMouseListener(handler: MouseListener?) {
        eventHandlers.remove(MouseListener::class.java, handler)
    }

    /**
     * Return list of mouse listeners registered with the graphic
     * @return listeners
     */
    fun getMouseListeners(): Array<MouseListener?>? {
        return eventHandlers.getListeners(MouseListener::class.java)
    }

    fun removeMouseListeners() {
        for (m in getMouseListeners()) {
            eventHandlers.remove(MouseListener::class.java, m)
        }
    }

    /**
     * Adds a mouse motion listener to the graphic
     * @param handler listener
     */
    fun addMouseMotionListener(handler: MouseMotionListener?) {
        Objects.requireNonNull(handler)
        eventHandlers.add(MouseMotionListener::class.java, handler)
    }

    /**
     * Removes a mouse motion listener from the graphic
     * @param handler listener
     */
    fun removeMouseMotionListener(handler: MouseMotionListener?) {
        eventHandlers.remove(MouseMotionListener::class.java, handler)
    }

    /**
     * Return list of mouse motion listeners registered with the graphic
     * @return listeners
     */
    fun getMouseMotionListeners(): Array<MouseMotionListener?>? {
        return eventHandlers.getListeners(MouseMotionListener::class.java)
    }

    fun removeMouseMotionListeners() {
        for (m in getMouseMotionListeners()) {
            eventHandlers.remove(MouseMotionListener::class.java, m)
        }
    } //endregion

    companion object {
        val HINT_SELECTION_ENABLED: String? = "selection-enabled"
        val HINT_TOOLTIP_ENABLED: String? = "tooltip-enabled"
        val HINT_POPUP_ENABLED: String? = "popupmenu-enabled"
        val HINT_MOUSE_DISABLED: String? = "mouse-disabled"
    }

    /**
     * Initialize graphic
     */
    init {
        addMouseListener(highlighter)
    }
}