/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import com.googlecode.blaisemath.svg.AttributeSetAdapter;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class AttributeSetAdapterTest {

    /**
     * Test of convertStyleAttributeSetAdapter method, of class AttributeSetAdapter.
     */
    @Test
    public void testToSVG() throws Exception {
        System.out.println("toSVG");
        assertEquals("fill:#ff0000; stroke:#00ff00", 
                AttributeSetAdapter.INST.marshal(Styles.fillStroke(Color.red, Color.green)));
    }
    
}
