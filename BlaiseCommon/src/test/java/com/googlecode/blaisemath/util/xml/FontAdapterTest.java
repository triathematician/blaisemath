/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import java.awt.Font;
import junit.framework.TestCase;

/**
 *
 * @author Elisha Peterson
 */
public class FontAdapterTest extends TestCase {

    /**
     * Test of marshal method, of class ColorAdapter.
     */
    public void testMarshal() throws Exception {
        System.out.println("marshal");
        FontAdapter ca = new FontAdapter();
        assertEquals("Dialog-BOLD-10", ca.marshal(new Font(Font.DIALOG, Font.BOLD, 10)));
    }

    /**
     * Test of unmarshal method, of class ColorAdapter.
     */
    public void testUnmarshal() throws Exception {
        System.out.println("unmarshal");
        FontAdapter ca = new FontAdapter();
        Font f = ca.unmarshal("Dialog-PLAIN-14");
        assertEquals("Dialog", f.getName());
        assertEquals(Font.PLAIN, f.getStyle());
        assertEquals(14, f.getSize());
        assertEquals("Dialog-PLAIN-14", ca.marshal(f));
    }
}
