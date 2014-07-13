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

package com.googlecode.blaisemath.style;

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


import com.googlecode.blaisemath.style.PointStyleBasic;
import com.googlecode.blaisemath.style.svg.StyleUtilsSVG;
import java.awt.Color;
import java.beans.IntrospectionException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Elisha Peterson
 */
public class PointStyleBasicTest {

    @Test
    public void testSerialization() throws IntrospectionException {
        System.out.println("svgSerialization");
        PointStyleBasic sty = new PointStyleBasic().fill(Color.black).stroke(Color.red).strokeWidth(1f).markerRadius(10f);
        assertEquals("fill:#000000; marker-radius:10.0; stroke:#ff0000; stroke-width:1.0", StyleUtilsSVG.convertStyleToSVG(sty));
    }
    
}
