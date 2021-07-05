package com.googlecode.blaisemath.graphics.swing

import com.google.common.base.Preconditions
import com.google.common.collect.Sets
import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.StyleHints
import com.googlecode.blaisemath.style.Styles
import com.googlecode.blaisemath.util.SetSelectionModel
import com.googlecode.blaisemath.util.swing.CanvasPainter
import junit.framework.TestCase
import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.function.Consumer

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
 * Mouse handler that enables selection on a composite graphic object.
 * Control must be down for any selection capability.
 * @param <G> type of render canvas
 * @author Elisha Peterson
</G> */
class JGraphicSelectionHandler<G>(
        /** Determines which objects can be selected  */
        private val owner: JGraphicComponent?
) : MouseAdapter(), CanvasPainter<Graphics2D?> {
    /** Whether selector is enabled  */
    private var enabled = true

    /** Model of selected items  */
    private val selection: SetSelectionModel<Graphic<Graphics2D?>?>? = SetSelectionModel()

    /** Style for drawing selection box  */
    private var selectionBoxStyle = Styles.fillStroke(
            Color(128, 128, 255, 32), Color(0, 0, 128, 64))
    private var pressPt: Point? = null
    private var dragPt: Point? = null
    private var selectionBox: Rectangle2D.Double? = null

    //region PROPERTIES
    fun getSelectionModel(): SetSelectionModel<Graphic<Graphics2D?>?>? {
        return selection
    }

    fun isSelectionEnabled(): Boolean {
        return enabled
    }

    fun setSelectionEnabled(enabled: Boolean) {
        if (this.enabled != enabled) {
            this.enabled = enabled
            if (!enabled) {
                selection.setSelection(emptySet())
            }
        }
    }

    fun getStyle(): AttributeSet? {
        return selectionBoxStyle
    }

    fun setStyle(style: AttributeSet?) {
        selectionBoxStyle = Preconditions.checkNotNull(style)
    }

    //endregion
    override fun paint(component: Component?, canvas: Graphics2D?) {
        if (enabled && selectionBox != null && selectionBox.width > 0 && selectionBox.height > 0) {
            ShapeRenderer.Companion.getInstance().render(selectionBox, selectionBoxStyle, canvas)
        }
    }

    //region EVENTS
    override fun mouseMoved(e: MouseEvent?) {
        if (e.isConsumed()) {
            return
        }
        val g = owner.selectableGraphicAt(e.getPoint())
        val gAll = owner.functionalGraphicAt(e.getPoint())
        if (gAll == null) {
            // reset to default if there is no active mouse graphic
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR))
        } else if (g != null) {
            // identify selectable graphics when you mouse over them
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        }
    }

    override fun mouseClicked(e: MouseEvent?) {
        if (!enabled || e.getButton() != MouseEvent.BUTTON1 || e.isConsumed()) {
            return
        }
        if (!isSelectionEvent(e)) {
            selection.setSelection(emptySet())
            return
        }
        val g: Graphic<Graphics2D?>? = owner.selectableGraphicAt(e.getPoint())
        if (g == null) {
            selection.setSelection(emptySet())
        } else if (e.isShiftDown()) {
            selection.deselect(g)
        } else if (e.isAltDown()) {
            selection.select(g)
        } else {
            selection.toggleSelection(g)
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        if (!enabled || e.isConsumed() || e.getButton() != MouseEvent.BUTTON1 || !isSelectionEvent(e)) {
            return
        }
        pressPt = e.getPoint()
        if (selectionBox == null) {
            selectionBox = Rectangle2D.Double()
        }
        selectionBox.setFrameFromDiagonal(pressPt, pressPt)
        e.consume()
    }

    override fun mouseDragged(e: MouseEvent?) {
        if (!enabled || e.isConsumed() || selectionBox == null || pressPt == null) {
            return
        }
        dragPt = e.getPoint()
        selectionBox.setFrameFromDiagonal(pressPt, dragPt)
        if (e.getSource() is Component) {
            (e.getSource() as Component).repaint()
        }
        e.consume()
    }

    override fun mouseReleased(e: MouseEvent?) {
        if (!enabled || e.isConsumed() || selectionBox == null || pressPt == null) {
            return
        }
        val releasePt = e.getPoint()
        if (owner.getInverseTransform() == null) {
            selectionBox.setFrameFromDiagonal(pressPt, releasePt)
        } else {
            selectionBox.setFrameFromDiagonal(
                    owner.toGraphicCoordinate(pressPt),
                    owner.toGraphicCoordinate(releasePt))
        }
        if (selectionBox.getWidth() > 0 && selectionBox.getHeight() > 0) {
            var gg = owner.getGraphicRoot().selectableGraphicsIn(selectionBox, owner.canvas())
            if (e.isShiftDown()) {
                val res: MutableSet<Graphic<Graphics2D?>?>? = Sets.newHashSet(selection.getSelection())
                res.removeAll(gg)
                gg = res
            } else if (e.isAltDown()) {
                gg.addAll(selection.getSelection())
            }
            selection.setSelection(gg)
        }
        selectionBox = null
        pressPt = null
        dragPt = null
        owner.repaint()
        e.consume()
    }

    companion object {
        private var MAC = false

        //endregion
        private fun detectMac() {
            val os = System.getProperty("os.name").toLowerCase()
            MAC = os.contains("mac")
        }

        private fun isSelectionEvent(e: InputEvent?): Boolean {
            return if (MAC) e.isMetaDown() else e.isControlDown()
        }
    }

    /**
     * Initialize for specified component
     * @param owner the component for handling
     */
    init {

        // highlight updates
        selection.addPropertyChangeListener(PropertyChangeListener { evt: PropertyChangeEvent? ->
            val old = evt.getOldValue() as MutableSet<Graphic<*>?>
            val nue = evt.getNewValue() as MutableSet<Graphic<*>?>
            Sets.difference(old, nue).forEach(Consumer { g: Graphic<*>? -> g.setStyleHint(StyleHints.SELECTED_HINT, false) })
            Sets.difference(nue, old).forEach(Consumer { g: Graphic<*>? -> g.setStyleHint(StyleHints.SELECTED_HINT, true) })
        })
        detectMac()
    }
}