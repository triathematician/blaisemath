/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.style;

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


import java.awt.Color;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class StyleUtilsSVGTest {

    /**
     * Test of convertStyleStyleUtilsSVG method, of class StyleUtilsSVG.
     */
    @Test
    public void testToSVG() throws Exception {
        System.out.println("toSVG");
        assertEquals("fill:#ff0000; stroke:#00ff00; stroke-width:1.0", StyleUtilsSVG.convertStyleToSVG(Styles.fillStroke(Color.red, Color.green)));
    }

    /**
     * Test of convertKeyStyleUtilsSVG method, of class StyleUtilsSVG.
     */
    @Test
    public void testToSVGKey() {
        System.out.println("toSVGKey");
        assertEquals("font-size", StyleUtilsSVG.convertKeyToSVG("fontSize"));
    }
}