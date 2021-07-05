package com.googlecode.blaisemath.util

import org.junit.Assert
import org.junit.Test
import java.awt.Color

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
*/   class ColorsTest {
    @Test
    fun testLighterThan() {
        println("lighterThan")
        Assert.assertEquals(Color.white, Colors.lighterThan(Color.white))
        Assert.assertEquals(Color.darkGray, Colors.lighterThan(Color.black))
        Assert.assertEquals(Color(114, 64, 64, 128), Colors.lighterThan(Color(50, 0, 0, 128)))
    }

    @Test
    fun testBlanderThan() {
        println("blanderThan")
        Assert.assertEquals(Color.white, Colors.blanderThan(Color.white))
        Assert.assertEquals(Color.black, Colors.blanderThan(Color.black))
        Assert.assertEquals(Color(50, 25, 25, 128), Colors.blanderThan(Color(50, 0, 0, 128)))
    }

    @Test
    fun testAlpha() {
        println("alphas")
        Assert.assertEquals(Color(255, 255, 255, 0), Colors.alpha(Color.white, 0))
    }

    @Test
    fun testInterpolate() {
        println("interpolate")
        Assert.assertEquals(Color.green, Colors.interpolate(Color.red, 0f, Color.green))
        Assert.assertEquals(Color(76, 178, 0), Colors.interpolate(Color.red, .3f, Color.green))
        Assert.assertEquals(Color(127, 127, 0), Colors.interpolate(Color.red, .5f, Color.green))
        Assert.assertEquals(Color.red, Colors.interpolate(Color.red, 1f, Color.green))
    }

    @Test
    fun testToString() {
        println("toString")
        Assert.assertEquals("#ff0000", Colors.encode(Color.red))
        Assert.assertEquals("#00ff00", Colors.encode(Color.green))
        Assert.assertEquals("#0000ff", Colors.encode(Color.blue))
        Assert.assertEquals("#01020304", Colors.encode(Color(1, 2, 3, 4)))
    }

    @Test
    fun testEncode() {
        println("encode")
        assertNPE { Colors.encode(null) }
        Assert.assertEquals("#ff0000", Colors.encode(Color.red))
        Assert.assertEquals("#00ff00", Colors.encode(Color.green))
        Assert.assertEquals("#0000ff", Colors.encode(Color.blue))
        Assert.assertEquals("#01020304", Colors.encode(Color(1, 2, 3, 4)))
    }

    @Test
    fun testDecode() {
        println("decode")
        Assert.assertEquals(Color.red, Colors.decode("ff0000"))
        Assert.assertEquals(Color.red, Colors.decode("#ff0000"))
        Assert.assertEquals(Color.green, Colors.decode("#00ff00"))
        Assert.assertEquals(Color.blue, Colors.decode("#0000ff"))
        Assert.assertEquals(Color(0, 0, 255, 128), Colors.decode("#0000ff80"))
        Assert.assertEquals(Color.blue, Colors.decode("#00f"))
        Assert.assertEquals(Colors.decode("#ff0033"), Colors.decode("#f03"))
        Assert.assertEquals(Color.blue, Colors.decode("blue"))
        Assert.assertEquals(Color(218, 165, 32), Colors.decode("goldenrod"))
        assertIllegal { Colors.decode("null") }
        assertIllegal { Colors.decode("not a color") }
    }

    companion object {
        private fun assertIllegal(r: Runnable?) {
            try {
                r.run()
                Assert.fail()
            } catch (x: IllegalArgumentException) {
                // expected
            }
        }

        private fun assertNPE(r: Runnable?) {
            try {
                r.run()
                Assert.fail()
            } catch (x: NullPointerException) {
                // expected
            }
        }
    }
}