package com.googlecode.blaisemath.graphics.swing

import com.google.common.base.Objects
import com.google.common.base.Preconditions
import com.googlecode.blaisemath.graphics.Graphic
import com.googlecode.blaisemath.graphics.GraphicComposite
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
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Point2D
import javax.swing.JPopupMenu
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener

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
 * Manages the entries on a [JGraphicComponent].
 * The primary additional behavior implemented by `GraphicRoot`, beyond that of its parent
 * `GraphicComposite`, is listening to mouse events on the component and
 * generating [GraphicMouseEvent]s from them.
 *
 * Subclasses might provide additional behavior such as (i) caching the shapes to be drawn
 * to avoid expensive recomputation, or (ii) sorting the shapes into an alternate draw order
 * (e.g. for projections from 3D to 2D).
 *
 * @author Elisha Peterson
 */
class JGraphicRoot(component: JGraphicComponent?) : GraphicComposite<Graphics2D?>() {
    /** Parent component upon which the graphics are drawn.  */
    protected val owner: JGraphicComponent?

    /** Context menu for actions on the graphics  */
    protected val popup: JPopupMenu? = JPopupMenu()

    /** Provides a pluggable way to generate mouse events  */
    protected var mouseFactory: GraphicMouseEvent.Factory? = GraphicMouseEvent.Factory()

    /** Current owner of mouse events. Gets first priority for mouse events that occur.  */
    private var mouseGraphic: Graphic<*>? = null

    /** Tracks current mouse location  */
    private var mouseLoc: Point2D? = null

    //region PROPERTIES
    override fun setParent(p: GraphicComposite<*>?) {
        Preconditions.checkArgument(p == null, "GraphicRoot cannot be added to another GraphicComposite")
    }

    override fun setStyleContext(styleContext: StyleContext?) {
        Preconditions.checkArgument(styleContext != null, "GraphicRoot must have a non-null StyleProvider!")
        super.setStyleContext(styleContext)
    }

    /**
     * Return current object used to generate mouse events.
     * @return mouse event factory
     */
    fun getMouseEventFactory(): GraphicMouseEvent.Factory? {
        return mouseFactory
    }

    /**
     * Modifies how mouse events are created.
     * @param factory responsible for generating mouse events
     */
    fun setMouseEventFactory(factory: GraphicMouseEvent.Factory?) {
        if (mouseFactory !== factory) {
            mouseFactory = Preconditions.checkNotNull(factory)
        }
    }

    //endregion
    //region EVENTS
    override fun fireGraphicChanged() {
        graphicChanged(this)
    }

    override fun graphicChanged(source: Graphic<*>?) {
        owner?.repaint()
    }

    /**
     * Create GraphicMouseEvent from given event.
     * @param e mouse event
     * @return associated graphic mouse event
     */
    private fun graphicMouseEvent(e: MouseEvent?): GraphicMouseEvent? {
        var localPoint: Point2D? = e.getPoint()
        if (owner.getInverseTransform() != null) {
            localPoint = owner.getInverseTransform().transform(localPoint, null)
        }
        return mouseFactory.createEvent(e, localPoint, this)
    }

    /**
     * Change current owner of mouse events.
     * @param gme graphic mouse event
     * @param keepCurrent whether to maintain current selection even if it's behind another graphic
     * @param canvas target canvas
     */
    private fun updateMouseGraphic(gme: GraphicMouseEvent?, keepCurrent: Boolean, canvas: Graphics2D?) {
        if (keepCurrent && mouseGraphic != null && GraphicUtils.isFunctional(mouseGraphic)
                && mouseGraphic.contains(gme.getGraphicLocation(), canvas)) {
            return
        }
        val nue: Graphic<*>? = mouseGraphicAt(gme.getGraphicLocation(), canvas)
        if (!Objects.equal(mouseGraphic, nue)) {
            mouseExit(mouseGraphic, gme)
            mouseGraphic = nue
            mouseEnter(mouseGraphic, gme)
        }
    }

    private fun mouseEnter(mouseGraphic: Graphic<*>?, event: GraphicMouseEvent?) {
        if (mouseGraphic != null) {
            event.setGraphicSource(mouseGraphic)
            for (l in mouseGraphic.mouseListeners) {
                l.mouseEntered(event)
                if (event.isConsumed()) {
                    return
                }
            }
        }
    }

    private fun mouseExit(mouseGraphic: Graphic<*>?, event: GraphicMouseEvent?) {
        if (mouseGraphic != null) {
            event.setGraphicSource(mouseGraphic)
            for (l in mouseGraphic.mouseListeners) {
                l.mouseExited(event)
                if (event.isConsumed()) {
                    return
                }
            }
        }
    }

    /** Delegate for mouse events  */
    private inner class MouseHandler : MouseListener, MouseMotionListener {
        override fun mouseClicked(e: MouseEvent?) {
            val gme = graphicMouseEvent(e)
            updateMouseGraphic(gme, false, owner.canvas())
            if (mouseGraphic != null) {
                for (l in mouseGraphic.getMouseListeners()) {
                    l.mouseClicked(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseMoved(e: MouseEvent?) {
            val gme = graphicMouseEvent(e)
            mouseLoc = gme.getGraphicLocation()
            updateMouseGraphic(gme, false, owner.canvas())
            if (mouseGraphic != null) {
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseMotionListeners()) {
                    l.mouseMoved(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mousePressed(e: MouseEvent?) {
            val gme = graphicMouseEvent(e)
            updateMouseGraphic(gme, false, owner.canvas())
            if (mouseGraphic != null) {
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseListeners()) {
                    l.mousePressed(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseDragged(e: MouseEvent?) {
            if (mouseGraphic != null) {
                val gme = graphicMouseEvent(e)
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseMotionListeners()) {
                    l.mouseDragged(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseReleased(e: MouseEvent?) {
            if (mouseGraphic != null) {
                val gme = graphicMouseEvent(e)
                gme.setGraphicSource(mouseGraphic)
                for (l in mouseGraphic.getMouseListeners()) {
                    l.mouseReleased(gme)
                    if (gme.isConsumed()) {
                        return
                    }
                }
            }
        }

        override fun mouseEntered(e: MouseEvent?) {
            // no behavior desired
        }

        override fun mouseExited(e: MouseEvent?) {
            if (mouseGraphic != null) {
                val gme = graphicMouseEvent(e)
                mouseExit(mouseGraphic, gme)
            }
        }
    } //endregion

    /**
     * Construct a default instance
     * @param component the graphic root's component
     */
    init {
        owner = Preconditions.checkNotNull(component)
        val mh: MouseHandler = MouseHandler()
        owner.addMouseListener(mh)
        owner.addMouseMotionListener(mh)
        owner.componentPopupMenu = popup

        // set up style
        setStyleContext(Styles.defaultStyleContext())
        style.put(Styles.FILL, Color.lightGray)
        style.put(Styles.STROKE, Color.black)

        // set up popup menu
        popup.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) {
                if (mouseLoc != null) {
                    popup.removeAll()
                    val selected = if (owner.isSelectionEnabled) owner.selectionModel.selection else null
                    initContextMenu(popup, null, mouseLoc, null, selected, owner.canvas())
                }
            }

            override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent?) {
                popup.removeAll()
            }

            override fun popupMenuCanceled(e: PopupMenuEvent?) {
                popup.removeAll()
            }
        })
    }
}