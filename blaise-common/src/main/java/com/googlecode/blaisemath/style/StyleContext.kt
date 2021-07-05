package com.googlecode.blaisemath.style

import com.google.common.annotations.Beta
import com.google.common.base.Preconditions
import com.google.common.collect.Sets
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.util.*

/*
* #%L
* BlaiseGraphics
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
*/ /**
 * Maintains multiple types of styles within a single context, and also
 * contains logic for modifying the style attribute sets based on "hints".
 *
 * @author Elisha Peterson
 */
@Beta
class StyleContext @JvmOverloads constructor(
        /** Parent context.  */
        private val parent: StyleContext? = null
) {
    /** Modifiers that apply to the styles in this context.  */
    private val modifiers: MutableSet<StyleModifier?>? = Sets.newHashSet()
    //region PROPERTIES
    /**
     * Get collection of style types supported by this context, not including
     * types supported by the parent.
     * @return types
     */
    fun getModifiers(): MutableSet<StyleModifier?>? {
        return modifiers
    }

    /**
     * Add new modifier.
     * @param mod modifier
     * @return true if changed
     */
    fun addModifier(mod: StyleModifier?): Boolean {
        return modifiers.add(mod)
    }

    /**
     * Remove modifier.
     * @param mod modifier
     * @return true if removed
     */
    fun removeModifier(mod: StyleModifier?): Boolean {
        return modifiers.remove(mod)
    }
    //endregion
    /**
     * Get collection of style types supported by this context, including
     * types supported by the parent context.
     * @return types
     */
    fun getAllModifiers(): MutableSet<StyleModifier?>? {
        return if (parent != null) Sets.union(parent.getAllModifiers(), modifiers) else modifiers
    }

    /**
     * Applies all modifiers in this context to the given style, returning the result.
     * @param style the style to modify
     * @param hints the hints to apply
     * @return the modified style
     */
    fun applyModifiers(style: AttributeSet?, vararg hints: String?): AttributeSet? {
        return applyModifiers(style, Sets.newLinkedHashSet(Arrays.asList(*hints)))
    }

    /**
     * Applies all modifiers in this context to the given style, returning the result.
     * @param style the style to modify
     * @param hints the hints to apply
     * @return the modified style
     */
    fun applyModifiers(style: AttributeSet?, hints: MutableSet<String?>?): AttributeSet? {
        Preconditions.checkNotNull(style)
        var res = style
        for (mod in getAllModifiers()) {
            res = mod.apply(res, hints)
        }
        return res
    }
}