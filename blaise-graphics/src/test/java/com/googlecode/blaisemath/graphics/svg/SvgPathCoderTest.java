package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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

import java.awt.geom.Path2D;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

public class SvgPathCoderTest {
    
    private static Path2D toPath(String p) {
        return new SvgPathCoder().decode(p);
    }

    private static String recyclePath(String path) {
        SvgPathCoder c = new SvgPathCoder();
        return c.encode(c.decode(path));
    }
    
    @Test
    public void testSvg() {
        // make sure no exceptions are thrown
        
        // lines
        toPath("M200,300 L400,50 L600,300 L800,550 L1000,300");
        
        // curves
        toPath("M100,200 C100,100 250,100 250,200 S400,300 400,200");
        toPath("M100,200 C100,100 400,100 400,200");
        toPath("M600,200 C675,100 975,100 900,200");
        toPath("M600,500 C600,350 900,650 900,500");
        toPath("M600,800 C625,700 725,700 750,800S875,900 900,800");
        
        // quad curves
        toPath("M200,300 Q400,50 600,300 T1000,300");
        
        // arcs
        toPath("M300,200 h-150 a150,150 0 1,0 150,-150 z");
        toPath("M275,175 v-150 a150,150 0 0,0 -150,150 z");
        toPath("M600,350 l 50,-25 "
                + "a25,25 -30 0,1 50,-25 l 50,-25 "
                + "a25,50 -30 0,1 50,-25 l 50,-25 "
                + "a25,75 -30 0,1 50,-25 l 50,-25 "
                + "a25,100 -30 0,1 50,-25 l 50,-25");
        toPath("M600,350 l 50,-25 "
                + "a25,25 -30 0,1 50,-25 l 50,-25 "
                + "25,50 -30 0,1 50,-25 l 50,-25 "
                + "25,75 -30 0,1 50,-25 l 50,-25 "
                + "25,100 -30 0,1 50,-25 l 50,-25");
    }
    
    @Test
    public void testSvgRel() {
        assertEquals("M 10 10 L 11 10 L 11 11", recyclePath("M10 10l1 0 0 1"));
    }
    
    @Test
    public void testSvgCloseMove() {
        assertEquals("M 0 0 L 1 0 ZM 1 1 L 2 2", recyclePath("M 0 0 L 1 0 Z m 1 1 l 1 1"));
    }
    
    @Test
    public void testSvgRel2() {
        assertEquals("M 600 350 L 650 325 L 650 325.000031 C 643.096436 311.192902 648.692871 294.403564 662.5 287.500031 C 676.307129 280.596466 693.096436 286.192902 700 300.000031",
                recyclePath("M600,350 l 50,-25a25,25 -30 0,1 50,-25"));
    }
    
}
