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

package com.googlecode.blaisemath.util

import com.googlecode.blaisemath.util.Colors.alpha
import com.googlecode.blaisemath.util.Colors.lightened
import com.googlecode.blaisemath.util.Colors.withReducedSaturation
import org.junit.Assert
import org.junit.Test
import java.awt.Color

class ColorsTest {
    @Test
    fun testLightened() {
        Assert.assertEquals(Color.white, Color.white.lightened())
        Assert.assertEquals(Color.darkGray, Color.black.lightened())
        Assert.assertEquals(Color(114, 64, 64, 128), Color(50, 0, 0, 128).lightened())
    }

    @Test
    fun testBlanderThan() {
        Assert.assertEquals(Color.white, Color.white.withReducedSaturation())
        Assert.assertEquals(Color.black, Color.black.withReducedSaturation())
        Assert.assertEquals(Color(50, 25, 25, 128), Color(50, 0, 0, 128).withReducedSaturation())
    }

    @Test
    fun testAlpha() {
        Assert.assertEquals(Color(255, 255, 255, 0), Color.white.alpha(0))
    }

    @Test
    fun testInterpolate() {
        Assert.assertEquals(Color.green, Colors.interpolate(Color.red, 0f, Color.green))
        Assert.assertEquals(Color(77, 179, 0), Colors.interpolate(Color.red, .3f, Color.green))
        Assert.assertEquals(Color(128, 128, 0), Colors.interpolate(Color.red, .5f, Color.green))
        Assert.assertEquals(Color.red, Colors.interpolate(Color.red, 1f, Color.green))
    }

    @Test
    fun testToString() {
        Assert.assertEquals("#ff0000", Colors.encode(Color.red))
        Assert.assertEquals("#00ff00", Colors.encode(Color.green))
        Assert.assertEquals("#0000ff", Colors.encode(Color.blue))
        Assert.assertEquals("#01020304", Colors.encode(Color(1, 2, 3, 4)))
    }

    @Test
    fun testEncode() {
        Assert.assertEquals("#ff0000", Colors.encode(Color.red))
        Assert.assertEquals("#00ff00", Colors.encode(Color.green))
        Assert.assertEquals("#0000ff", Colors.encode(Color.blue))
        Assert.assertEquals("#01020304", Colors.encode(Color(1, 2, 3, 4)))
    }

    @Test
    fun testDecode() {
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
        private fun assertIllegal(r: Runnable) {
            try {
                r.run()
                Assert.fail()
            } catch (x: IllegalArgumentException) {
                // expected
            }
        }

        private fun assertNPE(r: Runnable) {
            try {
                r.run()
                Assert.fail()
            } catch (x: NullPointerException) {
                // expected
            }
        }
    }
}