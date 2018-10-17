package com.googlecode.blaisemath.util.encode;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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


import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Elisha
 */
public class PointCoderTest {

    @Test
    public void testEncode() {
        System.out.println("encode");
        assertNPE(() -> new PointCoder().encode(null));
        Assert.assertEquals("(0,0)", new PointCoder().encode(new Point()));
        Assert.assertEquals("(3,4)", new PointCoder().encode(new Point(3, 4)));
        Assert.assertEquals("(-3,4)", new PointCoder().encode(new Point(-3, 4)));
    }

    @Test
    public void testDecode() {
        System.out.println("decode");
        Assert.assertEquals(new Point(), new PointCoder().decode("(0, 0)"));
        Assert.assertEquals(new Point(3, 4), new PointCoder().decode("(3,4)"));
        Assert.assertEquals(new Point(-3, 4), new PointCoder().decode(" ( -3 , 4 ) "));
    }
    
    private static void assertIllegal(Runnable r) {
        try {
            r.run();
            Assert.fail();
        } catch (IllegalArgumentException x) {
            // expected
        }
    }
    
    private static void assertNPE(Runnable r) {
        try {
            r.run();
            Assert.fail();
        } catch (NullPointerException x) {
            // expected
        }
    }

}
