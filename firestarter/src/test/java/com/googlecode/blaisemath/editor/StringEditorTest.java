/*
 * Copyright 2014 elisha.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class StringEditorTest {

    @Test
    public void testSupportsCustomEditor() {
        System.out.println("supportsCustomEditor");
        StringEditor instance = new StringEditor();
        assertTrue(instance.supportsCustomEditor());
    }

    @Test
    public void testGetJavaInitializationString() {
        System.out.println("getJavaInitializationString");
        StringEditor instance = new StringEditor();
        
        instance.setValue("test");
        assertEquals("\"test\"", instance.getJavaInitializationString());
        
        instance.setValue("\"test\"\n");
        assertEquals("\"\\\"test\\\"\\n\"", instance.getJavaInitializationString());
    }

    @Test
    public void testSetAsText() {
        System.out.println("setAsText");
        StringEditor instance = new StringEditor();
        instance.setAsText("test");
        assertEquals("test", instance.getValue());
    }
    
}
