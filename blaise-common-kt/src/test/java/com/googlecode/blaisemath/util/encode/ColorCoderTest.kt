package com.googlecode.blaisemath.util.encode

import com.googlecode.blaisemath.util.Colors
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
*/   

class ColorCoderTest {
    @Test
    fun testEncode() {
        Assert.assertEquals("#ff0000", ColorCoder.encode(Color.red))
        Assert.assertEquals("#00ff00", ColorCoder.encode(Color.green))
        Assert.assertEquals("#0000ff", ColorCoder.encode(Color.blue))
        Assert.assertEquals("#01020304", ColorCoder.encode(Color(1, 2, 3, 4)))
    }

    @Test
    fun testDecode() {
        Assert.assertEquals(Color.red, ColorCoder.decode("ff0000"))
        Assert.assertEquals(Color.red, ColorCoder.decode("#ff0000"))
        Assert.assertEquals(Color.green, ColorCoder.decode("#00ff00"))
        Assert.assertEquals(Color.blue, ColorCoder.decode("#0000ff"))
        Assert.assertEquals(Color(0, 0, 255, 128), ColorCoder.decode("#0000ff80"))
        Assert.assertEquals(Color.blue, ColorCoder.decode("#00f"))
        Assert.assertEquals(Colors.decode("#ff0033"), ColorCoder.decode("#f03"))
        Assert.assertEquals(Color.blue, ColorCoder.decode("blue"))
        Assert.assertEquals(Color(218, 165, 32), ColorCoder.decode("goldenrod"))
        assertIllegal { ColorCoder.decode("null") }
        assertIllegal { ColorCoder.decode("not a color") }
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
    }
}