/*
 * Copyright 2014 Elisha Peterson.
 *
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
 */

package org.blaise.graphics;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JPopupMenu;
import org.blaise.style.StyleContext;
import org.blaise.style.StyleContextDefault;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class GraphicCompositeTest {
    
    GraphicComposite gc;

    public GraphicCompositeTest() {
        gc = new GraphicComposite();
        gc.setStyleContext(StyleContextDefault.getInstance());
    }

    @Test
    public void testAddGraphic() {
        System.out.println("addGraphic");
        gc.addGraphic(new BasicPointGraphic());
        assertEquals(1, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testRemoveGraphic() {
        System.out.println("removeGraphic");
        BasicPointGraphic g = new BasicPointGraphic();
        gc.addGraphic(g);
        gc.removeGraphic(g);
        assertEquals(0, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testAddGraphics() {
        System.out.println("addGraphics");
        BasicPointGraphic g = new BasicPointGraphic();
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(g);
        gc.addGraphics(gfx);
        assertEquals(1, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testRemoveGraphics() {
        System.out.println("removeGraphics");
        BasicPointGraphic g = new BasicPointGraphic();
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(g);
        gc.addGraphics(gfx);
        gc.removeGraphics(gfx);
        assertEquals(0, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testReplaceGraphics() {
        System.out.println("replaceGraphics");
        BasicPointGraphic g = new BasicPointGraphic();
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(g);
        gc.addGraphics(gfx);
        gc.replaceGraphics(gfx, Lists.newArrayList(new BasicPointGraphic()));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(Iterables.contains(gc.getGraphics(), g));
    }

    @Test
    public void testGetGraphics() {
        System.out.println("getGraphics");
        BasicPointGraphic g = new BasicPointGraphic();
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(g);
        gc.setGraphics(gfx);
        assertTrue(Iterables.elementsEqual(gfx, gc.getGraphics()));
    }

    @Test
    public void testSetGraphics() {
        System.out.println("setGraphics");
        BasicPointGraphic g = new BasicPointGraphic();
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(g);
        gc.addGraphics(gfx);
        gc.setGraphics(Lists.newArrayList(new BasicPointGraphic()));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(Iterables.contains(gc.getGraphics(), g));
    }

    @Test
    public void testClearGraphics() {
        System.out.println("clearGraphics");
        BasicPointGraphic g = new BasicPointGraphic();
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(g);
        gc.addGraphics(gfx);
        gc.clearGraphics();
        assertEquals(0, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testGraphicAt() {
        System.out.println("graphicAt");
        BasicPointGraphic g = new BasicPointGraphic();
        gc.addGraphic(g);
        assertEquals(g, gc.graphicAt(new Point()));
        assertEquals(g, gc.graphicAt(new Point(1,0)));
        assertEquals(null, gc.graphicAt(new Point(10,10)));
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        BasicPointGraphic g = new BasicPointGraphic();
        gc.addGraphic(g);
        assertTrue(gc.contains(new Point()));
        assertTrue(gc.contains(new Point(1,0)));
        assertFalse(gc.contains(new Point(10,10)));
    }
    

//    @Test
//    public void testInitContextMenu() {
//        System.out.println("initContextMenu");
//        JPopupMenu menu = null;
//        Graphic src = null;
//        Point2D point = null;
//        Object focus = null;
//        Set selection = null;
//        GraphicComposite instance = new GraphicComposite();
//        instance.initContextMenu(menu, src, point, focus, selection);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGraphicChanged() {
//        System.out.println("graphicChanged");
//        Graphic source = null;
//        GraphicComposite instance = new GraphicComposite();
//        instance.graphicChanged(source);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetStyleContext() {
//        System.out.println("getStyleContext");
//        GraphicComposite instance = new GraphicComposite();
//        StyleContext expResult = null;
//        StyleContext result = instance.getStyleContext();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testSetStyleContext() {
//        System.out.println("setStyleContext");
//        StyleContext styler = null;
//        GraphicComposite instance = new GraphicComposite();
//        instance.setStyleContext(styler);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testIntersects() {
//        System.out.println("intersects");
//        Rectangle2D box = null;
//        GraphicComposite instance = new GraphicComposite();
//        boolean expResult = false;
//        boolean result = instance.intersects(box);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testDraw() {
//        System.out.println("draw");
//        Graphics2D canvas = null;
//        GraphicComposite instance = new GraphicComposite();
//        instance.draw(canvas);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testVisibleEntries() {
//        System.out.println("visibleEntries");
//        GraphicComposite instance = new GraphicComposite();
//        Iterable<Graphic> expResult = null;
//        Iterable<Graphic> result = instance.visibleEntries();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testVisibleEntriesInReverse() {
//        System.out.println("visibleEntriesInReverse");
//        GraphicComposite instance = new GraphicComposite();
//        Iterable<Graphic> expResult = null;
//        Iterable<Graphic> result = instance.visibleEntriesInReverse();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetTooltip() {
//        System.out.println("getTooltip");
//        Point2D p = null;
//        GraphicComposite instance = new GraphicComposite();
//        String expResult = "";
//        String result = instance.getTooltip(p);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testMouseGraphicAt() {
//        System.out.println("mouseGraphicAt");
//        Point2D point = null;
//        GraphicComposite instance = new GraphicComposite();
//        Graphic expResult = null;
//        Graphic result = instance.mouseGraphicAt(point);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testSelectableGraphicAt() {
//        System.out.println("selectableGraphicAt");
//        Point2D point = null;
//        GraphicComposite instance = new GraphicComposite();
//        Graphic expResult = null;
//        Graphic result = instance.selectableGraphicAt(point);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testSelectableGraphicsIn() {
//        System.out.println("selectableGraphicsIn");
//        Rectangle2D box = null;
//        GraphicComposite instance = new GraphicComposite();
//        Set<Graphic> expResult = null;
//        Set<Graphic> result = instance.selectableGraphicsIn(box);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
    
}
