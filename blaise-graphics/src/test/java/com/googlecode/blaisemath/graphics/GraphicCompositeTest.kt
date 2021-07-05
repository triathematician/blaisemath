package com.googlecode.blaisemath.graphics

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.googlecode.blaisemath.graphics.swing.JGraphics
import com.googlecode.blaisemath.style.StyleContext
import com.googlecode.blaisemath.style.StyleHints
import org.junit.Assert
import org.junit.Test
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
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
*/   class GraphicCompositeTest {
    val pt: PrimitiveGraphic<Point2D?, Graphics2D?>?
    val gc: GraphicComposite<Graphics2D?>?
    @Test
    fun testAddGraphic() {
        gc.addGraphic(pt)
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.addGraphic(pt))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
    }

    @Test
    fun testRemoveGraphic() {
        Assert.assertTrue(gc.addGraphic(pt))
        Assert.assertTrue(gc.removeGraphic(pt))
        Assert.assertEquals(0, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.removeGraphic(pt))
    }

    @Test
    fun testAddGraphics() {
        val gfx = Lists.newArrayList(pt)
        Assert.assertTrue(gc.addGraphics(gfx))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.addGraphics(gfx))
    }

    @Test
    fun testRemoveGraphics() {
        val gfx = Lists.newArrayList(pt)
        Assert.assertTrue(gc.addGraphics(gfx))
        Assert.assertTrue(gc.removeGraphics(gfx))
        Assert.assertFalse(gc.removeGraphics(gfx))
        Assert.assertEquals(0, Iterables.size(gc.getGraphics()).toLong())
    }

    @Test
    fun testReplaceGraphics() {
        val gfx = Lists.newArrayList(pt)
        Assert.assertFalse(gc.replaceGraphics(gfx, Collections.EMPTY_LIST))
        gc.addGraphics(gfx)
        Assert.assertTrue(gc.replaceGraphics(gfx, listOf(pt)))
        Assert.assertTrue(gc.replaceGraphics(gfx, listOf(JGraphics.point(Point2D.Double()))))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(Iterables.contains(gc.getGraphics(), pt))
    }

    @Test
    fun testGetGraphics() {
        val gfx = Lists.newArrayList(pt)
        gc.setGraphics(gfx)
        Assert.assertTrue(Iterables.elementsEqual(gfx, gc.getGraphics()))
    }

    @Test
    fun testSetGraphics() {
        val gfx = Lists.newArrayList(pt)
        gc.addGraphics(gfx)
        gc.setGraphics(Lists.newArrayList(JGraphics.point(Point2D.Double())))
        Assert.assertEquals(1, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(Iterables.contains(gc.getGraphics(), pt))
    }

    @Test
    fun testClearGraphics() {
        val gfx = Lists.newArrayList(pt)
        gc.addGraphics(gfx)
        Assert.assertTrue(gc.clearGraphics())
        Assert.assertEquals(0, Iterables.size(gc.getGraphics()).toLong())
        Assert.assertFalse(gc.clearGraphics())
    }

    @Test
    fun testGraphicAt() {
        gc.addGraphic(pt)
        Assert.assertEquals(pt, gc.graphicAt(Point(), null))
        Assert.assertEquals(pt, gc.graphicAt(Point(1, 0), null))
        Assert.assertNull(gc.graphicAt(Point(10, 10), null))
    }

    @Test
    fun testContains() {
        gc.addGraphic(pt)
        Assert.assertTrue(gc.contains(Point(), null))
        Assert.assertTrue(gc.contains(Point(1, 0), null))
        Assert.assertFalse(gc.contains(Point(10, 10), null))
    }

    @Test
    fun testInitContextMenu() {
        val menu = JPopupMenu()
        gc.initContextMenu(menu, null, Point(), null, null, null)
    }

    @Test
    fun testGraphicChanged() {
        val instance: GraphicComposite<*> = GraphicComposite<Any?>()
        instance.graphicChanged(pt)
    }

    @Test
    fun testGetStyleContext() {
        Assert.assertNotNull(gc.getStyleContext())
    }

    @Test
    fun testSetStyleContext() {
        gc.setStyleContext(null)
        gc.setStyleContext(StyleContext())
    }

    @Test
    fun testIntersects() {
        gc.addGraphic(pt)
        Assert.assertTrue(gc.intersects(Rectangle(0, 0, 10, 10), null))
        Assert.assertFalse(gc.intersects(Rectangle(5, 5, 10, 10), null))
    }

    @Test
    fun testVisibleEntries() {
        gc.addGraphic(pt)
        Assert.assertTrue(Iterables.elementsEqual(Lists.newArrayList(pt), gc.visibleEntries()))
        pt.setStyleHint(StyleHints.HIDDEN_HINT, true)
        Assert.assertTrue(Iterables.isEmpty(gc.visibleEntries()))
    }

    @Test
    fun testVisibleEntriesInReverse() {
        gc.addGraphic(pt)
        val p2: PrimitiveGraphic<*, *>? = JGraphics.point(Point())
        gc.addGraphic(p2)
        Assert.assertTrue(Iterables.elementsEqual(Lists.newArrayList(pt, p2), gc.visibleEntries()))
        Assert.assertTrue(Iterables.elementsEqual(Lists.newArrayList(p2, pt), gc.visibleEntriesInReverse()))
    }

    @Test
    fun testGetTooltip() {
        gc.addGraphic(pt)
        Assert.assertNull(gc.getTooltip(Point(), null))
        pt.setTooltipEnabled(true)
        Assert.assertNull(gc.getTooltip(Point(), null))
        pt.setDefaultTooltip("test")
        Assert.assertEquals("test", gc.getTooltip(Point(), null))
    }

    @Test
    fun testMouseGraphicAt() {
        gc.addGraphic(pt)
        Assert.assertEquals(pt, gc.mouseGraphicAt(Point(), null))
        pt.setMouseDisabled(true)
        Assert.assertNull(gc.mouseGraphicAt(Point(), null))
    }

    @Test
    fun testSelectableGraphicAt() {
        gc.addGraphic(pt)
        Assert.assertNull(gc.selectableGraphicAt(Point(), null))
        pt.setSelectionEnabled(true)
        Assert.assertEquals(pt, gc.selectableGraphicAt(Point(), null))
        pt.setSelectionEnabled(false)
        Assert.assertNull(gc.selectableGraphicAt(Point(), null))
    }

    @Test
    fun testSelectableGraphicsIn() {
        val box: Rectangle2D = Rectangle(0, 0, 5, 5)
        gc.addGraphic(pt)
        pt.setSelectionEnabled(true)
        Assert.assertTrue(Iterables.elementsEqual(listOf(pt), gc.selectableGraphicsIn(box, null)))
        pt.setSelectionEnabled(false)
        Assert.assertTrue(Iterables.isEmpty(gc.selectableGraphicsIn(box, null)))
    }

    init {
        gc = GraphicComposite()
        gc.setStyleContext(StyleContext())
        pt = JGraphics.point(Point2D.Double())
    }
}