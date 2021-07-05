package com.googlecode.blaisemath.coordinate

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.awt.Point
import java.util.*

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
*/   class CoordinateChangeEventTest {
    private var add: MutableMap<*, *>? = null
    private var remove: MutableSet<*>? = null
    private var addInst: CoordinateChangeEvent<*, *>? = null
    private var removeInst: CoordinateChangeEvent<*, *>? = null
    private var bothInst: CoordinateChangeEvent<*, *>? = null
    @Before
    fun setUp() {
        add = Collections.singletonMap("Object", Point())
        remove = setOf<String?>("Object2")
        addInst = CoordinateChangeEvent.Companion.createAddEvent<Any?, Any?>("Test", add)
        removeInst = CoordinateChangeEvent.Companion.createRemoveEvent<Any?, Any?>("Test", remove)
        bothInst = CoordinateChangeEvent.Companion.createAddRemoveEvent<Any?, Any?>("Test", add, remove)
    }

    @Test
    fun testCreateAddEvent() {
        println("createAddEvent")
        Assert.assertEquals("Test", addInst.getSource())
        Assert.assertNull(addInst.getRemoved())
        Assert.assertEquals(add, addInst.getAdded())
    }

    @Test
    fun testCreateRemoveEvent() {
        println("createRemoveEvent")
        Assert.assertEquals("Test", removeInst.getSource())
        Assert.assertEquals(remove, removeInst.getRemoved())
        Assert.assertNull(removeInst.getAdded())
    }

    @Test
    fun testCreateAddRemoveEvent() {
        println("createAddRemoveEvent")
        Assert.assertEquals("Test", bothInst.getSource())
        Assert.assertEquals(remove, bothInst.getRemoved())
        Assert.assertEquals(add, bothInst.getAdded())
    }

    @Test
    fun testToString() {
        println("toString")
        Assert.assertEquals("CoordinateChangeEvent[1 added,1 removed,source=Test]", bothInst.toString())
    }

    @Test
    fun testIsAddEvent() {
        println("isAddEvent")
        Assert.assertTrue(addInst.isAddEvent())
        Assert.assertFalse(removeInst.isAddEvent())
        Assert.assertTrue(bothInst.isAddEvent())
    }

    @Test
    fun testIsRemoveEvent() {
        println("isRemoveEvent")
        Assert.assertFalse(addInst.isRemoveEvent())
        Assert.assertTrue(removeInst.isRemoveEvent())
        Assert.assertTrue(bothInst.isRemoveEvent())
    }

    @Test
    fun testGetAdded() {
        println("getAdded")
        Assert.assertEquals(add, addInst.getAdded())
        Assert.assertNull(removeInst.getAdded())
    }

    @Test
    fun testGetRemoved() {
        println("getRemoved")
        Assert.assertNull(addInst.getRemoved())
        Assert.assertEquals(remove, removeInst.getRemoved())
    }
}