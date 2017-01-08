package com.googlecode.blaisemath.gesture;

/*
 * #%L
 * blaise-gestures
 * --
 * Copyright (C) 2015 - 2016 Elisha Peterson
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


import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GestureOrchestratorTest {
    
    @Test
    public void testNoGesture() {
        JPanel c = new JPanel();
        GestureOrchestrator<JPanel> go = new GestureOrchestrator<>(c);
        
        assertEquals(null, go.topGestureFor(mouseEvent(c)).orElse(null));
    }

    @Test
    public void testAddRemove() {
        JPanel c = new JPanel();
        GestureOrchestrator<JPanel> go = new GestureOrchestrator<>(c);
        TestGesture tg = new TestGesture(go);
        
        go.addGesture(tg);
        assertEquals(tg, go.topGestureFor(mouseEvent(c)).get());
        
        assertEquals(true, go.removeGesture(tg));
        assertEquals(false, go.removeGesture(tg));
    }
    
    @Test
    public void testTopGesture() {
        JPanel c = new JPanel();
        GestureOrchestrator<JPanel> go = new GestureOrchestrator<>(c);
        TestGesture tg1 = new TestGesture(go);
        TestGesture tg2 = new TestGesture(go);
        go.addGesture(tg1);
        go.addGesture(tg2);
        
        assertEquals(tg2, go.topGestureFor(mouseEvent(c)).get());
        
        go.removeGesture(tg2);
        assertEquals(tg1, go.topGestureFor(mouseEvent(c)).get());
    }
    
    @Test
    public void testDelegateFor() {
        JPanel c = new JPanel();
        GestureOrchestrator<JPanel> go = new GestureOrchestrator<>(c);
        TestGesture tg1 = new TestGesture(go);
        TestGesture tg2 = new TestGesture(go);
        tg2.eventFilter = evt -> evt.getID() == MouseEvent.MOUSE_CLICKED;
        go.addGesture(tg1);
        go.addGesture(tg2);
        
        assertEquals(tg1, go.topGestureFor(mouseEvent(c)).get());
        assertEquals(tg2, go.topGestureFor(mouseEvent2(c)).get());
    }

    private static MouseEvent mouseEvent(Component c) {
        return new MouseEvent(c, MouseEvent.MOUSE_MOVED, 0L, 0, 0, 0, 0, false, 0);
    }
    
    private static MouseEvent mouseEvent2(Component c) {
        return new MouseEvent(c, MouseEvent.MOUSE_CLICKED, 0L, 0, 0, 0, 0, false, 0);
    }
    
}
