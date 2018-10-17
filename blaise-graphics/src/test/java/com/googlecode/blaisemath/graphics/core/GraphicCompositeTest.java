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

package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.StyleContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JPopupMenu;
import com.googlecode.blaisemath.style.StyleHints;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Elisha Peterson
 */
public class GraphicCompositeTest {

    PrimitiveGraphic<Point2D,Graphics2D> pt;
    GraphicComposite gc;

    public GraphicCompositeTest() {
        gc = new GraphicComposite();
        gc.setStyleContext(new StyleContext());
        pt = JGraphics.point(new Point2D.Double());
    }

    @Test
    public void testAddGraphic() {
        System.out.println("addGraphic");
        gc.addGraphic(pt);
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(gc.addGraphic(pt));
        assertEquals(1, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testRemoveGraphic() {
        System.out.println("removeGraphic");
        assertTrue(gc.addGraphic(pt));
        assertTrue(gc.removeGraphic(pt));
        assertEquals(0, Iterables.size(gc.getGraphics()));
        assertFalse(gc.removeGraphic(pt));
    }

    @Test
    public void testAddGraphics() {
        System.out.println("addGraphics");
        ArrayList<PrimitiveGraphic<Point2D, Graphics2D>> gfx = Lists.newArrayList(pt);
        assertTrue(gc.addGraphics(gfx));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(gc.addGraphics(gfx));
    }

    @Test
    public void testRemoveGraphics() {
        System.out.println("removeGraphics");
        ArrayList<PrimitiveGraphic<Point2D, Graphics2D>> gfx = Lists.newArrayList(pt);
        assertTrue(gc.addGraphics(gfx));
        assertTrue(gc.removeGraphics(gfx));
        assertFalse(gc.removeGraphics(gfx));
        assertEquals(0, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testReplaceGraphics() {
        System.out.println("replaceGraphics");
        ArrayList<PrimitiveGraphic<Point2D, Graphics2D>> gfx = Lists.newArrayList(pt);
        assertFalse(gc.replaceGraphics(gfx, Collections.EMPTY_LIST));
        gc.addGraphics(gfx);
        assertTrue(gc.replaceGraphics(gfx, Arrays.asList(pt)));
        assertTrue(gc.replaceGraphics(gfx, Arrays.asList(JGraphics.point(new Point2D.Double()))));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(Iterables.contains(gc.getGraphics(), pt));
    }

    @Test
    public void testGetGraphics() {
        System.out.println("getGraphics");
        ArrayList<PrimitiveGraphic<Point2D, Graphics2D>> gfx = Lists.newArrayList(pt);
        gc.setGraphics(gfx);
        assertTrue(Iterables.elementsEqual(gfx, gc.getGraphics()));
    }

    @Test
    public void testSetGraphics() {
        System.out.println("setGraphics");
        ArrayList<PrimitiveGraphic<Point2D, Graphics2D>> gfx = Lists.newArrayList(pt);
        gc.addGraphics(gfx);
        gc.setGraphics(Lists.newArrayList(JGraphics.point(new Point2D.Double())));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(Iterables.contains(gc.getGraphics(), pt));
    }

    @Test
    public void testClearGraphics() {
        System.out.println("clearGraphics");
        ArrayList<PrimitiveGraphic<Point2D, Graphics2D>> gfx = Lists.newArrayList(pt);
        gc.addGraphics(gfx);
        assertTrue(gc.clearGraphics());
        assertEquals(0, Iterables.size(gc.getGraphics()));
        assertFalse(gc.clearGraphics());
    }

    @Test
    public void testGraphicAt() {
        System.out.println("graphicAt");
        gc.addGraphic(pt);
        assertEquals(pt, gc.graphicAt(new Point()));
        assertEquals(pt, gc.graphicAt(new Point(1,0)));
        assertEquals(null, gc.graphicAt(new Point(10,10)));
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        gc.addGraphic(pt);
        assertTrue(gc.contains(new Point()));
        assertTrue(gc.contains(new Point(1,0)));
        assertFalse(gc.contains(new Point(10,10)));
    }
    

    @Test
    public void testInitContextMenu() {
        System.out.println("initContextMenu");
        JPopupMenu menu = new JPopupMenu();
        gc.initContextMenu(menu, null, new Point(), null, null);
    }

    @Test
    public void testGraphicChanged() {
        System.out.println("graphicChanged");
        GraphicComposite instance = new GraphicComposite();
        instance.graphicChanged(pt);
    }

    @Test
    public void testGetStyleContext() {
        System.out.println("getStyleContext");
        assertNotNull(gc.getStyleContext());
        try {
            gc.setStyleContext(null);
            fail("Composites must have style contexts.");
        } catch (IllegalStateException x) {
            // expected
        }
    }

    @Test
    public void testSetStyleContext() {
        System.out.println("setStyleContext");
        gc.setStyleContext(new StyleContext());
    }

    @Test
    public void testIntersects() {
        System.out.println("intersects");
        gc.addGraphic(pt);
        assertTrue(gc.intersects(new Rectangle(0,0,10,10)));
        assertFalse(gc.intersects(new Rectangle(5,5,10,10)));
    }

    @Test
    public void testVisibleEntries() {
        System.out.println("visibleEntries");
        gc.addGraphic(pt);
        assertTrue(Iterables.elementsEqual(Lists.newArrayList(pt), gc.visibleEntries()));
        pt.setStyleHint(StyleHints.HIDDEN_HINT, true);
        assertTrue(Iterables.isEmpty(gc.visibleEntries()));
    }

    @Test
    public void testVisibleEntriesInReverse() {
        System.out.println("visibleEntriesInReverse");
        gc.addGraphic(pt);
        PrimitiveGraphic p2 = JGraphics.point(new Point());
        gc.addGraphic(p2);
        assertTrue(Iterables.elementsEqual(Lists.newArrayList(pt, p2), gc.visibleEntries()));
        assertTrue(Iterables.elementsEqual(Lists.newArrayList(p2, pt), gc.visibleEntriesInReverse()));
    }

    @Test
    public void testGetTooltip() {
        System.out.println("getTooltip");
        gc.addGraphic(pt);
        assertNull(gc.getTooltip(new Point()));
        pt.setTooltipEnabled(true);
        assertNull(gc.getTooltip(new Point()));
        pt.setDefaultTooltip("test");
        assertEquals("test",gc.getTooltip(new Point()));
    }

    @Test
    public void testMouseGraphicAt() {
        System.out.println("mouseGraphicAt");
        gc.addGraphic(pt);
        assertEquals(pt, gc.mouseGraphicAt(new Point()));
        pt.setMouseEnabled(false);
        assertNull(gc.mouseGraphicAt(new Point()));
    }

    @Test
    public void testSelectableGraphicAt() {
        System.out.println("selectableGraphicAt");
        gc.addGraphic(pt);
        assertEquals(null, gc.selectableGraphicAt(new Point()));
        pt.setSelectionEnabled(true);
        assertEquals(pt, gc.selectableGraphicAt(new Point()));
        pt.setSelectionEnabled(false);
        assertEquals(null, gc.selectableGraphicAt(new Point()));
    }

    @Test
    public void testSelectableGraphicsIn() {
        System.out.println("selectableGraphicsIn");
        Rectangle2D box = new Rectangle(0,0,5,5);
        gc.addGraphic(pt);
        pt.setSelectionEnabled(true);
        assertTrue(Iterables.elementsEqual(Arrays.asList(pt), gc.selectableGraphicsIn(box)));
        pt.setSelectionEnabled(false);
        assertTrue(Iterables.isEmpty(gc.selectableGraphicsIn(box)));
    }
    
}
