/**
 * SVGPathTest.java
 * Created Dec 10, 2012
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

import com.googlecode.blaisemath.svg.SVGPath;
import org.junit.Test;

/**
 * <p>
 * </p>
 * @author elisha
 */
public class SVGPathTest {
    
    @Test
    public void testSVG() {
        // make sure no exceptions are thrown
        
        // lines
        SVGPath.toPath("M200,300 L400,50 L600,300 L800,550 L1000,300");
        
        // curves
        SVGPath.toPath("M100,200 C100,100 250,100 250,200 S400,300 400,200");
        SVGPath.toPath("M100,200 C100,100 400,100 400,200");
        SVGPath.toPath("M600,200 C675,100 975,100 900,200");
        SVGPath.toPath("M600,500 C600,350 900,650 900,500");
        SVGPath.toPath("M600,800 C625,700 725,700 750,800S875,900 900,800");
        
        // quad curves
        SVGPath.toPath("M200,300 Q400,50 600,300 T1000,300");
        
        // arcs
        SVGPath.toPath("M300,200 h-150 a150,150 0 1,0 150,-150 z");
        SVGPath.toPath("M275,175 v-150 a150,150 0 0,0 -150,150 z");
        SVGPath.toPath("M600,350 l 50,-25 "
                + "a25,25 -30 0,1 50,-25 l 50,-25 "
                + "a25,50 -30 0,1 50,-25 l 50,-25 "
                + "a25,75 -30 0,1 50,-25 l 50,-25 "
                + "a25,100 -30 0,1 50,-25 l 50,-25");
        SVGPath.toPath("M600,350 l 50,-25 "
                + "a25,25 -30 0,1 50,-25 l 50,-25 "
                + "25,50 -30 0,1 50,-25 l 50,-25 "
                + "25,75 -30 0,1 50,-25 l 50,-25 "
                + "25,100 -30 0,1 50,-25 l 50,-25");
    }
    
}
