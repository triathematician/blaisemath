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
 * Copyright (C) 2009 - 2019 Elisha Peterson
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


import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class RectangularShapeEditorTest {

    /**
     * Test of getJavaInitializationString method, of class PointEditor.
     */
    @Test
    public void testGetJavaInitializationString() {
        System.out.println("getJavaInitializationString");
        RectangularShapeEditor instance = new RectangularShapeEditor();
        assertEquals("???", instance.getJavaInitializationString());
        instance.setValue(new Rectangle(3,4,5,6));
        assertEquals("???", instance.getJavaInitializationString());
    }

    /**
     * Test of setAsText method, of class PointEditor.
     */
    @Test
    public void testSetAsText() {
        System.out.println("setAsText");
        RectangularShapeEditor instance = new RectangularShapeEditor();
        instance.setAsText("3,4,5,6");
        assertEquals(new Rectangle2D.Double(3,4,5,6), instance.getValue());
    }

    /**
     * Test of getValue method, of class PointEditor.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        RectangularShapeEditor instance = new RectangularShapeEditor();
        instance.setValue(new Rectangle2D.Double(3,4,5,6));
        assertEquals(3, (double) instance.getValue(0), 1e-8);
        assertEquals(4, (double) instance.getValue(1), 1e-8);
        assertEquals(5, (double) instance.getValue(2), 1e-8);
        assertEquals(6, (double) instance.getValue(3), 1e-8);
    }

    /**
     * Test of setNewValue method, of class PointEditor.
     */
    @Test
    public void testSetNewValue() {
        System.out.println("setNewValue");
        RectangularShapeEditor instance = new RectangularShapeEditor();
        instance.setNewValue(new Rectangle2D.Double(3,4,5,6));
        assertEquals(3, (double) instance.getNewValue(0), 1e-8);
        assertEquals(4, (double) instance.getNewValue(1), 1e-8);
        assertEquals(5, (double) instance.getNewValue(2), 1e-8);
        assertEquals(6, (double) instance.getNewValue(3), 1e-8);
    }
    
}
