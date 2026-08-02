/*
* #%L
* BlaiseCommon
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

package com.googlecode.blaisemath.coordinate

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.awt.Point

class CoordinateChangeEventTest {
    private var add: Map<String, Point>? = null
    private var remove: Set<String>? = null
    private var addInst: CoordinateChangeEvent<String, Point>? = null
    private var removeInst: CoordinateChangeEvent<String, Point>? = null
    private var bothInst: CoordinateChangeEvent<String, Point>? = null

    @Before
    fun setUp() {
        add = mapOf("Object" to Point())
        remove = setOf("Object2")
        addInst = CoordinateChangeEvent("Test", added = add)
        removeInst = CoordinateChangeEvent("Test", removed = remove)
        bothInst = CoordinateChangeEvent("Test", added = add, removed = remove)
    }

    @Test
    fun testCreateAddEvent() {
        Assert.assertEquals("Test", addInst!!.source)
        Assert.assertNull(addInst!!.removed)
        Assert.assertEquals(add, addInst!!.added)
    }

    @Test
    fun testCreateRemoveEvent() {
        Assert.assertEquals("Test", removeInst!!.source)
        Assert.assertEquals(remove, removeInst!!.removed)
        Assert.assertNull(removeInst!!.added)
    }

    @Test
    fun testCreateAddRemoveEvent() {
        Assert.assertEquals("Test", bothInst!!.source)
        Assert.assertEquals(remove, bothInst!!.removed)
        Assert.assertEquals(add, bothInst!!.added)
    }

    @Test
    fun testIsAddEvent() {
        Assert.assertTrue(addInst!!.isAddEvent)
        Assert.assertFalse(removeInst!!.isAddEvent)
        Assert.assertTrue(bothInst!!.isAddEvent)
    }

    @Test
    fun testIsRemoveEvent() {
        Assert.assertFalse(addInst!!.isRemoveEvent)
        Assert.assertTrue(removeInst!!.isRemoveEvent)
        Assert.assertTrue(bothInst!!.isRemoveEvent)
    }

    @Test
    fun testGetAdded() {
        Assert.assertEquals(add, addInst!!.added)
        Assert.assertNull(removeInst!!.added)
    }

    @Test
    fun testGetRemoved() {
        Assert.assertNull(addInst!!.removed)
        Assert.assertEquals(remove, removeInst!!.removed)
    }
}