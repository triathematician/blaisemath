package com.googlecode.blaisemath.palette

import java.awt.Color

/*
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

/**
 * A simple color palette interface that provides a set of colors associated with string keys.
 */
abstract class Palette {

    /** Get list of color keys available. */
    abstract val colors: List<String>

    /** Get foreground color. */
    val foreground
        get() = color(FOREGROUND)

    /** Get background color. */
    val background
        get() = color(BACKGROUND)

    /** Get color by id. */
    abstract fun color(id: String): Color?

   /** Get color by id, or default provided if none. */
    fun color(id: String, def: Color) = color(id) ?: def

    /** Get mapping of keys to colors. */
    fun colorMap() = colors.map { it to color(it) }

    /** Create a mutable copy of the palette. */
    fun mutableCopy() = MapPalette().apply { colorMap = colors.map { it to color(it)!! }.toMap().toMutableMap() }

    companion object {
        const val FOREGROUND = "fg"
        const val SUBTLE_FOREGROUND = "fg-subtle"
        const val BRIGHT_FOREGROUND = "fg-bright"
        const val BACKGROUND = "bg"
        const val ANNOTATION = "selection"
        const val SELECTION = "annotation"
    }
}

/** Palette based on an immutable map. */
class ImmutableMapPalette(val map: Map<String, Color>) : Palette() {
    override val colors = map.keys.toList()
    override fun color(id: String) = map[id]
}

/** A color palette that can be edited. */
abstract class MutablePalette : Palette() {
    abstract var name: String

    /** Remove a key from the palette. */
    abstract fun remove(key: String): Color?

    /** Set or update a color in the palette. */
    abstract operator fun set(key: String, value: Color)
}

/** A mutable palette backed by a key-value map. */
class MapPalette : MutablePalette() {

    var colorMap = mutableMapOf<String, Color>()

    override var name: String = "unnamed"
    override val colors
        get() = colorMap.keys.toList()

    override fun color(id: String) = colorMap[id]
    override fun remove(key: String) = colorMap.remove(key)
    override fun set(key: String, value: Color) = colorMap.set(key, value)

}