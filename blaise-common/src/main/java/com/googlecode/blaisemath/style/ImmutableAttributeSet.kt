package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before

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
*/ /**
 * Provides an attribute set that throws exceptions if attempting to put or remove values.
 * (But its parent and values may still be changed directly.)
 *
 * @author Elisha Peterson
 */
internal class ImmutableAttributeSet  // prevent instantiation elsewhere
private constructor() : AttributeSet() {
    override fun remove(key: String?): Any? {
        notSupported()
        return null
    }

    override fun put(key: String?, value: Any?): Any? {
        notSupported()
        return null
    }

    override fun and(key: String?, `val`: Any?): AttributeSet? {
        notSupported()
        return null
    }

    private fun notSupported() {
        throw UnsupportedOperationException("ImmutableAttributeSet cannot be modified.")
    }

    companion object {
        /**
         * Makes an immutable copy of the provided attribute set. Uses the same parent
         * object if present.
         * @param set the set to copy
         * @return an immutable copy
         */
        fun immutableCopyOf(set: AttributeSet?): ImmutableAttributeSet? {
            val res = ImmutableAttributeSet()
            res.parent = set.parent
            res.attributeMap.putAll(set.attributeMap)
            return res
        }

        /**
         * Makes an immutable copy of the provided attribute set, with a different parent.
         * @param set the set to copy
         * @param par the parent
         * @return an immutable copy
         */
        fun immutableCopyOf(set: AttributeSet?, par: AttributeSet?): ImmutableAttributeSet? {
            val res = ImmutableAttributeSet()
            res.parent = par
            res.attributeMap.putAll(set.attributeMap)
            return res
        }
    }
}