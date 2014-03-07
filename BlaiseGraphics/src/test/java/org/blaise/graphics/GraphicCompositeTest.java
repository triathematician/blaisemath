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

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JPopupMenu;
import org.blaise.style.StyleContextDefault;
import org.blaise.style.VisibilityHint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class GraphicCompositeTest {

    BasicPointGraphic p;
    GraphicComposite gc;

    public GraphicCompositeTest() {
        gc = new GraphicComposite();
        gc.setStyleContext(StyleContextDefault.getInstance());
        p = new BasicPointGraphic();
    }

    @Test
    public void testAddGraphic() {
        System.out.println("addGraphic");
        gc.addGraphic(p);
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(gc.addGraphic(p));
        assertEquals(1, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testRemoveGraphic() {
        System.out.println("removeGraphic");
        assertTrue(gc.addGraphic(p));
        assertTrue(gc.removeGraphic(p));
        assertEquals(0, Iterables.size(gc.getGraphics()));
        assertFalse(gc.removeGraphic(p));
    }

    @Test
    public void testAddGraphics() {
        System.out.println("addGraphics");
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(p);
        assertTrue(gc.addGraphics(gfx));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(gc.addGraphics(gfx));
    }

    @Test
    public void testRemoveGraphics() {
        System.out.println("removeGraphics");
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(p);
        assertTrue(gc.addGraphics(gfx));
        assertTrue(gc.removeGraphics(gfx));
        assertFalse(gc.removeGraphics(gfx));
        assertEquals(0, Iterables.size(gc.getGraphics()));
    }

    @Test
    public void testReplaceGraphics() {
        System.out.println("replaceGraphics");
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(p);
        assertFalse(gc.replaceGraphics(gfx, Collections.EMPTY_LIST));
        gc.addGraphics(gfx);
        assertTrue(gc.replaceGraphics(gfx, Arrays.asList(p)));
        assertTrue(gc.replaceGraphics(gfx, Arrays.asList(new BasicPointGraphic())));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(Iterables.contains(gc.getGraphics(), p));
    }

    @Test
    public void testGetGraphics() {
        System.out.println("getGraphics");
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(p);
        gc.setGraphics(gfx);
        assertTrue(Iterables.elementsEqual(gfx, gc.getGraphics()));
    }

    @Test
    public void testSetGraphics() {
        System.out.println("setGraphics");
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(p);
        gc.addGraphics(gfx);
        gc.setGraphics(Lists.newArrayList(new BasicPointGraphic()));
        assertEquals(1, Iterables.size(gc.getGraphics()));
        assertFalse(Iterables.contains(gc.getGraphics(), p));
    }

    @Test
    public void testClearGraphics() {
        System.out.println("clearGraphics");
        ArrayList<BasicPointGraphic> gfx = Lists.newArrayList(p);
        gc.addGraphics(gfx);
        assertTrue(gc.clearGraphics());
        assertEquals(0, Iterables.size(gc.getGraphics()));
        assertFalse(gc.clearGraphics());
    }

    @Test
    public void testGraphicAt() {
        System.out.println("graphicAt");
        gc.addGraphic(p);
        assertEquals(p, gc.graphicAt(new Point()));
        assertEquals(p, gc.graphicAt(new Point(1,0)));
        assertEquals(null, gc.graphicAt(new Point(10,10)));
    }

    @Test
    public void testContains() {
        System.out.println("contains");
        gc.addGraphic(p);
        assertTrue(gc.contains(new Point()));
        assertTrue(gc.contains(new Point(1,0)));
        assertFalse(gc.contains(new Point(10,10)));
    }
    

    @Test
    public void testInitContextMenu() {
        System.out.println("initContextMenu");
        JPopupMenu menu = new JPopupMenu();
        gc.initContextMenu(menu, null, new Point(), null, null);
        // todo - more
    }

    @Test
    public void testGraphicChanged() {
        System.out.println("graphicChanged");
        GraphicComposite instance = new GraphicComposite();
        instance.graphicChanged(p);
    }

    @Test
    public void testGetStyleContext() {
        System.out.println("getStyleContext");
        assertNotNull(gc.getStyleContext());
        gc.setStyleContext(null);
        try {
            gc.getStyleContext();
            fail("Composites must have style contexts.");
        } catch (IllegalStateException x) {
            // expected
        }
    }

    @Test
    public void testSetStyleContext() {
        System.out.println("setStyleContext");
        gc.setStyleContext(StyleContextDefault.getInstance());
    }

    @Test
    public void testIntersects() {
        System.out.println("intersects");
        gc.addGraphic(p);
        assertTrue(gc.intersects(new Rectangle(0,0,10,10)));
        assertFalse(gc.intersects(new Rectangle(5,5,10,10)));
    }

    @Test
    public void testDraw() {
        System.out.println("draw");
        // TODO
    }

    @Test
    public void testVisibleEntries() {
        System.out.println("visibleEntries");
        gc.addGraphic(p);
        assertTrue(Iterables.elementsEqual(Lists.newArrayList(p), gc.visibleEntries()));
        p.setVisibilityHint(VisibilityHint.HIDDEN, true);
        assertTrue(Iterables.isEmpty(gc.visibleEntries()));
    }

    @Test
    public void testVisibleEntriesInReverse() {
        System.out.println("visibleEntriesInReverse");
        gc.addGraphic(p);
        BasicPointGraphic p2 = new BasicPointGraphic();
        gc.addGraphic(p2);
        assertTrue(Iterables.elementsEqual(Lists.newArrayList(p, p2), gc.visibleEntries()));
        assertTrue(Iterables.elementsEqual(Lists.newArrayList(p2, p), gc.visibleEntriesInReverse()));
    }

    @Test
    public void testGetTooltip() {
        System.out.println("getTooltip");
        gc.addGraphic(p);
        assertNull(gc.getTooltip(new Point()));
        p.setTooltipEnabled(true);
        assertNull(gc.getTooltip(new Point()));
        p.setDefaultTooltip("test");
        assertEquals("test",gc.getTooltip(new Point()));
    }

    @Test
    public void testMouseGraphicAt() {
        System.out.println("mouseGraphicAt");
        gc.addGraphic(p);
        assertEquals(p, gc.mouseGraphicAt(new Point()));
        p.setMouseEnabled(false);
        assertNull(gc.mouseGraphicAt(new Point()));
    }

    @Test
    public void testSelectableGraphicAt() {
        System.out.println("selectableGraphicAt");
        gc.addGraphic(p);
        assertEquals(p, gc.selectableGraphicAt(new Point()));
        p.setSelectionEnabled(false);
        assertEquals(gc, gc.selectableGraphicAt(new Point()));
    }

    @Test
    public void testSelectableGraphicsIn() {
        System.out.println("selectableGraphicsIn");
        Rectangle2D box = new Rectangle(0,0,5,5);
        gc.addGraphic(p);
        assertTrue(Iterables.elementsEqual(Arrays.asList(p), gc.selectableGraphicsIn(box)));
        p.setSelectionEnabled(false);
        assertTrue(Iterables.isEmpty(gc.selectableGraphicsIn(box)));
    }
    
}
