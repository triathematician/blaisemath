package com.googlecode.blaisemath.style
/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
 * Maintains multiple types of styles within a single context, and also contains logic for modifying the style attribute sets based on "hints".
 */
class StyleContext(val parent: StyleContext? = null) {

    /** Modifiers that apply to the styles in this context.  */
    val modifiers: MutableSet<StyleModifier> = mutableSetOf()

    /** Add modifier. */
    fun addModifier(mod: StyleModifier) = modifiers.add(mod)
    /** Remove modifier. */
    fun removeModifier(mod: StyleModifier) = modifiers.remove(mod)

    /** Get style modifiers supported by this context. */
    fun getAllModifiers(): Set<StyleModifier> = if (parent == null) modifiers else parent.getAllModifiers() + modifiers

    /** Applies all modifiers in this context to the given style, returning the result. */
    fun applyModifiers(style: AttributeSet, vararg hints: String) = applyModifiers(style, hints.toSet())
    /** Applies all modifiers in this context to the given style, returning the result. */
    fun applyModifiers(style: AttributeSet, hints: Set<String>): AttributeSet {
        var res = style
        for (mod in getAllModifiers()) {
            res = mod.invoke(res, hints)
        }
        return res
    }
}