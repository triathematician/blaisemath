/*
 * Copyright 2014 Elisha.
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

package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class RectangleEditorTest {

    /**
     * Test of getJavaInitializationString method, of class PointEditor.
     */
    @Test
    public void testGetJavaInitializationString() {
        System.out.println("getJavaInitializationString");
        RectangleEditor instance = new RectangleEditor();
        assertEquals("null", instance.getJavaInitializationString());
        instance.setValue(new Rectangle(3,4,5,6));
        assertEquals("new java.awt.Rectangle(3,4,5,6)", instance.getJavaInitializationString());
    }

    /**
     * Test of setAsText method, of class PointEditor.
     */
    @Test
    public void testSetAsText() {
        System.out.println("setAsText");
        RectangleEditor instance = new RectangleEditor();
        instance.setAsText("3,4,5,6");
        assertEquals(new Rectangle(3,4,5,6), instance.getValue());
    }

    /**
     * Test of getValue method, of class PointEditor.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        RectangleEditor instance = new RectangleEditor();
        instance.setValue(new Rectangle(3,4,5,6));
        assertEquals(3, (int) instance.getValue(0));
        assertEquals(4, (int) instance.getValue(1));
        assertEquals(5, (int) instance.getValue(2));
        assertEquals(6, (int) instance.getValue(3));
    }

    /**
     * Test of setNewValue method, of class PointEditor.
     */
    @Test
    public void testSetNewValue() {
        System.out.println("setNewValue");
        RectangleEditor instance = new RectangleEditor();
        instance.setNewValue(new Rectangle(3,4,5,6));
        assertEquals(3, (int) instance.getNewValue(0));
        assertEquals(4, (int) instance.getNewValue(1));
        assertEquals(5, (int) instance.getNewValue(2));
        assertEquals(6, (int) instance.getNewValue(3));
    }
    
}
