package com.googlecode.blaisemath.encode

import org.junit.Assert
import org.junit.Test
import java.awt.Font

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

class FontCoderTest {
    @Test
    fun testEncode() {
        Assert.assertEquals("Dialog-PLAIN-12", FontCoder.encode(Font(null)))
        Assert.assertEquals("Serif-BOLD-20", FontCoder.encode(Font("Serif", 1, 20)))
    }

    @Test
    fun testDecode() {
        Assert.assertEquals(Font("Dialog", 0, 12), FontCoder.decode("Dialog-PLAIN-12"))
        Assert.assertEquals(Font("Serif", 1, 20), FontCoder.decode("Serif-BOLD-20"))
        Assert.assertEquals("Dialog-PLAIN-12", FontCoder.encode(FontCoder.decode("null")))
        Assert.assertEquals("Dialog-PLAIN-12", FontCoder.encode(FontCoder.decode("not a font")))
    }
}