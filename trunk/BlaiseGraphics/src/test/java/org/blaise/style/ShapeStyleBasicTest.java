/*
 * Copyright 2014 Elisha Peterson.
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

package org.blaise.style;

import java.awt.Color;
import java.beans.IntrospectionException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Elisha Peterson
 */
public class ShapeStyleBasicTest {

    @Test
    public void testSerialization() throws IntrospectionException {
        System.out.println("svgSerialization");
        ShapeStyleBasic sty = new ShapeStyleBasic().fill(Color.black).stroke(Color.red).strokeWidth(2f);
        assertEquals("fill:#000000; stroke:#ff0000; stroke-width:2.0", StyleUtilsSVG.convertStyleToSVG(sty));
    }
    
}
