package com.googlecode.blaisemath.encode;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class FontCoderTest {

    @Test
    public void testEncode() {
        assertNPE(() -> new FontCoder().encode(null));
        Assert.assertEquals("Dialog-PLAIN-12", new FontCoder().encode(new Font(null)));
        Assert.assertEquals("Serif-BOLD-20", new FontCoder().encode(new Font("Serif", 1, 20)));
    }

    @Test
    public void testDecode() {
        Assert.assertEquals(new Font("Dialog", 0, 12), new FontCoder().decode("Dialog-PLAIN-12"));
        Assert.assertEquals(new Font("Serif", 1, 20), new FontCoder().decode("Serif-BOLD-20"));
        Assert.assertEquals("Dialog-PLAIN-12", new FontCoder().encode(new FontCoder().decode("null")));
        Assert.assertEquals("Dialog-PLAIN-12", new FontCoder().encode(new FontCoder().decode("not a font")));
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
