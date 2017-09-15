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
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class Point2DEditorTest {

    /**
     * Test of getJavaInitializationString method, of class PointEditor.
     */
    @Test
    public void testGetJavaInitializationString() {
        System.out.println("getJavaInitializationString");
        Point2DEditor instance = new Point2DEditor();
        assertEquals("null", instance.getJavaInitializationString());
        instance.setValue(new Point(3,4));
        assertEquals("new java.awt.geom.Point2D.Double(3.0,4.0)", instance.getJavaInitializationString());
    }

    /**
     * Test of setAsText method, of class PointEditor.
     */
    @Test
    public void testSetAsText() {
        System.out.println("setAsText");
        Point2DEditor instance = new Point2DEditor();
        instance.setAsText("3,4");
        assertEquals(new Point(3,4), instance.getValue());
    }

    /**
     * Test of getValue method, of class PointEditor.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        Point2DEditor instance = new Point2DEditor();
        instance.setValue(new Point(3,4));
        assertEquals(3, (double) instance.getValue(0), 1e-8);
        assertEquals(4, (double) instance.getValue(1), 1e-8);
    }

    /**
     * Test of setNewValue method, of class PointEditor.
     */
    @Test
    public void testSetNewValue() {
        System.out.println("setNewValue");
        Point2DEditor instance = new Point2DEditor();
        instance.setNewValueList(Arrays.asList(3.0, 4.0));
        assertEquals(3, (double) instance.getNewValue(0), 1e-8);
        assertEquals(4, (double) instance.getNewValue(1), 1e-8);
    }
    
}
