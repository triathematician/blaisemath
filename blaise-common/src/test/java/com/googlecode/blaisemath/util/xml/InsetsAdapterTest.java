/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import junit.framework.TestCase;

/**
 *
 * @author petereb1
 */
public class InsetsAdapterTest extends TestCase {

    public void testUnmarshal() throws Exception {
        System.out.println("unmarshal");
        assertEquals(new Insets(1, 2, 3, 4), new InsetsAdapter().unmarshal("insets[t=1,l=2,b=3,   r=4]"));
        assertEquals(new Insets(1, 2, 3, -4), new InsetsAdapter().unmarshal("insets[t=1,l=2,b=3,   r=-4]"));
        try {
            new InsetsAdapter().unmarshal("insets[1,2,3,4.0]");
            fail();
        } catch (IllegalArgumentException x) {
        }
        assertNull(new InsetsAdapter().unmarshal("insets[t=1,l=2,b=3,   r=4.0]"));
        try {
            new InsetsAdapter().unmarshal("3.0,4.0,5.0,6.0");
            fail();
        } catch (IllegalArgumentException x) {
        }
        assertNull(new InsetsAdapter().unmarshal(null));
        assertNull(new InsetsAdapter().unmarshal(""));
    }

    public void testMarshal() {
        System.out.println("marshal");
        assertEquals("null", new InsetsAdapter().marshal(null));
        assertEquals("insets[t=1,l=2,b=3,r=4]", new InsetsAdapter().marshal(new Insets(1, 2, 3, 4)));
    }

}
