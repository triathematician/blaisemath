package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
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
 */

/**
 * An object along with style and renderer information allowing it to be drawn on a graphics canvas. Key additional features are:
 *
 *  - A *parent* (via get and set methods), which is a [GraphicComposite] and provides access to default styles of various types.
 *  - Visibility settings (via get and set methods). See [StyleHints] for the parameters.
 *  - Three methods based on a point on the canvas:
 *     - [.boundingBox], providing a box that encloses the graphic
 *     - [.contains], testing whether the entry contains a point
 *     - [.getTooltip], returning the tooltip for a point (or null)

 * Implementations must provide the object to be rendered, as well as the render functionality, and they must implement their own drag functionality.
 *
 * @param <G> type of graphics canvas to render to
 */
abstract class Graphic<G : Any> {

    /** Stores the parent of this entry  */
    var parent: GraphicComposite<G>? = null

    /** Modifiers (ordered) that are applied to the style before drawing. */
    private val _styleHints = mutableSetOf<String>()
    /** Immutable public view of style hints. */
    val styleHints
        get() = _styleHints.toSet()

    /** Default text of tooltip  */
    var defaultTooltip: String? = null
        set(value) {
            field = value
            isTooltipEnabled = value != null
        }

    /** Context initializers  */
    private val contextMenuInitializers = mutableListOf<ContextMenuInitializer<Graphic<G>>>()
    /** Adds highlights to the graphic on mouseover.  */
    private val highlighter = HighlightOnMouseoverHandler().also { addMouseListener(it) }

    /** Handles property listening  */
    protected val pcs = PropertyChangeSupport(this)
    /** Stores event eventHandlers for the entry  */
    private val eventHandlers = EventListenerList()

    //region STYLE HINT PROPERTIES

    /** Sets style hints of graphic. */
    fun setStyleHints(hints: List<String>) {
        _styleHints.clear()
        _styleHints.addAll(hints)
        fireGraphicChanged()
    }
    /** Set status of a particular visibility hint. */
    fun setStyleHint(hint: String, status: Boolean) {
        val change = if (status) _styleHints.add(hint) else _styleHints.remove(hint)
        if (change) fireGraphicChanged()
    }

    /** Whether graphic supports context menu building */
    var isContextMenuEnabled
        get() = HINT_POPUP_ENABLED in styleHints
        set(value) = setStyleHint(HINT_POPUP_ENABLED, value)

    /** Add context menu builder. */
    fun addContextMenuInitializer(init: ContextMenuInitializer<Graphic<G>>) {
        if (!contextMenuInitializers.contains(init)) {
            contextMenuInitializers.add(init)
            isContextMenuEnabled = true
        }
    }
    /** Remove context menu builder. */
    fun removeContextMenuInitializer(init: ContextMenuInitializer<Graphic<G>>) {
        contextMenuInitializers.remove(init)
        if (contextMenuInitializers.isEmpty()) {
            isContextMenuEnabled = false
        }
    }
    /** Clear context menu builders. */
    fun clearContextMenuInitializers() {
        contextMenuInitializers.clear()
        isContextMenuEnabled = false
    }

    /** Selectability of graphic; influences UI. */
    var isSelectionEnabled
        get() = HINT_SELECTION_ENABLED in styleHints
        set(value) = setStyleHint(HINT_SELECTION_ENABLED, value)

    /** Highlight flag of graphic; influences UI. */
    var isHighlightEnabled
        get() = highlighter in eventHandlers.listenerList
        set(value) {
            if (value && highlighter !in eventHandlers.listenerList) addMouseListener(highlighter)
            else if (!value && highlighter in eventHandlers.listenerList) removeMouseListener(highlighter)
        }

    /** Whether tooltips are enabled */
    var isTooltipEnabled
        get() = HINT_TOOLTIP_ENABLED in styleHints
        set(value) = setStyleHint(HINT_TOOLTIP_ENABLED, value)

    /** Whether mouse events are enabled */
    var isMouseDisabled
        get() = HINT_MOUSE_DISABLED in styleHints
        set(value) = setStyleHint(HINT_MOUSE_DISABLED, value)

    //endregion

    //region COMPUTED PROPERTIES and LOOKUPS

    /**
     * Return style attributes of the graphic to be used for rendering. The result will have all style hints automatically
     * applied. Any attributes of the parent style are inherited.
     */
    fun renderStyle(): AttributeSet {
        var renderStyle = style
        val renderHints = styleHints
        parent?.let {
            val parStyle = it.style
            if (parStyle !== renderStyle.parent) {
                renderStyle = renderStyle.flatCopy().immutableWithParent(parStyle)
            }
            val useHints = renderHints + it.styleHints
            it.styleContext?.let {
                renderStyle = it.applyModifiers(renderStyle, useHints)
            }
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
    open fun initContextMenu(menu: JPopupMenu, src: Graphic<G>, point: Point2D, focus: Any?, selection: Set<Graphic<G>>, canvas: G) {
        for (cmi in contextMenuInitializers) cmi.initContextMenu(menu, src, point, focus, selection)
    }

    /** Return tooltip for the specified point */
    open fun getTooltip(p: Point2D, canvas: G?) = if (isTooltipEnabled) defaultTooltip else null

    //endregion

    //region ABSTRACT METHODS - STYLE, RENDER, POSITION

    /** Graphic style. */
    abstract val style: AttributeSet

    /** Render on canvas. */
    abstract fun renderTo(canvas: G)

    /** Get bounding box of graphic. */
    abstract fun boundingBox(canvas: G?): Rectangle2D?
    /** Check if graphic contains a point, e.g. to check for mouse events. */
    abstract fun contains(point: Point2D, canvas: G?): Boolean
    /** Check if graphic intersects a rectangle. */
    abstract fun intersects(box: Rectangle2D, canvas: G?): Boolean

    //endregion

    //region EVENTS

    /** Notify interested listeners of a change.  */
    protected open fun fireGraphicChanged() = parent?.graphicChanged(this)

    fun addPropertyChangeListener(pl: PropertyChangeListener) = pcs.addPropertyChangeListener(pl)
    fun addPropertyChangeListener(string: String, pl: PropertyChangeListener?) = pcs.addPropertyChangeListener(string, pl)
    fun removePropertyChangeListener(pl: PropertyChangeListener) = pcs.removePropertyChangeListener(pl)
    fun removePropertyChangeListener(string: String, pl: PropertyChangeListener) = pcs.removePropertyChangeListener(string, pl)

    fun addMouseListener(handler: MouseListener) = eventHandlers.add(MouseListener::class.java, handler)
    fun removeMouseListener(handler: MouseListener) = eventHandlers.remove(MouseListener::class.java, handler)
    fun getMouseListeners() = eventHandlers.getListeners(MouseListener::class.java)
    fun clearMouseListeners() = getMouseListeners().forEach { eventHandlers.remove(MouseListener::class.java, it) }

    fun addMouseMotionListener(handler: MouseMotionListener) = eventHandlers.add(MouseMotionListener::class.java, handler)
    fun removeMouseMotionListener(handler: MouseMotionListener) = eventHandlers.remove(MouseMotionListener::class.java, handler)
    fun getMouseMotionListeners() = eventHandlers.getListeners(MouseMotionListener::class.java)
    fun clearMouseMotionListeners() = getMouseMotionListeners().forEach { eventHandlers.remove(MouseMotionListener::class.java, it) }

    //endregion

    companion object {
        const val HINT_SELECTION_ENABLED = "selection-enabled"
        const val HINT_TOOLTIP_ENABLED = "tooltip-enabled"
        const val HINT_POPUP_ENABLED = "popupmenu-enabled"
        const val HINT_MOUSE_DISABLED = "mouse-disabled"
    }
}