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
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class PointEditorTest {

    /**
     * Test of getJavaInitializationString method, of class PointEditor.
     */
    @Test
    public void testGetJavaInitializationString() {
        System.out.println("getJavaInitializationString");
        PointEditor instance = new PointEditor();
        assertEquals("null", instance.getJavaInitializationString());
        instance.setValue(new Point(3,4));
        assertEquals("new java.awt.Point(3,4)", instance.getJavaInitializationString());
    }

    /**
     * Test of setAsText method, of class PointEditor.
     */
    @Test
    public void testSetAsText() {
        System.out.println("setAsText");
        PointEditor instance = new PointEditor();
        instance.setAsText("3,4");
        assertEquals(new Point(3,4), instance.getValue());
    }

    /**
     * Test of getValue method, of class PointEditor.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        PointEditor instance = new PointEditor();
        instance.setValue(new Point(3,4));
        assertEquals(3, (int) instance.getValue(0));
        assertEquals(4, (int) instance.getValue(1));
    }

    /**
     * Test of setNewValue method, of class PointEditor.
     */
    @Test
    public void testSetNewValueList() {
        System.out.println("setNewValueList");
        PointEditor instance = new PointEditor();
        instance.setNewValueList(Arrays.asList(3, 4));
        assertEquals(3, (int) instance.getNewValue(0));
        assertEquals(4, (int) instance.getNewValue(1));
    }
    
}
