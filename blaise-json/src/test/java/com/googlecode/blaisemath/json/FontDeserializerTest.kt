package com.googlecode.blaisemath.json

import org.junit.Assert
import org.junit.Test
import java.awt.Font
import java.io.IOException

/*-
* #%L
* blaise-json
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
*/   class FontDeserializerTest {
    @Test
    @Throws(IOException::class)
    fun testRead() {
        val f = BlaiseJson.allMapper().convertValue("Serif-BOLD-18", Font::class.java)
        Assert.assertEquals(18, f.size.toLong())
        Assert.assertEquals("Serif", f.family)
        Assert.assertEquals(Font.BOLD.toLong(), f.style.toLong())
    }

    @Test
    @Throws(IOException::class)
    fun testWrite() {
        Assert.assertEquals("\"Serif-BOLD-18\"", BlaiseJson.allMapper().writeValueAsString(Font("Serif", 1, 18)))
    }
}