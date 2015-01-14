/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.space;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.googlecode.blaisemath.space.Point3D;
import com.googlecode.blaisemath.space.SpatialMathUtils;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class SpacialMathUtilsTest {

    public SpacialMathUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testTripleProduct() {
        System.out.println("tripleProduct");
        Point3D p1 = new Point3D(2,1,4);
        Point3D p2 = new Point3D(5,-1,-3);
        Point3D p3 = new Point3D(2,6,5.5);
        double expResult = 119.5;
        double result = SpatialMathUtils.tripleProduct(p1, p2, p3);
        assertEquals(expResult, result, 1e-12);
    }

}
