package com.googlecode.blaisemath.graphics

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
import com.googlecode.blaisemath.style.StyleContext
import com.googlecode.blaisemath.style.StyleHints
import junit.framework.TestCase
import java.awt.Shape
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.util.*
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
*/ /**
 * An ordered collection of [Graphic]s, where the ordering indicates draw order.
 * May also have a [StyleContext] that graphics can reference when rendering.
 * The composite is NOT thread safe. Any access and changes should be done from a single
 * thread.
 *
 * @param <G> type of graphics canvas to render to
 *
 * @author Elisha Peterson
</G> */
open class GraphicComposite<G> : Graphic<G?>() {
    fun getBoundingBoxGraphic(): PrimitiveGraphic<Shape?, G?>? {
        return boundingBoxGraphic
    }

    /** Stores the shapes and their styles (in order)  */
    protected val entries: MutableSet<Graphic<G?>?>? = Sets.newLinkedHashSet()

    /** The attributes associated with the composite. These will be inherited by child graphics.  */
    protected var style: AttributeSet? = AttributeSet()

    /** The associated style provider; overrides the default style for the components in the composite (may be null).  */
    protected var styleContext: StyleContext? = null

    /** Delegate graphic used for drawing the bounding box  */
    private val boundingBoxGraphic: PrimitiveGraphic<Shape?, G?>? = PrimitiveGraphic()
    //region PROPERTIES
    /**
     * Get graphic entries in the order they are drawn.
     * @return iterator over the entries, in draw order
     */
    fun getGraphics(): MutableSet<Graphic<G?>?>? {
        return Collections.unmodifiableSet(entries)
    }

    /**
     * Explicitly set list of entries. The draw order will correspond to the iteration order.
     * @param graphics graphics in the composite
     */
    fun setGraphics(graphics: Iterable<out Graphic<G?>?>?) {
        clearGraphics()
        addGraphics(graphics)
    }

    /**
     * Return style provider with default styles
     * @return style provider with default styles
     */
    fun getStyleContext(): StyleContext? {
        return if (styleContext != null) {
            styleContext
        } else if (parent != null) {
            parent.getStyleContext()
        } else {
            StyleContext()
        }
    }

    /**
     * Sets default style provider for all child entries (may be null)
     * @param styleContext the style provider (may be null)
     */
    open fun setStyleContext(styleContext: StyleContext?) {
        if (this.styleContext != styleContext) {
            this.styleContext = styleContext
            fireGraphicChanged()
        }
    }

    override fun getStyle(): AttributeSet? {
        return style
    }

    fun setStyle(sty: AttributeSet?) {
        if (style !== sty) {
            val old: Any? = style
            style = sty
            fireGraphicChanged()
            pcs.firePropertyChange(PrimitiveGraphic.Companion.P_STYLE, old, style)
        }
    }

    fun isBoundingBoxVisible(): Boolean {
        return !GraphicUtils.isInvisible(boundingBoxGraphic)
    }

    fun setBoundingBoxVisible(show: Boolean) {
        if (isBoundingBoxVisible() != show) {
            boundingBoxGraphic.setStyleHint(StyleHints.HIDDEN_HINT, !show)
            fireGraphicChanged()
            pcs.firePropertyChange(P_BOUNDING_BOX_VISIBLE, !show, show)
        }
    }

    fun getBoundingBoxStyle(): AttributeSet? {
        return boundingBoxGraphic.getStyle()
    }

    fun setBoundingBoxStyle(style: AttributeSet?) {
        val old: Any? = getBoundingBoxStyle()
        if (old !== style) {
            boundingBoxGraphic.setStyle(style)
            fireGraphicChanged()
            pcs.firePropertyChange(P_BOUNDING_BOX_STYLE, old, style)
        }
    }

    fun getBoundingBoxRenderer(): Renderer<Shape?, G?>? {
        return boundingBoxGraphic.getRenderer()
    }

    fun setBoundingBoxRenderer(renderer: Renderer<Shape?, G?>?) {
        boundingBoxGraphic.setRenderer(renderer)
        fireGraphicChanged()
    }
    //endregion
    //region COMPOSITE METHODS
    /**
     * Add an entry to the composite.
     * @param gfc the entry
     * @return whether composite was changed by add
     */
    fun addGraphic(gfc: Graphic<G?>?): Boolean {
        if (addHelp(gfc)) {
            fireGraphicChanged()
            return true
        }
        return false
    }

    /**
     * Remove an entry from the composite
     * @param gfc the entry to remove
     * @return true if composite was changed
     */
    fun removeGraphic(gfc: Graphic<G?>?): Boolean {
        if (removeHelp(gfc)) {
            fireGraphicChanged()
            return true
        }
        return false
    }

    /**
     * Adds several entries to the composite
     * @param add the entries to add
     * @return true if composite was changed
     */
    fun addGraphics(add: Iterable<out Graphic<G?>?>?): Boolean {
        var change = false
        for (en in add) {
            change = addHelp(en) || change
        }
        return if (change) {
            fireGraphicChanged()
            true
        } else {
            false
        }
    }

    /**
     * Removes several entries from the composite
     * @param remove the entries to remove
     * @return true if composite was changed
     */
    fun removeGraphics(remove: Iterable<out Graphic<G?>?>?): Boolean {
        var change = false
        for (en in remove) {
            change = removeHelp(en) || change
        }
        return if (change) {
            fireGraphicChanged()
            true
        } else {
            false
        }
    }

    /**
     * Replaces entries
     * @param remove entries to remove
     * @param add entries to add
     * @return true if composite changed
     */
    fun replaceGraphics(remove: Iterable<out Graphic<G?>?>?, add: Iterable<out Graphic<G?>?>?): Boolean {
        var change = false
        for (en in remove) {
            change = removeHelp(en) || change
        }
        for (en in add) {
            change = addHelp(en) || change
        }
        if (change) {
            fireGraphicChanged()
        }
        return change
    }

    /**
     * Removes all entries, clearing their parents
     * @return true if composite was changed
     */
    fun clearGraphics(): Boolean {
        val change = !entries.isEmpty()
        entries.stream().filter { en: Graphic<G?>? -> en.getParent() === this }.forEach { en: Graphic<G?>? -> en.setParent(null) }
        entries.clear()
        if (change) {
            fireGraphicChanged()
            return true
        }
        return false
    }

    /** Add w/o events  */
    private fun addHelp(en: Graphic<G?>?): Boolean {
        if (entries.add(en)) {
            val par = en.getParent()
            par?.removeGraphic(en)
            en.setParent(this)
            return true
        }
        return false
    }

    /** Remove w/o events  */
    private fun removeHelp(en: Graphic<G?>?): Boolean {
        if (entries.remove(en)) {
            if (en.getParent() === this) {
                en.setParent(null)
            }
            return true
        }
        return false
    }

    //endregion
    //region GRAPHIC IMPLEMENTATIONS
    override fun boundingBox(canvas: G?): Rectangle2D? {
        return GraphicUtils.boundingBox(entries, canvas)
    }

    override fun contains(point: Point2D?, canvas: G?): Boolean {
        return graphicAt(point, canvas) != null
    }

    override fun intersects(box: Rectangle2D?, canvas: G?): Boolean {
        return entries.stream().anyMatch { en: Graphic<G?>? -> en.intersects(box, canvas) }
    }

    override fun renderTo(canvas: G?) {
        entries.stream().filter { en: Graphic<G?>? -> !StyleHints.isInvisible(en.getStyleHints()) }
                .forEach { en: Graphic<G?>? -> en.renderTo(canvas) }
        if (!GraphicUtils.isInvisible(boundingBoxGraphic)) {
            val baseStyle = boundingBoxGraphic.getStyle()
            val modStyle = getStyleContext().applyModifiers(baseStyle, styleHints)
            boundingBoxGraphic.setStyle(modStyle)
            boundingBoxGraphic.setPrimitive(boundingBox(canvas))
            boundingBoxGraphic.renderTo(canvas)
            boundingBoxGraphic.setStyle(baseStyle)
        }
    }
    //endregion
    //region QUERIES
    /**
     * Iterable over visible entries
     * @return iterable
     */
    fun visibleEntries(): Iterable<Graphic<G?>?>? {
        return Sets.filter(entries) { obj: Graphic<G?>? -> GraphicUtils.isVisible() }
    }

    /**
     * Iterable over visible entries, in reverse order
     * @return iterable
     */
    fun visibleEntriesInReverse(): Iterable<Graphic<G?>?>? {
        return Lists.reverse(Lists.newArrayList(visibleEntries()))
    }

    /**
     * Iterable over functional entries
     * @return iterable
     */
    fun functionalEntries(): Iterable<Graphic<G?>?>? {
        return Sets.filter(entries) { obj: Graphic<G?>? -> GraphicUtils.isFunctional() }
    }

    /**
     * Iterable over functional entries, in reverse order
     * @return iterable
     */
    fun functionalEntriesInReverse(): Iterable<Graphic<G?>?>? {
        return Lists.reverse(Lists.newArrayList(functionalEntries()))
    }

    /**
     * Return the topmost graphic at specified point, or null if there is none.
     * @param point the window point
     * @param canvas canvas
     * @return topmost graphic within the composite, or null if there is none
     */
    fun graphicAt(point: Point2D?, canvas: G?): Graphic<G?>? {
        for (en in visibleEntriesInReverse()) {
            if (en is GraphicComposite<*>) {
                val s = (en as GraphicComposite<G?>).graphicAt(point, canvas)
                if (s != null) {
                    return s
                }
            } else if (en.contains(point, canvas)) {
                return en
            }
        }
        return if (GraphicUtils.isFunctional(boundingBoxGraphic) && boundingBox(canvas).contains(point)) {
            this
        } else null
    }

    override fun getTooltip(p: Point2D?, canvas: G?): String? {
        for (en in visibleEntriesInReverse()) {
            if (en.isTooltipEnabled() && en.contains(p, canvas)) {
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
    fun mouseGraphicAt(point: Point2D?, canvas: G?): Graphic<G?>? {
        // return the first graphic containing the point, in draw order
        for (en in functionalEntriesInReverse()) {
            if (en.isMouseDisabled()) {
                // do nothing
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
    fun selectableGraphicAt(point: Point2D?, canvas: G?): Graphic<G?>? {
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
    fun selectableGraphicsIn(box: Rectangle2D?, canvas: G?): MutableSet<Graphic<G?>?>? {
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

    override fun initContextMenu(menu: JPopupMenu?, src: Graphic<G?>?, point: Point2D?, focus: Any?, selection: MutableSet<Graphic<G?>?>?, canvas: G?) {
        for (en in visibleEntriesInReverse()) {
            if ((en is GraphicComposite<*> || en.isContextMenuEnabled()) && en.contains(point, canvas)) {
                en.initContextMenu(menu, en, point, focus, selection, canvas)
            }
        }
        if (isContextMenuEnabled) {
            super.initContextMenu(menu, this, point, focus, selection, canvas)
        }
    }
    //endregion
    //region EVENTS
    /**
     * Called when a graphic has changed.
     * @param source the entry changed
     */
    open fun graphicChanged(source: Graphic<G?>?) {
        if (parent != null) {
            parent.graphicChanged(source)
        }
    } //endregion

    companion object {
        val P_BOUNDING_BOX_VISIBLE: String? = "boundingBoxVisible"
        val P_BOUNDING_BOX_STYLE: String? = "boundingBoxStyle"
    }

    /** Constructs with default settings  */
    init {
        isTooltipEnabled = true
        boundingBoxGraphic.setStyleHint(StyleHints.HIDDEN_HINT, true)
    }
}