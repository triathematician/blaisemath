package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic.Companion.P_STYLE
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.StyleContext
import com.googlecode.blaisemath.style.StyleHints.HIDDEN_HINT
import java.awt.Shape
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import javax.swing.JPopupMenu

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
 * An ordered collection of [Graphic]s, where the ordering indicates draw order. May also have a [StyleContext] that
 * graphics can reference when rendering. The composite is NOT thread safe. Any access and changes should be done from a
 * single thread.
 * @param <G> type of graphics canvas to render to
 */
open class GraphicComposite<G : Any> : Graphic<G>() {

    /** Graphic objects within this composite. */
    val _graphics = mutableSetOf<Graphic<G>>()

    /** Graphic objects within this composite. */
    var graphics
        get() = _graphics.toSet()
        set(value) {
            _graphics.clear()
            _graphics.addAll(value)
            fireGraphicChanged()
        }

    /** The attributes associated with the composite. These will be inherited by child graphics.  */
    override var style = AttributeSet()
        set(value) {
            val old = field
            field = value
            fireGraphicChanged()
            pcs.firePropertyChange(P_STYLE, old, field)
        }

    /** The associated style provider; overrides the default style for the components in the composite (may be null).  */
    private var _styleContext: StyleContext? = null

    /** The associated style provider; overrides the default style for the components in the composite (may be null).  */
    var styleContext: StyleContext? = _styleContext
        get() = _styleContext ?: parent?.styleContext
        set(value) {
            val old = field
            field = value
            fireGraphicChanged()
            pcs.firePropertyChange(P_STYLE, old, field)
        }

    //region BOUNDING BOX GRAPHIC

    /** Delegate graphic used for drawing the bounding box  */
    private val boundingBoxGraphic = PrimitiveGraphic<Shape, G>().apply {
        setStyleHint(HIDDEN_HINT, true)
    }

    var isBoundingBoxVisible
        get() = boundingBoxGraphic.isVisible
        set(value) {
            if (value != isBoundingBoxVisible) {
                boundingBoxGraphic.setStyleHint(HIDDEN_HINT, !value)
                fireGraphicChanged()
                pcs.firePropertyChange(P_BOUNDING_BOX_VISIBLE, !value, value)
            }
        }

    var boundingBoxStyle
        get() = boundingBoxGraphic.style
        set(value) {
            val old = boundingBoxStyle
            if (old != value) {
                boundingBoxGraphic.style = value
                fireGraphicChanged()
                pcs.firePropertyChange(P_BOUNDING_BOX_STYLE, old, style)
            }
        }
    var boundingBoxRenderer
        get() = boundingBoxGraphic.renderer
        set(value) {
            if (boundingBoxRenderer != value) {
                boundingBoxGraphic.renderer = value
                fireGraphicChanged()
            }
        }

    //endregion

    //region COMPOSITE METHODS

    /** Add to composite. */
    fun addGraphic(gfc: Graphic<G>) = addHelp(gfc).notify()
    /** Remove from composite. */
    fun removeGraphic(gfc: Graphic<G>) = removeHelp(gfc).notify()
    /** Add several to composite. */
    fun addGraphics(add: List<Graphic<G>>) = add.map { addHelp(it) }.any().notify()
    /** Remove several from composite. */
    fun removeGraphics(remove: List<Graphic<G>>) = remove.map { removeHelp(it) }.any().notify()
    /** Replaces entries */
    fun replaceGraphics(remove: List<Graphic<G>>, add: List<Graphic<G>>): Boolean {
        val change = remove.map { removeHelp(it) }.any() || add.map { addHelp(it) }.any()
        return change.notify()
    }
    /** Remove all entries and clear parents. */
    fun clearGraphics(): Boolean {
        val change = _graphics.isNotEmpty()
        _graphics.onEach { if (it.parent === this) it.parent = null }
        _graphics.clear()
        return change.notify()
    }

    /** Add without events. */
    private fun addHelp(en: Graphic<G>): Boolean {
        if (_graphics.add(en)) {
            en.parent?.removeGraphic(en)
            en.parent = this
            return true
        }
        return false
    }

    /** Remove without events. */
    private fun removeHelp(en: Graphic<G>): Boolean {
        if (_graphics.remove(en)) {
            en.parent?.removeGraphic(en)
            en.parent = null
            return true
        }
        return false
    }

    /** Sends event if true. */
    private fun Boolean.notify() = also { if (it) fireGraphicChanged() }

    //endregion

    //region GRAPHIC IMPLEMENTATIONS

    override fun boundingBox(canvas: G?) = boundingBox(entries, canvas)
    override fun contains(point: Point2D, canvas: G) = graphicAt(point, canvas) != null
    override fun intersects(box: Rectangle2?, canvas: G) = graphics.any { it.intersects(box, canvas) }

    override fun renderTo(canvas: G?) {
        entries.stream().filter { en: Graphic<G?>? -> !StyleHints.isInvisible(en.getStyleHints()) }
                .forEach { en: Graphic<G?>? -> en.renderTo(canvas) }
        if (!GraphicUtils.isInvisible(boundingBoxGraphic)) {
            val baseStyle = boundingBoxGraphic.getStyle()
            val modStyle = styleContext?.applyModifiers(baseStyle, styleHints)
            boundingBoxGraphic.style = modStyle
            boundingBoxGraphic.setPrimitive(boundingBox(canvas))
            boundingBoxGraphic.renderTo(canvas)
            boundingBoxGraphic.style = baseStyle
        }
    }

    //endregion

    //region QUERIES

    fun visibleEntries() = graphics.filter { it.isVisible }
    fun visibleEntriesInReverse() = visibleEntries().reversed()

    fun functionalEntries() = graphics.filter { it.isFunctional }
    fun functionalEntriesInReverse() = functionalEntries().reversed()

    /** Return the topmost graphic at specified point, or null if there is none. */
    open fun graphicAt(point: Point2D, canvas: G?): Graphic<G>? {
        for (en in visibleEntriesInReverse()) {
            if (en is GraphicComposite<G>) {
                en.graphicAt(point, canvas)?.let { return it }
            } else if (en.contains(point, canvas)) {
                return en
            }
        }
        return if (boundingBoxGraphic.isFunctional && boundingBox(canvas)?.contains(point) == true) {
            this
        } else null
    }

    override fun getTooltip(p: Point2D, canvas: G?): String? {
        for (en in visibleEntriesInReverse()) {
            if (en.isTooltipEnabled && en.contains(p, canvas)) {
                val l = en.getTooltip(p, canvas)
                if (l != null) {
                    return l
                }
            }
        }
        return defaultTooltip
    }

    /**
     * Return the topmost graphic at specified point that is interested in mouse events, or null if there is none.
     * @param point the window point
     * @param canvas graphics canvas
     * @return topmost graphic within the composite
     */
    open fun mouseGraphicAt(point: Point2D?, canvas: G?): Graphic<G?>? {
        // return the first graphic containing the point, in draw order
        for (en in functionalEntriesInReverse()) {
            if (en.isMouseDisabled()) {
                continue
            } else if (en is GraphicComposite<*>) {
                val s = (en as GraphicComposite<G?>).mouseGraphicAt(point, canvas)
                if (s != null) {
                    return s
                }
            } else if (en.contains(point, canvas)) {
                return en
            }
        }
        val rect = boundingBox(canvas)
        return if (GraphicUtils.isFunctional(boundingBoxGraphic) && rect != null && rect.contains(point)) {
            this
        } else null
    }

    /**
     * Return selectable graphic at given point
     * @param point point of interest
     * @param canvas canvas
     * @return graphic at point that can be selected
     */
    open fun selectableGraphicAt(point: Point2D?, canvas: G?): Graphic<G?>? {
        for (en in visibleEntriesInReverse()) {
            if (en is GraphicComposite<*>) {
                val s = (en as GraphicComposite<G?>).selectableGraphicAt(point, canvas)
                if (s != null) {
                    return s
                }
            } else if (en.isSelectionEnabled() && en.contains(point, canvas)) {
                return en
            }
        }
        return if (isSelectionEnabled && contains(point, canvas)) this else null
    }

    /**
     * Return collection of graphics in the composite in specified bounding box
     * @param box bounding box
     * @param canvas canvas
     * @return graphics within bounds
     */
    open fun selectableGraphicsIn(box: Rectangle2D?, canvas: G?): MutableSet<Graphic<G?>?>? {
        val result: MutableSet<Graphic<G?>?> = HashSet()
        for (g in visibleEntries()) {
            if (g is GraphicComposite<*>) {
                result.addAll((g as GraphicComposite<G?>).selectableGraphicsIn(box, canvas))
            }
            // no else belongs here
            if (g.intersects(box, canvas) && g.isSelectionEnabled()) {
                result.add(g)
            }
        }
        return result
    }

    override fun initContextMenu(menu: JPopupMenu, src: Graphic<G>, point: Point2D, focus: Any?, selection: Set<Graphic<G>>, canvas: G) {
        for (en in visibleEntriesInReverse()) {
            if ((en is GraphicComposite<*> || en.isContextMenuEnabled) && en.contains(point, canvas)) {
                en.initContextMenu(menu, en, point, focus, selection, canvas)
            }
        }
        if (isContextMenuEnabled) {
            super.initContextMenu(menu, this, point, focus, selection, canvas)
        }
    }

    //endregion

    //region EVENTS

    /** Called when a graphic has changed. */
    open fun graphicChanged(source: Graphic<G>): Unit? = parent?.graphicChanged(source)

    //endregion

    companion object {
        const val P_BOUNDING_BOX_VISIBLE = "boundingBoxVisible"
        const val P_BOUNDING_BOX_STYLE = "boundingBoxStyle"
    }
}